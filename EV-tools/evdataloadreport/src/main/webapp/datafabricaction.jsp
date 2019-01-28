<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>EV Data Fabrication</title>
	<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
     

    
    
</head>

<body>
	<%@ include file="views/includes/header.jsp" %>
  	<div class="wrapper">

<!--  -->

		<div id="sidebar">
			<%@ include file="/views/includes/navlinks.jsp" %>
		</div>
		
<%if(session.getAttribute("username") !=null)
	{%> 		
		<div id="content">

<table>
<tr>
<td><h1>EV Data Frabrication</h1></td>
</tr>
</table>


	<ul>
		<li> <a class="rplink" href="report.jsp" style="font-size: 12px; font-weight: bold;">Weekly Data Processing Report</a></li>
		
		<!-- show dir & scripts for authorized user only -->
		<%if(session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin")){ %>  
		<li> <a class="rplink" href="dataloadscripts.jsp" style="font-size: 12px; font-weight: bold;">Dataloading Directory/Scripts</a></li>
		<li> <a class="rplink" href="sourcevendors.jsp" style="font-size: 12px; font-weight: bold;">Source Files Transfer/download</a></li>
		<li> <a class="rplink" href="weeklydataload.jsp" style="font-size: 12px; font-weight: bold;">Weekly Data Processing</a></li>
		<li> <a class="rplink" href="adhocdataload.jsp" style="font-size: 12px; font-weight: bold;">Monthly/Adhoc Data Processing</a></li>
		
		<!-- <li> <a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Monthly Data Loading</a></li>
		<li> <a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Adhoc New Data Loading</a></li>
		<li> <a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Adhoc Data Corrections</a></li> -->
		<%} %>
		
	</ul>
	



  
  
<%-- <%

out.println("TEST2: " + session.getAttribute("weeknum"));

/* if(session.getAttribute("weeknum") !=null && session.getAttribute("weeknum").toString().length() >0)
{
	 
} */


%> --%>

</div>



<div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
	
	<!-- JS works, but replacing it with java instead bc link not work -->
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/copyright.js"></script> --%>
	
</p>
</div>
<!--  -->





</div>


<script type="text/javascript"> document.getElementById("weeknumber").selectedIndex=<%=session.getAttribute("weekindex")%>;</script>

<%} %> 

</body>
</html>