<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test News Bar</title>
<style>

.scrollerstyle{
font-family:webdings;background:#FFFFFF;border:1px solid #000000;cursor:hand;
}

</style>

</head>
<body>


<script language="javascript">


 var msgs = new Array(
     "Click here to go back to Dynamic Drive",
     "JavaScript Kit- free scripts and tutorials",
     "Freewarejava.com",
     "Wired News- Technology and beyond",
     "Qiksearch.com" ); // No comma after last ticker msg

 var msg_url = new Array(
     "http://www.dynamicdrive.com",
     "http://www.javascriptkit.com",
     "http://freewarejava.com",
     "http://www.wired.com",
     "http://www.qiksearch.com" ); // No comma after last ticker url

var barwidth='350px' //Enter main bar width in px or %
var setdelay=3000 //Enter delay between msgs, in mili-seconds
var mouseover_color='#E1FFE1' //Specify highlight color
var mouseout_color='#FFFFFF' //Specify default color
/////////////////////////////////////////////////////////////////////

var count=0;
var ns6=document.getElementById&&!document.all
var ie4=document.all&&navigator.userAgent.indexOf("Opera")==-1

if (ie4||ns6){
document.write('<form name="news_bar"><input type="button" value="3" onclick="moveit(0)" class="scrollerstyle" style="width:22px; height:22px; border-right-width:0px;" name="prev" title="Previous News"><input type="button" name="news_bar_but" onclick="goURL();" style="color:#000000;background:#FFFFFF; width:'+barwidth+'; height:22px; border-width:1; border-color:#000000; cursor:hand" onmouseover="this.style.background=mouseover_color" onmouseout="this.style.background=mouseout_color"><input type="button" value="4" onclick="moveit(1)" class="scrollerstyle" style="width:22px; height:22px; border-left-width:0px;" name="next" title="Next News"></form>');
}
else{
document.write('<form name="news_bar"><input type="button" value="Previous" onclick="moveit(0)">')
if (navigator.userAgent.indexOf("Opera")!=-1)
document.write('<input type="button" name="news_bar_but" onclick="goURL();" style="width:'+barwidth+'" border="0">')
else
document.write('<input type="button" name="news_bar_but" onclick="goURL();" width="'+barwidth+'" border="0">')
document.write('<input type="button" value="Next" onclick="moveit(1)"></form>')
}

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


</body>
</html>