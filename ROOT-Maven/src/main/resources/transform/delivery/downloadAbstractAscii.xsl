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

<xsl:include href="common/AbstractResults.xsl" />

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

	<xsl:template match="PAGE">
		<xsl:apply-templates select="HEADER"/>
		<xsl:apply-templates select="PAGE-RESULTS"/>
	</xsl:template>


	<xsl:template match="PAGE-RESULTS">
		<xsl:apply-templates select="PAGE-ENTRY"/>
	</xsl:template>

	<xsl:template match="LINK"></xsl:template>

	<xsl:template match="PAGE-ENTRY">

		<xsl:if test="string(DOCUMENTBASKETHITINDEX)">
			<xsl:text>&lt;RECORD </xsl:text><xsl:value-of select="DOCUMENTBASKETHITINDEX"/><xsl:text>&gt;</xsl:text>
			<xsl:text>&#xD;&#xA;</xsl:text>
		</xsl:if>

		<xsl:apply-templates select="EI-DOCUMENT" >
			<xsl:with-param name="ascii">true</xsl:with-param>
		</xsl:apply-templates>
		<xsl:text>&#xD;&#xA;</xsl:text>
		<xsl:choose>
			<xsl:when test="string(EI-DOCUMENT/CPRT)"><xsl:value-of select="strutil:replaceString(EI-DOCUMENT/CPRT,'2011',cal:get(cal:getInstance(), 1))" /></xsl:when>
			<xsl:otherwise><xsl:value-of select="EI-DOCUMENT/CPR"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text>&#xD;&#xA;</xsl:text>
		<xsl:text>&#xD;&#xA;</xsl:text>

	</xsl:template>

  <!-- override spacer to prevent char(160) from appearing in files - This character causes problems in China.
    This character is invisible in standard encodings -->
	<xsl:template name="DASH_SPACER" priority="1">
	  <xsl:text> - </xsl:text>
  </xsl:template>

	<xsl:template name="DOUBLE_SPACER" priority="1">
	  <xsl:text>  </xsl:text>
  </xsl:template>

  <xsl:template name="COMMA_SPACER" priority="1">
    <xsl:text> , </xsl:text>
  </xsl:template>


	<xsl:template match="AB2" priority="1">
		<xsl:text>&#xD;&#xA;</xsl:text>
		<a CLASS="MedBlackText"><br/><br/><b>Abstract: </b><xsl:text> </xsl:text><xsl:value-of select="ibfab:getPLAIN(.)" disable-output-escaping="yes"/></a>
    	</xsl:template>

</xsl:stylesheet>
