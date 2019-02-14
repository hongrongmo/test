<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@page import="java.util.ArrayList" %>
    <%@page import="java.util.HashMap" %>
    <%@page import="java.util.Map" %>
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DBA Operations</title>

<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/tables.css"/>
   
   <style type="text/css">
   html,
body {
	margin:0;
	padding:0;
	height:100%;
}
#wrapper {
	min-height:100%;
	position:relative;
}
#header {
	background:#ededed;
	padding:10px;
}

#sidebar {
    position:absolute;
    top:0; bottom:0; left:0;
    width:180px;
    background:#148C75;
    margin-top:100px;
}



#content {
	padding-bottom:80px; /* Height of the footer element */
	padding-left: 15px;
}
#footer {
	background:#ffab62;
	width:100%;
	height:40px;
	position:absolute;
	bottom:0;
	left:0;
}
 
 #mainawslinks li
 {
  padding-bottom: 4px;
  padding-top: 6px;
  font-weight: bold;
  color: #2F4F4F;
  font-size: 14px;
 }
 
 #awsinnerlink li
 {
 	font-size: 12px; 
 	font-weight: bold;
    margin-bottom: 6px;
 
 }  
   </style>
   
</head>


<body>

 <%if(session.getAttribute("username") !=null ){ %> 
 
<div id="wrapper">
		<%@ include file="/views/includes/header_for_dayton.jsp" %>
		<!-- <div id="sidebar">
		</div> -->
		
		
<div id="content">
		
<h1>DBA Operations</h1>
<p>
This page allows you to Check Opened Cursors for EIA RDS:

<br>
<br>
	<a class="rplink" href="${pageContext.servletContext.contextPath}/EIAOpenedCusrors?op=summary" name="cursorsummaryinfo">EIA_Open_Cursor_Count</a>
	<br>
	<a class="rplink" href="${pageContext.servletContext.contextPath}/EIAOpenedCusrors?op=detailed" name="cursordetailedinfo">EIA_Open_Cursor_Details</a>
	<br>
	<a class="rplink" href="#" onclick='addInputBox()'>Cusrsor's Sql Queries Info(using SID)</a>
	<br>
	
	<div id="customCursorInfo" style="margin-top: 6px;"> </div>
	

<!-- Curosr Info Table -->	
<%
if(session.getAttribute("DETAILEDCURSORINFO") !=null){
%>

<br>

<div id="summaryCursorInfo" style="margin-top: 6px;"> </div>
<div id="dynamicQuery" style="margin-top: 6px;"> </div>  
	
	
<div id="detailedcursorinfo">
 <table id="session" style="width: 95%">

   <thead>
   <tr>
  	 <%if(session.getAttribute("COLUMNNAMES") !=null){%>
  		 <c:forEach items="${COLUMNNAMES}" var="item">
   			<c:if test="${item ne null }">
   				<th>${item}</th>
   			</c:if>
		</c:forEach>
		<%} %>			
   </tr>
  </thead>
  
   
  <tbody>
   <% int i = 0; %>
    <c:forEach items="${DETAILEDCURSORINFO}" var="row">
       <tr>
       <c:forEach items="${COLUMNNAMES}" var="item">
				<c:choose>
					<c:when test="${row[item] ne null}">
						<td>${row[item]}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
		</c:forEach>
	</tr>	
   </c:forEach>
  </tbody>
</table>

<%} %>

</div>
<br>




</div>



<div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.

</p>
</div>


	</div>
<script type="text/javascript">
function addInputBox() { 

	var ni = document.getElementById('customCursorInfo'); 


	var newdiv = document.createElement('div'); 

	var divIdName = 'customCursorInfodiv'; 

	newdiv.setAttribute('id',divIdName); 

	newdiv.innerHTML = "<form name=\"customCursorInfo\" class=\"customCursorInfo\" action=\"${pageContext.servletContext.contextPath}/EIAOpenedCusrors?op=custom\" method=\"post\">"+
						"<b>Enter Oracle SID: </b><input type=\"text\" name=\"sid\" id=\"sid\" /> "+
						"<input name=\"submit\" type=\"submit\" value=\"Get Cursor Info\" /></form>";


	ni.appendChild(newdiv); 
	
}



</script>
<%} %>
</body>
</html>