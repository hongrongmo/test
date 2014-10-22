package org.ei.service.cars.rest.request;

import javax.servlet.http.HttpServletRequest;

import org.ei.config.EVProperties;
import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.service.cars.HttpMethod;
import org.ei.service.cars.RESTRequestParameters;
import org.ei.session.UserSession;

public class CARSRequestLoginFull extends CARSRequestBase {

    /**
     * Constructor
     *
     * @param httprequest
     * @param webUser
     * @throws ServiceException
     */
    public CARSRequestLoginFull(HttpServletRequest httprequest, UserSession userSession) throws ServiceException {
        super(httprequest, userSession);
        this.requestURI = CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) + CARSConfigVariables.getConstantAsString(CARSConfigVariables.LOGIN_FULL_URI);
        this.addRestRequestParameter(RESTRequestParameters.LOGIN_FULL_CANCEL_URI, EVProperties.getProperty(EVProperties.LOGIN_FULL_CANCEL_URI));
        this.setHTTPMethod(HttpMethod.GET);
    }

}
