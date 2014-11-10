<?xml version="1.0" ?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLDecoder"
    exclude-result-prefixes="html xsl java"
>
  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>

  <xsl:variable name="HREF-PREFIX"/>

  <xsl:template match="PAGE">

    <HTML>
    <HEAD>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
				<link href="/engresources/stylesheets/booktoc_v92.css" rel="stylesheet" type="text/css" />

        <script language="JavaScript">
        <xsl:comment>
        function loadFromToc(isbn,page)
        {
          isbn = "<xsl:value-of select="descendant::EI-DOCUMENT/BN13"/>";
          if(parent)
          {
  		      parent.bookNav.refreshPbookPage(isbn.toLowerCase(),page);
  		    }

		      return;
        }
        // </xsl:comment>
        </script>
    </HEAD>
    <BODY>
    <div style="margin:0;padding:0;background-color: c2c81d;">
    <img src="/engresources/images/toc.gif" border="0" width="655" height="36" alt="Table of Contents" />
    </div>
    <br/>
    <div style="margin-left:30px;" id="toc">
    <xsl:value-of disable-output-escaping="yes" select="TOC"/>
    </div>

    </BODY>
    </HTML>
  </xsl:template>

</xsl:stylesheet>