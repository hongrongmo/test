package org.ei.controller;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.email.SESEmail;
import org.ei.biz.email.SESMessage;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.exception.ServiceException;
import org.ei.session.BlockedIPEvent;
import org.ei.session.BlockedIPEvent.TimePeriod;
import org.ei.session.BlockedIPStatus;
import org.ei.stripes.util.HttpRequestUtil;

public class IPBlocker {
    private final static Logger log4j = Logger.getLogger(IPBlocker.class);

    public static final String IPBLOCKER_BUCKET_INTERVAL_MINUTES_PROPERTY = "ipblocker.bucket.interval.minutes";
    public static final String IPBLOCKER_SESSION_LIMIT_PROPERTY = "ipblocker.session.limit";
    public static final String IPBLOCKER_REQUEST_LIMIT_PROPERTY = "ipblocker.request.limit";
    public static final String IPBLOCKER_AUTHFAIL_LIMIT_PROPERTY = "ipblocker.authfail.limit";
    public static final String IPBLOCKER_NONCUSTOMER_REQUEST_LIMIT_PROPERTY = "ipblocker.noncustomer.request.limit";
    public static final String IPBLOCKER_EMAIL_TO_PROPERTY = "ipblocker.email.to";
    public static final String IPBLOCKER_EMAIL_FROM_PROPERTY = "ipblocker.email.from";
    public static final String IPBLOCKER_SESSION_RATE_LIMITOR_KEY = "ipblocker.session.rate.limitor";
    public static final String IPBLOCKER_AUTOBLOCK_ENABLED_PROPERTY = "ipblocker.autoblock.enabled";
    public static final String IPBLOCKER_REQUEST_PER_SESSION_LIMIT_PROPERTY = "ipblocker.request.per.session";

    // Singleton instance
    private static IPBlocker instance;

    // Available items to count
    public enum COUNTER {
        SESSION, REQUEST, AUTHFAIL, NONCUSTOMER_REQUEST
    }

    // Member variables
    private Map<String, String> blockMap = new HashMap<String, String>();
    private Map<String, String> whitelistMap = new HashMap<String, String>();
    private long createTime = System.currentTimeMillis();

    private static long reloadInterval = 600000L; // 10 minutes
    private static int bucketincrementtimemin = 5;
    private static long sessionlimit = 150;
    private static long requestlimit = 900;
    private static long nonCustRequestLimit = 600;
    private static long authFailLimit = 150;
    private static long maxRequestperSessionRate = 600;
    private static boolean autoBlockEnabled = false;
    private static String emailto;
    private static String sender;

    /**
     * Singleton instance
     *
     * @return
     * @throws IOException
     */
    public static synchronized IPBlocker getInstance() {
        long currentTime = System.currentTimeMillis();
        if (instance == null) {
            log4j.warn("Creating new IPBlocker instance (initial)");
            instance = new IPBlocker();
            refreshProperties();
        } else if ((currentTime - instance.getCreateTime()) > reloadInterval) {
            log4j.warn("Creating new IPBlocker instance (reload)");
            instance.blockMap.clear();
            instance.blockMap = null;
            instance = null;
            instance = new IPBlocker();
            refreshProperties();
        }

        return instance;
    }

    /**
     * Get the Runtime properties used with IPBlocker
     *
     * @throws IOException
     */
    private static void refreshProperties() {
        try {
            ApplicationProperties runtimeprops = EVProperties.getApplicationProperties();
            bucketincrementtimemin = Integer.parseInt(runtimeprops.getProperty(IPBLOCKER_BUCKET_INTERVAL_MINUTES_PROPERTY, "5"));
            sessionlimit = Long.parseLong(runtimeprops.getProperty(IPBLOCKER_SESSION_LIMIT_PROPERTY, "150"));
            requestlimit = Long.parseLong(runtimeprops.getProperty(IPBLOCKER_REQUEST_LIMIT_PROPERTY, "900"));
            nonCustRequestLimit = Long.parseLong(runtimeprops.getProperty(IPBLOCKER_NONCUSTOMER_REQUEST_LIMIT_PROPERTY, "600"));
            authFailLimit = Long.parseLong(runtimeprops.getProperty(IPBLOCKER_AUTHFAIL_LIMIT_PROPERTY, "150"));
            maxRequestperSessionRate = Long.parseLong(runtimeprops.getProperty(IPBLOCKER_REQUEST_PER_SESSION_LIMIT_PROPERTY, "600"));
            emailto = runtimeprops.getProperty(IPBLOCKER_EMAIL_TO_PROPERTY);
            sender = runtimeprops.getProperty(IPBLOCKER_EMAIL_FROM_PROPERTY);
            autoBlockEnabled = Boolean.parseBoolean(runtimeprops.getProperty(IPBLOCKER_AUTOBLOCK_ENABLED_PROPERTY, "false"));
        } catch (Throwable t) {
            log4j.error("Unable to refresh properties!", t);
        }
    }

    /**
     * Constructor - attempt to read blocked_ips.txt file from build path.
     *
     * @throws IOException
     */
    private IPBlocker() {
        log4j.info("Initializing blocked IPs Map...");

        try {
            List<BlockedIPStatus> blockedIPs = BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_ANY);
            if (blockedIPs != null && !blockedIPs.isEmpty()) {
                for (BlockedIPStatus ipstatus : blockedIPs) {
                    if (ipstatus.isBlocked()) {
                        this.blockMap.put(ipstatus.getIP(), "y");
                    } else if (BlockedIPStatus.STATUS_WHITELIST.equals(ipstatus.getStatus())) {
                        this.whitelistMap.put(ipstatus.getIP(), "true");
                    }
                }
            }
        } catch (Throwable t) {
            log4j.error("Unable to read blocked IPs from database...", t);
            this.blockMap.clear();
        }

        log4j.info(this.blockMap.size() + " entries added to blocked IPs Map!");

    }

    /**
     * Refresh blocked ips map.
     */
    private void refreshBlockedIpsMap(){
    	log4j.info("Refreshing blocked IPs Map...");

        try {
            List<BlockedIPStatus> blockedIPs = BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_ANY);
            if (blockedIPs != null && !blockedIPs.isEmpty()) {
                for (BlockedIPStatus ipstatus : blockedIPs) {
                    if (ipstatus.isBlocked()) {
                        this.blockMap.put(ipstatus.getIP(), "y");
                    } else if (BlockedIPStatus.STATUS_WHITELIST.equals(ipstatus.getStatus())) {
                        this.whitelistMap.put(ipstatus.getIP(), "true");
                    }
                }
            }
        } catch (Throwable t) {
            log4j.error("Unable to read blocked IPs from database...", t);
            this.blockMap.clear();
        }

        log4j.info(this.blockMap.size() + " entries refreshed to blocked IPs Map!");
    }

    /**
     * Determine if user is blocked by comparing IP to IPs from list.
     *
     * @param request
     * @return
     */
    public boolean isBlocked(String ip) {
        log4j.info("Checking for IP blocks...");
        MemcachedUtil memcached = MemcachedUtil.getInstance();

        // If blank, automatically false
        if (GenericValidator.isBlankOrNull(ip)) {
            return false;
        }

        // Check if IP is in blocked IPs list (file system static list)
        boolean blocked = blockMap.containsKey(ip);
        if (blocked) {
            log4j.warn("Returning true due to BLOCKED status for '" + ip + "'");
            return true;
        }

        // Check if IP is in whitelist
        boolean whitelist = whitelistMap.containsKey(ip);
        if (whitelist) {
            log4j.warn("Returning false due to WHITELIST status for '" + ip + "'");
            return false;
        }

        try {

        	// Priority 1 check
        	// Check the non customer request limit, this also auto block the user IP
        	if (isThresholdReached(ip, COUNTER.NONCUSTOMER_REQUEST)) {
            	String bucketname = getBucketName(ip, COUNTER.NONCUSTOMER_REQUEST);
                String messagebucket = bucketname + "_MESSAGE";
                if (memcached.get(messagebucket) == null) {
                	memcached.add(messagebucket, bucketincrementtimemin * 60, new Boolean(true));
                    BlockedIPEvent ipevent = new BlockedIPEvent(ip);
                    ipevent.setEvent(COUNTER.NONCUSTOMER_REQUEST.name());
                    ipevent.setBucket(bucketname);
                    if(autoBlockEnabled){
                    	ipevent.setMessage("Auto Block Applied : Maximum number of non customer requests exceeded (" + nonCustRequestLimit + ") in " + bucketincrementtimemin
                                + " minutes for IP address " + ipevent.getIP());
                    }else{
                    	ipevent.setMessage("Maximum number of non customer requests exceeded (" + nonCustRequestLimit + ") in " + bucketincrementtimemin
                                + " minutes for IP address " + ipevent.getIP());
                    }

                    BlockedIPStatus ipstatus = insertBlockedIPsTable(ipevent);
                    notifyEmail(ipevent, ipstatus);

                    // Refresh immediatly once the auto block is applied
                    if(autoBlockEnabled)refreshBlockedIpsMap();
                    return ipstatus.isBlocked();
                }
            }

            // Check if bucket has signaled abuse
            if (isThresholdReached(ip, COUNTER.REQUEST)) {
                // Take one-time action for this abuse
                String bucketname = getBucketName(ip, COUNTER.REQUEST);
                String messagebucket = bucketname + "_MESSAGE";
                if (memcached.get(messagebucket) == null) {
                    memcached.add(messagebucket, bucketincrementtimemin * 60, new Boolean(true));
                    BlockedIPEvent ipevent = new BlockedIPEvent(ip);
                    ipevent.setEvent(COUNTER.REQUEST.name());
                    ipevent.setBucket(bucketname);
                    ipevent.setMessage("Maximum number of requests exceeded (" + requestlimit + ") in " + bucketincrementtimemin + " minutes for IP address "
                        + ipevent.getIP());
                    BlockedIPStatus ipstatus = insertBlockedIPsTable(ipevent);
                    notifyEmail(ipevent, ipstatus);
                    return ipstatus.isBlocked();
                }
            }else if (isThresholdReached(ip, COUNTER.SESSION)) {
                // Take one-time action for this abuse
                String bucketname = getBucketName(ip, COUNTER.SESSION);
                String messagebucket = bucketname + "_MESSAGE";
                if (memcached.get(messagebucket) == null) {
                    memcached.add(messagebucket, bucketincrementtimemin * 60, new Boolean(true));
                    BlockedIPEvent ipevent = new BlockedIPEvent(ip);
                    ipevent.setEvent(COUNTER.SESSION.name());
                    ipevent.setBucket(bucketname);
                    ipevent.setMessage("Maximum number of new sessions exceeded (" + sessionlimit + ") in " + bucketincrementtimemin
                        + " minutes for IP address " + ipevent.getIP());
                    BlockedIPStatus ipstatus = insertBlockedIPsTable(ipevent);
                    notifyEmail(ipevent, ipstatus);
                    return ipstatus.isBlocked();
                }
            }else if (isThresholdReached(ip, COUNTER.AUTHFAIL)) {
            	// Take one-time action for this abuse
                String bucketname = getBucketName(ip, COUNTER.AUTHFAIL);
                String messagebucket = bucketname + "_MESSAGE";
                if (memcached.get(messagebucket) == null) {
                	memcached.add(messagebucket, bucketincrementtimemin * 60, new Boolean(true));
                    BlockedIPEvent ipevent = new BlockedIPEvent(ip);
                    ipevent.setEvent(COUNTER.AUTHFAIL.name());
                    ipevent.setBucket(bucketname);
                    ipevent.setMessage("Maximum number of new authentication failure exceeded (" + authFailLimit + ") in " + bucketincrementtimemin
                        + " minutes for IP address " + ipevent.getIP());
                    BlockedIPStatus ipstatus = insertBlockedIPsTable(ipevent);
                    notifyEmail(ipevent, ipstatus);
                    return ipstatus.isBlocked();
                }
            }


        } catch (Throwable t) {
            log4j.error("Unable to check session/request limits!", t);

        }

        // Default return false!
        return false;
    }

    /**
     * Increment a count.
     *
     * @param counter
     * @return
     * @throws IOException
     */
    public long increment(String ip, COUNTER counter) {
        MemcachedUtil memcached = MemcachedUtil.getInstance();
        String bucketname = getBucketName(ip, counter);
        try {
            log4j.info("Incrementing bucket: " + bucketname + " by 1, time interval = " + bucketincrementtimemin * 60);
            return memcached.incr(bucketname, 1, 1L, bucketincrementtimemin * 60);
        } catch (Throwable t) {
            log4j.warn("Unable to increment " + counter.name() + " for IP " + ip);
            try {
                memcached.delete(bucketname);
            } catch (Throwable t2) {};
            return 0;
        }
    }

    /**
     * Determine if threshold limit reached for COUNTER
     *
     * @param ip
     * @param counter
     * @return
     * @throws IOException
     */
    public boolean isThresholdReached(String ip, COUNTER counter) {
        MemcachedUtil memcached = MemcachedUtil.getInstance();
        String bucket = "";
        long count = 0;
        try {
            bucket = getBucketName(ip, counter);
            Object obj = memcached.get(bucket);
            if (obj != null && obj.toString() != null)
                count = Long.parseLong(obj.toString().trim());

            if (counter.equals(COUNTER.REQUEST))
                return count > requestlimit;
            else if (counter.equals(COUNTER.SESSION))
                return count > sessionlimit;
            else if (counter.equals(COUNTER.AUTHFAIL))
                return count > authFailLimit;
            else if (counter.equals(COUNTER.NONCUSTOMER_REQUEST))
                return count > nonCustRequestLimit;
            else
                return false;
        } catch (Throwable t) {
            log4j.error("Unable to parse value from bucket " + bucket, t);
            try {
                if (bucket != null) memcached.delete(bucket);
            } catch (Throwable t2) {};
            return false;
        }
    }

    
    /**
     * Clear bucket.
     *
     * @param ip the ip
     * @param counter the counter
     */
    public void clearBucket(String ip, COUNTER counter) {
        MemcachedUtil memcached = MemcachedUtil.getInstance();
        String bucket = getBucketName(ip, COUNTER.REQUEST);
        log4j.info("Deleting bucket " + bucket);
        memcached.delete(bucket);
        memcached.delete(bucket + "_MESSAGE");
    }

    /**
     * Gets the bucket name prefix.
     *
     * @param ip
     *            the ip
     * @return the bucket name prefix
     * @throws ParseException
     *             the parse exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public String getBucketName(String ip, COUNTER counter) {
        return (ip + "_" + getTimeStampByInterval() + "_" + counter.name());
    }

    /**
     * Gets the time stamp by interval.
     *
     * @return the time stamp by interval
     * @throws ParseException
     *             the parse exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getTimeStampByInterval() {
        Calendar timestamp = Calendar.getInstance();
        timestamp.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        int divider = timestamp.get(Calendar.MINUTE) / bucketincrementtimemin;
        String minStr = "00";
        if (divider != 0) {
            int minsCalculated = divider * bucketincrementtimemin;
            if (minsCalculated < 10) {
                minStr = "0" + minsCalculated;
            } else {
                minStr = "" + minsCalculated;
            }
        }
        return timestamp.get(Calendar.HOUR_OF_DAY) + minStr + "00";
    }

    /**
     * Update BlockedIPStatus and BlockedIPEvent tables. Should ONLY be used when an event has occurred.
     *
     * @param ipevent
     * @return
     * @throws ServiceException
     * @throws Exception
     */
    public static BlockedIPStatus insertBlockedIPsTable(BlockedIPEvent ipevent) {
        log4j.warn("Attempting to insert a new entry for the ip (" + ipevent.getIP() + ") into the table.");
        try {
            ipevent.save();

            // Update the main ipstatus entry
            BlockedIPStatus ipstatus = BlockedIPStatus.load(ipevent.getIP());
            if (ipstatus == null) {

            	ipstatus = new BlockedIPStatus(ipevent.getIP());
            }
            // Apply auto block only when the non customer request limit is exceeded and auto block flag is enabled
            if(ipevent.getEvent().equalsIgnoreCase(COUNTER.NONCUSTOMER_REQUEST.name()) && autoBlockEnabled){
            	ipstatus.setStatus(BlockedIPStatus.STATUS_BLOCKED);
            }
            ipstatus.addAccount(org.ei.biz.personalization.cars.Account.getAccountInfo(ipevent.getIP()));
            ipstatus.setTimestamp(new Date());
            ipstatus.save();

            return ipstatus;
        } catch (Throwable t) {
            log4j.error("Unable to save event!", t);
            return null;
        }
    }

    static String newline = System.getProperty("line.separator") == null ? "\r\f" : System.getProperty("line.separator");

    /**
     * Notify stakeholders via email of abuse
     *
     * @param ipevent
     * @param ipstatus
     * @throws Exception
     */
    public static void notifyEmail(BlockedIPEvent ipevent, BlockedIPStatus ipstatus) {

        try {
            if (ipevent == null || ipstatus == null) {
                throw new IllegalArgumentException("Event or Status object was null!");
            }

            if (GenericValidator.isBlankOrNull(emailto) || GenericValidator.isBlankOrNull(sender)) {
                throw new IllegalArgumentException("Email addresses are not set!");
            }

            log4j.info("Preparing email for event...");
            String subject = "****** EV " + ipevent.getEvent() + " RATE EXCEEDED (" + ipevent.getIP() + ")! ******";

            StringBuffer message = new StringBuffer();
            message.append("##########################################################################################################################"
                + newline);
            message.append(ipevent.getMessage() + newline);
            message.append("    Timestamp: " + ipevent.getTimestamp() + newline);
            message.append("    From host: '" + (InetAddress.getLocalHost()).getHostName() + "'" + newline);
            if (!GenericValidator.isBlankOrNull(ipstatus.getAccountName())) {
                message.append("    Account:  " + ipstatus.getAccountName() + " (" + ipstatus.getAccountNumber() + ") " + newline);
                if (!GenericValidator.isBlankOrNull(ipstatus.getDepartmentName())) {
                    message.append("    Department:  " + ipstatus.getDepartmentName() + " (" + ipstatus.getDepartmentNumber() + ") " + newline);
                }
            }
            message.append("##########################################################################################################################"
                + newline + newline);

            message
                .append("You can take action by submitting the form at http://lngcentral.lexisnexis.com/depts/Edit/AppSupp/Forms/SecurityBreach.aspx (This URL requires Elsevier VPN access)"
                    + newline + newline);

            message.append("You can view all abuse at http://evtools.engineeringvillage.com/status/ipblocker.url (This URL is username/password protected!)" + newline
                + newline);

            List<BlockedIPEvent> history = BlockedIPEvent.getByTimePeriod(ipevent.getIP(), TimePeriod.LASTYEAR);
            message.append("Customer Abuse History (" + history.size() + " item" + (history.size() > 1 ? "s" : "") + "): " + newline);
            for (BlockedIPEvent historyitem : history) {
                message.append("    Timestamp:  " + historyitem.getTimestamp() + newline);
                message.append("    " + historyitem.getMessage() + newline + newline);
            }

            log4j.warn("Attempting to notify '" + emailto + "' admin from email '" + sender + "' for the ip (" + ipevent.getIP() + ") with the message: '"
                + ipevent.getMessage() + "'.");
            SESMessage sesmessage = new SESMessage();
            sesmessage.setDestination(emailto);
            sesmessage.setMessage(subject, message.toString(),false);
            sesmessage.setFrom(sender);
            SESEmail.getInstance().send(sesmessage);
        } catch (Throwable t) {
            log4j.error("Error occurred while sending email!", t);
        }

    }

    public Map<String, String> getBlockmap() {
        return this.blockMap;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public long getReloadInterval() {
        return reloadInterval;
    }


    /**
     * Checks if is request per session rate exceeded.
     *
     * @param request the request
     * @return true, if is request per session rate exceeded
     */
    public boolean isRequestPerSessionRateExceeded(HttpServletRequest request) {


    	boolean isBlocked = false;

        HttpSession session = request.getSession(false);
        if (session == null)
            return false;

        // Initialize if not present
        SessionRate sessionrate = (SessionRate) session.getAttribute(IPBLOCKER_SESSION_RATE_LIMITOR_KEY);
        if (sessionrate == null) {
            sessionrate = new SessionRate();
        }else {
            sessionrate.incrementTotalRequest();
        }

        long seconds = (new Date().getTime() - sessionrate.getFirstrequest()) / 1000;

        //Reset the session rate object once the predefined reset time is reached, and don't reset the value of  'blockWithCaptcha'
        if(seconds>(bucketincrementtimemin*60)){
        	sessionrate.reset(false);
        }

        //If the session is blocked already, only block the non ajax requests, because that leads to partial page rendering in many of our pages
        if(sessionrate.isBlockWithCaptcha()){
        	isBlocked =  !isAjax(request);
        	buildInComingUrl(request,sessionrate);
        }else{
        	//If the total request for the session exceeds the allowed limit, update the session object and notify the admin
        	//again, this captcha block will applicable only for non ajax requests
        	if(sessionrate.getTotalRequest()>maxRequestperSessionRate){
            	if(!isAjax(request)){
            		 sessionrate.setBlockWithCaptcha(true);
    	    		 String ip = HttpRequestUtil.getIP(request);
    	    		 String message = "Customer SESSION has been blocked with CAPTCHA page due to high activity. IP(" + ip + "), total number of requests " + sessionrate.getTotalRequest() + " exceeded for the session with in " + (bucketincrementtimemin*60)+" seconds.";
    	             BlockedIPEvent ipevent = new BlockedIPEvent(ip);
    	             ipevent.setEvent(BlockedIPEvent.EVENT_REQUESTRATE_LIMIT);
    	             ipevent.setBucket(ip + "_" + session.getId() + "_REQUESTRATE");
    	             ipevent.setMessage(message);
    	             BlockedIPStatus ipstatus = IPBlocker.insertBlockedIPsTable(ipevent);
    	             notifyEmail(ipevent, ipstatus);
    	             isBlocked = true;
    	             buildInComingUrl(request,sessionrate);
            	}
            }
        }
        session.setAttribute(IPBLOCKER_SESSION_RATE_LIMITOR_KEY, sessionrate);
        return isBlocked;
    }

    /**
     * Builds the in coming url.
     *
     * @param request the request
     * @param sessionRate the session rate
     */
    private void buildInComingUrl(HttpServletRequest request, SessionRate sessionRate)  {

    	//Don't build the url if the current request is ajax or captcha related
    	if(isAjax(request) ||
    			request.getRequestURI().contains("captcha/display.url") ||
    			request.getRequestURI().contains("captcha/verify.url") ||
    			request.getRequestURI().contains("captcha/image.url") ||
    			request.getRequestURI().contains("system/endsession.url")){
    		return ;
    	}

    	//To identify this is the url generated as part of captch block, adding the 'redirectFlow' parameter
    	StringBuffer redirect  = new StringBuffer(request.getRequestURI()+"?redirectFlow=Generic&");

    	try {
    		Enumeration<String> e = request.getParameterNames();
        	while(e.hasMoreElements())
    		{
    			String name = e.nextElement();
    			String value = "";
    			if(name.equalsIgnoreCase("database"))
    			{
    				int dbvalue = 0;
    				String[] multiValue = request.getParameterValues(name);
    				for(int i=0;i<multiValue.length; i++)
    				{
    					dbvalue += Integer.parseInt(multiValue[i]);
    				}
    				value = Integer.toString(dbvalue);
    			}else{
    				value = (String) request.getParameter(name);
    			}
    			redirect.append(name + "=" + URLEncoder.encode(value, "UTF-8"));
    			if (e.hasMoreElements()) redirect.append("&");
    		}
        	sessionRate.setIncomingUrl( redirect.toString());
    	} catch (Exception exp) {
			log4j.error("URL encoding is failed!, exception = "+exp.getMessage());
			sessionRate.setIncomingUrl("/home.url?redirectFlow=Generic&");
		}
    }

    private static boolean isAjax(HttpServletRequest request) {
	   return "XMLHttpRequest"
	             .equals(request.getHeader("X-Requested-With"));
	}

    /**
     * Inner class to track request rate.
     *
     * @author kamaramx
     *
     */
    public static class SessionRate implements Serializable{

    	private static final long serialVersionUID = 6009901472688306504L;

		private int totalRequest = 1;
        private long firstrequest = new Date().getTime();
        private boolean blockWithCaptcha = false;
        private String incomingUrl = null;

       	public String getIncomingUrl() {
			return incomingUrl;
		}

		public void setIncomingUrl(String incomingUrl) {
			this.incomingUrl = incomingUrl;
		}

		public int getTotalRequest() {
			return totalRequest;
		}

		public void setTotalRequest(int totalRequest) {
			this.totalRequest = totalRequest;
		}

		public long getFirstrequest() {
			return firstrequest;
		}

		public void setFirstrequest(long firstrequest) {
			this.firstrequest = firstrequest;
		}

		public boolean isBlockWithCaptcha() {
			return blockWithCaptcha;
		}

		public void setBlockWithCaptcha(boolean blockWithCaptcha) {
			this.blockWithCaptcha = blockWithCaptcha;
		}

		public void reset(boolean resetCaptcha) {
            firstrequest = new Date().getTime();
            totalRequest = 1;
            incomingUrl = null;
            if(resetCaptcha){
            	blockWithCaptcha = false;
            }
        }

		public int incrementTotalRequest() {
            return ++totalRequest;
        }

    }

}