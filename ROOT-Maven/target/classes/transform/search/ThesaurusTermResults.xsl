<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:actionbean="java:org.ei.stripes.action.search.ThesaurusAjaxAction"
    xmlns:termpath="java:org.ei.stripes.view.thesaurus.TermPath"
    xmlns:termsearchresults="java:org.ei.stripes.view.thesaurus.TermSearchResults"
    xmlns:termsearchresult="java:org.ei.stripes.view.thesaurus.TermSearchResult"
    xmlns:pagenavigation="java:org.ei.stripes.view.PageNavigation"
    exclude-result-prefixes="html xsl"
>
	<xsl:param name="actionbean"/>
	
	<xsl:template match="/DOC">
	    <xsl:value-of select="actionbean:setSessionid($actionbean,//SESSION-ID)"/>
	    <xsl:value-of select="actionbean:setDatabase($actionbean,//DOC/DATABASE)"/>
	    <xsl:value-of select="actionbean:setCompendex($actionbean,//DOC/HAS-CPX[.='true'])"/>
	    <xsl:value-of select="actionbean:setInspec($actionbean,//DOC/HAS-INSPEC[.='true'])"/>
	    <xsl:value-of select="actionbean:setGeobase($actionbean,//DOC/HAS-GEO[.='true'])"/>
	    <xsl:value-of select="actionbean:setGeoref($actionbean,//DOC/HAS-GRF[.='true'])"/>
	    <xsl:value-of select="actionbean:setTerm($actionbean,//DOC/TERM)"/>
	    
		<!-- Build term path elements -->
		<xsl:apply-templates select="TPATH/STEP | DATA/TPATH/STEP"></xsl:apply-templates>
		
		<!-- Build list of results -->
		<xsl:variable name="termsearchresults" select="termsearchresults:new()"/>
		<xsl:value-of select="termsearchresults:setMode($termsearchresults, name(SEARCH | SUGGEST | NOSUGGEST))"/>
		<xsl:value-of select="actionbean:setTermsearchresults($actionbean, $termsearchresults)"/>
		<xsl:apply-templates select="SEARCH/PAGE/TREC | SUGGEST/PAGE/TREC | NOSUGGEST/PAGE/TREC"/>
		
		<!-- Add page navigation -->
		<xsl:apply-templates select="NPAGES"/>
	</xsl:template>
	
	<!-- ********************************************* -->
	<!-- Term path template                            -->
	<!-- ********************************************* -->
	<xsl:include href="ThesaurusTermPath.xsl"/>
	
	<!-- ********************************************* -->
	<!-- Term search results template                  -->
	<!-- ********************************************* -->
	<xsl:template match="SEARCH/PAGE/TREC | SUGGEST/PAGE/TREC | NOSUGGEST/PAGE/TREC">

		<!-- Create result-->
		<xsl:variable name="termsearchresult" select="termsearchresult:new()"/>
		<xsl:value-of select="termsearchresult:setTerm($termsearchresult, ID/MT/CU | ID/MT/PR | ID/MT/LE)"></xsl:value-of>
		<xsl:value-of select="termsearchresult:setType($termsearchresult, name(ID/MT/CU | ID/MT/PR | ID/MT/LE))"></xsl:value-of>
		
		<xsl:value-of select="actionbean:addTermsearchresult($actionbean, $termsearchresult)"/>
	</xsl:template>
	
	<!-- ********************************************* -->
	<!-- Term search page navigation template          -->
	<!-- ********************************************* -->
	<xsl:template match="NPAGES">
		<xsl:variable name="pagenavigation" select="pagenavigation:new()"/>
		<xsl:value-of select="pagenavigation:setResultscount($pagenavigation, //DOC/COUNT)"></xsl:value-of>
		<xsl:value-of select="pagenavigation:setCurrentpage($pagenavigation, NPAGE[@CURR='CURR']/PNUM)"></xsl:value-of>
		<xsl:value-of select="pagenavigation:setResultsperpage($pagenavigation, 10)"></xsl:value-of>
		
		<xsl:value-of select="actionbean:setPagenav($actionbean, $pagenavigation)"/>
	</xsl:template>
</xsl:stylesheet>