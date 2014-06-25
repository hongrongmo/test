<%@ page language="java" %><%@ page session="false" %><%@ page import="org.ei.domain.*" %><%@ page import="org.ei.books.*" %><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.config.*"%><%@ page import="java.util.*"%><%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.parser.base.*"%><%@ page import="org.ei.email.*"%><%@ page import="javax.mail.internet.*"%><%

  FastSearchControl sc = null;

  String currentRecord=null;
  String searchID=null;
  SearchResult result=null;
  String totalDocCount=null;

%><%!
  DatabaseConfig databaseConfig = null;
  int pagesize = 0;

  public void jspInit()
  {
      try
      {
        // Get the value of the number of documents to be displayed in a search results page form Runtime.properties file
        RuntimeProperties runtimeProps = ConfigService.getRuntimeProperties();
        pagesize = Integer.parseInt(runtimeProps.getProperty("PAGESIZE"));
        databaseConfig = DatabaseConfig.getInstance();
      } catch(Exception e) {
        e.printStackTrace();
      }
  }
%><%
  ControllerClient client = new ControllerClient(request, response);
  UserSession ussession=(UserSession)client.getUserSession();
  String sessionId=ussession.getID();
   IEVWebUser user = ussession.getUser();
  currentRecord=request.getParameter("DOCINDEX");
  searchID=request.getParameter("SEARCHID");

  if(currentRecord == null )
  {
    currentRecord = "1";
  }

  int index=Integer.parseInt(currentRecord);
  int recnum = -1;
  int totalcount = -1;

	Query tQuery = Searches.getSearch(searchID);
	if(tQuery == null)
	{
	  tQuery = SavedSearches.getSearch(searchID);
	}
	tQuery.setSearchQueryWriter(new FastQueryWriter());
	tQuery.setDatabaseConfig(databaseConfig);
	tQuery.setCredentials(user.getCartridge());

	/*
	*   Handle the Hit Highlighting
	*/
	BooleanQuery booleanQuery = tQuery.getParseTree();
	HitHighlighter highlighter = new HitHighlighter(booleanQuery);

	String dedupFlag = "false";
	if(request.getParameter("DupFlag") != null)
	{
	  dedupFlag = request.getParameter("DupFlag");
	}
	String dedupOption = request.getParameter("fieldpref");
	String dedupDB = request.getParameter("dbpref");
	String origin = request.getParameter("origin");
	String linkSet = request.getParameter("linkSet");
	String dbLink = request.getParameter("dbLink");
	String dedupSet = request.getParameter("dedupSet");
	String database = request.getParameter("database");

	int dedupset = 0;
	if(dedupSet != null && dedupSet.length() > 0)
	{
	  dedupset = Integer.parseInt(dedupSet);
	}

	sc = new FastSearchControl();
	sc.setHitHighlighter(highlighter);

	String dataFormat = null;
	String cid = null;
	String format = request.getParameter("format");
	if(format!=null)
	{
		if(format.endsWith("AbstractFormat"))
		{
		  cid = "dedupSearchAbstractFormat";
		  dataFormat = Abstract.ABSTRACT_FORMAT;
		}
		else if(format.endsWith("TocFormat"))
		{
		  cid = "dedupSearchAbstractFormat";
		  dataFormat = Abstract.ABSTRACT_FORMAT;
		}
		else if(format.endsWith("DetailedFormat"))
		{
		  cid = "dedupSearchDetailedFormat";
		  dataFormat = FullDoc.FULLDOC_FORMAT;
		}
	}
	else
	{
		format = Abstract.ABSTRACT_FORMAT;
		dataFormat = Abstract.ABSTRACT_FORMAT;
	}

	DedupBroker dedupBroker = new DedupBroker();
	result = sc.openSearch(tQuery,
			       		   sessionId,
			       		   pagesize,
			               true);

	FastDeduper deduper = sc.dedupSearch(1000,
										 dedupOption,
										 dedupDB);

	DedupData wanted = deduper.getWanted();
	DedupData unwanted = deduper.getUnwanted();
	List wantedList =  null;

	if(origin != null && origin.equalsIgnoreCase("summary"))
	{
		if(linkSet != null && linkSet.equalsIgnoreCase("deduped"))
		{
			if(dbLink == null || dbLink.length() == 0)
			{
				wantedList = wanted.getDocIDs();
			}
			else
			{
				wantedList = wanted.getDocIDs(dbLink);
			}
		}
		else if(linkSet != null && linkSet.equalsIgnoreCase("removed"))
		{
			if(dbLink != null && dbLink.length() > 0)
			{
				wantedList = unwanted.getDocIDs(dbLink);
			}
			else
			{
				wantedList = unwanted.getDocIDs();
			}
		}
	}
	else
	{
		  wantedList = wanted.getDocIDs();
	}

	PageEntry entry = dedupBroker.buildEntry(index,
				       						 wantedList,
				       						 dataFormat,
				       						 sessionId,
				       						 highlighter);

	EIDoc curDoc = entry.getDoc();
	String docID = ((entry.getDoc()).getDocID()).getDocID();


	totalDocCount = tQuery.getRecordCount();
	recnum = Integer.parseInt(currentRecord);
	totalcount = Integer.parseInt(totalDocCount);

	request.setAttribute("CONTROLLER_CLIENT", client);
	request.setAttribute("PAGE_ENTRY", entry);
	request.setAttribute("NUMBER", new Integer(3));
	request.setAttribute("DEDUP-FLAG", new StringBuffer().append(request.getParameter("DupFlag")));
	request.setAttribute("QUERY", tQuery);

	StringBuffer prevurl = new StringBuffer();
	if(recnum > 1)
	{
		prevurl = getprevURL(cid,recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database,format);
	}
	request.setAttribute("PREV_URL", prevurl);

	StringBuffer nexturl = new StringBuffer();
	if(recnum < wantedList.size())
	{
		nexturl = getnextURL(cid,recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database,format);
	}
	request.setAttribute("NEXT_URL", nexturl);

	StringBuffer srurl = getsrURL(recnum,searchID,database,tQuery);
	request.setAttribute("SEARCH_RESULTS_URL", srurl);

	StringBuffer dedupurl = getdedupURL(recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database,format);
	request.setAttribute("DEDUP_RESULTS_URL", dedupurl);


	StringBuffer nsurl = getnsURL(database,tQuery);
	request.setAttribute("NEW_SEARCH_URL", nsurl);

	StringBuffer absurl = getabsURL(tQuery,recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database);
	request.setAttribute("ABS_URL", absurl);

	String pii = null;
	if(curDoc.isBook())
	{
		pii = ((BookDocument) curDoc).getChapterPii();
	}
	StringBuffer bookdeturl = getbookdetURL(recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database,pii,docID);
	request.setAttribute("BOOKDET_URL", bookdeturl);

	StringBuffer deturl = getdetURL(tQuery,recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database);
	request.setAttribute("DET_URL", deturl);

	StringBuffer patrefurl = getpatrefURL(recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database, docID);
	request.setAttribute("PATREF_URL", patrefurl);

	StringBuffer nonpatrefurl = getnonpatrefURL(recnum,searchID,dedupFlag,dedupOption,dedupDB,origin,linkSet,dbLink,dedupSet,database, docID);
	request.setAttribute("NONPATREF_URL", nonpatrefurl);

	request.setAttribute("SEARCH_CONTEXT", "dedup");
	pageContext.forward("abstractDetailedRecord.jsp");


%><%!

	StringBuffer getprevURL(String cid,int recnum,String searchID,String dedupFlag,String dedupOption,String dedupDB,String origin,String linkSet,String dbLink,String dedupSet,String database,String format)
	{
		StringBuffer prevurlbuf = new StringBuffer();
		prevurlbuf.append("CID=").append(cid).append("&amp;");
		prevurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		prevurlbuf.append("DupFlag=").append(dedupFlag).append("&amp;");
		prevurlbuf.append("dbpref=").append(dedupDB).append("&amp;");
		prevurlbuf.append("fieldpref=").append(dedupOption).append("&amp;");
		prevurlbuf.append("origin=").append(origin).append("&amp;");
		prevurlbuf.append("dbLink=").append(dbLink).append("&amp;");
		prevurlbuf.append("linkSet=").append(linkSet).append("&amp;");
		prevurlbuf.append("dedupSet=").append(dedupSet).append("&amp;");
		prevurlbuf.append("DOCINDEX=").append(Integer.toString((recnum-1))).append("&amp;");
		prevurlbuf.append("database=").append(database).append("&amp;");
		prevurlbuf.append("format=").append(format);

		return prevurlbuf;
	}

	StringBuffer getnextURL(String cid,int recnum,String searchID,String dedupFlag,String dedupOption,String dedupDB,String origin,String linkSet,String dbLink,String dedupSet,String database,String format)
	{
		StringBuffer nexturlbuf = new StringBuffer();
		nexturlbuf.append("CID=").append(cid).append("&amp;");
		nexturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		nexturlbuf.append("DupFlag=").append(dedupFlag).append("&amp;");
		nexturlbuf.append("dbpref=").append(dedupDB).append("&amp;");
		nexturlbuf.append("fieldpref=").append(dedupOption).append("&amp;");
		nexturlbuf.append("origin=").append(origin).append("&amp;");
		nexturlbuf.append("dbLink=").append(dbLink).append("&amp;");
		nexturlbuf.append("linkSet=").append(linkSet).append("&amp;");
		nexturlbuf.append("dedupSet=").append(dedupSet).append("&amp;");
		nexturlbuf.append("DOCINDEX=").append(Integer.toString((recnum+1))).append("&amp;");
		nexturlbuf.append("database=").append(database).append("&amp;");
		nexturlbuf.append("format=").append(format);

		return nexturlbuf;
	}

	StringBuffer getsrURL(int recnum,String searchID,String database,Query tQuery)
	{
		StringBuffer srurlbuf = new StringBuffer();
		srurlbuf.append("CID=").append(XSLCIDHelper.searchResultsCid(tQuery.getSearchType())).append("&");
		srurlbuf.append("SEARCHID=").append(searchID).append("&");
		srurlbuf.append("COUNT=").append(Integer.toString((recnum))).append("&");
		srurlbuf.append("database=").append(database);

		return srurlbuf;
	}

	StringBuffer getdedupURL(int recnum,String searchID,String dedupFlag,String dedupOption,String dedupDB,String origin,String linkSet,String dbLink,String dedupSet,String database,String format)
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


	StringBuffer getbookdetURL(int recnum,
							 String searchID,
							 String dedupFlag,
							 String dedupOption,
							 String dedupDB,
							 String origin,
							 String linkSet,
							 String dbLink,
							 String dedupSet,
							 String database,
							 String pii,
							 String docID)
	{
		StringBuffer bookdetbuf = new StringBuffer();
		bookdetbuf.append("CID=dedupSearchBookSummary").append("&amp;");
		bookdetbuf.append("SEARCHID=").append(searchID).append("&amp;");
		bookdetbuf.append("DupFlag=").append(dedupFlag).append("&amp;");
		bookdetbuf.append("dbpref=").append(dedupDB).append("&amp;");
		bookdetbuf.append("fieldpref=").append(dedupOption).append("&amp;");
		bookdetbuf.append("origin=").append(origin).append("&amp;");
		bookdetbuf.append("dbLink=").append(dbLink).append("&amp;");
		bookdetbuf.append("linkSet=").append(linkSet).append("&amp;");
		bookdetbuf.append("dedupSet=").append(dedupSet).append("&amp;");
		bookdetbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		bookdetbuf.append("database=").append(database).append("&amp;");
		if(pii != null)
		{
			bookdetbuf.append("pii=").append(pii).append("&amp;");
		}
		bookdetbuf.append("docid=").append(docID);
		return bookdetbuf;
	}

	StringBuffer getnonpatrefURL(int recnum,
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
		refurlbuf.append("CID=").append("dedupSearchNonPatentReferencesFormat").append("&amp;");
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