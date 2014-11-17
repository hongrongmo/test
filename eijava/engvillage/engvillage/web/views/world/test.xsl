<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template match="doc">
  <xsl:value-of select="."/>
    <PRE>

	<a href="/controller/servlet/Controller?CID=home">Test Session</a>

	<FORM method="post" action="/controller/servlet/Controller">
		<input type="hidden" name="CID" value="home"/>
		<input type="submit" value="Test"/>
	</FORM>




   </PRE>
  </xsl:template>
</xsl:stylesheet>
