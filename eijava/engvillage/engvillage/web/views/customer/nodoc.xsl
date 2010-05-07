<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="html xsl"
  >
<xsl:output method="html" indent="no" />
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template match="NODOC">

<html>
<head>
<title>Welcome to Engineering Village</title>
<script language="Javascript" src="/engresources/js/StylesheetLinks.js" />

 <xsl:text disable-output-escaping="yes">
   <![CDATA[
     <xsl:comment>
      <script language="JavaScript">
        function page_close()
        {
          window.close();
        }
      </script>
    </xsl:comment>
  ]]>
</xsl:text>

</head>

<body bgcolor="#ffffff">
<table cellspacing="0" cellpadding="0" width="100%" border="0"><!-- start logo/title bar -->
  <tbody>
  <tr>
    <td valign="bottom"><img src="/ohubservice/images/enginformation.gif" /></td></tr>
  <tr>
    <td bgcolor="#000000" height="2"><img height="2"
  src="/ohubservice/images/spacer.gif" /></td></tr>
  <tr>
    <td height="50">&#160;<br /></td></tr></tbody></table>
<center>
<table cellspacing="0" cellpadding="0" width="400" align="middle" border="0">
  <tbody>
  <tr>
    <td valign="top" bgcolor="#000000" colspan="5" height="1"><img height="1"
      src="/ohubservice/images/spacer.gif" border="0" /></td></tr>
  <tr>
    <td valign="top" width="1" bgcolor="#000000"><img src="/ohubservice/images/spacer.gif"
      width="1" border="0" /></td>
    <td valign="top" bgcolor="#dedede" colspan="3" height="5"><a
      class="SmBlackText"><img height="5" src="/ohubservice/images/spacer.gif"
    border="0" /></a></td>
    <td valign="top" width="1" bgcolor="#000000"><img src="/ohubservice/images/spacer.gif"
      width="1" border="0" /></td></tr>
  <tr>
    <td valign="top" width="1" bgcolor="#000000"><img src="/ohubservice/images/spacer.gif"
      width="1" border="0" /></td>
    <td valign="top" width="5" bgcolor="#dedede"><img src="/ohubservice/images/spacer.gif"
      width="5" border="0" /></td>
    <td valign="top" bgcolor="#dedede"><a class="MedBlackText">Full-text linking to this article is not yet available.</a></td>
    <td valign="top" width="5" bgcolor="#dedede"><img src="/ohubservice/images/spacer.gif"
      width="5" border="0" /></td>
    <td valign="top" width="1" bgcolor="#000000"><img src="/ohubservice/images/spacer.gif"
      width="1" border="0" /></td></tr>
  <tr>
    <td valign="top" width="1" bgcolor="#000000"><img src="/ohubservice/images/spacer.gif"
      width="1" border="0" /></td>
    <td valign="top" bgcolor="#dedede" colspan="3" height="5"><a
      class="SmBlackText"><img height="5" src="/ohubservice/images/spacer.gif"
    border="0" /></a></td>
    <td valign="top" width="1" bgcolor="#000000"><img src="/ohubservice/images/spacer.gif"
      width="1" border="0" /></td></tr>
  <tr>
    <td valign="top" bgcolor="#000000" colspan="5" height="1"><img height="1"
      src="/ohubservice/images/spacer.gif" border="0" /></td></tr></tbody></table></center>
<center>
<form name="back"><a class="SmBlackText"><input onClick="page_close()" type="button" value="Close" /></a>
</form></center><br/>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
  <tr>
    <td>
      <center><a class="SmBlackText">&#169; 2010 Elsevier Inc. All rights reserved.
</a></center></td></tr></tbody>
</table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>