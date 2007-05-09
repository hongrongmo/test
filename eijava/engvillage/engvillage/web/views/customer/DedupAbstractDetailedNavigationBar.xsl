<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  exclude-result-prefixes="html xsl xslcid"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

  <xsl:template name="DEDUP-AB-DETAILED-NAVBAR">
  <!-- this file renders abstract detailed results navigation bar in combination with abstractDetailedRecord.jsp -->

    <xsl:variable name="DEDUP"><xsl:value-of select="/PAGE/DEDUP"/></xsl:variable>
    <xsl:variable name="FIELDPREF"><xsl:value-of select="/PAGE/FIELDPREF"/></xsl:variable>
    <xsl:variable name="DBPREF"><xsl:value-of select="/PAGE/DBPREF"/></xsl:variable>
    <xsl:variable name="DBLINK"><xsl:value-of select="/PAGE/DBLINK"/></xsl:variable>
    <xsl:variable name="LINKSET"><xsl:value-of select="/PAGE/LINKSET"/></xsl:variable>
    <xsl:variable name="ORIGIN"><xsl:value-of select="/PAGE/ORIGIN"/></xsl:variable>
    <xsl:variable name="DEDUPSET"><xsl:value-of select="/PAGE/DEDUPSET"/></xsl:variable>
    <xsl:variable name="SEARCH-ID"><xsl:value-of select="/PAGE/SEARCH-ID"/></xsl:variable>
    <xsl:variable name="PREV-PAGE"><xsl:value-of select="/PAGE/PREV-PAGE-ID"/></xsl:variable>
    <xsl:variable name="NEXT-PAGE"><xsl:value-of select="/PAGE/NEXT-PAGE-ID"/></xsl:variable>
    <xsl:variable name="RESULTS-COUNT"><xsl:value-of select="/PAGE/RESULTS-COUNT"/></xsl:variable>
    <xsl:variable name="PAGE-INDEX"><xsl:value-of select="/PAGE/PAGE-INDEX"/></xsl:variable>
    <xsl:variable name="INDEX"><xsl:value-of select="/PAGE/CURR-PAGE-ID"/></xsl:variable>
    <xsl:variable name="CID"><xsl:value-of select="/PAGE/CID"/></xsl:variable>
    <xsl:variable name="DATABASE"><xsl:value-of select="/PAGE/SESSION-DATA/DATABASE-MASK"/></xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
      <xsl:value-of select="/PAGE/SESSION-DATA/SEARCH-TYPE"/>
    </xsl:variable>

    <xsl:variable name="DEDUPRESULTSCID">dedup</xsl:variable>

    <xsl:variable name="SEARCHRESULTSCID">
      <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
    </xsl:variable>
<!--
    <xsl:variable name="SEARCHRESULTSCID">
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Book')">quickSearchCitationFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Easy')">expertSearchCitationFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchCitationFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchCitationFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Combined')">combineSearchHistory</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Thesaurus')">thesSearchCitationFormat</xsl:when>
        <xsl:otherwise>expertSearchCitationFormat</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
-->
    <xsl:variable name="NEWSEARCHCID">
      <xsl:value-of select="xslcid:newSearchCid($SEARCH-TYPE)"/>
    </xsl:variable>
<!--
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Book')">ebookSearch</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Easy')">easySearch</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Quick')">quickSearch</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Expert')">expertSearch</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Combined')">combineSearchHistory</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Thesaurus')">thesHome</xsl:when>
        <xsl:otherwise>expertSearch</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
-->
    <center>
      <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1" >
        <tr>
          <td align="left" valign="middle" height="24">
            <xsl:if test="($DEDUPSET &gt; 0) ">
              &spcr8;
              <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$SEARCHRESULTSCID"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$PAGE-INDEX"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:attribute>
              <img height="16" src="/engresources/images/sr.gif" border="0" /></a>
              &spcr8;
              <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$DEDUPRESULTSCID"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$PAGE-INDEX"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;DupFlag=<xsl:value-of select="$DEDUP"/>&amp;dbpref=<xsl:value-of select="$DBPREF"/>&amp;fieldpref=<xsl:value-of select="$FIELDPREF"/>&amp;origin=<xsl:value-of select="$ORIGIN"/>&amp;dbLink=<xsl:value-of select="$DBLINK"/>&amp;linkSet=<xsl:value-of select="$LINKSET"/>
              </xsl:attribute>
              <img height="16" src="/engresources/images/dr.gif" border="0" /></a>
            </xsl:if>
            &spcr8;
            <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$NEWSEARCHCID"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:attribute>
              <img height="16" src="/engresources/images/ns.gif" border="0" />
            </a>
          </td>
          <xsl:choose>
            <xsl:when test="not(/PAGE/HIDE-NAV)">
              <td align="right" valign="middle" height="24">
                <xsl:if test="(not($INDEX=1) and $DEDUPSET &gt; 0)">
                  <a href="/controller/servlet/Controller?CID={$CID}&amp;SEARCHID={$SEARCH-ID}&amp;navigator=PREV&amp;DupFlag={$DEDUP}&amp;dbpref={$DBPREF}&amp;fieldpref={$FIELDPREF}&amp;origin={$ORIGIN}&amp;dbLink={$DBLINK}&amp;linkSet={$LINKSET}&amp;dedupSet={$DEDUPSET}&amp;DOCINDEX={$PREV-PAGE}&amp;PAGEINDEX={$PAGE-INDEX}&amp;database={$DATABASE}&amp;format={$CID}">
                  <img src="/engresources/images/pp.gif" border="0" /></a>
                  &spcr8;
                </xsl:if>
                <xsl:if test="(not($INDEX=$DEDUPSET) and $DEDUPSET &gt; 0) ">
                  <a href="/controller/servlet/Controller?CID={$CID}&amp;SEARCHID={$SEARCH-ID}&amp;navigator=NEXT&amp;DupFlag={$DEDUP}&amp;dbpref={$DBPREF}&amp;fieldpref={$FIELDPREF}&amp;origin={$ORIGIN}&amp;dbLink={$DBLINK}&amp;linkSet={$LINKSET}&amp;dedupSet={$DEDUPSET}&amp;DOCINDEX={$NEXT-PAGE}&amp;PAGEINDEX={$PAGE-INDEX}&amp;database={$DATABASE}&amp;format={$CID}">
                  <img src="/engresources/images/np.gif" border="0" /></a>
                </xsl:if>
                &spcr8;
              </td>
            </xsl:when>
          </xsl:choose>
        </tr>
      </table>
    </center>

  </xsl:template>

</xsl:stylesheet>