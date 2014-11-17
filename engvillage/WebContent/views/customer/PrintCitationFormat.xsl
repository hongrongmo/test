<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="java html xsl"
>

  <xsl:output method="html" indent="no"/>
  <xsl:strip-space elements="html:* xsl:*" />

  <!--- include file which includes header for printer friendly format -->
  <xsl:include href="PrintHeader.xsl"/>

  <xsl:include href="common/CitationResults.xsl" />
  <xsl:include href="currentYear.xsl" />
  <xsl:template match="SECTION-DELIM">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="LINK"></xsl:template>

  <xsl:variable name="HREF-PREFIX"/>

  <xsl:template match="PAGE-ENTRY">
    <tr>
      <td valign="top" colspan="5" height="8"><img src="/static/images/s.gif" height="8"/></td>
    </tr>
    <tr>
      <td valign="top"><A CLASS="MedBlackText"><xsl:value-of select="DOCUMENTBASKETHITINDEX"/>.</A></td>
      <td valign="top" width="4"><IMG src="/static/images/spacer.gif" width="4" /></td>
      <td valign="top" width="100%" align="left">
        <xsl:apply-templates select="EI-DOCUMENT"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="FOOTER">
    <xsl:text disable-output-escaping="yes">
      <![CDATA[</table></center><br/><center><TABLE cellSpacing="0" cellPadding="0" border="0" width="99%"><TR><TD><CENTER><A class="SmBlackText">Copyright &#169; ]]>
    </xsl:text>
    	<!-- INCLUDE YEAR  -->
		<xsl:call-template name="YEAR"/>
    <xsl:text disable-output-escaping="yes">
      <![CDATA[ Elsevier Inc. All rights reserved. </A></CENTER></TD></TR></TABLE></center></body></html>]]>
    </xsl:text>
  </xsl:template>

</xsl:stylesheet>