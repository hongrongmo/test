<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Weekly DataLoading</title>

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
				<li><a href="#" onclick="showcor()">S300 Correction</a></li>
				<li><a href="#" onclick="showcor1()">CPX Correction</a></li>
				<li><a href="#" onclick="showcor2()">GRF Correction</a></li>
				<li><a href="#" onclick="showcor3()">New DataLoading</a></li>
				<li><a href="#" onclick="showcor4()">Patent Loading</a></li>
				<li><a href="#" onclick="showcor5()">Email Alerts</a></li>
			</ul>
		
		</div>

<%if((session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").equals("admin"))){ %>  
		<div id="content">

			<h1>Weekly DataLoading</h1>

</br>
</br>


<!-- 1. S300 Corrections -->
<div id="weeklys300corr" style="display: block;">
			<!-- S300 Correction -->

				<table id="weeklydataload">
					<caption>S300 Correction (PUI)</caption>
				
					<tbody>
						<tr>
							<td id="weeklydlkey">Check BD Email Notifications to identify the files to process (i.e. Seq #):</td>
							<td id="weeklydlvalue"><b>For Example: </b></br>
							
							<p>A delivery has been sent for order id : 4361 and delivery sequence number: 19
							</br>
							<b>File location  :</b> ftp://localhost//sftp/customers/eei/data/31/</br>
							<b>Order name     :</b> Compendex indexed S200-S300 - UPDATE - Eng. Info. - 002</br>
							<b>Quantity       :</b> 13195</p>

							
							</br>
							<p>A delivery has been sent for order id : 4341 and delivery sequence number: 19
							</br>
							<b>File location  :</b> ftp://localhost//sftp/customers/eei/data/31/</br>
							<b>Order name     :</b> Compendex not indexed S200-S300 - UPDATE - Eng. Info. - 002</br>
							<b>Quantity       :</b> 237</p>
						    </br>
							<p>A delivery has been sent for order id : 4362 and delivery sequence number: 19
							</br>
							<b>File location  :</b> ftp://localhost//sftp/customers/eei/data/31/</br>
							<b>Order name     :</b> Compendex not indexed S300 - UPDATE - Eng. Information - 002</br>
							<b>Quantity       :</b> 291</p>
							</br>  
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Process the files (FAST Upload included):</td>
							<td id="weeklydlvalue">cd /data/loading/bd/aip_correction
							</br>
							</br>
							./aip_bdcor_good_dba.sh  [YYYYWW05] |tee -a /data/loading/bd/aip_correction/processed/[YYYYWW205].txt
							
							</br>
							</br>
							<b>YYYYWW:</b> is current weeknumber from dataloading Ec2 prompt (i.e. 201435)
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey"> Directories:</td>
							<td id="weeklydlvalue"><b>Home Directory: </b>/data/loading/bd/aip_correction
							</br>
							</br>
							<b>Data Source Files: </b>/data/incoming/update/AIP
							</br>
							</br>
							<b>Correction Data Files: </b>/data/loading/bd/aip_correction/file
							</br>
							(i.e. 20153605_s300_combined.zip)
							</br>
							</br>
							<b>Dump Directory: </b>/data/loading/bd/aip_correction/dump
							</br>
							</br>
							<b>Lookup Log Directory: </b>/data/loading/bd/aip_correction/logs
							</td>
						</tr>
						
					</tbody>
				</table>

</br>
</br>

</div>

		
<!-- 2. CPX Corrections -->
<div id="weeklycpxcorr" style="display: none;">
	<table id="weeklydataload">
					<caption>CPX Correction (Accessnumber)</caption>
				
					<tbody>
						<tr>
							<td id="weeklydlkey">Check BD Email Notifications to identify the files to process (i.e. Seq #):</td>
							<td id="weeklydlvalue"><b>For Example: </b></br>
							
							<p>A delivery has been sent for order id : 4365 and delivery sequence number: 19
							</br>
							<b>File location  :</b> ftp://localhost//sftp/customers/eei/data/23/</br>
							<b>Order name     :</b> CPXTOC unindexed - UPDATE - Engineering Information - 003</br>
							<b>Quantity       :</b> 113</p>
							</br>
							<p>A delivery has been sent for order id : 4321 and delivery sequence number: 19
							</br>
							<b>File location  :</b> ftp://localhost//sftp/customers/eei/data/27/</br>
							<b>Order name     :</b> Compendex - DELETE - Engineering Information - 004</br>
							<b>Quantity       :</b> 7</p>
							
							</br>
							<p>A delivery has been sent for order id : 4401 and delivery sequence number: 19
							</br>
							<b>File location  :</b> ftp://localhost//sftp/customers/eei/data/26/</br>
							<b>Order name     :</b> Compendex Conferences - UPDATE - Engineering Information -03</br>
							<b>Quantity       :</b> 4052</p>

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
							<td id="weeklydlkey">Link new correction files to our working directory:</td>
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
								<li>TOC delete (code: 03)</li>
								<li>EEI delete (code: 04)</li>
								<li>CONF delete (code: 08)</li>
								<li>TOC update (code: 01)</li>
								<li>EEI update (code: 02)</li>
								<li>CONF update (code: 07)</li>
							</ul>
							
							</td>
						</tr>
						
						
						<tr>
							<td id="weeklydlkey">Perform correction:</td>
							<td id="weeklydlvalue">cd /data/loading/bd/corrections
							</br>
							</br>
							<b>[1] TOC delete</b></br>
							./bd2_correction.sh tocdelete  [YYYYWW03] cpx  |tee -a /data/loading/bd/corrections/processed/[YYYYWW03].txt
								
							</br>
							</br>
							<b>[2] EEI delete</b></br>
							./bd2_correction.sh eeidelete  [YYYYWW04] cpx  |tee -a /data/loading/bd/corrections/processed/[YYYYWW04].txt
							
							</br>
							</br>
							
							<b>[3] CONF delete</b></br>
							./bd2_correction.sh confdelete  [YYYYWW08] cpx  |tee -a /data/loading/bd/corrections/processed/[YYYYWW08].txt
							
							</br>
							</br>
							<b>[4] TOC update</b></br>
							./bd2_correction.sh tocupdate  [YYYYWW01] cpx  |tee -a /data/loading/bd/corrections/processed/[YYYYWW01].txt
							
							</br>
							</br>
							<b>[5] EEI update</b></br>
							./bd2_correction.sh eeiupdate  [YYYYWW02] cpx  |tee -a /data/loading/bd/corrections/processed/[YYYYWW02].txt
							
							</br>
							</br>
							<b>[6] CONF update</b></br>
							./bd2_correction.sh confupdate  [YYYYWW07] cpx  |tee -a /data/loading/bd/corrections/processed/[YYYYWW07].txt
							
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
							(i.e. cpx_eei_update_4301_18_1.zip)
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


		
<!-- 3. New DataLoading -->
<div id="weeklynewload" style="display: none;">

			<table id="weeklydataload">
				<caption>
					New DataLoading (BD/NON-BD)
				</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">Converting Phase:</td>
							<td id="dlvalue"><b>Non-BD Datasets:</b>
							</br></br>
							/data/loading/nonbd_convert_all.sh
							</br>
							</br>
							/data/loading/ept/ept_unpack.sh [filename] #Have to get filename from Newstar email
							</br>
							</br>
							/data/loading/ept/ept_convert.sh [filename]
							
							</br>
							</br>
							/data/loading/nonbd_load_all.sh
							
							</br>
							</br>
							<b>BD Datasets:</b>
							</br>
							</br>
							/data/loading/bd/bd_queueup.sh
							
							</br>
							</br>
							/data/loading/bd/bd_convert.sh
							
							</br>
							</br>
							/data/loading/bd/bd_load.sh
							
							</td>
						</tr>
						<tr>
							<td id="dlkey">QA Phase:</td>
							<td><b>#Check to see if anything was not either loaded or rejected as a duplicate record</b>
							</br>
							</br>
							<img alt="New DataLoad QA" src="${pageContext.servletContext.contextPath}/static/images/QA-new-dataload.jpg">
							</br>
							</br>
							<b>aws_qa (bash alias)</b>
							</br>
							</br>
								/data/fast/chk_weekly_extracts.sh [YYYYWW]
							
							</br>
							</br>
							<b>YYYYWW:</b> is current weeknumber from dataloading Ec2 prompt (i.e. 201435)
							
						   </td>
						</tr>
							
						<tr>
							<td id="dlkey">Fast Extracts & Upload:</td>
							<td>/data/fast/bulk_extract.sh
							</br>
							</br>
							/data/fast/send_to_fast
							
						   </td>
						</tr>	
						
						<tr>
							<td id="dlkey">CPX Reference Loading (run from CronJob):</td>
							<td>/data/loading/bd/bd_ref_load.sh</td>
						</tr>
						
						
						<tr>
							<td id="dlkey">Bulletins:</td>
							<td>/data/loading/bulletins/bulletins_wk.sh
							</br>
							</br>
							/data/loading/bulletins/bulletins_mo.sh
							
						   </td>
						</tr>
						
						<tr>
							<td id="dlkey">Lookups (run from CronJob):</td>
							<td>/data/loading/lookups/lookups_main.sh
							
						   </td>
						</tr>
						
							
				</tbody>
			</table>
			
		</div>		
		
<!-- 4. GRF Correction -->
<div id="weeklygrfcorr" style="display: none;">
			<table id="weeklydataload">
				<caption>GRF Correction (ID_number)</caption>
				
				<tbody>
					<tr>
							<td id="dlkey">Check Email Notifications to identify the files to process (i.e. Seq #):</td>
							<td id="weeklydlvalue"><b>For Example: </b></br>
							
							<p>The following files are ready for pick up from our FTP site:</br>
							 EA201534.zip (containing 2598 items)</br>
							 EU201534.XML (containing 49 items) </br>
							 ED201534.XML (containing 2 items)</p>
							</br>
							
							<b>Note for File Name:</b></br>
							<ul>
								<li><b>EA: </b> GRF Add File</li>
								<li><b>EU: </b> GRF Update File</li>
								<li><b>ED: </b> GRF Delete File
							</ul>

							
						</td>
					</tr>
					
					<tr>
						<td id="dlkey">Verify the file:</td>
						<td id="weeklydlvalue">cd /data/archive/GRF/</br>
						ls -ltr EU* ED*
						</td>
					</tr>
					
					<tr>
						<td id="dlkey">Perform Correction (only take EU and ED files):</td>
						<td id="weeklydlvalue"><b>set:</b></br>
						<ul>
							<li>ED (Code: 10)</li>
							<li>EU (Code: 11)</li>
						</ul>
						</br>
						
						cd /data/corrections/grf
						</br>
						</br>
						./good_grf_correction_dba.sh [FileName] [YYYYWW11] |tee -a /data/corrections/grf/processed/[YYYYWW11].txt
						
						</br>
						</br>
						<b>filename:</b> is updatefile name as in email notification (i.e. EU201534.XML)
						</br>
						</br>
						<b>YYYYWW:</b> is current weeknumber from dataloading Ec2 prompt (i.e. 201435)
						
						</td>
					</tr>
					
					<tr>
							<td id="weeklydlkey"> Directories:</td>
							<td id="weeklydlvalue"><b>Home Directory: </b>/data/corrections/grf
							</br>
							</br>
							<b>Correction Data Files: </b>/data/corrections/grf/file
							</br>
							(i.e. EU201534.XML)
							</br>
							</br>
							<b>Dump Directory: </b>/data/corrections/grf/dump
							</br>
							</br>
							<b>Lookup Sqlldr Log Directory: </b>/data/corrections/grf/logs
							</td>
						</tr>
							
							
				</tbody>
			</table>
			
		</div>		
		
<!-- 5. Patent -->
<div id="weeklypat" style="display: none;">
			<!-- ELT New -->

			<table id="weeklydataload">
				<caption>Patent New Load</caption>
				
				<tbody>
						<tr>
							<td id="weeklydlkey">Check BD Email Notifications to identify the files to process:</td>
							<td id="weeklydlvalue"><b>For Example:</b></br></br>
							<b>Authority: EP</b></br>
							<b>Batch Type:</b>	Xml, UpdateRequest</br>
							<b>Batches:</b>	38</br>
							<b>Counts:</b>	20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 19999, 20000, 19999, 20000, 20000, 20000, 19999, 20000, 20000, 20000, 20000, 20000, 19994, 19963, 19981, 19967, 19984, 19988, 19991, 19988, 19986, 19990, 19991, 20000, 19995, 17176</br>
							<b>Filenames:</b>	674437.zip, 674438.zip, 674439.zip, 674440.zip, 674441.zip, 674442.zip, 674443.zip, 674444.zip, 674445.zip, 674446.zip, 674447.zip, 674448.zip, 674449.zip, 674450.zip, 674451.zip, 674452.zip, 674453.zip, 674454.zip, 674455.zip, 674456.zip, 674457.zip, 674458.zip, 674459.zip, 674460.zip, 674461.zip, 674462.zip, 674463.zip, 674464.zip, 674465.zip, 674466.zip, 674467.zip, 674468.zip, 674469.zip, 674470.zip, 674471.zip, 674472.zip, 674473.zip, 674474.zip
							</br></br>

							<b>Authority: US</b></br>
							<b>Batch Type:</b>	Xml, UpdateRequest</br>
							<b>Batches:</b>	104</br>
							<b>Counts:</b>	20000, 19998, 19994, 20000, 19999, 19997, 19995, 19997, 19998, 20000, 19999, 19998, 19997, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 19999, 19998, 19996, 19992, 19997, 19994, 19998, 19998, 19997, 19993, 19999, 20000, 20000, 20000, 20000, 20000, 20000, 19999, 19998, 20000, 20000, 20000, 20000, 20000, 20000, 19999, 19999, 19996, 19996, 20000, 19998, 19999, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 20000, 19999, 19999, 19998, 20000, 20000, 19999, 20000, 20000, 20000, 20000, 20000, 19999, 20000, 19999, 19999, 11827
							</br>
							<b>Filenames:</b>	674516.zip, 674517.zip, 674518.zip, 674519.zip, 674520.zip, 674521.zip, 674522.zip, 674523.zip, 674524.zip, 674525.zip, 674526.zip, 674527.zip, 674528.zip, 674529.zip, 674530.zip, 674531.zip, 674532.zip, 674533.zip, 674534.zip, 674535.zip, 674536.zip, 674537.zip, 674538.zip, 674539.zip, 674540.zip, 674541.zip, 674542.zip, 674543.zip, 674544.zip, 674545.zip, 674546.zip, 674547.zip, 674548.zip, 674549.zip, 674550.zip, 674551.zip, 674552.zip, 674553.zip, 674554.zip, 674555.zip, 674556.zip, 674557.zip, 674558.zip, 674559.zip, 674560.zip, 674561.zip, 674562.zip, 674563.zip, 674564.zip, 674565.zip, 674566.zip, 674567.zip, 674568.zip, 674569.zip, 674570.zip, 674571.zip, 674572.zip, 674573.zip, 674574.zip, 674575.zip, 674576.zip, 674577.zip, 674578.zip, 674579.zip, 674580.zip, 674581.zip, 674582.zip, 674583.zip, 674584.zip, 674585.zip, 674586.zip, 674587.zip, 674588.zip, 674589.zip, 674590.zip, 674591.zip, 674592.zip, 674593.zip, 674594.zip, 674595.zip, 674596.zip, 674597.zip, 674598.zip, 674599.zip, 674600.zip, 674601.zip, 674602.zip, 674603.zip, 674604.zip, 674605.zip, 674606.zip, 674607.zip, 674608.zip, 674609.zip, 674610.zip, 674611.zip, 674612.zip, 674613.zip, 674614.zip, 674615.zip, 674616.zip, 674617.zip, 674618.zip, 674619.zip
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Get patent files:</td>
							<td id="weeklydlvalue"><b>Use Screen: </b></br></br>
							For Example: screen -S pat201536
							
							</br>
							</br>
							/data/loading/get_ipdd_patents.sh
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Check downloaded files:</td>
							<td id="weeklydlvalue"><b>heck downloaded files and compare with email notification above.</b></br></br>
							cd /data/loading/ipdd2/tmp
							</br>
							</br>
							ls
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Move downloaded raw files:</td>
							<td id="weeklydlvalue">move raw files from /ora/data_load/ipdd2/tmp to /data/loading/ipdd2</br></br>
							<b>Note: files have to be moved not copied to /data/loading/ipdd2/raw</b>
							</br>
							</br>
							cd /data/loading/ipdd2
							</br>
							</br>
							./move_source_files.sh
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Check zipcheck.txt:</td>
							<td id="weeklydlvalue">more zipcheck.txt</br></br>
							<b>For Example:</b>
							</br>
							</br>
							No errors detected in compressed data of /data/loading/ipdd2/tmp/514960.zip.
							</br>
							</br>
							Note: The last zip file should match the last one in email notification from Univentio
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">convert, load upt_master and ref tables on EID:</td>
							<td id="weeklydlvalue">./pat_load_all.sh [YYYYWW] >output/[YYYYWW].txt 2>&1
							</br>
							</br>
							<b>YYYYWW:</b> is current weeknumber from dataloading Ec2 prompt (i.e. 201536)
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Check Logs:</td>
							<td id="weeklydlvalue">/data/loading/bin/checklogs.sh . 2 |grep [YYYYWW]
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Generate 3 sql files for updating counts:</td>
							<td id="weeklydlvalue">./upt_count_update.sh [YYYYWW] EID
							</br>
							</br>
							ls -ltr
							</br>
							</br>
							<b>For Example: </b>
							</br>
							-rw-r--r--  1 ec2-user ec2-user 17739749 Dec 23 09:59 pat_cit_201501.sql</br>
							-rw-r--r--  1 ec2-user ec2-user   755904 Dec 23 09:59 pat_ref_201501.sql</br>
							-rw-r--r--  1 ec2-user ec2-user   383383 Dec 23 09:59 non_pat_ref_201501.sql</br>
							</br>
							</br>
							
							./update_ref_count.sh [YYYYWW] EID
							
							</br>
							</br>
							./update_cit_count.sh [YYYYWW] EID
							
							
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Check Record Count:</td>
							<td id="weeklydlvalue">sqlplus ba_loading/ny5av@eid
							</br>
							<ul>
								<li>select count(REF_MID) from PATENT_REFS where load_number = [YYYYWW];</li>
								<li>select count(M_ID) from NON_PAT_REFS where load_number = [YYYYWW];</li>
								<li>select count(M_ID) from UPT_MASTER where load_number = [YYYYWW];</li>
								<li>select count(REF_MID) from PATENT_REFS;</li>
								<li>select count(M_ID) from NON_PAT_REFS;</li>
								<li>select count(M_ID) from UPT_MASTER;</li>
							</ul>
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Load data to EIB, EIA:</td>
							<td id="weeklydlvalue">cd /data/loading/ipdd2/eib
							</br>
							</br>
							./pat_load_all.sh [YYYYWW]
							
							</br>
							</br>
							<b>Check logs:</b>
							
							</br>
							/data/loading/bin/checklogs.sh . 2 |grep [YYYYWW]
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Check counts:</td>
							<td id="weeklydlvalue">Open Two Screens: (one for EIB, one for EIA)</br></br>
							sqlplus ba_loading/ny5av@eib
							</br>
							sqlplus ba_loading/ny5av@eia
							</br>
							set head off;
							</br>
							</br>
							
							<b>Run below queries in both screens:</b>
							</br>
								<ul>
									<li>select count(REF_MID) from PATENT_REFS where load_number = [YYYYWW];</li>
									<li>select count(M_ID) from NON_PAT_REFS where load_number = [YYYYWW];</li>
									<li>select count(M_ID) from UPT_MASTER where load_number = [YYYYWW];</li>
									<li>select count(REF_MID) from PATENT_REFS;</li>
									<li>select count(M_ID) from NON_PAT_REFS;</li>
									<li>select count(M_ID) from UPT_MASTER;</li>
								</ul>
							</td>
						</tr>
						
					<tr>
							<td id="weeklydlkey">Fast Extract:</td>
							<td id="weeklydlvalue">cd /data/fast/pat
							</br>
							<ul style="list-style-type: decimal;">
								<li>./pat_weekly.sh [YYYYWW]</li>
								<li><b>Check count:</b>
									<ul>
										<li>$ cd ei/logs</li>
										<li>$ cat extract.log |grep [YYYYWW] | grep "adding" | grep _upt_ | awk 'BEGIN { FS=" "; sm=0; } { sm = sm + $9 } END { print "Total : "sm; }'</li>			
									</ul>
								</li>	
							<li>./pat_weekly_cit.sh [YYYYWW]</li>
							<li><b>Check count:</b>
								<ul>
									<li>cd ei/logs</li>
									<li>cat extract.log |grep [YYYYWW] | grep "adding" | grep _uptcit_ | awk 'BEGIN { FS=" "; sm=0; } { sm = sm + $9 } END { print "Total : "sm; }'</li>
								</ul>
							</li>
							</ul>
							</td>
						</tr>	
						
						<tr>
							<td id="weeklydlkey">Archive files:</td>
							<td id="weeklydlvalue">cd  /data/loading/ipdd2/
							</br>
							</br>
							./archive_files.sh [YYYYWW]
							</td>
						</tr>
						
						
				</tbody>
			</table>
			
		</div>				
		

<!-- 6. Email Alerts -->
<div id="weeklyemailalert" style="display: none;">

			<table id="weeklydataload">
				<caption>Email ALerts:</caption>
				
				<tbody>
						<tr>
							<td id="dlkey">CleanUp Last Week Email Alert Transactions:</td>
							<td id="dlvalue">ssh -i Prod.pem 10.178.165.34
							</br>
							</br>
								use screen.
							</br>
							</br>
								cd /data/emailalerts
								</br>
								</br>
								<b>(1.1) Uncomment few lines in override.properties</b>
								</br>
								</br>
								vi override.properties
								
								<br>
								</br>
								<b>do the following:</b>
								<ul>
									<li>uncommonet "TESTRECEPIENTS=z.yang@elsevier.com"</li>
									<li>uncomment "TESTCCRECEPIENTS=z.yang@elsevier.com"</li>
									<li>uncomment "TESTEMAILFILTER=elsevier.com"
								</ul>
								more override.properties
								
								</br>
								</br>
								<b>(1.2) Clean Up last week transactions</b>
								</br>
								</br>
								/usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java -jar emailalert-jar-with-dependencies.jar
								
								<ul>
									<li>Would you like to clean up the existing email transactions for a particular year week?(ex:'yes' or 'no') : <b>[yes]</b></li>
									<li>You must specify a Year and a Week number in the following format, YYYYWW. i.e. 200542 :<b>Last weeknumber (i.e. 201535)</b></li>
									<li>Would you like to use the cert database?(ex:'yes' or 'no') :<b>[no]</b></li>
									<li><b>Verify:</b> database mode  selected &nbsp;<b>and</b>&nbsp;Year Week value entered</li>
									<li>Are you sure to start the process? say 'yes' for start and 'no' for quit:<b>[yes]</b></li>
								</ul>
							</td>
						</tr>
						
						<tr>
							<td id="weeklydlkey">Run Test:</td>
							<td id="weeklydlvalue">/usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java -jar emailalert-jar-with-dependencies.jar
							</br>
							<ul>
								<li>Would you like to clean up the existing email transactions for a particular year week?(ex:'yes' or 'no') :<b>[no]</b></li>
								<li>Would you like to start the email transaction for a particular year week?(ex:'yes' or 'no') :<b>[yes]</b></li>
								<li>Would you like to use override.properties?(ex:'yes' or 'no') :<b>[yes]</b></li>
								<li>Would you like to run this application in test mode?(ex:'yes' or 'no') :<b>[yes]</b></li>
								<li>You must specify a Year and a Week number in the following format, YYYYWW. i.e. 200542 :<b>Current weeknumber (i.e. 201536)</b></li>
								<li>Would you like to use the cert database?(ex:'yes' or 'no') :<b>[no]</b></li>
								<li>Would you like to change the filter value 'elsevier.com' in test mode?(ex:'yes' or 'no') :<b>[no]</b></li>
								<li>Would you like to change the recepient value 'z.yang@elsevier.com' in test mode?(ex:'yes' or 'no') :<b>[no]</b></li>
								<li>Would you like to change the cced recepient value 'z.yang@elsevier.com' in test mode?(ex:'yes' or 'no') :<b>[no]</b></li>
								<li><b>Verify:</b> Running  mode  selected &nbsp;<b>and</b>&nbsp; Database mode  selected &nbsp;<b>and</b>&nbsp;
								Year Week value entered &nbsp;<b>and</b>&nbsp; Filter text used &nbsp;<b>and</b>&nbsp; Recepient email id used
								&nbsp;<b>and</b>&nbsp; CC Recepient email id used</li>
								<li>Are you sure to start the process? say 'yes' for start and 'no' for quit:<b>[yes]</b></li>
								<li>
							</ul>
							
							</br>
							<b>Check Logs:</b>
							</br>
							</br>
							cd /data/emailalerts/logs
							</br>
							</br>
							tail -f application.log
							</br>
							</br>
							verify if the process finished (shoudl see total Count of test email alerts sent out) 
							</br>
							</br>
						cd /data/emailalerts/logs
						</br>
						</br>
						tail -300 application.log
						</br>
						</br>
						<b>For Example:</b> Count of Email Alerts to be sent = 351
						
						</br>
						</br>
						<b>Check outlook & Compare with EV</b>
					</td>
				</tr>
						
				
				<tr>
							<td id="weeklydlkey">Run Prod:</td>
							<td id="weeklydlvalue"></br>
							</br>
							cd /data/emailalerts/logs
							</br>
							</br>
								<b>(1.1) Comment few lines in override.properties</b>
							</br>
							</br>
								vi override.properties
								<br>
								</br>
								<b>do the following:</b>
								<ul>
									<li>commonet "TESTRECEPIENTS=z.yang@elsevier.com"</li>
									<li>comment "TESTCCRECEPIENTS=z.yang@elsevier.com"</li>
									<li>comment "TESTEMAILFILTER=elsevier.com"
								</ul>
									more override.properties
								</br>
								</br>
									<b>(1.2) Run Prod Email Alert:</b>
							</br>
							</br>
								/usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java -jar emailalert-jar-with-dependencies.jar
							</br>
							<ul>
								<li>Would you like to clean up the existing email transactions for a particular year week?(ex:'yes' or 'no') :<b>[no]</b></li>
								<li>Would you like to start the email transaction for a particular year week?(ex:'yes' or 'no') :<b>[yes]</b></li>
								<li> Would you like to use override.properties?(ex:'yes' or 'no') :<b>[yes]</b></li>
								<li>Would you like to run this application in test mode?(ex:'yes' or 'no') :>b>[no]</li>
								<li>You must specify a Year and a Week number in the following format, YYYYWW. i.e. 200542 :<b>Current weeknumber(i.e. 201536)</b></li>
								<li>Would you like to use the cert database?(ex:'yes' or 'no') :<b>[no]</b></li>
								<li><b>Verify:</b> Running  mode  selected &nbsp;<b>and</b>&nbsp; Database mode  selected  &nbsp;<b>and</b>&nbsp;
								Year Week value entered</li>
								<li>Are you sure to start the process? say 'yes' for start and 'no' for quit:<b>[yes]</b></li>
							</ul>
						
						<b>Check Logs:</b>
							</br>
							</br>
							cd /data/emailalerts/logs
							</br>
							</br>
							tail -f application.log
							</br>
							</br>
							verify if the process finished (shoudl see total Count of test email alerts sent out) 
							</br>
							</br>
						cd /data/emailalerts/logs
						</br>
						</br>
						tail -300 application.log
						</br>
						</br>
						<b>For Example:</b> Count of Email Alerts to be sent = 351
							
					</td>
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

document.getElementById("weeklys300corr").style.display="block";

document.getElementById("weeklycpxcorr").style.display="none";
document.getElementById("weeklynewload").style.display="none";
document.getElementById("weeklygrfcorr").style.display="none";
document.getElementById("weeklypat").style.display="none";
document.getElementById("weeklyemailalert").style.display="none";

}

function showcor1() { 

	document.getElementById("weeklycpxcorr").style.display="block";
	
	document.getElementById("weeklys300corr").style.display="none";
	document.getElementById("weeklynewload").style.display="none";
	document.getElementById("weeklygrfcorr").style.display="none";
	document.getElementById("weeklypat").style.display="none";
	document.getElementById("weeklyemailalert").style.display="none";

	}
	
function showcor2() { 

	document.getElementById("weeklygrfcorr").style.display="block";
	
	document.getElementById("weeklys300corr").style.display="none";
	document.getElementById("weeklycpxcorr").style.display="none";
	document.getElementById("weeklynewload").style.display="none";
	document.getElementById("weeklypat").style.display="none";
	document.getElementById("weeklyemailalert").style.display="none";

	}
	
function showcor3() { 

	document.getElementById("weeklynewload").style.display="block";
	
	document.getElementById("weeklys300corr").style.display="none";
	document.getElementById("weeklycpxcorr").style.display="none";
	document.getElementById("weeklygrfcorr").style.display="none";
	document.getElementById("weeklypat").style.display="none";
	document.getElementById("weeklyemailalert").style.display="none";

	}
	
function showcor4() { 

	document.getElementById("weeklypat").style.display="block";
	
	document.getElementById("weeklys300corr").style.display="none";
	document.getElementById("weeklycpxcorr").style.display="none";
	document.getElementById("weeklynewload").style.display="none";
	document.getElementById("weeklygrfcorr").style.display="none";
	document.getElementById("weeklyemailalert").style.display="none";

	}
	
function showcor5() { 

	document.getElementById("weeklyemailalert").style.display="block";
	
	document.getElementById("weeklys300corr").style.display="none";
	document.getElementById("weeklycpxcorr").style.display="none";
	document.getElementById("weeklynewload").style.display="none";
	document.getElementById("weeklygrfcorr").style.display="none";
	document.getElementById("weeklypat").style.display="none";

	}
	
	
</script>



</body>
</html>