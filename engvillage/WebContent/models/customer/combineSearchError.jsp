<!--
   This page the follwing params as input and generates XML output.
   @param java.lang.String.database
-->

<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->

<%@ page import=" java.util.*"%>
<%@ page import=" java.net.*"%>

<!--import statements of ei packages.-->

<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page  errorPage="/error/errorPage.jsp"%>

<!--Setting the buffer size to 20kb.-->
<%@ page  buffer="20kb"%>

<%

    //This variable for Query Object
    Query queryObject =new Query();

    // This variable is used to hold ControllerClient instance
    ControllerClient client = new ControllerClient(request, response);

    // This variable for sessionId
    String sessionId = null;

    // Variable to hold the Personalization userid
    String pUserId = null;
    // Variable to hold the Personalizaton status
    boolean personalization = false;


    //This variable is used the recent query in the xml format
    String recentXmlQueryString=null;


    //This vriable for batabase name
    String dbName=null;
    //this variables for client customization
    ClientCustomizer clientCustomizer=null;
    boolean isPersonalizationPresent=true;
    String customizedLogo="";

    if(request.getParameter("database")!=null)
    {
        dbName=request.getParameter("database").trim();
    }

    /**
    *  Getting the UserSession object from the Controller client .
    *  Getting the session id from the usersession.
    *
    */

        UserSession ussession=(UserSession)client.getUserSession();
        //client.updateUserSession(ussession);
        sessionId=ussession.getSessionid();
        pUserId = ussession.getUserid();
        if((pUserId != null) && (pUserId.trim().length() != 0))
        {
            personalization=true;
        }

        String customerId=ussession.getCustomerid().trim();
        clientCustomizer=new ClientCustomizer(ussession);
        if(clientCustomizer.isCustomized())
        {
            isPersonalizationPresent=clientCustomizer.checkPersonalization();
            customizedLogo=clientCustomizer.getLogo();
        }


        String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

        out.write("<PAGE>");
        out.write("<HEADER/>");

        out.write(strGlobalLinksXML);

        out.write("<NAVIGATION-BAR/>");
        out.write("<FOOTER/>");
        out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
        out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
        out.write("<DBMASK>"+dbName+"</DBMASK>");
        out.write("<SESSION-ID>"+sessionId+"</SESSION-ID>");
        out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
        if (request.getParameter("dberr") != null && request.getParameter("dberr").equals("mismatch")) {
            out.write("<ERRSTR>Only searches from the same database can be combined.</ERRSTR>");
        } else if (request.getParameter("database") != null) {
            if (request.getParameter("database").equals("8") || (request.getParameter("database").equals("16"))) {
                out.write("<ERRSTR>Remote searches can not be combined.</ERRSTR>");
            }
        }
        out.write("</PAGE>");
        out.print("<!--END-->");


%>
