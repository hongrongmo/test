<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
  xmlns:srt="java:org.ei.domain.Sort"
  xmlns:bit="java:org.ei.util.BitwiseOperators"
  xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
  exclude-result-prefixes="java html xsl DD srt bit custoptions"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl" />
<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="AbstractDetailedNavigationBar.xsl" />
<xsl:include href="template.SEARCH_RESULTS.xsl" />
<xsl:include href="Footer.xsl" />

<xsl:include href="DeDupControl.xsl" />
<xsl:include href="common/CitationResults.xsl" />
<xsl:include href="LocalHolding.xsl" />

<xsl:param name="CUST-ID">0</xsl:param>
  
<xsl:variable name="CID"><xsl:value-of select="//PAGE/CID"/></xsl:variable>

<xsl:variable name="FULLTEXT">
  <xsl:value-of select="//FULLTEXT" />
</xsl:variable>

<xsl:variable name="PERSONALIZATION-PRESENT">
  <xsl:value-of select="//PAGE/PERSONALIZATION"/>
</xsl:variable>

<xsl:variable name="LOCALHOLDINGS-CITATION">
  <xsl:value-of select="//LOCALHOLDINGS-CITATION"/>
</xsl:variable>

<xsl:variable name="CURRENT-PAGE">
  <xsl:value-of select="//PAGE/CURR-PAGE-ID"/>
</xsl:variable>

<xsl:variable name="SEARCH-ID">
  <xsl:value-of select="//PAGE/SEARCH-ID"/>
</xsl:variable>

<xsl:variable name="SESSION-ID">
  <xsl:value-of select="//PAGE/SESSION-ID" />
</xsl:variable>

<xsl:variable name="DISPLAY-QUERY">
  <xsl:value-of select="//PAGE/SESSION-DATA/DISPLAY-QUERY"/>
</xsl:variable>

<xsl:variable name="ENCODED-DISPLAY-QUERY">
  <xsl:value-of select="java:encode($DISPLAY-QUERY)"/>
</xsl:variable>

<xsl:variable name="BACKURL">
  <xsl:value-of select="//PAGE//BACKURL"/>
</xsl:variable>

<xsl:variable name="DATABASE">
  <xsl:value-of select="//PAGE/DBMASK"/>
</xsl:variable>

<xsl:variable name="RESULTS-NAV">
	<xsl:value-of select="/PAGE/PAGE-NAV/RESULTS-NAV"/>
</xsl:variable>

 <xsl:variable name="ENCODED-RESULTS-NAV">
        <xsl:value-of select="java:encode($RESULTS-NAV)" />
 </xsl:variable>


<xsl:variable name="SEARCH-TYPE">
  <xsl:value-of select="/PAGE/SESSION-DATA/SEARCH-TYPE"/>
</xsl:variable>

<xsl:variable name="RESULTS-COUNT">
  <xsl:value-of select="//RESULTS-COUNT"/>
</xsl:variable>

<xsl:variable name="NP-COUNT">
  <xsl:value-of select="//NP-COUNT"/>
</xsl:variable>

<xsl:variable name="RESULTS-PER-PAGE">
  <xsl:value-of select="//PAGE/RESULTS-PER-PAGE"/>
</xsl:variable>

<xsl:template match="PAGE">

  <xsl:variable name="SEARCH-TYPE">
    <xsl:value-of select="SESSION-DATA/SEARCH-TYPE"/>
  </xsl:variable>

  <xsl:variable name="RESULTS-PER-PAGE">
    <xsl:value-of select="//RESULTS-PER-PAGE"/>
  </xsl:variable>

  <xsl:variable name="DATABASE-DISPLAYNAME">
    <xsl:value-of select="DD:getDisplayName(SELECTED-DATABASE)"/>
  </xsl:variable>

  <xsl:variable name="DATABASE">
    <xsl:value-of select="DBMASK"/>
  </xsl:variable>

  <xsl:variable name="DATABASE-ID">
    <xsl:value-of select="//SESSION-DATA/DATABASE/ID"/>
  </xsl:variable>

  <html>
  <head>
      <META http-equiv="Expires" content="0"/>
      <META http-equiv="Pragma" content="no-cache"/>
      <META http-equiv="Cache-Control" content="no-cache"/>
      <title>Engineering Village - <xsl:value-of select="$DATABASE-DISPLAYNAME"/><xsl:text> </xsl:text><xsl:value-of select="$SEARCH-TYPE"/> Search Results</title>

      <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
      <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/SearchResults_V7.js"/>

      <xsl:if test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus') or ($SEARCH-TYPE='Combined')">
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/QuickResults.js"/>
      </xsl:if>
      <xsl:if test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Easy')">
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/ExpertResults.js"/>
      </xsl:if>
  </head>

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

    <!-- APPLY HEADER -->
    <xsl:apply-templates select="HEADER">
      <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
    </xsl:apply-templates>

    <center>

      <!-- Insert the Global Links -->
      <!-- logo, search history, selected records, my folders. end session -->
      <xsl:apply-templates select="GLOBAL-LINKS">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
      </xsl:apply-templates>

      <!-- Insert the navigation bar -->
      <!-- tabbed navigation -->
      <xsl:apply-templates select="PAGE-NAV">
        <xsl:with-param name="HEAD" select="$SEARCH-ID"/>
      </xsl:apply-templates>

      <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <tr>
          <td valign="top">

            <xsl:if test="not($SEARCH-TYPE='Easy')">
              <xsl:apply-templates select="//SESSION-DATA/SC"/>
            </xsl:if>

            <xsl:apply-templates select="PAGE-RESULTS"/>

            <!-- Display of 'Next Page' Navigation Bar -->
            <xsl:apply-templates select="NAVIGATION-BAR">
              <xsl:with-param name="HEAD" select="$SEARCH-ID"/>
              <xsl:with-param name="LOCATION">Bottom</xsl:with-param>
            </xsl:apply-templates>


            <p align="right">
            <a class="MedBlueLink" href="#">Back to top</a> <a href="#"><img src="/engresources/images/top.gif" border="0"/></a>
            </p>

          </td>
        </tr>
      </table>

    </center>

    <!-- Insert the Footer table -->
    <xsl:apply-templates select="FOOTER">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    </xsl:apply-templates>

    <br/>
  </body>
  </html>

</xsl:template>

<!-- This xsl displays the results in Citation Format when the database is Compendex -->
<xsl:template match="PAGE-RESULTS">
  <!-- Start of  Citation Results  -->
  <FORM name="quicksearchresultsform">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
      <tr>
        <td>
          <xsl:apply-templates select="UPT-DOC"/>
        </td>
      </tr>
    </table>
  </FORM>
  <!-- END of  Citation Results  -->
</xsl:template>

<xsl:variable name="CID-PREFIX">
  <xsl:choose>
    <xsl:when test="(substring($CID,0,10)='reference')">reference</xsl:when>
    <xsl:when test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus')">quickSearch</xsl:when>
    <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">expertSearch</xsl:when>
    <xsl:otherwise>expertSearch</xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:variable name="HREF-PREFIX" />

<xsl:template match="UPT-DOC">
  <xsl:variable name="REF-CNT">
    <xsl:value-of select="EI-DOCUMENT/RCT"/>
  </xsl:variable>
  <xsl:variable name="DATABASE-ID">
    <xsl:value-of select="EI-DOCUMENT/DBID"/>
  </xsl:variable>
  <xsl:variable name="SELECTED-DB">
    <xsl:value-of select="//PAGE/DBMASK"/>
  </xsl:variable>
  <xsl:variable name="CIT-CNT">
    <xsl:value-of select="EI-DOCUMENT/CCT"/>
  </xsl:variable>
  <xsl:variable name="DOC-ID">
    <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
  </xsl:variable>
  <xsl:variable name="AUTH-CODE">
    <xsl:value-of select="EI-DOCUMENT/AUTHCD"/>
  </xsl:variable>
  <xsl:variable name="PATENT-NUM">
    <xsl:value-of select="EI-DOCUMENT/PAP"/>
  </xsl:variable>
  <xsl:variable name="EPPNUM">
    <xsl:value-of select="$AUTH-CODE"/><xsl:value-of select="$PATENT-NUM"/>
  </xsl:variable>
  <xsl:variable name="KIND-CODE">
    <xsl:value-of select="EI-DOCUMENT/KC"/>
  </xsl:variable>
  <xsl:variable name="NONREF-CNT">
    <xsl:value-of select="EI-DOCUMENT/NPRCT"/>
  </xsl:variable>
  <xsl:variable name="INDEX">
    <xsl:value-of select="$CURRENT-PAGE"/>
  </xsl:variable>
<xsl:variable name="TOP-ABSTRACT-LINK">
<xsl:value-of select="/PAGE/PAGE-NAV/ABS-NAV"/>
</xsl:variable>
<xsl:variable name="TOP-DETAILED-LINK">
<xsl:value-of select="/PAGE/PAGE-NAV/DET-NAV"/>
</xsl:variable>


<!-- "&amp;pcinav=0~<patent#>~Patents that cite <patent#>" encoded into links -->
    <xsl:variable name="CITEDBY-PM">
      <xsl:value-of select="EI-DOCUMENT/PM"/>
    </xsl:variable>

<!-- ZY 9/15/09 Append patent kind in searchword1 for EP patents -->
    <xsl:variable name="CITEDBY-QSTR">
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Quick')">CID=quickSearchCitationFormat&amp;searchWord1=<xsl:value-of select="$CITEDBY-PM"/><xsl:if test="$AUTH-CODE='EP'"><xsl:value-of select="$KIND-CODE"/></xsl:if>&amp;section1=PCI&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:when>
        <xsl:otherwise>CID=expertSearchCitationFormat&amp;searchWord1=<xsl:value-of select="$CITEDBY-PM"/><xsl:if test="$AUTH-CODE='EP'"><xsl:value-of select="$KIND-CODE"/></xsl:if><xsl:value-of select="java:encode(' WN PCI')"/>&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

<!--
    JAM replaced with above by for "pcinav"
  <xsl:variable name="CITEDBY-QSTR">
    <xsl:choose>
      <xsl:when test="($SEARCH-TYPE='Quick')">CID=quickSearchCitationFormat&amp;searchWord1=<xsl:value-of select="EI-DOCUMENT/PM"/>&amp;section1=PCI&amp;database=49152</xsl:when>
      <xsl:otherwise>CID=expertSearchCitationFormat&amp;searchWord1=<xsl:value-of select="EI-DOCUMENT/PM"/><xsl:value-of select="java:encode(' WN PCI')"/>&amp;database=49152</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
-->
  <xsl:variable name="FULLTEXT-LINK">
    <xsl:value-of select="EI-DOCUMENT/FT/@FTLINK"/>
  </xsl:variable>

  <center>
    <table border="0" cellspacing="0" cellpadding="0" width="99%">
      <tr>
        <td>
          <img src="/engresources/images/s.gif" border="0" height="10" />
        </td>
      </tr>
      <tr>
        <td align="left">
            <!-- all top of page links -->
            <A CLASS="LgBlueLink" HREF="/controller/servlet/Controller?{$TOP-ABSTRACT-LINK}">Abstract</A>
            <A CLASS="MedBlackText">&#160; - &#160;</A>
            <A CLASS="LgBlueLink" HREF="/controller/servlet/Controller?{$TOP-DETAILED-LINK}">Detailed</A>
            <xsl:if test="($REF-CNT) and not($REF-CNT ='') and not($REF-CNT ='0')">
        		  <xsl:variable name="PATENT-REFS-LINK">
        		     <xsl:value-of select="/PAGE/PAGE-NAV/PATREF-NAV" />
        		  </xsl:variable>
              <A CLASS="MedBlackText">&#160; - &#160;</A>
              <A title="Show patents that are referenced by this patent" CLASS="LgBlueLink" HREF="/controller/servlet/Controller?{$PATENT-REFS-LINK}">Patent Refs</A>&nbsp;<A CLASS="MedBlackText">(<xsl:value-of select="$REF-CNT"/>)</A>
            </xsl:if>
            <xsl:if test="($NONREF-CNT) and not($NONREF-CNT ='') and not($NONREF-CNT ='0')">
              <A CLASS="MedBlackText">&#160; - &#160;</A>
              <a class="BrownText">Other Refs</a>&nbsp;<A CLASS="MedBlackText">(<xsl:value-of select="$NONREF-CNT"/>)</A>
            </xsl:if>
            <xsl:if test="($CIT-CNT) and not($CIT-CNT ='') and not($CIT-CNT ='0')">
              <A CLASS="MedBlackText">&#160; - &#160;</A>
              <A title="Show patents that reference this patent" CLASS="LgBlueLink" HREF="/controller/servlet/Controller?{$CITEDBY-QSTR}&amp;yearselect=yearrange&amp;searchtype={$SEARCH-TYPE}&amp;sort=yr">Cited by</A>&nbsp;<A CLASS="MedBlackText">(<xsl:value-of select="$CIT-CNT"/>)</A>
            </xsl:if>
            
	    <xsl:variable name="CHECK-CUSTOM-OPT">
		<xsl:value-of select="custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, EI-DOCUMENT/DO, EI-DOCUMENT/DOC/DB/DBMASK)" />
	    </xsl:variable>    
			        
            <xsl:if test="($CHECK-CUSTOM-OPT ='true')">
              <a class="MedBlackText">&#160; - &#160;</a>
              <a title="Full-text" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false">
                 <img id="ftimg" src="/engresources/images/av.gif" alt="Full-text"/></a>
            </xsl:if>
            <xsl:if test="($LOCALHOLDINGS-CITATION='true')">
              <A CLASS="MedBlackText">&#160; - &#160;</A>
              <xsl:apply-templates select="LOCAL-HOLDINGS" mode="CIT">
                <xsl:with-param name="vISSN"><xsl:value-of select="EI-DOCUMENT/SN"/></xsl:with-param>
              </xsl:apply-templates>
            </xsl:if>
            <!-- end of links-->
        </td>
      </tr>
      <tr>
        <td>
          <img src="/engresources/images/s.gif" border="0" height="10" />
        </td>
      </tr>
    </table>
  </center>

  <center>
    <table border="0" cellspacing="0" cellpadding="0" width="99%">
      <tr>
        <td valign="top">
          <xsl:variable name="AREIS">
            <xsl:if test="($NP-COUNT) and ($NP-COUNT ='1')">
              <xsl:text> is </xsl:text>
            </xsl:if>
            <xsl:if test="($NP-COUNT) and not($NP-COUNT ='1')">
              <xsl:text> are </xsl:text>
            </xsl:if>
          </xsl:variable>
          <xsl:variable name="REFS">
            <xsl:if test="($NP-COUNT) and ($NP-COUNT ='1')">
              <xsl:text> reference </xsl:text>
            </xsl:if>
            <xsl:if test="($NP-COUNT) and not($NP-COUNT ='1')">
              <xsl:text> references </xsl:text>
            </xsl:if>
          </xsl:variable>
          <a class="BlueText"><b>There <xsl:value-of select="$AREIS" /> <xsl:value-of select="$NP-COUNT"/> other <xsl:value-of select="$REFS" /> for the following patent:</b></a>
        </td>
      </tr>
      <tr><td valign="top"><img src="/engresources/images/s.gif" height="20"/></td></tr>
      <tr>
        <td valign="top" align="left">
          <xsl:apply-templates select="EI-DOCUMENT"/>
        </td>
      </tr>
      <tr><td height="10" colspan="4"><img src="/engresources/images/s.gif" height="10"/></td></tr>
      <tr><td colspan="4"><a CLASS="BigBlackTxt3"><b>Other References</b></a></td></tr>
      <tr><td height="10" colspan="4"><img src="/engresources/images/s.gif" height="10"/></td></tr>
      <tr><td height="10"><img src="/engresources/images/s.gif" height="10"/></td></tr>
        <xsl:apply-templates select="REF-DOCS"/>
    </table>
  </center>
</xsl:template>

<xsl:template match="REF-DOCS">
  <tr>
    <td valign="top" width="100%" align="left">
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <xsl:apply-templates select="PAGE-ENTRY"/>
      </table>
    </td>
  </tr>
</xsl:template>

<xsl:template match="PAGE-ENTRY">

  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="//SEARCH-ID"/>
  </xsl:variable>
  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="//CURR-PAGE-ID"/>
  </xsl:variable>
  <xsl:variable name="SELECTED-DB">
    <xsl:value-of select="//DBMASK"/>
  </xsl:variable>
  <xsl:variable name="DATABASE-ID">
    <xsl:value-of select="EI-DOCUMENT/DOC/DB/ID"/>
  </xsl:variable>
  <xsl:variable name="INDEX">
    <xsl:value-of select="NP-DOC/INDEX"/>
  </xsl:variable>
  <xsl:variable name="SOURCE">
    <xsl:value-of select="NP-DOC/SOURCE"/>
  </xsl:variable>
  <xsl:variable name="DOC-ID">
    <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
  </xsl:variable>
  <xsl:variable name="PATENT-NUM">
    <xsl:value-of select="EI-DOCUMENT/PM1"/>
  </xsl:variable>
  <xsl:variable name="CPX-DOCID">
    <xsl:value-of select="NP-DOC/CPX-DOCID"/>
  </xsl:variable>
  <xsl:variable name="INS-DOCID">
    <xsl:value-of select="NP-DOC/INS-DOCID"/>
  </xsl:variable>


  <tr>
    <td valign="top" align="left" width="3">
      <input type="hidden" name="HANDLE"><xsl:attribute name="value"><xsl:value-of select="$INDEX"/></xsl:attribute></input>
      <input type="hidden" name="DOC-ID"><xsl:attribute name="value"><xsl:value-of select="$DOC-ID"/></xsl:attribute></input>
      <xsl:variable name="IS-SELECTED"><xsl:value-of select="IS-SELECTED" disable-output-escaping="yes"/></xsl:variable>
      <xsl:variable name="CB-NO"><xsl:number/></xsl:variable>
    </td>
    <td valign="top" width="3"><img src="/engresources/images/s.gif" width="3"/></td>
    <td valign="top" width="3"><img src="/engresources/images/s.gif" width="3"/></td>
    <td valign="top"><img src="/engresources/images/s.gif" name="image_basket"/>
      <table>
        <tr>
          <td valign="top"><A CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</A> &nbsp;&nbsp;&nbsp;</td>
          <td valign="top"><A CLASS="MedBlackText"><b>Source:</b></A><xsl:text> </xsl:text> <a CLASS="MedBlackText"><xsl:value-of select="$SOURCE" disable-output-escaping="yes"/></a></td>
        </tr>
        <tr>
          <td valign="top">&nbsp;</td>
          <td>
            <!-- Harcoded database masks on these links -->
            <xsl:if test="($CPX-DOCID) and not($CPX-DOCID ='')">
              <A CLASS="LgBlueLink" HREF="/controller/servlet/Controller?CID=referenceDetailedFormat&amp;SEARCHID={$SEARCH-ID}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;database=1&amp;format=referenceDetailedFormat&amp;docid={$CPX-DOCID}&amp;searchnav={$ENCODED-RESULTS-NAV}">Compendex</A>&nbsp;&nbsp;
              <xsl:if test="($INS-DOCID) and not($INS-DOCID ='')">
                <A CLASS="MedBlackText"> - </A>&nbsp;&nbsp;
              </xsl:if>
            </xsl:if>
            <xsl:if test="($INS-DOCID) and not($INS-DOCID ='')">
              <A CLASS="LgBlueLink" HREF="/controller/servlet/Controller?CID=referenceDetailedFormat&amp;SEARCHID={$SEARCH-ID}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;database=2&amp;format=referenceDetailedFormat&amp;docid={$INS-DOCID}&amp;searchnav={$ENCODED-RESULTS-NAV}">Inspec</A>&nbsp;&nbsp;
            </xsl:if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</xsl:template>

</xsl:stylesheet>