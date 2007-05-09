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

  <xsl:template name="TAGSEARCH-AB-DETAILED-NAVBAR">
  <!-- this file renders abstract detailed results navigation bar in combination with abstractDetailedRecord.jsp -->

    <xsl:variable name="TAG-SEARCH-FLAG"><xsl:value-of select="TAG-SEARCH-FLAG"/></xsl:variable>
    <xsl:variable name="SEARCH-ID"><xsl:value-of select="/PAGE/SEARCH-ID"/></xsl:variable>
    <xsl:variable name="PREV-PAGE"><xsl:value-of select="/PAGE/PREV-PAGE-ID"/></xsl:variable>
    <xsl:variable name="NEXT-PAGE"><xsl:value-of select="/PAGE/NEXT-PAGE-ID"/></xsl:variable>
    <xsl:variable name="RESULTS-COUNT"><xsl:value-of select="/PAGE/RESULTS-COUNT"/></xsl:variable>
    <xsl:variable name="PAGE-INDEX"><xsl:value-of select="/PAGE/PAGE-INDEX"/></xsl:variable>
    <xsl:variable name="INDEX"><xsl:value-of select="/PAGE/CURR-PAGE-ID"/></xsl:variable>
    <xsl:variable name="CID"><xsl:value-of select="/PAGE/CID"/></xsl:variable>
   <!-- <xsl:variable name="DATABASE"><xsl:value-of select="/PAGE/SESSION-DATA/DATABASE-MASK"/></xsl:variable>
    <xsl:variable name="DATABASE"><xsl:value-of select="/PAGE/DBMASK"/></xsl:variable> -->
    <xsl:variable name="DATABASE"><xsl:value-of select="EI-DOCUMENT/DOC/DB/DBMASK"/></xsl:variable>
    <xsl:variable name="DEFAULT-DB-MASK"><xsl:value-of select="DEFAULT-DB-MASK"/></xsl:variable>
    <xsl:variable name="SCOPE">
	<xsl:value-of select="//SCOPE-REC"/>
    </xsl:variable>

   <xsl:variable name="GROUP-ID">
	<xsl:value-of select="//GROUP-ID"/>
   </xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
      <xsl:value-of select="SEARCH-TYPE-TAG"/>
    </xsl:variable>

    <xsl:variable name="TAGSEARCHRESULTSCID">tagSearch</xsl:variable>
    <xsl:variable name="NEWSEARCHCID">
      <xsl:value-of select="xslcid:newSearchCid($SEARCH-TYPE)"/>
    </xsl:variable>
    <xsl:variable name="SEARCHRESULTSCID">
      <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
    </xsl:variable>

    <center>
      <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1" >
        <tr>
          <td align="left" valign="middle" height="24">
              <xsl:if test="($RESULTS-COUNT &gt; 0) ">
              &spcr8;
              <a><xsl:attribute name="href">/controller/servlet/Controller?CID=tagSearch&amp;searchtype=TagSearch&amp;searchtags=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$PAGE-INDEX"/>&amp;SCOPE=<xsl:value-of select="$SCOPE"/>&amp;groupID=<xsl:value-of select="$GROUP-ID"/>&amp;database=<xsl:value-of select="$DEFAULT-DB-MASK"/></xsl:attribute>
              <img height="16" src="/engresources/images/sr.gif" border="0" /></a>
              </xsl:if>
            &spcr8;
            <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$NEWSEARCHCID"/>&amp;database=<xsl:value-of select="$DEFAULT-DB-MASK"/></xsl:attribute>
              <img height="16" src="/engresources/images/ns.gif" border="0" />
            </a>
          </td>
          <xsl:choose>
            <xsl:when test="not(/PAGE/HIDE-NAV)">
              <td align="right" valign="middle" height="24">
                    <xsl:if test="(not($INDEX=1) and $RESULTS-COUNT &gt; 0)">
                  <a href="/controller/servlet/Controller?CID={$CID}&amp;SEARCHID={$SEARCH-ID}&amp;navigator=PREV&amp;tagSearchFlag={$TAG-SEARCH-FLAG}&amp;DOCINDEX={$PREV-PAGE}&amp;PAGEINDEX={$PAGE-INDEX}&amp;searchtype=tagSearch&amp;database={$DEFAULT-DB-MASK}&amp;SCOPE={$SCOPE}&amp;defaultdbmask={$DEFAULT-DB-MASK}&amp;format={$CID}">
                  <img src="/engresources/images/pp.gif" border="0" /></a>
                  &spcr8;
                </xsl:if>
                    <xsl:if test="(not($INDEX=$RESULTS-COUNT) and $RESULTS-COUNT &gt; 0) ">
                  <a href="/controller/servlet/Controller?CID={$CID}&amp;SEARCHID={$SEARCH-ID}&amp;navigator=NEXT&amp;tagSearchFlag={$TAG-SEARCH-FLAG}&amp;DOCINDEX={$NEXT-PAGE}&amp;PAGEINDEX={$PAGE-INDEX}&amp;searchtype=tagSearch&amp;SCOPE={$SCOPE}&amp;database={$DEFAULT-DB-MASK}&amp;defaultdbmask={$DEFAULT-DB-MASK}&amp;format={$CID}">
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