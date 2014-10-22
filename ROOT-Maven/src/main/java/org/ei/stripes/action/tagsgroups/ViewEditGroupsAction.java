package org.ei.stripes.action.tagsgroups;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.email.SESEmail;
import org.ei.biz.email.SESMessage;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.ISecuredAction;
import org.ei.biz.security.TagsGroupsIndivAccessControl;
import org.ei.stripes.action.personalaccount.IPersonalLogin;
import org.ei.tags.Color;
import org.ei.tags.Color.IColor;
import org.ei.tags.Member;
import org.ei.tags.TagGroup;
import org.ei.tags.TagGroupBroker;

@UrlBinding("/tagsgroups/editgroups.url")
public class ViewEditGroupsAction extends TagsGroupsAction implements IPersonalLogin, ISecuredAction {
    private final static Logger log4j = Logger.getLogger(ViewEditGroupsAction.class);

    public static final String VIEWEDITGROUPS_URL = "/tagsgroups/editgroups.url";

    private String message;					// Message (when needed)
    private String groupid;					// ID for the current group (edit ONLY!)

    // Create group fields
    @Validate(required = true)
    private String groupname;				// Group name textbox
    @Validate(required = true)
    private String groupcolor;				// Color dropdown selection
    @Validate(maxlength = 300)
    private String groupdescription = "";		// Description textarea
    private String[] member;				// Array of checkboxes (name='member') on Edit form for previously added members
    private String groupinvite;				// Invitation textarea

    private TagGroup[] groups;				// List of groups (display only)
    private Member[] members;				// List of members for group (edit only)

    private IColor[] colors = Color.getInstance().getColors();	// Colors dropdown (edit/create)

    /**
     * Override for the ISecuredAction interface. This ActionBean requires Tags & Groups feature to be enabled in addition to individual authentication
     */
    @Override
    public IAccessControl getAccessControl() {
        return new TagsGroupsIndivAccessControl();
    }

    /**
     * Tags and groups create group page
     *
     * @return Resolution
     * @throws Exception
     */
    @DefaultHandler
    @DontValidate
    public Resolution display() throws Exception {
        setRoom(ROOM.tagsgroups);

        String userid = context.getUserid();
        TagGroupBroker groupBroker = new TagGroupBroker();
        groups = groupBroker.getGroups(userid, true);

        // If message is present, add it to the global errors to
        // get output by the stripes:errors tag
        if (!GenericValidator.isBlankOrNull(message)) {
            context.getValidationErrors().addGlobalError(new SimpleError(message));
        }

        // Forward to the JSP for display
        return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/viewgroups.jsp");
    }

    /**
     * Display the create group page
     *
     * @return Resolution
     * @throws Exception
     */
    @HandlesEvent("create")
    @DontValidate
    public Resolution create() throws Exception {
        setRoom(ROOM.tagsgroups);

        // Forward to the JSP for display
        return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/creategroup.jsp");
    }

    /**
     * Tags and groups view/edit groups page
     *
     * @return Resolution
     * @throws Exception
     */
    @HandlesEvent("createsubmit")
    @Validate
    public Resolution createsubmit() {
        setRoom(ROOM.tagsgroups);

        String userid = context.getUserid();

        TagGroup addGroup = new TagGroup();
        addGroup.setDatefounded();
        addGroup.setTitle(groupname);
        addGroup.setDescription(groupdescription == null ? "" : groupdescription);
        addGroup.setColor(groupcolor);
        addGroup.setOwnerid(userid);

        TagGroupBroker groupBroker = new TagGroupBroker();
        String inviteid = null;
        try {
            if (groupname != null && userid != null) {
                inviteid = groupBroker.addGroup(addGroup);
            }
        } catch (Exception e) {
            log4j.error("Unable to create group: ", e);
            context.getValidationErrors().addGlobalError(new LocalizableError("org.ei.stripes.action.tagsgroups.ViewEditGroupsAction.systemerror"));
            return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/creategroup.jsp");
        }

        // Send an email about the group
        sendMessage(userid, inviteid, context.getRequest());

        // Redirect to the View/Edit Groups page
        return new RedirectResolution(VIEWEDITGROUPS_URL);
    }

    /**
     * Display the edit group page
     *
     * @return Resolution
     * @throws Exception
     */
    @HandlesEvent("edit")
    @DontValidate
    public Resolution edit() throws Exception {
        setRoom(ROOM.tagsgroups);

        TagGroupBroker groupBroker = new TagGroupBroker();
        groups = new TagGroup[1];
        groups[0] = groupBroker.getGroup(groupid, true);
        // Ensure the group was retrieved
        if (groups[0] == null) {
            log4j.warn("Unable to retrieve group with ID: " + groupid);
            context.getValidationErrors().addGlobalError(new LocalizableError("org.ei.stripes.action.tagsgroups.ViewEditGroupsAction.systemerror"));
            return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/viewgroups.jsp");
        }

        // If a user tries to hack an edit group URL to edit another user's
        // Group, return them to groups home. Bad customer! :)
        String userid = context.getUserid();
        if (!userid.equals(groups[0].getOwnerid())) {
            log4j.warn("Attempt by User with ID: " + userid + " to edit Group owned by: " + groupid);
            return new RedirectResolution(TagsGroupsAction.DISPLAY_URL);
        }

        groupname = groups[0].getTitle();
        groupcolor = groups[0].getColor();
        groupdescription = groups[0].getDescription();
        members = groups[0].getMembers();

        // Forward to the JSP for display
        return new ForwardResolution("/WEB-INF/pages/customer/tagsgroups/creategroup.jsp");
    }

    /**
     * Handles editing a groups
     *
     * @return Resolution
     * @throws Exception
     */
    @HandlesEvent("editsubmit")
    @Validate
    public Resolution editsubmit() throws Exception {
        setRoom(ROOM.tagsgroups);

        TagGroupBroker groupBroker = new TagGroupBroker();
        String userid = context.getUserid();

        String inviteid = groupid;
        TagGroup tgroup = new TagGroup();
        tgroup.setGroupID(groupid);
        tgroup.setDescription(groupdescription);
        tgroup.setColor(groupcolor);
        groupBroker.updateGroup(tgroup, member);

        // Send an email about the group
        sendMessage(userid, inviteid, context.getRequest());

        // Redirect to the View/Edit Groups page
        return new RedirectResolution(VIEWEDITGROUPS_URL);
    }

    /**
     * Delete a group
     *
     * @return Resolution
     * @throws Exception
     */
    @HandlesEvent("delete")
    @DontValidate
    public Resolution delete() throws Exception {
        setRoom(ROOM.tagsgroups);

        // Delete it!
        TagGroupBroker groupBroker = new TagGroupBroker();
        if (!GenericValidator.isBlankOrNull(groupid)) {
            groupBroker.removeGroup(groupid);
            log4j.info("Removed group with id: " + groupid);
        } else {
            log4j.warn("No group id for delete!");
        }

        // Redirect to remove delete parm
        return new RedirectResolution(VIEWEDITGROUPS_URL);
    }

    /**
     * Cancels the current user's membership in a group
     *
     * @return Resolution
     * @throws Exception
     */
    @HandlesEvent("cancel")
    @DontValidate
    public Resolution cancel() throws Exception {
        setRoom(ROOM.tagsgroups);

        // Delete it!
        TagGroupBroker groupBroker = new TagGroupBroker();
        if (!GenericValidator.isBlankOrNull(groupid)) {
            groupBroker.removeMembers(groupid, new String[] { context.getUserid() });
            log4j.info("Removed current user from group with id: " + groupid);
        } else {
            log4j.warn("Unable to remove member from group!");
        }

        // Redirect to remove delete parm
        return new RedirectResolution(TagsGroupsAction.DISPLAY_URL);
    }

    @Override
    public String getLoginNextUrl() {
        return VIEWEDITGROUPS_URL;
    }

    @Override
    public String getLoginCancelUrl() {
        return TagsGroupsAction.DISPLAY_URL;
    }

    /**
     * Attempts to send an invitation to the new Group
     *
     * @param userid
     * @param inviteid
     */
    private void sendMessage(String userid, String inviteid, HttpServletRequest request) {

        try {
            TagGroupBroker groupBroker = new TagGroupBroker();
            TagGroup[] groups = groupBroker.getGroups(userid, true);

            if (!GenericValidator.isBlankOrNull(groupinvite)) {

                Member inviter = getInviter(groups, inviteid, userid);

                // Build the 'to' list
                List<String> tolist = new ArrayList<String>();
                String[] emailaddresses = groupinvite.split(",");
                for (int i = 0; i < emailaddresses.length; i++) {
                    if (emailaddresses[i] != null && !emailaddresses[i].trim().equals("")) {
                        /*
                         * don't add the inviter to the to: list - they are the main recipient now
                         */
                        if (!emailaddresses[i].trim().equals(inviter.getEmail())) {
                            tolist.add((String) emailaddresses[i].trim());
                        }
                    }
                }

                // Send the message when there are recipients
                if (tolist.size() > 0) {
                    String accptLink = "http://" + request.getServerName()
                        + (request.getServerPort() > 0 && request.getServerPort() != 80 ? ":" + request.getServerPort() : "")
                        + "//tagsgroups/invite.url?groupid=" + inviteid;
                    String from = inviter.getEmail();
                    StringBuffer subjectBuf = new StringBuffer();
                    subjectBuf.append("Engineering Village Tag Group invitation from ");
                    subjectBuf.append(inviter.getFullName());
                    String subject = subjectBuf.toString();
                    StringBuffer message = new StringBuffer();

                    message.append("This email was sent to you on behalf of " + from + ".<br/><br/>\n \n" );
                    message.append(inviter.getFullName());
                    message.append(" invites you to join the Engineering Village Tag Group: ");
                    message.append(groupname);
                    message.append(".<br/>\r\n");
                    message.append("<br/>\r\n");
                    message.append("<br/>\r\n");
                    message.append("Click on the link <a href=\"").append(accptLink).append("\">");
                    message.append(accptLink).append("</a> to accept the invitation.<br/>\r\n");
                    message.append("<br/>\r\n");
                    message.append("<br/>\r\n");

                    log4j.debug("Sending TagsGroups invite: ");
                    log4j.debug(message.toString());

                    String strmessage = message.toString();

                    SESMessage sesmessage = new SESMessage();
                    sesmessage.setMessage(subject, strmessage,true);
                    sesmessage.setFrom(SESMessage.DEFAULT_SENDER);


                    if (tolist.size() == 1) {
                    	sesmessage.setDestination(tolist);
                    } else
                    {
                    	sesmessage.setDestination(null, null, tolist);
                    }
                    SESEmail.getInstance().send(sesmessage);
                }
            }
        } catch (Exception e) {
            log4j.error("Unable to send TagsGroups invitation email!", e);
        }

    }

    /**
     * Get a Member object from a TagGroup
     *
     * @param groups
     * @param groupID
     * @param userID
     * @return
     */
    private Member getInviter(TagGroup[] groups, String groupID, String userID) {
        for (int i = 0; i < groups.length; i++) {
            if (groups[i].getGroupID().equals(groupID)) {
                return groups[i].getMember(userID);
            }
        }

        return null;
    }

    //
    //
    // GETTERS/SETTERS
    //
    //

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupdescription() {
        return groupdescription;
    }

    public void setGroupdescription(String groupdescription) {
        this.groupdescription = groupdescription;
    }

    public String getGroupcolor() {
        return groupcolor;
    }

    public void setGroupcolor(String groupcolor) {
        this.groupcolor = groupcolor;
    }

    public String getGroupinvite() {
        return groupinvite;
    }

    public void setGroupinvite(String groupinvite) {
        this.groupinvite = groupinvite;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public TagGroup[] getGroups() {
        return groups;
    }

    public void setGroups(TagGroup[] groups) {
        this.groups = groups;
    }

    public String[] getMember() {
        return member;
    }

    public void setMember(String[] member) {
        this.member = member;
    }

    public IColor[] getColors() {
        return colors;
    }

    public void setColors(IColor[] colors) {
        this.colors = colors;
    }

    public Member[] getMembers() {
        return members;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
