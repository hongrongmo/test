<%@ page session="false" %>
<%@ page import="java.net.*" %>

<%@ page errorPage="/error/errorPage.jsp"%>
<%

	String thesAction = request.getParameter("thesAction");
	String term = request.getParameter("term");
	String database = request.getParameter("database");
	String termEncode = null;
	if(term != null)
	{
		termEncode = URLEncoder.encode(term);
	}
	
	String searchid = request.getParameter("searchid");

%>



<DOC>

	<%
		if(searchid == null)
		{
	%>



	<LINK1>CID=<%=thesAction%>&amp;term=<%=termEncode%>&amp;database=<%=database%></LINK1>
	<LINK2>CID=thesClipBoard&amp;database=<%=database%></LINK2>


	<%
		}
		else
		{
		
	%>

	<LINK1>CID=thesTermSearch&amp;database=<%=database%></LINK1>
	<LINK2>CID=thesClipBoard&amp;database=<%=database%>&amp;searchid=<%=searchid%></LINK2>

	<%
		}	
	
	%>


</DOC>



