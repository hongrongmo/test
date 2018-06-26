<xsl:stylesheet
   version="1.0"
   xmlns:ibfab="java:org.ei.data.insback.runtime.InspecArchiveAbstract"
   xmlns:strutil="java:org.ei.util.StringUtil"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   exclude-result-prefixes="xsl strutil ibfab"
>
<!--
   every line must end with CR/LF
   &#xD; is a CARRIAGE RETURN (ASCII Hex 13)
   &#xA; is a LINE FEED (ASCII Hex 10)
-->

   <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

   <xsl:preserve-space elements="text"/>

    <!-- default text() rule in parent, downloadRISAscii.xsl, prevents unwated elements that are in the XML data from appearing -->

   <xsl:template match="EI-DOCUMENT">
      <!-- This 'mode' enables us to start the
         record with the TY and then
         not repeat the same info again later -->
      <xsl:apply-templates select="TY" mode="start-record"/>
      <xsl:apply-templates />
   </xsl:template>

   <xsl:template match="TY" mode="start-record">
      <xsl:text>TY  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
   </xsl:template>
   <xsl:template match="TY">
      <!-- do nothing when not starting the record -->
   </xsl:template>

   <xsl:template match="BT">
      <xsl:text>BT  - </xsl:text><xsl:value-of select="normalize-space(.)" />
      <xsl:if test="string(../MD)">
         <xsl:text>, </xsl:text><xsl:value-of select="../MD" />
      </xsl:if>
      <xsl:text>&#xD;&#xA;</xsl:text>
   </xsl:template>

    <xsl:template match="AU|A1">
      <xsl:text>A1  - </xsl:text><xsl:value-of select="strutil:stripHtml(.)"/><xsl:text>&#xD;&#xA;</xsl:text>
      <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="ED">
      <xsl:text>ED  - </xsl:text><xsl:value-of select="strutil:stripHtml(.)"/><xsl:text>&#xD;&#xA;</xsl:text>
      <xsl:apply-templates />
    </xsl:template>
    <!-- <xsl:template match="IV|PAS|A2"> -->
    <xsl:template match="A2">
      <xsl:text>A2  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
      <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="JF|JO|JA|VL|IS|SP|EP|SN|BN|PB|UR">
      <xsl:value-of select="name()"/><xsl:text>  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="PY|Y1|Y2">
      <xsl:value-of select="name()"/><xsl:text>  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="TI|T1|T2|T3|N1|M2|M3">
      <xsl:value-of select="name()"/><xsl:text>  - </xsl:text><xsl:value-of select="strutil:stripHtml(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="U1|U2|U3|U4|U5">
      <xsl:value-of select="name()"/><xsl:text>  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="MH|KW|CV">
      <xsl:text>KW  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="CVS">
    	<xsl:apply-templates />
    </xsl:template>
    <xsl:template match="CVMS">
      	<xsl:apply-templates />
    </xsl:template>
    <xsl:template match="CVMA|CVMN|CVMP|CVM|CVA|CVN|CVP|MJS">
      	<xsl:text>KW  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>

   <xsl:template match="FL">
      <xsl:text>U2  - </xsl:text><xsl:value-of select="strutil:stripHtml(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
   </xsl:template>

   <xsl:template match="CY">
      <xsl:text>CY  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
   </xsl:template>
   <xsl:template match="AN">
      <xsl:text>U1  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
   </xsl:template>
    <xsl:template match="S1">
      <xsl:text>SN  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="AFF">
      <xsl:text>AD  - </xsl:text><xsl:for-each select="AF"><xsl:value-of select="strutil:stripHtml(.)" /><xsl:if test="position() != last()">; </xsl:if><xsl:text>&#xD;&#xA;</xsl:text></xsl:for-each>
    </xsl:template>
    <xsl:template match="AD">
      <xsl:text>AD  - </xsl:text><xsl:value-of select="strutil:stripHtml(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>
    <xsl:template match="N2">
      <xsl:text>N2  - </xsl:text><xsl:value-of select="strutil:stripHtml(.)"/><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="DO|L2">
      <xsl:text>DO  - </xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
      <xsl:text>L2  - http://dx.doi.org/</xsl:text><xsl:value-of select="normalize-space(.)" /><xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="AB2">
      <xsl:text>N2  - </xsl:text>
      <xsl:value-of select="ibfab:getPLAIN(.)" />
      <xsl:text>&#32;</xsl:text>
      <xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>

 </xsl:stylesheet>