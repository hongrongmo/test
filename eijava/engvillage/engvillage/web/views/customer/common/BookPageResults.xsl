<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
    exclude-result-prefixes="schar hlight java html xsl"
>

    <xsl:output method="html" indent="no"/>
    <xsl:strip-space elements="html:* xsl:*" />

    <xsl:template mode="BOOK" match="EI-DOCUMENT">
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <xsl:apply-templates mode="BOOK" />
      </table>
    </xsl:template>

    <!-- sit on this -->
    <xsl:template mode="BOOK" match="NO_SO"/>

    <!-- top level elements with labels and nested value children -->
    <xsl:template mode="BOOK" match="BKYS|AGS|AUS|EDS|IVS|CLS|FLS|CVS|RGIS|DISPS|CTS|OCVS|OCLS|NDI|CHI|AOI|AFS|EFS|PASM|PEXM|PIM">
        <tr>
            <td valign="top" ><img src="/engresources/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
            <xsl:if test="string(@label)">
                <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
            </xsl:if>
            </td>
            <td valign="top" width="10"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
            <span CLASS="MedBlackText"><xsl:apply-templates mode="BOOK"/></span>
            </td>
        </tr>
        <xsl:call-template name="SPACER"/>
    </xsl:template>

    <xsl:template mode="BOOK" match="TI|TT">
      <tr>
        <td valign="top" ><img src="/engresources/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label"><span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b></span></td>
        <td valign="top" width="10"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left"><a CLASS="MedBlackText"><b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></b></a></td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>

    <xsl:template mode="BOOK" match="PAN|PAPD|PAPX|PANS|PANUS|PAPCO|PM|PM1">
      <tr>
        <td valign="top" ><img src="/engresources/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label"><span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b></span></td>
        <td valign="top" width="10"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left"><a CLASS="MedBlackText"><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></a></td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>

    <!-- top level LINKED node(s) with simple label/value children -->
    <xsl:template mode="BOOK" match="SN|BN|CN|CC|MH|MI|PNUM|E_ISSN|COL">
      <tr>
        <td valign="top" ><img src="/engresources/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label"><a CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b></a></td>
        <td valign="top" width="10"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left">
          <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
          </xsl:call-template>
        </td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>

    <!-- Controlled/Uncontrolled child node(s) within VALUE under FLS/CVS/AGS -->
    <xsl:template mode="BOOK" match="BKY|FL|CV|AG|CT|OC|PS|PA|RGI">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:variable name="SEPARATOR">
        <xsl:if test="(position() mod 10) = 0">
          <xsl:text> </xsl:text>
        </xsl:if>
        <a class="SmBlackText">&#160; - &#160;</a>
      </xsl:variable>
      <xsl:if test="not(position()=last())"><a CLASS="SmBlackText"><xsl:value-of select="$SEPARATOR"/></a></xsl:if>
    </xsl:template>

    <xsl:template mode="BOOK" match="FS">
      <xsl:text> </xsl:text>
      <span CLASS="MedBlackText"><xsl:value-of select="normalize-space(text())"/></span>
      <xsl:text> </xsl:text>
      <xsl:if test="not(position()=last())">
        <a class="SmBlackText">&#160; - &#160;</a>
      </xsl:if>
      <xsl:if test="position()=last()">
        <xsl:text> </xsl:text>
      </xsl:if>
    </xsl:template>

    <!-- SPECIAL child node(s) within VALUE under AUS, EDS, IVS -->
    <xsl:template mode="BOOK" match="AU|ED|IV">
      <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
      <!-- If the name does not contain the text Anon -->
      <xsl:if test="boolean(not(contains($NAME,'Anon')))">
        <xsl:call-template name="LINK">
          <xsl:with-param name="TERM"><xsl:value-of select="$NAME"/></xsl:with-param>
          <xsl:with-param name="FIELD">AU</xsl:with-param>
        </xsl:call-template>
      </xsl:if>
      <!-- If the name contains Anon text -->
      <xsl:if test="boolean(contains($NAME,'Anon'))"><A CLASS="SmBlackText"><xsl:value-of select="$NAME"/></A></xsl:if>
      <xsl:if test="(@label)">
        <A CLASS="MedBlackText"><Sup><xsl:value-of select="@label"/></Sup><xsl:text> </xsl:text></A>
      </xsl:if>
      <xsl:if test="not(position()=last())"><A CLASS="SmBlackText">;</A><xsl:text> </xsl:text></xsl:if>
      <xsl:if test="position()=last()"><xsl:text> </xsl:text></xsl:if>
    </xsl:template>

    <xsl:template mode="BOOK" match="PAS">
      <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
      <!-- If the name does not contain the text Anon -->
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="$NAME" /></xsl:with-param>
        <xsl:with-param name="FIELD">AF</xsl:with-param>
      </xsl:call-template>
      <!-- If the name contains Anon text -->
      <xsl:if test="(@label)">
        <A CLASS="MedBlackText"><Sup><xsl:value-of select="@label"/></Sup><xsl:text> </xsl:text></A>
      </xsl:if>
      <xsl:if test="not(position()=last())">; </xsl:if>
    </xsl:template>

    <!-- CLassification child node(s) within VALUE (under CLS) -->
    <xsl:template mode="BOOK" match="CL">
      <xsl:apply-templates mode="BOOK"/>
      <xsl:if test="not(position()=last())">
        <a class="SmBlackText">&#160; - &#160;</a>
      </xsl:if>
    </xsl:template>

    <!-- COPYRIGHT NOTICE - 'Utitlity'-type element  -->
    <!-- top level node(s) with label/value children -->
    <xsl:template mode="BOOK" match="CPR">
      <tr>
        <td valign="top" ><img src="/engresources/images/s.gif" border="0"/></td>
        <td valign="top" ><img src="/engresources/images/s.gif" border="0"/></td>
        <td valign="top" width="10"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left"><span CLASS="MedBlackText"><xsl:value-of select="." disable-output-escaping="yes"/></span></td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>

    <!-- default rule when displaying text values in this DETAILED stylesheet ONLY - markup/highlight matched terms -->
    <xsl:template mode="BOOK" match="text()">
      <xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template mode="BOOK" match="DOC">
      <tr>
        <td valign="top" ><img src="/engresources/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label">
          <span CLASS="MedBlackText"><b>Database:</b> </span>
        </td>
        <td valign="top" width="10"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left"><span CLASS="MedBlackText"><xsl:value-of select="DB/DBNAME" disable-output-escaping="yes"/></span></td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>

    <!-- DEFAULT RULE - simple display label/value fields-->
    <xsl:template mode="BOOK" match="*">
      <tr>
        <td valign="top" ><img src="/engresources/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label">
          <xsl:if test="string(@label)">
            <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
          </xsl:if>
        </td>
        <td valign="top" width="10"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left">
          <span CLASS="MedBlackText"><xsl:apply-templates mode="BOOK"/></span>
        </td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>
    <!-- END OF DEFAULT RULE - simple display label/value fields-->

    <!-- do not display these elements> -->
    <xsl:template mode="BOOK" match="CVR|THMB|THMBS">
    </xsl:template>
    <xsl:template mode="BOOK" match="TCO|PVD|FT|CPRT|KD|ID|NPRCT|RCT|CCT|VIEWS">
    </xsl:template>

  <!-- START OF UTILITIES named template(s), attribute-set(s) -->

  <!-- NAMED TEMPLATE, aka method -->
    <xsl:template mode="BOOK" name="SPACER">
      <tr><td valign="top" colspan="4" height="8"><img src="/engresources/images/s.gif" border="0" height="8"/></td></tr>
      <xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>

  <!-- NAMED TEMPLATE, aka method -->
    <xsl:template mode="BOOK" name="LINK">
      <xsl:param name="TERM"/>
      <xsl:param name="FIELD"/>
      <xsl:param name="CLASS"/>

      <xsl:variable name="ENCODED-SEARCH-TERM"><xsl:value-of select="java:encode(schar:preprocess(hlight:removeMarkup($TERM)))" disable-output-escaping="yes"/></xsl:variable>
      <xsl:variable name="TAG-RESULT-DBMASK"><xsl:value-of select="ancestor::EI-DOCUMENT/DOC/DB/DBMASK"/></xsl:variable>

      <xsl:variable name="THIS-DOCUMENT-DB">
        <xsl:choose>
          <xsl:when test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK)='32'">1</xsl:when>
	        <xsl:when test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK)='4096'">2</xsl:when>
          <xsl:otherwise><xsl:value-of select="ancestor::EI-DOCUMENT/DOC/DB/DBMASK"/></xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="YEARRANGE">
        <xsl:choose>
          <xsl:when test="string(//SESSION-DATA/LASTFOURUPDATES)"><xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES" /></xsl:when>
          <xsl:otherwise>yearrange</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

        <xsl:variable name="START-YEAR-PARAM">
          <xsl:choose>
            <xsl:when test="($FIELD='AU')"></xsl:when>
<!--      <xsl:when test="string(//SESSION-DATA/START-YEAR)">&amp;startYear=<xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:when> -->
            <xsl:otherwise></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="END-YEAR-PARAM">
          <xsl:choose>
            <xsl:when test="($FIELD='AU')"></xsl:when>
<!--      <xsl:when test="string(//SESSION-DATA/END-YEAR)">&amp;endYear=<xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:when> -->
            <xsl:otherwise></xsl:otherwise>
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
        <xsl:variable name="SEARCH-TYPE-TAG">
		      <xsl:value-of select="//SEARCH-TYPE-TAG"/>
        </xsl:variable>

        <xsl:variable name="SEARCH-TYPE">
          <xsl:choose>
      	    <xsl:when test="string($SEARCH-TYPE-TAG)">
		          <xsl:value-of select="//SEARCH-TYPE-TAG"/>
	          </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="//SEARCH-TYPE"/>
            </xsl:otherwise>
	        </xsl:choose>
        </xsl:variable>

        <xsl:variable name="DATABASE">
          <xsl:choose>
      	    <xsl:when test="($SEARCH-TYPE='TagSearch') or ($SEARCH-TYPE='tagSearch')">
              <xsl:value-of select="$TAG-RESULT-DBMASK"/>
	          </xsl:when>
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

        <!-- Default Sort Options for these links -->
        <!-- make changes here(like field or adding direction) -->
        <xsl:variable name="DEFAULT-LINK-SORT">sort=yr</xsl:variable>
        <!-- &amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/> is appended to HREF attribute -->

        <a class="SpLink">
        <xsl:if test="string($CLASS)">
          <xsl:attribute name="CLASS"><xsl:value-of select="$CLASS"/></xsl:attribute>
        </xsl:if>

        <xsl:if test="string($SEARCH-TYPE)">
          <xsl:choose>
            <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">
              <xsl:variable name="INDEX-FIELD">
                <xsl:choose>
                  <xsl:when test="($FIELD='PNUM')">CT</xsl:when>
                  <xsl:when test="($FIELD='E_ISSN')">SN</xsl:when>
                  <xsl:otherwise><xsl:value-of select="$FIELD"/></xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:variable name="ENCODED-SEARCH-SECTION"><xsl:value-of select="java:encode(' WN ')"/><xsl:value-of select="$INDEX-FIELD"/></xsl:variable>
              <xsl:attribute name="href">/controller/servlet/Controller?CID=expertSearchCitationFormat&amp;searchWord1=({<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}<xsl:value-of select="$ENCODED-SEARCH-SECTION"/>)&amp;database=<xsl:value-of select="$DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="INDEX-FIELD">
                <xsl:choose>
                  <xsl:when test="($FIELD='PNUM')">CT</xsl:when>
                  <xsl:when test="($FIELD='E_ISSN')">SN</xsl:when>
                  <xsl:when test="($FIELD='BKY')">KY</xsl:when>
                  <xsl:when test="($FIELD='COL')">CL</xsl:when>
                  <xsl:otherwise><xsl:value-of select="$FIELD"/></xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:attribute name="href">/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;searchWord1={<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}&amp;section1=<xsl:value-of select="$INDEX-FIELD"/>&amp;database=<xsl:value-of select="$DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/>&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:value-of select="hlight:addMarkup($TERM)" disable-output-escaping="yes"/></a>

    </xsl:template>

    <!-- attribute-set -->
    <xsl:attribute-set name="r-align-label">
      <xsl:attribute name="valign">top</xsl:attribute>
      <xsl:attribute name="align">right</xsl:attribute>
      <xsl:attribute name="width">150</xsl:attribute>
    </xsl:attribute-set>

  <!-- END   OF UTILITIES named template(s), attribute-set(s) -->
</xsl:stylesheet>