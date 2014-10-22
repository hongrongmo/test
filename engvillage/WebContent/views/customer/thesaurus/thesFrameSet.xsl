<?xml version="1.0"?>
<xsl:stylesheet
  	 version="1.0"
  	 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  	 xmlns:java="java:java.net.URLEncoder"
  	 exclude-result-prefixes="java xsl"
>


<xsl:template match="DOC">

<xsl:variable name="LINK1">
	<xsl:value-of select="LINK1"/>
</xsl:variable>

<xsl:variable name="LINK2">
	<xsl:value-of select="LINK2"/>
</xsl:variable>

<HTML>
<HEAD><TITLE>Ei Thesaurus</TITLE></HEAD>

<FRAMESET rows="*,173">
<FRAME SRC="/controller/servlet/Controller?EISESSION=$SESSIONID&amp;{$LINK1}&amp;formSubmit=y" name="mainFrame"/>
<FRAME SRC="/controller/servlet/Controller?EISESSION=$SESSIONID&amp;{$LINK2}&amp;formSubmit=y" name="clipFrame"/>
</FRAMESET>

</HTML>


</xsl:template>
</xsl:stylesheet>