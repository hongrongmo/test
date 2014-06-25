<%@ page language="java" %>
<%@ page session="false" %>
<!--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.database
 * @param java.lang.String.totalDocCount
 * @param java.lang.String.sessionId
 * @param java.lang.String.searchID
 -->

<!-- import statements of Java packages-->

<%@ page import="java.util.*"%>
<%@ page import="java.io.FileWriter"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.util.*"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.data.upt.runtime.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%><%

	String currentRecord=request.getParameter("DOCINDEX");
	String searchID=request.getParameter("SEARCHID");
	String format = null;
	String cid = null;
	String database = null;
	String scopeParam = null;
	int iscope = 1;
	String groupID = null;

	if(request.getParameter("tagscope") != null)
	{
		scopeParam = request.getParameter("tagscope");
		if(scopeParam.indexOf(":") > -1)
		{
			String[] scopeParts = scopeParam.split(":");
			iscope = Integer.parseInt(scopeParts[0]);
			groupID = scopeParts[1];
		}
		else
		{
			iscope = Integer.parseInt(scopeParam);
		}
	}
	else
	{
		scopeParam = Integer.toString(iscope);
	}

	if(request.getParameter("format")!=null)
	{
		format=request.getParameter("format");
		cid = format.trim();
	}

	if(request.getParameter("CID")!=null)
	{
		cid=request.getParameter("CID");
	}

	String docID = request.getParameter("docid");

	if(request.getParameter("database")!=null)
	{
		database=request.getParameter("database");
	}

	if(currentRecord == null )
	{
		currentRecord = "1";
	}

	int index=Integer.parseInt(currentRecord);

	StringBuffer srurl = new StringBuffer();
	srurl = getsrURL(searchID,
					 iscope,
					 database);

	request.setAttribute("SEARCH_RESULTS_URL", srurl);


	StringBuffer nsurl = new StringBuffer();
	nsurl = getnsURL(database);
	request.setAttribute("NEW_SEARCH_URL", nsurl);


	StringBuffer absurl = new StringBuffer();
	absurl = getabsURL(index,
					   searchID,
					   iscope,
					   database);
	request.setAttribute("ABS_URL", absurl);


	StringBuffer deturl = new StringBuffer();
	deturl = getdetURL(index,
					   searchID,
					   iscope,
					   database);
	request.setAttribute("DET_URL", deturl);


	StringBuffer patrefurl = new StringBuffer();
	patrefurl = getpatrefURL(index,
					   	  searchID,
					      scopeParam,
					      database,
					      docID);
	request.setAttribute("PATREF_URL", patrefurl);


	pageContext.forward("nonPatentRecords.jsp");
%><%!

	private StringBuffer getsrURL(String tagSearch,
								  int iscope,
								  String database)
	{
		StringBuffer srurlbuf = new StringBuffer();
		srurlbuf.append("CID=tagSearch").append("&");
		srurlbuf.append("searchtags=").append(URLEncoder.encode(tagSearch)).append("&");
		srurlbuf.append("database=").append(database).append("&");
		srurlbuf.append("scope=").append(Integer.toString(iscope));
		return srurlbuf;
	}

	private StringBuffer getnsURL(String database)
	{
		StringBuffer nsurlbuf = new StringBuffer();
		nsurlbuf.append("CID=tagsLoginForm").append("&amp;");
		nsurlbuf.append("database=").append(database);
		return nsurlbuf;
	}

	private StringBuffer getabsURL(int index,
								   String tagSearch,
								   int iscope,
								   String database)
	{
		StringBuffer absurlbuf = new StringBuffer();
		absurlbuf.append("CID=").append("tagSearchAbstractFormat").append("&amp;");
		absurlbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		absurlbuf.append("DOCINDEX=").append(Integer.toString((index))).append("&amp;");
		absurlbuf.append("database=").append(database).append("&amp;");
		absurlbuf.append("tagscope=").append(Integer.toString(iscope)).append("&amp;");
		absurlbuf.append("format=").append("tagSearchAbstractFormat");

		return absurlbuf;
	}

	StringBuffer getpatrefURL(int index,
							  String tagSearch,
							  String scopeParam,
							  String database,
							  String docID)
	{
		StringBuffer patrefbuf = new StringBuffer();
		patrefbuf.append("CID=").append("tagSearchReferencesFormat").append("&amp;");
		patrefbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		patrefbuf.append("DOCINDEX=").append(Integer.toString((index))).append("&amp;");
		patrefbuf.append("database=").append(database).append("&amp;");
		patrefbuf.append("tagscope=").append(scopeParam).append("&amp;");
		patrefbuf.append("docid=").append(docID).append("&amp;");
		patrefbuf.append("format=").append("tagSearchAbstractFormat");
		return patrefbuf;
	}


	private StringBuffer getdetURL(int index,
								   String tagSearch,
								   int iscope,
								   String database)
	{
		StringBuffer deturlbuf = new StringBuffer();
		deturlbuf.append("CID=").append("tagSearchDetailedFormat").append("&amp;");
		deturlbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		deturlbuf.append("DOCINDEX=").append(Integer.toString((index))).append("&amp;");
		deturlbuf.append("database=").append(database).append("&amp;");
		deturlbuf.append("tagscope=").append(Integer.toString(iscope)).append("&amp;");
		deturlbuf.append("format=").append("tagSearchDetailedFormat");

		return deturlbuf;
	}
%>