<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:cal="java:java.util.GregorianCalendar"
    xmlns:htmlmanipulator="java:org.ei.util.HtmlManipulator"
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
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AN">EVLABELAccession numberEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//TT">EVLABELTitle of translationEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AUS">EVLABELAuthorEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AFS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PF)">EVLABELAuthor affiliationEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CAUS">EVLABELCorresponding authorEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//EDS">EVLABELEditorEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//IVS">EVLABELInventorEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//SO | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//RIL)">EVLABELSourceEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//SE">EVLABELAbbreviated source titleEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//RSP | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//SP">EVLABELSponsorEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//RN | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//RN_LABEL">EVLABELReport numberEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PN|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//I_PN | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BPN)">EVLABELPublisherEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//VOM">EVLABELVolumeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//IS">EVLABELIssueEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PP | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PP_pp | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//p_PP)">EVLABELPagesEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//SD">EVLABELIssue dateEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//MT">EVLABELMonograph titleEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//VT">EVLABELVolume titleEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PASM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//EASM)">EVLABELAssigneeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PAPIM">EVLABELApplication informationEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PAN | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PANS)">EVLABELApplication numberEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PINFO">EVLABELPatent informationEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PM|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PM1)">EVLABELPublication numberEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PIM">EVLABELPriority informationEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PD_YR | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//UPD | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PPD)">EVLABELPublication dateEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PA">EVLABELPaper numberEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//KC">EVLABELKindEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PAP">EVLABELPatent numberEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AUTHCD">EVLABELPatent authorityEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PFD">EVLABELFiling dateEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PIDD">EVLABELPatent issue dateEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//COPA">EVLABELCountry of applicationEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//YR | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BYR">EVLABELPublication yearEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//LA">EVLABELLanguageEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB//DBMASK='131072')]//SN">EVLABELISSNEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//E_ISSN">EVLABELE-ISSNEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BN">EVLABELISBNEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BN13">EVLABELISBN13EVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//DO">EVLABELDOIEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//ARTICLE_NUMBER">EVLABELArticle numberEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BPC">EVLABELBook page countEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//DERW">EVLABELDERWENT accession noEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CF">EVLABELConference nameEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//MD">EVLABELConference dateEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//ML">EVLABELConference locationEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CC">EVLABELConference codeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CN">EVLABELCODENEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CPUB | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PL">EVLABELCountry of publicationEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//FTTJ">EVLABELTranslation serial titleEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//DT">EVLABELDocument typeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AV">EVLABELAvailabilityEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//SC">EVLABELScopeEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AB | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AB2)">EVLABELAbstractEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//AT">EVLABELAbstract typeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//NR">EVLABELNumber of referencesEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//BKYS">EVLABELBook KeywordsEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CPO">EVLABELCompaniesEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CMS">EVLABELChemicalsEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//DSM">EVLABELDesignated statesEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//MJSM">EVLABELMajor termsEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//MH">EVLABELMain headingEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CVS">EVLABELControlled/Subject termsEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CRM">EVLABELCAS registry number(s)EVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//FLS">EVLABELUncontrolled termsEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CLS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//CLGM)">EVLABELClassification codeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//FSM">EVLABELField of searchEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//RGIS">EVLABELRegional termsEVLABEL,</xsl:if>
        <xsl:if test="(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PIDM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PIDEPM)">EVLABELIPC codeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PIDM8">EVLABELIPC-8 codeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PUCM">EVLABELUS classificationEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//PECM">EVLABELELCA codeEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//COL">EVLABELCollectionEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//LOCS">EVLABELCoordinatesEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//TRS">EVLABELTreatmentEVLABEL,</xsl:if>
        <xsl:if test="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT//DISPS">EVLABELDisciplineEVLABEL,</xsl:if>
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
        <xsl:if test="contains($headerString, 'EVLABELAccession numberEVLABEL')">
            <xsl:choose>
                <xsl:when test="AN">"<xsl:apply-templates select="AN" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELTitle of translationEVLABEL')">
            <xsl:choose>
                <xsl:when test="TT">"<xsl:apply-templates select="TT" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELAuthorEVLABEL')">
            <xsl:choose>
                <xsl:when test="AUS">"<xsl:apply-templates select="AUS" ></xsl:apply-templates>",</xsl:when>
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
        <xsl:if test="contains($headerString, 'EVLABELCorresponding authorEVLABEL')">
            <xsl:choose>
                <xsl:when test="CAUS">"<xsl:apply-templates select="CAUS" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELEditorEVLABEL')">
            <xsl:choose>
                <xsl:when test="EDS">"<xsl:apply-templates select="EDS" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELInventorEVLABEL')">
            <xsl:choose>
                <xsl:when test="IVS">"<xsl:apply-templates select="IVS" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELSourceEVLABEL')">
            <xsl:choose>
                <xsl:when test="SO">"<xsl:apply-templates select="SO" ></xsl:apply-templates>",</xsl:when>
                <xsl:when test="RIL">"<xsl:apply-templates select="RIL" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELAbbreviated source titleEVLABEL')">
            <xsl:choose>
                <xsl:when test="SE">"<xsl:apply-templates select="SE" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELSponsorEVLABEL')">
          <xsl:choose>
            <xsl:when test="RSP">"<xsl:apply-templates select="RSP" />",</xsl:when>
            <xsl:when test="SP">"<xsl:apply-templates select="SP" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELReport numberEVLABEL')">
          <xsl:choose>
            <xsl:when test="RN">"<xsl:apply-templates select="RN" />",</xsl:when>
            <xsl:when test="RN_LABEL">"<xsl:apply-templates select="RN_LABEL" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPublisherEVLABEL')">
            <xsl:choose>
                <xsl:when test="PN">"<xsl:apply-templates select="PN" ></xsl:apply-templates>",</xsl:when>
                <xsl:when test="I_PN">"<xsl:apply-templates select="I_PN" ></xsl:apply-templates>",</xsl:when>
                <xsl:when test="BPN">"<xsl:apply-templates select="BPN" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELVolumeEVLABEL')">
            <xsl:choose>
                <xsl:when test="VOM">"<xsl:apply-templates select="VOM" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELIssueEVLABEL')">
            <xsl:choose>
                <xsl:when test="IS">"<xsl:apply-templates select="IS" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPagesEVLABEL')">
          <xsl:variable name="pagesValue">
              <xsl:choose>
                    <xsl:when test="PP"><xsl:apply-templates select="PP" /></xsl:when>
                    <xsl:when test="PP_pp"><xsl:apply-templates select="PP_pp" /></xsl:when>
                    <xsl:when test="p_PP"><xsl:apply-templates select="p_PP" /></xsl:when>
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
                <xsl:when test="SD">"<xsl:apply-templates select="SD" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELMonograph titleEVLABEL')">
          <xsl:choose>
            <xsl:when test="MT">"<xsl:apply-templates select="MT" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELVolume titleEVLABEL')">
          <xsl:choose>
            <xsl:when test="VT">"<xsl:apply-templates select="VT" />",</xsl:when>
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
        <xsl:if test="contains($headerString, 'EVLABELApplication informationEVLABEL')">
          <xsl:choose>
            <xsl:when test="PAPIM">"<xsl:apply-templates select="PAPIM" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELApplication numberEVLABEL')">
            <xsl:choose>
                <xsl:when test="PAN">"<xsl:apply-templates select="PAN"/>",</xsl:when>
                <xsl:when test="PANS">"<xsl:apply-templates select="PANS" />",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPatent informationEVLABEL')">
          <xsl:choose>
            <xsl:when test="PINFO">"<xsl:apply-templates select="PINFO" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPublication numberEVLABEL')">
            <xsl:choose>
                <xsl:when test="(PM|PM1)">"<xsl:apply-templates select="PM"/><xsl:if test="(PM and PM1)"><xsl:text> and </xsl:text></xsl:if><xsl:apply-templates select="PM1"/>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
                </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPriority informationEVLABEL')">
          <xsl:choose>
            <xsl:when test="PIM">"<xsl:apply-templates select="PIM" />",</xsl:when>
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
        <xsl:if test="contains($headerString, 'EVLABELPaper numberEVLABEL')">
          <xsl:choose>
            <xsl:when test="PA">"<xsl:apply-templates select="PA" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELKindEVLABEL')">
          <xsl:choose>
            <xsl:when test="KC">"<xsl:apply-templates select="KC" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPatent numberEVLABEL')">
            <xsl:choose>
                <xsl:when test="PAP">"<xsl:apply-templates select="PAP" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELPatent authorityEVLABEL')">
            <xsl:choose>
                <xsl:when test="AUTHCD">"<xsl:apply-templates select="AUTHCD" />",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELFiling dateEVLABEL')">
            <xsl:choose>
                <xsl:when test="PFD">"<xsl:apply-templates select="PFD" ></xsl:apply-templates>",</xsl:when>
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
        <xsl:if test="contains($headerString, 'EVLABELPublication yearEVLABEL')">
            <xsl:choose>
                <xsl:when test="YR">"<xsl:apply-templates select="YR" ></xsl:apply-templates>",</xsl:when>
                <xsl:when test="BYR">"<xsl:apply-templates select="BYR" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELLanguageEVLABEL')">
            <xsl:choose>
                <xsl:when test="LA">"<xsl:apply-templates select="LA" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELISSNEVLABEL') and not(DOC/DB/DBMASK='131072')">
            <xsl:choose>
                <xsl:when test="SN">"<xsl:apply-templates select="SN" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELE-ISSNEVLABEL')">
            <xsl:choose>
                <xsl:when test="E_ISSN">"<xsl:apply-templates select="E_ISSN" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELISBNEVLABEL')">
            <xsl:choose>
                <xsl:when test="BN">"<xsl:apply-templates select="BN" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELISBN13EVLABEL')">
            <xsl:choose>
                <xsl:when test="BN13">"<xsl:apply-templates select="BN13" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELDOIEVLABEL')">
            <xsl:choose>
                <xsl:when test="DO">"<xsl:apply-templates select="DO" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELArticle numberEVLABEL')">
            <xsl:choose>
                <xsl:when test="ARTICLE_NUMBER">"<xsl:apply-templates select="ARTICLE_NUMBER" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELBook page countEVLABEL')">
            <xsl:choose>
                <xsl:when test="BPC">"<xsl:apply-templates select="BPC" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELDERWENT accession noEVLABEL')">
            <xsl:choose>
                <xsl:when test="DERW">"<xsl:apply-templates select="DERW" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELConference nameEVLABEL')">
            <xsl:choose>
                <xsl:when test="CF">"<xsl:apply-templates select="CF" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELConference dateEVLABEL')">
            <xsl:choose>
                <xsl:when test="MD">"<xsl:apply-templates select="MD" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELConference locationEVLABEL')">
            <xsl:choose>
                <xsl:when test="ML">"<xsl:apply-templates select="ML" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELConference codeEVLABEL')">
            <xsl:choose>
                <xsl:when test="CC">"<xsl:apply-templates select="CC" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCODENEVLABEL')">
            <xsl:choose>
                <xsl:when test="CN">"<xsl:apply-templates select="CN" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCountry of publicationEVLABEL')">
            <xsl:choose>
                <xsl:when test="CPUB">"<xsl:apply-templates select="CPUB" ></xsl:apply-templates>",</xsl:when>
                <xsl:when test="PL">"<xsl:apply-templates select="PL" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELTranslation serial titleEVLABEL')">
          <xsl:choose>
            <xsl:when test="FTTJ">"<xsl:apply-templates select="FTTJ" />",</xsl:when>
            <xsl:otherwise>"",</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELDocument typeEVLABEL')">
            <xsl:choose>
                <xsl:when test="DT">"<xsl:apply-templates select="DT" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELAvailabilityEVLABEL')">
            <xsl:choose>
                <xsl:when test="AV">"<xsl:apply-templates select="AV" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELScopeEVLABEL')">
            <xsl:choose>
                <xsl:when test="SC">"<xsl:apply-templates select="SC" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELAbstractEVLABEL')">
            <xsl:choose>
                <xsl:when test="AB2">"<xsl:apply-templates select="AB2"/>",</xsl:when>
                <xsl:when test="AB">"<xsl:apply-templates select="AB" />",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELAbstract typeEVLABEL')">
            <xsl:choose>
                <xsl:when test="AT">"<xsl:apply-templates select="AT" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELNumber of referencesEVLABEL')">
            <xsl:choose>
                <xsl:when test="NR">"<xsl:apply-templates select="NR" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELBook KeywordsEVLABEL')">
            <xsl:choose>
                <xsl:when test="BKYS">"<xsl:apply-templates select="BKYS" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCompaniesEVLABEL')">
            <xsl:choose>
                <xsl:when test="CPO">"<xsl:apply-templates select="CPO" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELChemicalsEVLABEL')">
            <xsl:choose>
                <xsl:when test="CMS">"<xsl:apply-templates select="CMS" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELDesignated statesEVLABEL')">
            <xsl:choose>
                <xsl:when test="DSM">"<xsl:apply-templates select="DSM" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELMajor termsEVLABEL')">
            <xsl:choose>
                <xsl:when test="MJSM">"<xsl:apply-templates select="MJSM" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELMain headingEVLABEL')">
            <xsl:choose>
                <xsl:when test="MH">"<xsl:apply-templates select="MH" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELControlled/Subject termsEVLABEL')">
            <xsl:choose>
                <xsl:when test="CVS">"<xsl:apply-templates select="CVS" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCAS registry number(s)EVLABEL')">
            <xsl:choose>
                <xsl:when test="CRM">"<xsl:apply-templates select="CRM" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELUncontrolled termsEVLABEL')">
            <xsl:choose>
                <xsl:when test="FLS">"<xsl:apply-templates select="FLS" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELClassification codeEVLABEL')">
            <xsl:choose>
                <xsl:when test="CLS">"<xsl:apply-templates select="CLS" />",</xsl:when>
                 <xsl:when test="CLGM">"<xsl:apply-templates select="CLGM" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELField of searchEVLABEL')">
            <xsl:choose>
                <xsl:when test="FSM">"<xsl:apply-templates select="FSM" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELRegional termsEVLABEL')">
            <xsl:choose>
                <xsl:when test="RGIS">"<xsl:apply-templates select="RGIS" />",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELIPC codeEVLABEL')">
            <xsl:choose>
                <xsl:when test="PIDM">"<xsl:apply-templates select="PIDM"/>",</xsl:when>
                <xsl:when test="PIDEPM">"<xsl:apply-templates select="PIDEPM" />",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELIPC-8 codeEVLABEL')">
            <xsl:choose>
                <xsl:when test="PIDM8">"<xsl:apply-templates select="PIDM8" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELUS classificationEVLABEL')">
            <xsl:choose>
                <xsl:when test="PUCM">"<xsl:apply-templates select="PUCM" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELELCA codeEVLABEL')">
            <xsl:choose>
                <xsl:when test="PECM">"<xsl:apply-templates select="PECM" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise>"",</xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCollectionEVLABEL')">
            <xsl:choose>
                <xsl:when test="COL">"<xsl:apply-templates select="COL" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCoordinatesEVLABEL')">
            <xsl:choose>
                <xsl:when test="LOCS">"<xsl:apply-templates select="LOCS" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELTreatmentEVLABEL')">
            <xsl:choose>
                <xsl:when test="TRS">"<xsl:apply-templates select="TRS" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELDisciplineEVLABEL')">
            <xsl:choose>
                <xsl:when test="DISPS">"<xsl:apply-templates select="DISPS" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELDatabaseEVLABEL')">
            <xsl:choose>
                <xsl:when test="DOC/DB/DBNAME">"<xsl:apply-templates select="DOC/DB/DBNAME" ></xsl:apply-templates>",</xsl:when>
                <xsl:otherwise><xsl:text>,</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="contains($headerString, 'EVLABELCopyrightEVLABEL')">
            <xsl:choose>
                <xsl:when test="CPRT">"<xsl:apply-templates select="CPRT" ></xsl:apply-templates>",</xsl:when>
                <xsl:when test="CPR">"<xsl:apply-templates select="CPR" ></xsl:apply-templates>",</xsl:when>
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
<xsl:template match="CPRT" priority="1">
    <xsl:value-of select="str:replaceString(.,'2011',cal:get(cal:getInstance(), 1))" />
</xsl:template>

<xsl:template match="PID|PUC|PEC" priority="1">
    <xsl:value-of select="translate(str:stripHtml(htmlmanipulator:replaceHtmlEntities(./CID)),$remove,'')" />
    <xsl:if test="not(position()=last())"> - </xsl:if>
</xsl:template>

</xsl:stylesheet>