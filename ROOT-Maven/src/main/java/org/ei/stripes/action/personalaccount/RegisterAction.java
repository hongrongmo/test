package org.ei.stripes.action.personalaccount;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.service.cars.CARSRequestType;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.PageType;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.session.SSOHelper;

/**
 * This class is used for registration and profile modification.
 * 
 * @author harovetm
 *
 */
@UrlBinding("/customer/profile{$event}.url")
public class RegisterAction extends CARSActionBean {
	
	private final static Logger log4j = Logger.getLogger(RegisterAction.class);

   @Override
    public IAccessControl getAccessControl() {
       String bulkid = context.getRequest().getParameter("bulk_id");
       if ("/association".equals(context.getEventName()) || !GenericValidator.isBlankOrNull(bulkid)) {
           return new NoAuthAccessControl();
       } else {
           return super.getAccessControl();
       }
    }

    /**
     * Default handler - displays the registration page
     * 
     * @return Resolution
     */
    @DefaultHandler
    @DontValidate
    public Resolution display() {
        String eventname = context.getEventName();
        
        if(StringUtils.isNotBlank(getNextUrlFromRequest(context))){
        	context.getUserSession().setNextUrl(getNextUrlFromRequest(context));
        }
        
        if ("/display".equals(eventname)) {
            log4j.info("Displaying registration page (CARS)");
            requesttype = CARSRequestType.PROFILEDISPLAY;
        } else {
            log4j.info("Handling event: " + eventname);
        }
        return super.display();
    }

    @HandlesEvent("/association") 
    public Resolution handlebulk() {
        requesttype = CARSRequestType.BULKAUTHENTICATE;
        return super.display();
    }
    
    /**
     * Overridable method to determine landing page.  Defaults to carsresponse.jsp 
     * to show generic CARS response
     */
    protected Resolution getResolution() {
        CARSResponse carsresponse = context.getCarsResponse();
        if (carsresponse.getPageType() != PageType.GENERIC) {
            return new RedirectResolution(CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI) +
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.SETTINGS_URI) +
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_URL_EXTN));
        } else {
            return super.getResolution();
        }
    }
    
    @After
    public void writeSSOCookieSinceValueChangedAfterLogin() {
		if(SSOHelper.isStateChanged(getRequest(), getBrowserSSOKey())){
			Cookie[] cookies = getRequest().getCookies();

			if(cookies != null ) {  
		        for( int i=0; i<cookies.length; i++ ) {  
		        Cookie cookie = cookies[i];
					if(CARSStringConstants.ACW.value().equals(cookie.getName())) {  
			        	cookie.setValue(getUpdatedCookieFromCARSRequest());  
			        	cookie.setMaxAge(-1);
			        	cookie.setPath("/");
			        	cookie.setDomain(".engineeringvillage.com");
			        	context.getResponse().addCookie(cookie);
			        	break;
			        }
		        }
			}
		}
	}
    
    
    private String getUpdatedCookieFromCARSRequest()  {
		try {
			return URLEncoder.encode(getBrowserSSOKey(), CARSStringConstants.URL_ENCODE.value());
		} catch (UnsupportedEncodingException e) {
			log4j.warn("Problem in decoding the SSO Key using " + CARSStringConstants.URL_ENCODE.value() + e.getMessage());
		}
		return "";
	}

	private String getBrowserSSOKey() {
		return context.getUserSession().getBrowserSSOKey();
	}


}
