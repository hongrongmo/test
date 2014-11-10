<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  xmlns:actionbean="java:org.ei.stripes.action.results.SearchResultsAction"
  xmlns:dedupsummary="java:org.ei.stripes.view.DedupSummary"
  xmlns:removeddups="java:org.ei.stripes.view.RemovedDups"
  xmlns:dbcount="java:org.ei.stripes.view.DbCount"
  exclude-result-prefixes="html xsl xslcid"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template name="DEDUP-SUMMARY">
<xsl:param name="actionbean"></xsl:param>

<xsl:variable name="dedupsummary" select="dedupsummary:new()"/>

  <xsl:variable name="SELECTED-DB">
    <xsl:value-of select="//DBMASK"/>
  </xsl:variable>

  <xsl:variable name="DB-PREF">
    <xsl:value-of select="DBPREF"/>
  </xsl:variable>

  <xsl:variable name="FIELD-PREF">
    <xsl:value-of select="FIELDPREF"/>
  </xsl:variable>

  <xsl:variable name="DEDUP-SET">
    <xsl:value-of select="DEDUPSET"/>
  </xsl:variable>

  <xsl:variable name="DBLINK">
    <xsl:value-of select="DBLINK"/>
  </xsl:variable>

  <xsl:variable name="LINKSET">
    <xsl:value-of select="LINKSET"/>
  </xsl:variable>

  <xsl:variable name="PREF-FIELD">
    <xsl:choose>
      <xsl:when test="($FIELDPREF='0')">No Field</xsl:when>
      <xsl:when test="($FIELDPREF='1')">Abstract</xsl:when>
      <xsl:when test="($FIELDPREF='2')">Index Terms</xsl:when>
      <xsl:when test="($FIELDPREF='4')">Full Text</xsl:when>
      <xsl:otherwise>No Field</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="PREF-DB">
    <xsl:choose>
      <xsl:when test="($DB-PREF='cpx')">Compendex</xsl:when>
      <xsl:when test="($DB-PREF='ins')">Inspec</xsl:when>
      <xsl:when test="($DB-PREF='geo')">GEOBASE</xsl:when>
      <xsl:when test="($DB-PREF='grf')">GeoRef</xsl:when>
      <xsl:when test="($DB-PREF='pch')">PaperChem</xsl:when>
      <xsl:when test="($DB-PREF='elt')">EnCompassLit</xsl:when>
      <xsl:when test="($DB-PREF='chm')">Chimica</xsl:when>
      <xsl:otherwise>Compendex</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="START-YEAR">
    <xsl:value-of select="//SESSION-DATA/START-YEAR"/>
  </xsl:variable>

  <xsl:variable name="END-YEAR">
    <xsl:value-of select="//SESSION-DATA/END-YEAR"/>
  </xsl:variable>

  <xsl:variable name="RESULTS-COUNT">
    <xsl:value-of select="//SESSION-DATA/RESULTS-COUNT"/>
  </xsl:variable>

  <xsl:variable name="CID">
    <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
  </xsl:variable>
  
   <xsl:variable name="REMOVED-DUPS">
    <xsl:value-of select="sum(/PAGE/DEDUPSET-REMOVED-DUPS/REMOVED-DUPS/attribute::COUNT)"/>
  </xsl:variable>

  <xsl:variable name="DEDUPSET-COUNT">
    <xsl:value-of select="sum(DEDUPSET-COUNTER/DB-COUNT/attribute::COUNT)"/>
  </xsl:variable>
  
  <xsl:variable name="REMOVED-SUBSET">
    <xsl:value-of select="//REMOVED-SUBSET"/>
  </xsl:variable>
  
  <xsl:variable name="DEDUPSUBSET">
    <xsl:value-of select="//DEDUPSUBSET"/>
  </xsl:variable>
  
  <xsl:variable name="ORIGIN">
    <xsl:value-of select="//ORIGIN"/>
  </xsl:variable>
  
  

	<xsl:value-of select="actionbean:setStartYear($actionbean,$START-YEAR)"/>
	<xsl:value-of select="actionbean:setEndYear($actionbean,$END-YEAR)"/>
	<xsl:value-of select="dedupsummary:setDbpref($dedupsummary,$DB-PREF)"/>
	<xsl:value-of select="dedupsummary:setFieldpref($dedupsummary,$FIELD-PREF)"/>
	
	<xsl:value-of select="dedupsummary:setDbpreflabel($dedupsummary,$PREF-DB)"/>
	<xsl:value-of select="dedupsummary:setFieldpreflabel($dedupsummary,$PREF-FIELD)"/>
	<xsl:value-of select="dedupsummary:setRemovedDupsCount($dedupsummary,$REMOVED-DUPS)"/>
	<xsl:value-of select="dedupsummary:setDbLink($dedupsummary,$DBLINK)"/>
	<xsl:value-of select="dedupsummary:setLinkSet($dedupsummary,$LINKSET)"/>
	<xsl:value-of select="dedupsummary:setDedupsetCount($dedupsummary,$DEDUPSET-COUNT)"/>
	<xsl:value-of select="dedupsummary:setRemvoedSubsetCount($dedupsummary,$REMOVED-SUBSET)"/>
	<xsl:value-of select="dedupsummary:setDedupSubsetCount($dedupsummary,$DEDUPSUBSET)"/>
	<xsl:value-of select="dedupsummary:setOrigin($dedupsummary,$ORIGIN)"/>
	
	
    <xsl:for-each select="DEDUPSET-REMOVED-DUPS/REMOVED-DUPS">
	     <xsl:variable name="removeddups" select="removeddups:new()"/>
	     
	     <xsl:variable name="dbName" select="@DBNAME"/>
	     <xsl:variable name="name" select="@DB"/>
	     <xsl:variable name="count" select="@COUNT"/>
	     
	     <xsl:value-of select="removeddups:setDbName($removeddups, $dbName)"/>
	     <xsl:value-of select="removeddups:setName($removeddups, $name)"/>
	     <xsl:value-of select="removeddups:setCount($removeddups, $count)"/>
	     
	     <xsl:value-of select="dedupsummary:addRemovedDups($dedupsummary, $removeddups)"/>
   </xsl:for-each>
     
   <xsl:for-each select="DEDUPSET-COUNTER/DB-COUNT">
		 <xsl:variable name="dbcount" select="dbcount:new()"/>
	     
	     <xsl:variable name="dbName" select="@DBNAME"/>
	     <xsl:variable name="name" select="@DB"/>
	     <xsl:variable name="count" select="@COUNT"/>
	     
	     <xsl:value-of select="dbcount:setDbName($dbcount, $dbName)"/>
	     <xsl:value-of select="dbcount:setName($dbcount, $name)"/>
	     <xsl:value-of select="dbcount:setCount($dbcount, $count)"/>
	     
	     <xsl:value-of select="dedupsummary:addDbCount($dedupsummary, $dbcount)"/>

   </xsl:for-each>
              
    <xsl:value-of select="actionbean:setDedupsummary($actionbean, $dedupsummary)"/>
</xsl:template>

</xsl:stylesheet>