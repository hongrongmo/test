<?xml version="1.0"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLDecoder"
  xmlns:stringUtil="java:org.ei.util.StringUtil"
  exclude-result-prefixes="java xsl html"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template match="TREC">

<html>
<head>
  <title>Scope notes</title>
  <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
  <script type="text/javascript">
  //<![CDATA[
  function initialize()
  {
  var strdomain = document.domain;
  var apikeys = { "www.engineeringvillage.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhTq1aVOHKz326Ly74ktyh0JEC50cxRcwqEG9_IZn1rf-kXlYHD0OAjeKw", "cert.engineeringvillage.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhTq1aVOHKz326Ly74ktyh0JEC50cxRcwqEG9_IZn1rf-kXlYHD0OAjeKw", "www.engineeringvillage2.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhQycePdRUCs6MMAMRXc5pXGU3hn3hRfr_zEZfLKIyUShHi94g2KWSfGEw","www.engineeringvillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhSAdZp0PxlTEhTFv3KMS9ULbNb5zBRqzELdPImli2KTwR6NaFokcBv4bA","www.engineeringvillage2.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhSQVDkHczPvGQBBH-1zyyQlxvW27BTYxtGf1JR2Az5uJMv5BiYUcHc0fg","www.chemvillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhRc2oirmkLapUOdAKcS-7VrLe4NmRRygHzNcODvNaPwfo8u7kjzm8R3Pg","www.papervillage2.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhRoQ9NpbmNyNRg_ZVHqDXsW5KVm9BQAIDpSj4LWpPYl79Ju3n8FsZYtiA","www.paperchem.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhSwBt7CUughPYSQRGOq0g9Cnw2rgRS09l1TiHYrU0IFHRsrrd0Ioscj6g","www.papervillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhR4KYQ73p3WzPF7syNfyrFgFZtwaxR5sJ7ySrsRekJV7hIExrrtRqD08g","www.papervillage2.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhS3XaAm7oOu6A7GrMVbYRe2u9FCpRSfk-An4_9sm-2YoETe1SnzwtWffg","www.apiencompass.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhRHcUpwnapUojBcsWU6PxPlD9oAeBR6FefYn5tiecY3qDiH2LruH550xw","www.eiencompass.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhR1px2Xrv_o7VDsm6kNUoHpHa-UvhQXJf3NTv0tT22yWiSZ3EogdInG7Q","www.encompassvillage.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhQyD6pGsbDZ_IMKl1sG3ErY3WRqNRTxQeViRtl5W6vKzuRQpp1iqSg8Ig","www.encompassvillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhQG36b3Ti2jJLY9hFCzqh5YnzGkahRkPw6Cz4ehvpyGlSKiP6_Kp_z9Ug"};
  var domainkey = apikeys[strdomain];
  var staticImage = null;
  staticImage = document.getElementById("mapStaticImage");
  if(staticImage != null)
  {
  	var staticImageURL = staticImage.getAttribute("src") + domainkey;
  	staticImage.setAttribute("src",staticImageURL);
  }
  }
  //]]>
  </script>
  <style type="text/css">
    body {font-family:arial,verdana,geneva; margin: 5; padding: 5; background: #CEEBFF;}
  </style>
</head>

<body bgcolor="#CEEBFF" topmargin="0">
<xsl:attribute name="onload">initialize()</xsl:attribute>
<table border="0" cellspacing="0" cellpadding="0" width="99%">
  <tr><td valign="top" height="4"><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
  <tr><td valign="top" align="right"><a href="javascript:window.close();"><img src="/static/images/close.gif" border="0"/></a></td></tr>
  <tr><td valign="top" height="10"><img src="/static/images/s.gif" border="0" height="10"/></td></tr>
  <tr><td valign="top"><a CLASS="DBlueText"><b><xsl:value-of select="MT"/></b></a></td></tr>

	<xsl:apply-templates select="SN"/>
	<xsl:apply-templates select="DATE"/>
	<xsl:apply-templates select="HSN"/>
	<xsl:apply-templates select="CPAGE"/>
	<xsl:apply-templates select="TYPE"/>
	<xsl:apply-templates select="COORDINATES"/>
	
  <tr><td valign="top" height="20"><img src="/static/images/s.gif" border="0" height="20"/></td></tr>

</table>
</body>
</html>

</xsl:template>

<xsl:template match="CPAGE">
	<tr><td valign="top" height="6"><img src="/static/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top">
	<a CLASS="SmBlackText"><b>Related classification codes: </b>
		<xsl:apply-templates/>
	</a></td></tr>
</xsl:template>

<xsl:template match="CL">
	<xsl:apply-templates select="CID"/>:
	<xsl:value-of select="CTI" disable-output-escaping="yes"/>
	<xsl:if test="not(position()=last())">; </xsl:if>
</xsl:template>

<xsl:template match="SN">
	 <xsl:variable name="snValue">
	    <xsl:value-of select="java:decode(.)"/>
	</xsl:variable>
    <xsl:variable name="firstPart">
	    <xsl:value-of select="stringUtil:getEptSnFirstPart($snValue)"/>
	</xsl:variable>
	<xsl:variable name="appendixValue">
	    <xsl:value-of select="stringUtil:getEptSnAppendixValue($snValue)"/>
	</xsl:variable>
	<xsl:variable name="lastPart">
	    <xsl:value-of select="stringUtil:getEptSnLastPart($snValue)"/>
	</xsl:variable>
	<tr><td valign="top" height="6"><img src="/static/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>Scope notes: </b>
	<xsl:value-of select="$firstPart"/></a>
	<xsl:choose>
		<xsl:when test="$appendixValue='Appendix B'">
			<a href="/engresources/help/encompass/Appendb2013.pdf" CLASS="SmBlackText" target="_blank"><xsl:value-of select="$appendixValue"/></a>
		</xsl:when>
		<xsl:when test="$appendixValue='Appendix C'">
			<a href="/engresources/help/encompass/Appendc2013.pdf" CLASS="SmBlackText" target="_blank"><xsl:value-of select="$appendixValue"/></a>
		</xsl:when>
		<xsl:when test="$appendixValue='Appendix D'">
			<a href="/engresources/help/encompass/Appendd2013.pdf" CLASS="SmBlackText" target="_blank"><xsl:value-of select="$appendixValue"/></a>
		</xsl:when>
		<xsl:when test="$appendixValue='Appendix E'">
			<a href="/engresources/help/encompass/Appende2013.pdf" CLASS="SmBlackText" target="_blank"><xsl:value-of select="$appendixValue"/></a>
		</xsl:when>
		<xsl:when test="$appendixValue='Appendix F'">
			<a href="/engresources/help/encompass/Appendf2013.pdf" CLASS="SmBlackText" target="_blank"><xsl:value-of select="$appendixValue"/></a>
		</xsl:when>
		<xsl:when test="$appendixValue='Appendix G'">
			<a href="/engresources/help/encompass/Appendg2013.pdf" CLASS="SmBlackText" target="_blank"><xsl:value-of select="$appendixValue"/></a>
		</xsl:when>
		<xsl:when test="$appendixValue='Appendix B'">
			<a href="/engresources/help/encompass/Appendb2013.pdf" CLASS="SmBlackText" target="_blank"><xsl:value-of select="$appendixValue"/></a>
		</xsl:when>
		<xsl:otherwise>
			<a CLASS="SmBlackText"><xsl:value-of select="$appendixValue"/></a>
		</xsl:otherwise>
	</xsl:choose>
	<a CLASS="SmBlackText"><xsl:value-of select="$lastPart"/>
	</a></td></tr>
</xsl:template>

<xsl:template match="SI">
	<tr><td valign="top" height="6"><img src="/engresources/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>Search Info: </b>
		<xsl:value-of select="java:decode(.)"/>
	</a></td></tr>
</xsl:template>

<xsl:template match="DATE">
	<tr><td valign="top" height="6"><img src="/static/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>Introduced: </b>
		<xsl:value-of select="."/>
	</a></td></tr>
</xsl:template>

<xsl:template match="OPT">
	(<xsl:apply-templates/>)
</xsl:template>

<xsl:template match="CID">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="HSN">
	<tr><td valign="top" height="6"><img src="/static/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>History: </b>
		<xsl:value-of select="."/>
	</a></td></tr>
</xsl:template>

<xsl:template match="TYPE">
	<tr><td valign="top" height="6"><img src="/static/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>Type of term: </b>
		<xsl:value-of select="."/>
	</a></td></tr>
</xsl:template>

<xsl:template match="COORDINATES">
	<tr><td valign="top" height="6"><img src="/static/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>Coordinates: </b>
		<xsl:value-of select="."/>
	</a>
	<tr><td valign="top" height="20"><img src="/static/images/s.gif" border="0" height="20"/></td></tr>
	
	<xsl:if test="/TREC/DRAWMAP">	
	  <xsl:variable name="COORD1">
	    <xsl:value-of select="//TREC/COORD1"/>
	  </xsl:variable>
	  <xsl:variable name="COORD2">
	    <xsl:value-of select="//TREC/COORD2"/>
	  </xsl:variable>
	  <xsl:variable name="COORD3">
	    <xsl:value-of select="//TREC/COORD3"/>
	  </xsl:variable>
	  <xsl:variable name="COORD4">
	    <xsl:value-of select="//TREC/COORD4"/>
	  </xsl:variable>
	  
	  <xsl:if test="$COORD1 != $COORD2 and $COORD3 != $COORD4">
	    <xsl:variable name="GMAPPATH">
	      <xsl:value-of select="concat($COORD2,',',$COORD3,'|',$COORD1,',',$COORD3,'|',$COORD1,',',$COORD4,'|',$COORD2,',',$COORD4,'|',$COORD2,',',$COORD3)"/>
	    </xsl:variable>
	    <tr>
	    <td>
	    <img id="mapStaticImage" src="{concat('http://maps.google.com/staticmap?path=rgba:0x0000FFff,weight:5|',$GMAPPATH,'&amp;size=250x200&amp;hl=en&amp;frame=true&amp;key=')}"/>
	    </td>
	    </tr>
	  </xsl:if>
	</xsl:if>
	
	</td></tr>
</xsl:template>

</xsl:stylesheet>