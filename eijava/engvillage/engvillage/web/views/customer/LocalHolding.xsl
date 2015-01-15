<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  exclude-result-prefixes="java html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

  <xsl:template match="LOCAL-HOLDINGS">
    <xsl:param name="vISSN"/>
    <xsl:apply-templates select="LOCAL-HOLDING">
      <xsl:with-param name="vISSN" select="$vISSN"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="LOCAL-HOLDINGS"  mode="CIT">
    <xsl:param name="vISSN"/>
    <xsl:param name="FULLTEXT-LINK">Y</xsl:param>

    <xsl:if test="($FULLTEXT-LINK='Y')">
        <A CLASS="MedBlackText">&#160; - &#160;</A>
    </xsl:if>      
    <xsl:apply-templates select="LOCAL-HOLDING"  mode="CIT">
      <xsl:with-param name="vISSN" select="$vISSN"/> 
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="LOCAL-HOLDING">
        <xsl:param name="vISSN"/>
    <xsl:variable name="vLINK-LABEL">
      <xsl:value-of select="LINK-LABEL"/>
    </xsl:variable>
    <xsl:variable name="vFUTURE-URL">
      <xsl:value-of select="FUTURE-URL"/>
    </xsl:variable>
    <xsl:variable name="vDYNAMIC-URL">
      <xsl:value-of select="DYNAMIC-URL"/>
    </xsl:variable>
    <xsl:variable name="vDEFAULT-URL">
      <xsl:value-of select="DEFAULT-URL"/>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="not($vISSN='') and not($vISSN='null')">
        <xsl:choose>
          <xsl:when test="not(FUTURE-URL='')">
            <a CLASS="SpLink" href="{$vDYNAMIC-URL}" target="new">
              <img src="{$vFUTURE-URL}" alt="{$vLINK-LABEL}" border="0" />
            </a>
          </xsl:when>
          <xsl:otherwise>
            <a CLASS="SpLink" href="{$vDYNAMIC-URL}" target="new">
              <xsl:value-of select="$vLINK-LABEL" />
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="not($vDEFAULT-URL='')">
        <xsl:choose>
          <xsl:when test="not(FUTURE-URL='')">
            <a CLASS="SpLink" href="{$vDEFAULT-URL}" target="new">
              <img src="{$vFUTURE-URL}" alt="{$vLINK-LABEL}" border="0" />
            </a>
          </xsl:when>
          <xsl:otherwise>
            <a CLASS="SpLink" href="{$vDEFAULT-URL}" target="new">
              <xsl:value-of select="$vLINK-LABEL" />
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
    </xsl:choose>
    <xsl:if test="not(position()=last())">
      <A CLASS="MedBlackText">&#160; - &#160;</A>
    </xsl:if>
  
  </xsl:template>


  <xsl:template match="LOCAL-HOLDING"  mode="CIT">
    <xsl:param name="vISSN"/>

    <xsl:variable name="vLINK-LABEL">
      <xsl:value-of select="LINK-LABEL"/>
    </xsl:variable>
    <xsl:variable name="vFUTURE-URL">
      <xsl:value-of select="FUTURE-URL"/>
    </xsl:variable>
    <xsl:variable name="vDYNAMIC-URL">
      <xsl:value-of select="DYNAMIC-URL"/>
    </xsl:variable>
    <xsl:variable name="vDEFAULT-URL">
      <xsl:value-of select="DEFAULT-URL"/>
    </xsl:variable>
    <xsl:if test="position()=1 or (position()=2)">
      <xsl:choose>
        <xsl:when test="not($vISSN='') and not($vISSN='null')">
          <xsl:choose>
            <xsl:when test="not(FUTURE-URL='')">
              <a CLASS="LgBlueLink" href="{$vDYNAMIC-URL}" target="new">
                <img src="{$vFUTURE-URL}" alt="{$vLINK-LABEL}" border="0" />
              </a>
            </xsl:when>
            <xsl:otherwise>
              <a CLASS="LgBlueLink" href="{$vDYNAMIC-URL}" target="new">
                <xsl:value-of select="$vLINK-LABEL" />
              </a>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="not($vDEFAULT-URL='')">
          <xsl:choose>
            <xsl:when test="not(FUTURE-URL='')">
              <a CLASS="LgBlueLink" href="{$vDEFAULT-URL}" target="new">
                <img src="{$vFUTURE-URL}" alt="{$vLINK-LABEL}" border="0" />
              </a>
            </xsl:when>
            <xsl:otherwise>
              <a CLASS="LgBlueLink" href="{$vDEFAULT-URL}" target="new">
                <xsl:value-of select="$vLINK-LABEL" />
              </a>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
      </xsl:choose>
      <xsl:if test="position()=1 and (not(position()=last()))">
          <A CLASS="MedBlackText">&#160; - &#160;</A>
      </xsl:if>
     </xsl:if>
  </xsl:template>
</xsl:stylesheet>