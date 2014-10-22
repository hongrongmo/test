<?xml version="1.0" ?>
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
    xmlns:actionbean="java:org.ei.stripes.action.search.SearchDisplayAction"
    exclude-result-prefixes="html xsl"
>

	<xsl:param name="actionbean"/>
	
	<!-- Search and Session IDs -->
	<xsl:variable name="SEARCH-ID"><xsl:value-of select="//SEARCH-ID" /></xsl:variable>
	<xsl:variable name="SESSION-ID"><xsl:value-of select="//SESSION-ID"/></xsl:variable>

	<!-- ********************************************************** -->
	<!-- Parse search history elements                              -->
	<!-- ********************************************************** -->
	<xsl:include href="SearchHistory.xsl"/>

	<!-- ********************************************************** -->
	<!-- Parse search form (state) elements                         -->
	<!-- ********************************************************** -->
	<xsl:include href="SearchForm.xsl"/>

	<xsl:template match="/QUICK-SEARCH">

		<!-- SEARCH FORM STATE -->	    
	    <xsl:apply-templates select="SESSION-DATA"></xsl:apply-templates>
	    
		<!-- The usermask variable -->	    
		<xsl:value-of select="actionbean:setUsermask($actionbean, USERMASK)"/>
		
   		<!-- Set the list of available years by database -->
		<xsl:value-of select="actionbean:setStringYear($actionbean, YEAR-STRING)"/>

		<!-- eBook present? -->	    
		<xsl:if test="//EBOOK-SEARCH">
			<xsl:value-of select="actionbean:setCreds($actionbean, //CREDS)"/>
			<xsl:for-each select="SESSION-DATA/COLLECTIONS/COLL">
				<xsl:value-of select="actionbean:addSelcol($actionbean, text())"/>
			</xsl:for-each>
			<xsl:apply-templates select="//EBOOK-SEARCH/*">
			</xsl:apply-templates>
		</xsl:if>
	    
	    <!-- Include search history (all search forms) -->
	    <xsl:call-template name="SEARCH-HISTORY">
			<xsl:with-param name="actionbean" select="$actionbean"></xsl:with-param>
		</xsl:call-template>
		<xsl:value-of select="actionbean:setAlertcount($actionbean, /PAGE/SESSION-HISTORY/EMAIL-ALERT-COUNT)"></xsl:value-of>
		<xsl:value-of select="actionbean:setSavedcount($actionbean, /PAGE/SESSION-HISTORY/SAVEDSEARCHES-COUNT)"></xsl:value-of>
	    
	</xsl:template>

	<!-- *************************************************************** -->
	<!-- Parse eBook elements                                            -->
	<!-- *************************************************************** -->
	<xsl:template match="//EBOOK-SEARCH/*">
		<xsl:variable name="ebookitem" select="ebookitem:new()"/>
		<xsl:value-of select="ebookitem:setShortname($ebookitem, SHORTNAME)"/>
		<xsl:value-of select="ebookitem:setDisplayname($ebookitem, DISPLAYNAME)"/>
		<xsl:value-of select="ebookitem:setSubcount($ebookitem, SUBCOUNT)"/>
		<xsl:for-each select="CVS/CV">
			<xsl:value-of select="ebookitem:addSearchlink($ebookitem, text())"/>
		</xsl:for-each>
		<xsl:value-of select="actionbean:addEbookitem($actionbean, $ebookitem)"/>
	</xsl:template>
	

</xsl:stylesheet>