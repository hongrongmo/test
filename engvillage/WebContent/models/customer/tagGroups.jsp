<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java"%><%@ page session="false"%><%@ page
	import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page
	import="org.ei.domain.*"%><%@ page
	import="org.engvillage.biz.controller.ControllerClient"%><%@ page
	import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page
	import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page
	import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.*"%><%@ page
	import="java.util.Enumeration"%><%@ page import="org.ei.domain.*"%><%@ page
	import="org.ei.config.*"%><%@ page import="org.ei.email.*"%><%@ page
	import="org.ei.util.*"%><%@ page import="java.util.*"%><%@ page
	import="java.net.*"%><%@ page import="java.io.*"%><%@ page
	import="javax.mail.internet.*"%><%@ page
	import="org.ei.domain.personalization.GlobalLinks"%><%@ page
	import="org.ei.domain.personalization.SavedSearches"%><%@ page
	errorPage="/error/errorPage.jsp"%>
<%
    String gcolor = null;
    String gtitle = null;
    String gdescription = null;
    String editgroupID = null;
    ControllerClient client = null;
    String database = null;
    String customizedLogo = "";
    boolean isPersonalizationPresent = true;
    boolean personalization = false;
    List docIDList = null;
    String flagedit = null;
    String flagadd = null;
    String inviteMembers = null;
    String invitaitiongroupid = null;

    client = new ControllerClient(request, response);
    UserSession ussession = (UserSession) client.getUserSession();
    String[] credentials = ussession.getCartridge();
    String pUserId = ussession.getUserid();

    TagGroupBroker groupBroker = new TagGroupBroker();
    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);
    database = clientCustomizer.getDefaultDB();

    if (request.getParameter("CGROUPID") != null) {
        editgroupID = request.getParameter("CGROUPID");
    }

    if (request.getParameter("title") != null) {
        gtitle = request.getParameter("title");
    }

    if (request.getParameter("description") != null) {
        gdescription = request.getParameter("description");
    }

    if (request.getParameter("gcolor") != null) {
        gcolor = request.getParameter("gcolor");
    }

    if (request.getParameter("EDIT") != null) {
        flagedit = request.getParameter("EDIT");
    }

    if (request.getParameter("ADD") != null) {
        flagadd = request.getParameter("ADD");
    }

    String[] members = request.getParameterValues("member");

    if (clientCustomizer.isCustomized()) {
        isPersonalizationPresent = clientCustomizer.checkPersonalization();
        customizedLogo = clientCustomizer.getLogo();
    }

    if ((pUserId != null) && (pUserId.trim().length() != 0)) {
        personalization = true;
    } else {
        /*
         *	No personal account so redirect to the login screen.
         */
        client
            .setRedirectURL("/controller/servlet/Controller?CID=personalLoginForm&displaylogin=true&database=27");
        client.setRemoteControl();
        return;
    }

    String inviteID = null;

    if (flagadd != null && !flagadd.trim().equals("")) {
        TagGroup addGroup = new TagGroup();
        addGroup.setDatefounded();
        addGroup.setTitle(gtitle);
        addGroup.setDescription(gdescription);
        addGroup.setColor(gcolor);
        addGroup.setOwnerid(pUserId);
        if (gtitle != null && pUserId != null) {
            inviteID = groupBroker.addGroup(addGroup);
        }
    }

    if (flagedit != null && !flagedit.trim().equals("")) {
        inviteID = editgroupID;
        TagGroup tgroup = new TagGroup();
        tgroup.setGroupID(editgroupID);
        tgroup.setDescription(gdescription);
        tgroup.setColor(gcolor);
        groupBroker.updateGroup(tgroup, members);
    }

    //Send email
    if (request.getParameter("invitelist") != null) {
        inviteMembers = request.getParameter("invitelist");
    }

    TagGroup[] groups = groupBroker.getGroups(pUserId, true);

    if (inviteMembers != null) {
        Member inviter = getInviter(groups, inviteID, pUserId);
        List tolist = new ArrayList();
        String[] emailaddresses = inviteMembers.split(",");
        for (int i = 0; i < emailaddresses.length; i++) {
            if (emailaddresses[i] != null && !emailaddresses[i].trim().equals("")) {
                /* don't add the inviter to the to: list - they are the main recipient now */
                if (!emailaddresses[i].trim().equals(inviter.getEmail())) {
                    tolist.add((String) emailaddresses[i].trim());
                }
            }
        }
        if (tolist.size() > 0) {
            try {
                String accptLink = "http://www.engineeringvillage.com/controller/servlet/Controller?CID=tagAcceptInvitation&groupid=" + inviteID;
                String from = inviter.getEmail();
                StringBuffer subjectBuf = new StringBuffer();
                subjectBuf.append("Engineering Village Tag Group invitation from ");
                subjectBuf.append(inviter.getFullName());
                String subject = subjectBuf.toString();
                StringBuffer message = new StringBuffer();
                message.append(inviter.getFullName());
                message.append(" invites you to join the Engineering Village Tag Group: ");
                message.append(gtitle);
                message.append(".<br>\r\n");
                message.append("<br>\r\n");
                message.append("<br>\r\n");
                message.append("Click on the link <a href=\"").append(accptLink).append("\">");
                message.append(accptLink).append("</a> to accept the invitation.<br>\r\n");
                message.append("<br>\r\n");
                message.append("<br>\r\n");

                System.out.println(message.toString());
                String strmessage = message.toString();
                long lo = System.currentTimeMillis();
                java.util.Date d = new java.util.Date(lo);
                EMail email = EMail.getInstance();
                EIMessage eimessage = new EIMessage();
                //setSender no longer sets from address - use setFrom for From and setSender for Sender
                //eimessage.setSender(from);
                eimessage.setSender(EIMessage.DEFAULT_SENDER);
                eimessage.setFrom(from);

                if (tolist.size() == 1) {
                    /* send to the dfsingle recipient */
                    eimessage.addTORecepients(tolist);
                } else /* bcc the group list */
                {
                    eimessage.addBCCRecepients(tolist);
                }
                eimessage.setSubject(subject);
                eimessage.setSentDate(d);
                eimessage.setContentType("text/html");
                eimessage.setMessageBody(strmessage);
                email.sendMessage(eimessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     client.log("SEARCH_ID", searchID);
     client.log("QUERY_STRING", tQuery.getDisplayQuery());
     client.log("DB_NAME", Integer.toString(did.getDatabase().getMask()));
     client.log("PAGE_SIZE", Integer.toString(pagesize));
     client.log("HIT_COUNT", totalDocCount);
     client.log("NUM_RECS", "1");
     client.log("context", "search");
     client.log("DOC_INDEX", (new Integer(index)).toString());
     client.log("DOC_ID", did.getDocID());
     client.log("FORMAT", dataFormat);
     client.log("ACTION", "document");
     client.setRemoteControl();
     */
    String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

    out.write("<PAGE>");
    out.write("<SESSION-ID>" + ussession.getSessionid() + "</SESSION-ID>");
    out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
    out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");
    out.write("<PERSONALIZATION>" + personalization + "</PERSONALIZATION>");
    out.write("<PUSERID>" + pUserId + "</PUSERID>");
    out.write("<HEADER/>");
    out.write(strGlobalLinksXML);
    out.write("<FOOTER/>");
    out.write("<DBMASK>" + database + "</DBMASK>");
    out.write("<ADDTAGSELECTRANGE-NAVIGATION-BAR/>");
    out.write("<GROUPS-NAVIGATION-BAR/>");
    if (groups != null && groups.length > 0) {
        out.write("<TGROUPS>");
        for (int i = 0; i < groups.length; i++) {
            groups[i].toXML(out);
        }
        out.write("</TGROUPS>");
    }
    out.write("</PAGE>");
    out.write("<!--END-->");
    out.flush();
%><%!private Member getInviter(TagGroup[] groups, String groupID, String userID) {
        for (int i = 0; i < groups.length; i++) {
            if (groups[i].getGroupID().equals(groupID)) {
                return groups[i].getMember(userID);
            }
        }

        return null;
    }%>
