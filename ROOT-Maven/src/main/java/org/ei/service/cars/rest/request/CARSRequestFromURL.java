package org.ei.service.cars.rest.request;

import javax.servlet.http.HttpServletRequest;

import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.session.UserSession;

public class CARSRequestFromURL extends CARSRequestBase {

    /**
     * Constructor
     * 
     * @param httprequest
     * @param webUser
     * @throws ServiceException
     */
    public CARSRequestFromURL(HttpServletRequest httprequest, UserSession userSession) throws ServiceException {
        super(httprequest, userSession);
        // Create request from current URL
        String urlextn = CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_URL_EXTN);
        this.requestURI = this.httprequest.getRequestURI().replace(urlextn, "");
    }
    
}
