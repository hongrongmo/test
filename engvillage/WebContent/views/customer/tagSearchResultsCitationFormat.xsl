<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    xmlns:srt="java:org.ei.domain.Sort"
    xmlns:bit="java:org.ei.util.BitwiseOperators"
    xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
    xmlns:book="java:org.ei.books.BookDocument"
    exclude-result-prefixes="java html xsl DD srt bit custoptions book"
>

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl" />
<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="tagsResultsNavigationBar.xsl" />
<xsl:include href="template.SEARCH_RESULTS.xsl" />
<xsl:include href="template.TAG_SUMMARY.xsl" />
<xsl:include href="common/CitationResults.xsl" />
<xsl:include href="Footer.xsl" />

<xsl:param name="CUST-ID">0</xsl:param>

<xsl:variable name="FULLTEXT">
    <xsl:value-of select="//FULLTEXT" />
</xsl:variable>

<xsl:variable name="LOCALHOLDINGS-CITATION">
    <xsl:value-of select="//LOCALHOLDINGS-CITATION"/>
</xsl:variable>

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="/PAGE/SESSION-ID" />
</xsl:variable>

<xsl:variable name="SEARCH-ID">
	<xsl:value-of select="/PAGE/SEARCH-ID"/>
</xsl:variable>

<xsl:variable name="ENCODED-SEARCH-ID">
	<xsl:value-of select="java:encode($SEARCH-ID)"/>
</xsl:variable>

<xsl:template match="PAGE">

    <xsl:variable name="CURRENT-PAGE">
        <xsl:value-of select="//CURR-PAGE-ID"/>
    </xsl:variable>

    <xsl:variable name="RESULTS-COUNT">
        <xsl:value-of select="//RESULTS-COUNT"/>
    </xsl:variable>

    <xsl:variable name="RESULTS-PER-PAGE">
        <xsl:value-of select="//RESULTS-PER-PAGE"/>
    </xsl:variable>

    <xsl:variable name="DATABASE-DISPLAYNAME">
      <xsl:value-of select="DD:getDisplayName(SELECTED-DATABASE)"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
        <xsl:value-of select="DBMASK"/>
    </xsl:variable>

    <xsl:variable name="DATABASE-ID">
        <xsl:value-of select="//SESSION-DATA/DATABASE/ID"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
        <xsl:value-of select="SEARCH-TYPE"/>
    </xsl:variable>

    <html>
    <head>
        <META http-equiv="Expires" content="0"/>
        <META http-equiv="Pragma" content="no-cache"/>
        <META http-equiv="Cache-Control" content="no-cache"/>
    <title>Engineering Village - Tag Search Results</title>

    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/SearchResults_V7.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/URLEncode.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/AutocompleteTagSearch.js"/>

    <xsl:if test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='TagSearch') or ($SEARCH-TYPE='Thesaurus') or ($SEARCH-TYPE='Combined')">
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/QuickResults.js"/>
    </xsl:if>
    <xsl:if test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Easy')">
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/ExpertResults.js"/>
    </xsl:if>

    <!-- End of javascript -->

    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" >
    <center>

      <!-- APPLY HEADER -->
      <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      </xsl:apply-templates>

      <!-- Insert the Global Links -->
      <!-- logo, search history, selected records, my folders. end session -->
      <xsl:apply-templates select="GLOBAL-LINKS">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      <xsl:with-param name="LINK">Tags</xsl:with-param>

      </xsl:apply-templates>

      <!-- Insert the navigation bar -->
      <!-- tabbed navigation -->
      <xsl:apply-templates select="TAGS-NAVIGATION-BAR">
        <xsl:with-param name="HEAD" select="$SEARCH-ID"/>
        <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
        <xsl:with-param name="LOCATION">Top</xsl:with-param>
      </xsl:apply-templates>

        <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top">

              <!-- Insert the TOP Results Manager -->
              <xsl:call-template name="RESULTS-MANAGER">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
                <xsl:with-param name="LOCATION">top</xsl:with-param>
                <xsl:with-param name="DISPLAY-FORMAT">citation</xsl:with-param>
              </xsl:call-template>

	            <xsl:call-template name="TAG-SUMMARY"/>

              <xsl:apply-templates select="PAGE-RESULTS"/>

              <!-- Display of 'Next Page' Navigation Bar -->
              <xsl:apply-templates select="TAGS-NAVIGATION-BAR">
                <xsl:with-param name="HEAD" select="$SEARCH-ID"/>
                 <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
                <xsl:with-param name="LOCATION">Bottom</xsl:with-param>
              </xsl:apply-templates>

              <!-- Insert the BOTTOM Results Manager -->
              <xsl:call-template name="RESULTS-MANAGER">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
                <xsl:with-param name="LOCATION">bottom</xsl:with-param>
                <xsl:with-param name="DISPLAY-FORMAT">citation</xsl:with-param>
              </xsl:call-template>

              <p align="right">
              <a class="MedBlueLink" href="#">Back to top</a> <a href="#"><img src="/static/images/top.gif" border="0"/></a>
              </p>

            </td>
          </tr>
        </table>

      </center>

      <!-- Insert the Footer table -->
      <xsl:apply-templates select="FOOTER">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      </xsl:apply-templates>

      <br/>
    </body>
    </html>

</xsl:template>

  <!-- This xsl displays the results in Citation Format when the database is Compendex -->
  <xsl:template match="PAGE-RESULTS">
    <!-- Start of  Citation Results  -->
    <form style="margin:0px; padding:0px;" name="quicksearchresultsform">
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <xsl:apply-templates select="PAGE-ENTRY"/>
      </table>
    </form>
    <!-- END of  Citation Results  -->
  </xsl:template>

    <xsl:variable name="SEARCH-TYPE">
      <xsl:value-of select="SEARCH-TYPE"/>
    </xsl:variable>

    <xsl:variable name="CID-PREFIX">
        <xsl:choose>
            <xsl:when test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus')">quickSearch</xsl:when>
            <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">expertSearch</xsl:when>
      <xsl:otherwise>expertSearch</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="HREF-PREFIX"/>

  <xsl:template match="PAGE-ENTRY">

    <xsl:variable name="FULLTEXT-LINK">
        <xsl:value-of select="EI-DOCUMENT/FT/@FTLINK"/>
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

    <xsl:variable name="TAG-SEARCH-FLAG">
        <xsl:value-of select="//TAG-SEARCH"/>
    </xsl:variable>

    <xsl:variable name="DISPLAY-QUERY">
        <xsl:choose>
	<xsl:when test="($TAG-SEARCH-FLAG='true')">
 	<xsl:value-of select="//SEARCH-ID"/>
 	</xsl:when>
	<xsl:otherwise>
	<xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/>
	</xsl:otherwise>
	</xsl:choose>
    </xsl:variable>

    <xsl:variable name="ENCODED-DISPLAY-QUERY">
        <xsl:value-of select="java:encode($DISPLAY-QUERY)"/>
    </xsl:variable>

    <xsl:variable name="SELECTED-DB">
        <xsl:value-of select="/PAGE/DBMASK"/>
    </xsl:variable>

    <xsl:variable name="DATABASE-ID">
        <xsl:value-of select="EI-DOCUMENT/DOC/DB/ID"/>
    </xsl:variable>

    <xsl:variable name="INDEX">
        <xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/>
    </xsl:variable>


    <xsl:variable name="TAG-RESULT-DBNAME"><xsl:value-of select="EI-DOCUMENT/DOC/DB/DBMASK"/></xsl:variable>

    <xsl:variable name="DOC-ID">
        <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
    </xsl:variable>

     <xsl:variable name="SEARCH-TYPE">
            <xsl:value-of select="SEARCH-TYPE"/>
    </xsl:variable>

    <xsl:variable name="SCOPE">
	<xsl:value-of select="//SCOPE"/>
    </xsl:variable>

   <xsl:variable name="GROUP-ID">
	<xsl:value-of select="//GROUP-ID"/>
   </xsl:variable>

   <xsl:variable name="CIT-CNT">
     <xsl:value-of select="EI-DOCUMENT/CCT"/>
   </xsl:variable>

   <xsl:variable name="REF-CNT">
    <xsl:value-of select="EI-DOCUMENT/RCT"/>
   </xsl:variable>

    <xsl:variable name="NONREF-CNT">
       <xsl:value-of select="EI-DOCUMENT/NPRCT" />
    </xsl:variable>

        <tr>
            <td valign="top" colspan="4" height="5"><img src="/static/images/s.gif"/></td>
        </tr>
        <tr>
            <td valign="top" align="left" width="3">
                <input type="hidden" name="HANDLE"><xsl:attribute name="value"><xsl:value-of select="$INDEX"/></xsl:attribute></input>
                <input type="hidden" name="DOC-ID"><xsl:attribute name="value"><xsl:value-of select="$DOC-ID"/></xsl:attribute></input>
                <xsl:variable name="IS-SELECTED"><xsl:value-of select="IS-SELECTED" disable-output-escaping="yes"/></xsl:variable>
                <xsl:variable name="CB-NO"><xsl:number/></xsl:variable>
                <input type="checkbox" name="cbresult" onClick="selectUnselectRecord( this,{$INDEX},'{$DOC-ID}','{$ENCODED-DISPLAY-QUERY}','{$DATABASE-ID}','$SESSIONID','{$SEARCH-ID}',{$RESULTS-COUNT})">

              <xsl:if test="$IS-SELECTED='true'">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
                </input>
            </td>
            <td valign="top" width="3"><img src="/static/images/s.gif"/><A CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</A></td>
            <td valign="top" width="3"><img src="/static/images/s.gif" width="3"/></td>
            <td valign="top"><img src="/static/images/s.gif" name="image_basket"/><xsl:apply-templates select="EI-DOCUMENT"/><br/>

            <!-- do not show Abstract and Detailed links if this is a Referex Record -->
	         <xsl:if test="not(EI-DOCUMENT/DOC/DB/DBMASK = '131072')">
            <a class="LgBlueLink" href="/search/doc/abstract.url?pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;SEARCHID={$ENCODED-SEARCH-ID}&amp;DOCINDEX={$INDEX}&amp;database={$SELECTED-DB}&amp;tagscope={$SCOPE}&amp;format={$CID-PREFIX}AbstractFormat">Abstract</a><a class="MedBlackText">&#160; - &#160;</a><a class="LgBlueLink" href="/controller/servlet/Controller?CID=tagSearchDetailedFormat&amp;SEARCHID={$ENCODED-SEARCH-ID}&amp;DOCINDEX={$INDEX}&amp;tagscope={$SCOPE}&amp;database={$SELECTED-DB}&amp;format={$CID-PREFIX}DetailedFormat">Detailed</a>
           </xsl:if>


           <xsl:variable name="BOOKS_OPEN_WINDOW_PARAMS">height=800,width=700,status=yes,resizable,scrollbars=1,menubar=no</xsl:variable>


    <!-- Show book summary if this is a Referex Book Record (page 0) or a Referex Page Record (page != 0) -->
		<xsl:if test="(EI-DOCUMENT/DOC/DB/DBMASK = '131072')">

		      <!-- jam: Page Details (SAME AS Detailed link) link only for book pages (page !=0) -->
		      <xsl:if test="not(EI-DOCUMENT/BPP = '0')">
		      <a class="LgBlueLink" TITLE="Page Details" HREF="/search/doc/abstract.url?pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;SEARCHID={$ENCODED-SEARCH-ID}&amp;DOCINDEX={$INDEX}&amp;database={$SELECTED-DB}&amp;tagscope={$SCOPE}&amp;format={$CID-PREFIX}AbstractFormat">Page Details</a>
		      <a class="MedBlackText">&#160; - &#160;</a>
		      </xsl:if>

			<xsl:if test="not(EI-DOCUMENT/BPP = '0')">
        <a class="LgBlueLink" TITLE="Book Details" HREF="/controller/servlet/Controller?CID=tagSearchBookSummary&amp;SEARCHID={$ENCODED-SEARCH-ID}&amp;DOCINDEX={$INDEX}&amp;database={$SELECTED-DB}&amp;tagscope={$SCOPE}&amp;docid={$DOC-ID}&amp;format={$CID-PREFIX}AbstractFormat&amp;pii={EI-DOCUMENT/PII}">Book Details</a>
			</xsl:if>
			<xsl:if test="(EI-DOCUMENT/BPP = '0')">
        <a class="LgBlueLink" TITLE="Book Details" HREF="/search/doc/abstract.url?pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;SEARCHID={$ENCODED-SEARCH-ID}&amp;DOCINDEX={$INDEX}&amp;database={$SELECTED-DB}&amp;tagscope={$SCOPE}&amp;format={$CID-PREFIX}AbstractFormat">Book Details</a>
			</xsl:if>

			<xsl:if test="not(EI-DOCUMENT/BPP = '0')">
				<a class="MedBlackText">&#160; - &#160;</a>
				<a class="LgBlueLink">
				<xsl:attribute name="TITLE">Read Page</xsl:attribute>
				<xsl:attribute name="HREF">javascript:_referex=window.open('/controller/servlet/Controller?CID=bookFrameset&amp;SEARCHID=<xsl:value-of select="$ENCODED-SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;docid=<xsl:value-of select="$DOC-ID"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>','_referex','<xsl:value-of select="$BOOKS_OPEN_WINDOW_PARAMS"/>');_referex.focus();void('');</xsl:attribute>
				<img alt="Read Page" src="/static/images/read_page.gif" style="border:0px; vertical-align:middle"/>
				</a>
			</xsl:if>

      <xsl:if test="string(EI-DOCUMENT/PII)">
        <a class="MedBlackText">&#160; - &#160;</a>
        <a class="LgBlueLink">
          <xsl:attribute name="target">_referex</xsl:attribute>
          <xsl:attribute name="TITLE">Read Chapter</xsl:attribute>
          <xsl:attribute name="HREF"><xsl:value-of select="book:getReadChapterLink(/PAGE/PAGE-RESULTS/PAGE-ENTRY/WOBLSERVER,EI-DOCUMENT/BN13,EI-DOCUMENT/PII, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
          <img alt="Read Chapter" src="/static/images/read_chp.gif" style="border:0px; vertical-align:middle"/>
          </a>
      </xsl:if>
		</xsl:if>

		<xsl:if test="($REF-CNT) and not($REF-CNT ='0') and not($REF-CNT ='')">
		<A CLASS="MedBlackText">&#160; - &#160;</A>
		<A title="Show patents that are referenced by this patent" CLASS="LgBlueLink" HREF="/controller/servlet/Controller?CID=tagSearchReferencesFormat&amp;SEARCHID={$ENCODED-SEARCH-ID}&amp;DOCINDEX={$INDEX}&amp;database=1&amp;tagscope={$SCOPE}&amp;docid={$DOC-ID}">Patent Refs</A>
		&nbsp;<A CLASS="MedBlackText">(<xsl:value-of select="$REF-CNT"/>)</A>
		</xsl:if>

		<xsl:if test="($NONREF-CNT) and not($NONREF-CNT ='0') and not($NONREF-CNT ='')">
		<A CLASS="MedBlackText">&#160; - &#160;</A>
		<A CLASS="LgBlueLink" HREF="/controller/servlet/Controller?CID=tagSearchNonPatentReferencesFormat&amp;SEARCHID={$ENCODED-SEARCH-ID}&amp;DOCINDEX={$INDEX}&amp;database=1&amp;tagscope={$SCOPE}&amp;docid={$DOC-ID}">Other Refs</A>
		&nbsp;<A CLASS="MedBlackText">(<xsl:value-of select="$NONREF-CNT"/>)</A>
		</xsl:if>

		<xsl:if test="($CIT-CNT) and not($CIT-CNT ='0') and not($CIT-CNT ='')">
		<A CLASS="MedBlackText">&#160; - &#160;</A>
		<A title="Show patents that reference this patent" CLASS="LgBlueLink" HREF="/controller/servlet/Controller">Cited by</A>
		&nbsp;<A CLASS="MedBlackText">(<xsl:value-of select="$CIT-CNT"/>)</A>
		</xsl:if>

		<xsl:variable name="CHECK-CUSTOM-OPT">
			<xsl:value-of select="custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, EI-DOCUMENT/DO, EI-DOCUMENT/DOC/DB/DBMASK)" />
		</xsl:variable>

		<xsl:if test="($CHECK-CUSTOM-OPT ='true')">
			<A CLASS="MedBlackText">&#160; - &#160;</A>
			<a href="" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false"><img src="/static/images/av.gif" align="absbottom" border="0"/></a>
		</xsl:if>

            <xsl:if test="($LOCALHOLDINGS-CITATION='true')">
                <xsl:apply-templates select="LOCAL-HOLDINGS" mode="CIT">
                <xsl:with-param name="vISSN"><xsl:value-of select="EI-DOCUMENT/SN"/></xsl:with-param>
                </xsl:apply-templates>
            </xsl:if>

      </td>
    </tr>
    <tr>
      <td valign="top" colspan="4" height="5"><img src="/static/images/s.gif" height="5" border="0"/></td>
    </tr>

</xsl:template>

</xsl:stylesheet>

