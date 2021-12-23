<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="refresh" content="60">

<title>DB Connections</title>

	<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
     <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/tables.css"/>
    
   
   <style type="text/css">
   
   ul li{
   margin: 0 0 0 0;
   }

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
	padding-bottom:40px; /* Height of the footer element */
	padding-left: 200px;
}
#footer {
	background:#ffab62;
	width:100%;
	height:40px;
	position:absolute;
	bottom:0;
	left:0;
	text-indent: 6px;
}

   </style>
   
</head>


<body>

<div id="wrapper">

		<%@ include file="/views/includes/header.jsp" %>
		
		<div id="sidebar">
			<%@ include file="/views/includes/navlinks.jsp" %>
		</div>
	
		<div id="content">
		
		<h1>DB Connections</h1>
	
	<div id="totalcon">	
		<%if(session.getAttribute("TOTALDBCONNECTIONS") !=null && session.getAttribute("TOTALDBCONNECTIONS").toString().length() >0){ %>
		</br>
	
			Total DB Connections for <span style="font-family: cursive; font-size: 14px; color: #191970; font-weight: bold;"><%=session.getAttribute("RDSNAME")%> :</span> <span style="color:#800000; font-size: 14px; font-weight: bold;"><%=session.getAttribute("TOTALDBCONNECTIONS") %></span>
		<%} %>
		
	</div>
	
	</br>
	<div id="conbyuser">
		<%if(session.getAttribute("CONNECTIONCOUNTBYUSER") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("CONNECTIONCOUNTBYUSER")).size() >0){ %>
		<table id="dbconnections">
 				 <caption>All Connections Count By UserName</caption>
  
  				 <thead>
   					<tr>
      					<th>USERNAME</th>
      					<th>COUNT</th>
    				</tr>
  				</thead>
 
  			<tbody>
  					<c:forEach items="${CONNECTIONCOUNTBYUSER}" var="item">
  						<tr>
  							<c:if test="${item.USERNAME ne null}"><td>${item.USERNAME}</td></c:if>
  							<c:if test="${item.TOTAL ne null}"><td>${item.TOTAL}</td></c:if>
  		  				</tr>
  				</c:forEach>
  			</tbody>
 		</table>
   
		<%} %>
	</div>
</div>
	
	
	
	
		
<div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
	
	<!-- JS works, but replacing it with java instead bc link not work -->
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/copyright.js"></script>
	
</p>
</div> 		
</div>				
				
</body>
</html>