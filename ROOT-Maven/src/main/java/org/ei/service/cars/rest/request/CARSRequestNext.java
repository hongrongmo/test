package org.ei.service.cars.rest.request;

import javax.servlet.http.HttpServletRequest;

import org.ei.exception.ServiceException;
import org.ei.session.UserSession;

/**
 * This is a special CARS request type meant to be used when there is a 
 * followup URL to go to.  It does NOT require an HTTP request or user
 * objects
 * 
 * @author harovetm
 *
 */
public class CARSRequestNext extends CARSRequestBase {
    
    /**
     * Constructor
     * 
     * @param httprequest
     * @param webUser
     * @throws ServiceException
     */
    public CARSRequestNext(HttpServletRequest httprequest, String nexturi, UserSession usersession) throws ServiceException {
        super(httprequest, usersession);
        this.requestURI = nexturi;
    }

    @Override
    public void appendRESTRequestParams() throws ServiceException {
        appendRESTRequestCommonParams();
        return;
    }

    @Override
    public void appendUserInfoInToRESTrequestParams() {
        return;
    }
    
}
