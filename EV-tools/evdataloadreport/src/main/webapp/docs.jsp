<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Documentation and References</title>
<link type="text/css" rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/static/css/reports.css" />
<link type="text/css" rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/static/css/main.css" />
	<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/tables.css"/>

<style type="text/css">
html,body {
	margin: 0;
	padding: 0;
	height: 100%;
}

#wrapper {
	min-height: 100%;
	position: relative;
}

#header {
	background: #ededed;
	padding: 10px;
}

#sidebar {
	position: absolute;
	top: 0;
	bottom: 0;
	left: 0;
	width: 180px;
	background: #148C75;
	margin-top: 100px;
}

#content {
	padding-bottom: 80px; /* Height of the footer element */
	padding-left: 200px;
}

#footer {
	background: #ffab62;
	width: 100%;
	height: 40px;
	position: absolute;
	bottom: 0;
	left: 0;
	text-indent: 6px;
}

#docs
{
padding-top: 15px;
}

</style>


</head>
<body>

<div id="wrapper">
		<%@ include file="/views/includes/header.jsp"%>
		<div id="sidebar">
			<%@ include file="/views/includes/navlinks.jsp"%>
		</div>


		<div id="content">


			<h1>References / Documentation</h1>
			
			<div id="docs">
			
				<table id="majorlinks">
					<tbody>
					<tr>
						<td><a href="https://docs.google.com/document/d/1a1YPAeHC0wa4pdWGZV9dh6NmkceIm0QQ9YuZQAxneS0/edit#heading=h.gjdgxs" target="_blank">EV XMLSearch Interface Guide (OpenXML)</a></td>
					</tr>
					<tr>
						<td><a href="https://docs.google.com/document/d/1DDdeJz11nHudtHlPiORWw1At8fxqxxNFhHe_Prfo06M/edit#heading=h.gjdgxs" target="_blank">In Process GeoRef (GRF IP)</a></td>
					</tr>
					<tr>
						<td><a href="https://drive.google.com/drive/folders/0Bzdv19B4Cg7YbDNUb1VGcjN0S2c" target="_blank">EngineeringVillage Disaster Recovery (DR)</a></td>
					</tr>
					<tr>
						<td><a href="https://drive.google.com/drive/folders/0Bzdv19B4Cg7YbDNUb1VGcjN0S2c" target="_blank">Engineering Village Disaster Recover Report</a></td>
					</tr>
					<tr>
						<td><a href="https://drive.google.com/drive/folders/0Bzdv19B4Cg7YbDNUb1VGcjN0S2c" target="_blank">EV Fast Search Index Functional Specification</a></td>
					</tr>
					
					<tr>
						<td><a href="${pageContext.servletContext.contextPath}/resources/FAST_ESP_Content_Processing_Workflow.docx" target="_blank">FAST ESP Content Processing Workflow</a></td>
					</tr>
					
					<tr>
						<td><a href="https://docs.google.com/document/d/1NzbitMUZ_JbStsUWlkjCLQeE4Wc5U3xmkhc2iujEo3Q/edit#heading=h.qm93csthx0wy" target="_blank">New Alert Application Process Guide</a></td>
					</tr>
					<tr>
						<td><a href="https://drive.google.com/open?id=0B_JbQ95Jeq6seG5vcWxhMmVNTUE" target="_blank">Git Branching Procedures for Scopus Development</a></td>
					</tr>
					<tr>
						<td><a href="${pageContext.servletContext.contextPath}/resources/GitLabManual.doc" target="_blank">Git Basic Commands</a></td>
					</tr>
					<tr>
						<td><a href="${pageContext.servletContext.contextPath}/resources/Git-Usefule-Commands.pdf" target="_blank">Git Useful Commands</a></td>
					</tr>
					<tr>
						<td><a href="${pageContext.servletContext.contextPath}/resources/eGitInstallation.doc" target="_blank">eGit Plugin for Eclipse Installation</a></td>
					</tr>
					<!-- Temp comment out due to space limitation on my computer -->
					<%-- <tr>
						<td><a href="${pageContext.servletContext.contextPath}/resources/GIT_training.mp4" target="_blank">Git Training</a></td>
					</tr> --%>
					
					
					
		
				</tbody>
			</table>
				
			</div>
		</div>
				



		<div id="footer">
			<p id="copyright">
				Copyright &copy; &nbsp;<%=new java.util.Date().getYear() + 1900%>&nbsp;<a
					title="Elsevier home page (opens in a new window)" target="_blank"
					href="http://www.elsevier.com">Elsevier B.V.</a> All rights
				reserved.

			</p>
		</div>


	</div>
	
			
</body>
</html>

