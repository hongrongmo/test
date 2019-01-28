<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Source Vendors</title>

<link type="text/css" rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/static/css/reports.css" />
<link type="text/css" rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/static/css/main.css" />
<link type="text/css" rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/static/css/layout.css" />
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

#srcvendor
{
font-size: 1.1em;
}

ul
 {
 	list-style-type: none;
 	
 } 
ul li
{
	padding-bottom: 0px;
 	margin-bottom: 0px;
 	margin-left: 0px;
 	
} 

</style>


</head>
<body>

<%if(session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin")){ %>

	<div id="wrapper">
		<%@ include file="/views/includes/header.jsp"%>
		<div id="sidebar">
			<%@ include file="/views/includes/navlinks.jsp"%>
		</div>


		<div id="content">


			<h1>Source Vendors</h1>
			<p>This page list DataSet Name, Source vendors, FTP site, roughly Count, #Files:</p>
			
			</br>
			
			<b>Download Script : </b>/data/loading/get_datafiles.sh
			
			</br>
			</br>
			
			<table id="srcvendor">	
			<tbody>
		<tr>
				<td><b>a. CBNB: (Chemical Business NewsBase)</b></td>
				<td>
					<ul>
						<li>ftp://cbnb:Chem1data@ftp.lexisnexis.com/*</li>
						<li><b>Count:</b> 1,000 ~ 3,000</li>
						<li><b>#Files:</b> ~5</li>
					</ul>
				</td>
			</tr>
			
			<tr>
				<td><b>b. NTIS: (National Technical Information Service)</b></td>
				<td>
					<ul>
						<li>https://dblease.ntis.gov/ntis/</li>
						<li><b>Count:</b> 1,000 ~ 2,000</li>
						<li><b>#Files:</b> 1~3</li>
					</ul>
				</td>
			</tr>
			
			<tr>
				<td><b>c. BD:(BD/OPSBANK I,II)</b></td>
				<td>
					<ul>
						<li><b>Opsbank II downloads from SFTP server:</b> (data/22, data/23,data/24, data/25, data/26, data/27)</li>
						<li>sftp://sftp-opsbank2.elsevier.com</li>
					</ul>
				</td>
			</tr>
			
			<tr>
				<td><b>c.1 CPX:</b></td>
				<td>
					<ul>
						<li><b>CPX XML NEW/Corrections:</b>
							<ul>
								<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/21/*</li>
								<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/23/*</li>
								<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/24/*</li>
								<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/25/*</li>
								<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/26/*</li>
								<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/27/*</li>
								<li><b>Count(NEW) :</b> 40,000 ~ 70,000</li>
								<li><b>#Files : </b> 6 ~ 8</li>
								<li><b>Count(Corr) :</b> 15,000 ~ 60,000</li>
								<li><b>#Files:</b> 6 ~ 12</li>
								<li><b>Count (Adhoc Files):</b> ~150,000</li>
								<li><b>#Files:</b> 1 ~ 8</li>
							</ul>
						</li>	
					</ul>
				</td>
			</tr>
			

			<tr>
				<td><b>c.2 CPX Articles In Press (AIP) Update:</b></td>
				<td>
					<ul>
						<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/30/*</li>
						<li>sftp://sftp-opsbank2.elsevier.com</li>
						<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/31/*</li>
						<li><b>Opsbank II downloads from SFTP server:</b> (data/30, data/31)</li>
						<li>sftp://sftp-opsbank2.elsevier.com</li>
						<li><b>Count(NEW) :</b> 3,000 ~ 6,000</li>
						<li><b>#Files:</b> 2</li>
						<li><b>Count(Corr) :</b> 2,000 ~ 5,000</li>
						<li><b>#Files:</b> 3</li>
					</ul>
				</td>
			</tr>
			
			<tr>
				<td><b>c.3 PCH:</b></td>
				<td>
					<ul>
						<li>ftp://eei:ftp4eei@ftp.elsevier.nl/81/*</li>
						<li><b>Count:</b> 1,000 ~ 2,000</li>
						<li><b>#Files:</b>1</li>
					</ul>
					<ul>
						<li><b>Paper corrections:</b>
						<ul>
							<li>ftp://eei:ftp4eei@ftp.elsevier.nl/16/pchcrxn*.*</li>
							<li>Opsbank II downloads from SFTP server:	(data/81, data/82, data/83)</li>
							<li>sftp://sftp-opsbank2.elsevier.com</li>
						</ul>
						</li>
					</ul>
				</td>
			</tr>
			
		<tr>
			<td><b>c.4 Encompass Lit:</b></td>
			<td>
				<ul>
					<li>ftp://lit:ftp4lit@ftp.elsevier.nl/77/</li>
					<li>ftp://lit:ftp4lit@ftp.elsevier.nl/78/</li>
					<li>ftp://lit:ftp4lit@ftp.elsevier.nl/79/</li>
					<li>Opsbank II downloads from SFTP server: (data/77, data/78, data/79)</li>
					<li>sftp://sftp-opsbank2.elsevier.com</li>
					<li><b>Count :</b> 1,000 ~ 2,000</li>
					<li><b>#Files:</b> 1</li>
				</ul>
			</td>
		</tr>	
		
		
		<tr>
			<td><b>c.5 Geobase:</b></td>
			<td>
				<ul>
					<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/90/*</li>
					<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/71/*</li>
					<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/72/*</li>
					<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/73/*</li>
					<li>sftp://sftp-opsbank2.elsevier.com</li>
					<li>Opsbank II downloads from SFTP server: (data/71, data/72, data/73)</li>
					<li>sftp://sftp-opsbank2.elsevier.com</li>
					<li><b>Count :</b> 3,000 ~ 6,000</li>
					<li><b>#Files:</b> 1</li>
					<li></li>
					<li><b>Geo Thesaurus (yearly):</b>
						<ul>
						<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/Thesauri/*</li>
					</ul>
			    	</li>
				</ul>
			</td>
		</tr>		
		
		<tr>
			<td><b>c.6 Chimica:</b></td>
			<td>
				<ul>
					<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/06/*</li>
					<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/07/*</li>
					<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/08/*</li>
					
					<li>Opsbank II downloads from SFTP server: (data/06, data/07, data/08)</li>
					<li>sftp://sftp-opsbank2.elsevier.com</li>
					<li><b>Count:</b> 4,000 ~ 10,000</li>
					<li><b>#Files:</b> 3</li>
					<li></li>
					<li><b>Castor:</b>
						<ul>
						<li>ftp://eei:ftp4eei@ftp.elsevier.nl//data/05/</li>
					</ul>
			    	</li>
				</ul>
			</td>
		</tr>		
		
		
		<tr>
			<td><b>d. Encompass PAT: (Newstar, Derwent, Innodata):</b></td>
			<td>
				<ul>
					<li>newstar ftp server</li>
					<li>ftp.derwent.co.uk</li>
					<li><b>Count:</b> 300 ~ 2,000</li>
					<li><b>#Files:</b> 1 ~ 2</li>
				</ul>
			</td>
		</tr>		
		
		<tr>
			<td><b>e. Inspec: (The Institution of Engineering and Technology):</b></td>
			<td>
				<ul>
					<li>ftp://inspec:Phys1data@ftp.lexisnexis.com/*</li>
					<li><b>Count:</b> 15,000 ~ 20,000</li>
					<li><b>#Files:</b> 1</li>
					<li><b>Citations:</b>
						<ul>
							<li>ftp://inspec:Phys1data@ftp.lexisnexis.com/Citations/*</li>
						</ul>
			    	</li>
				</ul>
			</td>
		</tr>		
		
		
		<tr>
			<td><b>f. GeoRef: (American Geosciences Institute):</b></td>
			<td>
				<ul>
					<li>ftp://elsevier@ftp.georef.org/*</li>
					<li><b>Count (New) :</b> 2,000 ~ 5,000</li>
					<li><b>#Files:</b> 1</li>
					<li><b>Count (Corr) :</b> 2,000 ~ 20,000</li>
					<li><b>#Files:</b> 3 </li>
					<li><b>Georef IP (Monthly):</b> </li>
					<li><b>Count :</b> ~ 181,000</li>
					<li><b>#Files:</b> 1</li>	
				</ul>
			</td>
		</tr>
		
		<tr>
			<td><b>g. Patent: LexisNexis Univentio:</b></td>
			<td>
				<ul>
					<li>ftp://ipdatadirect.lexisnexis.com/*	</li>
					<li><b>Count:</b> 20,000 ~ 60,000</li>
					<li><b>#Files:</b> 20 ~ 100</li>
				</ul>
			</td>
		</tr>	
		
			
		</tbody>
	</table>
			

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

<%} %>
</body>
</html>