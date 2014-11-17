<?xml version="1.0" ?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:gui="java:org.ei.bulletins.BulletinGUI"
  xmlns:java="java:java.net.URLEncoder"
  exclude-result-prefixes="xsl html gui java"
>
<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:variable name="RESOURCE-PATH">
  <xsl:value-of select="/PAGE/RESOURCE-PATH"/>
</xsl:variable>
<xsl:variable name="SESSION-ID">
  <xsl:value-of select="/PAGE/SESSION-ID"/>
</xsl:variable>
<xsl:variable name="ENCODED-QSTR">
  <xsl:value-of select="java:encode(/PAGE/QTOP/QSTR)"/>
</xsl:variable>
<xsl:variable name="QSTR">
  <xsl:value-of select="/PAGE/QTOP/QSTR"/>
</xsl:variable>
<xsl:variable name="QCO">
  <xsl:value-of select="/PAGE/QTOP/QCO"/>
</xsl:variable>
<xsl:variable name="QDIS">
  <xsl:value-of select="/PAGE/QTOP/QDIS"/>
</xsl:variable>
<xsl:variable name="QTIT">
  <xsl:value-of select="/PAGE/QTOP/QTIT"/>
</xsl:variable>
<xsl:variable name="QCAT">
  <xsl:value-of select="/PAGE/QTOP/QCAT"/>
</xsl:variable>
<xsl:variable name="DB">
  <xsl:value-of select="/PAGE/DB"/>
</xsl:variable>
<xsl:variable name="YR">
  <xsl:value-of select="/PAGE/YR"/>
</xsl:variable>
<xsl:variable name="NXT">
  <xsl:value-of select="/PAGE/NAV/@NEXT"/>
</xsl:variable>
<xsl:variable name="PRV">
  <xsl:value-of select="/PAGE/NAV/@PREV"/>
</xsl:variable>
<xsl:variable name="CARTRIDGES">
  <xsl:value-of select="/PAGE/CARTRIDGES"/>
</xsl:variable>
<xsl:variable name="LITCR">
  <xsl:value-of select="/PAGE/LITCR"/>
</xsl:variable>
<xsl:variable name="PATCR">
  <xsl:value-of select="/PAGE/PATCR"/>
</xsl:variable>
<xsl:variable name="SELECTED-DB">
  <xsl:value-of select="/PAGE/SELECTED-DB"/>
</xsl:variable>
<xsl:variable name="LIT-HTML">
  <xsl:value-of select="/PAGE/LIT-HTML"/>
</xsl:variable>
<xsl:variable name="LIT-PDF">
  <xsl:value-of select="/PAGE/LIT-PDF"/>
</xsl:variable>
<xsl:variable name="PAT-HTML">
  <xsl:value-of select="/PAGE/PAT-HTML"/>
</xsl:variable>
<xsl:variable name="PAT-PDF">
  <xsl:value-of select="/PAGE/PAT-PDF"/>
</xsl:variable>
<xsl:variable name="SHOW-HTML">
  <xsl:value-of select="/PAGE/SHOW-HTML"/>
</xsl:variable>
<xsl:variable name="SHOW-PDF">
  <xsl:value-of select="/PAGE/SHOW-PDF"/>
</xsl:variable>


<xsl:include href="../Header.xsl"/>
<xsl:include href="../GlobalLinks.xsl"/>
<xsl:include href="../Footer.xsl" />
<xsl:include href="bulletinTemplates.xsl"/>

<xsl:template match="PAGE">
<html>
  <head>
    <title>Engineering Village - EnCompass Bulletins</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Bulletin.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
  </head>
  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
    <center>

      <!-- INCLUDE THE HEADER -->
      <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$SELECTED-DB"/>
        <xsl:with-param name="SEARCH-TYPE">Bulletins</xsl:with-param>
      </xsl:apply-templates>
      <!-- INCLUDE THE GLOBAL LINKS BAR -->
      <xsl:apply-templates select="GLOBAL-LINKS">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select ="$SELECTED-DB"/>
        <xsl:with-param name="LINK">Bulletins</xsl:with-param>
        <xsl:with-param name="RESOURCE-PATH" select="$RESOURCE-PATH"/>
      </xsl:apply-templates>

      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
          <td valign="top" align="middle" width="100%">
            <table border="0" bgcolor="#C3C8D1" cellspacing="0" cellpadding="0" width="99%">
              <tr>
                <td width="1" bgcolor="#C3C8D1">
                  <img src="/engresources/images/s.gif"/>
                </td>
                <td width="100%">
                  <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
                    <tr>
                      <td valign="top" height="20">
                      <img src="/engresources/images/s.gif" height="20"/>
                      </td>
                    </tr>
                  </table>
                  <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#F5F5F5">
                    <tr>
                      <td valign="top" height="20">
                        <img src="/engresources/images/s.gif" height="20"/>
                      </td>
                    </tr>
                  </table>
                  <table border="0" bgcolor="#F5F5F5" cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                      <td width="20" valign="top">
                        <img src="/engresources/images/s.gif" width="20" />
                      </td>
                      <td width="250" valign="top"  border="0" cellspacing="0" cellpadding="0">
                        <!-- Right side table for search form -->
                        <form name="search" method="get" action="/controller/servlet/Controller">
                          <input type="hidden" name="docIndex" value="1"/>
                          <input type="hidden" name="litcr" value="{$LITCR}"/>
                          <input type="hidden" name="patcr" value="{$PATCR}"/>
                          <input type="hidden" name="database" value="{$SELECTED-DB}"/>
                          <input type="hidden" name="CID" value="bulletinResults"/>
                          <table border="0" cellspacing="0" cellpadding="0" width="100%" bgcolor="#C3C8D1">
                            <tr>
                              <td width="10" bgcolor="#C3C8D1">
                                <img src="/engresources/images/s.gif" width="10"/>
                              </td>
                              <td bgcolor="#C3C8D1" cellspacing="0" cellpadding="0">
                                <table border="0" cellspacing="0" cellpadding="0" width="220">
                                  <tr>
                                    <td>
                                      <img src="/engresources/images/s.gif" width="4"/>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" align="left">
                                      <a class="DBlueText"><b>DISPLAY ARCHIVES</b></a>
                                    </td>
                                  </tr>
                                    <tr>
                                    <td valign="top" height="20">
                                      <img src="/engresources/images/s.gif" height="20"/>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top">
                                      <a class="SmBlackText">In order to view the Bulletins Archives, please select Database, Year of Publication and Category and hit the "Display" button.</a>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" height="15">
                                      <img src="/engresources/images/s.gif" height="15"/>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top">
                                      <a class="SmBlackText"><b>Select Database</b></a>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top">
                                      <xsl:choose>
                                        <xsl:when test="($DB = '1')">
                                          <input type="radio" name="db" id="radLit" onclick="refreshCategories()" checked="true" value="1"></input>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <input type="radio" name="db" id="radLit" onclick="refreshCategories()" value="1"></input>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                      <a class="SmBlackText"><label for="radLit">EnCompassLIT</label></a> &#160;
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top">
                                      <xsl:choose>
                                        <xsl:when test="($DB = '2')">
                                          <input type="radio" name="db" id="radPat" onclick="refreshCategories()" value="2" checked="true"></input>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <input type="radio" name="db" id="radPat" onclick="refreshCategories()" value="2"></input>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                      <a class="SmBlackText"><label for="radPat">EnCompassPAT</label></a> &#160;
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" height="10">
                                      <img src="/engresources/images/s.gif" height="10"/>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" >
                                      <a class="SmBlackText"><b>Select Year of Publication</b></a>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top">
                                      <a class="SmBlackText">
                                        <!-- Start of table for search form -->
                                        <xsl:value-of disable-output-escaping="yes" select="gui:createYearLb($QSTR)"/>
                                        <!-- end of search form -->
                                      </a>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" height="10">
                                      <img src="/engresources/images/s.gif" height="10"/>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top">
                                      <a class="SmBlackText"><b>Select Category</b></a>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top">
                                      <!-- Start of table for search form -->
                                      <xsl:value-of disable-output-escaping="yes" select="gui:createCategoryLb($CARTRIDGES,$QSTR)"/>
                                      <!-- end of search form -->
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" height="15">
                                      <img src="/engresources/images/s.gif" height="15"/>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" align="right">
                                      <input type="image" name="display" value="Display" src="/engresources/images/display.gif" border="0"/>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td>
                                      <img src="/engresources/images/s.gif" width="20"/>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                              <td width="10" bgcolor="#C3C8D1">
                                <img src="/engresources/images/s.gif" width="10"/>
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                      <td valign="top">
                        <!-- Left side table for bulletins display -->
                        <table border="0" cellspacing="0" cellpadding="0" width="100%">
                          <tr>
                            <td colspan="3" height="5" bgcolor="#3173B5">
                              <img src="/engresources/images/s.gif" height="5"/>
                            </td>
                            <td width="20" align="right" >
                              <img src="/engresources/images/s.gif" width="20" height="5"/>
                            </td>
                          </tr>
                          <tr>
                            <td width="20" bgcolor="#3173B5" align="left">
                              <img src="/engresources/images/s.gif" width="20" />
                            </td>
                            <td align="left" bgcolor="#3173B5" height="20">
                              <a CLASS="SmWhiteText"><b><xsl:value-of select="$QTIT" disable-output-escaping="yes"/></b></a>
                            </td>
                            <td align="right" valign="middle" bgcolor="#3173B5">
                              <a title="Most Recent Bulletins" href="/controller/servlet/Controller?CID=bulletinSearch&amp;queryStr={$ENCODED-QSTR}&amp;EISESSION={$SESSION-ID}&amp;database={$SELECTED-DB}"><img src="/engresources/images/rec.gif" border="0"/></a>&#160;
                            </td>
                            <td width="20" align="right" >
                              <img src="/engresources/images/s.gif" width="20" />
                            </td>
                          </tr>
                          <tr>
                            <td colspan="3" height="5" bgcolor="#3173B5">
                              <img src="/engresources/images/s.gif" height="5" />
                            </td>
                            <td width="20" align="right" >
                              <img src="/engresources/images/s.gif" width="20" height="5"/>
                            </td>
                          </tr>
                          <tr>
                            <td width="20">
                              <img src="/engresources/images/s.gif" width="20"/>
                            </td>
                            <td valign="top" align="left" colspan="2">
                              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                <tr>
                                  <td width="6" height="10" colspan="2">
                                    <img src="/engresources/images/s.gif" width="5"/>
                                  </td>
                                </tr>
                                <tr>
                                  <td valign="top" colspan="2">
                                    <a class="SmBlackText">
                                    <b>
                                      <xsl:value-of select="$QDIS" disable-output-escaping="yes"/>
                                    </b>
                                    &#160;&#160;
                                    <b>
                                    <xsl:choose>
                                      <xsl:when test="$QCO = 0"> 0 bulletins found. </xsl:when>
                                      <xsl:otherwise>
                                        <xsl:value-of select="$QCO"/> bulletins found.
                                      </xsl:otherwise>
                                    </xsl:choose>
                                    </b>
                                    </a>
                                  </td>
                                </tr>
                                <tr>
                                  <td height="8" colspan="2">
                                  <img src="/engresources/images/s.gif" height="8"/>
                                  </td>
                                </tr>
                                <tr>
                                  <td>
                                    <!-- table for archive table -->
                                    <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                      <tr>
                                        <td height="1"  bgcolor="#3173B5">
                                          <img src="/engresources/images/s.gif" height="1"/>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td height="5">
                                          <img src="/engresources/images/s.gif" height="5"/>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td valign="top">
                                          <table border="0" cellspacing="0" cellpadding="0">
                                            <tr>
                                              <td  align="left" width="15%" nowrap="true">
                                                <a class="SmBlackText"><b>Bulletin</b></a>
                                              </td>
                                              <td  width="50">
                                                <img src="/engresources/images/s.gif" width="50" height="20"/>
                                              </td>
                                              <td nowrap="true" width="25%">
                                                <a class="SmBlackText"><b>Published Date</b></a>
                                              </td>
                                              <td width="50">
                                                <img src="/engresources/images/s.gif" width="50" height="20"/>
                                              </td>
                                              <td width="15%" nowrap="true">
                                                <a class="SmBlackText"><b>PDF</b></a>
                                              </td>
                                              <td width="50">
                                                <img src="/engresources/images/s.gif" width="50" height="20"/>
                                              </td>
                                              <xsl:choose>
                                                <xsl:when test="($DB = '1')">
                                                  <td colspan="2" width="15%">
                                                    <img src="/engresources/images/s.gif" height="20"/>
                                                  </td>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <td width="15%" nowrap="true" colspan="2">
                                                    <a class="SmBlackText"><b>GIF</b></a>
                                                  </td>
                                                </xsl:otherwise>
                                              </xsl:choose>
                                              <td width="20">
                                                <img src="/engresources/images/s.gif" width="20"/>
                                              </td>
                                            </tr>
                                            <xsl:apply-templates select="BULLETINS" mode="RESULTS"/>
                                            <tr>
                                              <td colspan="8" height="10">
                                                <img src="/engresources/images/s.gif" height="10"/>
                                              </td>
                                            </tr>
                                          </table>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td  height="1">
                                          <img src="/engresources/images/s.gif" height="1"/>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td height="15">
                                          <img src="/engresources/images/s.gif" height="15"/>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td align="middle">
                                          <xsl:if test="($QCO &gt; 0)">
                                            <xsl:if test="($PRV &gt; 0)">
                                              <a href="/controller/servlet/Controller?CID=bulletinResults&amp;docIndex={$PRV}&amp;queryStr={$ENCODED-QSTR}&amp;EISESSION={$SESSION-ID}&amp;database={$SELECTED-DB}">
                                                <img src="/engresources/images/pp.gif" border="0"/>
                                              </a>
                                            </xsl:if> &#160; &#160; &#160;
                                            <xsl:if test="($NXT &gt; 0)">
                                              <a href="/controller/servlet/Controller?CID=bulletinResults&amp;docIndex={$NXT}&amp;queryStr={$ENCODED-QSTR}&amp;EISESSION={$SESSION-ID}&amp;database={$SELECTED-DB}">
                                                <img src="/engresources/images/np.gif" border="0"/>
                                              </a>
                                            </xsl:if>
                                          </xsl:if>
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                            </td>
                            <td width="5">
                              <img src="/engresources/images/s.gif" width="5"/>
                            </td>
                          </tr>
                        </table>
                      </td>
                      <td>
                        <img src="/engresources/images/s.gif"/>
                      </td>
                    </tr>
                    <tr>
                      <td height="20">
                        <img src="/engresources/images/s.gif" height="20"/>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="1" bgcolor="#C3C8D1">
                  <img src="/engresources/images/s.gif" width="1"/>
                </td>
              </tr>
              <tr>
                <td colspan="3" height="1" bgcolor="#C3C8D1">
                  <img src="/engresources/images/s.gif" height="1"/>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </center>

    <!-- Insert the Footer table -->
    <xsl:apply-templates select="FOOTER">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$SELECTED-DB"/>
      <xsl:with-param name="RESOURCE-PATH" select="$RESOURCE-PATH"/>
    </xsl:apply-templates>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
