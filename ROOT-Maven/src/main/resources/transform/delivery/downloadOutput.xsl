<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- This xsl displays the downloaded records -->
<xsl:output method = "text" omit-xml-declaration = "yes" />

	<xsl:template match="root">
		<xsl:value-of select="." disable-output-escaping="yes"/>
	</xsl:template>
</xsl:stylesheet>