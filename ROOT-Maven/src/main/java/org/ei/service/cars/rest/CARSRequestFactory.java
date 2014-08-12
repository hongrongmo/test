package org.ei.service.cars.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSRequestType;
import org.ei.service.cars.rest.request.CARSRequest;
import org.ei.service.cars.rest.request.CARSRequestAuthenticate;
import org.ei.service.cars.rest.request.CARSRequestBulkAuthenticate;
import org.ei.service.cars.rest.request.CARSRequestFromURL;
import org.ei.service.cars.rest.request.CARSRequestIpAuthenticate;
import org.ei.service.cars.rest.request.CARSRequestLoginFull;
import org.ei.service.cars.rest.request.CARSRequestNext;
import org.ei.service.cars.rest.request.CARSRequestProfileDisplay;
import org.ei.service.cars.rest.request.CARSRequestTerminate;
import org.ei.session.UserSession;

public class CARSRequestFactory {
	private final static Logger log4j = Logger.getLogger(CARSRequestFactory.class);


    /**
     * Build a new CARS request object from request type
     * @param requesttype
     * @param request
     * @param authtoken
     * @return
     * @throws ServiceException
     */
    public static CARSRequest buildCARSRequest(CARSRequestType requesttype, HttpServletRequest request, UserSession userSession) throws ServiceException {
        log4j.info("Building CARS request object, type = " + requesttype.value());

		// Ensure request is passed in
		if (request == null) {
			throw new IllegalArgumentException("HttpServletRequest object is required!");
		}

        // Build CARS request based in incoming request type
		CARSRequest carsrequest = null;
        if (CARSRequestType.URLBASED == requesttype) {
            carsrequest = new CARSRequestFromURL(request, userSession);
        } else if (CARSRequestType.AUTHENTICATE == requesttype) {
            carsrequest = new CARSRequestAuthenticate(request, userSession);
        } else if (CARSRequestType.IPAUTHENTICATE == requesttype) {
            carsrequest = new CARSRequestIpAuthenticate(request, userSession);
        } else if (CARSRequestType.LOGINFULL == requesttype) {
            carsrequest = new CARSRequestLoginFull(request, userSession);
        } else if (CARSRequestType.TERMINATE == requesttype) {
            carsrequest = new CARSRequestTerminate(request, userSession);
        } else if (CARSRequestType.PROFILEDISPLAY == requesttype) {
            carsrequest = new CARSRequestProfileDisplay(request, userSession);
        } else if (CARSRequestType.BULKAUTHENTICATE == requesttype) {
            // TEMPORARY FIX!  CARS currently needs the authtoken cleared out on non-IP bulk
            if (userSession.getUser() != null) {
                userSession.getUser().setAuthToken(null);
            }
            carsrequest = new CARSRequestBulkAuthenticate(request, userSession);
		} else {
			throw new IllegalArgumentException("Invalid request type!");
		}
        carsrequest.setRequestType(requesttype);
        return carsrequest;
	}


    /**
     * Build a CARSRequset from the "next" URI.  This can be returned from CARS in
     * certain requests (forgot password, for example).
     *
     * @param nexturi
     * @return
     * @throws ServiceException
     */
    public static CARSRequest buildCARSRequest(HttpServletRequest httprequest, String nexturi, UserSession usersession) throws ServiceException {
        log4j.info("Building CARS request object, nexturi = " + nexturi);
        return new CARSRequestNext(httprequest, nexturi, usersession);
    }
}
