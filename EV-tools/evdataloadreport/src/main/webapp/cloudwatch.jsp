<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%@page import="java.util.ArrayList" %>
     <%@page import="java.util.HashMap" %>
     <%@page import="org.aws.DescribeEc2" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AWS CloudWatch</title>

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
	padding-left: 200px;
}
#footer {
	background:#ffab62;
	width:100%;
	height:40px;
	position:absolute;
	bottom:0;
	left:0;
}
   </style>

</head>
<body>

<%if(session.getAttribute("username") !=null && session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").toString().equalsIgnoreCase("admin"))
	{%>	
	
<div id="wrapper">
		<%@ include file="/views/includes/header.jsp" %>
		<div id="sidebar">
			<%@ include file="/views/includes/navlinks.jsp" %>
		</div>
		
		
<div id="content">
<h1>Aws CloudWatch</h1>

<br>
<br>
<p>
To view Cloudwatch Metrics for a namespace (RDS/EC2), select the instance ID & metric Name first:</p>
<br>
<form name="cloudwatch" id="cloudwatch" action="${pageContext.servletContext.contextPath}/CloudWatch" method="post">

<select name="namespace" id="namespace" onchange="submit()" style="width: 180px;">
<option value="default">Select NameSpace</option>
<option value="AWS/RDS">RDS</option>
<option value="AWS/EC2">EC2</option>
</select> 
<%if(session.getAttribute("INSTANCEIDS") !=null){ %>

<!-- Instances -->
<select name="instances" id="instances" onchange="submit()">
<c:choose>
	<c:when test="${empty INSTANCEIDS and empty METRICS}">
		<option>Please select namespace</option>
	</c:when>
	<c:otherwise>
		<option value="default">Please select Instance ID</option>
		 <c:forEach items="${INSTANCEIDS}" var="option">
        <option value="${option.key}" ${param.instances == option.key ? 'selected' : ''}>${option.value}</option>
    </c:forEach>
	</c:otherwise>
</c:choose>
 </select> 
 <%} %>
 
 
 <!-- Metrics -->
 <%if(session.getAttribute("METRICS") !=null){ %>
 <select name="metrics" id="metrics" onchange="submit()">
<c:choose>
	<c:when test="${empty METRICS and empty INSTANCEIDS}">
		<option>Please select Instance ID</option>
	</c:when>
	<c:otherwise>
		<option value="default">Please select Metric</option>
		 <c:forEach items="${METRICS}" var="option">
        <option value="${option.key}" ${param.metrics == option.key ? 'selected' : ''}>${option.value}</option>
    </c:forEach>
	</c:otherwise>
</c:choose>
 </select> 
 <%}%>


<!-- Statistics -->

<%if(session.getAttribute("STATISTICS") !=null){ %>

<select name="statistic" id="statistic" onchange="submit()">
<c:choose>
	<c:when test="${empty STATISTICS and empty METRICS}">
		<option>Please select Metric</option>
	</c:when>
	<c:otherwise>
		<option value="default">Please select Statistic</option>
		 <c:forEach items="${STATISTICS}" var="option">
        <option value="${option.key}" ${param.metrics == option.key ? 'selected' : ''}>${option.value}</option>
    </c:forEach>
	</c:otherwise>
</c:choose>
 </select> 
 <%} %>
 
 

</form>



<br>
<%if(session.getAttribute("METRICDATA") !=null && ((HashMap<String,String>)session.getAttribute("METRICDATA")).size() >0){ %>
<table id="metricinfo">

<caption>Metrics Info for: ${SELECTEDNAMESPACE}: ${SELECTEDINSTANCEID} : ${SELECTEDSTATISTIC} - ${SELECTEDMETRICNAME} </caption>
<thead>
   <tr>
      <th>DATE/TIME</th>
      <th>VALUE</th>
    </tr>
  </thead>
  
  <tbody>
<%
HashMap<String,String> metricData = (HashMap<String,String>)session.getAttribute("METRICDATA");
   for(String Key: metricData.keySet()) {%>
       <tr>
			<td><%=Key %></td>
			<td><%=metricData.get(Key) %></td>
	   </tr>
	   <%} %>				
    </tbody>
</table>

<%}%>


</div>



<div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.

</p>
</div>


</div>


<!-- keep drodownlist at selected value -->
<script type="text/javascript">

<% if(session.getAttribute("NAMESPACEINDEX")==null)
{%>
	document.getElementById("namespace").selectedIndex=0;
<%}
else{%>
document.getElementById("namespace").selectedIndex=<%=session.getAttribute("NAMESPACEINDEX")%>;
<%}%>


<% if(session.getAttribute("STATISTICSELECTEDINDEX")==null)
{%>
	document.getElementById("statistic").selectedIndex=0;
<%}
else{%>
document.getElementById("statistic").selectedIndex=<%=session.getAttribute("STATISTICSELECTEDINDEX")%>;
<%}%>



</script>
<%} %>
</body>
</html>