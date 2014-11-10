<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="xsl html"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template match="HEADER">

    <xsl:text disable-output-escaping="yes">
    <![CDATA[<html>]]>
    </xsl:text>

    <head>
        <title>Print Records</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/encompassFields.js"/>
    </head>

    <xsl:text disable-output-escaping="yes">
    <![CDATA[<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">]]>
    </xsl:text>


    <TABLE cellSpacing="0" cellPadding="0" width="99%" border="0">
      <TR>
        <TD valign="top">
            <img src="/engresources/images/ev2logo5.gif" border="0"/>
        </TD>
        <TD valign="center" align="right">
            <A href="javascript:window.print()"><IMG src="/engresources/images/printer.gif" border="0"/></A>
            <A href="javascript:window.close();"><IMG src="/engresources/images/close.gif" border="0"/></A>
        </TD>
      </TR>
      <TR>
        <TD vAlign="top" colSpan="2" height="5">
            <IMG height="5" src="/engresources/images/spacer.gif" border="0"/>
        </TD>
      </TR>
      <TR>
        <TD vAlign="top" bgColor="#3173b5" colSpan="2" height="2">
            <IMG height="2" src="/engresources/images/spacer.gif" border="0"/>
        </TD>
      </TR>
      <TR>
        <TD vAlign="top" colSpan="2" height="10">
            <IMG height="10" src="/engresources/images/spacer.gif" border="0"/>
        </TD>
      </TR>
    </TABLE>

    <xsl:text disable-output-escaping="yes">
    <![CDATA[<center><table border="0" width="99%" cellspacing="0" cellpadding="0">]]>
    </xsl:text>

</xsl:template>

</xsl:stylesheet>