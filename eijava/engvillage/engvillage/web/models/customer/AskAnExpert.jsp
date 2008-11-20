<!-- This file creates the xml for result coming from serach result page or from
     selected set page.
-->
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.config.*"%>

<%@ page errorPage="/error/errorPage.jsp" %>
<!--setting page buffer size to 20kb-->
<%@ page buffer="20kb"%>


<%
    // This variable for sessionId
    String sessionId="";
    // This variable for searchId
    String searchId="";
    // This variable for database name
    String database="";

    String section="";
    String sUserId="";
    String userId = null;
    SessionID sessionIdObj = null;

    // This variable is used to hold ControllerClient instance
    ControllerClient client = new ControllerClient(request, response);


    /**
     *  Getting the UserSession object from the Controller client .
     *  Getting the session id from the usersession.
     */
    UserSession ussession=(UserSession)client.getUserSession();
    sessionId=ussession.getID();
    sessionIdObj = ussession.getSessionID();

    sUserId=ussession.getProperty("P_USER_ID");

    if( sUserId != null)
    {
        userId = sUserId;
    }

    if(request.getParameter("searchid") != null)
    {
        searchId = request.getParameter("searchid");
    }

    if(request.getParameter("database") != null)
    {
        database = request.getParameter("database");
    }
    if(request.getParameter("section") != null)
    {
        section = request.getParameter("section");
    }


    if(true)
    {
      //Writing the XML
      out.write("<PAGE>");
      out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
      out.write("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
      out.write("<DATABASE>"+database+"</DATABASE>");
      out.write("<SECTION>"+section+"</SECTION>");
      out.write("<ERROR-PAGE>false</ERROR-PAGE>");
      out.write("</PAGE>");
      out.write("<!--END-->");
      out.flush();
    }
    else
    {
      out.write("<PAGE>");
      out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
      out.write("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
      out.write("<DATABASE>"+database+"</DATABASE>");
      out.write("<ERROR-PAGE>true</ERROR-PAGE>");
      out.write("</PAGE>");
      out.write("<!--END-->");
      out.flush();
    }
%>