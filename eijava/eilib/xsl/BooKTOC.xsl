<?xml version="1.0" ?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:bk="http://www.elsevier.com/xml/bk/dtd"
  xmlns:ce="http://www.elsevier.com/xml/common/dtd"
  xmlns:sb="http://www.elsevier.com/xml/common/struct-bib/dtd"
  exclude-result-prefixes="html xsl bk ce sb">
  <xsl:output method="html" indent="yes" encoding="ISO-8859-1"/>
  <xsl:strip-space elements="*" />

  <xsl:template match="bk:book">
    <ul>
      <xsl:apply-templates />
    </ul>
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
    <li>
      <b><xsl:apply-templates select="ce:label" mode="bktitle"/><xsl:text> </xsl:text><xsl:apply-templates select="ce:title" mode="bktitle"/></b>
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

    <xsl:variable name="folder">
      <xsl:value-of select="name((ancestor::*)[2])" />qqDELqq<xsl:value-of select="ce:pii" />
    </xsl:variable>

    <xsl:variable name="linktitle">
      <xsl:apply-templates select="document($folder)" mode="linktitle"/>
    </xsl:variable>

    <li>
      <xsl:variable name="isbn" select="//ce:isbn" />
      <a title="{$linktitle}" href="javascript:loadFromToc('{$isbn}',{$page});void('');">
        <xsl:value-of select="$linktitle" />
      </a>
    </li>
  </xsl:template>

  <xsl:template match="ce:label" mode="bktitle">
    <xsl:if test="string(normalize-space(text()))"><xsl:value-of select="text()"/><xsl:text>&#160;-&#160;</xsl:text></xsl:if>
  </xsl:template>
  <xsl:template match="ce:title|ce:section-title" mode="bktitle">
    <xsl:apply-templates />
  </xsl:template>
  <xsl:template match="ce:subtitle" mode="bktitle">
    <xsl:text>:&#160;</xsl:text><xsl:value-of select="normalize-space(text())"/>
  </xsl:template>

  <xsl:template match="ce:further-reading" mode="bktitle">
    <xsl:apply-templates select="ce:section-title" mode="bktitle"/>
  </xsl:template>

  <xsl:template match="ce:index" mode="bktitle">
    <xsl:apply-templates select="ce:section-title" mode="bktitle"/>
  </xsl:template>

  <xsl:template match="bk:index|bk:glossary|bk:bibliography" mode="linktitle">
    <xsl:apply-templates select="ce:label" mode="bktitle"/><xsl:apply-templates select="ce:further-reading" mode="bktitle"/><xsl:apply-templates select="ce:index" mode="bktitle"/>
  </xsl:template>

  <!-- template that generates linktitle from XML document() call -->
  <xsl:template match="bk:chapter|bk:fb-non-chapter|bk:simple-chapter" mode="linktitle">
    <xsl:apply-templates select="ce:label" mode="bktitle"/><xsl:apply-templates select="ce:title" mode="bktitle"/><xsl:apply-templates select="ce:subtitle" mode="bktitle"/>
  </xsl:template>

  <xsl:template match="bk:introduction" mode="linktitle">
    <xsl:text>Introduction</xsl:text>
  </xsl:template>

  <xsl:template match="ce:label"/>
  <xsl:template match="ce:title"/>
  <xsl:template match="ce:subtitle"/>
  <xsl:template match="ce:section-title" />
  <xsl:template match="ce:abstract" />
  <xsl:template match="ce:author-group" />
  <xsl:template match="ce:article-footnote" />
  <xsl:template match="ce:keywords" />
  <xsl:template match="ce:author-group" />
  <xsl:template match="ce:cross-ref" />


</xsl:stylesheet>