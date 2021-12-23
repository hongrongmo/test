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
 	margin-left: 0px;
 	
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



<!-- Table1: EV Emergency Support -->

<table id="contact">
	<caption>EV Support</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th>Contact:</th>
		</tr>
	</thead>
	<tbody>
	
		<!-- Contact 1 -->
		<tr>
				<td id="contactname"><b>T.M. Harover</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937.247.8426</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 937.748.5562<li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 937.748.5562<li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:harover@elsevier.com">Harover, T M. (ELS-DAY)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:tharover@gmail.com">Harover, T M.</a></li>
					</ul>
							
					</td>
			</tr>
		<!-- END of Contact 1 -->		
		
		<!-- Contact 2 -->	
			<tr>
				<td id="contactname"><b>Scott Robbins</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8463</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 937 620 1513</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 937 226 1815</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:robert.robbins@elsevier.com">Robbins, R. S. (ELS-DAY)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:scott.robbins@gmail.com ">Robbins, R. S.</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 2 -->	
		
		<!-- Contact 3 -->	
			<tr>
				<td id="contactname"><b>Frank Huang</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3651</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 201 310 3443</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 732 738 5586</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:F.Huang@elsevier.com">Frank Huang (ELS-NYC)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 3 -->	
		
		<!-- Contact 4 -->	
			<tr>
				<td id="contactname"><b>Andre Parrish</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3707</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 201 362 9398</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 201 386 0164</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:At.Parrish@elsevier.com">Andre Parrish (ELS-NYC)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:andre.t.parrish@gmail.com">Andre Parrish</a> , <a href="mailto:andre_parrish@yahoo.com">Andre Parrish</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 4 -->	
		
		<!-- Contact 5 -->	
			<tr>
				<td id="contactname"><b>Hanan Teleb</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3654</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 201 300 9111</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 201 546 7464</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:h.teleb@elsevier.com">Hanan Teleb (ELS-NYC)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:hanan_ebraheem@yahoo.com">Hanan Teleb</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 5 -->
		
		<!-- Contact 6 -->	
			<tr>
				<td id="contactname"><b>Steve Petric</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 865 7120</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 937 344 9680</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 937 848 2891</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:steve.petric@elsevier.com">Steve Petric (ELS-DAY)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:steve.petric@gmail.com">Steve Petric</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 6 -->
		
		<!-- Contact 7 -->	
			<tr>
				<td id="contactname"><b>Sally Fell</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937.247.3582</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 440.292.7845</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 440.865.3199</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:Sally.Fell@elsevier.com">Sally Fell (ELS-DAY)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:sfell13@mail.bw.edu">Sally Fell</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 7 -->
		
		
		
		
	</tbody>
</table>

</br>
</br>


<!-- Table2: Source Vendors -->

<table id="contact">
	<caption>Source Vendors</caption>
</table>

<!-- subTable1 -->
<table id="contact">	
	<caption id="nestedtablecaption" style="color:#4682B4; background-color: #FFFFFF;">BD</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th>Contact:</th>
		</tr>
	</thead>
	<tbody>
	
	<!-- Contact 1 -->
		<tr>
				<td><b>Boutkan, Kees C</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:C.Boutkan@elsevier.com">Boutkan, Kees C (ELS-AMS)</a></li>
					</ul>
				</td>
			</tr>
			<!-- END of Contact 1 -->
			
			<tr>
				<td><b>Lekkerkerker, Frans</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:F.Lekkerkerker@elsevier.com">Lekkerkerker, Frans (ELS-AMS)</a></li>
					</ul>
				</td>
			</tr>
			<!-- END of Contact 2 -->	
			
			<tr>
				<td><b>Vegter, Jurriaan</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:J.Vegter@elsevier.com">Vegter, Jurriaan (ELS-AMS)</a></li>
					</ul>
				</td>
			</tr>
			<!-- END of Contact 3 -->	
			
			<tr>
				<td><b>Hoff, Don van't</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:D.Hoff@elsevier.com">Hoff, Don van't (ELS-AMS)</a></li>
					</ul>
				</td>
			</tr>
			<!-- END of Contact 4 -->
			

		</tbody>
	</table>

</br>
</br>
<!-- subTable2 -->

<table id="contact">	
	<caption id="nestedtablecaption" style="color:#4682B4; background-color: #FFFFFF;">Inspec</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th>Contact:</th>
		</tr>
	</thead>
	<tbody>
	
	<!-- Contact 1 -->
		<tr>
				<td><b>Pache,Jeff</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:jpache@theiet.org">Pache,Jeff</a></li>
					</ul>
				</td>
			</tr>
			<!-- END of Contact 1 -->
			
			<tr>
				<td><b>IDEAS.Products</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:IDEAS.Products@theiet.org">IDEAS.Products</a></li>
					</ul>
				</td>
			</tr>
			<!-- END of Contact 2 -->	

		</tbody>
	</table>


</br>
</br>
<!-- subTable3 -->

<table id="contact">	
	<caption id="nestedtablecaption" style="color:#4682B4; background-color: #FFFFFF;">Patent</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th>Contact:</th>
		</tr>
	</thead>
	<tbody>
	
	<!-- Contact 1 -->
		<tr>
				<td><b>Univentio</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:CustomerSupport@univentio.com">Customer Support</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:MAli@univentio.com">Ali, Majid (LNG-LEI)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Gmeyvogel@UNIVENTIO.COM">Meyvogel, Gijsbert (LNG-LEI)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Jvandekreeke@univentio.com">Van de Kreeke, John (LNG-LEI)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:dknippers@univentio.com">Knippers, Daniel (LNG-LEI)</a></li>
					</ul>
				</td>
			</tr>
			
			<!-- End of contact1 -->
			
			<tr>
				<td><b>VTW</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:h.spanhaak@elsevier.com">Spanhaak, Harm (ELS-AMS)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:d.berkvens@elsevier.com">Berkvens, Dorine (ELS-AMS)</a></li>
					</ul>
				</td>
			</tr>
			<!-- END of Contact 2 -->	
			
		</tbody>
	</table>
		

</br>
</br>




	
<!-- Tabl3: Contacts Dayton -->
<table id="contact">
	<caption>Dayton</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th>Contact:</th>
		</tr>
	</thead>
	
	<tbody>
			<tr>
			<!-- Contact 1 -->
				<td id="contactname"><b>Stark, Wesley W</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 865 1554</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 314 578 7426</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 937 865 1554</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:W.Stark@Elsevier.com">Stark, Wesley W. (ELS-DAY)</a></li>
					
					</ul>
							
				</td>
			</tr>
		<!-- END of Contact 1 -->	
		
		
		<!-- Contact 2 -->
		<tr>
				<td id="contactname"><b>Petric, Steven C.</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 865 7120</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 937 344 9680</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 937 848 2891</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:steven.petric@elsevier.com">Petric, Steven C. (ELS-DAY)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:steve.petric@gmail.com">Petric, Steven C.</a></li>
				
					</ul>
								
				</td>
			</tr>
		<!-- END of Contact 2 -->	
		
		<!-- Contact 3 -->
		<tr>
				<td id="contactname"><b>Fell, Sally</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 3582</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 440 292 7845</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 440 865 3199</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:Sally.Fell@elsevier.com">Fell, Sally (ELS-DAY)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:sfell13@mail.bw.edu">Fell, Sally</a></li>
				
					</ul>
								
				</td>
			</tr>
		<!-- END of Contact 3 -->		
		
		
		<!-- Contact 4 -->
		<tr>
				<td id="contactname"><b>Harover, T M.</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8426</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 937 748 5562</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 937 748 5562</li>						
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:harover@elsevier.com">Harover, T M. (ELS-DAY)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:tharover@gmail.com">Harover, T M.</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 4 -->	
		
		<!-- Contact 5 -->
		<tr>
				<td id="contactname"><b>Newell, Perry</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8458</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Perry.Newell@elsevier.com">Newell, Perry (ELS-DAY)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 5 -->	
		
		
		<!-- Contact 6 -->
		<tr>
				<td id="contactname"><b>Cheung-Geisel, Anna Y.</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 865 1984</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Anna.Cheung-Geisel@elsevier.com">Cheung-Geisel, Anna Y. (ELS-DAY)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 6 -->	
		
		
		
		<!-- Contact 7 -->
		<tr>
				<td id="contactname"><b>Robbins, R. S.</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8463</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 937 620 1513</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 226 1815</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:robert.robbins@elsevier.com">Robbins, R. S. (ELS-DAY) </a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:scott.robbins@gmail.com ">Robbins, R. S.</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 7 -->
		
		<!-- Contact 8 -->
		<tr>
				<td id="contactname"><b>Singh, Mahendra P.</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8463</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:m.p.singh@elsevier.com">Singh, Mahendra P. (ELS-CON)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 8 -->		
		
		<!-- Contact 9 -->
		<tr>
				<td id="contactname"><b>Kamaraj, Mohan</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 937 247 8439</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:mohan.kamaraj@elsevier.com">Kamaraj, Mohan (ELS-CON)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 9 -->		
		
				
			
		
		<!-- Contact 10 -->
		<tr>
				<td id="contactname"><b>Vandergriff, Jared</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 314 447 8780</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 314 435 4293</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 314 447 8780</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:J.Vandergriff@Elsevier.com">Vandergriff, Jared (ELS-STL)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 10  -->			
					
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
			<th>Contact:</th>
		</tr>
	</thead>
	<tbody>
	
	<!-- Contact 1 -->
		<tr>
				<td id="contactname"><b>Harold Perry</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Harold.Perry@microsoft.com">Harold Perry</a></li>
					</ul>
				</td>
			</tr>
			<!-- END of Contact 1 -->	
			
			<!-- Contact 2 -->
			<tr>
				<td id="contactname"><b>Nick Meader</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Nick.Meader@microsoft.com">Nick Meader</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 2 -->			
		
		<!-- Contact 3 -->
		<tr>
				<td id="contactname"><b>Herrold, John</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:john.herrold@microsoft.com">Herrold, John (FAST)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 3 -->		
		
		
		<!-- Contact 4 -->
		<tr>
				<td id="contactname"><b>EI MESG MS Operations</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:ei-ops@microsoft.com">EI MESG MS Operations(Customer Service)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 4 -->	
		
		<!-- Contact 5 -->
		<tr>
				<td id="contactname"><b>Nishan Kossinna</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:v-nikoss@microsoft.com">Nishan Kossinna (Creative Solutions Limited)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 5 -->	

		
		<!-- Contact 6 -->
		<tr>
				<td id="contactname"><b>Tharindu Thundeniya</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:v-ththu@microsoft.com">Tharindu Thundeniya (Creative Solutions Limited)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 6 -->	
		
		
		<!-- Contact 7 -->
		<tr>
				<td id="contactname"><b>Savinda Thilakarathna</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:v-savit@microsoft.com">Savinda Thilakarathna (Creative Solutions Limited)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 7 -->
		
		
		
	</tbody>
</table>

<br/>
<br/>


<!-- Table5: Contact NYC -->

<table id="contact">
	<caption>NYC</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th>Contact:</th>
		</tr>
	</thead>
	<tbody>
	
		<!-- Contact 1 -->
		<tr>
				<td id="contactname"><b>Salk, Judy</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3645</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 347 260 2527<li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:J.Salk@elsevier.com">Salk, Judy (ELS-NYC)</a></li>
					</ul>
							
					</td>
			</tr>
		<!-- END of Contact 1 -->		
		
		<!-- Contact 2 -->	
			<tr>
				<td id="contactname"><b>Huang, Frank</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3651</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 201 310 3443</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 732 738 5586</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:f.Huang@elsevier.com">Frank Huang (ELS-NYC)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 2 -->	
		
		<!-- Contact 3 -->	
			<tr>
				<td id="contactname"><b>Vittorio, Salvatore</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3694</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 646 258 3440</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:S.Vittorio@elsevier.com">Vittorio, Salvatore (ELS-NYC)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 3 -->	
		
		
		<!-- Contact 4 -->	
			<tr>
				<td id="contactname"><b>Alli, Nadia</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3872</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:n.alli@elsevier.com">Alli, Nadia (ELS-NYC)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 4 -->
		
		<!-- Contact 5 -->	
			<tr>
				<td id="contactname"><b>Nowacki, Perry</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3863</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:P.Nowacki@elsevier.com">Nowacki, Perry (ELS-NYC)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 5 -->
		
		
		
		
		<!-- Contact 6 -->
		<tr>
				<td id="contactname"><b>Mo, Hongrong</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3715</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:H.Mo@elsevier.com">Mo, Hongrong (ELS-NYC) </a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 6 -->			
		
		<!-- Contact 7 -->
		<tr>
				<td id="contactname"><b>Andre Parrish</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3707</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 201 362 9398</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 201 386 0164</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:At.Parrish@elsevier.com">Parrish, Andre T (ELS-NYC)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:andre.t.parrish@gmail.com">Parrish, Andre T</a> , <a href="mailto:andre_parrish@yahoo.com">Parrish, Andre T</a>
						</li>
						
					</ul>
				</td>
			</tr>
		<!-- END of Contact 7 -->		
		
		
		<!-- Contact 8 -->
		<tr>
				<td id="contactname"><b>Yang, Zhun</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3655</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:Z.Yang@elsevier.com">Yang, Zhun (ELS-NYC)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 8 -->	
		
		<!-- Contact 9 -->
		<tr>
				<td id="contactname"><b>Teleb, Hanan</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +1 212 633 3654</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +1 201 300 9111</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Home):</b> +1 201 546 7464</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Work):</b> <a href="mailto:h.teleb@elsevier.com">Teleb, Hanan (ELS-NYC)</a></li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email (Personal):</b> <a href="mailto:hanan_ebraheem@yahoo.com">Teleb, Hanan</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 9 -->	

		<!-- Contact 10 -->
		<tr>
				<td><b>NYC Team</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:ei.operation@elsevier.com">ei.operation</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 10 -->	
		
		
	</tbody>
</table>

<br/>
<br/>



<!-- Table6: Other Locations -->
<table id="contact">
	<caption>Other Locations</caption>
    <thead>
		<tr>
			<th>Name:</th>
			<th></th>
			<th>Contact:</th>
		</tr>
	</thead>
	
	<tbody>
			<!-- Contact 1 -->
			<tr>
				<td id="contactname"><b>McGarva, Matthew W.</b></td>
				<td></td>
				<td id="contactinfo">
					<ul>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Work):</b> +44 207 424 4245</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Phone (Cell):</b> +44 778 551 1301</li>
						<li><b style="color:#696969; font-style: italic; font-size: 12px;">Email:</b> <a href="mailto:m.mcgarva@elsevier.com">McGarva, Matthew W. (ELS)</a></li>
					</ul>
				</td>
			</tr>
		<!-- END of Contact 1  -->		
		
	</tbody>
</table>

<br/>
<br/>


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