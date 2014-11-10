<%@ page  errorPage="/error/errorPage.jsp"%>
<%@ page session="false" %>

<%@ page  import="org.ei.domain.PublisherBroker"%>
<%@ page  import="org.ei.domain.ChinaLinkBuilder"%>
<%@ page  import="java.util.Iterator"%>
<%@ page  import="java.util.Map"%>
<%@ page  import="org.ei.util.*"%>


<%

PublisherBroker pubBroker = PublisherBroker.getInstance();
ChinaLinkBuilder chinaLinkBuilder = new ChinaLinkBuilder();

chinaLinkBuilder.setAulast(request.getParameter("AULAST"));
chinaLinkBuilder.setAufirst(request.getParameter("AUFIRST"));
chinaLinkBuilder.setAufull(request.getParameter("AUFULL"));
chinaLinkBuilder.setIssn(request.getParameter("ISSN"));
chinaLinkBuilder.setIssn9(request.getParameter("ISSN9"));
chinaLinkBuilder.setCoden(request.getParameter("CODEN"));
chinaLinkBuilder.setTitle(request.getParameter("TITLE"));
chinaLinkBuilder.setStitle(request.getParameter("STITLE"));
chinaLinkBuilder.setAtitle(request.getParameter("ATITLE"));
chinaLinkBuilder.setVolume(request.getParameter("VOLUME"));
chinaLinkBuilder.setIssue(request.getParameter("ISSUE"));
chinaLinkBuilder.setSpage(request.getParameter("SPAGE"));
chinaLinkBuilder.setEpage(request.getParameter("EPAGE"));
chinaLinkBuilder.setCreds(request.getParameter("CREDS"));

String issn 	= chinaLinkBuilder.getIssn();
String pubID = null;

chinaLinkBuilder.buildUrls();

if(issn != null && issn.length() > 0)
{
  pubID = pubBroker.fetchPubID(issn);
}

String strErrorMessage = "<ERROR-MESSAGE>The publisher of this title, is not one of the following:</ERROR-MESSAGE>";

String error = request.getParameter("ERROR");
if((error != null) && error.equalsIgnoreCase("credentials")) 
{
	strErrorMessage= "<ERROR-MESSAGE>The publisher of this title, " + pubID + " is not one of the following :</ERROR-MESSAGE>";
}
if((error != null) && error.equalsIgnoreCase("issn")) 
{
	strErrorMessage= "<ERROR-MESSAGE>We could not find a publisher for this title. There is no ISSN.</ERROR-MESSAGE>";
}

out.write("<PAGE>");
out.write("<HEADER/>");
out.write(strErrorMessage);
out.write("<PUBLISHERS>");

Map<String,String> urls = chinaLinkBuilder.getCredUrls();
Iterator<String> itr = urls.keySet().iterator();
StringUtil util = new StringUtil();
while(itr.hasNext()) 
{
	String strKey = (String) itr.next();

	out.write("<PUBLISHER>");
	out.write("<URL>" + util.replace((String)urls.get(strKey), "&", "&amp;",util.REPLACE_GLOBAL, util.MATCH_CASE_SENSITIVE) + "</URL>");
	out.write("<URL-LABEL>" + strKey + "</URL-LABEL>");
	out.write("</PUBLISHER>");
}

out.write("</PUBLISHERS>");
out.write("</PAGE>");
out.write("<!--END-->");
out.flush();


%>


