package org.ei.stripes.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.domain.ChinaLinkBuilder;
import org.ei.domain.PublisherBroker;

@UrlBinding("/chinalinking.url")
public class ChinaLinkingAction extends EVActionBean {
    
    private static Logger log4j = Logger.getLogger(ChinaLinkingAction.class);
    
    private Map<String,String> linkingurls;

    /**
     * Override for the ISecuredAction interface.  This action should be open to "world"
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    /**
     * Default handler for incoming requests.  Note that this repaced the chinaLinking.jsp
     * and the chinaLinkingError.jsp from the legacy code.
     * 
     * @return
     */
    @DefaultHandler
    @DontValidate
    public Resolution handle() {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        
        log4j.info("Handling china link!");
        
        //
        // If there is an Elsevier DOI on the incoming URL AND there is a CREDS string
        // then redirect the the dx.doi.org site
        //
        String doi = request.getParameter("DOI");
        if ((doi != null && (doi.indexOf("10.1016/") == 0 || doi.indexOf("10.1006/") == 0)) &&
            (request.getParameter("CREDS") != null && request.getParameter("CREDS").indexOf("Elsevier") != -1)) {
            return new RedirectResolution("http://dx.doi.org/" + doi);
        }
        
        //
        // Build links for the error page
        //
        try {
            PublisherBroker pubBroker = PublisherBroker.getInstance();
            ChinaLinkBuilder chinaLinkBuilder = new ChinaLinkBuilder();
            
            chinaLinkBuilder.setAulast(request.getParameter("AULAST"));
            chinaLinkBuilder.setAufirst(request.getParameter("AUFIRST"));
            chinaLinkBuilder.setAufull(request.getParameter("AUFULL"));
            chinaLinkBuilder.setIssn(request.getParameter("ISSN"));
            chinaLinkBuilder.setIssn9(request.getParameter("ISSN9"));
            chinaLinkBuilder.setIssue(request.getParameter("ISSUE"));
            chinaLinkBuilder.setCoden(request.getParameter("CODEN"));
            chinaLinkBuilder.setTitle(request.getParameter("TITLE"));
            chinaLinkBuilder.setStitle(request.getParameter("STITLE"));
            chinaLinkBuilder.setAtitle(request.getParameter("ATITLE"));
            chinaLinkBuilder.setVolume(request.getParameter("VOLUME"));
            chinaLinkBuilder.setSpage(request.getParameter("SPAGE"));
            chinaLinkBuilder.setEpage(request.getParameter("EPAGE"));
            chinaLinkBuilder.setCreds(request.getParameter("CREDS"));
            
            String url = null;
            String pubID = null;
            String issn = chinaLinkBuilder.getIssn();
            chinaLinkBuilder.buildUrls();
            this.linkingurls = chinaLinkBuilder.getCredUrls();
            
            this.message = "The publisher of this title is not one of the following :";
            if (issn != null && issn.length() > 0) {
                pubID = pubBroker.fetchPubID(issn);
                if (pubID != null && pubID.length() > 0) {
                    url = chinaLinkBuilder.getPubURL(pubID);
                    if (url == null) {
                        this.message = "The publisher of this title, " + pubID + " is not one of the following :";
                    }
                }
            }
            else {
                this.message = "We could not find a publisher for this title. There is no ISSN.";
            }
            
        } catch (Exception e) {
            log4j.error("Unable to process links!", e);
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        }
        
        return new ForwardResolution("/WEB-INF/pages/world/chinalinking.jsp");
    }

    public Map<String, String> getLinkingurls() {
        return linkingurls;
    }

}
