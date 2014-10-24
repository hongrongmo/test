package org.ei.logservice;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.logging.CLFLogger;
import org.ei.logging.CLFMessage;
import org.ei.logservice.config.RuntimeProperties;

@SuppressWarnings("serial")
public class LogServer extends HttpServlet {

    private CLFLogger logger;
    private static Logger log4j = Logger.getLogger(LogServer.class);

    /**
     * Init the servlet.  Uses config parameters to get the path
     * to the log file.
     */
    public void init() throws ServletException {
        log4j.info("Init LogServer");

        // Ensure the "runlevel" property is set
        String runlevel = System.getProperty(RuntimeProperties.SYSTEM_ENVIRONMENT_RUNLEVEL);
        if (GenericValidator.isBlankOrNull(runlevel)) {
            log4j.error(
                "\n\n*********************************************************************\n" +
                "* UNABLE TO RETRIEVE '" + RuntimeProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "' property!\n" +
                "* THIS MUST BE INITIALIZED VIA TOMCAT STARTUP PARAM \n" +
                "* (e.g. -D" + RuntimeProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "=<environment>)\n" +
                "*********************************************************************\n\n");
            throw new RuntimeException("Unable to initialize - property '" + RuntimeProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "' is not set!");
        }


        try {
            logger = new CLFLogger();
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }

    }

    /**
     * Shutdown servlet procedure
     */
    public void destroy() {
        try {
            logger.shutdown();
        } catch (Exception e) {
            // nothing here yet
        }

    }

    /**
     * Main entry for servlet.  Services log requests to enqueue new entries.
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            // Try to get the sessionid from the appdata
            String appdata = request.getParameter("appdata");
            if (!GenericValidator.isBlankOrNull(appdata)) {
            	if (appdata.contains("sid=")) {
            		for (String semi : appdata.split(";")) {
            			if (semi.contains("sid")) {
            				String sid[] = semi.split("=");
            				if (sid == null || sid.length != 2) {
            	        		log4j.warn("AppData contains 'sid' but it is malformed!");
            				}
            			}
            		}
            	} else {
            		log4j.warn("No 'sid' value found in AppData...");
            	}
                log4j.warn("********Enqueue Logging usage entry, AppData= " + appdata);
        	} else {
        		log4j.warn("No 'sid' value found in AppData...");
            }
            
            // Enqueue the message to be logged
            String clfMessage = messenge(request);
            logger.enqueue(new CLFMessage(clfMessage));
            
        } catch (Throwable t) {
            log4j.error("Unable to enqueue new entry!", t);
        }
    }

    /**
     * Convert the current request into a database-ready String.
     * 
     * @param request
     * @return
     */
    public String messenge(HttpServletRequest request) {
        StringBuffer message = new StringBuffer();
        message.append("<" + request.getParameter("appid") + "> ");
        message.append("<" + request.getParameter("reqid") + "> ");
        message.append("<" + toCLF(request) + "> ");
        message.append("<" + request.getParameter("cookies") + "> ");
        message.append("<" + request.getParameter("appdata") + "> ");
        return message.toString();
    }

    /**
     * Convert current request int CLF-ready String
     * 
     * @param request
     * @return
     */
    private String toCLF(HttpServletRequest request) {
        StringBuffer clf = new StringBuffer();
        Calendar calendar = new GregorianCalendar();
        //
        Date date = new Date();
        date.setTime((long) Long.parseLong(request.getParameter("date")));
        calendar.setTime(date);
        int zone = (calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000));
        DateFormat format3 = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss ");
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);

        clf.append(request.getParameter("host"));
        clf.append(" " + request.getParameter("rfc931"));
        clf.append(" " + request.getParameter("username"));
        clf.append(" " + format3.format(date) + nf.format(zone) + "00]");
        clf.append(" \"" + request.getParameter("HTTPmethod"));
        clf.append(" " + request.getParameter("uri_stem"));
        // || ((request.getParameter("uri_query")).compareTo("null") == 0)
        if (request.getParameter("uri_query") != null) {
            clf.append("?" + request.getParameter("uri_query"));
        }
        clf.append(" HTTP/" + request.getParameter("prot_version"));
        clf.append("\" " + request.getParameter("statuscode"));
        clf.append(" " + request.getParameter("bytes"));
        if (request.getParameter("referrer") != null) {
            if (request.getParameter("referrer").equalsIgnoreCase("null")) {
                clf.append(" \"-");
            } else {
                clf.append(" \"" + request.getParameter("referrer"));
            }
        } else {
            clf.append(" \"-");
        }
        clf.append("\" \"" + request.getParameter("user_agent"));
        clf.append("\" \"" + request.getParameter("cookies") + "\"");

        return (clf.toString());
    }
}
