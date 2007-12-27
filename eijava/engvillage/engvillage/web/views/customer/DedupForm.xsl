<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLDecoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java xsl">

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="DedupFormNavigationBar.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
        <xsl:value-of select="DBMASK"/>
    </xsl:variable>

    <xsl:variable name="COUNT">
        <xsl:value-of select="COUNT"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-ID">
        <xsl:value-of select="SEARCH-ID"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
        <xsl:value-of select="SEARCH-TYPE"/>
    </xsl:variable>

    <xsl:variable name="RESULTS-COUNT">
        <xsl:value-of select="RESULTS-COUNT"/>
    </xsl:variable>

    <html>
    <head>
        <title>Remove Duplicates</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

    <!-- APPLY HEADER -->
    <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SESSION-ID" select="SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="DATABASE"/>
    </xsl:apply-templates>

    <center>

    <!-- Insert the Global Links -->
  <!-- logo, search history, selected records, my folders. end session -->
  <xsl:apply-templates select="GLOBAL-LINKS">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
  </xsl:apply-templates>

  <xsl:apply-templates select="DEDUPFORM-NAVIGATION-BAR">
    <xsl:with-param name="HEAD" select="$SEARCH-ID"/>
    <xsl:with-param name="LOCATION">Top</xsl:with-param>
  </xsl:apply-templates>


  <table border="0" width="80%" cellspacing="0" cellpadding="0">
    <tr>
      <td height="20" bgcolor="#FFFFFF" colspan="2"><img src="/engresources/images/s.gif" height="20"/></td>
    </tr>
    <tr>
      <td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" height="1"/></td>
    </tr>
    <tr>
      <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1"/></td>
      <td valign="top">

      <form name="removedup" action="/controller/servlet/Controller?EISESSION={$SESSION-ID}&amp;CID=dedup&amp;SEARCHID={$SEARCH-ID}&amp;COUNT={$COUNT}&amp;database={$DATABASE}" method="POST">
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
          <td width="2" bgcolor="#3173B5"><img src="/engresources/images/s.gif" width="2"/></td>
          <td height="10" colspan="3" bgcolor="#3173B5"><a CLASS="LgWhiteText"><b>Remove Duplicates</b></a></td>
        </tr>
        <tr>
          <td colspan="4" height="8"><img src="/engresources/images/s.gif" height="8"/></td>
        </tr>
        <tr>
          <td width="2"><img src="/engresources/images/s.gif" width="2"/></td>
          <td valign="top" colspan="3"><a CLASS="MedBlackText">Duplicate records will be removed from the first 1000 records in the result set. Use the form below to choose the fields and the database that you prefer to see results from.</a></td>
        </tr>
        <tr>
          <td colspan="4" height="4"><img src="/engresources/images/s.gif" height="4"/></td>
        </tr>
        <tr>
          <td width="4"><img src="/engresources/images/s.gif" width="4"/></td>
          <td valign="top" width="40%">
            <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top"><a class="MedBlackText"><b>Field Preferences:</b></a>&#160;
                  <a class="DecLink" href="javascript:makeUrl('Deduplication_Feature.htm')">
                  <img src="/engresources/images/blue_help.gif" align="absmiddle" border="0"/>
                  </a>
                </td>
              </tr>
              <tr>
                <td valign="middle">
                  <xsl:variable name="FIELDPREF">
                    <xsl:choose>
                      <xsl:when test="/PAGE/FORM[@NAME='removedup']/FIELDPREF">
                        <xsl:value-of select="/PAGE/FORM[@NAME='removedup']/FIELDPREF"/>
                      </xsl:when>
                      <xsl:otherwise>0</xsl:otherwise>
                    </xsl:choose>
                  </xsl:variable>
                  <ul class="MedBlackText" style="list-style-type:none; list-style:inside; margin:0;">
                    <li>
                      <input type="radio" id="rdo0" name="fieldpref" value="0">
                      <xsl:if test="$FIELDPREF='0'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                      </input>
                      <label for="rdo0">No field preference</label>
                    </li>
                    <li>
                      <input type="radio" id="rdo4" name="fieldpref" value="4">
                      <xsl:if test="$FIELDPREF='4'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                      </input>
                      <label for="rdo4">Has Full Text</label>
                    </li>
                    <li>
                      <input type="radio" id="rdo1" name="fieldpref" value="1">
                      <xsl:if test="$FIELDPREF='1'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                      </input>
                      <label for="rdo1">Has Abstract</label>
                    </li>
                    <li>
                      <input type="radio" id="rdo2" name="fieldpref" value="2">
                      <xsl:if test="$FIELDPREF='2'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                      </input>
                      <label for="rdo2">Has Index Terms</label>
                    </li>
                  </ul>
                </td>
              </tr>
            </table>
          </td>
          <td width="10"><img src="/engresources/images/s.gif" width="10"/></td>
          <td valign="top" width="60%">
            <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top"><a class="MedBlackText"><b><label for="dbpref">Database Preferences:</label></b></a>&#160;
                 <a class="DecLink" href="javascript:makeUrl('Deduplication_Feature.htm')">
     		    	<img src="/engresources/images/blue_help.gif" align="absmiddle" border="0"/>
	            </a>
                </td>
              </tr>
              <tr>
                <td height="2"><img src="/engresources/images/s.gif" height="2"/></td>
              </tr>
              <tr>
                <td valign="middle">&#160; &#160; <a class="MedBlackText">
                  <select id="dbpref" name="dbpref">
                    <xsl:for-each select="DEDUPABLE-DB/DB">
                      <option>
                        <xsl:attribute name="value"><xsl:value-of select="@ID"/></xsl:attribute>
                        <xsl:if test="/PAGE/FORM[@NAME='removedup']/DBPREF=@ID">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="@NAME"/>
                      </option>
                    </xsl:for-each>
                  </select></a>
                </td>
              </tr>
              <tr>
                <td height="10"><img src="/engresources/images/s.gif" border="0" height="10"/></td>
              </tr>
              <tr>
                <td valign="top">&#160; &#160; <input type="image" src="/engresources/images/cont.gif" border="0"/></td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td height="6"><img src="/engresources/images/s.gif" height="6"/></td>
        </tr>
      </table>
      </form>
      </td>
      <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1"/></td>
    </tr>
    <tr>
      <td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1"/></td>
    </tr>
  </table>

  <br/>

    <!-- Start of footer-->
    <xsl:apply-templates select="FOOTER">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    </xsl:apply-templates>

  </center>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
