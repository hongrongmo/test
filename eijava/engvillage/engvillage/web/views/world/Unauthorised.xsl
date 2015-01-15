<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="Footer.xsl"/>

<xsl:template match="root">


<html>

<head>

<title>Engineering Village - System Error</title>
		<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>

 <xsl:text disable-output-escaping="yes">

	 <![CDATA[

	 <xsl:comment>

		 <script language="javascript">

			 	// This method is here for when the error page
				// is loaded into a child window.
				// we do not want the liks to be loaded in the child
				// but in the parent
				function forwardLink(url) {

					if (window.opener != null) {
						window.opener.location = url;
						self.close();
					} else {
						document.location = url;
					}

				}
		</script>

	</xsl:comment>

	]]>

</xsl:text>

<!-- end of javascript -->

	<title>Engineering Village - System error</title>

</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
<!-- Start of logo table -->
<table border="0" width="99%" cellspacing="0" cellpadding="0">
<tr><td valign="top">
<a href="javascript:forwardLink('/controller/servlet/Controller?CID=home')">
<img src="/engresources/images/ev2logo5.gif" border="0"/>
</a>
</td></tr>
<tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
<tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td></tr>
<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
</table>
<!-- End of logo table -->


<!-- Start of the lower area below the navigation bar -->
<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
	<tr>
	<td valign="top" height="15" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td></tr>
	<tr>
	<td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td>
	<td valign="top" colspan="2"><a class="EvHeaderText">Unauthorized request/Expired session</a></td></tr>
	<tr>
	<td valign="top" height="2" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td></tr>
	<tr>
	<td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td><td valign="top">
	<A CLASS="MedBlackText">
	Either the page you requested does not exist, or you are not authorized to view it. Your session may have also expired; please </A>
	<A CLASS="LgBlueLink" href="javascript:forwardLink('/controller/servlet/Controller?CID=home')">click here</A>
	<A CLASS="MedBlackText"> to return to Engineering Village.</A>

	</td><td valign="top" width="10"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td></tr>
</table>

<!-- end of the lower area below the navigation bar -->
<xsl:apply-templates select="FOOTER"/>


</body>
</html>
</xsl:template>

</xsl:stylesheet>