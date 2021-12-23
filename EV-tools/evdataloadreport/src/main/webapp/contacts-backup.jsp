<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Contacts</title>

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
 
ul
 {
 	list-style-type: none;
 	
 } 
ul li
{
	padding-bottom: 0px;
 	margin-bottom: 0px;
 	
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
<h1>Contacts</h1>

</br>
</br>
</br>



<!-- Table1: Source Vendors -->

<table id="contact">
	<caption>Source Vendors</caption>
</table>

<table id="contact">	
	<caption id="nestedtablecaption" style="color:#4682B4; background-color: #FFFFFF;">BD</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th> Contact: </th>
		</tr>
	</thead>
	<tbody>
	
	<!-- Contact 1 -->
		<tr>
				<td> <b>Coming Soon</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
			<!-- END of Contact 1 -->	

		</tbody>
	</table>

</br>
</br>
	
<!-- Tabl2: Contacts Dayton -->
<table id="contact">
	<caption>Dayton</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th> Contact: </th>
		</tr>
	</thead>
	
	<tbody>
			<tr>
			<!-- Contact 1 -->
				<td> <b>Stark, Wesley W </b></td>
				<td></td>
				<td>
					
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 865 1554
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Mobile):</b> +1 314 578 7426
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 937 865 1554
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:W.Stark@Elsevier.com">Stark, Wesley W. (ELS-DAY)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 1 -->	
		
		
		<!-- Contact 2 -->
		<tr>
				<td> <b>Petric, Steven C.</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 865 7120
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:steven.petric@elsevier.com">Petric, Steven C. (ELS-DAY)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 2 -->			
		
		<!-- Contact 3 -->
		<tr>
				<td> <b>Salk, Judy</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3645
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Mobile):</b> +1 347 260 2527
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:J.Salk@elsevier.com">Salk, Judy (ELS-NYC)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 3 -->		
		
		
		<!-- Contact 4 -->
		<tr>
				<td> <b>Harover, T M. </b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8426
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Mobile):</b> +1 937 748 5562
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:harover@elsevier.com">Harover, T M. (ELS-DAY)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 4 -->	
		
		<!-- Contact 5 -->
		<tr>
				<td> <b>Cheung-Geisel, Anna Y.</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 865 1984
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Anna.Cheung-Geisel@elsevier.com">Cheung-Geisel, Anna Y. (ELS-DAY)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 5 -->	
		
		
		
		<!-- Contact 6 -->
		<tr>
				<td> <b>Robbins, R. S.</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8463
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:robert.robbins@elsevier.com">Robbins, R. S. (ELS-DAY) </a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 6 -->
		
		<!-- Contact 7 -->
		<tr>
				<td> <b>Singh, Mahendra P. </b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8463
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:m.p.singh@elsevier.com">Singh, Mahendra P. (ELS-CON)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 7 -->		
		
		<!-- Contact 8 -->
		<tr>
				<td> <b>Kamaraj, Mohan</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8439
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:mohan.kamaraj@elsevier.com">Kamaraj, Mohan (ELS-CON)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 8 -->		
		
				
			
		
		<!-- Contact 9 -->
		<tr>
				<td> <b>Vandergriff, Jared</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 314 447 8780
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Mobile):</b> +1 314 435 4293
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 314 447 8780
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:J.Vandergriff@Elsevier.com">Vandergriff, Jared (ELS-STL)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 9  -->			
		
			<!-- Contact 10 -->
		<tr>
				<td> <b>McGarva, Matthew W.</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +44 207 424 4245
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Mobile):</b> +44 778 551 1301
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:m.mcgarva@elsevier.com">McGarva, Matthew W. (ELS)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 10  -->		
		
					
</tbody>					
</table>






<br/>
<br/>
<!-- Table3: Contact NYC -->

<table id="contact">
	<caption>NYC</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th> Contact: </th>
		</tr>
	</thead>
	<tbody>
			<tr>
			<!-- Contact 1 -->
				<td> <b>Huang, Frank </b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3651
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Mobile):</b> +1 201 310 3443
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:f.Huang@elsevier.com">Frank Huang (ELS-NYC)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 1 -->	
		
		
		<!-- Contact 2 -->
		<tr>
				<td> <b>Mo, Hongrong</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3715
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:H.Mo@elsevier.com">Mo, Hongrong (ELS-NYC) </a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 2 -->			
		
		<!-- Contact 3 -->
		<tr>
				<td> <b>Andre Parrish</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3707
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Mobile):</b> +1 201 386 0164
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:At.Parrish@elsevier.com">Parrish, Andre T (ELS-NYC)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 3 -->		
		
		
		<!-- Contact 4 -->
		<tr>
				<td> <b>Yang, Zhun</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3655
									</td>
								</tr>
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Z.Yang@elsevier.com">Yang, Zhun (ELS-NYC)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 4 -->	
		
		<!-- Contact 5 -->
		<tr>
				<td> <b>Teleb, Hanan</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3654
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Mobile):</b> +1 201 300 9111
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Mobile):</b> +1 201 546 7464
									</td>
								</tr>
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:h.teleb@elsevier.com">Teleb, Hanan (ELS-NYC)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 5 -->	

		<!-- Contact 6 -->
		<tr>
				<td> <b>NYC Team</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:ei.operation@elsevier.com">ei.operation</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 6 -->	
		
		
	</tbody>
</table>



<br/>
<br/>
<!-- Table4: Contact Fast -->

<table id="contact">
	<caption>Fast</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th> Contact: </th>
		</tr>
	</thead>
	<tbody>
	
	<!-- Contact 1 -->
		<tr>
				<td> <b>Harold Perry</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Harold.Perry@microsoft.com">Harold Perry</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
			<!-- END of Contact 1 -->	
			
			<!-- Contact 2 -->
			<tr>
				<td> <b>Nick Meader</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Nick.Meader@microsoft.com">Nick Meader</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 2 -->			
		
		<!-- Contact 3 -->
		<tr>
				<td> <b>Herrold, John</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:john.herrold@microsoft.com">Herrold, John (FAST)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 3 -->		
		
		
		<!-- Contact 4 -->
		<tr>
				<td> <b>EI MESG MS Operations</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:ei-ops@microsoft.com">EI MESG MS Operations(Customer Service)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 4 -->	
		
		<!-- Contact 5 -->
		<tr>
				<td> <b>Nishan Kossinna</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:v-nikoss@microsoft.com">Nishan Kossinna (Creative Solutions Limited)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 5 -->	

		
		<!-- Contact 6 -->
		<tr>
				<td> <b>Tharindu Thundeniya</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:v-ththu@microsoft.com">Tharindu Thundeniya (Creative Solutions Limited)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 6 -->	
		
		
		<!-- Contact 7 -->
		<tr>
				<td> <b>Savinda Thilakarathna</b></td>
				<td></td>
				<td>
							<table id="innercontact">
								<tr>
									<td>
										<b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:v-savit@microsoft.com">Savinda Thilakarathna (Creative Solutions Limited)</a>
									</td>
								</tr>	
							</table>
								
					</td>
			</tr>
		<!-- END of Contact 7 -->
		
		
		
	</tbody>
</table>


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