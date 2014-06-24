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

  <xsl:template match="ENGnetBASE-EXPERT-SEARCH">
    <xsl:param name="SESSION-ID"/>
    <xsl:param name="SELECTED-DB"/>
    <xsl:param name="NEWS">true</xsl:param>
    <xsl:param name="TIPS">true</xsl:param>

    <!-- variables used in transfer of data between search forms -->
    <xsl:variable name="SEARCH-WORD-1">
        <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-PHRASE-1"/>
    </xsl:variable>

    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/ExpertSearchForm_V16.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/RemoteDbLink_V5.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Robohelp.js"/>

        <xsl:text disable-output-escaping="yes">
            <![CDATA[
                <xsl:comment>
                    <script language="javascript">

                        // THIS FUNCTION WILL RESET THE EXPERT SEARCH FORM PARAMETERS TO THE DEFAULT VALUES
                        function doReset()
                        {
                            document.quicksearch.searchWord1.value="";
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
                                    <img src="/static/images/spacer.gif" border="0" width="1"/>
                                </td>
                                <td valign="top" bgcolor="#FFFFFF">
                                    <!-- Tiny table for news features -->
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td valign="top" height="5" colspan="3">
                                                <img src="/static/images/spacer.gif" border="0" height="5"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" width="3">
                                                <img src="/static/images/spacer.gif" border="0" width="3"/>
                                            </td>
                                            <td valign="top">
                                                <a CLASS="SmBlackText">CRC ENGnetBASE gives you online access to some of the world's leading engineering handbooks. Currently more than 140 titles are available. Explore technical concepts in depth by entering your query.</a>
                                            </td>
                                            <td valign="top" width="2">
                                                <img src="/static/images/spacer.gif" border="0" width="2"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" colspan="3" height="6"><img src="/static/images/spacer.gif" border="0" height="6"/></td>
                                        </tr>
                                        <tr>
                                            <td valign="top" width="3">
                                                <img src="/static/images/spacer.gif" border="0" width="3"/>
                                            </td>
                                            <td valign="top" align="right">
					    <a class="MedBlueLink" href="javascript:makeUrl('CRC_ENGnetBASE_.htm')">
                                            <b>More</b></a></td>
                                            <td valign="top" width="2">
                                                <img src="/static/images/spacer.gif" border="0" width="2"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" colspan="3" height="5"><img src="/static/images/spacer.gif" border="0"/></td>
                                        </tr>
                                    </table>
                                    <!-- End of Tiny table for news features -->
                                </td>
                    <td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/spacer.gif" border="0" width="1" /></td>
                        </tr>
                        <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/static/images/spacer.gif" border="0" height="1" /></td></tr>
                        <tr><td valign="top" colspan="3" height="20"><img src="/static/images/spacer.gif" border="0" height="20" /></td></tr>

                        <!-- start of a second table for remote dbs -->
                        <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160; More Search Options</b></a></td></tr>
                        <tr><td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/spacer.gif" border="0" width="1" /></td>
                            <td valign="top" bgcolor="#FFFFFF">
                            <xsl:call-template name="REMOTE-DBS">
                                <xsl:with-param name="SEARCHTYPE">expert</xsl:with-param>
                                <xsl:with-param name="LOCATION">CRC ENGnetBASE</xsl:with-param>
                            </xsl:call-template>
                        </td>

                          <td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/s.gif" width="1"/></td>
                        </tr>
                        <tr>
                          <td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/static/images/spacer.gif" border="0" height="1" /></td>
                        </tr>
                        </table>
                        <!-- End of left most table for news item -->
                    </xsl:if>
                    </td>
                    <td valign="top" width="10" bgcolor="#FFFFFF">
                        <img src="/static/images/spacer.gif" border="0" width="10"/>
                    </td>
                    <td valign="top" align="right" width="100%">
                        <!-- Start of table for search form -->
                        <table border="0" align="right" width="100%" cellspacing="0" cellpadding="0">
                            <tr>
                                <td valign="top" bgcolor="#C3C8D1" width="100%">
                                    <!-- Start of table for search form -->
                                    <form name="quicksearch" method="post" action="/controller/servlet/Controller?CID=engnetbaseExpert" onSubmit="return searchValidation();">
                                        <INPUT type="HIDDEN" name="database" value="{$SELECTED-DB}"/>
                                        <table border="0" cellspacing="0" cellpadding="0" width="490" bgcolor="#C3C8D1">
                                            <tr>
                                                <td valign="top" height="20" colspan="5">
                                                    <img src="/static/images/spacer.gif" border="0" height="20"/>
                                                </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" width="4" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                                               <td valign="top" colspan="4" bgcolor="#C3C8D1"><a class="MedBlackText"><b>CRC ENGnetBASE</b></a></td>
                                            </tr>

                                            <tr>
                                                <td valign="top" colspan="3" bgcolor="#C3C8D1" height="8">
                                                    <img src="/static/images/spacer.gif" border="0" height="8"/>
                                                </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" width="4" bgcolor="#C3C8D1">
                                                <img src="/static/images/spacer.gif" border="0" width="4"/>
                                               </td>
                                               <td valign="top" colspan="2" bgcolor="#C3C8D1">
                                                 <a CLASS="SmBlueTableText"><b>ENTER SEARCH TERMS BELOW</b></a>
                                               </td>
                                            </tr>
                                            <tr>
                                               <td valign="top" width="4" bgcolor="#C3C8D1">
                                                <img src="/static/images/spacer.gif" border="0" width="4"/>
                                               </td>
                                               <td valign="top" width="15" bgcolor="#C3C8D1">
                                                <img src="/static/images/spacer.gif" border="0" width="15"/>
                                               </td>
                                               <td valign="top" bgcolor="#C3C8D1">
                                                <a CLASS="MedBlackText">
                                                    <textarea rows="4" cols="35" wrap="PHYSICAL" NAME="searchWord1">
                                                        <xsl:value-of select="$SEARCH-WORD-1"/>
                                                    </textarea>
                                                </a>
                                                <a href="javascript:makeUrl('CRC_ENGnetBASE_.htm')">
						 <img src="/static/images/blue_help1.gif" border="0"/>
			    			</a>
                                               </td>
                                               </tr>
                                               <tr>
                                                   <td valign="top" colspan="3" bgcolor="#C3C8D1" height="8">
                                                    <img src="/static/images/spacer.gif" border="0" height="8"/>
                                                   </td>
                                               </tr>
                                               <tr>
                                                   <td valign="top" width="4" bgcolor="#C3C8D1">
                                                    <img src="/static/images/spacer.gif" border="0" width="4"/>
                                                   </td>
                                                   <td valign="top" width="15" bgcolor="#C3C8D1">
                                                    <img src="/static/images/spacer.gif" border="0" width="15"/>
                                                   </td>
                                                   <td valign="top" align="left">
                                                       <input type="image" src="/static/images/search_orange1.gif" border="0" name="search" value="Search"/>
                                                       &#160; &#160;
                                                       <a href="javascript:doReset()">
                                                        <img src="/static/images/reset_orange1.gif" border="0"/>
                                                       </a>
                                                   </td>
                                               </tr>
                                        </table>
                                    </form>
                                    <!-- end of search form -->
                                </td>
                            </tr>
                            <tr>
                                <td valign="top">
                  <xsl:if test="$TIPS='true'">
                                    <!-- Start of table for help tips -->
                                    <center>
                                        <table border="0" width="99%" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <td valign="top" width="100%">
                                                    <!-- table for Help tips -->
                                                        <table border="0" width="100%" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <td valign="top" colspan="3" height="10">
                                                                    <img src="/static/images/spacer.gif" border="0" height="10"/>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td valign="top" width="1" bgcolor="#3173B5">
                                                                    <img src="/static/images/spacer.gif" border="0" width="1"/>
                                                                </td>
                                                            <td valign="top">
                                                                <table border="0" width="100%" cellspacing="0" cellpadding="0">
                                                                    <tr>
                                                                        <td valign="top" bgcolor="#3173B5" height="1" colspan="3">
                                                                            <img src="/static/images/spacer.gif" border="0" height="1"/>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td valign="top" height="10" bgcolor="#3173B5" colspan="3">
                                                                            <a CLASS="LgWhiteText"><b>&#160; Search Tips</b></a>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td valign="top" height="10" colspan="3">
                                                                            <img src="/static/images/spacer.gif" border="0" height="10"/>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td valign="top" width="4">
                                                                            <img src="/static/images/spacer.gif" width="4" border="0"/>
                                                                        </td>
                                                                        <td valign="top">
                                                                            <a CLASS="MedBlackText">
                                                                            Use truncation  (*) to search for words that begin with the same letters.<br/>
                                                                            comput* returns computer, computers, computerize, computerization

                                                                            <P>
                                                                            Stem search terms using $<br/>
                                                                            $management returns manage, managed, manager, managers, managing, management
                                                                            </P>

                                                                            <P>
                                                                            Remember to use variant spellings.<br/>
                                                                            Seat belt* or seatbelt*

                                                                            </P>

                                                                            </a>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                    <td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td valign="top" bgcolor="#3173B5" height="1" colspan="3">
                                                                            <img src="/static/images/spacer.gif" border="0" height="1"/>
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                            <td valign="top" width="1" bgcolor="#3173B5">
                                                                <img src="/static/images/spacer.gif" border="0" width="1"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td valign="top" height="8" colspan="3">
                                                                <img src="/static/images/spacer.gif" border="0" height="8"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                    <!-- End of table for search tips -->
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- End of table for search form -->
                                    </center>
                                    </xsl:if>
                                </td>
                                </tr>
                            </table>
                    </td>
                </tr>
            </table>
            <!-- End of table for content below the navigation bar -->
    </center>

</xsl:template>
</xsl:stylesheet>
