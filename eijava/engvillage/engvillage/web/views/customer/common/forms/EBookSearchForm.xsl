<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    xmlns:DP="java:org.ei.domain.Displayer"
    xmlns:SF="java:org.ei.domain.SearchForm"
    exclude-result-prefixes="java xsl html DD DP SF"
>

    <xsl:include href="TinyLoginForm.xsl"/>
    <xsl:include href="MoreSearchSources.xsl"/>

    <xsl:template match="EBOOK-SEARCH">
        <xsl:param name="SESSION-ID"/>
        <xsl:param name="NEWS">true</xsl:param>
        <xsl:param name="TIPS">true</xsl:param>

        <xsl:variable name="SELECTED-DB">131072</xsl:variable>

        <xsl:variable name="SELCOLS">
            <xsl:value-of select="//SESSION-DATA/COLLECTIONS"/>
        </xsl:variable>

        <xsl:variable name="DATABASE">
            <xsl:value-of select="$SELECTED-DB"/>
        </xsl:variable>

        <xsl:variable name="USERMASK">
            <xsl:value-of select="//USERMASK"/>
        </xsl:variable>

        <xsl:variable name="FRM">
            <xsl:value-of select="//CREDS"/>
        </xsl:variable>

        <xsl:variable name="PERSONALIZATION">
            <xsl:value-of select="/DOC/PERSONALIZATION"/>
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

        <xsl:variable name="SORT-OPTION"><xsl:value-of select="//SESSION-DATA/SORT-OPTION"/></xsl:variable>

        <!-- added Autostemming to XS -->
        <xsl:variable name="AUTOSTEMMING"><xsl:value-of select="//SESSION-DATA/AUTOSTEMMING"/></xsl:variable>

        <!-- START OF JAVA SCRIPT -->
        <SCRIPT LANGUAGE="JavaScript" SRC="/engresources/js/Login.js"/>
        <SCRIPT LANGUAGE="JavaScript" SRC="/engresources/js/ReferexSearch_V8.js"/>
        <SCRIPT LANGUAGE="JavaScript" SRC="/engresources/js/Robohelp.js"/>

        <!-- END OF JAVA SCRIPT -->

        <!-- Start of table for content below the navigation bar -->
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
            <tr>
                <td valign="top" width="140">

                    <xsl:if test="$NEWS='true'">
                        <!-- Start of left most table for news item -->
                        <table border="0" width="140" cellspacing="0" cellpadding="0">
                            <tr>
                                <td valign="top" height="15" bgcolor="#3173B5" colspan="3">
                                    <a class="LgWhiteText">
                                        <b>&#160; Referex</b>
                                    </a>
                                </td>
                            </tr>
                            <tr>
                                <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" border="0" width="1"/></td>
                                <td valign="top" bgcolor="#FFFFFF">
                                    <!-- Tiny table for news features -->
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td valign="top" height="5" colspan="3"><img src="/engresources/images/s.gif" border="0" height="5"/></td>
                                        </tr>
                                        <tr>
                                            <td valign="top" width="3"><img src="/engresources/images/s.gif" border="0" width="3"/></td>
                                            <td valign="top">
                                                <a class="SmBlackText">Referex Engineering is a specialized electronic reference product
                                                    that draws upon hundreds of premium engineering titles to provide engineering students and
                                                professionals with the answers and information they require at school, work, and in practice.</a>
                                            </td>
                                            <td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td>
                                        </tr>
                                    </table>
                                    <!-- End of Tiny table for news features -->
                                </td>
                                <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" border="0" width="1"/></td>
                            </tr>
                            <tr>
                                <td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/s.gif" border="0" height="1"/></td>
                            </tr>
                            <tr>
                                <td valign="top" colspan="3" height="20"><img src="/engresources/images/s.gif" border="0" height="20"/></td>
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
                            <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/s.gif" border="0" height="1"/></td></tr>

                            <tr><td valign="top" colspan="3" height="20"><img src="/engresources/images/s.gif" border="0" height="20"/></td></tr>
                            <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a class="LgWhiteText"><b>&#160;Search Tips</b></a></td></tr>
                            <tr>
                                <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" border="0" width="1"/></td>
                                <td valign="top" bgcolor="#FFFFFF">
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td valign="top" height="5" colspan="3">
                                                <img src="/engresources/images/s.gif" border="0" height="5"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" width="3">
                                                <img src="/engresources/images/s.gif" border="0" width="3"/>
                                            </td>
                                            <td valign="top">
                                                <a class="SmBlackText">Keyword is the default setting which searches the text of the eBook and returns sections of the eBook. Searching by author, ISBN, publisher, subject, or title, returns records for entire eBooks<br/>
                                                    <br/>To browse eBooks, simply click on a collection name or one of the subjects listed below them. The search results will contain a complete list of titles by collection or subject.
                                                </a>
                                            </td>
                                            <td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td>
                                        </tr>
                                    </table>
                                </td>
                                <td valign="top" bgcolor="#3173B5" width="1"><img src="/engresources/images/s.gif" border="0" width="1"/></td>
                            </tr>
                            <tr>
                                <td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/engresources/images/s.gif" border="0" height="1"/></td>
                            </tr>
                            <tr>
                                <td valign="top" colspan="3" height="20"><img src="/engresources/images/s.gif" border="0" height="20"/></td>
                            </tr>
                        </table>
                        <!-- End of left most table for new etc. -->
                    </xsl:if>

                </td>
                <td valign="top" bgcolor="#FFFFFF"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
                <td valign="top" width="100%">

                    <!-- Start of table for search form -->
                    <table border="0" width="100%" cellspacing="0" cellpadding="0">
                        <tr>
                            <td align="left" valign="top" bgcolor="#C3C8D1">
                                <!-- Start of search form -->
                                <FORM name="quicksearch" action="/controller/servlet/Controller" METHOD="POST" onSubmit="return searchValidation();" >
                                    <!-- This form is always used to search referex -->
                                    <input type="hidden" name="database" value="131072" />
                                    <input type="hidden" name="yearselect" value="yearrange" />
                                    <input type="hidden" name="CID" value="quickSearchCitationFormat" />
                                    <input type="hidden" name="searchtype" value="Book"/>
                                    <!-- Start of table for search form -->
                                    <table border="0" cellspacing="0" cellpadding="0" width="500" bgcolor="#C3C8D1">
                                        <tr>
                                            <td valign="top" width="100%" colspan="5" ><img src="/engresources/images/s.gif" height="20"/></td>
                                        </tr>
                                        <tr>
                                            <td align="left" valign="top" width="100%" colspan="5" >
                                                <!-- display REFEREX checkboxes -->
                                                <xsl:value-of disable-output-escaping="yes"  select="DP:referexCheckBoxes($FRM,$SELCOLS)"/>
                                                <!-- end of REFEREX checkboxes -->
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" width="100%" colspan="5" ><img src="/engresources/images/s.gif" height="8"/></td>
                                        </tr>
                                        <tr>
                                            <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                                            <td valign="top" width="233" colspan="2" ><a CLASS="SmBlueTableText"><b>SEARCH FOR</b></a></td>
                                            <td valign="top" width="233" colspan="2" ><a CLASS="SmBlueTableText"><b>SEARCH IN</b></a></td>
                                        </tr>
                                        <tr>
                                            <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                                            <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                                            <td valign="top" width="218">
                                                <a CLASS="MedBlackText">
                                                    <input type="text" name="searchWord1" size="29" >
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="$SEARCH-WORD-1"/>
                                                        </xsl:attribute>
                                                    </input>
                                                </a>
                                                &#160;
                                            </td>
                                            <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                                            <td valign="top" width="218">
                                                <a CLASS="MedBlackText">&#160;

                                                    <select size="1" name="section1">
                                                        <xsl:value-of disable-output-escaping="yes" select="SF:getOption($SEARCH-OPTION-1,$SELECTED-DB,'section')"/>
                                                    </select>

                                                </a>
                                                <a href="javascript:makeUrl('Referex_Search_Fields.htm')">
                                                    <img src="/engresources/images/blue_help1.gif" border="0"/>
                                                </a>
                                            </td>
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
                                                    <input type="text" name="searchWord2" size="17" >
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="$SEARCH-WORD-2"/>
                                                        </xsl:attribute>
                                                    </input>
                                                </a>
                                                &#160;
                                            </td>
                                            <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                                            <td valign="top" width="218">
                                                <a CLASS="MedBlackText">&#160;

                                                    <select size="1" name="section2">
                                                        <xsl:value-of disable-output-escaping="yes" select="SF:getOption($SEARCH-OPTION-2,$SELECTED-DB,'section')"/>
                                                    </select>

                                                </a>
                                            </td>
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
                                                    <input type="text" name="searchWord3" size="17" >
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="$SEARCH-WORD-3"/>
                                                        </xsl:attribute>
                                                    </input>
                                                </a>
                                                &#160;
                                            </td>
                                            <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                                            <td valign="top" width="218">
                                                <a CLASS="MedBlackText">&#160;

                                                    <select size="1" name="section3">
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
                                            <td valign="top" width="4" ><img src="/engresources/images/s.gif" width="4"/></td>
                                            <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                                            <td valign="top" width="233" >&#160;</td>
                                            <td valign="top" width="15" ><img src="/engresources/images/s.gif" width="15"/></td>
                                            <td valign="top" width="233" >
                                                <br/>
                                                <input type="image" src="/engresources/images/search_orange1.gif" name="search" value="Search" alt="Search" border="0"/>
                                                &#160; &#160;
                                                <a href="/controller/servlet/Controller?CID=ebookSearch&amp;database={$SELECTED-DB}">
                                                    <img src="/engresources/images/reset_orange1.gif" alt="Reset" border="0"/>
                                                </a>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" colspan="5" ><img src="/engresources/images/s.gif" height="4" /></td>
                                        </tr>
                                    </table>
                                    <!-- End of table for search form -->
                                </FORM>
                                <!-- end of search form -->
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" bgcolor="#C3C8D1"><a class="SmBlueTableText">&#160; BROWSE BOOKS BY COLLECTION OR SUBJECT</a></td>
                        </tr>
                        <tr>
                            <td valign="top" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" border="0" height="5"/></td>
                        </tr>
                        <tr>
                            <td align="center" valign="top" bgcolor="#C3C8D1">
                                <table border="0" width="97%" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td height="1" colspan="3" bgcolor="#148BA9">
                                            <img src="/engresources/images/s.gif" border="0" height="1"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td bgcolor="#148BA9"><img src="/engresources/images/s.gif" border="0" width="1"/></td>
                                        <td valign="top" width="100%">
                                            <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#F5F5F5">
                                                <tr height="30">
                                                    <td valign="top" width="4" bgcolor="#148BA9">
                                                        <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                    </td>

                                                    <xsl:for-each select="//EBOOK-SEARCH/*">
                                                        <xsl:variable name="curHead">
                                                            <xsl:value-of select="position()"/>
                                                        </xsl:variable>
                                                        <xsl:for-each select="HEAD">

                                                            <xsl:if test="$curHead &lt; 4">
                                                                <td valign="top" width="4" bgcolor="#148BA9">
                                                                    <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                </td>
                                                                <td valign="middle" bgcolor="#148BA9">
                                                                    <xsl:value-of disable-output-escaping="yes" select="."/>
                                                                </td>
                                                            </xsl:if>
                                                            <xsl:if test="count(//EBOOK-SEARCH/*) = 1 and $curHead = 1">
                                                                <td valign="top" width="4" bgcolor="#148BA9">
                                                                    <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                </td>
                                                                <td valign="middle" bgcolor="#148BA9">
                                                                </td>
                                                                <td valign="top" width="4" bgcolor="#148BA9">
                                                                    <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                </td>
                                                                <td valign="middle" bgcolor="#148BA9">
                                                                </td>
                                                                <td valign="top" width="4" bgcolor="#148BA9">
                                                                    <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                </td>
                                                                <td valign="middle" bgcolor="#148BA9">
                                                                </td>
                                                            </xsl:if>
                                                            <xsl:if test="count(//EBOOK-SEARCH/*) = 2 and $curHead = 2">
                                                                <td valign="top" width="4" bgcolor="#148BA9">
                                                                    <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                </td>
                                                                <td valign="middle" bgcolor="#148BA9">
                                                                </td>
                                                                <td valign="top" width="4" bgcolor="#148BA9">
                                                                    <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                </td>
                                                                <td valign="middle" bgcolor="#148BA9">
                                                                </td>
                                                            </xsl:if>
                                                            <xsl:if test="count(//EBOOK-SEARCH/*) >= 3 and $curHead = 3">
                                                                <td valign="top" width="4" bgcolor="#148BA9">
                                                                    <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                </td>
                                                                <td valign="middle" bgcolor="#148BA9">
                                                                </td>
                                                            </xsl:if>
                                                        </xsl:for-each>
                                                    </xsl:for-each>
                                                </tr>
                                                <tr>
                                                    <td valign="top" width="4"><img src="/engresources/images/s.gif" border="0" width="4"/></td>
                                                    <xsl:for-each select="//EBOOK-SEARCH/*">
                                                        <xsl:variable name="curPos">
                                                            <xsl:value-of select="position()"/>
                                                        </xsl:variable>
                                                        <xsl:for-each select="CVS">
                                                            <xsl:if test="$curPos &lt; 4">
                                                                <td valign="top" width="4"><img src="/engresources/images/s.gif" border="0" width="4"/></td>
                                                                <td valign="top">
                                                                    <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                                                        <tr>
                                                                            <td height="5">
                                                                                <img src="/engresources/images/s.gif" height="5"></img>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td valign="top">
                                                                                <xsl:value-of disable-output-escaping="yes" select="."/>
                                                                                <br/><xsl:value-of disable-output-escaping="yes" select="../BSTATE"/>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </xsl:if>
                                                        </xsl:for-each>
                                                    </xsl:for-each>
                                                </tr>
                                                <tr>
                                                    <td colspan="2" height="10"><img src="/engresources/images/s.gif" height="10"/></td>
                                                </tr>

                                                <!-- ADD BOTTOM ROW IF MORE THAN THREE COLLECTIONS -->
                                                <xsl:if test="count(//EBOOK-SEARCH/*) &gt; 3">
                                                    <tr height="30">
                                                        <td valign="top" width="4" bgcolor="#148BA9">
                                                            <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                        </td>
                                                        <xsl:for-each select="//EBOOK-SEARCH/*">
                                                            <xsl:variable name="curHead">
                                                                <xsl:value-of select="position()"/>
                                                            </xsl:variable>
                                                            <xsl:for-each select="HEAD">
                                                                <xsl:if test="$curHead &gt; 3">
                                                                    <td valign="top" width="4" bgcolor="#148BA9">
                                                                        <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                    </td>
                                                                    <td valign="middle" bgcolor="#148BA9">
                                                                        <xsl:value-of disable-output-escaping="yes" select="."/>
                                                                    </td>
                                                                </xsl:if>
                                                                <xsl:if test="count(//EBOOK-SEARCH/*) = 4 and $curHead = 4">
                                                                    <td valign="top" width="4" bgcolor="#148BA9">
                                                                        <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                    </td>
                                                                    <td valign="middle" bgcolor="#148BA9">
                                                                    </td>
                                                                    <td valign="top" width="4" bgcolor="#148BA9">
                                                                        <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                    </td>
                                                                    <td valign="middle" bgcolor="#148BA9">
                                                                    </td>
                                                                    <td valign="top" width="4" bgcolor="#148BA9">
                                                                        <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                    </td>
                                                                    <td valign="middle" bgcolor="#148BA9">
                                                                    </td>
                                                                </xsl:if>
                                                                <xsl:if test="count(//EBOOK-SEARCH/*) = 5 and $curHead = 5">
                                                                    <td valign="top" width="4" bgcolor="#148BA9">
                                                                        <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                    </td>
                                                                    <td valign="middle" bgcolor="#148BA9">
                                                                    </td>
                                                                    <td valign="top" width="4" bgcolor="#148BA9">
                                                                        <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                    </td>
                                                                    <td valign="middle" bgcolor="#148BA9">
                                                                    </td>
                                                                </xsl:if>
                                                                <xsl:if test="count(//EBOOK-SEARCH/*) >= 6 and $curHead = 6">
                                                                    <td valign="top" width="4" bgcolor="#148BA9">
                                                                        <img src="/engresources/images/s.gif" border="0" width="4"/>
                                                                    </td>
                                                                    <td valign="middle" bgcolor="#148BA9">
                                                                    </td>
                                                                </xsl:if>
                                                            </xsl:for-each>
                                                        </xsl:for-each>
                                                    </tr>
                                                    <tr>
                                                        <td valign="top" width="4"><img src="/engresources/images/s.gif" border="0" width="4"/></td>
                                                        <xsl:for-each select="//EBOOK-SEARCH/*">
                                                            <xsl:variable name="curPos">
                                                                <xsl:value-of select="position()"/>
                                                            </xsl:variable>
                                                            <xsl:for-each select="CVS">
                                                                <xsl:if test="$curPos &gt; 3">
                                                                    <td valign="top" width="4"><img src="/engresources/images/s.gif" border="0" width="4"/></td>
                                                                    <td valign="top">
                                                                        <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                                                            <tr>
                                                                                <td height="5">
                                                                                    <img src="/engresources/images/s.gif" height="5"></img>
                                                                                </td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td valign="top">
                                                                                    <xsl:value-of disable-output-escaping="yes" select="."/>
                                                                                    <br/><xsl:value-of disable-output-escaping="yes" select="../BSTATE"/>
                                                                                </td>
                                                                            </tr>
                                                                        </table>
                                                                    </td>
                                                                </xsl:if>
                                                            </xsl:for-each>
                                                        </xsl:for-each>
                                                    </tr>
                                                </xsl:if>
                                            </table>
                                        </td>
                                        <td width="1" bgcolor="#148BA9"><img src="/engresources/images/s.gif" border="0" width="1"/></td>
                                    </tr>
                                    <tr>
                                        <td height="1" colspan="3" bgcolor="#148BA9"><img src="/engresources/images/s.gif" border="0" height="1"/></td>
                                    </tr>
                                    <tr>
                                        <td colspan="3"><img src="/engresources/images/s.gif" border="0" height="10"/></td>
                                    </tr>
                                </table>

                            </td>
                        </tr>
                    </table>

                </td>
            </tr>
        </table>

    </xsl:template>

</xsl:stylesheet>