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
    xmlns:actionbean="java:org.ei.stripes.action.results.SearchResultsAction"
    xmlns:searchresult="java:org.ei.stripes.view.SearchResult"
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
	<!-- Parse search form (state) elements                         -->
	<!-- ********************************************************** -->
	<xsl:include href="../search/SearchForm.xsl"/>

	<!-- ********************************************************** -->
	<!-- INDIVIDUAL RESULTS (depends on HREF-PREFIX!) -->
	<!-- ********************************************************** -->
	<xsl:variable name="HREF-PREFIX"/>
	<xsl:include href="common/CitationResults.xsl"/>
	<xsl:include href="SearchResultsPatentRefsFormat.xsl"/>


	<xsl:variable name="SEARCH-TYPE"><xsl:value-of select="//SEARCH-TYPE"/></xsl:variable>
    <xsl:variable name="RERUN-CID"><xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/></xsl:variable>
	<xsl:variable name="RESULTS-COUNT"><xsl:value-of select="//RESULTS-COUNT"/></xsl:variable>
	<xsl:variable name="COUNT"><xsl:value-of select="//CURR-PAGE-ID"/></xsl:variable>
	<xsl:variable name="DISPLAY-QUERY"><xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/></xsl:variable>
	<xsl:variable name="I-QUERY"><xsl:value-of select="//SESSION-DATA/I-QUERY"/></xsl:variable>
	<xsl:variable name="QUERY-ID"><xsl:value-of select="//SEARCH-ID"/></xsl:variable>
	<xsl:variable name="DATABASE"><xsl:value-of select="/PAGE/DBMASK"/></xsl:variable>
	<xsl:variable name="BACKURL"><xsl:value-of select="/PAGE/BACKURL"/></xsl:variable>
	<xsl:variable name="RSS-LINK"><xsl:value-of select="/PAGE/RSSLINK"/></xsl:variable>
	<xsl:variable name="SAVEDSEARCH"><xsl:value-of select="//SESSION-DATA/SAVEDSEARCH"/></xsl:variable>
	<xsl:variable name="EMAILALERT"><xsl:value-of select="//SESSION-DATA/EMAIL-ALERT"/></xsl:variable>
	<xsl:variable name="COMPMASK"><xsl:value-of select="/PAGE/NAVIGATORS/COMPMASK"/></xsl:variable>
	<xsl:variable name="FULLTEXT"><xsl:value-of select="//FULLTEXT"/></xsl:variable>
	<xsl:variable name="CURRENT-PAGE"><xsl:value-of select="//CURR-PAGE-ID"/></xsl:variable>
	<xsl:variable name="CHINA"><xsl:value-of select="build:china()"/></xsl:variable>

	<!-- ********************************************************** -->
	<!-- Main template start                                        -->
	<!-- ********************************************************** -->
	<xsl:template match="/PAGE">
		<!-- SEARCH FORM STATE -->
	    <xsl:apply-templates select="SESSION-DATA"></xsl:apply-templates>

	    <xsl:value-of select="actionbean:setChina($actionbean,boolean('true'=$CHINA))"/>

	    <xsl:value-of select="actionbean:setDatabase($actionbean,$DATABASE)"/>
	    <xsl:value-of select="actionbean:setCompmask($actionbean,$COMPMASK)"/>
	    <xsl:value-of select="actionbean:setSearchtype($actionbean,$SEARCH-TYPE)"/>
	    <xsl:value-of select="actionbean:setReruncid($actionbean,$RERUN-CID)"/>
	    <xsl:value-of select="actionbean:setBackurl($actionbean,$BACKURL)"/>
	    <xsl:value-of select="actionbean:setResultscount($actionbean,$RESULTS-COUNT)"/>
	    <xsl:value-of select="actionbean:setResultsperpage($actionbean,//RESULTS-PER-PAGE)"/>
	    <xsl:value-of select="actionbean:setDisplayquery($actionbean,$DISPLAY-QUERY)"/>

		<xsl:value-of select="actionbean:setSortdir($actionbean, /PAGE/SESSION-DATA/SORT-DIRECTION)"/>

		<xsl:value-of select="actionbean:setFulltextenabled($actionbean, boolean('true'=$FULLTEXT))"/>
		<xsl:value-of select="actionbean:setRemoveduplicates($actionbean, boolean('true'=//DEDUPABLE))"/>
		<xsl:value-of select="actionbean:setClearonnewsearch($actionbean, boolean('true'=//CLEAR-ON-VALUE))"/>
		<xsl:value-of select="actionbean:setPatentref($actionbean, boolean('true'))"/>
		<xsl:value-of select="actionbean:setDocindex($actionbean,//DOCINDEX)"/>
		<xsl:value-of select="actionbean:setIsnavchrt($actionbean, boolean('false'=//NAVCHRT))"/>

		<!-- Build the saved search link -->
		<!--
		<xsl:variable name="SAVEDSEARCH-LINK">
			<xsl:call-template name="SAVEDSEARCH"/>
		</xsl:variable>
		<xsl:value-of select="actionbean:setSavedsearchlink($actionbean, $SAVEDSEARCH-LINK)"/>
		 -->

		<!-- Build the create alert link -->
		<!--
		<xsl:variable name="CREATEALERT-LINK" >
			<xsl:call-template name="CREATEALERT" />
		</xsl:variable>
		<xsl:value-of select="actionbean:setCreatealertlink($actionbean, $CREATEALERT-LINK)"/>
		-->

		<xsl:value-of select="actionbean:setSavedsearchflag($actionbean, $SAVEDSEARCH )"/>
		<xsl:value-of select="actionbean:setCreatealertflag($actionbean, $EMAILALERT)"/>

        <!-- Build the RSS link -->
        <!-- JAM - suppress RSS link if this is a Book search or the results only include books -->
        <xsl:if test="(/PAGE/RSSLINK='true') and not(($SEARCH-TYPE='Book') or $COMPMASK='131072')">
        <xsl:variable name="RSS-LINK">
&lt;a href="#" title="Create an RSS feed for this search query" onClick="window.open('/rss/display.url?database=<xsl:value-of select="$DATABASE"/>&amp;term1=<xsl:value-of select="java:encode($I-QUERY)"/>','newwindow','width=700,height=500,toolbar=no,location=no,scrollbars,resizable');return false;"&gt;RSS feed&lt;/a&gt;
		</xsl:variable>
		<xsl:value-of select="actionbean:setRsslink($actionbean, $RSS-LINK)"/>
		</xsl:if>


		<!-- Build the query display ONLY when > refine element present -->
		<xsl:if test="count(//REFINE-STACK/NAVIGATOR) > 1">
		<xsl:apply-templates select="//REFINE-STACK/NAVIGATOR"/>
		</xsl:if>

		<!-- Add the sort options -->
		<xsl:apply-templates select="SORTABLE-FIELDS"/>

		<!-- Add the Patent ref results or other ref based on CID  -->

		<xsl:choose>
       		<xsl:when test="($CID='quickSearchNonPatentReferencesFormat' or $CID='expertSearchNonPatentReferencesFormat')">

				 <xsl:apply-templates select="PAGE-RESULTS/UPT-DOC/REF-DOCS/PAGE-ENTRY/NP-DOC">
			    	<xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"></xsl:with-param>
			    </xsl:apply-templates>
			</xsl:when>
       		<xsl:otherwise>

       			<xsl:apply-templates select="PAGE-RESULTS/UPT-DOC/REF-DOCS/PAGE-ENTRY">
			    	<xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"></xsl:with-param>
			    </xsl:apply-templates>
			 </xsl:otherwise>
        </xsl:choose>

	   <xsl:apply-templates select="PAGE-RESULTS/UPT-DOC"/>

		<!-- Add navigators -->
	    <xsl:apply-templates select="NAVIGATORS/NAVIGATOR"/>

    	<!-- Add page navigation -->
		<xsl:call-template name="PAGENAVIGATION"/>

	</xsl:template>

	<!-- *************************************************************** -->
	<!-- Parse the navigator results                                     -->
	<!-- *************************************************************** -->
	<xsl:template match="NAVIGATORS/NAVIGATOR">
		<xsl:variable name="navigator" select="navigator:new()"/>
		<xsl:value-of select="navigator:setName($navigator, @NAME)"/>
		<xsl:value-of select="navigator:setLabel($navigator, @LABEL)"/>
		<xsl:value-of select="navigator:setState($navigator, @INCLUDEALL)"/>
		<xsl:value-of select="navigator:setMore($navigator, PAGERS/MORE/@COUNT)"/>
		<xsl:value-of select="navigator:setLess($navigator, PAGERS/LESS/@COUNT)"/>
		<xsl:value-of select="navigator:setTotalSize($navigator, PAGERS/TOTALSIZE/@COUNT)"/>
		<xsl:value-of select="navigator:setField($navigator, @FIELD)"/>
		<xsl:value-of select="navigator:setOpen($navigator, @OPEN)"/>
		<!-- add the navigator items -->
		<xsl:for-each select="MODIFIER">
		    <!-- new navigator item -->
	    	<xsl:variable name="item" select="navigatoritem:new()"/>
	    	<!-- load data into variables -->
	    	<xsl:variable name="count" select="@COUNT"/>
	    	<xsl:variable name="value" select="./VALUE/text()"/>
	    	<xsl:variable name="label" select="./LABEL/text()"/>
	    	<!-- add data to item bean object -->
	    	<xsl:value-of select="navigatoritem:setCount($item, $count)"/>
	    	<xsl:value-of select="navigatoritem:setValue($item, $value)"/>
	    	<xsl:value-of select="navigatoritem:setLabel($item, $label)"/>
	    	<!--  -->
	    	<!-- add item bean object to navigator -->
	        <xsl:value-of select="navigator:addItem($navigator, $item)"/>
	    </xsl:for-each>
	    <!--  add navigator to actionbean -->
	    <xsl:value-of select="actionbean:addNavigator($actionbean, $navigator)"/>
	</xsl:template>

	<!-- *************************************************************** -->
	<!-- Parse the refine query information                              -->
	<!-- *************************************************************** -->
	<xsl:template match="REFINE-STACK/NAVIGATOR">
	    <!-- new navigator item -->
    	<xsl:variable name="navigatoritem" select="navigatoritem:new()"/>
    	<!-- load data into variables -->
    	<xsl:value-of select="navigatoritem:setCount($navigatoritem, MODIFIER/@COUNT)"/>
    	<xsl:value-of select="navigatoritem:setValue($navigatoritem, MODIFIER/VALUE)"/>
    	<xsl:value-of select="navigatoritem:setLabel($navigatoritem, MODIFIER/LABEL)"/>
    	<xsl:value-of select="navigatoritem:setIncludeall($navigatoritem, @INCLUDEALL)"/>
 	    <xsl:value-of select="actionbean:addRefinequery($actionbean, $navigatoritem)"/>
 	</xsl:template>

	<!-- ********************************************* -->
	<!-- Get sort options				               -->
	<!-- ********************************************* -->
	<xsl:template match="SORTABLE-FIELDS">
	    <xsl:variable name="SORTMASK">
	      <xsl:choose>
	        <xsl:when test="string(/PAGE/NAVIGATORS/COMPMASK)">
	          <xsl:value-of select="/PAGE/NAVIGATORS/COMPMASK"/>
	        </xsl:when>
	        <xsl:otherwise>
	          <xsl:value-of select="/PAGE/DBMASK"/>
	        </xsl:otherwise>
	      </xsl:choose>
	    </xsl:variable>

	    <xsl:for-each select="SORTBYFIELD[(bit:containsBit($SORTMASK,@mask))]/SORT-DIR">
				<xsl:variable name="sortoption" select="sortoption:new()"/>
				<xsl:value-of select="sortoption:setField($sortoption, ../@value)"/>
				<xsl:value-of select="sortoption:setDefaultdirection($sortoption, ../@defaultdir)"/>
				<xsl:value-of select="sortoption:setDisplay($sortoption, ../.)"/>
				<xsl:value-of select="sortoption:setDirection($sortoption, @dir)"/>
				<xsl:value-of select="actionbean:addSortoption($actionbean, $sortoption)"/>
	    </xsl:for-each>
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

		<xsl:value-of select="actionbean:setPagenav($actionbean, $pagenavigation)"/>
	</xsl:template>

	<xsl:template match="PASM">
		<xsl:param name="sr"/>
		<xsl:variable name="LABEL"><xsl:value-of select="@label" /></xsl:variable>
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
		<xsl:choose>
			<xsl:when test="PAS">
				<xsl:for-each select="PAS">
					<xsl:variable name="LINK">
						<xsl:value-of select="name(.)"/>
					</xsl:variable>
				   <xsl:value-of select="searchresult:addAssigneelink($sr,$LINK)"/>
			   </xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
			    <xsl:value-of select="searchresult:addAssigneelink($sr,text())"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>