<?xml version="1.0"?>

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="html xsl"
	>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Footer.xsl"/>

<xsl:template match="root">

	<html lang="en">
	<head>
		<title>Welcome to Engineering Village</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
		<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
		<link rel="stylesheet" href="/engresources/stylesheets/ev2home.css" type="text/css"/>

 <xsl:text disable-output-escaping="yes">
	 <![CDATA[
	 <xsl:comment>
			<SCRIPT type="text/javascript" language="JavaScript">
			<!--
			 function validForm(passwordval) {
					if (passwordval.SYSTEM_USERNAME.value =="") {
						alert ("You must enter a Username")
						passwordval.SYSTEM_USERNAME.focus()
						return false
					}

					if (passwordval.SYSTEM_PASSWORD.value =="") {
						alert ("You must enter a password")
						passwordval.SYSTEM_PASSWORD.focus()
						return false
					}
					return true
			}
      // -->
			</script>
	</xsl:comment>
	]]>

</xsl:text>

<!-- end of javascript -->

</head>
	<body onload="javascript:setFocus();">
		<div id="evlogo"><h1><a href="/controller/servlet/Controller?CID=home"><span>Engineering Village</span></a></h1></div>
		<div class="green_border"/>
		<div id="contentmain">
			<div id="welcome">
				<h2><a href="/controller/servlet/Controller?CID=home"><span>Welcome to Engineering Village</span></a></h2>
				<dl>
					<dt>
						<p>Engineering Village is the premier web-based discovery	platform meeting the information needs of the engineering	community. By coupling powerful search tools, an intuitive user	interface and essential content sources, Engineering Village has	become the globally accepted source of choice for engineers, engineering students, researchers and information professionals.</p>
						<p>Engineering Village provides access to today's most important engineering content through one single interface:</p>
					</dt>
					<dd>
						<ul class="ev_list">
							<li>Compendex&#174;</li>
							<li>Engineering Index Backfile</li>
							<li>Inspec&#174;</li>
							<li>Inspec Archive</li>
							<li>NTIS</li>
							<li>Referex</li>
							<li>Patents from USPTO and esp@cenet</li>
							<li>Ei Patents</li>
							<li>EnCompassLIT</li>
							<li>EnCompassPAT</li>
							<li>GeoBase</li>
							<li>Chimica</li>
							<li>CBNB</li>
							<li>PaperChem</li>
							<li>GeoRef</li>
						</ul>
					</dd>
				</dl>
			</div>

			<div id="content_right">
				<div id="regusers">
					<form target="_top" name="passwordval" method="post" action="/controller/servlet/Controller" onsubmit="javascript:return validForm(passwordval);">
						<input type="hidden" name="CID" value="login" />
						<input type="hidden" name="SYSTEM_NEWSESSION" value="true" />
						<dt><span>Registered Users</span></dt>
						<p><a class="RedText">Invalid username or password.  Please try again.</a></p>
						<p><label for="idUsr">Username</label>:&#160;<input name="SYSTEM_USERNAME" id="idUsr" type="text" size="13"/>
						</p>
						<p><label for="idPwd">Password</label>:&#160;&#160;<input name="SYSTEM_PASSWORD" type="password" id="idPwd" size="13"/>
						</p>
						<p style="margin-left:65px;">
						<input type="image" src="/engresources/images/login_button.gif" name="login" alt="Log-In"/>
						&#160;<a class="MedBlueLink" href="/controller/servlet/AthensService">Athens Log-in</a>
						</p>
						<p style="margin-left:70px;"><a class="MedBlueLink" href="https://evauth.engineeringvillage.com/shibboleth/protected.php">Institutional Log-in (Shibboleth)</a></p>
						<p>You may have access to this product through your library or institutional website. Please consult staff at your library or institution for instructions on remote access.
						</p>
						<p><a href="/controller/servlet/Controller?CID=ncFeedback" class="SpLink">Forgot your institutional password?</a></p>
					</form>
				</div>
			  <div id="evoptions">
	        <div id="nav_buy"><h3><a href="https://store.engineeringvillage.com/ppd/choose.do"><span>Buy a Day Pass</span></a></h3></div>
	        <div id="nav_tour"><h3><a href="#" onClick="window.open('/engresources/tour/tour_databases.html', 'newpg', 'status=yes,resizable,scrollbars=1,width=740,height=500')"><span>Tour Engineering Village</span></a></h3></div>
	        <div id="nav_trial"><h3><a target="_blank" href="/engresources/trialForm.jsp"><span>Request a Free Trial</span></a></h3></div>
				</div>
			</div>
		</div>
		<div id="learnabout">
			<dl id="leftside">
				<dt><span>Engineering Village provides:</span></dt>
				<dd>
					<ul>
					    <li>Combined database searching of all databases including deduplication.</li>
						<li>Personalized e-mail alerts.</li>
						<li>The ability to save searches and create personalized folders.</li>
						<li>Easy, Quick &amp; Expert Search options, all of which allow you to save and combine searches.</li>
						<li>The ability to choose preferred output formats (citation, abstracts or detailed) for Selected Record sets, which can then be viewed, printed, saved, downloaded or e-mailed.</li>
					</ul>
				</dd>
			</dl>
			<dl id="rightside">
				<dd>
					<dt><span>Engineering Village provides:</span></dt>
					<ul>
						<li>OpenURL linking to Endeavor LinkFinder Plus, Ex Libris SFX, Serials Solutions Article Linker, and Innovative Interfaces Web Bridge for local holdings checking and full text option presentation.</li>
						<li>Links to full-text using CrossRef.</li>
						<li>Links to document delivery services.</li>
						<li>Context sensitive help.</li>
						<li>Reference Services: Ask a Librarian &amp; Ask an Engineer.</li>
						<li>RSS Feeds and Faceted Searching.</li>
						<li>Tags &amp; Groups.</li>
					</ul>
				</dd>
			</dl>
		</div>

		<div class="green_border"/>
		<br/>

		<xsl:apply-templates select="FOOTER"/>

	<xsl:text disable-output-escaping="yes">
	<![CDATA[
	<xsl:comment>
		<SCRIPT type="text/javascript" language="JavaScript">
    <!--
			function setFocus(){
				document.passwordval.SYSTEM_USERNAME.focus();
			}
    // -->
		</SCRIPT>
	</xsl:comment>
	]]>
	</xsl:text>

</body>
</html>

</xsl:template>

</xsl:stylesheet>