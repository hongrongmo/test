<%@page import="org.ei.config.ApplicationProperties"%>
<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.domain.*"%><%@ page import="org.engvillage.biz.controller.ControllerClient"%><%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.*"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page  errorPage="/error/errorPage.jsp"%><%
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
            ApplicationProperties runtimeProps = ApplicationProperties.getInstance();
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
	String pUserId = ussession.getUserid();
	String customerId=ussession.getCustomerid().trim();
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