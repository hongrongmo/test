<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet []>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:actionbean="java:org.ei.stripes.action.search.ThesaurusSearchAction"
    xmlns:termpath="java:org.ei.stripes.view.thesaurus.TermPath"
    xmlns:fullrecresults="java:org.ei.stripes.view.thesaurus.FullRecResults"
    xmlns:termsearchresults="java:org.ei.stripes.view.thesaurus.TermSearchResults"
    xmlns:termsearchresult="java:org.ei.stripes.view.thesaurus.TermSearchResult"
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
	    
		<xsl:value-of select="actionbean:setTermsearchcount($actionbean,//DOC/COUNT)"/>

		<!-- Build term path elements -->
		<xsl:apply-templates select="TPATH/STEP | DATA/TPATH/STEP"></xsl:apply-templates>
		
		<!-- Build full rec and (optional) list of results -->
		<xsl:apply-templates select="DATA/HIT/TREC"></xsl:apply-templates>
		
		<!-- Process NOHIT results if present -->
		<xsl:if test="DATA/NOHIT/PAGE/TREC">
			<xsl:variable name="termsearchresults" select="termsearchresults:new()"/>
			<xsl:value-of select="actionbean:setTermsearchresults($actionbean, $termsearchresults)"/>
			<xsl:apply-templates select="DATA/NOHIT/PAGE/TREC"/>
		</xsl:if>
			
	</xsl:template>
	
	<!-- ********************************************* -->
	<!-- Build results template                        -->
	<!-- ********************************************* -->
	<xsl:template match="DATA/HIT/TREC"> 
			<xsl:variable name="fullrecresults" select="fullrecresults:new()"/>
			<xsl:value-of select="fullrecresults:setScopenote($fullrecresults, boolean(@INFO='true'))"/>
			<!-- Set the main term -->
			<xsl:variable name="mainterm" select="termsearchresult:new()"/>
			<xsl:value-of select="termsearchresult:setType($mainterm, name(ID/MT/CU | ID/MT/PR | ID/MT/LE))"/>
			<xsl:value-of select="termsearchresult:setTerm($mainterm, ID/MT/CU | ID/MT/PR | ID/MT/LE)"/>
			<xsl:value-of select="termsearchresult:setRnum($mainterm, ID/RNUM)"/>
			<xsl:value-of select="termsearchresult:setNtc($mainterm, NTC)"/>
			<xsl:value-of select="fullrecresults:setMain($fullrecresults, $mainterm)"/>

			<!-- Set the use term if present -->
			<xsl:if test="UT">
				<xsl:variable name="useterm" select="termsearchresult:new()"/>
				<xsl:value-of select="termsearchresult:setType($useterm, name(UT/PAGE/TREC/ID/MT/CU | UT/PAGE/TREC/ID/MT/PR | UT/PAGE/TREC/ID/MT/LE))"/>
				<xsl:value-of select="termsearchresult:setTerm($useterm, UT/PAGE/TREC/ID/MT/CU | UT/PAGE/TREC/ID/MT/PR | UT/PAGE/TREC/ID/MT/LE)"/>
				<xsl:value-of select="termsearchresult:setRnum($useterm, UT/PAGE/TREC/ID/RNUM)"/>
				<xsl:value-of select="termsearchresult:setNtc($useterm, UT/PAGE/TREC/NTC)"/>
				<xsl:value-of select="fullrecresults:setUse($fullrecresults, $useterm)"/>
			</xsl:if>

			<!-- Process term results -->
			<xsl:apply-templates select="NT/PAGE/TREC | BT/PAGE/TREC | RT/PAGE/TREC | LT/PAGE/TREC | PT/PAGE/TREC">
				<xsl:with-param name="fullrecresults" select="$fullrecresults"></xsl:with-param>
			</xsl:apply-templates>
			
			<!-- Set the fullrecresults into the action bean -->
			<xsl:value-of select="actionbean:setFullrecresults($actionbean, $fullrecresults)"/>

	</xsl:template>		
		
	<!-- ********************************************* -->
	<!-- Term path template                            -->
	<!-- ********************************************* -->
	<xsl:include href="ThesaurusTermPath.xsl"/>
	
	<!-- ********************************************* -->
	<!-- Full rec results template                     -->
	<!-- ********************************************* -->
	<xsl:template match="NT/PAGE/TREC | BT/PAGE/TREC | RT/PAGE/TREC | LT/PAGE/TREC | PT/PAGE/TREC">
		<xsl:param name="fullrecresults"></xsl:param>

		<!-- Create result-->
		<xsl:if test="ID/MT/CU | ID/MT/PR | ID/MT/LE">
			<xsl:variable name="termsearchresult" select="termsearchresult:new()"/>
			<xsl:value-of select="termsearchresult:setTerm($termsearchresult, ID/MT/CU | ID/MT/PR | ID/MT/LE)"></xsl:value-of>
			<xsl:value-of select="termsearchresult:setType($termsearchresult, name(ID/MT/CU | ID/MT/PR | ID/MT/LE))"></xsl:value-of>
			<xsl:choose>
			<xsl:when test="ancestor::NT">
				<xsl:value-of select="fullrecresults:addNarrower($fullrecresults, $termsearchresult)"/>
			</xsl:when>
			<xsl:when test="ancestor::BT">
				<xsl:value-of select="fullrecresults:addBroader($fullrecresults, $termsearchresult)"/>
			</xsl:when>
			<xsl:when test="ancestor::RT">
				<xsl:value-of select="fullrecresults:addRelated($fullrecresults, $termsearchresult)"/>
			</xsl:when>
			<xsl:when test="ancestor::LT">
				<xsl:value-of select="fullrecresults:addLeadin($fullrecresults, $termsearchresult)"/>
			</xsl:when>
			<xsl:when test="ancestor::PT">
				<xsl:value-of select="fullrecresults:addLeadin($fullrecresults, $termsearchresult)"/>
			</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	
	<!-- ********************************************* -->
	<!-- NOHIT processing                              -->
	<!-- ********************************************* -->
	<xsl:template match="DATA/NOHIT/PAGE/TREC">
		<!-- Create result-->
		<xsl:if test="ID/MT/CU | ID/MT/PR | ID/MT/LE">
		<xsl:variable name="termsearchresult" select="termsearchresult:new()"/>
		<xsl:value-of select="termsearchresult:setTerm($termsearchresult, ID/MT/CU | ID/MT/PR | ID/MT/LE)"></xsl:value-of>
		<xsl:value-of select="termsearchresult:setType($termsearchresult, name(ID/MT/CU | ID/MT/PR | ID/MT/LE))"></xsl:value-of>
		
		<xsl:value-of select="actionbean:addTermsearchresult($actionbean, $termsearchresult)"/>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>