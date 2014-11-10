<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
    exclude-result-prefixes="schar java html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<!--- all XSL include files -->
<xsl:include href="template.SEARCH_RESULTS.xsl" />
<xsl:include href="common/DetailedResults.xsl" />

    <xsl:template match="PAGE">

    <html>
    <head>
    <title>Engineering Village - View Duplicated Records</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/encompassFields.js"/>
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

        <center>

            <table border="0" width="99%" cellspacing="0" cellpadding="0">
                <tr>
                  <td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td>
                  <td valign="middle" align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td>
                </tr>
                <tr>
                    <td valign="top" colspan="2" height="5"><img height="5" src="/engresources/images/s.gif" border="0"/></td>
                </tr>
                <tr>
                    <td valign="top" bgcolor="#3173b5" colspan="2" height="2"><img height="2" src="/engresources/images/s.gif" border="0"/></td>
                </tr>
                <tr>
                    <td valign="top" height="20" colspan="5"><img src="/engresources/images/s.gif" border="0" height="20"/></td>
                </tr>
                <tr>
                    <td valign="top" colspan="2"><a class="EvHeaderText">View Duplicated Records</a></td>
                </tr>
                <tr>
                    <td valign="top" height="5" colspan="2"><img src="/engresources/images/s.gif" border="0" height="5"/></td>
                </tr>
                <tr>
                    <td valign="top">
                        <xsl:apply-templates select="PAGE-RESULTS"/>
                    </td>
                </tr>

            </table>
        </center>
        </body>
        </html>

    </xsl:template>

    <xsl:template match="PAGE-RESULTS">
        <FORM name="detailed">
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
              <xsl:apply-templates select="PAGE-ENTRY"/>
            </table>
        </FORM>
    </xsl:template>

    <xsl:template match="PAGE-ENTRY">

        <!-- All of these variables are for the selected set 'remove' link -->
        <xsl:variable name="DOC-ID">
            <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
        </xsl:variable>
        <xsl:variable name="INDEX">
            <xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/>
        </xsl:variable>

        <tr>
            <td valign="top" colspan="2" ><img src="/engresources/images/s.gif" border="0" height="15"/></td>
        </tr>
        <tr>
            <td valign="top" ><img src="/engresources/images/s.gif" border="0" width="3"/></td>
            <td valign="top">
                <A CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</A>
            </td>
            <td valign="top" ><img src="/engresources/images/s.gif" border="0" width="3"/></td>
            <td valign="bottom" align="left">
                <xsl:apply-templates select="EI-DOCUMENT"/>
            </td>
        </tr>

    </xsl:template>

</xsl:stylesheet>
