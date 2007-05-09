<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.domain.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.*"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page  errorPage="/error/errorPage.jsp"%><%
	SessionID sessionIdObj = null;
	Database databaseObj = null;
	ControllerClient client = null;
	String database = null;
	String customizedLogo="";
	boolean  isPersonalizationPresent=true;
	boolean personalization = false;
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionIdObj = ussession.getSessionID();
	User user=ussession.getUser();

	String customerId=user.getCustomerID().trim();
	String pUserId = ussession.getProperty("P_USER_ID");

	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);

    if(clientCustomizer.isCustomized())
    {
    	isPersonalizationPresent=clientCustomizer.checkPersonalization();
    	customizedLogo=clientCustomizer.getLogo();
    }

	if((pUserId != null) && (pUserId.trim().length() != 0))
	{
		personalization=true;
	}
	else
	{
		/*
		*	No personal account so redirect to the login screen.
		*/
		client.setRedirectURL("/controller/servlet/Controller?CID=personalLoginForm&displaylogin=true&database=27&nexturl=CID%3DdeleteTag&backurl=CID%3DdeleteTag");
		client.setRemoteControl();
		return;
	}

	database = clientCustomizer.getDefaultDB();

	String deleteTag = request.getParameter("deletetag");
    TagBroker tagBroker = new TagBroker();
    int scopeint = 1;
	String groupID = null;
	String scope = request.getParameter("scope");
	if(scope != null)
	{
		if(!scope.equals("1") &&
		   !scope.equals("2") &&
		   !scope.equals("4"))
		{
			scopeint = 3;
			groupID = scope;
		}
		else
		{
			scopeint = Integer.parseInt(scope);
		}
	}

	if(deleteTag != null)
	{
		tagBroker.deleteTag(deleteTag,
							scopeint,
							pUserId,
							customerId,
							groupID,
							null);
	}

	String[] taglabels = tagBroker.getUserTagNames(scopeint,
							   pUserId,
							   customerId,
							   groupID);
    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
	out.write("<PAGE>");

	if((pUserId != null) && (pUserId.trim().length() != 0))
	{
		TagGroupBroker groupBroker = new TagGroupBroker();
		TagGroup[] tgroups = groupBroker.getGroups(pUserId, false);
		if (tgroups != null && tgroups.length > 0)
		{
			out.write("<TGROUPS>");
			for(int i=0;i<tgroups.length; i++)
			{
				tgroups[i].toXML(out);
			}
			out.write("</TGROUPS>");
		}
	}
	out.write("<SCOPE>");

	if(scope == null || scope.trim().equals(""))
	{
		scope = "1";
	}
	out.write(scope);
	out.write("</SCOPE>");

	out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
	out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
	out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
	out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
	out.write("<HEADER/>");
	out.write(strGlobalLinksXML);
	out.write("<FOOTER/>");
	out.write("<DBMASK>"+database+"</DBMASK>");
	out.write("<ADDTAGSELECTRANGE-NAVIGATION-BAR/>");
	out.write("<GROUPS-NAVIGATION-BAR/>");
	out.write("<TAGS>");
	for(int i = 0; i < taglabels.length; i++)
	{
    	out.write("<TAG><![CDATA[");
    	out.write(taglabels[i]);
    	out.write("]]></TAG>");
	}

	out.write("</TAGS>");
	out.write("</PAGE>");
	out.write("<!--END-->");
	out.flush();

%>
