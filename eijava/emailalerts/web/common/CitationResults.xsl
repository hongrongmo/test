<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    exclude-result-prefixes="hlight java html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

    <xsl:template match="EI-DOCUMENT" >
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
        <xsl:apply-templates select="RN"/>
        <xsl:apply-templates select="VO"/>
        
        <xsl:apply-templates select="SD"/>
        
        <xsl:apply-templates select="MT"/>
        <xsl:apply-templates select="VT"/>
        
	<xsl:if test="not(string(SD))">
        	<xsl:apply-templates select="YR"/>
        </xsl:if>
        
        <xsl:apply-templates select="PD_YR"/>
  <!--  <xsl:apply-templates select="UPD"/> -->
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
<!--        <xsl:apply-templates select="KC"/> -->
        <xsl:apply-templates select="KD"/>
        <xsl:if test="$ascii='true'">
            <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="PFD"/>
        <xsl:apply-templates select="PIDD"/>
        <xsl:apply-templates select="PPD"/>
        <!-- added for 1884 -->
        <xsl:apply-templates select="PID"/>
        <xsl:apply-templates select="COPA"/>

        <xsl:apply-templates select="LA"/>
        <xsl:apply-templates select="NF"/>
        </a>

        <xsl:if test="$ascii='true'">
            <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="DOC/DB/DBNAME"/>

        <xsl:if test="$ascii='true'">
            <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="FTTJ"/>
        <xsl:apply-templates select="STT"/>

    </xsl:template>

    <xsl:template match="TI">
        <a CLASS="MedBlackText"><b><xsl:value-of select="." disable-output-escaping="yes"/></b></a><br/>
    </xsl:template>
    
    <xsl:template match="KC">
        <xsl:text> </xsl:text><b>Kind:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="KD">
        <xsl:text> </xsl:text><b>Kind:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
        <xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="AUS|IVS|EDS">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="RSP">
        <xsl:text> </xsl:text><b>Sponsor:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="RN_LABEL">
        <xsl:text> </xsl:text><b>Report:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="SO">
        <b>Source:</b><xsl:text> </xsl:text><i><xsl:value-of select="." disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VO">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SD">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MT">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><i><xsl:value-of select="." disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VT">
        <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
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
    <xsl:template match="PAS">
        <b> Assignee:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PASM">
        <b> Assignee:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAN">
        <b> Application number:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAP">
        <b> Patent number:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    
    <xsl:template match="PM">
        <b> Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PM1">
        <b>Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PFD">
        <br/>
        <b> Filing date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PIDD">
        <b> Patent issue date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PPD">
        <b> Publication date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="UPD">
        <b> Publication date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

  <!-- jam added for 1884 -->
    <xsl:template match="PID">
        <b> Patent issue date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
  <!-- jam added for 1884  AND INSPEC Patents-->
    <xsl:template match="COPA">
        <b> Country of application:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="PD_YR">
        <b> Publication date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="NV">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <!-- publisher will ONLY show if there is no other leading information
        so this will follow 'Source' label - no comma needed                -->
    <xsl:template match="PN">
        <xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="RN">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PA">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="p_PP">
        <xsl:text>, p </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP_pp">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> pp</xsl:text>
    </xsl:template>

    <xsl:template match="LA">
        <xsl:text> </xsl:text><b>Language:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="NF">
            <xsl:text> </xsl:text><b>Figures:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="DBNAME">
        <a CLASS="SmBlackText"><br/><b>Database:</b><xsl:text> </xsl:text><xsl:value-of select="."/></a><xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="FTTJ|STT">
        <br/>
        <a CLASS="SmBlackText"><xsl:value-of select="." disable-output-escaping="yes"/></a>
    </xsl:template>

    <xsl:template match="PF">
    <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>)</xsl:text>
    </xsl:template>

    <xsl:template match="AFF">
	<xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="AF">
            <span CLASS="SmBlackText"><xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>)</xsl:text></span>
    </xsl:template>
    <xsl:template match="EF">
            <span CLASS="SmBlackText"><xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>)</xsl:text></span>
    </xsl:template>
    <xsl:template match="AU|ED|IV">
        <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>

        <xsl:variable name="THIS-DOCUMENT-DB">
            <xsl:choose>
                <xsl:when test="(../../DOC/DB/DBMASK)='32'">1</xsl:when>
                <xsl:when test="(../../DOC/DB/DBMASK)='4096'">2</xsl:when>
                <xsl:otherwise><xsl:value-of select="../../DOC/DB/DBMASK"/></xsl:otherwise>
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

        <!-- the linked field here is ALWAYS 'AU' (ED or IV) -->
<!--
        <xsl:variable name="START-YEAR">
            <xsl:choose>
                <xsl:when test="string(//RESULTS-DATABASES/DATABASE[@ID=$DATABASE]/DATABASE-START)"><xsl:value-of select="//RESULTS-DATABASES/DATABASE[@ID=$DATABASE]/DATABASE-START"/></xsl:when>
                <xsl:when test="string(//SESSION-DATA/START-YEAR)"><xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:when>
                <xsl:when test="string(/PAGE/CUSTOMIZED-STARTYEAR)"><xsl:value-of select="/PAGE/CUSTOMIZED-STARTYEAR"/></xsl:when>
                <xsl:otherwise>1990</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="END-YEAR">
            <xsl:choose>
                <xsl:when test="string(//RESULTS-DATABASES/DATABASE[@ID=$DATABASE]/DATABASE-END)"><xsl:value-of select="//RESULTS-DATABASES/DATABASE[@ID=$DATABASE]/DATABASE-END"/></xsl:when>
                <xsl:when test="string(//SESSION-DATA/END-YEAR)"><xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:when>
                <xsl:when test="string(/PAGE/CUSTOMIZED-ENDYEAR)"><xsl:value-of select="/PAGE/CUSTOMIZED-ENDYEAR"/></xsl:when>
                <xsl:otherwise>2005</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
-->
    <!-- the variable $SEARCH-TYPE can be empty so string($SEARCH-TYPE) can be FALSE -->
    <!-- when there is no search in the XML output (when PRINTING) -->
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

        <!-- If the name does not contain the text Anon -->
        <xsl:if test="boolean(not(contains($NAME,'Anon')))">
            <A class="SpLink">
                <xsl:if test="string($SEARCH-TYPE)">
                    <xsl:choose>
                         <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">
                            <xsl:attribute name="href"><xsl:value-of select="$HREF-PREFIX"/>/controller/servlet/Controller?CID=expertSearchCitationFormat&amp;searchWord1={<xsl:value-of select="java:encode($NAME)"/>}<xsl:value-of select="java:encode(' WN AU')"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/>&amp;yearselect=yearrange&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href"><xsl:value-of select="$HREF-PREFIX"/>/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of select="java:encode($NAME)"/>}&amp;section1=AU&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/>&amp;yearselect=yearrange&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            <xsl:value-of select="$NAME" disable-output-escaping="yes"/>
            </A>
            <xsl:if test="position()=1 and (position()=last())">
                <xsl:if test="name(.)='ED'">
                    <a CLASS="SmBlackText"><xsl:text>, ed.</xsl:text> </a>
                </xsl:if>
            </xsl:if>
            <xsl:apply-templates select="AF"/>
            <xsl:apply-templates select="EF"/>
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

</xsl:stylesheet>