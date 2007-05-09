<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLDecoder"
	exclude-result-prefixes="java html xsl">

<xsl:template name="P-GRAY-BAR">
 <!-- Start of navigation bar -->
    <!-- This could include a 'template' Navigation Bar with some modifications -->
    	<xsl:variable name="DECODED-RESULTS-NAV">
    	    <xsl:value-of select="java:decode(/PAGE/PAGE-NAV/RESULTS-NAV)"/>
    	</xsl:variable>
    	<xsl:variable name="DECODED-NEWSEARCH-NAV">
    		<xsl:value-of select="java:decode(/PAGE/PAGE-NAV/NEWSEARCH-NAV)"/>
    	</xsl:variable>
    	
    
    <center>
      <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
        <tr>
          <td align="left" valign="middle" height="24">
            <!-- jam (NTIS bug 44) changed link to test for SEARCHID if the 'Search Results' link is to be displayed
              on the Search History page.  It was checking results count. -->
            <xsl:if test="string($DECODED-RESULTS-NAV)">
            	&spcr8;
            	<a href="/controller/servlet/Controller?{$DECODED-RESULTS-NAV}"><img src="/engresources/images/sr.gif" border="0"/></a>
            	&spcr8;
            	<a href="/controller/servlet/Controller?{$DECODED-NEWSEARCH-NAV}"><img src="/engresources/images/ns.gif" border="0"/></a>
            </xsl:if>

          </td>
        </tr>
      </table>
    </center>

</xsl:template>
</xsl:stylesheet>