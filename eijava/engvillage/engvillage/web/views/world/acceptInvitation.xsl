<?xml version="1.0"?>

<xsl:stylesheet
  	 version="1.0"
  	 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  	 xmlns:java="java:java.net.URLEncoder">

<xsl:include href="Footer.xsl"/>

<xsl:template match="root">


<html>

<head>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>



<!-- end of javascript -->

	<title>Accept Group Invitation</title>

</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
<!-- Start of logo table -->
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
	<td valign="top">
		<a href="/controller/servlet/Controller?CID=home"><img src="/engresources/images/ev2logo5.gif" border="0"/></a>
	</td>
	</tr>
	<tr>
	   <td valign="top" height="5">
	   <img src="/engresources/images/spacer.gif" border="0" height="5"/>
	   </td>
	</tr>
	<tr>
		<td valign="top" height="2" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td>
	</tr>
	<tr>
		<td valign="top" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td>
	</tr>
</table>
<!-- End of logo table -->


<!-- table for contents -->
	<center>
		<table border="0" width="99%" cellspacing="0" cellpadding="0">
			<tr>
			<td><center><table><tr><td><br/><br/>
			<span CLASS="MedBlackText">

				<b>Welcome to Engineering Village.</b><p/>

				You must be recognized as an Engineering Village customer to accept this invitation.
			</span>
			</td></tr></table></center>
			</td>
			</tr>
		</table>
	</center>


<br/>

<!-- end of the lower area below the navigation bar -->
<xsl:apply-templates select="FOOTER"/>


</body>
</html>
</xsl:template>

</xsl:stylesheet>