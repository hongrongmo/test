<%@ page language="java"%>
<%@ page session="false"%>
<%@ page contentType="text/xml"%>

<%@ page import="java.util.*"%>
<%@ page import="javax.mail.internet.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.components.feedback.*"%>
<%@ page import="org.ei.email.*"%>

<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>

<%@ page errorPage="/error/errorPage.jsp"%>


<%
	// Variable to hold sessionId
	String sessionId = null;
	// Variable to hold the Personalization userid
	String pUserId = null;
	// Variable to hold the Personalizaton status
	boolean personalization = false;

	// variable to hold database name
	String database = null;
	//variable to hold personalization feature
	boolean  isPersonalizationPresent=true;
	String customizedLogo="";

	SessionID sessionIdObj = null;

	String username = request.getParameter("fullname");
	String userMailAddress = request.getParameter("email");
	String topic = request.getParameter("comment");
	String feedbackText = request.getParameter("suggestions");

	if(request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}

	ControllerClient client = new ControllerClient(request, response);

	UserSession ussession=(UserSession)client.getUserSession();

	sessionIdObj = ussession.getSessionID();

	pUserId = ussession.getUserIDFromSession();
	if((pUserId != null) && (pUserId.trim().length() != 0)){
		personalization=true;
	}
   IEVWebUser user = ussession.getUser();
	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
		customizedLogo=clientCustomizer.getLogo();
	}
  if(feedbackText != null)
  {
    String userinfo = System.getProperty("line.separator") + "---------------------------------------------------" + System.getProperty("line.separator") + ussession.getUser().toString();
    feedbackText = feedbackText.concat(userinfo);
  }
	String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

	try {
		FeedbackForm feedbackForm = new FeedbackForm();
		feedbackForm.setUsername(username);
		feedbackForm.setUserMailAddress(userMailAddress);
		feedbackForm.setTopic(topic);
		feedbackForm.setFeedbackText(feedbackText);

		FeedbackComponent feedbackComponent = new FeedbackComponent();
		feedbackComponent.init();
		feedbackComponent.submit(feedbackForm);
	} catch(Exception e) {
		e.printStackTrace();
	}

%>
<PAGE>
<SESSION-ID><%=sessionId%></SESSION-ID>
<CUSTOMIZED-LOGO><%=customizedLogo%></CUSTOMIZED-LOGO>
<PERSONALIZATION-PRESENT><%=isPersonalizationPresent%></PERSONALIZATION-PRESENT>
<PERSONALIZATION><%=personalization%></PERSONALIZATION>
<HEADER/>
<%=	strGlobalLinksXML %>
<FOOTER/>
<DBMASK><%=database%></DBMASK>
</PAGE>

