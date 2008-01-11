<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:bit="java:org.ei.util.BitwiseOperators"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    xmlns:DP="java:org.ei.domain.Displayer"
    xmlns:SF="java:org.ei.domain.SearchForm"
    exclude-result-prefixes="java xsl html DD bit DP SF"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />
<xsl:include href="TinyLoginForm.xsl"/>
<xsl:include href="MoreSearchSources.xsl"/>

<xsl:template match="EXPERT-SEARCH">
    <xsl:param name="SESSION-ID"/>
    <xsl:param name="SELECTED-DB"/>
    <xsl:param name="YEAR-STRING"/>
    <xsl:param name="NEWS">true</xsl:param>
    <xsl:param name="TIPS">true</xsl:param>

    <xsl:variable name="DATABASE">
    <xsl:choose>
        <xsl:when test="boolean(string-length(normalize-space($SELECTED-DB))>0)">
            <xsl:value-of select="$SELECTED-DB"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="/PAGE/DBMASK"/>
        </xsl:otherwise>
    </xsl:choose>
    </xsl:variable>


    <xsl:variable name="DATABASE-DISPLAYNAME">
        <xsl:value-of select="DD:getDisplayName($SELECTED-DB)"/>
    </xsl:variable>

    <xsl:variable name="USERMASK">
        <xsl:value-of select="//USERMASK"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-WORD-1"><xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-PHRASE-1"/></xsl:variable>
    <xsl:variable name="SORT-OPTION"><xsl:value-of select="//SESSION-DATA/SORT-OPTION"/></xsl:variable>
    <xsl:variable name="LASTFOURUPDATES"><xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/></xsl:variable>
    <xsl:variable name="START-YEAR"><xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:variable>
    <xsl:variable name="END-YEAR"><xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:variable>

    <!-- added Autostemming to XS -->
    <xsl:variable name="AUTOSTEMMING"><xsl:value-of select="//SESSION-DATA/AUTOSTEMMING"/></xsl:variable>

  <!-- START OF JAVA SCRIPT -->
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/ExpertSearchForm_V11.js"/>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Login.js"/>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>

  <!-- END OF JAVA SCRIPT -->

    <!--
    <xsl:variable name="LOOKUP">/engvillage/models/customer/LookUpParameters.jsp?database=<xsl:value-of select="$SELECTED-DB"/>&amp;searchtype=Expert</xsl:variable>
    -->
    <center>
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
      <tr>
        <td valign="top" width="140">
          <xsl:if test="$NEWS='true'">
            <!-- left most table for news items -->
            <table border="0" width="140" cellspacing="0" cellpadding="0">
            <tr>
                <td valign="top" height="15" bgcolor="#3173B5" colspan="3">

                  <table border="0" cellspacing="0" cellpadding="4">
                  <tr><td>
                      <a CLASS="LgWhiteText"><B><xsl:value-of select="//NEWS-TITLE"/></B></a>
                  </td></tr>
                  </table>

                </td>
            </tr>
            <tr>
              <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" width="1"/></td>
              <td valign="top" bgcolor="#FFFFFF">
                <!-- Tiny table for news features -->
                <table border="0" id = "newsTable" width="100%" cellspacing="0" cellpadding="0">
                    <!--
                    <tr>
                      <td valign="top" height="5" colspan="3"><img src="/engresources/images/s.gif" height="5"/></td>
                    </tr>
                    -->
                    <!--
                    <tr>
                      <td valign="top" width="3"><img src="/engresources/images/s.gif" width="3"/></td>
                      <td valign="top"><a CLASS="SmBlackText">
                     -->
                      <xsl:value-of select="//NEWS-TEXT" disable-output-escaping="yes"/>
                      <!--
                      </a></td>
                      <td valign="top" width="2"><img src="/engresources/images/s.gif" width="2"/></td>


                    </tr>
                    -->
                    <!--
                    <tr>
                        <td valign="top" colspan="3" height="6"><img src="/engresources/images/s.gif" height="6"/></td>
                    </tr>
                    -->
                    <!-- 12/07  specs removing this link -->
                    <!--
                    <tr>
                        <td valign="top" width="3"><img src="/engresources/images/s.gif" width="3"/></td>
                        <td valign="top" align="right">
                          <a class="MedBlueLink" href="javascript:makeUrl('Content_Resources_Introduction.htm')">
                          <b>More</b>
                          </a>
                        </td>
                        <td valign="top" width="2"><img src="/engresources/images/s.gif" width="2"/></td>
                    </tr>
                    -->
                    <!--
                    <tr>
                        <td valign="top" colspan="3" height="5"><img src="/engresources/images/s.gif"/></td>
                    </tr>
                    -->
                </table>
                <!-- End of Tiny table for news features -->
                </td><td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/spacer.gif" border="0" width="1" /></td></tr>
            <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/spacer.gif" border="0" height="1" /></td></tr>
            <tr><td valign="top" colspan="3" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20" /></td></tr>

            <xsl:if test="(//PERSONALIZATION='false')">
                <xsl:variable name="NEXTURL">CID=expertSearch&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>
                <xsl:call-template name="SMALL-LOGIN-FORM">
                    <xsl:with-param name="DB" select="$DATABASE"/>
                    <xsl:with-param name="NEXT-URL" select="$NEXTURL"/>
                </xsl:call-template>
            </xsl:if>

            <!-- start of a second table for remote dbs -->
            <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160; More Search Sources</b></a></td></tr>
            <tr><td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/spacer.gif" border="0" width="1" /></td>
            <td valign="top" bgcolor="#FFFFFF">
                <xsl:call-template name="REMOTE-DBS">
                    <xsl:with-param name="SEARCHTYPE">expert</xsl:with-param>
                </xsl:call-template>
            </td>


            <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" width="1"/></td></tr>
          <tr>
            <td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/s.gif" height="1"/></td>
          </tr>
        </table>
        <!-- End of left most table for news item -->
      </xsl:if>

      </td>

      <td valign="top" width="10" bgcolor="#FFFFFF"><img src="/engresources/images/s.gif" width="10"/></td>
      <td valign="top" align="right" width="100%">
        <!-- START OF TABLE FOR SEARCH FORM -->
          <table border="0" align="right" width="100%" cellspacing="0" cellpadding="0">
            <tr>
              <td valign="top" bgcolor="#C3C8D1" width="100%">
                <!-- Start of table for search form -->
                <table border="0" cellspacing="0" cellpadding="0" width="500" bgcolor="#C3C8D1">
                <!-- Step 2 -->
              <form name="quicksearch" action="/controller/servlet/Controller?CID=expertSearchCitationFormat" METHOD="POST"  onSubmit="return searchValidation()" >
              <tr>
                <td valign="top" height="20" colspan="3"><img src="/engresources/images/s.gif" height="20"/></td></tr>
              <tr>
                <td valign="top" colspan="3">
                    <!-- display database checkboxes -->
                    <xsl:value-of disable-output-escaping="yes" select="DP:toHTML($USERMASK,$SELECTED-DB,'expert')"/>
                    <!-- end of database checkboxes -->
                </td>
              </tr>
              <tr>
                <td valign="top" colspan="3" bgcolor="#C3C8D1" height="8"><img src="/engresources/images/s.gif" height="8"/></td></tr>
              <tr>
                <td valign="top" width="4" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" width="4"/></td>
                <td valign="top" colspan="2" bgcolor="#C3C8D1"><A CLASS="SmBlueTableText"><B><label for="srchWrd1">ENTER SEARCH TERMS BELOW</label></B></A></td></tr>
              <tr>
                <td valign="top" width="4" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" width="4"/></td>
                <td valign="top" width="15" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" width="15"/></td>
                <td valign="top" bgcolor="#C3C8D1">
                  <A CLASS="MedBlackText">
                    <TEXTAREA id="srchWrd1" ROWS="4" COLS="35" WRAP="PHYSICAL" NAME="searchWord1" onBlur="updateWinds()">
                    <xsl:value-of select="$SEARCH-WORD-1"/>
                    </TEXTAREA>
                  </A>
                    <a href="javascript:makeUrl('Expert_Search.htm')">
                    <img src="/engresources/images/blue_help1.gif" border="0"/>
                    </a>
                </td>
              </tr>
              <tr>
                <td valign="top" colspan="3" bgcolor="#C3C8D1" height="8"><img src="/engresources/images/s.gif" height="8"/></td>
              </tr>
              <tr>
                <td valign="top" width="4" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" width="4"/></td>
                <td valign="top" colspan="2">
                  <table border="0" width="100%" cellspacing="0" cellpadding="0">
                    <tr>
                      <td valign="top" colspan="2"><a CLASS="SmBlueTableText">SEARCH FROM</a></td>
                      <td valign="top" colspan="2"><a CLASS="SmBlueTableText">SORT BY</a></td>
                    </tr>
                    <tr>
                      <td valign="top" width="15"><img src="/engresources/images/s.gif" width="15"/></td>
                      <td valign="top" bgcolor="#C3C8D1">
                        <table border="0" cellspacing="0" cellpadding="0">
                          <tr>
                            <td valign="top">
                              <input type="radio" name="yearselect" value="yearrange">
                                <xsl:if test="not(number($LASTFOURUPDATES))">
                                  <xsl:attribute name="checked"/>
                                </xsl:if>
                              </input>&#160;
                              <a CLASS="MedBlackText">
                              <select name="startYear" onchange="selectYearRange(0);">
                                <xsl:value-of disable-output-escaping="yes" select="SF:getYear($SELECTED-DB,$START-YEAR,$YEAR-STRING,'startYear')"/>
                              </select>
                              </a>
                              <a CLASS="SmBlueTableText"> TO </a>
                              <a CLASS="MedBlackText">
                              <select name="endYear" onchange="selectYearRange(0);">
                                <xsl:value-of disable-output-escaping="yes" select="SF:getYear($SELECTED-DB,$END-YEAR,$YEAR-STRING,'endYear')"/>
                              </select>
                              <input type="hidden" name="stringYear">
                              <xsl:attribute name="value"><xsl:value-of select="$YEAR-STRING"/></xsl:attribute>
                              </input>
                              </a>
                            </td>
                          </tr>
                          <tr>
                            <td height="4"><img src="/engresources/images/spacer.gif" height="4"/></td>
                          </tr>
                          <tr>
                            <td valign="top">
                              <input type="radio" name="yearselect" value="lastupdate"  id="rdupdt" onclick="checkLastUpdates();">
                                <xsl:if test="number($LASTFOURUPDATES)">
                                  <xsl:attribute name="checked"/>
                                </xsl:if>&#160;
                                <a class="SmBlackText">
                                  <select name="updatesNo" onchange="selectYearRange(1);">
                                      <xsl:for-each select="//UPDATES[@SHORTNAME='UP']/OPTIONS/OPTION">
                                      <option>
                                        <xsl:if test="(@SHORTNAME=$LASTFOURUPDATES)">
                                          <xsl:attribute name="selected"/>
                                        </xsl:if>
                                        <xsl:attribute name="value"><xsl:value-of select="@SHORTNAME"/></xsl:attribute><xsl:value-of select="@DISPLAYNAME"/>
                                      </option>
                                      </xsl:for-each>
                                  </select>
                                </a>
                              </input>
                              <a class="SmBlackText">&#160;<label for="rdupdt">Updates</label></a>
                               <a href="javascript:makeUrl('Date_Limits.htm')">
                              <img src="/engresources/images/blue_help1.gif" border="0"/>
                              </a>
                            </td>
                          </tr>
                        </table>
                      </td>
                      <td valign="top" width="15"><img src="/engresources/images/s.gif" width="15"/></td>
                      <td valign="top">
                        <!-- jam - "Turkey" added context sensitive help icon to relevance -->
                        <input type="radio" name="sort" id="chkrel" value="relevance">
                          <xsl:if test="($SORT-OPTION='relevance') or not(($SORT-OPTION='publicationYear') or ($SORT-OPTION='yr'))">
                            <xsl:attribute name="checked"/>
                          </xsl:if>
                        </input>
                        <a CLASS="SmBlackText"><label for="chkrel">Relevance</label></a>&#160;
                        <a href="javascript:makeUrl('Sorting_from_the_Search_Form.htm')">
                        <img src="/engresources/images/blue_help1.gif" border="0"/>
                        </a>
                        <input type="radio" name="sort" id="chkyr" value="yr">
                          <xsl:if test="($SORT-OPTION='publicationYear') or ($SORT-OPTION='yr')">
                            <xsl:attribute name="checked"/>
                          </xsl:if>
                        </input>
                        <a CLASS="SmBlackText"><label for="chkyr">Publication year</label></a>
                        <br/>
                        <a CLASS="SmBlackText">
                        <input type="checkbox" id="chkstm" name="autostem" >
                          <xsl:if test="($AUTOSTEMMING='off')">
                            <xsl:attribute name="checked"/>
                          </xsl:if>
                        </input>
                        <label for="chkstm">Autostemming off</label>
                        </a>
                        &#160;&#160;
                        <a href="javascript:makeUrl('Autostemming.htm')">
                        <img src="/engresources/images/blue_help1.gif" border="0"/>
                        </a>
                      </td>
                    </tr>
                    <tr>
                      <td valign="top" colspan="2">&#160;</td>
                      <td valign="top" colspan="2">
                        <A CLASS="MedBlackText" onClick="return searchValidation()"><input name="search" value="Search" type="image" src="/engresources/images/search_orange1.gif" border="0"/></A>&#160; &#160;
                        <a href="/controller/servlet/Controller?CID=expertSearch&amp;database={$SELECTED-DB}">
                          <img src="/engresources/images/reset_orange1.gif" border="0"/>
                        </a>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </form>
          </table>
          <!-- END OF TABLE FOR SEARCH FORM -->
        </td>
        <td valign="top" align="right" width="5" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" width="5"/></td>
        <!-- Start of table for lookup indexes -->
        <td valign="top" align="right" width="120" bgcolor="#C3C8D1">
          <div id="browseindexes">
            <!-- Start of Right most table for lookup -->
            <table border="0" width="130" align="right" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top" height="20" colspan="5"><img src="/engresources/images/s.gif" height="20"/></td>
              </tr>
              <tr>
                <td valign="top" bgcolor="#3173B5" height="15" colspan="5">
                  &#160; <a CLASS="LgWhiteText"><b>Browse Indexes</b></a> &#160;
                  <a href="javascript:makeUrl('Browse_Indexes.htm')"><img src="/engresources/images/help_white.gif" border="0"/></a>
                </td>
              </tr>
              <tr>
                <td width="1" bgcolor="#3173B5"><img src="/engresources/images/s.gif" width="1"/></td>
                <td width="4" bgcolor="#FFFFFF"><img src="/engresources/images/s.gif" width="4"/></td>
                <td width="120" bgcolor="#FFFFFF">
                    <div id="lookups"> &#160; <br/> &#160; </div>
                </td>
                <td width="4" bgcolor="#FFFFFF"><img src="/engresources/images/s.gif" width="4"/></td>
                <td width="1" bgcolor="#3173B5"><img src="/engresources/images/s.gif" width="1"/></td>
              </tr>
              <tr><td valign="top" colspan="5" height="1" bgcolor="#3173B5"><img src="/engresources/images/s.gif" height="1"/></td></tr>
            </table>
            <!-- End of Right most table for lookup -->
          </div>
        </td>
        <td valign="top" width="5" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" width="5"/></td>
      </tr>
       <tr><td valgn="top" colspan="4" height="15" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" height="15"/></td></tr>
       <!-- Start of table for Expert Search Codes -->
       <tr><td valign="top" colspan="4" bgcolor="#C3C8D1">
        <center>
        <table border="0" width="90%" cellspacing="0" cellpadding="0" align="middle">
        <tr><td valign="top" width="1" bgcolor="#000000"><img src="/engresources/images/s.gif" width="1"/></td>
        <td valign="top">
        <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
        <tr><td valign="top" bgcolor="#000000" height="1" colspan="3"><img src="/engresources/images/s.gif" height="1"/></td></tr>
        <tr><td valign="top" height="10" bgcolor="#8294B4" colspan="3"><a CLASS="LgWhiteText"><b>&#160; Search Codes</b></a> &#160;
        <a href="javascript:makeUrl('Expert_Search_Fields_and_Fields__Codes.htm')">
       	   <img src="/engresources/images/blue_help1.gif" border="0"/>
        </a>
        </td></tr>
        <tr><td valign="top" bgcolor="#000000" height="1" colspan="3"><img src="/engresources/images/s.gif" height="1"/></td></tr>
        <tr><td valign="top" width="4"><img src="/engresources/images/s.gif" width="4"/></td>
        <td valign="top" width="100%">
        <!-- Small table to put the Treatment Codes in columns -->
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top" colspan="12"><img src="/engresources/images/s.gif" height="2"/></td>
          </tr>
          <xsl:if test="(//LEGEND)">
              <tr>
                <td valign="top" colspan="12"><span CLASS="SmBlackText"><xsl:value-of select="//LEGEND" disable-output-escaping="yes"/></span></td>
              </tr>
              <tr>
                <td valign="top" colspan="12"><img src="/engresources/images/s.gif" height="2"/></td>
              </tr>
          </xsl:if>


            <xsl:variable name="PERCOL">
              <xsl:value-of select="ceiling(count(//SEARCH-CODES/FIELD) div 3)"/>
            </xsl:variable>
            <tr>
              <xsl:for-each select="//SEARCH-CODES/FIELD">
                <xsl:if test="(position() mod $PERCOL = 1)">
                  <xsl:text disable-output-escaping="yes">
                  <![CDATA[
                  <td valign="top">
                  <table border="0" width="100%" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><a CLASS="MedBlackText"><b><u>Field</u></b></a></td>
                      <td>&#160;&#160;&#160;</td>
                      <td><a CLASS="MedBlackText"><b><u>Code</u></b></a></td>
                      <td>&#160;</td>
                    </tr>
                  ]]>
                  </xsl:text>
                </xsl:if>
               <tr>
                  <td valign="top" height="1">
                    <a class="SmBlackText">
                      <xsl:value-of select="@LABEL"/>
                      <xsl:if test="string(DB)">
                       <a class="SmBlackText">&#160;(</a>
                       <xsl:for-each select="DB">
                       <span class="BoldBlueText"><xsl:value-of select="."/></span>
                       <xsl:if test="not(position()=last())"><A CLASS="SmBlackText"> , </A></xsl:if>
                       </xsl:for-each>
                       <a class="SmBlackText">)</a>
                    </xsl:if>
                  </a>
                  </td>
                  <td valign="top">&#160;&#160;&#160;</td>
                  <td valign="top">
                  <a class="SmBlackText">
                     <xsl:value-of select="@ID"/>
                  </a>
                  </td>
                  <td>&#160;</td>
                </tr>
                <xsl:if test="(position() mod $PERCOL =0) or (position() = last())">
                  <xsl:text disable-output-escaping="yes">
                    <![CDATA[
                    </table>
                    </td>]]>
                  </xsl:text>
                </xsl:if>
              </xsl:for-each>
            </tr>

          <tr>
            <td valign="top" colspan="12"><img src="/engresources/images/s.gif" height="2"/></td>
          </tr>
        </table>
        <!-- End of Small table to put Treatment Codes in columns -->
        </td>
        <td valign="top" width="4"><img src="/engresources/images/s.gif" width="4"/></td></tr>
        <tr><td valign="top" bgcolor="#000000" height="1" colspan="3"><img src="/engresources/images/s.gif" height="1"/></td></tr>
        </table>
        </td>
        <td valign="top" width="1" bgcolor="#000000"><img src="/engresources/images/s.gif" width="1"/></td>
        </tr>
        </table>
       </center>
       </td></tr>
       <tr><td valign="top" colspan="4" height="8" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" height="8"/></td></tr>
       <tr><td valign="top" colspan="4">

      <xsl:if test="$TIPS='true'">
          <!-- Start of table for help tips -->
          <center>
         <table border="0" width="100%" cellspacing="0" cellpadding="0">
             <tr><td valign="top" width="100%">
           <!-- table for Help tips -->
             <table border="0" width="100%" cellspacing="0" cellpadding="0">
              <tr><td valign="top" colspan="3" height="10"><img src="/engresources/images/s.gif" height="10"/></td></tr>
              <tr><td valign="top" width="1" bgcolor="#3173B5"><img src="/engresources/images/s.gif" width="1"/></td>
                  <td valign="top">
                <table border="0" width="100%" cellspacing="0" cellpadding="0">
                   <tr><td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/engresources/images/s.gif" height="1"/></td></tr>
                   <tr><td valign="top" height="10" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160; Search Tips</b></a></td></tr>
                   <tr><td valign="top" height="15" colspan="3"><img src="/engresources/images/s.gif" height="15"/></td></tr>
                   <tr><td valign="top" width="4"><img src="/engresources/images/s.gif" width="4"/></td>
                         <td valign="top">
                         <table border="0" width="100%" cellspacing="0" cellpadding="0">
                         <tr><td colspan="2">
                         <a CLASS="MedBlackText">Search within a specific field using "wn" </a></td></tr>
                         <tr><td width="10"></td><td><a CLASS="MedBlackText">{test bed} wn ALL AND {atm networks} wn TI<br/>
                         (window wn TI AND sapphire wn TI) OR Sakamoto, K* wn AU<br/><br/></a></td></tr>

                         <tr><td colspan="2">
                         <a CLASS="MedBlackText">Use truncation  (*) to search for words that begin with the same letters.</a></td></tr>
                         <tr><td width="10"></td><td><a CLASS="MedBlackText">comput* returns computer, computers, computerize, computerization<br/><br/></a></td></tr>


                         <tr><td colspan="2">
                         <a CLASS="MedBlackText">Truncation can also be used to replace any number of characters internally.</a></td></tr>
                     <tr><td width="10"></td><td><a CLASS="MedBlackText">sul*ate returns sulphate or sulfate<br/><br/></a></td></tr>


                         <tr><td colspan="2">
                         <a CLASS="MedBlackText">Use wildcard (?) to replace a single character.</a></td></tr>
                     <tr><td width="10"></td><td><a CLASS="MedBlackText">wom?n retrieves woman or women<br/><br/></a></td></tr>




                         <tr><td colspan="2">
                         <a CLASS="MedBlackText">Stem search terms using $</a></td></tr>
                         <tr><td width="10"></td><td><a CLASS="MedBlackText">$management returns manage, managed, manager, managers, managing, management<br/><br/></a></td></tr>


                         <tr><td colspan="2">
                         <a CLASS="MedBlackText">To search for an exact phrase or phrases containing stop words (and, or, not, near), enclose terms in braces or quotation marks.</a></td></tr>
                         <tr><td width="10"></td><td><a CLASS="MedBlackText">{Journal of Microwave Power and Electromagnetic Energy} wn ST<br/>
                         "near field scanning" wn CV<br/><br/></a></td></tr>


                         <tr><td colspan="2">
                         <a CLASS="MedBlackText">Use NEAR or ONEAR to search for terms in proximity.  ONEAR specifies the exact order of terms.  NEAR and ONEAR cannot be used with truncation, wildcards, parenthesis, braces or quotation marks.</a></td></tr>
                         <tr><td width="10"></td><td><a CLASS="MedBlackText">Avalanche ONEAR/0 diodes<br/>
                         Solar NEAR energy<br/>
                         Wind NEARr/3 power<br/>
                         $industrial NEAR $management<br/><br/></a></td></tr>


                         <tr><td colspan="2">
                         <a CLASS="MedBlackText">Browse the author look-up index to select all variations of an author's name.</a></td></tr>
                         <tr><td width="10"></td><td><a CLASS="MedBlackText">Smith, A. OR  Smith, A.J. OR Smith, Alan OR Smith, Alan J.<br/><br/></a></td></tr>

                         </table>
                         </td></tr>
                         <tr><td colspan="3" height="8"><img src="/engresources/images/s.gif" height="8"/></td></tr>
                   <tr><td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/engresources/images/s.gif" height="1"/></td></tr>
                 </table></td>
                   <td valign="top" width="1" bgcolor="#3173B5"><img src="/engresources/images/s.gif" width="1"/></td></tr>
              <tr><td valign="top" height="8" colspan="3"><img src="/engresources/images/s.gif" height="8"/></td></tr>
             </table>
            </td></tr>
            </table>
            </center>
          <!-- End of table for Help tips -->
        </xsl:if>
      <!-- End of table for search form -->
        </td>
      </tr>
      </table>
    </td>
<!--    <td valign="top" width="10" bgcolor="#FFFFFF"><img src="/engresources/images/s.gif" width="10"/></td>
-->
    </tr>
  </table>
</center>
<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/RemoteDbLink_V5.js"/>

<!-- End of table for content below the navigation bar -->
  <script TYPE="text/javascript" language="javascript">
  <xsl:comment>
  flipImage(<xsl:value-of select="$SELECTED-DB"/>);
  // </xsl:comment>
  </script>
</xsl:template>

</xsl:stylesheet>
