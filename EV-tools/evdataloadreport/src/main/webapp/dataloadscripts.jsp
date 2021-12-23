<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Dataloading Directory and Scripts</title>

<link type="text/css" rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/static/css/reports.css" />
<link type="text/css" rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/static/css/main.css" />
<link type="text/css" rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/static/css/layout.css" />
	<link type="text/css" rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/static/css/tables.css" />
	

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
}

#sidebar li
{
padding-bottom: 0px;
}


</style>
</head>
<body>

	<div id="wrapper">
		<%@ include file="/views/includes/header.jsp"%>
		<div id="sidebar">
			<ul class="navlinks">
				<li><a href="#" onclick="showcor()">Data Corrections</a></li>
				<li><a href="#" onclick="showaipcpxnew()">AIP/CPX New Loading</a></li>
				<li><a href="#" onclick="showchmnew()">CHM New Loading</a></li>
				<li><a href="#" onclick="showpchnew()">PCH New Loading</a></li>
				<li><a href="#" onclick="showeltnew()">ELT New Loading</a></li>
				<li><a href="#" onclick="showgeonew()">GEO New Loading</a></li>
				<li><a href="#" onclick="showinsnew()">INS New Loading</a></li>
				<li><a href="#" onclick="showntisnew()">NTIS New Loading</a></li>
				<li><a href="#" onclick="showcbnbnew()">CBNB New Loading</a></li>
				<li><a href="#" onclick="showeptnew()">EPT New Loading</a></li>
				<li><a href="#" onclick="showgrfnew()">GRF New Loading</a></li>
				<li><a href="#" onclick="showuptnew()">UPT/EUP New Loading</a></li>
			</ul> 
		
		</div>

<%if((session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin"))){ %> 

		<div id="content">


			<h1>Dataloading Directories and Scripts</h1>

</br>
</br>

<!-- <p style="margin-top: 10px;"> to view dataloading directories & scripts, click on category link  on the left</p> -->


<!-- 1. Corrections -->
<div id="correction" style="display: none;">
			<!-- S300 Correction -->

			<table id="dldirscript">
				<caption>
					S300 Correction:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/CPX/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue">/data/incoming/update/AIP</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/bd/aip_correction/file</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/bd/aip_correction</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">aip_bdcor_good_dba.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Process Log dir:</td>
							<td id="dlvalue">/data/loading/bd/aip_correction/processed</td>
						</tr>
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/bd/aip_correction/file</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/bd/aip_correction/file</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/loading/bd/aip_correction</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">aip_bdcor_fast_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/loading/bd/aip_correction/fast</td>
						</tr>
							
				</tbody>
			</table>

</br>
</br>


			<!-- CPX Correction -->
			<table id="dldirscript">
				<caption>
					Cpx Correction:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/CPX/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue"><b>Update:</b>
							</br>/data/incoming/update/CPX</br>
							<b>Delet:</b>
							</br>/data/incoming/delete/CPX
							
							</td>
						</tr>
						<tr>
							<td id="dlkey">Link Files Script dir:</td>
							<td id="dlvalue">/data/loading/bd/corrections</td>
						</tr>
						<tr>
							<td id="dlkey">Link Files Script name:</td>
							<td id="dlvalue">link_new_correction_files.sh</td>
						</tr>
						
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/bd/corrections/file/cpx</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/bd/corrections</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">bd2_correction.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Process Log dir:</td>
							<td id="dlvalue">/data/loading/bd/corrections/processed</td>
						</tr>
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/bd/corrections/file/cpx</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/bd/corrections/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/loading/bd/corrections</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">bdcor_fast_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/loading/bd/corrections/fast</td>
						</tr>
							
				</tbody>
			</table>


</br>
</br>
			
			<!-- GRF correction -->
			<table id="dldirscript">
				<caption>
					Grf Correction:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/GRF</td>
						</tr>
						
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/corrections/grf/file</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/corrections/grf</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">good_grf_correction_dba.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Process Log dir:</td>
							<td id="dlvalue">/data/corrections/grf/processed</td>
						</tr>
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/corrections/grf/file</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/corrections/grf/file</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/corrections/grf</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">Extract Class embeded within Main Script</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/corrections/grf/fast</td>
						</tr>
							
				</tbody>
			</table>
			
</br>
</br>
			
			<!-- ELT correction -->
			<table id="dldirscript">
				<caption>
					ELT Correction:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/ELT/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue"><b>Update:</b>
							</br>/data/incoming/update/ELT</br>
							<b>Delet:</b>
							</br>/data/incoming/delete/ELT
							
							</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/corrections/elt/file</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/corrections/elt</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">elt_correction_dba.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Process Log dir:</td>
							<td id="dlvalue">/data/corrections/elt/processed</td>
						</tr>
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/corrections/elt/file</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/corrections/elt/file</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/corrections/elt</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">Extract Class embeded within Main Script</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/corrections/elt/fast</td>
						</tr>
							
				</tbody>
			</table>
			
</br>
</br>
			
			<!-- GEO correction -->
			<table id="dldirscript">
				<caption>
					GEO Correction:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/GEO/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue"><b>Update:</b>
							</br>/data/incoming/update/GEO</br>
							<b>Delet:</b>
							</br>/data/incoming/delete/GEO
							
							</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/corrections/geo/file</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/corrections/geo</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">geo_correction_dba.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Process Log dir:</td>
							<td id="dlvalue">/data/corrections/geo/processed</td>
						</tr>
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/corrections/geo/file</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/corrections/geo/file</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/corrections/geo</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">Extract Class embeded within Main Script</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/corrections/geo/fast</td>
						</tr>
							
				</tbody>
			</table>
						

</br>
</br>
			
			<!-- INS correction -->
			<table id="dldirscript">
				<caption>
					INS Correction:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/INS/XML</td>
						</tr>
						
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/corrections/ins/file</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/corrections/ins</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">ins_correction_dba.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Process Log dir:</td>
							<td id="dlvalue">/data/corrections/ins/processed</td>
						</tr>
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/corrections/ins/file</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/corrections/ins/file</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/corrections/ins</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">Extract Class embeded within Main Script</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/corrections/ins/fast</td>
						</tr>
							
				</tbody>
			</table>
			
</div>

		
<!-- 2. AIP& CPX NEW -->
<div id="aipcpxnew" style="display: block;">
			<!-- AIP New -->

			<table id="dldirscript">
				<caption>
					AIP New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/CPX/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue">/data/incoming/new/AIP</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/bd/aip/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/bd/aip</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">aip_convert.sh </br>
								aip_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/bd/aip/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/bd/aip/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/cpx</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">cpx_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/cpx/fast</td>
						</tr>
							
				</tbody>
			</table>
			
</br>
</br>

<!-- CPX New -->
<table id="dldirscript">
				<caption>
					CPX New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/CPX/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue">/data/incoming/new/CPX</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/bd/cpx/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/bd/cpx</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">cpx_convert.sh </br>
								cpx_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/bd/cpx/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/bd/cpx/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/cpx</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">cpx_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/cpx/fast</td>
						</tr>
							
				</tbody>
			</table>

		</div>


		
<!-- 3. CHM NEW -->
<div id="chmnew" style="display: none;">
			<!-- CHM New -->

			<table id="dldirscript">
				<caption>
					CHM New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/CHM/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue">/data/incoming/new/CHM</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/bd/chm/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/bd/chm</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">chm_convert.sh </br>
								chm_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/bd/chm/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/bd/chm/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/chm</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">chm_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/chm/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>		
		
		<!-- 4. PCH NEW -->
<div id="pchnew" style="display: none;">
			<!-- PCH New -->

			<table id="dldirscript">
				<caption>
					PCH New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/PCH/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue">/data/incoming/new/PCH</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/bd/pch/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/bd/pch</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">pch_convert.sh </br>
								pch_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/bd/pch/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/bd/pch/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/pch</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">pch_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/pch/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>		
		
		<!-- 5. ELT NEW -->
<div id="eltnew" style="display: none;">
			<!-- ELT New -->

			<table id="dldirscript">
				<caption>
					ELT New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/ELT/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue">/data/incoming/new/ELT</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/bd/elt/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/bd/elt</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">elt_convert.sh </br>
								elt_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/bd/elt/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/bd/elt/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/elt</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">elt_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/elt/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>				
		

		<!-- 6. GEO NEW -->
<div id="geonew" style="display: none;">
			<!-- GEO New -->

			<table id="dldirscript">
				<caption>
					GEO New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/GEO/XML</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue">/data/incoming/new/GEO</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/bd/geo/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/bd/geo</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">geo_convert.sh </br>
								geo_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/bd/geo/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/bd/geo/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/geo</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">geo_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/geo/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>						
		

		<!-- 7. INS NEW -->
<div id="insnew" style="display: none;">
			<!-- INS New -->

			<table id="dldirscript">
				<caption>
					INS New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/INS/XML</td>
						</tr>
						
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/ins/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/ins</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">ins_convert.sh </br>
								ins_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/ins/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/ins/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/ins</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">ins_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/ins/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>								
		
		
		<!-- 8. NTIS NEW -->
<div id="ntisnew" style="display: none;">
			<!-- NTIS New -->

			<table id="dldirscript">
				<caption>
					NTIS New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/NTIS</td>
						</tr>
						
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/ntis/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/ntis</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">ntis_convert.sh </br>
								ntis_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/ntis/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/ntis/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/ntis</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">ntis_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/ntis/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>								
		
		
		<!-- 9. CBNB NEW -->
<div id="cbnbnew" style="display: none;">
			<!-- CBNB New -->

			<table id="dldirscript">
				<caption>
					CBNB New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/CBN</td>
						</tr>
						
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/cbn/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/cbn</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">cbn_convert.sh </br>
								cbn_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/cbn/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/cbn/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/cbn</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">cbn_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/cbn/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>								
		

		<!-- 10. EPT NEW -->
<div id="eptnew" style="display: none;">
			<!-- EPT New -->

			<table id="dldirscript">
				<caption>
					EPT New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/EPT</td>
						</tr>
						<tr>
							<td id="dlkey">New Files dir:</td>
							<td id="dlvalue">/data/incoming/new/EPT</td>
						</tr>
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/archive/EPT</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/ept</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">ept_unpack.sh </br>
								ept_convert.sh </br>
								
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/ept</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/ept/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/ept</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">ept_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/ept/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>								
		
				
		<!-- 10. GRF NEW -->
<div id="grfnew" style="display: none;">
			<!-- GRF New -->

			<table id="dldirscript">
				<caption>
					GRF New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/GRF</td>
						</tr>
						
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/grf/[LoadNumber] </br>
								<b>LoadNumber:</b> replace [LoadNumber] with corresponding value
							</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/grf</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">grf_convert.sh </br>
								grf_load.sh
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/grf/[LoadNumber]</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/grf/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/grf</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">grf_extract.sh</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/grf/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>							
		
		
		<!-- 11. 12. UPT/EUP NEW -->
<div id="uptnew" style="display: none;">
			<!-- UPT/EUP New -->

			<table id="dldirscript">
				<caption>
					UPT/EUP New Load:
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">FTP dir:</td>
							<td id="dlvalue">/data/archive/IPDD</td>
						</tr>
						
						<tr>
							<td id="dlkey">Files to be processed dir:</td>
							<td id="dlvalue">/data/loading/ipdd2/raw</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script dir:</td>
							<td id="dlvalue">/data/loading/ipdd2</td>
						</tr>
						<tr>
							<td id="dlkey">Main Script name:</td>
							<td id="dlvalue">pat_load_all.sh</td>
						</tr>
						
						<tr>
							<td id="dlkey">Output File dir:</td>
							<td id="dlvalue">/data/loading/ipdd2/out</td>
						</tr>
						<tr>
							<td id="dlkey">Sqlldr Log dir:</td>
							<td id="dlvalue">/data/loading/ipdd2/logs</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script dir:</td>
							<td id="dlvalue">/data/fast/pat</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract Script name:</td>
							<td id="dlvalue">pat_weekly.sh </br>
								pat_weekly_cit.sh
							</td>
						</tr>
						<tr>
							<td id="dlkey">Fast Extract File(s) dir:</td>
							<td id="dlvalue">/data/fast/pat/fast</td>
						</tr>
							
				</tbody>
			</table>
			
		</div>															
		
<%} %> 
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


<script> 
function showcor() { 

document.getElementById("correction").style.display="block";

document.getElementById("aipcpxnew").style.display="none";
document.getElementById("pchnew").style.display="none";
document.getElementById("chmnew").style.display="none";
document.getElementById("eltnew").style.display="none";
document.getElementById("geonew").style.display="none";
document.getElementById("insnew").style.display="none";
document.getElementById("ntisnew").style.display="none";
document.getElementById("cbnbnew").style.display="none";
document.getElementById("eptnew").style.display="none";
document.getElementById("grfnew").style.display="none";
document.getElementById("uptnew").style.display="none";
}

function showaipcpxnew() { 

	document.getElementById("aipcpxnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("eltnew").style.display="none";
	document.getElementById("geonew").style.display="none";
	document.getElementById("insnew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}
	
	
function showchmnew() { 

	document.getElementById("chmnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("eltnew").style.display="none";
	document.getElementById("geonew").style.display="none";
	document.getElementById("insnew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}
		

function showpchnew() { 

	document.getElementById("pchnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("eltnew").style.display="none";
	document.getElementById("geonew").style.display="none";
	document.getElementById("insnew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}


function showeltnew() { 

	document.getElementById("eltnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("geonew").style.display="none";
	document.getElementById("insnew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}


function showgeonew() { 

	document.getElementById("geonew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("eltnew").style.display="none";	
	document.getElementById("insnew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}

function showinsnew() { 

	document.getElementById("insnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("eltnew").style.display="none";	
	document.getElementById("geonew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}

function showntisnew() { 

	document.getElementById("ntisnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("eltnew").style.display="none";	
	document.getElementById("geonew").style.display="none";
	document.getElementById("insnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}

function showcbnbnew() { 

	document.getElementById("cbnbnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("eltnew").style.display="none";	
	document.getElementById("geonew").style.display="none";
	document.getElementById("insnew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}
	
function showeptnew() { 

	document.getElementById("eptnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("eltnew").style.display="none";	
	document.getElementById("geonew").style.display="none";
	document.getElementById("insnew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}	

function showgrfnew() { 

	document.getElementById("grfnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("eltnew").style.display="none";
	document.getElementById("geonew").style.display="none";
	document.getElementById("insnew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("uptnew").style.display="none";
	}
	
function showuptnew() { 

	document.getElementById("uptnew").style.display="block";
	
	document.getElementById("correction").style.display="none";
	document.getElementById("aipcpxnew").style.display="none";
	document.getElementById("chmnew").style.display="none";
	document.getElementById("pchnew").style.display="none";
	document.getElementById("eltnew").style.display="none";	
	document.getElementById("insnew").style.display="none";
	document.getElementById("ntisnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";
	document.getElementById("eptnew").style.display="none";
	document.getElementById("grfnew").style.display="none";
	document.getElementById("cbnbnew").style.display="none";

	}	
</script>
</body>
</html>