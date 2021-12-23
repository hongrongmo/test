<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Monthly and Adhoc Data Loading</title>

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
				<li><a href="#" onclick="showcor()">Monthly Georef IP Processing</a></li>
				<li><a href="#" onclick="showcor1()">CPX Adhoc Correction</a></li>
				<li><a href="#" onclick="showcor2()">CPX Adhoc New DataLoading</a></li>
				<li><a href="#" onclick="showcor3()">CPX Adhoc Fast Extract</a></li>
			</ul>
		
		</div>

<%if((session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin"))){ %> 

		<div id="content">

			<h1>Monthly/Adhoc Data DataLoading</h1>

</br>
</br>


<!-- 1. GRF IP Loading -->
<div id="grfip" style="display: block;">

			<table id="weeklydataload">
				<caption>Georef IP Monthly Processing</caption>
				
				<tbody>
					<tr>
						<td id="weeklydlkey">Check Email Notifications to identify the file to process:</td>
						<td id="weeklydlvalue"><b>For Example: </b></br>
							
							<p>The following file is ready for pick up from our FTP site: </br>
							PROC_XML0815.zip (containing 185,389 references)</p>
						</td>
					</tr>
						
					<tr>
						<td id="weeklydlkey">Process the file:</td>
						<td id="weeklydlvalue">cd /data/loading/bd/corrections/file/grf
						</br>
						</br>
						 <b>1. For Update:</b>
							GrfIP_correction_eid.sh [filename] [loadnumber] ip_add 
							
						</br>
						</br>
							Then run deletion after make sure fast deleted GRF IP records marked for deletion from #1
						</br>
						</br>
						 <b>2. For Delete:</b>
						 GrfIP_correction_eid.sh [filename] [loadnumber] ip_delete 
							
						</br>
						</br>	
						<b>filename:</b> as in email notification i.e. PROC_XML0815.zip
						</br>
						</br>
						<b>loadnumber:</b> in format [YYYYWW] i.e. 401508
							
					 </td>
				  </tr>

					</tbody>
				</table>

</br>
</br>

</div>

		
<!-- 2. adhoc CPX Corrections -->
<div id="cpxadhoccorr" style="display: none;">
	<table id="weeklydataload">
					<caption>CPX Adhoc Correction</caption>
				
					<tbody>
						<tr>
							<td id="weeklydlkey">Check Email Notifications to identify the files to process:</td>
							<td id="weeklydlvalue"><b>For Example: </b></br>
							
							<p>The Content Helpdesk as asked me to deliver a number of CPX CARs to EI as update items. </br>
							I have made a file <b>CPX-ADHOC-upd-20150122.xml</b> with 15 items. </br>
							The file has been placed on the sFTP in subdirectory TEST for user EEI.</p>
							</td>
						</tr>
						
						
						<tr>
							<td id="weeklydlkey">Verify File has been downloaded:</td>
							<td id="weeklydlvalue">cd /data/archive/CPX/XML
							</br>
							ls -ltr CPX-ADHOC-upd-20150122.xml
							</td>
						</tr>
						
						
						<tr>
							<td id="weeklydlkey">Archive up old *out files:</td>
							<td id="weeklydlvalue">cd /data/loading/bd/corrections/file/cpx
							</br>
							mv *.out processed_out_files/
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Link adhoc correction files to our working directory:</td>
							<td id="weeklydlvalue">cd /data/loading/bd/corrections
							</br>
							./link_new_correction_files.sh 7
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Check files:</td>
							<td id="weeklydlvalue">cd /data/loading/bd/corrections/file/cpx
							</br>
							ls -ltr
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Make the files in a right order:</td>
							<td id="weeklydlvalue"><b>Rule of order:</b>
							</br>
							<ul style="list-style-type: decimal;">
								<li>adhoc delete (code: 21)</li>
								<li>adhoc update (code: 20)</li>
							</ul>
							
							</td>
						</tr>
						
						
						<tr>
							<td id="weeklydlkey">Perform correction:</td>
							<td id="weeklydlvalue">cd /data/loading/bd/corrections
							</br>
							</br>
							<b>[1] ADHOC delete</b></br>
							./bd2_correction.sh  adhocdelete  [YYYYWW21] cpx  |tee -a /data/loading/bd/corrections/processed/[YYYYWW21].txt

							</br>
							</br>
							<b>[2] ADHOC update</b></br>
							./bd2_correction.sh  adhocupdate  [YYYYWW20] cpx  |tee -a /data/loading/bd/corrections/processed/[YYYYWW20].txt
							
							</br>
							</br>
							<b>YYYYWW:</b> is current weeknumber from dataloading Ec2 prompt (i.e. 201435)
							
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Fast upload:</td>
							<td id="weeklydlvalue"><b>NDA:</b></br>
							
							/data/loading/bd/corrections/new_send_cor_to_fast.sh [YYYYWW]
							
							</br>
							</br>
							<b>Azure:</b></br>
							/data/loading/bd/corrections/new_send_cor_to_azure.sh [YYYYWW]
							
							</td>
						</tr>

						<tr>
							<td id="weeklydlkey"> Directories:</td>
							<td id="weeklydlvalue"><b>Home Directory: </b>/data/loading/bd/corrections
							</br>
							</br>
							<b>Correction Data Files: </b>/data/loading/bd/corrections/file
							</br>
							(i.e. cpx-adhoc-update-20150208_4541_3_1.zip)
							</br>
							</br>
							<b>Dump Directory: </b>/data/loading/bd/corrections/dump
							</br>
							</br>
							<b>Sqlldr Log Directory: </b>/data/loading/bd/corrections/logs
							</td>
						</tr>
						
					</tbody>
				</table>
			
</br>
</br>
</div>


		
<!-- 3. Ahoc New DataLoading -->
<div id="cpxadhocnew" style="display: none;">

			<table id="weeklydataload">
				<caption>CPX Adhoc New DataLoading
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">Converting Phase:</td>
							<td id="dlvalue">/data/loading/bd/bd_queueup.sh
							</br>
							</br>
							cd /data/loading/bd/cpx
							</br>
							</br>
							./cpx_convert.sh [YYYYWW] [AdhocFileName]
							
							</br>
							</br>
							
							<b>YYYYWW:</b> is current weeknumber from dataloading Ec2 prompt (i.e. 201435)
							</br>
							</br>
							<b>AdhocFileName:</b> is adhoc filename (i.e. cpx-adhoc-new-20150626.xml)
							</td>
						</tr>
						
						<tr>
							<td id="dlkey">Loading into DB Phase:</td>
							<td id="dlvalue">cd /data/loading/bd/cpx
							</br>
							</br>
							./cpx_load.sh [AdhocOutFileName] [DbServer]
							
							</br>
							</br>
							<b>AdhocOutFileName:</b> is sql loadable adhoc out file name (i.e. cpx-adhoc-new-20150626.xml.201528.out)
							</br>
							</br>
							<b>DbServer:</b> is RDS name (i.e. eid)
							</td>
						</tr>
						
						
						<tr>
							<td id="dlkey">QA Phase:</td>
							<td id="dlvalue">Check to see if anything was not either loaded or rejected as a duplicate record
							</br>
							</br>
							<img alt="New DataLoad QA" src="${pageContext.servletContext.contextPath}/static/images/QA-new-dataload.jpg">
							</br>
							</br>
							<b>aws_qa (bash alias)</b>
							</br>
							</br>
								/data/fast/chk_weekly_extracts.sh [YYYYWW]
						   </td>
						</tr>
							
						<tr>
							<td id="dlkey">Fast Extracts & Upload:</td>
							<td id="dlvalue">cd /data/fast/cpx
							</br>
							</br>
							./cpx_extract.sh [YYYYWW]
							
							</br>
							</br>
							cp /data/fast/cpx/fast/batch_[YYYYWW]_0001/EIDATA/[filename.zip] /data/fast//upload_to_fast
							</br>
							</br>
							cp /data/fast/cpx/fast/batch_[YYYYWW]_0001/PROD/[filename.ctl] /data/fast//upload_to_fast
							</br>
							</br>
							/data/fast/send_to_fast
							
						   </td>
						</tr>	
						
				</tbody>
			</table>
			
		</div>		
		

<!-- 4. Adhoc CPX Fast Extract -->
<div id="cpxadhocextract" style="display: none;">

			<table id="weeklydataload">
				<caption>CPX Adhoc Fast Extract</caption>
				
				<tbody>
					<tr>
						<td id="weeklydlkey">CPX Adhoc Fast Extract:</td>
						<td id="weeklydlvalue"><b>to extract Cpx records due to Manual update (i.e 14 An Accessnumber, AbstractData)</b>
						</br>
						</br>
						 <b>1. cd /ebs_scratch/Hanan_AN11k_update/fast_extract</b>
							
						</br>
						</br>
						<b>2. vi cpx_extract.sh</b>
						</br>
						</br>
							Modify the script: add temp tabe name , schema that has the temp table, and password
						</br>
						</br>	
						 <b>3. Run Fast extract Script</b>
						 </br>
						 </br>
						 ./cpx_extract.sh [loadnumber]
							
						</br>
						</br>	
							<b>loadnumber:</b> Use loadnumber "1" to extract whole table regadless actual loadnumbers in temp table
							</br>
							</br>
							For example:</br>
							./cpx_extract.sh 1
							
						</br>
						</br>
					 </td>
				  </tr>
				  
				  <tr>
						<td id="weeklydlkey">CPX Adhoc Fast Upload:</td>
						<td id="weeklydlvalue"><b>to Upload Fast extract LoadUnit</b>
						</br>
						</br>
						 <b>1. cd /data/fast</b>
							
						</br>
						</br>
						 <b>2. Run Fast Upload Script</b>
						 </br>
						 </br>
						 ./sendfastunits.sh [ZipFileName with Absolute Path]
							
						</br>
						</br>	
							For example:</br>
							./sendfastunits.sh /ebs_scratch/Hanan_AN11k_update/fast_extract/fast/batch_1_0001/EIDATA/1441308887627_cpx_add_1-0001.zip
							
						</br>
						</br>
					 </td>
				  </tr>

					</tbody>
				</table>

</br>
</br>

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

document.getElementById("grfip").style.display="block";

document.getElementById("cpxadhocnew").style.display="none";
document.getElementById("cpxadhoccorr").style.display="none";
document.getElementById("cpxadhocextract").style.display="none";


}

function showcor1() { 
	document.getElementById("cpxadhoccorr").style.display="block";
	
	document.getElementById("cpxadhocnew").style.display="none";
	document.getElementById("grfip").style.display="none";
	document.getElementById("cpxadhocextract").style.display="none";
	}
	
function showcor2() { 
	document.getElementById("cpxadhocnew").style.display="block";
	
	document.getElementById("cpxadhoccorr").style.display="none";
	document.getElementById("grfip").style.display="none";
	document.getElementById("cpxadhocextract").style.display="none";
	}
	
function showcor3() { 
	document.getElementById("cpxadhocextract").style.display="block";
	
	document.getElementById("cpxadhocnew").style.display="none";
	document.getElementById("grfip").style.display="none";
	document.getElementById("cpxadhoccorr").style.display="none";
	}

</script>



</body>
</html>