<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>EV Operations-NYC</title>

<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
   
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
    width:150px;
    background:#148C75;
    margin-top:100px;
}



#content {
	padding-bottom:80px; /* Height of the footer element */
	padding-left: 170px;
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

<body>

<div id="wrapper">
		<%@ include file="/views/includes/header.jsp" %>
		<!-- <div id="header"></div> -->
		<div id="sidebar"></div>
		<div id="content">

<h1>EV Operations (NYC)</h1>

	<ul>
		<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold; margin-bottom: 4px;">3rd Level Customer Support</a></li>
			
			<li><b>Usage Reports:</b>   </br>
			<ul class="innerlist">
				<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold; margin-bottom: 4px;">Emetrics Interface</a></li>
				<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold; margin-bottom: 4px;">Daily Usage Reports</a></li>
				<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold; margin-bottom: 4px;">Adhoc Usage Reports</a></li>
			</ul>
			
				<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold; margin-bottom: 4px;">Email Alerts</a></li>
			
	</ul>



</div>



<div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.

</p>
</div>


</div>



</body>



</body>
</html>