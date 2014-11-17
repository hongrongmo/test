<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="html"
>

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
  <xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="DBMASK">
  <xsl:value-of select="/PAGE/DBMASK"/>
</xsl:variable>

<html>

<head>
  <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
  <title>About Engineering Information</title>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

  <center>
    <table border="0" width="100%" cellspacing="0" cellpadding="0">
      <tr><td valign="top">
            <xsl:apply-templates select="HEADER"/>
      </td></tr>
      <tr><td valign="top">
        <!-- Insert the Global Link table -->
        <xsl:apply-templates select="GLOBAL-LINKS">
          <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
          <xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
        </xsl:apply-templates>

      </td></tr>
    </table>

    <table border="0" width="99%" cellspacing="0" cellpadding="0">
      <tr><td valign="top" colspan="10" height="20" bgcolor="#C3C8D1"><img src="/static/images/s.gif" border="0"/></td></tr>
      <tr><td valign="top" colspan="10" height="20"><img src="/static/images/s.gif" border="0"/></td></tr>
    </table>

  </center>


  <!-- table for contents -->
  <center>
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
      <tr><td valign="top"><a class="EvHeaderText">About Elsevier Engineering Information</a></td></tr>
      <tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
      <tr>
        <td valign="top">
        <a class="MedBlackText">Elsevier Engineering Information is the leader in providing online information, knowledge, and support of the highest professional relevance for research and industrial practitioners in applied physical sciences and engineering.  Elsevier Engineering Information offers an array of products and services for universities, corporations and government organizations, including: Engineering Village &#153;, EnCompass&#153;, Compendex&#174;, The Engineering Index&#174;, Chemical Business NewsBase&#174;, Chimica&#153; and PaperChem&#153;.<br/>
        <br/>
        For more information about Elsevier Engineering Information, please visit our Web site at </a><a class="LgBLueLink" HREF="http://www.ei.org" onClick="window.open('http://www.ei.org', 'newpg', 'status=yes,resizable,scrollbars=yes,menubar=yes,location=yes,directories=yes,width=700,height=450'); return false;">http://www.ei.org</a>.
        </td>
      </tr>
    </table>
  </center>


  <br/>

  <!-- end of the lower area below the navigation bar -->
  <xsl:apply-templates select="FOOTER">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
  </xsl:apply-templates>

</body>
</html>

</xsl:template>

</xsl:stylesheet>