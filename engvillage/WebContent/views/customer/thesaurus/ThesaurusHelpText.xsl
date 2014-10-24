<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java html xsl">

<xsl:include href="../common/forms/TinyLoginForm.xsl"/>
<xsl:include href="../common/forms/MoreSearchSources.xsl"/>

<xsl:template name="THESAURUS-HELP-TEXT">

    <xsl:param name="ACTION"/>
    <xsl:param name="RESULT"/>

            <table border="0" width="140" cellspacing="0" cellpadding="0">
                <tr><td valign="top">
                    <!-- left most table for news items -->
                    <table border="0" width="140" cellspacing="0" cellpadding="0">
                        <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160;Thesaurus</b></a></td></tr>
                        <tr><td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
                            <td valign="top" bgcolor="#FFFFFF">
                            <!-- Tiny table for news features -->
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>


                                <xsl:choose>
                                    <xsl:when test="($ACTION='BROWSE')">
                                        <xsl:choose>
                                            <xsl:when test="($RESULT='Y')">
                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">To add terms to your search, click the box in the Select column.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Click on a hyperlinked term to display its thesaurus entry.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Terms in italics are lead-in terms that point to controlled vocabulary.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Terms with an asterisk are previously used terms that have been replaced by newer terms.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                            </xsl:when>
                                            <xsl:otherwise>
                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">To add terms to your search, click the box in the Select column</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Click on a hyperlinked term to display its thesaurus entry.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Terms in italics are lead-in terms that point to controlled vocabulary.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Terms with an asterisk are previously used terms that have been replaced by newer terms.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <xsl:when test="($ACTION='SEARCH')">
                                        <xsl:choose>
                                            <xsl:when test="($RESULT='Y')">
                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">To add terms to your search, click the box in the Select column.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Click on a hyperlinked term to display its thesaurus entry.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Terms in italics are lead-in terms that point to controlled vocabulary.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Terms with an asterisk are previously used terms that have been replaced by newer terms.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                            </xsl:when>
                                            <xsl:otherwise>
                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">If there are no results for your search term, the system will suggest possible matches.</a></td></tr>

                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Select the best match and click "Search again" or click a hyperlinked term to go to the thesaurus record for that term or re-enter the term in the upper text box and click "Submit."</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <xsl:when test="($ACTION='FULLREC')">
                                        <xsl:choose>
                                            <xsl:when test="($RESULT='Y')">
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Click on a hyperlinked term to display its thesaurus entry.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Terms in italics are lead-in terms that point to the controlled vocabulary.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Terms with an asterisk are previously used terms that have been replaced by newer terms.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">To add terms to your search, click the box in the Select column.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">To see the scope note for a term, click on the <img src="/static/images/i3.gif" border="0"/> icon.</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                            </xsl:when>
                                            <xsl:otherwise>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
                                                <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">If there are no results for your search term, the system will suggest possible matches. Click a hyperlinked term to go to the thesaurus record for that term or re-enter the term in the upper text box and click "Submit."</a></td></tr>
                                                <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                </xsl:choose>

                            </table>
                            <!-- End of Tiny table for news features -->
                            </td>
                            <td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
                        </tr>
                        <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
                    </table>
                    <!-- End of left most table for news item -->
                </td></tr>
    </table>

</xsl:template>


<xsl:template name="DEFAULT-HELP-TEXT">
    <xsl:param name="PERSONALIZATION">false</xsl:param>
    <xsl:param name="SELECTED-DB"/>
    <xsl:param name="TERMSEARCH"/>
<SCRIPT LANGUAGE="Javascript" SRC="/static/js/Login.js"/>


<!-- left most table for news items -->
    <table border="0" width="140" cellspacing="0" cellpadding="0">


    <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160;Thesaurus</b></a></td></tr>
        <tr><td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
            <td valign="top" bgcolor="#FFFFFF">
                        <!-- Tiny table for news features -->
    <table border="0" cellspacing="0" cellpadding="0">
        <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
        <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">The thesaurus function allows you to identify controlled vocabulary terms, find synonyms and related terms and improve your search strategy with suggested and narrower terms.</a></td><td valign="top" width="2"><img src="/static/images/spacer.gif" border="0" width="2"/></td></tr>
        <tr><td colspan="2" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
        <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a CLASS="SmBlackText">Controlled vocabulary terms are used to index articles. Since the thesauri have evolved over time, this function can be used to trace the usage of controlled terms. </a></td><td valign="top" width="2"><img src="/static/images/spacer.gif" border="0" width="2"/></td></tr>
        <tr><td colspan="2" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
        <tr>
            <td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td>
            <td valign="top" align="right">
             <a class="MedBlueLink" href="javascript:makeUrl('Select_Thesarus_.htm')">
	    <b>More</b></a></td>
            <td valign="top" width="2"><img src="/static/images/spacer.gif" border="0" width="2"/></td>
        </tr>
        <tr><td colspan="2" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
    </table>
    <!-- End of Tiny table for news features -->
        </td>
        <td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
    </tr>




        <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
        <tr><td valign="top" colspan="3" height="20"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>

            <!-- start of a second table for personal account -->
            <xsl:choose>
            <xsl:when test="($PERSONALIZATION='false')">
                <xsl:variable name="NEXTURL">CID=thesHome&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>

                <xsl:call-template name="SMALL-LOGIN-FORM">
                    <xsl:with-param name="DB" select="$SELECTED-DB"/>
                    <xsl:with-param name="NEXT-URL" select="$NEXTURL"/>
                </xsl:call-template>

            </xsl:when>
            </xsl:choose>
            <!-- End of Tiny table for Personal account -->

            <xsl:choose>
            <xsl:when test="($TERMSEARCH='true')">
            </xsl:when>
            <xsl:otherwise>
            <!-- start of a second table for remote dbs -->
            <tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160; More Search Sources</b></a></td></tr>
            <tr><td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/s.gif" border="0" width="1" /></td>
                <td valign="top" bgcolor="#FFFFFF">
                    <xsl:call-template name="REMOTE-DBS">
                        <xsl:with-param name="SEARCHTYPE">quick</xsl:with-param>
                    </xsl:call-template>
                </td>
                <td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/s.gif" border="0" width="1"/></td></tr>
            <tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/static/images/s.gif" border="0" height="1"/></td></tr>
            </xsl:otherwise>
            </xsl:choose>
        </table>
    <!-- End of left most table for news item -->
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/RemoteDbLink_V5.js"/>


</xsl:template>



<xsl:template name="THESAURUS-SEARCH-TIPS">

    <tr><td valign="top" colspan="4">
    <!-- Start of table for help tips -->
    <table border="0" width="100%" cellspacing="0" cellpadding="0">
    <tr><td valign="top" width="100%">
    <!-- table for Help tips -->
        <table border="0" width="100%" cellspacing="0" cellpadding="0">
            <tr><td valign="top" colspan="3" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
            <tr>
                <td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
                <td valign="top">
                    <table border="0" width="100%" cellspacing="0" cellpadding="0">
                        <tr><td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
                        <tr><td valign="top" height="10" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160;Search Tips</b></a></td></tr>
                        <tr><td valign="top" height="15" colspan="3"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
                        <tr><td valign="top" width="4"><img src="/static/images/spacer.gif" width="4" border="0"/></td><td valign="top"><a CLASS="MedBlackText">

                            Use "Search" to display controlled vocabulary terms that contain the term that you are searching for as well as broader, narrower and related terms. For example, searching for light rail will retrieve Light rail transit, monorails, railroads, rapid transit, subways, trackless trolleys, trolley cars and urban planning.<P/>
                            Use "Exact Term" if you know a controlled vocabulary term and want go directly to its thesaurus entry which contains broader, narrower and related terms as well as scope notes, prior terms and lead-in terms.<P/>
                            Use "Browse" to scan the thesaurus alphabetically.<P/>
                            All terms have hyperlinks that point to the thesaurus entry.<P/>
                            Clicking a select box will move a term to the Search Box where it can be used to perform a database search using the Boolean operators AND or OR along with Engineering Village Quick Search limits. All controlled terms present in the database have a Select box. Lead-in terms that have never been used as controlled vocabulary terms can not be selected.<P/>

                            </a>
                        </td></tr>
                        <tr><td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
                    </table>
                </td>
                <td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
            </tr>
            <tr><td valign="top" height="8" colspan="3"><img src="/static/images/spacer.gif" border="0" height="8"/></td></tr>
        </table>
        <!-- End of table for Help tips -->
    </td></tr>
    </table>
    </td></tr>

</xsl:template>


<xsl:template name="THESAURUS-REFINE-SEARCH-TIPS">

    <tr><td valign="top" colspan="4">
    <!-- Start of table for help tips -->
    <table border="0" width="100%" cellspacing="0" cellpadding="0">
    <tr><td valign="top" width="100%">
    <!-- table for Help tips -->
        <table border="0" width="100%" cellspacing="0" cellpadding="0">
            <tr><td valign="top" colspan="3" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
            <tr>
                <td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
                <td valign="top">
                    <table border="0" width="100%" cellspacing="0" cellpadding="0">
                        <tr><td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
                        <tr><td valign="top" height="10" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160;Search Tips</b></a></td></tr>
                        <tr><td valign="top" height="15" colspan="3"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
                        <tr><td valign="top" width="4"><img src="/static/images/spacer.gif" width="4" border="0"/></td><td valign="top"><a CLASS="MedBlackText">

                            Use the refine feature to change the terms in the Search Box, limit the results or change the sort order of the database output.<P/>
                            Highlight and remove any term(s) you wish to eliminate from the Search Box.<P/>
                            Use the upper search box to find additional terms to add to your search. Terms selected will be added to terms currently in the Search Box.<P/>
                            Use the limits to change the scope of the search. Use the "Sort By" option to change the order of the search results.<P/>

                            </a>
                        </td></tr>
                        <tr><td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
                    </table>
                </td>
                <td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
            </tr>
            <tr><td valign="top" height="8" colspan="3"><img src="/static/images/spacer.gif" border="0" height="8"/></td></tr>
        </table>
        <!-- End of table for Help tips -->
    </td></tr>
    </table>
    </td></tr>

</xsl:template>




</xsl:stylesheet>