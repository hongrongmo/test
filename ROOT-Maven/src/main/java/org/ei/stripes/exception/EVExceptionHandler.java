package org.ei.stripes.exception;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.exception.ActionBeanNotFoundException;
import net.sourceforge.stripes.exception.DefaultExceptionHandler;
import net.sourceforge.stripes.exception.StripesServletException;

import org.apache.commons.validator.GenericValidator;
import org.apache.jasper.JasperException;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.config.EVProperties;
import org.ei.exception.EVBaseException;
import org.ei.service.amazon.AmazonServiceHelper;
import org.ei.session.AWSInfo;
import org.ei.session.UserSession;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.action.EVActionBean;

import com.amazonaws.services.sns.AmazonSNSAsyncClient;

/**
 * This class handles all exceptions for the EV web application. NOTE that it forwards to /WEB-INF/pages/world/exception.jsp - it should NOT forward to any
 * layout using standard.jsp because it relies on the EVActionBean hierarchy.
 *
 * @author harovetm
 *
 */
public class EVExceptionHandler extends DefaultExceptionHandler {

    private final static Logger log4j = Logger.getLogger(EVExceptionHandler.class);

    public static final String GA_EXCEPTION_CATEGORY = "Exception";

    /**
     * Log an exception including request and current user information
     *
     * @param t
     * @param request
     */
    public static void logException(String custommessage, Throwable t) {
        logException(custommessage, t, null, log4j);
    }

    /**
     * Log an exception including request and current user information
     *
     * @param t
     * @param request
     */
    public static void logException(String custommessage, Throwable t, HttpServletRequest request) {
        logException(custommessage, t, request, log4j);
    }

    private String snsalerttopic;
    private int errorcount = 0;
    private long errorinterval = 10 * 60 * 1000;
    private int errorthreshold = 100;
    private boolean errorsent;
    private Date errorstart;
    private Map<String, Integer> errorcountbyexception = new HashMap<String, Integer>();

    /**
     * Check the current error count and send message to SNS if beyond threshhold
     */
    private void casErrorCount(Throwable t) {
        Date now = new Date();
        if (errorstart == null || now.getTime() - errorstart.getTime() > errorinterval) {
            this.errorcount = 1;
            this.errorstart = new Date();
            this.errorsent = false;
            this.errorcountbyexception.clear();
            try {
                this.snsalerttopic = EVProperties.getProperty(EVProperties.SNS_TOPIC_AWSALERTS);
                String errorthreshold = EVProperties.getProperty(EVProperties.MAX_ERROR_THRESHHOLD);
                String errorinterval = EVProperties.getProperty(EVProperties.MAX_ERROR_INTERVAL);
                if (!GenericValidator.isBlankOrNull(errorthreshold))
                    this.errorthreshold = Integer.parseInt(errorthreshold);
                if (!GenericValidator.isBlankOrNull(errorinterval))
                    this.errorinterval = Long.parseLong(errorinterval) * 60 * 1000;
            } catch (Exception e) {
                log4j.error("Unable to initialize max error properties");
                // Defaults
                this.snsalerttopic = "arn:aws:sns:us-east-1:230521890328:AWSAlerts-Prod";
                this.errorinterval = 10 * 60 * 1000;
                this.errorthreshold = 100;
            }
        }

        Integer exceptioncount = this.errorcountbyexception.get(t.getClass().getName());
        this.errorcountbyexception.put(t.getClass().getName(), exceptioncount == null ? 1 : ++exceptioncount);

        if (!errorsent && ++errorcount > errorthreshold) {
            // Send notification to appropriate SNS topic
            AmazonSNSAsyncClient client = AmazonServiceHelper.getInstance().getAmazonSNSAsyncClient();
            String subject = "[ENGINEERING VILLAGE ALERT] Maximum Error Count exceeded.";
            StringBuffer message = new StringBuffer("Maximum Error Count exceeded.  Instance ID: " + AWSInfo.getAWSMetaData(AWSInfo.INSTANCE_ID) + "\n\n");
            message.append("===================================================================\n");
            message.append("                          Errors \n");
            message.append("===================================================================\n");
            for (String key : this.errorcountbyexception.keySet()) {
                message.append("  *  " + key + " : " + this.errorcountbyexception.get(key) + "\n");
            }

            client.publish(this.snsalerttopic, message.toString(), subject);
            this.errorsent = true;
        }
    }

    /**
     * Log an exception including request and current user information
     *
     * @param t
     * @param request
     */
    public static void logException(String custommessage, Throwable t, HttpServletRequest request, Logger logger) {
        // Callers can pass in their own logger. Ensure it's set
        if (logger == null) {
            logger = log4j;
        }

        // If there is a custom error message, add it first
        StringBuilder errorout = new StringBuilder();
        if (!GenericValidator.isBlankOrNull(custommessage)) {
            errorout.append(custommessage + "\n");
        } else {
            errorout.append("An exception has occurred!\n");
        }

        // Logs the current request.
        errorout.append("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
        if (t != null) {
            errorout.append("+    Exception class: " + t.getClass().getName() + "\n");
            errorout.append("+    Message:   " + t.getMessage() + "\n");
        }

        // The rest depends on a valid request!
        if (request != null) {
            errorout.append("+    URL:       " + request.getRequestURI() + (request.getQueryString() != null ? ("?" + request.getQueryString()) : "") + "\n");

            // Dump POST data if present
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                Enumeration<String> en = request.getParameterNames();
                String key;
                String[] value;
                while (en.hasMoreElements()) {
                    key = (String) en.nextElement();
                    value = request.getParameterValues(key);
                    for (int i = 0; i < value.length; i++) {
                        errorout.append("+    parameter " + key + " = " + value[i] + "\n");
                    }
                }
            }

            // Try to output user info
            try {
                EVActionBean actionbean = (EVActionBean) request.getAttribute("actionBean");
                if (actionbean != null) {
                    EVActionBeanContext context = actionbean.getContext();
                    if (context != null) {
                        UserSession usersession = context.getUserSession();
                        if (usersession != null) {
                            errorout.append("+    Session ID:     " + usersession.getID() + "\n");
                            IEVWebUser u = usersession.getUser();
                            if (u != null) {
                                errorout.append("+    Username:       " + u.getUsername() + "\n");
                                errorout.append("+    Userid:         " + u.getUserId() + "\n");
                                errorout.append("+    Customer ID:    " + u.getCustomerID() + "\n");
                                // errorout.append("+    Contract ID:    " + u.getContractID() + "\n");
                            }
                        }
                    }
                }
            } catch (Throwable t2) {
                errorout.append("+    Unable to dump EVActionBean\n");
            }
        }

        errorout.append("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

        if (t != null) {
            logger.error(errorout.toString(), t);
        } else {
            logger.error(errorout.toString());
        }
    }

    /**
     * Common init method to set some info for the JSP
     *
     * @param exc
     * @param request
     */
    private void init(Exception exc, HttpServletRequest request) {
        if (null != exc.getMessage()) {
            request.setAttribute("exception", exc.getMessage());
        } else {
            request.setAttribute("exception", "No default message!");
        }
        request.setAttribute("googleanalytics", EVProperties.getProperty(EVProperties.GOOGLE_ANALYTICS_ACCOUNT));
    }

    /**
     * Handle ActionBeanNotFound exception.  This happens when a URL matches a pattern for an ActionBean but no handler could be found
     * @param exc
     * @param request
     * @param response
     * @return
     */
    public Resolution handleActionBeanNotFound(ActionBeanNotFoundException exc, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("message", "This request could not be completed.");
        log4j.warn("Error processing request: " + request.getRequestURI());
        return new ForwardResolution("/WEB-INF/pages/world/exception.jsp");
    }
    
    /**
     * Handle StripesServletExceptions
     *
     * @param exc
     * @param request
     * @param response
     * @return
     */
    public Resolution handleStripesServletException(StripesServletException exc, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("message", "This request could not be completed.");
        log4j.warn("Error processing request: " + request.getRequestURI());
        return new ForwardResolution("/WEB-INF/pages/world/exception.jsp");
    }

    /**
     * Handle BasicExceptions
     *
     * @param exc
     * @param request
     * @param response
     * @return
     */
    public Resolution handleBasicException(EVBaseException exc, HttpServletRequest request, HttpServletResponse response) {
        logException(null, exc, request);
        init(exc, request);
        request.setAttribute("message", exc.getCustomerMessage());
        request.setAttribute("exception", exc.toXML());
        response.setStatus(500);

        // Add a google analytics event
        EVActionBean actionbean = (EVActionBean) request.getAttribute("actionBean");
        if (actionbean != null) {
            request.setAttribute("usersession", actionbean.getContext().getUserSession());
            actionbean.createWebEvent(GA_EXCEPTION_CATEGORY, exc.getClass().getSimpleName(), "Error Code: " + Integer.toString(exc.getErrorCode())
                + ", Instance ID: " + new AWSInfo().getEc2Id() + ", Message: " + exc.getMessage());
        }

        // Send SNS message if needed
        casErrorCount(exc);

        return new ForwardResolution("/WEB-INF/pages/world/exception.jsp");
    }

    /**
     * Handle JasperExceptions
     *
     * @param exc
     * @param request
     * @param response
     * @return
     */
    public Resolution handleJasperException(JasperException exc, HttpServletRequest request, HttpServletResponse response) {
        // This could be the exception.jsp with a problem. In that
        // case we don't want to keep going to it (infinite loop).
        logException(null, exc, request);
        init(exc, request);
        response.setStatus(500);

        // Send SNS message if needed
        casErrorCount(exc);

        return null;
    }

    /**
     * Handle HistoryExceptions
     *
     * @param exc
     * @param request
     * @param response
     * @return
     */
    public Resolution handleAnyTypeException(Exception exc, HttpServletRequest request, HttpServletResponse response) {
        logException(null, exc, request);
        init(exc, request);
        response.setStatus(500);

        // Add a google analytics event
        EVActionBean actionbean = (EVActionBean) request.getAttribute("actionBean");
        if (actionbean != null) {
            request.setAttribute("usersession", actionbean.getContext().getUserSession());
            actionbean.createWebEvent(GA_EXCEPTION_CATEGORY, exc.getClass().getSimpleName(), exc.getMessage() + "; Instance ID: " + new AWSInfo().getEc2Id());
        }

        // Send SNS message if needed
        casErrorCount(exc);

        return new ForwardResolution("/WEB-INF/pages/world/exception.jsp");
    }

    /*
     * ExceptionHandler implementation for system error message but to have thisworking we have to change all the redirections and also CID check would notwork.
     * Since CID would be always the action Bean which is throwing the exceptionto handle that we need to throw the specific the exceptions and define handler
     * foreach exception. Each handler will have its own parameter set for JSP.
     *
     * to use exception handler this class needs to implement Interface Exceptionhandler.
     */

    /*
     * @Override public void init(Configuration arg0) throws Exception { // do nothing }
     *
     * @Override public void handle(Throwable exc, HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
     *
     *
     * String cid = request.getParameter("CID");
     *
     * if(cid != null && cid.equalsIgnoreCase("invalidcid")){ request.setAttribute("pageTitle", "System Error"); invalidcid = true; }
     *
     * if(cid != null && cid.equalsIgnoreCase("endsession")){ request.setAttribute("pageTitle", "Session Ended"); request.getSession(true); endsession = true; }
     *
     * if(cid != null && cid.equalsIgnoreCase("error")){ request.setAttribute("pageTitle", "Engineering Village - System Error"); error = true;
     * processErrorXml(request); }
     *
     * request.setAttribute("message", exc.getMessage());
     *
     * request.getRequestDispatcher("/WEB-INF/pages/world/exceptionMessage.jsp").forward(request, response); }
     */

}
