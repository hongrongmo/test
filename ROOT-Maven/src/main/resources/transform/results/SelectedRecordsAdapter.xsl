<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:build="java:org.ei.system.Build"
    xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
    xmlns:pagenavigation="java:org.ei.stripes.view.PageNavigation"
    xmlns:navigator="java:org.ei.stripes.view.SearchResultNavigator"
    xmlns:navigatoritem="java:org.ei.stripes.view.ResultsNavigatorItem"
    xmlns:actionbean="java:org.ei.stripes.action.results.SelectedRecordsAction"
    xmlns:sortoption="java:org.ei.domain.SortOption"
    xmlns:srt="java:org.ei.domain.Sort"
    xmlns:bit="java:org.ei.util.BitwiseOperators"
    xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java html xsl"
>
	<xsl:param name="actionbean"/>
	<xsl:param name="CUST-ID">0</xsl:param>

	<!-- Search and Session IDs -->
	<xsl:variable name="SEARCH-ID"><xsl:value-of select="//SEARCH-ID" /></xsl:variable>
	<xsl:variable name="SESSION-ID"><xsl:value-of select="//SESSION-ID"/></xsl:variable>
	<xsl:variable name="CID"><xsl:value-of select="//CID"/></xsl:variable>

	<!-- ********************************************************** -->
	<!-- INDIVIDUAL RESULTS (depends on HREF-PREFIX!) -->	    
	<!-- ********************************************************** -->
	<xsl:variable name="HREF-PREFIX"/>
	<xsl:include href="common/CitationResults.xsl"/>

	<xsl:variable name="SEARCH-TYPE"><xsl:value-of select="//SEARCH-TYPE"/></xsl:variable>
    <xsl:variable name="RERUN-CID"><xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/></xsl:variable>
	<xsl:variable name="RESULTS-COUNT"><xsl:value-of select="//RESULTS-COUNT"/></xsl:variable>
	<xsl:variable name="COUNT"><xsl:value-of select="//CURR-PAGE-ID"/></xsl:variable>
	<xsl:variable name="DISPLAY-QUERY"><xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/></xsl:variable>
	<xsl:variable name="I-QUERY"><xsl:value-of select="//SESSION-DATA/I-QUERY"/></xsl:variable>
	<xsl:variable name="QUERY-ID"><xsl:value-of select="//SEARCH-ID"/></xsl:variable>
	<xsl:variable name="DATABASE"><xsl:value-of select="/PAGE/DBMASK"/></xsl:variable>
	<xsl:variable name="BACKURL"><xsl:value-of select="/PAGE/BACKURL"/></xsl:variable>
	<xsl:variable name="COMPMASK"><xsl:value-of select="/PAGE/NAVIGATORS/COMPMASK"/></xsl:variable>	
	<xsl:variable name="FULLTEXT"><xsl:value-of select="//FULLTEXT"/></xsl:variable>	
	<xsl:variable name="CURRENT-PAGE"><xsl:value-of select="//CURR-PAGE-ID"/></xsl:variable>
	<xsl:variable name="BACK-INDEX"><xsl:value-of select="//BACK-INDEX"/></xsl:variable>
	
	<!-- ********************************************************** -->
	<!-- Main template start                                        -->
	<!-- ********************************************************** -->
	<xsl:template match="/PAGE">
    	<xsl:value-of select="actionbean:setSessionid($actionbean,$SESSION-ID)"/>
		<xsl:value-of select="actionbean:setSearchid($actionbean,$SEARCH-ID)"/>
		<xsl:value-of select="actionbean:setCid($actionbean,$CID)"/>
		<xsl:value-of select="actionbean:setDatabase($actionbean,$DATABASE)"/>
	    <xsl:value-of select="actionbean:setCompmask($actionbean,$COMPMASK)"/>
	    <xsl:value-of select="actionbean:setSearchtype($actionbean,$SEARCH-TYPE)"/>
	    <xsl:value-of select="actionbean:setReruncid($actionbean,$RERUN-CID)"/>
	    <xsl:value-of select="actionbean:setBackurl($actionbean,$BACKURL)"/>
	    <xsl:value-of select="actionbean:setResultscount($actionbean,$RESULTS-COUNT)"/>
	    <xsl:value-of select="actionbean:setResultsperpage($actionbean,//RESULTS-PER-PAGE)"/>
	    <xsl:value-of select="actionbean:setDisplayquery($actionbean,$DISPLAY-QUERY)"/>
        <xsl:value-of select="actionbean:setBackindex($actionbean,$BACK-INDEX)"/>
        <xsl:value-of select="actionbean:setFulltextenabled($actionbean, boolean('true'=$FULLTEXT))"/>
		<xsl:value-of select="actionbean:setClearonnewsearch($actionbean, boolean('true'=//CLEAR-ON-VALUE))"/>
		<xsl:value-of select="actionbean:setFolderid($actionbean, //FOLDER-ID)"/>
		<xsl:value-of select="actionbean:setFolderName($actionbean, //FOLDER-NAME)"/>
		<xsl:value-of select="actionbean:setFolderSize($actionbean, //FOLDER-SIZE)"/>
		
		<xsl:if test="($CID='viewCitationSavedRecords') or ($CID='viewAbstractSavedRecords') 
		or ($CID='viewDetailedSavedRecords') or ($CID='deleteFromSavedRecords')  
		or ($CID='deleteAllFromFolder')">
			<xsl:value-of select="actionbean:setNewsearch($actionbean, //NEWSEARCHURL)"/>
			<xsl:value-of select="actionbean:setSearchresults($actionbean, //SEARCHRESULTSURL)"/>
			<xsl:value-of select="actionbean:setDocindex($actionbean,//DOCINDEX)"/>
			<xsl:value-of select="actionbean:setFormat($actionbean,//FORMAT)"/>
		</xsl:if>
		
		<!-- Build the query display ONLY when > refine element present -->
		<xsl:if test="count(//REFINE-STACK/NAVIGATOR) > 1">
		<xsl:apply-templates select="//REFINE-STACK/NAVIGATOR"/>
		</xsl:if>

		<!-- Add the sort options -->
		<xsl:apply-templates select="SORTABLE-FIELDS"/>

		<!-- Add the results -->
	    <xsl:apply-templates select="PAGE-RESULTS/PAGE-ENTRY">
	    	<xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"></xsl:with-param>
	    </xsl:apply-templates>
	    
    	<!-- Add page navigation -->
		<xsl:call-template name="PAGENAVIGATION"/>
     	
	</xsl:template>
	
	<!-- ********************************************* -->
	<!-- Results page navigation template              -->
	<!-- ********************************************* -->
	<xsl:template name="PAGENAVIGATION">
		<xsl:variable name="pagenavigation" select="pagenavigation:new()"/>
		<xsl:value-of select="pagenavigation:setResultscount($pagenavigation, //RESULTS-COUNT)"></xsl:value-of>
		<xsl:value-of select="pagenavigation:setCurrentindex($pagenavigation, //CURR-PAGE-ID)"></xsl:value-of>
		<xsl:value-of select="pagenavigation:setNextindex($pagenavigation, //NEXT-PAGE-ID)"></xsl:value-of>
		<xsl:value-of select="pagenavigation:setPrevindex($pagenavigation, //PREV-PAGE-ID)"></xsl:value-of>
		<xsl:value-of select="pagenavigation:setResultsperpage($pagenavigation, //RESULTS-PER-PAGE)"></xsl:value-of>
		<xsl:value-of select="pagenavigation:setCurrentpage($pagenavigation, //CURR-PAGE-ID)"></xsl:value-of>
		<xsl:value-of select="actionbean:setPagenav($actionbean, $pagenavigation)"/>
	</xsl:template>

	

</xsl:stylesheet>