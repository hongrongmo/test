<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<!--- all XSL include files -->
<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="template.SEARCH_RESULTS.xsl" />
<xsl:include href="Footer.xsl"/>
<xsl:include href="LocalHolding.xsl" />

<!--- end of XSL include files -->

<xsl:include href="common/CitationResults.xsl" />

<xsl:variable name="DATABASE-MASK">
        <xsl:value-of select="//DBMASK"/>
</xsl:variable>

<xsl:variable name="FULLTEXT">
    <xsl:value-of select="//FULLTEXT" />
</xsl:variable>

<xsl:variable name="LOCALHOLDINGS-CITATION">
    <xsl:value-of select="//LOCALHOLDINGS-CITATION"/>
</xsl:variable>

  <xsl:template match="PAGE">

    <xsl:variable name="SEARCH-TYPE">
        <xsl:value-of select="SEARCH-TYPE"/>
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

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>


    <xsl:variable name="DATABASE">
        <xsl:value-of select="DATABASE"/>
    </xsl:variable>

    <html>
    <head>
    <title>My Folders: <xsl:value-of select="$FOLDER-NAME"/></title>
            <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
            <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/SavedRecords.js"/>
    </head>
    <!-- start of body -->
    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" >

    <center>

        <!-- Start of header -->
        <xsl:apply-templates select="HEADER"/>

        <!-- Start of globalnavigation bar -->
        <xsl:apply-templates select="GLOBAL-LINKS">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
            <xsl:with-param name="SELECTED-DB" select="$DATABASE-MASK"/>
            <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
        </xsl:apply-templates>

    <xsl:choose>
        <!-- When there are zero document in the folder -->
        <xsl:when test="(string-length(normalize-space(ERROR))>0)">
            <center>
                <!-- Start of topnavigation bar -->
                <table border="0" width="99%" cellspacing="0" cellpadding="0">
                    <tr>
                        <td align="left" valign="middle" height="24" bgcolor="#C3C8D1">&spcr8;<a href="/controller/servlet/Controller?CID=viewPersonalFolders&amp;database={$DATABASE-MASK}"><img src="/engresources/images/myfolders.gif" border="0"/></a></td>
                    </tr>
                </table>
            </center>
            <br/>
            <table border="0" width="99%" cellspacing="0" cellpadding="0">
                <tr>
                    <td valign="top"><a class="EvHeaderText">Folder Name: </a><a CLASS="MedBlackText"><xsl:value-of select="$FOLDER-NAME"/></a></td>
                </tr>
                <tr>
                    <td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td>
                </tr>
                <tr>
                    <td><a CLASS="MedBlackText">There are no records in this folder</a></td>
                </tr>
            </table>
        </xsl:when>
      <xsl:otherwise>

        <!-- Simple "Navigation Bar" -->
            <table border="0" width="99%" cellspacing="0" cellpadding="0">
                <tr>
                    <td align="left" valign="middle" height="24"  bgcolor="#C3C8D1">&spcr8;<a href="/controller/servlet/Controller?CID=viewPersonalFolders&amp;database={$DATABASE-MASK}"><img src="/engresources/images/myfolders.gif" border="0"/></a></td>
                </tr>
            </table>

            <!-- Insert the TOP Results Manager -->
            <xsl:call-template name="RESULTS-MANAGER">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
            <xsl:with-param name="LOCATION">top</xsl:with-param>
          <xsl:with-param name="DISPLAY-FORMAT">citation</xsl:with-param>
          <xsl:with-param name="CURRENT-VIEW">savedrecords</xsl:with-param>
            </xsl:call-template>

            <!-- Start of table for search results -->
            <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top" colspan="5">
                <table border="0" width="99%" cellspacing="0" cellpadding="0">
                    <tr><td valign="top" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
                    <tr><td valign="top"><a class="EvHeaderText">Folder Name: </a><a CLASS="MedBlackText"><xsl:value-of select="$FOLDER-NAME"/></a></td></tr>
                    <tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
                </table>
            </td>
          </tr>
          <tr>
                  <td valign="top" colspan="5">
                      <a CLASS="SmBlackText">
                             There
                                <xsl:choose>
                                    <xsl:when test="($FOLDER-SIZE='1')">
                                        is  <xsl:value-of select="$FOLDER-SIZE"/> saved record in this folder
                                    </xsl:when>
                                    <xsl:otherwise>
                                        are <xsl:value-of select="$FOLDER-SIZE"/> saved records in this folder
                                    </xsl:otherwise>
                                </xsl:choose>
              </a>
            </td>
          </tr>
                <FORM name="citation">
                    <xsl:apply-templates select="PAGE-RESULTS"/>
                </FORM>
        </table>

            <!-- Insert the BOTTOM Results Manager -->
            <xsl:call-template name="RESULTS-MANAGER">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
            <xsl:with-param name="LOCATION">bottom</xsl:with-param>
          <xsl:with-param name="DISPLAY-FORMAT">citation</xsl:with-param>
          <xsl:with-param name="CURRENT-VIEW">savedrecords</xsl:with-param>
            </xsl:call-template>

      </xsl:otherwise>
    </xsl:choose>

    <!-- Insert Footer -->
    <xsl:apply-templates select="FOOTER">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE-MASK"/>
    </xsl:apply-templates>


    </center>
    </body>
    </html>


    </xsl:template>

    <!-- This xsl displays the results in Citation Format when the database is Compendex -->
    <xsl:template match="PAGE-RESULTS">

        <!-- Start of  Citation Results  -->

            <FORM name="citation">
                <xsl:apply-templates select="PAGE-ENTRY"/>
            </FORM>

        <!-- END of  Citation Results  -->

    </xsl:template>

    <xsl:variable name="HREF-PREFIX"/>

    <xsl:template match="PAGE-ENTRY">

        <xsl:variable name="FOLDER-ID">
            <xsl:value-of select="/PAGE/FOLDER-ID"/>
        </xsl:variable>
        <xsl:variable name="INDEX">
            <xsl:value-of select="DOCUMENTBASKETHITINDEX"/>
        </xsl:variable>
        <xsl:variable name="DATABASE-ID">
            <xsl:value-of select="EI-DOCUMENT/DBID" />
        </xsl:variable>

        <xsl:variable name="DOC-ID">
            <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
        </xsl:variable>

        <xsl:variable name="FULLTEXT-LINK">
            <xsl:value-of select="EI-DOCUMENT/FT/@FTLINK"/>
        </xsl:variable>

        <tr>
            <td valign="top" colspan="5" height="8"><img src="/engresources/images/s.gif" border="0" height="8"/></td>
        </tr>
        <tr>
        <td valign="top" align="right">
                <A href="/controller/servlet/Controller?CID=deleteFromSavedRecords&amp;format=citation&amp;database={$DATABASE-MASK}&amp;docid={$DOC-ID}&amp;folderid={$FOLDER-ID}"><img src="/engresources/images/remove.gif" border="0"/></A>
            </td>
            <td valign="top" width="3"><img src="/engresources/images/s.gif" border="0" width="3"/></td>

            <td valign="top"><A CLASS="MedBlackText"><xsl:value-of select="$INDEX"/>.</A></td>

            <td valign="top" width="4"><img src="/engresources/images/s.gif" border="0" width="4"/></td>

            <td valign="bottom" width="100%" align="left">
                <xsl:apply-templates select="EI-DOCUMENT"/>
            </td>
        </tr>

        <xsl:if test="((($FULLTEXT='true') and ($FULLTEXT-LINK = 'Y')) or ($FULLTEXT-LINK = 'A')) or ($LOCALHOLDINGS-CITATION='true')" >
             <tr>
                 <td valign="bottom" colspan="4" height="30"><img src="/engresources/images/s.gif" border="0" height="30"/></td>
                 <td valign="middle" align="left">

                <xsl:if test="(($FULLTEXT='true') and ($FULLTEXT-LINK = 'Y')) or ($FULLTEXT-LINK = 'A')">
	                  <a href="" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false"><img src="/engresources/images/av.gif" border="0" /></a>
		                  <xsl:if test="(($LOCALHOLDINGS-CITATION='true') and (count(LOCAL-HOLDINGS) > 0))">
			                    <A CLASS="MedBlackText">&#160; - &#160;</A>
		                  </xsl:if>
                </xsl:if>

                <xsl:if test="($LOCALHOLDINGS-CITATION='true')">
                    <xsl:apply-templates select="LOCAL-HOLDINGS">
                    <xsl:with-param name="vISSN"><xsl:value-of select="EI-DOCUMENT/SN"/></xsl:with-param>
                    </xsl:apply-templates>
                </xsl:if>
                 </td>
            </tr>
        </xsl:if>

        <!-- END OF unique code for Selected Set -->

    </xsl:template>

</xsl:stylesheet>
