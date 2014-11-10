<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/static/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
>
 <!--
 	 This page displays
 	 1. login form if the login status is false,
 	 2. login form with error message if the login fails and
 	 3. navigate to the appropriate page.
 -->

<xsl:include href="Header.xsl" />
<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="PersonalGrayBar.xsl" />
<xsl:include href="Footer.xsl" />

<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="CID">
	<xsl:value-of select="CID"/>
</xsl:variable>

<xsl:variable name="SEARCH-ID">
	<xsl:value-of  select="SEARCH-ID"/>
</xsl:variable>

<xsl:variable name="SEARCH-TYPE">
	<xsl:value-of select="SEARCH-TYPE"/>
</xsl:variable>


<xsl:variable name="DATABASE">
	<xsl:value-of  select="DATABASE"/>
</xsl:variable>

<xsl:variable name="SEARCH-TAGS">
	<xsl:value-of select="java:encode(SEARCH-TAGS)"/>
</xsl:variable>

<xsl:variable name="COUNT">
	<xsl:value-of  select="COUNT"/>
</xsl:variable>

<xsl:variable name="SCOPE">
	<xsl:value-of  select="SCOPE"/>
</xsl:variable>
<html>
<head>
	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
	<title>Tag Edite Confirm</title>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
<center>

<!-- APPLY HEADER -->
<xsl:apply-templates select="HEADER">
	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
	<xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
</xsl:apply-templates>

<!-- Insert the Global Links -->
<!-- logo, search history, selected records, my folders. end session -->
<xsl:apply-templates select="GLOBAL-LINKS">
	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
	<xsl:with-param name="LINK">Tags</xsl:with-param>
</xsl:apply-templates>

<!-- Gray Navigation Bar -->
<xsl:call-template name="P-GRAY-BAR">
	<xsl:with-param name="SESSIONID" select="$SESSION-ID"/>
	<xsl:with-param name="DATABASE" select="$DATABASE"/>
	<xsl:with-param name="SEARCHID" select="$SEARCH-ID"/>
	<xsl:with-param name="COUNT" select="$COUNT"/>
	<xsl:with-param name="SEARCHTYPE" select="$SEARCH-TYPE"/>
	<xsl:with-param name="CID" select="$CID"/>
</xsl:call-template>

<!-- Table below the logo area -->
<table border="0" width="80%" cellspacing="0" cellpadding="0">
<br/>
<br/>
<br/>
<br/>
	<center><span CLASS="MedBlackText">Your edits have been saved.<br/> Return to </span> 
	<a CLASS="LgBlueLink" href="/search/results/tags.url?CID=tagSearch&amp;COUNT=1&amp;database={$DATABASE}&amp;scope={$SCOPE}&amp;searchtags={$SEARCH-TAGS}">search results</a> <span CLASS="MedBlackText">, 
	or return to </span><a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=tagsLoginForm&amp;database={$DATABASE}">Tags + Groups</a> <span CLASS="MedBlackText"> home page.</span></center>
<br/>
<br/>
<br/>
<br/>
</table>

<!-- Insert the Footer table -->
<xsl:apply-templates select="FOOTER">
	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
</xsl:apply-templates>

</center>

</body>
</html>

</xsl:template>

</xsl:stylesheet>
