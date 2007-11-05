<%@ page language="java" %><%@ page session="false" %><%@ page import="org.ei.data.encompasslit.runtime.EltDocBuilder"%><%@ page import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.domain.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.TagBroker"%><%@ page import="org.ei.tags.Tag"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page  errorPage="/error/errorPage.jsp"%><%
	ControllerClient client = null;
	String docId = null;
	String terms = "SAMPLE;SAMPLE|SAMPLE;SAMPLE";

	if(request.getParameter("docid") != null)
	{
		docId = request.getParameter("docid");
		//docId = "ept_7ced0110061a9bb5fM7f0119255120119";
	}
	else
	{
		//docId = "ept_7ced0110061a9bb5fM7f0119255120119";
		docId ="elt_fabe9fe337d1a1eM445719817173223";
	}

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	User user=ussession.getUser();

	String customerId=user.getCustomerID().trim();
	String pUserId = ussession.getProperty("P_USER_ID");
	
	//EltDocBuilder docBuilder = new EltDocBuilder(database);
	MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
	List docIds = new ArrayList();
	DatabaseConfig dConfig = DatabaseConfig.getInstance();
    Database databse = dConfig.getDatabase(docId.substring(0, 3));
    DocID did = new DocID(docId, databse);
	docIds.add(did);
	List listOfDocIDs = builder.buildPage(docIds,LinkedTermDetail.LINKEDTERM_FORMAT);
	EIDoc eiDoc = (EIDoc) listOfDocIDs.get(0);
	terms =(String) eiDoc.getLongTerms();

	System.out.println("result length"+terms.length());
	
	if (terms.length() > 500000)
	{
		terms = terms.substring(0,500000);
	}
	
	if (terms == null || terms.trim().equals(""))
	{
		terms = "SAMPLE;SAMPLE|SAMPLE;SAMPLE";
	}
		
	out.write("<PAGE>");
	out.write("<HEADER/>");
	out.write("<FOOTER/>");
	out.write("<LTERMS><![CDATA["+terms+"]]></LTERMS>");
	out.write("</PAGE>");
	out.write("<!--END-->");
	out.close();
%>