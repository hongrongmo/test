<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Major Links</title>
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
	text-indent: 6px;
}
  
#majorlinks td , #otherlinks td
 {
 	vertical-align:  top;
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
		
		<div id="content">
		

<h1>Helpful Links</h1>

</br>
</br>
</br>
<!-- Table 1: major Links -->
<table id="majorlinks">
<caption style="font-weight: bold; font-size: 1.4em; color: navy; text-align: left; background-color: #E6E6FA;">EV &amp; Elsevier Links</caption>

<tbody>
<!-- row1,column1 -->
			<tr>
			<td colspan="3"></td>
			</tr>
			<tr>
			<td colspan="3"></td>
			</tr>
			
			
	<tr>
		<td style="padding-right: 400px;"><div id="theader">EV:</div></td>
		<td style="padding-right: 20px;"><div id="theader">Elsevier Apps:</div></td>

	</tr>
	<!-- row2, Colm1 -->
	<tr>
		<td>
			<!--  inner table -->
			<table>
				<tr>
					<td><a href="http://www.engineeringvillage.com/search/quick.url" target="_blank">Engineering Village (EV-Prod)</a></td>
				</tr>

				<tr>
					<td><a href="http://release1-www.engineeringvillage.com/home.url" target="_blank">Engineering Village (EV-Release)</a></td>
				</tr>
				<tr>
					<td><a href="http://cert3.engineeringvillage.com/search/quick.url" target="_blank">Engineering Village (EV-cert3)</a></td>
				</tr>
				
				<tr>
					<td><a href="http://www.engineeringvillage.com/HealthCheck.url" target="_blank">Engineering Village HealthCheck (EV-Prod)</a></td>
				</tr>

				<tr>
					<td><a href="http://www.engineeringvillage.com/status.url" target="_blank">EV Status Page (Prod)</a></td>
				</tr>
				
				<tr>
					<td><a href="http://www.engineeringvillage.com/ipcheck.jsp" target="_blank">EV Find MyIP (Prod)</a></td>
				</tr>


				<tr>
					<td><a href="http://support.lexisnexis.com/ip/report.asp" target="_blank">LexisNexis Client IP Verification</a></td>
				</tr>
				
				<tr>
					<td><a href="http://www.engineeringvillage.com/system/whoami.url?CID=whoami" target="_blank">EV Whoami (Prod)</a></td>
				</tr>
				
				<tr>
					<td><a href="http://backoffice.engineeringvillage.com/backoffice/menu.do" target="_blank">Back Office (Prod)</a></td>
				</tr>
				<tr>
					<td><a href="http://release1-backoffice.engineeringvillage.com/backoffice/menu.do" target="_blank">Back Office (Release)</a></td>
				</tr>
				<tr>
					<td><a href="http://emetrics.engineeringvillage.com/emetrics/menu.do" target="_blank">Usage Reports</a></td>
				</tr>

				<tr>
					<td><a href="http://evtools.engineeringvillage.com/login" target="_blank">EV Tools</a></td>
				</tr>
				
				<tr>
					<td><a href="https://chief.elsevier.com/main/auth/login" target="_blank">Chief Login</a></td>
				</tr>
				
				
				<tr>
					<td><a href="https://cs.elsevier.com/accountStructureMain.url" target="_blank">Find Chief ID for Ev Customer</a></td>
				</tr>
				
				
				
				<tr>
					<td><a href="https://gitblit.et-scm.com/" target="_blank">Git Blit</a></td>
				</tr>
				
				<tr>
					<td><a href="https://elsshare.science.regn.net/sites/STTSPMO/Project_Sites/Projects-SciVerse/Engineering_Village_UI_Refresh/default.aspx" target="_blank">EV SharePoint</a></td>
				</tr>
				
				<tr>
					<td><a href="http://stiki.elsevier.com/eHelpdesk/96" target="_blank">EV Stiki</a></td>
				</tr>
				
				
				<tr>
					<td><a href="http://help.engineeringvillage.com/Engineering_Village_Help_Left.htm#CSHID=Quick_srch_over.htm|StartTopic=Content%2FQuick_srch_over.htm|SkinName=svs_evillage" target="_blank">EV Help</a></td>
				</tr>
				<tr>
					<td><a href="http://help.elsevier.com/app/ask_ev/p/9639?_ga=1.216601118.98a396ddM027bM419fMa6f8M43d3285087a0i-2fca9ddf" target="_blank">EV Support Contact</a></td>
				</tr>
				<tr>
					<td><a href="http://blog.engineeringvillage.com/" target="_blank">EV What's New</a></td>
				</tr>	
				
				<tr>
					<td><a href="http://www.engineeringvillage.com/askanexpert/display.url" target="_blank">Ask An Expert</a></td>
				</tr>
				
				
				<!-- <tr>
					<td><a href="#">EV Team Contact</a></td>
				</tr> -->
			</table>
		</td>
		
		<!-- row2, colm2 -->
		<td>
			<table id="innertable">
					<tr>
						<td><a href="http://www.elsevier.com/" target="_blank">Elsevier Home Page</a></td>
					</tr>
					<tr>
						<td><a href="http://www.sciencedirect.com/" target="_blank">SienceDirect Home Page</a></td>
					</tr>
					
					<tr>
						<td><a href="http://lngcentral.lexisnexis.com/depts/Edit/AppSupp/Forms/SecurityBreach.aspx " target="_blank">SienceDirect SecurityBreach Check Tool</a></td>
					</tr>
					
					
					<tr>
						<td><a href="http://www.scopus.com/" target="_blank">Scopus Home Page</a></td>
					</tr>
					<tr>
						<td><a href="http://www.embase.com/" target="_blank">Embase Home Page</a></td>
					</tr>
					<tr>
						<td><a href="http://www.geofacets.com/" target="_blank">GeoFacets Home Page</a></td>
					</tr>
			</table>
		</td>
		
	</tr>
	
</tbody>

</table>

</br>



<!-- Other Links -->
<table id="otherlinks">
<caption style="font-weight: bold; font-size: 1.4em; color: navy; text-align: left; background-color: #E6E6FA; width:925px;">Other Links</caption>

<tbody>
<!-- row1,column1 -->
			<tr>
			<td colspan="3"></td>
			</tr>
			<tr>
			<td colspan="3"></td>
			</tr>
			
	
	<!-- row2, Colm1 -->
	<tr>
		<td>
			<a href="https://scival.atlassian.net/secure/Dashboard.jspa" target="_blank">JIra Home Page</a>
		</td>
	</tr>
	<tr>
		<td>
			<a href="https://peoplehub.reedelsevier.com/psp/IHPRD/?cmd=login&languageCd=ENG&" target="_blank">People Hub</a>
		</td>
	</tr>
	
	<tr>
		<td>
			<a href="http://nonsolus/home/index.asp" target="_blank">Non Solus</a>
		</td>
	</tr>
	
	<tr>
		<td>
			<a href="http://ebiz.app.science.regn.net" target="_blank">Time Sheet</a>
		</td>
	</tr>
	
	<tr>
		<td>
			<a href="http://stiki.elsevier.com/CHIEF/Home" target="_blank">CHIEF Stiki System</a>
		</td>
	</tr>
	
	
	
</tbody>
</table>

<!-- END of other Links -->		


</br>


<!-- Fast Links-->
<table>
<caption style="font-weight: bold; font-size: 1.4em; color: navy; text-align: left; background-color: #E6E6FA; width:925px;">Fast Links</caption>

<tbody>

	<tr>
		<td colspan="3"></td>
	</tr>
	<tr>
		<td colspan="3"></td>
	</tr>
	<tr>
		<td colspan="3"></td>
	</tr>
			
	<tr>
	<td>
		<table  id="fast">
			<thead>
		<tr>
			<th>Name:</th>
			<th>IP:Port</th>
			<th>DNS </th>
		</tr>
	</thead>
		
		<tbody>	
	
	<!-- row1, Colm1, Azure  -->
	<tr>
		<td colspan="3" style="background-color: #FFEFD5; font-weight: bold; font-size: 14px; color: #800000;"> Azure:
		</td>
	</tr>
	<tr>
		<td>EI-Main-P1</td>
		<td>137.135.116.146:15100</td>
		<td><a href="http://evprod14.cloudapp.net:15100" target="_blank">evprod14.cloudapp.net:15100</a></td>
	</tr>
	
	<tr>
		<td>EI-Main-STAGE</td>
		<td>137.135.117.237:15100</td>
		<td><a href="http://evprod06.cloudapp.net:15100" target="_blank">evprod06.cloudapp.net:15100</a></td>
	</tr>
	
	<tr>
		<td>EI-Main-DEV</td>
		<td> 137.135.117.250:15100</td>
		<td><a href="http://evprod08.cloudapp.net:15100" target="_blank">evprod08.cloudapp.net:15100</a></td>
	</tr>
	
	<tr>
		<td>EI-Lemm-P1</td>
		<td>137.135.117.237:15100</td>
		<td><a href="http://evprod14.cloudapp.net:80" target="_blank">evprod14.cloudapp.net:80</a></td>
	</tr>
	
	<tr>
		<td>FTP</td>
		<td>137.135.115.143:21</td>
		<td>evprod01.cloudapp.net:21</td>
	</tr>
	
	
	
	<!-- row2, Colm1, NDA  -->
	<tr>
		<td colspan="3" style="background-color: #FFEFD5; font-weight: bold; font-size: 14px; color: #800000;"> NDA:
		</td>
	</tr>
	
	<tr>
		<td>EI-Main</td>
		<td>208.68.141.86:15100</td>
		<td><a href="http://ei-main.nda.fastsearch.net:15100/" target="_blank">ei-main.nda.fastsearch.net:15100</a></td>
	</tr>
	
	<tr>
		<td>EI-Main-P1</td>
		<td>208.68.141.81:15100</td>
		<td><a href="http://ei-main-p1.nda.fastsearch.net:15100/" target="_blank">ei-main-p1.nda.fastsearch.net:15100</a></td>
	</tr>
	
	<tr>
		<td>EI-Main-P2</td>
		<td>208.68.141.82:15100</td>
		<td><a href="http://ei-main-p2.nda.fastsearch.net:15100/" target="_blank">ei-main-p2.nda.fastsearch.net:15100</a></td>
	</tr>
	
	<tr>
		<td>EI-Lemm-P1</td>
		<td>208.68.141.83:80</td>
		<td><a href="http://ei-lemm-p1.nda.fastsearch.net/" target="_blank">ei-lemm-p1.nda.fastsearch.net:80</a></td>
	</tr>
	
	<tr>
		<td>EI-Lemm-P2</td>
		<td>208.68.141.84:80</td>
		<td><a href="http://ei-lemm-p2.nda.fastsearch.net/" target="_blank">ei-lemm-p2.nda.fastsearch.net:80</a></td>
	</tr>
	
	<tr>
		<td>EI-Main-STAGE</td>
		<td>208.68.141.85:15100</td>
		<td><a href="http://ei-stage.nda.fastsearch.net:15100/" target="_blank">ei-stage.nda.fastsearch.net:15100</a></td>
	</tr>
	
	<tr>
		<td>EI-Lemm-STAGE</td>
		<td>208.68.141.85:80</td>
		<td><a href="http://ei-stage.nda.fastsearch.net/" target="_blank">ei-stage.nda.fastsearch.net:80</a></td>
	</tr>
	
	
	<tr>
		<td>EI-REPORTING</td>
		<td>208.68.141.162:19000</td>
		<td><a href="http://ei-reporting.nda.fastsearch.net:19000/" target="_blank">ei-reporting.nda.fastsearch.net:19000</a></td>
	</tr>
	
	<tr>
		<td>EI-FTP</td>
		<td>208.68.141.144:21</td>
		<td>ei-ftp.nda.fastsearch.net:21</td>
	</tr>
	
	
	
	
	
	
	</tbody>
	</table>

	</td>
</tr>
</table>	
	
<!-- END of Fast Links -->				

</br>
</br>

<!-- FAST Automaic Feeding Window -->
<table id="fast" style="width: 70%">

<thead>
		<tr>
			<th>Fast Indexig Window</th>
		</tr>
	</thead>
		
<tbody>

	<tr>
		<td colspan="3" style="padding: 6px 6px 10px 6px;">
		<b>Fast feeding automatic window schedule:</b></br></br>
		11am EST, (3PM GMT) on Wednesday  <b>to</b>  10:59am EST (2:59PM GMT) on Saturday
		
		</td>
	</tr>
	
	
	
</tbody>
</table>
				
				
</br>
</br>				
	
	
		
				

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