<?xml version="1.0"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  exclude-result-prefixes="html xsl java"
  >

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template match="ROOT">

  <xsl:variable name="EXCEPTION">
    <xsl:value-of select="//EXCEPTION"/>
  </xsl:variable>

  <xsl:variable name="FEEDURL">http://<xsl:value-of select="./SERVERNAME"/>/controller/servlet/Controller?CID=openRSS&amp;SYSTEM_PT=t&amp;queryID=<xsl:value-of select="./QUERYID"/></xsl:variable>
  <xsl:variable name="ENCODED-FEEDURL"><xsl:value-of select="java:encode($FEEDURL)"/></xsl:variable>

  <html>
    <head>
      <title>Engineering Village RSS Feed</title>
      <script type="text/javascript" language="javascript" src="/engresources/js/StylesheetLinks.js"></script>
      <link rel="alternate" type="application/rss+xml" title="Engineering Village RSS results for search query of {TERM1}" href="{$FEEDURL}" />
    </head>

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
    <center>

      <table width="99%" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td>
          <td align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td>
        </tr>
        <tr>
          <td height="2" colspan="2"><img src="/engresources/images/s.gif" border="0" height="2"/></td>
        </tr>
        <tr>
          <td height="2" bgcolor="#3173B5" colspan="2"><img src="/engresources/images/s.gif" border="0" height="2"/></td>
        </tr>
      </table>

      <p/>

      <table width="99%" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td height="20"><img src="/engresources/images/s.gif" height="20"/></td>
        </tr>
          <xsl:choose>
            <xsl:when test="string-length($EXCEPTION)>0" >
              <tr>
                <td><a class="MedBlackText">Exception: <xsl:value-of select="$EXCEPTION"/></a></td>
              </tr>
            </xsl:when>
            <xsl:otherwise>
              <tr>
                <td><a class="MedBlackText"><xsl:value-of select="$FEEDURL"/></a></td>
              </tr>
            </xsl:otherwise>
          </xsl:choose>
        <tr>
          <td height="25"><img src="/engresources/images/s.gif" height="25" width="1"/></td>
        </tr>
      </table>

      <p/>

      <table width="99%" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td valign="top"><a class="BlueText">Copy and paste the link to your RSS reader. Each week when the database is updated any new results matching your query will be displayed in your RSS reader. Up to 400 titles will be delivered with each update. <br/><br/>For more information on RSS see the Help section.</a></td>
        </tr>
        <tr>
          <td height="25"><img src="/engresources/images/s.gif" height="25" width="1"/></td>
        </tr>
        <tr>
          <td valign="top"><a class="BlueText">If you use a common RSS reader, you may click your choice below to subscribe.</a></td>
        </tr>
        <tr>
          <td height="25"><img src="/engresources/images/s.gif" height="25" width="1"/></td>
        </tr>
        <tr>
          <td valign="top">
              <a target="_blank" href="http://add.my.yahoo.com/content?.intl=us&amp;url={$ENCODED-FEEDURL}"><img src="http://us.i1.yimg.com/us.yimg.com/i/us/my/addtomyyahoo4.gif" width="91" height="17" border="0" alt="Add to My Yahoo!"/></a>
            &#160;&#160;
              <a target="_blank" href="http://www.newsgator.com/ngs/subscriber/subext.aspx?url={$ENCODED-FEEDURL}"><img src="http://www.newsgator.com/images/ngsub1.gif" alt="Subscribe in NewsGator Online" border="0"/></a>
            &#160;&#160;
              <a target="_blank" href="http://www.bloglines.com/sub/{$FEEDURL}"><img src="http://www.bloglines.com/images/sub_modern5.gif" alt="Subscribe with Bloglines" border="0"/></a>
            &#160;&#160;
<!--              <a target="_blank" href="http://www.rojo.com/add-subscription?resource={$ENCODED-FEEDURL}"><img src="http://www.rojo.com/skins/static/images/add-to-rojo.gif" alt="Subscribe with Rojo" border="0"/></a>
            &#160;&#160; -->
              <a target="_blank" href="http://fusion.google.com/add?feedurl={$ENCODED-FEEDURL}"><img src="http://buttons.googlesyndication.com/fusion/add.gif" alt="Add to Google" width="104" height="17" border="0"/></a>
          </td>
        </tr>
      </table>
    </center>

  </body>
  </html>

</xsl:template>

</xsl:stylesheet>

