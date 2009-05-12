<%@ page session="false" %>
<%@ page import="org.ei.thesaurus.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.ei.controller.*" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.util.StringUtil"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%

    boolean isPersonalizationPresent=true;
    boolean isFullTextPresent=true;
    boolean isEmailAlertsPresent=true;
    int customizedStartYear=1970;
    int customizedEndYear=1970;
    String customizedLogo="";
    String pUserId = "";
    boolean personalization = false;
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();


    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();
    User user = ussession.getUser();


    String customerId = user.getCustomerID().trim();

    String sessionID = ussession.getID();
    SessionID sessionIdObj = ussession.getSessionID();

    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);
    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());


    String term = request.getParameter("term");
    String index = request.getParameter("index");
    String dbName = request.getParameter("database");
    String databaseID = null;

    pUserId = ussession.getProperty("P_USER_ID");
    if((pUserId != null) && (pUserId.trim().length() != 0))
    {
        personalization=true;
    }


    if(clientCustomizer.isCustomized())
    {
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
        isFullTextPresent=clientCustomizer.checkFullText();
        isEmailAlertsPresent=clientCustomizer.checkEmailAlert();
        customizedLogo=clientCustomizer.getLogo();
    }

    if(dbName == null ||
      !dbName.equals("2") ||
       dbName.length() == 0)
    {


        databaseID = "cpx";

        if(clientCustomizer.getStartYear()!=-1)
        {
            customizedStartYear=clientCustomizer.getStartYear();

        }
        if(clientCustomizer.getEndYear()!=-1)
        {
            customizedEndYear=clientCustomizer.getEndYear();
        }
    }
    else if(dbName.equals("2"))
    {
        databaseID = "ins";
    }
    else if(dbName.equals("8192"))
    {
        databaseID = "geo";
    }    
    else if(dbName.equals("2097152"))
    {
        databaseID = "grf";
    }


    out.write("<DOC>");
    out.write("<HEADER/>");
    out.write("<THESAURUS-HEADER/>");
    out.write("<SEARCH-TYPE>Thesaurus</SEARCH-TYPE>");
    out.write("<HAS-INSPEC>"+UserCredentials.hasCredentials(2, databaseConfig.getMask(user.getCartridge()))+"</HAS-INSPEC>");
    out.write("<HAS-CPX>"+UserCredentials.hasCredentials(1, databaseConfig.getMask(user.getCartridge()))+"</HAS-CPX>");
    out.write("<HAS-GEO>"+UserCredentials.hasCredentials(8192, databaseConfig.getMask(user.getCartridge()))+"</HAS-GEO>");
    out.write("<HAS-GRF>"+UserCredentials.hasCredentials(2097152, databaseConfig.getMask(user.getCartridge()))+"</HAS-GRF>");
    out.write(strGlobalLinksXML);
    out.write("<FOOTER/>");
    out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
    out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
    out.write("<CUSTOMIZED-STARTYEAR>"+customizedStartYear+"</CUSTOMIZED-STARTYEAR>");
    out.write("<CUSTOMIZED-ENDYEAR>"+customizedEndYear+"</CUSTOMIZED-ENDYEAR>");
    out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
    out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
    out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
    out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
    out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
    out.write("<PERSON-USER-ID>"+pUserId+"</PERSON-USER-ID>");
    out.write("<DATABASE>");
    out.write(dbName);
    out.write("</DATABASE>");
%>
<%@ include file="../database.jsp"%>

<%
    out.write("</DOC>"); 
%>
