package org.ei.stripes.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.controller.IPBlocker;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.FastClient;
import org.ei.domain.personalization.MergePersonalAccount;
import org.ei.domain.personalization.PersonalAccountException;
import org.ei.domain.personalization.cars.Account;
import org.ei.download.util.SaveToGoogleUsage;
import org.ei.email.SESEmail;
import org.ei.email.SESMessage;
import org.ei.exception.InfrastructureException;
import org.ei.service.amazon.s3.AmazonS3Service;
import org.ei.service.amazon.s3.AmazonS3ServiceImpl;
import org.ei.session.BaseCookie;
import org.ei.session.BlockedIPEvent;
import org.ei.session.BlockedIPEvent.TimePeriod;
import org.ei.session.BlockedIPStatus;
import org.ei.session.SessionCache;
import org.ei.session.UserSession;
import org.ei.stripes.util.HttpRequestUtil;
import org.ei.system.ApplicationStatusVO;
import org.ei.system.EVSaxParser;

@UrlBinding("/status{$event}.url")
public class ApplicationStatus extends EVActionBean {

    private Logger log4j = Logger.getLogger(ApplicationStatus.class);

    private static long starttime = System.currentTimeMillis();
    private String authURL;
    private String appName;
    private String openxmlURL = "/controller/servlet/Controller?CID=openXML&dbchkbx=1&DATABASE=1&XQUERYX=%3Cquery%3E%3CandQuery%3E%3Cword+path%3D%22db%22%3Ecpx%3C%2Fword%3E%3Cword%3Eworld%3C%2Fword%3E%3C%2FandQuery%3E%3C%2Fquery%3E&AUTOSTEM=on&STARTYEAR=1990&ENDYEAR=2009&SORT=re&xmlsearch=Submit+Query";
    private String txtsimulatedip;
    private String txtblockedip;
    private boolean enabled;
    private String unmergeEmail = "";
    public static final String SIM_IP_SALT = "saltyipsaregreat";
    private ApplicationStatusVO viewbean = new ApplicationStatusVO();

    private String emailto;
    private String emailfrom = "ei-noreply@elsevier.com";

    private SessionCache sCache;

    private String cacheKey;
    private String cacheVal;
    private int cacheMins;
    
    private String usageOption;
    private String startDate;
    private String endDate;

    private String propKey = "";

    private String propValue = "";

    // Utility inner class for name/value pair entries
    public static class NameValuePair {
        public NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private String value;
    }

    /**
     * Override for the ISecuredAction interface. This ActionBean does NOT require authentication!
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    /**
     * Ensure request is from a valid IP address
     *
     * @return
     */
    @Before
    private Resolution validateIP() {
        HttpServletRequest request = context.getRequest();
        if (GenericValidator.isBlankOrNull(txtsimulatedip)) {
            txtsimulatedip = HttpRequestUtil.getIP(request);
        }
        // Validate IP address
        if (!validateIP(request)) {
            log4j.warn("Invalid access via '" + request.getRemoteAddr() + "'");
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        }
        return null;
    }

    /**
     * Service a request for the default status page
     */
    @DefaultHandler
    @DontValidate
    public Resolution home() throws ServletException, IOException {
        HttpServletRequest request = context.getRequest();
        //
        // Get request properties
        //
        Enumeration<String> enames = request.getHeaderNames();
        Queue<ApplicationStatusVO.NameValuePair> requestproperties = viewbean.getRequestproperties();
        requestproperties.add(new ApplicationStatusVO.NameValuePair("Remote Address", request.getRemoteAddr()));
        while (enames.hasMoreElements()) {
            String name = enames.nextElement();
            String value = request.getHeader(name);
            requestproperties.add(new ApplicationStatusVO.NameValuePair(name, value));
        }
        requestproperties.add(new ApplicationStatusVO.NameValuePair("IP address", HttpRequestUtil.getIP(request)));

        Runtime rt = Runtime.getRuntime();

        long currenttime = System.currentTimeMillis();
        long uptime = currenttime - starttime;
        SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yy");

        Queue<ApplicationStatusVO.NameValuePair> webappproperties = viewbean.getWebappproperties();
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Environment", EVProperties.getRuntimeProperties().getRunlevel()));
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Total memory", Long.toString(rt.totalMemory() / 1024 / 1024) + " MB"));
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Free memory", Long.toString(rt.freeMemory() / 1024 / 1024) + " MB"));
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Used memory", Long.toString((rt.totalMemory() - rt.freeMemory()) / 1024 / 1024) + " MB"));
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Start time", formatter.format(starttime)));
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Up time", getDurationBreakdown(uptime)));
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Release Version", EVProperties.getRuntimeProperty(RuntimeProperties.RELEASE_VERSION)));

        boolean up = false;
        // Attempt to get search database connection
        up = checkSessionDatabase();
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Session Database", up ? "<span style='color:green'>Up</span>"
            : "<span style='color:red'>Down</span>"));

        // Attempt to get search database connection
        up = checkSearchDatabase();
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Search Database", up ? "<span style='color:green'>Up</span>"
            : "<span style='color:red'>Down</span>"));

        // Attempt to get search database connection (uses current IP)
        UserSession usersession = context.getUserSession();
        webappproperties.add(new ApplicationStatusVO.NameValuePair("UserSession", usersession != null ? "<span style='color:green'>Up</span>"
            : "<span style='color:red'>Down</span>"));

        // Attempt to get Fast connection
        // up = checkFast(EVProperties.getRuntimeProperties().getFastUrl());
        webappproperties.add(new ApplicationStatusVO.NameValuePair("Fast Connection", up ? "<span style='color:green'>Up</span>"
            : "<span style='color:red'>Down</span>"));

        //
        // Forward to the JSP
        //
        return new ForwardResolution("/WEB-INF/pages/status/status.jsp");
    }

    /**
     * Service request for "environment" display
     *
     * @return
     * @throws IOException
     * @throws ServletException
     * @throws MessagingException
     * @throws AddressException
     */
    @HandlesEvent("/sendmail")
    public Resolution sendmail() throws ServletException, IOException {
        try {
            SESMessage sesmessage = new SESMessage();
			sesmessage.setDestination(emailto);
            sesmessage.setMessage("Testing email", "<h1>Testing from application status page</h1>",true);
            sesmessage.setFrom(emailfrom);
            SESEmail.getInstance().send(sesmessage);
        } catch (Exception e) {
            this.context.getValidationErrors().add("emailto",
                new SimpleError("Unable to send email: " + e.getClass().toString() + ", message: '" + e.getMessage() + "'"));
        }

        //
        // Forward to the JSP
        //
        return home();
    }

    /**
     * Service request for "environment" display
     *
     * @return
     */
    @HandlesEvent("/environment")
    public Resolution environment() {
        try {
            RuntimeProperties rtp = EVProperties.getRuntimeProperties();
            Object[] keys = rtp.keySet().toArray();
            for (Object key : keys) {
                this.viewbean.getRuntimeproperties().put((String) key, rtp.getProperty((String) key));
            }
            Object[] keystrings = this.viewbean.getRuntimeproperties().keySet().toArray();
            Arrays.sort(keystrings);
            context.getRequest().setAttribute("sortedkeys", keystrings);
        } catch (Exception e) {
            log4j.error("Unable to build RuntimeProperties from ControllerConfig (Stripes)", e);
        }

        return new ForwardResolution("/WEB-INF/pages/status/environment.jsp");
    }

    @HandlesEvent("/updateprop")
    public Resolution updateProperty() {
        if (StringUtils.isNotBlank(propKey) && StringUtils.isNotBlank(propValue)) {
            EVProperties.getRuntimeProperties().setProperty(propKey, propValue);

        }
        return environment();
    }

    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    /**
     * Service request for "user info" display
     *
     * @return
     */
    @HandlesEvent("/userinfo")
    public Resolution userinfo() {

        AmazonS3Service s3Srvc = new AmazonS3ServiceImpl();
        try {
            viewbean.setCustomerImages(s3Srvc.getCustomerImagesList());
            RuntimeProperties runtimeprops = RuntimeProperties.getInstance();
            String customerImagePath = runtimeprops.getProperty(RuntimeProperties.CUSTOMER_IMAGES_URL_PATH, "https://s3.amazonaws.com/ev-customer-images/");
            viewbean.setCustomerImagePath(customerImagePath);
        } catch (Exception e) {
            log4j.error("Unable to retrieve customer images list from cloud", e);
        }
        viewbean.setUsersession(context.getUserSession());

        return new ForwardResolution("/WEB-INF/pages/status/userinfo.jsp");
    }

    @HandlesEvent("/simulatedip")
    public Resolution simulatedip() {

        String s = BaseCookie.getCookieValue(context.getRequest(), "SIMULATEDIP");

        if (!GenericValidator.isBlankOrNull(s)) {

            s = isValidSimIp(s);
            if (s != null) {
                this.txtsimulatedip = s;
            } else {
                this.txtsimulatedip = "";
            }
        }
        return new ForwardResolution("/WEB-INF/pages/status/simulatedip.jsp");
    }

    private long system_1hour = 60 * 60 * 1000;
    private long system_1day = system_1hour * 24;
    private long system_1week = system_1day * 7;

    @HandlesEvent("/ipblocker")
    public Resolution showBlockedIps() throws Exception {
        HttpServletRequest request = context.getRequest();
        request.setAttribute("blockedIpsList", BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_ANY));
        return new ForwardResolution("/WEB-INF/pages/status/ipblocker.jsp");
    }

    @HandlesEvent("/ticurl")
    public Resolution ticurl() {

        return new ForwardResolution("/WEB-INF/pages/status/ticurl.jsp");
    }

    @HandlesEvent("/openxml")
    public Resolution openxml() {

        return new ForwardResolution("/WEB-INF/pages/status/openxml.jsp");
    }

    @HandlesEvent("/openrss")
    public Resolution openrss() {

        return new ForwardResolution("/WEB-INF/pages/status/openrss.jsp");
    }

    @HandlesEvent("/openurl")
    public Resolution openurl() {

        return new ForwardResolution("/WEB-INF/pages/status/openurl.jsp");
    }

    @HandlesEvent("/unmerge")
    public Resolution unmerge() {

        return new ForwardResolution("/WEB-INF/pages/status/unmerge.jsp");
    }

    @HandlesEvent("/searchwidget")
    public Resolution searchwidget() {

        return new ForwardResolution("/WEB-INF/pages/status/searchwidget.jsp");
    }

    /**
     * Handles the submit button for the simulated IP
     *
     * @return
     */
    @HandlesEvent("simulatedipsubmit")
    public Resolution simulatedipsubmit() {
        // Use the submitted value as the new IP
        String cookieValue = txtsimulatedip;
        try {
            cookieValue += "_" + DigestUtils.md5Hex(txtsimulatedip + SIM_IP_SALT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BaseCookie.setCookie(context.getRequest(), context.getResponse(), "SIMULATEDIP", cookieValue, -1);
        return new RedirectResolution("/status/simulatedip.url");
    }
    
    @HandlesEvent("/driveusage") 
    public Resolution driveUsage() throws Exception {
    	
    	if(usageOption == null || usageOption.equalsIgnoreCase("")){
    		usageOption = "downloadformat";
    	}
    	HttpServletRequest request = context.getRequest();
    	Map<String,String> usageData = SaveToGoogleUsage.getUsageData(usageOption,startDate,endDate);
    	
    	if(startDate != null && endDate != null ){
    		request.setAttribute("dateQueryinfo","Date filter applied from "+startDate +" to "+endDate);
    	}
    	
    	if(usageData.get("totalCount") != null){
    		request.setAttribute("totalCount",usageData.get("totalCount"));
    		usageData.remove("totalCount");
    	}else{
    		request.setAttribute("totalCount",'0');
    	}
    	
    	request.setAttribute("usageData", usageData);
    	return new ForwardResolution("/WEB-INF/pages/status/googledriveusage.jsp");
    }

    @HandlesEvent("blockedipsubmit")
    public Resolution blockedipsubmit() throws Exception {
        if (!HttpRequestUtil.isValidIP(txtblockedip)) {
            ValidationErrors errors = new ValidationErrors();
            errors.addGlobalError(new SimpleError("Invalid IP address!"));
            getContext().setValidationErrors(errors);
            return new RedirectResolution("/status/ipblocker.url").flash(this);
        }

        String blockedIp = txtblockedip;
        if (txtblockedip != null) {
            try {
                BlockedIPStatus ipstatus = BlockedIPStatus.load(txtblockedip);
                if (ipstatus != null) {
                    ValidationErrors errors = new ValidationErrors();
                    errors.addGlobalError(new SimpleError("The IP(" + txtblockedip + ") you are trying to add it is already part of blocked list."));
                    getContext().setValidationErrors(errors);
                } else {
                    ipstatus = new BlockedIPStatus(txtblockedip);
                    ipstatus.addAccount(Account.getAccountInfo(txtblockedip));
                    ipstatus.save();
                }
                txtblockedip = null;
                getContext().getMessages().add(new SimpleMessage("The IP " + blockedIp + " has been successfully added."));
            } catch (Exception exception) {
                if (exception.getMessage().indexOf("unique constraint") != -1) {
                    ValidationErrors errors = new ValidationErrors();
                    errors.addGlobalError(new SimpleError("The IP(" + txtblockedip + ") you are trying to add it is already part of blocked list."));
                    getContext().setValidationErrors(errors);
                }
            }
        }
        return new RedirectResolution("/status/ipblocker.url").flash(this);
    }

    @HandlesEvent("ipcounterstatus")
    public Resolution ipcounterstatus() throws Exception {
        if (!HttpRequestUtil.isValidIP(txtblockedip)) {
            ValidationErrors errors = new ValidationErrors();
            errors.addGlobalError(new SimpleError("Invalid IP address!"));
            getContext().setValidationErrors(errors);
            return new RedirectResolution("/status/ipblocker.url").flash(this);
        }

        if (txtblockedip != null) {
            IPBlocker blocker = IPBlocker.getInstance();
            try {
                Map<String, String> statusMap = blocker.retreiveCurrentStatus(txtblockedip);
                context.getRequest().setAttribute("ip", txtblockedip);
                context.getRequest().setAttribute("statusMap", statusMap);
                txtblockedip = null;
            } catch (Exception exception) {
                context.getRequest().setAttribute("ip", txtblockedip);
                context.getRequest().setAttribute("error", "Error occured while fetching the data!");
                return new ForwardResolution("/WEB-INF/pages/status/counterstatus.jsp");
            }
        }
        return new ForwardResolution("/WEB-INF/pages/status/counterstatus.jsp");
    }


    @HandlesEvent("iphistory")
    public Resolution iphistory() throws Exception {
        if (!HttpRequestUtil.isValidIP(txtblockedip)) {
            ValidationErrors errors = new ValidationErrors();
            errors.addGlobalError(new SimpleError("Invalid IP address!"));
            getContext().setValidationErrors(errors);
            return new RedirectResolution("/status/ipblocker.url").flash(this);
        }

        if (txtblockedip != null) {
            try {
                context.getRequest().setAttribute("ip", txtblockedip);
                context.getRequest().setAttribute("descHistList", BlockedIPEvent.getByTimePeriod(txtblockedip, TimePeriod.LASTYEAR));
                txtblockedip = null;
            } catch (Exception exception) {
                context.getRequest().setAttribute("ip", txtblockedip);
                context.getRequest().setAttribute("error", "Error occured while fetching the data!");
                return new ForwardResolution("/WEB-INF/pages/status/deschistory.jsp");
            }
        }
        return new ForwardResolution("/WEB-INF/pages/status/deschistory.jsp");
    }

    @HandlesEvent("deleteblockedip")
    public Resolution deleteblockedip() throws Exception {
        if (!HttpRequestUtil.isValidIP(txtblockedip)) {
            ValidationErrors errors = new ValidationErrors();
            errors.addGlobalError(new SimpleError("Invalid IP address!"));
            getContext().setValidationErrors(errors);
            return new RedirectResolution("/status/ipblocker.url").flash(this);
        }

        String blockedIp = txtblockedip;
        if (txtblockedip != null) {
            try {
                BlockedIPStatus ipstatus = BlockedIPStatus.load(txtblockedip);
                if (ipstatus != null) {
                    ipstatus.delete();
                }
                txtblockedip = null;
                getContext().getMessages().add(new SimpleMessage("The IP " + blockedIp + " has been successfully removed."));
            } catch (Exception exception) {
                if (exception.getMessage().indexOf("No rows deleted") != -1) {
                    ValidationErrors errors = new ValidationErrors();
                    errors.addGlobalError(new SimpleError("The IP(" + txtblockedip + ") you are trying delete is not part of blocked list."));
                    getContext().setValidationErrors(errors);
                }
            }
        }
        return new RedirectResolution("/status/ipblocker.url").flash(this);
    }

    @HandlesEvent("updateblockedip")
    public Resolution updateblockedip() throws Exception {
        if (!HttpRequestUtil.isValidIP(txtblockedip)) {
            ValidationErrors errors = new ValidationErrors();
            errors.addGlobalError(new SimpleError("Invalid IP address!"));
            getContext().setValidationErrors(errors);
            return new RedirectResolution("/status/ipblocker.url").flash(this);
        }

        String blockedIp = txtblockedip;
        if (txtblockedip != null) {
            try {
                BlockedIPStatus ipstatus = BlockedIPStatus.load(txtblockedip);
                if (ipstatus != null) {
                    ipstatus.setStatus(enabled ? BlockedIPStatus.STATUS_BLOCKED : BlockedIPStatus.STATUS_UNBLOCKED);
                    ipstatus.save();
                }
                txtblockedip = null;
                if (enabled) {
                    getContext().getMessages().add(new SimpleMessage("The IP " + blockedIp + " has been successfully blocked."));
                } else {
                    getContext().getMessages().add(new SimpleMessage("The IP " + blockedIp + " has been successfully unblocked."));
                }

            } catch (Exception exception) {
                if (exception.getMessage().indexOf("No rows updated") != -1) {
                    ValidationErrors errors = new ValidationErrors();
                    errors.addGlobalError(new SimpleError("The IP(" + txtblockedip + ") you are trying update is not part of blocked list."));
                    getContext().setValidationErrors(errors);
                }
            }
        }
        return new RedirectResolution("/status/ipblocker.url").flash(this);
    }

    /**
     * Check if the cookie value is a valid ip overrid generated from our system
     *
     * @param cookieValue
     * @return if it is return the ip
     */
    public static String isValidSimIp(String cookieValue) {
        if (GenericValidator.isBlankOrNull(cookieValue)) {
            return null;
        }
        String ip = cookieValue.substring(0, cookieValue.indexOf("_"));
        String md5 = cookieValue.substring(cookieValue.indexOf("_") + 1);
        String check = "";
        try {
            check = DigestUtils.md5Hex(ip + SIM_IP_SALT);

        } catch (Exception e) {

            e.printStackTrace();
        }

        if (check.equals(md5)) {
            return ip;
        }

        return null;
    }

    /**
     * Handles the clear button for the simulated IP
     *
     * @return
     */
    @HandlesEvent("simulatedipclear")
    public Resolution simulatedipclear() {
        // Use the submitted value as the new IP
        BaseCookie.deleteCookie(context.getRequest(), context.getResponse(), "SIMULATEDIP");
        return new RedirectResolution("/status/simulatedip.url");
    }

    /**
     * Method to check IP address for authentication purposes. Use the client's IP address from the request to authenticate access to the status pages. Only
     * internal users are permitted to access these pages. The standard loop back address (127.0.0.1) is also considered invalid to allow negative testing.
     *
     * @param request
     *            HttpRequest containing the requesting (client) IP to validate
     * @return boolean indicating if this is a valid LN IP address
     */
    public boolean validateIP(HttpServletRequest request) {
        // Fetch the requesting (client) IP address for this request
        String clientIP = request.getHeader("x-forwarded-for");
        if (clientIP == null)
            clientIP = request.getRemoteAddr();

        // Assume invalid until proven otherwise
        boolean validIP = false;

        // If IP is null, we're done. Otherwise attempt to authenitcate.
        if (!GenericValidator.isBlankOrNull(clientIP)) {
            // Put IP string into IP object and attempt to authenticate
            Inet4Address testIP = null;
            try {
                testIP = (Inet4Address) InetAddress.getByName(clientIP);
                clientIP = testIP.toString();

                byte[] octets = testIP.getAddress();

                // Allow addresses in the range 138.12.x.x (DEV)
                if ((octets[0] == (byte) 138) && (octets[1] == (byte) 12)) {
                    validIP = true;
                }
                // Allow addresses in the range 127.0.0.1 (LOCALHOST)
                else if ((octets[0] == (byte) 127) && (octets[1] == (byte) 0) && (octets[2] == (byte) 0) && (octets[3] == (byte) 1)) {
                    validIP = true;
                }
                // Allow addresses in the range 198.185.23.x (CERT)
                else if ((octets[0] == (byte) 198) && (octets[1] == (byte) 185) && (octets[2] == (byte) 23)) {
                    validIP = true;
                }
                // Allow addresses in the range 10.178.x.x (CERT)
                else if ((octets[0] == (byte) 10) && (octets[1] == (byte) 178)) {
                    validIP = true;
                }
                // Allow addresses in the range 198.185.25.x (PROD)
                else if ((octets[0] == (byte) 198) && (octets[1] == (byte) 185)) {
                    validIP = true;
                }
                // Allow VPN access
                else if ((octets[0] == (byte) 172) && (octets[1] == (byte) 29)) {
                    validIP = true;
                } else {
                    log4j.warn("IP address '" + clientIP + "' not in the range 138.12.x.x, " + "198.185.23.x, 198.185.x.x, or 172.29.x.x");
                }
            } catch (UnknownHostException e) {
                log4j.warn("Caught exception while validating IP: " + clientIP, e);
                validIP = false;
            }
        }
        return validIP;
    }

    /**
     * Convert a millisecond duration to a string format
     *
     * @param millis
     *            A duration to convert to a string form
     * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
     */
    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return (sb.toString());
    }

    /**
     * Ensure search database is available
     *
     * @return
     */
    public boolean checkSearchDatabase() {

        Connection con = null;
        ConnectionBroker broker = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean flag = false;
        try {

            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            pstmt = con.prepareStatement("select m_id from bd_master");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String mID = rs.getString("m_id");
                if (mID != null)
                    flag = true;
                else
                    flag = false;
                break;
            }
        } catch (Exception e) {
            log4j.error("Unable to get search connection (status page)");
            return false;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e1) {
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e1) {
                }
            }
            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                }
            }
        }
        return flag;
    }

    /**
     * Ensure search database is available
     *
     * @return
     */
    public boolean checkSessionDatabase() {

        Connection con = null;
        ConnectionBroker broker = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean flag = false;
        try {

            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement("select session_id from user_session");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String sesID = rs.getString("session_id");
                if (sesID != null)
                    flag = true;
                else
                    flag = false;
                break;
            }
        } catch (Exception e) {
            log4j.error("Unable to get session connection (status page)");
            return false;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e1) {
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e1) {
                }
            }
            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                } catch (Exception cpe) {
                }
            }
        }
        return flag;
    }

    /**
     * Check Fast connection (runs sample search)
     *
     * @return
     */
    public boolean checkFast(String fastURL) {
        BufferedReader in = null;
        boolean testResult = false;
        if (GenericValidator.isBlankOrNull(fastURL)) {
            log4j.error("Fast URL is empty!");
            return false;
        }

        try {
            String query = "db:cpx";
            FastClient client = new FastClient();
            client.setBaseURL(fastURL);
            client.setResultView("ei");
            client.setOffSet(0);
            client.setPageSize(25);
            client.setQueryString(query);
            client.setDoCatCount(true);
            client.setDoNavigators(true);
            client.setPrimarySort("ausort");
            client.setPrimarySortDirection("+");
            client.search();
            int countresult = client.getHitCount();
            if (countresult > -1) {
                return true;
            }
        } catch (Exception e) {
            testResult = false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return testResult;
    }

    /**
     * Runs a search via Open XML
     *
     * @param openxmlURL
     * @return
     */
    public boolean checkOpenXML(String openxmlURL) {

        InputStream stream = null;
        String resultCount = null;
        boolean searchFlag = false;

        try {
            HttpClient httpClient = new HttpClient();
            HttpMethod getMethod = new GetMethod(openxmlURL);
            httpClient.executeMethod(getMethod);
            stream = getMethod.getResponseBodyAsStream();
            EVSaxParser parser = new EVSaxParser();
            parser.parseDocument(stream);
            resultCount = parser.getResultCount();
            if (resultCount != null && Integer.parseInt(resultCount) > -1) {
                searchFlag = true;
            } else {
                searchFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchFlag;

    }

    public String getTxtsimulatedip() {
        return this.txtsimulatedip;
    }

    public void setTxtsimulatedip(String txtsimulatedip) {
        this.txtsimulatedip = txtsimulatedip;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTxtblockedip() {
        return txtblockedip;
    }

    public void setTxtblockedip(String txtblockedip) {
        this.txtblockedip = txtblockedip;
    }

    public ApplicationStatusVO getViewbean() {
        return this.viewbean;
    }

    public void setViewbean(ApplicationStatusVO viewbean) {
        this.viewbean = viewbean;
    }

    public String getUnmergeEmail() {
        return unmergeEmail;
    }

    public void setUnmergeEmail(String unmergeEmail) {
        this.unmergeEmail = unmergeEmail;
    }

    public String getEmailto() {
        return this.emailto;
    }

    public void setEmailto(String emailto) {
        this.emailto = emailto;
    }

    public String getEmailfrom() {
        return this.emailfrom;
    }

    public void setEmailfrom(String emailfrom) {
        this.emailfrom = emailfrom;
    }
    
    public String getUsageOption() {
		return usageOption;
	}

	public void setUsageOption(String usageOption) {
		this.usageOption = usageOption;
	}
	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
