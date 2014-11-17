<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
	exclude-result-prefixes="java xsl html DD"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl" />
<xsl:include href="Footer.xsl" />
<xsl:include href="GlobalLinks.xsl"/>

<xsl:include href="common/forms/QuickSearchForm.xsl" />

<xsl:include href="uspto/USPTOQSForm.xsl"/>
<xsl:include href="engnetbase/ENGnetBASEQSForm.xsl"/>


<xsl:template match="QUICK-SEARCH">

	<xsl:variable name="DATABASE">
		<xsl:value-of select="SELECTED-DATABASE"/>
	</xsl:variable>

	<xsl:variable name="DATABASE-DISPLAYNAME">
		<xsl:value-of select="DD:getDisplayName(SELECTED-DATABASE)"/>
	</xsl:variable>

	<xsl:variable name="quicksearch">/engvillage/models/customer/SearchParameters.jsp</xsl:variable>

	<xsl:variable name="SESSION-ID">
		<xsl:value-of select="SESSION-ID"/>
	</xsl:variable>

    <xsl:variable name="SEARCH-OPTION-1">
      <xsl:value-of select="SESSION-DATA/SEARCH-PHRASE/SEARCH-OPTION-1"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-OPTION-2">
      <xsl:value-of select="SESSION-DATA/SEARCH-PHRASE/SEARCH-OPTION-2"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-OPTION-3">
      <xsl:value-of select="SESSION-DATA/SEARCH-PHRASE/SEARCH-OPTION-3"/>
    </xsl:variable>

    <xsl:variable name="DOCUMENT-TYPE">
      <xsl:value-of select="SESSION-DATA/DOCUMENT-TYPE"/>
    </xsl:variable>

    <xsl:variable name="TREATMENT-TYPE">
      <xsl:value-of select="SESSION-DATA/TREATMENT-TYPE"/>
    </xsl:variable>

    <xsl:variable name="DISCIPLINE-TYPE">
      <xsl:value-of select="SESSION-DATA/DISCIPLINE-TYPE"/>
    </xsl:variable>

    <xsl:variable name="START-YEAR">
    	<xsl:value-of select="SESSION-DATA/START-YEAR"/>
    </xsl:variable>

    <xsl:variable name="END-YEAR">
    	<xsl:value-of select="SESSION-DATA/END-YEAR"/>
    </xsl:variable>

    <xsl:variable name="YEAR-STRING">
    	<xsl:value-of select="YEAR-STRING"/>
    </xsl:variable>

<html>
	<head>
		<META http-equiv="Expires" content="0"/>
		<META http-equiv="Pragma" content="no-cache"/>
		<META http-equiv="Cache-Control" content="no-cache"/>
		<title>Engineering Village - Quick Search</title>
		<SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	</head>
  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0"
  		>

  <center>

	 <!-- INCLUDE THE HEADER -->
    <xsl:apply-templates select="HEADER">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      <xsl:with-param name="SEARCH-TYPE">Quick</xsl:with-param>
    </xsl:apply-templates>

	  <!-- INCLUDE THE GLOBAL LINKS BAR -->
    <xsl:apply-templates select="GLOBAL-LINKS">
		  <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		  <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		  <xsl:with-param name="LINK">Quick</xsl:with-param>
		</xsl:apply-templates>

	  <!-- Condition to check which databases's search form is to be displayed -->
		<xsl:choose>

  		<!-- Start of USPTO Quick Search Form -->
  		<xsl:when test="($DATABASE='8')">
  			<xsl:apply-templates select="USPTO-QUICK-SEARCH">
  				<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  				<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  			</xsl:apply-templates>
  		</xsl:when>
  		<!-- End of Inspec Quick Search form -->

  		<!-- Start of CRC ENGnetBASE Quick Search Form -->
  		<xsl:when test="($DATABASE='16')">
  			<xsl:apply-templates select="ENGnetBASE-QUICK-SEARCH">
  				<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  				<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  			</xsl:apply-templates>
  		</xsl:when>
  		<!-- End of CRC ENGnetBASE Quick Search form -->

  		<!-- Start of Combined Quick Search Form -->
  		<xsl:otherwise>
  			<xsl:apply-templates select="COMBINED-QUICK-SEARCH">
  			  	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  				<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  				<xsl:with-param name="YEAR-STRING" select="$YEAR-STRING"/>

  		  </xsl:apply-templates>
      </xsl:otherwise>
  		<!-- End of Combined  Quick Search form -->

		</xsl:choose>

		<!-- INCLUDE THE FOOTER -->
    <xsl:apply-templates select="FOOTER">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		</xsl:apply-templates>

  </center>

		<!-- ADDED BY JM -->
    <script type="text/javascript"  language="javascript">
      <xsl:comment>
      if(typeof(document.quicksearch.searchWord1) != 'undefined') {
        document.quicksearch.searchWord1.focus();
      }
      // </xsl:comment>
    </script>
    </body>
</html>

</xsl:template>
</xsl:stylesheet>
