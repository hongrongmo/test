package org.ei.stripes.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.session.UserPreferences;

@UrlBinding("/activateaccess")
public class SelfManraRedirectAction extends EVActionBean {

    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    @DefaultHandler
    public Resolution redirect() {
        boolean manraEnabled = context.getUserSession().getUser().getPreference(UserPreferences.FENCE_ALLOW_MANRA);
        if (manraEnabled) {
            return new RedirectResolution("/customer/authenticate/manra.url");
        } else {
            return new RedirectResolution(EVPathUrl.EV_HOME.value());
        }
    }
}
