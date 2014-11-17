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

	<xsl:variable name="PERSONALIZATION">
		<xsl:value-of select="//PERSONALIZATION"/>
	</xsl:variable>

  <xsl:variable name="PERSONALIZATION-PRESENT">
  		<xsl:value-of select="//PERSONALIZATION-PRESENT"/>
  </xsl:variable>

  <xsl:variable name="SESSION-ID">
  	<xsl:value-of select="SESSION-ID"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-ID">
  		<xsl:value-of  select="SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="COUNT">
  		<xsl:value-of  select="COUNT"/>
  </xsl:variable>

  <xsl:variable name="DATABASE">
  		<xsl:value-of  select="DATABASE"/>
  </xsl:variable>

  <xsl:variable name="DOCIDS">
  		<xsl:value-of  select="DOCIDS"/>
  </xsl:variable>

  <xsl:variable name="QUERY-ID">
  		<xsl:value-of  select="QUERY-ID"/>
  </xsl:variable>

  <xsl:variable name="SOURCE">
  		<xsl:value-of  select="SOURCE"/>
  </xsl:variable>

  <xsl:variable name="OPTION-VALUE">
  	<xsl:value-of  select="OPTION-VALUE"/>
  </xsl:variable>

  <xsl:variable name="RESULTS-FORMAT">
  	<xsl:value-of  select="RESULTS-FORMAT"/>
  </xsl:variable>

  <xsl:variable name="DISPLAY-FORM">
  	<xsl:value-of  select="LOGIN-FORM/DISPLAY-FORM"/>
  </xsl:variable>

  <xsl:variable name="LOGIN-STATUS">
  	<xsl:value-of  select="LOGIN-FORM/LOGIN-STATUS"/>
  </xsl:variable>

  <xsl:variable name="EMAIL-EXISTS">
  	<xsl:value-of select="LOGIN-FORM/EMAIL-EXISTS"/>
  </xsl:variable>

  <xsl:variable name="CID">
  	<xsl:value-of select="CID"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-TYPE">
  	<xsl:value-of select="SEARCH-TYPE"/>
  </xsl:variable>

	<html>
	<head>
		<title>Personal Profile</title>
  	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
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
  </xsl:apply-templates>

  <!-- Gray bar -->
  <xsl:call-template name="P-GRAY-BAR"/>
  	
    <!-- Table below the logo area -->
  <P/>
  <table border="0" width="99%" cellspacing="0" cellpadding="0">
    <tr><td height="20"><img src="/static/images/s.gif" height="20"/></td></tr>
    <tr><td valign="top" colspan="2"><a class="EvHeaderText">My Profile</a></td></tr>
    <tr><td height="10"><img src="/static/images/s.gif" height="10"/></td></tr>
    <tr>
      <td valign="top">
        <xsl:if test="($PERSONALIZATION-PRESENT='true')">
          <xsl:variable name="NEXTURL">CID=viewSavedSearches&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>
          <p>
          <a class="LgBlueLink">
            <xsl:attribute name="HREF">
          	<xsl:choose>
          		<xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?CID=viewSavedSearches&amp;database=<xsl:value-of select="$DATABASE"/></xsl:when>
          		<xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/></xsl:otherwise>
          	</xsl:choose>
            </xsl:attribute>
            View/Update Saved Searches
          </a>
          <br/>
          <a class="MedBlackText">Manage your Saved Searches.</a>
          </p>
        </xsl:if>
        <xsl:if test="($PERSONALIZATION-PRESENT='true')">
          <xsl:variable name="NEXTURL">CID=viewSavedSearches&amp;database=<xsl:value-of select="$DATABASE"/>&amp;show=alerts</xsl:variable>
          <p>
          <a CLASS="LgBlueLink">
            <xsl:attribute name="HREF">
          	<xsl:choose>
          		<xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?CID=viewSavedSearches&amp;database=<xsl:value-of select="$DATABASE"/>&amp;show=alerts</xsl:when>
          		<xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/></xsl:otherwise>
          	</xsl:choose>
            </xsl:attribute>
            View/Update Alerts
          </a>
          <br/>
          <a class="MedBlackText">Manage your Email Alerts.</a>
          </p>
        </xsl:if>
        <xsl:if test="($PERSONALIZATION-PRESENT='true')">
          <xsl:variable name="NEXTURL">CID=viewPersonalFolders&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>
          <p>
          <a class="LgBlueLink">
            <xsl:attribute name="HREF">
          	<xsl:choose>
          		<xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=viewPersonalFolders&amp;database=<xsl:value-of select="$DATABASE"/></xsl:when>
          		<xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/></xsl:otherwise>
          	</xsl:choose>
            </xsl:attribute>
            View/Update Folders
          </a>
          <br/>
          <a class="MedBlackText">View, rename or delete your folders.</a>
          </p>
        </xsl:if>
        <xsl:if test="($PERSONALIZATION-PRESENT='true')">
          <xsl:variable name="NEXTURL">CID=editPersonalAccount&amp;editdisplay=true&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>
          <p>
          <a class="LgBlueLink">
            <xsl:attribute name="HREF">
          	<xsl:choose>
          		<xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?CID=editPersonalAccount&amp;editdisplay=true&amp;database=<xsl:value-of select="$DATABASE"/></xsl:when>
          		<xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/></xsl:otherwise>
          	</xsl:choose>
            </xsl:attribute>
            Edit/Remove Account
          </a>
          <br/>
          <a class="MedBlackText">View and change details of your Personal Account.</a>
          </p>
        </xsl:if>
      </td>
    </tr>
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
