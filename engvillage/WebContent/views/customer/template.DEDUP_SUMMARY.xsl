<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  exclude-result-prefixes="html xsl xslcid"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template name="DEDUP-SUMMARY">

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

  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="CURR-PAGE-ID"/>
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

  <xsl:variable name="ORIGINAL-SEARCH-LABEL">
    <xsl:value-of select="$RESULTS-COUNT"/> Total records for <xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/><xsl:if test="(SEARCH-TYPE!='Combined')">, <xsl:value-of select="$START-YEAR"/>-<xsl:value-of select="$END-YEAR"/></xsl:if>
  </xsl:variable>

  <xsl:variable name="CID">
    <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
  </xsl:variable>


  <xsl:variable name="REMOVED-DUPS">
    <xsl:value-of select="sum(DEDUPSET-REMOVED-DUPS/REMOVED-DUPS/attribute::COUNT)"/>
  </xsl:variable>

  <table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td colspan="3" height="15"><img src="/static/images/s.gif" height="15"/></td>
      </tr>
      <tr>
        <td colspan="3"><a CLASS="MedBlackText"><b>Deduplication Summary</b></a>&#160;<a CLASS="SmBlackText">(First 1000 search results)</a></td>
      </tr>
      <tr>
        <td><a class="SrtLink" href="/controller/servlet/Controller?CID=dedupForm&amp;database={$SELECTED-DB}&amp;SEARCHID={$SEARCH-ID}&amp;COUNT=1&amp;SEARCHTYPE={$SEARCH-TYPE}&amp;RESULTSCOUNT={$RESULTS-COUNT}&amp;dbpref={$DB-PREF}&amp;fieldpref={$FIELD-PREF}">Deduplication criteria</a></td>
        <td width="20"><img src="/static/images/s.gif" width="20" height="1"/></td>
        <td><a CLASS="SmBlackText"><xsl:value-of select="$PREF-FIELD"/>&#160;Preferred;&#160;&#160;<xsl:value-of select="$PREF-DB"/>&#160;Preferred</a>&#160; </td>
      </tr>
      <tr>
        <td><a class="SmBlackText">Original search</a></td>
        <td width="20"><img src="/static/images/s.gif" width="20" height="1"/></td>
        <td><a class="SrtLink" href="/controller/servlet/Controller?CID={$CID}&amp;SEARCHID={$SEARCH-ID}&amp;navigator=PREV&amp;COUNT=1&amp;database={$SELECTED-DB}"><xsl:value-of select="$ORIGINAL-SEARCH-LABEL"/></a>&#160;</td>
      </tr>
      <tr>
        <td><a class="SmBlackText">Duplicates removed</a></td>
        <td width="20"><img src="/static/images/s.gif" width="20" height="1"/></td>
        <td valign="top">
            <xsl:choose>
            <xsl:when test="not($REMOVED-DUPS='0')">
                <a class="SmBlackText">
                <xsl:if test="(($LINKSET='removed') and string($DBLINK)) or not($LINKSET='removed')">
                  <xsl:attribute name="class">SrtLink</xsl:attribute>
                  <xsl:attribute name="href">/search/results/dedup.url?CID=dedup&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;dbpref=<xsl:value-of select="$DB-PREF"/>&amp;fieldpref=<xsl:value-of select="$FIELD-PREF"/>&amp;origin=summary&amp;linkSet=removed</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="$REMOVED-DUPS"/>&#160;Total
                </a>&#160;
                <a class="SmBlackText">(</a>
            </xsl:when>
            <xsl:otherwise>
                <a class="SmBlackText"><xsl:value-of select="$REMOVED-DUPS"/>&#160;Total</a>
            </xsl:otherwise>
            </xsl:choose>
            <xsl:for-each select="DEDUPSET-REMOVED-DUPS/REMOVED-DUPS">
              <a class="SmBlackText">
                <xsl:if test="(($LINKSET='removed') and (not($DBLINK=@DB) and not(@COUNT=0))) or (not($LINKSET='removed') and not(@COUNT=0))">
                  <xsl:attribute name="class">SrtLink</xsl:attribute>
                  <xsl:attribute name="href">/search/results/dedup.url?CID=dedup&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;dbpref=<xsl:value-of select="$DB-PREF"/>&amp;fieldpref=<xsl:value-of select="$FIELD-PREF"/>&amp;dbLink=<xsl:value-of select="@DB"/>&amp;origin=summary&amp;linkSet=removed</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="@COUNT"/>&#160;<xsl:value-of select="@DBNAME"/>
              </a>
              <xsl:if test="not(position()=last())">,&#160;</xsl:if>
            </xsl:for-each>
            <xsl:if test="not($REMOVED-DUPS='0')">
            <a class="SmBlackText">)</a>
            </xsl:if>
        </td>
      </tr>
      <tr>
        <td><a class="SmBlackText">Deduplicated set</a></td>
        <td width="20"><img src="/static/images/s.gif" width="20" height="1"/></td>
        <td valign="top">

            <a class="SmBlackText">
                <xsl:if test="(string($LINKSET) and ($LINKSET='deduped') and string($DBLINK)) or (string($LINKSET) and not($LINKSET='deduped'))">
                  <xsl:attribute name="class">SrtLink</xsl:attribute>
                  <xsl:attribute name="href">/search/results/dedup.url?CID=dedup&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;dbpref=<xsl:value-of select="$DB-PREF"/>&amp;fieldpref=<xsl:value-of select="$FIELD-PREF"/>&amp;origin=summary&amp;linkSet=deduped</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="sum(DEDUPSET-COUNTER/DB-COUNT/attribute::COUNT)"/>&#160;Total
            </a>&#160;

            <a class="SmBlackText">(</a>
            <xsl:for-each select="DEDUPSET-COUNTER/DB-COUNT">
              <a class="SmBlackText">
                <xsl:if test="(($LINKSET='deduped') and (not($DBLINK=@DB)) and not(@COUNT=0)) or (not($LINKSET='deduped') and not(@COUNT=0))">
                  <xsl:attribute name="class">SrtLink</xsl:attribute>
                  <xsl:attribute name="href">/search/results/dedup.url?CID=dedup&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;dbpref=<xsl:value-of select="$DB-PREF"/>&amp;fieldpref=<xsl:value-of select="$FIELD-PREF"/>&amp;dbLink=<xsl:value-of select="@DB"/>&amp;origin=summary&amp;linkSet=deduped</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="@COUNT"/>&#160;<xsl:value-of select="@DBNAME"/>
              </a>
              <xsl:if test="not(position()=last())">,&#160;</xsl:if>
            </xsl:for-each>
            <a class="SmBlackText">)</a>

        </td>
      </tr>
  </table>

</xsl:template>

</xsl:stylesheet>