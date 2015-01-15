<?xml version="1.0" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:si="http://www.elsevier.com/xml/si/dtd"
	xmlns:ce="http://www.elsevier.com/xml/common/dtd"
	xmlns:sb="http://www.elsevier.com/xml/common/struct-bib/dtd"
	xmlns:ja="http://www.elsevier.com/xml/ja/dtd"
	exclude-result-prefixes="html xsl si ce sb ja">
	<xsl:output method="html" indent="yes" />
	<xsl:strip-space elements="*" />

	<xsl:template match="si:serial-issue">
		<ul>
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="si:issue-info">
		<!--  <xsl:value-of select="ce:isbn"/> -->
	</xsl:template>

	<xsl:template match="si:issue-data" />

	<xsl:template match="si:issue-body">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="si:issue-sec">
		<li>
			<xsl:value-of select="ce:section-title" />
			<ul>
				<xsl:apply-templates />
			</ul>
		</li>
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

		<li>
			<xsl:variable name="isbn" select="//ce:isbn" />
			<a href="javascript:loadFromToc('{$isbn}',{$page});void('');">
				<xsl:apply-templates
					select="document(ce:pii)//ce:title" />
			</a>
		</li>
	</xsl:template>

	<xsl:template match="ce:section-title" />
	<xsl:template match="ce:abstract" />
	<xsl:template match="ce:author-group" />
	<xsl:template match="ce:article-footnote" />
	<xsl:template match="ce:keywords" />

	<xsl:template match="ce:pages">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="ce:first-page">
		<xsl:value-of select="." />
	</xsl:template>
	<xsl:template match="ce:last-page">
		to
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="ce:title">
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="ce:author-group" />
<!-- None of this is needed since we are just extracting he //ce:title from the imported article
	file (main.xml)
	<xsl:template match="ja:simple-article">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ja:simple-head">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ja:article">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ja:head">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ja:item-info" />
	<xsl:template match="ja:tail" />
	<xsl:template match="ja:simple-tail" />
 -->
</xsl:stylesheet>