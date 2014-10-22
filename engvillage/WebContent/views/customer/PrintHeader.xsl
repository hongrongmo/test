<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="xsl html"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="./HeaderNull.xsl"/>

<xsl:template match="HEADER">

    <xsl:text disable-output-escaping="yes">
    <![CDATA[<html>]]>
    </xsl:text>

    <head>
        <title>Print Records</title>
		<script type="text/javascript" src="/static/js/jquery/jquery-1.7.2.min.js"></script>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/encompassFields.js"/>
		<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_txt_v01.css"/>
		<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_common_sciverse_v01.css"/>
		<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_abstract_v01.css"/>
    </head>

    <xsl:text disable-output-escaping="yes">
    <![CDATA[<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">]]>
    </xsl:text>

<!-- HEADER -->
	<xsl:call-template name="HEADERNULL"><xsl:with-param name="PRINT"><xsl:value-of select="boolean(true)"/></xsl:with-param></xsl:call-template>

    <xsl:text disable-output-escaping="yes">
    <![CDATA[<center><table border="0" width="99%" cellspacing="0" cellpadding="0">]]>
    </xsl:text>

</xsl:template>

</xsl:stylesheet>