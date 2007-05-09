<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    xmlns:ibfab="java:org.ei.data.inspec.InspecArchiveAbstract"
    exclude-result-prefixes="hlight schar ibfab java html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

  <xsl:template match="EI-DOCUMENT">
      <xsl:param name="ascii">false</xsl:param>

      <xsl:apply-templates select="TI"/>
      <xsl:if test="$ascii='true'">
          <xsl:text>&#xD;&#xA;</xsl:text>
      </xsl:if>
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
      <xsl:apply-templates select="VO"/>

      <xsl:apply-templates select="RN"/>
      <xsl:apply-templates select="SD"/>
      <xsl:apply-templates select="PA"/>

      <xsl:apply-templates select="MT"/>
      <xsl:apply-templates select="VT"/>

      <xsl:apply-templates select="PAS"/>
      <xsl:apply-templates select="PAN"/>
      <xsl:apply-templates select="PAP"/>
      <xsl:apply-templates select="PFD"/>
      <xsl:apply-templates select="PPD"/>
      <!-- added for 1884 -->
      <xsl:apply-templates select="PID"/>

      <xsl:apply-templates select="COPA"/>

      <xsl:apply-templates select="YR"/>
      <xsl:apply-templates select="PD_YR"/>

      <xsl:if test="not(string(p_PP) or string(PP_pp))">
        <xsl:apply-templates select="PP"/>
      </xsl:if>

      <xsl:apply-templates select="p_PP"/>
      <xsl:apply-templates select="PP_pp"/>


      <xsl:apply-templates select="LA"/>

      <xsl:if test="string(SN) or string(CN) or string(BN)">
        <br/>
        <xsl:if test="$ascii='true'">
          <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
      </xsl:if>

      <xsl:apply-templates select="SN"/>
      <xsl:apply-templates select="CN"/>
      <xsl:apply-templates select="BN"/>

      <xsl:apply-templates select="CF"/>
      <xsl:apply-templates select="MD"/>
      <xsl:apply-templates select="ML"/>
      <xsl:apply-templates select="SP"/>
      <xsl:apply-templates select="CLOC"/>

      <xsl:apply-templates select="I_PN"/>
      <!-- <xsl:apply-templates select="PL"/> -->

      <xsl:apply-templates select="CPUB"/>

      <xsl:apply-templates select="FTTJ"/>
      </a>

      <xsl:apply-templates select="AB"/>
      <xsl:apply-templates select="AB2"/>
      <xsl:apply-templates select="IMG"/>
      <xsl:apply-templates select="NR"/>
      <xsl:apply-templates select="AT"/>
    <!--  <xsl:apply-templates select="CVS"/> -->

      <BR/>
      <xsl:if test="$ascii='true'">
        <xsl:text>&#xD;&#xA;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="DB"/>
      <P/>
    </xsl:template>

    <xsl:template match="TI">
      <a CLASS="MedBlackText"><b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></b></a><br/>
    </xsl:template>

    <xsl:template match="AUS|EDS|IVS">
      <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="RSP">
     <xsl:text> </xsl:text><b>Sponsor:</b><xsl:text> </xsl:text> <xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="RN_LABEL">
      <xsl:text> </xsl:text><b>Report:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="SO">
      <b>Source: </b><i><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VO">
      <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SD">
      <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MT">
      <xsl:text>,</xsl:text><xsl:text> </xsl:text><i><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VT">
      <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <!-- publisher will ONLY show if there is no other leading information
        so this will follow 'Source' label - no comma needed                -->
    <xsl:template match="PN">
      <xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="PAS">
      <b> Assignee:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAN">
      <b> Application number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAP">
      <b> Patent number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PFD">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b> Filing date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PPD">
      <b> Publication date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="YR">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PD_YR">
      <b> Publication date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="RN">
      <xsl:text>, </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PA">
      <xsl:text>, </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
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

    <xsl:template match="SN">
      <b>ISSN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="CN">
      <b>CODEN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="BN">
      <b>ISBN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="CF">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b>Conference:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MD">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="ML">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="SP">
      <b> Sponsor:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="I_PN">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b>Publisher:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PL">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PLA">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b>Place of publication: </b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>

    <!-- jam added for 1884 -->
    <xsl:template match="PID">
      <b> Patent issue date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="COPA">
      <b> Country of application:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CPUB">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b>Country of publication: </b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="FTTJ">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="AB">
      <xsl:text>&#xD;&#xA;</xsl:text>
      <span CLASS="MedBlackText"><a CLASS="MedBlackText"><br/><br/><b>Abstract: </b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></a></span>
    </xsl:template>
    <xsl:template match="AB2">
      <xsl:text>&#xD;&#xA;</xsl:text>
      <span CLASS="MedBlackText"><a CLASS="MedBlackText"><br/><br/><b>Abstract: </b><xsl:text> </xsl:text><xsl:value-of select="ibfab:getHTML(hlight:addMarkupCheckTagging(.))" disable-output-escaping="yes"/></a></span>
    </xsl:template>

    <!-- START OF IMAGES from INSPEC BACKFILE -->
    <xsl:template match="IMGGRP">
      <p/>
      <table border="0" cellspacing="0" cellpadding="0">
        <xsl:apply-templates />
      </table>
    </xsl:template>

    <xsl:template match="IROW">
      <tr>
      <xsl:apply-templates />
      </tr>
    </xsl:template>

    <xsl:template match="IMAGE">
      <xsl:variable name="cap" select= "text()"/>
      <td valign="top" align="middle"><a href="javascript:window.open('/fig/{@img}','newwind','width=650,height=600,scrollbars=yes,resizable,statusbar=yes');void('');"><img src="/fig/{@img}" alt="{$cap}" border="1" width="100" height="100"/></a>
      <br/><a class="SmBlackText"><xsl:value-of select="$cap" disable-output-escaping="yes"/></a></td><td valign="top" width="15"></td>
    </xsl:template>

    <xsl:template match="NR">
      <a CLASS="MedBlackText"><xsl:text> (</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> refs.)</xsl:text></a>
    </xsl:template>
    <xsl:template match="AT">
      <a CLASS="MedBlackText"><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/></a>
    </xsl:template>

    <xsl:template match="CVS">
      <xsl:variable name="PROVIDER"><xsl:value-of select="../PVD"/></xsl:variable>
      <xsl:text>&#xD;&#xA;</xsl:text>
      <a CLASS="MedBlackText"><br/><br/><b><xsl:value-of select="$PROVIDER"/> controlled terms: </b></a>
      <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="DB">
      <br/><a CLASS="SmBlackText"><b>Database:</b><xsl:text> </xsl:text><xsl:value-of select="."/></a>
    </xsl:template>
    
    <xsl:template match="STT">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <a CLASS="SmBlackText"><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></a>
    </xsl:template>

    <xsl:template match="PF">
      <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/><xsl:text>)</xsl:text>
    </xsl:template>

    <xsl:template match="AFF">
      <span CLASS="SmBlackText"><xsl:text> (</xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/><xsl:text>)</xsl:text></span>
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

    <xsl:template match="CV|MH">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">CV</xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())"><xsl:text> </xsl:text><A CLASS="SmBlackText"> | </A><xsl:text> </xsl:text></xsl:if>
    </xsl:template>

    <xsl:template name="LINK">

      <xsl:param name="TERM"/>
      <xsl:param name="FIELD"/>
      <xsl:param name="NAME"/>

      <xsl:variable name="ENCODED-SEARCH-TERM"><xsl:value-of select="java:encode(schar:preprocess(hlight:removeMarkup($TERM)))" disable-output-escaping="yes"/></xsl:variable>

      <xsl:variable name="THIS-DOCUMENT-DB">
        <xsl:choose>
          <xsl:when test="(ancestor::EI-DOCUMENT/child::DM)='32'">1</xsl:when>
          <xsl:when test="(ancestor::EI-DOCUMENT/child::DM)='4096'">2</xsl:when>
          <xsl:otherwise><xsl:value-of select="ancestor::EI-DOCUMENT/child::DM"/></xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="DATABASE">
        <xsl:choose>
          <!-- View Saved Records -->
          <xsl:when test="string(/PAGE/FOLDER-ID)">
            <xsl:value-of select="$THIS-DOCUMENT-DB"/>
          </xsl:when>
          <!-- View Selected Set -->
          <xsl:when test="contains(/PAGE/CID,'SelectedSet')">
            <xsl:choose>
              <!-- View Selected Set - Multiple Queries -->
              <xsl:when test="(/PAGE/HAS-MULTIPLE-QUERYS='true')">
                <xsl:value-of select="$THIS-DOCUMENT-DB"/>
              </xsl:when>
              <!-- View Selected Set - Single Query -->
              <xsl:otherwise>
                <xsl:value-of select="/PAGE/DBMASK"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <!-- Search Results Records -->
          <xsl:otherwise>
            <xsl:value-of select="/PAGE/DBMASK"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="ENCODED-DATABASE">
        <xsl:value-of select="java:encode($DATABASE)" />
      </xsl:variable>

      <xsl:variable name="YEARRANGE">
        <xsl:choose>
          <xsl:when test="string(//SESSION-DATA/LASTFOURUPDATES)"><xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES" /></xsl:when>
          <xsl:otherwise>yearrange</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <!-- the variable $SEARCH-TYPE can be empty so string($SEARCH-TYPE) can be FALSE -->
      <!-- when there is no search in the XML output -->
      <!-- -->
      <!-- //SEARCH-TYPE is defined in a SEARCHRESULT as the type of the current search -->
      <!-- //SEARCH-TYPE is defined in a SELECTEDSET by the type of the last search  -->
      <!-- //SEARCH-TYPE is defined in a FOLDER by the type of the last search  -->
      <!--  if there is no search before viewing a folder, the XML <SEARCH-TYPE>NONE</SEARCH-TYPE> -->
      <!--  is output by viewSavedRecordsOfFolder.jsp when recentXMLQuery==null -->
      <xsl:variable name="SEARCH-TYPE">
        <xsl:value-of select="//SEARCH-TYPE"/>
      </xsl:variable>

        <!-- Default Sort Options for these links -->
        <!-- make changes here(like field or adding direction) -->
        <xsl:variable name="DEFAULT-LINK-SORT">sort=yr</xsl:variable>
        <!-- &amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/> is appended to HREF attribute -->

      <A CLASS="SpLink">
        <xsl:if test="string($SEARCH-TYPE)">
          <xsl:choose>
            <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">
              <xsl:variable name="ENCODED-SEARCH-SECTION"><xsl:value-of select="java:encode(' WN ')"/><xsl:value-of select="$FIELD"/></xsl:variable>
              <xsl:attribute name="href">/controller/servlet/Controller?CID=expertSearchCitationFormat&amp;searchWord1=({<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}<xsl:value-of select="$ENCODED-SEARCH-SECTION"/>)&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="href">/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}&amp;section1=<xsl:value-of select="$FIELD"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/>&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="$NAME='MH'"><b><xsl:value-of select="hlight:addMarkup($TERM)" disable-output-escaping="yes"/></b></xsl:if>
        <xsl:if test="not($NAME='MH')"><xsl:value-of select="hlight:addMarkup($TERM)" disable-output-escaping="yes"/></xsl:if>
      </A>

    </xsl:template>

</xsl:stylesheet>