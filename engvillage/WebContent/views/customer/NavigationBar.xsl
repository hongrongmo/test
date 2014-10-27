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

<xsl:template match="NAVIGATION-BAR">
  <!-- This xsl displays Navigation Bar for both quick and expert searchs.
       This file has logic for selecting number of records to be displayed in page, for going to previous
       and next pages, etc. This file renders in combination with QuickSearchResults.jsp and ExpertSearchResults.jsp. -->
  <xsl:param name="SESSION-ID"/>
  <xsl:param name="SELECTED-DB"/>
  <!-- This variable is used to find out whether the Navigation Bar should contain the Refine Search
     and New Search Button -->
  <xsl:param name="HEAD"/>
  <xsl:param name="LOCATION"/>

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

  <xsl:variable name="CID">
    <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
  </xsl:variable>

  <xsl:variable name="NEWSEARCHCID">
    <xsl:value-of select="xslcid:newSearchCid($SEARCH-TYPE)"/>
  </xsl:variable>


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
            <xsl:when test="($SEARCH-TYPE='Easy') and ($LOCATION='Top')">
              <form style="margin-bottom:0;" name="shortsearch" method="post" action="/controller/servlet/Controller" onsubmit="return shortSearchValidation(this);">
                  <input type="hidden" name="CID"><xsl:attribute name="value"><xsl:value-of select="$CID"/></xsl:attribute></input>
                  <input type="hidden" name="SEARCHID"><xsl:attribute name="value"><xsl:value-of select="$SEARCH-ID"/></xsl:attribute></input>
                  <input type="hidden" name="RESULTSCOUNT"><xsl:attribute name="value"><xsl:value-of select="$RESULTS-COUNT"/></xsl:attribute></input>
                  <input type="hidden" name="database"><xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute></input>
                  <input type="hidden" name="RERUN"><xsl:attribute name="value"><xsl:value-of select="$SEARCH-ID"/></xsl:attribute></input>
                  <input type="hidden" name="hiddensearchWord1" value="{$HIDDEN-SEARCHWORD1}"/>
                  <input type="hidden" name="startYear" value="{$START-YEAR}"/>
                  <input type="hidden" name="endYear" value="{$END-YEAR}"/>
                  <input type="hidden" name="yearselect" value="yearrange"/>
                  <input type="hidden" name="searchtype" value="Easy"/>
                  <input type="hidden" name="section" value="ALL"/>
                  <input type="hidden" name="append" value=""/>
                  <img width="8" height="1" src="/static/images/s.gif" border="0" />
                  <a href="javascript:setRefine(document.shortsearch);" >
                  <img src="/static/images/rs.gif" border="0" />
                  </a>
                  <img width="8" height="1" src="/static/images/s.gif" border="0" />
                  <a class="SmBlackText">
                    <input style="vertical-align:text-bottom;" class="SmBlackText" type="text" name="searchWord1" size="30" maxlength="256"/>
                    <img width="8" height="1" src="/static/images/s.gif" border="0" /><img width="8" height="1" src="/static/images/s.gif" border="0" />
                    <select style="vertical-align:text-bottom;" class="SmBlackText" name="where">
                      <option value="2">Within results</option>
                      <option value="1">All content</option>
                    </select>
                  </a>
                  <img width="8" height="1" src="/static/images/s.gif" border="0" />
                  <input type="image" src="/static/images/go_1.gif" name="search" value="Search" border="0"/>
                  <img width="8" height="1" src="/static/images/s.gif" border="0" />
              </form>

            <xsl:text disable-output-escaping="yes">
            <![CDATA[
            <xsl:comment>
            <script language="javascript">
              <!--
              function setRefine(sourceform)
              {
                if(typeof(sourceform) != 'undefined')
                {
                  document.location="#top";
                  sourceform.searchWord1.value = sourceform.hiddensearchWord1.value;
                  sourceform.where[1].selected = true;
                  sourceform.searchWord1.focus();
                }
              }
              // -->
            </script>
            </xsl:comment>
            ]]>
            </xsl:text>

            </xsl:when>
            <xsl:when test="($SEARCH-TYPE='Easy') and ($LOCATION='Bottom')">
              <img width="8" height="1" src="/static/images/s.gif" border="0" />
              <a href="javascript:setRefine(document.shortsearch);" >
              <img src="/static/images/rs.gif" border="0" />
              </a>
            </xsl:when>
            <xsl:otherwise>
              <xsl:choose>
                <xsl:when test="($HEAD=$SEARCH-ID)">
                  <img width="8" height="1" src="/static/images/s.gif" border="0" />
                  <a>
                    <xsl:choose>
                      <xsl:when test="($SEARCH-TYPE='Book')">
                        <xsl:attribute name="href">/controller/servlet/Controller?CID=ebookSearch&amp;searchID=<xsl:value-of select="$SEARCH-ID"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                      </xsl:when>
                      <xsl:when test="($SEARCH-TYPE='Thesaurus')">
                        <xsl:attribute name="href">/controller/servlet/Controller?CID=thesFrameSet&amp;searchID=<xsl:value-of select="$SEARCH-ID"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                      </xsl:when>
                      <xsl:when test="($SEARCH-TYPE='Expert')">
                        <xsl:attribute name="href">/controller/servlet/Controller?CID=expertSearch&amp;searchID=<xsl:value-of select="$SEARCH-ID"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                      </xsl:when>
                      <xsl:when test="($SEARCH-TYPE='Quick')">
                        <xsl:attribute name="href">/controller/servlet/Controller?CID=quickSearch&amp;searchID=<xsl:value-of select="$SEARCH-ID"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                      </xsl:when>
                      <xsl:when test="($SEARCH-TYPE='Combined')">
                        <xsl:attribute name="href">/controller/servlet/Controller?CID=combinedRefineSearch&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=1&amp;FORWARD=true&amp;RESULTS=yes&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                      </xsl:when>
                    </xsl:choose>
                    <img src="/static/images/rs.gif" border="0" />
                  </a>
                  <img width="8" height="1" src="/static/images/s.gif" border="0" />
                  <a>
                    <xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$NEWSEARCHCID"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                    <img src="/static/images/ns.gif" border="0" />
                  </a>
                  <xsl:if test="(boolean(($INDEX-VALUE + $RESULTS-PER-PAGE) &gt; 4000))">
                  	<img width="8" height="1" src="/static/images/s.gif" border="0" />                
                  	<a class="MedOrangeText"><b> only first 4000 records can be viewed</b></a>
                   </xsl:if>
                </xsl:when>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      <form name="selectresults" method="post" action="/controller/servlet/Controller">
        <INPUT TYPE="HIDDEN" name="CID"><xsl:attribute name="value"><xsl:value-of select="$CID"/></xsl:attribute></INPUT>
        <INPUT TYPE="HIDDEN" name="SEARCHID"><xsl:attribute name="value"><xsl:value-of select="$SEARCH-ID"/></xsl:attribute></INPUT>
        <INPUT TYPE="HIDDEN" name="RESULTSCOUNT"><xsl:attribute name="value"><xsl:value-of select="$RESULTS-COUNT"/></xsl:attribute></INPUT>
        <INPUT TYPE="HIDDEN" name="database"><xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute></INPUT>
        <td align="right" valign="middle" height="24">
            <table cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td align="right" valign="middle" height="24">
                      <xsl:if test="(not($CURRENT-PAGE=1)) and (not($CURRENT-PAGE &lt; $RESULTS-PER-PAGE))">
                        <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$CID"/>&amp;navigator=PREV&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$PREV-PAGE"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                        <img src="/static/images/pp.gif" border="0" />
                        </a>
                      </xsl:if>
                      <img width="8" height="1" src="/static/images/s.gif" border="0" />
                      <xsl:if test="(boolean(($INDEX-VALUE + $RESULTS-PER-PAGE - 1) &lt; 4000) and (($INDEX-VALUE + $RESULTS-PER-PAGE - 1) &lt; $RESULTS-COUNT) and (not($RESULTS-COUNT=$RESULTS-PER-PAGE)))">
                        <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$CID"/>&amp;navigator=NEXT&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$NEXT-PAGE"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
                        <img src="/static/images/np.gif" border="0" />
                        </a>
                      </xsl:if>
                      <img width="16" height="1" src="/static/images/s.gif" border="0" />
                    </td>
                    <td align="right" valign="middle" width="20" height="24">
                      <xsl:if test="not(boolean($RESULTS-COUNT &lt; $RESULTS-PER-PAGE)) and not(boolean($RESULTS-COUNT = $RESULTS-PER-PAGE))">
                        <xsl:value-of disable-output-escaping="yes" select="gui:getPagingComponent('COUNT',$CURRENT-PAGE,25,$RESULTS-COUNT)"/>
                      </xsl:if>
                    </td>
                    <td align="right" valign="middle" width="36" height="24">
                      <xsl:if test="not(boolean($RESULTS-COUNT &lt; $RESULTS-PER-PAGE)) and not(boolean($RESULTS-COUNT = $RESULTS-PER-PAGE))">
                        <img width="8" height="1" src="/static/images/s.gif" border="0" />
                        <input type="image" name="go" src="/static/images/go1.gif" border="0" height="20" />
                        <img width="8" height="1" src="/static/images/s.gif" border="0" />
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
