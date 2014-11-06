<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:cal="java:java.util.GregorianCalendar"
    xmlns:str="java:org.ei.util.StringUtil"
    exclude-result-prefixes="cal java xsl">

<!--
This xsl file will display the data of selected records from the  Search Results in an CSV format.
Important: Don't change the order of the elements in this file, this will change the order of CSV fields and corresponding data
Note: If you want to change the order, change it in dynamicHeader variable and in default template elements together
-->

<xsl:include href="downloadCSV.xsl" />
<xsl:output method="text" indent="no" encoding="utf-8" />
<xsl:strip-space elements="text"/>
<xsl:template match="/">
    <xsl:variable name="dynamicHeader" >
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//TI | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BTI)">EVLABELTitleEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BCT">EVLABELBook chapterEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='2048') and not(./DOC/DB/DBMASK='32768') and not(./DOC/DB/DBMASK='16384')]//AUS">EVLABELAuthorEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AFS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PF)">EVLABELAuthor affiliationEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//EDS">EVLABELEditorEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//IVS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[./DOC/DB/DBMASK[contains(',2048,32768,16384,',concat(',',text(),','))]]//AUS">EVLABELInventorEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//SO | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//RIL)">EVLABELSourceEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB//DBMASK='131072')]//BN">EVLABELISBNEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BN13 ">EVLABELISBN13EVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BPN">EVLABELPublisherEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//YR | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BYR">EVLABELPublication yearEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//RSP">EVLABELSponsorEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//RN_LABEL">EVLABELReportEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//RN">EVLABELReport numberEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//VO">EVLABELVolume and IssueEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PP | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PP_pp | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//p_PP | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BPP)">EVLABELPagesEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//SD">EVLABELIssue dateEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//MT">EVLABELMonograph titleEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PA">EVLABELPaper numberEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PASM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//EASM)">EVLABELAssigneeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PAN">EVLABELApplication numberEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PAP">EVLABELPatent numberEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PINFO | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PIM">EVLABELPatent informationEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PM|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PM1)">EVLABELPublication numberEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PD_YR | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//UPD | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PPD)">EVLABELPublication dateEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//KD">EVLABELKindEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PFD">EVLABELFiling dateEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PIDD">EVLABELPatent issue dateEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//COPA">EVLABELCountry of applicationEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//LA">EVLABELLanguageEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AV">EVLABELAvailabilityEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//DT[text()='Article in Press']">EVLABELArticle In PressEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//DT[text()='In Process']">EVLABELIn ProcessEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//COL">EVLABELCollectionEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//DBNAME">EVLABELDatabaseEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CPRT|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CPR)">EVLABELCopyrightEVLABEL,</xsl:if>
        <xsl:text>EVLABELData ProviderEVLABEL,</xsl:text>
   </xsl:variable>
    <xsl:variable name="headerWithOutEVLabel">
        <xsl:call-template name="string-replace-all">
          <xsl:with-param name="text" select="$dynamicHeader" />
          <xsl:with-param name="replace" select="'EVLABEL'" />
          <xsl:with-param name="by" select="''" />
        </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="normalize-space(substring($headerWithOutEVLabel, 1, string-length($headerWithOutEVLabel)-1))"/>
    <xsl:text>&#xD;&#xA;</xsl:text>
    <xsl:apply-templates select="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT" >
        <xsl:with-param name="headerString" select="$dynamicHeader" />
    </xsl:apply-templates>
</xsl:template>
<xsl:template match="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT">
    <xsl:param name="headerString"/>
    <xsl:variable name="dynamicRow" >
        <xsl:if test="contains($headerString, 'EVLABELTitleEVLABEL')">
          <xsl:choose>
            <xsl:when test="TI">"<xsl:apply-templates select="TI"/>",</xsl:when>
            <xsl:when test="BTI">"<xsl:apply-templates select="BTI" /><xsl:if test="string(BTST)">: <xsl:value-of select="BTST" disable-output-escaping="yes"/></xsl:if>",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELBook chapterEVLABEL')">
          <xsl:choose>
            <xsl:when test="BCT">"<xsl:apply-templates select="BCT" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELAuthorEVLABEL')">
            <xsl:choose>
                <xsl:when test="AUS and not(DOC/DB/DBMASK='2048') and not(DOC/DB/DBMASK='32768') and not(DOC/DB/DBMASK='16384')">"<xsl:apply-templates select="AUS" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELAuthor affiliationEVLABEL')">
            <xsl:choose>
                <xsl:when test="AFS">"<xsl:apply-templates select="AFS" />",</xsl:when>
                <xsl:when test="PF">"<xsl:apply-templates select="PF" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELEditorEVLABEL')">
          <xsl:choose>
            <xsl:when test="EDS">"<xsl:apply-templates select="EDS" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELInventorEVLABEL')">
            <xsl:choose>
                <xsl:when test="IVS">"<xsl:apply-templates select="IVS" ></xsl:apply-templates>",</xsl:when>
                <xsl:when test="(AUS and (DOC/DB/DBMASK[contains(',2048,32768,16384,',concat(',',text(),','))]))">"<xsl:apply-templates select="AUS" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELSourceEVLABEL')">
          <xsl:choose>
            <xsl:when test="SO">"<xsl:apply-templates select="SO" />",</xsl:when>
            <xsl:when test="RIL">"<xsl:apply-templates select="RIL" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELISBNEVLABEL')">
          <xsl:choose>
            <xsl:when test="BN and not(DOC/DB//DBMASK='131072')">"<xsl:apply-templates select="BN" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELISBN13EVLABEL')">
          <xsl:choose>
            <xsl:when test="BN13">"<xsl:apply-templates select="BN13" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPublisherEVLABEL')">
          <xsl:choose>
            <xsl:when test="BPN">"<xsl:apply-templates select="BPN" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPublication yearEVLABEL')">
          <xsl:choose>
            <xsl:when test="YR">"<xsl:apply-templates select="YR" />",</xsl:when>
            <xsl:when test="BYR">"<xsl:apply-templates select="BYR" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELSponsorEVLABEL')">
          <xsl:choose>
            <xsl:when test="RSP">"<xsl:apply-templates select="RSP" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELReportEVLABEL')">
          <xsl:choose>
            <xsl:when test="RN_LABEL">"<xsl:apply-templates select="RN_LABEL" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELReport numberEVLABEL')">
          <xsl:choose>
            <xsl:when test="RN">"<xsl:apply-templates select="RN" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELVolume and IssueEVLABEL')">
          <xsl:choose>
            <xsl:when test="VO">"<xsl:apply-templates select="VO" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPagesEVLABEL')">
          <xsl:variable name="pagesValue">
              <xsl:choose>
                    <xsl:when test="PP"><xsl:apply-templates select="PP" /></xsl:when>
                    <xsl:when test="PP_pp"><xsl:apply-templates select="PP_pp" /></xsl:when>
                    <xsl:when test="p_PP"><xsl:apply-templates select="p_PP" /></xsl:when>
                    <xsl:when test="BPP"><xsl:apply-templates select="BPP" /></xsl:when>
                    <xsl:otherwise></xsl:otherwise>
              </xsl:choose>
          </xsl:variable>
          <xsl:choose>
            <xsl:when test="contains($pagesValue,',')">"<xsl:value-of select="$pagesValue"/>",</xsl:when>
            <xsl:otherwise>"<xsl:value-of select="$pagesValue"/>",</xsl:otherwise>
         </xsl:choose>
       </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELIssue dateEVLABEL')">
          <xsl:choose>
            <xsl:when test="SD">"<xsl:apply-templates select="SD" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELMonograph titleEVLABEL')">
          <xsl:choose>
            <xsl:when test="MT">"<xsl:apply-templates select="MT" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPaper numberEVLABEL')">
          <xsl:choose>
            <xsl:when test="PA">"<xsl:apply-templates select="PA" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELAssigneeEVLABEL')">
          <xsl:choose>
            <xsl:when test="PASM">"<xsl:apply-templates select="PASM" />",</xsl:when>
            <xsl:when test="EASM">"<xsl:apply-templates select="EASM" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELApplication numberEVLABEL')">
          <xsl:choose>
            <xsl:when test="PAN">"<xsl:apply-templates select="PAN" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPatent numberEVLABEL')">
          <xsl:choose>
            <xsl:when test="PAP">"<xsl:apply-templates select="PAP" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPatent informationEVLABEL')">
          <xsl:choose>
            <xsl:when test="PINFO">"<xsl:apply-templates select="PINFO" />",</xsl:when>
            <xsl:when test="PIM">"<xsl:apply-templates select="PIM" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPublication numberEVLABEL')">
          <xsl:choose>
            <xsl:when test="(PM|PM1)">"<xsl:apply-templates select="PM"/><xsl:if test="(PM and PM1)"><xsl:text> and </xsl:text></xsl:if><xsl:apply-templates select="PM1"/>",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPublication dateEVLABEL')">
          <xsl:choose>

            <xsl:when test="PD_YR">"<xsl:apply-templates select="PD_YR" />",</xsl:when>
            <xsl:when test="UPD">"<xsl:apply-templates select="UPD"/>",</xsl:when>
            <xsl:when test="PPD">"<xsl:apply-templates select="PPD" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELKindEVLABEL')">
          <xsl:choose>
            <xsl:when test="KD">"<xsl:apply-templates select="KD" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELFiling dateEVLABEL')">
          <xsl:choose>
            <xsl:when test="PFD">"<xsl:apply-templates select="PFD" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPatent issue dateEVLABEL')">
          <xsl:choose>
            <xsl:when test="PIDD">"<xsl:apply-templates select="PIDD" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCountry of applicationEVLABEL')">
          <xsl:choose>
            <xsl:when test="COPA">"<xsl:apply-templates select="COPA" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELLanguageEVLABEL')">
            <xsl:choose>
                <xsl:when test="LA">"<xsl:apply-templates select="LA" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELAvailabilityEVLABEL')">
            <xsl:choose>
                <xsl:when test="AV">"<xsl:apply-templates select="AV" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELArticle In PressEVLABEL')">
          <xsl:choose>
            <xsl:when test="DT">"<xsl:apply-templates select="DT" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCollectionEVLABEL')">
          <xsl:choose>
            <xsl:when test="COL">"<xsl:apply-templates select="COL" />",</xsl:when>
            <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELDatabaseEVLABEL')">
          <xsl:choose>
            <xsl:when test="DOC/DB/DBNAME">"<xsl:apply-templates select="DOC/DB/DBNAME" />",</xsl:when>
            <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCopyrightEVLABEL')">
          <xsl:choose>
            <xsl:when test="CPRT">"<xsl:apply-templates select="CPRT" />",</xsl:when>
            <xsl:when test="CPR">"<xsl:apply-templates select="CPR" />",</xsl:when>
            <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELData ProviderEVLABEL')"><xsl:text>"Engineering Village",</xsl:text></xsl:if>
    </xsl:variable>
    <xsl:value-of select="normalize-space(substring($dynamicRow, 1, string-length($dynamicRow)-1))"/>
    <xsl:text>&#xD;&#xA;</xsl:text>
</xsl:template>
<xsl:template name="string-replace-all">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text,$replace)" />
        <xsl:value-of select="$by" />
        <xsl:call-template name="string-replace-all">
          <xsl:with-param name="text"
          select="substring-after($text,$replace)" />
          <xsl:with-param name="replace" select="$replace" />
          <xsl:with-param name="by" select="$by" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="DT" priority="1">
   <xsl:if test="text()='Article in Press'"><xsl:text> Article in Press</xsl:text></xsl:if>
   <xsl:if test="text()='In Process'"><xsl:text> In Process</xsl:text></xsl:if>
</xsl:template>

</xsl:stylesheet>