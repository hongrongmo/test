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
<xsl:variable name="HAS-GRF">
    <xsl:value-of select="//DOC/HAS-GRF"/>
</xsl:variable>
<xsl:variable name="HAS-GEO">
    <xsl:value-of select="//DOC/HAS-GEO"/>
</xsl:variable>
<xsl:variable name="HAS-EPT">
    <xsl:value-of select="//DOC/HAS-EPT"/>
</xsl:variable> 

<xsl:template match="//DOC/THESAURUS-HEADER">

<tr><td valign="top" width="100%" bgcolor="#C3C8D1">

    <table border="0" cellspacing="0" cellpadding="0" width="625" bgcolor="#C3C8D1">
    <form name="search" method="post" action="/controller/servlet/Controller" onsubmit="return validate()">

        <input type="hidden" name="formSubmit" value="y"/>
            <tr><td valign="top" height="10" colspan="6"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
            <tr><td valign="top" width="4"><img src="/static/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="SmBlueTableText"><b>SELECT DATABASE</b></a></td><td valign="top" colspan="3" align="left"><a CLASS="SmBlueTableText"><b><label for="txtTrm">ENTER TERM</label></b></a></td></tr>
            <tr><td valign="top" width="4"><img src="/static/images/spacer.gif" border="0" width="4"/></td><td valign="top" width="15"><img src="/static/images/spacer.gif" border="0" width="15"/></td>

            <td valign="top" width="265">
            
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Robohelp.js"/>

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
	    <img src="/static/images/blue_help1.gif" border="0"/>
	</a>
        </td><td valign="top" width="15"><img src="/static/images/spacer.gif" border="0" width="15"/></td><td valign="top">


            <input id="txtTrm" type="text" name="term" size="29" value="{$TERM}"/></td></tr> 
            <tr><td valign="top" colspan="6" bgcolor="#C3C8D1" height="4"><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>
        <tr>
        <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td><td valign="top" width="15"><img src="/static/images/s.gif" border="0" width="15"/></td>
        <td>
        <xsl:choose>
            <xsl:when test="($HAS-GRF='true')">
                <xsl:choose>
                    <xsl:when test="($DATABASE='2097152')">
                        <a CLASS="MedBlackText">
                            <input id="rdGrf" type="radio" name="database" value="2097152" checked="true" onclick="flipClip('2097152')"/>
                        </a>
                        <a CLASS="MedBlackText"><label for="rdGrf">GeoRef</label></a>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
                    </xsl:when>
                    <xsl:otherwise>
                        <a CLASS="MedBlackText">
                            <input id="rdGrf" type="radio" name="database" value="2097152" onclick="flipClip('2097152')"/>
                        </a>
                        <a CLASS="MedBlackText"><label for="rdGrf">GeoRef</label></a>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>            
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="($HAS-GEO='true')">
                <xsl:choose>
                    <xsl:when test="($DATABASE='8192')">
                        <a CLASS="MedBlackText">
                            <input id="rdGeo" type="radio" name="database" value="8192" checked="true" onclick="flipClip('8192')"/>
                        </a>
                        <a CLASS="MedBlackText"><label for="rdGeo">GEOBASE</label></a>&#160;
                    </xsl:when>
                    <xsl:otherwise>
                        <a CLASS="MedBlackText">
                            <input id="rdGeo" type="radio" name="database" value="8192" onclick="flipClip('8192')"/>
                        </a>
                        <a CLASS="MedBlackText"><label for="rdGeo">GEOBASE</label></a>&#160;
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>            
        </xsl:choose>
         <xsl:choose>
	    <xsl:when test="($HAS-EPT='true')">               
		<xsl:choose>
		    <xsl:when test="($DATABASE='2048')">
			<a CLASS="MedBlackText">
			    <input id="rdEpt" type="radio" name="database" value="2048" checked="true"/>
			    <a CLASS="MedBlackText"><label for="rdEpt">EncompassPat</label></a>&#160;
			</a>
		    </xsl:when>
		    <xsl:otherwise>
			<a CLASS="MedBlackText">
			    <input id="rdEpt" type="radio" name="database" value="2048"/>
			    <a CLASS="MedBlackText"><label for="rdEpt">EncompassPat</label></a>&#160;
			</a>
		    </xsl:otherwise>
		</xsl:choose>
	    </xsl:when>
            </xsl:choose>
             <xsl:choose>
	    	    <xsl:when test="($HAS-ELT='true')">               
	    		<xsl:choose>
	    		    <xsl:when test="($DATABASE='1024')">
	    			<a CLASS="MedBlackText">
	    			    <input id="rdElt" type="radio" name="database" value="1024" checked="true"/>
	    			    <a CLASS="MedBlackText"><label for="rdElt">EnCompassLIT</label></a>&#160;
	    			</a>
	    		    </xsl:when>
	    		    <xsl:otherwise>
	    			<a CLASS="MedBlackText">
	    			    <input id="rdElt" type="radio" name="database" value="1024"/>
	    			    <a CLASS="MedBlackText"><label for="rdElt">EnCompassLIT</label></a>&#160;
	    			</a>
	    		    </xsl:otherwise>
	    		</xsl:choose>
	    	    </xsl:when>
            </xsl:choose>
        </td>
        </tr>
            <tr><td valign="top" width="4" colspan="3"><img src="/static/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

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
	    	    <img src="/static/images/blue_help1.gif" border="0"/>
	    </a>
            </td><td valign="top" align="left"><input type="image" src="/static/images/submit1.gif" border="0"/></td></tr>
    </form>
    </table>

</td></tr>
<!--
    <tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" height="2"/></td></tr>
-->
<tr><td valign="top" height="5" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

</xsl:template>


</xsl:stylesheet>