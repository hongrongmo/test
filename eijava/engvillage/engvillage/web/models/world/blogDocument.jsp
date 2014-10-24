<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>

<!--import statements of ei packages.-->

<%@ page import="org.ei.domain.*" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<%
    EIDoc entry =null;
    Database databaseObj=null;
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    String database=null;
    String dataFormat = Abstract.ABSTRACT_FORMAT;
    String mid = null;
    String sessionId = null;
%>

<%


/*******************************************************************/

 List entryList 	 = new ArrayList();
 mid 			 = request.getParameter("MID");
 database 		 = request.getParameter("DATABASE");
 databaseObj		 = databaseConfig.getDatabase(database);
 DocID id 		 = new DocID(0,mid,databaseObj);
 entryList.add(id);
 DocumentBuilder builder = new MultiDatabaseDocBuilder();
 List docList 		 = builder.buildPage(entryList,dataFormat);
 entry 			 = (EIDoc)docList.get(0);

/*****************************************************************/

 out.write("<PAGE><LINK>false</LINK>");
 out.write("<HEADER/>");
 out.write("<DBMASK>");
 out.write(database);
 out.write("</DBMASK>");
 out.write("<DATABASE>"+database+"</DATABASE>");
 out.write("<PAGE-RESULTS><PAGE-ENTRY>");
 entry.toXML(out);
 out.write("</PAGE-ENTRY></PAGE-RESULTS>");
 out.write("</PAGE>");
 out.flush();


%>
