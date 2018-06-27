<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:ibfab="java:org.ei.data.insback.runtime.InspecArchiveAbstract"
	xmlns:cal="java:java.util.GregorianCalendar"
	xmlns:strutil="java:org.ei.util.StringUtil"
	exclude-result-prefixes="cal java xsl strutil"
>

<!--
This xsl file will display the data of selected records from the  Search Results in an Citation format.
All the business rules which govern the display format are also provided.
-->

<xsl:import href="common/DetailedResults.xsl" />

<xsl:output method="text" indent="no" encoding="iso-8859-1" />

<xsl:preserve-space elements="text"/>


  <xsl:template match="HEADER">

  	<xsl:variable name="CURRENT-DATE" select="cal:getInstance()" />
  	<!-- The Calendar class uses zero-based months;i.e. January is month 0, February is month 1, and
  	so on. We have to add one to get the customary month number. -->
  	<xsl:variable name="month" select="cal:get($CURRENT-DATE, 2) + 1" />
  	<xsl:variable name="day" select="cal:get($CURRENT-DATE, 5)" />
  	<xsl:variable name="year" select="cal:get($CURRENT-DATE, 1)" />
  	<xsl:text>Engineering Village Selected Records Download: </xsl:text><xsl:value-of select="$month" /><xsl:text>/</xsl:text><xsl:value-of select="$day" /><xsl:text>/</xsl:text><xsl:value-of select="$year" />
  	<xsl:text>&#xD;&#xA;</xsl:text>
  	<xsl:text>&#xD;&#xA;</xsl:text>

  </xsl:template>

  <xsl:template match="LINK"></xsl:template>


  <xsl:template match="SECTION-DELIM">
  	<xsl:apply-templates />
  </xsl:template>

  <xsl:template match="PAGE">
    <xsl:apply-templates select="PAGE-RESULTS"/>
  </xsl:template>

  <xsl:template match="PAGE-RESULTS">
    <xsl:apply-templates select="PAGE-ENTRY"/>
  </xsl:template>

	<xsl:template match="PAGE-ENTRY">

		<xsl:if test="string(DOCUMENTBASKETHITINDEX)">
			<xsl:text>&lt;RECORD </xsl:text><xsl:value-of select="DOCUMENTBASKETHITINDEX"/><xsl:text>&gt;</xsl:text>
			<xsl:text>&#xD;&#xA;</xsl:text>
		</xsl:if>

		<xsl:apply-templates select="EI-DOCUMENT"/>

		<xsl:text>&#xD;&#xA;</xsl:text>
		<xsl:text>&#xD;&#xA;</xsl:text>

	</xsl:template>

  <!-- override spacer to prevent char(160) from appearing in files - This character causes problems in China.
    This character is invisible in standard encodings -->
	<xsl:template name="DASH_SPACER" priority="1">
	  <xsl:text> - </xsl:text>
  </xsl:template>

  <!-- override the default CPR template in any included stylesheets -->
	<xsl:template match="CPR" priority="1">
  </xsl:template>

  <!-- output the CPRT - Text (non html) copyright element -->
	<xsl:template match="CPRT" priority="1">
   		<xsl:value-of select="strutil:replaceString(.,'2011',cal:get(cal:getInstance(), 1))" />
	</xsl:template>

	<xsl:template match="ABS" priority="1">
  	<a CLASS="MedBlackText"> <xsl:value-of select="ibfab:getPLAIN(.)" disable-output-escaping="yes"/></a>
 	</xsl:template>

	<!-- higher priority template to override template in DetailedResults to format Author Affiliation for ASCII -->
  <xsl:template match="AF|EF" priority="1">
    <xsl:if test="(@id)">(<xsl:value-of select="@id"/>)<xsl:text> </xsl:text></xsl:if>
    <xsl:value-of select="normalize-space(text())" disable-output-escaping="yes"/>
    <xsl:if test="not(position()=last())">;</xsl:if>
    <xsl:text> </xsl:text>
  </xsl:template>

	<!-- higher priority template to override template in DetailedResults to format Author for ASCII -->
  <xsl:template match="AU|ED|IV" priority="1">
    <xsl:value-of select="normalize-space(text())"/><xsl:if test="name()='IV'"><xsl:if test="CO"><xsl:text> (</xsl:text><xsl:value-of select="CO" /><xsl:text>) </xsl:text>
      </xsl:if>
    </xsl:if>

    <!-- put author affiliation id as a value in parens following the author name -->
    <xsl:if test="(@id)"><xsl:text> </xsl:text>(<xsl:value-of select="@id"/>)</xsl:if>
    <xsl:if test="not(position()=last())">;</xsl:if>
    <xsl:text> </xsl:text>
  </xsl:template>

  <!-- overrride IPC ECLS and US Codes to remove ClassTitle Display which comes out in HTML -->
   <xsl:template match="PID|PUC|PEC" priority="1">
		<xsl:value-of select="./CID" />
    <xsl:if test="not(position()=last())">; </xsl:if>
   </xsl:template>

   <!-- overrride AN to add line break -->
   <xsl:template match="AN" priority="1">
         <xsl:if test="string(@label)">
		<xsl:value-of select="@label"/>:</xsl:if><xsl:value-of select="."/><xsl:text>&#xD;&#xA;</xsl:text>
   </xsl:template>

   <!-- overrride AN to remove some special character -->
   <xsl:template match="CAU">
       	<xsl:if test="string(text())"><xsl:value-of select="normalize-space(text())" disable-output-escaping="yes"/></xsl:if><xsl:if test="EMAIL">(<A CLASS="SpLink"><xsl:attribute name="href">mailto:<xsl:value-of select="EMAIL"/></xsl:attribute><xsl:attribute name="title">Corresponding author</xsl:attribute><xsl:value-of select="EMAIL"/></A>)</xsl:if>
    	</xsl:template>

</xsl:stylesheet>
