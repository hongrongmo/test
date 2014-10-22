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
<xsl:include href="./HeaderNull.xsl"/>

    <xsl:template match="PAGE">
    <html>
    <head>
	<link rel="stylesheet" type="text/css" media="all" href="/static/css/ev_common_sciverse.css"/>
	<style type="text/css">
		#pageresultstable {margin-left: 10px; padding:0;}
		form {margin: 0; padding:0)
	</style>
    <title>Engineering Village - View Duplicated Records</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/encompassFields.js"/>
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
<!-- HEADER -->
		<xsl:call-template name="HEADERNULL"/>

		<div style="padding: 10px 10px 0;"><a class="EvHeaderText">View Duplicated Records</a></div>

		<xsl:apply-templates select="PAGE-RESULTS"/>
        </body>
        </html>

    </xsl:template>

    <xsl:template match="PAGE-RESULTS">
        <FORM name="detailed">
            <table border="0" cellspacing="0" cellpadding="0" width="100%" id="pageresultstable">
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
            <td valign="top" colspan="2" ><img src="/static/images/s.gif" border="0" height="15"/></td>
        </tr>
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0" width="3"/></td>
            <td valign="top">
                <A CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</A>
            </td>
            <td valign="top" ><img src="/static/images/s.gif" border="0" width="3"/></td>
            <td valign="top" align="left">
                <xsl:apply-templates select="EI-DOCUMENT"/>
            </td>
        </tr>

    </xsl:template>

</xsl:stylesheet>
