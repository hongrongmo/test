<%@ page session="false" %>
<%@ page import="org.ei.thesaurus.*" %>
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
<form action="termsuggester.jsp">

Term:<input type="text" name="term" value="<%=term%>">
Max # of Suggestion<input type="text" size="3" name="num" value="<%=num%>">
<br>
<input type="submit">
<p>
</center>
<%

	long begin = System.currentTimeMillis();
	
	if(term.length() > 0)
	{
		ThesaurusRecordID recID = null;
		ThesaurusRecordBroker broker = new ThesaurusRecordBroker("inspec");
		ThesaurusRecord trec  = broker.getRecordFor(term);
		if(trec == null)
		{
		
			 ThesaurusPage tpage = broker.getSuggestions(term.toLowerCase(),
								         Integer.parseInt(num));
			out.println("There was no match for: <b>"+ term+"</b>");
			out.println("<br>Are you looking for any of the terms below?<p>");
			for(int i=0; i<tpage.size(); i++)
			{
				ThesaurusRecord r =  tpage.get(i);
				out.println(r.getMainTerm()+"<br>");
			}
			
		}
		else
		{
			out.println("Found exact match: <b>"+ trec.getMainTerm()+"</b>");	
		}			              
	}
	long end = System.currentTimeMillis();
	
	out.println("<br>"+Long.toString(end-begin));
%>