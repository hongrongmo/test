<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

    <xsl:template match="EI-DOCUMENT">
        <xsl:apply-templates select="TI"/>
        <xsl:apply-templates select="AUS"/>
        <xsl:apply-templates select="EDS"/>
        <xsl:apply-templates select="IVS"/>

        <a CLASS="SmBlackText">

        <xsl:choose>
            <xsl:when test ="string(NO_SO)">
                <xsl:text></xsl:text>
            </xsl:when>
            <xsl:when test="string(SO)">
                <xsl:apply-templates select="SO"/>
            </xsl:when>
            <xsl:otherwise>
                <b>Source: </b>
            </xsl:otherwise>
        </xsl:choose>

    <xsl:apply-templates select="PF"/>
    <xsl:apply-templates select="RSP"/>
    <xsl:apply-templates select="RN_LABEL"/>

        <xsl:apply-templates select="PN"/>
        <xsl:apply-templates select="RN"/>
        <xsl:apply-templates select="VO"/>

        <xsl:apply-templates select="SD"/>

        <xsl:apply-templates select="MT"/>
        <xsl:apply-templates select="VT"/>

    <xsl:if test="not(string(SD))">
      <xsl:apply-templates select="YR"/>
    </xsl:if>

    <xsl:apply-templates select="PD_YR"/>
    <xsl:apply-templates select="NV"/>
    <xsl:apply-templates select="PA"/>
        <xsl:apply-templates select="PP"/>
        <xsl:apply-templates select="p_PP"/>
        <xsl:apply-templates select="PP_pp"/>

        <xsl:apply-templates select="PAS"/>
    <xsl:apply-templates select="PASM"/>
        <xsl:apply-templates select="PAN"/>
        
        <xsl:apply-templates select="PAP"/>
    <xsl:apply-templates select="PM"/>
    <xsl:apply-templates select="PM1"/>
    <xsl:apply-templates select="UPD"/>
    <xsl:apply-templates select="KD"/>
        
        <xsl:apply-templates select="PFD"/>
<!--      <xsl:apply-templates select="PIDD"/> -->
        <xsl:apply-templates select="PPD"/>
    <!-- added for 1884 -->
    <xsl:apply-templates select="PID"/>
    <xsl:apply-templates select="COPA"/>

    <xsl:apply-templates select="LA"/>
    <xsl:apply-templates select="NF"/>
    </a>

    <xsl:apply-templates select="DOC/DB/DBNAME"/>
        <P/>
    </xsl:template>

    <xsl:template match="TI">
        <a CLASS="MedBlackText"><b><xsl:value-of select="." disable-output-escaping="yes"/></b></a><xsl:text> </xsl:text><xsl:text>&#xD;&#xA;</xsl:text><br/>
    </xsl:template>
    <xsl:template match="AUS|EDS|IVS">
        <xsl:apply-templates />
    </xsl:template>

  <xsl:template match="KD">
    <xsl:text> </xsl:text><b>Kind:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
    <xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template match="PM">
    <b> Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>
  <xsl:template match="PM1">
    <b>Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>

    <xsl:template match="SO">
        <b>Source: </b><i><xsl:value-of select="." disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VO">
        <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SD">
        <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MT">
        <xsl:text>,</xsl:text><xsl:text> </xsl:text><i><xsl:value-of select="." disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VT">
        <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

  <xsl:template match="PD_YR">
    <b> Publication date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>
  <xsl:template match="NV">
    <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>

    <xsl:template match="PAS|PASM">
        <b> Assignee:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAN">
        <b> Application number:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAP">
        <b> Patent number:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PFD">
        <xsl:text>&#xD;&#xA;</xsl:text><br/>
        <b> Filing date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PPD|UPD">
        <b> Publication date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

  <xsl:template match="YR">
    <xsl:if test="../DOC/DB/DBNAME='NTIS'">
    <xsl:if test="(string(../PF)) or (string(../RSP)) or (string(../RN_LABEL))">
          <xsl:text>, </xsl:text>
    </xsl:if>
    <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:if>
    <xsl:if test="not(../DOC/DB/DBNAME='NTIS')">
          <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="PN">
    <xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>
    <xsl:template match="RN">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PA">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <!-- PAGE RANGES -->
    <xsl:template match="PP">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="p_PP">
        <xsl:text>, p</xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP_pp">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>pp </xsl:text>
    </xsl:template>

    <xsl:template match="LA">
        <xsl:text> </xsl:text><b>Language:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
  <xsl:template match="NF">
    <xsl:text> </xsl:text><b>Figures:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>

    <xsl:template match="SN">
        <xsl:text>&#xD;&#xA;</xsl:text><br/>
        <b> ISSN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="CN">
        <b> CODEN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="BN">
        <xsl:text>&#xD;&#xA;</xsl:text><br/>
        <b> ISBN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="CF">
        <xsl:text>&#xD;&#xA;</xsl:text><br/>
        <b>Conference:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MD">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="ML">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="SP">
        <b> Sponsor:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

  <xsl:template match="PF">
    <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>)</xsl:text>
  </xsl:template>
  <xsl:template match="RSP">
    <xsl:text> </xsl:text><b>Sponsor:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>
  <xsl:template match="RN_LABEL">
    <xsl:text> </xsl:text><b>Report:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>

    <xsl:template match="I_PN">
        <xsl:text>&#xD;&#xA;</xsl:text><br/>
        <b>Publisher:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PL">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PLA">
        <xsl:text>&#xD;&#xA;</xsl:text><br/>
        <b>Place of publication: </b><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="DBNAME">
        <xsl:text>&#xD;&#xA;</xsl:text><br/><a CLASS="SmBlackText"><b>Database:</b><xsl:text> </xsl:text><xsl:value-of select="."/></a>
        <xsl:text>&#xD;&#xA;</xsl:text><br/><xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="AFF">
        <a CLASS="SmBlackText"><xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>)</xsl:text></a>
    </xsl:template>

  <xsl:template match="PIDD">
    <b> Patent issue date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>

  <!-- jam added for 1884 -->
  <xsl:template match="PID">
    <b> Patent issue date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>
  <!-- jam added for 1884  AND INSPEC Patents-->
  <xsl:template match="COPA">
    <b> Country of application:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>


    <xsl:template match="AU|ED|IV">
        <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>

        <!-- If the name does not contain the text Anon -->
        <xsl:if test="boolean(not(contains($NAME,'Anon')))">
            <xsl:call-template name="LINK">
                <xsl:with-param name="TERM"><xsl:value-of select="$NAME"/></xsl:with-param>
                <xsl:with-param name="FIELD">AU</xsl:with-param>
            </xsl:call-template>
            <xsl:if test="position()=1 and (position()=last())">
                <xsl:if test="name(.)='ED'">
                    <a CLASS="SmBlackText"><xsl:text>, ed.</xsl:text></a>
                </xsl:if>
            </xsl:if>
            <xsl:apply-templates select="AFF"/>
        </xsl:if>
        <!-- If the name contains Anon text -->
        <xsl:if test="boolean(contains($NAME,'Anon'))"><A CLASS="SmBlackText"><xsl:value-of select="$NAME"/></A></xsl:if>
        <xsl:if test="not(position()=last())"><A CLASS="SmBlackText">;</A><xsl:text> </xsl:text></xsl:if>
        <xsl:if test="position()=last()">
            <xsl:if test="name(.)='ED' and not(position()=1)">
                <a CLASS="SmBlackText"><xsl:text> eds.</xsl:text></a>
            </xsl:if>
            <xsl:text> </xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template name="LINK">
        <xsl:param name="TERM"/>
        <xsl:param name="FIELD"/>
        <xsl:param name="NAME"/>

        <A CLASS="SpLink">
            <xsl:value-of select="$TERM" disable-output-escaping="yes"/>
        </A>

    </xsl:template>

</xsl:stylesheet>