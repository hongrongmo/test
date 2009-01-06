<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="xsl html"
>
<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">
<html>
<head>
	<title>Engineering Village - Session Ended</title>
	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
  <SCRIPT LANGUAGE="JavaScript">
    <xsl:comment>
    <![CDATA[
    // This method is here for when the error page
    // is loaded into a child window.
    // we do not want the liks to be loaded in the child
    // but in the parent
    function forwardLink(url)
    {

      if (window.opener != null)
      {
        window.opener.location = url;
        self.close();
      }
      else
      {
        parent.document.location = url;
      }

    }
    function clearCookies() {
      document.cookie = 'RXSESSION=0; expires=Thu, 01-Jan-70 00:00:01 GMT;';
      document.cookie = 'EISESSION=0; expires=Thu, 01-Jan-70 00:00:01 GMT;';
    }
    ]]>
    // </xsl:comment>
  </SCRIPT>
</head>

	<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" onload="javascript:clearCookies();">
	<!-- Start of logo table -->
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
		<tr><td valign="top"><a href="javascript:forwardLink('/controller/servlet/Controller?CID=home')"><img src="/engresources/images/ev2logo5.gif" border="0"/></a></td></tr>
		<tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td></tr>
		<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
	</table>
	<!-- End of logo table -->


	<!-- Start of the lower area below the navigation bar -->
	<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
		<tr><td valign="top" height="15" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td></tr>
		<tr>
			<td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td>
			<td valign="top" colspan="2"><a class="EvHeaderText">Session Ended</a></td>
		</tr>
		<tr>
			<td valign="top" height="2" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td>
		</tr>
		<tr>
			<td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td>
			<td valign="top">


				<A CLASS="MedBlackText">You have ended your Engineering Village search session.  To return to Engineering Village, please click on link below</A><br/><br/>
				<a CLASS="LgBlueLink" href="javascript:forwardLink('/controller/servlet/Controller?CID=home')">Begin a new session</a>

				<xsl:if test="(/PAGE/DAYPASS ='true')">
				<br/>
				<br/>
				<a class="EvHeaderText">
				Day Pass Customers
				</a>
				<br/>
				<br/>
				<A CLASS="MedBlackText">

				To begin a new session please return to your </A><a CLASS="LgBlueLink" href="javascript:forwardLink('https://store.engineeringvillage.com/ppd/myaccount.do')">store account</a>
				<A CLASS="MedBlackText"> and click on an active Day Pass link.</A>
				</xsl:if>

			</td>
			<td valign="top" width="10"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td>
		</tr>
	</table>
	<!-- end of the lower area below the navigation bar -->

	<xsl:apply-templates select="FOOTER"/>

	</body>
	</html>
</xsl:template>
</xsl:stylesheet>
