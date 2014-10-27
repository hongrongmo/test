<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java xsl"
>

  <xsl:output method="html" indent="no"/>
  <xsl:strip-space elements="html:* xsl:*" />

  <xsl:include href="thesaurus/thesClipBoard.xsl"/>

  <xsl:include href="common/forms/QuickSearchForm.xsl"/>
  <xsl:include href="common/forms/ExpertSearchForm.xsl"/>
  <xsl:include href="common/forms/EasySearchForm.xsl"/>
  <xsl:include href="common/forms/EBookSearchForm.xsl"/>

    <!-- This xsl displays Quick Search Refine form depending on database.
        It works in combination with QuickSearchResults.jsp and Expert
    -->
  <xsl:template match="SEARCH">
    <xsl:param name="DATABASE"/>
    <xsl:param name="SEARCH-TYPE"/>
    <xsl:param name="YEAR-STRING"/>

    <xsl:variable name="SESSION-ID"><xsl:value-of select="//SESSION-ID" /></xsl:variable>

    <xsl:choose>
      <xsl:when test="($SEARCH-TYPE='Easy')">
        <xsl:apply-templates select="//EASY-SEARCH">
          <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
          <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
          <xsl:with-param name="NEWS">false</xsl:with-param>
          <xsl:with-param name="TIPS">false</xsl:with-param>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:when test="($SEARCH-TYPE='Thesaurus')">
        <xsl:call-template name="THESAURUS-REFINE-SEARCH-FORM">
          <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="($SEARCH-TYPE='Quick')">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td valign="top" height="5">
              <img src="/engresources/images/s.gif" border="0" height="5"/>
            </td>
          </tr>
          <tr>
            <td valign="top" bgcolor="#FFFFFF" width="1"><img src="/engresources/images/s.gif" width="100"/></td>
            <td valign="top">
              <a class="EvHeaderText">Refine Search</a>
              <xsl:apply-templates select="//COMBINED-QUICK-SEARCH">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
                <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
                <xsl:with-param name="YEAR-STRING" select="$YEAR-STRING"/>
                <xsl:with-param name="NEWS">false</xsl:with-param>
                <xsl:with-param name="TIPS">false</xsl:with-param>
              </xsl:apply-templates>
            </td>
            <td valign="top" bgcolor="#FFFFFF" width="1"><img src="/engresources/images/s.gif" width="180"/></td>
          </tr>
        </table>
      </xsl:when>
      <xsl:when test="($SEARCH-TYPE='Book')">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td valign="top" height="5">
              <img src="/engresources/images/s.gif" border="0" height="5"/>
            </td>
          </tr>
          <tr>
            <td valign="top" bgcolor="#FFFFFF" width="1"><img src="/engresources/images/s.gif" width="100"/></td>
            <td valign="top">
              <img src="/engresources/images/s.gif" width="10"/>
              <a class="EvHeaderText">Refine Search</a>
              <xsl:apply-templates select="//EBOOK-SEARCH">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
                <xsl:with-param name="NEWS">false</xsl:with-param>
                <xsl:with-param name="TIPS">false</xsl:with-param>
              </xsl:apply-templates>
            </td>
            <td valign="top" bgcolor="#FFFFFF" width="1"><img src="/engresources/images/s.gif" width="180"/></td>
          </tr>
        </table>

      </xsl:when>
      <xsl:when test="($SEARCH-TYPE='Expert')">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td valign="top" height="5">
              <img src="/engresources/images/s.gif" border="0" height="5"/>
            </td>
          </tr>
          <tr>
            <td valign="top" bgcolor="#FFFFFF" width="1"><img src="/engresources/images/s.gif" width="100"/></td>
            <td valign="top">
              <img src="/engresources/images/s.gif" width="10"/>
              <a class="EvHeaderText">Refine Search</a>
              <xsl:apply-templates select="//EXPERT-SEARCH">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
                <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
                <xsl:with-param name="YEAR-STRING" select="$YEAR-STRING"/>
                <xsl:with-param name="NEWS">false</xsl:with-param>
                <xsl:with-param name="TIPS">false</xsl:with-param>
              </xsl:apply-templates>
            </td>
            <td valign="top" bgcolor="#FFFFFF" width="1"><img src="/engresources/images/s.gif" width="180"/></td>
          </tr>
        </table>
      </xsl:when>
      <xsl:when test="($SEARCH-TYPE='TagSearch')">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td height="20"><img src="/engresources/images/s.gif" height="20"/></td>
          </tr>
          <tr>
            <td valign="top">
              <form name="searchtagsform" action="/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=tagSearch&amp;searchtype=TagSearch&amp;database={$DATABASE}" METHOD="POST" >
                <table>
                  <tr>
                    <td valign="top"><a class="MedBlackText">
                      <input type="text" size="20" name="searchtags"/></a>
                      <img src="/engresources/images/s.gif" height="20"/>
                      <a><input type="image" src="/engresources/images/search_orange1.gif" name="edit" value="Edit" border="0"/></a>
                    </td>
                  </tr>
                </table>
              </form>
            </td>
          </tr>
          <tr>
            <td valign="top" height="5">
              <img src="/engresources/images/s.gif" border="0" height="5"/>
            </td>
          </tr>
        </table>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>

