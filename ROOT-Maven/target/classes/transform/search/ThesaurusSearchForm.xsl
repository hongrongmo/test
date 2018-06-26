<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY lt '<xsl:text disable-output-escaping="yes">&amp;lt;</xsl:text>'>
  <!ENTITY gt '<xsl:text disable-output-escaping="yes">&amp;gt;</xsl:text>'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:searchhistory="java:org.ei.engvillage.model.SearchHistory"
    xmlns:actionbean="java:org.ei.stripes.action.search.ThesaurusSearchAction"
    exclude-result-prefixes="html xsl"
> 
	
	<xsl:param name="actionbean"/>
	
	<!-- ********************************************************** -->
	<!-- Parse search history elements                              -->
	<!-- ********************************************************** -->
	<xsl:include href="SearchHistory.xsl"/>
	
	<xsl:template match="/DOC">

	    <xsl:value-of select="actionbean:setDatabase($actionbean,//DOC/DATABASE)"/>
	    <xsl:value-of select="actionbean:setUsermask($actionbean,//DOC/USERMASK)"/>
	    <xsl:value-of select="actionbean:setCompendex($actionbean,//DOC/HAS-CPX[.='true'])"/>
	    <xsl:value-of select="actionbean:setInspec($actionbean,//DOC/HAS-INSPEC[.='true'])"/>
	    <xsl:value-of select="actionbean:setGeobase($actionbean,//DOC/HAS-GEO[.='true'])"/>
	    <xsl:value-of select="actionbean:setGeoref($actionbean,//DOC/HAS-GRF[.='true'])"/>
		<xsl:value-of select="actionbean:setUpdatesNo($actionbean, CLIPBOARD/SESSION-DATA/LASTFOURUPDATES)"/>

		<!-- ************************************************************************* -->
		<!-- Set the document type (default + option list)                             -->
		<!-- ************************************************************************* -->
	    <xsl:variable name="DOCUMENT-TYPE-OPTS">
	    	<xsl:call-template name="MakeOptions">
	    		<xsl:with-param name="TYPE">DT</xsl:with-param>
	    		<xsl:with-param name="DEFAULT">
			        <xsl:choose>
			            <xsl:when test="CLIPBOARD/SESSION-DATA/DOCUMENT-TYPE"><xsl:value-of select="CLIPBOARD/SESSION-DATA/DOCUMENT-TYPE"/></xsl:when>
			            <xsl:otherwise>NO-LIMIT</xsl:otherwise>
			        </xsl:choose>
	    		</xsl:with-param>
	    	</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="actionbean:setDoctypeopts($actionbean,$DOCUMENT-TYPE-OPTS)"/>

		<!-- ************************************************************************* -->
		<!-- Set the treatment type (default + option list)                             -->
		<!-- ************************************************************************* -->
	    <xsl:variable name="TREATMENT-TYPE-OPTS">
	    	<xsl:call-template name="MakeOptions">
	    		<xsl:with-param name="TYPE">TR</xsl:with-param>
	    		<xsl:with-param name="DEFAULT">
			        <xsl:choose>
			            <xsl:when test="CLIPBOARD/SESSION-DATA/TREATMENT-TYPE"><xsl:value-of select="CLIPBOARD/SESSION-DATA/TREATMENT-TYPE"/></xsl:when>
			            <xsl:otherwise>NO-LIMIT</xsl:otherwise>
			        </xsl:choose>
	    		</xsl:with-param>
	    	</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="actionbean:setTreatmenttypeopts($actionbean,$TREATMENT-TYPE-OPTS)"/>

		<!-- ************************************************************************* -->
		<!-- Set the discipline type (default + option list)                           -->
		<!-- ************************************************************************* -->
	    <xsl:variable name="DISCIPLINE-TYPE-OPTS">
	    	<xsl:call-template name="MakeOptions">
	    		<xsl:with-param name="TYPE">DI</xsl:with-param>
	    		<xsl:with-param name="DEFAULT">
			        <xsl:choose>
			            <xsl:when test="CLIPBOARD/SESSION-DATA/DISCIPLINE-TYPE"><xsl:value-of select="CLIPBOARD/SESSION-DATA/DISCIPLINE-TYPE"/></xsl:when>
			            <xsl:otherwise>NO-LIMIT</xsl:otherwise>
			        </xsl:choose>
	    		</xsl:with-param>
	    	</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="actionbean:setDisciplinetypeopts($actionbean,$DISCIPLINE-TYPE-OPTS)"/>
	
		<!-- ************************************************************************* -->
		<!-- Set the languages (default + option list)                                 -->
		<!-- ************************************************************************* -->
	    <xsl:variable name="LANGUAGE-OPTS">
	    	<xsl:call-template name="MakeOptions">
	    		<xsl:with-param name="TYPE">LA</xsl:with-param>
	    		<xsl:with-param name="DEFAULT">
			        <xsl:choose>
			            <xsl:when test="CLIPBOARD/SESSION-DATA/LANGUAGE"><xsl:value-of select="CLIPBOARD/SESSION-DATA/LANGUAGE"/></xsl:when>
			            <xsl:otherwise>NO-LIMIT</xsl:otherwise>
			        </xsl:choose>
	    		</xsl:with-param>
	    	</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="actionbean:setLanguageopts($actionbean,$LANGUAGE-OPTS)"/>
	
		<!-- ************************************************************************* -->
		<!-- Set the sort option                                                       -->
		<!-- ************************************************************************* -->
	    <xsl:variable name="SORT-OPTION">
	        <xsl:choose>
	            <xsl:when test="CLIPBOARD/SESSION-DATA/SORT-OPTION"><xsl:value-of select="CLIPBOARD/SESSION-DATA/SORT-OPTION"/></xsl:when>
	            <xsl:otherwise>Relevance</xsl:otherwise>
	        </xsl:choose>
	    </xsl:variable>
		<xsl:value-of select="actionbean:setSort($actionbean,$SORT-OPTION)"/>
	
		<!-- ************************************************************************* -->
		<!-- Set the combine terms option and search box contents                      -->
		<!-- ************************************************************************* -->
		<xsl:if test="CLIPBOARD/CVS"> 
			<xsl:if test="CLIPBOARD/CVS/@CONNECTOR"><xsl:value-of select="actionbean:setCombine($actionbean, CLIPBOARD/CVS/@CONNECTOR)"></xsl:value-of></xsl:if>
		    <xsl:for-each select="CLIPBOARD/CVS/CV">
		    	<xsl:variable name="clipoption">&lt;option value="<xsl:value-of select="text()"/>"&gt;<xsl:value-of select="text()"/>&lt;/option&gt;</xsl:variable>
		    	<xsl:value-of select="actionbean:addClipoption($actionbean,$clipoption)"/>
		    </xsl:for-each>
		</xsl:if>

		<!-- ************************************************************************* -->
		<!-- Create start year and end year option lists                               -->
		<!-- ************************************************************************* -->
		<xsl:variable name="STARTYEAROPTS">
	    	<xsl:call-template name="MakeOptions">
	    		<xsl:with-param name="TYPE">YR</xsl:with-param>
	    		<xsl:with-param name="DEFAULT"><xsl:value-of select="CLIPBOARD/SESSION-DATA/START-YEAR"/></xsl:with-param>
	    	</xsl:call-template>
        </xsl:variable>
		<xsl:value-of select="actionbean:setStartyearopts($actionbean,$STARTYEAROPTS)"/>

		<xsl:variable name="ENDYEAROPTS">
	    	<xsl:call-template name="MakeOptions">
	    		<xsl:with-param name="TYPE">YR</xsl:with-param>
	    		<xsl:with-param name="DEFAULT"><xsl:value-of select="CLIPBOARD/SESSION-DATA/END-YEAR"/></xsl:with-param>
	    	</xsl:call-template>
        </xsl:variable>
		<xsl:value-of select="actionbean:setEndyearopts($actionbean,$ENDYEAROPTS)"/>

	    <!-- Include search history (all search forms) -->
	    <xsl:call-template name="SEARCH-HISTORY">
			<xsl:with-param name="actionbean" select="$actionbean"></xsl:with-param>
		</xsl:call-template>
	    
	    <xsl:apply-templates/>
	</xsl:template>
	
	<!-- ********************************************************** -->
	<!-- Template to create select options for limiters             -->
	<!-- ********************************************************** -->
	<xsl:template name="MakeOptions">
		<xsl:param name="TYPE"></xsl:param>
		<xsl:param name="DEFAULT"></xsl:param>
		<xsl:for-each select="//LIMITERS/CONSTRAINED-FIELD[@SHORTNAME=$TYPE]/OPTIONS/OPTION">
	       &lt;option<xsl:if test="(@SHORTNAME=$DEFAULT)"> selected="selected"</xsl:if> value="<xsl:value-of select="@SHORTNAME"/>"&gt;<xsl:value-of select="@DISPLAYNAME"/>&lt;/option&gt;
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>