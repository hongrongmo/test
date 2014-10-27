package org.ei.stripes.action.autoComplete;

import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.ISecuredAction;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.stripes.action.EVActionBean;

@UrlBinding("/ac.url")
public class AutoCompleteAction extends EVActionBean implements ISecuredAction {

    private final static Logger log4j = Logger.getLogger(AutoCompleteAction.class);
    public boolean enabled = true;

    /**
     * Override for the ISecuredAction interface. This ActionBean does AccessControl based on the incoming event.
     */
    @Override
    public IAccessControl getAccessControl() {
        //
        // Authenticate and terminate requests do NOT require previous
        // authentication!
        //

        return new NoAuthAccessControl();

    }

    @HandlesEvent("disable")
    public Resolution disabled() throws AutoCompleteException {
        getContext().getUserSession().setAutoCompleteEnabled(enabled);
        return new StreamingResolution("text/json", "{}");
    }


    public void setEnabled(boolean value) {
        this.enabled = value;
    }

}
