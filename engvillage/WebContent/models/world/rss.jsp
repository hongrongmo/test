<%@page import="org.apache.log4j.Logger"%>
<%@ page language="java"%>
<%@ page session="false"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="org.ei.xmlio.*"%>
<%@ page buffer="20kb"%>
<%
    Logger log4j = Logger.getLogger("rss.jsp");
	String xmlDoc = null;
	String totalDocCount = null;
	SearchResult result = null;
	org.ei.domain.Query queryObject = null;
	ControllerClient client = new ControllerClient(request, response);
	String sessionId = null;
	String searchID = null;
	String format = StringUtil.EMPTY_STRING;
	String term1 = null;
	String dbName = null;
	String sortBy = "";
	String sortDir = "";
	String autoStem = "";
	StringBuffer query = null;
	String category = null;
	String update = null;
	SearchControl sc = null;
	DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

	try {

		String rssID = request.getParameter("RSSQueryID");
		String queryID = null;
		if (rssID != null) {
			xmlDoc = RSS.getRssQuery(rssID).QUERY;
			dbName = RSS.getRssQuery(rssID).DATABASE;
			sortBy = RSS.getRssQuery(rssID).SORTBY;
			sortDir = RSS.getRssQuery(rssID).SORTDIR;
			update = RSS.getRssQuery(rssID).UPDATENUMBER;
			category = RSS.getRssQuery(rssID).CATEGORY;
		} else if (request.getParameter("queryID") != null) {
			queryID = request.getParameter("queryID");
			String[] rssInfo = RSS.getQuery(queryID);
			if (rssInfo == null || rssInfo.length < 2) {
			    throw new RuntimeException("Unable to retrieve RSS query!");
			}
			dbName = rssInfo[0];
			term1 = rssInfo[1];
			sortBy = "relevance";
			sortDir = "dw";
			update = "1";
		} else {
			throw new RuntimeException("Parameter 'RSSQueryID' or 'queryID' is missing!");
		}

		sc = new FastSearchControl();

		/*
		UserSession ussession = (UserSession) client.getUserSession();
		sessionId = ussession.getSessionid();
		sessionIdObj = ussession.getSessionID();
		IEVWebUser user = ussession.getUser();
		int intDbMask = Integer.parseInt(dbName);
		String[] credentials = ussession.getCartridge();
		if (credentials == null) {
			credentials = new String[3];
			credentials[0] = "CPX";
			credentials[1] = "INS";
			credentials[2] = "NTI";
		}
*/
        int intDbMask = Integer.parseInt(dbName);
		List<String> carlist = UserSession.buildUserCartridgeForRSS(intDbMask);
		String[] credentials = null;
		if (carlist.size() <= 0) {
		    credentials = new String[3];
		    credentials[0] = "CPX";
		    credentials[1] = "INS";
		    credentials[2] = "NTI";
		} else {
		    credentials = (String[]) carlist.toArray(new String[carlist.size()]);
		}

        queryObject = new org.ei.domain.Query(databaseConfig, credentials);
		queryObject.setDataBase(intDbMask);
		searchID = new GUID().toString();
		queryObject.setID(searchID);
		queryObject.setSearchType(org.ei.domain.Query.TYPE_EXPERT);
		queryObject.setSortOption(new Sort(sortBy, sortDir));
		if (xmlDoc != null && term1 == null) {
			XqueryxParser xp = new XqueryxParser();
			XqueryxRewriter xr = new XqueryxRewriter();
			org.ei.xmlio.Query xquery = (org.ei.xmlio.Query) xp.parse(xmlDoc);
			xquery.accept(xr);
			term1 = xr.getQuery();
		}
		queryObject.setSearchPhrase(term1, "", "", "", "", "", "", "");
		queryObject.setAutoStemming("on");
		update = "1";
		queryObject.setLastFourUpdates(update);
		queryObject.setSearchQueryWriter(new FastQueryWriter());
		queryObject.compile();
		//System.out.println("RSS Fast Query:"+ queryObject.getSearchQuery());
		result = sc.openSearch(queryObject, sessionId, 25, true);
		List docIds = sc.getDocIDRange(1, 400);
		totalDocCount = Integer.toString(result.getHitCount());
		queryObject.setRecordCount(totalDocCount);
		queryObject.setSessionID(sessionId);

		format = "XMLCITATION";
		client.log("search_id", queryObject.getID());
		client.log("query_string", queryObject.getPhysicalQuery());
		client.log("sort_by", queryObject.getSortOption().getSortField());
		client.log("suppress_stem", queryObject.getAutoStemming());
		client.log("context", "search");
		client.log("action", "search");
		client.log("type", "basic");
		client.log("rss", "y");
		client.log("db_name", dbName);
		client.log("page_size", "25");
		client.log("format", format);
		client.log("doc_id", " ");
		client.log("num_recs", totalDocCount);
		client.log("doc_index", "1");
		client.log("hits", totalDocCount);
		client.setRemoteControl();
		if (category == null) {
			category = queryObject.getDisplayQuery();
		}

        UserSession ussession = (UserSession) client.getUserSession();
		if (docIds.size() > 0) {
			Pagemaker pagemaker = new Pagemaker(sessionId, 25, docIds, Citation.XMLCITATION_FORMAT);
			String serverName = ussession.getProperty(UserSession.ENV_BASEADDRESS);
			out.write("<!--BH--><HEADER>");
			out.write("<SEARCH-ID>" + queryID + "</SEARCH-ID>");
			out.write("</HEADER>");
			out.write("<DBMASK>");
			out.write(dbName);
			out.write("</DBMASK>");
			out.write("<CATEGORY-TITLE>" + category + "</CATEGORY-TITLE>");
			out.write("<!--EH-->");
			while (pagemaker.hasMorePages()) {
				List builtDocuments = pagemaker.nextPage();
				for (int i = 0; i < builtDocuments.size(); i++) {
					EIDoc eidoc = (EIDoc) builtDocuments.get(i);
					out.write("<!--BR-->");
					out.write("<SERVER>" + serverName + "</SERVER>");
					eidoc.toXML(out);
					out.write("<!--ER-->");
				}
			}
			out.write("<!--*-->");
			out.write("<!--BF--><FOOTER/><!--EF-->");
		} else {
			out.write("<!--BH--><HEADER>");
			out.write("<SEARCH-ID>" + queryID + "</SEARCH-ID>");
			out.write("</HEADER>");
			out.write("<DBMASK>");
			out.write(dbName);
			out.write("</DBMASK>");
			out.write("<CATEGORY-TITLE>" + category + "</CATEGORY-TITLE>");
			out.write("<!--EH-->");
			out.write("<!--*-->");
			out.write("<!--BF--><FOOTER/><!--EF-->");
		}
	} catch (Exception e) {
		// Output empty feed!
		log4j.fatal("Unable to generate RSS feed!  Exception: " + e.getClass().getName() + ", message: " + e.getMessage());
		out.write("<!--BH--><HEADER/><!--EH--><!--*--><!--BF--><FOOTER/><!--EF-->");
	} finally {
		if (sc != null) {
			sc.closeSearch();
		}
	}
%>
