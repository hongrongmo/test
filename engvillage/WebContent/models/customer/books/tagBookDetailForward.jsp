<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.tags.*"%><%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page import="org.engvillage.biz.controller.ControllerClient"%><%@ page import="org.ei.email.*"%><%@ page import="org.ei.domain.*"%><%@ page errorPage="/error/errorPage.jsp"%><%@ page import="javax.mail.internet.*"%><%

	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	String sessionId=ussession.getSessionid();
	SessionID sessionIdObj = ussession.getSessionID();
	IEVWebUser user = ussession.getUser();
	String[] credentials = ussession.getCartridge();
	String customerId=ussession.getCustomerid();
	String pUserId = ussession.getUserid();
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

	int index = Integer.parseInt(request.getParameter("DOCINDEX"));

	String dataFormat = null;
	String cid = null;
	String format = request.getParameter("format");
	if(format!=null)
	{
		if(format.endsWith("AbstractFormat"))
		{
		  cid = "tagSearchAbstractFormat";
		  dataFormat = Abstract.ABSTRACT_FORMAT;
		}
		else if(format.endsWith("TocFormat"))
		{
		  cid = "tagSearchAbstractFormat";
		  dataFormat = Abstract.ABSTRACT_FORMAT;
		}
		else if(format.endsWith("DetailedFormat"))
		{
		  cid = "tagSearchDetailedFormat";
		  dataFormat = FullDoc.FULLDOC_FORMAT;
		}
	}
	else
	{
		format = Abstract.ABSTRACT_FORMAT;
		dataFormat = Abstract.ABSTRACT_FORMAT;
	}

	String tagSearch = request.getParameter("SEARCHID");
	String database = request.getParameter("database");

	TagBroker tagBroker = new TagBroker();
	int nTotalDocs = tagBroker.count(tagSearch,
								 	 iscope,
								 	 pUserId,
								 	 customerId,
								 	 groupID,
								 	 credentials);

	PageEntry entry = tagBroker.getPageEntry(tagSearch,
											 iscope,
											 pUserId,
											 customerId,
											 groupID,
											 index,
											 sessionId,
											 dataFormat,
											 credentials);

	EIDoc curDoc = entry.getDoc();
	DocID did = curDoc.getDocID();


	request.setAttribute("CONTROLLER_CLIENT", client);
	request.setAttribute("PAGE_ENTRY", entry);
	request.setAttribute("NUMBER", new Integer(3));

	StringBuffer prevurl = new StringBuffer();
	if(index > 1)
	{
		prevurl = getprevURL(cid,index,tagSearch,scopeParam,database,format);
	}
	request.setAttribute("PREV_URL", prevurl);


	StringBuffer nexturl = new StringBuffer();
	if(nTotalDocs > index)
	{
		nexturl = getnextURL(cid,index,tagSearch,scopeParam,database,format);
	}

	request.setAttribute("NEXT_URL", nexturl);

	StringBuffer backurl = getbackURL(cid,index,tagSearch,scopeParam,database,format);
	String backurlString = backurl.toString();

	StringBuffer srurl = getsrURL(tagSearch,scopeParam,database);
	request.setAttribute("SEARCH_RESULTS_URL", srurl);

	StringBuffer nsurl = getnsURL(database);
	request.setAttribute("NEW_SEARCH_URL", nsurl);

	StringBuffer absurl = getabsURL(index,tagSearch,scopeParam,database);
	request.setAttribute("ABS_URL", absurl);

	StringBuffer deturl = getdetURL(index,tagSearch,scopeParam,database);
	request.setAttribute("DET_URL", deturl);

	StringBuffer patrefurl = getpatrefURL(index,tagSearch,scopeParam,database, did);
	request.setAttribute("PATREF_URL", patrefurl);

	StringBuffer nonpatrefurl = getnonpatrefURL(index,tagSearch,scopeParam,database, did);
	request.setAttribute("NONPATREF_URL", nonpatrefurl);

	StringBuffer addurl = getaddURL(backurlString,database);
	request.setAttribute("ADD_TAG_URL", addurl);

	StringBuffer editurl = geteditURL(backurlString);
	request.setAttribute("EDIT_TAG_URL", editurl);
	request.setAttribute("SEARCH_CONTEXT", "tag");
	request.setAttribute("ALT_DISPLAY_QUERY", tagSearch);
	pageContext.forward("bookDetail.jsp");

%><%!

	StringBuffer getprevURL(String cid,
							int index,
							String tagSearch,
							String scopeParam,
							String database,
							String format)
	{
		StringBuffer prevurlbuf = new StringBuffer();
		prevurlbuf.append("CID=").append(cid).append("&amp;");
		prevurlbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		prevurlbuf.append("DOCINDEX=").append(Integer.toString(index-1)).append("&amp;");
		prevurlbuf.append("database=").append(database).append("&amp;");
		prevurlbuf.append("tagscope=").append(scopeParam).append("&amp;");
		prevurlbuf.append("format=").append(format);

		return prevurlbuf;
	}

	StringBuffer getbackURL(String cid,
							int index,
							String tagSearch,
							String scopeParam,
							String database,
							String format)
	{
		StringBuffer prevurlbuf = new StringBuffer();
		prevurlbuf.append("CID=").append(cid).append("&");
		prevurlbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&");
		prevurlbuf.append("DOCINDEX=").append(Integer.toString(index)).append("&");
		prevurlbuf.append("database=").append(database).append("&");
		prevurlbuf.append("tagscope=").append(scopeParam).append("&");
		prevurlbuf.append("format=").append(format);

		return prevurlbuf;
	}


	StringBuffer getnextURL(String cid,
							int index,
							String tagSearch,
							String scopeParam,
							String database,
							String format)
	{
		StringBuffer nexturlbuf = new StringBuffer();
		nexturlbuf.append("CID=").append(cid).append("&amp;");
		nexturlbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		nexturlbuf.append("DOCINDEX=").append(Integer.toString(index+1)).append("&amp;");
		nexturlbuf.append("database=").append(database).append("&amp;");
		nexturlbuf.append("tagscope=").append(scopeParam).append("&amp;");
		nexturlbuf.append("format=").append(format);

		return nexturlbuf;
	}

	StringBuffer getsrURL(String tagSearch,
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


	StringBuffer getnsURL(String database)
	{
		StringBuffer nsurlbuf = new StringBuffer();
		nsurlbuf.append("CID=").append(XSLCIDHelper.newSearchCid("tagSearch")).append("&amp;");
		nsurlbuf.append("database=").append(database);

		return nsurlbuf;
	}

	StringBuffer getabsURL(int index,
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

	StringBuffer getpatrefURL(int index,
						   	  String tagSearch,
						   	  String scopeParam,
						      String database,
						      DocID docID)
	{
		StringBuffer patrefbuf = new StringBuffer();
		patrefbuf.append("CID=").append("tagSearchReferencesFormat").append("&amp;");
		patrefbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		patrefbuf.append("DOCINDEX=").append(Integer.toString((index))).append("&amp;");
		patrefbuf.append("database=").append(database).append("&amp;");
		patrefbuf.append("tagscope=").append(scopeParam).append("&amp;");
		patrefbuf.append("docid=").append(docID.getDocID()).append("&amp;");
		patrefbuf.append("format=").append("tagSearchAbstractFormat");

		return patrefbuf;
	}

	StringBuffer getnonpatrefURL(int index,
							  String tagSearch,
							  String scopeParam,
							  String database,
							  DocID docID)
	{
		StringBuffer nonpatrefbuf = new StringBuffer();
		nonpatrefbuf.append("CID=").append("tagSearchNonPatentReferencesFormat").append("&amp;");
		nonpatrefbuf.append("SEARCHID=").append(URLEncoder.encode(tagSearch)).append("&amp;");
		nonpatrefbuf.append("DOCINDEX=").append(Integer.toString((index))).append("&amp;");
		nonpatrefbuf.append("database=").append(database).append("&amp;");
		nonpatrefbuf.append("tagscope=").append(scopeParam).append("&amp;");
		nonpatrefbuf.append("docid=").append(docID.getDocID()).append("&amp;");
		nonpatrefbuf.append("format=").append("tagSearchAbstractFormat");
		return nonpatrefbuf;
	}


	StringBuffer getdetURL(int index,
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

	private StringBuffer getaddURL(String backURL, String database)
	{
		StringBuffer addurlbuf = new StringBuffer();
		addurlbuf.append("CID=").append("addTagForward").append("&amp;");
		addurlbuf.append("database=").append(database).append("&amp;");
		addurlbuf.append("RETURNURL=").append(URLEncoder.encode(backURL));
		return addurlbuf;
	}

	private StringBuffer geteditURL(String backURL)
	{
		StringBuffer addurlbuf = new StringBuffer();
		addurlbuf.append("CID=").append("editForward").append("&amp;");
		addurlbuf.append("RETURNURL=").append(URLEncoder.encode(backURL));
		return addurlbuf;
	}


%>