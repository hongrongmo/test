<%@ page language="java"%>
<%@ page session="false"%>
<%@ page contentType="text/xml"%>
<!--
	This page the follwing params as input and generates XML output.
	@param java.lang.String.database
-->
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="java.util.*"%>

<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.Searches"%>

<%

	int customizedStartYear = -1;
	int customizedEndYear = -1;
	String database  = request.getParameter("database");
	String searchID = request.getParameter("searchID");
	String recentXmlQueryString = null;
	StringBuffer strBufThesXML = new StringBuffer();
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	User user = ussession.getUser();

	if(searchID != null)
	{

		String sessionId=ussession.getID();

//		recentXmlQueryString = Searches.getXMLSearch(searchID);
//		Query tQuery = new Query(new FastQueryWriter(),
//            					 DatabaseConfig.getInstance(),
//            					 user.getCartridge(),
//            					 recentXmlQueryString);

        Query tQuery = Searches.getSearch(searchID);
        tQuery.setSearchQueryWriter(new FastQueryWriter());
        tQuery.setDatabaseConfig(DatabaseConfig.getInstance());
        tQuery.setCredentials(user.getCartridge());
        recentXmlQueryString = tQuery.toXMLString();

		// creating Thesaurus XML for output
		BooleanQuery bq  = tQuery.getParseTree();
		String strThesTerm = "";

		ExactTermGatherer etg = new ThesaurusCVTermGatherer();
		List lstThesTerms = etg.gatherExactTerms(bq);
		Iterator itrThesTerms = lstThesTerms.iterator();
		String firstCon = etg.getFirstConnector();

		strBufThesXML.append("<CVS CONNECTOR='"+firstCon+"'>");
		while(itrThesTerms.hasNext())
		{
			strThesTerm = (String) itrThesTerms.next();
			strBufThesXML.append("<CV><![CDATA[");
			strBufThesXML.append(strThesTerm);
			strBufThesXML.append("]]></CV>");
		}
		strBufThesXML.append("</CVS>");

	}


	if(database == null)
	{
		database ="1";
	}

%>

<CLIPBOARD>
<CONTEXT>T</CONTEXT>
<%
	if(recentXmlQueryString != null)
	{
		out.write(strBufThesXML.toString());
		out.write(recentXmlQueryString);
	}
  else
  {
		out.write("<SESSION-DATA>");
		//out.write("<START-YEAR>1990</START-YEAR>");
		out.write("<END-YEAR>" + String.valueOf(SearchForm.ENDYEAR) + "</END-YEAR>");
		out.write("</SESSION-DATA>");
	}

%>
<%@ include file="../queryForm.jsp"%>
</CLIPBOARD>