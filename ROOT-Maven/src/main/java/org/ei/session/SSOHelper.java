package org.ei.session;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ei.service.cars.CARSAuthenticationConstants;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.service.cars.CARSStringConstants;

public class SSOHelper {

	private final static Logger log4j = Logger.getLogger(SSOHelper.class);
	
	public static boolean isSSOCookiePresent(HttpServletRequest request) {
		String ssoCookie = BaseCookie.getCookieValue(request,CARSAuthenticationConstants.SSO_COOKIE.value());
		return (null != ssoCookie);
	}
	
	/**
	 * This method decodes the SSO key obtained from the cookie or URL.
	 * 
	 * @param request - The http servlet request.
	 * @return String - the decoded sso key value
	 */
	public static String decodeSSOCookieKey(HttpServletRequest request) {
		String encodedSSOKey = BaseCookie.getCookieValue(request, CARSAuthenticationConstants.SSO_COOKIE.value());
		String decodedSSOKey = null;
		
		try {
			if (null != encodedSSOKey) {
				decodedSSOKey = URLDecoder.decode(encodedSSOKey, CARSStringConstants.URL_ENCODE.value());
			}
		} catch (UnsupportedEncodingException uee) {
			log4j.warn("Problem in decoding the SSO Key using " + CARSStringConstants.URL_ENCODE.value() + uee.getMessage());
		}
		return decodedSSOKey;
	}
    

	public static String getCookieKey(HttpServletRequest request) {
		String encodedSSOKey = BaseCookie.getCookieValue(request, CARSAuthenticationConstants.SSO_COOKIE.value());
			return encodedSSOKey;
	}
	

	public static String getSSOCookieValue(HttpServletRequest request) {
		String ssoCookieValue = null;
		Cookie[] allCookies = request.getCookies();
		if (allCookies == null || allCookies.length <= 0) {
			return null;
		}
		for (Cookie cookie : allCookies) {
			if (null != cookie && CARSAuthenticationConstants.SSO_COOKIE.value().equals(cookie.getName())) {
				ssoCookieValue = getCookieKey(request);
				break;
			}
		}
		return ssoCookieValue;
	}
	


	public static boolean isSSOTimeIntervalPassed(Long SSOLastAccessTime) {
		long currentTimestamp = System.currentTimeMillis();
		long sessionTimestamp = SSOLastAccessTime;
		
		if (sessionTimestamp != 0) {
			long timeDiff = (currentTimestamp - sessionTimestamp) / (60 * 1000);
			long revalidateInterVal = CARSConfigVariables.getConstantAsInt(CARSConfigVariables.SSO_REVALIDATION_TIME);
			
			if (revalidateInterVal < timeDiff) {
				return true;
			}
		}
		return false;
	}
	
	
	 public static long getSSOLastAccessTime(UserSession userSession) {
			Object ssoLAccTimes = userSession.getCarsLastSucessAccessTime();
			Long lastAccessTime = null;
			if (ssoLAccTimes != null) {
				lastAccessTime = (Long) ssoLAccTimes;
				if (null != lastAccessTime) {
					return lastAccessTime.longValue();
				}
			}
			return 0;
		}
	
	public static boolean isStateChanged(HttpServletRequest request, String browserSSOKeyFromSession) {
		String ssoKeyInCookie = decodeSSOCookieKey(request);
		String ssoKeyInSession = null;
		
		if (null != browserSSOKeyFromSession) {
			ssoKeyInSession = browserSSOKeyFromSession;
		}
		if (null != ssoKeyInCookie && null != ssoKeyInSession && !ssoKeyInCookie.equals(ssoKeyInSession)) {
			return true;
		}
		return false;
	}
	

	public static boolean isPessimisticLogin() {
		return CARSConfigVariables.getConstantAsBoolean(CARSConfigVariables.SSO_REDIRECT_AUTH);
	}
}
