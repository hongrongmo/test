package org.ei.stripes.action.tagsgroups;

import java.util.Comparator;
import java.util.List;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.SimpleError;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.TagsGroupsAccessControl;
import org.ei.gui.ListBoxOption;
import org.ei.stripes.action.EVActionBean;
import org.ei.tags.AlphaComp;
import org.ei.tags.Scope;
import org.ei.tags.SizeComp;
import org.ei.tags.Tag;
import org.ei.tags.TagBroker;
import org.ei.tags.TagCloud;
import org.ei.tags.TagGroupBroker;
import org.ei.tags.TimeComp;

/**
 * This class renders the Tags & Groups home page
 * 
 * @author harovetm
 * 
 */
@UrlBinding("/tagsgroups/display.url")
public class TagsGroupsAction extends EVActionBean {
    public static final String DISPLAY_URL = "tagsgroups/display.url";
    private final static Logger log4j = Logger.getLogger(TagsGroupsAction.class);

    private String searchid;
    private String userid;
    private String searchtype;
    private int resultscount;
    private String message;

    // Scope of the tag request. Can contain the following values:
    // 1 - Public tags (default)
    // 2 - Private tags
    // 3 - Group tags: will be followed by a ':' and then the Group ID
    // 4 - Institution tags
    private String scope = Integer.toString(Scope.SCOPE_PUBLIC);

    // Sort type for the tag display - Alpha (default), Popularity
    // or Most recent
    private String sort = "alpha";

    private List<ListBoxOption> scopes;
    private List<Tag> tags;

    /**
     * Override for the ISecuredAction interface. This ActionBean requires Tags & Groups feature to be enabled in addition to regular authentication
     */
    @Override
    public IAccessControl getAccessControl() {
        return new TagsGroupsAccessControl();
    }

    /**
     * Tags and groups default display
     * 
     * @return Resolution
     * @throws Exception
     */
    @HandlesEvent("display")
    @DontValidate
    public Resolution display() throws Exception {
        setRoom(ROOM.tagsgroups);
        userid = context.getUserid();
        String customerId = context.getUserSession().getUser().getCustomerID().trim();

        // See if there is an error message (email invitation?)
        if (!GenericValidator.isBlankOrNull(message)) {
            context.getValidationErrors().addGlobalError(new SimpleError(message));
        }

        // Get the scope for the tags
        Scope sc = new Scope(userid, scope);
        int iscope = sc.getScope();
        this.scopes = sc.getScopeOptions();

        // Get the tags!
        TagBroker tbroker = new TagBroker(new TagGroupBroker());
        Tag[] tags = tbroker.getConsolidatedTags(TagBroker.CONSOLIDATED_TAG_RETRIEVAL, (iscope == Scope.SCOPE_LOGIN ? Scope.SCOPE_PRIVATE : iscope), userid,
            customerId, sc.getGroupid());

        // Build the TagCloud
        Comparator<Tag> comp = null;
        if (GenericValidator.isBlankOrNull(sort) || sort.equals("alpha")) {
            comp = new AlphaComp();
        } else if (sort.equals("size")) {
            comp = new SizeComp();
        } else if (sort.equals("time")) {
            comp = new TimeComp();
        }
        TagCloud cloud = new TagCloud(tags, 4, comp);
        this.tags = cloud.getTagsDisplay();

        // Forward to the JSP for display
        return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/home.jsp");
    }

    //
    //
    // GETTERS/SETTERS
    //
    //

    public String getSearchid() {
        return searchid;
    }

    public void setSearchid(String searchid) {
        this.searchid = searchid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSearchtype() {
        return searchtype;
    }

    public void setSearchtype(String searchtype) {
        this.searchtype = searchtype;
    }

    public int getResultscount() {
        return resultscount;
    }

    public void setResultscount(int resultscount) {
        this.resultscount = resultscount;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<ListBoxOption> getScopes() {
        return scopes;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
