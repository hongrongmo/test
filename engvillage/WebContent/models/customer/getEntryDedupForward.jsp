<%@ page language="java"%><%@ page session="false"%>
<%@ page import="java.util.*"%>
<%@ page import="org.ei.tags.*"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.ei.email.*"%>
<%@ page import="javax.mail.internet.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.query.base.*"%>

<%
	FastSearchControl sc = null;

	String currentRecord = null;
	String searchID = null;
	String cid = null;

	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession = (UserSession) client.getUserSession();
	String sessionId = ussession.getSessionid();
	String pUserId = ussession.getUserid();
	// Get the request parameters
	currentRecord = request.getParameter("DOCINDEX");
	searchID = request.getParameter("SEARCHID");

	if (currentRecord == null) {
		currentRecord = "1";
	}
	int index = Integer.parseInt(currentRecord);
	int recnum = -1;
	int totalcount = -1;

	Query tQuery = Searches.getSearch(searchID);
	if (tQuery == null) {
		tQuery = SavedSearches.getSearch(searchID);
	}
	tQuery.setSearchQueryWriter(new FastQueryWriter());
	tQuery.setDatabaseConfig(DatabaseConfig.getInstance());
	tQuery.setCredentials(ussession.getCartridge());

	/*
	 *   Handle the Hit Highlighting
	 */
	BooleanQuery booleanQuery = tQuery.getParseTree();
	HitHighlighter highlighter = new HitHighlighter(booleanQuery);

	String dedupFlag = "false";
	if (request.getParameter("DupFlag") != null) {
		dedupFlag = request.getParameter("DupFlag");
	}
	String dedupOption = request.getParameter("fieldpref");
	String dedupDB = request.getParameter("dbpref");
	String origin = request.getParameter("origin");
	String linkSet = request.getParameter("linkSet");
	String dbLink = request.getParameter("dbLink");
	String dedupSet = request.getParameter("dedupSet");

	String dedupSearchID = null;
	String criteria = null;
	int dedupSubset = 0;
	int dedupset = 0;
	if (dedupSet != null && dedupSet.length() > 0) {
		dedupset = Integer.parseInt(dedupSet);
	}

	sc = new FastSearchControl();
	sc.setHitHighlighter(highlighter);

	if (format != null) {
		if (format.endsWith("AbstractFormat")) {
			dataFormat = Abstract.ABSTRACT_FORMAT;
		} else if (format.endsWith("TocFormat")) {
			dataFormat = Abstract.ABSTRACT_FORMAT;
		} else if (format.endsWith("DetailedFormat")) {
			dataFormat = FullDoc.FULLDOC_FORMAT;
		}
	} else {
		format = Abstract.ABSTRACT_FORMAT;
		dataFormat = Abstract.ABSTRACT_FORMAT;
	}

	/* StringBuffer backurl = new StringBuffer();
	backurl.append("&");
	backurl.append("DupFlag=").append(request.getParameter("DupFlag")).append("&");
	backurl.append("fieldpref=").append(request.getParameter("fieldpref")).append("&");
	backurl.append("dbpref=").append(request.getParameter("dbpref")).append("&");
	backurl.append("origin=").append(request.getParameter("origin")).append("&");
	backurl.append("linkSet=").append(request.getParameter("linkSet")).append("&");
	backurl.append("dbLink=").append(request.getParameter("dbLink")).append("&");
	backurl.append("dedupSet=").append(request.getParameter("dedupSet"));
	 */

	DedupBroker dedupBroker = new DedupBroker();

	result = sc.openSearch(tQuery, sessionId, pagesize, true);

	FastDeduper deduper = sc.dedupSearch(1000, dedupOption, dedupDB);

	DedupData wanted = deduper.getWanted();
	DedupData unwanted = deduper.getUnwanted();
	List wantedList = null;

	if (origin != null && origin.equalsIgnoreCase("summary")) {
		if (linkSet != null && linkSet.equalsIgnoreCase("deduped")) {
			if (dbLink == null || dbLink.length() == 0) {
				wantedList = wanted.getDocIDs();
			} else {
				wantedList = wanted.getDocIDs(dbLink);
			}
		} else if (linkSet != null
				&& linkSet.equalsIgnoreCase("removed")) {
			if (dbLink != null && dbLink.length() > 0) {
				wantedList = unwanted.getDocIDs(dbLink);
			} else {
				wantedList = unwanted.getDocIDs();
			}
		}
	} else {
		wantedList = wanted.getDocIDs();
	}

	PageEntry entry = dedupBroker.buildEntry(index, wantedList,
			dataFormat, sessionId, highlighter);
	maintainCache = true;
	totalDocCount = tQuery.getRecordCount();
	recnum = Integer.parseInt(currentRecord);
	totalcount = Integer.parseInt(totalDocCount);

	request.setAttribute("CONTROLLER_CLIENT", client);
	request.setAttribute("PAGE_ENTRY", entry);
	request.setAttribute("NUMBER", new Integer(3));

	StringBuffer prevurl = new StringBuffer();
	if (recnum > 1) {
		prevurl = new StringBuffer();
		prevurl.append("CID=").append(cid).append("&amp;");
		prevurl.append("SEARCHID=")
				.append(request.getParameter("SEARCHID"))
				.append("&amp;");
		prevurl.append("DupFlag=").append(dedupFlag).append("&");
		prevurl.append("dbpref=").append(dedupDB).append("&");
		prevurl.append("fieldpref=").append(dedupOption).append("&");
		prevurl.append("origin=").append(origin).append("&");
		prevurl.append("dbLink=").append(dbLink).append("&");
		prevurl.append("linkSet=").append(linkSet).append("&");
		prevurl.append("dedupSet=").append(dedupSet).append("&");
		prevurl.append("DOCINDEX=")
				.append(Integer.toString((recnum - 1))).append("&amp;");
		prevurl.append("database=")
				.append(request.getParameter("database"))
				.append("&amp;");
		prevurl.append("format=")
				.append(request.getParameter("format"));
	}
	request.setAttribute("PREV_URL", prevurl);

	StringBuffer nexturl = new StringBuffer();
	if (recnum < totalcount) {
		nexturl.append("CID=").append(cid).append("&amp;");
		nexturl.append("SEARCHID=")
				.append(request.getParameter("SEARCHID"))
				.append("&amp;");
		nexturl.append("DupFlag=").append(dedupFlag).append("&");
		nexturl.append("dbpref=").append(dedupDB).append("&");
		nexturl.append("fieldpref=").append(dedupOption).append("&");
		nexturl.append("origin=").append(origin).append("&");
		nexturl.append("dbLink=").append(dbLink).append("&");
		nexturl.append("linkSet=").append(linkSet).append("&");
		nexturl.append("dedupSet=").append(dedupSet).append("&");
		nexturl.append("DOCINDEX=")
				.append(Integer.toString((recnum + 1))).append("&amp;");
		nexturl.append("database=")
				.append(request.getParameter("database"))
				.append("&amp;");
		nexturl.append("format=")
				.append(request.getParameter("format"));
	}
	request.setAttribute("NEXT_URL", nexturl);

	StringBuffer srurl = new StringBuffer();
	srurl.append("CID=")
			.append(XSLCIDHelper.searchResultsCid(tQuery
					.getSearchType())).append("&amp;");
	srurl.append("SEARCHID=").append(request.getParameter("SEARCHID"))
			.append("&amp;");
	srurl.append("COUNT=").append(Integer.toString((recnum)))
			.append("&amp;");
	srurl.append("database=").append(request.getParameter("database"));
	request.setAttribute("SEARCH_RESULTS_URL", srurl);

	StringBuffer nsurl = new StringBuffer();
	nsurl = new StringBuffer();
	nsurl.append("CID=")
			.append(XSLCIDHelper.newSearchCid(tQuery.getSearchType()))
			.append("&amp;");
	nsurl.append("database=").append(request.getParameter("database"));
	request.setAttribute("NEW_SEARCH_URL", nsurl);

	StringBuffer absurl = new StringBuffer();
	absurl = new StringBuffer();
	absurl.append("CID=")
			.append(XSLCIDHelper.formatBase(tQuery.getSearchType())
					+ "AbstractFormat").append("&amp;");
	absurl.append("SEARCHID=").append(request.getParameter("SEARCHID"))
			.append("&amp;");
	absurl.append("DupFlag=").append(dedupFlag).append("&");
	absurl.append("dbpref=").append(dedupDB).append("&");
	absurl.append("fieldpref=").append(dedupOption).append("&");
	absurl.append("origin=").append(origin).append("&");
	absurl.append("dbLink=").append(dbLink).append("&");
	absurl.append("linkSet=").append(linkSet).append("&");
	absurl.append("dedupSet=").append(dedupSet).append("&");

	absurl.append("DOCINDEX=").append(Integer.toString((recnum + 1)))
			.append("&amp;");
	absurl.append("database=").append(request.getParameter("database"))
			.append("&amp;");
	absurl.append("format=").append(
			XSLCIDHelper.formatBase(tQuery.getSearchType())
					+ "AbstractFormat");

	request.setAttribute("ABS_URL", absurl);

	StringBuffer deturl = new StringBuffer();
	deturl = new StringBuffer();
	deturl.append("CID=")
			.append(XSLCIDHelper.formatBase(tQuery.getSearchType())
					+ "DetailedFormat").append("&amp;");
	deturl.append("SEARCHID=").append(request.getParameter("SEARCHID"))
			.append("&amp;");
	deturl.append("DupFlag=").append(dedupFlag).append("&");
	deturl.append("dbpref=").append(dedupDB).append("&");
	deturl.append("fieldpref=").append(dedupOption).append("&");
	deturl.append("origin=").append(origin).append("&");
	deturl.append("dbLink=").append(dbLink).append("&");
	deturl.append("linkSet=").append(linkSet).append("&");
	deturl.append("dedupSet=").append(dedupSet).append("&");

	deturl.append("DOCINDEX=").append(Integer.toString((recnum + 1)))
			.append("&amp;");
	deturl.append("database=").append(request.getParameter("database"))
			.append("&amp;");
	deturl.append("format=").append(
			XSLCIDHelper.formatBase(tQuery.getSearchType())
					+ "DetailedFormat");
	request.setAttribute("DET_URL", deturl);

	pageContext.forward("abstractDetailedRecord.jsp");
%>