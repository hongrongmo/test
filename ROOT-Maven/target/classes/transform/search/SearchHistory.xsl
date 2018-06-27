<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:searchhistory="java:org.ei.stripes.view.SearchHistory"
    xmlns:actionbean="java:org.ei.stripes.action.search.SearchDisplayAction"
    xmlns:SearchHistoryDedupCriteria="java:org.ei.stripes.view.SearchHistoryDedupCriteria"
    exclude-result-prefixes="html xsl"
>
	<!-- *************************************************************** -->
	<!-- Parse session-history elements                                  -->
	<!-- *************************************************************** -->
	<xsl:template name="SEARCH-HISTORY">
	<xsl:for-each select="//SESSION-HISTORY/SESSION-DATA">
		<xsl:variable name="START-YEAR"><xsl:value-of select="START-YEAR"/></xsl:variable>
		<xsl:variable name="END-YEAR"><xsl:value-of select="END-YEAR"/></xsl:variable>
		<xsl:variable name="SEARCH-TYPE"><xsl:value-of select="SEARCH-TYPE"/></xsl:variable>
		<xsl:variable name="LAST-FOUR-UPDATES"><xsl:value-of select="LASTFOURUPDATES"/></xsl:variable>
		<xsl:variable name="DATABASE"><xsl:value-of select="DATABASE/NAME"/></xsl:variable>
		<xsl:variable name="DATABASE-ID"><xsl:value-of select="DATABASE/ID"/></xsl:variable>
		<xsl:variable name="QUERY-ID"><xsl:value-of select="QUERY-ID"/></xsl:variable>
		<xsl:variable name="YEAR-RANGE"><xsl:value-of select="YEAR-RANGE"/></xsl:variable>
		<xsl:variable name="DATABASE-MASK"><xsl:value-of select="DATABASE-MASK"/></xsl:variable>
		<xsl:variable name="EMAILALERT"><xsl:value-of select="EMAILALERTWEEK"/></xsl:variable>
		<xsl:variable name="SERIAL-NUMBER"><xsl:value-of select="SERIAL-NUMBER"/></xsl:variable>
		<xsl:variable name="SAVEDSEARCH"><xsl:value-of select="SAVEDSEARCH"/></xsl:variable>
		<xsl:variable name="EMAIL-ALERT"><xsl:value-of select="EMAIL-ALERT"/></xsl:variable>
		
		<xsl:variable name="SEARCH-INDEX">
		<xsl:choose>
		  <xsl:when test="boolean(string-length(normalize-space($SERIAL-NUMBER))>0)"><xsl:value-of select ="$SERIAL-NUMBER"/></xsl:when>
		  <xsl:otherwise><xsl:number/></xsl:otherwise>
		</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="DISPLAY-QUERY">
		  <xsl:choose>
		    <xsl:when test="count(REFINE-STACK/NAVIGATOR/MODIFIER) = 1"><xsl:value-of select="REFINE-STACK/NAVIGATOR/MODIFIER/LABEL"/></xsl:when>
		    <xsl:otherwise><xsl:value-of select="DISPLAY-QUERY"/></xsl:otherwise>
		  </xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="SORTFIELD"><xsl:value-of select="SORT-OPTION"/></xsl:variable>

		<xsl:variable name="YEARS">
		<xsl:choose>
		  <xsl:when test="($DATABASE-MASK='8') or ($DATABASE-MASK='16')">&#160;</xsl:when>
		  <xsl:when test="($SEARCH-TYPE='Combined')">&#160;</xsl:when>
		  <xsl:when test="string($EMAILALERT)">Week <xsl:value-of select="$EMAILALERT"/></xsl:when>
		  <!-- Display all years for the USPTO -->
		  <xsl:when test="string($YEAR-RANGE)"><xsl:value-of select="$YEAR-RANGE"/></xsl:when>
		  <xsl:when test="string($LAST-FOUR-UPDATES)">Last <xsl:value-of select="$LAST-FOUR-UPDATES"/> update(s)</xsl:when>
		  <xsl:otherwise>
		   	<xsl:value-of select="$START-YEAR"/>
		   	<xsl:if test="($END-YEAR !='')">&#160;-&#160;<xsl:value-of select="$END-YEAR"/></xsl:if>
		  </xsl:otherwise>
		</xsl:choose>
		</xsl:variable>
		
	   	<xsl:variable name="searchhistory" select="searchhistory:new()"/>
		<xsl:value-of select="searchhistory:setSerialnumber($searchhistory, $SEARCH-INDEX)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setSessionid($searchhistory, SESSION-ID)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setSearchtype($searchhistory, SEARCH-TYPE)"></xsl:value-of>	
		<xsl:value-of select="searchhistory:setDatabasemask($searchhistory, DATABASE-MASK)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setLanguage($searchhistory, LANGUAGE)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setQueryid($searchhistory, QUERY-ID)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setSort($searchhistory, SORT-OPTION)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setSortdir($searchhistory, SORT-DIRECTION)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setDisplayquery($searchhistory, $DISPLAY-QUERY)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setResultscount($searchhistory, RESULTS-COUNT)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setYears($searchhistory, $YEARS)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setAutostemming($searchhistory, AUTOSTEMMING)"></xsl:value-of>
		<xsl:value-of select="searchhistory:setEmailalert($searchhistory, boolean($EMAIL-ALERT='On'))"></xsl:value-of>
		<xsl:value-of select="searchhistory:setSavedsearch($searchhistory, boolean($SAVEDSEARCH='On'))"></xsl:value-of>
		
		<xsl:for-each select="DUPSET/CRITERIA">
			 	<xsl:variable name="SearchHistoryDedupCriteria" select="SearchHistoryDedupCriteria:new()"/>
			 	
			 	<xsl:variable name="FIELDPREF"><xsl:value-of select="FIELDPREF"/></xsl:variable>
				<xsl:variable name="DBPREF"><xsl:value-of select="DBPREF"/></xsl:variable>
			 	
			 	<xsl:value-of select="SearchHistoryDedupCriteria:setFieldPref($SearchHistoryDedupCriteria, $FIELDPREF)"></xsl:value-of>
		        <xsl:value-of select="SearchHistoryDedupCriteria:setDbPref($SearchHistoryDedupCriteria, $DBPREF)"></xsl:value-of>
			    
			    <xsl:value-of select="searchhistory:addDedupCriterias($searchhistory, $SearchHistoryDedupCriteria)"/>
		</xsl:for-each>
		
		
		
		<xsl:value-of select="actionbean:addSearchHistory($actionbean, $searchhistory)"/>
	</xsl:for-each>
	</xsl:template>
	

</xsl:stylesheet>