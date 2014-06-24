<%@page import="org.apache.log4j.Logger"%>
<%@ page language="java"%>
<%@ page session="false"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.books.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.DecimalFormat"%>
<%@page import="org.ei.tags.EditGrouper"%>
<%@page import="org.ei.tags.ScopeComp"%>
<%@page import="org.ei.tags.TagBubble"%>
<%@page import="org.ei.service.amazon.s3.AmazonS3ServiceImpl"%>
<%@page import="org.ei.service.amazon.s3.AmazonS3Service"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>
<%
    final Logger log4j = Logger.getLogger("bookDetail.jsp");
    FastSearchControl sc = null;
    PageEntry entry = null;
    BookDocument curDoc = null;
    EIDoc curEIDoc =null;
    SearchResult result = null;
    SessionID sessionIdObj = null;
    ClientCustomizer clientCustomizer = null;

    String totalDocCount = null;
    String sessionId = null;
    String pUserId = null;

    String cid = null;
    String searchID = null;
    String docid = null;
    String docindex = null;
    String database = null;
    String pii = null;

    String isbn = null;
    String dataFormat = null;
    String customizedLogo = null;

    boolean personalization = false;
    boolean isPersonalizationPresent = true;
    String resultsPage=null;
    StringBuffer prevurl = new StringBuffer();
    StringBuffer nexturl = new StringBuffer();
    StringBuffer pagedeturl = new StringBuffer();
    String internalSearch = null;
    
%>
<%!
    DatabaseConfig databaseConfig = null;
    DecimalFormat df = null;
    int pagesize = 0;
    String wobl_url = "";

    public void jspInit()
    {
        try
        {
            df = new DecimalFormat("0000");
            df.setMinimumIntegerDigits(4);

            databaseConfig = DatabaseConfig.getInstance();

            // Get the value of the number of documents to be displayed in a search results page form Runtime.properties file
            RuntimeProperties runtimeProps = ConfigService.getRuntimeProperties();
            pagesize = Integer.parseInt(runtimeProps.getProperty("PAGESIZE"));
            wobl_url = runtimeProps.getProperty(RuntimeProperties.WHOLE_BOOK_DOWNLOAD_BASE_URL);

            // jam Y2K3
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
%>
<%
	try {
		//Getting sessionid from controllerClient
		log4j.info("Starting...");
		ControllerClient client = new ControllerClient(request, response);
		UserSession ussession = (UserSession) client.getUserSession();
		sessionId = ussession.getID();
		sessionIdObj = ussession.getSessionID();
		pUserId = ussession.getUserIDFromSession();
		if ((pUserId != null) && (pUserId.trim().length() != 0)) {
			personalization = true;
		}

		IEVWebUser user = ussession.getUser();
		String[] credentials = user.getCartridge();
		String customerId = user.getCustomerID();
		String contractId = user.getContractID();

		clientCustomizer = new ClientCustomizer(ussession);
		customizedLogo = "";
		if (clientCustomizer.isCustomized()) {
			isPersonalizationPresent = clientCustomizer.checkPersonalization();
			customizedLogo = clientCustomizer.getLogo();
		}

		// Get the request parameters
		cid = request.getParameter("CID");
		docindex = request.getParameter("DOCINDEX");
		resultsPage = request.getParameter("PAGEINDEX");
		searchID = request.getParameter("SEARCHID");
		database = request.getParameter("database");
		docid = request.getParameter("docid");
		pii = request.getParameter("pii");

		log4j.info("Request parms, CID=" + cid + ", docindex=" + docindex + ", resultsPage=" + resultsPage + ", searchID=" + searchID + ", database="
				+ database + ", docid=" + docid + ", pii=" + pii);

		dataFormat = FullDoc.FULLDOC_FORMAT;
		int recnum = Integer.parseInt(docindex);

		String pdfpage = "";
		int index = 1;
		try {
			index = Integer.parseInt(docindex);
		} catch (NumberFormatException e) {
		}

		// retreive query object and individually set properties
		Query tQuery = Searches.getSearch(searchID);
		if ("bookSummary".equalsIgnoreCase(cid) && request.getParameter("HITSEARCH") != null && request.getParameter("HITSEARCH").equals("yes")) {
			log4j.info("Setup for bookSummary...");
			if (tQuery == null) {
				tQuery = SavedSearches.getSearch(searchID);
			}

			if (tQuery != null) {
				tQuery.setSearchQueryWriter(new FastQueryWriter());
				tQuery.setDatabaseConfig(databaseConfig);
				tQuery.setCredentials(user.getCartridge());

				sc = new FastSearchControl();
				result = sc.openSearch(tQuery, sessionId, pagesize, true);

				entry = result.entryAt(index, dataFormat);
				curEIDoc = entry.getDoc();
				DocID did = curEIDoc.getDocID();
				docid = did.getDocID();
				entry = null;

			} else {
				log4j.warn("Query object empty for bookSummary!");
			}
		}
		if (docid != null) {
			log4j.info("Setup for docid...");
			docid = docid.toLowerCase();
			String[] bookid = docid.split("_", 3);
			if (bookid != null && bookid.length == 3) {
				isbn = bookid[1];
				pdfpage = bookid[2];
				try {
					if (Long.parseLong(pdfpage) == 0) {
						pdfpage = "1";
					}
				} catch (NumberFormatException e) {
					pdfpage = "1";
				}
			}
		} else {
			log4j.info("Setup for pdfpage...");
			pdfpage = "1";
		}

		if (docindex == null) {
			docindex = "1";
		}

		// if this is naivgation frame, don't attempt this
		// entry will be built from docid, we do not need the query
		if ("bookNav".equalsIgnoreCase(cid)) {
			log4j.info("Setup for bookNav...");
			if (tQuery == null) {
				tQuery = SavedSearches.getSearch(searchID);
			}
			if (tQuery != null) {
				tQuery.setSearchQueryWriter(new FastQueryWriter());
				tQuery.setDatabaseConfig(databaseConfig);
				tQuery.setCredentials(user.getCartridge());

				sc = new FastSearchControl();
				result = sc.openSearch(tQuery, sessionId, pagesize, true);

				if (result.getHitCount() == 0) {
					entry = null;
				} else {
					entry = result.entryAt(index, dataFormat);
				}
			}
		}

		if ((tQuery == null) || (entry == null)) {
			log4j.info("Query or entry object is null, build from isbn + pdfpage");
			Database pagdb = DatabaseConfig.getInstance().getDatabase("pag");
			DocumentBuilder builder = pagdb.newBuilderInstance();

			List docids = new ArrayList();

			String strdocid = "pag" + "_" + isbn + "_" + pdfpage;
			if (cid.toLowerCase().endsWith("booksummary")) {
				strdocid = "pag" + "_" + isbn + "_" + "0";
			}
			docids.add(new DocID(strdocid, pagdb));

			List docs = builder.buildPage(docids, dataFormat);
			EIDoc bookdoc = (EIDoc) docs.iterator().next();
			entry = new PageEntry(bookdoc, false);

		}

		curDoc = (BookDocument) entry.getDoc();
		isbn = curDoc.getISBN13();

		DocID did = curDoc.getDocID();

		StringBuffer tocurl = new StringBuffer();
		tocurl.append("CID=").append("bookToc").append("&");
		tocurl.append("SEARCHID=").append(searchID).append("&");
		tocurl.append("DOCINDEX=").append(docindex).append("&");
		tocurl.append("database=").append(database).append("&");
		tocurl.append("docid=").append(docid);

		/* This single docview page can only be for 1 record/book */
		totalDocCount = "1";

		/**
		 *   Log Functionality
		 */
		log(client, cid, isbn);

		String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
		String strQuery = "";

		out.write("<PAGE>");

		String searchContext = "default";
		if (request.getAttribute("SEARCH_CONTEXT") != null) {
			searchContext = (String) request.getAttribute("SEARCH_CONTEXT");
		}
		out.write("<SEARCH-CONTEXT>");
		out.write(searchContext);
		out.write("</SEARCH-CONTEXT>");
		out.write("<SESSION-DATA><SEARCH-TYPE>Book</SEARCH-TYPE></SESSION-DATA>");

		if (!"bookToc".equalsIgnoreCase(cid) && !"bookNav".equalsIgnoreCase(cid)) {
			out.write("<PAGE-NAV>");

			StringBuffer srurl = new StringBuffer();
			StringBuffer nsurl = new StringBuffer();
			StringBuffer absurl = new StringBuffer();
			StringBuffer deturl = new StringBuffer();
			StringBuffer bookdeturl = new StringBuffer();

			if (request.getAttribute("SEARCH_RESULTS_URL") == null) {
				srurl = getsrURL(recnum, searchID, database, tQuery);
			} else {
				srurl = (StringBuffer) request.getAttribute("SEARCH_RESULTS_URL");
			}

			StringBuffer dedupurl = new StringBuffer();
			if (request.getAttribute("DEDUP_RESULTS_URL") != null) {
				dedupurl = (StringBuffer) request.getAttribute("DEDUP_RESULTS_URL");
			}

			if (request.getAttribute("NEW_SEARCH_URL") == null) {
				nsurl = getnsURL(database, tQuery);
			} else {
				nsurl = (StringBuffer) request.getAttribute("NEW_SEARCH_URL");
			}

			if (request.getAttribute("ABS_URL") == null) {
				absurl = getabsURL(tQuery, recnum, searchID, database);
			} else {
				absurl = (StringBuffer) request.getAttribute("ABS_URL");
			}

			if (request.getAttribute("DET_URL") == null) {
				deturl = getdetURL(tQuery, recnum, searchID, database);
			} else {
				deturl = (StringBuffer) request.getAttribute("DET_URL");
			}

			if ("bookSummary".equalsIgnoreCase(cid)) {

				if (request.getParameter("RESULTCOUNT") != null) {
					totalDocCount = request.getParameter("RESULTCOUNT");
				}

				if (request.getParameter("INTERNALSEARCH") != null) {
					internalSearch = request.getParameter("INTERNALSEARCH");
				}

				pagedeturl = getpagedetURL(tQuery, recnum, searchID, database);

				if (recnum > 1) {
					prevurl = getprevURL(cid, recnum, searchID, database, dataFormat, docid, pii, totalDocCount, internalSearch);
				}

				int totalcount = Integer.parseInt(totalDocCount);
				//maximum record 4000

				if (recnum < totalcount && recnum < 4000) {
					nexturl = getnextURL(cid, recnum, searchID, database, dataFormat, docid, pii, totalDocCount, internalSearch);
				}

			}

			out.write("<RESULTS-NAV><![CDATA[");
			out.write(srurl.toString());
			out.write("]]></RESULTS-NAV>");
			out.write("<ABS-NAV>");
			out.write(absurl.toString());
			out.write("</ABS-NAV>");
			out.write("<DET-NAV>");
			out.write(deturl.toString());
			out.write("</DET-NAV>");
			out.write("<NEWSEARCH-NAV>");
			out.write(nsurl.toString());
			out.write("</NEWSEARCH-NAV>");
			if (dedupurl.length() > 0) {
				out.write("<DEDUP-RESULTS-NAV>");
				out.write(dedupurl.toString());
				out.write("</DEDUP-RESULTS-NAV>");
			}

			StringBuffer rpurl = getreadPageURL(recnum, searchID, docid, database);
			out.write("<READPAGE-NAV>");
			out.write(rpurl.toString());
			out.write("</READPAGE-NAV>");
			if (pagedeturl.length() > 0) {
				out.write("<PAGEDET-NAV>");
				out.write(pagedeturl.toString());
				out.write("</PAGEDET-NAV>");
			}
			if (prevurl.length() > 0) {
				out.write("<PREV>");
				out.write(prevurl.toString());
				out.write("</PREV>");
			}
			if (nexturl.length() > 0) {
				out.write("<NEXT>");
				out.write(nexturl.toString());
				out.write("</NEXT>");
			}
			out.write("</PAGE-NAV>");
		}

		String strTicket = BookDocument.getReadPageTicket(isbn, customerId);

		out.write("<TICKET><![CDATA[");
		out.write(strTicket);
		out.write("]]></TICKET>");

		out.write("<DOCVIEW><![CDATA[");
		out.write(wobl_url);
		out.write("]]></DOCVIEW>");

		out.write("<PDFARGS><![CDATA[");
		out.write(BookDocument.PDF_ANCHOR);
		out.write("]]></PDFARGS>");

		if (tQuery != null) {
			LemQueryWriter searchQueryWriter = new LemQueryWriter();
			BaseParser parser = new BaseParser();
			BooleanQuery queryTree = (BooleanQuery) parser.parse(tQuery.getPhysicalQuery());

			if (BookDocument.isHitHighlightable(isbn)) {
				strQuery = searchQueryWriter.getQuery(queryTree);
				if (strQuery != null) {
					strQuery = strQuery.replaceFirst(";$", "");
				}
			}
		}

		out.write("<HILITE><![CDATA[");
		if (strQuery != null) {
			out.write(java.net.URLEncoder.encode(strQuery, "UTF-8"));
		}
		out.write("]]></HILITE>");

		out.write("<TOCURL>");
		out.write(URLEncoder.encode(tocurl.toString()));
		out.write("</TOCURL>");
		
	    out.write("<BOOKIMGS-URL><![CDATA[");
	    out.write(wobl_url + "/images/" + isbn + "/" + isbn + "small.jpg");
	    out.write("]]></BOOKIMGS-URL>");

		out.write(strGlobalLinksXML);

		TagBubble bubble = new TagBubble("", "", "nextURL", pUserId, customerId, did, new ScopeComp());
		// here are the tags:

		EditGrouper grouper = new EditGrouper(pUserId, customerId, did.getDocID());

		bubble.toXML(out);
		grouper.editXML(bubble.getTags(), out);

		out.write("<HEADER/>");
		out.write("<ABSTRACT-DETAILED-NAVIGATION-BAR/>");
		out.write("<ABSTRACT-DETAILED-RESULTS-MANAGER/>");
		out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
		out.write("<RESULTS-COUNT>" + totalDocCount + "</RESULTS-COUNT>");
		out.write("<SESSION-ID>" + sessionIdObj.toString() + "</SESSION-ID>");
		out.write("<PERSONALIZATION>" + personalization + "</PERSONALIZATION>");
		out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");

		out.write("<DBMASK>");
		out.write(database);
		out.write("</DBMASK>");

		if (internalSearch != null) {
			out.write("<INTERNALSEARCH>");
			out.write(internalSearch);
			out.write("</INTERNALSEARCH>");
		}

		out.write("<CID>" + cid + "</CID>");
		out.write("<CUSTOMER-ID>" + customerId + "</CUSTOMER-ID>");
		out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");

		out.write("<DATABASE>" + database + "</DATABASE>");
		out.write("<SESSION-ID>" + sessionIdObj.toString() + "</SESSION-ID>");
		out.write("<SEARCH-ID>" + searchID + "</SEARCH-ID>");

		out.write("<CURR-PAGE>" + df.format(Long.parseLong(pdfpage)) + "</CURR-PAGE>");
		if (pii != null) {
			out.write("<PII>" + pii + "</PII>");
		}
		if ("bookSummary".equalsIgnoreCase(cid)) {
			out.write("<PAGE-INDEX>" + resultsPage + "</PAGE-INDEX>");
			out.write("<PREV-PAGE-ID>" + (index - 1) + "</PREV-PAGE-ID>");
			out.write("<CURR-PAGE-ID>" + index + "</CURR-PAGE-ID>");
			out.write("<NEXT-PAGE-ID>" + (index + 1) + "</NEXT-PAGE-ID>");
		}
		out.write("<PAGE-RESULTS>");
		databaseConfig.toXML(credentials, out);
		entry.toXML(out);

		out.write("</PAGE-RESULTS>");
		if (tQuery != null) {
			out.write(tQuery.toXMLString());
		}

		if (!"bookNav".equalsIgnoreCase(cid)) {
			out.write("<CLOUD>");
			out.write("<![CDATA[");
			curDoc.getTagCloud(out);
			out.write("]]>");
			out.write("</CLOUD>");

			out.write("<TOC>");
			out.write("<![CDATA[");
			curDoc.getTOC(out);
			out.write("]]>");
			out.write("</TOC>");
		}
        
		out.write("<FOOTER/>");
		out.write("</PAGE>");
		out.flush();

		//    Writer wrtr = new BufferedWriter(new OutputStreamWriter(System.out));
		//    entry.toXML(wrtr);
		//    wrtr.flush();

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if (sc != null) {
			sc.closeSearch();
		}
	}
%>
<%!void log(ControllerClient client, String cid, String isbn) {

		client.log("isbn", isbn);
		if (cid != null && cid.equals("printBookDetail"))
			client.log("referex", "printBookDetail");
		else
			client.log("referex", "bookDetail");

		client.setRemoteControl();
	}

	StringBuffer getsrURL(int recnum, String searchID, String database, Query tQuery) {

		StringBuffer srurlbuf = new StringBuffer();
		System.out.println("before CID");
		if (tQuery == null) {
			srurlbuf.append("CID=").append(XSLCIDHelper.searchResultsCid("Book")).append("&");
		} else {
			srurlbuf.append("CID=").append(XSLCIDHelper.searchResultsCid(tQuery.getSearchType())).append("&");
		}

		srurlbuf.append("SEARCHID=").append(searchID).append("&");
		srurlbuf.append("COUNT=").append(Integer.toString((recnum))).append("&");
		srurlbuf.append("database=").append(database);
		return srurlbuf;
	}

	StringBuffer getnsURL(String database, Query tQuery) {
		StringBuffer nsurlbuf = new StringBuffer();
		if (tQuery == null) {
			nsurlbuf.append("CID=").append(XSLCIDHelper.newSearchCid("Book")).append("&amp;");
		} else {
			nsurlbuf.append("CID=").append(XSLCIDHelper.newSearchCid(tQuery.getSearchType())).append("&amp;");
		}
		nsurlbuf.append("database=").append(database);
		return nsurlbuf;
	}

	StringBuffer getabsURL(Query tQuery, int recnum, String searchID, String database) {
		StringBuffer absurlbuf = new StringBuffer();
		if (tQuery == null) {
			absurlbuf.append("CID=").append(XSLCIDHelper.formatBase("Book") + "AbstractFormat").append("&amp;");
		} else {
			absurlbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType()) + "AbstractFormat").append("&amp;");
		}
		absurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		absurlbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		absurlbuf.append("database=").append(database).append("&amp;");
		if (tQuery == null) {
			absurlbuf.append("format=").append(XSLCIDHelper.formatBase("Book") + "AbstractFormat");
		} else {
			absurlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType()) + "AbstractFormat");
		}
		return absurlbuf;
	}

	StringBuffer getdetURL(Query tQuery, int recnum, String searchID, String database) {
		StringBuffer deturlbuf = new StringBuffer();
		if (tQuery == null) {
			deturlbuf.append("CID=").append(XSLCIDHelper.formatBase("Book") + "DetailedFormat").append("&amp;");
		} else {
			deturlbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType()) + "DetailedFormat").append("&amp;");
		}
		deturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		deturlbuf.append("DOCINDEX=").append(Integer.toString(recnum)).append("&amp;");
		deturlbuf.append("database=").append(database).append("&amp;");
		if (tQuery == null) {
			deturlbuf.append("format=").append(XSLCIDHelper.formatBase("Book") + "DetailedFormat");
		} else {
			deturlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType()) + "DetailedFormat");
		}
		return deturlbuf;
	}

	StringBuffer getreadPageURL(int recnum, String searchID, String docid, String database) {
		StringBuffer rpurlbuf = new StringBuffer();
		rpurlbuf.append("CID=").append("bookFrameset").append("&amp;");
		rpurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		rpurlbuf.append("DOCINDEX=").append(Integer.toString(recnum)).append("&amp;");
		rpurlbuf.append("docid=").append(docid).append("&amp;");
		rpurlbuf.append("database=").append(database);
		return rpurlbuf;
	}

	StringBuffer getpagedetURL(Query tQuery, int recnum, String searchID, String database) {
		StringBuffer deturlbuf = new StringBuffer();
		deturlbuf.append("CID=").append("pageDetailedFormat").append("&amp;");
		deturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		deturlbuf.append("DOCINDEX=").append(Integer.toString(recnum)).append("&amp;");
		deturlbuf.append("database=").append(database).append("&amp;");
		if (tQuery == null) {
			deturlbuf.append("format=").append(XSLCIDHelper.formatBase("Book") + "DetailedFormat");
		} else {
			deturlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType()) + "DetailedFormat");
		}
		return deturlbuf;
	}

	StringBuffer getprevURL(String cid, int recnum, String searchID, String database, String format, String docID, String pii, String resultcount,
			String internalSearch) {
		StringBuffer prevurlbuf = new StringBuffer();
		prevurlbuf.append("CID=bookSummary").append("&amp;");
		prevurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		prevurlbuf.append("DOCINDEX=").append(Integer.toString((recnum - 1))).append("&amp;");
		prevurlbuf.append("database=").append(database).append("&amp;");
		if (pii != null) {
			prevurlbuf.append("pii=").append(pii).append("&amp;");
		}
		prevurlbuf.append("RESULTCOUNT=").append(resultcount).append("&amp;");
		if (internalSearch != null) {
			prevurlbuf.append("INTERNALSEARCH=").append(internalSearch).append("&amp;");
		}
		prevurlbuf.append("HITSEARCH=yes").append("&amp;");
		prevurlbuf.append("docid=").append(docID);
		return prevurlbuf;
	}

	StringBuffer getnextURL(String cid, int recnum, String searchID, String database, String format, String docID, String pii, String resultcount,
			String internalSearch) {
		StringBuffer nexturlbuf = new StringBuffer();
		nexturlbuf.append("CID=bookSummary").append("&amp;");
		nexturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		nexturlbuf.append("DOCINDEX=").append(Integer.toString((recnum + 1))).append("&amp;");
		nexturlbuf.append("database=").append(database).append("&amp;");
		if (pii != null) {
			nexturlbuf.append("pii=").append(pii).append("&amp;");
		}
		nexturlbuf.append("RESULTCOUNT=").append(resultcount).append("&amp;");
		if (internalSearch != null) {
			nexturlbuf.append("INTERNALSEARCH=").append(internalSearch).append("&amp;");
		}
		nexturlbuf.append("HITSEARCH=yes").append("&amp;");
		nexturlbuf.append("docid=").append(docID);

		return nexturlbuf;
	}%>
