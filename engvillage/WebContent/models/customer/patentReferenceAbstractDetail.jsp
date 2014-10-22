<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java"%>
<%@ page session="false"%>
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

<!--import statements of ei packages.-->

<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.data.upt.runtime.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>
<%@ page import="org.ei.domain.Searches"%>

<%@ page errorPage="/error/errorPage.jsp"%>

<%@ page buffer="20kb"%>

<%
    SearchControl sc = null;
    PageEntry entry = null;

    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    String pUserId = null;
    boolean personalization = false;

    String searchID = null;
    String database = null;
    String format = null;
    String cid = null;

    String currentRecord = null;

    String dataFormat = null;

    ClientCustomizer clientCustomizer = null;
    boolean isPersonalizationPresent = true;
    boolean isLHLPresent = true;
    boolean isFullTextPresent = true;
    boolean isBlogLinkPresent = false;
    boolean isLocalHolidinsPresent = true;
    String customizedLogo = "";
    String customizedStartYear = "";
    String customizedEndYear = "";
%>
<%
    try {

        //Getting sessionid from controllerClient
        ControllerClient client = new ControllerClient(request, response);
        UserSession ussession = (UserSession) client.getUserSession();
        pUserId = ussession.getUserid();
        if ((pUserId != null) && (pUserId.trim().length() != 0)) {
            personalization = true;
        }

        String[] credentials = ussession.getCartridge();
        String customerId = ussession.getCustomerid().trim();

        clientCustomizer = new ClientCustomizer(ussession);
        isFullTextPresent = clientCustomizer.checkFullText();
        isBlogLinkPresent = clientCustomizer.checkBlogLink();

        if (clientCustomizer.isCustomized()) {
            isPersonalizationPresent = clientCustomizer.checkPersonalization();
            isLHLPresent = clientCustomizer.checkDDS();
            isLocalHolidinsPresent = clientCustomizer.checkLocalHolding();
            customizedLogo = clientCustomizer.getLogo();
        }

        // Get the request parameters
        currentRecord = request.getParameter("DOCINDEX");
        searchID = request.getParameter("SEARCHID");

        // Get the CID to which the request is to be sent
        // This is used in displaying search results in the appropriate format

        if (request.getParameter("format") != null) {
            format = request.getParameter("format").trim();
            cid = format.trim();
        }

        if (request.getParameter("database") != null) {
            database = request.getParameter("database").trim();
        }

        if (currentRecord == null) {
            currentRecord = "1";
        }

        int index = Integer.parseInt(currentRecord);

        // prevent null pointer later on if no match is made on format
        dataFormat = FullDoc.FULLDOC_FORMAT;
        if (format != null) {
            if (format.toLowerCase().endsWith("abstractformat")) {
                dataFormat = Abstract.ABSTRACT_FORMAT;
            } else if (format.toLowerCase().endsWith("detailedformat")) {
                dataFormat = FullDoc.FULLDOC_FORMAT;
            }
        }

        String sDocId = request.getParameter("docid");

        List docIds = new ArrayList();
        DocID did = new DocID(1, sDocId, databaseConfig.getDatabase(sDocId.substring(0, 3)));
        docIds.add(did);
        DocumentBuilder builder = new MultiDatabaseDocBuilder();

        List eiDocs = builder.buildPage(docIds, dataFormat);
        EIDoc doc = (EIDoc) eiDocs.get(0);

        cid = request.getParameter("CID");
        entry = new PageEntry(doc, false);

        StringBuffer backurl = new StringBuffer();
        backurl.append("CID=").append(request.getParameter("CID")).append("&");
        backurl.append("SEARCHID=").append(request.getParameter("SEARCHID")).append("&");
        backurl.append("DOCINDEX=").append(request.getParameter("DOCINDEX")).append("&");
        backurl.append("PAGEINDEX=").append(request.getParameter("PAGEINDEX")).append("&");
        backurl.append("database=").append(request.getParameter("database")).append("&");
        backurl.append("format=").append(request.getParameter("CID"));

        String encodedSearchNav = "";
        if (request.getParameter("searchnav") != null) {
            encodedSearchNav = URLEncoder.encode(request.getParameter("searchnav"));
        }

        StringBuffer absurl = new StringBuffer();
        if (request.getAttribute("ABS_URL") == null) {
            absurl = getabsURL(sDocId, database, encodedSearchNav);
        } else {
            absurl = (StringBuffer) request.getAttribute("ABS_URL");
        }

        StringBuffer deturl = new StringBuffer();
        if (request.getAttribute("DET_URL") == null) {
            deturl = getdetURL(sDocId, database, encodedSearchNav);
        } else {
            deturl = (StringBuffer) request.getAttribute("DET_URL");
        }

        StringBuffer patrefurl = new StringBuffer();
        if (request.getAttribute("PATREF_URL") == null) {
            patrefurl = getpatrefURL(sDocId, database, encodedSearchNav);
        } else {
            patrefurl = (StringBuffer) request.getAttribute("PATREF_URL");
        }

        StringBuffer nonpatrefurl = new StringBuffer();
        if (request.getAttribute("NONPATREF_URL") == null) {
            nonpatrefurl = getnonpatrefURL(sDocId, database, encodedSearchNav);
        } else {
            nonpatrefurl = (StringBuffer) request.getAttribute("NONPATREF_URL");
        }

        /**
         *   Log Functionality
         */
        /*
        client.log("SEARCH_ID", searchID);
        client.log("DB_NAME", Integer.toString(did.getDatabase().getMask()));
        client.log("NUM_RECS", "1");
        client.log("context", "search");
        client.log("DOC_INDEX", (new Integer(index)).toString());
        client.log("DOC_ID", did.getDocID());
        client.log("FORMAT", dataFormat);
        client.log("ACTION", "document");
         */
        client.setRemoteControl();

        String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

        //	FileWriter out1 = new FileWriter("c:/baja/ReferenerDetail.xml");
        out.write("<PAGE>");
        out.write("<HIDE-NAV/>");
        out.write("<BACKURL>");
        out.write(URLEncoder.encode(backurl.toString()));
        out.write("</BACKURL>");
        out.write("<PAGE-NAV>");
        out.write("<ABS-NAV>");
        out.write(absurl.toString());
        out.write("</ABS-NAV>");
        out.write("<DET-NAV>");
        out.write(deturl.toString());
        out.write("</DET-NAV>");
        out.write("<RESULTS-NAV><![CDATA[");
        out.write(request.getParameter("searchnav"));
        out.write("]]></RESULTS-NAV>");
        out.write("<PATREF-NAV>");
        out.write(patrefurl.toString());
        out.write("</PATREF-NAV>");

        out.write("<NONPATREF-NAV>");
        out.write(nonpatrefurl.toString());
        out.write("</NONPATREF-NAV>");

        out.write("</PAGE-NAV>");

        out.write("<HEADER/>");
        out.write("<ABSTRACT-DETAILED-NAVIGATION-BAR/>");
        out.write("<DBMASK>");
        out.write(database);
        out.write("</DBMASK>");
        out.write("<FOOTER/>");
        out.write(strGlobalLinksXML);
        out.write("<DEDUP>false</DEDUP>");

        out.write("<ABSTRACT-DETAILED-RESULTS-MANAGER/>");
        out.write("<SEARCH/>");
        out.write("<SESSION-TABLE/>");
        out.write("<CUSTOMIZED-STARTYEAR>" + customizedStartYear + "</CUSTOMIZED-STARTYEAR>");
        out.write("<CUSTOMIZED-ENDYEAR>" + customizedEndYear + "</CUSTOMIZED-ENDYEAR>");

        out.write("<CID>" + cid + "</CID>");
        out.write("<CUSTOMER-ID>" + customerId + "</CUSTOMER-ID>");
        out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");
        out.write("<LHL>" + isLHLPresent + "</LHL>");
        out.write("<BLOGLINK>" + isBlogLinkPresent + "</BLOGLINK>");
        out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
        out.write("<FULLTEXT>" + isFullTextPresent + "</FULLTEXT>");
        out.write("<LOCALHOLDINGS>" + isLocalHolidinsPresent + "</LOCALHOLDINGS>");
        out.write("<DATABASE>" + database + "</DATABASE>");
        out.write("<RESULTS-COUNT>1</RESULTS-COUNT>");
        out.write("<SESSION-ID>" + ussession.getSessionid() + "</SESSION-ID>");
        out.write("<PERSONALIZATION>" + personalization + "</PERSONALIZATION>");
        out.write("<SEARCH-ID>" + searchID + "</SEARCH-ID>");
        out.write("<PAGE-INDEX>1</PAGE-INDEX>");
        out.write("<PREV-PAGE-ID>" + (index - 1) + "</PREV-PAGE-ID>");
        out.write("<CURR-PAGE-ID>" + index + "</CURR-PAGE-ID>");
        out.write("<NEXT-PAGE-ID>" + (index + 1) + "</NEXT-PAGE-ID>");
        out.write("<PAGE-RESULTS>");
        // output databases based on credentials
        databaseConfig.toXML(credentials, out);

        entry.toXML(out);
        //out1.write("<PAGE-RESULTS>");
        //entry.toXML(out1);
        //        out1.write("</PAGE-RESULTS>");
        out.write("</PAGE-RESULTS>");

        out.write("</PAGE>");
        out.write("<!--END-->");
        out.flush();
        //out1.flush();
        // Caches the Result for the next set of search results documents
        //sc.maintainCache(index,0);

    } finally {
        if (sc != null) {
            sc.closeSearch();
        }
    }
%><%!StringBuffer getabsURL(String docID, String database, String encodedSearchNav) {
        StringBuffer absurlbuf = new StringBuffer();
        absurlbuf.append("CID=referenceAbstractFormat&amp;format=referenceAbstractFormat&amp;");
        absurlbuf.append("database=").append(database).append("&amp;");
        absurlbuf.append("docid=").append(docID).append("&amp;");
        absurlbuf.append("searchnav=").append(encodedSearchNav);
        return absurlbuf;
    }

    StringBuffer getdetURL(String docID, String database, String encodedSearchNav) {
        StringBuffer absurlbuf = new StringBuffer();
        absurlbuf.append("CID=referenceDetailedFormat&amp;format=referenceDetailedFormat&amp;");
        absurlbuf.append("database=").append(database).append("&amp;");
        absurlbuf.append("docid=").append(docID).append("&amp;");
        absurlbuf.append("searchnav=").append(encodedSearchNav);
        return absurlbuf;
    }

    StringBuffer getpatrefURL(String docID, String database, String encodedSearchNav) {
        StringBuffer refurlbuf = new StringBuffer();
        refurlbuf.append("CID=referenceReferencesFormat&amp;format=patentReferencesFormat&amp;");
        refurlbuf.append("database=").append(database).append("&amp;");
        refurlbuf.append("docid=").append(docID).append("&amp;");
        refurlbuf.append("searchnav=").append(encodedSearchNav);
        return refurlbuf;
    }

    StringBuffer getnonpatrefURL(String docID, String database, String encodedSearchNav) {
        StringBuffer nonpatrefurlbuf = new StringBuffer();
        nonpatrefurlbuf.append("CID=referenceNonPatentReferencesFormat&amp;format=ReferencesFormat&amp;");
        nonpatrefurlbuf.append("database=").append(database).append("&amp;");
        nonpatrefurlbuf.append("docid=").append(docID).append("&amp;");
        nonpatrefurlbuf.append("searchnav=").append(encodedSearchNav);
        return nonpatrefurlbuf;
    }%>
