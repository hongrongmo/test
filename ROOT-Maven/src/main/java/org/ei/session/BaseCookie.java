package org.ei.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.service.cars.CARSAuthenticationConstants;

public class BaseCookie {

	public static final String COOKIE_JUST_REMOVED = "REMOVED";

	public static final String VALUE_SEPARATOR = "|";

	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] currentCookies = request.getCookies();

		if (currentCookies == null) {
			return null;
		}

		for (int i = 0; i < currentCookies.length; i++) {
			if (currentCookies[i].getName().equals(cookieName)) {
				if (currentCookies[i].getValue() != null
						&& !(currentCookies[i].getValue().trim().length() == 0 || currentCookies[i].getValue().equals(COOKIE_JUST_REMOVED))) {
					return currentCookies[i].getValue();
				}
			}
		}
		return null;
	}

	public static String updateCookieValue(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int expirationValue) {
		Cookie[] currentCookies = request.getCookies();
		Cookie updateCookie = new Cookie(cookieName, cookieValue);
		updateCookie.setMaxAge(expirationValue);

		if (currentCookies == null) {
			return null;
		}
		for (int i = 0; i < currentCookies.length; i++) {
			if (currentCookies[i].getName().equals(cookieName)) {
				if (currentCookies[i].getValue() != null
						&& !(currentCookies[i].getValue().trim().length() == 0 || currentCookies[i].getValue().equals(COOKIE_JUST_REMOVED))) {
					updateCookie.setValue(cookieValue);
					String contextRoot = request.getContextPath();
					if (contextRoot != null && contextRoot.length() > 0) {
						updateCookie.setPath(contextRoot);
					} else {
						updateCookie.setPath("/");
					}
				}
				break;
			}
		}
		response.addCookie(updateCookie);
		return null;
	}

	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int expirationValue) {
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(expirationValue);

		String contextRoot = request.getContextPath();

		if (contextRoot != null && contextRoot.length() > 0) {
			cookie.setPath(contextRoot);
		} else {
			cookie.setPath("/");
		}
		response.addCookie(cookie);
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, COOKIE_JUST_REMOVED);
		cookie.setMaxAge(0);

		String contextRoot = request.getContextPath();
		if (contextRoot != null && contextRoot.length() > 0) {
			cookie.setPath(contextRoot);
		} else {
			cookie.setPath("/");
		}
		response.addCookie(cookie);
	}

	public static String getAuthTokenCookie(HttpServletRequest request) {
		return BaseCookie.getCookieValue(request, CARSAuthenticationConstants.AUTHTOKEN_COOKIE_NAME.value());
	}

}
