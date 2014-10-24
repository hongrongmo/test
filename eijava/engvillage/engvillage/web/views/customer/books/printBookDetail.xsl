<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY copy '<xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text>'>
]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40" xmlns:java="java:java.net.URLEncoder" exclude-result-prefixes="java html xsl">
    <xsl:variable name="QDIS">
        <xsl:value-of select="/PAGE/QTOP/QDIS"/>
    </xsl:variable>
    <xsl:variable name="QCO">
        <xsl:value-of select="/PAGE/QTOP/QCO"/>
    </xsl:variable>

    <xsl:variable name="ENCODED-QSTR">
        <xsl:value-of select="java:encode(/PAGE/QTOP/QSTR)"/>
    </xsl:variable>
    <xsl:variable name="INUM">
        <xsl:value-of select="/PAGE/BP/INUM"/>
    </xsl:variable>
    <xsl:variable name="NXT">
        <xsl:value-of select="$INUM + 1"/>
    </xsl:variable>
    <xsl:variable name="PRV">
        <xsl:value-of select="$INUM - 1"/>
    </xsl:variable>
    <xsl:variable name="SCY">Disable</xsl:variable>
    <xsl:variable name="QTY">Q</xsl:variable>
     <xsl:variable name="ISBN">
        <xsl:value-of select="/PAGE/BP/BN"/>
    </xsl:variable>

    <xsl:include href="bookTemplates.xsl"/>
    <xsl:template match="PAGE">
        <html>
            <head>
                <title>Print Records</title>
                <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
            </head>
            <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
                <TABLE cellSpacing="0" cellPadding="0" width="99%" border="0">
                    <TR>
                        <TD valign="top">
                            <img src="/engresources/images/ev2logo5.gif" border="0"/>
                        </TD>
                        <TD valign="center" align="right">
                            <A href="javascript:window.print()">
                                <IMG src="/engresources/images/printer.gif" border="0"/>
                            </A>
                            <A href="javascript:window.close();">
                                <IMG src="/engresources/images/close.gif" border="0"/>
                            </A>
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
                <center>
                    <table border="0" width="99%" cellspacing="0" cellpadding="0">
                        <xsl:apply-templates select="BP" mode="BOOK"/>
                        <!-- end  of abstract -->
                        <!-- Footer -->
                        <br/>
                    </table>
                </center>
                <br/>
                <center>
                    <TABLE cellSpacing="0" cellPadding="0" border="0" width="99%">
                        <TR>
                            <TD>
                                <CENTER>
                                    <A class="SmBlackText">&#169; 2010 Elsevier Inc. All rights reserved.</A>
                                </CENTER>
                            </TD>
                        </TR>
                    </TABLE>
                </center>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
