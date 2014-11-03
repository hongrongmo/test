package org.ei.struts.emetrics.customer.view;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to store information about a specific user. This class is used
 * so that the information is not scattered throughout the HttpSession.
 * Only this object is stored in the session for the user. This class
 * implements the HttpSessionBindingListener interface so that it can
 * be notified of session timeout and perform the proper cleanup.
 */
public class UserContainer implements Serializable {

	protected static Log log = LogFactory.getLog(UserContainer.class);

	//  Data about the user that is cached
	private UserView userView;
	/**
	 * The Locale object for the user. Although Struts stores a Locale for
	 * each user in the session, the locale is also maintained here.
	 */
	private Locale locale;

	/**
	 * Default Constructor
	 */
	public UserContainer() {
	}

	/**
	 * Set the locale for the user.
	 */
	public void setLocale(Locale aLocale) {
		locale = aLocale;
	}

	/**
	 * Retrieve the locale for the user.
	 */
	public Locale getLocale() {
		return locale;
	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView newView) {
		userView = newView;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("\n UserContainer \n ");
		sb.append("\n Locale: " + ((locale != null) ? (locale.toString()) : "null"));
		sb.append("\n UserView: " + ((userView != null) ? (userView.toString()) : "null"));
		return sb.toString();
	}
}