<%@page import="org.apache.el.parser.JJTELParserState"%>
<%@page import="java.util.StringTokenizer"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.lang.*" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
    <%@ page import="java.util.HashMap" %>
    <%@ page import="java.util.Iterator" %>
    <%@ page import="javax.servlet.http.HttpServlet" %>
    <%@ page import="javax.servlet.http.HttpServletRequest" %>
    <%@ page import="javax.servlet.http.HttpServletResponse" %>
    <%@ page import="javax.servlet.http.HttpSession" %>
   
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DataBase Schemas & Tables</title>

<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/tables.css"/>

    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.4.2.min.js">
        </script>
<script type="text/javascript">

$(document).ready(function() { 
	$('li.category').addClass('plusimageapply'); $('li.category').children().addClass('selectedimage'); $('li.category').children().hide(); 
	$('li.category').each( function(column) { 
	$(this).click(function(event){ 
	if (this == event.target) { 
	if($(this).is('.plusimageapply')) { 
	$(this).children().show(); 
	$(this).removeClass('plusimageapply'); $(this).addClass('minusimageapply'); 
	} else { 
	$(this).children().hide(); 
	$(this).removeClass('minusimageapply'); $(this).addClass('plusimageapply'); 
	} 
	} 
	}); 
	} ); 
	});

</script>
    
    <style type="text/css">
    
    ul
    {
    	list-style: none;
    	padding-left: 4px;
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
	text-indent: 6px;
}

#expList ul
{
margin-top: 0px;
margin-bottom: 0px;
font-weight: bold;
}


#expList ul li 
{
color:#000080;
font-size: 12px;
margin-bottom: 0px;
margin-top: 4px;
font-weight: normal;

}

.category
{
margin-bottom: 0px;
}

#maincategory
{
font-weight: bold; 
font-size: 16px; 
color: #A52A2A;
}
/* for expanded Schema/Tables List*/

#subcategory
{
font-weight: bold; 
font-style:italic; 
font-size: 14px; 
color: #008B8B; 
padding-top: 4px;
}

.plusimageapply{ 
list-style-image:url(collapsed.png); cursor:pointer; } 
.minusimageapply{ list-style-image:url(expanded.png); cursor:pointer; } 
.selectedimage{ list-style-image:url(List-Option.png); cursor:pointer; padding-left: 15px;}

</style>
    
    
</head>
<body>

<%if(request.getParameter("database") !=null){session.setAttribute("database", request.getParameter("database").toString());} %>

<div id="wrapper">
		<%@ include file="views/includes/header.jsp" %>
		<!-- <div id="header"></div> -->
		<div id="sidebar">
		
		<%@ include file="/views/includes/navlinks.jsp" %>
		
			
<!-- Schema Tables List Test -->
				
<%--   <%if(session.getAttribute("SCHEMATABLES") !=null)
	{%>
	<b>Size of Schema Tables List <%=((HashMap<String,String>)session.getAttribute("SCHEMATABLES")).size()%></b>
	<%}%> --%> 
		

		</div>
		
		
<div id="content">
		

		<h1>Database Schema(s) and Tables</h1>
		</br>
		</br>
		<p>To see table structure (Description, Indexes, and Constraints), Expand Schema, then click on the <b>Table Name</b></p>
		
		<div>
		<ul>
			<li class="category" id="maincategory">DB Schemas
			<%
			if(session.getAttribute("SCHEMATABLES") !=null)
			{
				String value="";
 				 Iterator it = ((HashMap <String, String>)session.getAttribute("SCHEMATABLES")).entrySet().iterator();
 				 		while(it.hasNext())
 				 		{
 				 			Map.Entry<String,String> pair = (Map.Entry<String,String>)it.next();
 				 			
 				 			%>

 				 		<ul id="expList">
 				 			<li class="category" id="subcategory"><%= pair.getKey()%>
 				 				<ul>
 				 					<li class="category">Tables
 				 						<ul>
 				 							<%if(pair.getValue() !=null && pair.getValue().contains(","))
 				 							{ 
 				 								StringTokenizer st = new StringTokenizer(pair.getValue(),",");
 				 								while(st.hasMoreTokens())
 				 								{ value = st.nextToken();%>
 				 									<li><a href="${pageContext.servletContext.contextPath}/DataStructure?s=<%= pair.getKey()%>&t=<%= value%>" target="_blank"><%= value %></a>
 				 									</li>
 				 								<% }
 				 						
 				 							}
 				 							else if(pair.getValue() !=null)
 				 							{%>
 				 					
 				 								<li><%= pair.getValue() %></li>
 				 						
 				 							<%}%>
 				 						</ul>
 				 					</li>
 				 				</ul>
 				 			</li>
 				 		</ul>	
 				 
 				 		<% 	
 				 		}%>
 				 		
			<%}
 				 		%> 
 				 		
		</div>
	
	
		
</div>				


 	<div id="footer">
		<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
		title="Elsevier home page (opens in a new window)" target="_blank"
		href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
		</p>
	</div> 

</div>

	


</body>
</html>