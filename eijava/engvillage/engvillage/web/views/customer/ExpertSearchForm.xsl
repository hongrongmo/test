<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
	exclude-result-prefixes="xsl html java DD"
>

<!-- HERE WE INCLUDE THE GENERIC HEADER,FOOTER,GLOBAL LINKS, EXPERT SEARCH FORM, AND INSPEC EXPERT SEARCH FORM FILES -->
<xsl:include href="Header.xsl" />
<xsl:include href="Footer.xsl" />
<xsl:include href="GlobalLinks.xsl"/>

<xsl:include href="common/forms/ExpertSearchForm.xsl" />

<xsl:include href="uspto/USPTOESForm.xsl" />
<xsl:include href="engnetbase/ENGnetBASEESForm.xsl" />

<xsl:template match="QUICK-SEARCH">

  <xsl:variable name="DATABASE">
  	<xsl:value-of select="SELECTED-DATABASE"/>
  </xsl:variable>

  <xsl:variable name="DATABASE-DISPLAYNAME">
		<xsl:value-of select="DD:getDisplayName(SELECTED-DATABASE)"/>
  </xsl:variable>

  <xsl:variable name="SESSION-ID">
	  <xsl:value-of select="SESSION-ID"/>
  </xsl:variable>

  <xsl:variable name="START-YEAR">
	<xsl:value-of select="SESSION-DATA/START-YEAR"/>
  </xsl:variable>

  <xsl:variable name="YEAR-STRING">
    <xsl:value-of select="YEAR-STRING"/>
  </xsl:variable>


  <xsl:variable name="END-YEAR">
	<xsl:value-of select="SESSION-DATA/END-YEAR"/>
  </xsl:variable>

<html>
  <head>
    <META http-equiv="Expires" content="0"/>
   	<META http-equiv="Pragma" content="no-cache"/>
  	<META http-equiv="Cache-Control" content="no-cache"/>
	  <title>Engineering Village - Expert Search</title>
		<SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
  </head>

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

  <center>

	<!-- INCLUDE THE HEADER -->
	<xsl:apply-templates select="HEADER">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		<xsl:with-param name="SEARCH-TYPE">Expert</xsl:with-param>
	</xsl:apply-templates>
	<!-- INCLUDE THE GLOBAL LINKS BAR -->
	<xsl:apply-templates select="GLOBAL-LINKS">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		<xsl:with-param name="LINK">Expert</xsl:with-param>
	</xsl:apply-templates>
	<!-- CONDITION TO CHECK WHICH DATABASES'S SEARCH FORM IS TO BE DISPLAYED -->

  <xsl:choose>

	<!-- START OF USPTO EXPERT SEARCH FORM -->
	<xsl:when test="($DATABASE='8')">
		<xsl:apply-templates select="USPTO-EXPERT-SEARCH">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		</xsl:apply-templates>
	</xsl:when>
	<!-- END OF THE USPTO EXPERT SEARCH FROM  -->
  <xsl:when test="($DATABASE='16')">
		<xsl:apply-templates select="ENGnetBASE-EXPERT-SEARCH">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		</xsl:apply-templates>
	</xsl:when>

	<!-- Start of Expert Search Form -->
	<xsl:otherwise>
		<xsl:apply-templates select="EXPERT-SEARCH">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
			<xsl:with-param name="YEAR-STRING" select="$YEAR-STRING"/>
		</xsl:apply-templates>
	</xsl:otherwise>
	<!-- End of Expert Search form -->


 </xsl:choose>

	<!-- INCLUDE FOOTER  -->
	<xsl:apply-templates select="FOOTER">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
	</xsl:apply-templates>

  </center>
	</body>
  </html>

</xsl:template>
</xsl:stylesheet>