<%@ page language="java" %><%@ page session="false" %><%@ page import="org.ei.data.encompasslit.runtime.EltDocBuilder"%><%@ page import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.domain.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.TagBroker"%><%@ page import="org.ei.tags.Tag"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page  errorPage="/error/errorPage.jsp"%><%@ page import="org.ei.parser.base.*"%>
<%!
	ControllerClient client = null;
	String docId = null;
	String terms = null;
	Query queryObject = null;
	RuntimeProperties eiProps = null;
    DatabaseConfig databaseConfig = null;
    int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);
    
  	
  	public void jspInit()
  	{
    	try
    	{
      		eiProps = ConfigService.getRuntimeProperties();
      		databaseConfig = DatabaseConfig.getInstance();     		
      		customizedEndYear = Integer.parseInt(eiProps.getProperty("SYSTEM_ENDYEAR"));    
    	}
    	catch(Exception e)
    	{
       		e.printStackTrace();
    	}
  	}
%>
<% 	
  	ControllerClient client = new ControllerClient(request, response);

	docId = request.getParameter("docid");
	boolean isTag  = false;
	
	BooleanQuery bQuery = null;
   	HitHighlighter highlighter = null;

	//long terms docId = "ept_7ced0110061a9bb5fM7f0119255120119";
	//docId ="elt_fabe9fe337d1a1eM445719817173223";
	
	String searchId = request.getParameter("searchId");
	String searchType = request.getParameter("searchType");
	String database = request.getParameter("database");
	String stag = request.getParameter("istag");
	if(stag != null && stag.equals("tag"))
	{
		isTag = true;
	}
		
	UserSession ussession=(UserSession)client.getUserSession();
    User user = ussession.getUser();
    String[] credentials = user.getCartridge();
    String sessionId = ussession.getID();
    
    if (!isTag && searchId != null && !searchId.equals(""))
    {
		queryObject = Searches.getSearch(searchId);
    	queryObject.setSearchQueryWriter(new FastQueryWriter());
    	queryObject.setDatabaseConfig(databaseConfig);
    	queryObject.setCredentials(credentials);
    	bQuery = queryObject.getParseTree();     
   		highlighter = new HitHighlighter(bQuery);
   	}
	MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();  	
	List docIds = new ArrayList();
	DatabaseConfig dConfig = DatabaseConfig.getInstance();
    Database databse = dConfig.getDatabase(docId.substring(0, 3));
    DocID did = new DocID(docId, databse);
	docIds.add(did); 
	List listOfDocIDs = builder.buildPage(docIds,LinkedTermDetail.LINKEDTERM_FORMAT);

	if(listOfDocIDs != null && listOfDocIDs.size() > 0)
	{
		EIDoc eiDoc = (EIDoc) listOfDocIDs.get(0);
		if(!isTag && searchId != null && !searchId.equals(""))
		{
			eiDoc = (EIDoc)highlighter.highlight(eiDoc);
		}
		
		terms = (String) eiDoc.getLongTerms();
		
		if(!isTag && searchId != null && !searchId.equals(""))
		{
			terms = HitHighlightFinisher.addMarkup(terms);
		}
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