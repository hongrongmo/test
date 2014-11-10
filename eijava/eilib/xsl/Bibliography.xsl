<?xml version="1.0" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:bk="http://www.elsevier.com/xml/bk/dtd"
	xmlns:ce="http://www.elsevier.com/xml/common/dtd"
	xmlns:sb="http://www.elsevier.com/xml/common/struct-bib/dtd"
	exclude-result-prefixes="html xsl bk ce sb">
	<xsl:output method="html" indent="yes" encoding="utf-8"/>
	<xsl:strip-space elements="*" />

	<xsl:template match="bk:book">
		<xsl:apply-templates select="bk:rear"/>
	</xsl:template>
	<xsl:template match="bk:rearpart|bk:rear">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="bk:index|bk:glossary|bk:fb-non-chapter"/>
	<xsl:template match="info|bk:info"/>

	<xsl:template match="bk:bibliography">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ce:include-item">
		<xsl:variable name="folder">
			<xsl:value-of select="name((ancestor::*)[2])" />qqDELqq<xsl:value-of select="ce:pii" />
		</xsl:variable>
		<xsl:apply-templates select="document($folder)"/>
	</xsl:template>

	<xsl:template match="ce:further-reading">
		<h3><xsl:value-of select="ce:section-title"/></h3>
		<xsl:apply-templates select="ce:further-reading-sec"/>
	</xsl:template>

	<xsl:template match="ce:further-reading-sec">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="ce:bib-reference">
		<p><xsl:apply-templates /></p>
	</xsl:template>

	<xsl:template match="ce:label">
		<xsl:value-of select="text()"/><xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="sb:reference">
		<xsl:apply-templates/><xsl:text>. </xsl:text>
	</xsl:template>

	<xsl:template match="sb:comment">
		<xsl:value-of select="text()"/><xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="sb:host">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="sb:contribution">
		<xsl:apply-templates select="sb:editors"/>
		<xsl:apply-templates select="sb:authors"/>
		<xsl:apply-templates select="sb:title" mode="contribution"/>
	</xsl:template>

	<xsl:template match="sb:edited-book">
		<xsl:text>In: </xsl:text><xsl:apply-templates select="sb:editors"/>
		<xsl:apply-templates select="sb:title"/>
		<xsl:apply-templates select="sb:publisher"/>
		<xsl:apply-templates select="sb:date"/>
	</xsl:template>
	
	<xsl:template match="sb:book">
		<xsl:apply-templates select="sb:editors"/>
		<xsl:apply-templates select="sb:title"/>
		<xsl:apply-templates select="sb:publisher"/>
		<xsl:apply-templates select="sb:date"/>
	</xsl:template>
	
	<xsl:template match="sb:issue">
		<xsl:apply-templates select="sb:series"/>
		<xsl:apply-templates select="sb:date"/><xsl:text>, </xsl:text>
	</xsl:template>

	<xsl:template match="sb:series">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="sb:volume-nr">
		<b><xsl:value-of select="text()"/></b>
	</xsl:template>

	<xsl:template match="sb:title" mode="contribution">
		<xsl:value-of select="sb:maintitle"/>,
	</xsl:template>

	<xsl:template match="sb:title">
		<i><xsl:value-of select="sb:maintitle"/></i><xsl:apply-templates select="sb:subtitle"/>,
	</xsl:template>

	<xsl:template match="sb:subtitle">
		<xsl:text>:</xsl:text> <xsl:value-of select="text()"/>
	</xsl:template>

	<xsl:template match="sb:date">
		<xsl:text> (</xsl:text><xsl:value-of select="text()"/><xsl:text>)</xsl:text>
	</xsl:template>

	<xsl:template match="sb:publisher">
		<xsl:apply-templates select="sb:name"/><xsl:apply-templates select="sb:location"/>
	</xsl:template>
	<xsl:template match="sb:name">
		<xsl:text> </xsl:text><xsl:value-of select="text()"/>
	</xsl:template>
	<xsl:template match="sb:location">
		<xsl:text>, </xsl:text><xsl:value-of select="text()"/>
	</xsl:template>

	<xsl:template match="sb:editors">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="sb:authors">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="sb:author">
		<xsl:value-of select="ce:given-name"/><xsl:text> </xsl:text><xsl:value-of select="ce:surname"/><xsl:text> and </xsl:text>
	</xsl:template>

	<xsl:template match="sb:author[position()=last()]">
		<xsl:value-of select="ce:given-name"/><xsl:text> </xsl:text><xsl:value-of select="ce:surname"/><xsl:text>; </xsl:text>
	</xsl:template>
	
	<xsl:template match="sb:editor">
		<xsl:value-of select="ce:given-name"/><xsl:text> </xsl:text><xsl:value-of select="ce:surname"/><xsl:text> and </xsl:text>
	</xsl:template>

	<xsl:template match="sb:editor[position()=last()]">
		<xsl:value-of select="ce:given-name"/><xsl:text> </xsl:text><xsl:value-of select="ce:surname"/><xsl:text>; </xsl:text>
	</xsl:template>

	<xsl:template match="sb:pages">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="sb:first-page">
		<xsl:text>pp </xsl:text><xsl:value-of select="text()"/>
	</xsl:template>

	<xsl:template match="sb:last-page">
		<xsl:text>&#150;</xsl:text><xsl:value-of select="text()"/>
	</xsl:template>

</xsl:stylesheet>