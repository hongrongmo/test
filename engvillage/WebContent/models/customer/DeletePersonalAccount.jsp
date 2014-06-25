<!--
	This page deletes all the information relating to the user from the system.
-->
<%@ page session="false" %>

<%@ page import="java.util.Properties"%>
<%@ page  import=" org.ei.domain.personalization.*"%>
<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.session.*"%>
<%@ page  import="org.ei.email.*"%>
<%@ page  import="org.ei.domain.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>



<%
    boolean personalization = false;
    boolean isPersonalizationPresent=true;

	//declare variable to hold client session.
	ControllerClient client = null;
	//declare variable to hold session id.
	String sSessionId = null;
	// declare variable to hold user id from the session.
	String sUserId = null;
	//declare variable to hold user id
	String nUserId = null;
	//declare variable to hold xml string.
	StringBuffer sb = new StringBuffer("<PAGE>");
	// declare variable to hold database param value.
	String database = null;
	//declare variable to hold searchid param value.
	String searchid = null;
	// declare variable to hold page number.
	String count = null;

	ClientCustomizer clientCustomizer=null;
	// Stores the source attribute of the customized logo image
	String customizedLogo="";

	if( request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}
	if( request.getParameter("count") != null)
	{
		count = request.getParameter("count");
	}
	if( request.getParameter("searchid") != null)
	{
        searchid = request.getParameter("searchid");
	}

	sb.append("<DATABASE>"+database+"</DATABASE>");
	sb.append("<COUNT>"+count+"</COUNT>");
	sb.append("<SEARCH-ID>"+searchid+"</SEARCH-ID>");

	// Create a session object using the Controllerclient object and
	// get the session id and user id from that object.
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();
	sSessionId = ussession.getID();
	sUserId = ussession.getUserIDFromSession();
	 IEVWebUser user = ussession.getUser();
   
    sb.append("<HEADER/>");
    sb.append(GlobalLinks.toXML(user.getCartridge()));
    sb.append("<FOOTER/>");

	sb.append("<SESSION-ID>"+sSessionId+"</SESSION-ID>");

	String customerId=user.getCustomerID().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		customizedLogo=clientCustomizer.getLogo();
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
	}

	//check for user id existance.
	if( sUserId != null)
	{
		nUserId = sUserId;
		personalization = true;		
	}

	PersonalAccount pAccount = new PersonalAccount();
	//remove the account from the system.
	boolean removeFlag = pAccount.removeUserProfile(nUserId);

	//build the xml to display the confirmation to the user.
	if(removeFlag)
	{
		sb.append("<USER-PROFILE>");
		sb.append("<USER-STATUS>updated</USER-STATUS>");
		sb.append("<USER-EXISTS>false</USER-EXISTS>");
		sb.append("</USER-PROFILE>");
	}
	//remove the user id from the user session and update the user session.
	Properties props = ussession.getProperties();
	props.remove("P_USER_ID");
	ussession.setProperties(props);
	client.updateUserSession(ussession);
	client.setRemoteControl();
    // jam - make sure outgoing XML reflects the fact they are logged out!
    personalization = false;		

	sb.append("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
	sb.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
	sb.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");
	sb.append("</PAGE>");
 	out.println(sb.toString());
%>







