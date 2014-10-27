package org.ei.service.cars.rest.request;

import javax.servlet.http.HttpServletRequest;

import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.session.UserSession;

public class CARSRequestProfileDisplay extends CARSRequestBase {

    /**
     * Constructor
     * 
     * @param httprequest
     * @param webUser
     * @throws ServiceException
     */
    public CARSRequestProfileDisplay(HttpServletRequest httprequest, UserSession userSession) throws ServiceException {
        super(httprequest, userSession);
        this.requestURI = CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) + CARSConfigVariables.getConstantAsString(CARSConfigVariables.PROFILE_DISPLAY_URI);
    }
    

    

}
