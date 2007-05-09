<%@ page  import="java.net.URLEncoder"%>
<%@ page session="false" %>

<%
	String term1=request.getParameter("term1");
	String database=request.getParameter("database");
	String serverName=request.getServerName();
	int serverPort = request.getServerPort();
	if (serverPort!=80)
	{
		serverName=serverName+":"+serverPort;
	}
%>
<html>
<head>
<title>RSS</title>
<script language="JavaScript" type="text/javascript">
	window.top.document.title = window.document.title;
</script>
	<script type="text/javascript" language="javascript" src="/engresources/js/StylesheetLinks.js"></script>
</head>

<body topmargin="0" marginwidth="0" marginheight="0">

<center>
<table border="0" cellspacing="0" cellpadding="0" width="99%">
	<tr><td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td><td align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td></tr>
	<tr><td height="2" colspan="2"><img src="/engresources/images/s.gif" border="0" height="2"/></td></tr>
	<tr><td height="2" bgcolor="#3173B5" colspan="2"><img src="/engresources/images/s.gif" border="0" height="2"/></td></tr>
</table>
</center>


<center>
<table width="99%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td height="20"><img src="/engresources/images/s.gif" height="20"/></td>
	</tr>
	
	<% if ((database!=null && !database.equals("")) && (term1!=null && !term1.equals(""))) { %>
	
		<tr>
			<td valign="top">http://<%=serverName%>/controller/servlet/Controller?CID=openRSS&SYSTEM_PT=t&database=<%=database%>&term1=<%=URLEncoder.encode(term1)%></td>
		</tr>
		
		<tr>
			<td height="20"><img src="/engresources/images/s.gif" height="20"/></td>
		</tr>
		
		<tr>
			<td valign="top" align="middle"><a class="MedBlackText"><b>This is the RSS link for your search</b></a></td>
		</tr>
		
		<tr>
			<td height="20"><img src="/engresources/images/s.gif" height="20"/></td>
		</tr>
		
		<tr>
			<td valign="top" align="middle"><a class="MedBlackText"><b>You may copy it to your RSS feeder.</b></a></td>
		</tr>
	<%} else { %>
		
		<tr><td valign="top"><a class="MedBlackText">Either database or search term is missed</a></td></tr>
	<% } %>
</table>
</center>
	
</body>
</html>