<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>
<!-- ************************************************************************* -->
<!-- NOTE!!!  The various drop-down (doc type, discipline type, etc.) values   -->
<!-- are NOT set here but instead are handled in the corresponding action      -->
<!-- bean.  See SearchDisplayAction.java                                       -->
<!-- ************************************************************************* -->
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:searchhistory="java:org.ei.engvillage.model.SearchHistory"
    xmlns:ebookitem="java:org.ei.stripes.view.EbookSearchFormItem"
    xmlns:actionbean="java:org.ei.stripes.action.search.BaseSearchAction"
    exclude-result-prefixes="html xsl"
>

	<!-- Personalization variables -->
	<xsl:variable name="PERSONALIZATION"><xsl:value-of select="//PERSONALIZATION"/></xsl:variable>
	<xsl:variable name="PERSONALIZATION-PRESENT"><xsl:value-of select="//PERSONALIZATION-PRESENT"/></xsl:variable>

	<xsl:template match="SESSION-DATA">

	    <xsl:value-of select="actionbean:setSearchid($actionbean,$SEARCH-ID)"/>
    	<xsl:value-of select="actionbean:setSessionid($actionbean,$SESSION-ID)"/>

	    <!-- Is personalization present?  Look for at root level -->
	    <xsl:value-of select="actionbean:setPersonalization($actionbean, //PERSONALIZATION-PRESENT[.='true'])"/>

	    <!-- SESSION-DATA node variables -->
	    <xsl:value-of select="actionbean:setSearchWord1($actionbean, SEARCH-PHRASE/SEARCH-PHRASE-1)"/>
		<xsl:value-of select="actionbean:setSearchWord2($actionbean, SEARCH-PHRASE/SEARCH-PHRASE-2)"/>
		<xsl:value-of select="actionbean:setSearchWord3($actionbean, SEARCH-PHRASE/SEARCH-PHRASE-3)"/>
		<xsl:value-of select="actionbean:setSearchWords($actionbean, SEARCH-PHRASE/SEARCH-PHRASES)"/>
		<xsl:value-of select="actionbean:setBoolean1($actionbean, SEARCH-PHRASE/BOOLEAN-1)"/>
		<xsl:value-of select="actionbean:setBoolean2($actionbean, SEARCH-PHRASE/BOOLEAN-2)"/>
		<xsl:value-of select="actionbean:setBooleans($actionbean, SEARCH-PHRASE/BOOLEANS)"/>
		<xsl:value-of select="actionbean:setSection1($actionbean, SEARCH-PHRASE/SEARCH-OPTION-1)"/>
		<xsl:value-of select="actionbean:setSection2($actionbean, SEARCH-PHRASE/SEARCH-OPTION-2)"/>
		<xsl:value-of select="actionbean:setSection3($actionbean, SEARCH-PHRASE/SEARCH-OPTION-3)"/>
		<xsl:value-of select="actionbean:setSections($actionbean, SEARCH-PHRASE/SEARCH-OPTIONS)"/>
		<xsl:value-of select="actionbean:setUpdatesNo($actionbean, LASTFOURUPDATES)"/>
		<xsl:value-of select="actionbean:setEmailalertweek($actionbean, EMAILALERTWEEK)"/>
		<xsl:value-of select="actionbean:setDoctype($actionbean, DOCUMENT-TYPE)"/>
		<xsl:value-of select="actionbean:setTreatmentType($actionbean, TREATMENT-TYPE)"/>
		<xsl:value-of select="actionbean:setLanguage($actionbean, LANGUAGE)"/>
		<xsl:value-of select="actionbean:setDisciplinetype($actionbean, DISCIPLINE-TYPE)"/>
		<xsl:value-of select="actionbean:setSort($actionbean, SORT-OPTION)"/>
		<xsl:value-of select="actionbean:setStartYear($actionbean, START-YEAR)"/>
		<xsl:value-of select="actionbean:setEndYear($actionbean, END-YEAR)"/>
		<xsl:value-of select="actionbean:setAutostem($actionbean, AUTOSTEMMING)"/>

	</xsl:template>

</xsl:stylesheet>