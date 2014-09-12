<!--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.database
 */
-->
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page  import=" java.util.Enumeration"%>
<%@ page  import=" java.net.URLEncoder"%>

<!--import statements of ei packages.-->
<%@ page  import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page  import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>

<%@ page  import="org.ei.query.base.*"%>
<%@ page  import="org.ei.domain.*"%>

<%@ page  errorPage="/error/errorPage.jsp"%>

<%

	/**
	 *  Getting the UserSession object from the Controller client .
	 *  Getting the session id from the usersession.
	 *
	 */
    ControllerClient client = new ControllerClient(request, response);
	UserSession ussession = (UserSession) client.getUserSession();
    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);

    String defaultDB;
	String startPage;
    String customizedStartPage = null;

	if(clientCustomizer.getStartPage() != null)
	{
		startPage = clientCustomizer.getStartPage();
	}
	else
	{
		startPage = "quickSearch";
	}

    if(request.getParameter("database")!=null)
    {
    	defaultDB=request.getParameter("database").trim();
    }
    else if(clientCustomizer.getDefaultDB() != null)
	{
  		defaultDB = clientCustomizer.getDefaultDB();
  	}
  	else
  	{
  		defaultDB = "1";
  	}

  	customizedStartPage = "CID="+startPage + "&database="+defaultDB;

  	StringBuffer buff=new StringBuffer();
  	buff.append("/controller/servlet/Controller?").append(customizedStartPage);

	client.setRedirectURL(buff.toString());
	client.setRemoteControl();
	return;
%>