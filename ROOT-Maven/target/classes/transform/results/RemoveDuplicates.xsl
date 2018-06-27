<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:actionbean="java:org.ei.stripes.action.results.DedupResultsAction"
    xmlns:selectoption="java:org.ei.domain.SelectOption"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="xsl">


<xsl:param name="actionbean"/>
<xsl:template match="PAGE">

    <xsl:value-of select="actionbean:setSearchid($actionbean,SEARCH-ID)"/>
    <xsl:value-of select="actionbean:setSessionid($actionbean,SESSION-ID)"/>
    <xsl:value-of select="actionbean:setDatabase($actionbean,DBMASK)"/>
    <xsl:value-of select="actionbean:setCount($actionbean,COUNT)"/>
    <xsl:value-of select="actionbean:setSearchtype($actionbean,SEARCH-TYPE)"/>
    <xsl:value-of select="actionbean:setResultscount($actionbean,RESULTS-COUNT)"/>

	<!-- Set the field preference -->
	<xsl:variable name="FIELDPREF">
	  <xsl:choose>
	    <xsl:when test="/PAGE/FORM[@NAME='removedup']/FIELDPREF">
	      <xsl:value-of select="/PAGE/FORM[@NAME='removedup']/FIELDPREF"/>
	    </xsl:when>
	    <xsl:otherwise>0</xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
    <xsl:value-of select="actionbean:setFieldpref($actionbean,$FIELDPREF)"/>

	<!-- Create the list of databases for the dedup preference -->
	<xsl:for-each select="DEDUPABLE-DB/DB">
		<xsl:variable name="option" select="selectoption:new()"/>
	    <xsl:value-of select="selectoption:setLabel($option,@NAME)"/>
	    <xsl:value-of select="selectoption:setValue($option,@ID)"/>
	    <xsl:value-of select="selectoption:setSelected($option,boolean(/PAGE/FORM[@NAME='removedup']/DBPREF=@ID))"/>
	    <xsl:value-of select="actionbean:addDatabasepref($actionbean,$option)"/>
	</xsl:for-each>

</xsl:template>
</xsl:stylesheet>
