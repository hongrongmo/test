<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.ei.xmlio.*"%>
<%@ page buffer="20kb"%>
<%
	String xmlDoc=null;
	String totalDocCount=null;
	SearchResult result=null;
	org.ei.domain.Query queryObject = null;
	ControllerClient client = new ControllerClient(request,
						       response);
	String sessionId = null;
	SessionID sessionIdObj = null;
	String searchID=null;
	String format = StringUtil.EMPTY_STRING;
	String term1 = null;
	String dbName=null;
	String sortBy="";
	String sortDir = "";
	String autoStem="";
	StringBuffer query = null;
	String category = null;
	String update = null;
	SearchControl sc = null;
	DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

try
{

	String rssID = request.getParameter("RSSQueryID");
	String queryID = null;	
	queryID=request.getParameter("queryID");
	String[] rssInfo = RSS.getQuery(queryID);
	dbName = rssInfo[0];
	term1 = rssInfo[1];
	sortBy  = "relevance";
	sortDir = "dw";
	update = "1";
	sc = new FastSearchControl();
    	UserSession ussession=(UserSession)client.getUserSession();
    	sessionId=ussession.getID();
    	sessionIdObj = ussession.getSessionID();
    	User user=ussession.getUser();
    	int intDbMask = Integer.parseInt(dbName);
    	String[] credentials = user.getCartridge();
    	if(credentials == null)
    	{
    		credentials = new String[3];
    		credentials[0]="CPX";
    		credentials[1]="INS";
    		credentials[2]="NTI";
    	}

    	queryObject = new org.ei.domain.Query(databaseConfig, credentials);
    	queryObject.setDataBase(intDbMask);
    	searchID=new GUID().toString();
    	queryObject.setID(searchID);
	queryObject.setSearchType(org.ei.domain.Query.TYPE_EXPERT);
	queryObject.setSortOption(new Sort(sortBy,sortDir));
        queryObject.setSearchPhrase(term1,"","","","","","","");
	queryObject.setAutoStemming("on");
	update = "1";
	queryObject.setLastFourUpdates(update);
        queryObject.setSearchQueryWriter(new FastQueryWriter());
        queryObject.compile();
        result = sc.openSearch(queryObject,
                               sessionId,
                               25,
                               true);
        List docIds = sc.getDocIDRange(1,50);
        totalDocCount=Integer.toString(result.getHitCount());
	queryObject.setRecordCount(totalDocCount);
        queryObject.setSessionID(sessionId);

	client.log("search_id", queryObject.getID());
	client.log("query_string", queryObject.getPhysicalQuery());
	client.log("sort_by", queryObject.getSortOption().getSortField());
	client.log("suppress_stem", queryObject.getAutoStemming());
	client.log("context", "search");
	client.log("action", "search");
	client.log("type", "basic");
	client.log("rssp","y");
	client.log("db_name", dbName);
	client.log("page_size", "25");
	client.log("format", format);
	client.log("doc_id", " ");
	client.log("num_recs", totalDocCount);
	client.log("doc_index", "1");
	client.log("hits", totalDocCount);
	client.setRemoteControl();
	if(category==null)
	{
		category=queryObject.getDisplayQuery();
        }

	if(docIds.size()>0)
	{
		Pagemaker pagemaker = new Pagemaker(sessionId,
						    50,
						    docIds,
						    Abstract.ABSTRACT_FORMAT);
		String serverName= ussession.getProperty("ENV_BASEADDRESS");
		out.write("<RSS>");
		out.write("<SEARCH-ID>"+queryID+"</SEARCH-ID>");
		out.write("<SERVER>"+serverName+"</SERVER>");
		out.write("<DBMASK>");
		out.write(dbName);
		out.write("</DBMASK>");
		out.write("<CATEGORY-TITLE>"+category+"</CATEGORY-TITLE>");
		List builtDocuments = pagemaker.nextPage();
		for(int i=0;i<builtDocuments.size();i++)
		{
		
			EIDoc eidoc = (EIDoc)builtDocuments.get(i);
			eidoc.toXML(out);
		}
		out.write("</RSS>");
	}
	else
	{
		out.write("<RSS>");
		out.write("<SEARCH-ID>"+queryID+"</SEARCH-ID>");
		out.write("<DBMASK>");
		out.write(dbName);
		out.write("</DBMASK>");
		out.write("<CATEGORY-TITLE>"+category+"</CATEGORY-TITLE>");
		out.write("</RSS>");
	}
}
catch(Exception e)
{
    out.write("<PAGE><EXCEPTION><![CDATA["+e.getMessage()+"]]></EXCEPTION></PAGE>");
    e.printStackTrace();
}
finally
{
    if(sc != null)
    {
        sc.closeSearch();
    }
}
%>
