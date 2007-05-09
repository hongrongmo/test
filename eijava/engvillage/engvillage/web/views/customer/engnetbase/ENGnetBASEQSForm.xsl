<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:bit="java:org.ei.util.BitwiseOperators"
    exclude-result-prefixes="java xsl html"
>
    <xsl:include href="../common/forms/MoreSearchSources.xsl"/>

    <xsl:template match="ENGnetBASE-QUICK-SEARCH">
        <xsl:param name="SESSION-ID"/>
        <xsl:param name="SELECTED-DB"/>
    <xsl:param name="NEWS">true</xsl:param>
    <xsl:param name="TIPS">true</xsl:param>

    <xsl:variable name="USERMASK">
        <xsl:value-of select="//USERMASK"/>
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

        <xsl:variable name="AUTOSTEMMING">
            <xsl:value-of select="//SESSION-DATA/AUTOSTEMMING"/>
        </xsl:variable>

    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/QuickSearchForm_V7.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/RemoteDbLink_V5.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>

        <xsl:text disable-output-escaping="yes">
            <![CDATA[
                <xsl:comment>
                    <script language="javascript">

                        // This function resets the form
                        function doReset()
                        {
                            document.quicksearch.searchWord1.value="";
                            document.quicksearch.searchWord2.value="";
                            document.quicksearch.searchWord3.value="";
                            document.quicksearch.section1[0].selected=true;
                            document.quicksearch.section2[0].selected=true;
                            document.quicksearch.section3[0].selected=true;
                            document.quicksearch.boolean1[0].selected=true;
                            document.quicksearch.boolean2[0].selected=true;
                            document.quicksearch.astem.checked = false;

                            document.quicksearch.searchWord1.focus();

                        }

                    </script>
                </xsl:comment>
            ]]>
        </xsl:text>

    <center>

            <!-- Start of table for content below the navigation bar -->
            <table border="0" width="99%" cellspacing="0" cellpadding="0">
                <tr>
                    <td valign="top" width="140">
            <xsl:if test="$NEWS='true'">
                        <!-- left most table for news items -->
                        <table border="0" width="140" cellspacing="0" cellpadding="0">
                            <tr>
                                <td valign="top" height="15" bgcolor="#3173B5" colspan="3">
                                    <a CLASS="LgWhiteText"><b>&#160; ENGnetBASE </b></a>
                                </td>
                            </tr>
                            <tr>
                                <td valign="top" bgcolor="#3173B5" width="1">
                                    <img src="/engresources/images/spacer.gif" border="0" width="1"/>
                                </td>
                                <td valign="top" bgcolor="#FFFFFF">
                                    <!-- Tiny table for news features -->
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
                                                <a CLASS="SmBlackText">CRC ENGnetBASE gives you online access to some of the world's leading engineering handbooks. Currently, more than 140 titles are available. Explore technical concepts in depth by entering your query.</a>
                                            </td>
                                            <td valign="top" width="2">
                                                <img src="/engresources/images/spacer.gif" border="0" width="2"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" colspan="3" height="6"><img src="/engresources/images/spacer.gif" border="0" height="6"/></td>
                                        </tr>
                                        <tr>
                                            <td valign="top" width="3">
                                                <img src="/engresources/images/spacer.gif" border="0" width="3"/>
                                            </td>
                                            <td valign="top" align="right">
                                            <a class="MedBlueLink" href="javascript:makeUrl('CRC_ENGnetBASE_.htm')">
                                            <b>More</b></a></td>
                                            <td valign="top" width="2">
                                                <img src="/engresources/images/spacer.gif" border="0" width="2"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" colspan="3" height="5"><img src="/engresources/images/spacer.gif" border="0"/></td>
                                        </tr>

                                    </table>
                                    <!-- End of Tiny table for news features -->
                                </td>
                    <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/spacer.gif" border="0" width="1" /></td>
                        </tr>
                        <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/spacer.gif" border="0" height="1" /></td></tr>
                        <tr><td valign="top" colspan="3" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20" /></td></tr>

                        <!-- start of a second table for remote dbs -->
                        <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160; More Search Options</b></a></td></tr>
                        <tr><td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/spacer.gif" border="0" width="1" /></td>
                        <td valign="top" bgcolor="#FFFFFF">
                            <xsl:call-template name="REMOTE-DBS">
                                <xsl:with-param name="SEARCHTYPE">quick</xsl:with-param>
                                <xsl:with-param name="LOCATION">CRC ENGnetBASE</xsl:with-param>
                            </xsl:call-template>
                        </td>

                          <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" width="1"/></td>
                        </tr>
                        <tr>
                          <td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/spacer.gif" border="0" height="1" /></td>
                        </tr>
                        </table>
                        <!-- End of left most table for news item -->
                    </xsl:if>
                    </td>
                    <td valign="top" width="10" bgcolor="#FFFFFF">
                        <img src="/engresources/images/spacer.gif" border="0" width="10"/>
                    </td>
                    <td valign="top" align="right" width="100%">
                        <!-- Start of table for search form -->
                        <table border="0" align="right" width="100%" cellspacing="0" cellpadding="0">
                            <tr>
                                <td valign="top" bgcolor="#C3C8D1" colspan="4">
                                    <!-- Start of table for search form -->
                                    <form name="quicksearch" action="/controller/servlet/Controller?CID=engnetbaseQuick" method="post" onSubmit="return searchValidation();">
                                     <INPUT type="HIDDEN" name="database" value="{$SELECTED-DB}"/>
                                        <table border="0" cellspacing="0" cellpadding="0" width="470" bgcolor="#C3C8D1">
                                            <tr>
                                                <td valign="top" height="20" colspan="5">
                                                    <img src="/engresources/images/spacer.gif" border="0" height="20"/>
                                                </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" width="4" bgcolor="#C3C8D1"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td>
                                               <td valign="top" colspan="4" bgcolor="#C3C8D1"><a class="MedBlackText"><b>CRC ENGnetBASE</b></a></td>
                                            </tr>
                                            <tr>
                                                <td valign="top" colspan="5" bgcolor="#C3C8D1" height="8">
                                                    <img src="/engresources/images/spacer.gif" border="0" height="8"/>
                                                </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" width="4" bgcolor="#C3C8D1">
                                                 <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                                               </td>
                                               <td valign="top" colspan="2" bgcolor="#C3C8D1">
                                                 <a CLASS="SmBlueTableText"><b>SEARCH FOR</b></a>
                                               </td>
                                               <td valign="top" colspan="2" bgcolor="#C3C8D1">
                                                <a CLASS="SmBlueTableText"><b>SEARCH IN</b></a>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" width="4" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                                               </td>
                                               <td valign="top" width="15" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" width="15"/>
                                               </td>
                                               <td valign="top" bgcolor="#C3C8D1">
                                                <input type="text" name="searchWord1" size="29">
                                                    <xsl:attribute name="value">
                                                        <xsl:value-of select="$SEARCH-WORD-1"/>
                                                    </xsl:attribute>
                                                </input>
                                               </td>
                                               <td valign="top" bgcolor="#C3C8D1" colspan="2" align="left">
                                                   <a CLASS="MedBlackText" colspan="2">&#160; &#160; &#160;
                                                    <select name="section1">
                                                        <xsl:for-each select="//FIELDS/FIELD">
                                                            <option >
                                                                <xsl:if test="(@SHORTNAME=$SEARCH-OPTION-1)">
                                                                    <xsl:attribute name="selected"/>
                                                                </xsl:if>
                                                                <xsl:attribute name="value"><xsl:value-of select="@SHORTNAME"/></xsl:attribute>
                                                                <xsl:value-of select="@DISPLAYNAME"/>
                              </option>
                                                        </xsl:for-each>
                                                    </select>
                                                   </a>
                                                   <a href="javascript:makeUrl('CRC_ENGnetBASE_.htm')">
						   	<img src="/engresources/images/blue_help1.gif" border="0"/>
			                            </a>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" colspan="5" height="4" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" height="4"/>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" width="4" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                                               </td>
                                               <td valign="top" width="15" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" width="15"/>
                                               </td>
                                               <td valign="top" height="10" bgcolor="#C3C8D1" width="240">
                                                 <table border="0" cellspacing="0" cellpadding="0" width="240" height="10" bgcolor="#C3C8D1">
                                                   <tr>
                                                    <td valign="top" bgcolor="#C3C8D1">
                                                         <a CLASS="MedBlackText">
                                                            <select name="boolean1">
                                                                <xsl:choose>
                                                                    <xsl:when test="($BOOLEAN-1='OR') and not(boolean($BOOLEAN-1=''))">
                                                                        <OPTION value="AND">AND</OPTION>
                                                                        <OPTION value="OR" selected="selected">OR</OPTION>
                                                                        <OPTION value="NOT">NOT</OPTION>
                                                                    </xsl:when>
                                                                    <xsl:when test="($BOOLEAN-1='NOT') and not(boolean($BOOLEAN-1=''))">
                                                                        <OPTION value="AND">AND</OPTION>
                                                                        <OPTION value="OR">OR</OPTION>
                                                                        <OPTION value="NOT" selected="selected">NOT</OPTION>
                                                                    </xsl:when>
                                                                    <xsl:when test="($BOOLEAN-1='')">
                                                                        <OPTION value="AND" selected="selected">AND</OPTION>
                                                                        <OPTION value="OR">OR</OPTION>
                                                                        <OPTION value="NOT">NOT</OPTION>
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                        <OPTION value="AND" selected="selected">AND</OPTION>
                                                                        <OPTION value="OR">OR</OPTION>
                                                                        <OPTION value="NOT">NOT</OPTION>
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
                                                            </select>
                                                        </a>
                                                    </td>
                                                    <td valign="top" width="4">
                                                        <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                                                    </td>
                                                    <td valign="top" height="10" bgcolor="#C3C8D1">
                                                        <a CLASS="MedBlackText">
                                                            <input type="text" size="17" name="searchWord2">
                                                                <xsl:attribute name="value">
                                                                    <xsl:value-of select="$SEARCH-WORD-2"/>
                                                                </xsl:attribute>
                                                            </input>
                                                        </a>
                                                    </td>
                                                    </tr>
                                                 </table>
                                               </td>
                                               <td valign="top" colspan="2" bgcolor="#C3C8D1" width="211" align="left">
                                                <a CLASS="MedBlackText" height="10">&#160; &#160; &#160;
                                                  <select name="section2">
                                                        <xsl:for-each select="//FIELDS/FIELD">
                                                            <option >
                                                                <xsl:if test="(@SHORTNAME=$SEARCH-OPTION-1)">
                                                                    <xsl:attribute name="selected"/>
                                                                </xsl:if>
                                                                <xsl:attribute name="value"><xsl:value-of select="@SHORTNAME"/></xsl:attribute>
                                                                <xsl:value-of select="@DISPLAYNAME"/>
                              </option>
                                                        </xsl:for-each>
                                                  </select>
                                                </a>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" height="4" colspan="5" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" height="4"/>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" width="4" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                                               </td>
                                               <td valign="top" width="15" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" width="15"/>
                                               </td>
                                               <td valign="top" height="10" bgcolor="#C3C8D1" width="240">
                                                 <table border="0" cellspacing="0" cellpadding="0" width="240" height="10">
                                                   <tr>
                                                    <td valign="top" bgcolor="#C3C8D1">
                                                        <a CLASS="MedBlackText">
                                                            <select name="boolean2">
                                                                <xsl:choose>
                                                                    <xsl:when test="($BOOLEAN-2='OR') and not(boolean($BOOLEAN-2=''))">
                                                                        <OPTION value="AND">AND</OPTION>
                                                                        <OPTION value="OR" selected="selected">OR</OPTION>
                                                                        <OPTION value="NOT">NOT</OPTION>
                                                                    </xsl:when>
                                                                    <xsl:when test="($BOOLEAN-2='NOT') and not(boolean($BOOLEAN-2=''))">
                                                                        <OPTION value="AND">AND</OPTION>
                                                                        <OPTION value="OR">OR</OPTION>
                                                                        <OPTION value="NOT" selected="selected">NOT</OPTION>
                                                                    </xsl:when>
                                                                    <xsl:when test="($BOOLEAN-2='')">
                                                                        <OPTION value="AND" selected="selected">AND</OPTION>
                                                                        <OPTION value="OR">OR</OPTION>
                                                                        <OPTION value="NOT">NOT</OPTION>
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                        <OPTION value="AND" selected="selected">AND</OPTION>
                                                                        <OPTION value="OR">OR</OPTION>
                                                                        <OPTION value="NOT">NOT</OPTION>
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
                                                            </select>
                                                        </a>
                                                    </td>
                                                    <td valign="top" width="4">
                                                        <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                                                    </td>
                                                    <td valign="top" height="10" bgcolor="#C3C8D1">
                                                        <a CLASS="MedBlackText">
                                                            <input type="text" size="17" name="searchWord3">
                                                                <xsl:attribute name="value">
                                                                    <xsl:value-of select="$SEARCH-WORD-3"/>
                                                                </xsl:attribute>
                                                            </input>
                                                        </a>
                                                    </td>
                                                   </tr>
                                                 </table>
                                               </td>
                                               <td valign="top" colspan="2" bgcolor="#C3C8D1" width="211" align="left">
                                                   <a CLASS="MedBlackText" height="10">&#160; &#160; &#160;
                                                      <select name="section3">
                                                        <xsl:for-each select="//FIELDS/FIELD">
                                                            <option >
                                                                <xsl:if test="(@SHORTNAME=$SEARCH-OPTION-1)">
                                                                    <xsl:attribute name="selected"/>
                                                                </xsl:if>
                                                                <xsl:attribute name="value"><xsl:value-of select="@SHORTNAME"/></xsl:attribute>
                                                                <xsl:value-of select="@DISPLAYNAME"/>
                                </option>
                                                        </xsl:for-each>
                                                      </select>
                                                   </a>
                                               </td>
                                            </tr>
                                            <tr>
                                                <td valign="top" colspan="5" height="8" bgcolor="#C3C8D1">
                                                    <img src="/engresources/images/spacer.gif" border="0" height="8"/>
                                                </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" colspan="5" height="4">
                                                <img src="/engresources/images/spacer.gif" border="0" height="4"/>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" width="4" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" width="4"/>
                                               </td>
                                               <td valign="top" width="15" bgcolor="#C3C8D1">
                                                <img src="/engresources/images/spacer.gif" border="0" width="15"/>
                                               </td>
                                               <td valign="top" bgcolor="#C3C8D1">
                          <a CLASS="SmBlackText">
                                                    <input type="checkbox" name="astem">
                                                    <xsl:if test="($AUTOSTEMMING='off')">
                                                      <xsl:attribute name="checked"/>
                                                    </xsl:if>
                                                    </input>
                          Autostemming off &#160;
                          </a>
                                                  <a href="javascript:makeUrl('Autostemming.htm')">
						  	<img src="/engresources/images/blue_help1.gif" border="0"/>
			    			 </a>
                                               </td>
                                               <td valign="bottom">&#160;
                                               <input type="image" src="/engresources/images/search_orange1.gif" border="0" name="search" value="Search"/>
                                                &#160; &#160;
                                                <a href="javascript:doReset()"> <img name="reset" src="/engresources/images/reset_orange1.gif"  border="0"/></a>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" colspan="5" height="1">
                                                 <img src="/engresources/images/spacer.gif" border="0" height="1" width="455"/>
                                               </td>
                                            </tr>
                                        </table>
                                    </form>
                                    <!-- end of search form -->
                                </td>
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
                                                    <tr>
                                                        <td valign="top" colspan="3" height="10">
                                                            <img src="/engresources/images/spacer.gif" border="0" height="10"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td valign="top" width="1" bgcolor="#3173B5">
                                                            <img src="/engresources/images/spacer.gif" border="0" width="1"/>
                                                        </td>
                                                        <td valign="top">
                                                            <table border="0" width="100%" cellspacing="0" cellpadding="0">
                                                                <tr>
                                                                    <td valign="top" bgcolor="#3173B5" height="1" colspan="3">
                                                                        <img src="/engresources/images/spacer.gif" border="0" height="1"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td valign="top" height="10" bgcolor="#3173B5" colspan="3">
                                                                        <a CLASS="LgWhiteText"><b>&#160; Search Tips</b></a>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td valign="top" height="10" colspan="3">
                                                                        <img src="/engresources/images/spacer.gif" border="0" height="10"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td valign="top" width="4">
                                                                        <img src="/engresources/images/spacer.gif" width="4" border="0"/>
                                                                    </td>
                                                                    <td valign="top">
                                                                        <a CLASS="MedBlackText">
                                                                        Use truncation  (*) to search for words that begin with the same letters.<br/>
                                                                        comput* returns computer, computers, computerize, computerization

                                                                        <P>
                                                                        Terms are automatically stemmed.<br/>
                                                                        management returns manage, managed, manager, managers, managing, management<br/>
                                                                        Click "Autostemming off" to disable this feature.
                                                                        </P>

                                                                        <P>
                                                                        Remember to use variant spellings.<br/>
                                                                        Seat belt* or seatbelt*
                                                                        </P>

                                                                        </a>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td valign="top" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td>
                                                                </tr>
                                                                <tr>
                                                                    <td valign="top" bgcolor="#3173B5" height="1" colspan="3">
                                                                        <img src="/engresources/images/spacer.gif" border="0" height="1"/>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </td>
                                                        <td valign="top" width="1" bgcolor="#3173B5">
                                                            <img src="/engresources/images/spacer.gif" border="0" width="1"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td valign="top" height="8" colspan="3">
                                                            <img src="/engresources/images/spacer.gif" border="0" height="8"/>
                                                        </td>
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
                        <!-- End of table for search form -->
                    </td>
                </tr>
            </table>

    </center>

            <!-- End of table for content below the navigation bar -->

</xsl:template>
</xsl:stylesheet>
