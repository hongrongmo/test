package org.ei.stripes.action.tagsgroups;

import java.net.URLEncoder;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.SystemMessage;
import org.ei.tags.Member;
import org.ei.tags.TagGroup;
import org.ei.tags.TagGroupBroker;

@UrlBinding("/tagsgroups/invite.url")
public class InviteAction extends EVActionBean {
	private final static Logger log4j = Logger.getLogger(InviteAction.class);

	private String groupid;
	private boolean world;
	private TagGroup group;
	
	/**
	 * Override for the ISecuredAction interface.  This is a special 
	 * ActionBean that we allow access to anyone BUT to complete the
	 * invitation they must be an authenticated user!  
	 */
	@Override
	public IAccessControl getAccessControl() {
		return new NoAuthAccessControl();
	}

	/**
	 * Default display for group invitations.  Note the "world"
	 * event below also - this method handles valid EV customers
	 * where the "world" event handles non-EV visitors.
	 * 
	 * @return Resolution
	 * @throws Exception
	 */
	@DefaultHandler
	@DontValidate
	public Resolution invite() throws Exception {
		setRoom(ROOM.tagsgroups);
		
		// Make sure this is an authenticated user
		if (context.getUserSession() == null || context.getUserSession().getUser() == null || !context.getUserSession().getUser().isCustomer()) {
			log4j.warn("Unable to accept invitation - user is NOT authenticated!");
			world = true;
			return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/invite.jsp");
		} 
		
		// Make sure there is a groupid present
		if (GenericValidator.isBlankOrNull(groupid)) {
			log4j.warn("No groupid for invitation!");
			String message = URLEncoder.encode("<DISPLAY>The invitation is no longer valid.</DISPLAY>","UTF-8");
			return new RedirectResolution(SystemMessage.SYSTEM_ERROR_URL+"?exception="+message);
		}

		// Try to retrieve the group
		TagGroupBroker groupBroker = new TagGroupBroker();
		group = groupBroker.getGroup(groupid, true);
		if (group == null) {
			log4j.warn("No group could be found for ID:" + groupid);
			String message = URLEncoder.encode("<DISPLAY>The invitation is no longer valid.</DISPLAY>","UTF-8");
			return new RedirectResolution(SystemMessage.SYSTEM_ERROR_URL+"?exception="+message);
		}
		
		// Redirect to login page if user not personally logged in.  NOTE that
		// we are NOT marking the class with IPersonalLogin so that
		// we can handle the "world" event!
		if (!context.isUserLoggedIn()) {
			Member invitee = group.getMember(group.getOwnerid());
			String nexturl = URLEncoder.encode("/tagsgroups/invite.url?groupid="+groupid, "UTF-8");
			String message = URLEncoder.encode((invitee == null ? "You have been invited" : invitee.getFullName() + " has invited you") +
					" to join the Engineering Village Tag Group: " + group.getTitle() + ".<br/>You must login or register below to accept this invitation.","UTF-8");
			return new RedirectResolution("/customer/authenticate/loginfull.url?nexturl=" + nexturl + "&message=" + message);
		}
		
		// Already accepted?
		Member found = group.getMember(context.getUserid());
		if (found != null) {
			LocalizableError error = 
				new LocalizableError("org.ei.stripes.action.tagsgroups.InviteAction.alreadyaccepted",
						"<span style='color:" + group.getColorByID().getCode() + "'>" + 
						group.getTitle() + "</span>");
			String message = URLEncoder.encode(error.getMessage(null), "UTF-8");
			return new RedirectResolution("/tagsgroups/display.url?message="+message);
		}
		
		// Forward to the JSP for display
		return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/invite.jsp");
	}
	
	/**
	 * Default display for AUTHGROUP WORLD.  See the ContentConfig.xml file.  
	 * Basically this is someone who does NOT have access to EV (IP or 
	 * otherwise)
	 * 
	 * TODO better message on page?
	 * TODO remove after re-factoring release!
	 * 
	 * @return Resolution
	 * @throws Exception
	 */
	@HandlesEvent("world")
	@DontValidate
	public Resolution world() throws Exception {
		return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/invite.jsp");
	}

	/**
	 * Handle accept of invitation
	 * 
	 * @return Resolution
	 * @throws Exception
	 */
	@HandlesEvent("accept")
	@DontValidate
	public Resolution accept() throws Exception {
		setRoom(ROOM.tagsgroups);
		
		// Make sure there is a groupid present
		if (GenericValidator.isBlankOrNull(groupid)) {
			log4j.warn("No groupid for invitation!");
			String message = URLEncoder.encode("<DISPLAY>The invitation is no longer valid.</DISPLAY>","UTF-8");
			return new RedirectResolution(SystemMessage.SYSTEM_ERROR_URL+"?exception="+message);
		}

		// Try to retrieve the group
		TagGroupBroker groupBroker = new TagGroupBroker();
		group = groupBroker.getGroup(groupid, true);
		if (group == null) {
			log4j.warn("No group could be found for ID:" + groupid);
			String message = URLEncoder.encode("<DISPLAY>The invitation is no longer valid.</DISPLAY>","UTF-8");
			return new RedirectResolution(SystemMessage.SYSTEM_ERROR_URL+"?exception="+message);
		}
		
		// Redirect to login page if user not logged in.  NOTE that
		// we are NOT marking the class with IPersonalLogin so that
		// we can handle the "world" event!
		if (!context.isUserLoggedIn()) {
			Member invitee = group.getMember(group.getOwnerid());
			String nexturl = URLEncoder.encode("/tagsgroups/invite.url?groupid="+groupid, "UTF-8");
			String message = (invitee == null ? "You have been invited" : invitee.getFullName() + " has invited you") +
					" to join the Engineering Village Tag Group: <b>" + group.getTitle() + "</b>";
			return new RedirectResolution("/customer/authenticate/loginfull.url?nexturl=" + nexturl + "&message=" + message);
		}
		
		// Accept invitation
		TagGroupBroker broker = new TagGroupBroker();
		int result = broker.addMember(groupid,context.getUserid());
		if (result == 0) {
			log4j.warn("TagGroupBroker returned '0' indicating error accepting invitation for groupid: " + groupid);
			context.getValidationErrors().addGlobalError(new LocalizableError("org.ei.stripes.action.tagsgroups.InviteAction.inviteerror"));
			return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/invite.jsp");
		}
		
		// Redirect to Tags & Groups home
		return new RedirectResolution("/tagsgroups/display.url");
	}


	//
	//
	// GETTERS/SETTERS
	//
	//
	
	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public boolean isWorld() {
		return world;
	}

	public void setWorld(boolean world) {
		this.world = world;
	}

	public TagGroup getGroup() {
		return group;
	}

	public void setGroup(TagGroup group) {
		this.group = group;
	}

}
