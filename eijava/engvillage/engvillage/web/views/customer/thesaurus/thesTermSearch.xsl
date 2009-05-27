<?xml version="1.0"?>
<xsl:stylesheet
     version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:java="http://www.jclark.com/xt/java/java.net.URLEncoder"
     xmlns:sutil="http://www.jclark.com/xt/java/org.ei.util.StringUtil">

<!--- all XSL include files -->

<xsl:include href="../Header.xsl" />
<xsl:include href="ThesaurusHeader.xsl" />
<xsl:include href="../Footer.xsl" />
<xsl:include href="../GlobalLinks.xsl"/>
<xsl:include href="thesPath.xsl"/>
<xsl:include href="ThesaurusHelpText.xsl"/>




<!-- end of include -->

    <xsl:variable name="TERM">
        <xsl:value-of select="//DOC/TERM" disable-output-escaping="yes"/>
    </xsl:variable>
    <xsl:variable name="PREV-PNUM">
        <xsl:value-of select="//DOC/PREV-PNUM"/>
    </xsl:variable>
    <xsl:variable name="NEXT-PNUM">
        <xsl:value-of select="//DOC/NEXT-PNUM"/>
    </xsl:variable>
    <xsl:variable name="ACTION">
        <xsl:value-of select="//DOC/ACTION"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
        <xsl:value-of select="//DOC/DATABASE"/>
    </xsl:variable>

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>

<xsl:variable name="RESULT">
    <xsl:value-of select="//DOC/RESULT"/>
</xsl:variable>

<xsl:template match="DOC">

<html>
<head>
<title>Ei Thesaurus -- Browse</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
<xsl:text disable-output-escaping="yes">
<![CDATA[

<SCRIPT LANGUAGE="JavaScript">
function validate()
{
    if ((document.search.for1.value.length == 0) || (document.search.for1.value = null))
    {
        alert ('Please enter some search term in the text box');
        search.for1.focus();
    }
}


function checkTerms()
{
    if(typeof(parent.clipFrame) != "undefined" &&
       typeof(parent.clipFrame.document) != "undefined" &&
       typeof(parent.clipFrame.document.forms[0]) != "undefined" &&
       typeof(document.checkform) != "undefined" &&
       typeof(document.checkform.selectUnselect) != "undefined")

    {

        var cliplength = parent.clipFrame.document.forms[0].clip.options.length;
        var optionsArray = new Array(cliplength);

        for (i=0; i < cliplength; i++)
        {
            optionsArray[parent.clipFrame.document.forms[0].clip.options[i].text] = 'YES';
        }


        var checkboxlength = document.checkform.selectUnselect.length;

        if(checkboxlength == undefined)
        {
            var mainStringaa = document.checkform.selectUnselect.value;
            var mainStringbb = replaceSubstring(mainStringaa,"(", "");
            var mainStringcc = replaceSubstring(mainStringbb,")", "");
            var mainString3 = mainStringcc;

            if(optionsArray[mainString3] == 'YES')
            {
                document.checkform.selectUnselect.checked = true;
            }

        }
        else
        {
            for (i=0;i<checkboxlength; i++)
            {
                var mainStringaa = document.checkform.selectUnselect[i].value;
                var mainStringbb = replaceSubstring(mainStringaa,"(", "");
                var mainStringcc = replaceSubstring(mainStringbb,")", "");
                var mainString3 = mainStringcc;

                if(optionsArray[mainString3] == 'YES')
                {
                    document.checkform.selectUnselect[i].checked = true;
                }
            }

        }

    }
}



function clearTerms()
{
    if((typeof(document.checkform) != "undefined") && typeof(document.checkform.selectUnselect) != "undefined")
    {
        var checkboxlength = document.checkform.selectUnselect.length;
        if(checkboxlength == undefined)
        {
            document.checkform.selectUnselect.checked = false;
        }
        else
        {
            for (i=0;i<checkboxlength; i++)
            {
                document.checkform.selectUnselect[i].checked = false;
            }
        }
    }
}
function replaceSubstring(inputString, fromString, toString)
{
    // Goes through the inputString and replaces every occurrence of fromString with toString
    var temp = inputString;
    if (fromString == "")
    {
        return inputString;
    }
    if (toString.indexOf(fromString) == -1)
    {
        // If the string being replaced is not a part of the replacement string (normal situation)
        while (temp.indexOf(fromString) != -1)
        {
            var toTheLeft = temp.substring(0, temp.indexOf(fromString));
            var toTheRight = temp.substring(temp.indexOf(fromString)+fromString.length, temp.length);
            temp = toTheLeft + toString + toTheRight;
        }
    }
    else
    {
        // String being replaced is part of replacement string (like "+" being replaced with "++") - prevent an infinite loop
        var midStrings = new Array("~", "`", "_", "^", "#");
        var midStringLen = 1;
        var midString = "";
        // Find a string that doesn't exist in the inputString to be used
        // as an "inbetween" string
        while (midString == "")
        {
            for (var i=0; i < midStrings.length; i++)
            {
                var tempMidString = "";
                for (var j=0; j < midStringLen; j++)
                {
                    tempMidString += midStrings[i];
                }
                if (fromString.indexOf(tempMidString) == -1)
                {
                    midString = tempMidString;
                    i = midStrings.length + 1;
                }
            }
        }
        // Keep on going until we build an "inbetween" string that doesn't exist
        // Now go through and do two replaces - first, replace the "fromString" with the "inbetween" string
        while (temp.indexOf(fromString) != -1)
        {
            var toTheLeft = temp.substring(0, temp.indexOf(fromString));
            var toTheRight = temp.substring(temp.indexOf(fromString)+fromString.length, temp.length);
            temp = toTheLeft + midString + toTheRight;
        }
        // Next, replace the "inbetween" string with the "toString"
        while (temp.indexOf(midString) != -1)
        {
            var toTheLeft = temp.substring(0, temp.indexOf(midString));
            var toTheRight = temp.substring(temp.indexOf(midString)+midString.length, temp.length);
            temp = toTheLeft + toString + toTheRight;
        }
    }
    // Ends the check to see if the string being replaced is part of the replacement string or not
    return temp;
    // Send the updated string back to the user
}

</SCRIPT>
]]>
</xsl:text>
</head>

<body topmargin="0" marginwidth="0" marginheight="0" onload="checkTerms()">

<!-- INCLUDE THE HEADER -->

    <xsl:apply-templates select="HEADER">
          <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
          <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
          <xsl:with-param name="SEARCH-TYPE">Thesaurus</xsl:with-param>
          <xsl:with-param name="TARGET">_parent</xsl:with-param>
     </xsl:apply-templates>

<!-- INCLUDE THE GLOBAL LINKS BAR -->

      <xsl:apply-templates select="GLOBAL-LINKS">
          <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
          <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
          <xsl:with-param name="LINK">Thesaurus</xsl:with-param>
          <xsl:with-param name="TARGET">_parent</xsl:with-param>
    </xsl:apply-templates>



<center>
<table border="0" cellspacing="0" cellpadding="0" width="99%">
<tr><td valign="top" width="140">
    <xsl:choose>
        <xsl:when test="($TERM != '')">
            <xsl:call-template name="THESAURUS-HELP-TEXT">
                <xsl:with-param name="ACTION">SEARCH</xsl:with-param>
                <xsl:with-param name="RESULT" select="$RESULT"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="DEFAULT-HELP-TEXT">
                <xsl:with-param name="PERSONALIZATION">true</xsl:with-param>
                <xsl:with-param name="TERMSEARCH">true</xsl:with-param>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>

</td>

<td valign="top" width="10"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td>
<!-- -->
<td valign="top">


<table border="0" width="100%" cellspacing="0" cellpadding="0">

<xsl:apply-templates select="//DOC/THESAURUS-HEADER"/>


    <xsl:choose>
        <xsl:when test="($TERM != '')">
            <tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td></tr>
            <tr><td valign="top" height="5" bgcolor="#C3C8D1"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
            <tr><td valign="top" bgcolor="#C3C8D1">
            <center>
            <table border="0" width="90%" cellspacing="0" cellpadding="0" bgcolor="#CEEBFF">
                <tr><td valign="top" height="1" colspan="3" bgcolor="#000000"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td></tr>
                <tr><td valign="top" width="1" bgcolor="#000000"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td>
                    <td valign="top">
                        <table border="0" cellspacing="0" cellpadding="0" width="550">
                            <form name="checkform" method="post" action="/controller/servlet/Controller">
                                <tr><td valign="top" height="5" colspan="4"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
                                <tr>
                                    <xsl:apply-templates select="TPATH"/>
                                </tr>
                                <input type="hidden" name="CID" value="thesTermSearch"/>
                                <input type="hidden" name="database" value="{$DATABASE}"/>
                                <xsl:apply-templates select="SEARCH|SUGGEST|NOSUGGEST"/>
                            </form>
                        </table>
                    </td>
                    <td valign="top" width="1" bgcolor="#000000"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td></tr>
                <tr><td valign="top" height="1" bgcolor="#000000" colspan="3"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td></tr>
            </table>
            </center>
            </td></tr>
            <tr><td valign="top" bgcolor="#C3C8D1" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

        </xsl:when>
        <xsl:otherwise>

            <xsl:call-template name="THESAURUS-REFINE-SEARCH-TIPS"/>

        </xsl:otherwise>
    </xsl:choose>

<tr><td valign="top" align="middle">
    <table border="0" cellspacing="0" cellpadding="0">


        <tr><td valign="top"><xsl:apply-templates select="//DOC/NPAGES/NPAGE"/></td></tr>
    </table>

</td></tr>
</table>
</td></tr>
</table>
</center>
</body>
</html>
</xsl:template>

<xsl:template match="TREC" mode="SEARCH">
    <xsl:apply-templates select ="ID/MT" mode="HIT" />
</xsl:template>

<xsl:template match="TREC" mode="SUGGEST">



    <xsl:variable name="MT"><xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/></xsl:variable>
    <xsl:variable name="MT1"><xsl:value-of select="ID/MT" disable-output-escaping="yes" /></xsl:variable>
    <tr>
        <td valign="top" width="25" height="1"><img src="/engresources/images/spacer.gif" border="0" width="25" height="1"/></td>
        <td valign="top">
            <xsl:choose>
                <xsl:when test="(position() = 1)">
                    <input type="radio" name="term" value="{$MT1}" checked="true"/>
                </xsl:when>
                <xsl:otherwise>
                    <input type="radio" name="term" value="{$MT1}"/>
                </xsl:otherwise>
            </xsl:choose>
        <a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}">
        <xsl:apply-templates select="ID/MT" mode="SUGG"/>
        </a></td>
    </tr>
</xsl:template>



<xsl:template match="SEARCH/PAGE">
    <tr><td valign="top" colspan="4">&#160;&#160;<a CLASS="MedBlackText"><xsl:value-of select="//DOC/COUNT"/> matching terms found</a></td></tr>
    <tr><td valign="top" height="2" colspan="4"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td></tr>
    <tr>
        <td valign="top" width="55" height="1"><img src="/engresources/images/spacer.gif" border="0" width="55" height="1"/></td><td valign="top" width="400"><a CLASS="SmBlackText"><u><b>Terms</b></u></a></td><td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td><td valign="top" align="left"><a CLASS="SmBlackText"><u><b>Select</b></u></a></td><td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td>
        <!-- Explode Commented as of now -->
        <td valign="top" align="left"><a CLASS="SmBlackText"><img src="/engresources/images/spacer.gif" border="0" height="1" width="50"/></a></td>

    </tr>

    <xsl:apply-templates select="TREC" mode="SEARCH"/>

    <tr><td valign="top" colspan="4" height="4"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
</xsl:template>

<xsl:template match="SUGGEST/PAGE">
    <tr><td valign="top" colspan="4">&#160;&#160;<a CLASS="RedText">Your search did not find a match with the spelling "<xsl:value-of select='//DOC/TERM'/>".  Did you mean?</a></td></tr>
    <tr><td valign="top" height="5" colspan="4"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

    <xsl:apply-templates select="TREC" mode="SUGGEST"/>

    <tr><td valign="top" height="4" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
    <tr><td valign="top" width="25" height="1"><img src="/engresources/images/spacer.gif" border="0" width="25" height="1"/></td><td valign="top">&#160;&#160;&#160;&#160;&#160;&#160;<input type="image" src="/engresources/images/newsearch1.gif" border="0"/></td></tr>
    <tr><td valign="top" colspan="4" height="4"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
</xsl:template>

<xsl:template match="NOSUGGEST">
    <tr><td valign="top" colspan="4">&#160;&#160;<a CLASS="RedText">Your search for "<xsl:value-of select='//DOC/TERM'/>" did not find any matching term.</a></td></tr>
    <tr><td valign="top" height="5" colspan="4"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
    <tr><td valign="top" height="4" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
</xsl:template>


<xsl:template match="//DOC/NPAGES/NPAGE[@CURR='PREV']">
    <xsl:variable name="TERM"><xsl:value-of select="java:encode(//DOC/TERM)"/></xsl:variable>
    <xsl:variable name="PREV-PNUM"><xsl:value-of select="//DOC/PREV-PNUM"/></xsl:variable>
    <xsl:variable name="NEXT-PNUM"><xsl:value-of select="//DOC/NEXT-PNUM"/></xsl:variable>
    <xsl:variable name="PNUM"><xsl:value-of select="java:encode(PNUM)"/></xsl:variable>

    <a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=thesTermSearch&amp;term={$TERM}&amp;pageNumber={$PNUM}&amp;database={$DATABASE}">Previous</a>&#160;&#160;&#160;<a CLASS="SmBlackText"> | &#160;</a>
</xsl:template>

<xsl:template match="//DOC/NPAGES/NPAGE[@CURR='CURR']">
<xsl:variable name="PAGES">
    <xsl:value-of select="//DOC/NPAGES/@PAGES"/>
</xsl:variable>
    <xsl:variable name="TERM"><xsl:value-of select="java:encode(//DOC/TERM)"/></xsl:variable>
    <xsl:variable name="PREV-PNUM"><xsl:value-of select="//DOC/PREV-PNUM"/></xsl:variable>
    <xsl:variable name="NEXT-PNUM"><xsl:value-of select="//DOC/NEXT-PNUM"/></xsl:variable>
    <xsl:variable name="PNUM"><xsl:value-of select="PNUM"/></xsl:variable>

    <a CLASS="MedBlackText"><b> [ <xsl:value-of select="PNUM"/> ] </b></a>&#160;<a CLASS="SmBlackText">

    <xsl:choose>
        <xsl:when test="($PAGES!=$PNUM)">
            |
        </xsl:when>
    </xsl:choose>
    &#160;</a>
</xsl:template>

<xsl:template match="//DOC/NPAGES/NPAGE[@CURR='NOTCURR']">
    <xsl:variable name="TERM"><xsl:value-of select="java:encode(//DOC/TERM)"/></xsl:variable>
    <xsl:variable name="PREV-PNUM"><xsl:value-of select="//DOC/PREV-PNUM"/></xsl:variable>
    <xsl:variable name="NEXT-PNUM"><xsl:value-of select="//DOC/NEXT-PNUM"/></xsl:variable>
    <xsl:variable name="PNUM"><xsl:value-of select="java:encode(PNUM)"/></xsl:variable>

    <a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=thesTermSearch&amp;term={$TERM}&amp;pageNumber={$PNUM}&amp;database={$DATABASE}"><xsl:value-of select="PNUM"/></a>&#160;<a CLASS="SmBlackText"> | &#160;</a>
</xsl:template>

<xsl:template match="//DOC/NPAGES/NPAGE[@CURR='NEXT']">
    <xsl:variable name="TERM"><xsl:value-of select="java:encode(//DOC/TERM)"/></xsl:variable>
    <xsl:variable name="PREV-PNUM"><xsl:value-of select="//DOC/PREV-PNUM"/></xsl:variable>
    <xsl:variable name="NEXT-PNUM"><xsl:value-of select="//DOC/NEXT-PNUM"/></xsl:variable>
    <xsl:variable name="PNUM"><xsl:value-of select="java:encode(PNUM)"/></xsl:variable>

    <a CLASS="SmBlackText">&#160;&#160;</a>&#160;&#160;<a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=thesTermSearch&amp;term={$TERM}&amp;pageNumber={$PNUM}&amp;database={$DATABASE}">Next</a>&#160;
</xsl:template>




<xsl:template match="CU" mode="HIT">
    <xsl:variable name="MT"><xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/></xsl:variable>
    <xsl:variable name="MTP"><xsl:value-of select="sutil:replaceSingleQuotes(.)" disable-output-escaping="yes"/></xsl:variable>

    <tr>
    <td valign="top" width="55" height="1"><img src="/engresources/images/spacer.gif" border="0" width="55" height="1"/></td><td valign="top"><a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}">
    <xsl:value-of select="." disable-output-escaping="yes"/></a></td><td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td><td valign="top" align="left"><input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/></td><td valign="top" width="8"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td>
    <td valign="top"></td>

    </tr>
</xsl:template>

<xsl:template match="LE" mode="HIT">
    <xsl:variable name="MT"><xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/></xsl:variable>

    <tr><td valign="top" width="55" height="1"><img src="/engresources/images/spacer.gif" border="0" width="55" height="1"/></td><td valign="top"><a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}"><i>
    <xsl:value-of select="." disable-output-escaping="yes"/></i></a></td><td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td>
    <td valign="top" align="left">
    </td>
    <td valign="top" width="8"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td>


    <td valign="top"></td>

    </tr>
</xsl:template>

<xsl:template match="PR" mode="HIT">
    <xsl:variable name="MT"><xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/></xsl:variable>
    <xsl:variable name="MTP"><xsl:value-of select="sutil:replaceSingleQuotes(.)" disable-output-escaping="yes"/></xsl:variable>

    <tr>
    <td valign="top" width="55" height="1"><img src="/engresources/images/spacer.gif" border="0" width="55" height="1"/></td><td valign="top"><a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}"><i>
    <xsl:value-of select="." disable-output-escaping="yes"/></i>
    <xsl:if test="(//DOC/DATA/HIT/TREC/STATUS!='N')">*</xsl:if>
    </a></td><td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td><td valign="top" align="left"><xsl:if test="(//DOC/DATA/HIT/TREC/STATUS!='N')"><input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/></xsl:if></td><td valign="top" width="8"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td>


    <td valign="top"></td>


    </tr>
</xsl:template>


<xsl:template match="CU" mode="SUGG">
    <xsl:variable name="MT"><xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/></xsl:variable>

    <a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}">
    <xsl:value-of select="." disable-output-escaping="yes"/></a>
</xsl:template>

<xsl:template match="LE" mode="SUGG">
    <xsl:variable name="MT"><xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/></xsl:variable>

    <a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}">
    <i><xsl:value-of select="." disable-output-escaping="yes"/></i>
    </a>
</xsl:template>

<xsl:template match="PR" mode="SUGG">
    <xsl:variable name="MT"><xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/></xsl:variable>

    <a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}">
    <i><xsl:value-of select="." disable-output-escaping="yes"/></i>
    <xsl:if test="(//DOC/DATA/HIT/TREC/STATUS!='N')">*</xsl:if>
    </a>
</xsl:template>

</xsl:stylesheet>





