<%--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String
--%>
<%@ page language="java" %>
<%@ page session="false"%>
<%@ page contentType="text/xml"%>

<%-- import statements of Java packages --%>
<%@ page import="java.util.*"%>

<%-- import statements of ei packages. --%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>

<%@ page errorPage="/error/errorPage.jsp"%>
<%

	SessionID sessionIdObj = null;

	// Variable to hold the Personalization userid
	String pUserId = null;
	// Variable to hold the Personalizaton status
	boolean personalization = false;

	// variable to hold database name
	String database = null;
	//variable to hold personalization feature
	boolean  isPersonalizationPresent=true;
	String customizedLogo="";
	
	String refEmail = "";
	
	if(request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}


	ControllerClient client = new ControllerClient(request, response);


	UserSession ussession=(UserSession)client.getUserSession();

	//client.updateUserSession(ussession);
	sessionIdObj = ussession.getSessionID();

	pUserId = ussession.getProperty("P_USER_ID");
	if((pUserId != null) && (pUserId.trim().length() != 0)){
		personalization=true;
	}
    User user=ussession.getUser();
	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
	
	if(clientCustomizer.getRefEmail() != null &&
		clientCustomizer.getRefEmail().length()>0)
	{
		refEmail = clientCustomizer.getRefEmail();
	}

    log("refEmail: "+refEmail);	
    
	if(clientCustomizer.isCustomized())
	{
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
		customizedLogo=clientCustomizer.getLogo();
	}

	client.setRemoteControl();

	String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
%>
<PAGE>
<SESSION-ID><%=sessionIdObj.toString()%></SESSION-ID>
<CUSTOMIZED-LOGO><%=customizedLogo%></CUSTOMIZED-LOGO>
<PERSONALIZATION-PRESENT><%=isPersonalizationPresent%></PERSONALIZATION-PRESENT>
<PERSONALIZATION><%=personalization%></PERSONALIZATION>
<REFEMAIL><%=refEmail%></REFEMAIL>
<HEADER/>
<%=	strGlobalLinksXML %>
<FOOTER/>
<DBMASK><%=database%></DBMASK>
</PAGE>