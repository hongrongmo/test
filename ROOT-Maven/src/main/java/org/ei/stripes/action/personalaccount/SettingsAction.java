package org.ei.stripes.action.personalaccount;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NormalAuthRequiredAccessControl;

/**
 * This class services requests for the Settings page.  
 * @author harovetm
 *
 */
@UrlBinding("/customer/settings.url")
public class SettingsAction extends CARSActionBean {
	
	private final static Logger log4j = Logger.getLogger(SettingsAction.class);

	
	@Override
    public IAccessControl getAccessControl() {
        return new NormalAuthRequiredAccessControl();
    }
	
    /**
     * Default handler - displays the registration page
     * 
     * @return Resolution
     */
    @DefaultHandler
    @DontValidate
    public Resolution handle() {
        // Get the resolution from the base class
        Resolution resolution = super.display();
        // Override the Room setting
        setRoom(ROOM.mysettings);
        // Return!
        return resolution;
    }

}
