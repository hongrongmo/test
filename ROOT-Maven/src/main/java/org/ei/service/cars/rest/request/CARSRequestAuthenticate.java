package org.ei.service.cars.rest.request;

import javax.servlet.http.HttpServletRequest;

import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.service.cars.HttpMethod;
import org.ei.session.UserSession;

public class CARSRequestAuthenticate extends CARSRequestBase {
    
    /**
     * Constructor
     * 
     * @param httprequest
     * @param webUser
     * @throws ServiceException
     */
    public CARSRequestAuthenticate(HttpServletRequest httprequest, UserSession useSession) throws ServiceException {
        super(httprequest, useSession);
        this.requestURI = CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) + CARSConfigVariables.getConstantAsString(CARSConfigVariables.INITIAL_AUTHENTICATE_URI);
        // Authenticate requests will ALWAYS be GET requests but sometimes a request coincides with another 
        // CARS request.  For example, a request to Bulk authenticate someone when they are NOT already
        // authenticated.  This means the IP authentication check will try to run first but the incoming
        // http_method parameter for the Bulk request would change this to a POST!  So, just always set
        // to GET here after we've done the setup via the super() call.
        this.httpMethod = HttpMethod.GET;
    }
    
}
