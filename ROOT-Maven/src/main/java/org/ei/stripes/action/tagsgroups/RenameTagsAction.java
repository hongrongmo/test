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

import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.TagsGroupsIndivAccessControl;
import org.ei.stripes.action.personalaccount.IPersonalLogin;
import org.ei.tags.TagBroker;
import org.ei.tags.TagGroup;
import org.ei.tags.TagGroupBroker;

@UrlBinding("/tagsgroups/renametag.url")
public class RenameTagsAction extends TagsGroupsAction implements IPersonalLogin {
	private final static Logger log4j = Logger
			.getLogger(RenameTagsAction.class);

    public static final String RENAMETAG_URL = "/tagsgroups/renametag.url";

    private List<TagGroup> tgroups = new ArrayList<TagGroup>();
	private List<String> taglabels = new ArrayList<String>();
	private String oldtag;
	private String newtag;
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
	 * Rename tag page display
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
			if (isScoupGroup()) {
				scopeint = 3;
				groupID = getScopeOption();
			} else {
				scopeint = Integer.parseInt(getScopeOption());
			}
		}

		if (getScopeOption() == null || getScopeOption().trim().equals("")) {
			setScopeOption("1");
		}
		getUserTags(customerId, userid, tagBroker, scopeint, groupID);

		if ((userid != null) && (userid.trim().length() != 0)) {
			getUserGroups(userid);
		}

		return new ForwardResolution(
				"/WEB-INF/pages/customer/tagsgroups/renametag.jsp");
	}

	@HandlesEvent("renametagsubmit")
	@DontValidate
	public Resolution renametag() throws Exception {
		setRoom(ROOM.tagsgroups);

		IEVWebUser user = context.getUserSession().getUser();
		String customerId = user.getCustomerID().trim();
		String userid = user.getUserId().trim();
		database = user.getDefaultDB();

		TagBroker tagBroker = new TagBroker();
		int scopeint = 1;
		String groupID = null;

		if (getScopeOption() != null) {
			if (isScoupGroup()) {
				scopeint = 3;
				groupID = getScopeOption();
			} else {
				scopeint = Integer.parseInt(getScopeOption());
			}
		}

		if (getScopeOption() == null || getScopeOption().trim().equals("")) {
			setScopeOption("1");
		}

		if (getNewtag() != null && getOldtag() != null) {
			tagBroker.updateTagName(getOldtag(), getNewtag(), userid,
					customerId, groupID, scopeint);
		}

		getUserTags(customerId, userid, tagBroker, scopeint, groupID);

		if ((userid != null) && (userid.trim().length() != 0)) {
			getUserGroups(userid);
		}

		return new ForwardResolution(
				"/WEB-INF/pages/customer/tagsgroups/renametag.jsp");
	}

	private boolean isScoupGroup() {
		return !getScopeOption().equals("1") && !getScopeOption().equals("2")
				&& !getScopeOption().equals("4");
	}

    @Override
    public String getLoginNextUrl() {
        return RENAMETAG_URL;
    }
    
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
		taglabels = Arrays.asList(tagBroker.getUserTagNames(scopeint, userid,
				customerId, groupID));
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

	public String getNewtag() {
		return newtag;
	}

	public void setNewtag(String newtag) {
		this.newtag = newtag;
	}

	public String getOldtag() {
		return oldtag;
	}

	public void setOldtag(String oldtag) {
		this.oldtag = oldtag;
	}

	public String getScopeOption() {
		return scopeOption;
	}

	public void setScopeOption(String scopeOption) {
		this.scopeOption = scopeOption;
	}

}
