<!--
This page expects the following params
* @param java.lang.String.displaylogin.
* @param java.lang.String.displayform.
* @param java.lang.String.database.
and
1. displays the login form if the displaylogin param value is not null.
2. displays the login form with error message if the user id or password is invalid.
3. redirects to the appropriate page based on the displayform value
if the authentication is successfull.
-->
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page import="org.ei.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.tags.*"%>
<%@ page import="java.io.*"%>

<%

    ControllerClient client = null;
    StringBuffer sb =new StringBuffer("<PAGE>");
    String database = null;
    String searchId = null;
    String count = null;
    String docids = null;
	String CID = null;
	String searchType = null;
    String optionValue = null;
    String queryId = null;
    String resultsFormat = null;
    String source = null;

    // Variable to hold the Personalizaton status
    String pUserId = null;
    boolean personalization = false;
    boolean isPersonalizationPresent=true;

    ClientCustomizer clientCustomizer=null;
    TagBroker tagBroker=null;
    Tag tag = null;
	// Stores the source attribute of the customized logo image
	String customizedLogo="";
	//get the client session object from that get the session id.
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();
	String sSessionId = ussession.getID();
	SessionID sessionIdObj = ussession.getSessionID();
	User user=ussession.getUser();
	String customerId=user.getCustomerID().trim();
	String strContractId = user.getContractID().trim();

	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		customizedLogo=clientCustomizer.getLogo();
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
	}

	pUserId = ussession.getProperty("P_USER_ID");
	if((pUserId != null) && (pUserId.length() != 0))
	{
		personalization=true;
	}

	database = clientCustomizer.getDefaultDB();
	sb.append("<DBMASK>").append(database).append("</DBMASK>");
    sb.append("<HEADER/><GROUPS-NAVIGATION-BAR/>");
    sb.append(GlobalLinks.toXML(user.getCartridge()));
    sb.append("<FOOTER/>");


    sb.append("<SESSION-ID>").append(sessionIdObj.toString()).append("</SESSION-ID>");
    sb.append("<CUSTOMIZED-LOGO>").append(customizedLogo ).append("</CUSTOMIZED-LOGO>");
    sb.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
    sb.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");


    String userID = null;
    String groupID = null;

    int iscope = Scope.SCOPE_PUBLIC;
    String scopeParam = null;

    if(request.getParameter("scope") != null)
    {
    	scopeParam = request.getParameter("scope");
    	if(scopeParam.indexOf(":") > -1)
    	{
    		String[] scopeParts = scopeParam.split(":");
    		iscope = Integer.parseInt(scopeParts[0]);
    		groupID = scopeParts[1];
    	}
    	else
    	{
    		iscope = Integer.parseInt(scopeParam);
    		if(iscope == Scope.SCOPE_PRIVATE)
    		{
    			userID = pUserId;
    		}
    	}
    }
    else
    {
    	scopeParam = Integer.toString(iscope);
    }

    String sort = request.getParameter("sort");
    Comparator comp = null;
    out.write(sb.toString());
    if(sort == null || sort.equals("alpha"))
	{
		comp = new AlphaComp();
		out.write("<SORT type='alpha' scope='");
		out.write(scopeParam);
		out.write("'/>");
    }
	else if(sort.equals("size"))
	{
		comp = new SizeComp();
		out.write("<SORT type='size' scope='");
		out.write(scopeParam);
		out.write("'/>");
	}
    else if(sort.equals("time"))
    {
		comp = new TimeComp();
		out.write("<SORT type='time' scope='");
		out.write(scopeParam);
		out.write("'/>");
	}

   TagGroupBroker groupBroker = new TagGroupBroker();
   TagGroup groups[] = null;
   if(pUserId != null)
   {
	   groups = groupBroker.getGroups(pUserId, false);
   }

   Scope sc = new Scope(pUserId,
   						iscope,
   			   			groupID,
   			   			customerId,
      				    groups);
   sc.toXML(out);
   TagBroker tbroker = new TagBroker(groupBroker);
   Tag[] tags = tbroker.getConsolidatedTags(75,
	    				             		iscope,
	    				             		userID,
	    				             		customerId,
    				             			groupID);
   TagCloud cloud = new TagCloud(tags,
   				 				 4,
   				 				 comp);
   cloud.toXML(out);
   out.write("<USERID>");
   if((pUserId != null) && (pUserId.length() != 0))
   {
   		out.write(pUserId);
   }
   out.write("</USERID>");
   out.write("</PAGE>");
%>


