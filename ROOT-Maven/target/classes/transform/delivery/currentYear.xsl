<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet 
	version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:datetime="http://exslt.org/dates-and-times"
	xmlns:html="http://www.w3.org/TR/REC-html40"
  	exclude-result-prefixes="html xsl">
  	 

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<!-- Bhanu 02/16/2012 - added name="YEAR" so we may
  explicitly use xsl:call-template instead of xsl:apply-tempates
  in order to inlcude the year in output  .
      
  -->
<xsl:template match="YEAR" name="YEAR">
  <!-- This xsl displays System Year. This file is included, whenever is calendar is to be displayed.
  -->
  <xsl:variable name="systemdate" select="datetime:dateTime()" />
  <xsl:value-of select="substring($systemdate,1,4)" />
  
  <!-- End of year -->

</xsl:template>

</xsl:stylesheet>