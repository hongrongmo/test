<%@ page language="java" session="false" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
	<title>Engineering Village - Session Expired</title>
	<link type="image/x-icon" href="/static/images/engineering_village_favicon.gif" rel="SHORTCUT ICON"></link>
	<link href="/static/css/ev_txt.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	<link href="/static/css/ev_common_sciverse.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</head>
<body onload="onloadfunction();">

<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
<div id="container" class="marginL15">
	
	<br/>
	<br/>
	
	
	<h3>Your session has expired due to inactivity. </h3><span><a href="/home.url" title="Begin new session">Begin new session.</a></span>
	
    <br/>
    <br/>
    <br/>
</div>
<script>
function onloadfunction(){
	
	  if (/MSIE (\d+\.\d+);/.test(navigator.userAgent))
	  { 
	     var ieversion=new Number(RegExp.$1);
	     if (ieversion<8)
	     {
	       if(document.getElementById('ie7msg') != 'undefined' &&  document.getElementById('ie7msg') != null ){
	    	   document.getElementById('ie7msg').style.display = 'block';
	       }
	     }
	  }
}

</script>

<jsp:include page="/WEB-INF/pages/include/footer.jsp" />
<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />

</body>
</html>

