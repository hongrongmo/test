<%@ page session="false" %>

<html>
<body>
<table>
<tr><td colspan="2"><font size="5">Local Holding Test Page</font></td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td>ISBN1</td><td><%=request.getParameter("bn1")%></td></tr>
<tr><td>ISBN2</td><td><%=request.getParameter("bn2")%></td></tr>
<tr><td>YEAR</td><td><%=request.getParameter("year")==null?"":request.getParameter("year")%></td></tr>
<tr><td>ISSN</td><td><%=request.getParameter("sn")%></td></tr>
<tr><td>ISSN9</td><td><%=request.getParameter("issn9")%></td></tr>
<tr><td>EISSN</td><td><%=request.getParameter("eissn")%></td></tr>
<tr><td>AUTHOR FIRST NAME</td><td><%=request.getParameter("aufirst")%></td></tr>
<tr><td>AUTHOR LAST NAME</td><td><%=request.getParameter("aulast")%></td></tr>
<tr><td>SOURCE TITLE</td><td><%=request.getParameter("title")%></td></tr>
<tr><td>ABBREVIATED TITLE</td><td><%=request.getParameter("stitle")%></td></tr>
<tr><td>ARTICLE TITLE</td><td><%=request.getParameter("atitle")%></td></tr>
<tr><td>CONFERENCE TITLE</td><td><%=request.getParameter("ctitle")%></td></tr>
<tr><td>VOLUME</td><td><%=request.getParameter("volume")%></td></tr>
<tr><td>ISSUE</td><td><%=request.getParameter("issue")%></td></tr>
<tr><td>START PAGE</td><td><%=request.getParameter("spage")%></td></tr>
<tr><td>END PAGE</td><td><%=request.getParameter("epage")%></td></tr>
<tr><td>PAGES</td><td><%=request.getParameter("pages")%></td></tr>
<tr><td>ARTICLE NUMBER</td><td><%=request.getParameter("artnum")%></td></tr>
<tr><td>ACCESSION NUMBER</td><td><%=request.getParameter("anum")%></td></tr>
<tr><td>DOI</td><td><%=request.getParameter("doi")%></td></tr>
</table>
</body>
</html>