<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:DP="java:org.ei.domain.Displayer"
    exclude-result-prefixes="DP html xsl"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template match="COMBINED-REFINE-SEARCH">

<xsl:param name="SESSION-ID"/>
<xsl:param name="SELECTED-DB"/>
<xsl:param name="SEARCH-PHRASE-1"/>
<xsl:param name="SORT-OPTION"/>

    <xsl:variable name="USERMASK">
        <xsl:value-of select="//USERMASK"/>
    </xsl:variable>
  <SCRIPT LANGUAGE="Javascript" SRC="/static/js/CombinedSearchForm.js"/>
  <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Robohelp.js"/>

<xsl:variable name="CUSTOMER-ID"><xsl:value-of select="//CUSTOMER-ID"/></xsl:variable>

<center>
<!-- Start of table for combine search form -->
    <table border="0" cellspacing="0" cellpadding="0" width="99%" bgcolor="#C3C8D1">
        <tr>
            <td valign="top" height="20" colspan="7" bgcolor="FFFFFF">
                <img src="/static/images/s.gif" height="20"/>
            </td>
        </tr>
        <tr>
            <td valign="top" colspan="7" bgcolor="#FFFFFF">
                <a class="EvHeaderText">Combine Previous Searches</a>
            </td>
        </tr>
        <tr>
            <td valign="top" height="5" colspan="7" bgcolor="#FFFFFF">
                <img src="/static/images/s.gif" height="5"/>
            </td>
        </tr>

        <form name="expertsearch" action="/controller/servlet/Controller?CID=combineSearchHistory" METHOD="POST"  onSubmit="return searchValidation()" >
        <tr>
            <td valign="top" height="4" colspan="7">
                <img src="/static/images/s.gif" height="4"/>
            </td>
        </tr>
        <tr>
           <td valign="top" width="4" bgcolor="#C3C8D1">
                <img src="/static/images/s.gif" width="4"/>
            </td>
            <td valign="top" colspan="6" bgcolor="#C3C8D1">
                <xsl:value-of disable-output-escaping="yes" select="DP:toHTML($USERMASK,$SELECTED-DB,'quick')"/>
            </td>
        </tr>
        <tr>
            <td valign="top" colspan="7" height="4" bgcolor="#C3C8D1">
                <img src="/static/images/s.gif" height="4"/>
            </td>
        </tr>
        <tr>
            <td valign="top" width="4" bgcolor="#C3C8D1">
                <img src="/static/images/s.gif" width="4"/>
            </td>
            <td valign="top" colspan="2" bgcolor="#C3C8D1">
                <A CLASS="SmBlueTableText"><B>ENTER SEARCHES TO COMBINE</B></A>
            </td>
            <td valign="top" width="15">
                <img src="/static/images/s.gif" width="15"/>
            </td>
            <td valign="top" colspan="3" bgcolor="#C3C8D1">
                <A CLASS="SmBlueTableText"><B>SORT BY</B></A>
            </td>
        </tr>
        <tr>
            <td valign="top" width="4" bgcolor="#C3C8D1">
                <img src="/static/images/s.gif" width="4"/>
            </td>
            <td valign="top" width="15" bgcolor="#C3C8D1">
                <img src="/static/images/s.gif" width="15"/>
            </td>
            <td valign="top" bgcolor="#C3C8D1" width="300">
                <A CLASS="MedBlackText">
                    <textarea name="combine" ROWS="4" COLS="35" WRAP="PHYSICAL">
                        <xsl:value-of select="$SEARCH-PHRASE-1"/>
                    </textarea>
                </A>
                <a href="javascript:makeUrl('Search_History_Features.htm')">
	           <img src="/static/images/blue_help1.gif" border="0"/>
	        </a>  
            </td>
            <td valign="top" width="7">
                <img src="/static/images/s.gif" width="7"/>
            </td>
            <td valign="top" width="7">
                <img src="/static/images/s.gif" width="7"/>
            </td>

            <td valign="top" width="210">

                <!-- jam - "Turkey" added context sensitive help icon to relevance -->
                <input type="radio" name="sort" value="relevance">
                  <xsl:if test="($SORT-OPTION='relevance') or not(($SORT-OPTION='publicationYear') or ($SORT-OPTION='yr'))">
                    <xsl:attribute name="checked"/>
                  </xsl:if>
                </input>
                <a CLASS="SmBlackText">Relevance</a>&#160;
                <a href="javascript:makeUrl('Sorting_from_the_Search_Form.htm')">
			<img src="/static/images/blue_help1.gif" border="0"/>
	        </a>  
                <input type="radio" name="sort" value="yr">
                  <xsl:if test="($SORT-OPTION='publicationYear') or ($SORT-OPTION='yr')">
                    <xsl:attribute name="checked"/>
                  </xsl:if>
                </input>
                <a CLASS="SmBlackText">Publication year</a>

            </td>
            <td valign="top">

                 <A CLASS="MedBlackText" onClick="return searchValidation()">
                       <input type="image" src="/static/images/search_orange1.gif" border="0"/>
                 </A>&#160;
                    <a href="javascript:doReset()"><img src="/static/images/reset_orange1.gif" border="0"/></a>
            </td>
        </tr>
        <tr>
            <td valign="top" height="4" colspan="7">
                <img src="/static/images/s.gif" height="4"/>
            </td>
        </tr>
        </form>
    </table>
<!-- End search for combine search form -->
</center>



</xsl:template>

</xsl:stylesheet>
