<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template name="OHUB-ONLOAD">if(document.images['fullDocImageTop']){imageFlip()}</xsl:template>

<xsl:template name="OHUB-ONLOAD-CIT">imageFlip()</xsl:template>

<xsl:template name="OHUB-IMAGE">

<xsl:param name="FULL-IMAGE" />

<!-- for abstract/detailed page -->
<xsl:variable name="ISSN">
	<xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/IVIP/@ISSN"/>
</xsl:variable>

<xsl:variable name="FIRST-PAGE">
	<xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/IVIP/@firstPage"/>
</xsl:variable>

<xsl:variable name="FIRST-VOLUME">
	<xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/IVIP/@firstVolume"/>
</xsl:variable>

<xsl:variable name="FIRST-ISSUE">
	<xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/IVIP/@firstIssue"/>
</xsl:variable>

<xsl:variable name="DOC-ID">
	<xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC-ID"/>
</xsl:variable>


<!-- for citation page -->
<xsl:variable name="cISSN">
	<xsl:value-of select="EI-DOCUMENT/IVIP/@ISSN"/>
</xsl:variable>

<xsl:variable name="cFIRST-PAGE">
	<xsl:value-of select="EI-DOCUMENT/IVIP/@firstPage"/>
</xsl:variable>

<xsl:variable name="cFIRST-VOLUME">
	<xsl:value-of select="EI-DOCUMENT/IVIP/@firstVolume"/>
</xsl:variable>

<xsl:variable name="cFIRST-ISSUE">
	<xsl:value-of select="EI-DOCUMENT/IVIP/@firstIssue"/>
</xsl:variable>

<xsl:variable name="cDOC-ID">
	<xsl:value-of select="EI-DOCUMENT/DOC-ID" disable-output-escaping="yes"/>
</xsl:variable>

<xsl:variable name="IMAGE-INDEX">
	<xsl:value-of select="position() - 1" />
</xsl:variable>

<xsl:choose>
	<xsl:when test= "($FULL-IMAGE = 'bottom')">
		<a href="/ohubservice/servlet/OHUBService?ohubIDType=ip&amp;reason=get&amp;ISSN={$ISSN}&amp;firstPage={$FIRST-PAGE}&amp;firstVolume={$FIRST-VOLUME}&amp;firstIssue={$FIRST-ISSUE}&amp;DOCID={$DOC-ID}" onclick="window.open('/ohubservice/servlet/OHUBService?EISESSION=$SESSIONID&amp;ohubIDType=ip&amp;reason=get&amp;ISSN={$ISSN}&amp;firstPage={$FIRST-PAGE}&amp;firstVolume={$FIRST-VOLUME}&amp;firstIssue={$FIRST-ISSUE}&amp;DOCID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false"><img name='fullDocImageB'  src="/ohubservice/images/checking.gif" border="0"/></a>
	</xsl:when>

	<xsl:when test= "($FULL-IMAGE = 'top')">
		<a href="/ohubservice/servlet/OHUBService?ohubIDType=ip&amp;reason=get&amp;ISSN={$ISSN}&amp;firstPage={$FIRST-PAGE}&amp;firstVolume={$FIRST-VOLUME}&amp;firstIssue={$FIRST-ISSUE}&amp;DOCID={$DOC-ID}" onclick="window.open('/ohubservice/servlet/OHUBService?EISESSION=$SESSIONID&amp;ohubIDType=ip&amp;reason=get&amp;ISSN={$ISSN}&amp;firstPage={$FIRST-PAGE}&amp;firstVolume={$FIRST-VOLUME}&amp;firstIssue={$FIRST-ISSUE}&amp;DOCID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false"><img name='fullDocImageTop'  src="/ohubservice/images/checking.gif" border="0" height="15" width="70" /></a>		
	</xsl:when>

	<xsl:when test= "($FULL-IMAGE = 'citation')">
		<a href="" onclick="window.open('/ohubservice/servlet/OHUBService?ohubIDType=ip&amp;reason=get&amp;ISSN={$cISSN}&amp;firstPage={$cFIRST-PAGE}&amp;firstVolume={$cFIRST-VOLUME}&amp;firstIssue={$cFIRST-ISSUE}&amp;DOCID={$cDOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false"><img name='fullDocImageCit{$IMAGE-INDEX}'  src="/ohubservice/images/checking.gif" border="0" height="18" width="70" /></a>		
	</xsl:when>
</xsl:choose>

</xsl:template>

</xsl:stylesheet>