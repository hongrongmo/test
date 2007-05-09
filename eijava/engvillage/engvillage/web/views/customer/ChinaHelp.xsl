<?xml version="1.0" encoding="GB2312"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="html xsl"
>

<xsl:output method="html" indent="no" encoding="GB2312"/>

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
    <xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="DATABASE">
    <xsl:value-of select="/PAGE/DBMASK"/>
</xsl:variable>

<html>

<head>
    <title>Help</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

<center>

  <xsl:apply-templates select="HEADER"/>

    <!-- Insert the Global Link table -->
    <xsl:apply-templates select="GLOBAL-LINKS">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        <xsl:with-param name="LINK">help</xsl:with-param>
    </xsl:apply-templates>

    <!-- Empty Gray Navigation Bar / DO NOT CHANGE -->
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <tr><td valign="middle" height="24"  bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" border="0"/></td></tr>
    </table>


<!-- Start of the lower area below the navigation bar -->
<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
  <tr><td valign="top" height="15" colspan="3"><img src="/engresources/images/s.gif" border="0"/></td></tr>
  <tr><td valign="top" width="20"><img src="/engresources/images/s.gif" border="0" width="20"/></td>
    <td valign="top" colspan="2">
      <table width="400" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="143"><a class="EvHeaderText">Help</a></td>
       <!--   <td width="134" align="right"><img src="/engresources/images/chs_icon.gif" width="70" height="19" border="0"/></td> -->
          <td width="257" align="left"><A href="/controller/servlet/Controller?CID=help&amp;database={$DATABASE}" onFocus="javascript:this.blur();"><img src="/engresources/images/english_version.gif" border="0"/></A></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr><td valign="top" height="2" colspan="3"><img src="/engresources/images/s.gif" border="0"/></td></tr>
  <tr><td valign="top" width="20"><img src="/engresources/images/s.gif" border="0" width="20"/></td>
    <td valign="top"> <A CLASS="MedBlackText"> 这里提供两种帮助方式：在线HTML和可打印的PDF（需要安装</A><A CLASS="LgBlueLink" href="#" onClick="javascript:window.open('http://www.adobe.com/');">Adobe Acrobat Reader</A><A CLASS="MedBlackText">）.</A>
      <P/> <A CLASS="MedBlackText"><b>快速检索帮助</b></A><br/>
        <A CLASS="LgBlueLink" HREF="#" onClick="javascript:var newpg = window.open('/engresources/chinahelp/qs_help_overview.html', 'newpg', 'status=yes,resizable,scrollbars=1,width=770,height=450');newpg.focus();">HTML</A><A CLASS="MedBlackText"> | </A><A CLASS="LgBlueLink" HREF="#" onClick="javascript:var newpg = window.open('/engresources/chinahelp/quickSearchCHS.pdf', 'newpg', 'status=yes,resizable,scrollbars=1,width=770,height=450');newpg.focus();">PDF</A>
      <P/> <A CLASS="MedBlackText"><b>高级检索帮助</b></A><br/>
        <A CLASS="LgBlueLink" HREF="#" onClick="javascript:var newpg = window.open('/engresources/chinahelp/es_help_overview.html', 'newpg', 'status=yes,resizable,scrollbars=1,width=770,height=450');newpg.focus();">HTML</A><A CLASS="MedBlackText"> | </A><A CLASS="LgBlueLink" HREF="#" onClick="javascript:var newpg = window.open('/engresources/chinahelp/expertSearchCHS.pdf', 'newpg', 'status=yes,resizable,scrollbars=1,width=770,height=450');newpg.focus();">PDF</A>
      <P/> <A CLASS="MedBlackText">你可以在快速检索、高级检索页点击 </A><img src="/engresources/images/blue_help1.gif" border="0"/><A CLASS="MedBlackText">按钮访问相应的帮助内容。</A>
    </td><td valign="top" width="10"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
  </tr>
</table>
<!-- end of the lower area below the navigation bar -->

  <xsl:apply-templates select="FOOTER">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:apply-templates>
</center>

</body>
</html>
</xsl:template>

</xsl:stylesheet>