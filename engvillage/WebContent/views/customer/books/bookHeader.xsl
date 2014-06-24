<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  exclude-result-prefixes="java html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template name="BOOK-HEADER">

  <xsl:variable name="CUSTOMIZED-LOGO">
    <xsl:value-of select="//CUSTOMIZED-LOGO"/>
  </xsl:variable>

  <!-- Start of logo table -->
  <table border="0" width="99%" cellspacing="0" cellpadding="0">
    <tr>
      <td valign="top">
        <table border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top">
              <a title="Home" target="_top" href="/controller/servlet/Controller?CID=home">
              <xsl:choose>
                <xsl:when test="not($CUSTOMIZED-LOGO='')">
                  <img src="/engresources/custimages/{$CUSTOMIZED-LOGO}.gif" border="0"/>
                </xsl:when>
                <xsl:otherwise>
                  <img alt="Engineering Village" src="/static/images/ev2logo5.gif" border="0"/>
                </xsl:otherwise>
              </xsl:choose>
              </a>
            </td>
          </tr>
        </table>
      </td>
      <td valign="middle" align="right">
        <a title="End Session" target="_top" href="/controller/servlet/Controller?CID=endSession&amp;SYSTEM_LOGOUT=true">
          <img src="/static/images/endsession.gif" border="0" alt="End Session" />
        </a>
      </td>
    </tr>
  </table>

</xsl:template>

</xsl:stylesheet>