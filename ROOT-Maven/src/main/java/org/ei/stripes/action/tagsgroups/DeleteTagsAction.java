package org.ei.stripes.action.tagsgroups;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.TagsGroupsIndivAccessControl;
import org.ei.stripes.action.personalaccount.IPersonalLogin;
import org.ei.tags.TagBroker;
import org.ei.tags.TagGroup;
import org.ei.tags.TagGroupBroker;

@UrlBinding("/tagsgroups/deletetag.url")
public class DeleteTagsAction extends TagsGroupsAction implements
		IPersonalLogin {
	private final static Logger log4j = Logger
			.getLogger(DeleteTagsAction.class);

	public static final String DELETETAG_URL = "/tagsgroups/deletetag.url";
	
	private String deletetag;
	private List<TagGroup> tgroups = new ArrayList<TagGroup>();
	private List<String> taglabels = new ArrayList<String>();
	private String scopeOption;

	/**
	 * Override for the ISecuredAction interface.  This ActionBean requires 
	 * Tags & Groups feature to be enabled in addition to individual authentication
	 */
	@Override
	public IAccessControl getAccessControl() {
		return new TagsGroupsIndivAccessControl();
	}

	/**
	 * Tags and groups view/edit groups page
	 * 
	 * @return Resolution
	 * @throws Exception
	 */
	@DefaultHandler
	@DontValidate
	public Resolution display() throws Exception {
		setRoom(ROOM.tagsgroups);
		IEVWebUser user = context.getUserSession().getUser();
		String customerId = user.getCustomerID().trim();
		String userid = user.getUserId().trim();
		database = user.getDefaultDB();

	    TagBroker tagBroker = new TagBroker();
	    int scopeint = 1;
		String groupID = null;
		
		if (getScopeOption() != null) {
			if (isScopeGroup()) {
				scopeint = 3;
				groupID = getScopeOption();
			} else {
				scopeint = Integer.parseInt(getScopeOption());
			}
		}

		getUserTags(customerId, userid, tagBroker, scopeint, groupID);
	    
		if (user.isIndividuallyAuthenticated()) {
			getUserGroups(userid);
		}
		return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/deletetag.jsp");
	}

	
	
	/**
	 * Tags and groups view/edit groups page
	 * 
	 * @return Resolution
	 * @throws Exception
	 */
	@HandlesEvent("deletetagsubmit")
	@Validate
	public Resolution deleteTag() throws Exception {
		setRoom(ROOM.tagsgroups);
		IEVWebUser user = context.getUserSession().getUser();
		String customerId = user.getCustomerID().trim();
		String userid = user.getUserId().trim();
		database = user.getDefaultDB();

		TagBroker tagBroker = new TagBroker();
	    int scopeint = 1;
		String groupID = null;
		
		if (getScopeOption() != null) {
			if (isScopeGroup()) {
				scopeint = 3;
				groupID = getScopeOption();
			} else {
				scopeint = Integer.parseInt(getScopeOption());
			}
		}

		if (getDeletetag() != null) {
			tagBroker.deleteTag(getDeletetag(), scopeint, userid, customerId,groupID, null);
		}

		getUserTags(customerId, userid, tagBroker, scopeint, groupID);
	    
		if ((userid != null) && (userid.trim().length() != 0)) {
			getUserGroups(userid);
		}
		return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/deletetag.jsp");

	}

    /**
     * Return the next URL after login
     */
    @Override
    public String getLoginNextUrl() {
        return DELETETAG_URL;
    }
    
    /**
     * Return the cancel URL for login
     */
    @Override
    public String getLoginCancelUrl() {
        return TagsGroupsAction.DISPLAY_URL;
    }
    
	private void getUserGroups(String userid) throws Exception {
		TagGroupBroker groupBroker = new TagGroupBroker();
		tgroups = Arrays.asList(groupBroker.getGroups(userid, false));
	}

	private void getUserTags(String customerId, String userid,
			TagBroker tagBroker, int scopeint, String groupID) throws Exception {
		taglabels = Arrays.asList(tagBroker.getUserTagNames(scopeint,userid,customerId,groupID));
	}

	private boolean isScopeGroup() {
		return !getScopeOption().equals("1") && !getScopeOption().equals("2") && !getScopeOption().equals("4");
	}

	public String getDeletetag() {
		return deletetag;
	}

	public void setDeletetag(String deleteTag) {
		this.deletetag = deleteTag;
	}

	public List<TagGroup> getTgroups() {
		return tgroups;
	}

	public void setTgroups(List<TagGroup> tgroups) {
		this.tgroups = tgroups;
	}

	public List<String> getTaglabels() {
		return taglabels;
	}

	public void setTaglabels(List<String> taglabels) {
		this.taglabels = taglabels;
	}
	
	public String getScopeOption() {
		return scopeOption;
	}


	public void setScopeOption(String scopeOption) {
		this.scopeOption = scopeOption;
	}

}
