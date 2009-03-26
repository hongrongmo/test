<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="html xsl"
>

    <xsl:output method="html" indent="no"/>
    <xsl:strip-space elements="html:* xsl:*" />

    <!--- include file which includes header for printer friendly format -->
    <xsl:include href="PrintHeader.xsl"/>

    <xsl:include href="common/DetailedResults.xsl" />

    <xsl:template match="SECTION-DELIM">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="LINK"></xsl:template>

    <xsl:template match="PAGE-ENTRY">
        <tr>
            <td valign="top" colspan="5" height="8"><img src="/engresources/images/s.gif" height="8"/></td>
        </tr>
        <tr>
            <td valign="top"><xsl:apply-templates select="DOCUMENTBASKETHITINDEX"/></td>
            <td valign="top" width="4"><IMG src="/engresources/images/s.gif" width="4" /></td>
            <td valign="top" width="100%" align="left"><xsl:apply-templates select="EI-DOCUMENT"/></td>
        </tr>
    </xsl:template>

    <xsl:template match="FOOTER">
        <xsl:text disable-output-escaping="yes">
            <![CDATA[</table></center><br/><center><TABLE cellSpacing="0" cellPadding="0" border="0" width="99%"><TR><TD><CENTER><A class="SmBlackText">&#169; 2009 Elsevier Inc. All rights reserved.</A></CENTER></TD></TR></TABLE></center></body></html>]]>
        </xsl:text>
    </xsl:template>

    <xsl:template match="DOCUMENTBASKETHITINDEX">
        <A CLASS="MedBlackText"><xsl:value-of select="."/>.</A>
    </xsl:template>

</xsl:stylesheet>

