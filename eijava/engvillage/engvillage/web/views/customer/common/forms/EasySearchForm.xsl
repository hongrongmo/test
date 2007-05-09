<?xml version="1.0"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:DP="java:org.ei.domain.Displayer"
  xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
  exclude-result-prefixes="xsl html DP DD"
>

<xsl:template match="EASY-SEARCH">
  <xsl:param name="SESSION-ID"/>
  <xsl:param name="SELECTED-DB"/>

  <xsl:variable name="DATABASE-DISPLAYNAME">
    <xsl:value-of select="DD:getDisplayName($SELECTED-DB)"/>
  </xsl:variable>

  <xsl:variable name="EASYDB">
    <xsl:value-of select="//EASYDB"/>
  </xsl:variable>


  <xsl:variable name="SEARCH-WORD-1"><xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/></xsl:variable>
  <xsl:variable name="USERMASK"><xsl:value-of select="//USERMASK"/></xsl:variable>


  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/EasySearchForm.js"/>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>

  <FORM name="easysearch" action="/controller/servlet/Controller?CID=expertSearchCitationFormat" METHOD="POST"  onSubmit="return searchValidation();">

    <input type="hidden" name="searchtype" value="Easy"/>
    <center>
      <table border="0" width="80%" cellspacing="0" cellpadding="0" align="middle" bgcolor="#C3C8D1">
        <tr><td height="25" bgcolor="#F5F5F5" colspan="4"><img src="/engresources/images/s.gif" height="25"/></td></tr>
        <tr><td valign="top" height="10" colspan="4" bgcolor="#F5F5F5"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
        <tr><td colspan="4" height="15" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" height="15"/></td></tr>
        <tr>
          <td valign="top" align="middle">
          <table border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td valign="top" align="middle">
              <A CLASS="MedBlackText">
                <input type="text" name="searchWord1" size="60">
                    <xsl:attribute name="value">
                      <xsl:value-of select="$SEARCH-WORD-1"/>
                    </xsl:attribute>
              </input>
              </A>
              </td>
              <td width="10"><img src="/engresources/images/s.gif" width="10"/></td>
              <td valign="middle"><a CLASS="MedBlackText" onClick="return searchValidation()"><input type="image" src="/engresources/images/search_orange1.gif" name="search" value="Search" border="0"/></a></td>
              <td width="15"><img src="/engresources/images/s.gif" width="15"/></td>
              <td valign="middle"><a CLASS="SmBlueText"><b>?</b></a>
                 <a CLASS="DecLink" href="javascript:makeUrl('Content_Resources_Introduction.htm')">Help</a></td>
            </tr>
            <tr><td valign="top" height="8" colspan="5"><img src="/engresources/images/spacer.gif" border="0" height="8"/></td></tr>
            <xsl:if test="($EASYDB = 'on')">
                <tr><td align="middle">
                    <!-- display database checkboxes -->
                    <xsl:value-of disable-output-escaping="yes" select="DP:toHTML($USERMASK,$SELECTED-DB,'easy')"/>
                    <!-- end of database checkboxes -->
                </td></tr>
            </xsl:if>
          </table>
          </td>
        </tr>
        <tr><td valign="top" height="15" colspan="4"><img src="/engresources/images/s.gif" border="0" height="15"/></td></tr>
        <tr><td colspan="4" height="30" bgcolor="#F5F5F5"><img src="/engresources/images/s.gif" height="30" /></td></tr>
        <tr><td colspan="4" height="2" bgcolor="#3173b5"><img src="/engresources/images/s.gif" height="2" /></td></tr>
        <tr><td colspan="4" height="20" bgcolor="#F5F5F5"><img src="/engresources/images/s.gif" height="20" /></td></tr>
      </table>
    </center>
  </FORM>
</xsl:template>

</xsl:stylesheet>