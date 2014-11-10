<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java.net.URLEncoder"
  exclude-result-prefixes="java xsl html"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Footer.xsl"/>


<xsl:template match="root">

<html>
<head>
 <title>Engineering Village - System error</title>
		<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
		 <xsl:comment>
			 <script language="javascript">
	      <![CDATA[
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
	      ]]>
			</script>
		</xsl:comment>

<!-- end of javascript -->
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" leftmargin="0" rightmargin="0">
  <!-- Start of toplogo and navigation bar -->
  <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
  <tr><td valign="top" height="4"><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
  <tr><td valign="top"><a href="javascript:forwardLink('/controller/servlet/Controller?CID=home');"><img src="/static/images/ev2logo5.gif" border="0"/></a></td></tr>
  <tr><td height="20" bgcolor="#FFFFFF"><img src="/static/images/s.gif" height="20"/></td></tr>
  <tr><td valign="top" height="1" width="690" bgcolor="#FFFFFF"><img src="/static/images/s.gif" height="1" width="690"/></td></tr>
  </table>
  <!-- end of toplogo and navigation bar -->

  <!-- Start of the lower area below the navigation bar -->
  <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
  <tr><td valign="top" height="15" colspan="3"><img src="/static/images/s.gif" border="0"/></td></tr>
  <tr><td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td><td valign="top" colspan="2"><a class="EvHeaderText">System error</a></td></tr>
  <tr><td valign="top" height="2" colspan="3"><img src="/static/images/s.gif" border="0"/></td></tr>
  <tr><td valign="top" width="20" height="2"><img src="/static/images/s.gif" border="0" width="20" height="2"/></td><td valign="top" height="2" bgcolor="#6699CC"><img src="/static/images/s.gif" border="0" height="2"/></td><td valign="top" width="10" height="2"><img src="/static/images/s.gif" border="0" width="10" height="2"/></td></tr>
  <tr><td valign="top" height="20" colspan="3"><img src="/static/images/s.gif" border="0" height="20"/></td></tr>
  <tr><td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td>
  	<td valign="top">
  		<xsl:choose>
  			<xsl:when test="string(/root/MESSAGE/DISPLAY)">
  				<A CLASS="MedBlackText"><xsl:value-of select="/root/MESSAGE/DISPLAY"/></A>
  			</xsl:when>
  			<xsl:otherwise>
  				<A CLASS="MedBlackText">Sorry, a system error has occurred, and your request cannot be completed.</A>
  			</xsl:otherwise>

  		</xsl:choose>
  		<P>
  		<A CLASS="MedBlackText">You may </A>
  		<A CLASS="LgBlueLink" href="javascript:forwardLink('/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=ncFeedback');">contact us</A><A CLASS="MedBlackText"> to report this problem.</A>
  		</P>
  	</td>
  	<td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
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