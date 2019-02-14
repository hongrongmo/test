<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Aws Maintenance</title>

<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
   
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
 
 #mainawslinks li
 {
  padding-bottom: 4px;
  padding-top: 6px;
  font-weight: bold;
  color: #2F4F4F;
  font-size: 14px;
 }
 
 #awsinnerlink li
 {
 	font-size: 12px; 
 	font-weight: bold;
    margin-bottom: 6px;
 
 }  
   </style>
</head>
<body>

<body>


<div id="wrapper">
		<%@ include file="/views/includes/header.jsp" %>
		<div id="sidebar">
			<%@ include file="/views/includes/navlinks.jsp" %>
		</div>
		
		
		<div id="content">

<%if(session.getAttribute("username") !=null ){ %> 
		
<h1>Aws Maintenance</h1>
<p>
This page allows you to view NYC related templates for:

	<ul id="mainawslinks">
		<li>RDS Templates:
			<ul id="awsinnerlink">
				<li><a class="rplink" href="https://gitblit.et-scm.com/blob/?f=cloudformation/EIA_RDS.template&r=AGRM/EngineeringVillageCommon.git&h=master" target="_blank">EIA RDS Template</a></li>
				<li><a class="rplink" href="https://gitblit.et-scm.com/blob/?f=cloudformation/EIB_RDS.template&r=AGRM/EngineeringVillageCommon.git&h=master" target="_blank">EIB RDS Template</a></li>
				<li><a class="rplink" href="https://gitblit.et-scm.com/blob/?f=cloudformation/EID_RDS.template&r=AGRM/EngineeringVillageCommon.git&h=master" target="_blank">EID RDS Template</a></li>
			</ul>
			</li>
			
	</ul>
	
	<ul id="mainawslinks">
		<li>DataFabrication EC2 Template:
			<ul id="awsinnerlink">
				<li><a class="rplink" href="https://gitblit.et-scm.com/blob/;jsessionid=D012CB086F64439A93ED1325FF04F6B0.VNpdhuPOH9yzKKPodWATA?f=cloudformation/dataLoading.template&r=AGRM/EngineeringVillageCommon.git&h=master" target="_blank">Data Fabrication EC2</a></li>
			</ul>
	</li>
	</ul>
	
	<ul id="mainawslinks">		
		<li>EmailAlerts EC2 Template:
			<ul>
				<li><a class="rplink" href="https://gitblit.et-scm.com/blob/;jsessionid=D012CB086F64439A93ED1325FF04F6B0.VNpdhuPOH9yzKKPodWATA?f=cloudformation/emailalerts.template&r=AGRM/EngineeringVillageCommon.git&h=master" target="_blank">EmailAlerts EC2</a></li>
			</ul>
		</li>
	</ul>
		
	<ul id="mainawslinks">		
		<li>Usage Processing EC2 Template:
			<ul id="awsinnerlink">
				<li><a class="rplink" href="https://gitblit.et-scm.com/blob/;jsessionid=D012CB086F64439A93ED1325FF04F6B0.VNpdhuPOH9yzKKPodWATA?f=cloudformation/UsageProcessing.template&r=AGRM/EngineeringVillageCommon.git&h=master" target="_blank">Usage Processing EC2</a></li>
			</ul>
		</li>
	</ul>
	
	<ul id="mainawslinks">	
		<li>Emetrics EC2 Template:
			<ul id="awsinnerlink">
				<li><a class="rplink" href="https://gitblit.et-scm.com/blob/;jsessionid=D012CB086F64439A93ED1325FF04F6B0.VNpdhuPOH9yzKKPodWATA?f=cloudformation/emetrics.template&r=AGRM/EngineeringVillageCommon.git&h=master" target="_blank">Emetrics EC2</a></li>
			</ul>
		</li>		
	</ul>
	
	<ul id="mainawslinks">	
		<li>AWS Cost Report:
			<ul id="awsinnerlink">
			<li><a class="rplink" href="https://console.aws.amazon.com/billing/home?region=us-east-1#/" taregt="_blank">Total Cost</a></li>
				<li><a class="rplink" href="https://console.aws.amazon.com/ec2/reports/?breadCrumb=EC2Console#InstanceUsage:Range=FOURTEEN_DAYS;GroupBy=NONE;Units=Instance_Hours;granularity=Daily" target="_blank">EC2 Instance Usage Report</a></li>
				<li><a class="rplink" href="https://console.aws.amazon.com/ec2/reports/?breadCrumb=EC2Console#ReservedInstanceUtilization:" target="_blank">EC2 Reserved Instance Utilization Report</a></li>
			</ul>
		</li>		
	</ul>
	

<%} %>

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