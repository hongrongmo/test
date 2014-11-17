<?xml version="1.0" encoding="UTF-8"?>
<!-- This XSL renders the XML returned by EmailConfirmation.jsp. -->
<!-- This XSL is used to show the confirmation page. -->
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="xsl html"
>

<xsl:output method="html"/>
<xsl:include href="Footer.xsl" />

<xsl:template match="PAGE">

<html>
<head>
	<title>Selected Set: E-mail Sent</title>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
<!-- Start of logo table -->
<center>
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td>
			<td valign="middle" align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td>
		</tr>
		<tr><td valign="top" height="5" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="2" bgcolor="#3173B5" colspan="2"><img src="/engresources/images/spacer.gif" height="2" border="0"/></td></tr>
		<tr><td valign="top" height="10" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
	</table>
</center>
<!-- End of logo table -->

<!-- message -->
<center>
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
		<tr><td valign="top" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
		<tr><td valign="top"><A CLASS="MedBlackText">Your e-mail has been successfully sent to <xsl:value-of select="TO-ADDRESS"/></A></td></tr>
	</table>
</center>


  <!-- INCLUDE FOOTER  -->
	<xsl:call-template name="FOOTER"/>


</body>
</html>
</xsl:template>

</xsl:stylesheet>
