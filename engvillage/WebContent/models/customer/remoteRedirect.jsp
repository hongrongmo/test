<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<!-- import statements of ei packages.-->
<%@ page import="org.engvillage.biz.controller.ControllerClient" %>
<%@ page import="org.engvillage.biz.controller.UserSession" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.util.StringUtil" %>

<%
    //Getting controllerclient for the requset and response object
    ControllerClient client = new ControllerClient(request, response);

    /**
     *  Getting the UserSession object from the Controller client .
     *  Getting the session id from the usersession.
     */
    UserSession ussession = (UserSession)client.getUserSession();
    String sessionId = sessionId=ussession.getSessionid();
    
    String queryID = request.getParameter("SEARCHID");
    Query query = Searches.getSearch(queryID);

    int selectedDB = query.getDataBase();
    String searchType = query.getSearchType();

    String urlString=StringUtil.EMPTY_STRING;

    if(selectedDB == 16)
    {
        urlString = "/controller/servlet/Controller?CID=engnetbase" + searchType;
    }
    else if(selectedDB == 8)
    {
        urlString = "/controller/servlet/Controller?CID=uspto" + searchType;
    }

    urlString = urlString.concat("&SEARCHID=").concat(queryID).concat("&database=").concat(String.valueOf(selectedDB)).concat("&refreshOnPersonalLogin=").concat("true");
    client.setRedirectURL(urlString);
    client.setRemoteControl();
%>