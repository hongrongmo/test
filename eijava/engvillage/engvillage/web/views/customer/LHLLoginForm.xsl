<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet  version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
	>

<xsl:output method="html" encoding="UTF-8"/>

<xsl:include href="./Footer.xsl"/>

<!--
   This xsl file takes the userid and password and authenticates the user for linda hall oredr request
-->

<!-- end of include -->

<xsl:template match="LINDA-HALL">

	<xsl:variable name="DOC-ID">
		<xsl:value-of select="DOC-ID"/>
	</xsl:variable>

	<xsl:variable name="DATABASE-ID">
		<xsl:value-of select="DATABASE"/>
	</xsl:variable>

	<xsl:variable name="MATCH">
		<xsl:value-of select="MATCH"/>
	</xsl:variable>

	<xsl:variable name="SESSION-ID">
		<xsl:value-of select="SESSION-ID"/>
	</xsl:variable>

<html>
<head>
	<title>Linda Hall Library Document Request</title>
	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
		<xsl:text disable-output-escaping="yes">
		<![CDATA[
		<xsl:comment>
			<SCRIPT LANGUAGE="JavaScript">

			/* Hide content from old browsers */
			function validForm(lhlrequest) {
				if (lhlrequest.pword.value =="") {
					alert ("You must enter a password")
					lhlrequest.pword.focus()
					return false
				}
			}
			</SCRIPT>
		</xsl:comment>
		]]>
		</xsl:text>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" onLoad="lhlrequest.pword.focus();">
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

	<center>
		<table border="0" width="99%" cellspacing="0" cellpadding="0">

			<tr><td valign="top"><a class="EvHeaderText">Linda Hall Library Document Request</a></td></tr>
			<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>			
			<tr><td valign="top"><a CLASS="MedBlackText">A password is required to access the Linda Hall Library document request form:</a></td></tr>
			<tr><td valign="top" height="3"><img src="/engresources/images/spacer.gif" border="0" height="3"/></td></tr>

			<tr><td valign="top" width="250">
			<table border="0" width="250" cellspacing="0" cellpadding="0">

				<FORM name="lhlrequest" method="post" action="/controller/servlet/Controller?CID=lhlViewOrderForm" onsubmit="return validForm(lhlrequest)">
					<input type="hidden" name="docid">
						<xsl:attribute name="value"><xsl:value-of select="$DOC-ID"/></xsl:attribute>
					</input>
					<input type="hidden" name="database">
						<xsl:attribute name="value"><xsl:value-of select="$DATABASE-ID"/></xsl:attribute>
					</input>
					<tr><td valign="middle"><a CLASS="MedBlackText">Password: </a></td><td valign="top"><a CLASS="MedBlackText"><input type="password" name="pword" size="24"/></a></td></tr>
					<tr><td valign="top" height="4" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
					<tr><td valign="top">&#160; </td><td valign="top"><a CLASS="MedBlackText"><input type="submit" name="submit" value="Submit"/></a></td></tr>
				</FORM>
			</table>
			</td></tr>
			<tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
			<tr><td valign="top"><a CLASS="MedBlackText">If you don't have a password, you can place an order directly with </a><a CLASS="LgBlueLink" href="http://www.lindahall.org/services/document_delivery/partners/eicompendex.shtml">Linda Hall Library.</a></td></tr>
		</table>
	</center>


<!-- Insert Footer -->
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
