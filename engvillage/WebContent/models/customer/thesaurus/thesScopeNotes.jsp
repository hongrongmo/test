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
    else if(dbName.equals("2"))
    {
        databaseID = "ins";
    }
    else if(dbName.equals("2048"))
    {
        databaseID = "ept";
    }
    else if(dbName.equals("1024"))
    {
         databaseID = "elt";
    }
    else if(dbName.equals("8192"))
    {
        databaseID = "geo";
    }    
    else if(dbName.equals("2097152"))
    {
        databaseID = "grf";
    }


    if(term.length() > 0)
    {
	if(term.indexOf("*")>0)
	{
		term = term.replaceAll("\\*$","");
	}
	ThesaurusRecord trec=null;
	try
	{
		
        Database database = (DatabaseConfig.getInstance()).getDatabase(databaseID);
     
        ThesaurusRecordBroker broker = new ThesaurusRecordBroker(database);
       
        ThesaurusRecordID recID = new ThesaurusRecordID(term, database);
     
        ThesaurusRecordID[] recIDs = new ThesaurusRecordID[1];
       
        recIDs[0] = recID;
       
        ThesaurusPage tpage = broker.buildPage(recIDs, 0, 0);
       
        trec = tpage.get(0);
       
        out.print("<TREC>");
        trec.accept(new ThesNotesXMLVisitor(out));      
        out.print("</TREC>");
       
        
	 }
	  catch (Exception cpe)
	  {
		cpe.printStackTrace();
	  }   

    }


%>



