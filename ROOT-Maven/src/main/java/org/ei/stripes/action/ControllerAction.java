package org.ei.stripes.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.commons.validator.GenericValidator;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.biz.security.NormalAuthRequiredAccessControl;
import org.ei.biz.security.WorldAccessControl;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.controller.DataRequest;
import org.ei.controller.DataResponse;
import org.ei.controller.OutputPrinter;
import org.ei.controller.content.ContentConfig;
import org.ei.controller.content.ContentDescriptor;
import org.ei.controller.logging.LogEntry;
import org.ei.controller.logging.Logger;
import org.ei.session.SessionManager;
import org.ei.session.UserSession;
import org.ei.stripes.AuthInterceptor;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.action.personalaccount.HomeAction;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.stripes.util.HttpRequestUtil;

/**
 * This class handles legacy requests for EV.  Prior to the UI Refresh project
 * requests were handled in one large Controller class that did everything.
 * Now there are Interceptor classes to handle the request and this class
 * simply transforms XML from the engvillage app into HTML via stylesheet.
 *
 * The goal is to eventually remove this class and have all requests handled
 * completely without XML transformations!
 *
 * NOTE the mapping below!  All /servlet/Controller requests come here!!
 *
 * @author harovetm
 *
 */
@UrlBinding("/controller/servlet/Controller")
public class ControllerAction extends EVActionBean {

    private static final org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(ControllerAction.class);

    /**
     * 
     * @return
     */
    @After(stages=LifecycleStage.BindingAndValidation)
    private Resolution legacyRedirect() {
        HttpServletRequest request = context.getRequest();
        ContentConfig contentconfig = EVProperties.getContentConfig();

        // Use the CID from the request to determine if this is legacy request
        if (contentconfig == null) {
            throw new RuntimeException("No ContentConfig object could be found - check app initialization!");
        } else if (GenericValidator.isBlankOrNull(this.CID)) {
            log4j.warn("ControllerAction was invoked but no CID found!  Request was '" + request.getRequestURI() + (request.getQueryString() == null ? "" : "?"+request.getQueryString()) + "'");
            return HomeAction.HOME_RESOLUTION;
        }

        // Only redirect when incoming URL is /controller/servlet/Controller which only
        // maps to ControllerAction
        log4j.info("Incoming CID for legacy check: " + this.CID);
        if (contentconfig.containsKey("legacy_" + this.CID)) {
            // Create redirect URL with query string
            String redirURL = contentconfig.getContentDescriptor("legacy_" + this.CID).getDisplayURL();
            String urlAfterHash = null;
            if (redirURL.contains("#init")) {
                urlAfterHash =  redirURL.substring(redirURL.indexOf("#init"),  redirURL.length());
                redirURL = redirURL.substring(0, redirURL.indexOf("#init"));
            }
            
            if (redirURL.contains("?")) {
                redirURL += "&" + request.getQueryString();
            } else {
                redirURL += "?" + request.getQueryString();
            }
            if(urlAfterHash!=null)redirURL+=urlAfterHash;
            
            log4j.info("[" + HttpRequestUtil.getIP(request) + "] Redirecting from legacy URL to: '" + redirURL + "'");
            return new RedirectResolution(redirURL);
        }
        
        return null;
    }
    
	/**
	 * Override for the ISecuredAction interface.  This ActionBean services
	 * both "world" and "customer" access levels from the legacy mappings
	 * in the ContentConfig.xml file.  This means we need to check the
	 * AUTHGROUP to see what IAccessControl should be returned.
	 *
	 * NOTE: none of the remaining world/cutomer URLs in the ContentConfig.xml
	 * file require individual authentication otherwise this would NOT
	 * work!
	 */
	@Override
	public IAccessControl getAccessControl() {
		EVActionBeanContext context = (EVActionBeanContext)getContext();
		UserSession usess = context.getUserSession();
		HttpServletRequest request = context.getRequest();
		ContentDescriptor cd;
		try {
			// Determine from the ContentDescriptor if this is a WORLD
			// or CUSTOMER request.  Missing CID will revert to auth
			// required.
			cd = context.buildContentDescriptor(usess, request);
			if (cd == null) {
			    // Return "world" access to skip all authentication since this will be going to home page anyway
			    return new WorldAccessControl();
			} else if (ContentDescriptor.AUTHGROUP_WORLD_NAME.equals(cd.getAuthGroup())) {
			    // ContentConfig file indicates this is "world" access
	        	return new WorldAccessControl();
	        } else {
	            // Require normal auth access
	        	return new NormalAuthRequiredAccessControl();
	        }
		} catch (Exception e) {
			log4j.warn("Unable to build ContentDescriptor to determine AccessControl!");
        	return new NoAuthAccessControl();
		}
	}

	/**
	 * Handles all requests to main controller on {/controller}/servlet/Controller.
	 * @see AuthInterceptor for all the pre-work that goes on every request.  By the
	 * time the request gets here it SHOULD be a valid request!
	 *
	 * @return Resolution or null.  Usually the output will either be 1) null when
	 * the request is handled by writing to the OutputPrinter object (legacy EV) or
	 * 2) a ForwardResolution object that maps to a Stripes action to finish
	 * servicing the request
	 */
	@DefaultHandler
	public Resolution handle() throws Exception {
		log4j.info("Handling request in ControllerAction");

		if(GenericValidator.isBlankOrNull(CID)){
			return HomeAction.HOME_RESOLUTION;
		}
		EVActionBeanContext context = (EVActionBeanContext)getContext();
		HttpServletRequest request = context.getRequest();
		HttpServletResponse response = context.getResponse();

		UserSession usersession = context.getUserSession();
		String serverName = context.getServerName(request);

		DataRequest dataRequest = null;
		dataRequest = context.getDataRequest(usersession, request);
		if(dataRequest == null || !dataRequest.hasValidContentDescriptor())
		{
			// This should NOT happen!
			log4j.error("No DataRequest object found!");
        	throw new RuntimeException("Unable to build DataRequest object!");
		}


		// *********************************************************************
		// This section looks up the CID on the request from the ContentConfig
		// file (lives in /engvillage) and attempts to route the request based
		// on the data found there.
		// *********************************************************************
        ContentDescriptor cd;
		try {
			cd = dataRequest.getContentDescriptor();
	        // When XML mode is OFF the data source URL is actually used to display
	        // HTML content.
	        if (!cd.isXmlmode()) {
	    		log4j.info("Direct display mode!!  Forwarding to '" + cd.getDataSourceURL() + "'");
	    		finalize(null, usersession);
	        	return new ForwardResolution(cd.getDataSourceURL());
	        }
	        // When refresh mode is ON we will use a Stripes action to complete
	        // the request.
	        else if (cd.isRefresh()) {
	    		log4j.info("Refresh mode!!  Forwarding to '" + cd.getDisplayURL() + "'");
	        	return new ForwardResolution(cd.getDisplayURL());
	        }
		} catch (Exception e) {
        	EVExceptionHandler.logException("Unable to create ContentDescriptor object", e, request);
        	throw new RuntimeException("Unable to create ContentDescriptor object");
		}


		// *************************************************************
		// Build the OutputPrinter object.  This will write to the
		// response when NOT handled via ForwardResolution.  This is
		// for legacy EV paths in the ContentConfig.xml file.
		// *************************************************************
		OutputPrinter printer;
		try {
			boolean appendSession = Boolean.parseBoolean(EVProperties.getProperty(ApplicationProperties.APPEND_SESSION));
			printer = context.buildPrinter(request, response, appendSession);
		} catch (Exception e) {
			EVExceptionHandler.logException("Unable to create OutputPrinter", e, request);
			throw new RuntimeException("Unable to create OutputPrinter",e);
		}

		Logger logger = Logger.getInstance();
		String dataCacheDir = EVProperties.getProperty(ApplicationProperties.DATA_CACHE_DIR);

		DataResponse dataResponse = null;
		LogEntry logEntry = null;


		try
		{
			// *********************************************************************
			// Use the DataResponseBroker to finish servicing the request
			// *********************************************************************
            dataResponse = context.getDataResponse(request, dataCacheDir, printer, dataRequest);
			usersession = dataResponse.getUpdatedSession();

			if (!GenericValidator.isBlankOrNull(dataResponse.getRedirectURL())) {
				log4j.info("Redirecting after XML retrieval.  URL: " + dataResponse.getRedirectURL());
				return new RedirectResolution(dataResponse.getRedirectURL(),false);
			}

		} catch(Exception e) {
			// ******************************************************************
			// Deal with Exception from code above
			// ******************************************************************
			EVExceptionHandler.logException("Error has occurred processing request", e, request);

				// May get the "SHUTDOWN" message when connection can't be made to data (model) layer!
				if(e.getMessage() != null && e.getMessage().equals("SHUTDOWN")){
					printer.printFatalError();
					log4j.warn("Shutting down");
					System.exit(-1);
				}

				// Already trying to get the exception page??
				if(EVActionBeanContext.REDIR_PAGE_GENERAL_EXCEPTION.equals(dataRequest.getContentDescriptor().getContentID()))
				{
					return new StreamingResolution("text/html", "The website is currently unavailable.");
				}
				// Not already in redirect, try to redirect to it
				else{
					return SystemMessage.SYSTEM_ERROR_RESOLUTION;
				}
		}
		finally
		{
			this.getContext().getLogEntry().addDataResponse(dataResponse);
			finalize(printer, usersession);
		}
		return null;
    }

	/**
	 * Finalize the request
	 *
	 * @param printer
	 * @param logEntry
	 * @param usersession
	 */
	private void finalize(OutputPrinter printer, UserSession usersession) {
		SessionManager sessionmanager = new SessionManager(context.getRequest(), context.getResponse());
		Logger logger = Logger.getInstance();

		try {
			if (printer != null) {
				printer.close();
			}
		} catch (Exception e) {
			EVExceptionHandler.logException("Unable to close Printer: " + e.getMessage(), e);
		}

		/*if (usersession != null) {
			try {
				sessionmanager.doTouch(usersession);
			} catch (Exception e) {
				EVExceptionHandler.logException("Unable to touch usersession: " + e.getMessage(), e);
			}
		}*/
	}
}
