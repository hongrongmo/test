package org.ei.stripes.action.personalaccount;

import java.net.MalformedURLException;
import java.util.Map;

import javax.servlet.http.Cookie;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.controller.Customer;
import org.ei.service.cars.CARSTemplateNames;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVPathUrl;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.action.WebAnalyticsEventProperties;
import org.ei.web.cookie.CookieHandler;

@UrlBinding("/home.url")
public class HomeAction extends CARSActionBean {

	private final static Logger log4j = Logger.getLogger(HomeAction.class);

	public static final Resolution HOME_RESOLUTION = new RedirectResolution(EVPathUrl.EV_HOME.value());
    public static final Resolution HOME_AUTHERROR_RESOLUTION = new RedirectResolution(EVPathUrl.EV_HOME.value() + "?autherror=t");
    public static final Resolution HOME_FWD_RESOLUTION = new ForwardResolution("/WEB-INF/pages/world/welcome.jsp");

    protected String error; // Indicates if error has occurred
    protected boolean login; // Indicates if this is a login request
    protected boolean redir; // Indicates if this is a redirect to home page (no
                             // access)
    protected boolean cancel; // User should go to cancel URL in session if possible
    protected boolean trialform; // Indicates if we should display the trial
                                 // form no matter what

	/**
	 * Override for the ISecuredAction interface.  This ActionBean does NOT
	 * require authentication!
	 */
	@Override
	public IAccessControl getAccessControl() {
		return new NoAuthAccessControl();
	}

    /**
     * Displays the home page. If the user is authenticated redirect to
     * appropriate start page
     * 
     * @return
     * @throws MalformedURLException 
     */

    @DefaultHandler
    @DontValidate
    public Resolution handleRequest() throws MalformedURLException {

        setRoom(ROOM.blank);

        UserSession userSession= context.getUserSession();
        if (userSession == null){ 
            log4j.error("No User Session found!");
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        }   
        IEVWebUser user = userSession.getUser();
        
        //
        // Figure out where to go
        //
        CARSResponse carsresponse = context.getCarsResponse();
        if (user.isCustomer()) {
            if (isDepartmentNotSelected()) {
                return new ForwardResolution("/WEB-INF/pages/customer/departmenttracking.jsp");
            }
            return gotoStartPage(userSession, this.cancel);
        } else if (carsresponse != null && CARSTemplateNames.CARS_PATH_CHOICE.name().equals(carsresponse.getTemplateName())) {
            return super.getResolution();
        } else {
            // Request the LOGIN_FULL template from CARS and display
            createLoginFullCARSResponse(context);
            // Add a GA event for welcome page
        	createWebEvent(WebAnalyticsEventProperties.CAT_HOME,WebAnalyticsEventProperties.ACTION_WELCOME,"");
            this.carsresponsepage = "/WEB-INF/pages/world/welcome.jsp";
            return super.getResolution();
        }
    }
    

    protected boolean isDepartmentNotSelected() {
        Map<String, Customer> dmap = getDepartmentMap();
        return (!getCookieMap().containsKey("DEPARTMENT") && 
            (dmap != null && dmap.containsKey(getCustomerID())) && 
            (getRequest().getParameter("SYSTEM_DEPARTMENT") == null));
    }

    protected Map<String, Cookie> getCookieMap() {
        return CookieHandler.getCookieMap(getRequest());
    }

    protected Map<String, Customer> getDepartmentMap() {
        return CookieHandler.getDepartmentMap(getRequest());
    }

    protected String getCustomerID() {
        return getCustomerFromSession().getCustomerID();
    }

    protected IEVWebUser getCustomerFromSession() {
        return getContext().getUserSession().getUser();
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean isRedir() {
        return redir;
    }

    public void setRedir(boolean redir) {
        this.redir = redir;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isTrialform() {
        return trialform;
    }

    public void setTrialform(boolean trialform) {
        this.trialform = trialform;
    }

}
