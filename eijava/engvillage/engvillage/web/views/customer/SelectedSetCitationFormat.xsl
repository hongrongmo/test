<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:resolver="org.ei.gui.InternalResolver"
	xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
	xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
	
	exclude-result-prefixes="java html xsl DD custoptions"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<!--- all XSL include files -->
<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="SelectedSetNavigationBar.xsl"/>
<xsl:include href="template.SEARCH_RESULTS.xsl" />
<xsl:include href="Footer.xsl"/>
<xsl:include href="LocalHolding.xsl" />

<!--- end of XSL include files -->

<xsl:include href="common/CitationResults.xsl" />

<xsl:param name="CUST-ID">0</xsl:param>

<xsl:variable name="RESULTS-DATABASE">
  <xsl:value-of select="/PAGE/DBMASK"/>
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

<xsl:variable name="FULLTEXT">
	<xsl:value-of select="/PAGE/FULLTEXT"/>
</xsl:variable>

<xsl:variable name="LOCALHOLDINGS-CITATION">
    <xsl:value-of select="/PAGE/LOCALHOLDINGS-CITATION"/>
</xsl:variable>

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

			<title>Selected Set - Citation Format Hello<xsl:value-of select="RESULTS-PER-PAGE"/>Hello</title>
			<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
			<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/URLEncode.js"/>
			<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/SelectedSet_V7.js"/>
		</head>

	<!-- start of body -->
	<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

	<center>

		<!-- Start of header -->
		<xsl:apply-templates select="HEADER">
      <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
    </xsl:apply-templates>

		<!-- Start of globalnavigation bar -->
		<xsl:apply-templates select="GLOBAL-LINKS">
		  <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		  <xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
		  <xsl:with-param name="SEARCH-ID" select="$SEARCH-RESULTS-QUERY"/>
		  <xsl:with-param name="LINK" select="resolver:resolveSearchType($ENCODED-NEWSEARCH-NAV)"/>
		</xsl:apply-templates>

		<!-- Start of top  blue bar for navigation-->
		<xsl:apply-templates select="SELECTED-SET-NAVIGATION-BAR"/>

  	<!-- Insert the TOP Results Manager -->
  	<xsl:if test="not($RESULTS-COUNT = '0')">
		<xsl:call-template name="RESULTS-MANAGER">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="LOCATION">top</xsl:with-param>
			<xsl:with-param name="DISPLAY-FORMAT">citation</xsl:with-param>
			<xsl:with-param name="CURRENT-VIEW">selectedset</xsl:with-param>
		</xsl:call-template>
	</xsl:if>

		<!-- Start of table for selected set -->

		<table border="0" width="99%" cellspacing="0" cellpadding="0">
			<tr>
				<td valign="top" height="20" colspan="5"><img src="/engresources/images/s.gif" border="0" height="20"/></td>
			</tr>
			<tr>
				<td valign="top" colspan="5"><a class="EvHeaderText">Selected Records</a></td>
			</tr>
			<tr>
				<td valign="top" height="5" colspan="5"><img src="/engresources/images/s.gif" border="0" height="5"/></td>
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

	</center>

	<!-- End of document display -->
	<center>

	<!-- Insert the BOTTOM Results Manager -->
	<xsl:if test="not($RESULTS-COUNT = '0')">
		<xsl:call-template name="RESULTS-MANAGER">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    			<xsl:with-param name="LOCATION">bottom</xsl:with-param>
    			<xsl:with-param name="DISPLAY-FORMAT">citation</xsl:with-param>
    			<xsl:with-param name="CURRENT-VIEW">selectedset</xsl:with-param>
		</xsl:call-template>
	<!-- Insert Navigation bar-->
		<xsl:apply-templates select="SELECTED-SET-NAVIGATION-BAR"/>
	</xsl:if>


	<!-- Insert Footer -->
	<xsl:apply-templates select="FOOTER">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
	</xsl:apply-templates>

	</center>

	</body>
	</html>
</xsl:template>

	<!-- This xsl displays the results in Citation Format when the database is Compendex -->
	<xsl:template match="PAGE-RESULTS">

		<!-- Start of  Citation Results  -->

			<FORM name="citation">
				<xsl:apply-templates select="PAGE-ENTRY"/>
			</FORM>

		<!-- END of  Citation Results  -->

	</xsl:template>

  <xsl:variable name="HREF-PREFIX"/>

	<xsl:template match="PAGE-ENTRY">

		<!-- All of these variables are for the selected set 'remove' link -->
		<xsl:variable name="SEARCH-ID">
			<xsl:value-of select="/PAGE/SEARCH-RESULTS/SEARCH-ID"/>
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
<!--
		<xsl:variable name="LAST-FOUR-UPDATES">
			<xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/>
	  </xsl:variable>
-->
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
		<xsl:variable name="FULLTEXT-LINK">
			<xsl:value-of select="EI-DOCUMENT/FT/@FTLINK"/>
		</xsl:variable>

		<!-- End of the variables are for the selected set 'remove' link -->

		<tr>
			<td valign="top" colspan="5" height="25"><img src="/engresources/images/spacer.gif" border="0" height="8"/></td>
		</tr>
		<tr>
			<td valign="top" align="left">
				<A href="/controller/servlet/Controller?CID=deleteFromSelectedSet&amp;viewType=citationSelectedSet&amp;docid={$DOC-ID}&amp;basketsize={$RESULTS-COUNT}&amp;handle={$INDEX}&amp;DATABASETYPE={$RESULTS-DATABASE}&amp;searchresults={$ENCODED-RESULTS-NAV}&amp;newsearch={$ENCODED-NEWSEARCH-NAV}"><img src="/engresources/images/remove.gif" border="0"/></A>
			</td>
			<td valign="top" width="3"><img src="/engresources/images/spacer.gif" border="0" width="3"/></td>
			<td valign="top" align="middle">
				<A CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</A>
			</td>
			<td valign="top" width="3"><img src="/engresources/images/spacer.gif" border="0" width="3"/></td>
			<td valign="bottom" width="100%" align="left">
				<xsl:apply-templates select="EI-DOCUMENT"/>
			</td>
		</tr>
		
	<xsl:variable name="CHECK-CUSTOM-OPT">
		<xsl:value-of select="custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, EI-DOCUMENT/DO, EI-DOCUMENT/DOC/DB/DBMASK)" />
	</xsl:variable>
	<xsl:if test="(($CHECK-CUSTOM-OPT ='true') or ($LOCALHOLDINGS-CITATION='true'))" >
        <tr>
          <td valign="bottom" colspan="4" height="30"><img src="/engresources/images/spacer.gif" border="0" height="30"/></td>
          <td valign="middle" align="left">

     		
     		<xsl:if test="($CHECK-CUSTOM-OPT ='true')">
                	<a href="" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false"><img src="/engresources/images/av.gif" border="0" /></a>
    		</xsl:if>

		<xsl:if test="($LOCALHOLDINGS-CITATION='true')">
			<xsl:apply-templates select="LOCAL-HOLDINGS" mode="CIT">
				<xsl:with-param name="vISSN"><xsl:value-of select="EI-DOCUMENT/SN"/></xsl:with-param>
				<xsl:with-param name="FULLTEXT-LINK">
					<xsl:choose>
						<xsl:when test="($CHECK-CUSTOM-OPT ='true')">Y</xsl:when>
						<xsl:otherwise>N</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:apply-templates>
		</xsl:if>
          </td>
        </tr>
		</xsl:if>

		<!-- END OF unique code for Selected Set -->

</xsl:template>

</xsl:stylesheet>
