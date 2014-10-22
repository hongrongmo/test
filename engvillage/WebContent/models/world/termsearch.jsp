<%@ page session="false" %>
<%@ page import="org.ei.thesaurus.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="java.util.*" %>


<%
	String term = request.getParameter("term");
	String num = request.getParameter("num");
	if(term == null)
	{
		term = "";
	}
	
	if(num == null)
	{
		num = "10";
	}

%>

<center>
<form action="termsearch.jsp">

Term:<input type="text" name="term" value="<%=term%>">
Number of results<input type="text" size="3" name="num" value="<%=num%>">
<br>
<input type="submit">
<p>
</center>
<%
		
	if(term.length() > 0)
	{
		long begin = System.currentTimeMillis();
		DatabaseConfig dConfig = DatabaseConfig.getInstance();
		Database database = dConfig.getDatabase("inspec");
		ThesaurusQuery query = new ThesaurusQuery(database, term);
		query.compile();
		ThesaurusSearchControl sc = new ThesaurusSearchControl();
		ThesaurusSearchResult sr = sc.search(query, Integer.parseInt(num));
		ThesaurusPage tpage = sr.pageAt(1);
		out.println("<p>");
		for(int i=0; i<tpage.size();i++)
		{
			ThesaurusRecord trec = tpage.get(i);
			out.println(trec.getRecID().getMainTerm()+"<br>");
		}
		
		long end = System.currentTimeMillis();
	
		out.println("<br>"+Long.toString(end-begin));
	}
%>