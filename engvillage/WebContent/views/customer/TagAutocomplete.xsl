<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/static/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLDecoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java xsl">

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

	<xsl:template match="PAGE">
		<html>
			<body>
				<xsl:value-of select="TAG"/>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>

