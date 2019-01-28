<%@page import="java.util.StringTokenizer"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
    <%@ page import="java.util.HashMap" %>
    <%@ page import="java.util.Iterator" %>
    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Master Tables Structure</title>

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

#expList
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


/* for expanded Schema/Tables List*/


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
		
		<%-- <ul>
					<li><a id="navlink" href="${pageContext.servletContext.contextPath}/DataStructure?table=1">BD_MASTER</a></li>
					<li><a id="navlink" href="${pageContext.servletContext.contextPath}/DataStructure?table=2">C84_MASTER</a></li>
					<li><a id="navlink" href="${pageContext.servletContext.contextPath}/DataStructure?table=3">INS_MASTER</a></li>
					<li><a id="navlink" href="${pageContext.servletContext.contextPath}/DataStructure?table=4">UPT_MASTER</a></li>
					<li><a id="navlink" href="${pageContext.servletContext.contextPath}/DataStructure?table=5">EPT_MASTER</a></li>
					<li><a id="navlink" href="${pageContext.servletContext.contextPath}/DataStructure?table=6">CBN_MASTER</a></li>
					<li><a id="navlink" href="${pageContext.servletContext.contextPath}/DataStructure?table=7">GRF_MASTER</a></li>
			</ul>
			
			
			<!-- Schema Tables List Test -->
				
 
  
  <%if(session.getAttribute("SCHEMATABLES") !=null)
	{%>
	<b>Size of Schema Tables List <%=((HashMap<String,String>)session.getAttribute("SCHEMATABLES")).size()%></b>
	<%}%> --%>
		

		</div>
		
		
		<div id="content">
		

		<h1>Data Structure (Master_Tables)</h1>
		</br>
		</br>
		<!-- <p>To see table structure (Description, Indexes, and Constraints), click on the <b>Table Name link</b> on the left</p>
		 -->
		
		
		 <div>
  				
  				<%-- <%if(session.getAttribute("STRUCTURE") !=null && ((HashMap<String,String>)session.getAttribute("STRUCTURE")).size() >0){%> --%>
  				<%if(session.getAttribute("STRUCTURE") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("STRUCTURE")).size() >0){%>
  				<table id="ds">
   				<thead>
   					<tr>
   						<caption><%=(String)session.getAttribute("TABLENAME")+" " %>Structure</caption>
   					</tr>
   				</thead>
   				<thead>
   					<tr>
      					<th>COLUMN_ID</th>
      					<th>COLUMN_NAME</th>
      					<th>DATA_TYPE</th>
      					<th>DATA_LENGTH</th>
      					<th>NULLABLE</th>
      					<th>DATA_DEFAULT</th>
    				</tr>
  				</thead>
   
 				 <tbody>
 				<%--  <%
 				 Iterator it = ((HashMap <String, String>)session.getAttribute("STRUCTURE")).entrySet().iterator();
 				 		while(it.hasNext())
 				 		{
 				 			Map.Entry<String,String> pair = (Map.Entry<String,String>)it.next();
 				 			
 				 			%>
 				 			<tr>
 				 			<td><%= pair.getKey()%></td>
 				 			<td><%= pair.getValue()%></td>
 				 			</tr>
 				 		<% 	
 				 		}
 				 		%> --%>
 				 		
 				<c:forEach items="${STRUCTURE}" var="item">
       			  <tr>
					<c:if test="${item.COLUMN_ID ne null}"><td>${item.COLUMN_ID}</td></c:if>
					<c:if test="${item.COLUMN_NAME ne null}"><td>${item.COLUMN_NAME}</td></c:if>
					<c:if test="${item.DATA_TYPE ne null}"><td>${item.DATA_TYPE}</td></c:if>
					<c:if test="${item.DATA_LENGTH ne null}"><td>${item.DATA_LENGTH}</td></c:if>
					<c:if test="${item.NULLABLE ne null}"><td>${item.NULLABLE}</td></c:if>
					
					<c:choose>
						<c:when test="${item.DATA_DEFAULT eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.DATA_DEFAULT}</td>
						</c:otherwise>
					</c:choose>
					
				</tr>	
   			 </c:forEach>
   			 
					
				</tbody>
	</table>
	
		<%} %>

</br>
</br>
		
		<!-- Table indexes -->
		
		<%if(session.getAttribute("INDEXES") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("INDEXES")).size() >0){%>
  				<table id="ds">
   				<thead>
   					<tr>
   						<caption><%=(String)session.getAttribute("TABLENAME")+" " %>Indexes</caption>
   					</tr>
   				</thead>
   				<thead>
   					<tr>
      					<th>TABLE_OWNER</th>
      					<th>INDEX_NAME</th>
      					<th>UNIQUENESS</th>
      					<th>STATUS</th>
      					<th>INDEX_TYPE</th>
      					<th>PARTITIONED</th>
      					<th>FUNCIDX</th>
      				    <th>JOIN_INDEX</th> 
      					<th>COLUMN_NAME</th>
      					<th>COLUMN_EXPRESSION</th>
      					
    				</tr>
  				</thead>
   
 				 <tbody>
 				 
 				  <c:forEach items="${INDEXES}" var="item">
       			<tr>
					<td>${item.TABLE_OWNER}</td>
					<c:choose>
						<c:when test="${item.INDEX_NAME eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.INDEX_NAME}</td>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${item.UNIQUENESS eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.UNIQUENESS}</td>
						</c:otherwise>
					</c:choose>
					
					
					<c:choose>
						<c:when test="${item.STATUS eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.STATUS}</td>
						</c:otherwise>
					</c:choose>
					
					
					<c:choose>
						<c:when test="${item.INDEX_TYPE eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.INDEX_TYPE}</td>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${item.PARTITIONED eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.PARTITIONED}</td>
						</c:otherwise>
					</c:choose>
					
					
					<c:choose>
						<c:when test="${item.FUNCIDX eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.FUNCIDX}</td>
						</c:otherwise>
					</c:choose>
					
					
					<c:choose>
						<c:when test="${item.JOIN_INDEX eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.JOIN_INDEX}</td>
						</c:otherwise>
					</c:choose>

					<c:choose>
						<c:when test="${item.COLUMN_NAME eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.COLUMN_NAME}</td>
						</c:otherwise>
					</c:choose>
					
					
					<c:choose>
						<c:when test="${item.COLUMN_EXPRESSION eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.COLUMN_EXPRESSION}</td>
						</c:otherwise>
					</c:choose>
					
				</tr>	
   			 </c:forEach>
    
				</tbody>
	</table>
	
		<%} %>
		
		
		</br>
		</br>
			
		<!-- Table constraints -->
		
		<%if(session.getAttribute("CONSTRAINTS") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("CONSTRAINTS")).size() >0){%>
  				<table id="ds">
   				<thead>
   					<tr>
   						<caption><%=(String)session.getAttribute("TABLENAME")+" " %>Constraints</caption>
   					</tr>
   				</thead>
   				<thead>
   					<tr>
      					<th>OWNER</th>
      					<th>CONSTRAINT_NAME</th>
      					<th>CONSTRAINT_TYPE</th>
      					<th>SEARCH_CONDITION</th>
      					<th>STATUS</th>
      					<th>GENERATED</th>
      					<th>LAST_CHANGE</th>
      					<th>INDEX_OWNER</th>
      					<th>INDEX_NAME</th>
      					
    				</tr>
  				</thead>
   
 				 <tbody>
 				 
 				  <c:forEach items="${CONSTRAINTS}" var="item">
       			<tr>
					<td>${item.OWNER}</td>
					<td>${item.CONSTRAINT_NAME}</td>
					<td>${item.CONSTRAINT_TYPE}</td>
					<c:choose>
						<c:when test="${item.SEARCH_CONDITION eq null}">
							<td>(null)</td>
						</c:when>
						
						<c:otherwise>
							<td>${item.SEARCH_CONDITION}</td>
						</c:otherwise>
					
					</c:choose>
						
					
					<td>${item.STATUS}</td>
					<td>${item.GENERATED}</td>
					<td>${item.LAST_CHANGE}</td>
					<c:choose>
						<c:when test="${item.INDEX_OWNER eq null}">
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.INDEX_OWNER}</td>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${item.INDEX_NAME eq null}"> 
							<td>(null)</td>
						</c:when>
						<c:otherwise>
							<td>${item.INDEX_NAME}</td>
						</c:otherwise>
					</c:choose>
					
					
					
				</tr>	
   			 </c:forEach>
    
				</tbody>
	</table>
	
		<%} %>
		
	<!--  view sql stmt only for authorized users -->
	<%if((session.getAttribute("username") !=null) && (session.getAttribute("ROLE") !=null) && (session.getAttribute("ROLE").toString().equalsIgnoreCase("admin"))
			&& session.getAttribute("TABLENAME") !=null)
	{%>
		<p> to view sql statment click here <a class="sqlstmt" id="sqlstmt" title="See sql statement" href="#" onclick="window.open('sqlstatements.jsp', 'SQL Statement', 'width=1000 ,height=900,toolbar=yes,scrollbars=1')">
    			<img class="sqlimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/Full-Text.png" title=""></img>
				</a>
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