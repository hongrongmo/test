<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
  	xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  	xmlns:java="java:java.net.URLDecoder"
	exclude-result-prefixes="html xsl xslcid"
	>

	<xsl:template match="SELECTED-SET-NAVIGATION-BAR">

	<!-- This xsl displays the navigation bar which allows the user to navigate thru' the documents
		 present in the Selected Set. based on conditions the pre v and next buttons are displayed.
		 USer is also allowed to to search resutls from which the user entered the selected page.
		 There is a new search button which takes the user to default
		 quick or expert search form depending on which type of search was last performed
		 by the user.

	-->

	<xsl:variable name="SESSION-ID">
		<xsl:value-of select="//SESSION-ID" disable-output-escaping="yes"/>
	</xsl:variable>

	<xsl:variable name="SEARCH-RESULTS-SEARCH-ID">
		<xsl:value-of select="//SEARCH-RESULTS/SEARCH-ID" disable-output-escaping="yes"/>
	</xsl:variable>

	<xsl:variable name="SEARCH-RESULTS-PAGE-INDEX">
		<xsl:value-of select="//SEARCH-RESULTS/PAGE-INDEX" disable-output-escaping="yes"/>
	</xsl:variable>

	<xsl:variable name="RESULTS-COUNT">
		<xsl:value-of select="//BASKET-NAVIGATION/RESULTS-COUNT" disable-output-escaping="yes"/>
	</xsl:variable>

	<xsl:variable name="PREV-PAGE">
		<xsl:value-of select="//BASKET-NAVIGATION/PREV-PAGE-ID" disable-output-escaping="yes"/>
	</xsl:variable>

	<xsl:variable name="NEXT-PAGE">
		<xsl:value-of select="//BASKET-NAVIGATION/NEXT-PAGE-ID" disable-output-escaping="yes"/>
	</xsl:variable>

	<xsl:variable name="SEARCH-TYPE">
		<xsl:value-of select="//PAGE/SEARCH-TYPE"/>
	</xsl:variable>

	<xsl:variable name="BASKET-COUNT">
		<xsl:value-of select="//BASKET-NAVIGATION/PAGE-INDEX" disable-output-escaping="yes"/>
	</xsl:variable>

	<xsl:variable name="DATABASE">
		<xsl:value-of select="//DBMASK" disable-output-escaping="yes"/>
	</xsl:variable>

	<xsl:variable name="RESULTS-PER-PAGE">
		<xsl:value-of select="//RESULTS-PER-PAGE"/>
	</xsl:variable>

	<xsl:variable name="CID">
		<xsl:value-of select="//CID"/>
	</xsl:variable>
	<xsl:variable name="SCOPE">
		<xsl:value-of select="//SCOPE"/>
	</xsl:variable>
	<xsl:variable name="GROUP-ID">
		<xsl:value-of select="//GROUP-ID"/>
	</xsl:variable>
	
	<xsl:variable name="DECODED-RESULTS-NAV">
	    <xsl:value-of select="java:decode(/PAGE/PAGE-NAV/RESULTS-NAV)"/>
	</xsl:variable>
	<xsl:variable name="ENCODED-RESULTS-NAV">
		<xsl:value-of select="/PAGE/PAGE-NAV/RESULTS-NAV"/>
	</xsl:variable>
	<xsl:variable name="DECODED-NEWSEARCH-NAV">
		<xsl:value-of select="java:decode(/PAGE/PAGE-NAV/NEWSEARCH-NAV)"/>
	</xsl:variable>
	<xsl:variable name="ENCODED-NEWSEARCH-NAV">
		<xsl:value-of select="/PAGE/PAGE-NAV/NEWSEARCH-NAV"/>
	</xsl:variable>


	<center>

	<!--
		This condition checks the search type so that
		the button links to the search form  (Quick /Expert) which was last used by the User.
	-->

    <xsl:variable name="NEWSEARCHCID">
      <xsl:value-of select="xslcid:newSearchCid($SEARCH-TYPE)"/>
    </xsl:variable>
    <xsl:variable name="SEARCHRESULTSCID">
      <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
    </xsl:variable>

		<!-- Must be consistent with NavigationBar.xsl -->
		<!-- Start of topnavigation bar -->
		<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
			<tr>
				<td align="left" valign="middle" height="24">
					<xsl:if test="string($DECODED-RESULTS-NAV)">
						&spcr8;
						<a href="/controller/servlet/Controller?{$DECODED-RESULTS-NAV}"><img src="/engresources/images/sr.gif" border="0"/></a>
						&spcr8;
						<a href="/controller/servlet/Controller?{$DECODED-NEWSEARCH-NAV}"><img src="/engresources/images/ns.gif" border="0" /></a>
					</xsl:if>
				</td>
				<xsl:if test="not($RESULTS-COUNT = '0')">
					<td align="right" valign="middle" height="24">
						<!--
							These two conditions checks the basket size to determine whether the previous and next buttons
							are to be displayed. Previous button is displayed only when the current record
							is not the first record in te basket. Next button is displayed only when the number of records
							 in the basket is greater tha nthe product of number of the first record and the basket size
							 (as defined in the RuntimeProperties.properties file)
						-->
						<xsl:if test="boolean(not($BASKET-COUNT=1))">
							<a href="/controller/servlet/Controller?CID={$CID}&amp;navigator=PREV&amp;BASKETCOUNT={$PREV-PAGE}&amp;DATABASETYPE={$DATABASE}&amp;searchresults={$ENCODED-RESULTS-NAV}&amp;newsearch={$ENCODED-NEWSEARCH-NAV}">
							<img src="/engresources/images/pp.gif" border="0" /></a>
							&spcr8;
						</xsl:if>
						<xsl:if test="(boolean($BASKET-COUNT * $RESULTS-PER-PAGE &lt; $RESULTS-COUNT))">
							<a href="/controller/servlet/Controller?CID={$CID}&amp;navigator=NEXT&amp;BASKETCOUNT={$NEXT-PAGE}&amp;DATABASETYPE={$DATABASE}&amp;searchresults={$ENCODED-RESULTS-NAV}&amp;newsearch={$ENCODED-NEWSEARCH-NAV}">
							<img src="/engresources/images/np.gif" border="0" /></a>
						</xsl:if>
						&spcr8;
					</td>
				</xsl:if>
			</tr>
		</table>
		</center>
		<!-- End of topnavigation bar -->
	</xsl:template>

</xsl:stylesheet>
