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

	<xsl:template match="PATENT-REFERENCES-NAVIGATION-BAR">
	<!-- this file renders abstract detailed results navigation bar in combination with abstractDetailedRecord.jsp -->

		<xsl:variable name="SEARCH-ID"><xsl:value-of select="/PAGE/SEARCH-ID"/></xsl:variable>
		<xsl:variable name="PREV-PAGE"><xsl:value-of select="/PAGE/PREV-PAGE-ID"/></xsl:variable>
		<xsl:variable name="NEXT-PAGE"><xsl:value-of select="/PAGE/NEXT-PAGE-ID"/></xsl:variable>
		<xsl:variable name="RESULTS-COUNT"><xsl:value-of select="/PAGE/RESULTS-COUNT"/></xsl:variable>
		<xsl:variable name="PAGE-INDEX"><xsl:value-of select="/PAGE/PAGE-INDEX"/></xsl:variable>
		<xsl:variable name="INDEX"><xsl:value-of select="/PAGE/PAGE-RESULTS/UPT-DOC/EI-DOCUMENT/IDX"/></xsl:variable>
		<xsl:variable name="CID"><xsl:value-of select="/PAGE/CID"/></xsl:variable>
		<xsl:variable name="DATABASE"><xsl:value-of select="/PAGE/SESSION-DATA/DATABASE-MASK"/></xsl:variable>

		<xsl:variable name="SEARCH-TYPE">
			<xsl:value-of select="/PAGE/SESSION-DATA/SEARCH-TYPE"/>
		</xsl:variable>



    <xsl:variable name="SEARCHRESULTSCID">
      <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
    </xsl:variable>

    <xsl:variable name="NEWSEARCHCID">
      <xsl:value-of select="xslcid:newSearchCid($SEARCH-TYPE)"/>
    </xsl:variable>


		<center>
			<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1" >
  	    <tr>
					<td align="left" valign="middle" height="24">
						<xsl:if test="($RESULTS-COUNT &gt; 0) ">
  						&spcr8;
							<a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$SEARCHRESULTSCID"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$PAGE-INDEX"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:attribute>
							<img height="16" src="/engresources/images/sr.gif" border="0" /></a>
						</xsl:if>
            &spcr8;
            <a><xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$NEWSEARCHCID"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:attribute>
						<img height="16" src="/engresources/images/ns.gif" border="0" />
						</a>
					</td>

				</tr>
			</table>
		</center>

	</xsl:template>

</xsl:stylesheet>
