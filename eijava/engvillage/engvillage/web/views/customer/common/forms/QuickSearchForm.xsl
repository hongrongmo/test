<?xml version="1.0"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:SF="java:org.ei.domain.SearchForm"
  xmlns:bit="java:org.ei.util.BitwiseOperators"
  xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
  xmlns:DP="java:org.ei.domain.Displayer"

  exclude-result-prefixes="java xsl html DD bit DP SF"
>
  <xsl:include href="TinyLoginForm.xsl"/>
  <xsl:include href="MoreSearchSources.xsl"/>

  <xsl:template match="COMBINED-QUICK-SEARCH">

     <!-- This file renders Combined Quick Search Form in combination with SearchParameters.jsp  -->

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

    <xsl:variable name="USERMASK">
        <xsl:value-of select="//USERMASK"/>
    </xsl:variable>

    <xsl:variable name="DATABASE-DISPLAYNAME">
      <xsl:value-of select="DD:getDisplayName($SELECTED-DB)"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-WORD-1">
      <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-PHRASE-1"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-WORD-2">
      <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-PHRASE-2"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-WORD-3">
      <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-PHRASE-3"/>
    </xsl:variable>

    <xsl:variable name="BOOLEAN-1">
      <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/BOOLEAN-1"/>
    </xsl:variable>

    <xsl:variable name="BOOLEAN-2">
      <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/BOOLEAN-2"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-OPTION-1">
      <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-OPTION-1"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-OPTION-2">
      <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-OPTION-2"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-OPTION-3">
      <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-OPTION-3"/>
    </xsl:variable>

    <xsl:variable name="DOCUMENT-TYPE">
      <xsl:value-of select="//SESSION-DATA/DOCUMENT-TYPE"/>
    </xsl:variable>

    <xsl:variable name="DISCIPLINE-TYPE">
      <xsl:value-of select="//SESSION-DATA/DISCIPLINE-TYPE"/>
    </xsl:variable>

    <xsl:variable name="TREATMENT-TYPE">
      <xsl:value-of select="//SESSION-DATA/TREATMENT-TYPE"/>
    </xsl:variable>

    <xsl:variable name="LANGUAGE">
      <xsl:value-of select="//SESSION-DATA/LANGUAGE"/>
    </xsl:variable>

    <xsl:variable name="SORT-OPTION">
      <xsl:choose>
        <xsl:when test="//SESSION-DATA/SORT-OPTION">
          <xsl:value-of select="//SESSION-DATA/SORT-OPTION"/>
        </xsl:when>
        <xsl:otherwise><xsl:value-of select="//SORT-DEFAULT"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="AUTOSTEMMING">
      <xsl:value-of select="//SESSION-DATA/AUTOSTEMMING"/>
    </xsl:variable>

    <xsl:variable name="LASTFOURUPDATES">
      <xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/>
    </xsl:variable>

    <xsl:variable name="START-YEAR"><xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:variable>
    <xsl:variable name="END-YEAR"><xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:variable>

    <xsl:variable name="LOOKUP">/engvillage/models/customer/LookUpParameters.jsp?database=<xsl:value-of select="$SELECTED-DB"/>&amp;searchtype=Quick</xsl:variable>

    <center>
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <tr>
          <td valign="top" width="140">
          <xsl:if test="$NEWS='true'">
            <!-- left most table for news items -->
              <table border="0" width="140" cellspacing="0" cellpadding="0">
                <tr>
                  <td valign="top" height="15" bgcolor="#3173B5" colspan="3">

                 <!-- <table border="0" cellspacing="0" cellpadding="4"> -->
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

                <xsl:value-of select="//NEWS-TEXT" disable-output-escaping="yes"/>

                    <!--
                    <tr>
                      <td valign="top" width="3"><img src="/engresources/images/s.gif" width="3"/></td>
                   	   <td valign="top">
                        <a CLASS="SmBlackText"><xsl:value-of select="//NEWS-TEXT" disable-output-escaping="yes"/>
                        </a>
                      </td>
                      <td valign="top" width="2"><img src="/engresources/images/s.gif" width="2"/></td>
                    </tr>

                    -->
                   <!--
                    <tr>
                      <td valign="top" colspan="3" height="6"><img src="/engresources/images/s.gif" height="6"/></td>
                    </tr>
                     -->
                     <!-- 12/07 specs removing this link -->
                     <!--
                    <tr>
                      <td valign="top" width="3"><img src="/engresources/images/s.gif" width="3"/></td>

                  		<td valign="top" align="right">
                    		<a class="MedBlueLink" href="javascript:makeUrl('Content_Resources_Introduction.htm')"><b>More</b></a>
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
                     </td><td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" border="0" width="1"/></td></tr>
                <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/s.gif" border="0" height="1"/></td></tr>
                <tr><td valign="top" colspan="3" height="20"><img src="/engresources/images/s.gif" border="0" height="20"/></td></tr>

                <xsl:if test="(//PERSONALIZATION='false')">
                  <xsl:variable name="NEXTURL">CID=quickSearch&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>
                  <xsl:call-template name="SMALL-LOGIN-FORM">
                    <xsl:with-param name="DB" select="$DATABASE"/>
                    <xsl:with-param name="NEXT-URL" select="$NEXTURL"/>
                  </xsl:call-template>
                </xsl:if>

                <!-- start of a second table for remote dbs -->
                <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160; More Search Sources</b></a></td></tr>
                <tr><td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" border="0" width="1" /></td>
                  <td valign="top" bgcolor="#FFFFFF">
                    <xsl:call-template name="REMOTE-DBS">
                      <xsl:with-param name="SEARCHTYPE">quick</xsl:with-param>
                    </xsl:call-template>
                  </td>
                  <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" border="0" width="1"/></td></tr>
                <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/s.gif" border="0" height="1"/></td></tr>
            </table>
            <!-- End of left most table for news item -->
            </xsl:if>
          </td>
          <td valign="top" width="10" bgcolor="#FFFFFF"><img src="/engresources/images/s.gif" width="10"/></td>
          <td valign="top" align="right" width="100%">
            <!-- Start of table for search form -->
            <table border="0" width="100%" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top" bgcolor="#C3C8D1">

                  <FORM name="quicksearch" action="/controller/servlet/Controller?CID=quickSearchCitationFormat" METHOD="POST"  onSubmit="return searchValidation()" >

                    <!-- Start of table for search form -->
                    <table border="0" cellspacing="0" cellpadding="0" width="500" bgcolor="#C3C8D1">
                      <tr>
                        <td valign="top" width="500" colspan="5" ><img src="/engresources/images/s.gif" height="20"/></td>
                      </tr>
                      <tr>
                        <td valign="top" width="500" colspan="5" >
                        <!-- display database checkboxes -->
                        <xsl:value-of disable-output-escaping="yes" select="DP:toHTML($USERMASK,$SELECTED-DB,'quick')"/>
                        <!-- end of database checkboxes -->
                        </td>
                      </tr>
                      <tr>
                        <td valign="top" width="500" colspan="5" ><img src="/engresources/images/s.gif" height="8"/></td>
                      </tr>
                      <tr>
                        <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                        <td valign="top" width="233" colspan="2" ><a CLASS="SmBlueTableText"><b><label for="srchwrd1">SEARCH FOR</label></b></a></td>
                        <td valign="top" width="233" colspan="2" ><a CLASS="SmBlueTableText"><b><label for="sect1">SEARCH IN</label></b></a></td>
                      </tr>
                      <tr>
                        <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="218">
                          <a CLASS="MedBlackText">
                            <input type="text" name="searchWord1" id="srchWrd1" size="29" onBlur="javascript:updateWinds(); ">
                              <xsl:attribute name="value">
                                <xsl:value-of select="$SEARCH-WORD-1"/>
                              </xsl:attribute>
                            </input>
                          </a>
                          &#160;
                        </td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="218">
                            <a CLASS="MedBlackText">
                            <select size="1" name="section1" id="sect1">
                              <xsl:value-of disable-output-escaping="yes" select="SF:getOption($SEARCH-OPTION-1,$SELECTED-DB,'section')"/>
                            </select>
                            </a>
			    <a href="javascript:makeUrl('Searching_Fields_and_Limits_Introduction.htm')">
			    <img src="/engresources/images/blue_help1.gif" border="0"/>
			    </a>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top" colspan="5" ><img src="/engresources/images/s.gif" height="4"/></td>
                      </tr>
                      <tr>
                        <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="218">
                            <a CLASS="MedBlackText">
                              <select size="1" name="boolean1">
                                <xsl:choose>
                                  <xsl:when test="($BOOLEAN-1='OR') and not(boolean($BOOLEAN-1=''))">
                                    <option value="AND">AND</option>
                                    <option value="OR" selected="selected">OR</option>
                                    <option value="NOT">NOT</option>
                                  </xsl:when>
                                  <xsl:when test="($BOOLEAN-1='NOT') and not(boolean($BOOLEAN-1=''))">
                                    <option value="AND">AND</option>
                                    <option value="OR">OR</option>
                                    <option value="NOT" selected="selected">NOT</option>
                                  </xsl:when>
                                  <xsl:when test="($BOOLEAN-1='')">
                                    <option value="AND" selected="selected">AND</option>
                                    <option value="OR">OR</option>
                                    <option value="NOT">NOT</option>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <option value="AND" selected="selected">AND</option>
                                    <option value="OR">OR</option>
                                    <option value="NOT">NOT</option>
                                  </xsl:otherwise>
                                </xsl:choose>
                              </select>
                            </a>&#160;
                            <a CLASS="MedBlackText">
                              <input type="text" name="searchWord2" size="17" onBlur="javascript:updateWinds();">
                                <xsl:attribute name="value">
                                  <xsl:value-of select="$SEARCH-WORD-2"/>
                                </xsl:attribute>
                              </input>
                            </a>
                        </td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="218">
                          <a CLASS="MedBlackText">
                          <select size="1" name="section2">
                              <xsl:value-of disable-output-escaping="yes" select="SF:getOption($SEARCH-OPTION-2,$SELECTED-DB,'section')"/>
                          </select>
                          </a>
                        </td>
                      </tr>
<!--
                    </table>
                    <table border="1" cellspacing="0" cellpadding="0" width="470" bgcolor="#C3C8D1">
-->
                      <tr>
                        <td valign="top" colspan="5" ><img src="/engresources/images/s.gif" height="4"/></td>
                      </tr>
                      <tr>
                        <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="218">
                            <a CLASS="MedBlackText">
                              <select size="1" name="boolean2">
                                <xsl:choose>
                                  <xsl:when test="($BOOLEAN-2='OR') and not(boolean($BOOLEAN-2=''))">
                                    <option value="AND">AND</option>
                                    <option value="OR" selected="selected">OR</option>
                                    <option value="NOT">NOT</option>
                                  </xsl:when>
                                  <xsl:when test="($BOOLEAN-2='NOT') and not(boolean($BOOLEAN-2=''))">
                                    <option value="AND">AND</option>
                                    <option value="OR">OR</option>
                                    <option value="NOT" selected="selected">NOT</option>
                                  </xsl:when>
                                  <xsl:when test="($BOOLEAN-2='')">
                                    <option value="AND" selected="selected">AND</option>
                                    <option value="OR">OR</option>
                                    <option value="NOT">NOT</option>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <option value="AND" selected="selected">AND</option>
                                    <option value="OR">OR</option>
                                    <option value="NOT">NOT</option>
                                  </xsl:otherwise>
                                </xsl:choose>
                              </select>
                            </a>&#160;
                            <a CLASS="MedBlackText">
                              <input type="text" name="searchWord3" size="17" onBlur="javascript:updateWinds();">
                                <xsl:attribute name="value">
                                  <xsl:value-of select="$SEARCH-WORD-3"/>
                                </xsl:attribute>
                              </input>
                            </a>
                        </td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="218">
                          <a CLASS="MedBlackText" height="10">
                          <select size="1" Name="section3">
                              <xsl:value-of disable-output-escaping="yes" select="SF:getOption($SEARCH-OPTION-3,$SELECTED-DB,'section')"/>
                          </select>
                          </a>
                        </td>
                      </tr>
                    </table>

                    <table border="0" cellspacing="0" cellpadding="0" width="470" bgcolor="#C3C8D1">
                      <tr>
                        <td valign="top" width="470" colspan="5" ><img src="/engresources/images/s.gif" height="4"/></td>
                      </tr>
                      <tr>
                        <td valign="top" height="2"><img src="/engresources/images/s.gif" height="2"/></td>
                        <td valign="top" colspan="4" height="2" bgcolor="#3173B5" width="100%"><img src="/engresources/images/s.gif" height="2"/></td>
                      </tr>
                      <tr>
                        <td valign="top" width="470" colspan="5" ><img src="/engresources/images/s.gif" height="4"/></td>
                      </tr>
                      <tr>
                        <!-- indentation / spacing -->
                        <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                        <td valign="top" width="233" colspan="2" ><a CLASS="SmBlueTableText"><b>LIMIT BY</b></a></td>
                        <td valign="top" width="233" colspan="2" ><a CLASS="SmBlueTableText"><b>SORT BY</b></a></td>
                      </tr>
                      <tr>
                        <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="233" >
                          <table border="0" cellspacing="0" cellpadding="0" >
                            <xsl:if test="(boolean(bit:hasBitSet($USERMASK,1))
                            	or boolean(bit:hasBitSet($USERMASK,2))
                            	or boolean(bit:hasBitSet($USERMASK,16384))
                            	or boolean(bit:hasBitSet($USERMASK,32768))
                            	or boolean(bit:hasBitSet($USERMASK,262144))
                            	or boolean(bit:hasBitSet($USERMASK,1048576))
                            	or boolean(bit:hasBitSet($USERMASK,8192)))">
                              <tr>
                                <td>
                                  <a CLASS="MedBlackText">
                                  <select style="width:175px;" size="1" name="doctype" onchange="checkPatent(this.form);">
                                      <xsl:value-of disable-output-escaping="yes" select="SF:getOption($DOCUMENT-TYPE,$SELECTED-DB,'doctype')"/>
                                  </select>
                                  </a>
				    <a href="javascript:makeUrl('Limits.htm')">
				    <img src="/engresources/images/blue_help1.gif" border="0"/>
				    </a>
                                </td>
                              </tr>
                            </xsl:if>
                              <xsl:if test="(boolean(bit:hasBitSet($USERMASK,1)) or boolean(bit:hasBitSet($USERMASK,2)))">                                 
                                <tr>
                                  <td valign="top" ><img src="/engresources/images/s.gif" height="4"/></td>
                                </tr>
                                <tr>
                                  <td>
                                    <a CLASS="MedBlackText">
                                    <select style="width:175px;" size="1" name="treatmentType">
                                      <xsl:value-of disable-output-escaping="yes" select="SF:getOption($TREATMENT-TYPE,$SELECTED-DB,'treattype')"/>
                                    </select>
                                    </a>
                                    <a href="javascript:makeUrl('Limits.htm')">
				    <img src="/engresources/images/blue_help1.gif" border="0"/>
				    </a>
                                  </td>
                                </tr>
                              </xsl:if>
                              <xsl:if test="boolean(bit:hasBitSet($USERMASK,2)) or boolean(bit:hasBitSet($USERMASK,1048576)) ">
                                <tr>
                                  <td valign="top" ><img src="/engresources/images/s.gif" height="4"/></td>
                                </tr>
                                <tr>
                                  <td>
                                    <a CLASS="MedBlackText">
                                    <select style="width:200px;" size="1" name="disciplinetype">
                                        <xsl:value-of disable-output-escaping="yes" select="SF:getOption($DISCIPLINE-TYPE,$SELECTED-DB,'discipline')"/>
                                    </select>
                                    </a>
                                    <a href="javascript:makeUrl('Limits.htm')">
				       <img src="/engresources/images/blue_help1.gif" border="0"/>
				    </a>
                                  </td>
                                </tr>
                              </xsl:if>
                              <tr>
                                <td valign="top" ><img src="/engresources/images/s.gif" height="4"/></td>
                              </tr>
                              <tr>
                                <td>
                                  <a CLASS="MedBlackText">
                                    <select size="1" name="language">
                                    	<xsl:value-of disable-output-escaping="yes" select="SF:getOption($LANGUAGE,$SELECTED-DB,'language')"/>
                                    </select>
                                  </a>
                                </td>
                              </tr>
                          </table>
                        </td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="233" >

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
                          <input type="checkbox" name="autostem" id="chkstm">
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
                        <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="233" >

                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr><td height="4"><img src="/engresources/images/s.gif" height="4"/></td></tr>
                            <tr>
                              <td valign="top">
                                <a CLASS="MedBlackText">
                                  <input type="radio" name="yearselect" value="yearrange" >
                                  <xsl:if test="not(number($LASTFOURUPDATES))">
                                    <xsl:attribute name="checked"/>
                                  </xsl:if>
                                  </input>&#160;
                                 
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
                            <tr><td height="4"><img src="/engresources/images/s.gif" height="4"/></td></tr>
                            <tr>
                              <td valign="top">
                                <a CLASS="MedBlackText">
                                <input type="radio" name="yearselect" value="lastupdate" id="rdupdt" onclick="checkLastUpdates();">
                                  <xsl:if test="number($LASTFOURUPDATES)">
                                    <xsl:attribute name="checked"/>
                                  </xsl:if>&#160;
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
                                </input>
                                </a>
                                <a class="SmBlackText">&#160;<label for="rdupdt">Updates</label></a>
			        <a href="javascript:makeUrl('Date_Limits.htm')">
				  <img src="/engresources/images/blue_help1.gif" border="0"/>
		                </a>
                              </td>
                            </tr>
                          </table>

                        </td>
                        <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                        <td valign="top" width="233" >
                          <a CLASS="MedBlackText" onClick="return searchValidation()">
                          <input type="image" src="/engresources/images/search_orange1.gif" name="search" value="Search" border="0"/>
                          </a>&#160; &#160;
                          <a href="/controller/servlet/Controller?CID=quickSearch&amp;database={$SELECTED-DB}">
                            <img src="/engresources/images/reset_orange1.gif" border="0"/>
                          </a>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top" colspan="5" ><img src="/engresources/images/s.gif" height="4" /></td>
                      </tr>
                    </table>
                    <!-- End of table for search form -->
                  </FORM>
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
              <!-- End of table for lookup indexes. -->
              <tr>
                <td valign="top" colspan="4">
                  <xsl:if test="$TIPS='true'">
                    <!-- Start of table for help tips -->
                    <table border="0" width="100%" cellspacing="0" cellpadding="0">
                      <tr>
                        <td valign="top" width="100%">
                          <!-- table for Help tips -->
                          <table border="0" width="100%" cellspacing="0" cellpadding="0">
                            <tr><td valign="top" colspan="3" height="10"><img src="/engresources/images/s.gif" height="10"/></td></tr>
                            <tr><td valign="top" width="1" bgcolor="#3173B5"><img src="/engresources/images/s.gif" width="1"/></td>
                              <td valign="top">
                                <table border="0" width="100%" cellspacing="0" cellpadding="0">
                                  <tr><td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/engresources/images/s.gif" height="1"/></td></tr>
                                  <tr><td valign="top" height="10" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160; Search Tips</b></a></td></tr>
                                  <tr><td valign="top" height="15" colspan="3"><img src="/engresources/images/s.gif" height="15"/></td></tr>
                                  <tr><td valign="top" width="4"><img src="/engresources/images/s.gif" width="4"/></td><td valign="top">
                                  <table border="0" width="100%" cellspacing="0" cellpadding="0">
                                  <tr><td valign="top" colspan="2">
                                  <a CLASS="MedBlackText">Use truncation  (*) to search for words that begin with the same letters.
                                  </a></td></tr>
                                  <tr><td width="10"></td><td valign="top"><a CLASS="MedBlackText">comput* returns computer, computers, computerize, computerization<br/><br/></a></td></tr>
                                  <tr><td colspan="2">
                                  <a CLASS="MedBlackText">Truncation can also be used to replace any number of characters internally.</a></td></tr>
                                  <tr><td width="10"></td><td valign="top"><a CLASS="MedBlackText">sul*ate returns sulphate or sulfate<br/><br/></a></td></tr>
                                  <tr><td colspan="2" valign="top">
                                  <a CLASS="MedBlackText">Use wildcard (?) to replace a single character.</a></td></tr>
                                  <tr><td width="10"></td><td valign="top">
                                  <a CLASS="MedBlackText">wom?n retrieves woman or women<br/><br/></a></td></tr>
                                  <tr><td colspan="2" valign="top">
                                  <a CLASS="MedBlackText">Terms are automatically stemmed, except in the author field, unless the "Autostemming off" feature is checked.</a></td></tr>
                                  <tr><td width="10"></td><td valign="top"><a CLASS="MedBlackText">management returns manage, managed, manager, managers, managing, management<br/><br/></a></td></tr>
                                  <tr><td colspan="2" valign="top">
                                  <a CLASS="MedBlackText">To search for an exact phrase or phrases containing stop words (and, or, not, near), enclose terms in braces or quotation marks.</a></td></tr>
                                  <tr><td width="10"></td><td valign="top"><a CLASS="MedBlackText">{Journal of Microwave Power and Electromagnetic Energy} <br/>
                                  "near field scanning"<br/><br/></a></td></tr>
                                  <tr><td colspan="2" valign="top">
                                  <a CLASS="MedBlackText">Use NEAR or ONEAR to search for terms in proximity.  ONEAR specifies the exact order of terms.  NEAR and ONEAR cannot be used with truncation, wildcards, parenthesis, braces or quotation marks.  NEAR and ONEAR can be used with stemming.</a></td></tr>
                                  <tr><td width="10"></td><td valign="top"><a CLASS="MedBlackText">Avalanche ONEAR/0 diodes<br/>
                                  Solar NEAR energy<br/><br/></a></td></tr>
                                  <tr><td valign="top" colspan="2">
                                  <a CLASS="MedBlackText">Browse the author look-up index to select all variations of an author's name</a></td></tr>
                                  <tr><td width="10"></td><td valign="top"><a CLASS="MedBlackText">Smith, A. OR  Smith, A.J. OR Smith, Alan J.<br/><br/></a></td></tr>
                                  </table>
                                  </td></tr>
                                  <tr><td colspan="3" height="8"><img src="/engresources/images/s.gif" height="8"/></td></tr>
                                  <tr><td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/engresources/images/s.gif" height="1"/></td></tr>
                                </table>
                              </td>
                              <td valign="top" width="1" bgcolor="#3173B5"><img src="/engresources/images/s.gif" width="1"/></td>
                            </tr>
                            <tr>
                              <td valign="top" height="8" colspan="3"><img src="/engresources/images/s.gif" height="8"/></td>
                            </tr>
                          </table>
                          <!-- End of  table for Help tips -->
                        </td>
                      </tr>
                    </table>
                  </xsl:if>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </center>
<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/QuickSearchForm_V10.js"/>
<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Login.js"/>
<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/RemoteDbLink_V5.js"/>
<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>
  <script type="text/javascript"  language="javascript">
  <xsl:comment>
  flipImage(<xsl:value-of select="$SELECTED-DB"/>);
  // </xsl:comment>
  </script>
  </xsl:template>
</xsl:stylesheet>
