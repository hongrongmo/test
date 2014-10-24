<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:gui="java:org.ei.gui.PagingComponents"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  exclude-result-prefixes="gui java html xsl xslcid"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="xsl:*" />

<xsl:template match="DEDUPFORM-NAVIGATION-BAR">
  <xsl:param name="HEAD"/>
  <xsl:param name="LOCATION"/>

  <xsl:variable name="RESULTS-COUNT">
    <xsl:value-of select="//RESULTS-COUNT"/>
  </xsl:variable>

  <xsl:variable name="DATABASE">
    <xsl:value-of select="//DBMASK"/>
  </xsl:variable>

  <xsl:variable name="ENCODED-DATABASE">
    <xsl:value-of select="java:encode($DATABASE)"/>
  </xsl:variable>

  <xsl:variable name="PAGE-INDEX">
    <xsl:value-of select="//COUNT"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="//SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-TYPE">
    <xsl:value-of select="//SEARCH-TYPE"/>
  </xsl:variable>

<xsl:variable name="SEARCHRESULTSCID">
  <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
</xsl:variable>
<xsl:variable name="NEWSEARCHCID">
  <xsl:value-of select="xslcid:newSearchCid($SEARCH-TYPE)"/>
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

    <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
      <tr>
        <td align="left" valign="middle" height="24">
          <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
            <xsl:if test="($RESULTS-COUNT &gt; 0) ">
                <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$SEARCHRESULTSCID"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$PAGE-INDEX"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;DupFlag=false</xsl:attribute>
                    <img height="16" src="/engresources/images/sr.gif" border="0" />
                </a>
            </xsl:if>
          <img width="8" height="1" src="/engresources/images/s.gif" border="0" />

          <a>
            <xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$NEWSEARCHCID"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/></xsl:attribute>
            <img src="/engresources/images/ns.gif" border="0" />
          </a>
        </td>
      </tr>
    </table>

  </xsl:template>

</xsl:stylesheet>
