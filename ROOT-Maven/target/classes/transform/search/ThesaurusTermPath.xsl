<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:actionbean="java:org.ei.stripes.action.search.ThesaurusAjaxAction"
    xmlns:termpath="java:org.ei.stripes.view.thesaurus.TermPath"
    exclude-result-prefixes="html xsl"
>
	<!-- ********************************************* -->
	<!-- Term path template                            -->
	<!-- ********************************************* -->
	<xsl:template match="TPATH/STEP | DATA/TPATH/STEP">
		<xsl:variable name="termpath" select="termpath:new()"/>
		<xsl:value-of select="termpath:setSti($termpath, STI)"></xsl:value-of>
		<xsl:value-of select="termpath:setSlink($termpath, SLINK)"></xsl:value-of>	
		<xsl:value-of select="termpath:setScon($termpath, SCON)"></xsl:value-of>
		<xsl:value-of select="termpath:setSnum($termpath, SNUM)"></xsl:value-of>

		<xsl:value-of select="actionbean:addTermpath($actionbean, $termpath)"/>
	</xsl:template>
	
</xsl:stylesheet>