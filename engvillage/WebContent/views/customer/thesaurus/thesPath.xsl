<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl">

<xsl:template match="TPATH">
	<td colspan="5" valign="top"> 
		<xsl:apply-templates/>
	</td>
</xsl:template>

<xsl:template match="STEP">
	
	<xsl:choose>
		<xsl:when test="position() = 1">
			<a CLASS="SmBlackText">
			<b>&#160;&#160;<xsl:value-of select="SCON"/>:&#160;&#160;</b></a> 
		</xsl:when>
	</xsl:choose>
	
	<xsl:variable name="LINK">
		<xsl:value-of select="SLINK"/>
	</xsl:variable>
	
	<xsl:variable name="SNUM">
			<xsl:value-of select="SNUM"/>
	</xsl:variable>

	
	
	<xsl:choose>
		<xsl:when test="position() = last()">
			<a CLASS="SmBlackText"><b><xsl:value-of select="STI" disable-output-escaping="yes"/></b></a>
		</xsl:when>
		<xsl:when test="position() != last()">
			<a href="/controller/servlet/Controller?snum={$SNUM}&amp;{$LINK}" CLASS="MedBlueLink">	
			<b><xsl:value-of select="STI"  disable-output-escaping="yes" /></b>
			</a> 		
		</xsl:when>	
	</xsl:choose>
	
	
	<xsl:choose>
		<xsl:when test="position() != last()">
			<a CLASS="SmBlackText">&#160;&#160;&gt;&gt;&#160;&#160;</a>
		</xsl:when>
	</xsl:choose>

</xsl:template>



</xsl:stylesheet>