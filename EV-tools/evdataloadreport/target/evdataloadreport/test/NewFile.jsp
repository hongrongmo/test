<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@taglib prefix="weeks" uri="/WEB-INF/tlds/weeks.tld"%>    

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>EV DataLoading Login Page</title>
<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
 <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
 <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/css/ev_common_sciverse.css"/>
 
    
 <!--  does not work when call script from head, but does when call from within body, after tag reference the .js
   <script src="${pageContext.request.contextPath}/static/js/copyright.js"></script>
   <script type="text/javascript" src="<% request.getContextPath();%>/static/js/copyright.jsp"></script>
    
-->   
    <style>
    	table {border: 0px solid #aaaaaa; float:none}
    	table td {border: 0px }
    </style>
 
</head>

<body>
 
 <!--  HH TEST DropdownList usig tags & JAva class Tag Handler "WeeksHandler", WORKS very well, just need to specify CSS STYLE -->
<%--  <div>
 
 Select Week: <weeks:week/>
 </div> --%>
  
  
  
  <!-- HH: Test DropDownList2 to be able to do the css style -->
  
   
    <SELECT>
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
    
    
    
  <!--  -->
  
  <div id="main">
<p id="copyright"></p>

<jsp:include page="/views/includes/copyright.jsp" />
<%-- <jsp:include page="/views/includes/footer.jsp" /> --%>

</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/copyright.js"></script>

</body>


</html>