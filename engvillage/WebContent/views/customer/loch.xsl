<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java xsl html"
>
<xsl:template match="/">
<html>

<head>
<title>LocHoldings</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    </meta>

    <xsl:text disable-output-escaping="yes">
    <![CDATA[
    <xsl:comment>
    <SCRIPT LANGUAGE="JavaScript">

    function Validate(){
        var eaddress = emailresults.emailaddress.value;
        if ((eaddress == null)||(eaddress == "")){
            alert("please enter email address");
            return false;
        } else if (eaddress.indexOf("@") < 0 ){
            alert("please enter valid email address");
            return false;
        } else if (eaddress.indexOf(".") < 0 ){
            alert("please enter valid email address");
            return false;
        } else {
            return true;
        }
    }

    </SCRIPT>
    </xsl:comment>
    ]]>
    </xsl:text>


</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

<center>

    <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <tr>
            <td valign="top"><img src="/static/images/ev2logo5.gif" border="0"></img> </td>
        </tr>

        <tr>
            <td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"></img></td>
        </tr>
        <tr>
            <td valign="top" height="2" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" height="2"></img></td>
        </tr>
        <tr>
            <td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"></img></td>
        </tr>
    </table>

    <table border="0" width="100%" >
    <form name = "emailresults" method="POST" onSubmit = "return Validate()" action="?CID=lochsent">
    <tr>
        <td width="10%" height="113"></td>


        <td width="80%" height="113">

        <table border="1" width="100%" id="table2" cellspacing="0" cellpadding="0">

            <xsl:variable name="ARTICLETITLE">
                <xsl:value-of select="//ARTICLETITLE"/>
            </xsl:variable>

            <xsl:if test="string($ARTICLETITLE)">
            <tr>
                <td>ARTICLE TITLE:  </td>
                <td>
                <xsl:value-of select="$ARTICLETITLE" disable-output-escaping="yes"/>
                <input type="hidden" name = "ARTICLETITLE">
                <xsl:attribute name="value">
                         <xsl:value-of select="$ARTICLETITLE"/>
                </xsl:attribute>
                </input>
                </td>

            </tr>
            </xsl:if>

            <xsl:variable name="AUTHOR">
                <xsl:value-of select = "//AUTHOR" />
            </xsl:variable>

            <xsl:if test="string($AUTHOR)">
            <tr>
                <td>AUTHOR:  </td>
                <td>
                <xsl:value-of select="$AUTHOR" disable-output-escaping="yes"/>
                </td>
                <input type="hidden" name = "AUTHOR">
                <xsl:attribute name="value">
                         <xsl:value-of select="$AUTHOR"/>
                </xsl:attribute>
                </input>

            </tr>
            </xsl:if>

            <xsl:variable name="SERIALTITLE">
                <xsl:value-of select="//SERIALTITLE"/>
            </xsl:variable>

            <xsl:if test="string($SERIALTITLE)">
            <tr>
                <td>SOURCE TITLE:  </td>
                <td>
                <xsl:value-of select="$SERIALTITLE" disable-output-escaping="yes"/>
                </td>

                <input type="hidden" name = "SERIALTITLE">
                <xsl:attribute name="value">
                         <xsl:value-of select="$SERIALTITLE"/>
                </xsl:attribute>
                </input>
            </tr>
            </xsl:if>
            
           <xsl:variable name="CONFTITLE">
                <xsl:value-of select="//CONFTITLE"/>
            </xsl:variable>

            <xsl:if test="string($CONFTITLE)">
            <tr>
                <td>CONFERENCE NAME:  </td>
                <td>
                <xsl:value-of select="$CONFTITLE" disable-output-escaping="yes"/>
                </td>

                <input type="hidden" name = "CONFTITLE">
                <xsl:attribute name="value">
                         <xsl:value-of select="$CONFTITLE"/>
                </xsl:attribute>
                </input>
            </tr>
            </xsl:if>

            <xsl:variable name="SOURCE">
                <xsl:value-of select="//SOURCE"/>
            </xsl:variable>

            <xsl:if test="string($SOURCE)">
            <tr>
                <td>SOURCE:  </td>
                <td>
                <xsl:value-of select="$SOURCE" disable-output-escaping="yes"/>
                </td>

                <input type="hidden" name = "SOURCE">
                <xsl:attribute name="value">
                         <xsl:value-of select="$SOURCE"/>
                </xsl:attribute>
                </input>
            </tr>
            </xsl:if>


            <xsl:variable name="ISSN">
                <xsl:value-of select="//ISSN"/>
            </xsl:variable>

            <xsl:if test="string($ISSN)">
            <tr>
                <td>ISSN:  </td>
                <td>
                <xsl:value-of select="$ISSN" disable-output-escaping="yes"/>
                </td>

                <input type="hidden" name = "ISSN">
                <xsl:attribute name="value">
                         <xsl:value-of select="$ISSN"/>
                </xsl:attribute>
                </input>
            </tr>
            </xsl:if>

            <xsl:variable name="ISBN">
                <xsl:value-of select="//ISBN"/>
            </xsl:variable>

            <xsl:if test="string($ISBN)">
            <tr>
                <td>ISBN:  </td>
                <td>
                <xsl:value-of select="$ISBN" disable-output-escaping="yes"/>
                </td>

                <input type="hidden" name = "ISBN">
                <xsl:attribute name="value">
                         <xsl:value-of select="$ISBN"/>
                </xsl:attribute>
                </input>
            </tr>
            </xsl:if>


            <xsl:variable name="VOLUME">
                <xsl:value-of select="//VOLUME"/>
            </xsl:variable>

            <xsl:if test="string($VOLUME)">
            <tr>
                <td>VOLUME:  </td>
                <td>
                <xsl:value-of select="$VOLUME" disable-output-escaping="yes"/>
                </td>

                <input type="hidden" name = "VOLUME">
                <xsl:attribute name="value">
                         <xsl:value-of select="$VOLUME"/>
                </xsl:attribute>
                </input>
            </tr>
            </xsl:if>

            <xsl:variable name="ISSUE">
                <xsl:value-of select="//ISSUE"/>
            </xsl:variable>

            <xsl:if test="string($ISSUE)">
            <tr>
                <td>ISSUE:  </td>
                <td>
                <xsl:value-of select="$ISSUE" disable-output-escaping="yes"/>
                </td>
                <input type="hidden" name = "ISSUE">
                <xsl:attribute name="value">
                         <xsl:value-of select="$ISSUE"/>
                </xsl:attribute>
                </input>
            </tr>
            </xsl:if>


            <xsl:variable name="STARTPAGE">
                <xsl:value-of select="//STARTPAGE"/>
            </xsl:variable>

            <xsl:if test="string($STARTPAGE)">
            <tr>
                <td>START PAGE:  </td>
                <td>
                <xsl:value-of select="$STARTPAGE" disable-output-escaping="yes"/>
                </td>

                <input type="hidden" name = "STARTPAGE">
                <xsl:attribute name="value">
                         <xsl:value-of select="$STARTPAGE"/>
                </xsl:attribute>
                </input>
            </tr>
            </xsl:if>


            <xsl:variable name="YEAR">
                <xsl:value-of select="//YEAR"/>
            </xsl:variable>

            <xsl:if test="string($YEAR)">
            <tr>
                <td>YEAR:  </td>
                <td>
                <xsl:value-of select="$YEAR" disable-output-escaping="yes"/>
                </td>

                <input type="hidden" name = "YEAR">
                <xsl:attribute name="value">
                         <xsl:value-of select="$YEAR"/>
                </xsl:attribute>
                </input>
            </tr>
            </xsl:if>

        </table>


        </td>


        <td width="10%" height="113"></td>
    </tr>
    <tr>
    <td width="10%"></td>
    <td width="80%">
    Please fill in the form below to send a request for the above document to library@lamrc.com
    </td>
    <td width="10%"></td>
    </tr>
    <tr>
        <td width="10%"></td>
        <td width="80%" align="center">

    <table border="0" width="100%" id="table3">

    <tr>
        <td>Return email
        </td>
        <td>
            <input type="text" name="emailaddress" size="26"/>
        </td>
    </tr>
    <tr>
        <td>
            Comments
        </td>
        <td>
            <textarea rows="6" name="comments" cols="64"></textarea>
        </td>
    </tr>
    <tr>
    <td>
    </td>
        <td align="center">
            <input type="submit"  value="Submit" name="B1"/>
            <input type="reset" value="Reset" name="B2"/>
        </td>
    </tr>
    </table>
    </td>
        <td></td>
    </tr>
    <tr>
        <td width="10%"></td>
        <td width="80%"></td>
        <td></td>
    </tr>
    </form>
</table>
</center>
</body>

</html>

</xsl:template>

</xsl:stylesheet>