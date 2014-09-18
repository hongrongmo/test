<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="org.ei.tags.*"%><%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page import="org.engvillage.biz.controller.ControllerClient"%><%@ page import="org.ei.email.*"%><%@ page import="javax.mail.internet.*"%><%

	String emailgroupid = null;
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	String pUserId = ussession.getUserid();
	String customerId=ussession.getCustomerid().trim();

	int scopeint = -1;
	String groupID = null;
	if (request.getParameter("scope")!= null)
	{
		String scope_group = request.getParameter("scope").trim();
		if (scope_group.equals("1") ||
			scope_group.equals("2") ||
			scope_group.equals("4"))
		{
			scopeint = Integer.parseInt(scope_group);
		}
		else
		{
			scopeint = Scope.SCOPE_GROUP;
			groupID = scope_group;
		}
	}


	String multiTag = request.getParameter("tag");
	String mtags[] = multiTag.split(",");
	TagBroker broker = new TagBroker();

	for(int i=0; i<mtags.length; i++)
	{
		String theTag = mtags[i].trim();
		Tag ttag = new Tag();
		ttag.setTag(theTag);
		String tagDoc = request.getParameter("tagdoc");
		String[] tdocParts = tagDoc.split(":");

		ttag.setDocID(tdocParts[0]);
		ttag.setMask(Integer.parseInt(tdocParts[1]));
		if(tdocParts.length > 2)
		{
			ttag.setCollection(tdocParts[2]);
		}

		ttag.setCustID(customerId);
		ttag.setUserID(pUserId);
		ttag.setScope(scopeint);
		ttag.setGroupID(groupID);
		broker.addTag(ttag);
	}

	if (request.getParameter("notifygroup") != null &&
		request.getParameter("notifygroup").equals("on"))
	{
		try
		{
			TagGroupBroker groupBroker = new TagGroupBroker();
			TagGroup group = groupBroker.getGroup(groupID, true);
			List toList = getEmailList(group.getMembers());

			Member notifier = group.getMember(pUserId);
			String from=notifier.getEmail();

  		/* remove the notifier from the to List */
  		toList.remove(from);

			String atag = request.getParameter("tag");
			String subject= "Engineering Village Tag Group Notification";
			StringBuffer message = new StringBuffer();
			message.append("Note to all members of the Tag Group: ");
			message.append(group.getTitle());
			message.append("<br>\r\n");
			message.append("<br>\r\n");
			message.append(notifier.getFullName());
			message.append(" is sending notification that the tag(s) below have been applied for the group:");
			message.append("<br>\r\n");
			message.append("<br>\r\n");
			message.append(multiTag);

			String recentTags = getRecentTags(group.getTags(),mtags);
			if(recentTags.length() > 0)
			{
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
			System.out.println(strmessage);

			long lo=System.currentTimeMillis();
			java.util.Date d=new java.util.Date(lo);
			EMail email=EMail.getInstance();
			EIMessage eimessage = new EIMessage();
			//setSender no longer sets from address - use setFrom for From and setSender for Sender
			//eimessage.setSender(from);
      eimessage.setSender(EIMessage.DEFAULT_SENDER);
      eimessage.setFrom(from);

			eimessage.addTORecepient(from);
			eimessage.addBCCRecepients(toList);
			eimessage.setSubject(subject);
			eimessage.setSentDate(d);
      eimessage.setContentType("text/html");
			eimessage.setMessageBody(strmessage);
			email.sendMessage(eimessage);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	client.setRedirectURL("/controller/servlet/Controller?"+request.getParameter("RETURNURL"));
	client.setRemoteControl();

	%><%!

	private List getEmailList(Member[] members)
	{
		List tolist = new ArrayList();
		for (int i = 0; i < members.length ; i++)
		{
			tolist.add(members[i].getEmail());
		}

		return tolist;
	}


	private String getRecentTags(Tag[] tags,
						   String[] appliedTags)
	{
		StringBuffer tagBuf = new StringBuffer();
		if(tags != null)
		{
			long curtime = System.currentTimeMillis();
			curtime = curtime - 86400000;
			for(int i=0; i<tags.length;i++)
			{
				long lasttouched = tags[i].getTimestamp();
				if(lasttouched > curtime && notIn(tags[i].getTagName(), appliedTags))
				{
					if(tagBuf.length() > 0)
					{
						tagBuf.append(", ");
					}
					tagBuf.append(tags[i].getTagName());
				}
			}
		}
		return tagBuf.toString();
	}

	private boolean notIn(String tagName,
						  String[] appliedTags)
	{
		for(int i=0;i<appliedTags.length; i++)
		{
			if(tagName.equalsIgnoreCase(appliedTags[i]))
			{
				return false;
			}
		}
		return true;
	}
	%>