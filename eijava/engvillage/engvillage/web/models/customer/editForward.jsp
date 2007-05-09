<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.domain.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.*"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page  errorPage="/error/errorPage.jsp"%><%
	SessionID sessionIdObj = null;
	ControllerClient client = null;
%><%!
    int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);
    int pagesize = 0;
    int dedupSetSize = 0;
    DatabaseConfig databaseConfig = null;

    public void jspInit()
    {
        try
        {
            RuntimeProperties runtimeProps = ConfigService.getRuntimeProperties();
            pagesize = Integer.parseInt(runtimeProps.getProperty("PAGESIZE"));
            dedupSetSize = Integer.parseInt(runtimeProps.getProperty("DEDUPSETSIZE"));

            databaseConfig = DatabaseConfig.getInstance();

            // jam Y2K3
            customizedEndYear = Integer.parseInt(runtimeProps.getProperty("SYSTEM_ENDYEAR"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
%><%
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	User user=ussession.getUser();
	String pUserId = ussession.getProperty("P_USER_ID");
	String customerId=user.getCustomerID().trim();
	String docID = null;
	if (request.getParameter("tagdoc") != null)
	{
		docID = request.getParameter("tagdoc");
	}

   Map requestMap = request.getParameterMap();
   TagEditor editor = new TagEditor(requestMap,
   									pUserId,
            						customerId,
            						docID);
 	client.setRedirectURL("/controller/servlet/Controller?"+request.getParameter("RETURNURL"));
	client.setRemoteControl();
%>