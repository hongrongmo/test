<?xml version="1.0"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java xsl html"
>
  <xsl:include href="../common/forms/MoreSearchSources.xsl"/>

  <xsl:template match="USPTO-EXPERT-SEARCH">
    <xsl:param name="SESSION-ID"/>
    <xsl:param name="SELECTED-DB"/>
    <xsl:param name="NEWS">true</xsl:param>
    <xsl:param name="TIPS">true</xsl:param>

    <xsl:variable name="SEARCH-WORD-1">
        <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-PHRASE-1"/>
    </xsl:variable>

    <xsl:variable name="START-YEAR">
        <xsl:value-of select="//SESSION-DATA/START-YEAR"/>
    </xsl:variable>

    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/ExpertSearchForm_V16.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/RemoteDbLink_V5.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Robohelp.js"/>

      <xsl:text disable-output-escaping="yes">

          <![CDATA[
              <xsl:comment>
                  <script language="javascript">

            // function for resetting to default values
                      function doReset() {

              document.quicksearch.searchWord1.value="";
              document.quicksearch.yearRange[0].selected=true;

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
                        <td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a class="LgWhiteText"><b>&#160; USPTO</b></a></td>
                    </tr>
                    <tr>
                        <td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/spacer.gif" border="0" width="1" /></td>
                        <td valign="top" bgcolor="#FFFFFF">
                            <!-- Tiny table for news features -->
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5" /></td>
                                </tr>
                                <tr>
                                <td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3" /></td>
                                <td valign="top"><a class="SmBlackText">Search over six million records from the United States Patent and Trademark Office.  Patents are fully searchable from January 1, 1976 to date.</a> </td>
                                <td valign="top" width="2"><img src="/static/images/spacer.gif" border="0" width="2" /></td>
                                </tr>
                                <tr>
                                <td valign="top" colspan="3" height="6"><img src="/static/images/spacer.gif" border="0" height="6"/></td>
                                </tr>
                                <tr>
                                <td valign="top" width="3">
                                  <img src="/static/images/spacer.gif" border="0" width="3"/>
                                </td>
                                <td valign="top" align="right">
				<a class="MedBlueLink" href="javascript:makeUrl('USPTO_patent_search.htm')">
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
                            <xsl:with-param name="LOCATION">USPTO</xsl:with-param>
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
        <td valign="top" width="10" bgcolor="#FFFFFF"><img src="/static/images/spacer.gif" border="0" width="10" /></td>
        <td valign="top" >

            <!-- Start of table for search form -->
            <table border="0" align="right" width="100%" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top" bgcolor="#C3C8D1">
                    <form name="quicksearch" action="/controller/servlet/Controller?CID=usptoExpert" method="post" onSubmit="return searchValidation();">
                    <INPUT type="HIDDEN" name="database" value="{$SELECTED-DB}"/>

                    <table border="0" cellspacing="0" cellpadding="0" width="470" bgcolor="#C3C8D1">
                      <tr>
                        <td valign="top" colspan="5"><img src="/static/images/spacer.gif" border="0" height="20" /></td>
                      </tr>
                      <tr>
                        <td valign="top" width="4" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
                        <td valign="top" colspan="4" bgcolor="#C3C8D1"><a class="MedBlackText"><b>USPTO</b></a></td>
                      </tr>
                      <tr>
                        <td valign="top" colspan="5" bgcolor="#C3C8D1" height="8"><img src="/static/images/spacer.gif" border="0" height="8" /></td>
                      </tr>

                      <tr>
                        <td valign="top" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" width="4" /></td>
                        <td valign="top" colspan="2" bgcolor="#C3C8D1"><a class="SmBlueTableText"><b>ENTER SEARCH TERMS BELOW</b></a>
                        </td>
                        <td valign="top" colspan="2" bgcolor="#C3C8D1">&#160;</td>
                      </tr>
                      <tr>
                        <td valign="top" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" width="4" /></td>
                        <td valign="top" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" width="15" /></td>
                        <!-- get search word 1 -->
                        <td valign="top" colspan="3" bgcolor="#C3C8D1">
                          <textarea rows="4" cols="35" wrap="PHYSICAL" NAME="searchWord1">
                            <xsl:value-of select="$SEARCH-WORD-1"/>
                          </textarea>
			    <a href="javascript:makeUrl('USPTO_patent_search.htm')">
			    <img src="/static/images/blue_help1.gif" border="0"/>
			    </a>                            
                        </td>
                      </tr>
                      <tr>
                        <td valign="top" colspan="5" height="4" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" height="4" /></td>
                      </tr>
                      <tr>
                        <td valign="top" colspan="5" height="4"><img src="/static/images/spacer.gif" border="0" height="4" /></td>
                      </tr>
                      <tr bgcolor="#C3C8D1">
                        <td valign="top" width="4" ><img src="/static/images/spacer.gif" border="0" width="4" /></td>
                        <td valign="top" colspan="4" ><a class="SmBlueTableText"><b>LIMIT BY</b></a></td>
                      </tr>
                      <tr bgcolor="#C3C8D1">
                        <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="4" /></td>
                        <td valign="top" ><img src="/static/images/spacer.gif" border="0" width="15" /></td>
                        <td valign="top" >
                          <a class="SmBlackText">
                          <select name="yearRange">
                            <xsl:for-each select="//LIMITERS/CONSTRAINED-FIELD/YR-OPTIONS/OPTION">
                              <OPTION>
                              <xsl:if test="(@NAME=$START-YEAR)">
                                <xsl:attribute name="selected"/>
                              </xsl:if>
                              <xsl:attribute name="value"><xsl:value-of select="@NAME"/></xsl:attribute>
                              <xsl:value-of select="@VALUE"/>
                              </OPTION>
                            </xsl:for-each>
                            </select>
                          </a>
                        </td>
                        <td valign="bottom"><a CLASS="MedBlackText" onClick="return searchValidation()"><input type="image" src="/static/images/search_orange1.gif" name="search" value="Search" border="0"/></a> &#160;<a href="javascript:doReset()"><img src="/static/images/reset_orange1.gif" border="0" /></a></td>
                      </tr>
                    </table>
                    </form>
              <!-- end of search form -->
                </td>
                <td valign="top" width="10" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" width="10" /></td>
                <td valign="top" align="right" bgcolor="#C3C8D1">
                    <table border="0" width="120" align="right" cellspacing="0" cellpadding="0">
                        <tr><td>&#160;</td></tr>
                    </table>
                </td>
                <td valign="top" width="10" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" width="10" /></td>
            </tr>
            <tr>
                <td valgn="top" colspan="4" height="15" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" height="15"/></td>
            </tr>
            <tr>
                <td valign="top" colspan="4" bgcolor="#C3C8D1">
                    <center>
                    <table border="0" width="90%" cellspacing="0" cellpadding="0" align="middle" >
                        <tr>
                            <td valign="top" width="1" bgcolor="#000000"><img src="/static/images/s.gif" width="1"/></td>
                            <td valign="top" >

                                <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
                                    <tr>
                                        <td valign="top" bgcolor="#000000" height="1" colspan="3"><img src="/static/images/spacer.gif" border="0" height="1"/></td>
                                    </tr>
                                    <tr>
                                        <td valign="top" height="10" bgcolor="#8294B4" colspan="3"><a CLASS="LgWhiteText"><b>&#160; Search Codes</b></a> &#160;
					<a href="javascript:makeUrl('USPTO_patent_search.htm')">
					    	<img src="/static/images/blue_help2.gif" border="0"/>
                                        </a></td>
                                    </tr>
                                    <tr>
                                        <td valign="top" bgcolor="#000000" height="1" colspan="3"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
                                    <tr>
                                        <td valign="top" width="4"><img src="/static/images/spacer.gif" width="4" border="0"/></td>
                                        <td valign="top" width="100%">
                                            <!-- TABLE TO PUT THE TREATMENT CODES IN 2 COLUMNS -->
                                            <table border="0" width="100%" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td valign="top" width="70"><a CLASS="MedBlackText"><b><u>Field</u></b></a></td>
                                                    <td valign="top" width="10" align="left"><a CLASS="MedBlackText"><b><u>Code</u></b></a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="20" height="1"><img src="/static/images/spacer.gif" border="0" height="1" width="20"/></td>
                                                    <td valign="top"><a CLASS="MedBlackText"><b><u>Field</u></b></a></td>
                                                    <td valign="top"><a CLASS="MedBlackText"><b><u>Code</u></b></a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="20" height="1"><img src="/static/images/spacer.gif" border="0" height="1" width="20"/></td>
                                                    <td valign="top"><a CLASS="MedBlackText"><b><u>Field</u></b></a></td>
                                                    <td valign="top"><a CLASS="MedBlackText"><b><u>Code</u></b></a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top" colspan="5" height="4"><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">All fields</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">All</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="20" height="1"><img src="/static/images/spacer.gif" border="0" width="20" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Assignee country</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">ACN</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="20" height="1"><img src="/static/images/spacer.gif" border="0" width="20" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Attorney/Agent</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">LREP</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Title</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">TI</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="20" height="1"><img src="/static/images/spacer.gif" border="0" height="1" width="24"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Internat'l classification</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">ICL</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">PCT information</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">PCT</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Abstract</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">AB</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">US classification</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">CCL</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Foreign priority</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">PRIR</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Issue date</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">SD</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Primary examiner</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">EXP</a></td><td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Reissue data</a></td><td valign="top"><a CLASS="SmBlackText">REIS</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Patent number</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">PT</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Assistant examiner</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">EXA</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Related US app. information</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">RLAP</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Application date</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">AP</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Inventor name</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">AU</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">US references</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">REF</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Application serial number</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">APN</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Inventor city</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">IC</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="20" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Foreign references</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">FREF</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Application type</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">APT</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Inventor state</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">IS</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="20" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Other references</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">OREF</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Assignee name</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">AF</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Inventor country</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">ICN</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="20" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Claim(s)</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">ACLM</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Assignee city</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">AC</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Government interest</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">GOVT</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="20" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Description/Specification</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">SPEC</a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top"><a CLASS="SmBlackText">Assignee state</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">AS</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="24" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">Parent case info.</a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">PARN</a></td>
                                                    <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0"/></td>
                                                    <td valign="top" width="24" height="1"><img src="/static/images/spacer.gif" border="0" width="20" height="1"/></td>
                                                    <td valign="top"><a CLASS="SmBlackText">&#160; </a></td>
                                                    <td valign="top"><a CLASS="SmBlackText">&#160; </a></td>
                                                </tr>
                                                <tr>
                                                    <td valign="top" height="2" colspan="5"><img src="/static/images/spacer.gif" border="0" height="2"/></td>
                                                </tr>
                                            </table>
                                        </td>
                                        <!-- END OF SMALL TABLE TO PUT TREATMENT CODES IN 2 COLUMNS -->
                                        <td valign="top" width="4"><img src="/static/images/spacer.gif" width="4" border="0"/></td></tr>
                                    <tr>
                                        <td valign="top" bgcolor="#000000" height="1" colspan="3"><img src="/static/images/spacer.gif" border="0" height="1"/></td>
                                    </tr>
                                </table>
                            </td>
                            <td valign="top" width="1" bgcolor="#000000"><img src="/static/images/s.gif" width="1"/></td>
                        </tr>
                    </table>
                    </center>
                </td>
            </tr>
            <tr>
                <td valgn="top" colspan="4" height="15" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" height="15"/></td>
            </tr>
    <!-- End of table for lookup indexes. -->
    <xsl:if test="$TIPS='true'">
        <tr>
            <td valign="top" colspan="4" >
            <!-- Start of table for help tips -->
            <table border="0" width="100%" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top" width="100%">
                <!-- table for Help tips -->
                  <table border="0" width="100%" cellspacing="0" cellpadding="0">
                    <tr>
                      <td valign="top" colspan="3" height="10"><img src="/static/images/spacer.gif" border="0" height="10" /></td>
                    </tr>
                    <tr>
                      <td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="1" /></td>
                      <td valign="top">
                        <table border="0" width="100%" cellspacing="0" cellpadding="0">
                          <tr>
                            <td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/static/images/spacer.gif" border="0" height="1" /></td>
                          </tr>
                          <tr>
                            <td valign="top" height="10" bgcolor="#3173B5" colspan="3"><a class="LgWhiteText"><b>&#160; Search Tips</b></a></td>
                          </tr>
                          <tr>
                            <td valign="top" height="15" colspan="3"><img src="/static/images/spacer.gif" border="0" height="15" /></td>
                          </tr>
                          <tr>
                            <td valign="top" width="4"><img src="/static/images/spacer.gif" width="4" border="0" /></td>
                            <td valign="top">
                            <a class="MedBlackText">
                            Use truncation  (*) to search for words that begin with the same letters.<br/>
                            comput* returns computer, computers, computerize, computerization

                            <P>
                            To search for an exact phrase or phrases containing stop words (and, or, not, near), enclose terms in braces or quotation marks. <br/>
                            {chip technology}
                            </P>

                            <P>
                            Dates may be formatted in several ways. <br/>
                            Search on June 4, 2002 as either, 20020604, June-4-2002 or Jun-4-2002
                            </P>

                            <P>
                            Names should be entered last name first name middle initial.<br/>
                            Yeh George<br/>
                            Clark George C
                            </P>
                            </a>
                            </td>
                          </tr>
                          <tr>
                            <td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td>
                          </tr>
                          <tr>
                            <td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/static/images/spacer.gif" border="0" height="1" /></td>
                          </tr>
                        </table>
                      </td>
                      <td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="1" /></td>
                    </tr>
                    <tr>
                      <td valign="top" height="8" colspan="3"><img src="/static/images/spacer.gif" border="0" height="8" /></td>
                    </tr>
                  </table>
                  <!-- End of  table for Help tips -->
                </td>
              </tr>
            </table>
        </td>
        </tr>
    </xsl:if>
    </table>
        <!-- End of table for search form -->
        </td>
      </tr>
    </table>
    <!-- End of table for content below the navigation bar -->
    </center>

  </xsl:template>

</xsl:stylesheet>
