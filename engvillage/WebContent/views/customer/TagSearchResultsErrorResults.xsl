<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/static/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  exclude-result-prefixes="java html xsl DD xslcid"
>
  <xsl:output method="html" indent="no"/>
  <xsl:strip-space elements="html:* xsl:*" />

  <xsl:include href="Header.xsl" />
  <xsl:include href="GlobalLinks.xsl" />
  <xsl:include href="SearchResultsRefineSearchForm.xsl" />
  <xsl:include href="Footer.xsl" />
 <!-- <xsl:include href="CombinedRefineSearchForm.xsl" /> -->

  <xsl:template match="PAGE">

    <xsl:variable name="RESULTS">
        <xsl:value-of select="RESULTS" />
    </xsl:variable>

    <xsl:variable name="SESSION-ID">
      <xsl:value-of select="SESSION-ID" />
    </xsl:variable>

    <xsl:variable name="SEARCH-ID">
      <xsl:value-of select="SEARCH-ID"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
      <xsl:value-of select="//DBMASK"/>
    </xsl:variable>

    <xsl:variable name="DATABASE-DISPLAYNAME">
      <xsl:value-of select="DD:getDisplayName($DATABASE)"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
      <xsl:value-of select="//SEARCH-TYPE"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-OPTION-1">
      <xsl:value-of select="SESSION-DATA/SEARCH-PHRASE/SEARCH-OPTION-1"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-OPTION-2">
      <xsl:value-of select="SESSION-DATA/SEARCH-PHRASE/SEARCH-OPTION-2"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-OPTION-3">
      <xsl:value-of select="SESSION-DATA/SEARCH-PHRASE/SEARCH-OPTION-3"/>
    </xsl:variable>

    <xsl:variable name="DISPLAY-QUERY">
      <xsl:value-of select="//DISPLAY-QUERY"/>
    </xsl:variable>

    <xsl:variable name="DOCUMENT-TYPE">
      <xsl:value-of select="SESSION-DATA/DOCUMENT-TYPE"/>
    </xsl:variable>

    <xsl:variable name="TREATMENT-TYPE">
      <xsl:value-of select="SESSION-DATA/TREATMENT-TYPE"/>
    </xsl:variable>

    <xsl:variable name="DISCIPLINE-TYPE">
      <xsl:value-of select="SESSION-DATA/DISCIPLINE-TYPE"/>
    </xsl:variable>

    <xsl:variable name="START-YEAR">
      <xsl:value-of select="SESSION-DATA/START-YEAR"/>
    </xsl:variable>

    <xsl:variable name="END-YEAR">
      <xsl:value-of select="SESSION-DATA/END-YEAR"/>
    </xsl:variable>

    <xsl:variable name="YEAR-STRING">
      <xsl:value-of select="CUSTOMIZED-STARTYEAR"/>
    </xsl:variable>

  <xsl:variable name="LASTUPDATES">
    <xsl:value-of select="SESSION-DATA/LASTFOURUPDATES"/>
  </xsl:variable>

  <xsl:variable name="EMAILALERTWEEK">
    <xsl:value-of select="SESSION-DATA/EMAILALERTWEEK"/>
  </xsl:variable>

    <html>
      <head>
        <xsl:choose>
        <xsl:when test="($RESULTS='yes')">
            <title>Combined Refine Search</title>
        </xsl:when>
        <xsl:otherwise>
        <title><xsl:value-of select="$SEARCH-TYPE"/> Search Error Results</title>
        </xsl:otherwise>
        </xsl:choose>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
        <xsl:if test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus') or ($SEARCH-TYPE='Combined')">
          <SCRIPT LANGUAGE="Javascript" SRC="/static/js/QuickResults.js"/>
        </xsl:if>
        <xsl:if test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Easy')">
          <SCRIPT LANGUAGE="Javascript" SRC="/static/js/ExpertResults.js"/>
        </xsl:if>
      </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

      <center>
      <table border="0" width="100%" cellspacing="0" cellpadding="0">
        <tr>
          <td valign="top">
            <xsl:apply-templates select="HEADER">
              <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
            </xsl:apply-templates>
          </td>
        </tr>
        <tr>
          <td valign="top">
            <!-- Insert the Global Link table -->
            <xsl:apply-templates select="GLOBAL-LINKS">
              <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
              <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
              <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
            </xsl:apply-templates>
          </td>
        </tr>
      </table>
      </center>

      <center>

        <!-- Table to display the refine search and new search-->
        <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1" >
          <tr >
            <td align="left" valign="middle" height="24">
        <!--      &spcr8;
              <xsl:choose>
                <xsl:when test="($SEARCH-TYPE='Thesaurus')">
                  <a href="/controller/servlet/Controller?CID=thesFrameSet&amp;searchid={$SEARCH-ID}&amp;database={$DATABASE}"><img src="/static/images/rs.gif" border="0" /></a>
                </xsl:when>
                <xsl:otherwise>
                  <a href="#searchAnchor"><img src="/static/images/rs.gif" border="0" /></a>
                </xsl:otherwise>
              </xsl:choose> -->
              &spcr8;

              <!-- jam 12/19/2002 bug
                "after any ES ending in zero hits,
                click on New search. You get the QS page".
                "quickSearch" was hardcoded on this link,
                I changed to test -->

              <xsl:variable name="CID-SEARCH">
                <xsl:value-of select="xslcid:newSearchCid($SEARCH-TYPE)"/>
              </xsl:variable>

              <a href="/controller/servlet/Controller?CID={$CID-SEARCH}&amp;database={$DATABASE}"><img src="/static/images/ns.gif" border="0" /></a>
            </td>
          </tr>
        </table>
      </center>

            <xsl:choose>
            <xsl:when test = "(not($RESULTS='yes'))">
            <!-- start of top blue bar for navigation-->
      <center>
        <table border="0" width="99%" cellspacing="0" cellpadding="0" height="20">
          <tr>
            <td valign="top" height="10" colspan="2">
              <img src="/static/images/s.gif" height="10" border="0"/>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan="2">
              <a class="EvHeaderText">Search Results</a>
            </td>
          </tr>
            <tr>
            <td valign="top" height="5" colspan="2">
                <img src="/static/images/s.gif" border="0" height="5"/>
            </td>
            </tr>

          <tr>
            <td valign="top" colspan="2">
              <A CLASS="MedBlackText">0 records found in <xsl:value-of select="$DATABASE-DISPLAYNAME"/> for tag search: <xsl:value-of select="$DISPLAY-QUERY"/>

              </A>

            </td>
          </tr>
          <tr>
            <td valign="top" colspan="2" height="10">
              <img src="/static/images/s.gif" border="0" height="10"/>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan="2">
            <xsl:choose>
                <xsl:when test="/PAGE/ERROR-CODE='1012'">
                    <a CLASS="MedBlackText"><b>Your search could not be completed due to the number of permutations produced by the use of the wildcard. Please try to refine your search.</b></a>
                </xsl:when>
                <xsl:otherwise>
                    <a CLASS="MedBlackText"><b>Your search retrieved 0 records. Check to make sure that you have entered valid tag search terms, or from the tag cloud.</b></a>
                </xsl:otherwise>
            </xsl:choose>


            </td>
          </tr>
        </table>
      </center>
      <!-- End of top blue bar for navigation-->

      <A name="searchAnchor"/>
      <!-- Display of Refine Search Form -->
      <xsl:apply-templates select="SEARCH">
        <xsl:with-param name="DATABASE" select="$DATABASE" />
        <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE" />
        <xsl:with-param name="YEAR-STRING" select="$YEAR-STRING" />

      </xsl:apply-templates>

            </xsl:when>
            </xsl:choose>

      <!-- Insert the Footer table -->
      <xsl:apply-templates select="FOOTER">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
       </xsl:apply-templates>

    </body>
    </html>

  </xsl:template>

</xsl:stylesheet>