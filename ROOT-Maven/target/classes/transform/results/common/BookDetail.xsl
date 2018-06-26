<?xml version="1.0" ?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
    xmlns:rfx="java:org.ei.books.collections.ReferexCollection"
    xmlns:searchresult="java:org.ei.stripes.view.SearchResult"
    xmlns:abstractterm="java:org.ei.stripes.view.AbstractTerm"
    xmlns:abstractrecord="java:org.ei.stripes.view.AbstractRecord"
    xmlns:book="java:org.ei.books.BookDocument"
    xmlns:crlkup="java:org.ei.data.CRNLookup"
    xmlns:ctd="java:org.ei.domain.ClassTitleDisplay"
    exclude-result-prefixes="schar java html xsl crlkup ctd rfx"
>
  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  
	<!-- Search and Session IDs -->
	<xsl:variable name="SEARCH-ID"><xsl:value-of select="//SEARCH-ID" /></xsl:variable>
	<xsl:variable name="SESSION-ID"><xsl:value-of select="/PAGE/SESSION-ID"/></xsl:variable>
	<xsl:variable name="SEARCH-TYPE"><xsl:value-of select="//SEARCH-TYPE"/></xsl:variable>
	<xsl:variable name="SEARCH-CONTEXT"><xsl:value-of select="/PAGE/SEARCH-CONTEXT" /></xsl:variable>
	
	<!-- ********************************************************** -->
	<!-- Parse abstract elements                                    -->
	<!-- ********************************************************** -->
	<xsl:variable name="HREF-PREFIX" />
	
	<xsl:include href="AbstractResults.xsl" />
	<xsl:param name="CUST-ID">0</xsl:param>
	<xsl:param name="actionbean"></xsl:param>
	
	
	
	<xsl:variable name="FULLTEXT"><xsl:value-of select="//FULLTEXT" /></xsl:variable>
	<xsl:variable name="BOOKS_OPEN_WINDOW_PARAMS">height=800,width=700,status=yes,resizable,scrollbars=1,menubar=no</xsl:variable>
	
	<xsl:variable name="DEDUP"><xsl:value-of select="/PAGE/DEDUP"/></xsl:variable>
	<xsl:variable name="CURRENT-PAGE"><xsl:value-of select="/PAGE/CURR-PAGE-ID"/></xsl:variable>
	<xsl:variable name="SELECTED-DB"><xsl:value-of select="/PAGE/DBMASK"/></xsl:variable>
	<xsl:variable name="COMPMASK"><xsl:value-of select="/PAGE/NAVIGATORS/COMPMASK"/></xsl:variable>
	
	<xsl:variable name="CID-PREFIX">
		<xsl:choose>
			<xsl:when test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus')">quickSearch</xsl:when>
			<xsl:otherwise>expertSearch</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>	

  <xsl:template match="PAGE">

    <xsl:variable name="BOOKS_OPEN_WINDOW_PARAMS">height=800,width=700,status=yes,resizable,scrollbars=1,menubar=no</xsl:variable>


    <xsl:variable name="SEARCH-TYPE">
      <xsl:choose>
        <xsl:when test="/PAGE/SEARCH-CONTEXT='tag'">TagSearch</xsl:when>
        <xsl:otherwise><xsl:value-of select="SESSION-DATA/SEARCH-TYPE" /></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <html>
    <head>
        <title><xsl:value-of select="$SEARCH-TYPE"/> Search Book Details</title>
        <link type="image/x-icon" href="/static/images/favicon_ei.ico" rel="SHORTCUT ICON"></link>
        <script LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
        <script LANGUAGE="Javascript" SRC="/static/js/ReferexSearch.js"/>
		    <link href="/static/css/booktoc.css" rel="stylesheet" type="text/css" />

       
    </head>
    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

      <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
      </xsl:apply-templates>

      <center>

        <!-- INCLUDE THE GLOBAL LINKS BAR -->
        <xsl:apply-templates select="GLOBAL-LINKS">
          <xsl:with-param name="SESSION-ID" select="SESSION-ID" />
          <xsl:with-param name="SELECTED-DB" select="DBMASK" />
          <xsl:with-param name="LINK" select="$SEARCH-TYPE" />
        </xsl:apply-templates>

          <!-- tabbed navigation -->
          <xsl:apply-templates select="ABSTRACT-DETAILED-NAVIGATION-BAR">
            <xsl:with-param name="HEAD" select="SEARCH-ID" />
          </xsl:apply-templates>

        <!-- Insert the navigation bar -->
        <xsl:apply-templates select="PAGE-NAV"/>


          <table border="0" width="99%" cellspacing="0" cellpadding="0">
            <tr>
              <td>

                <!-- Results manager -->
                <center>
                  <table border="0" cellspacing="0" cellpadding="0" width="99%" >
                    <tr>
                      <td colspan="2">
                        <img src="/static/images/s.gif" border="0" height="10" />
                      </td>
                    </tr>
                    <tr>
                      <td align="left">

                        <xsl:if test="/PAGE/PAGE-NAV/DET-NAV">
                          <a CLASS="LgBlueLink">
                          <xsl:attribute name="TITLE">Page Details</xsl:attribute>
                          <xsl:attribute name="HREF">/controller/servlet/Controller?<xsl:value-of select="/PAGE/PAGE-NAV/DET-NAV"/></xsl:attribute>Page Details</a>
                        </xsl:if>

                        <xsl:if test="/PAGE/PAGE-NAV/ABS-NAV or /PAGE/PAGE-NAV/DET-NAV">
                          <a class="MedBlackText">&#160; - &#160;</a>
                        </xsl:if>
                        <a class="BrownText">Book Details</a>

                        <xsl:if test="string(/PAGE/PAGE-NAV/READPAGE-NAV)">
                        <a class="MedBlackText">&#160; - &#160;</a>
                        <a class="LgBlueLink">
                          <xsl:attribute name="TITLE">Read Page</xsl:attribute>
                          <xsl:attribute name="HREF">javascript:_referex=window.open('/controller/servlet/Controller?<xsl:value-of select="/PAGE/PAGE-NAV/READPAGE-NAV"/>','_referex','<xsl:value-of select="$BOOKS_OPEN_WINDOW_PARAMS"/>');_referex.focus();void('');</xsl:attribute>
                          <img alt="Read Page" src="/static/images/read_page.gif" style="border:0px; vertical-align:middle"/>
                          </a>
                        </xsl:if>

                        <xsl:if test="string(descendant::PII)">
                          <a class="MedBlackText">&#160; - &#160;</a>
                          <a class="LgBlueLink">
                            <xsl:attribute name="target">_referex</xsl:attribute>
                            <xsl:attribute name="TITLE">Read Chapter</xsl:attribute>
                            <xsl:attribute name="HREF"><xsl:value-of select="book:getReadChapterLink(descendant::WOBLSERVER, descendant::EI-DOCUMENT/BN13, descendant::PII, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                            <img alt="Read Chapter" src="/static/images/read_chp.gif" style="border:0px; vertical-align:middle"/>
                            </a>
                        </xsl:if>

                        <a class="MedBlackText">&#160; - &#160;</a>
                        <a class="LgBlueLink">
                          <xsl:attribute name="target">_referex</xsl:attribute>
                          <xsl:attribute name="TITLE">Read Book</xsl:attribute>
                          <xsl:attribute name="HREF"><xsl:value-of select="book:getReadBookLink(descendant::WOBLSERVER, descendant::EI-DOCUMENT/BN13, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                          <img alt="Read Book" src="/static/images/read_book.gif" style="border:0px; vertical-align:middle"/>
                          </a>

                      </td>
                      <td align="right">

                        <form name="quicksearch" style="display:inline; margin:0px; padding:0px;" action="/search/results/quick.url" method="POST" onSubmit="return searchValidation();">
                          <input type="hidden" name="EI-SESSION" value="$SESSIONID"/>
                          <input type="hidden" name="database" value="131072"/>
                          <input type="hidden" name="yearselect" value="yearrange"/>
                          <input type="hidden" name="CID" value="quickSearchCitationFormat"/>
                          <input type="hidden" name="searchtype" value="Book"/>
                          <input type="hidden" name="section1" value="KY"/>

                          <input type="hidden" name="boolean1" value="AND"/>
                          <input type="hidden" name="searchWord2"><xsl:attribute name="value"><xsl:value-of select="descendant::EI-DOCUMENT/BN13"/></xsl:attribute></input>
                          <input type="hidden" name="section2" value="BN"/>

                          <a class="MedBlackText"><label for="txtsearchWord1">Search this book</label></a>
                          &#160;&#160;<input style="font-size:11px; vertical-align:middle;" type="text" size="24" maxlength="64" name="searchWord1" id="txtsearchWord1"/>
                          &#160;<input style="vertical-align:middle;" type="image" src="/static/images/search_orange1.gif" name="search" value="Search" alt="Search this book" border="0"/>
                        </form>&#160;&#160;
                      </td>
                    </tr>
                    <tr>
                      <td colspan="2">
                        <img src="/static/images/s.gif" border="0" height="10" />
                      </td>
                    </tr>
                  </table>
                </center>
                <!-- END Results Manager -->

                <xsl:apply-templates select="PAGE-RESULTS"/>
              </td>
            </tr>
          </table>
        </center>

        <!-- Insert the Footer table -->
        <xsl:apply-templates select="FOOTER">
          <xsl:with-param name="SESSION-ID" select="SESSION-ID" />
          <xsl:with-param name="SELECTED-DB" select="DBMASK" />
        </xsl:apply-templates>
        <br/>

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-6113832-3");
pageTracker._trackPageview();
} catch(err) {}</script>
    </body>
    </html>
  </xsl:template>

  <xsl:template match="BPP" priority="1">
    <br/>
  </xsl:template>

  <xsl:template match="BKYS|BKY" priority="1">
  </xsl:template>

  <xsl:template match="AB" priority="1">
  </xsl:template>

  <xsl:template match="PAGE-RESULTS">
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <tr>
            <td valign="top" align="right">&#160;</td>
            <td valign="top" width="3"><img src="/static/images/s.gif" border="0" width="3" name="image_basket"/></td>
            <td valign="top">&#160;</td>
            <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            <td valign="top" width="100%" align="left">
                <table border="0" width="99%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td valign="top">
                      <img border="0" width="100" height="145" style="float:left; margin-right:10px;">
                        <xsl:attribute name="SRC"><xsl:value-of select="//BOOKIMGS"/>/images/<xsl:value-of select="descendant::EI-DOCUMENT/BN13"/>/<xsl:value-of select="descendant::EI-DOCUMENT/BN13"/>small.jpg</xsl:attribute>
                        <xsl:attribute name="ALT"><xsl:value-of select="descendant::EI-DOCUMENT/BTI"/></xsl:attribute>
                      </img>
                    </td>
                    <td valign="top" width="100%" align="left">
                      <xsl:apply-templates select="descendant::EI-DOCUMENT"/>
                      <p/>
                      <a name="TOC" CLASS="MedBlackText"><b>Table of Contents</b></a>
                      <p/>
                      <div id="toc">
                      <xsl:value-of disable-output-escaping="yes" select="/PAGE/TOC"/>
                      </div>
                      <p/>
                      <a CLASS="SpBoldLink" href="#top">Back to Top</a>
                    </td>

                    <td valign="top">
                      <div class="m">
                        <img src="/static/images/s.gif" border="0" width="1" />
                      </div>
                    </td>
                    <td valign="top" width="30%" align="left">
                      <xsl:if test="//GLOBAL-LINKS/TAGGROUPS">
                        <xsl:apply-templates select="/PAGE/TAG-BUBBLE"/>
                      </xsl:if>

                      <p/>
                      <div id="bookcloud">
                        <div class="t" style="width:215px;">
                        <div class="b">
                        <div class="l">
                        <div class="r">
                        <div class="blc">
                        <div class="brc">
                        <div class="tlc">
                        <div class="trc">
                          <table border="0" style="margin:0px; padding:0px; width:100%">
                            <tr><td colspan="3"><img src="/static/images/s.gif" height="4"/></td></tr>
                            <tr>
                              <td><img src="/static/images/s.gif" border="0" width="1"/></td>
                              <td align="left">
                                  <img src="/static/images/cloud_icon.jpg" alt="Keyword Cloud" align="absmiddle" border="0"/>&#160;<span class="CloudTitle">Keyword Cloud</span>&#160;<a href="javascript:makeUrl('Referex_Keyword_Cloud.htm')"><img src="/static/images/blue_help.gif" alt="" border="0"/></a>
                                  <br/>
                                  <span class="ExSmBlackText">Keywords that appear most frequently in this book</span>
                                  <br/><br/>
                                  <xsl:value-of disable-output-escaping="yes" select="/PAGE/CLOUD"/>
                              </td>
                              <td><img src="/static/images/s.gif" border="0" width="1"/></td>
                            </tr>
                            <tr><td colspan="3"><img src="/static/images/s.gif" height="4"/></td></tr>
                          </table>
                        </div></div></div></div></div></div></div></div>
                      </div>

                    </td>
                  </tr>
                </table>
                <!-- END Book Detail Document -->
            </td>
          </tr>
    </table>
  </xsl:template>


</xsl:stylesheet>
