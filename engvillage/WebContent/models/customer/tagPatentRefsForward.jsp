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
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
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
	int offset = 0;

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

	if(request.getParameter("offset")!=null)
	{
		offset =Integer.parseInt(request.getParameter("offset"));
	}

	if(request.getParameter("CID")!=null)
	{
		cid=request.getParameter("CID");
	}

	if(request.getParameter("database")!=null)
	{
		database=request.getParameter("database");
	}

	if(currentRecord == null )
	{
		currentRecord = "1";
	}

	String docID = request.getParameter("docid");
	int index=Integer.parseInt(currentRecord);

	StringBuffer srurl = getsrURL(searchID,
					              scopeParam,
					              database);

	request.setAttribute("SEARCH_RESULTS_URL", srurl);


 	StringBuffer nsurl = getnsURL(database);
	request.setAttribute("NEW_SEARCH_URL", nsurl);


	StringBuffer absurl = getabsURL(index,
					                searchID,
					                scopeParam,
					                database);
	request.setAttribute("ABS_URL", absurl);


	StringBuffer deturl = getdetURL(index,
					                searchID,
					                scopeParam,
					                database);
	request.setAttribute("DET_URL", deturl);


	StringBuffer nonpatrefurl = getnonpatrefURL(index,
						                        searchID,
						                        scopeParam,
						                        database,
						                        docID);
	request.setAttribute("NONPATREF_URL", nonpatrefurl);


    StringBuffer backurl = getbackURL(index,
							          searchID,
							          scopeParam,
							          database,
							          docID);

	StringBuffer nextrefurl =  new StringBuffer(backurl.toString());
	nextrefurl.append("&offset=");
	nextrefurl.append(Integer.toString(offset+25));
	request.setAttribute("NEXTREF_URL", nextrefurl);

	StringBuffer prevrefurl =  new StringBuffer(backurl.toString());
	prevrefurl.append("&offset=");
	prevrefurl.append(Integer.toString(offset-25));
	request.setAttribute("PREVREF_URL", prevrefurl);


	pageContext.forward("patentReferences.jsp");
%><%!

	private StringBuffer getsrURL(String tagSearch,
								  String scopeParam,
								  String database)
	{
		StringBuffer srurlbuf = new StringBuffer();
		srurlbuf.append("CID=tagSearch").append("&");
		srurlbuf.append("searchtags=").append(URLEncoder.encode(tagSearch)).append("&");
		srurlbuf.append("database=").append(database).append("&");
		srurlbuf.append("scope=").append(scopeParam);
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
								   String scopeParam,
								   String database)
	{
		StringBuffer absurlbuf = new StringBuffer();
		absurlbuf.append("CID=").append("tagSearchAbstractFormat").append("&amp;");
		absurlbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		absurlbuf.append("DOCINDEX=").append(Integer.toString((index))).append("&amp;");
		absurlbuf.append("database=").append(database).append("&amp;");
		absurlbuf.append("tagscope=").append(scopeParam).append("&amp;");
		absurlbuf.append("format=").append("tagSearchAbstractFormat");

		return absurlbuf;
	}



	private StringBuffer getnonpatrefURL(int index,
							             String tagSearch,
							             String scopeParam,
							             String database,
							             String docID)
	{
		StringBuffer nonpatrefbuf = new StringBuffer();
		nonpatrefbuf.append("CID=").append("tagSearchNonPatentReferencesFormat").append("&amp;");
		nonpatrefbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		nonpatrefbuf.append("DOCINDEX=").append(Integer.toString((index))).append("&amp;");
		nonpatrefbuf.append("database=").append(database).append("&amp;");
		nonpatrefbuf.append("tagscope=").append(scopeParam).append("&amp;");
		nonpatrefbuf.append("docid=").append(docID).append("&amp;");
		nonpatrefbuf.append("format=").append("tagSearchAbstractFormat");
		return nonpatrefbuf;
	}


	private StringBuffer getdetURL(int index,
								   String tagSearch,
								   String scopeParam,
								   String database)
	{
		StringBuffer deturlbuf = new StringBuffer();
		deturlbuf.append("CID=").append("tagSearchDetailedFormat").append("&amp;");
		deturlbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		deturlbuf.append("DOCINDEX=").append(Integer.toString((index))).append("&amp;");
		deturlbuf.append("database=").append(database).append("&amp;");
		deturlbuf.append("tagscope=").append(scopeParam).append("&amp;");
		deturlbuf.append("format=").append("tagSearchDetailedFormat");

		return deturlbuf;
	}


	private StringBuffer getbackURL(int index,
								    String tagSearch,
								    String scopeParam,
								    String database,
								    String docID)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("CID=").append("tagSearchReferencesFormat").append("&");
		buf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&");
		buf.append("DOCINDEX=").append(Integer.toString((index))).append("&");
		buf.append("database=").append(database).append("&");
		buf.append("tagscope=").append(scopeParam).append("&");
		buf.append("docid=").append(docID).append("&");
		buf.append("format=").append("ReferencesFormat");
		return buf;
	}

%>