<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>

<!--import statements of ei packages.-->

<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.data.DataCleaner"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>

<%@ page errorPage="/error/errorPage.jsp"%>
<!-- Increasing the buffer size to 20kb -->
<%@ page buffer="20kb"%>
<%
    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession = (UserSession) client.getUserSession();
    String sessionId = ussession.getID();

    User user = ussession.getUser();

    Query queryObject = null;
    try
    {
        String searchID = request.getParameter("SEARCHID");

        if((searchID != null) || !(searchID.equals(StringUtil.EMPTY_STRING)))
        {
            // SearchID is not null
            // run from history - Saved Search, Email Alert, Session History
            //log(" HISTORY " + searchID);
            // set these to false - email alert Alert or run from Saved Searches will set as needed

            queryObject = Searches.getSearch(searchID);
        }


        out.write("<PAGE>");
        out.write(queryObject.toXMLString());

        String field = request.getParameter("field");
        if(field == null)
        {
          field = "db";
        }
        String navfield = field.concat("nav");

        NavigatorCache navcache = new NavigatorCache(sessionId);
        ResultNavigator navigators  = navcache.getFromCache(searchID);
        EiNavigator anav = navigators.getNavigatorByName(navfield);
        if(anav != null)
        {
          int count = ResultsState.DEFAULT_STATE_COUNT;
          ResultsState resultsState = queryObject.getResultsState();
          Map rs = resultsState.getStateMap();
          if((rs != null) && (rs.get(field) != null))
          {
            count = ((Integer) rs.get(field)).intValue();
          }
          else
          {
            log(" rs is null");
          }

          String navstring = anav.toString(count);

          out.write("<ANALYZE-DATA><![CDATA[" + StringUtil.zipText(navstring) + "]]></ANALYZE-DATA>");
          out.write("<CURRENT-COUNT>"+count+"</CURRENT-COUNT>");
          out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
        }
        out.write("<FOOTER/>");
        out.write("</PAGE>");
        out.flush();

    }
    finally
    {

    }
%>
