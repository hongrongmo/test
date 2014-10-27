<?xml version="1.0" ?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLDecoder"
    exclude-result-prefixes="html xsl java"
>
  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  <xsl:strip-space elements="html:* xsl:*" />

  <xsl:template match="PAGE">
    <!-- <xsl:message>Highlight terms = <xsl:value-of select="HILITE"/></xsl:message> -->

    <HTML>
    <HEAD>
        <TITLE>Referex Engineering Village</TITLE>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>

        <script language="Javascript">
        <xsl:comment>
            var chapterStartPage = <xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BSPG"/>;
            var currPage = 1;
            var annotated = true;
            var acrotoolbar = true;
            function setCurrPage(pagenum)
            {
              currPage = pagenum * 1;
            }
            function nextPage(isbn)
            {
              refreshPbookPage(isbn,currPage + 1);
            }
            function prevPage(isbn)
            {
              refreshPbookPage(isbn,currPage - 1);
            }
            function refreshPbookPageFromTOC(isbn, pagenum) {
              chapterStartPage = pagenum;
              return refreshPbookPage(isbn, pagenum);
            }

            function refreshPbookPage(isbn, pagenum)
            {
              isbn = "<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN13"/>";
              if((pagenum == null) || (pagenum.length == 0))
              {
                pagenum = 1;
              }
              pagenum = pagenum * 1;
              if((pagenum &lt;= 0 ) || (pagenum &gt; <xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BPC"/>))
              {
                return false;
              }

              currPage = pagenum;
              if(currPage == 0)
              {
                pagenum = 1;
                currPage = pagenum;
              }

              var strlocation= "<xsl:value-of select="DOCVIEW"/>/" + isbn + "/pg_" + padZeros(pagenum,4) + ".pdf"
              var now = new Date() ;
              var milli = now.getTime();
              strlocation += "?t=" + milli; /* Create unqiue URL to prevent Acrobat Reader from caching */
              if(annotated)
              {
                strlocation += "#xml=<xsl:value-of select="DOCVIEW"/>/" + isbn + "/pg_" + padZeros(pagenum,4) + ".pdf.offsetinfo?"
                strlocation += "hilite=<xsl:value-of select="HILITE"/>&amp;original_content_type=application%2Fpdf";
                strlocation += "&amp;toolbar=" + ((acrotoolbar) ? "1" : "0") + "&amp;statusbar=0&amp;messages=0&amp;navpanes=0";
              }
              else
              {
                strlocation += "#toolbar=" + ((acrotoolbar) ? "1" : "0") + "&amp;statusbar=0&amp;messages=0&amp;navpanes=0";
              }

              window.parent.frames["bookPage"].location = strlocation;
              refreshPageBox();

            }
            function toggleToolbar()
            {
              acrotoolbar = !acrotoolbar;
              refreshPbookPage('<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN13"/>',currPage);
            }
            function toggleAnnotations()
            {
              annotated = !annotated;
              refreshPbookPage('<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN13"/>',currPage);
              if(annotated)
              {
                document.images["highlights"].src = "/static/images/t_on.jpg";
              }
              else
              {
                document.images["highlights"].src = "/static/images/t_off.jpg";
              }
            }
            function refreshPageBox()
            {
              this.document.forms["pagesel"].page.value = currPage; // + " of <xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BPC"/>";
            }

            function padZeros(num, totalLen) {
               var numStr = num.toString();
               var numZeros = totalLen - numStr.length;
               if (numZeros &gt; 0)
               {
                  for (var i = 1; i &lt;= numZeros; i++)
                  {
                     numStr = "0" + numStr;
                  }
               }
               return numStr;
            }

            function closeAndReturn()
            {
              if(confirm("Close window and return to Book description"))
              {
                if(this.window.parent.opener)
                {
                  this.window.parent.opener.location='/controller/servlet/Controller?<xsl:value-of select="java:decode(TOCURL)"/>AbstractFormat';
                  this.window.parent.close();
                }
              }
            }

            function manualPbookPageSelect()
            {
              var pagenum = document.forms["pagesel"].page.value;
              var isbn = document.forms["pagesel"].isbn.value;

              if(pagenum != parseInt(pagenum))
              {
                alert("Please enter a valid page number.");
                refreshPageBox();
                return false;
              }

              if((pagenum &lt;= 0 ) || (pagenum &gt; <xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BPC"/>))
              {
                alert("Please enter a page number between 1 and <xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BPC"/>.");
                refreshPageBox();
                return false;
              }

              refreshPbookPage(isbn, pagenum);
              return false;
            }

        // </xsl:comment>
        </script>
        <style>
        div {margin:0;padding:0; border:0px solid black;}
        .button
        {
          margin-left:4px;
          margin-right:4px;
          vertical-align:middle;
          width:28px;
          height:28px;
          border:0;
        }
        </style>
    </HEAD>
    <BODY style="padding: 2 5 0 5;">
      <TABLE style="margin:0px;padding:0px;width:99%; border:0px solid black;">
      <TBODY>
        <TR>
          <TD width="55%">
            <xsl:apply-templates select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT" mode="BookNav"/>
            <br/>
          </TD>
          <TD width="45%">
            <form style="margin:0;padding:0;text-align:right;" name="pagesel" onsubmit="javascript:return manualPbookPageSelect();" method="POST">
              <input name="isbn" type="hidden">
                <xsl:attribute name="value"><xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN"/></xsl:attribute>
              </input>
              <a>
                <xsl:attribute name="Title">First Page</xsl:attribute>
                <xsl:attribute name="HREF">javascript:refreshPbookPage('<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN"/>','1');</xsl:attribute>
                <img class="button" src="/static/images/bk_fp.gif" alt="First Page"/>
              </a>
              <a id="pp">
                <xsl:attribute name="Title">Previous Page</xsl:attribute>
                <xsl:attribute name="HREF">javascript:prevPage('<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN"/>');</xsl:attribute>
                <img class="button" src="/static/images/bk_pp.gif" alt="Previous Page"/>
              </a>
              <input style="vertical-align:middle;text-align:center;" name="page" size="3" type="text" maxlength="4"/>
              <a id="np">
                <xsl:attribute name="Title">Next Page</xsl:attribute>
                <xsl:attribute name="HREF">javascript:nextPage('<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN"/>');</xsl:attribute>
                <img class="button" src="/static/images/bk_np.gif" alt="Next Page"/>
              </a>
              <a>
                <xsl:attribute name="Title">Last Page</xsl:attribute>
                <xsl:attribute name="HREF">javascript:refreshPbookPage('<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN"/>','<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BPC"/>');</xsl:attribute>
                <img class="button" src="/static/images/bk_lp.gif" alt="Last Page"/>
              </a>
            </form>
            <div style="padding-top:3px; clear:both;text-align:right;">
              <a class="SmBlueLink">
                <xsl:attribute name="Target">bookPage</xsl:attribute>
                <xsl:attribute name="Title">Table of Contents</xsl:attribute>
                <xsl:attribute name="HREF">/controller/servlet/Controller?<xsl:value-of select="java:decode(TOCURL)"/></xsl:attribute>Table of Contents</a>&#160;&#160;
              <a class="SmBlueLink">
                <xsl:attribute name="Title">Start Chapter</xsl:attribute>
                <xsl:attribute name="HREF">javascript:refreshPbookPage('<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BN"/>','<xsl:value-of select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/BSPG"/>');</xsl:attribute>Start Chapter</a>&#160;&#160;
              <a class="SmBlueLink">
                <xsl:attribute name="Title">Toggle Highlights</xsl:attribute>
                <xsl:attribute name="HREF">javascript:toggleAnnotations();</xsl:attribute><img name="highlights" class="button" src="/static/images/t_on.jpg" alt="Toggle Highlights"/>
              </a>
            </div>
          </TD>
        </TR>
      </TBODY>
      </TABLE>
      <SCRIPT language="JavaScript">
        <xsl:comment>
        {
          setCurrPage('<xsl:value-of select="CURR-PAGE"/>');
          refreshPageBox();
        }
        // </xsl:comment>
      </SCRIPT>
    </BODY>
    </HTML>
  </xsl:template>



    
    
  <!-- override EI-DOCUMENT templates in standard Citation XSL -->
  <xsl:template match="EI-DOCUMENT" mode="BookNav">
    <img style="border:1px solid black; float:left; margin-right:10px" width="56" height="69">
      <xsl:attribute name="src"><xsl:value-of select="//BOOKIMGS-URL"/></xsl:attribute>
      <xsl:attribute name="alt">
        <xsl:value-of select="./BTI"/>
      </xsl:attribute>
    </img>
    <span class="SmBlackText">
      <xsl:apply-templates select="BTI" mode="BookNav"/>
      <br/>
      <xsl:apply-templates select="AUS" mode="BookNav"/>
      <xsl:apply-templates select="EDS" mode="BookNav"/>
      <br/>
      <xsl:apply-templates select="BN13" mode="BookNav"/>
      <xsl:apply-templates select="BPC" mode="BookNav"/>
      <br/>
      <xsl:apply-templates select="BPN" mode="BookNav"/>
      <xsl:apply-templates select="BYR" mode="BookNav"/>
    </span>
  </xsl:template>

  <xsl:template match="BTI" mode="BookNav">
    <b><xsl:value-of select="." disable-output-escaping="yes"/></b>
  </xsl:template>

  <!-- Book ISBN  -->
  <xsl:template match="BN|BN13" mode="BookNav"><xsl:value-of select="@label"/>: <xsl:value-of select="." disable-output-escaping="yes"/></xsl:template>

  <!-- Book Total PAge Count -->
  <xsl:template match="BPC" mode="BookNav">, <xsl:value-of select="." disable-output-escaping="yes"/>&#160;pp</xsl:template>

  <xsl:template match="BPN" mode="BookNav"><xsl:value-of select="." disable-output-escaping="yes"/>,&#160;</xsl:template>

  <xsl:template match="BYR" mode="BookNav"><xsl:value-of select="." disable-output-escaping="yes"/></xsl:template>


  <!-- override Author/Editor templates in standard Citation XSL -->
  <xsl:template match="AUS|EDS" mode="BookNav">
    <xsl:apply-templates mode="BookNav"/>
  </xsl:template>

  <!-- override Author/Editor templates in standard Citation XSL -->
  <xsl:template match="AU|ED" mode="BookNav">
    <xsl:value-of select="normalize-space(text())" disable-output-escaping="yes"/>
    <xsl:if test="not(position()=last())">;&#160;</xsl:if>
    <xsl:if test="position()=last()">
      <xsl:if test="name(.)='ED' and not(position()=1)">
        <xsl:text> eds.</xsl:text>
      </xsl:if>
      &#160;
    </xsl:if>
  </xsl:template>

  <xsl:template match="PDFARGS">
    <xsl:value-of disable-output-escaping="yes" select="text()"/>
  </xsl:template>

</xsl:stylesheet>
