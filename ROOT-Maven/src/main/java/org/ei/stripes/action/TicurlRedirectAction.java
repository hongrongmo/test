package org.ei.stripes.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;

@UrlBinding("/ticurl")
public class TicurlRedirectAction extends EVActionBean {

    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    @DefaultHandler
    public Resolution redirect() {
        return new RedirectResolution("/customer/authenticate.url?" + context.getRequest().getQueryString());
    }
}
