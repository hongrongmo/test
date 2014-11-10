<?xml version="1.0" ?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="html xsl"
>
  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Frameset//EN"/>
  <xsl:strip-space elements="html:* xsl:*" />

  <xsl:template match="ROOT">

    <HTML>
    <HEAD><TITLE>Engineering Village</TITLE></HEAD>

    <FRAMESET rows="100,*">
    <FRAME name="bookNav" scrolling="no">
      <xsl:attribute name="SRC">/controller/servlet/Controller?<xsl:value-of select="NAVURL"/></xsl:attribute>
    </FRAME>
    <FRAME name="bookPage">
      <xsl:attribute name="SRC"><xsl:apply-templates select="PAGEURL"/></xsl:attribute>
    </FRAME>
    </FRAMESET>
    </HTML>
  </xsl:template>

  <xsl:template match="PAGEURL">
    <xsl:value-of disable-output-escaping="yes" select="text()"/>
  </xsl:template>

</xsl:stylesheet>

