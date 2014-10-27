<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:gui="java:org.ei.gui.PagingComponents"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  exclude-result-prefixes="gui java html xsl xslcid"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="xsl:*" />

<xsl:template match="DEDUP-NAVIGATION-BAR">
  <!-- This variable is used to find out whether the Navigation Bar should contain the Refine Search
     and New Search Button -->
  <xsl:param name="HEAD"/>
  <xsl:param name="LOCATION"/>

  <xsl:variable name="ORIGIN">
    <xsl:value-of select="//ORIGIN"/>
  </xsl:variable>

  <xsl:variable name="DBLINK">
    <xsl:value-of select="//DBLINK"/>
  </xsl:variable>

  <xsl:variable name="LINKSET">
    <xsl:value-of select="//LINKSET"/>
  </xsl:variable>

  <xsl:variable name="FIELDPREF">
    <xsl:value-of select="//FIELDPREF"/>
  </xsl:variable>

  <xsl:variable name="DBPREF">
    <xsl:value-of select="//DBPREF"/>
  </xsl:variable>

  <xsl:variable name="REMOVED-DUPS">
    <xsl:value-of select="sum(/PAGE/DEDUPSET-REMOVED-DUPS/REMOVED-DUPS/attribute::COUNT)"/>
  </xsl:variable>

  <xsl:variable name="REMOVED-SUBSET">
    <xsl:value-of select="//REMOVED-SUBSET"/>
  </xsl:variable>

  <xsl:variable name="DEDUPSET">
    <xsl:choose>
        <xsl:when test="($LINKSET = 'removed')">
            <xsl:choose>
                <xsl:when test="($REMOVED-SUBSET > 0)">
                    <xsl:value-of select="$REMOVED-SUBSET"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$REMOVED-DUPS"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:when>
        <xsl:when test="($LINKSET = 'deduped')">
            <xsl:choose>
                <xsl:when test="($DEDUPSUBSET > 0)">
                    <xsl:value-of select="$DEDUPSUBSET"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="//DEDUPSET"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="//DEDUPSET"/>
        </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="SESSIONID">
    <xsl:value-of select="//SESSION-ID"/>
  </xsl:variable>
  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="//SEARCH-ID"/>
  </xsl:variable>
  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="//CURR-PAGE-ID"/>
  </xsl:variable>
  <xsl:variable name="PREV-PAGE">
    <xsl:value-of select="//PREV-PAGE-ID"/>
  </xsl:variable>
  <xsl:variable name="NEXT-PAGE">
    <xsl:value-of select="//NEXT-PAGE-ID"/>
  </xsl:variable>

  <xsl:variable name="DEDUPSUBSET">
    <xsl:value-of select="//DEDUPSUBSET"/>
  </xsl:variable>


  <xsl:variable name="RESULTS-COUNT">
    <xsl:value-of select="//PAGE/RESULTS-COUNT"/>
  </xsl:variable>
  <xsl:variable name="RESULTS-PER-PAGE">
    <xsl:value-of select="//PAGE/RESULTS-PER-PAGE"/>
  </xsl:variable>
  <xsl:variable name="DATABASE">
    <xsl:value-of select="//SESSION-DATA/DATABASE-MASK"/>
  </xsl:variable>
  <xsl:variable name="START-YEAR"><xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:variable>
  <xsl:variable name="END-YEAR"><xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:variable>
  <xsl:variable name="ENCODED-DATABASE">
    <xsl:value-of select="java:encode($DATABASE)"/>
  </xsl:variable>
  <xsl:variable name="SEARCH-TYPE">
    <xsl:value-of select="//SESSION-DATA/SEARCH-TYPE"/>
  </xsl:variable>
  <xsl:variable name="INDEX-VALUE">
    <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC/HITINDEX"/>
  </xsl:variable>

  <!-- jam EASY search refine search changes -->
  <xsl:variable name="SEARCHWORD1">
    <xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-PHRASE-1"/>
  </xsl:variable>

  <xsl:variable name="HIDDEN-SEARCHWORD1">
        <xsl:value-of select="//SESSION-DATA/I-QUERY"/>
  </xsl:variable>

<xsl:variable name="PAGE-INDEX">
    <xsl:value-of select="/PAGE/CURR-PAGE-ID"/>
</xsl:variable>

<xsl:variable name="SEARCHRESULTSCID">
  <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
</xsl:variable>
<!--
<xsl:choose>
    <xsl:when test="(='Book')">quickSearchCitationFormat</xsl:when>
    <xsl:when test="($SEARCH-TYPE='Easy')">expertSearchCitationFormat</xsl:when>
    <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchCitationFormat</xsl:when>
    <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchCitationFormat</xsl:when>
    <xsl:when test="($SEARCH-TYPE='Combined')">combineSearchHistory</xsl:when>
    <xsl:when test="($SEARCH-TYPE='Thesaurus')">thesSearchCitationFormat</xsl:when>
    <xsl:otherwise>expertSearchCitationFormat</xsl:otherwise>
</xsl:choose>
-->

  <xsl:variable name="CID">dedup</xsl:variable>
  <!-- The no of results in a range, that is to be displayed -->
  <xsl:variable name="DISPLAY-RANGE">
    <xsl:value-of select="($RESULTS-PER-PAGE * 10)"/>
  </xsl:variable>
  <!-- An increment variable to calculate the minRange -->
  <xsl:variable name="COUNT-MIN-RANGE">
    <xsl:value-of select="($CURRENT-PAGE div $DISPLAY-RANGE)"/>
  </xsl:variable>
  <!-- minRange represents the starting value of a particular range eg.1 or 251 or 501 or 751 etc. -->
  <xsl:variable name="MINIMUM-RANGE">
    <xsl:choose>
      <xsl:when test="boolean($PREV-PAGE =0) or boolean( ($CURRENT-PAGE - $RESULTS-PER-PAGE * 10) &lt; 0)">1</xsl:when>
      <xsl:when test="boolean($CURRENT-PAGE  &gt; ($DISPLAY-RANGE*(substring-before($COUNT-MIN-RANGE,'.' )))) " >
        <xsl:value-of select="( $DISPLAY-RANGE*(substring-before($COUNT-MIN-RANGE,'.' )) + 1) "/>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>
  <!-- To display the Previous text -->
  <xsl:variable name="COUNT-PREVIOUS">
    <xsl:value-of select="(($MINIMUM-RANGE - 1) div $DISPLAY-RANGE)" />
  </xsl:variable>
  <!-- The maximum value to be displayed for a particular range of 250 results. eg 250, 500,750 -->
  <xsl:variable name="MAX-RANGE">
    <xsl:choose>
      <xsl:when test="boolean( ($MINIMUM-RANGE + $DISPLAY-RANGE) &gt; $RESULTS-COUNT)">
        <xsl:value-of select="$RESULTS-COUNT" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="($MINIMUM-RANGE + $DISPLAY-RANGE)  "/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <!-- subMinRange will hold the value of the minRange and its increment by 25 or (range) in a particular range of 250 records -->
  <xsl:variable name="SUB-MIN-RANGE">
    <xsl:value-of select="$MINIMUM-RANGE"/>
  </xsl:variable>
  <!-- The max value for a particular range for eg. 25,50,75, 275,300 etc.  -->
  <xsl:variable name="SUB-MAX-RANGE">
    <xsl:choose>
      <xsl:when test="boolean(($SUB-MIN-RANGE + $RESULTS-PER-PAGE)  &gt; $RESULTS-COUNT)">
        <xsl:value-of select="$RESULTS-COUNT" />
      </xsl:when>
      <xsl:when test="boolean(($SUB-MIN-RANGE + $RESULTS-PER-PAGE) = $RESULTS-COUNT)">
        <xsl:value-of select="(($SUB-MIN-RANGE + $RESULTS-PER-PAGE) - 1)" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="(($SUB-MIN-RANGE + $RESULTS-PER-PAGE) - 1)" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

    <!-- Table to display the previous, next buttons and drop down box -->
    <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
      <tr>
        <td align="left" valign="middle" height="24">

              <xsl:choose>
                <xsl:when test="($HEAD=$SEARCH-ID)">
                  <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
                    <xsl:if test="($RESULTS-COUNT &gt; 0) ">
                        <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$SEARCHRESULTSCID"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$PAGE-INDEX"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;DupFlag=false</xsl:attribute>
                            <img height="16" src="/engresources/images/sr.gif" border="0" />
                        </a>
                    </xsl:if>
                  <img width="8" height="1" src="/engresources/images/s.gif" border="0" />

                  <a>
                    <xsl:choose>
                      <xsl:when test="($SEARCH-TYPE='Quick')">
                        <xsl:attribute name="href">/controller/servlet/Controller?CID=quickSearch&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                      </xsl:when>
                      <xsl:when test="($SEARCH-TYPE='Thesaurus')">
                        <xsl:attribute name="href">/controller/servlet/Controller?CID=thesHome&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:attribute name="href">/controller/servlet/Controller?CID=expertSearch&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                      </xsl:otherwise>
                    </xsl:choose>
                    <img src="/engresources/images/ns.gif" border="0" />
                  </a>
                </xsl:when>
              </xsl:choose>
        </td>
      <form name="selectresults" method="post" action="/controller/servlet/Controller">
        <INPUT TYPE="HIDDEN" name="CID"><xsl:attribute name="value"><xsl:value-of select="$CID"/></xsl:attribute></INPUT>
        <INPUT TYPE="HIDDEN" name="SEARCHID"><xsl:attribute name="value"><xsl:value-of select="$SEARCH-ID"/></xsl:attribute></INPUT>
        <INPUT TYPE="HIDDEN" name="RESULTSCOUNT"><xsl:attribute name="value"><xsl:value-of select="$RESULTS-COUNT"/></xsl:attribute></INPUT>
        <INPUT TYPE="HIDDEN" name="database"><xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute></INPUT>
        <input type="hidden" name="dedupSet" value="{$DEDUPSET}"/>
        <input type="hidden" name="dbpref" value="{$DBPREF}"/>
        <input type="hidden" name="fieldpref" value="{$FIELDPREF}"/>
        <input type="hidden" name="DupFlag" value="{$DEDUP}"/>
        <input type="hidden" name="origin" value="{$ORIGIN}"/>
        <input type="hidden" name="dbLink" value="{$DBLINK}"/>
        <input type="hidden" name="linkSet" value="{$LINKSET}"/>
        <input type="hidden" name="dedupSubset" value="{$DEDUPSUBSET}"/>
        <input type="hidden" name="removedSubset" value="{$REMOVED-SUBSET}"/>
        <input type="hidden" name="removedDups" value="{$REMOVED-DUPS}"/>
        <input type="hidden" name="NAVIGATOR" value="next"/>
        <td align="right" valign="middle" height="24">
            <table cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td align="right" valign="middle" height="24">
                      <xsl:if test="(not($CURRENT-PAGE=1)) and (not($CURRENT-PAGE &lt; $RESULTS-PER-PAGE))">
                        <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$CID"/>&amp;DupFlag=<xsl:value-of select="$DEDUP"/>&amp;dbpref=<xsl:value-of select="$DBPREF"/>&amp;fieldpref=<xsl:value-of select="$FIELDPREF"/>&amp;origin=<xsl:value-of select="$ORIGIN"/>&amp;dbLink=<xsl:value-of select="$DBLINK"/>&amp;linkSet=<xsl:value-of select="$LINKSET"/>&amp;navigator=PREV&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$PREV-PAGE"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                        <img src="/engresources/images/pp.gif" border="0" />
                        </a>
                      </xsl:if>
                      <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
                      <xsl:if test="(boolean(($INDEX-VALUE + $RESULTS-PER-PAGE - 1) &lt; $DEDUPSET) and (not($DEDUPSET=$RESULTS-PER-PAGE)))">
                        <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$CID"/>&amp;DupFlag=<xsl:value-of select="$DEDUP"/>&amp;dbpref=<xsl:value-of select="$DBPREF"/>&amp;fieldpref=<xsl:value-of select="$FIELDPREF"/>&amp;origin=<xsl:value-of select="$ORIGIN"/>&amp;dbLink=<xsl:value-of select="$DBLINK"/>&amp;linkSet=<xsl:value-of select="$LINKSET"/>&amp;navigator=NEXT&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$NEXT-PAGE"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                        <img src="/engresources/images/np.gif" border="0" />
                        </a>
                      </xsl:if>
                      <img width="16" height="1" src="/engresources/images/s.gif" border="0" />
                    </td>

                    <td align="right" valign="middle" width="20" height="24">
                      <xsl:if test="not(boolean($DEDUPSET &lt; $RESULTS-PER-PAGE)) and not(boolean($DEDUPSET = $RESULTS-PER-PAGE))">
                        <xsl:value-of disable-output-escaping="yes" select="gui:getPagingComponent('COUNT',$CURRENT-PAGE,25,$DEDUPSET)"/>
                      </xsl:if>
                    </td>
                     <td align="right" valign="middle" width="36" height="24">

                       <xsl:if test="not(boolean($DEDUPSET &lt; $RESULTS-PER-PAGE)) and not(boolean($DEDUPSET = $RESULTS-PER-PAGE))">
                         <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
                         <input type="image" name="go" src="/engresources/images/go1.gif" border="0" height="20" />
                         <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
                       </xsl:if>
                     </td>
                </tr>
            </table>
        </td>
      </form>
      </tr>
    </table>

  </xsl:template>

</xsl:stylesheet>
