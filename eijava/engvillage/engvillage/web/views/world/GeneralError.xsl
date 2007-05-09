<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:java="http://www.jclark.com/xt/java/java.net.URLEncoder">
<xsl:include href="Footer.xsl"/>


<xsl:template match="root">


<html>

<head>

 <title>Engineering Village System Error</title>
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
						parent.document.location = url;
					}

				}

			</script>
		</xsl:comment>
	]]>

</xsl:text>

<!-- end of javascript -->
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" leftmargin="0" rightmargin="0">
<!-- Start of toplogo and navigation bar -->
<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
<tr><td valign="top" height="4"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
<tr><td valign="top"><a href="javascript:forwardLink('/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=quickSearch');"><img src="/engresources/images/ev2logo5.gif" border="0"/></a></td></tr>
<tr><td height="20" bgcolor="#FFFFFF"><img src="/engresources/images/spacer.gif" height="20"/></td></tr>
<tr><td valign="top" height="1" width="690" bgcolor="#FFFFFF"><img src="/engresources/images/spacer.gif" height="1" width="690"/></td></tr>
</table>
<!-- end of toplogo and navigation bar -->


<!-- Start of the lower area below the navigation bar -->
<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
<tr><td valign="top" height="15" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td></tr>
<tr><td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td><td valign="top" colspan="2"><a class="EvHeaderText">System error</a></td></tr>
<tr><td valign="top" height="2" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td></tr>
<tr><td valign="top" width="20" height="2"><img src="/engresources/images/spacer.gif" border="0" width="20" height="2"/></td><td valign="top" height="2" bgcolor="#6699CC"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td><td valign="top" width="10" height="2"><img src="/engresources/images/spacer.gif" border="0" width="10" height="2"/></td></tr>
<tr><td valign="top" height="20" colspan="3"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
<tr><td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td>
	<td valign="top">
		<xsl:choose>
			<xsl:when test="string(/root/MESSAGE/DISPLAY)">
				<A CLASS="MedBlackText"><xsl:value-of select="/root/MESSAGE/DISPLAY"/></A>
			</xsl:when>
			<xsl:otherwise>
				<A CLASS="MedBlackText">Sorry, a system error has occurred, and your request cannot be completed.</A>
				<P>
				<A CLASS="MedBlackText">You may </A>
				<A CLASS="LgBlueLink" href="javascript:forwardLink('/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=feedback');">contact us</A><A CLASS="MedBlackText"> to report this problem, or you may begin a </A>
				<A CLASS="LgBlueLink" href="javascript:forwardLink('/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=quickSearch');">new search</A>.
				</P>
			</xsl:otherwise>
		</xsl:choose>
	</td>
	<td valign="top" width="10"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td>
</tr>
</table>
<!-- end of the lower area below the navigation bar -->
<br/>

	<!-- Start of footer-->
	<xsl:apply-templates select="FOOTER"/>

</body>

</html>

</xsl:template>

</xsl:stylesheet>