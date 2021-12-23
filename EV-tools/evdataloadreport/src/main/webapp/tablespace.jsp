<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Table Space</title>
<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
    <link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
	<link rel="icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
   
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


<!--  TableSpace Table -->
  <div>
  <%if(session.getAttribute("TS") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("TS")).size() >0
  && ((session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin")))){ %>
  <table id="ts">
   <thead>
   	<tr>
   		<caption>TableSpace For: <%=session.getAttribute("RDSNAME")%></caption>
   	</tr>
   </thead>
   <thead>
   <tr>
      <th>TableSpace</th>
      <th>Total (GB)</th>
      <th>Free (GB)</th>
      <th>Blocks (MB)</th>
      <th>% Free</th>
    </tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${TS}" var="item">
       <tr>
				<c:if test="${item.TABLESPACE_NAME ne null}"><td>${item.TABLESPACE_NAME}</td></c:if>
				<c:if test="${item.Free_GB ne null}"><td>${item.Free_GB}</td></c:if>
				<c:if test="${item.Free_GB_1 ne null}"><td>${item.Free_GB_1}</td></c:if>
				<c:if test="${item.Max_Free_MB ne null}"><td>${item.Max_Free_MB}</td></c:if>
				<c:if test="${item.Free_Percentage ne null}"><td>${item.Free_Percentage}</td></c:if>
				
			</tr>	
    </c:forEach>
    </tbody>
</table>

  <%} %>
  
  </div>
  
  </br>
  </br>
<!--  END  -->


<!--  TableSpace Table2 -->
  <div>
  <%if(session.getAttribute("TS2") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("TS2")).size() >0
  && ((session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin")))){ %>
  <table id="ts">
   <thead>
   <tr>
      <th>TableSpace</th>
      <th>Total (MB)</th>
      <th>FREE (MB)</th>
      <th>Blocks (MB)</th>
      <th>% Free</th>
    </tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${TS2}" var="item">
       <tr>
				<c:if test="${item.TABLESPACE_NAME ne null}"><td>${item.TABLESPACE_NAME}</td></c:if>
				<c:if test="${item.Free_MB ne null}"><td>${item.Free_MB}</td></c:if>
				<c:if test="${item.Free_MB_1 ne null}"><td>${item.Free_MB_1}</td></c:if>
				<c:if test="${item.Max_Free_MB ne null}"><td>${item.Max_Free_MB}</td></c:if>
				<c:if test="${item.Free_Percentage ne null}"><td>${item.Free_Percentage}</td></c:if>
				
			</tr>	
    </c:forEach>
    </tbody>
</table>

  <%} %>
  
  </div>
 
 
 </br>
  </br>
   
<!--  END  -->


<!--  TableSpace Table2 -->
  <div>
  <%if(session.getAttribute("TS3") !=null && ((session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin")))){ %>
  <table id="ts">
   <thead>
   <tr>
      <th>TOTAL SPACE USAGE</th>
    </tr>
  </thead>
 
  
  <tbody>
       <tr>
				<c:if test="${TS3 ne null}"><td>${TS3}</td></c:if>
    </tbody>
</table>

  <%} %>
  
  </div>
 
 
 </br>
 
   
<!--  END  -->

 <%if((session.getAttribute("username") !=null) && (session.getAttribute("ROLE") !=null) && (session.getAttribute("ROLE").toString().equalsIgnoreCase("admin")))
	{%>
		<p> to view sql statment click here <a class="sqlstmt" id="sqlstmt" title="See sql statement" href="#" onclick="window.open('sqlstatements.jsp', 'SQL Statement', 'width=1000 ,height=900,toolbar=yes,scrollbars=1')">
    	<img class="sqlimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/Full-Text.png" title=""></img></a>
	<%}
	
	%>	

</br>

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