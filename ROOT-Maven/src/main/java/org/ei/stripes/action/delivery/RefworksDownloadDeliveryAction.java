package org.ei.stripes.action.delivery;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;

@UrlBinding("/delivery/download/refworks.url")
public class RefworksDownloadDeliveryAction extends DownloadDeliveryAction {
    private final static Logger log4j = Logger.getLogger(RefworksDownloadDeliveryAction.class);
        
    /**
     * Override for the ISecuredAction interface.  Refworks service will try to callback
     * to this URL.  We need to let them in without authenticating!
     * TODO do some kind of MD5/token authentication??
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    /**
     * Handle the Refworks download request
     * @return
     * @throws Exception
     */
    @DontValidate
    @DefaultHandler
    public Resolution refworks() throws Exception {
        log4j.info("Submitting Refworks download...");

        return super.submit();
    }
    
    
}
