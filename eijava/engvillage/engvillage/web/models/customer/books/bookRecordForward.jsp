<%@ page language="java" %><%@ page session="false" %><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.domain.personalization.*" %><%@ page import="org.ei.session.*"%><%@ page import="org.ei.domain.*" %><%@ page import="org.ei.config.*"%><%@ page import="org.ei.data.books.BookPart"%><%@ page import="org.ei.books.*"%><%@ page import="org.ei.parser.base.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.util.GUID"%><%@ page import="java.io.*"%><%@ page import="java.net.*"%><%@ page import="java.util.*"%><%@ page import="java.text.DecimalFormat"%><%@ page errorPage="/error/errorPage.jsp"%><%@ page buffer="20kb"%><%

    FastSearchControl sc = null;
    PageEntry entry = null;
    BookDocument curDoc = null;
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

    String isbn = null;
    String dataFormat = null;
    String customizedLogo = null;

    isbn = request.getParameter("isbn");

    boolean personalization = false;
    boolean isPersonalizationPresent = true;

    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    int pagesize = 1;

    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession = (UserSession)client.getUserSession();
    sessionId = ussession.getID();
    sessionIdObj = ussession.getSessionID();
    pUserId = ussession.getProperty("P_USER_ID");

    User user = ussession.getUser();
    String[] credentials = user.getCartridge();

    Query tQuery = tQuery = new Query(databaseConfig, credentials);
    tQuery.setUserID(pUserId);
    tQuery.setSessionID(sessionId);
    searchID = (new GUID()).toString();
    tQuery.setID(searchID);
    tQuery.setSearchType(Query.TYPE_BOOK);
    tQuery.setDataBase(DatabaseConfig.PAG_MASK);
    tQuery.setAutoStemming("off");

    Map startEndYears = databaseConfig.getStartEndYears(credentials, DatabaseConfig.PAG_MASK);
    String bYear = (String) startEndYears.get(DatabaseConfig.STARTYEAR);
    String eYear = (String) startEndYears.get(DatabaseConfig.ENDYEAR);
    tQuery.setStartYear(bYear);
    tQuery.setEndYear(eYear);

    tQuery.setSearchPhrase(isbn,"BN","","","","","","");

    tQuery.setSearchQueryWriter(new FastQueryWriter());
    tQuery.compile();

    sc = new FastSearchControl();
    result = sc.openSearch(tQuery,
                	   sessionId,
                	   pagesize,
                	   false);
    tQuery.setRecordCount(Integer.toString(result.getHitCount()));

    Searches.saveSearch(tQuery);

    docid = "pag_" + isbn + "_0";
    docindex = "1";
    database = String.valueOf(DatabaseConfig.PAG_MASK);


	pageContext.forward("bookDetail.jsp?DOCINDEX=1&SEARCHID="+searchID+"&database=131072&docid="+docid);


%>