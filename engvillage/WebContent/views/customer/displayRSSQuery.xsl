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

<xsl:include href="./HeaderNull.xsl"/>

<xsl:template match="ROOT">

  <xsl:variable name="EXCEPTION">
    <xsl:value-of select="//EXCEPTION"/>
  </xsl:variable>

  <xsl:variable name="FEEDURL">http://<xsl:value-of select="./SERVERNAME"/>/controller/servlet/Controller?CID=openRSS&amp;SYSTEM_PT=t&amp;queryID=<xsl:value-of select="./QUERYID"/></xsl:variable>
  <xsl:variable name="ENCODED-FEEDURL"><xsl:value-of select="java:encode($FEEDURL)"/></xsl:variable>

  <html>
    <head>
      <title>Engineering Village RSS Feed</title>
    <link type="image/x-icon" href="/static/images/engineering_village_favicon.gif" rel="SHORTCUT ICON"></link>
	  <script type="text/javascript" language="javascript" src="/static/js/StylesheetLinks.js"></script>
	  <link rel="stylesheet" type="text/css" media="all" href="/static/css/ev_common_sciverse.css"/>
      <link rel="alternate" type="application/rss+xml" title="Engineering Village RSS results for search query of {TERM1}" href="{$FEEDURL}" />
      <style>table {padding-left: 15px}</style>
    </head>

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
	<xsl:call-template name="HEADERNULL"/>
    <center>

      <p/>

      <table width="99%" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td height="20"><img src="/static/images/s.gif" height="20"/></td>
        </tr>
          <xsl:choose>
            <xsl:when test="string-length($EXCEPTION)>0" >
              <tr>
                <td><span class="MedBlackText">Exception: <xsl:value-of select="$EXCEPTION"/></span></td>
              </tr>
            </xsl:when>
            <xsl:otherwise>
              <tr>
                <td><span class="MedBlackText"><xsl:value-of select="$FEEDURL"/></span></td>
              </tr>
            </xsl:otherwise>
          </xsl:choose>
        <tr>
          <td height="25"><img src="/static/images/s.gif" height="25" width="1"/></td>
        </tr>
      </table>

      <p/>

      <table width="99%" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td valign="top"><span class="MedBlackText">Copy and paste the link to your RSS reader. Each week when the database is updated any new results matching your query will be displayed in your RSS reader. Up to 400 titles will be delivered with each update. <br/><br/>For more information on RSS see the Help section.</span></td>
        </tr>
        <tr>
          <td height="25"><img src="/static/images/s.gif" height="25" width="1"/></td>
        </tr>
        <tr>
          <td valign="top"><span class="MedBlackText">If you use a common RSS reader, you may click your choice below to subscribe.</span></td>
        </tr>
        <tr>
          <td height="25"><img src="/static/images/s.gif" height="25" width="1"/></td>
        </tr>
        <tr>
          <td valign="top">
              <a href='http://cloud.feedly.com/#subscription%2Ffeed%2F{$ENCODED-FEEDURL}'  target='blank'><img id='feedlyFollow' src='http://s3.feedly.com/img/follows/feedly-follow-rectangle-volume-medium_2x.png' alt='follow us in feedly' width='71' height='28'/></a>
            &#160;&#160;
              <a target="_blank" href="http://add.my.yahoo.com/content?.intl=us&amp;url={$ENCODED-FEEDURL}"><img src="http://us.i1.yimg.com/us.yimg.com/i/us/my/addtomyyahoo4.gif" width="91" height="17" border="0" alt="Add to My Yahoo!"/></a>
            &#160;&#160;
              <a target="_blank" href="http://www.bloglines.com/sub/{$FEEDURL}"><img src="http://www.bloglines.com/images/sub_modern5.gif" alt="Subscribe with Bloglines" border="0"/></a>
            &#160;&#160;
          </td>
        </tr>
      </table>
    </center>

  </body>
  </html>

</xsl:template>

</xsl:stylesheet>

