<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DB Sessions</title>

<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/tables.css"/>
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

<%if(session.getAttribute("database") !=null && ((session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin")))){ %>  
  


<div id="wrapper">

		<%@ include file="/views/includes/header.jsp" %>
		
		<div id="sidebar">
		<ul class="navlinks">
			<li><a href="${pageContext.servletContext.contextPath}/Session?op=allcount">All Sessions Count By Users</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/Session?op=all">All Sessions</a></li>
			<li><a href="#" onclick='addInputBox()'>Show User Session</a></li>
			<li><a href="#" onclick='addInputBox2()'>Show User Session By UserName and OS User</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/Session?op=openedcursor">Show Opened Cursor</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/Session?op=showallparam">Show All Parameters</a></li>
			<li><a href="#" onclick='addInputBox3()'>Show a Single Parameter</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/Session?op=ctlfile">Show Control File</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/Session?op=logfile">Show Log File</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/Session?op=rollbackseg">Show Rollback Segment</a></li>

		</ul>
		




		</div>
	
		<div id="content">
			
			<c:if test="${not empty param.error}">
  				   <br />
				   <div class="paddingL10">
				   	 <img style="vertical-align:bottom" src="${pageContext.servletContext.contextPath}/static/images/red_warning.gif">
				   	 <b>&nbsp;&nbsp;No Input Specified, Please Enter all parameters</b>
				   </div>
				</c:if>
				
	
	<div id="sessionByOracleUser" style="margin-top: 6px;"> </div> 
	<div id="sessionByOracleAndOSUser" style="margin-top: 6px;"> </div> 
	<div id="parameter" style="margin-top: 6px;"> </div> 

				
<!--  All Session Count by users -->
<% if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("allcount") && session.getAttribute("ALLSESSIONCOUNT") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("ALLSESSIONCOUNT")).size() >0)
  { %>
  <div id="allsessioncount">

<table id="sessioncount">
  <caption>All Sessions Count By Users</caption>
  
   <thead>
   <tr>
      <th>USERNAME</th>
      <th>COUNT</th>
    </tr>
  </thead>
 
  <tbody>
  <c:forEach items="${ALLSESSIONCOUNT}" var="item">
  	<tr>
  		<c:if test="${item.USERNAME ne null}"><td>${item.USERNAME}</td></c:if>
  		<c:if test="${item.COUNT ne null}"><td>${item.COUNT}</td></c:if>
  		  	</tr>
  		</c:forEach>
  </tbody>
 </table>
   
  </div>
 <% } %>
 <!-- END -->
 
 
  
  
<!-- All Sessions -->  
<%-- 
   <%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("all") &&
   		session.getAttribute("ALLSESSION") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("ALLSESSION")).size() >0
   		&& ((session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin"))))  // WHEN DEPLOY
   { %> --%>
   
   <%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("all") &&
   		session.getAttribute("ALLSESSION") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("ALLSESSION")).size() >0)
   { %>   <!--  DURING TESTING -->
<div id="allsessions" style="display: block;"> 
  <table id="session">
  <caption>All Sessions</caption>
  
   <thead>
   <tr>
      <th>SID</th>
      <th>SERIAL#</th>
      <th>USERNAME</th>
      <th>OSUSER</th>
      <th>PROGRAM</th>
      <th>MACHINE</th>
      <th>TERMINAL</th>
      <th>LOGON TIME</th>
    </tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${ALLSESSION}" var="item">
       <tr>
				<c:if test="${item.SID ne null}"><td>${item.SID}</td></c:if>
				<c:if test="${item.SERIAL_NUM ne null}"><td>${item.SERIAL_NUM}</td></c:if>
				<c:choose>
					<c:when test="${item.USERNAME ne null}">
						<td>${item.USERNAME}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.OSUSER ne null}">
						<td>${item.OSUSER}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.PROGRAM ne null}">
						<td>${item.PROGRAM}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.MACHINE ne null}">
						<td>${item.MACHINE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.TERMINAL ne null}">
						<td>${item.TERMINAL}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.DATETIME ne null}">
						<td>${item.DATETIME}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
			</tr>	
    </c:forEach>
    </tbody>
</table>

</div>
</br>
</br> 
  <%} %>

   
 
<!-- END --> 
 
 
 <!-- User Session By Oracle UserName -->

<%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("usersession")
	&& session.getAttribute("SESSIONBYUSER") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("SESSIONBYUSER")).size() >0)
	{%>
<div id="sessionbyuser">	
		 <table id="session">
 		 <caption style="width:400px;">Session Info for UserName: <%= session.getAttribute("SESSIONNAME") %></caption>
  
  	 <thead>
  		 <tr>
     		 <th>SID</th>
      		<th>SERIAL#</th>
      		<th>USERNAME</th>
      		<th>OSUSER</th>
      		<th>PROGRAM</th>
      		<th>MACHINE</th>
      		<th>TERMINAL</th>
      		<th>LOGON TIME</th>
    	</tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${SESSIONBYUSER}" var="item">
       <tr>
       	<c:if test="${item.SID ne null}"><td>${item.SID}</td></c:if>
       	<c:if test="${item.SERIAL_NUM ne null}"><td>${item.SERIAL_NUM}</td></c:if>
				<c:choose>
					<c:when test="${item.USERNAME ne null}">
						<td>${item.USERNAME}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.OSUSER ne null}">
						<td>${item.OSUSER}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.PROGRAM ne null}">
						<td>${item.PROGRAM}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.MACHINE ne null}">
						<td>${item.MACHINE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.TERMINAL ne null}">
						<td>${item.TERMINAL}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.DATETIME ne null}">
						<td>${item.DATETIME}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
       </tr>
    </c:forEach>
   </tbody>
 </table>
 
</div>
 </br>
</br> 

	<%} %>

 <!-- END --> 


<!-- User Session By Oracle UserName  and OsUser-->

<%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("oousersession")
	&& session.getAttribute("SESSIONBYOOUSER") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("SESSIONBYOOUSER")).size() >0)
	{%>
<div id="sessionbyoouser">	
		 <table id="session">
 		 <caption style="width:600px;">Session Info for UserName: <%= session.getAttribute("SESSIONNAME") %> and OsUser: <%= session.getAttribute("OSUSER") %></caption>
  
  	 <thead>
  		 <tr>
     		 <th>SID</th>
      		<th>SERIAL#</th>
      		<th>USERNAME</th>
      		<th>OSUSER</th>
      		<th>PROGRAM</th>
      		<th>MACHINE</th>
      		<th>TERMINAL</th>
      		<th>LOGON TIME</th>
    	</tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${SESSIONBYOOUSER}" var="item">
       <tr>
       	<c:if test="${item.SID ne null}"><td>${item.SID}</td></c:if>
       	<c:if test="${item.SERIAL_NUM ne null}"><td>${item.SERIAL_NUM}</td></c:if>
				<c:choose>
					<c:when test="${item.USERNAME ne null}">
						<td>${item.USERNAME}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.OSUSER ne null}">
						<td>${item.OSUSER}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.PROGRAM ne null}">
						<td>${item.PROGRAM}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.MACHINE ne null}">
						<td>${item.MACHINE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.TERMINAL ne null}">
						<td>${item.TERMINAL}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.DATETIME ne null}">
						<td>${item.DATETIME}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
       </tr>
    </c:forEach>
   </tbody>
 </table>
 
</div> 
</br>
</br> 

	<%} %>
 
 <!-- END --> 



<!-- Opened Curosrs-->

<%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("openedcursor")
	&& session.getAttribute("OPENEDCURSORS") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("OPENEDCURSORS")).size() >0)
	{%>
 <div id="openedcursor">	
		 <table id="session">
 		 <caption style="width:400px;">Opened Cursors</caption>
  
  	 <thead>
  		 <tr>
     		 <th>USERNAME</th>
      		<th>Value</th>
      		<th>SID</th>
      		<th>SERIAL #</th>
      		<th>OSUSER</th>
      		<th>PROCESS</th>
      		<th>MACHINE</th>
      		<th>LOGON TIME</th>
    	</tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${OPENEDCURSORS}" var="item">
       <tr>
       	<c:if test="${item.USERNAME ne null}"><td>${item.USERNAME}</td></c:if>
       	<c:if test="${item.VALUE ne null}"><td>${item.VALUE}</td></c:if>
				<c:choose>
					<c:when test="${item.SID ne null}">
						<td>${item.SID}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.SERIAL_NUM ne null}">
						<td>${item.SERIAL_NUM}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.OSUSER ne null}">
						<td>${item.OSUSER}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.PROCESS ne null}">
						<td>${item.PROCESS}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.MACHINE ne null}">
						<td>${item.MACHINE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.DATETIME ne null}">
						<td>${item.DATETIME}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
       </tr>
    </c:forEach>
   </tbody>
 </table>

</div>
 
</br>
</br> 

	<%} %>
 
 <!-- END --> 
 
 
 <!-- Show All Parameters-->

<%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("showallparam")
	&& session.getAttribute("ALLPARAMETERS") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("ALLPARAMETERS")).size() >0)
	{%>
<div id="allparameters">	
		 <table id="session">
 		 <caption style="width:400px;">All Parameters</caption>
  
  	 <thead>
  		 <tr>
     		<th>NUM</th>
      		<th>NAME</th>
      		<th>DESCRIPTION</th>
      		<th>VALUE</th>
      		<!-- <th>DISPLAY_VALUE</th> -->   <!-- commented out to to save space for description column -->
      		<th>TYPE</th>
      		<th>MODIFIABLE?</th> 
    	</tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${ALLPARAMETERS}" var="item">
       <tr>
       	<c:if test="${item.NUM ne null}"><td>${item.NUM}</td></c:if>
       	<c:if test="${item.NAME ne null}"><td id="paramname">${item.NAME}</td></c:if>			
				
				<c:choose>
					<c:when test="${item.DESCRIPTION ne null}">
						<td>${item.DESCRIPTION}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.VALUE ne null}">
						<td>${item.VALUE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<!-- commented out to to save space for description column -->
				
				<%-- <c:choose>
					<c:when test="${item.DISPLAY_VALUE ne null}">
						<td>${item.DISPLAY_VALUE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose> --%>
				
				<c:choose>
					<c:when test="${item.TYPE ne null}">
						<c:choose>
							<c:when test="${item.TYPE eq '1'}">
								<td>Boolean</td>
							</c:when>
							<c:when test="${item.TYPE eq '2'}">
								<td>String</td>
							</c:when>
							<c:when test="${item.TYPE eq '3'}">
								<td>Integer</td>
							</c:when>
							<c:when test="${item.TYPE eq '4'}">
								<td>File</td>
							</c:when>
							<c:when test="${item.TYPE eq '6'}">
								<td>Big Integer</td>
							</c:when>
							<c:otherwise>
								<td>${item.TYPE}</td>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.ISSES_MODIFIABLE ne null}">
						<td>${item.ISSES_MODIFIABLE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
       </tr>
    </c:forEach>
   </tbody>
 </table>

</div>
 
</br>
</br>
	<%} %>
 

 <!-- END --> 
 
 
 
 <!-- Show A Single Parameter-->

<%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("showparam")
	&& session.getAttribute("ONEPARAMETER") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("ONEPARAMETER")).size() >0)
	{%>
 <div id="aparameter">	
		 <table id="session">
 		 <caption style="width:400px;">Show Parameter: <%= session.getAttribute("PARAMETERNAME") %></caption>
  
  	 <thead>
  		 <tr>
     		<th>NUM</th>
      		<th>NAME</th>
      		<th>VALUE</th>
      		<th>DESCRIPTION</th>
      		<th>DISPLAY_VALUE</th>
      		<th>TYPE</th>
      		<th>ISSES_MODIFIABLE</th>
    	</tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${ONEPARAMETER}" var="item">
       <tr>
       	<c:if test="${item.NUM ne null}"><td>${item.NUM}</td></c:if>
       	<c:if test="${item.NAME ne null}"><td>${item.NAME}</td></c:if>
				<c:choose>
					<c:when test="${item.VALUE ne null}">
						<td>${item.VALUE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.DESCRIPTION ne null}">
						<td>${item.DESCRIPTION}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.DISPLAY_VALUE ne null}">
						<td>${item.DISPLAY_VALUE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.TYPE ne null}">
						<c:choose>
							<c:when test="${item.TYPE eq '1'}">
								<td>Boolean</td>
							</c:when>
							<c:when test="${item.TYPE eq '2'}">
								<td>String</td>
							</c:when>
							<c:when test="${item.TYPE eq '3'}">
								<td>Integer</td>
							</c:when>
							<c:when test="${item.TYPE eq '4'}">
								<td>File</td>
							</c:when>
							<c:when test="${item.TYPE eq '6'}">
								<td>Big Integer</td>
							</c:when>
							<c:otherwise>
								<td>${item.TYPE}</td>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				
				<c:choose>
					<c:when test="${item.ISSES_MODIFIABLE ne null}">
						<td>${item.ISSES_MODIFIABLE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
       </tr>
    </c:forEach>
   </tbody>
 </table>

</div>
 
</br>
</br>
	<%} %>
 

 <!-- END --> 
 
 
 
 
  <!-- Control File -->
 
<%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("ctlfile")
	&& session.getAttribute("CTLFILE") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("CTLFILE")).size() >0)
	{%>
<div id="ctlfile">	
		 <table id="session">
 		 <caption style="width:400px;">Control File</caption>
  
  	 <thead>
  		 <tr>
     		 <th>NAME</th>
      		<th>BLOCK SIZE</th>
      		<th>FILE SIZE BLKS</th>
      		<th>STATUS</th>
      		<th>IS RECOVERY DEST FILE</th>
      		
    	</tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${CTLFILE}" var="item">
       <tr>
       	<c:if test="${item.NAME ne null}"><td>${item.NAME}</td></c:if>
       	<c:if test="${item.BLOCK_SIZE ne null}"><td>${item.BLOCK_SIZE}</td></c:if>
				<c:choose>
					<c:when test="${item.FILE_SIZE_BLKS ne null}">
						<td>${item.FILE_SIZE_BLKS}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.STATUS ne null}">
						<td>${item.STATUS}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.IS_RECOVERY_DEST_FILE ne null}">
						<td>${item.IS_RECOVERY_DEST_FILE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
       </tr>
    </c:forEach>
   </tbody>
 </table>

</div>
 
</br>
</br>
	<%} %>
 
 <!-- END --> 
 
 
  <!-- Log File -->
 
<%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("logfile")
	&& session.getAttribute("LOGFILE") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("LOGFILE")).size() >0)
	{%>
<div id="logfile">	
		 <table id="session">
 		 <caption style="width:400px;">Log File</caption>
  
  	 <thead>
  		 <tr>
     		 <th>MEMBER</th>
      		<th>GROUP #</th>
      		<th>TYPE</th>
      		<th>STATUS</th>
      		<th>IS RECOVERY DEST FILE</th>
      		
    	</tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${LOGFILE}" var="item">
       <tr>
       	<c:if test="${item.MEMBER ne null}"><td>${item.MEMBER}</td></c:if>
       	<c:if test="${item.GROUP_NUM ne null}"><td>${item.GROUP_NUM}</td></c:if>
				<c:choose>
					<c:when test="${item.TYPE ne null}">
						<td>${item.TYPE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.STATUS ne null}">
						<td>${item.STATUS}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.IS_RECOVERY_DEST_FILE ne null}">
						<td>${item.IS_RECOVERY_DEST_FILE}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
       </tr>
    </c:forEach>
   </tbody>
 </table>

</div>
 
</br>
</br>
	<%} %>
 
 <!-- END --> 
 

 <!-- Rollback Segment -->
 
<%if(session.getAttribute("OPERATION") !=null && session.getAttribute("OPERATION").toString().equalsIgnoreCase("rollbackseg")
	&& session.getAttribute("ROLLBACKSEGMENT") !=null && ((ArrayList<Map<String,String>>)session.getAttribute("ROLLBACKSEGMENT")).size() >0)
	{%>
<div id="rollbackseg">	
		 <table id="session">
 		 <caption style="width:400px;">Rollback Segment</caption>
  
  	 <thead>
  		 <tr>
     		 <th>SEGMENT NAME</th>
      		<th>TABLE SPACE NAME</th>
      		<th>WAITS</th>
      		<th>SHRINKS</th>
      		<th>WRAPS</th>
      		<th>STATUS</th>
      		
    	</tr>
  </thead>
 
  
  <tbody>
    <c:forEach items="${ROLLBACKSEGMENT}" var="item">
       <tr>
       	<c:if test="${item.SEGMENT_NAME ne null}"><td>${item.SEGMENT_NAME}</td></c:if>
       	<c:if test="${item.TABLESPACE_NAME ne null}"><td>${item.TABLESPACE_NAME}</td></c:if>
				<c:choose>
					<c:when test="${item.WAITS ne null}">
						<td>${item.WAITS}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.SHRINKS ne null}">
						<td>${item.SHRINKS}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.WRAPS ne null}">
						<td>${item.WRAPS}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.STATUS ne null}">
						<td>${item.STATUS}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
       </tr>
    </c:forEach>
   </tbody>
 </table>

</div>
 
</br>
</br>
	<%} %>
 
 <!-- END --> 
 
 
 <%if((session.getAttribute("username") !=null) && (session.getAttribute("ROLE") !=null) && (session.getAttribute("ROLE").toString().equalsIgnoreCase("admin")))
	{%>
		<p> to view sql statment click here <a class="sqlstmt" id="sqlstmt" title="See sql statement" href="#" onclick="window.open('sqlstatements.jsp', 'SQL Statement', 'width=1000 ,height=900,toolbar=yes,scrollbars=1')">
    	<img class="sqlimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/Full-Text.png" title=""></img>
		</a>
	<%}
	
	%>	
	
	
<script> 

//Get Session using Oracle User Name

function addInputBox() { 

var ni = document.getElementById('sessionByOracleUser'); 


var newdiv = document.createElement('div'); 

var divIdName = 'sessionbyuserinnerdiv'; 

newdiv.setAttribute('id',divIdName); 

newdiv.innerHTML = "<form name=\"usersession\" class=\"getsessionbyuser\" action=\"${pageContext.servletContext.contextPath}/Session?op=usersession\" method=\"post\">"+
					"<b>Enter the UserName</b><input type=\"text\" name=\"oracleUserName\"/> "+
					"<input name=\"submit\" type=\"submit\" value=\"View User Session\" /></form>";


ni.appendChild(newdiv); 

// set other session's INFO invisible
 
document.getElementById("allsessioncount").style.display='none';
document.getElementById("allsessions").style.display='none';

document.getElementById("sessionbyoouser").style.display='none';
document.getElementById("sessionbyoouserinnerdiv").style.display='none';

document.getElementById("openedcursor").style.display='none';
document.getElementById("allparameters").style.display='none';
document.getElementById("aparameter").style.display='none';
document.getElementById("ctlfile").style.display='none';
document.getElementById("logfile").style.display='none';


document.getElementById("sessionbyoouserinnerdiv").style.display='none';
document.getElementById("parametername").style.display='none';

} 


// Get Session using Oracle User Name and OS User
function addInputBox2() { 

	var ni = document.getElementById('sessionByOracleAndOSUser'); 


	var newdiv = document.createElement('div'); 

	var divIdName = 'sessionbyoouserinnerdiv'; 

	newdiv.setAttribute('id',divIdName); 

	newdiv.innerHTML = "<form name=\"ousersession\" class=\"getsessionbyuser\" action=\"${pageContext.servletContext.contextPath}/Session?op=oousersession\" method=\"post\">"+
						"<b>Enter Oracle UserName</b><input type=\"text\" name=\"oracleUserName\"/> "+
						"<b>Enter OS UserName</b><input type=\"text\" name=\"osUserName\"/> "+
						"<input name=\"submit\" type=\"submit\" value=\"View User Session\" /></form>";


	ni.appendChild(newdiv); 

	// set other session's INFO invisible
	
	document.getElementById("allsessioncount").style.display='none';
	document.getElementById("allsessions").style.display='none';
	
	document.getElementById("sessionbyuser").style.display='none';
	document.getElementById("sessionbyuserinnerdiv").style.display='none';
	
	document.getElementById("openedcursor").style.display='none';
	document.getElementById("allparameters").style.display='none';
	document.getElementById("aparameter").style.display='none';
	document.getElementById("ctlfile").style.display='none';
	document.getElementById("logfile").style.display='none';

	
	document.getElementById("usersession").style.display='none';
	document.getElementById("parametername").style.display='none';
	
	} 
	
	
// Show a Parameter
function addInputBox3() { 

	var ni = document.getElementById('parameter'); 


	var newdiv = document.createElement('div'); 

	var divIdName = 'parametername'; 

	newdiv.setAttribute('id',divIdName); 

	newdiv.innerHTML = "<form name=\"getparameter\" class=\"getparameter\" action=\"${pageContext.servletContext.contextPath}/Session?op=showparam\" method=\"post\">"+
						"<b>Enter the Parameter Name</b><input type=\"text\" name=\"parameterName\"/> "+
						"<input name=\"submit\" type=\"submit\" value=\"View Parameter\" /></form>";


	ni.appendChild(newdiv); 

	// set other session's INFO invisible

	document.getElementById("allsessioncount").style.display = 'none';
	document.getElementById("allsessions").style.display = 'none';
	document.getElementById("sessionbyuser").style.display='none';
	document.getElementById("sessionbyuserinnerdiv").style.display='none';
	
	document.getElementById("sessionbyoouser").style.display = 'none';
	document.getElementById("sessionbyoouserinnerdiv").style.display='none';
	
	document.getElementById("openedcursor").style.display = 'none';
	document.getElementById("allparameters").style.display = 'none';
	document.getElementById("ctlfile").style.display = 'none';
	document.getElementById("logfile").style.display = 'none';
	
	
	document.getElementById("usersession").style.display = 'none';
	document.getElementById("ousersession").style.display = 'none';
	} 
	
</script>

 
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
 
 <%} %>
</body>
</html>