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
    xmlns:discipline="java:org.ei.stripes.view.Discipline"
    xmlns:guru="java:org.ei.stripes.view.Guru"
    xmlns:search="java:org.ei.stripes.view.Search"
    xmlns:actionbean="java:org.ei.stripes.action.referenceServices.ReferenceServicesAction"
    xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java html xsl"
>
	<xsl:param name="actionbean"/>
	

	
	<!-- ********************************************************** -->
	<!-- Main template start                                        -->
	<!-- ********************************************************** -->
	<xsl:template match="/PAGE">

			<xsl:value-of select="actionbean:setLibrarianEmail($actionbean, /ASKALIBRARIAN/EIEMAIL/text())"/>
			<xsl:value-of select="actionbean:setSessionid($actionbean, SESSION-ID)"/>
			<xsl:variable name="gurusValue" select="./GURUS"></xsl:variable>
			<xsl:apply-templates select="DISCIPLINES/DISCIPLINE">
			<xsl:with-param name="gurus" select="$gurusValue"></xsl:with-param>
			</xsl:apply-templates>
	    
   	
	</xsl:template>
	
	
<xsl:template match="DISCIPLINES/DISCIPLINE">
     <xsl:param  name="gurus"></xsl:param>
	<xsl:variable name="discipline" select="discipline:new()"/>
	<xsl:variable name="disciplineName" select="@NAME"/>
	<xsl:value-of select="discipline:setName($discipline, $disciplineName)"/>
	
	<xsl:for-each select="GURU">
		<xsl:variable name="guruItem" select="guru:new()"/>
	
		 <xsl:variable name="name" select="@NAME"/>
		
		<xsl:value-of select="guru:setName($guruItem, $name)"/>
		
		<xsl:for-each select="SEARCHES/SEARCH">
			<xsl:variable name="searchItem" select="search:new()"/>
		
			<xsl:variable name="name1" select="@NAME"/>
			<xsl:variable name="href" select="./text()"/>
			
			<xsl:value-of select="search:setName($searchItem, $name1)"/>
			<xsl:value-of select="search:setHref($searchItem, $href)"/>
			 
			<xsl:value-of select="guru:addItem($guruItem, $searchItem)"/>
			
		</xsl:for-each>	
		     <xsl:variable name="searchLinkVal" select="$gurus/GURU[@NAME=$name]/GURU_SEARCHLINK/text()"/>
		     <xsl:variable name="guruInfoVal" select="$gurus/GURU[@NAME=$name]/GURU_INFO/text()"/>
		     <xsl:value-of select="guru:setGuruInfo($guruItem, $guruInfoVal)"/>
		     
		     <xsl:value-of select="guru:setSearchLink($guruItem, $searchLinkVal)"/>
			 <xsl:value-of select="discipline:addGuru($discipline, $guruItem)"/>
			 
	</xsl:for-each>
	
	<xsl:value-of select="actionbean:addDiscipline($actionbean, $disciplineName, $discipline)"/>
</xsl:template>


<!-- <xsl:template match="DISCIPLINES/DISCIPLINE">
<xsl:param  name="gurus"></xsl:param>
<p1><xsl:value-of select="$gurus"/></p1>	   
	<xsl:variable name="disciplineName" select="@NAME"/>
	
	<xsl:for-each select="GURU">
	
		 <xsl:variable name="name" select="@NAME"/>
		
		
		<xsl:for-each select="SEARCHES/SEARCH">
		
			<xsl:variable name="name1" select="@NAME"/>
			<xsl:variable name="href" select="./text()"/>
				 
			
		</xsl:for-each>	
		     <p2><xsl:value-of select="$gurus/GURU[@NAME=$name]/GURU_SEARCHLINK/text()"></xsl:value-of></p2>
			 
	</xsl:for-each>
	
	<xsl:value-of select="guru:set($guru, ./GURU_SEARCHLINK/text())"/>
	
</xsl:template>
 -->


</xsl:stylesheet>