<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java html xsl">

<xsl:variable name="HAS-INSPEC">
    <xsl:value-of select="//DOC/HAS-INSPEC"/>
</xsl:variable>
<xsl:variable name="HAS-CPX">
    <xsl:value-of select="//DOC/HAS-CPX"/>
</xsl:variable>


<xsl:template match="//DOC/THESAURUS-HEADER">

<tr><td valign="top" width="100%" bgcolor="#C3C8D1">

    <table border="0" cellspacing="0" cellpadding="0" width="625" bgcolor="#C3C8D1">
    <form name="search" method="post" action="/controller/servlet/Controller" onsubmit="return validate()">

        <input type="hidden" name="formSubmit" value="y"/>
            <tr><td valign="top" height="10" colspan="6"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
            <tr><td valign="top" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="SmBlueTableText"><b>SELECT DATABASE</b></a></td><td valign="top" colspan="3" align="left"><a CLASS="SmBlueTableText"><b><label for="txtTrm">ENTER TERM</label></b></a></td></tr>
            <tr><td valign="top" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" width="15"><img src="/engresources/images/spacer.gif" border="0" width="15"/></td>

            <td valign="top" width="265">
            
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>

    <xsl:text disable-output-escaping="yes">

    <![CDATA[
        <SCRIPT LANGUAGE="JavaScript">

            function flipClip(database)
            {
                var database = database;
                var url = "/controller/servlet/Controller?EISESSION=$SESSIONID&CID=thesHome&database="+database;

                var selectList = parent.clipFrame.document.forms["clipboard"].clip;
                var agree = true;

                if(selectList[0].text != "")
                {
                    agree=confirm("Your thesaurus terms will be lost. Are you sure you want to change databases?");
                }

                if(agree)
                {
                        parent.document.location = url;
                }
                else
                {
                    if(document.search.database[0].checked)
                    {
                        document.search.database[1].checked = true;
                    }
                    else
                    {
                        document.search.database[0].checked = true;
                    }
                }

            }

            function validate()
            {
                if(document.search.term.value=="")
                {
                    window.alert("Please enter a term");
                    return false;
                }
            }

        </SCRIPT>
        ]]>
    </xsl:text>



        <xsl:choose>
            <xsl:when test="($HAS-CPX='true')">
                <xsl:choose>
                    <xsl:when test="($DATABASE='1')">
                        <a CLASS="MedBlackText">
                            <input id="rdCpx" type="radio" name="database" value="1" checked="true" onclick="flipClip('1')"/>
                        </a>
                    </xsl:when>
                    <xsl:when test="($DATABASE='1') and ($HAS-INSPEC='true')">
                        <a CLASS="MedBlackText">
                            <input id="rdCpx" type="radio" name="database" value="1" onclick="flipClip('1')" />
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <a CLASS="MedBlackText">
                            <input id="rdCpx" type="radio" name="database" value="1" checked="true" onclick="flipClip('1')" />
                        </a>
                    </xsl:otherwise>
                </xsl:choose>
                <a CLASS="MedBlackText"><label for="rdCpx">Compendex</label></a> &#160;&#160;
            </xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="($HAS-INSPEC='true')">
                <xsl:choose>
                    <xsl:when test="($DATABASE='2')">
                        <a CLASS="MedBlackText">
                            <input id="rdIns" type="radio" name="database" value="2" checked="true" onclick="flipClip('2')"/>
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <a CLASS="MedBlackText">
                            <input id="rdIns" type="radio" name="database" value="2" onclick="flipClip('2')"/>
                        </a>
                    </xsl:otherwise>
                </xsl:choose>
                <a CLASS="MedBlackText"><label for="rdIns">Inspec</label></a>&#160;
            </xsl:when>
        </xsl:choose>
        <a href="javascript:makeUrl('Select_Thesarus_.htm')">
	    <img src="/engresources/images/blue_help1.gif" border="0"/>
	</a>
        </td><td valign="top" width="15"><img src="/engresources/images/spacer.gif" border="0" width="15"/></td><td valign="top">


            <input id="txtTrm" type="text" name="term" size="29" value="{$TERM}"/></td></tr>

            <tr><td valign="top" colspan="6" bgcolor="#C3C8D1" height="4"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
            <tr><td valign="top" width="4" colspan="3"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

            <xsl:choose>
                <xsl:when test="($ACTION='thesTermSearch')">
                    <input id="rdSch" type="radio" name="CID" value="thesTermSearch" checked="true"/>
                </xsl:when>
                <xsl:otherwise>
                    <input id="rdSch" type="radio" name="CID" value="thesTermSearch"/>
                </xsl:otherwise>
            </xsl:choose>
            <a CLASS="MedBlackText"><label for="rdSch">Search</label></a> &#160;


            <xsl:choose>
                <xsl:when test="($ACTION='thesFullRec')">
                    <input id="rdExt" type="radio" name="CID" value="thesFullRec" checked="true"/>
                </xsl:when>

                <xsl:otherwise>
                    <input id="rdExt" type="radio" name="CID" value="thesFullRec"/>
                </xsl:otherwise>
            </xsl:choose>
            <a CLASS="MedBlackText"><label for="rdExt">Exact Term</label></a> &#160;

            <xsl:choose>
                <xsl:when test="($ACTION='thesBrowse')">
                    <input id="rdBrw" type="radio" name="CID" value="thesBrowse" checked="true"/>
                </xsl:when>

                <xsl:otherwise>
                    <input id="rdBrw" type="radio" name="CID" value="thesBrowse"/>
                </xsl:otherwise>
            </xsl:choose>
            <a CLASS="MedBlackText"><label for="rdBrw">Browse</label></a> &#160;
            <a href="javascript:makeUrl('Overview.htm')">
	    	    <img src="/engresources/images/blue_help1.gif" border="0"/>
	    </a>
            </td><td valign="top" align="left"><input type="image" src="/engresources/images/submit1.gif" border="0"/></td></tr>
    </form>
    </table>

</td></tr>
<!--
    <tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td></tr>
-->
<tr><td valign="top" height="5" bgcolor="#C3C8D1"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

</xsl:template>


</xsl:stylesheet>