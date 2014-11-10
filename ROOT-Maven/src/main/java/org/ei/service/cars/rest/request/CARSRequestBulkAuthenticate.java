package org.ei.service.cars.rest.request;

import javax.servlet.http.HttpServletRequest;

import org.ei.exception.ServiceException;
import org.ei.session.UserSession;

public class CARSRequestBulkAuthenticate extends CARSRequestFromURL {

    public CARSRequestBulkAuthenticate(HttpServletRequest httprequest, UserSession userSession) throws ServiceException {
        super(httprequest, userSession);
    }

    public boolean isPostAsGet() {
        return true;
    }

}
