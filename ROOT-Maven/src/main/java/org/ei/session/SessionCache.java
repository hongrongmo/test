package org.ei.session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.personalization.EVWebUser;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.util.LRUTable;

public final class SessionCache {

	private String authURL;
	private String appName;
	private static SessionCache instance;
	private LRUTable cache = new LRUTable(100);
	private Logger log4j = Logger.getLogger(SessionCache.class);

	/**
	 * Private constructor taking authURl and application name
	 * 
	 * @param authURL
	 *            URL for the authentication service
	 * @param appName
	 *            Application name
	 */
	private SessionCache(String authURL, String appName) {
		this.authURL = authURL;
		this.appName = appName;
	}

	/**
	 * This method should be called once by the controller during initialization
	 * 
	 * @param authURL
	 *            URL for the authentication service
	 * @param appName
	 *            Application name
	 * @return SessionCache object
	 * @throws SessionException
	 */
	public static synchronized SessionCache init(String authURL, String appName) throws SessionException {
		if (instance != null) {
			throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, "SessionCache has already been initialized!");
		}

		instance = new SessionCache(authURL, appName);

		return instance;
	}

	/**
	 * This method is called anytime after the singleton is initialized.
	 * 
	 * @return
	 */
	public static SessionCache getInstance() {
		return instance;
	}

	/**
	 * Called when the user logs out of EV. This will trigger a call to the
	 * authentication service to remove the current user session .
	 * 
	 * @param userSession
	 * @return
	 * @throws SessionException
	 */
	public UserSession logout(UserSession userSession) throws SessionException {
		HttpURLConnection httpConn = null;
		InputStream inStream = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append(authURL).append("?rt=" + SessionService.REQUEST_LOGOUT + "&si=").append(userSession.getSessionID().getID());
			URL url = new URL(buf.toString());
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setDoOutput(true);

			inStream = httpConn.getInputStream();
			while (inStream.read() != -1) {
			}

		} catch (Exception e) {
			throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (Exception e) {

			}

			if (httpConn != null) {
				httpConn.disconnect();
			}
		}

		userSession.setUser(new EVWebUser());
		userSession.setProperties(new Properties());
		(userSession.getSessionID()).incrementVersion();
		if (cache.containsKey(userSession.getSessionID().getID())) {
			cache.put(userSession.getSessionID().getID(), userSession);
		}

		return userSession;
	}

	/**
	 * Touch the current session to mark it as active.
	 * 
	 * @param sessionID
	 * @throws SessionException
	 */
	public void touch(SessionID sessionID) throws SessionException {
		HttpURLConnection httpConn = null;
		InputStream inStream = null;
		try {

			long time = System.currentTimeMillis();
			if (cache.containsKey(sessionID.getID())) {
				UserSession usession = (UserSession) cache.get(sessionID.getID());
				usession.setLastTouched(time);
			}

			StringBuffer buf = new StringBuffer();
			buf.append(authURL).append("?rt=" + SessionService.REQUEST_TOUCH + "&si=").append(sessionID.getID());
			URL url = new URL(buf.toString());
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setDoOutput(true);

			inStream = httpConn.getInputStream();
			while (inStream.read() != -1) {

			}

		} catch (Exception e) {
			throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (Exception e) {

			}

			if (httpConn != null) {
				httpConn.disconnect();
			}
		}
	}

	/**
	 * Validate a customer IP address
	 * 
	 * @param ipAddress
	 * @return
	 * @throws SessionException
	 */
	public String validateCustomerIP(String ipAddress) throws SessionException {
		HttpURLConnection httpConn = null;
		InputStream inStream = null;
		String customerID = null;
		try {
			StringBuffer buf = new StringBuffer();

			buf.append(authURL).append("?rt=" + SessionService.REQUEST_VALIDATEIP + "&ip=").append(ipAddress).append("&ap=").append(this.appName);
			URL url = new URL(buf.toString());
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setDoOutput(true);
			inStream = httpConn.getInputStream();
			while (inStream.read() != -1) {
			}
			customerID = httpConn.getHeaderField("CUSTOMERID");
		} catch (Exception e) {
			throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (Exception e) {

			}

			if (httpConn != null) {
				httpConn.disconnect();
			}
		}

		return customerID;
	}

	/**
	 * Update the user session - marks it as 'touched'
	 * 
	 * @param uSession
	 * @return
	 * @throws SessionException
	 */
	public UserSession updateUserSession(UserSession uSession) throws SessionException {

		InputStream in = null;
		HttpURLConnection ucon = null;

		SessionID sesID = uSession.getSessionID();
		uSession.setSessionID(sesID);
		uSession.setTouched(true);
		uSession.setLastTouched(System.currentTimeMillis());

		if (cache.containsKey(sesID.getID())) {
			cache.put(sesID.getID(), uSession);
		}

		try {
			StringBuffer buf = new StringBuffer();
			buf.append(authURL).append("?");
			buf.append("rt=" + SessionService.REQUEST_UPDATE + "&ap=").append(this.appName);
			URL url = new URL(buf.toString());
			ucon = (HttpURLConnection) url.openConnection();
			ucon.setRequestMethod("GET");
			ucon.setDoOutput(true);

			Properties props = uSession.unloadToProperties();
			Enumeration<Object> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				ucon.setRequestProperty(key, props.getProperty(key));
			}

			in = ucon.getInputStream();
			while (in.read() != -1) {

			}

		} catch (Exception e) {
			throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			try {
				if (ucon != null) {
					ucon.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return uSession;
	}

	/**
	 * Retrieve the current User session. Will build a new session if one is not
	 * present or is expired.
	 * 
	 * @param sessionID
	 * @param ipAddress
	 * @param refURL
	 * @param username
	 * @param password
	 * @param entryToken
	 * @return
	 * @throws SessionException
	 */
	public UserSession getUserSession(SessionID sessionID, String ipAddress, String refURL, String username, String password, String entryToken)
			throws SessionException {
		UserSession nsession = null;

		if (username == null && entryToken == null && sessionID != null && cache.containsKey(sessionID.getID())) {
			String id = sessionID.getID();
			int version = sessionID.getVersionNumber();
			long currentTime = System.currentTimeMillis();
			nsession = (UserSession) cache.get(id);
			int version2 = (nsession.getSessionID()).getVersionNumber();
			long lastTouched = nsession.getLastTouched();
			long expireTime = nsession.getExpireIn();

			if (version == version2 && (currentTime - lastTouched) < expireTime) {
				nsession.setStatus(SessionStatus.OLD_FROM_CACHE);
				return nsession;
			} else {
				nsession = null;
			}
		}

		HttpURLConnection ucon = null;
		BufferedReader in = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append(authURL).append("?");
			buf.append("rt=" + SessionService.REQUEST_GET + "&ap=").append(this.appName).append("&");
			buf.append("ip=").append(ipAddress);

			if (!GenericValidator.isBlankOrNull(refURL)) {
				buf.append("&rf=").append(java.net.URLEncoder.encode(refURL, "UTF-8"));
			}

			if (!GenericValidator.isBlankOrNull(username)) {
				buf.append("&un=").append(username);
			}

			if (!GenericValidator.isBlankOrNull(entryToken)) {
				buf.append("&et=").append(entryToken);
			}

			if (!GenericValidator.isBlankOrNull(password)) {
				buf.append("&pa=").append(password);
			}

			if (sessionID != null) {
				buf.append("&si=" + sessionID.getID());
				buf.append("&sv=" + sessionID.getVersionNumber());
			}

			log4j.debug(buf.toString());

			URL url = new URL(buf.toString());
			ucon = (HttpURLConnection) url.openConnection();
			in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
			int j = 1;
			Properties sessionProps = new Properties();
			while (true) {
				String hkey = ucon.getHeaderFieldKey(j);

				if (hkey == null) {
					break;
				}

				// Get session information.
				if (hkey.indexOf("SESSION") > -1) {
					sessionProps.put(hkey, ucon.getHeaderField(j));
				}

				++j;
			}
			nsession = new UserSession();
			nsession.loadFromProperties(sessionProps);

			/*
			 * Don't cache the if there is an entry token.
			 */
			if (nsession.getProperty("ENTRY_TOKEN") == null) {
				cache.put(nsession.getSessionID().getID(), nsession);
			}
		} catch (Exception e) {
			throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, e);
		} finally {

			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			try {
				if (ucon != null) {
					ucon.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return nsession;
	}

}
