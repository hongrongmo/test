<xsl:stylesheet
    version="1.0"
    xmlns:ibfab="java:org.ei.data.insback.runtime.InspecArchiveAbstract"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:strutil="java:org.ei.util.StringUtil"
    exclude-result-prefixes="xsl strutil"
>

<!-- BIBTex  format -->
<!--
    every line must end with CR/LF
    &#xD; is a CARRIAGE RETURN (ASCII Hex 13)
    &#xA; is a LINE FEED (ASCII Hex 10)
-->

    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

    <xsl:preserve-space elements="text"/>

    <xsl:template match="EI-DOCUMENT">

        <!-- This 'mode' enables us to start the
            record with the TY and then
            not repeat the same info again later -->
        <xsl:apply-templates select="TY" mode="start-record"/>
        <xsl:apply-templates />

    </xsl:template>
    <xsl:template match="TY" mode="start-record">

    <xsl:variable name="DOC-TYPE">
    <xsl:choose>

        <xsl:when test="(.)='CONF'">inproceedings</xsl:when>
        <xsl:when test="(.)='CHAP'">inbook</xsl:when>
        <xsl:when test="(.)='BOOK'">book</xsl:when>
        <xsl:when test="(.)='UNPB'">unpublished</xsl:when>
        <xsl:otherwise>article</xsl:otherwise>

    </xsl:choose>
    </xsl:variable>

        <xsl:text>@</xsl:text><xsl:value-of select="$DOC-TYPE" disable-output-escaping="yes"/><xsl:text>{</xsl:text>
        <xsl:variable name="AN"><xsl:value-of select="../AN|../U1" /></xsl:variable>
        <xsl:value-of select="$AN" disable-output-escaping="yes"/><xsl:text> ,&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="CY">
        <xsl:text>address = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="AUS">
        <xsl:text>author = {</xsl:text>
        <xsl:apply-templates />
        <xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="SP">
        <xsl:variable name="SP"><xsl:value-of select="../SP" /></xsl:variable>
        <xsl:variable name="EP"><xsl:text>- </xsl:text><xsl:value-of select="../EP" /></xsl:variable>
        <xsl:text>pages = {</xsl:text><xsl:value-of select="$SP" disable-output-escaping="yes"/><xsl:text> </xsl:text><xsl:value-of select="$EP" disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="CVS">
        <xsl:text>keywords = {</xsl:text>
        <xsl:apply-templates />
        <xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="FLS">
        <xsl:text>note = {</xsl:text>
        <xsl:apply-templates />
        <xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
        <xsl:template match ="CV">
        <xsl:value-of select="normalize-space(text())"/>
        <xsl:text>;</xsl:text>
    </xsl:template>

    <xsl:template match ="FL">
    <xsl:value-of select="normalize-space(text())"/>
        <xsl:text>;</xsl:text>
    </xsl:template>

    <xsl:template match ="KW">
    <xsl:text>key = {</xsl:text>
    <xsl:value-of select="normalize-space(text())"/>
        <xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match ="AU">
        <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
        <xsl:if test="boolean(not(contains($NAME,'Anon')))">
            <xsl:value-of select="strutil:stripHtml($NAME)"/>
            <xsl:if test="position() != last()">
                <xsl:text> and </xsl:text>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    <xsl:template match="EDS">
        <xsl:text>editor = {</xsl:text>
        <xsl:apply-templates />
        <xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match ="ED">
        <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
        <xsl:if test="boolean(not(contains($NAME,'Anon')))">
          <xsl:value-of select="strutil:stripHtml($NAME)"/>
        <xsl:text>;</xsl:text>
        </xsl:if>
    </xsl:template>
    <!--
    <xsl:template match="DBNAME">
        <xsl:text>site key = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    -->
    <xsl:template match="LA">
        <xsl:text>language = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="IS">
        <xsl:text>number = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="N2">
        <xsl:text>abstract = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="AB2">
            <xsl:text>abstract = {</xsl:text>
            <xsl:value-of select="ibfab:getPLAIN(.)" disable-output-escaping="yes"/>
            <xsl:text>&#32;</xsl:text>
            <xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="N1|M1">
        <xsl:text>copyright = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="JO|T3">
        <xsl:text>journal = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="VL">
        <xsl:text>volume = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="SN">
        <xsl:text>issn = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="S1">
        <xsl:text>isbn = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="PY|Y1|Y2">
        <xsl:text>year = {</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="TI|T1">
        <xsl:text>title = {</xsl:text><xsl:value-of select="strutil:stripHtml(.)" disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="DO">
            <xsl:text>URL = {http://dx.doi.org/</xsl:text><xsl:value-of select="normalize-space(text())" disable-output-escaping="yes"/><xsl:text>},&#xD;&#xA;</xsl:text>
    </xsl:template>

 </xsl:stylesheet>