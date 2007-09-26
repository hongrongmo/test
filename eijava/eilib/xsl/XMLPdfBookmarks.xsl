<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:bk="http://www.elsevier.com/xml/bk/dtd"
	xmlns:ce="http://www.elsevier.com/xml/common/dtd"
	xmlns:sb="http://www.elsevier.com/xml/common/struct-bib/dtd"
	exclude-result-prefixes="xsl bk ce sb">
	<xsl:output method="xml" indent="yes" encoding="utf-8"/>
	<xsl:strip-space elements="*" />

	<xsl:template match="bk:book">
		<Bookmark>
			<xsl:apply-templates />
		</Bookmark>
	</xsl:template>

	<xsl:template match="bk:info|bk:top"/>
	<xsl:template match="bk:front">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="bk:body">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="bk:rear">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="bk:rearpart">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="bk:volume|bk:section|bk:part">
		<Title>
			<xsl:attribute name="Style">bold</xsl:attribute>
			<xsl:apply-templates select="ce:label" mode="bktitle"/> <xsl:apply-templates select="ce:title" mode="bktitle"/>
			<xsl:apply-templates />
		</Title>
	</xsl:template>

	<xsl:key name="pii-lookup" match="pii" use="id" />

	<xsl:variable name="pii-table" select="document('lookup.xml')/piis" />

	<xsl:template match="piis">
		<xsl:param name="curr-pii" />
		<xsl:value-of select="key('pii-lookup', $curr-pii)/page" />
	</xsl:template>
		
	<xsl:template match="ce:include-item">
		<xsl:variable name="page">
			<xsl:apply-templates select="$pii-table">
				<xsl:with-param name="curr-pii" select="ce:pii" />
			</xsl:apply-templates>
		</xsl:variable>
		
		<xsl:variable name="folder">
			<xsl:value-of select="name((ancestor::*)[2])" />qqDELqq<xsl:value-of select="ce:pii" />
		</xsl:variable>

		<xsl:variable name="linktitle">
			<xsl:apply-templates select="document($folder)" mode="linktitle"/>
		</xsl:variable>

		<Title>
			<xsl:attribute name="Action">GoTo</xsl:attribute>
			<xsl:attribute name="Page"><xsl:value-of select="$page"/> Fit</xsl:attribute>
			<xsl:value-of select="$linktitle" />
		</Title>

	</xsl:template>

	<xsl:template match="ce:label" mode="bktitle">
		<xsl:if test="string(normalize-space(text()))"><xsl:value-of select="text()"/><xsl:text> - </xsl:text></xsl:if>
	</xsl:template>
	<xsl:template match="ce:title|ce:section-title" mode="bktitle">
		<xsl:value-of select="text()"/>
	</xsl:template>
	<xsl:template match="ce:subtitle" mode="bktitle">
		<xsl:text>: </xsl:text><xsl:value-of select="text()"/>
	</xsl:template>

	<xsl:template match="ce:further-reading" mode="bktitle">
		<xsl:apply-templates select="ce:section-title" mode="bktitle"/>
	</xsl:template>
	
	<xsl:template match="bk:index|bk:glossary|bk:bibliography" mode="linktitle">
		<xsl:apply-templates select="ce:label" mode="bktitle"/><xsl:apply-templates select="ce:further-reading" mode="bktitle"/>
	</xsl:template>

	<xsl:template match="bk:introduction|bk:chapter|bk:fb-non-chapter|bk:simple-chapter" mode="linktitle">
		<xsl:apply-templates select="ce:label" mode="bktitle"/><xsl:apply-templates select="ce:title" mode="bktitle"/><xsl:apply-templates select="ce:subtitle" mode="bktitle"/>
	</xsl:template>
	
	<xsl:template match="ce:label"/>
	<xsl:template match="ce:title"/>
	<xsl:template match="ce:section-title" />
	<xsl:template match="ce:abstract" />
	<xsl:template match="ce:author-group" />
	<xsl:template match="ce:article-footnote" />
	<xsl:template match="ce:keywords" />
	<xsl:template match="ce:author-group" />

</xsl:stylesheet>