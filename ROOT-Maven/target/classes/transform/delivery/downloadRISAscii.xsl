<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	exclude-result-prefixes="xsl"
>
<!--
	&#xD; is a CARRIAGE RETURN (ASCII Hex 13)
	&#xA; is a LINE FEED (ASCII Hex 10)
-->

<xsl:include href="common/RISResults.xsl" />

<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

	<xsl:preserve-space elements="text"/>

	<xsl:template match="HEADER">

			<!-- start of Header -->
			<xsl:text>Provider: Elsevier Engineering Information, Inc.</xsl:text>
			<xsl:text>&#xD;&#xA;</xsl:text>
			<xsl:text>Database: </xsl:text><xsl:value-of select="DB"/>
			<xsl:text>&#xD;&#xA;</xsl:text>
			<xsl:text>Content: application/x-research-info-systems</xsl:text>
			<xsl:text>&#xD;&#xA;</xsl:text>
			<xsl:text>&#xD;&#xA;</xsl:text>
			<xsl:text>&#xD;&#xA;</xsl:text>
			<!-- end of Header -->

	</xsl:template>

	<xsl:template match="text()">
	</xsl:template>

	<xsl:template match="PAGE">
		<xsl:apply-templates select="HEADER"/>

		<xsl:apply-templates />

		<!-- end of end reference -->
		<xsl:text>&#xD;&#xA;</xsl:text>

	</xsl:template>

	<xsl:template match="PAGE-ENTRY">

		<xsl:apply-templates />

		<xsl:text>ER  - </xsl:text>
		<xsl:text>&#xD;&#xA;</xsl:text>
		<xsl:text>&#xD;&#xA;</xsl:text>
		<xsl:text>&#xD;&#xA;</xsl:text>
	</xsl:template>

</xsl:stylesheet>