package org.ei.service.cars.rest.request;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.service.cars.HttpMethod;
import org.ei.service.cars.RESTRequestParameters;
import org.ei.session.BaseCookie;
import org.ei.session.UserSession;

public class CARSRequestTerminate extends CARSRequestBase {

    /**
     * Constructor
     * 
     * @param httprequest
     * @param webUser
     * @throws ServiceException
     */
    public CARSRequestTerminate(HttpServletRequest httprequest, UserSession userSession) throws ServiceException {
    	
    	//super(httprequest, userSession);
    	this.httprequest = httprequest;
    	this.userSession = userSession;
    	
    	// Just need to pass authtoken to CARS.  Try to retrieve from user session first
    	if (userSession != null && userSession.getUser() != null && !GenericValidator.isBlankOrNull(userSession.getUser().getAuthToken())) {
    		this.getRestRequestParams().put(RESTRequestParameters.AUTH_TOKEN, userSession.getUser().getAuthToken());
    	} else if (!GenericValidator.isBlankOrNull(BaseCookie.getAuthTokenCookie(httprequest))) {
        	 this.getRestRequestParams().put(RESTRequestParameters.AUTH_TOKEN, BaseCookie.getAuthTokenCookie(httprequest));
        } else {
        	return;
        }
        
    	this.appendRESTRequestCommonParams();
    	this.requestURI = CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) + CARSConfigVariables.getConstantAsString(CARSConfigVariables.TERMINATE_URI);
        this.setHTTPMethod(HttpMethod.DELETE);
    }
    
    
    private boolean isAuthTokenEmptyForRestRequestParams() {
		return StringUtils.isBlank((String) this.getRestRequestParams().get(RESTRequestParameters.AUTH_TOKEN));
	}
    /**
     * Add request parameters for terminate action
     * @param carsrequest
     */
   /* protected void appendContextualParams() {
        this.addRestRequestParameter(RESTRequestParameters.PLATFORM_SITE,
            ANEServiceHelper.getANEServiceConfigurationProperty(ANEServiceConstants.PLATFORM_CODE) + "/" + ANEServiceHelper.getANEServiceConfigurationProperty(ANEServiceConstants.SITE_IDENTIFIER));
    }*/

   
}
