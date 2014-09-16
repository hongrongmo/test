<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false"%>
<%@ page import=" java.util.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.bulletins.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="java.net.*"%>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%!


  ApplicationProperties eiProps = null;
  ClientCustomizer clientCustomizer=null;
  boolean isPersonalizationPresent=true;

  public void jspInit()
  {
    	try
    	{
      		eiProps = ApplicationProperties.getInstance();
    	}
    	catch(Exception e)
    	{
       		e.printStackTrace();
    	}
  }

 %>
 <%
     /**
     *   This page contains the hardcoded xml.
     */

     // This variable holds sessionId
     SessionID sessionId = null;
     String sesID = null;

     // This variable holds the url
     URL url = null;
%>

<%

    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();

    clientCustomizer=new ClientCustomizer(ussession);
    isPersonalizationPresent=clientCustomizer.checkPersonalization();
    sessionId = ussession.getSessionid();
    sesID = sessionId.toString();

    String cartridges[] = ussession.getCartridge();
    String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());
    //String appID = ussession.getProperty(UserSession.APPLICATION_KEY);

    BulletinBuilder builder = new BulletinBuilder();
    BulletinPage btPage = builder.buildRecentBulletins();

    boolean showLitPdf  = false;
    boolean showPatPdf  = false;
    boolean showLitHtml = false;
    boolean showPatHtml = false;
    boolean hasLIT      = false;
    boolean hasPAT      = false;

    for (int i = 0; i < cartridges.length; i++)
    {
	if(cartridges[i].toUpperCase().indexOf("LIT_HTM") > -1)
		showLitHtml = true;
	if(cartridges[i].toUpperCase().indexOf("PAT_HTM") > -1)
		showPatHtml = true;
	if(cartridges[i].toUpperCase().indexOf("LIT_PDF") > -1)
		showLitPdf = true;
	if(cartridges[i].toUpperCase().indexOf("PAT_PDF") > -1)
		showPatPdf = true;
	if(cartridges[i].toUpperCase().indexOf("LIT_HTM") > -1 || cartridges[i].toUpperCase().indexOf("LIT_PDF") > -1)
		hasLIT = true;
	if(cartridges[i].toUpperCase().indexOf("PAT_HTM") > -1 || cartridges[i].toUpperCase().indexOf("PAT_PDF") > -1)
		hasPAT = true;
    }

    //if(appID == null)
    //{
    //	appID = "Def";
    //}

    String queryString = request.getParameter("queryStr");
    String selectedDB = request.getParameter("database");

    BulletinQuery query = new BulletinQuery();

    String database = "";

    StringBuffer sbCartridges  = new StringBuffer();
    StringBuffer litCartridges = new StringBuffer();
    StringBuffer patCartridges = new StringBuffer();
    BulletinGUI gui = new BulletinGUI();


    if(queryString != null)
    {
	query.setQuery(queryString);
    }
    else
    {
	if(hasLIT)
	{
		query.setDatabase("1");
	}
	else if (hasPAT)
	{
		query.setDatabase("2");
	}

	query.setCategory("");
	query.setYr("");
    }

    database = query.getDatabase();
    boolean showPdf = false;
    boolean showHtml = false;


    BulletinXMLVisitor xmlVisitor = new BulletinXMLVisitor(out,cartridges);

    for (int i = 0; i < cartridges.length; i++)
    {

        if(gui.validCartridge(database,cartridges[i]))
        {
        	sbCartridges.append(cartridges[i].toUpperCase());
        	 if(i != cartridges.length - 1)
        	sbCartridges.append(";");
        }
        if(gui.isLITCartridge(cartridges[i]))
        {
        	litCartridges.append(cartridges[i].toUpperCase());
        	if(i != cartridges.length - 1)
        	litCartridges.append(";");
        }

        if(gui.isPATCartridge(cartridges[i]))
        {
        	patCartridges.append(cartridges[i].toUpperCase());
        	if(i != cartridges.length - 1)
        	patCartridges.append(";");
        }
    }



    //String resourcePath = eiProps.getProperty("resourcePath"+appID);
    //String resourcePath = eiProps.getProperty("resourcePath");

    //client.log("EISESSION", sessionId);
    client.log("request", "recentBulletins");
    client.setRemoteControl();

    out.write("<PAGE>");
    out.write("<HEADER/>");
    out.write("<FOOTER/>");
    out.write(strGlobalLinksXML);
    out.write("<CARTRIDGES><![CDATA["+sbCartridges.toString()+"]]></CARTRIDGES>");
    out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
    out.write("<PATCR><![CDATA["+patCartridges.toString().toUpperCase()+"]]></PATCR>");
    out.write("<LITCR><![CDATA["+litCartridges.toString().toUpperCase()+"]]></LITCR>");
    out.write("<SELECTED-DB><![CDATA["+selectedDB+"]]></SELECTED-DB>");
    out.write("<HAS-LIT><![CDATA["+hasLIT+"]]></HAS-LIT>");
    out.write("<HAS-PAT><![CDATA["+hasPAT+"]]></HAS-PAT>");
    out.write("<LIT-HTML><![CDATA["+showLitHtml+"]]></LIT-HTML>");
    out.write("<LIT-PDF><![CDATA["+showLitPdf+"]]></LIT-PDF>");
    out.write("<PAT-HTML><![CDATA["+showPatHtml+"]]></PAT-HTML>");
    out.write("<PAT-PDF><![CDATA["+showPatPdf+"]]></PAT-PDF>");
    out.write("<SELECTED-DB><![CDATA["+selectedDB+"]]></SELECTED-DB>");
    //out.write("<RESOURCE-PATH>"+resourcePath+"</RESOURCE-PATH>");
    out.write("<SESSION-ID>"+sessionId+"</SESSION-ID>");
    out.write("<QTOP>");
    out.write("<QSTR><![CDATA[");
    out.write(query.toString());
    out.write("]]></QSTR>");
    out.write("</QTOP>");

    if(btPage != null)
    {
	out.write("<BULLETINS>");
	btPage.accept(xmlVisitor);
	out.write("</BULLETINS>");
    }
    out.write("</PAGE>");

%>