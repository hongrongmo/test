package org.ei.stripes.action.results;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.controller.RSSBlocker;
import org.ei.controller.RssCache;
import org.ei.domain.RSSFeed;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.util.HttpRequestUtil;
import org.ei.util.GUID;

@UrlBinding("/rss/{$event}.url")
public class RSSFeedAction extends EVActionBean {
    private static final Logger log4j = Logger.getLogger(RSSFeedAction.class);

    // Parameters for action
    private String queryID;
    private String term1;

    private String feedurl;
    private String encodedfeedurl;

    /**
     * This should be open to world!
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    /**
     * Displays the RSS page to the customer.  Offers them ways to view the feed.
     * @return
     * @throws InfrastructureException
     */
    @HandlesEvent("display")
    public Resolution display() throws InfrastructureException {
        UserSession ussession = context.getUserSession();
        if (ussession == null || ussession.getSessionID() == null || ussession.getUser() == null || ussession.getUser().getCustomerID() == null) {
            throw new InfrastructureException(SystemErrorCodes.RSS_FEED_ERROR, "No customer information present!");
        }

        try {
            this.queryID = new GUID().toString();

            // Store the RSS info (query) for future calls
            RSSFeed.setQuery(this.queryID, term1, this.database, ussession.getUser().getCustomerID().trim());

            // Encode the feed URL for each of the RSS links
            this.feedurl = "http://" + HttpRequestUtil.getServerName(context.getRequest()) + "/rss/feed.url?queryID=" + this.queryID + "&SYSTEM_PT=t";
            this.encodedfeedurl = URLEncoder.encode(this.feedurl, "UTF-8");

        } catch (Exception e) {
            log4j.error("Unable to save RSS query!", e);
            throw new InfrastructureException(SystemErrorCodes.RSS_FEED_ERROR, e);
        }

        return new ForwardResolution("/WEB-INF/pages/customer/results/rssdisplay.jsp");
    }

    /**
     * Handles requests for the RSS feed.  These feeds are open to the world!  But note
     * that the only content we expose is a snippet of the abstract, a link to the document
     * (customer must authenticate to view)
     * @return
     */
    @HandlesEvent("feed")
    public Resolution feed() {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        response.setContentType("application/rss+xml, application/rdf+xml, application/atom+xml, application/xml, text/xml; charset=UTF-8");

        // Ensure queryID is present
        if (GenericValidator.isBlankOrNull(this.queryID)) {
            log4j.error("No query ID on request!");
            try {
                response.getWriter().write(RSSFeed.ERROR_MESSAGE);
            } catch (IOException e) {}
        }

        try {
            // Determine if RSS feed is blocked (by IP)
            RSSBlocker blocker = RSSBlocker.getInstance();
            if (blocker.blocked(this.queryID)) {
                return new StreamingResolution("application/rss+xml, application/rdf+xml, application/atom+xml, application/xml, text/xml; charset=UTF-8",
                    RSSBlocker.BLOCKED_MESSAGE);
            }

            // Determine if RSS feed is cached
            if (RssCache.isCached(this.queryID)) {
                RssCache.printFile(this.queryID, response.getWriter());
            } else {
                RSSFeed rssfeed = new RSSFeed(this.queryID, HttpRequestUtil.getServerName(request));
                RssCache.cache(rssfeed, response.getWriter());
            }

        } catch (Exception e) {
            log4j.error("Unable to build RSS feed!!", e);
            try {
                response.getWriter().write(RSSFeed.ERROR_MESSAGE);
            } catch (IOException e1) {}
        }

        // Create RSS feed object
        return null; //new ForwardResolution("/WEB-INF/pages/customer/results/rssfeed.jsp");
    }

    //
    //
    // GETTERS / SETTERS
    //
    //

    /**
     * @return the queryid
     */
    public String getQueryID() {
        return queryID;
    }

    /**
     * @param queryid
     *            the queryid to set
     */
    public void setQueryID(String queryid) {
        this.queryID = queryid;
    }

    /**
     * @return the term1
     */
    public String getTerm1() {
        return term1;
    }

    /**
     * @param term1
     *            the term1 to set
     */
    public void setTerm1(String term1) {
        this.term1 = term1;
    }

    /**
     * @return the encodedfeedurl
     */
    public String getEncodedfeedurl() {
        return encodedfeedurl;
    }

    /**
     * @return the feedurl
     */
    public String getFeedurl() {
        return feedurl;
    }

}
