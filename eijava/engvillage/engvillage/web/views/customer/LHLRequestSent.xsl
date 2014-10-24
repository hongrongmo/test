<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet  version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:java="http://www.jclark.com/xt/java/java.net.URLEncoder"
	exclude-result-prefixes="java xsl"
	>

<xsl:output method="html" encoding="UTF-8"/>

<xsl:include href="./Footer.xsl"/>

<!--This XSL renders the XML Data provided by LHLEmailOrder.jsp -->
<!--This Page typically displays a email confirmation message that your Email has been sent -->
<xsl:template match="root">

<html>
<head>
	<title>Linda Hall Library Document Request</title>
	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
<!-- Start of logo table -->
	<center>
		<table border="0" width="99%" cellspacing="0" cellpadding="0">
			<tr><td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td><td valign="middle" align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td></tr>
			<tr><td valign="top" height="5" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
			<tr><td valign="top" height="2" bgcolor="#3173B5" colspan="2"><img src="/engresources/images/spacer.gif" height="2" border="0"/></td></tr>
			<tr><td valign="top" height="10" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
		</table>
	</center>
<!-- End of logo table -->


	<table border="0" cellspacing="0" cellpadding="0">
		<tr><td valign="top"><a class="EvHeaderText">Linda Hall Library Request Sent </a></td></tr>
		<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>					
		<tr><td valign="top"><a CLASS="MedBlackText">Your request has been sent. You will be contacted by a Linda Hall Library representative for confirmation of your order and for billing arrangements. To contact Linda Hall Library regarding your order, please call 1 (800) 662-1545 or e-mail </a><a CLASS="LgBlueLink" href="mailto:ei@lindahall.org">ei@lindahall.org</a></td></tr>
	</table>

	<!-- jam 12/23/2002
		while fixing bad character in LHL forms
		changed to use default footer for file
	-->
	<!-- Footer -->
	<xsl:call-template name="FOOTER"/>

</body>
</html>


</xsl:template>
</xsl:stylesheet>
