<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:resolver="org.ei.gui.InternalResolver"
	xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
	xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
  	xmlns:book="java:org.ei.books.BookDocument"
  	xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
	exclude-result-prefixes="schar java html xsl DD custoptions"
>

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
<xsl:strip-space elements="html:* xsl:*" />

<!--- all XSL include files -->
<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="SelectedSetNavigationBar.xsl"/>
<xsl:include href="template.SEARCH_RESULTS.xsl" />
<xsl:include href="Footer.xsl"/>
<xsl:include href="LocalHolding.xsl" />

<xsl:param name="CUST-ID">0</xsl:param>

<xsl:variable name="RESULTS-DATABASE">
	<xsl:value-of select="/PAGE/DBMASK"/>
</xsl:variable>

<xsl:variable name="FULLTEXT">
	<xsl:value-of select="/PAGE/FULLTEXT"/>
</xsl:variable>
<xsl:variable name="ENCODED-RESULTS-NAV">
	<xsl:value-of select="/PAGE/PAGE-NAV/RESULTS-NAV"/>
</xsl:variable>
<xsl:variable name="ENCODED-NEWSEARCH-NAV">
	<xsl:value-of select="/PAGE/PAGE-NAV/NEWSEARCH-NAV"/>
</xsl:variable>
<xsl:variable name="RESULTS-COUNT">
	<xsl:value-of select="/PAGE/BASKET-NAVIGATION/RESULTS-COUNT"/>
</xsl:variable>

<xsl:include href="common/AbstractResults.xsl" />

  <!-- Hide Map data -->
  <xsl:template match="CRDN|RECT|LOC|LOCS" priority="1"/>

	<xsl:template match="PAGE">

		<xsl:variable name="SESSION-ID">
			<xsl:value-of select="SESSION-ID"/>
		</xsl:variable>
		<xsl:variable name="SEARCH-RESULTS-SEARCH-ID">
			<xsl:value-of select="SEARCH-RESULTS/SEARCH-ID"/>
		</xsl:variable>
		<xsl:variable name="SEARCH-RESULTS-PAGE-INDEX">
			<xsl:value-of select="SEARCH-RESULTS/PAGE-INDEX"/>
		</xsl:variable>
		<xsl:variable name="SEARCH-RESULTS-COUNT">
			<xsl:value-of select="SEARCH-RESULTS/RESULTS-COUNT"/>
		</xsl:variable>
		<xsl:variable name="SEARCH-RESULTS-QUERY">
			<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/QUERY/SEARCH-QUERY"/>
		</xsl:variable>
		<xsl:variable name="CURRENT-PAGE">
			<xsl:value-of select="BASKET-NAVIGATION/CURR-PAGE-ID"/>
		</xsl:variable>
		<xsl:variable name="BASKET-COUNT">
			<xsl:value-of select="BASKET-NAVIGATION/PAGE-INDEX"/>
		</xsl:variable>
		<xsl:variable name="PREV-PAGE">
			<xsl:value-of select="BASKET-NAVIGATION/PREV-PAGE-ID"/>
		</xsl:variable>
		<xsl:variable name="NEXT-PAGE">
			<xsl:value-of select="BASKET-NAVIGATION/NEXT-PAGE-ID"/>
		</xsl:variable>
		<xsl:variable name="RESULTS-PER-PAGE">
			<xsl:value-of select="BASKET-NAVIGATION/RESULTS-PER-PAGE"/>
		</xsl:variable>

<!--
		<xsl:variable name="ENCODED-RESULT-DATABASE">
			<xsl:value-of select="java:encode($RESULTS-DATABASE)"/>
		</xsl:variable>
-->
<!--
    <xsl:variable name="RESULTS-DATABASE-ID">
			<xsl:value-of select="SESSION-DATA/DATABASE/ID"/>
		</xsl:variable>
-->
		<xsl:variable name="SEARCH-TYPE">
			<xsl:value-of select="SEARCH-TYPE"/>
		</xsl:variable>
		<xsl:variable name="HAS-MULTIPLE-QUERYS">
			<xsl:value-of select="HAS-MULTIPLE-QUERYS"/>
		</xsl:variable>


	<html>
	<head>
	<title>Selected Set - Abstract Format</title>
		<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
		<SCRIPT LANGUAGE="Javascript" SRC="/static/js/URLEncode.js"/>
		<SCRIPT LANGUAGE="Javascript" SRC="/static/js/SelectedSet_V7.js"/>
		<SCRIPT LANGUAGE="Javascript" SRC="/static/js/lindaHall.js"/>
	</head>

	<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

		<center>
			<!-- Insert header -->
			<xsl:apply-templates select="HEADER">
        			<xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
      			</xsl:apply-templates>

			<!-- Insert Global Links -->
			<xsl:apply-templates select="GLOBAL-LINKS">
				<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
				<xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
				<xsl:with-param name="LINK" select="resolver:resolveSearchType($ENCODED-NEWSEARCH-NAV)"/>
			</xsl:apply-templates>
			<!-- Insert Navigation Bar -->
			<xsl:apply-templates select="SELECTED-SET-NAVIGATION-BAR"/>
			<!-- Insert Results Manager -->
			<xsl:if test="not($RESULTS-COUNT = '0')">
    				<xsl:call-template name="RESULTS-MANAGER">
    					<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        				<xsl:with-param name="LOCATION">top</xsl:with-param>
        				<xsl:with-param name="DISPLAY-FORMAT">abstract</xsl:with-param>
        				<xsl:with-param name="CURRENT-VIEW">selectedset</xsl:with-param>
    				</xsl:call-template>
    			</xsl:if>

			<table border="0" width="99%" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" height="20" colspan="5"><img src="/static/images/s.gif" border="0" height="20"/></td>
				</tr>
				<tr>
					<td valign="top" colspan="5"><a class="EvHeaderText">Selected Records</a></td>
				</tr>
				<tr>
					<td valign="top" height="5" colspan="5"><img src="/static/images/s.gif" border="0" height="5"/></td>
				</tr>
				<xsl:choose>
					<xsl:when test="not($RESULTS-COUNT = '0')">
					<tr>
						<td valign="top" colspan="5">

						<A CLASS="SmBlackText">
						<xsl:variable name="MIN-RANGE">
						<xsl:choose>
							<xsl:when test="boolean($PREV-PAGE =0)">1</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="((($CURRENT-PAGE * $RESULTS-PER-PAGE)+1) - $RESULTS-PER-PAGE )"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:variable name="MAX-RANGE">
							<xsl:choose>
								<xsl:when test="boolean($RESULTS-COUNT &gt; ($CURRENT-PAGE * $RESULTS-PER-PAGE) )">
									<xsl:value-of select="($CURRENT-PAGE * $RESULTS-PER-PAGE)"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$RESULTS-COUNT"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:value-of select="$MIN-RANGE"/> - <xsl:value-of select="$MAX-RANGE"/> of
						<xsl:value-of select="$RESULTS-COUNT"/> selected records
						 </A>
						</td>
					</tr>
					<xsl:apply-templates select="PAGE-RESULTS"/>


					</xsl:when>
					<xsl:otherwise>
						<tr><td>
							<br/>
							<span class="MedBlackText">Currently there are no records selected.</span>
							<br/>
							<br/>
							<br/>
							<br/>
						</td></tr>
					</xsl:otherwise>
				</xsl:choose>
			</table>

			<!-- Insert Results Manager -->
			<xsl:if test="not($RESULTS-COUNT = '0')">
    				<xsl:call-template name="RESULTS-MANAGER">
    				<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        			<xsl:with-param name="LOCATION">bottom</xsl:with-param>
        			<xsl:with-param name="DISPLAY-FORMAT">abstract</xsl:with-param>
        			<xsl:with-param name="CURRENT-VIEW">selectedset</xsl:with-param>
    				</xsl:call-template>

				<!-- Start of bottom blue bar for navigation-->
				<xsl:apply-templates select="SELECTED-SET-NAVIGATION-BAR"/>

			</xsl:if>


			<!-- start of Footer -->
			<xsl:apply-templates select="FOOTER">
				<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
				<xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
			</xsl:apply-templates>

		</center>
		</body>
		</html>

	</xsl:template>

	<xsl:template match="PAGE-RESULTS">

			<FORM name="abstract">
				<xsl:apply-templates select="PAGE-ENTRY"/>
			</FORM>

	</xsl:template>

	<xsl:template match="PAGE-ENTRY">

		<!-- All of these variables are for the selected set 'remove' link -->
		<xsl:variable name="SEARCH-ID">
			<xsl:value-of select="/PAGE/SEARCH-ID"/>
		</xsl:variable>
		<xsl:variable name="CURRENT-PAGE">
			<xsl:value-of select="/PAGE/BASKET-NAVIGATION/CURR-PAGE-ID"/>
		</xsl:variable>

		<xsl:variable name="RESULTS-PER-PAGE">
			<xsl:value-of select="/PAGE/RESULTS-PER-PAGE"/>
		</xsl:variable>
		<xsl:variable name="SEARCH-TYPE">
			<xsl:value-of select="/PAGE/SEARCH-TYPE"/>
		</xsl:variable>
		<xsl:variable name="SEARCH-RESULTS-COUNT">
			<xsl:value-of select="/PAGE/SEARCH-RESULTS/RESULTS-COUNT"/>
		</xsl:variable>
<!--		<xsl:variable name="LAST-FOUR-UPDATES">
			<xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/>
	     </xsl:variable> -->
		<xsl:variable name="DATABASE">
			<xsl:value-of select="EI-DOCUMENT/DB"/>
		</xsl:variable>
		<xsl:variable name="DATABASE-ID">
			<xsl:value-of select="EI-DOCUMENT/DBID"/>
		</xsl:variable>
<!--
		<xsl:variable name="ENCODED-DATABASE">
			<xsl:value-of select="java:encode($DATABASE)"/>
		</xsl:variable>
-->
		<xsl:variable name="DOC-ID">
			<xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
		</xsl:variable>
		<xsl:variable name="INDEX">
			<xsl:value-of select="DOCUMENTBASKETHITINDEX"/>
		</xsl:variable>
		<xsl:variable name="BASKET-COUNT">
			<xsl:value-of select="/PAGE/BASKET-NAVIGATION/PAGE-INDEX"/>
		</xsl:variable>
		<!-- End of the variables are for the selected set 'remove' link -->

		<tr>
			<td valign="top" colspan="5" height="15"><img src="/static/images/spacer.gif" border="0" height="15"/></td>
		</tr>
		<tr>
			<td valign="top" align="right">
				<A href="/controller/servlet/Controller?CID=deleteFromSelectedSet&amp;viewType=abstractSelectedSet&amp;docid={$DOC-ID}&amp;basketsize={$RESULTS-COUNT}&amp;handle={$INDEX}&amp;DATABASETYPE={$RESULTS-DATABASE}&amp;searchresults={$ENCODED-RESULTS-NAV}&amp;newsearch={$ENCODED-NEWSEARCH-NAV}"><img src="/static/images/remove.gif" border="0"/></A>
			</td>
			<td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td>
			<td valign="top">
				<A CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</A>
			</td>
			<td valign="top" width="4"><img src="/static/images/spacer.gif" border="0" width="3"/></td>
			<td valign="bottom" width="100%" align="left">
				<xsl:apply-templates select="EI-DOCUMENT"/>
			</td>
		</tr>

		<!-- Local holdings, Full text and Linda hall Links variables -->

		<xsl:variable name="FULLTEXT-LINK">
			<xsl:value-of select="EI-DOCUMENT/FT/@FTLINK"/>
		</xsl:variable>

		<xsl:variable name="LHL">
			<xsl:value-of select="/PAGE/LHL"/>
		</xsl:variable>
		<xsl:variable name="LOCALHOLDINGS">
			<xsl:value-of select="/PAGE/LOCALHOLDINGS"/>
		</xsl:variable>
		<xsl:variable name="CHECK-CUSTOM-OPT">
				<xsl:value-of select="custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, EI-DOCUMENT/DO , EI-DOCUMENT/DOC/DB/DBMASK)" />
		</xsl:variable>

		<xsl:if test="($CHECK-CUSTOM-OPT ='true') or ($LHL='true') or ($LOCALHOLDINGS='true')">
			<tr>
				<td valign="top" colspan="4"><img src="/static/images/s.gif" border="0"/></td>
				<td valign="top" align="left" bgcolor="#C3C8D1"><a class="MedBlackText"><b>&#160; Full-text and Local Holdings Links</b></a></td>
			</tr>

			<xsl:if test="($LOCALHOLDINGS='true')">
				<tr>
					<td valign="top" colspan="4"><img src="/static/images/s.gif" height="30" border="0"/></td>
					<td valign="top" align="left" >
						<xsl:apply-templates select="LOCAL-HOLDINGS" >
            	  					<xsl:with-param name="vISSN"><xsl:value-of select="EI-DOCUMENT/SN"/></xsl:with-param>
            					</xsl:apply-templates>
					</td>
				</tr>
			</xsl:if>

			<xsl:if test="($CHECK-CUSTOM-OPT ='true')" >
				<tr>
					<td valign="top" colspan="4"><img src="/static/images/s.gif" height="30" border="0"/></td>
					<td align="left"><a href="" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false"><img src="/static/images/av.gif" border="0"/></a></td>
				</tr>
			</xsl:if>

      			<xsl:if test="(EI-DOCUMENT/DOC/DB/DBMASK != '131072') and ($LHL='true')">
				<tr>
					<td valign="top" colspan="4"><img src="/static/images/s.gif" height="30" border="0"/></td>
					<td align="left">
						<a CLASS="MedBlueLink" href="javascript:lindaHall('$SESSIONID','{$DOC-ID}','{$DATABASE-ID}')">Linda Hall Library document delivery service</a>
					</td>
				</tr>
			</xsl:if>

      <xsl:if test="(EI-DOCUMENT/DOC/DB/DBMASK = '131072')">
        <xsl:variable name="BOOKS_OPEN_WINDOW_PARAMS">height=800,width=700,status=yes,resizable,scrollbars=1,menubar=no</xsl:variable>
				<tr>
					<td valign="top" colspan="4"><img src="/static/images/s.gif" height="30" border="0"/></td>
					<td valign="top">
            <xsl:if test="not(EI-DOCUMENT/BPP = '0')">
              <a >
                <xsl:attribute name="TITLE">Read Page</xsl:attribute>
                <xsl:attribute name="HREF">javascript:_referex=window.open('/controller/servlet/Controller?CID=bookFrameset&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;docid=<xsl:value-of select="$DOC-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/>','_referex','<xsl:value-of select="$BOOKS_OPEN_WINDOW_PARAMS"/>');_referex.focus();void('');</xsl:attribute>
                <img alt="Read Page" src="/static/images/read_page.gif" style="border:0px; vertical-align:middle"/>
                </a>
              &#160;
            </xsl:if>

              <xsl:if test="string(EI-DOCUMENT/PII)">
                <a>
                  <xsl:attribute name="target">_referex</xsl:attribute>
                  <xsl:attribute name="TITLE">Read Chapter</xsl:attribute>
                  <xsl:attribute name="HREF"><xsl:value-of select="book:getReadChapterLink(WOBLSERVER, EI-DOCUMENT/BN13, EI-DOCUMENT/PII, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                  <img alt="Read Chapter" src="/static/images/read_chp.gif" style="border:0px; vertical-align:middle"/>
                  </a>
                &#160;
              </xsl:if>

                <a >
                <xsl:attribute name="target">_referex</xsl:attribute>
                <xsl:attribute name="TITLE">Read Book</xsl:attribute>
                <xsl:attribute name="HREF"><xsl:value-of select="book:getReadBookLink(WOBLSERVER, EI-DOCUMENT/BN13, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                <img alt="Read Book" src="/static/images/read_book.gif" style="border:0px; vertical-align:middle"/>
                </a>
					</td>
				</tr>
      </xsl:if>
		</xsl:if>


	</xsl:template>

</xsl:stylesheet>
