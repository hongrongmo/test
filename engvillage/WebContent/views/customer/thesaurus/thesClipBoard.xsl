<?xml version="1.0"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java html xsl"
>

<xsl:template match="CLIPBOARD" name="THESAURUS-REFINE-SEARCH-FORM">


    <xsl:variable name="CONTEXT">
        <xsl:value-of select="//CONTEXT"/>
    </xsl:variable>

    <xsl:variable name="QUERY-ID">
        <xsl:value-of select="//SESSION-DATA/QUERY-ID"/>
    </xsl:variable>

    <xsl:variable name="START-YEAR"><xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:variable>
    <xsl:variable name="END-YEAR"><xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:variable>

    <xsl:variable name="LASTFOURUPDATES">
        <xsl:choose>
            <xsl:when test="//SESSION-DATA/LASTFOURUPDATES"><xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/></xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="DOCUMENT-TYPE">
        <xsl:choose>
            <xsl:when test="//SESSION-DATA/DOCUMENT-TYPE"><xsl:value-of select="//SESSION-DATA/DOCUMENT-TYPE"/></xsl:when>
            <xsl:otherwise>NO-LIMIT</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="TREATMENT-TYPE">
        <xsl:choose>
            <xsl:when test="//SESSION-DATA/TREATMENT-TYPE"><xsl:value-of select="//SESSION-DATA/TREATMENT-TYPE"/></xsl:when>
            <xsl:otherwise>NO-LIMIT</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="DISCIPLINE-TYPE">
        <xsl:choose>
            <xsl:when test="//SESSION-DATA/DISCIPLINE-TYPE"><xsl:value-of select="//SESSION-DATA/DISCIPLINE-TYPE"/></xsl:when>
            <xsl:otherwise>NO-LIMIT</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="LANGUAGE">
        <xsl:choose>
            <xsl:when test="//SESSION-DATA/LANGUAGE"><xsl:value-of select="//SESSION-DATA/LANGUAGE"/></xsl:when>
            <xsl:otherwise>NO-LIMIT</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="SORT-OPTION">
        <xsl:choose>
            <xsl:when test="//SESSION-DATA/SORT-OPTION"><xsl:value-of select="//SESSION-DATA/SORT-OPTION"/></xsl:when>
            <xsl:otherwise>Relevance</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="COMBINE-TERMS">
            <xsl:value-of select="/*/CVS/@CONNECTOR"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
        <xsl:choose>
            <xsl:when test="//SESSION-DATA/DATABASE-MASK"><xsl:value-of select="//SESSION-DATA/DATABASE-MASK"/></xsl:when>
            <xsl:otherwise>1</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
<html>
<head>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/ThesaurusForm_V7.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Robohelp.js"/>

</head>

<body topmargin="0" marginwidth="0" marginheight="0">
<center>

<table border="0" cellspacing="0" cellpadding="0" width="99%">
    <tr>

        <xsl:choose>
            <xsl:when test="($CONTEXT = 'T')">
                <td valign="top" width="150">
                    <table border="0" width="150" cellspacing="0" cellpadding="0">
                    <tr>
                        <td valign="top" height="1">
                            <img src="/static/images/spacer.gif" border="0" width="150" height="1"/>

                        </td>
                    </tr>
                    </table>
                </td>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="($CONTEXT != 'T')">
                <td valign="top" width="70">
                    <table border="0" width="30" cellspacing="0" cellpadding="0">
                    <tr>
                        <td valign="top" height="1">
                            <img src="/static/images/spacer.gif" border="0" width="30" height="1"/>

                        </td>
                    </tr>
                    </table>
                </td>
            </xsl:when>
        </xsl:choose>


        <td align="center" valign="top" width="100%" bgcolor="#C3C8D1">

        <FORM NAME="clipboard" OnSubmit="javascript:return runValidate();" METHOD="POST" TARGET="_top" ACTION="/controller/servlet/Controller?CID=thesSearchCitationFormat">
            <table border="0" cellspacing="0" cellpadding="0" width="99%">
                <tr>
                    <td valign="top" colspan="9"><img src="/static/images/spacer.gif" border="0" height="4"/></td>
                </tr>
                <tr>
                    <td valign="top"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" width="25%" colspan="2"><a CLASS="SmBlueTableText"><b>LIMIT BY</b></a></td>
                    <td valign="top"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" width="25%" colspan="2"><a CLASS="SmBlueTableText"><b>SEARCH BOX</b></a></td>
                    <td valign="top"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" width="50%" colspan="2"><a CLASS="SmBlueTableText"><b>COMBINE SEARCH WITH</b></a></td>
                </tr>
                <tr>
                    <td valign="top" width="4"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" width="4"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top">

                        <!-- doctype -->
                        <a CLASS="MedBlackText">
                            <select name="doctype" onchange="checkPatent(this.form);">
                                <xsl:for-each select="//LIMITERS/CONSTRAINED-FIELD[@SHORTNAME='DT']/OPTIONS/OPTION">
                                    <xsl:choose>
                                        <xsl:when test="(@SHORTNAME=$DOCUMENT-TYPE)">
                                            <option selected="selected">
                                                <xsl:attribute name="value"><xsl:value-of select="@SHORTNAME"/></xsl:attribute>
                                                <xsl:value-of select="@DISPLAYNAME"/>
                                            </option>
                                        </xsl:when>
                                        <xsl:when test="not(@SHORTNAME=$DOCUMENT-TYPE)">
                                            <option>
                                                <xsl:attribute name="value"><xsl:value-of select="@SHORTNAME"/></xsl:attribute>
                                                <xsl:value-of select="@DISPLAYNAME"/>
                                            </option>
                                        </xsl:when>
                                    </xsl:choose>
                                </xsl:for-each>
                            </select>
                        </a>
			<a href="javascript:makeUrl('Limits.htm')">
			    <img src="/static/images/blue_help1.gif" border="0"/>
			 </a>
                    </td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" rowspan="10">

                            <xsl:call-template name="CVS"/>

                    </td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top">


                        <xsl:choose>
                            <xsl:when test="($COMBINE-TERMS='or')">
                                <input type="radio" name="andor" value="AND" /><a CLASS="MedBlackText">AND</a>&#160;
                                <input type="radio" name="andor" value="OR" checked="true"/><a CLASS="MedBlackText">OR</a>
                            </xsl:when>
                            <xsl:when test="($COMBINE-TERMS='and')">
                                <input type="radio" name="andor" value="AND" checked="true" /><a CLASS="MedBlackText">AND</a>&#160;
                                <input type="radio" name="andor" value="OR" /><a CLASS="MedBlackText">OR</a>
                            </xsl:when>
                            <xsl:otherwise>
                                <input type="radio" name="andor" value="AND" /><a CLASS="MedBlackText">AND</a>&#160;
                                <input type="radio" name="andor" value="OR" checked="true"/><a CLASS="MedBlackText">OR</a>
                            </xsl:otherwise>
                        </xsl:choose>

                    </td>
                </tr>
                <tr>
                    <td valign="top" colspan="5"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" colspan="3"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                </tr>



                <tr>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top">

                        <!-- treatment Type -->


                        <select name="treatmentType">
                            <xsl:for-each select="//LIMITERS/CONSTRAINED-FIELD[@SHORTNAME='TR']/OPTIONS/OPTION">
                                <xsl:variable name="DISPLAY-NAME">
                                    <xsl:value-of select="@DISPLAYNAME"/>
                                </xsl:variable>
                                <xsl:variable name="SHORT-NAME">
                                    <xsl:value-of select="@SHORTNAME"/>
                                </xsl:variable>
                                <xsl:choose>
                                    <xsl:when test="($SHORT-NAME=$TREATMENT-TYPE)">
                                        <option selected="selected">
                                            <xsl:attribute name="value"><xsl:value-of select="$SHORT-NAME"/></xsl:attribute>
                                            <xsl:value-of select="$DISPLAY-NAME"/>
                                        </option>
                                    </xsl:when>
                                    <xsl:when test="not($SHORT-NAME=$TREATMENT-TYPE)">
                                        <option>
                                            <xsl:attribute name="value"><xsl:value-of select="$SHORT-NAME"/></xsl:attribute>
                                            <xsl:value-of select="$DISPLAY-NAME"/>
                                        </option>
                                    </xsl:when>
                                </xsl:choose>
                            </xsl:for-each>
                        </select>
			<a href="javascript:makeUrl('Limits.htm')"><img src="/static/images/blue_help1.gif" border="0"/></a>
                    </td>

                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>

                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" rowspan="3">
                        <!-- sort option -->
                        <a CLASS="SmBlueTableText"><b>SORT BY</b></a>
                        <br/>
                            <!-- jam - "Turkey" added context sensitive help icon to relevance -->
                            <input type="radio" name="sort" value="relevance">
                              <xsl:if test="($SORT-OPTION='relevance') or not(($SORT-OPTION='publicationYear') or ($SORT-OPTION='yr'))">
                                <xsl:attribute name="checked"/>
                              </xsl:if>
                            </input>
                            <a CLASS="SmBlackText">Relevance</a>&#160;
			    <a href="javascript:makeUrl('Sorting_from_the_Search_Form.htm')">
			         <img src="/static/images/blue_help1.gif" border="0"/>
			    </a>
                            <input type="radio" name="sort" value="yr">
                              <xsl:if test="($SORT-OPTION='publicationYear') or ($SORT-OPTION='yr')">
                                <xsl:attribute name="checked"/>
                              </xsl:if>
                            </input>
                            <a CLASS="SmBlackText">Publication year</a>
                    </td>
                </tr>
                <tr>
                    <td valign="top" colspan="5"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" colspan="2"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                </tr>

                <!-- discipline Type -->
                <xsl:if test="//LIMITERS/CONSTRAINED-FIELD[@SHORTNAME='DI']">
                    <tr>
                        <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                        <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                        <td valign="top">
                            <a CLASS="MedBlackText">
                            <select name="disciplinetype">
                                <xsl:for-each select="//LIMITERS/CONSTRAINED-FIELD[@SHORTNAME='DI']/OPTIONS/OPTION">
                                    <xsl:variable name="DISPLAY-NAME">
                                        <xsl:value-of select="@DISPLAYNAME"/>
                                    </xsl:variable>
                                    <xsl:variable name="SHORT-NAME">
                                        <xsl:value-of select="@SHORTNAME"/>
                                    </xsl:variable>
                                    <xsl:choose>
                                        <xsl:when test="($SHORT-NAME=$DISCIPLINE-TYPE)">
                                            <option selected="selected">
                                            <xsl:attribute name="value"><xsl:value-of select="$SHORT-NAME"/></xsl:attribute>
                                            <xsl:value-of select="$DISPLAY-NAME"/>
                                            </option>
                                        </xsl:when>
                                        <xsl:when test="not($SHORT-NAME=$DISCIPLINE-TYPE)">
                                            <option>
                                            <xsl:attribute name="value"><xsl:value-of select="$SHORT-NAME"/></xsl:attribute>
                                            <xsl:value-of select="$DISPLAY-NAME"/>
                                            </option>
                                        </xsl:when>
                                    </xsl:choose>
                                </xsl:for-each>
                            </select>
                            </a>
			  <a href="javascript:makeUrl('Limits.htm')">
			    <img src="/static/images/blue_help1.gif" border="0"/>
			    </a>

                        </td>
                        <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                        <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>

                        <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                        <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                        <td valign="top" >&#160;</td>
                    </tr>
                    <tr>
                        <td valign="top" colspan="5"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                        <td valign="top" colspan="3"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    </tr>
                </xsl:if>

                <tr>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top">


                        <!-- language -->
                        <select name="language">
                            <xsl:for-each select="//LIMITERS/CONSTRAINED-FIELD[@SHORTNAME='LA']/OPTIONS/OPTION">
                                <xsl:variable name="DISPLAY-NAME">
                                    <xsl:value-of select="@DISPLAYNAME"/>
                                </xsl:variable>
                                <xsl:variable name="SHORT-NAME">
                                    <xsl:value-of select="@SHORTNAME"/>
                                </xsl:variable>
                                <xsl:choose>
                                    <xsl:when test="($SHORT-NAME=$LANGUAGE)">
                                        <option selected="selected">
                                            <xsl:attribute name="value"><xsl:value-of select="$SHORT-NAME"/></xsl:attribute>
                                            <xsl:value-of select="$DISPLAY-NAME"/>
                                        </option>
                                    </xsl:when>
                                    <xsl:when test="not($SHORT-NAME=$LANGUAGE)">
                                        <option>
                                            <xsl:attribute name="value"><xsl:value-of select="$SHORT-NAME"/></xsl:attribute>
                                            <xsl:value-of select="$DISPLAY-NAME"/>
                                        </option>
                                    </xsl:when>
                                </xsl:choose>
                            </xsl:for-each>
                        </select>

                    </td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>

                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>

                </tr>
                <tr>
                    <td valign="top" colspan="5"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" colspan="3"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                </tr>
                <tr>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" >
                        <!-- yearrange - last four updates -->

            <a CLASS="MedBlackText">
            <input type="radio" name="yearselect" value="yearrange" >
            <xsl:if test="not(number($LASTFOURUPDATES))">
                <xsl:attribute name="checked"/>
            </xsl:if>
            </input>&#160;
            <select NAME="startYear" onchange="document.clipboard.yearselect[0].checked=true">
              <xsl:for-each select="//LIMITERS/CONSTRAINED-FIELD[@SHORTNAME='YR']/OPTIONS/OPTION">
                <OPTION>
                  <xsl:if test="(@SHORTNAME=$START-YEAR)">
                    <xsl:attribute name="selected"/>
                  </xsl:if>
                  <xsl:attribute name="value"><xsl:value-of select="@SHORTNAME"/></xsl:attribute>
                  <xsl:value-of select="@DISPLAYNAME"/>
                </OPTION>
              </xsl:for-each>
            </select>
            </a>
            <a CLASS="SmBlueTableText"> TO </a>
            <a CLASS="MedBlackText">
            <select NAME="endYear" onchange="document.clipboard.yearselect[0].checked=true">
              <xsl:for-each select="//LIMITERS/CONSTRAINED-FIELD[@SHORTNAME='YR']/OPTIONS/OPTION">
                <OPTION>
                  <xsl:if test="(@SHORTNAME=$END-YEAR)">
                    <xsl:attribute name="selected"/>
                  </xsl:if>
                  <xsl:attribute name="value"><xsl:value-of select="@SHORTNAME"/></xsl:attribute>
                  <xsl:value-of select="@DISPLAYNAME"/>
                </OPTION>
              </xsl:for-each>
            </select>
            </a>
            <br/>
            <input type="radio" name="yearselect" value="lastupdate">
            <xsl:if test="number($LASTFOURUPDATES)">
                <xsl:attribute name="checked"/>
            </xsl:if>&#160;
            <select name="updatesNo" onchange="document.clipboard.yearselect[1].checked=true">
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
            <a class="SmBlackText">&#160;Updates </a>
		<a href="javascript:makeUrl('Date_Limits.htm')">
		    <img src="/static/images/blue_help1.gif" border="0"/>
		    </a>
                        <!-- end of yearrange - last four updates -->
                    </td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>

                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" >
                        <INPUT type="image" src="/static/images/search_yellow.gif" border="0"/>
                        <xsl:choose>
                            <xsl:when test="($CONTEXT = 'T')">
                                &#160; <a href="javascript:resetForm()"><img src="/static/images/reset_yellow.gif" border="0"/></a>
                            </xsl:when>
                        </xsl:choose>
            &#160; <a href="javascript:removeSelected()"><img src="/static/images/remove_yellow.gif" border="0"/></a>
                        <xsl:choose>
                            <xsl:when test="($CONTEXT != 'T')">
                                &#160; <a href="/controller/servlet/Controller?CID=thesFrameSet&amp;searchid={$QUERY-ID}&amp;database={$DATABASE}"><img src="/static/images/thesrefine.gif" border="0"/></a>
                            </xsl:when>
                        </xsl:choose>
                    </td>
                </tr>
                <tr>
                    <td valign="top" colspan="5"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                    <td valign="top" colspan="3"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                </tr>
                <tr>
                    <td valign="top" colspan="9"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                </tr>
            </table>

            <INPUT TYPE="HIDDEN" NAME="searchType" Value="Thesaurus"/>
            <INPUT TYPE="HIDDEN" NAME="database" Value="{$DATABASE}"/>
            <INPUT TYPE="HIDDEN" NAME="searchWord1" Value=""/>

        </FORM>

        </td>
        <xsl:choose>
            <xsl:when test="($CONTEXT != 'T')">
                <td valign="top" width="70">
                    <table border="0" width="30" cellspacing="0" cellpadding="0">
                    <tr>
                        <td valign="top" height="1">
                            <img src="/static/images/spacer.gif" border="0" width="30" height="1"/>

                        </td>
                    </tr>
                    </table>
                </td>
            </xsl:when>
        </xsl:choose>

    </tr>
</table>

    <xsl:if test="($CONTEXT = 'T')">
        <BR/>

        <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="0" BORDER="0">
        <TR><TD>
            <CENTER>

                <A CLASS="MedBlueLink" TARGET="_parent" HREF="/controller/servlet/Controller?CID=aboutEI&amp;database=Compendex">About Ei</A>
                <A CLASS="SmBlackText">&#160; - &#160;</A>
                <A CLASS="MedBlueLink" TARGET="_parent" HREF="/controller/servlet/Controller?CID=aboutEV&amp;database=Compendex">About Engineering Village</A>
                <A CLASS="SmBlackText">&#160; - &#160;</A>
                <A CLASS="MedBlueLink" TARGET="_parent" HREF="/controller/servlet/Controller?CID=feedback&amp;database=Compendex">Feedback</A>
                <A CLASS="SmBlackText">&#160; - &#160;</A>
                <A CLASS="MedBlueLink" TARGET="_parent" HREF="/controller/servlet/Controller?CID=privacyPolicy&amp;database=Compendex">Privacy Policy</A>
                <br/>

            <A CLASS="SmBlackText">&#169; 2014 Elsevier Inc. All rights reserved.</A>
            </CENTER>
        </TD></TR>
        </TABLE>

    </xsl:if>
</center>
</body>
</html>

</xsl:template>

    <xsl:template name="CVS">

        <select name="clip" multiple="true" size="8">

            <xsl:for-each select="/*/CVS">
                <xsl:apply-templates />
            </xsl:for-each>

            <option value="1"></option>
            <option value="2"></option>
            <option value="3"></option>
            <option value="4"></option>
            <option value="5"></option>
            <option value="6"></option>
            <option value="7"></option>
            <option value="8"></option>
            <option value="1"></option>
            <option value="2"></option>
            <option value="3"></option>
            <option value="4"></option>
            <option value="5"></option>
            <option value="6"></option>
            <option value="7"></option>
            <option value="1"></option>
            <option value="2"></option>
            <option value="3"></option>
            <option value="4"></option>
            <option value="5"></option>
            <option value="6"></option>
            <option value="7"></option>
            <option value="1"></option>
            <option value="2"></option>
            <option value="3"></option>
            <option value="4"></option>
            <option value="5"></option>
            <option value="6"></option>
            <option value="7"></option>
            <option value="1"></option>
            <option value="2"></option>
            <option value="3"></option>
            <option value="4"></option>
            <option value="5"></option>
            <option value="6"></option>
            <option value="7"></option>
            <option value="8"></option>
            <option value="8"></option>
            <option value="8"></option>
            <option value="8"></option>
            <option value="1"></option>
            <option value="2"></option>
            <option value="3"></option>
            <option value="4"></option>
            <option value="5"></option>
            <option value="6"></option>
            <option value="7"></option>
            <option value="8"></option>
            <option value="9">____________________________________________________</option>
        </select>
    </xsl:template>

    <xsl:template match="CV">
        <option>
            <xsl:attribute name="value">
                <xsl:value-of select="java:encode(.)"/>
            </xsl:attribute>
        <xsl:value-of select="."/>
        </option>
    </xsl:template>

</xsl:stylesheet>