package org.ei.stripes.action;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.navigators.state.ResultNavigatorStateHelper;
import org.ei.exception.EVBaseException;
import org.ei.exception.SessionException;
import org.ei.session.UserSession;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.action.results.AnalyzeNavigatorAction;

/**
 * This action class handles all requests to update the user's session
 * values.  This is for things like facet open/close state, highlighting
 * on/off, etc...
 *
 * @author harovetm
 *
 */
@UrlBinding("/session/{$event}.url")
public class UpdateUserSession extends EVActionBean {

	private final static Logger log4j = Logger.getLogger(AnalyzeNavigatorAction.class);

	// Navigator state variables
    private String navid;
	private int open;
    private int order=999;
    private int more;

	// Generic boolean state variable.  Some user session options are simple
	// true/false, on/off values
	private boolean value;

	/**
	 * Handles updating the facet/navigator display state
	 *
	 * @return Resolution
	 */
	@HandlesEvent("navstate")
	@DontValidate
	public Resolution navstate() throws EVBaseException{

		EVActionBeanContext context = getContext();

		if (GenericValidator.isBlankOrNull(navid)) {
			log4j.warn("Attempt to update navigator state with empty navigator ID!");
			return null;
		}

		// Attempt to get the user's session
		UserSession usersession = context.getUserSession();
		if (usersession == null) {
			log4j.warn("UserSession object not available!");
			return null;
		}

		//
		// Update and return to controller
		//
		ResultNavigatorStateHelper navstatehelper = new ResultNavigatorStateHelper(usersession);
	    navstatehelper.updateNavState(navid);

		//
		// Update the user session
		//
		try {
			context.updateUserSession(usersession);
		} catch (SessionException e) {
			log4j.error("************ Unable to update navigator state: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Handles updating the facet/navigator display state
	 *
	 * @return Resolution
	 */
	@HandlesEvent("highlight")
	@DontValidate
	public Resolution highlight() {
		// Attempt to get the user's session
		UserSession usersession = context.getUserSession();
		if (usersession == null) {
			log4j.warn("UserSession object not available!");
			return null;
		}

		// Default for highlighting is ON so we ONLY put into session when false!
		if (value) {
			usersession.removeProperty(UserSession.HIGHLIGHT_STATE);
		} else {
			usersession.setHighlightState("false");
		}

		//
		// Update the user session
		//
		try {
			context.updateUserSession(usersession);
		} catch (SessionException e) {
			log4j.error("************ Unable to update higlight state: " + e.getMessage());
		}
		return null;
	}

	//
	//
	// GETTERS/SETTERS
	//
	//
	public int getOpen() {
		return open;
	}

	public void setOpen(int open) {
		this.open = open;
	}

	public String getNavid() {
		return navid;
	}

	public void setNavid(String navid) {
		this.navid = navid;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }

}
