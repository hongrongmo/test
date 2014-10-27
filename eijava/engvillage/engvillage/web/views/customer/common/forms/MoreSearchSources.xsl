<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="html xsl">

<xsl:template name="REMOTE-DBS">
    <xsl:param name="SEARCHTYPE"/>
    <xsl:param name="LOCATION">local</xsl:param>

    <!-- Tiny table for remote dbs -->
    <table border="0" cellspacing="0" cellpadding="0">
        <xsl:for-each select="//DATABASES/DATABASE">
            <xsl:choose>
                <xsl:when test="$LOCATION = @NAME" >
                    <!-- Skip this 'menu' option since this is the location of the current search -->
                </xsl:when>
                <xsl:when test="($LOCATION = 'Referex' or $LOCATION='USPTO' or $LOCATION='CRC ENGnetBASE')">
                    <tr><td valign="top" height="4" colspan="3"><img src="/engresources/images/s.gif" border="0" height="4" /></td></tr>
                    <tr>
                        <td valign="top" width="3"><img src="/engresources/images/s.gif" border="0" width="3" /></td>
                        <td valign="top"><a CLASS="MedBlueLink" href="#"><xsl:attribute name="onclick">openRemoteDb('<xsl:value-of select="@VALUE"/>','$SESSIONID','<xsl:value-of select="$SEARCHTYPE"/>')</xsl:attribute><b><xsl:value-of select="@NAME"/></b></a></td>
                        <td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2" /></td>
                    </tr>
                </xsl:when>
                <xsl:when test="((@VALUE = '8') or (@VALUE = '16') or not(number(@VALUE)))" >
                    <tr><td valign="top" height="4" colspan="3"><img src="/engresources/images/s.gif" border="0" height="4" /></td></tr>
                    <tr>
                        <td valign="top" width="3"><img src="/engresources/images/s.gif" border="0" width="3" /></td>
                        <td valign="top"><a CLASS="MedBlueLink" href="#"><xsl:attribute name="onclick">openRemoteDb('<xsl:value-of select="@VALUE"/>','$SESSIONID','<xsl:value-of select="$SEARCHTYPE"/>')</xsl:attribute><b><xsl:value-of select="@NAME"/></b></a></td>
                        <td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2" /></td>
                    </tr>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
        <tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td></tr>
        <tr><td valign="top" height="5" colspan="3"><img src="/engresources/images/s.gif" border="0" height="5" /></td></tr>
    </table>
    <!-- End of Tiny table for remote dbs -->

</xsl:template>
</xsl:stylesheet>