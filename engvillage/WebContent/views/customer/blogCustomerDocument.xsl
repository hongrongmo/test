<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/static/images/s.gif" border="0" />'>
]>

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

<xsl:include href="../customer/common/AbstractResults.xsl" />

<xsl:variable name="DATABASE-MASK">
  <xsl:value-of select="//DBMASK"/>
</xsl:variable>

<xsl:template match="PAGE">

  <html>
  <head>
    <title>EI DOCUMENT</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>

  </head>
  <!-- start of body tag -->
  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
  <center>
          <table border="0" cellspacing="0" cellpadding="0" width="99%">
            <tr><td valign="top"><img src="/static/images/ev2logo5.gif" border="0"/></td><td valign="middle" align="right"><a href="javascript:window.close();"><img src="/static/images/close.gif" border="0"/></a></td></tr>
            <tr><td colspan="2" height="5"><img src="/static/images/s.gif" height="5"/></td></tr>
            <tr><td colspan="2" height="2" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0"/></td></tr>
            <tr><td colspan="2" height="25"><img src="/static/images/s.gif" height="25"/></td></tr>
          </table>
        </center>


    <center>
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
    <xsl:apply-templates select="PAGE-RESULTS"/>
      </table>
    </center>
  </body>
  </html>

  </xsl:template>

  <xsl:template match="PAGE-RESULTS">

    <FORM name="abstract">
      <xsl:apply-templates select="PAGE-ENTRY"/>
    </FORM>

  </xsl:template>

  <xsl:template match="PAGE-ENTRY">

    <xsl:variable name="DATABASE-ID">
      <xsl:value-of select="EI-DOCUMENT/DBID"/>
    </xsl:variable>

    <xsl:variable name="DOC-ID">
      <xsl:value-of select="EI-DOCUMENT/DOC-ID"/>
    </xsl:variable>

    <tr>
         <td valign="top" colspan="5" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td>
    </tr>

    <tr>
            <td valign="top" width="3"><img src="/static/images/s.gif" border="0" width="3"/></td>
        <td valign="top">
      <A CLASS="MedBlackText"><xsl:value-of select="position()"/>.</A>
        </td>
        <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
        <td valign="top" width="100%" align="left">
      <xsl:apply-templates select="EI-DOCUMENT"/>
        </td>
    </tr>
    <tr>
       <td valign="top" width="6" colspan="2"><img src="/static/images/s.gif" border="0" width="6"/></td>
       <td colspan="3" align="center">
      <A CLASS="SmBlackText">&#169; 2013 Elsevier Inc. All rights reserved.</A>
       </td>
    </tr>

  </xsl:template>

</xsl:stylesheet>
