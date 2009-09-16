<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:ti="java:org.ei.query.base.HitHighlightFinisher"
  xmlns:book="java:org.ei.books.BookDocument"
  xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
  exclude-result-prefixes="java html xsl ti book custoptions">

  <xsl:strip-space elements="html:* xsl:*" />
  <xsl:param name="CUST-ID">0</xsl:param>

  <xsl:variable name="SELECTED-DB"><xsl:value-of select="//DATABASE"/></xsl:variable>
  <xsl:variable name="BOOKS_OPEN_WINDOW_PARAMS">height=800,width=700,status=yes,resizable,scrollbars=1,menubar=no</xsl:variable>

  <xsl:variable name="SEARCH-ID"><xsl:value-of select="//SEARCH-ID" /></xsl:variable>

  <xsl:variable name="CID"><xsl:value-of select="//PAGE/CID" /></xsl:variable>
  <xsl:variable name="TAG-SEARCH-FLAG">
    <xsl:value-of select="//TAG-SEARCH-FLAG" />
  </xsl:variable>
  <xsl:variable name="SCOPE">
    <xsl:value-of select="//SCOPE-REC" />
  </xsl:variable>
  <xsl:variable name="GROUP-ID">
    <xsl:value-of select="//GROUP-ID" />
  </xsl:variable>
  <xsl:variable name="DEFAULT-DB-MASK">
    <xsl:value-of select="//DEFAULT-DB-MASK"/>
  </xsl:variable>
  <xsl:variable name="CID-PREFIX">
    <xsl:choose>
      <xsl:when test="($CID='referenceAbstractFormat') or ($CID='referenceDetailedFormat')">reference</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus')">quickSearch</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">expertSearch</xsl:when>
      <xsl:otherwise>expertSearch</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!-- if using deduplication, then populate the link suffix else this variable will be empty -->
  <xsl:variable name="DEDUP-LINK-SUFFIX">
    <xsl:if test="/PAGE/DEDUP='true'">&amp;DupFlag=<xsl:value-of select="/PAGE/DEDUP"/>&amp;dbpref=<xsl:value-of select="/PAGE/DBPREF"/>&amp;fieldpref=<xsl:value-of select="/PAGE/FIELDPREF"/>&amp;origin=<xsl:value-of select="/PAGE/ORIGIN"/>&amp;dbLink=<xsl:value-of select="/PAGE/DBLINK"/>&amp;linkSet=<xsl:value-of select="/PAGE/LINKSET"/>&amp;dedupSet=<xsl:value-of select="/PAGE/DEDUPSET"/></xsl:if>
  </xsl:variable>

  <xsl:template match="ABSTRACT-DETAILED-RESULTS-MANAGER">

    <xsl:param name="FORMAT"/>
    <xsl:variable name="PERSONALIZATION">
      <xsl:value-of select="//PERSONALIZATION" />
    </xsl:variable>
    <xsl:variable name="BACKURL">
      <xsl:value-of select="/PAGE/BACKURL" />
    </xsl:variable>
    <!-- ZY:Added top fulltext image -->
    <xsl:variable name="FULLTEXT">
      <xsl:value-of select="//FULLTEXT" />
    </xsl:variable>
    <xsl:variable name="BLOGLINK">
      <xsl:value-of select="//BLOGLINK" />
    </xsl:variable>
    <xsl:variable name="FULLTEXT-LINK">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/FT/@FTLINK" />
    </xsl:variable>
    <xsl:variable name="DOC-ID">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC/DOC-ID" />
    </xsl:variable>
    <xsl:variable name="RESULTS-PER-PAGE">
      <xsl:value-of select="//RESULTS-PER-PAGE" />
    </xsl:variable>
    <xsl:variable name="DATABASE">
      <xsl:value-of select="//SESSION-DATA/DATABASE-MASK" />
    </xsl:variable>
    <xsl:variable name="ENCODED-DATABASE">
      <xsl:value-of select="java:encode($DATABASE)" />
    </xsl:variable>
    <xsl:variable name="DATABASE-ID">
      <xsl:value-of select="//EI-DOCUMENT/DOC/DB/ID" />
    </xsl:variable>
    <xsl:variable name="TITLE1">
      <xsl:value-of select="java:encode(ti:removeMarkup(//EI-DOCUMENT/TI))" />
    </xsl:variable>
    <xsl:variable name="TITLE2">
      <xsl:value-of select="java:encode(ti:removeMarkup(//FIELD[@ID='TI']/VALUE))" />
    </xsl:variable>
    <xsl:variable name="TITLE">
      <xsl:if test="not($TITLE1='null')">
        <xsl:value-of select="$TITLE1" />
      </xsl:if>
      <xsl:if test="not($TITLE2='null')">
        <xsl:value-of select="$TITLE2" />
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="RESULTS-COUNT">
      <xsl:value-of select="//RESULTS-COUNT" />
    </xsl:variable>
    <xsl:variable name="CURRENT-PAGE">
      <xsl:value-of select="//PAGE-INDEX" />
    </xsl:variable>

    <xsl:variable name="INDEX">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC/HITINDEX" />
    </xsl:variable>

    <!-- reference counts links-->
    <xsl:variable name="NONREF-CNT">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/NPRCT" />
    </xsl:variable>
    <xsl:variable name="CIT-CNT">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/CCT" />
    </xsl:variable>
    <xsl:variable name="REF-CNT">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/RCT" />
    </xsl:variable>

    <!-- end pat links -->
    <xsl:variable name="AUTH-CODE">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/AUTHCD" />
    </xsl:variable>
    <xsl:variable name="PAT-NUM">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/PAP" />
    </xsl:variable>
    <xsl:variable name="KIND">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/KC" />
    </xsl:variable>

<!-- "&amp;pcinav=0~<patent#>~Patents that cite <patent#>" encoded into links -->
    <xsl:variable name="CITEDBY-PM">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/PM"/>
    </xsl:variable>

<!-- ZY 9/15/09 Append patent kind in searchword1 for EP patents -->
    <xsl:variable name="CITEDBY-QSTR">
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Quick')">CID=quickSearchCitationFormat&amp;searchWord1=<xsl:value-of select="$CITEDBY-PM"/><xsl:if test="$AUTH-CODE='EP'"><xsl:value-of select="$KIND"/></xsl:if>&amp;section1=PCI&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:when>
        <xsl:otherwise>CID=expertSearchCitationFormat&amp;searchWord1=<xsl:value-of select="$CITEDBY-PM"/><xsl:if test="$AUTH-CODE='EP'"><xsl:value-of select="$KIND"/></xsl:if><xsl:value-of select="java:encode(' WN PCI')"/>&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="NEXTURL">CID=viewSavedFolders&amp;docid=<xsl:value-of select="$DOC-ID" />&amp;database=<xsl:value-of select="$ENCODED-DATABASE" /></xsl:variable>

    <!-- Start of table for resultsmanager. -->
    <center>
      <table border="0" cellspacing="0" cellpadding="0" width="99%" >
        <tr>
          <td colspan="2">
            <img src="/engresources/images/s.gif" border="0" height="10" />
          </td>
        </tr>
        <tr>
          <td width="50%" align="left">
            <xsl:if test="not((/PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC/DB/DBMASK= '131072'))">
              <a CLASS="BrownText">
                <xsl:attribute name="TITLE">Abstract</xsl:attribute>
                <xsl:if test="not($FORMAT = 'ABSTRACT')">
                  <xsl:attribute name="HREF">/controller/servlet/Controller?<xsl:value-of select="/PAGE/PAGE-NAV/ABS-NAV"/></xsl:attribute>
                  <xsl:attribute name="class">LgBlueLink</xsl:attribute>
                </xsl:if>Abstract</a>

              <a CLASS="MedBlackText">&#160; - &#160;</a>
              <a CLASS="BrownText">
                <xsl:attribute name="TITLE">Detailed</xsl:attribute>
                <xsl:if test="not($FORMAT = 'DETAILED')">
                  <xsl:attribute name="HREF">/controller/servlet/Controller?<xsl:value-of select="/PAGE/PAGE-NAV/DET-NAV"/></xsl:attribute>
                  <xsl:attribute name="class">LgBlueLink</xsl:attribute>
                </xsl:if>Detailed</a>
            </xsl:if>

            <xsl:if test="(/PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC/DB/DBMASK = '131072')">

              <!-- jam: Page Details (SAME AS Detailed link) NOT LINKED  -->
              <xsl:if test="not(EI-DOCUMENT/BPP = '0')">
                <a class="BrownText">Page Details</a>
                <a class="MedBlackText">&#160; - &#160;</a>
              </xsl:if>

              <a class="LgBlueLink">
                <xsl:attribute name="TITLE">Book Details</xsl:attribute>
                <xsl:attribute name="HREF">/controller/servlet/Controller?<xsl:value-of select="/PAGE/PAGE-NAV/BOOKDET-NAV"/></xsl:attribute>Book Details</a>

              <xsl:if test="not(/PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BPP = '0')">
                <a class="MedBlackText">&#160; - &#160;</a>
                <a class="LgBlueLink">
                  <xsl:attribute name="TITLE">Read Page</xsl:attribute>
                  <xsl:attribute name="HREF">javascript:_referex=window.open('/controller/servlet/Controller?CID=bookFrameset&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;docid=<xsl:value-of select="$DOC-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/>','_referex','<xsl:value-of select="$BOOKS_OPEN_WINDOW_PARAMS"/>');_referex.focus();void('');</xsl:attribute>
                  <img alt="Read Page" src="/engresources/images/read_page.gif" style="border:0px; vertical-align:middle"/>
                  </a>
              </xsl:if>

              <xsl:if test="string(/PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/PII)">
                <a class="MedBlackText">&#160; - &#160;</a>
                <a class="LgBlueLink">
                  <xsl:attribute name="target">_referex</xsl:attribute>
                  <xsl:attribute name="TITLE">Read Chapter</xsl:attribute>
                  <xsl:attribute name="HREF"><xsl:value-of select="book:getReadChapterLink(/PAGE/PAGE-RESULTS/PAGE-ENTRY/WOBLSERVER,/PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN13,/PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/PII, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                  <img alt="Read Chapter" src="/engresources/images/read_chp.gif" style="border:0px; vertical-align:middle"/>
                  </a>
              </xsl:if>

                <a class="MedBlackText">&#160; - &#160;</a>
                <a class="LgBlueLink">
                  <xsl:attribute name="target">_referex</xsl:attribute>
                  <xsl:attribute name="TITLE">Read Book</xsl:attribute>
                  <xsl:attribute name="HREF"><xsl:value-of select="book:getReadBookLink(/PAGE/PAGE-RESULTS/PAGE-ENTRY/WOBLSERVER,/PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN13, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                  <img alt="Read Book" src="/engresources/images/read_book.gif" style="border:0px; vertical-align:middle"/>
                  </a>
            </xsl:if>

            <!-- there will always be Abs. & Det. text/links preceding these links -->
            <!-- so each will be prefixed with a spacer -->
              <xsl:if test="($REF-CNT) and not($REF-CNT ='0') and not($REF-CNT ='')">
                <A class="MedBlackText">&#160; - &#160;</A>

                <xsl:variable name="PATENT-REFS-LINK">
                  <xsl:value-of select="/PAGE/PAGE-NAV/PATREF-NAV" />
                </xsl:variable>
                <a title="Show patents that are referenced by this patent" class="LgBlueLink" href="/controller/servlet/Controller?{$PATENT-REFS-LINK}">Patent Refs</a>&#160;<a class="MedBlackText">(<xsl:value-of select="$REF-CNT" />)</a>
              </xsl:if>
              <xsl:if test="($NONREF-CNT) and not($NONREF-CNT ='0') and not($NONREF-CNT ='')">
              <xsl:variable name="NONPATENT-REFS-LINK">
                <xsl:value-of select="/PAGE/PAGE-NAV/NONPATREF-NAV" />
              </xsl:variable>

                <A class="MedBlackText">&#160; - &#160;</A>
                <a class="LgBlueLink" href="/controller/servlet/Controller?{$NONPATENT-REFS-LINK}">Other Refs</a>&#160;<a class="MedBlackText">(<xsl:value-of select="$NONREF-CNT" />)</a>
              </xsl:if>
              <xsl:if test="($CIT-CNT) and not($CIT-CNT ='0') and not($CIT-CNT ='')">
                <A class="MedBlackText">&#160; - &#160;</A>
                <a title="Show patents that reference this patent" class="LgBlueLink" href="/controller/servlet/Controller?{$CITEDBY-QSTR}&amp;yearselect=yearrange&amp;searchtype={$SEARCH-TYPE}&amp;sort=yr">Cited by</a>&#160;<a class="MedBlackText">(<xsl:value-of select="$CIT-CNT" />)</a>
              </xsl:if>

	      <xsl:variable name="CHECK-CUSTOM-OPT">
	        <xsl:value-of select="custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, //PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DO , //PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC/DB/DBMASK)" />
	      </xsl:variable>

              <!-- there will always be Abs. & Det. text/links preceding this image -->
              <xsl:if test="($CHECK-CUSTOM-OPT ='true')">
                <a class="MedBlackText">&#160; - &#160;</a>
                <a title="Full-text" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false">
                  <img id="ftimg" src="/engresources/images/av.gif" alt="Full-text"/></a>
              </xsl:if>
          </td>
          <td width="50%" align="right">
            <xsl:if test="($BLOGLINK='true')">
              <a class="MedBlueLink" onClick="javascript:window.open('/controller/servlet/Controller?CID=openBlog&amp;MID={$DOC-ID}&amp;DATABASE={$DATABASE-ID}&amp;TITLE={$TITLE}')">
              <img style="vertical-align: middle;" src="/engresources/images/blog.gif" border="0" /></a>&#160; &#160;
            </xsl:if>
            <a href="javascript: emailFormat('$SESSIONID','{$SEARCH-TYPE}','{$SEARCH-ID}','{$ENCODED-DATABASE}','{$DATABASE-ID}');void('');">
              <img style="vertical-align: middle;" src="/engresources/images/em.gif" border="0" /></a>&#160; &#160;
            <a href="javascript:printFormat('$SESSIONID','{$SEARCH-TYPE}','{$SEARCH-ID}','{$ENCODED-DATABASE}','{$DATABASE-ID}')">
              <img style="vertical-align: middle;" src="/engresources/images/pr.gif" border="0" /></a>&#160; &#160;
            <a href="javascript:downloadFormat('$SESSIONID','{$SEARCH-TYPE}','{$SEARCH-ID}','{$ENCODED-DATABASE}','{$DATABASE-ID}')">
              <img style="vertical-align: middle;" src="/engresources/images/dw.gif" border="0" /></a>&#160; &#160;
            <a ttile="Save to Folder">
              <xsl:attribute name="href">
                <xsl:choose>
                  <xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=viewSavedFolders&amp;database=<xsl:value-of select="$ENCODED-DATABASE" />&amp;backurl=<xsl:value-of select="$BACKURL" />&amp;docid=<xsl:value-of select="$DOC-ID" /></xsl:when>
                  <xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID" />&amp;count=<xsl:value-of select="$CURRENT-PAGE" />&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE" />&amp;displaylogin=true&amp;database=<xsl:value-of select="$ENCODED-DATABASE" />&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)" />&amp;backurl=<xsl:value-of select="$BACKURL" /></xsl:otherwise>
                </xsl:choose>
              </xsl:attribute><img style="vertical-align: middle;" src="/engresources/images/sv.gif" border="0" /></a>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <img src="/engresources/images/s.gif" border="0" height="10" />
          </td>
        </tr>
      </table>
    </center>
    <!-- End of table for results manager -->
  </xsl:template>

</xsl:stylesheet>
