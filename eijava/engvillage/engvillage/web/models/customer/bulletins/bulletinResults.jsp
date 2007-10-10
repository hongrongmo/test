<%@ page language="java" %>
<%@ page session="false"%>
<%@ page import=" java.util.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.logging.*"%>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.bulletins.*"%>
<%@ page import="java.net.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%!


    RuntimeProperties eiProps = null;
  	
  	public void jspInit()
  	{
    	try
    	{
      		eiProps = ConfigService.getRuntimeProperties();
     
    	}
    	catch(Exception e)
    	{
       		e.printStackTrace();
    	}
  	}
 %>
 <%
    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();
    
    SessionID sessionId = ussession.getSessionID();  
    String sesID = sessionId.toString();
    
    User user = ussession.getUser();
    String cartridges[] = user.getCartridge();
    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
    String appID = ussession.getProperty(UserSession.APPLICATION_KEY);
    SessionID sessionIdObj = ussession.getSessionID();
    	
    boolean showLitPdf = false;
    boolean showPatPdf = false;
    boolean showLitHtml = false;
    boolean showPatHtml = false;

    for (int i = 0; i < cartridges.length; i++) {
	if(cartridges[i].toUpperCase().indexOf("LIT_HTM") > -1)
		showLitHtml = true;
	if(cartridges[i].toUpperCase().indexOf("PAT_HTM") > -1)
		showPatHtml = true;
	if(cartridges[i].toUpperCase().indexOf("LIT_PDF") > -1)
		showLitPdf = true;
	if(cartridges[i].toUpperCase().indexOf("PAT_PDF") > -1)
		showPatPdf = true;
    }

    if(appID == null)
    {
 	appID = "Def";
    }
    String resourcePath   = eiProps.getProperty("resourcePath"+appID);

    String queryString = request.getParameter("queryStr");	
    String selectedDB = request.getParameter("database");	

    int pageSize = 25;
	   
    BulletinQuery query = new BulletinQuery();
    
    int docIndex = Integer.parseInt(request.getParameter("docIndex"));
    
    
    BulletinPage btPage = null;
    BulletinBuilder builder = new BulletinBuilder();
    BulletinResultNavigator navigator = null;
    String db = "";
    String yr = "";
    String category = null;
        
    if(queryString != null)
    {    
	query.setQuery(queryString);
	        
	yr = query.getYr();
	db = query.getDatabase();
	category = query.getCategory();		
	btPage = builder.buildBulletinResults(db,yr,category);
		
    } 
    else 
    {		
	db = request.getParameter("db");	
	yr = request.getParameter("yr");	
	category = request.getParameter("cy");

	query.setYr(yr);
	query.setDatabase(db);
	query.setCategory(category);

	btPage = builder.buildBulletinResults(db,yr,category);
    }
    boolean showPdf = false;
    boolean showHtml = false;
	
    if(db.equals("1")){
	showPdf = showLitPdf;
	showHtml = showLitHtml;
    }
    else 
    {
	showPdf = showPatPdf;
	showHtml = showPatHtml;
    }
		
    	navigator = new BulletinResultNavigator(docIndex,pageSize,btPage.size());
    	btPage = navigator.filter(btPage);
    
    	BulletinXMLVisitor xmlVisitor = new BulletinXMLVisitor(out,cartridges);
    	
    	StringBuffer sbCartridges = new StringBuffer();
    	StringBuffer litCartidges = new StringBuffer();
    	StringBuffer patCartridges = new StringBuffer();
    	BulletinGUI gui = new BulletinGUI();
  	
  	for (int i = 0; i < cartridges.length; i++) {
  	
        if(gui.validCartridge(db,cartridges[i])){
        	sbCartridges.append(cartridges[i].toUpperCase());
        	 if(i != cartridges.length - 1)
        	sbCartridges.append(";");
        }
        if(gui.isLITCartridge(cartridges[i])){
        	litCartidges.append(cartridges[i].toUpperCase());
        	if(i != cartridges.length - 1)
        	litCartidges.append(";");
        }
        
        if(gui.isPATCartridge(cartridges[i])){
        	patCartridges.append(cartridges[i].toUpperCase());
        	if(i != cartridges.length - 1)
        	patCartridges.append(";");
        }
         
       
        	
    }
    
	client.log("request", "bulletinSearch");
	client.log("dbname", db);
	client.log("category",category);
	client.log("year",yr);
	client.log("custid", user.getCustomerID());	
	
	client.setRemoteControl();

	out.write("<PAGE>");
	out.write("<HEADER/>");
	out.write("<FOOTER/>");
	out.write("<DB>"+db+"</DB>");
	out.write("<RESOURCE-PATH>"+resourcePath+"</RESOURCE-PATH>");
	out.write(strGlobalLinksXML);
	out.write("<CARTRIDGES><![CDATA["+sbCartridges+"]]></CARTRIDGES>");
	out.write("<PATCR><![CDATA["+patCartridges+"]]></PATCR>");
	out.write("<SELECTED-DB><![CDATA["+selectedDB+"]]></SELECTED-DB>");
	out.write("<LITCR><![CDATA["+litCartidges+"]]></LITCR>");
	out.write("<SHOW-HTML><![CDATA["+showHtml+"]]></SHOW-HTML>");
	out.write("<SHOW-PDF><![CDATA["+showPdf+"]]></SHOW-PDF>");
	out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
	navigator.toXML(out);
	out.write("<QTOP>");
	out.write("<QSTR><![CDATA[");
	out.write(query.toString());
	out.write("]]></QSTR>");
	out.write("<QCO>");
	out.write(Integer.toString(navigator.getHitCount()));
	out.write("</QCO>");
	out.write("<QDIS><![CDATA[");
	out.write(query.getDisplayQuery());
	out.write("]]></QDIS>");
	out.write("</QTOP>");
	   
	    
	if(btPage != null){
		out.write("<BULLETINS>");
		btPage.accept(xmlVisitor);
		out.write("</BULLETINS>");
	}

	out.write("</PAGE>");

%>