<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.Map" %>
    <%@ page import="java.util.*" %>
    <%@ page import="java.io.*" %>
   <%@ page import="java.nio.file.Files"%>
    <%@ page import="org.ei.Export" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Weekly Data Processing Report</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/reports.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
    <link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
	<link rel="icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
    <!-- <style>
    	table {border: 0px solid #ababab; float:none}
    	table td {border: 0px }
    </style> -->
    
</head>

<body>
	<%@ include file="views/includes/header.jsp" %>
  	<div class="wrapper">
  		<div tiles:fragment="content">
  			<div class="innercontainer">
  				<c:if test="${not empty param.error}">
  				   <br />
				   <div class="paddingL10">
				   	 <img style="vertical-align:bottom" src="${pageContext.servletContext.contextPath}/static/images/red_warning.gif">
				   	 <b>&nbsp;&nbsp;Invalid username and password.</b>
				   </div>
				</c:if>
				<c:if test="${not empty param.logout}">
				   <br />
				   <span id="messagecontainer">You have been logged out.</span>
				   <br />
				</c:if>
				<br />
				</div>

<!--  -->

<div class="innercontainer">
<!-- <div id="nav">


</div> -->

<div id="section2" style="padding-left: 10px; padding-right: 10px;">    <!-- rename it from section to section2 so it does not get left padding -->

<b style="color: #4169E1">Select Processing Weeknumber:</b> 
<table>
<tr>
<td>

<!-- Year dropdownlist -->
<form name="yr" action="${pageContext.servletContext.contextPath}/Reports" method="get"> 
<select name="year" id="year" onchange="this.form.submit()">
 <OPTION VALUE="0">2015</OPTION>
 <OPTION VALUE="1" selected="selected">2016</OPTION>
 </select>
 </form>
 
</td>

<!-- Weeks dropdownList -->
<td>

 <form name="f" action="${pageContext.servletContext.contextPath}/Reports" method="post"> 
   
 <SELECT name="weeknummber" id="weeknumber" style="width: 180px">
    <% 
    int startWeek = 1;
    final int endWeek = 53;
    int year = 2016;   // set default to current year
    if(session.getAttribute("year") !=null)
    {
    	year = Integer.parseInt(session.getAttribute("year").toString());
    }
   
    String currentWeek;
    int weekdecrease = endWeek;
    Calendar cal = null;
    
    int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    for(;startWeek <= endWeek; startWeek++)
    { 
    	if(startWeek <10)
    	{
    		currentWeek = year + "0" + startWeek;
    	}
    	else
    	{
    		currentWeek = Integer.toString(year) + Integer.toString(startWeek);
    	}
    	
    	int diff = week - (startWeek);
    	cal = Calendar.getInstance();
		int j = cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek();
		 if(year==2015)
		{
			//cal.add(Calendar.DATE, -(week*7)-j+1);
			//cal.add(Calendar.DATE, -(week*7)-j+3);
			
			 cal.add(Calendar.YEAR, -1);
			 cal.add(Calendar.DATE, -((diff+1)*7)-j+2);
				
		}
		else
		{ 
			//cal.add(Calendar.DATE, ((week-1)*7)-j+1);
			cal.add(Calendar.DATE, -(diff*7)-j+1);
		}
			
    	Date start = cal.getTime();
    	cal.add(Calendar.DATE,  4);
    	Date end = cal.getTime();
    	
    	/* if(year ==2015)
    	{
    		week--;
    	}
    	else if(year >=2016)
    	{
    		week++;
    	}
    	 */
    	
    if(startWeek<19 && year==2015)  // only 2015 that started from wk 19
    	{
    %>
        <OPTION VALUE="<%=currentWeek%>" disabled="disabled"> Wk <%=" "+startWeek + " ,"+start.toString().substring(4, 11).trim() + " - " + end.toString().substring(4, 11).trim()%></OPTION>
<%
    	}
    	else
    	{
    		%>
    		 <OPTION VALUE="<%=currentWeek%>"> Wk <%=" "+startWeek + " ,"+start.toString().substring(4, 11).trim() + " - " + end.toString().substring(4, 11).trim()%></OPTION>
    <%
    	}
	}
    %>
    </SELECT> 
    <input type="hidden" name="selectedValue" value=''/> 
    <input name="submit" type="submit" value="submit" />
 
 <!-- another place for pdf export & download -->
	&nbsp;&nbsp;&nbsp;&nbsp;
 <% if(((ArrayList<ArrayList<String>>)session.getAttribute("Report")) !=null && ((ArrayList<ArrayList<String>>)session.getAttribute("Report")).size()>0)
{%>


 <input type="submit" name="export" value="PDF Export" />
 <%} %>
 <!--  -->
 
 
 </form>
</td>

</tr>
</table>
	 
    </br>
 

   <table id="report">
   <thead>
   	<tr>
   		<%-- <caption>LoadNumber: <%= session.getAttribute("weeknum")%></caption> --%>
   		<caption>LoadNumber: <%
   		if(session.getAttribute("weeknum") ==null)
   		{
   			out.println("");
   		}
   		
   		else
   		{
   			out.println(session.getAttribute("weeknum"));
   		}
   		%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   		<span class="weekdate" style="float: center">Week: <%
   				if(session.getAttribute("WeekRange") == null)
   				{
   					out.println("");
   				}
   				else
   				{
   					out.println(session.getAttribute("WeekRange"));
   				}
   			
   				%></span>
   		
   		</caption>
   		
   		
   	</tr>
   </thead>
   <thead>
   <tr>
      <th>DataSet</th>
      <th>Operation</th>
      <th>Week(YYYYWK)</th>
      <th>Source File Name</th>
      <th>Source File Count</th>
      <th>Conv File Count</th>
      <th>Loaded Database Count</th>
      <%if(session.getAttribute("weeknum") !=null && Integer.parseInt(session.getAttribute("weeknum").toString()) >=201547){ %>
       <th>Update Count</th>
      <%} %>
      <th>Discrepancy</th>
      <th>Reason</th>
    </tr>
  </thead>
  
  <tfoot>
    <tr>				
      <c:if test="${Total ne null and not empty Total.TOTALSRCCOUT}"><td>Total</td></c:if>
       <c:if test="${Total ne null and not empty Total.TOTALSRCCOUT}"><td></td></c:if>
       <c:if test="${Total ne null and not empty Total.TOTALSRCCOUT}"><td></td></c:if>
       <c:if test="${Total ne null and not empty Total.TOTALSRCCOUT}"><td></td></c:if>
       
       
       <c:if test="${Total.TOTALSRCCOUT ne null}"><td>${Total.TOTALSRCCOUT}</td></c:if>
       <c:if test="${Total.TOTALCONVERTEDCOUNT ne null}"><td>${Total.TOTALCONVERTEDCOUNT}</td></c:if>
       <c:if test="${Total.TOTALLOADEDCOUNT ne null}"><td>${Total.TOTALLOADEDCOUNT}</td></c:if>
        <%if(session.getAttribute("weeknum") !=null && Integer.parseInt(session.getAttribute("weeknum").toString()) >=201547){ %>
       <c:if test="${Total.TOTALUPDATEDCOUNT ne null}"><td>${Total.TOTALUPDATEDCOUNT}</td></c:if>
       <%} %>
       <c:if test="${Total.TOTALREJECTEDCOUNT ne null}"><td>${Total.TOTALREJECTEDCOUNT}</td></c:if>
      
	 <c:if test="${Total ne null and not empty Total.TOTALSRCCOUT}"><td></td></c:if>
    </tr>
  </tfoot>
  
  
  
  <tbody>
   <% int i = 0; %>
    <c:forEach items="${Report}" var="item">
       <tr>
				<c:choose>
					<c:when test="${item.DATASET ne null}">
						<td>${item.DATASET}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.OPERATION ne null}">
						<td>${item.OPERATION}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.LOADNUMBER ne null}">
						<td>${item.LOADNUMBER}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.SOURCEFILENAME ne null}">
						<td>${item.SOURCEFILENAME}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.SOURCEFILECOUNT ne null}">
						<td>${item.SOURCEFILECOUNT}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.CONVERTEDFILECOUNT ne null}">
						<td>${item.CONVERTEDFILECOUNT}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				
				<c:choose>
					<c:when test="${item.MASTERTABLECOUNT ne null}">
						<td>${item.MASTERTABLECOUNT}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<%if(session.getAttribute("weeknum") !=null && Integer.parseInt(session.getAttribute("weeknum").toString()) >=201547){ %>
				<c:choose>
					<c:when test="${item.UPDATE ne null}">
						<td>${item.UPDATE}</td>
					</c:when>
					<c:otherwise>
						<td>0</td>
					</c:otherwise>
				</c:choose>
				<%} %>
				
				<c:choose>
					<c:when test="${item.SRC_MASTER_DIFF ne null}">
						<td>${item.SRC_MASTER_DIFF}</td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${item.ERRORMESSAGE ne null}">
						<td>${item.ERRORMESSAGE} 
							<%if((((ArrayList <Map<String,String>>)session.getAttribute("Report")).get(i).get("ERRORMESSAGE").length()>0)){ %>
							<a class="datanotes" id="datanotes" title="See Data Processing notes" href="#" onclick="window.open('dataprocessingnotes.jsp', 'Notes', 'width=999 ,height=800,toolbar=yes,scrollbars=1')">
    						<img class="dataenoteimg" border="0" align="bottom" src="${pageContext.request.contextPath}/static/images/Full-Text.png" title=""></img>
							</a>
							<%} i++;%></td>
					</c:when>
					<c:otherwise>
						<td>(null)</td>
					</c:otherwise>
				</c:choose>
			</tr>	
    </c:forEach>
    </tbody>
</table>



</br>



<!-- Testing Export Report to a pdf file, without download, works fine -->
<%-- <% if(((ArrayList<ArrayList<String>>)session.getAttribute("Report")) !=null && ((ArrayList<ArrayList<String>>)session.getAttribute("Report")).size()>0)
{%>
 <input type="button" name="Export" onclick="<%new Export().createPdf("C:/ws/EV-tools/evdataloadreport/test_table.pdf",(ArrayList<Map<String,String>>)session.getAttribute("Report"),(Map<String,Integer>)session.getAttribute("Total"));%>" value="PDF Export"/>

<%
} %>
 --%>



</div>

<!--  END OF INNERCONTAINER -->
</div>



<%-- <div id="footer">
<p id="copyright">Copyright &copy; &nbsp;<%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/copyright.js"></script>
	
</p>
</div> --%>
<!--  -->





			</div>
		</div>


<script type="text/javascript"> 
<% if(session.getAttribute("weekindex")==null)
{
	// set default week for current year
	if(session.getAttribute("year")==null || (session.getAttribute("year") !=null && Integer.parseInt(session.getAttribute("year").toString())>=2016))
		{%>
			document.getElementById("weeknumber").selectedIndex=<%=Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)-1%>;
		<%}
	
	//document.getElementById("weeknumber").selectedIndex=<%=Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)-1;  //original for 2015 before adding 2016
	
}
else{%>
document.getElementById("weeknumber").selectedIndex=<%=session.getAttribute("weekindex")%>;
<%}%>



// Retain year to selected value 
<% if(session.getAttribute("yearindex")==null)
{%>
	document.getElementById("year").selectedIndex=1;
<%}
else
{%>
document.getElementById("year").selectedIndex=<%=session.getAttribute("yearindex")%>;
<%}%>

</script>

<!--  open dataloading notes window -->
<script type="text/javascript">
function DataloadNotes()
{
	var wind =  window.open("dataprocessingnotes.jsp","","width=100, height=100");
	wind.document.title = "Data Rejections Notes";
	}
</script>


</body>
</html>