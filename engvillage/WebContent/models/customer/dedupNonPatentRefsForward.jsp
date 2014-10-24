<%@ page language="java" %><%@ page session="false" %><%@ page import="org.ei.domain.*" %><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.config.*"%><%@ page import="java.util.*"%><%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page import="org.engvillage.biz.controller.ControllerClient"%><%@ page import="org.ei.parser.base.*"%><%@ page import="org.ei.email.*"%><%@ page import="javax.mail.internet.*"%><%


	String currentRecord=request.getParameter("DOCINDEX");
	if(currentRecord == null )
	{
		currentRecord = "1";
	}
	int recnum = Integer.parseInt(currentRecord);


	String dedupFlag = "false";
	if(request.getParameter("DupFlag") != null)
	{
	  dedupFlag = request.getParameter("DupFlag");
	}
    String searchID=request.getParameter("SEARCHID");
	String dedupOption = request.getParameter("fieldpref");
	String dedupDB = request.getParameter("dbpref");
	String origin = request.getParameter("origin");
	String linkSet = request.getParameter("linkSet");
	String dbLink = request.getParameter("dbLink");
	String dedupSet = request.getParameter("dedupSet");
	String database = request.getParameter("database");

	Query tQuery = Searches.getSearch(searchID);
	if(tQuery == null)
	{
	  tQuery = SavedSearches.getSearch(searchID);
	}


	StringBuffer srurl = getsrURL(recnum,searchID,database,tQuery);
	request.setAttribute("SEARCH_RESULTS_URL", srurl);

	StringBuffer dedupurl = getdedupURL(recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database);
	request.setAttribute("DEDUP_RESULTS_URL", dedupurl);


	StringBuffer nsurl = getnsURL(database,tQuery);
	request.setAttribute("NEW_SEARCH_URL", nsurl);


	StringBuffer absurl = getabsURL(tQuery,recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database);
	request.setAttribute("ABS_URL", absurl);

	StringBuffer deturl = getdetURL(tQuery,recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database);
	request.setAttribute("DET_URL", deturl);

	StringBuffer patrefurl = getpatrefURL(recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database, request.getParameter("docid"));
	request.setAttribute("PATREF_URL", patrefurl);

	request.setAttribute("NONPATREF_URL", new StringBuffer("test"));
	request.setAttribute("SEARCH_CONTEXT", "dedup");
	pageContext.forward("nonPatentRecords.jsp");
%><%!


	StringBuffer getsrURL(int recnum,String searchID,String database,Query tQuery)
	{
		StringBuffer srurlbuf = new StringBuffer();
		srurlbuf.append("CID=").append(XSLCIDHelper.searchResultsCid(tQuery.getSearchType())).append("&");
		srurlbuf.append("SEARCHID=").append(searchID).append("&");
		srurlbuf.append("COUNT=").append(Integer.toString((recnum))).append("&");
		srurlbuf.append("database=").append(database);

		return srurlbuf;
	}

	StringBuffer getdedupURL(int recnum,String searchID,String dedupFlag,String dedupOption,String dedupDB,String origin,String linkSet,String dbLink,String dedupSet,String database)
	{

		int pageIndex = -1;

		if((recnum%25) == 0)
		{
			pageIndex  = (recnum/25);
		}
		else
		{
			pageIndex  = (recnum/25) + 1;
		}

		recnum = ((pageIndex - 1) * 25) + 1;

		StringBuffer dedupurlbuf = new StringBuffer();
		dedupurlbuf.append("CID=").append("dedup").append("&amp;");
		dedupurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		dedupurlbuf.append("COUNT=").append(Integer.toString((recnum))).append("&amp;");
		dedupurlbuf.append("DupFlag=").append(dedupFlag).append("&amp;");
		dedupurlbuf.append("dbpref=").append(dedupDB).append("&amp;");
		dedupurlbuf.append("fieldpref=").append(dedupOption).append("&amp;");
		dedupurlbuf.append("origin=").append(origin).append("&amp;");
		dedupurlbuf.append("dbLink=").append(dbLink).append("&amp;");
		dedupurlbuf.append("linkSet=").append(linkSet).append("&amp;");
		dedupurlbuf.append("database=").append(database).append("&amp;");

		return dedupurlbuf;
	}

	StringBuffer getnsURL(String database,Query tQuery)
	{
		StringBuffer nsurlbuf = new StringBuffer();
		nsurlbuf.append("CID=").append(XSLCIDHelper.newSearchCid(tQuery.getSearchType())).append("&amp;");
		nsurlbuf.append("database=").append(database);

		return nsurlbuf;
	}

	StringBuffer getabsURL(Query tQuery,int recnum,String searchID,String dedupFlag,String dedupOption,String dedupDB,String origin,String linkSet,String dbLink,String dedupSet,String database)
	{
		StringBuffer absurlbuf = new StringBuffer();
		absurlbuf.append("CID=").append("dedupSearchAbstractFormat").append("&amp;");
		absurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		absurlbuf.append("DupFlag=").append(dedupFlag).append("&amp;");
		absurlbuf.append("dbpref=").append(dedupDB).append("&amp;");
		absurlbuf.append("fieldpref=").append(dedupOption).append("&amp;");
		absurlbuf.append("origin=").append(origin).append("&amp;");
		absurlbuf.append("dbLink=").append(dbLink).append("&amp;");
		absurlbuf.append("linkSet=").append(linkSet).append("&amp;");
		absurlbuf.append("dedupSet=").append(dedupSet).append("&amp;");
		absurlbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		absurlbuf.append("database=").append(database).append("&amp;");
		absurlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"AbstractFormat");

		return absurlbuf;
	}

	StringBuffer getdetURL(Query tQuery,int recnum,String searchID,String dedupFlag,String dedupOption,String dedupDB,String origin,String linkSet,String dbLink,String dedupSet,String database)
	{
		StringBuffer deturlbuf = new StringBuffer();
		deturlbuf.append("CID=").append("dedupSearchDetailedFormat").append("&amp;");
		deturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		deturlbuf.append("DupFlag=").append(dedupFlag).append("&amp;");
		deturlbuf.append("dbpref=").append(dedupDB).append("&amp;");
		deturlbuf.append("fieldpref=").append(dedupOption).append("&amp;");
		deturlbuf.append("origin=").append(origin).append("&amp;");
		deturlbuf.append("dbLink=").append(dbLink).append("&amp;");
		deturlbuf.append("linkSet=").append(linkSet).append("&amp;");
		deturlbuf.append("dedupSet=").append(dedupSet).append("&amp;");
		deturlbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		deturlbuf.append("database=").append(database).append("&amp;");
		deturlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"DetailedFormat");

		return deturlbuf;
	}

	StringBuffer getpatrefURL(int recnum,
							  String searchID,
							  String dedupFlag,
							  String dedupOption,
							  String dedupDB,
							  String origin,
							  String linkSet,
							  String dbLink,
							  String dedupSet,
							  String database,
							  String docID)
	{
		StringBuffer refurlbuf = new StringBuffer();
		refurlbuf.append("CID=").append("dedupSearchReferencesFormat").append("&amp;");
		refurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		refurlbuf.append("DupFlag=").append(dedupFlag).append("&amp;");
		refurlbuf.append("dbpref=").append(dedupDB).append("&amp;");
		refurlbuf.append("fieldpref=").append(dedupOption).append("&amp;");
		refurlbuf.append("origin=").append(origin).append("&amp;");
		refurlbuf.append("dbLink=").append(dbLink).append("&amp;");
		refurlbuf.append("linkSet=").append(linkSet).append("&amp;");
		refurlbuf.append("dedupSet=").append(dedupSet).append("&amp;");
		refurlbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		refurlbuf.append("database=").append(database).append("&amp;");
		refurlbuf.append("docid=").append(docID).append("&amp;");
		refurlbuf.append("format=referencesFormat");
		return refurlbuf;
	}



%>