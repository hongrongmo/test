<%@ page session="false" %>
<%@ page import="org.ei.thesaurus.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
    String term = request.getParameter("term");
    String dbName = request.getParameter("database");
    String databaseID = null;

    if(dbName.equals("1"))
    {
        databaseID = "cpx";
    }
    else
    {
        databaseID = "ins";
    }


    if(term.length() > 0)
    {

        Database database = (DatabaseConfig.getInstance()).getDatabase(databaseID);
        ThesaurusRecordBroker broker = new ThesaurusRecordBroker(database);
        ThesaurusRecordID recID = new ThesaurusRecordID(term, database);
        ThesaurusRecordID[] recIDs = new ThesaurusRecordID[1];
        recIDs[0] = recID;
        ThesaurusPage tpage = broker.buildPage(recIDs, 0, 0);

        ThesaurusRecord trec = tpage.get(0);

        out.print("<TREC>");
        trec.accept(new ThesNotesXMLVisitor(out));
        out.print("</TREC>");

    }


%>



