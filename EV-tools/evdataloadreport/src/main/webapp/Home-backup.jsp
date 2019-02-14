<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Home Backup</title>
	<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
    <link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
	<link rel="icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
    <!-- <style>
    	table {border: 0px solid #ababab; float:none}
    	table td {border: 0px }
    </style> -->
    
</head>

<body>
	<%@ include file="views/includes/header.jsp" %>
  	<div class="maincontainer">
  		<div tiles:fragment="content">
  			<div class="innercontainer">
  				<c:if test="${not empty param.error}">
  				   <br />
				   <div class="paddingL10">
				   	 <img style="vertical-align:bottom" src="${pageContext.servletContext.contextPath}/static/images/red_warning.gif">
				   	 <b>&nbsp;&nbsp;Invalid username and password.</b>
				   </div>
				</c:if>
				<c:if test="${not empty param.logout}">
				   <br />
				   <span id="messagecontainer">You have been logged out.</span>
				   <br />
				</c:if>
				<br />
				</div>

<!--  -->


<!-- <div id="nav">


</div> -->

<div id="section">
<h1>EV Weekly DataLoading Report</h1>
<p>
This page allows you to view weekly dataloading reports for all of the Engineering Village Datasets; including (corrections & Monthly GRF IP):
	<ul>
		<li> Compendex </li>
		<li> Inspec </li>
		<li> NTIS </li>
		<li> PaperChem </li>
		<li> Chimica </li>
		<li> CBNB </li>
		<li> EncompassLit </li>
		<li> EncompassPat </li>
		<li> GeoBase </li>
		<li> GeoRef </li>
		<li> US Patents </li>
		<li> EP Patents </li>
	</ul>
	
To view any of these reports, Select WeekNumber.
</p>


Select WeekNumber:

 <form name="f" action="${pageContext.servletContext.contextPath}/Reports" method="post">   
 <SELECT name="weeknummber" id="weeknumber">
    <% 
    int startWeek = 1;
    final int endWeek = 52;
    int year = 2015;
    String currentWeek;
    
    for(;startWeek <= endWeek; startWeek++)
    { 
    	if(startWeek <10)
    	{
    		currentWeek = year + "0" + startWeek;
    	}
    	else
    	{
    		currentWeek = Integer.toString(year) + Integer.toString(startWeek);
    	}
    		
    	
    %>
        <OPTION VALUE="<%=currentWeek%>"> <%=startWeek%>&nbsp;&nbsp;&nbsp;</OPTION>
<%  }
    %>
    </SELECT> 
    <input type="hidden" name="selectedValue" value=''/> 
    <input name="submit" type="submit" value="submit" />
 
	 </form>
	 
    </br>
    </br>
   
  
  
<%

out.println("TEST2: " + session.getAttribute("weeknum"));

/* if(session.getAttribute("weeknum") !=null && session.getAttribute("weeknum").toString().length() >0)
{
	 
} */


%>


<!-- Test Report Data -->
<%
if(((ArrayList<ArrayList<String>>)session.getAttribute("Report")) !=null)
{
	//int size = ((ArrayList<ArrayList<String>>)session.getAttribute("Report")).size();
	int size = ((ArrayList<Map<String,String>>)session.getAttribute("Report")).size();
	out.println("List Size is :" +  Integer.toString(size));
	
	
}


%>

   <table id="report">
   <thead>
   	<tr>
   		<caption>LoadNumber: <%= session.getAttribute("weeknum")%></caption>
   	</tr>
   </thead>
   <thead>
   <tr>
      <th>DataSet</th>
      <th>Operation</th>
      <th>Week(YYYYWK)</th>
      <th>Source File Name</th>
      <th>Source File Count</th>
      <th>Loaded Database Count</th>
      <th>Discrepancy</th>
      <th>Reason</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${Report}" var="item">
       <tr>
				<c:if test="${item.DATASET ne null}"><td>${item.DATASET}</td></c:if>
				<c:if test="${item.OPERATION ne null}"><td>${item.OPERATION}</td></c:if>
				<c:if test="${item.LOADNUMBER ne null}"><td>${item.LOADNUMBER}</td></c:if>
				<c:if test="${item.SOURCEFILENAME ne null}"><td>${item.SOURCEFILENAME}</td></c:if>
				<c:if test="${item.SOURCEFILECOUNT ne null}"><td>${item.SOURCEFILECOUNT}</td></c:if>
				<c:if test="${item.MASTERTABLECOUNT ne null}"><td>${item.MASTERTABLECOUNT}</td></c:if>
				<c:if test="${item.SRC_MASTER_DIFF ne null}"><td>${item.SRC_MASTER_DIFF}</td></c:if>
				<c:if test="${item.ERRORMESSAGE ne null}"><td>${item.ERRORMESSAGE}</td></c:if>
			</tr>	
    </c:forEach>
    </tbody>
</table>


    

<%-- <ul>
    <c:forEach items="${Report}" var="item">
        <li>${item}</li>
    </c:forEach>
</ul> --%>



</div>

<div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/copyright.js"></script>
	
</p>
</div>
<!--  -->





			</div>
		</div>

<script type="text/javascript"> document.getElementById("weeknumber").selectedIndex=<%=session.getAttribute("weekindex")%>;</script>


</body>
</html>