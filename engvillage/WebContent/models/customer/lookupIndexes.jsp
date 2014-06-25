<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->

<%@ page  import=" java.util.*"%>
<%@ page  import=" java.sql.*"%>

<!--import statements of ei packages.-->
<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.session.*"%>
<%@ page  import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page  import="org.ei.config.*"%>
<%@ page  import="org.ei.util.*"%>


<%@ page  errorPage="/error/errorPage.jsp"%>
<!--Setting page buffersize to 20k-->
<%@ page  buffer="20kb"%>

<!--
 This jsp generates xml for lookupindexes.

-->

<%
   String sessionId=null;
   String sesID = null;
   String searchfield =null;
   String count=null;
   String searchWord=null;
   String searchType="";
   String lookupSearchID = null;
   LookupIndexes lookupIndexes=null;
   int index=0;


  // This variable is used to hold ControllerClient instance
  ControllerClient client = new ControllerClient(request, response);


  UserSession ussession=(UserSession)client.getUserSession();
  IEVWebUser user = ussession.getUser();


  sessionId=ussession.getSessionID().toString();
  sesID = ussession.getID();

  if(request.getParameter("searchWord")!=null)
  {
    searchWord=request.getParameter("searchWord").trim();
  }
  else
  {
    searchWord="A";
  }

if(request.getParameter("lookup")!=null)
{
    searchfield=request.getParameter("lookup");
}

if(request.getParameter("COUNT")!=null)
{
     count=request.getParameter("COUNT");
}


int databaseMask = -1;



  databaseMask=Integer.parseInt(request.getParameter("database"));



  if(request.getParameter("searchtype")!=null)
  {
       searchType=request.getParameter("searchtype");
  }

  boolean newQuery = false;

  if(request.getParameter("lookupSearchID")!=null)
  {
        lookupSearchID=request.getParameter("lookupSearchID");
  }
  else
  {
    lookupSearchID = new GUID().toString();
    newQuery = true;
  }


  if(count!=null)
  {
    index=Integer.parseInt(count);
  }
  else
  {
    index=1;
  }

  DatabaseConfig dconfig = DatabaseConfig.getInstance();
     lookupIndexes= new LookupIndexes(sesID,
                          100,
                          dconfig);

     // Logging
     client.log("context", "search");
     client.log("action", "lookup");
     client.log("index", (new Integer(index)).toString());
     client.log("selected", searchfield);
     client.log("type", searchType);
     client.log("query_string", searchWord);
     client.log("db_name", new Integer(databaseMask).toString());
     client.setRemoteControl();


     //writing xml out
     out.write("<PAGE>");
     out.write("<SESSION-ID>"+sessionId+"</SESSION-ID>");
     out.write("<SEARCHWORD>"+searchWord+"</SEARCHWORD>");
     out.write("<LOOKUP-SEARCHID>"+lookupSearchID+"</LOOKUP-SEARCHID>");
     out.write("<SELECTED-LOOKUP>"+searchfield+"</SELECTED-LOOKUP>");

     out.write("<DATABASE>"+databaseMask+"</DATABASE>");
     out.write("<SEARCH-TYPE>"+searchType+"</SEARCH-TYPE>");
     out.write("<PREV-PAGE-COUNT>"+(index-1)+"</PREV-PAGE-COUNT>");
     out.write("<CURR-PAGE-COUNT>"+index+"</CURR-PAGE-COUNT>");
     out.write("<NEXT-PAGE-COUNT>"+(index+1)+"</NEXT-PAGE-COUNT>");



     int upgradeMask = dconfig.doUpgrade(databaseMask,
                         user.getCartridge());

     lookupIndexes.getXML(index,
                  lookupSearchID,
                  searchWord,
                  searchfield,
                  upgradeMask,
                  out);
     out.write("</PAGE>");

%>