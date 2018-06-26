<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLDecoder"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    xmlns:srt="java:org.ei.domain.Sort"
    xmlns:bit="java:org.ei.util.BitwiseOperators"
    xmlns:book="java:org.ei.books.BookDocument"
    xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
    xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
    xmlns:sortoption="java:org.ei.domain.SortOption"
    xmlns:abstractrecord="java:org.ei.stripes.view.AbstractRecord"
    xmlns:actionbean="java:org.ei.stripes.action.results.PageDetailViewAction"
    exclude-result-prefixes="java xsl DD srt bit xslcid custoptions book"
>

<!-- Search and Session IDs -->
<xsl:variable name="SEARCH-ID"><xsl:value-of select="//SEARCH-ID" /></xsl:variable>
<xsl:variable name="SESSION-ID"><xsl:value-of select="/PAGE/SESSION-ID"/></xsl:variable>
<xsl:variable name="SEARCH-TYPE"><xsl:value-of select="//SEARCH-TYPE"/></xsl:variable>
<xsl:variable name="SEARCH-CONTEXT"><xsl:value-of select="/PAGE/SEARCH-CONTEXT" /></xsl:variable>
<!-- ********************************************************** -->
<!-- Parse search form (state) elements                         -->
<!-- ********************************************************** -->
<xsl:include href="../search/SearchForm.xsl"/>

<!-- ********************************************************** -->
<!-- Include tags and groups stylesheet -->
<!-- ********************************************************** -->
<xsl:include href="common/TagBubble.xsl"/>

<!-- ********************************************************** -->
<!-- Parse abstract elements                                    -->
<!-- ********************************************************** -->
<xsl:variable name="HREF-PREFIX" />

<xsl:include href="DeDupControl.xsl" />
<xsl:include href="common/DetailedResults.xsl" />
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



<!-- ********************************************************** -->
<!-- Start of main template                                     -->
<!-- ********************************************************** -->
<xsl:template match="PAGE">
	<!-- SEARCH FORM STATE -->	    
    <xsl:apply-templates select="SESSION-DATA"></xsl:apply-templates>

    <xsl:variable name="COUNT"><xsl:value-of select="//CURR-PAGE-ID"/></xsl:variable>
    <xsl:variable name="RESULTS-COUNT"><xsl:value-of select="//RESULTS-COUNT"/></xsl:variable>
    <xsl:variable name="RESULTS-PER-PAGE"><xsl:value-of select="//RESULTS-PER-PAGE"/></xsl:variable>
    <xsl:variable name="DATABASE-DISPLAYNAME"><xsl:value-of select="DD:getDisplayName(DBMASK)"/></xsl:variable>
    <xsl:variable name="DATABASE"><xsl:value-of select="DBMASK"/></xsl:variable>
    <xsl:variable name="DATABASE-ID"><xsl:value-of select="//SESSION-DATA/DATABASE/ID"/></xsl:variable>
    <xsl:variable name="SEARCH-TYPE"><xsl:value-of select="SESSION-DATA/SEARCH-TYPE"/></xsl:variable>
    
 	<xsl:variable name="DISPLAY-QUERY">
		<xsl:choose>
			<xsl:when test="($SEARCH-CONTEXT='tag')">
				[Tag]
				<xsl:value-of select="/PAGE/ALT-DISPLAY-QUERY" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:value-of select="actionbean:setDisplaydb($actionbean,DD:getDisplayName($DATABASE))"></xsl:value-of>
    <xsl:value-of select="actionbean:setCompmask($actionbean,$COMPMASK)"/>
    <xsl:value-of select="actionbean:setShowmap($actionbean,boolean($COMPMASK='8192' or $COMPMASK='2097152' or $COMPMASK='2105344') and (/PAGE/NAVIGATORS/NAVIGATOR[@NAME='geonav']))"/>
	<xsl:value-of select="actionbean:setDatabase($actionbean,$DATABASE)" />
	<xsl:value-of select="actionbean:setCompmask($actionbean,$COMPMASK)" />
<!-- 	<xsl:value-of select="actionbean:setSearchtype($actionbean,$SEARCH-TYPE)" /> -->
	<xsl:value-of select="actionbean:setResultscount($actionbean,$RESULTS-COUNT)" />
	<xsl:value-of select="actionbean:setResultsperpage($actionbean,//RESULTS-PER-PAGE)" />
	<xsl:value-of select="actionbean:setDisplayquery($actionbean,$DISPLAY-QUERY)" />
	<xsl:value-of select="actionbean:setLindahall($actionbean,boolean(//LHL = 'true'))" />
	
	<xsl:value-of select="actionbean:setStartYear($actionbean,SESSION-DATA/START-YEAR)" />
	<xsl:value-of select="actionbean:setEndYear($actionbean,SESSION-DATA/END-YEAR)" />
	<xsl:value-of select="actionbean:setUpdatesNo($actionbean, SEARCH-PHRASE/UPDATES)"/>
	<xsl:value-of select="actionbean:setEmailalertweek($actionbean, EMAILALERTWEEK)"/>
	<xsl:value-of select="actionbean:setCurrentpage($actionbean,$COUNT)"/>
	<xsl:value-of select="actionbean:setInternalsearch($actionbean,//INTERNALSEARCH)"/>
    <!-- <xsl:value-of select="actionbean:setToc($actionbean,//TOC)"/> -->
	<!--  <xsl:value-of select="actionbean:setCloud($actionbean,//CLOUD)"/> -->
	<xsl:value-of select="actionbean:setBloglink($actionbean,boolean(//BLOGLINK = 'true'))" />
	

	<xsl:if test="DEDUPABLE='true'">
		<xsl:call-template name="DEDUP-SCRIPT" />
	</xsl:if>
	
	<!-- ***************************************************** -->
	<!-- See the CitationRestults stylesheet for this template -->
	<!-- ***************************************************** -->
	<xsl:apply-templates select="PAGE-NAV"/>

	<!-- ***************************************************** -->
	<!-- See the CitationRestults stylesheet for this template -->
	<!-- ***************************************************** -->
	<xsl:apply-templates select="PAGE-RESULTS/PAGE-ENTRY"/>

	<xsl:apply-templates select="//SESSION-DATA/SC" />
	<xsl:apply-templates select="//SESSION-DATA/DATABASE" />

	<xsl:apply-templates select="//SESSION-DATA/REFINE-STACK" />

	<xsl:apply-templates select="SORTABLE-FIELDS" />

	<!-- ***************************************************** -->
	<!-- See the TagBubble stylesheet for this template -->
	<!-- ***************************************************** -->
	<xsl:apply-templates select="TAG-BUBBLE"/>

  </xsl:template>



	<!-- ********************************************* -->
	<!-- Get sort options -->
	<!-- ********************************************* -->
	<xsl:template match="SORTABLE-FIELDS">
		<xsl:variable name="SORTMASK">
			<xsl:choose>
				<xsl:when test="string(/PAGE/NAVIGATORS/COMPMASK)">
					<xsl:value-of select="/PAGE/NAVIGATORS/COMPMASK" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="/PAGE/DBMASK" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:for-each
			select="SORTBYFIELD[(bit:containsBit($SORTMASK,@mask))]/SORT-DIR">
			<xsl:variable name="sortoption" select="sortoption:new()" />
			<xsl:value-of select="sortoption:setField($sortoption, ../@value)" />
			<xsl:value-of select="sortoption:setDefaultdirection($sortoption, ../@defaultdir)" />
			<xsl:value-of select="sortoption:setDisplay($sortoption, ../.)" />
			<xsl:value-of select="sortoption:setDirection($sortoption, @dir)" />
			<xsl:value-of select="actionbean:addSortoption($actionbean, $sortoption)" />
		</xsl:for-each>
	</xsl:template>


	<!-- *************************************************************** -->
	<!-- Parse the Page Navigation                                    -->
	<!-- *************************************************************** -->
	<xsl:template match="PAGE-NAV">
		<xsl:value-of select="actionbean:setPrevqs($actionbean, java:decode(PREV,'UTF-8'))"/>
		<xsl:value-of select="actionbean:setNextqs($actionbean, java:decode(NEXT,'UTF-8'))"/>
		<xsl:value-of select="actionbean:setResultsqs($actionbean, java:decode(RESULTS-NAV,'UTF-8'))"/>
		<xsl:value-of select="actionbean:setNewsearchqs($actionbean, java:decode(NEWSEARCH-NAV,'UTF-8'))"/>
		<xsl:value-of select="actionbean:setDedupnavqs($actionbean, java:decode(DEDUP-RESULTS-NAV,'UTF-8'))"/>
		<xsl:value-of select="actionbean:setAbsnavqs($actionbean, java:decode(ABS-NAV,'UTF-8'))"/>
		<xsl:value-of select="actionbean:setDetnavqs($actionbean, java:decode(DET-NAV,'UTF-8'))"/>
		<xsl:value-of select="actionbean:setBookdetnavqs($actionbean, java:decode(BOOKDET-NAV,'UTF-8'))"/>
		<xsl:value-of select="actionbean:setPagedetnavqs($actionbean, java:decode(PAGEDET-NAV,'UTF-8'))"/>		
	</xsl:template>
</xsl:stylesheet>