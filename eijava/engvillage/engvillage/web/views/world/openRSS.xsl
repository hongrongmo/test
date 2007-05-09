<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
  exclude-result-prefixes="xsl DD">
<xsl:output method="html"/>



<xsl:variable name="SERVER">
  <xsl:value-of select="/SECTION-DELIM/SERVER"/>
</xsl:variable>


<xsl:template match="SECTION-DELIM">
    <xsl:apply-templates select="HEADER"/>
  <xsl:apply-templates select="EI-DOCUMENT"/>
  <xsl:apply-templates select="FOOTER"/>
</xsl:template>


<xsl:template match="HEADER">
        <xsl:text disable-output-escaping="yes"><![CDATA[<rss version="2.0"><channel>]]></xsl:text>
        <title><xsl:value-of select="/SECTION-DELIM/CATEGORY-TITLE"/></title>
  <xsl:text disable-output-escaping="yes"><![CDATA[<link>]]></xsl:text>
    http://www.engineeringvillage.com/controller/servlet/Controller?CID=openRSS&amp;SYSTEM_PT=t&amp;queryID=<xsl:value-of select="SEARCH-ID"/>
  <xsl:text disable-output-escaping="yes"><![CDATA[</link>]]></xsl:text>

  <description>Engineering Village RSS results for database
    <xsl:value-of select="DD:getDisplayName(/SECTION-DELIM/DBMASK)"/>
    and search query of <xsl:value-of select="/SECTION-DELIM/CATEGORY-TITLE"/>
  </description>
  <language>en-us</language>
  <ttl>2880</ttl>
  <copyright>Copyright 2007 Elsevier Inc. All rights reserved.</copyright>
</xsl:template>

<xsl:template match="FOOTER">
        <xsl:text disable-output-escaping="yes">
            <![CDATA[</channel></rss>]]>
        </xsl:text>
</xsl:template>

<xsl:template match="EI-DOCUMENT">
  <item>
    <title><xsl:value-of select="TI"/></title>
    <xsl:text disable-output-escaping="yes"><![CDATA[<link>]]></xsl:text>
      <xsl:text>http://</xsl:text>
      <xsl:value-of select="$SERVER" />
      <xsl:text>/controller/servlet/Controller?</xsl:text>
      <xsl:text>CID=expertSearchCitationFormat</xsl:text>
      <xsl:text>&amp;yearselect=yearrange&amp;SYSTEM_PT=t</xsl:text>
      <xsl:text>&amp;startYear=1969</xsl:text>
      <xsl:text>&amp;endYear=2007</xsl:text>
      <xsl:text>&amp;DOCINDEX=1</xsl:text>
      <xsl:text>&amp;format=quickSearchDetailedFormat</xsl:text>
      <xsl:text>&amp;database=</xsl:text>
      <xsl:value-of select="DOC/DB/DBMASK"/>
      <xsl:text>&amp;searchWord1=</xsl:text>
      <xsl:choose><xsl:when test="string(AN)"><xsl:value-of select="AN"/><xsl:text>+WN+AN</xsl:text></xsl:when>
      		  <xsl:otherwise>"<xsl:value-of select="DOC/DOC-ID"/>"</xsl:otherwise>
      </xsl:choose>
    <xsl:text disable-output-escaping="yes"><![CDATA[</link>]]></xsl:text>
  </item>
</xsl:template>
</xsl:stylesheet>













