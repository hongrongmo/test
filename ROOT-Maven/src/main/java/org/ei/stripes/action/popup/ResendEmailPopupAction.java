package org.ei.stripes.action.popup;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.stripes.action.EVActionBean;

@UrlBinding("/popup.url")
public class ResendEmailPopupAction extends EVActionBean {
	
	 public IAccessControl getAccessControl() {
	        return new NoAuthAccessControl();
	    }

	    @DefaultHandler
	    public Resolution display() {
	      
	        return getResolution();
	        
	    }
	    /**
	     * Defaults to reactivate.jsp 
	     */
	    protected Resolution getResolution() {
	        return new ForwardResolution("/WEB-INF/pages/popups/resendemail.jsp");
	    }
	    
	    public String getEmail(){
	    	return context.getUserSession().getUser().getEmail();
	    }
}
