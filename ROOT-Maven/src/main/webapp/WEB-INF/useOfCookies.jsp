<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
	<title>Engineering Village - Cookies</title>
	<link type="image/x-icon" href="/static/images/favicon.ico" rel="SHORTCUT ICON"></link>
	<link href="/static/css/ev_common_sciverse.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"/>
	<style type="text/css"> 
		p {padding:0; margin: 7px 0 0 0}
		h2 {padding:0; margin:7px 0 3px}
		h3 {background: none repeat scroll 0 center #BBDAD2;font-size: 100%;margin: 1em 0;padding: 2px 5px;}
		
		table {border: 1px solid #ababab; float:none}
		table td {padding: 2px 7px; border: 1px solid #ababab; border-collapse: collapse; word-wrap: break-word; max-width: 500px}
		input, select { margin: 0; padding: 0}
		input.button {padding: 3px}
		 
		.clear {clear:both;font-size:0;height:0;}
		
		.tabnav { font-size:12px; text-align: left; margin: 1em 0 1em 0; border-bottom: 1px solid #6C6; list-style-type: none; padding: 3px 10px 3px 10px; font-weight: bold}
		.tabnav li.tab {border-bottom: 1px solid white; background-color: white; display: inline; }
		.tabnav li.active {position:relative; top:1px}
		.tabnav li a { padding: 3px 6px; border: 1px solid #6C6; background-color: #CFC; color: #666; margin-right: 0px; text-decoration: none; border-bottom: none;}
		.tabnav li.active a { font-size:14px; background-color: #fff; color: #000;}

		.custimagetitle { font-weight: bold; font-style: italic; text-decoration: underline; margin-bottom: 5px }

		.example{cursor:pointer}
		.searchcomponentfullwrap {margin: 0; padding: 0 22px 7px 0;}
		.searchcomponentlabel {margin: 0;font-weight: bold;color:#148C75;font-size: 14px;float: left;line-height: 14px;width: 112px;vertical-align: middle;}
		.databasecheckall {float:left; margin: 0;padding:0}
		.databasecheckgroup {float: left; width: 600px; margin: 0 0 0 8px; padding:0}
		.databasecheckgroup li {list-style:none; float: left; width: 150px; margin:0 0 3px 0; padding:0}
		.databasecheckgroup li label {margin-left: 5px}
		.databasecheckall input, 
		.databasecheckgroup input {
			width: 13px;     
			height: 13px;     
			padding: 0;     
			margin:0;     
			vertical-align: bottom;     
			position: relative;     
			top: -1px;     
			*overflow: hidden; 
		} 
	</style>
	<script type="text/javascript" src="/static/js/jquery/jquery-1.7.2.min.js?v=${releaseversion}"></script>
</head>

<body>
	<jsp:include page="/WEB-INF/pages/include/headernull.jsp"/>

	<div id="container" style="padding-left:10px; padding-top:25px; padding-bottom: 10px;">
      
      <p>The website uses cookies. By using the website and agreeing to this policy, you consent to our use of cookies in accordance with the terms of this policy.</p>

		<h3>About cookies</h3>
		<p>Cookies are files, often including unique identifiers, that are sent by web servers to web browsers, and which may then be sent back to the server each time the browser requests a page from the server.</p>
		<p>Cookies can be used by web servers to identity and track users as they navigate different pages on a website, and to identify users returning to a website.</p>
		<p>Cookies may be either "persistent" cookies or "session" cookies.  A persistent cookie consists of a text file sent by a web server to a web browser, which will be stored by the browser and will remain valid until its set expiry date (unless deleted by the user before the expiry date). A session cookie, on the other hand, will expire at the end of the user session, when the web browser is closed.</p>


		<h3>Cookies on the website</h3>
		<table align="left" cellpadding="0" cellspacing="0" border="0" id="userinfo_table">
		<thead><tr style="background-color: #BBDAD2;"><td><b>Cookie Name</b></td><td><b>Description</b></td></tr></thead>
		<tbody>
			<tr><td><b>SECUREID </b></td><td>Used for Captcha processing (user submits multiple searches, EV verifies they are human).</td></tr>
			<tr><td><b>EISESSION </b></td><td>Session ID cookie.  Used to implement a user’s session.</td></tr>
			<tr><td><b>DEPARTMENT </b></td><td>Certain customers have requested some cookie-level tracking.  For them, this cookie indicates when they’ve already seen the department splash page.</td></tr>
			<tr><td><b>DAYPASS </b></td><td>Indicates user is on a day-long pass for EV.</td></tr>
			<tr><td><b>custid </b></td><td>Indicates user has Institutional (Shibboleth) login for EV.</td></tr>
			<tr><td><b>idp </b></td><td>Indicates user has Institutional (Shibboleth) login for EV.</td></tr>
			<tr><td><b>__utma<br/>__utmb<br/>__utmc<br/>__utmz </b></td><td>These web analytics cookies, provided by Google Inc., are used to collect information about how visitors use our site. We use the information to compile reports and to help us improve the site. The cookies collect information in an anonymous form, including the number of visitors to the site, where visitors have come to the site from and the pages they visited.</td></tr>
			
			
		</tbody>
		</table>

		<h3>How we use cookies</h3>
		<p>Cookies do not contain any information that personally identifies you, but personal information that we store about you may be linked, by us, to the information stored in and obtained from cookies. The cookies used on the website include those which are strictly necessary cookies for access and navigation, cookies that track usage (performance cookies), remember your choices (functionality cookies).</p>
		<p>We may use the information we obtain from your use of our cookies for the following purposes:</p>
		<ol>
			<li>to recognise your computer when you visit the website</li>
			<li>to track you as you navigate the website</li>
			<li>to improve the website’s usability</li>
			<li>to analyse the use of the website</li>
			<li>in the administration of the website</li>
			<li>to personalise the website for you, including targeting advertisements which may be of particular interest to you.</li>
		</ol>

		<h3>Blocking cookies</h3>
		<p>Most browsers allow you to refuse to accept cookies. For example:</p>
		<ol>
			<li>in Internet Explorer you can refuse all cookies by clicking "Tools", "Internet Options", "Privacy", and selecting "Block all cookies" using the sliding selector.</li>
			<li>in Firefox you can block all cookies by clicking "Tools", "Options", and un-checking "Accept cookies from sites" in the "Privacy" box.</li>
			<li>in Google Chrome you can adjust your cookie permissions by clicking "Options", "Under the hood", Content Settings in the "Privacy" section. Click on the Cookies tab in the Content Settings.</li>
			<li>in Safari you can block cookies by clicking “Preferences”, selecting the “Privacy” tab and “Block cookies”.</li>
		</ol>	
		<p>Blocking all cookies will, however, have a negative impact upon the usability of many websites.  If you block cookies, you may not be able to use certain features on the website (log on, access content, use search functions).</p>

		<h3>Deleting cookies</h3>
		<p>You can also delete cookies already stored on your computer:</p>
		<ol>
			<li>in Internet Explorer, you must manually delete cookie files.</li>
			<li>in Firefox, you can delete cookies by, first ensuring that cookies are to be deleted when you "clear private data" (this setting can be changed by clicking "Tools", "Options" and "Settings" in the "Private Data" box) and then clicking "Clear private data" in the "Tools" menu.</li>
			<li>in Google Chrome you can adjust your cookie permissions by clicking "Options", "Under the hood", Content Settings in the "Privacy" section. Click on the Cookies tab in the Content Settings.</li>
			<li>in Safari you can delete cookies by clicking “Preferences”, selecting the “Privacy” tab and “Remove All Website Data”.</li>
		</ol>
		<p>Obviously, doing this may have a negative impact on the usability of many websites.</p>
	</div>	
	<span class="clear"></span>
	<jsp:include page="/WEB-INF/pages/include/footer.jsp" />
	<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />

</body>

</html>