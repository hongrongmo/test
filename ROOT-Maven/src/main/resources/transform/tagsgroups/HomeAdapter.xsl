<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tag="java:org.ei.tags.Tag"
    xmlns:taggroup="java:org.ei.tags.TagGroup"
    xmlns:actionbean="java:org.ei.stripes.action.TagsGroupsAction"
    exclude-result-prefixes="xsl actionbean"
>
	<xsl:param name="actionbean"/>

	<!-- ********************************************************** -->
	<!-- Main template start                                        -->
	<!-- ********************************************************** -->
	<xsl:template match="/PAGE">
	    <xsl:value-of select="actionbean:setDatabase($actionbean,DBMASK)"/>
	    <xsl:value-of select="actionbean:setSearchtype($actionbean,SEARCH-TYPE)"/>
	    <xsl:value-of select="actionbean:setBackurl($actionbean,BACKURL)"/>
	    <xsl:value-of select="actionbean:setResultscount($actionbean,RESULTS-COUNT)"/>
	    <xsl:value-of select="actionbean:setSearchid($actionbean,SEARCH-ID)"/>
	    <xsl:value-of select="actionbean:setUserid($actionbean,USERID)"/>
	    <xsl:if test="SORT">
	    	<xsl:value-of select="actionbean:setSort($actionbean,SORT/@type)"/>
		    <xsl:value-of select="actionbean:setScope($actionbean,SORT/@scope)"/>
	    </xsl:if>
	    
	    <xsl:apply-templates select="CLOUD/CTAG"/>

	    <xsl:apply-templates select="SCOPES/SCOPE"/>

	</xsl:template>

	<!-- ********************************************************** -->
	<!-- Template for cloud tags                                    -->
	<!-- ********************************************************** -->
	<xsl:template match="CLOUD/CTAG">
		<xsl:variable name="taggroup" select="taggroup:new()"/>
		<xsl:value-of select="taggroup:setGroupID($taggroup, TGROUP/GID)"/>
		<xsl:variable name="COLOR">
		  <xsl:choose>
		    <xsl:when test="(CSCOPE='1')">#002D68</xsl:when>
		    <xsl:when test="(CSCOPE='2')">#A3B710</xsl:when>
		    <xsl:when test="(CSCOPE='4')">#A56500</xsl:when>
		    <xsl:when test="(CSCOPE='3')"><xsl:value-of select="TGROUP/COLOR/CODE"/></xsl:when>
		  </xsl:choose>
		</xsl:variable>
		<xsl:value-of select="taggroup:setColor($taggroup,$COLOR)"/>

		<xsl:variable name="tag" select="tag:new()"/>
		<xsl:value-of select="tag:setTag($tag, text())"/>
		<xsl:value-of select="tag:setSize($tag, @class)"/>
		<xsl:value-of select="tag:setGroup($tag, $taggroup)"/>
		<xsl:value-of select="tag:setScope($tag,CSCOPE)"/>
		
		
		<xsl:value-of select="actionbean:addTag($actionbean, $tag)"/>
	</xsl:template>

	<!-- ********************************************************** -->
	<!-- Template for scope tags                                    -->
	<!-- ********************************************************** -->
	<xsl:template match="SCOPES/SCOPE">
		<xsl:value-of select="actionbean:addScope($actionbean, @name, @value)"/>
	</xsl:template>
</xsl:stylesheet>