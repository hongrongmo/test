<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:template match="BP" mode="TC">
    <LI>
        <a class="SpLink"><xsl:value-of select="PTI"/></a>
    </LI>
    <xsl:apply-templates select="BPS" mode="TC"/>
  </xsl:template>

  <xsl:template match="BOOKS">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="BPS">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="BPS" mode="TC">
    <UL type="disc">
      <xsl:apply-templates mode="TC"/>
    </UL>
  </xsl:template>

  <xsl:template match="BPS" mode="BOOK">
    <UL type="disc">
      <xsl:apply-templates mode="TC"/>
    </UL>
  </xsl:template>

</xsl:stylesheet>
