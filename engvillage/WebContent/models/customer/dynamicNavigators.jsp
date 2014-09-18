<%@ page language="java"%><%@ page import="java.util.*"%><%@ page
	import="org.ei.domain.navigators.*"%><%@ page import="org.ei.domain.*"%><%@ page
	import="org.engvillage.biz.controller.ControllerClient"%><%@ page
	import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page
	import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page
	import="org.ei.domain.Searches"%><%@ page
	import="org.ei.tags.TagBroker"%><%@ page import="org.ei.tags.Tag"%><%@ page
	import="org.ei.domain.personalization.GlobalLinks"%><%@ page
	import="org.ei.domain.personalization.SavedSearches"%><%@ page
	errorPage="/error/errorPage.jsp"%><%@ page
	import="org.ei.parser.base.*"%>
<%!DatabaseConfig databaseConfig = null;
    int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);

    public void jspInit() {
        try {
            databaseConfig = DatabaseConfig.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            log("jspInit Error: " + e.getMessage());
        }
    }%>
<%
    ControllerClient client = null;
    Query queryObject = null;
    SearchResult result = null;
    ResultNavigator nav = null;
    SearchControl sc = new FastSearchControl();
    UserSession ussession = null;
    String sessionId = null;
    String[] credentials = new String[] { "CPX", "UPO", "CRC", "SOL", "INS", "DSS", "ESN", "SCI", "EMS", "EEV", "OJP", "SPI", "NTI", "THS", "C84",
        "IBF", "UPA", "EUP", "CBN", "GEO", "PCH", "CHM", "ELT", "EPT", "GSP", "LHC", "EZY", "GAR", "ELE", "CHE", "MAT", "COM", "CIV", "SEC", "BPE",
        "ZBF", "CSY2004", "a", "czl", "frl", "he", "ng", "ocl", "prp", "ps_l", "ts", "tl", "czp", "cp", "ets", "frp", "ocp", "pp", "psp", "ps_p",
        "pol", "tp", "PAG", "GRF", "GRF", "GRF" };

    client = new ControllerClient(request, response);

    ussession = (UserSession) client.getUserSession();
    if (ussession != null) {
        sessionId = ussession.getSessionid();
        credentials = ussession.getCartridge();
    }

    String searchId = request.getParameter("searchId");
    /*  if(searchId == null)
     {
     searchId = "1474e45117d09b01c1M7ff814536192173";
     } */

    if (searchId != null && !searchId.equals("") && !searchId.equals("undefined")) {
        log("dynamicNavigators => searchId: " + searchId + ", sessionId " + sessionId);

        try {
            queryObject = Searches.getSearch(searchId);
            if (queryObject != null) {
                queryObject.setSearchQueryWriter(new FastQueryWriter());
                queryObject.setDatabaseConfig(databaseConfig);
                queryObject.setCredentials(credentials);

                sc.setUseNavigators(true);
                result = sc.openSearch(queryObject, sessionId, 1, false);

                nav = sc.getNavigator();
            }
        } catch (Exception e) {
            log("exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    if (nav != null) {
        out.write(nav.toXML(queryObject.getResultsState()));
    } else {
        out.write("<NAVIGATORS>");
        /*
        out.write("<NAVIGATOR ");
        out.write(" NAME=\"Test\"");
        out.write(" LABEL=\"Test\"");
        out.write(" INCLUDEALL=\"\"");
        out.write(" FIELD=\"\">");
        out.write("<MODIFIER COUNT=\"12\">");
        out.write("<VALUE><![CDATA[One]]></VALUE>");
        out.write("<LABEL><![CDATA[One]]></LABEL>");
        out.write("</MODIFIER>");
        out.write("</NAVIGATOR>");
         */
        out.write("</NAVIGATORS>");
    }
    out.close();
%>