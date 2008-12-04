<?xml version="1.0" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:book="java:org.ei.books.BookDocument"
    exclude-result-prefixes="java html xsl book"
    >
<!-- This is the page which is 'switched to' bt the controller when the Book Details link -->
<!-- is selected from a Book Record in Search results - Not a Page Record in Search results  -->
<!-- This page has navigation, whereas the other, bookDetail.xsl is the "dead end" available -->
<!-- from any page record citaton(Search results), abstract or detailed view -->

  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  <xsl:strip-space elements="html:* xsl:*" />

  <xsl:include href="Header.xsl" />
  <xsl:include href="GlobalLinks.xsl" />
  <xsl:include href="Footer.xsl" />
  <xsl:include href="DeDupControl.xsl" />
  <xsl:include href="TagBubble.xsl" />
  <xsl:include href="AbstractDetailedNavigationBar.xsl" />
  <xsl:include href="DedupAbstractDetailedNavigationBar.xsl" />
  <xsl:include href="TagSearchAbstractDetailedNavigationBar.xsl" />
  <xsl:include href="AbstractDetailedResultsManager.xsl" />

  <xsl:include href="LocalHolding.xsl" />

  <xsl:include href="common/AbstractResults.xsl" />

  <xsl:template match="PAGE">

    <xsl:variable name="DEDUP">
      <xsl:value-of select="DEDUP"/>
    </xsl:variable>

    <xsl:variable name="SESSION-ID">
      <xsl:value-of select="SESSION-ID" />
    </xsl:variable>

    <xsl:variable name="SEARCH-ID">
      <xsl:value-of select="SEARCH-ID"/>
    </xsl:variable>

    <xsl:variable name="TAG-SEARCH-FLAG">
      <xsl:value-of select="//TAG-SEARCH-FLAG" />
    </xsl:variable>

    <xsl:variable name="DATABASE">
      <xsl:choose>
        <xsl:when test="($TAG-SEARCH-FLAG='true')">
        <xsl:value-of select="DEFAULT-DB-MASK"/>
         </xsl:when>
         <xsl:otherwise>
        <xsl:value-of select="DBMASK" />
         </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
      <xsl:value-of select="SESSION-DATA/SEARCH-TYPE"/>
    </xsl:variable>

    <html>
    <head>
        <title><xsl:value-of select="$SEARCH-TYPE"/> Search Book Details Format</title>
				<link href="/engresources/stylesheets/booktoc.css" rel="stylesheet" type="text/css" />
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Autocomplete.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/ReferexSearch_V8.js"/>

         <script language="javascript">
          <xsl:comment>
            function loadFromToc(isbn,page)
            {
              isbn = "<xsl:value-of select="descendant::EI-DOCUMENT/BN13"/>";
              var common = "&amp;docid=pag_"+isbn.toLowerCase()+"_"+page+"&amp;SEARCHID=<xsl:value-of select="/PAGE/SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="/PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC/HITINDEX"/>&amp;database=<xsl:value-of select="/PAGE/SESSION-DATA/DATABASE-MASK"/>";
              var newwwin = window.open('/controller/servlet/Controller?CID=bookFrameset'+ common,'_referex','<xsl:value-of select="$BOOKS_OPEN_WINDOW_PARAMS"/>');void('');
              if(newwwin)
              {
                newwwin.focus();
              }
              pageTracker._trackEvent('Books','TOC',isbn);
              return;
            }
            function searchFromCloud(isbn,tag)
            {
              pageTracker._trackEvent('Books','Cloud',isbn + ", " + tag);
              document.location = "/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;yearselect=yearrange&amp;database=131072&amp;searchWord1={" + escape(tag) + "}&amp;searchWord2=" + isbn + "&amp;boolean1=AND&amp;section1=KY&amp;section2=BN";
              return;
            }
            // This function called when you want to add the document to the selected set and when
            // you want to delete the document from the selected set.In this two
            // functions will be performed based on checkbox checked value.If the checked value is true
            // the document will add to the selected set otherwise the document will be removed from
            // the selected set.
            function selectUnselectRecord(cbno,handle,docid,searchquery,database,sessionid,searchid)
            {
              var now = new Date();
              var milli = now.getTime();
              var cbcheck = document.quicksearchresultsform.cbresult.checked;
              document.images['image_basket'].src="/engresources/Basket.jsp?select=" + ((cbcheck) ? "mark" : "unmark") + "&amp;handle="+handle+"&amp;docid="+docid+"&amp;database="+escape(database)+"&amp;sessionid="+sessionid+"&amp;searchquery="+escape(searchquery)+"&amp;searchid="+searchid+"&amp;timestamp="+ milli;
            }

            // </xsl:comment>
         </script>
    </head>
    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

      <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
      </xsl:apply-templates>

      <center>
        <!-- Insert the Global Link table -->
        <xsl:apply-templates select="GLOBAL-LINKS">
          <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
           <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
          <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
        </xsl:apply-templates>

        <xsl:choose>
          <xsl:when test="($TAG-SEARCH-FLAG='true')">
          <!-- Insert the navigation bar -->
          <xsl:call-template name="TAGSEARCH-AB-DETAILED-NAVBAR">
          <xsl:with-param name="HEAD" select="$SEARCH-ID" />
          </xsl:call-template>
          </xsl:when>
          <xsl:when test="($DEDUP='true')">
          <!-- Insert the navigation bar -->
          <xsl:call-template name="DEDUP-AB-DETAILED-NAVBAR">
          <xsl:with-param name="HEAD" select="$SEARCH-ID"/>
          </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
          <!-- Insert the navigation bar -->
          <xsl:apply-templates select="PAGE-NAV"/>
          </xsl:otherwise>
        </xsl:choose>


        <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top">
              <!-- Insert the Results manager -->
              <xsl:if test="not(descendant::EI-DOCUMENT/BPP = '0')">
                <xsl:apply-templates select="ABSTRACT-DETAILED-RESULTS-MANAGER"/>
              </xsl:if>
              <xsl:if test="(descendant::EI-DOCUMENT/BPP = '0')">

                <!-- Results manager -->
                <center>
                  <table border="0" cellspacing="0" cellpadding="0" width="99%" >
                    <tr>
                      <td colspan="2">
                        <img src="/engresources/images/s.gif" border="0" height="10" />
                      </td>
                    </tr>
                    <tr>
                      <td align="left">
                        <a class="BrownText">Book Details</a>
                        <a class="MedBlackText">&#160; - &#160;</a>
                        <a class="LgBlueLink">
                          <xsl:attribute name="target">_referex</xsl:attribute>
                          <xsl:attribute name="TITLE">Read Book</xsl:attribute>
                          <xsl:attribute name="HREF"><xsl:value-of select="book:getReadBookLink(descendant::WOBLSERVER, descendant::EI-DOCUMENT/BN13, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                          <img alt="Read Book" src="/engresources/images/read_book.gif" style="border:0px; vertical-align:middle"/>
                          </a>
                      </td>
                      <td align="right">

                        <form name="quicksearch" style="display:inline; margin:0px; padding:0px;" action="/controller/servlet/Controller" method="POST" onSubmit="return searchValidation();">
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
                          &#160;<input style="vertical-align:middle;" type="image" src="/engresources/images/search_orange1.gif" name="search" value="Search" alt="Search this book" border="0"/>
                        </form>&#160;&#160;

                      </td>
                    </tr>
                    <tr>
                      <td colspan="2">
                        <img src="/engresources/images/s.gif" border="0" height="10" />
                      </td>
                    </tr>
                  </table>
                </center>
                <!-- END Results Manager -->

              </xsl:if>
              <xsl:apply-templates select="PAGE-RESULTS"/>
            </td>
          </tr>
        </table>
      </center>

      <!-- Insert the Footer -->
      <xsl:apply-templates select="FOOTER">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      </xsl:apply-templates>

      <br/>

      <script language="JavaScript" type="text/javascript" src="/engresources/js/wz_tooltip.js"></script>

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

    <xsl:template match="PAGE-RESULTS">
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <xsl:apply-templates select="PAGE-ENTRY"/>
      </table>
    </xsl:template>

    <xsl:variable name="SEARCH-TYPE">
      <xsl:value-of select="//SESSION-DATA/SEARCH-TYPE"/>
    </xsl:variable>

    <xsl:template match="PAGE-ENTRY">

        <xsl:variable name="SEARCH-ID">
          <xsl:value-of select="//SEARCH-ID"/>
        </xsl:variable>

        <xsl:variable name="CURRENT-PAGE">
          <xsl:value-of select="//CURR-PAGE-ID"/>
        </xsl:variable>

        <xsl:variable name="RESULTS-COUNT">
          <xsl:value-of select="//RESULTS-COUNT"/>
        </xsl:variable>

        <xsl:variable name="RESULTS-PER-PAGE">
          <xsl:value-of select="//RESULTS-PER-PAGE"/>
        </xsl:variable>

        <xsl:variable name="SEARCH-CONTEXT">
        	<xsl:value-of select="/PAGE/SEARCH-CONTEXT" />
        </xsl:variable>

        <xsl:variable name="SCOPE-REC">
          <xsl:value-of select="//SCOPE-REC"/>
        </xsl:variable>
        <xsl:variable name="DISPLAY-QUERY">
          <xsl:choose>
            <xsl:when test="($SEARCH-CONTEXT='tag')">
            	[Tag] <xsl:value-of select="/PAGE/ALT-DISPLAY-QUERY" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:variable name="ENCODED-DISPLAY-QUERY">
          <xsl:value-of select="java:encode($DISPLAY-QUERY)"/>
        </xsl:variable>

        <xsl:variable name="LAST-FOUR-UPDATES">
          <xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/>
        </xsl:variable>

        <xsl:variable name="SELECTED-DB">
          <xsl:value-of select="//SESSION-DATA/DATABASE/SHORTNAME"/>
        </xsl:variable>
        <xsl:variable name="DATABASE">
          <xsl:value-of select="EI-DOCUMENT/DOC/DB/DBNAME"/>
        </xsl:variable>
        <xsl:variable name="DATABASE-ID">
          <xsl:value-of select="EI-DOCUMENT/DOC/DB/ID"/>
        </xsl:variable>

        <xsl:variable name="INDEX">
          <xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/>
        </xsl:variable>

        <xsl:variable name="DOC-ID">
          <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
        </xsl:variable>

        <xsl:variable name="DEDUP">
          <xsl:value-of select="//DEDUP" />
        </xsl:variable>

        <input type="hidden" name="HANDLE"><xsl:attribute name="value"><xsl:value-of select="$INDEX"/></xsl:attribute></input>
        <input type="hidden" name="DOC-ID"><xsl:attribute name="value"><xsl:value-of select="$DOC-ID"/></xsl:attribute></input>

        <tr>
            <td colspan="7">
              <xsl:choose>
                <xsl:when test="not(/PAGE/HIDE-NAV)">
                  <a CLASS="SmBlueText">Record <xsl:value-of select="$INDEX"/> from <xsl:value-of select="$DATABASE" /> for: <xsl:value-of select="$DISPLAY-QUERY" /></a>
                  <xsl:choose>
                    <xsl:when test="string(//SESSION-DATA/EMAILALERTWEEK)"><a CLASS="SmBlueText">, Week <xsl:value-of select="//SESSION-DATA/EMAILALERTWEEK"/></a></xsl:when>
                    <xsl:when test="not($LAST-FOUR-UPDATES='')"><a CLASS="SmBlueText">, Last <xsl:value-of select="$LAST-FOUR-UPDATES"/> update(s)</a></xsl:when>
                    <xsl:when test="(//SESSION-DATA/SEARCH-TYPE!='Combined')">
              		  	<xsl:if test="not($SEARCH-CONTEXT='tag')">
                        <a CLASS="SmBlueText">, <xsl:value-of select="//PAGE/SESSION-DATA/START-YEAR"/>-<xsl:value-of select="//PAGE/SESSION-DATA/END-YEAR"/></a>
                      </xsl:if>
                    </xsl:when>
                  </xsl:choose>
                </xsl:when>
              </xsl:choose>
            </td>
        </tr>
        <tr>
          <td colspan="7"><img src="/engresources/images/s.gif" border="0" height="10"/></td>
        </tr>
        <tr>
          <td align="left" colspan="7"><a class="SmBlackText">Check record to add to Selected Records</a></td>
        </tr>
        <tr><td colspan="7"><img src="/engresources/images/s.gif" border="0" height="10"/></td></tr>
        <tr>
            <td valign="top" align="right">
              <form name="quicksearchresultsform">
              <xsl:variable name="IS-SELECTED"><xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/IS-SELECTED" disable-output-escaping="yes"/></xsl:variable>
              <xsl:variable name="CB-NO"><xsl:number/></xsl:variable>
              <input type="checkbox" name="cbresult" onClick="selectUnselectRecord( {$CB-NO},{$INDEX},'{$DOC-ID}','{$ENCODED-DISPLAY-QUERY}','{$DATABASE-ID}','$SESSIONID','{$SEARCH-ID}')">
                <xsl:if test="$IS-SELECTED='true'">
                  <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
              </input>
              </form>
            </td>
            <td><img src="/engresources/images/s.gif" border="0" width="3" name="image_basket"/></td>
            <td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</a></td>
            <td><img src="/engresources/images/s.gif" border="0" width="4"/></td>
            <td width="100%" align="left" valign="top">

              <table border="0" width="99%" cellspacing="0" cellpadding="0">
                <tr>
                  <td valign="top">
                    <img border="0" width="100" height="150" style="float:left; margin-right:10px;">
                      <xsl:attribute name="SRC"><xsl:value-of select="//BOOKIMGS"/>/images/<xsl:value-of select="EI-DOCUMENT/BN13"/>/<xsl:value-of select="EI-DOCUMENT/BN13"/>small.jpg</xsl:attribute>
                      <xsl:attribute name="ALT"><xsl:value-of select="EI-DOCUMENT/BTI"/></xsl:attribute>
                    </img>
                  </td>
                  <td valign="top" width="100%" align="left">
                    <xsl:apply-templates select="EI-DOCUMENT" />
                    <p/>
                    <a name="toc" class="MedBlackText"><b>Table of Contents</b></a>
                    <p/>
                    <div id="toc">
                      <xsl:value-of disable-output-escaping="yes" select="TOC"/>
                    </div>
                    <p/>
                    <a class="SpBoldLink" href="#top">Back to Top</a>
                  </td>
                </tr>
              </table>
            </td>

            <xsl:if test="not($DEDUP='true')">
                <td valign="top">
                  <div class="m">
                    <img src="/engresources/images/s.gif" border="0" width="1" />
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
                        <tr><td colspan="3"><img src="/engresources/images/s.gif" height="4"/></td></tr>
                        <tr>
                          <td><img src="/engresources/images/s.gif" border="0" width="1"/></td>
                          <td align="left">
                              <img src="/engresources/images/cloud_icon.jpg" alt="Keyword Cloud" align="absmiddle" border="0"/>&#160;<span style="font-family:arial,verdana,geneva;  font-size: 18px; color:#000000;">Keyword Cloud</span>&#160;&#160;<a href="javascript:makeUrl('Keyword_Cloud.htm')"><img src="/engresources/images/blue_help.gif" alt="" border="0"/></a>
                              <br/>
                              <span class="ExSmBlackText">Keywords that appear most frequently in this book</span>
                              <br/><br/>
                              <xsl:value-of disable-output-escaping="yes" select="CLOUD"/>
                          </td>
                          <td><img src="/engresources/images/s.gif" border="0" width="1"/></td>
                        </tr>
                        <tr><td colspan="3"><img src="/engresources/images/s.gif" height="4"/></td></tr>
                      </table>
                    </div></div></div></div></div></div></div></div>
                  </div>

                </td>
            </xsl:if>


          </tr>
<!--  end of ajax -->
<!-- end -->

    </xsl:template>

</xsl:stylesheet>

