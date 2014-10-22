package org.ei.stripes.action.personalaccount;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;

/**
 * This class services requests for the Athens/Institution page  
 *   
 * @author harovetm
 *
 */
@UrlBinding("/customer/institutionchoice.url")
public class InstitutionChoiceAction extends CARSActionBean {
	
	private final static Logger log4j = Logger.getLogger(InstitutionChoiceAction.class);

    /**
     * Override for the ISecuredAction interface. This ActionBean does AccessControl
     * based on the incoming event.
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    /**
     * Default handler - displays the registration page
     * 
     * @return Resolution
     */
    @DefaultHandler
    @DontValidate
    public Resolution handle() {
        return super.display();
    }

}
