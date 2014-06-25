<?xml version="1.0" encoding="UTF-8"?>
<!-- This XSL renders the XML returned by EmailConfirmation.jsp. -->
<!-- This XSL is used to show the confirmation page. -->
<!-- ********************************************************************** -->
<!-- TODO: put this whole thing under the new Refresh pattern (Stripes/JSP) -->
<!-- ********************************************************************** -->
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="xsl html"
>

<xsl:output method="html"/>
<xsl:include href="Footer.xsl" />
<xsl:include href="./HeaderNull.xsl"/>

<xsl:template match="PAGE">

<html>
<head>
	<title>Selected Set: Email Sent</title>
  <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
	<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_txt_v01.css"/>
	<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_common_sciverse_v01.css"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
	<!-- HEADER -->
	<xsl:call-template name="HEADERNULL"/>

<!-- message -->
<center>
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
		<tr><td valign="top" height="20"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>
		<tr><td valign="top"><A CLASS="MedBlackText">Your email has been successfully sent to <xsl:value-of select="TO-ADDRESS"/></A></td></tr>
	</table>
</center>


  <!-- INCLUDE FOOTER  -->
	<xsl:call-template name="FOOTER"/>


</body>
</html>
</xsl:template>

</xsl:stylesheet>
