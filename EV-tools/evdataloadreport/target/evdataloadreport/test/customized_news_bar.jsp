<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Customized News Bar</title>

<style>

div.wrap{
    text-align: left;
    margin-left: 0px;
    left: 0px;
}


div.infobar{
border-right: 1px solid #189028;
    border-top: 1px solid #189028;
    border-left: 1px solid #189028;
    padding-right: 4px;
    padding-top: 2px;
    padding-left: 4px;
    background-color: #bbbbbb;
   overflow: auto;
   /* position: relative;    */ 
    float: left;
/*    -moz-transform: rotate(270deg);
    -moz-rotation-point: 0 0;
    -webkit-transform: rotate(270deg);
    -webkit-rotation-point: 0 0;
    -o-transform: rotate(45deg); */
    -ms-transform: rotate(90deg);  
    height: auto;
    top: 0px;
    left:0px;
    margin-top: 200px;
}

.news_bar
{
margin-left: 0px;
}
</style>

</head>
<body>


<div class="wrap">
<script language="javascript">


 var msgs = new Array(
     "<%= Calendar.getInstance().getTime()%>",
     "Current Week Number: <%= Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + 1%>",
     "Current Data Processing Load Number: "+"2015"+"<%= Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + 1%>"
     ); // No comma after last ticker msg


var barwidth='350px' //Enter main bar width in px or %
var setdelay=3000 //Enter delay between msgs, in mili-seconds
var mouseover_color='#E1FFE1' //Specify highlight color
var mouseout_color='#FFFFF0' //Specify default color
/////////////////////////////////////////////////////////////////////

var count=0;
var ns6=document.getElementById&&!document.all
var ie4=document.all&&navigator.userAgent.indexOf("Opera")==-1


/* document.write('<div class="infobar"><form name="news_bar"><input type="button" value="3" onclick="moveit(0)" class="scrollerstyle" style="width:22px; height:22px; border-right-width:0px;" name="prev" title="Previous News"><input type="button" name="news_bar_but" onclick="goURL();" style="font-weight: bold;color:#800000;background:#FFFFFF; width:'+barwidth+'; height:30px; border-width:1; border-color:#000000; cursor:hand" onmouseover="this.style.background=mouseover_color" onmouseout="this.style.background=mouseout_color"><input type="button" value="4" onclick="moveit(1)" class="scrollerstyle" style="width:22px; height:22px; border-left-width:0px;" name="next" title="Next News"></form></div>'); */

document.write('<div class="infobar"><form name="news_bar" style="margin-left:0px;left:0px"><input type="button" name="news_bar_but" onclick="goURL();" style="font-weight: bold;color:#800000;background:#FFFFFF; width:'+barwidth+'; height:30px; border-width:1; border-color:#000000; cursor:hand" onmouseout="this.style.background=mouseout_color"></form></div>');
	


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



</body>
</html>