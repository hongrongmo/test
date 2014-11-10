<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Authentication Failure</title>
<link href="/static/css/popups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>

</head>
<body>
<div>
<div class="title">Warning</div>
<div class="msg">
	<p>Engineering Village has changed their authentication system.  Our records indicate that the username and password are no longer valid.  You will need to contact your administrator for new credentials</p><br/>
	<p>Your Institution:</p><br/>
	<p>Your Contact Information:</p><br/>
	<p>You may also access Engineering Village using your Athens/Institutional (Shibboleth) login.</p><br/>
	
</div>


<div style="float:right;padding-top:15px">
	<a href="#" onClick="TINY.box.hide();return false;" >Close</a>
</div>

</div>
</body>
</html>