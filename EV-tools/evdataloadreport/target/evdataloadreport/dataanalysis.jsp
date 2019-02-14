<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Data Analysis</title>
<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
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

	
<div id="wrapper">
		<%@ include file="/views/includes/header.jsp" %>
		<!-- <div id="header"></div> -->
		<div id="sidebar">
			<%@ include file="/views/includes/navlinks.jsp" %>
		</div>
		
<%if(session.getAttribute("username") !=null)
	{%>		
		<div id="content">

<c:if test="${not empty param.status}">
	<c:if test="${param.status eq 1}">
  				   
  				   <script type="text/javascript">
				 	 alert('Record Added Successfully');
				  </script>
				  
				   </c:if>
				   
				   <c:if test="${param.status eq -1}">
  				   
  					<script type="text/javascript">
					alert('No Records Added!');
					 </script>
				  
				</c:if>
</c:if>


<table>
		<tr>
		<td><h1>Data Analysis</h1></td>
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
			<div id="theader">Vendors' Data Quality Issue(s):</div>
		</td>
	
	   <td>
	    	<div id="theader">DTD:</div>
	    	
	   	</td>
	</tr>
	    
	<tr>
		<td>
		<ul style="list-style-type: none; font-size: 14px;">
			<li style="color: #8B0000;"><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/List-Option.png" title=""> <b>JIRA</b></img>
				<ul class="innerlist">
					<li><a class="rplink" title="Jira Link"  href="https://scival.atlassian.net/secure/RapidBoard.jspa?rapidView=254&view=planning&selectedIssue=EVOPS-6&versions=visible&epics=visible" target="_blank" style="font-size: 12px; font-weight: bold;">All Issues</a></li>
					<li><a class="rplink" title="Jira Link"  href="https://scival.atlassian.net/secure/RapidBoard.jspa?rapidView=254&view=planning&selectedIssue=EVOPS-104&versions=visible&epics=visible&selectedEpic=EVOPS-21" target="_blank" style="font-size: 12px; font-weight: bold;">BD Issues</a></li>
					<li><a class="rplink" title="Jira Link"  href="https://scival.atlassian.net/secure/RapidBoard.jspa?rapidView=254&view=planning&selectedIssue=EVOPS-6&versions=visible&epics=visible&selectedEpic=EVOPS-2" target="_blank" style="font-size: 12px; font-weight: bold;">New Eilib</a></li>
					<li><a class="rplink" title="Jira Link" href="https://scival.atlassian.net/secure/RapidBoard.jspa?rapidView=254&view=planning&selectedIssue=EVOPS-104&versions=visible&epics=visible&selectedEpic=EVOPS-138" target="_blank" style="font-size: 12px; font-weight: bold;">Cpx Reload</a></li>
					<li><a class="rplink" title="Jira Link" href="https://scival.atlassian.net/secure/RapidBoard.jspa?rapidView=254&view=planning&selectedIssue=EVOPS-6&versions=visible&epics=visible&selectedEpic=EVOPS-23" target="_blank" style="font-size: 12px; font-weight: bold;">AWS</a></li>
					<li><a class="rplink" title="Jira Link" href="https://scival.atlassian.net/secure/RapidBoard.jspa?rapidView=254&view=planning&selectedIssue=EVOPS-6&versions=visible&epics=visible&selectedEpic=EVOPS-34" target="_blank" style="font-size: 12px; font-weight: bold;">Fast to Azure</a></li>
					<li><a class="rplink" title="Jira Link" href="https://scival.atlassian.net/secure/RapidBoard.jspa?rapidView=254&view=planning&selectedIssue=EVOPS-6&versions=visible&epics=visible&selectedEpic=EVOPS-102" target="_blank" style="font-size: 12px; font-weight: bold;">Inspec Data Issue</a></li>
					<li><a class="rplink" title="Jira Link" href="https://scival.atlassian.net/secure/RapidBoard.jspa?rapidView=254&view=planning&selectedIssue=EVOPS-6&versions=visible&epics=visible" target="_blank" style="font-size: 12px; font-weight: bold;">Issues Without Epics</a></li>
					
					<%-- <li><a class="rplink" title="BD Issues"  href="${pageContext.servletContext.contextPath}/DAnalysis?id=1" style="font-size: 12px; font-weight: bold;">BD Issues</a></li>
					<li><a class="rplink" title="CPX Issues" href="${pageContext.servletContext.contextPath}/DAnalysis?id=2" style="font-size: 12px; font-weight: bold;">Cpx Issues</a></li> --%>
					<!-- <li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Georef</a></li>
					<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Ntis</a></li>
					<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Cbnb</a></li>
					<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Ept</a></li>
					<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Patent</a></li> -->
				</ul>
			</li>	
			<li style="color: #8B0000;"><img class="editimg" border="0" align="bottom" src="${pageContext.request.contextPath}/List-Option.png" title=""> <b>NYC</b></img>
			</br>
				<form name="f" action="${pageContext.servletContext.contextPath}/DAnalysis" method="post">
				<b style="color: black; font-size: 12px;">Select category to list issues:</b>
					<select name="issuecategory">
						<option value="bd">BD</option>
						<option value="cpx">CPX</option>
						<option value="aip">AIP</option>
						<option value="c84">C84</option>
						<option value="pch">PCH</option>
						<option value="elt">ELT</option>
						<option value="geo">GEO</option>
						<option value="chm">CHM</option>
						<option value="ins">INS</option>
						<option value="ibf">IBF</option>
						<option value="ept">EPT</option>
						<option value="nti">NTI</option>
						<option value="cbn">CBN</option>
						<option value="grf">GRF</option>
						<option value="gip">GIP</option>
						<option value="upt">UPT</option>
						<option value="eup">EUP</option>
						<option value="other">OTHER</option>
					</select>
					
					<input name="submit" type="submit" value="View Issues" />
					
				</form>
			</li>
			
			<li> to add a new issue, click <a href="${pageContext.servletContext.contextPath}/DAnalysis?addnewissue">here</a>
			<% // delete the issue info first that previously showed, so next time does not show up unless click the issue link 
	  		  session.removeAttribute("DATAISSUESINFO");
	  		  %>
  		 
			</li>
		</ul>
			
	</td>
	
		 <td>
		 
		 <ul class="innerlist">
					<li><a class="rplink" href="${pageContext.request.contextPath}/resources/ani512.dtd" style="font-size: 12px; font-weight: bold;">BD (cpx, geo, elt, chm, pch) </a></li>
					<li><a class="rplink" href="${pageContext.request.contextPath}/resources/inspec_xml.dtd" style="font-size: 12px; font-weight: bold;">Inspec</a></li>
					<li><a class="rplink" href="${pageContext.request.contextPath}/resources/Inspec_Backfile.dtd" style="font-size: 12px; font-weight: bold;">Inspec Backfile (IBF)</a></li>
					<li><a class="rplink" href="#" style="font-size: 12px; font-weight: bold;" onclick="noDTD();">NTIS</a></li>
					<li><a class="rplink" href="#" style="font-size: 12px; font-weight: bold;" onclick="noDTD();">CBNB</a></li>
					<li><a class="rplink" href="#" style="font-size: 12px; font-weight: bold;" onclick="noDTD();">EncompassPAT</a></li>
					<li><a class="rplink" href="#" style="font-size: 12px; font-weight: bold;" onclick="noDTD();">GeoRef</a></li>
					<li><a class="rplink" href="#" style="font-size: 12px; font-weight: bold;" onclick="noDTD();">US/EP Patents</a></li>
					
					
			</ul>
			
	 	</td>
	 
	</tr>
	
	<tr>
		<td>
			<div id="theader">Adhoc Data Analysis Report:</div>
		</td>
	
	   <td>
	    	<!-- <div id="theader"></div> -->
	    	
	   	</td>
	</tr>
	
	<tr>
		<td>
				<ul class="innerlist">
					<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Report1</a></li>
					<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Report2</a></li>
					<li><a class="rplink" href="template.jsp" style="font-size: 12px; font-weight: bold;">Report3</a></li>
					
					
					
			</ul>
		</td>
	</tr>
</table>	    
	 

		
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

<!-- Display Alert that DTD file not exist for datasets that does not have DTD -->
<script type="text/javascript">

function noDTD()
{
	alert('No DTD available');
}

</script>

</body>
</html>