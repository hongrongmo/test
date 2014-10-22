<?xml version="1.0" ?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
  xmlns:book="java:org.ei.books.BookDocument"
  xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
  exclude-result-prefixes="schar java html xsl custoptions"
>

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
<xsl:strip-space elements="html:* xsl:*" />

<!--- all XSL include files -->
<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="template.SEARCH_RESULTS.xsl" />
<xsl:include href="Footer.xsl"/>
<!-- end of include -->

<xsl:include href="LocalHolding.xsl" />

<xsl:include href="common/DetailedResults.xsl" />

<xsl:param name="CUST-ID">0</xsl:param>

<xsl:variable name="DATABASE-MASK">
    <xsl:value-of select="//DBMASK"/>
</xsl:variable>

<xsl:variable name="FULLTEXT">
    <xsl:value-of select="//FULLTEXT"/>
</xsl:variable>

  <!-- Hide Map data -->
  <xsl:template match="CRDN|RECT|LOC|LOCS" priority="1"/>

<xsl:template match="PAGE">

    <xsl:variable name="SEARCH-TYPE">
        <xsl:value-of select="SEARCH-TYPE"/>
    </xsl:variable>
    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>
    <xsl:variable name="FOLDER-ID">
        <xsl:value-of select="FOLDER-ID"/>
    </xsl:variable>
    <xsl:variable name="FOLDER-COUNT">
        <xsl:value-of select="FOLDER-COUNT"/>
    </xsl:variable>
    <xsl:variable name="FOLDER-NAME">
        <xsl:value-of select="FOLDER-NAME"/>
    </xsl:variable>
    <xsl:variable name="FOLDER-SIZE">
        <xsl:value-of select="FOLDER-SIZE"/>
    </xsl:variable>

    <xsl:variable name="DEFAULT-DATABASE">
            <xsl:value-of select="DATABASE"/>
    </xsl:variable>


    <html>
    <head>
        <title>My Folders: <xsl:value-of select="$FOLDER-NAME"/></title>
            <SCRIPT LANGUAGE="Javascript" SRC="/static/js/SavedRecords.js"/>
            <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
            <SCRIPT LANGUAGE="Javascript" SRC="/static/js/lindaHall.js"/>
            <SCRIPT LANGUAGE="Javascript" SRC="/static/js/encompassFields.js"/>
    </head>
    <!-- start of body tag -->
    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

        <center>

        <!-- Insert header -->
        <xsl:apply-templates select="HEADER"/>

        <!-- Insert Global Links -->
        <xsl:apply-templates select="GLOBAL-LINKS">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
            <xsl:with-param name="SELECTED-DB" select="$DATABASE-MASK"/>
            <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
        </xsl:apply-templates>

    <!-- Simple "Navigation Bar" -->
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
            <tr>
                <td align="left" valign="middle" height="24" bgcolor="#C3C8D1"><img width="8" height="1" src="/static/images/s.gif" border="0" /><a href="/controller/servlet/Controller?CID=viewPersonalFolders&amp;database={$DATABASE-MASK}"><img src="/static/images/myfolders.gif" border="0"/></a></td>
            </tr>
        </table>

        <!-- Insert the TOP Results Manager -->
        <xsl:call-template name="RESULTS-MANAGER">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="LOCATION">top</xsl:with-param>
      <xsl:with-param name="DISPLAY-FORMAT">detailed</xsl:with-param>
      <xsl:with-param name="CURRENT-VIEW">savedrecords</xsl:with-param>
        </xsl:call-template>

        <table border="0" width="99%" cellspacing="0" cellpadding="0">
            <tr>
                <td valign="top" colspan="5">
                    <table border="0" width="99%" cellspacing="0" cellpadding="0">
                        <tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>
                        <tr><td valign="top"><a class="EvHeaderText">Folder Name: </a><a CLASS="MedBlackText"><xsl:value-of select="$FOLDER-NAME"/></a></td></tr>
                        <tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td valign="top" colspan="5">
                    <a CLASS="SmBlackText">There
                    <xsl:choose>
                        <xsl:when test="($FOLDER-SIZE='1')">is </xsl:when>
                        <xsl:otherwise>are </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="$FOLDER-SIZE"/> saved records in this folder
                    </a>
                </td>
            </tr>
            <xsl:apply-templates select="PAGE-RESULTS"/>
        </table>


        <!-- Insert the BOTTOM Results Manager -->
        <xsl:call-template name="RESULTS-MANAGER">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="LOCATION">bottom</xsl:with-param>
      <xsl:with-param name="DISPLAY-FORMAT">detailed</xsl:with-param>
      <xsl:with-param name="CURRENT-VIEW">savedrecords</xsl:with-param>
        </xsl:call-template>

            <!-- Insert the Footer table -->
            <xsl:apply-templates select="FOOTER">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
                <xsl:with-param name="SELECTED-DB" select="$DATABASE-MASK"/>
            </xsl:apply-templates>

    </center>
    </body>
</html>

    </xsl:template>

    <xsl:template match="PAGE-RESULTS">

        <FORM name="detailed">
            <xsl:apply-templates select="PAGE-ENTRY"/>
        </FORM>

    </xsl:template>


    <xsl:template match="PAGE-ENTRY">

        <xsl:variable name="FOLDER-ID">
            <xsl:value-of select="/PAGE/FOLDER-ID"/>
        </xsl:variable>

        <xsl:variable name="DATABASE-ID">
            <xsl:value-of select="EI-DOCUMENT/DOC/DB/ID"/>
        </xsl:variable>

        <xsl:variable name="DOC-ID">
            <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
        </xsl:variable>

        <xsl:variable name="INDEX">
            <xsl:value-of select="position()"/>
        </xsl:variable>


        <tr>
            <td valign="top" colspan="5" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td>
        </tr>

        <tr>
            <td valign="top" align="left">
                <A href="/controller/servlet/Controller?CID=deleteFromSavedRecords&amp;format=detailed&amp;database={$DATABASE-MASK}&amp;docid={$DOC-ID}&amp;folderid={$FOLDER-ID}">
                    <img src="/static/images/remove.gif" border="0"/>
                </A>
            </td>

            <td valign="top" width="3"><img src="/static/images/s.gif" border="0" width="3"/></td>
            <td valign="top">
                <A CLASS="MedBlackText"><xsl:value-of select="position()"/>.</A>
            </td>
            <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            <td valign="top" width="100%" align="left">
                <xsl:apply-templates select="EI-DOCUMENT"/>
            </td>
        </tr>

        <!-- Local holdings, Full text and Linda hall Links variables -->

        <xsl:variable name="FULLTEXT-LINK">
            <xsl:value-of select="EI-DOCUMENT/FT/@FTLINK"/>
        </xsl:variable>

        <xsl:variable name="LHL">
            <xsl:value-of select="//LHL"/>
        </xsl:variable>
        <xsl:variable name="LOCALHOLDINGS">
            <xsl:value-of select="//LOCALHOLDINGS"/>
        </xsl:variable>

	 <xsl:variable name="CHECK-CUSTOM-OPT">
		<xsl:value-of select="custoptions:checkFullText($FULLTEXT,$FULLTEXT-LINK,$CUST-ID, EI-DOCUMENT/DO , EI-DOCUMENT/DOC/DB/DBMASK)" />
	 </xsl:variable>

    <xsl:if test=" (($CHECK-CUSTOM-OPT ='true') or ($LHL='true') or ($LOCALHOLDINGS='true'))">
      <tr>
        <td valign="top" colspan="4" height="15"><img src="/static/images/s.gif" border="0"/></td>
        <td valign="top" height="15" bgcolor="#C3C8D1"><a CLASS="MedBlackText"><b>&#160; Full-text and Local Holdings Links</b></a></td>
      </tr>

      <xsl:if test="($LOCALHOLDINGS='true')">
        <tr>
          <td valign="top" colspan="4" height="15"><img src="/static/images/s.gif" border="0"/></td>
          <td valign="bottom" align="left" >
            <xsl:apply-templates select="LOCAL-HOLDINGS" >
              <xsl:with-param name="vISSN"><xsl:value-of select="EI-DOCUMENT/SN"/></xsl:with-param>
            </xsl:apply-templates>
          </td>
        </tr>
        <tr>
          <td valign="top" colspan="5" height="4"><img src="/static/images/s.gif" border="0" height="4"/></td>
        </tr>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="($CHECK-CUSTOM-OPT ='true')" >
          <tr>
            <td valign="bottom" colspan="4" height="30"><img src="/static/images/s.gif" border="0" height="30"/></td>
            <td valign="middle" align="left">
              <a href="" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false"><img src="/static/images/av.gif" border="0" /></a>
            </td>
          </tr>
        </xsl:when>
        <xsl:otherwise>
          <tr><td valign="bottom" colspan="4" height="30"><img src="/static/images/s.gif" border="0" height="30"/></td>
          </tr>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:if test="(EI-DOCUMENT/DOC/DB/DBMASK != '131072') and ($LHL='true')">
        <tr>
          <td valign="top" colspan="4" height="15"><img src="/static/images/s.gif" border="0"/></td>
          <td valign="top">
            <a CLASS="MedBlueLink" href="javascript:lindaHall('$SESSIONID','{$DOC-ID}','{$DATABASE-ID}')">Linda Hall Library document delivery service</a>
          </td>
        </tr>
        <tr>
          <td valign="top" colspan="5" height="4"><img src="/static/images/s.gif" border="0" height="4"/></td>
        </tr>
      </xsl:if>


        <xsl:if test="(EI-DOCUMENT/DOC/DB/DBMASK = '131072')">
          <xsl:variable name="BOOKS_OPEN_WINDOW_PARAMS">height=800,width=700,status=yes,resizable,scrollbars=1,menubar=no</xsl:variable>
  				<tr>
  					<td valign="top" colspan="4"><img src="/static/images/s.gif" height="30" border="0"/></td>
  					<td valign="top">
              <xsl:if test="not(EI-DOCUMENT/BPP = '0')">
                <a >
                  <xsl:attribute name="TITLE">Read Page</xsl:attribute>
                  <xsl:attribute name="HREF">javascript:_referex=window.open('/controller/servlet/Controller?CID=bookFrameset&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;docid=<xsl:value-of select="$DOC-ID"/>&amp;database=<xsl:value-of select="$DATABASE-MASK"/>','_referex','<xsl:value-of select="$BOOKS_OPEN_WINDOW_PARAMS"/>');_referex.focus();void('');</xsl:attribute>
                  <img alt="Read Page" src="/static/images/read_page.gif" style="border:0px; vertical-align:middle"/>
                  </a>
                &#160;
              </xsl:if>

              <xsl:if test="not(EI-DOCUMENT/PII = '')">
                <a>
                  <xsl:attribute name="target">_referex</xsl:attribute>
                  <xsl:attribute name="TITLE">Read Chapter</xsl:attribute>
                  <xsl:attribute name="HREF"><xsl:value-of select="book:getReadChapterLink(WOBLSERVER, EI-DOCUMENT/BN13, EI-DOCUMENT/PII, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                  <img alt="Read Chapter" src="/static/images/read_chp.gif" style="border:0px; vertical-align:middle"/>
                  </a>
                &#160;
              </xsl:if>

                <a >
                  <xsl:attribute name="target">_referex</xsl:attribute>
                  <xsl:attribute name="TITLE">Read Book</xsl:attribute>
                  <xsl:attribute name="HREF"><xsl:value-of select="book:getReadBookLink(WOBLSERVER, EI-DOCUMENT/BN13, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                  <img alt="Read Book" src="/static/images/read_book.gif" style="border:0px; vertical-align:middle"/>
                  </a>
  					</td>
  				</tr>
        </xsl:if>

		</xsl:if>

    </xsl:template>

</xsl:stylesheet>
