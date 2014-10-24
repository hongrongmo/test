<!--
 * This page the follwing params as input and generates XML output.
 * This JSP is used when all the records in a page of a particular type of display
 * (citation,detailed or abstract) are to be printed
 *
 * @param java.lang.String.searchid
 * @param java.lang.String.pagesize
 * @param java.lang.String.database
 * @param java.lang.String.displayformat
  * @param java.lang.String.count
-->
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.*"%>
<!--import statements of ei packages.-->
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>

<%@ page  errorPage="/error/errorPage.jsp"%>

<%

			ControllerClient client = null;
			// This variable is used to hold SearchComponent instance
			 //Setting the object refernce.
			DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
			Database databaseObj = null;
			DocID  docID=null;
			SearchControl sc=null;
			Page page1 = null;
		   //This variable for searchResults instance
			SearchResult result=null;
			//This variable for sessionId
			String sessionId  = null;
			String searchid  = null;
			String displayFormat = null;
			int pageSize = 0;
			int pageNo = 0;
			//This variable for database name
			String database = null;
			List DocIDList = null;
			List docIdList = null;
			List handleList = null;
			//variable to hold personalization feature
			boolean  isPersonalizationPresent=false;
			String customizedLogo="";


%>

<%

			// Create a session object using the controllerclient.java
			client = new ControllerClient(request, response);
			UserSession ussession=(UserSession)client.getUserSession();
			//client.updateUserSession(ussession);
			sessionId = ussession.getSessionid();

			ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
			isPersonalizationPresent=clientCustomizer.checkPersonalization();
			customizedLogo=clientCustomizer.getLogo();

			if(request.getParameter("searchid") != null)
			{
				searchid = request.getParameter("searchid");
			}
			if(request.getParameter("pagesize") != null)
			{
				pageSize = Integer.parseInt(request.getParameter("pagesize"));
			}
			if(request.getParameter("count") != null)
			{
				pageNo = Integer.parseInt(request.getParameter("count"));
			}
			if(request.getParameter("database") != null)
			{
				database = request.getParameter("database");
				if(database.equals("Compendex"))
				{
					databaseObj = databaseConfig.getDatabase("cpx");
				}
				else if(database.equals("INSPEC"))
				{
					databaseObj = databaseConfig.getDatabase("INSPEC");
				}
				else if(database.equals("Combined"))
				{
					databaseObj = databaseConfig.getDatabase("COM");
				}
				else if( (database.equals("Scirus")) || (database.equals("SCIRUS")) )
				{
					databaseObj = databaseConfig.getDatabase("SCIRUS");
				}



			}



			if(request.getParameter("displayformat") != null)
			{
				displayFormat = request.getParameter("displayformat");
			}


			try
			{
				sc = databaseObj.newSearchControlInstance();
				result = sc.openSearch(searchid,sessionId,pageSize);

				if(displayFormat.equals("citation"))
				{
					page1 = result.pageAt(pageNo , Citation.CITATION_FORMAT);
				}
				else	if(displayFormat.equals("abstract"))
				{
						page1 = result.pageAt(pageNo , Abstract.ABSTRACT_FORMAT);
				}
				else	if(displayFormat.equals("detailed"))
				{
						page1 = result.pageAt(pageNo , FullDoc.FULLDOC_FORMAT);
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}


		   //Writting out XML
			out.write("<!--BH-->");
			out.write("<PAGE>");
			out.write("<HEADER/>");
			out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
			out.write("</HEADER>");
			out.write("<!--EH-->");

			page1.toXML(out);

			out.println("<!--BF--><FOOTER/><!--EF-->");
			out.write("</PAGE>");
			out.println("<!--END-->");
			out.flush();

 %>