package org.ei.stripes.action.tagsgroups;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.email.SESEmail;
import org.ei.biz.email.SESMessage;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.TagsGroupsIndivAccessControl;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.EVPathUrl;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.action.personalaccount.IPersonalLogin;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.tags.Member;
import org.ei.tags.Scope;
import org.ei.tags.Tag;
import org.ei.tags.TagBroker;
import org.ei.tags.TagEditor;
import org.ei.tags.TagGroup;
import org.ei.tags.TagGroupBroker;

@UrlBinding("/tagsgroups/doc.url")
public class TagDocumentAction extends EVActionBean implements IPersonalLogin {
	private final static Logger log4j = Logger.getLogger(TagDocumentAction.class);

	public static final String TAGDOC_URL = "/tagsgroups/doc.url";

	// Scope of the tag request. Can contain the following values:
	// 1 - Public tags (default)
	// 2 - Private tags
	// 3 - Group tags: will be followed by a ':' and then the Group ID
	// 4 - Institution tags
	private String scope = Integer.toString(Scope.SCOPE_PUBLIC);

	// Search type to determine redirect/forward for Edit tags
	private String searchtype;
	// Input from text field
	private String searchgrouptags;
	// Input for editing tags
	private String[] edittag;
	// For group tags, indicator to send email to group
	private boolean notifygroup;
	// Document ID for tag
	private String tagdoc;

	/**
	 * Override for the ISecuredAction interface.  This ActionBean requires
	 * individual authentication
	 */
	@Override
	public IAccessControl getAccessControl() {
		return new TagsGroupsIndivAccessControl();
	}

	/**
	 * Tags and groups add tag(s) for a document
	 *
	 * @return Resolution
	 * @throws Exception
	 */
	@DefaultHandler
	@HandlesEvent("addtag")
	@DontValidate
	public Resolution addtag() {
		log4j.info("Adding tag(s) '" + searchgrouptags + "' for doc ID: '" + tagdoc + "', scope=" + scope);

		// OK to continue, add the tag from input
		try {
			if (GenericValidator.isBlankOrNull(backurl)) {
				backurl = EVPathUrl.EV_HOME.value();
			}

			// Ensure params are present
			if (GenericValidator.isBlankOrNull(searchgrouptags) ||
				GenericValidator.isBlankOrNull(scope) ||
				GenericValidator.isBlankOrNull(tagdoc)) {
				EVExceptionHandler.logException("Invalid arguments - tagdoc must be present!",null,context.getRequest());
				log4j.warn("Invalid arguments - tag, tagdoc & scope must be present!");
				// TODO add error message for page?  There's javascript validation so I'm
				// being lazy and not coding a message since you shouldn't be able to get here...
				return new RedirectResolution(backurl);
			}

			// Ensure user is logged in...
			if (!context.isUserLoggedIn()) {
				String returnurl = URLEncoder.encode(
					"CID=addTagForward&addtag=true&scope="+scope+"&tagdoc="+tagdoc+"&searchgrouptags="+searchgrouptags,"UTF-8");
				return new RedirectResolution(
					"/customer/authenticate/loginfull.url?backurl=" +backurl + "&nexturl=" +returnurl);
			}
			String customerId = context.getUserSession().getUser().getCustomerID().trim();
			String userid = context.getUserid();

			// Get the scope - PUBLIC, PRIVATE, etc.  If GROUP, it will be of format: "3:[groupid]"
			int iscope = -1;
			String groupID = null;
			String[] scopesplit = scope.split(":");
			iscope = Integer.parseInt(scopesplit[0]);
			if (scopesplit.length > 1) {
				groupID = scopesplit[1];
			}

			// Add the tag(s) via the TagBroker
			TagBroker broker = new TagBroker();
			String[] split = searchgrouptags.split(",");
			if (split.length == 0) {
				log4j.warn("No tags could be found!");
				// TODO add error message for page?  There's javascript validation so I'm
				// being lazy and not coding a message since you shouldn't be able to get here...
				return new RedirectResolution(backurl);
			}
			for (String tag : split) {
				// Ensure tag is not empty
				tag = tag.trim();
				if (GenericValidator.isBlankOrNull(tag)) {
					continue;
				}
				// Ensure tagdoc is valid
				String[] tdocParts = tagdoc.split(":");
				if (tdocParts.length < 2) {
					continue;
				}

				// Build tag object
				Tag otag = new Tag();
				otag.setTag(tag);
				otag.setDocID(tdocParts[0]);
				otag.setMask(Integer.parseInt(tdocParts[1]));
				if (tdocParts.length > 2) {
					otag.setCollection(tdocParts[2]);
				}
				otag.setCustID(customerId);
				otag.setUserID(userid);
				otag.setScope(iscope);
				otag.setGroupID(groupID);
				broker.addTag(otag);
			}

			//
			// Notify tag group members if indicated
			if (notifygroup) {
				sendNotification(userid, groupID, searchgrouptags, split);
			}

			// Return the tag link
			return new RedirectResolution(URLDecoder.decode(backurl, "UTF-8"));

		} catch (Exception e) {
			log4j.error("Unable to process request!",e);
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}
	}

	/**
	 * Tags and groups edit tag for a document
	 *
	 * @return Resolution
	 * @throws Exception
	 */
	@HandlesEvent("edittags")
	@DontValidate
	public Resolution edittag() throws Exception {
		log4j.info("Editing tags scope=" + scope);

		// Validate input params
		if (GenericValidator.isBlankOrNull(tagdoc)) {
			EVExceptionHandler.logException("Invalid arguments - tagdoc must be present!",null,context.getRequest());
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		// Setup some vars
		HttpServletRequest request = context.getRequest();
		String customerId = context.getUserSession().getUser().getCustomerID().trim();
		String userid = context.getUserid();

		// Use the TagEditor to update the tags
		// TODO update this crappy code
		try {
			Map<String, String[]> requestMap = request.getParameterMap();
			TagEditor editor = new TagEditor(requestMap, userid, customerId, tagdoc);
		} catch (Throwable t) {
			EVExceptionHandler.logException("Unable to process request!",t,context.getRequest());
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		// If we're already in a TagSearch, go to confirmation page, otherwise
		// redirect back to abstract page
		if ("TagSearch".equals(searchtype) || "Tags".equals(searchtype)) {
			return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/edittagconfirm.jsp");
		} else {
			return new RedirectResolution(URLDecoder.decode(backurl,"UTF-8"));
		}
	}

	/**
	 * Build and send an email to tag group members
	 *
	 * @param userid
	 * @param groupID
	 * @param multiTag
	 * @param mtags
	 * @throws Exception
	 */
	private void sendNotification(String userid, String groupID, String multiTag, String[] mtags) throws Exception {
		TagGroupBroker groupBroker = new TagGroupBroker();
		TagGroup group = groupBroker.getGroup(groupID, true);
		List<String> bccList = getEmailList(group.getMembers());

		Member notifier = group.getMember(userid);
		String from = notifier.getEmail();
		bccList.remove(from);

		String subject = "Engineering Village Tag Group Notification";
		StringBuffer message = new StringBuffer();

		message.append("This email was sent to you on behalf of " + from + ".<br/><br/>\n \n" );

		message.append("Note to all members of the Tag Group: ");
		message.append(group.getTitle());
		message.append("<br>\r\n");
		message.append("<br>\r\n");
		message.append(notifier.getFullName());
		message.append(" is sending notification that the tag(s) below have been applied for the group:");
		message.append("<br>\r\n");
		message.append("<br>\r\n");
		message.append(multiTag);

		String recentTags = getRecentTags(group.getTags(), mtags);
		if (recentTags.length() > 0) {
			message.append("<br>\r\n");
			message.append("<br>\r\n");
			message.append("The following tag(s) have been applied in the past 24 hours:");
			message.append("<br>\r\n");
			message.append("<br>\r\n");
			message.append(recentTags);

		}
		message.append("<br>\r\n");
		message.append("<br>\r\n");
		message.append("You can click <a href=\"http://www.engineeringvillage.com/controller/servlet/Controller?CID=tagGroups\">http://www.engineeringvillage.com/controller/servlet/Controller?CID=tagGroups</a> to view the updates to the group.");
		message.append("<br>\r\n");
		String strmessage = message.toString();

		SESMessage sesmessage = new SESMessage();
		sesmessage.setMessage(subject, strmessage,true);
		sesmessage.setFrom(SESMessage.DEFAULT_SENDER);
		List<String> toList = new ArrayList<String>();
		toList.add(from);
		sesmessage.setDestination(toList, null, bccList);
		SESEmail.getInstance().send(sesmessage);
	}

	/**
	 * Get email list for group members
	 *
	 * @param members
	 * @return
	 */
	private List<String> getEmailList(Member[] members) {
		List<String> tolist = new ArrayList<String>();
		for (int i = 0; i < members.length; i++) {
			tolist.add(members[i].getEmail());
		}

		return tolist;
	}

	/**
	 * Get list of recently-used tags
	 *
	 * @param tags
	 * @param appliedTags
	 * @return
	 */
	private String getRecentTags(Tag[] tags, String[] appliedTags) {
		StringBuffer tagBuf = new StringBuffer();
		if (tags != null) {
			long curtime = System.currentTimeMillis();
			curtime = curtime - 86400000;
			for (int i = 0; i < tags.length; i++) {
				long lasttouched = tags[i].getTimestamp();
				if (lasttouched > curtime
						&& notIn(tags[i].getTagName(), appliedTags)) {
					if (tagBuf.length() > 0) {
						tagBuf.append(", ");
					}
					tagBuf.append(tags[i].getTagName());
				}
			}
		}
		return tagBuf.toString();
	}

	/**
	 * Convenience method to check if tag is in a list.
	 *
	 * @param tagName
	 * @param appliedTags
	 * @return
	 */
	private boolean notIn(String tagName, String[] appliedTags) {
		for (int i = 0; i < appliedTags.length; i++) {
			if (tagName.equalsIgnoreCase(appliedTags[i])) {
				return false;
			}
		}
		return true;
	}

    @Override
    public String getLoginNextUrl() {
        // We have to get parms off of request since Stripes hasn't done
        // Binding and Validation yet!
        HttpServletRequest request = context.getRequest();
        tagdoc = request.getParameter("tagdoc");
        scope = request.getParameter("scope");
        searchgrouptags = request.getParameter("searchgrouptags");

        return TAGDOC_URL + "?" + context.getEventName() + "=true&scope="+scope+"&tagdoc="+tagdoc+"&searchgrouptags="+searchgrouptags;
    }

    @Override
    public String getLoginCancelUrl() {
        HttpServletRequest request = context.getRequest();
        return request.getParameter("backurl");
    }


	//
	//
	// GETTERS/SETTERS
	//
	//

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getSearchgrouptags() {
		return searchgrouptags;
	}

	public void setSearchgrouptags(String searchgrouptags) {
		this.searchgrouptags = searchgrouptags;
	}

	public String[] getEdittag() {
		return edittag;
	}

	public void setEdittag(String[] edittag) {
		this.edittag = edittag;
	}

	public boolean isNotifygroup() {
		return notifygroup;
	}

	public void setNotifygroup(boolean notifygroup) {
		this.notifygroup = notifygroup;
	}

	public String getTagdoc() {
		return tagdoc;
	}

	public void setTagdoc(String tagdoc) {
		this.tagdoc = tagdoc;
	}

	public String getSearchtype() {
		return searchtype;
	}

	public void setSearchtype(String searchtype) {
		this.searchtype = searchtype;
	}

}
