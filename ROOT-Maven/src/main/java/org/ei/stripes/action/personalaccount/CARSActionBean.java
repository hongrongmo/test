package org.ei.stripes.action.personalaccount;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.SimpleError;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSRequestProcessor;
import org.ei.service.cars.CARSRequestType;
import org.ei.service.cars.CARSTemplateNames;
import org.ei.service.cars.PageType;
import org.ei.service.cars.RESTRequestParameters;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.service.cars.rest.CARSRequestFactory;
import org.ei.service.cars.rest.request.CARSRequest;
import org.ei.session.UserSession;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.action.WebAnalyticsEventProperties;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.web.analytics.GoogleWebAnalyticsEvent;

@UrlBinding("/customer/{$event}.url")
public class CARSActionBean extends EVActionBean {

    private final static Logger log4j = Logger.getLogger(CARSActionBean.class);

    protected CARSRequestType requesttype = CARSRequestType.URLBASED;  // Default to use current URL minus extension
    protected String carsresponsepage = "/WEB-INF/pages/customer/carsresponse.jsp"; // Default CARS response JSP
    
    /**
     * Override for the ISecuredAction interface. This ActionBean does AccessControl
     * based on the incoming event.
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    /**
     * Default handler for generic CARS request
     * 
     * @return Resolution
     */
    @DefaultHandler
    @DontValidate
    public Resolution display() {

        setRoom(ROOM.blank);
        
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        CARSRequestProcessor carsrequestprocessor = null;
        UserSession userSession = context.getUserSession();

        // Build CARS request and call service to get response
        CARSResponse carsresponse = null;
        try {
            CARSRequest carsReq = null;
            carsReq = CARSRequestFactory.buildCARSRequest(this.requesttype, request, userSession);
            
            carsrequestprocessor = new CARSRequestProcessor();
            carsresponse = carsrequestprocessor.process(carsReq, request, response, userSession);
            context.setCarsResponse(carsresponse);
            if(carsReq != null && carsReq.getRequestURI().indexOf("authenticate") >= 0
            							&& CARSRequestType.URLBASED == this.requesttype){
            	//if this is an authenticate method and doesn't map to the login action then we should log it
            	//this would be used for things like self manra requests
            	GoogleWebAnalyticsEvent webEvent = new GoogleWebAnalyticsEvent(WebAnalyticsEventProperties.CAT_LOGIN, WebAnalyticsEventProperties.ACTION_LOGIN_URLBASED,carsReq.getRequestURI());
            	webEvent.recordRemoteEvent(context);
            }
            /*
            if (!CARSResponseStatus.OK.equals(carsresponse.getResponseStatus())) {
                return SystemMessage.SYSTEM_ERROR_RESOLUTION;
            }
            */

        } catch (ServiceException e) {
            EVExceptionHandler.logException("CARS action failed", e, request, log4j);
            context.getValidationErrors().addGlobalError(
                new SimpleError("Unable to process this request."));
        }
        
        
        return getResolution();
    }

    /**
     * Overridable method to determine landing page.  Defaults to carsresponse.jsp 
     * to show generic CARS response
     */
    protected Resolution getResolution() {
        CARSResponse carsresponse = context.getCarsResponse();
        try {
        	UserSession usersession = context.getUserSession();
            
        	// Don't show login box info with certain templates
            if (CARSTemplateNames.CARS_LOGIN.name().equals(carsresponse.getTemplateName()) ||
        		CARSTemplateNames.CARS_LOGOUT.name().equals(carsresponse.getTemplateName()) ||
        		CARSTemplateNames.CARS_LOGIN_FULL.name().equals(carsresponse.getTemplateName()) ||
        		CARSTemplateNames.CARS_PATH_CHOICE.name().equals(carsresponse.getTemplateName())) {
            	setShowLoginBox(false);
            }

            // Change the header to "null" on certain templates
            if (!usersession.getUser().isCustomer()) {
                carsresponse.setHeader(false);
            }
            
            // If the CARS page type is GENERIC we need to display template
            if (null != carsresponse.getPageType() &&(carsresponse.getPageType().equals(PageType.GENERIC) || carsresponse.getPageType().equals(PageType.POPUP))) {
                return new ForwardResolution(this.carsresponsepage);
            // Redirects can happen for 3rd party auth systems
            } else if (PageType.REDIRECT.equals(carsresponse.getPageType())) {
                if (StringUtils.isNotBlank(carsresponse.getShibbolethURL())){
                    return new RedirectResolution(carsresponse.getShibbolethURL());
                } else if (StringUtils.isNotBlank(carsresponse.getRedirectURL())) {
                    return new RedirectResolution(carsresponse.getRedirectURL());
                } else {
                    log4j.error("REDIRECT was indicated in CARS response but no valid redirect URL was found!");
                    return SystemMessage.SYSTEM_ERROR_RESOLUTION;
                }
            // Otherwise, just go to home page
            } else {
                return gotoStartPage(context.getUserSession());
            }
            
        } catch (Exception e) {
            // Log exception but continue...
            EVExceptionHandler.logException("Exception during error handling!!", e, context.getRequest(), log4j);
            return new ForwardResolution("/WEB-INF/pages/world/welcome.jsp");
        }
        
    }
    
    protected String getNextUrlFromRequest(EVActionBeanContext evcontext) {
		return evcontext.getRequest().getParameter("nexturl");
	}

    
    public static CARSResponse createLoginFullCARSResponse(EVActionBeanContext context){
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        UserSession userSession= context.getUserSession();
        
        CARSResponse carsresponse = null;
        
        try {
            CARSRequest carsReq = CARSRequestFactory.buildCARSRequest(CARSRequestType.LOGINFULL, context.getRequest(), userSession);
            carsReq.addRestRequestParameter(RESTRequestParameters.ANON_SHIBBOLETH, "true");
            CARSRequestProcessor carsrequestprocessor = new CARSRequestProcessor();
            carsresponse = carsrequestprocessor.process(carsReq, request, response, userSession);
            context.setCarsResponse(carsresponse);
        } catch (ServiceException e) {
            log4j.error("CARS authentication process failed: ", e);
            return null;
        }
        return carsresponse;
    }
}
