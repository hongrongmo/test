<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
    
    <%@page import="java.util.GregorianCalendar"%>
	<%@page import="java.util.Date"%>
	<%@page import="java.util.Calendar"%>

    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Home</title>
	<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
    <link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
	<link rel="icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
     <style>
    	/* table {border: 0px solid #ababab; float:none}
    	table td {border: 0px } */
    	<!-- UL LIST -->
    	
    	ul {
 			 list-style-type: circle;
 			 margin-left: 0px;
 			 padding: 0;
		   }
 
		li {
 			 font: 16px Helvetica, Verdana, sans-serif;
  			 border-bottom: 1px solid #ccc;
  			 width:150px;
		   }
 
		li:last-child {
  						/* border: none; */  /* last option does not have line at bottome*/
  						border-bottom: 1px solid #ccc;
					  }
 
		li a {
 				 text-decoration: none;
 				 color: #00008B;
  				 display: block;
  				 width: 200px;
 
  				-webkit-transition: font-size 0.3s ease, background-color 0.3s ease;
 				 -moz-transition: font-size 0.3s ease, background-color 0.3s ease;
 				 -o-transition: font-size 0.3s ease, background-color 0.3s ease;
 				 -ms-transition: font-size 0.3s ease, background-color 0.3s ease;
 				 transition: font-size 0.3s ease, background-color 0.3s ease;
			}
 
		li a:hover {
 					 font-size: 14px;
  					background: #f6f6f6;
					}
    	<!--  -->
    </style>
    
</head>

<body>

	<div class="wrapper">
<%-- <%if(session.getAttribute("username") !=null)
	{%> --%>
	<%@ include file="views/includes/header.jsp" %>
  
  	    <div id="sidebar"></div>


<div id="content">


<table>
<tr>
<td><h1>EV NYC PROCEDURES/REPORTS/DOCUMENTS</h1></td>

<td>

<div id="infobar">

<script language="javascript">


 var msgs = new Array(
     "<%= Calendar.getInstance().getTime()%>",
     "Current Week Number: <%= Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)%>",
     "Current Data Processing Load Number: "+"2016"+"0<%= Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)%>"
     ); // No comma after last ticker msg


var barwidth='350px' 	//Enter main bar width in px or %
var setdelay=1500 		//Enter delay between msgs, in mili-seconds
var mouseover_color='#FFE4B5'	 //Specify highlight color
var mouseout_color='#FFFFFF' 	//Specify default color
/////////////////////////////////////////////////////////////////////

var count=0;
var ns6=document.getElementById&&!document.all
var ie4=document.all&&navigator.userAgent.indexOf("Opera")==-1

document.write('<form name="news_bar" style="margin-left:0px;left:0px"><input type="button" name="news_bar_but" style="font-weight: bold;font-size: 14px; color:#800000;background:#FFFFFF; width:'+barwidth+'; height:30px; border-width:0; cursor:hand" onmouseover="this.style.background=mouseover_color" onmouseout="this.style.background=mouseout_color"></form>');
	


function init_news_bar(){
  document.news_bar.news_bar_but.value=msgs[count];
}
//moveit function by Dynamicdrive.com
function moveit(how){
if (how==1){ //cycle foward
if (count<msgs.length-1)
count++
else
count=0
}
else{ //cycle backward
if (count==0)
count=msgs.length-1
else
count--
}
document.news_bar.news_bar_but.value=msgs[count];
}

setInterval("moveit(1)",setdelay)

function goURL(){
 location.href=msg_url[count];
}

init_news_bar();

</script>

</div>
<!-- END of Info Bar -->

</td>

</tr>
</table>


	<ul>
		<li> <a class="dlink" href="datafabricaction.jsp">Data Fabrication</a></li>
		<li> <a class="dlink" href="dbaoperations.jsp">DBA Operations</a></li>
		<li> <a class="dlink" href="template.jsp">NYC Projects</a> </li>
		<li> <a class="dlink" href="cloudwatch.jsp">AWS CloudWatch</a> </li>
		<li> <a class="dlink" href="aws.jsp">AWS Maintenance</a> </li>
		<li> <a class="dlink" href="nycoperations.jsp">EV Operations(NYC)</a> </li>
		<li> <a class="dlink" href="dataanalysis.jsp">Data Analysis</a> </li>
		<li> <a class="dlink" href="docs.jsp">Documentation/Reference</a> </li>
		<li> <a class="dlink" href="majorlinks.jsp">Major Links</a> </li>
		<li> <a class="dlink" href="contacts.jsp">Contacts</a> </li>
		
	</ul>







</div>




<div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
	

</p>
</div>
<!--  -->


</div>


<script type="text/javascript"> document.getElementById("weeknumber").selectedIndex=<%=session.getAttribute("weekindex")%>;</script>

<%-- <%} %> --%>
</body>
</html>