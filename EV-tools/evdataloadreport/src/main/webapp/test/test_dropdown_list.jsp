<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>

</head>
<body>

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
    
</body>
</html>