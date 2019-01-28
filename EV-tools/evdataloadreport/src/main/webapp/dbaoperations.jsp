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
		<!-- <div id="header"></div> -->
		<div id="sidebar">
			<%@ include file="/views/includes/navlinks.jsp" %>
		</div>
		
<%if(session.getAttribute("ROLE")!=null){%>

		<div id="content">
		
		<table>
		<tr>
		<td><h1>DBA Operations</h1></td>
		</tr>
		</table>

</br>


<table id="dba">
	
			<!-- row1,column1 -->
			<tr>
			<td colspan="3"></td>
			</tr>
			<tr>
			<td colspan="3"></td>
			</tr>
			
			
	<tr>
		<td>
			<div id="theader">TableSpace Check:</div>
		</td>
	
	   <td>
	    	<div id="theader">OEM Check:</div>
	    	
	   	</td>
	</tr>
	    
	<tr>
		<td>
			<ul>
			<%if(session.getAttribute("ROLE")!=null && session.getAttribute("ROLE").equals("admin")){ %>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/Tablespace?ts=1" style="font-size: 12px; font-weight: bold;">EIA</a></li>
			    <li> <a class="eibtslink" href="${pageContext.servletContext.contextPath}/Tablespace?ts=2" style="font-size: 12px; font-weight: bold;">EIB</a></li>
			    <li> <a class="eidtslink" href="${pageContext.servletContext.contextPath}/Tablespace?ts=3" style="font-size: 12px; font-weight: bold;">EID</a></li>
			    <%} 
			    
			else{%>
				
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIA</a></li>
			    <li> <a class="eibtslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIB</a></li>
			    <li> <a class="eidtslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EID</a></li>
			<%}
			    %>
			</ul>
	    </td>
	    
	    
	    <!-- OEM -->
	    <td>
	    	<ul>
			<%if(session!=null && session.getAttribute("ROLE")!=null && session.getAttribute("ROLE").equals("admin")){ %>
				<li> <a class="eiatslink" href="https://<%=session.getAttribute("EIAENDPOINT")%>:1158/em" target="_blank" style="font-size: 12px; font-weight: bold;">EIA</a></li>
			    <li> <a class="eibtslink" href="https://<%=session.getAttribute("EIBENDPOINT")%>:1158/em" target="_blank" style="font-size: 12px; font-weight: bold;">EIB</a></li>
			    <li> <a class="eidtslink" href="https://<%=session.getAttribute("EIDENDPOINT")%>:1158/em" target="_blank" style="font-size: 12px; font-weight: bold;">EID</a></li>
			    <%} 
			    
			else{%>
				
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" target="_blank" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIA</a></li>
			    <li> <a class="eibtslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" target="_blank" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIB</a></li>
			    <li> <a class="eidtslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" target="_blank" style="font-size: 12px; font-weight: bold;" onclick="permission()">EID</a></li>
			<%}
			    %>
			</ul>	
	    
	    </td>
	  </tr>
	    
	   
     <tr>
		<td>
		<div id="theader" >Oracle Connection:</div>
		</td>
		
		<td>
		<div id="theader">Session Check:</div>
		</td>
		</tr>
		
		<tr>
		<td>
			<ul>
			<%if(session.getAttribute("ROLE")!=null && session.getAttribute("ROLE").equals("admin")){ %>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/Connections?database=1" style="font-size: 12px; font-weight: bold;">EIA</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/Connections?database=2" style="font-size: 12px; font-weight: bold;">EIB</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/Connections?database=3" style="font-size: 12px; font-weight: bold;">EID</a></li>
			    
			    <%} 
			    
			else{%>
				
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIA</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIB</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EID</a></li>
			    
			<%}
			    %>
			</ul>
	    </td>
	    
	    
	    <td>
			<ul>
			<%if(session.getAttribute("ROLE")!=null && session.getAttribute("ROLE").equals("admin")){ %>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}//DbMappings?database=1" style="font-size: 12px; font-weight: bold;">EIA</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/DbMappings?database=2" style="font-size: 12px; font-weight: bold;">EIB</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/DbMappings?database=3" style="font-size: 12px; font-weight: bold;">EID</a></li>
			    
			    <%} 
			    
			else{%>
				
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIA</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIB</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EID</a></li>
				
			    
			<%}
			    %>
			</ul>
	    </td>
     </tr>
     
     <tr>
		<td>
			<div id="theader">Usage Adhoc Analysis:</div>
		</td>
	
	   <td>
	    	<div id="theader">Data Structure (Master_Tables):</div>
	    	
	   	</td>
	</tr>
	
	<tr>
		<td>
			<ul>
			<%if(session.getAttribute("ROLE")!=null && session.getAttribute("ROLE").equals("admin")){ %>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/template.jsp" style="font-size: 12px; font-weight: bold;">EIA</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/template.jsp" style="font-size: 12px; font-weight: bold;">EIB</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/template.jsp" style="font-size: 12px; font-weight: bold;">EID</a></li>
				
			    
			    <%} 
			    
			else{%>
				
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIA</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIB</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EID</a></li>
				
			<%}
			    %>
			</ul>
	    </td>
	    
	    
	    <td>
			<ul>
			<%if(session.getAttribute("ROLE")!=null && session.getAttribute("ROLE").equals("admin")){ %>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/DbTables?database=1" style="font-size: 12px; font-weight: bold;">EIA</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/DbTables?database=2" style="font-size: 12px; font-weight: bold;">EIB</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/DbTables?database=3" style="font-size: 12px; font-weight: bold;">EID</a></li>
				
			    <%} 
			    
			else{%>
				
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIA</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIB</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EID</a></li>
				
			<%}
			    %>
			</ul>
	    </td>
     </tr>
    
</table>


</div>
<%} %>

<div id="footer" style="margin-bottom: 0px;">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
	
	<!-- JS works, but replacing it with java instead bc link not work -->
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/copyright.js"></script> --%>
	
</p>
</div>



</div>

<script type="text/javascript">

function permission()
{
	alert("dba permission required!");
	}
</script>



</body>
</html>