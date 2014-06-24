<?xml version="1.0"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="html"
>

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>

<xsl:include href="Footer.xsl"/>

<xsl:template match="root">
  <html>
  <head>
    <title>About Engineering Information</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
  </head>

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
  <!-- Start of logo table -->
  <table border="0" width="99%" cellspacing="0" cellpadding="0">
    <tr><td valign="top">
    <a href="/controller/servlet/Controller?CID=home">
    <img src="/static/images/ev2logo5.gif" border="0"/>
    </a>
    </td></tr>
    <tr><td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
    <tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" height="2"/></td></tr>
    <tr><td valign="top" height="20"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>
  </table>
  <!-- End of logo table -->


  <!-- table for contents -->
    <center>
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <tr><td valign="top"><a class="EvHeaderText">About Elsevier Engineering Information</a></td></tr>
        <tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
        <tr>
          <td valign="top">
          <a class="MedBlackText">Elsevier Engineering Information is the leader in providing online information, knowledge, and support of the highest professional relevance for research and industrial practitioners in applied physical sciences and engineering.  Elsevier Engineering Information offers an array of products and services for universities, corporations and government organizations, including: Engineering Village &#153;, EnCompass&#153;, Compendex&#174;, The Engineering Index&#174;, Chemical Business NewsBase&#174;, Chimica&#153; and PaperChem&#153;.<br/>
          <br/>
          For more information about Elsevier Engineering Information, please visit our Web site at </a><a class="LgBLueLink" HREF="http://www.ei.org" onClick="window.open('http://www.ei.org', 'newpg', 'status=yes,resizable,scrollbars=yes,menubar=yes,location=yes,directories=yes,width=700,height=450'); return false;">http://www.ei.org</a>
          <P>
          <a class="LgBlueLink" href="/controller/servlet/Controller?CID=home">Back to Login Page</a>
          </P>
          </td>
        </tr>
      </table>
    </center>
  <br/>

  <!-- end of the lower area below the navigation bar -->
  <xsl:apply-templates select="FOOTER"/>


  </body>
  </html>
</xsl:template>

</xsl:stylesheet>