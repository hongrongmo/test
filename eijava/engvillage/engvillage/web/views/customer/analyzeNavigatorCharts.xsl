<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    exclude-result-prefixes="java html xsl DD"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Footer.xsl" />
<xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID" />
    </xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
        <xsl:value-of select="//SEARCH-TYPE"/>
    </xsl:variable>


    <xsl:variable name="DATABASE-DISPLAYNAME">
      <xsl:value-of select="DD:getDisplayName(SELECTED-DATABASE)"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
        <xsl:value-of select="DBMASK"/>
    </xsl:variable>

    <xsl:variable name="DATABASE-ID">
        <xsl:value-of select="//SESSION-DATA/DATABASE/ID"/>
    </xsl:variable>

    <xsl:variable name="ANALYZE-DATA">
    	<xsl:value-of select="/PAGE/ANALYZE-DATA"/>
    </xsl:variable>

    <xsl:variable name="ZOOM-ANALYZE">
        <xsl:value-of select="/PAGE/ZOOM-ANALYZE-DATA"/>
    </xsl:variable>

    <xsl:variable name="ZOOM">
    	<xsl:value-of select="/PAGE/ZOOM"/>
    </xsl:variable>

    <xsl:variable name="COUNT">
    	<xsl:value-of select="CURRENT-COUNT"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-ID">
        <xsl:value-of select="/PAGE/SEARCH-ID"/>
    </xsl:variable>

   <html>
    <head>
        <META http-equiv="Expires" content="0"/>
        <META http-equiv="Pragma" content="no-cache"/>
        <META http-equiv="Cache-Control" content="no-cache"/>
        <title>Engineering Village</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
    <!-- End of javascript -->

    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
      <center>
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td>
            <td valign="middle" align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td>
          </tr>
          <tr>
            <td valign="top" height="2" bgcolor="#3173B5" colspan="3"><img src="/engresources/images/s.gif" border="0"/></td>
          </tr>
        </table>
      </center>
      <p style="font-family:sans-serif; font-size:14px; text-align:left;">Search Query:&#160;<span style="font-weight:bold;"><xsl:value-of select="SESSION-DATA/I-QUERY"/></span></p>
      <center>
				<table border="0" width="99%" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<xsl:if test="string(ANALYZE-DATA)">
								<img alt="Right-click on chart to save a copy of the image." border="0" width="550" height="500" src="/controller/servlet/BarChart?analyzedata={java:encode($ANALYZE-DATA)}&amp;count={$COUNT}"/>
							</xsl:if>
						</td>
					</tr>
				</table>

      </center>

      <!-- Insert the Footer table -->
      <xsl:apply-templates select="FOOTER">
      </xsl:apply-templates>

    </body>
    </html>
</xsl:template>
</xsl:stylesheet>

