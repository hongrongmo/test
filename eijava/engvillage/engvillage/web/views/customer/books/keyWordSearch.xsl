<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY copy '<xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text>'>
]>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:che="java:org.ei.data.books.CheLinkBuilder"
  xmlns:ele="java:org.ei.data.books.EleLinkBuilder"
  xmlns:mat="java:org.ei.data.books.MatLinkBuilder"
  xmlns:gui="java:org.ei.books.BookGUI"
  exclude-result-prefixes="java html xsl che ele mat gui">

  <xsl:include href="../GlobalLinks.xsl" />
  <xsl:include href="../Footer.xsl" />
  <xsl:include href="../common/forms/TinyLoginForm.xsl"/>
  <xsl:include href="../common/forms/MoreSearchSources.xsl"/>
  <xsl:include href="bookHeader.xsl" />

  <xsl:variable name="BCD">
    <xsl:value-of select="/DOC/BCD"/>
  </xsl:variable>
  <xsl:variable name="PCO">
    <xsl:value-of select="/DOC/PCO"/>
  </xsl:variable>
  <xsl:variable name="FRM">QUICK_SEARCH</xsl:variable>
  <xsl:variable name="PERSONALIZATION">
    <xsl:value-of select="/DOC/PERSONALIZATION"/>
  </xsl:variable>
  <xsl:variable name="SESSIONID">
    <xsl:value-of select="/DOC/SESSION-ID"/>
  </xsl:variable>
  <xsl:variable name="DATABASE">
    <xsl:value-of select="/DOC/DBMASK"/>
  </xsl:variable>

    <xsl:template match="DOC">
      <html>
        <head>
          <title>Engineering Village - eBook Search </title>
          <SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
          <SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/engresources/js/ReferexSearch.js"/>
          <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Login.js"/>

        </head>
      <body topmargin="0" marginheight="0" marginwidth="0">

        <!-- Start of logo table -->
        <xsl:call-template name="BOOK-HEADER"/>

        <!-- INCLUDE THE GLOBAL LINKS BAR -->
        <xsl:apply-templates select="GLOBAL-LINKS">
          <xsl:with-param name="SELECTED-DB"><xsl:value-of select="DBMASK"/></xsl:with-param>
          <xsl:with-param name="LINK">Book</xsl:with-param>
        </xsl:apply-templates>

        <!-- Start of table for content below the navigation bar -->
        <center>
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top" width="140">
            <!-- left most table for news items -->
            <table border="0" width="140" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top" height="15" bgcolor="#3173B5" colspan="3">
                <a class="LgWhiteText">
                <b>&nbsp; Referex</b>
                </a>
                </td>
              </tr>
              <tr>
                <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td>
                <td valign="top" bgcolor="#FFFFFF">
                  <!-- Tiny table for news features -->
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td valign="top" height="5" colspan="3"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td>
                    </tr>
                    <tr>
                      <td valign="top" width="3"><img src="/engresources/images/spacer.gif" border="0" width="3"/></td>
                      <td valign="top">
                      <a class="SmBlackText">Referex Engineering is a specialized electronic reference product
                      that draws upon hundreds of premium engineering titles to provide engineering students and
                      professionals with the answers and information they require at school, work, and in practice.</a>
                      </td>
                      <td valign="top" width="2"><img src="/engresources/images/spacer.gif" border="0" width="2"/></td>
                    </tr>
                  </table>
                  <!-- End of Tiny table for news features -->
                </td>
                <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td>
              </tr>
              <tr>
                <td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td>
              </tr>
              <tr>
                <td valign="top" colspan="3" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td>
              </tr>

                <xsl:if test="(//PERSONALIZATION='false')">
                  <xsl:variable name="NEXTURL">CID=keyWordSearch</xsl:variable>
                  <xsl:call-template name="SMALL-LOGIN-FORM">
                    <xsl:with-param name="DB" select="$DATABASE"/>
                    <xsl:with-param name="NEXT-URL" select="$NEXTURL"/>
                  </xsl:call-template>
                </xsl:if>

            <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160; More Search Sources</b></a></td></tr>
            <tr><td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" border="0" width="1" /></td>
                <td valign="top" bgcolor="#FFFFFF">
                    <xsl:call-template name="REMOTE-DBS">
                        <xsl:with-param name="SEARCHTYPE">quick</xsl:with-param>
                        <xsl:with-param name="LOCATION">Referex</xsl:with-param>
                    </xsl:call-template>
                </td>
                <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" border="0" width="1"/></td></tr>
            <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td></tr>

            <tr><td valign="top" colspan="3" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
            <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a class="LgWhiteText"><b>&nbsp;Search Tips</b></a></td></tr>
              <tr>
                <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td>
                <td valign="top" bgcolor="#FFFFFF">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td valign="top" height="5" colspan="3">
                      <img src="/engresources/images/spacer.gif" border="0" height="5"/>
                      </td>
                    </tr>
                    <tr>
                      <td valign="top" width="3">
                      <img src="/engresources/images/spacer.gif" border="0" width="3"/>
                      </td>
                      <td valign="top">
                        <a class="SmBlackText">Keyword is the default setting which searches the text of the eBook and returns sections of the eBook. Searching by author, ISBN, publisher, subject, or title, returns records for entire eBooks<br/>
                        <br/>To browse eBooks, simply click on a collection name or one of the subjects listed below them. The search results will contain a complete list of titles by collection or subject.
                        </a>
                      </td>
                      <td valign="top" width="2"><img src="/engresources/images/spacer.gif" border="0" width="2"/></td>
                    </tr>
                  </table>
                </td>
                <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td>
              </tr>
              <tr>
                <td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td>
              </tr>
              <tr>
                <td valign="top" colspan="3" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td>
              </tr>
            </table>
          <!-- End of left most table for news item -->
          </td>
          <td valign="top" width="10" bgcolor="#FFFFFF"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td>
          <td valign="top" align="right" width="100%">
            <!-- Start of table for search form -->
            <table border="0" align="right" width="100%" cellspacing="0" cellpadding="0">
            <!-- End of table for navigation bar -->
            <tr>
              <td valign="top" bgcolor="#C3C8D1">
              <!-- Start of table for search form -->
              <xsl:value-of disable-output-escaping="yes" select="gui:buildForm($FRM)"/>
              <!-- end of search form -->
              </td>
            </tr>
            <tr>
              <td valign="top" bgcolor="#C3C8D1" height="10">
              <img src="/engresources/images/spacer.gif" border="0" height="10"/>
              </td>
            </tr>
            <tr>
              <td valign="top" bgcolor="#C3C8D1">
              <a class="SmBlueTableText">&nbsp; BROWSE BOOKS BY COLLECTION OR SUBJECT</a>
              </td>
            </tr>
            <tr>
              <td valign="top" bgcolor="#C3C8D1" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td>
            </tr>
            <!-- End of table for lookup indexes. -->
            <tr>
              <td valign="top" colspan="4" bgcolor="#C3C8D1">
            <center>
              <table border="0" width="97%" cellspacing="0" cellpadding="0">
                <tr>
                  <td height="1" colspan="3" bgcolor="#148BA9">
                  <img src="/engresources/images/spacer.gif" border="0" height="1"/>
                  </td>
                </tr>
                <tr>
                <td width="1" bgcolor="#148BA9"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td>
                <td valign="top" width="100%">
                 <!-- Inner table to put the materials Science heirarchy -->
                 <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#F5F5F5">
                    <tr height="30">
                      <td valign="top" width="4" bgcolor="#148BA9">
                      <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                      </td>
                      <td valign="middle" bgcolor="#148BA9">
                      <a class="SmWhiteText" href="/controller/servlet/Controller?CID=resultsKeyWord&amp;docIndex=1&amp;catCodes=MAT&amp;queryType=Q">
                      <b> Materials &amp; Mechanical</b><a class="SmWhiteText">  (155)</a>
                      </a>
                      </td>
                      <td valign="top" bgcolor="#148BA9" width="4">
                      <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                      </td>
                      <td valign="middle" bgcolor="#148BA9">
                      <a class="SmWhiteText" href="/controller/servlet/Controller?CID=resultsKeyWord&amp;docIndex=1&amp;catCodes=ELE&amp;queryType=Q">
                      <b>Electronics &amp; Electrical</b><a class="SmWhiteText">  (138)</a>
                      </a>
                      </td>
                      <td valign="top" bgcolor="#148BA9" width="4">
                      <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                      </td>
                      <td valign="middle" bgcolor="#148BA9">
                      <a class="SmWhiteText" href="/controller/servlet/Controller?CID=resultsKeyWord&amp;docIndex=1&amp;catCodes=CHE&amp;queryType=Q">
                      <b>Chemical, Petrochemical &amp; Process</b><a class="SmWhiteText">  (114)</a>
                      </a>
                      </td>
                    </tr>
                    <tr>
                      <td valign="top" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td>
                      <td valign="top">
                      <!-- Individual table for materials -->
                        <table border="0" cellspacing="0" cellpadding="0" width="100%">
                          <xsl:value-of disable-output-escaping="yes" select="mat:convert2Html($BCD,$PCO,$FRM)"/>
                          <tr>
                            <td height="5">
                            <img src="/engresources/images/spacer.gif" height="5"></img>
                            </td>
                          </tr>
                          <tr>
                            <xsl:if test="$BCD = 'DEFAULT_VIEW'">
                              <td valign="top">
                              &nbsp; <a class="SpLink" href="/controller/servlet/Controller?CID=keyWordSearch&amp;catCode=ALL">More...</a>
                              </td>
                            </xsl:if>
                          </tr>
                        </table>
                      </td>
                      <td valign="top" width="10"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td>
                      <td valign="top">
                        <!-- Individual table for Eletrical -->
                        <table border="0" cellspacing="0" cellpadding="0" width="100%">
                          <xsl:value-of disable-output-escaping="yes" select="ele:convert2Html($BCD,$PCO,$FRM)"/>
                          <tr>
                            <td height="5">
                              <img src="/engresources/images/spacer.gif" height="5"/>
                            </td>
                          </tr>
                          <tr>
                            <xsl:if test="$BCD = 'DEFAULT_VIEW'">
                              <td valign="top">
                              &nbsp; <a class="SpLink" href="/controller/servlet/Controller?CID=keyWordSearch&amp;catCode=ALL">More...</a>
                              </td>
                            </xsl:if>
                          </tr>
                        </table>
                      </td>
                      <td valign="top" width="10"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td>
                      <td valign="top">
                      <!-- Individual table for chemical -->
                        <table border="0" cellspacing="0" cellpadding="0" width="100%" >
                          <xsl:value-of disable-output-escaping="yes" select="che:convert2Html($BCD,$PCO,$FRM)"/>
                          <tr>
                            <td height="5"><img src="/engresources/images/spacer.gif" height="5"></img></td>
                          </tr>
                          <tr>
                            <xsl:if test="$BCD = 'DEFAULT_VIEW'">
                              <td valign="top">
                              &nbsp; <a class="SpLink" href="/controller/servlet/Controller?CID=keyWordSearch&amp;catCode=ALL">More...</a>
                              </td>
                            </xsl:if>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr>
                      <td colspan="2" height="10"><img src="/engresources/images/spacer.gif" height="10"/></td>
                    </tr>
                  </table>
                </td>
                <td width="1" bgcolor="#148BA9"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td>
              </tr>
                <tr>
                  <td height="1" colspan="3" bgcolor="#148BA9"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td>
                </tr>
                <tr>
                  <td colspan="3" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td>
                </tr>
              </table>
            </center>
          </td>
            </tr>
            </table>
          <!-- End of table for search form -->
          </td>
          </tr>
        </table>
        <!-- End of table for content below the navigation bar -->

        <!-- Footer -->
        <br/>

        <xsl:apply-templates select="FOOTER">
          <xsl:with-param name="SELECTED-DB"><xsl:value-of select="DBMASK"/></xsl:with-param>
        </xsl:apply-templates>

      </center>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/RemoteDbLink_V5.js"/>

      </body>
      </html>
  </xsl:template>
</xsl:stylesheet>
