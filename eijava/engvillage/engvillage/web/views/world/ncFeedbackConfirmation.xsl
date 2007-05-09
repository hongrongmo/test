<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  	 version="1.0"
  	 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  	 xmlns:java="http://www.jclark.com/xt/java/java.net.URLEncoder">

<xsl:include href="Footer.xsl"/>

<xsl:template match="root">

<html>
<head>
	<title>Customer Feedback Sent</title>
		<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" rightmargin="0" leftmargin="0">
<!-- Start of logo table -->
<table border="0" width="99%" cellspacing="0" cellpadding="0">
<tr><td valign="top">
<a href="/controller/servlet/Controller?CID=home">
<img src="/engresources/images/ev2logo5.gif" border="0"/>
</a>
</td></tr>
<tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
<tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td></tr>
<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
</table>
<!-- End of logo table -->


<!-- Start of logo table -->
<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr><td valign="top">
		<xsl:apply-templates select="HEADER"/>
	</td></tr>
	<tr><td valign="top" colspan="3">
		<xsl:apply-templates select="GLOBAL-LINKS">
		<xsl:with-param name="LINK">Quick</xsl:with-param>
		</xsl:apply-templates>
	</td></tr>
	<tr><td valign="top" colspan="11" height="20" bgcolor="#C3C8D1"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
</table>
</center>
<!-- End of logo table -->


<!-- Start of the lower area below the navigation bar -->
<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
<tr><td valign="top" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
<tr><td valign="top"><a class="EvHeaderText">Thank you</a></td></tr>
<tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
<tr><td valign="top">
<P><A CLASS="MedBlackText">Thank you for your feedback! We value every comment sent to us and will respond to your feedback as quickly as possible.
To continue browsing Engineering Village, click on one of above menu tabs, or click </A>
<a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=home">here</a><A CLASS="MedBlackText"> to return to the home page .
</A></P>
</td></tr>
</table>
</center>
<!-- End of lower area below the navigation bar. -->

<xsl:apply-templates select="FOOTER"/>

</body>
</html>

</xsl:template>
</xsl:stylesheet>
