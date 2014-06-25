<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="http://www.jclark.com/xt/java/java.net.URLEncoder"
	exclude-result-prefixes="java html xsl">



<xsl:template match="CU">
	<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="LE">
	<i><xsl:value-of select="."/></i>
</xsl:template>

<xsl:template match="PR">
	<i><xsl:value-of select="."/>*</i>
</xsl:template>

</xsl:stylesheet>
