<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
    <%@ page import="java.util.Iterator" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DBA Operations</title>
	<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
    
    <link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
	<link rel="icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
    
  <!--   <style type="text/css">
    
    table td{
    padding-right: 300px;
    }
    
    </style> -->
    
</head>

<body>

<div id="wrapper">
		<%@ include file="/views/includes/header.jsp" %>
		
		<div id="sidebar"></div>
		<div id="content">
		
		
		<table>
		<tr>
		<td><h1>DBA Operations</h1></td>
		</tr>
		</table>


<table>
	
			<!-- row1,column1 -->
			<tr>
			<td colspan="3"></td>
			</tr>
			<tr>
			<td colspan="3"></td>
			</tr>
			
			
	<tr>
		<td id="theader" >
			<b>TableSpace Check:</b>
		</td>
	
	   <td id="theader" >
	    	<b>OEM Check:</b>
	    	
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
	    
	    
	    <td>
	    	<ul>
			<%if(session!=null && session.getAttribute("ROLE")!=null && session.getAttribute("ROLE").equals("admin")){ %>
				<li> <a class="eiatslink" href="https://<%=session.getAttribute("EIAENDPOINT")%>:1158/em" style="font-size: 12px; font-weight: bold;">EIA</a></li>
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
	  </tr>
	    
	   
     <tr>
		<td id="theader">
		<b>Oracle Connection:</b>
		</td>
		
		<td id="theader">
		<b>Session Check:</b>
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
     </tr>
     
     <tr>
		<td id="theader">
			<b>Usage Adhoc Analysis:</b>
		</td>
	
	   <td id="theader">
	    	<b>Data Structure (Master_Tables):</b>
	    	
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
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/template.jsp" style="font-size: 12px; font-weight: bold;">EIA</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/template.jsp" style="font-size: 12px; font-weight: bold;">EIB</a></li>
				<li> <a class="rplink" href="${pageContext.servletContext.contextPath}/template.jsp" style="font-size: 12px; font-weight: bold;">EID</a></li>
				
			    <%} 
			    
			else{%>
				
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/tablestructure.jsp?database=1" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIA</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/dbaoperations.jsp" style="font-size: 12px; font-weight: bold;" onclick="permission()">EIB</a></li>
				<li> <a class="eiatslink" href="${pageContext.servletContext.contextPath}/tablestructure.jsp?database=3" style="font-size: 12px; font-weight: bold;" onclick="permission()">EID</a></li>
				
			<%}
			    %>
			</ul>
	    </td>
     </tr>
	    
	    
	    
	    
</table>


</div>


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