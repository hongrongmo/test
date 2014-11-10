<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="html xsl"
>

    <xsl:output method="html" indent="no"/>
    <xsl:strip-space elements="html:* xsl:*" />

    <xsl:include href="common/AbstractResults.xsl" />
    <xsl:include href="currentYear.xsl" />
    <xsl:template match="SECTION-DELIM">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="LINK"></xsl:template>

    <xsl:template match="PAGE-ENTRY">
        <tr>
            <td valign="top" style="padding: 5px 7px"><xsl:apply-templates select="DOCUMENTBASKETHITINDEX"/></td>
            <td valign="top" width="100%" align="left" style="padding: 5px 0"><xsl:apply-templates select="EI-DOCUMENT"/></td>
        </tr>
    </xsl:template>

    <xsl:template match="FOOTER">
        <xsl:text disable-output-escaping="yes">
            <![CDATA[</table></center><br/><center><TABLE cellSpacing="0" cellPadding="0" border="0" width="99%"><TR><TD><CENTER><span class="SmBlackText">&#169; ]]>
	    </xsl:text>
	    	<!-- INCLUDE YEAR  -->
			<xsl:call-template name="YEAR"/>
	    <xsl:text disable-output-escaping="yes">
	      <![CDATA[  Elsevier Inc. All rights reserved.</span></CENTER></TD></TR></TABLE></center></body></html>]]>
        </xsl:text>
    </xsl:template>

    <xsl:template match="DOCUMENTBASKETHITINDEX">
        <A CLASS="MedBlackText"><xsl:value-of select="."/>.</A>
    </xsl:template>

</xsl:stylesheet>
