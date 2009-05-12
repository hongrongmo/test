<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java html xsl">

  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  <xsl:strip-space elements="html:* xsl:*" />

  <xsl:include href="../Header.xsl" />
  <xsl:include href="../Footer.xsl" />
  <xsl:include href="../GlobalLinks.xsl"/>
  <xsl:include href="ThesaurusHelpText.xsl"/>

  <xsl:variable name="SESSION-ID">
    <xsl:value-of select="//DOC/SESSION-ID"/>
  </xsl:variable>
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
  <xsl:variable name="PERSONALIZATION">
    <xsl:value-of select="//PERSONALIZATION"/>
  </xsl:variable>
  <xsl:variable name="TERM">
    <xsl:value-of select="//DOC/TERM"/>
  </xsl:variable>
  <xsl:variable name="DATABASE">
    <xsl:value-of select="//DOC/DATABASE"/>
  </xsl:variable>


<xsl:template match="DOC">

  <html>
  <head>
    <title>Engineering Village - Thesaurus</title>
    <SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
    <SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>
      
      <script type="text/javascript" language="javascript">
        <xsl:comment>
          function validate()
          {
              if(document.search.term.value=="")
              {
                  window.alert("Please enter a term");
                  return false;
              }
          }
          // </xsl:comment>
       </script>
  </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
    <center>

    <!-- INCLUDE THE HEADER -->
    <xsl:apply-templates select="HEADER">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      <xsl:with-param name="SEARCH-TYPE">Thesaurus</xsl:with-param>
    </xsl:apply-templates>

    <!-- INCLUDE THE GLOBAL LINKS BAR -->
    <xsl:apply-templates select="GLOBAL-LINKS">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      <xsl:with-param name="LINK">Thesaurus</xsl:with-param>
    </xsl:apply-templates>


<table border="0" cellspacing="0" cellpadding="0" width="99%">
<tr>
  <td valign="top" width="140">
      <table border="0" width="140" cellspacing="0" cellpadding="0">
          <tr><td valign="top">

              <xsl:call-template name="DEFAULT-HELP-TEXT">
                  <xsl:with-param name="PERSONALIZATION" select="$PERSONALIZATION"/>
                  <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
              </xsl:call-template>

          </td></tr>
      </table>
  </td>
  <td valign="top" width="10"><img src="/engresources/images/s.gif" border="0" width="10"/></td>
  <td valign="top" width="100%">
    <table border="0" align="right" width="100%" cellspacing="0" cellpadding="0">
    <tr>
      <td valign="top" bgcolor="#C3C8D1">
        <table border="0" cellspacing="0" cellpadding="0" width="625" bgcolor="#C3C8D1">
          <form name="search" method="post" action="/controller/servlet/Controller" onsubmit="return validate()">
            <input type="hidden" name="CID" value="thesFrameSet"/>
            <input type="hidden" name="formSubmit" value="y"/>
            <tr><td valign="top" height="15" colspan="6"><img src="/engresources/images/s.gif" border="0" height="15"/></td></tr>
            <tr><td valign="top" width="4"><img src="/engresources/images/s.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="SmBlueTableText"><b>SELECT DATABASE</b></a></td><td valign="top" colspan="3" align="left"><a CLASS="SmBlueTableText"><b><label for="txtTrm">ENTER TERM</label></b></a></td></tr>
            <tr><td valign="top" width="4"><img src="/engresources/images/s.gif" border="0" width="4"/></td><td valign="top" width="15"><img src="/engresources/images/s.gif" border="0" width="15"/></td>
            <td valign="top" width="265">
            <xsl:choose>
                <xsl:when test="($HAS-CPX='true')">
                    <xsl:choose>
                        <xsl:when test="($DATABASE='1')">
                            <a CLASS="MedBlackText">
                                <input id="rdCpx" type="radio" name="database" value="1" checked="true"/>
                            </a>
                        </xsl:when>
                        <xsl:when test="($DATABASE='2') and ($HAS-INSPEC='true')">
                            <a CLASS="MedBlackText">
                                <input id="rdCpx" type="radio" name="database" value="2"/>
                            </a>
                        </xsl:when>
                        <xsl:when test="($DATABASE='2097152') and ($HAS-GRF='true')">
                            <a CLASS="MedBlackText">
                                <input id="rdGrf" type="radio" name="database" value="2097152"/>
                            </a>
                        </xsl:when>
                        <xsl:when test="($DATABASE='8192') and ($HAS-GEO='true')">
                            <a CLASS="MedBlackText">
                                <input id="rdGeo" type="radio" name="database" value="8192"/>
                            </a>
                        </xsl:when>                         
                        <xsl:otherwise>
                            <a CLASS="MedBlackText">
                                <input id="rdCpx" type="radio" name="database" value="1" checked="true"/>
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
                                <input id="rdIns" type="radio" name="database" value="2" checked="true"/>
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <a CLASS="MedBlackText">
                                <input id="rdIns" type="radio" name="database" value="2"/>
                            </a>
                        </xsl:otherwise>
                    </xsl:choose>
                    <a CLASS="MedBlackText"><label for="rdIns">Inspec</label></a>&#160; 
                </xsl:when>
            </xsl:choose>            
                <a href="javascript:makeUrl('Select_Thesarus_.htm')">
	            <img src="/engresources/images/blue_help1.gif" border="0"/>
		</a>               
                </td>                
                <td valign="top" width="15"><img src="/engresources/images/s.gif" border="0" width="15"/></td><td valign="top"><input id="txtTrm" type="text" name="term" size="29" value="{$TERM}"/></td></tr>            
            <tr><td valign="top" colspan="6" bgcolor="#C3C8D1" height="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
            <tr>
            <td valign="top" width="4"><img src="/engresources/images/s.gif" border="0" width="4"/></td><td valign="top" width="15"><img src="/engresources/images/s.gif" border="0" width="15"/></td>
            <td>
            <xsl:choose>
                <xsl:when test="($HAS-GRF='true')">               
                    <xsl:choose>
                        <xsl:when test="($DATABASE='2097152')">
                            <a CLASS="MedBlackText">
                                <input id="rdGrf" type="radio" name="database" value="2097152" checked="true"/>
                                <a CLASS="MedBlackText"><label for="rdGrf">GeoRef</label></a>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <a CLASS="MedBlackText">
                                <input id="rdGrf" type="radio" name="database" value="2097152"/>
                                <a CLASS="MedBlackText"><label for="rdGrf">GeoRef</label></a>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
                            </a>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="($HAS-GEO='true')">               
                    <xsl:choose>
                        <xsl:when test="($DATABASE='8192')">
                            <a CLASS="MedBlackText">
                                <input id="rdGeo" type="radio" name="database" value="8192" checked="true"/>
                                <a CLASS="MedBlackText"><label for="rdGeo">GEOBASE</label></a>&#160;
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <a CLASS="MedBlackText">
                                <input id="rdGeo" type="radio" name="database" value="8192"/>
                                <a CLASS="MedBlackText"><label for="rdGeo">GEOBASE</label></a>&#160;
                            </a>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
            </xsl:choose>
            </td>
            </tr>
            <tr>
                <td valign="top" width="4" colspan="3"><img src="/engresources/images/s.gif" border="0" width="4"/></td>
                <td valign="top" colspan="2"><input id="rdSch" type="radio" name="thesAction" value="thesTermSearch" checked="true"/>
                <a CLASS="MedBlackText"><label for="rdSch">Search</label></a> &#160;<input id="rdExt" type="radio" name="thesAction" value="thesFullRec"/>
                <a CLASS="MedBlackText"><label for="rdExt">Exact Term</label></a> &#160;<input id="rdBrw" type="radio" name="thesAction" value="thesBrowse"/>
                <a CLASS="MedBlackText"><label for="rdBrw">Browse</label></a>&#160;
		 <a href="javascript:makeUrl('Overview.htm')">
		<img src="/engresources/images/blue_help1.gif" border="0"/>
		</a>   
               </td><td valign="top" align="left"><input type="image" src="/engresources/images/submit1.gif" border="0"/></td>
            </tr>
            <tr><td valign="top" height="5"><img src="/engresources/images/s.gif" border="0" height="5"/></td></tr>
    </form>
    </table>
</td>
</tr>
<xsl:call-template name="THESAURUS-SEARCH-TIPS"/>
</table>
<!-- End of table for search form -->
</td>
</tr>
</table>


  <xsl:call-template name="FOOTER">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:call-template>

</center>
</body>
</html>

</xsl:template>

</xsl:stylesheet>