<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet []>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:actionbean="java:org.ei.stripes.action.search.ThesaurusSearchAction"
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
	    
	    <!-- Special page navigation by index for browse -->
		<xsl:variable name="pagenavigation" select="pagenavigation:new()"/>
		<xsl:value-of select="pagenavigation:setPrevindex($pagenavigation, DATA/PINDEX)"></xsl:value-of>
		<xsl:value-of select="pagenavigation:setNextindex($pagenavigation, DATA/NINDEX)"></xsl:value-of>
		<xsl:value-of select="actionbean:setPagenav($actionbean, $pagenavigation)"/>		
		
		<!-- Build term path elements -->
		<xsl:apply-templates select="TPATH/STEP | DATA/TPATH/STEP"></xsl:apply-templates>
		
		<!-- Process results (always present) -->
		<xsl:variable name="termsearchresults" select="termsearchresults:new()"/>
		<xsl:value-of select="actionbean:setTermsearchresults($actionbean, $termsearchresults)"/>
		<xsl:apply-templates select="DATA/BROWSE/PAGE/TREC"/>
			
	</xsl:template>
	
	<!-- ********************************************* -->
	<!-- Term path template                            -->
	<!-- ********************************************* -->
	<xsl:include href="ThesaurusTermPath.xsl"/>
	
	<!-- ********************************************* -->
	<!-- Results processing                            -->
	<!-- ********************************************* -->
	<xsl:template match="DATA/BROWSE/PAGE/TREC">
		<!-- Create result-->
		<xsl:variable name="termsearchresult" select="termsearchresult:new()"/>
		<xsl:value-of select="termsearchresult:setTerm($termsearchresult, ID/MT/CU | ID/MT/PR | ID/MT/LE)"></xsl:value-of>
		<xsl:value-of select="termsearchresult:setType($termsearchresult, name(ID/MT/CU | ID/MT/PR | ID/MT/LE))"></xsl:value-of>
		
		<xsl:value-of select="actionbean:addTermsearchresult($actionbean, $termsearchresult)"/>
	</xsl:template>
	
</xsl:stylesheet>