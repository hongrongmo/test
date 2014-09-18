package org.engvillage.web.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.config.ApplicationProperties;
import org.ei.logging.LogClient;
import org.ei.util.GUID;

/*
 * This class will inspect the referring URLS and look for MD5 hash
 * The hash will be re-created from the data and the shared secret
 * and compared to the one provided on the referrer.
 *
 *
 * Add this to the WEB-INF/classes/log4j.properties
 * to isolate the logging for this class from the rest of the docview servlet
 * classes.
 *
 * log4j.appender.F=org.apache.log4j.RollingFileAppender
 * log4j.appender.F.File=${catalina.home}/logs/filter.log
 * log4j.appender.F.MaxFileSize=10MB
 * log4j.appender.F.MaxBackupIndex=5
 * log4j.appender.F.layout=org.apache.log4j.PatternLayout
 * log4j.appender.F.layout.ConversionPattern=%5p [%t] %d{yyyyMMddHHmmss} (%F:%L) - %m%n
 * log4j.logger.org.ei=INFO, F
 *
 * Add this to the docview/WEB-INF/web.xml
 *
 *   <filter>
 *   <filter-name>FastDocViewFilter</filter-name>
 *   <filter-class>org.ei.books.FastDocViewFilter</filter-class>
 *      <init-param>
 *       <param-name>logURL</param-name>
 *       <param-value>http://@log_server@/logservice/servlet/LogServer</param-value>
 *     </init-param>
 *   </filter>
 *
 *   <filter-mapping>
 *   <filter-name>FastDocViewFilter</filter-name>
 *   <url-pattern>/*</url-pattern>
 *   </filter-mapping>
 */
public final class FastDocViewFilter implements Filter {

    private static final Pattern pagenoregex = Pattern.compile("pg_(\\d{1,4})\\.pdf");
    private LogClient logClient;

    protected static Log log = LogFactory.getLog(FastDocViewFilter.class);
    private FilterConfig filterConfig = null;
    private static final String SHRDKEY = "!MM01234-5-6789MM#";

    public void destroy() {
        this.filterConfig = null;
        // TODO Auto-generated method stub
    }

    public void init(FilterConfig aConfig) throws ServletException {
        log.info("Starting up");

        this.filterConfig = aConfig;
        String logServiceURL = ApplicationProperties.getInstance().getProperty(ApplicationProperties.LOG_URL);
        this.logClient = new LogClient(logServiceURL);

        log.info("Set logservice URL to " + logServiceURL);
    }

    public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {

        long end, start = System.currentTimeMillis();

        // Skip logging for non-HTTP requests and responses.
        if (!(srequest instanceof HttpServletRequest) || !(sresponse instanceof HttpServletResponse)) {
            chain.doFilter(srequest, sresponse);
            return;
        }

        HttpServletRequest hreq = (HttpServletRequest) srequest;
        // String currentURL = hreq.getRequestURL().toString();
        // log.info(" currentURL: " + currentURL);

        String currentURI = hreq.getRequestURI();

        // Enumeration paramenum = hreq.getParameterNames();
        // while(paramenum.hasMoreElements()) {
        // String paramname = (String) paramenum.nextElement();
        // log.info(paramname + " <= " + hreq.getParameter(paramname));
        // }
        //
        String isbn = null;
        String pageno = null;
        String custid = null;

        if ((currentURI != null) && (currentURI.endsWith("pdf"))) {
            boolean passed = false;

            log.info(" currentURI: " + currentURI);

            String md5param = hreq.getParameter("TICKET");
            log.info(" ticket = " + md5param);
            String uriprefix = "docview/";
            isbn = currentURI.substring(currentURI.indexOf(uriprefix) + uriprefix.length(), currentURI.lastIndexOf("/"));
            Matcher m = pagenoregex.matcher(currentURI);
            if (m.find()) {
                pageno = m.group(1);
            }

            if (md5param != null) {

                // currentURI format /docview/0125816502/pg_0806.pdf
                String[] tokens = md5param.split(":");
                if ((tokens != null) && tokens.length == 3) {
                    StringBuffer ticketVal = new StringBuffer();
                    String reqticket = tokens[0];
                    custid = tokens[1];
                    String currentTime = tokens[2];
                    String newticket = "";

                    ticketVal.append(isbn).append(custid).append(currentTime);
                    ticketVal.append(FastDocViewFilter.SHRDKEY);

                    MessageDigest md5;
                    try {
                        md5 = MessageDigest.getInstance("MD5");
                        byte[] di = md5.digest(ticketVal.toString().getBytes());
                        newticket = new String(asHex(di));
                        log.info(" HASHED: " + newticket);
                    } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    passed = reqticket.equals(newticket);
                    if (passed) {
                        try {
                            // Test if time diff is greater than 30 minutes
                            if ((System.currentTimeMillis() - (Long.parseLong(currentTime))) > 1800000) {
                                log.info("Time expired on ticket.");
                                passed = false;
                            }
                        } catch (NumberFormatException e) {
                            passed = false;
                        }

                    } else {
                        log.info("Ticket mismatch.");
                    }
                }
            } else {
                passed = false;
            }
            if (!passed) {
                ((HttpServletResponse) sresponse).sendError(HttpServletResponse.SC_FORBIDDEN, "Bad Request");
                return;
            }
        }
        // move on to the next
        chain.doFilter(srequest, sresponse);

        if ((currentURI != null) && (currentURI.endsWith("pdf"))) {
            try {
                Enumeration<String> paraNames = hreq.getParameterNames();
                Hashtable<String, String> ht = new Hashtable<String, String>();

                while (paraNames.hasMoreElements()) {
                    String key = (String) paraNames.nextElement();
                    ht.put(key, hreq.getParameter(key));
                }
                ht.put("referex_download", "page");
                if (pageno != null) {
                    ht.put("page", pageno);
                }
                if (custid != null) {
                    ht.put("custid", custid);
                }
                if (isbn != null) {
                    ht.put("ISBN", isbn);
                }
                logClient.setappdata(ht);
                // populate mandatory fields for CLF construction
                logClient.setdate(start);
                logClient.setbegin_time(start);
                end = System.currentTimeMillis();
                logClient.setend_time(end);
                logClient.setresponse_time(end - start);
                if (ht.containsKey("DOCVIEWSESSION")) {
                    logClient.setsid((String) ht.get("DOCVIEWSESSION"));
                } else {
                    logClient.setsid("0");
                }

                String ipAddress = hreq.getHeader("x-forwarded-for");
                if (ipAddress == null) {
                    ipAddress = hreq.getRemoteAddr();
                }
                logClient.setHost(ipAddress);
                logClient.setrfc931("-");
                logClient.setusername("-");
                try {
                    logClient.setcust_id(custid);
                } catch (NumberFormatException e) {
                }
                logClient.setuser_agent(hreq.getHeader("User-Agent"));
                logClient.setHTTPmethod(hreq.getMethod());
                logClient.setreferrer(hreq.getHeader("referer"));
                logClient.seturi_stem(hreq.getRequestURI());
                logClient.seturi_query(hreq.getQueryString());
                logClient.setstatuscode(HttpServletResponse.SC_OK);
                logClient.setrid(new GUID().toString());
                logClient.setappid("EngVillage2");
                logClient.setappdata(ht);

                logClient.sendit();
                log.info("Sent log ..");
            } catch (Exception le) {
                le.printStackTrace();
                log.error(le);
            }
        }
    }

    private String asHex(byte[] hash) {
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++) {
            if (((int) hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

}
