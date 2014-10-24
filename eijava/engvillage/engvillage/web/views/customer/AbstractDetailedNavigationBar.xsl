<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
    xmlns:java="java:java.net.URLDecoder"
    exclude-result-prefixes="html xsl xslcid"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

    <xsl:template match="PAGE-NAV">
    <!-- this file renders abstract detailed results navigation bar in combination with abstractDetailedRecord.jsp -->

    <xsl:variable name="PREV-URL"><xsl:value-of select="PREV"/></xsl:variable>
    <xsl:variable name="NEXT-URL"><xsl:value-of select="NEXT"/></xsl:variable>
    <xsl:variable name="RESULTS-NAV-URL"><xsl:value-of disable-output-escaping="yes" select="java:decode(RESULTS-NAV)"/></xsl:variable>
    <xsl:variable name="NEWSEARCH-URL"><xsl:value-of select="java:decode(NEWSEARCH-NAV)"/></xsl:variable>
    <xsl:variable name="DEDUPRESULTS-NAV-URL"><xsl:value-of select="DEDUP-RESULTS-NAV"/></xsl:variable>


    <!--
    <xsl:variable name="NEWSEARCHCID">
      <xsl:value-of select="xslcid:newSearchCid($SEARCH-TYPE)"/>
    </xsl:variable>
    <xsl:variable name="SEARCHRESULTSCID">
      <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
    </xsl:variable>
    -->
      <center>
        <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1" >
          <tr>
            <td align="left" valign="middle" height="24">
              <xsl:if test="(string($RESULTS-NAV-URL))">
              	&spcr8;
                <a><xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="$RESULTS-NAV-URL"/></xsl:attribute>
                <img height="16" src="/engresources/images/sr.gif" border="0" /></a>
              </xsl:if>
              <xsl:if test="(string($DEDUPRESULTS-NAV-URL))">
                &spcr8;
                <a><xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="$DEDUPRESULTS-NAV-URL"/></xsl:attribute>
                <img height="16" src="/engresources/images/dr.gif" border="0" /></a>
              </xsl:if>
              &spcr8;
              <xsl:if test="(string($NEWSEARCH-URL))">
                <a><xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="$NEWSEARCH-URL"/></xsl:attribute>
                <img height="16" src="/engresources/images/ns.gif" border="0" /></a>
              </xsl:if>
            </td>
      	    <td align="right" valign="middle" height="24">
        	    <xsl:if test="string($PREV-URL)">
        	      <a>
        	      <xsl:attribute name="HREF">/controller/servlet/Controller?<xsl:value-of select="$PREV-URL"/></xsl:attribute>
        	      <img src="/engresources/images/pp.gif" border="0" /></a>
        	      &spcr8;
        	    </xsl:if>
        	    <xsl:if test="string($NEXT-URL)">
        	      <a>
        	      <xsl:attribute name="HREF">/controller/servlet/Controller?<xsl:value-of select="$NEXT-URL"/></xsl:attribute>
        	      <img src="/engresources/images/np.gif" border="0" /></a>
        	    </xsl:if>
        	    &spcr8;
      	    </td>
          </tr>
        </table>
      </center>

    </xsl:template>

</xsl:stylesheet>
