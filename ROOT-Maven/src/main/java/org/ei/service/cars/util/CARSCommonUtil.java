package org.ei.service.cars.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ei.domain.personalization.EVWebUser;
import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSAuthenticationConstants;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.service.cars.XPathEnum;
import org.ei.service.cars.rest.util.XMLUtil;
import org.ei.session.BaseCookie;

import com.elsevier.webservices.schemas.csas.constants.types.v7.UserAnonymityType;



public class CARSCommonUtil {
	

	public static boolean isUserInfoAvailable(String mimeResp) throws ServiceException {
		String authenticateUsrResp = null;
		if (StringUtils.isNotBlank(mimeResp)) {
			
			authenticateUsrResp = XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.CARS_AUTHENTICATE_USER_RESPONSE_TYPE.value());
			if (StringUtils.isNotBlank(authenticateUsrResp)) {
				return true;
			}
		}
		return false;
	}
	
//	
//	public static boolean isUserAlreadyExistsInSession() {
//	
//		if(null != EVSessionHelper.getSessionAttribute(EVSessionKeys.USER_OBJECT)){
//			return true;
//		}
//		return false;
//	}
//
//    public static EVWebUser getExistingUserFromSession() {
//        
//        if (null != EVSessionHelper.getSessionAttribute(EVSessionKeys.USER_OBJECT)){
//            return (EVWebUser) EVSessionHelper.getSessionAttribute(EVSessionKeys.USER_OBJECT);
//        }
//        return null;
//    }
//    
	public static boolean isExistingUserLoggedIn(EVWebUser webUser) {
		
		if (null != webUser) {
			return isValidUserInResponse(webUser.getUserAnonymity());
		}
		return false;
	}
	
	public static boolean isValidUserInResponse(String userAnonymity) {
		
		if (null != userAnonymity && UserAnonymityType.INDIVIDUAL.value().equals(userAnonymity)) {
			return true;
		}
		return false;
	}
	
	public static void handleCarsCookie(HttpServletRequest httpReq, HttpServletResponse httpResp, String carsCookie) {
		
		if (StringUtils.isNotBlank(carsCookie)) {
			if (isCarCookieAvailable(httpReq)) {
				BaseCookie.updateCookieValue(httpReq, httpResp,CARSAuthenticationConstants.CARS_COOKIE_NAME.value(), carsCookie, getCarsCookieMaxAge());
			} else {
				BaseCookie.setCookie(httpReq, httpResp, CARSAuthenticationConstants.CARS_COOKIE_NAME.value(), carsCookie, getCarsCookieMaxAge());
		}
		}
	}

	
	public static boolean isCarsRequest(HttpServletRequest request) {
		
		if (request.getRequestURI().contains(CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI))) {
			return true;
		}
		return false;
	}
	

	private static int getCarsCookieMaxAge() {
		return getCarsCookieExpirationTimeFromConfiguration() * 24 * 60 * 60;
	}


	private static int getCarsCookieExpirationTimeFromConfiguration() {
		return CARSConfigVariables.getConstantAsInt(CARSConfigVariables.CARS_COOKIE_EXPIRATION);
	}


	private static boolean isCarCookieAvailable(HttpServletRequest httpReq) {
		return BaseCookie.getCookieValue(httpReq, CARSAuthenticationConstants.CARS_COOKIE_NAME.value()) != null ? true : false;
	}

	public static void handleAuthTokenCookie(HttpServletRequest request, HttpServletResponse response, String authToken) {
		if (StringUtils.isNotBlank(authToken)) {
			if (isAuthTokenCookieAvailable(request)) {
				BaseCookie.updateCookieValue(request, response, CARSAuthenticationConstants.AUTHTOKEN_COOKIE_NAME.value(), authToken, getAuthTokenCookieMaxAge());
			} else {
				BaseCookie.setCookie(request, response, CARSAuthenticationConstants.AUTHTOKEN_COOKIE_NAME.value(), authToken, getAuthTokenCookieMaxAge());
			}
		}
	}
	
	private static int getAuthTokenCookieMaxAge() {
		return CARSConfigVariables.getConstantAsInt(CARSConfigVariables.AUTH_TOKEN_COOKIE_EXPIRATION);
	}
	
	private static boolean isAuthTokenCookieAvailable(HttpServletRequest httpReq) {
		return BaseCookie.getCookieValue(httpReq, CARSAuthenticationConstants.AUTHTOKEN_COOKIE_NAME.value()) != null ? true : false;
	}
}	
	
	

