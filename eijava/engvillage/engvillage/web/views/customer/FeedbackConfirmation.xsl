<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="html xsl"
>

<!-- This xsl renders FeedbackConfirmation.jsp.This xsl basically displays that the feedback message has been sent -->
<!-- This page is for a customer so we include Header ,GlobalLinks,Footer.So that user can navigate thru.There is seperate xsl for non customer-->
<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="DBMASK">
	<xsl:value-of select="DBMASK"/>
</xsl:variable>

<html>
<head>
    <title>Customer Feedback Sent</title>
  	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
</head>

	<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

    <center>

    <!-- Start of logo table -->
    <xsl:apply-templates select="HEADER"/>
   
    <!-- Insert the Global Link table -->
    <xsl:apply-templates select="GLOBAL-LINKS">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
    </xsl:apply-templates>

    <table border="0" width="99%" cellspacing="0" cellpadding="0">
    	<tr><td valign="top" colspan="10" height="20" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" border="0"/></td></tr>
    </table>
  
    <!-- Start of the lower area below the navigation bar -->
    <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
    <tr><td valign="top" height="20"><img src="/engresources/images/s.gif" border="0" height="20"/></td></tr>
    <tr><td valign="top"><a class="EvHeaderText">Thank you</a></td></tr>
    <tr><td valign="top" height="5"><img src="/engresources/images/s.gif" border="0" height="5"/></td></tr>
    <tr><td valign="top">
    <P><A CLASS="MedBlackText">Thank you for your feedback! We value every comment sent to us and will respond to your feedback as quickly as possible.
    To continue browsing Engineering Village, click </A>
    <a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=home">here</a><A CLASS="MedBlackText"> to return to the home page .
    </A></P>
    </td></tr>
    </table>
    <!-- End of lower area below the navigation bar. -->

    <xsl:apply-templates select="FOOTER">
    	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    	<xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
    </xsl:apply-templates>

  </center>

  </body>
</html>

</xsl:template>
</xsl:stylesheet>
