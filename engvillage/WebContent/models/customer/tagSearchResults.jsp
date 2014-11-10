<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@page import="org.apache.commons.validator.GenericValidator"%>
<%@ page language="java"%>
<%@ page session="false"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>
<%@ page import="org.ei.tags.*"%>
<%@page import="org.ei.exception.EVBaseException"%>
<%@page import="org.ei.exception.SystemErrorCodes"%>
<%@page import="org.ei.exception.SearchException"%>
<%
    String currentPage = null;
    Page oPage = null;
    ControllerClient client = new ControllerClient(request, response);
    String pUserId = "";
    boolean personalization = false;
    String searchID = null;
    int scopeint = 0;
    String groupID = null;
    int index = 0;
    int nTotalDocs = 0;
    int numRecs = 0;
    String database = null;
    String action = "search";
    ClientCustomizer clientCustomizer = null;
    TagBroker tagBroker = null;

    boolean isPersonalizationPresent = true;
    boolean isFullTextPresent = true;
    boolean isEmailAlertsPresent = true;
    boolean isCitLocalHoldingsPresent = false;
    boolean isRssLinkPresent = false;
    String customizedLogo = "";
    String searchTags = null;
    String TagSearchFlag = "true";
    String searchtype = null;
    UserSession ussession = (UserSession) client.getUserSession();
    String sessionId = ussession.getSessionid();
    pUserId = ussession.getUserid();
    if ((pUserId != null) && (pUserId.trim().length() != 0)) {
        personalization = true;
    }
    try {
        String customerId = ussession.getCustomerid().trim();
        String[] credentials = ussession.getCartridge();

        clientCustomizer = new ClientCustomizer(ussession);
        isFullTextPresent = clientCustomizer.checkFullText("citationResults");
        isRssLinkPresent = clientCustomizer.checkRssLink();
        isCitLocalHoldingsPresent = clientCustomizer.checkLocalHolding("citationResults");
        isPersonalizationPresent = clientCustomizer.checkPersonalization();
        isEmailAlertsPresent = clientCustomizer.checkEmailAlert();

        customizedLogo = clientCustomizer.getLogo();

        // Calulate the results per page
        int pagesize = 25; // Hard-coded default just in case!
        if (null != request.getParameter("pageSizeVal")) {
            pagesize = Integer.parseInt(request.getParameter("pageSizeVal"));
        } else {
            if (null != ussession.getRecordsPerPage()) {
                pagesize = Integer.parseInt(ussession.getRecordsPerPage());
            }
        }

        // Calculate the page navigation
        currentPage = request.getParameter("COUNT");
        if (GenericValidator.isBlankOrNull(currentPage)) {
            currentPage = request.getParameter("PAGE");
            if (!GenericValidator.isBlankOrNull(currentPage)) {
                // convert to index
                int i = Integer.parseInt(currentPage);
                currentPage = Integer.toString(((i - 1) * pagesize) + 1);
            }
        }
        if (GenericValidator.isBlankOrNull(currentPage)) {
            currentPage = "1";
        }

        boolean initialSearch = false;
        boolean clearBasket = false;

        DocumentBasket documentBasket = new DocumentBasket(sessionId);
        index = Integer.parseInt(currentPage.trim());
        String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

        if (request.getParameter("searchtags") != null) {
            searchTags = request.getParameter("searchtags");
        } else if (request.getParameter("searchgrouptags") != null) {
            searchTags = request.getParameter("searchgrouptags");
        }
        if (request.getParameter("SEARCHID") != null) {
            searchID = request.getParameter("SEARCHID");
            if (searchTags == null)
                searchTags = searchID;
        }

        database = clientCustomizer.getDefaultDB();

        if (request.getParameter("searchtype") != null) {
            searchtype = request.getParameter("searchtype");
        } else if (request.getParameter("SEARCHTYPE") != null) {
            searchtype = request.getParameter("SEARCHTYPE");
        }

        String scope = null;

        if (request.getParameter("scope") != null) {
            scope = request.getParameter("scope");
            if (scope.indexOf(":") == -1) {
                scopeint = Integer.parseInt(scope);
            } else {
                String[] parts = scope.split(":");
                scopeint = Integer.parseInt(parts[0]);
                groupID = parts[1];
            }
        }

        if ((searchID == null) || searchID.equals(StringUtil.EMPTY_STRING)) {
            initialSearch = true;
            if (documentBasket.getClearOnNewSearch()) {
                clearBasket = true;
            }
        } else {
            initialSearch = false;
            searchTags = searchID;
            action = "document";
        }

        if (searchTags != null) {
            tagBroker = new TagBroker();
            List docIDsList = new ArrayList();
            nTotalDocs = tagBroker.count(searchTags, scopeint, pUserId, customerId, groupID, credentials);
            if (nTotalDocs > 0) {
                oPage = tagBroker.getPage(searchTags, scopeint, pUserId, customerId, groupID, index, pagesize, sessionId, Citation.CITATION_FORMAT,
                    credentials);
                numRecs = oPage.docCount();
            }
        }

        if (initialSearch) {
            if (clearBasket) {
                documentBasket.removeAll();
            }
        }

        TagGroup[] groups = null;
        if (pUserId != null) {
            TagGroupBroker groupBroker = new TagGroupBroker();
            groups = groupBroker.getGroups(pUserId, false);
        }

        Scope sc = new Scope(pUserId, scope);

        String backURL = URLEncoder.encode(getBackURL(index, searchTags, scope, database));
        String newSearchURL = URLEncoder.encode(getnsURL(database));

        client.log("context", "search");
        client.log("action", action);
        client.log("type", "Tag");
        client.log("format", "citation");
        client.log("num_recs", Integer.toString(numRecs));
        client.log("doc_index", Integer.toString(index));
        client.log("hits", Integer.toString(nTotalDocs));
        client.setRemoteControl();
        out.write("<PAGE>");
        out.write("<CUSTOMER-ID>" + customerId + "</CUSTOMER-ID>");
        out.write("<HEADER/>");
        out.write("<DBMASK>");
        out.write(database);
        out.write("</DBMASK>");

        out.write("<BACKURL>");
        out.write(backURL);
        out.write("</BACKURL>");
        out.write("<PAGE-NAV>");
        out.write("<NEWSEARCH-NAV>");
        out.write(newSearchURL);
        out.write("</NEWSEARCH-NAV>");
        out.write("<RESULTS-NAV>");
        out.write(backURL);
        out.write("</RESULTS-NAV>");
        out.write("</PAGE-NAV>");

        out.write(strGlobalLinksXML);
        out.write("<FOOTER/>");
        out.write("<SESSION-TABLE/>");
        out.write("<SEARCH/>");
        out.write("<SESSION-ID>");
        out.write(sessionId);
        out.write("</SESSION-ID>");
        out.write("<FULLTEXT>" + isFullTextPresent + "</FULLTEXT>");
        out.write("<RSSLINK>" + isRssLinkPresent + "</RSSLINK>");
        out.write("<LOCALHOLDINGS-CITATION>" + isCitLocalHoldingsPresent + "</LOCALHOLDINGS-CITATION>");
        out.write("<EMAILALERTS-PRESENT>" + isEmailAlertsPresent + "</EMAILALERTS-PRESENT>");
        out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");
        out.write("<PERSONALIZATION>" + personalization + "</PERSONALIZATION>");
        out.write("<PERSON-USER-ID>" + pUserId + "</PERSON-USER-ID>");
        out.write("<SEARCH-ID><![CDATA[" + searchTags + "]]></SEARCH-ID>");
        out.write("<RESULTS-COUNT>" + nTotalDocs + "</RESULTS-COUNT>");
        pageNavXML(out, index, nTotalDocs);
        out.write("<TAGSET>" + nTotalDocs + "</TAGSET>");
        out.write("<TAGS-NAVIGATION-BAR/>");
        out.write("<RESULTS-MANAGER/>");
        out.write("<TAG-SEARCH>" + TagSearchFlag + "</TAG-SEARCH>");
        out.write("<CLEAR-ON-VALUE>" + documentBasket.getClearOnNewSearch() + "</CLEAR-ON-VALUE>");
        out.write("<RESULTS-PER-PAGE>" + pagesize + "</RESULTS-PER-PAGE>");
        out.write("<SEARCH-TYPE>" + searchtype + "</SEARCH-TYPE>");
        out.write("<SCOPE>" + request.getParameter("scope") + "</SCOPE>");
        sc.toXML(out);
        out.write("<GROUP-ID>" + groupID + "</GROUP-ID>");
        if (nTotalDocs > 0) {
            oPage.toXML(out);
        }

        out.write("</PAGE>");
    } catch (Exception e) {
        EVBaseException be = null;
        if (e instanceof EVBaseException) {
            be = (EVBaseException) e;
        } else {
            be = new SearchException(SystemErrorCodes.UNKNOWN_SEARCH_ERROR, e);
        }
        client.setException("true");
        client.setRemoteControl();
        out.write(be.toXML());
        out.flush();

        return;
    }
%>

<%!private String getnsURL(String database) {
        StringBuffer buf = new StringBuffer();
        buf.append("CID=tagsLoginForm&database=");
        buf.append(database);
        return buf.toString();
    }

    private String getBackURL(int count, String searchTags, String scope, String database) {
        StringBuffer backBuf = new StringBuffer();
        backBuf.append("CID=tagSearch&");
        backBuf.append("SEARCHID=");
        backBuf.append(URLEncoder.encode(searchTags));
        backBuf.append("searchtype=TagSearch");
        backBuf.append("&scope=");
        backBuf.append(scope);
        backBuf.append("&database=");
        backBuf.append(database);
        backBuf.append("&COUNT=");
        backBuf.append(Integer.toString(count));
        return backBuf.toString();
    }

    private void pageNavXML(Writer out, int recnum, int totalcount) throws Exception {
        int pageIndex = -1;
        if ((recnum % 25) == 0) {
            pageIndex = (recnum / 25);
        } else {
            pageIndex = (recnum / 25) + 1;
        }
        int currentPage = ((pageIndex - 1) * 25) + 1;
        int previousPage = currentPage - 25;
        if (previousPage > 0) {
            out.write("<PREV-PAGE-ID>");
            out.write(Integer.toString(previousPage));
            out.write("</PREV-PAGE-ID>");
        }
        out.write("<CURR-PAGE-ID>");
        out.write(Integer.toString(currentPage));
        out.write("</CURR-PAGE-ID>");
        System.out.println("Current page:" + currentPage);
        out.write("<RESULTS-COUNT>" + totalcount + "</RESULTS-COUNT>");

        if (currentPage + 24 < totalcount) {
            out.write("<NEXT-PAGE-ID>");
            out.write(Integer.toString(currentPage + 25));
            out.write("</NEXT-PAGE-ID>");
        }
        if (totalcount > 25) {
            out.write("<PAGE-SELECTOR>true</PAGE-SELECTOR>");
        }
    }%>