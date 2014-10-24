<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:gui="java:org.ei.gui.PagingComponents"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  exclude-result-prefixes="gui java html xsl xslcid"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="xsl:*" />

<xsl:template match="TAGS-NAVIGATION-BAR">
  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="/PAGE/SEARCH-ID"/>
  </xsl:variable>
  <xsl:variable name="ENCODED-SEARCH-ID">
      <xsl:value-of select="java:encode($SEARCH-ID)"/>
  </xsl:variable>
  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="/PAGE/CURR-PAGE-ID"/>
  </xsl:variable>
  <xsl:variable name="PREV-PAGE">
    <xsl:value-of select="/PAGE/PREV-PAGE-ID"/>
  </xsl:variable>
  <xsl:variable name="NEXT-PAGE">
    <xsl:value-of select="/PAGE/NEXT-PAGE-ID"/>
  </xsl:variable>  
  <xsl:variable name="TAGSET">
    <xsl:value-of select="//TAGSET"/>
  </xsl:variable>
  <xsl:variable name="DATABASE">
    <xsl:value-of select="/PAGE/DBMASK"/>
  </xsl:variable>
  <xsl:variable name="SCOPE">
    <xsl:value-of select="/PAGE/SCOPE"/>
  </xsl:variable>

    <!-- Table to display the previous, next buttons and drop down box -->
    <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
      <tr>
        <td align="left" valign="middle" height="24">
          
              <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
              <a>
                    <xsl:attribute name="href">/controller/servlet/Controller?CID=tagsLoginForm&amp;database=<xsl:value-of select="/PAGE/DBMASK"/></xsl:attribute>
                    <img src="/engresources/images/ns.gif" border="0" />
              </a>
        </td>
      	 <form name="selectresults" method="post" action="/controller/servlet/Controller">
         <INPUT TYPE="HIDDEN" name="CID" value="tagSearch"/>
         <INPUT TYPE="HIDDEN" name="SEARCHID"><xsl:attribute name="value"><xsl:value-of select="$SEARCH-ID"/></xsl:attribute></INPUT>
         <INPUT TYPE="HIDDEN" name="database"><xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute></INPUT>
         <INPUT TYPE="HIDDEN" name="scope"><xsl:attribute name="value"><xsl:value-of select="$SCOPE"/></xsl:attribute></INPUT>
         <td align="right" valign="middle" height="24">
           <table cellspacing="0" cellpadding="0" border="0">
             <tr>
               <td align="right" valign="middle" height="24">
                 <xsl:if test="string($PREV-PAGE)">
                   <a><xsl:attribute name="href">/controller/servlet/Controller?CID=tagSearch&amp;SEARCHID=<xsl:value-of select="$ENCODED-SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$PREV-PAGE"/>&amp;scope=<xsl:value-of select="$SCOPE"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:attribute>
                   <img src="/engresources/images/pp.gif" border="0" />
                   </a>
                 </xsl:if>
                 <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
                 <xsl:if test="string($NEXT-PAGE)">
                   <a><xsl:attribute name="href">/controller/servlet/Controller?CID=tagSearch&amp;SEARCHID=<xsl:value-of select="$ENCODED-SEARCH-ID"/>&amp;COUNT=<xsl:value-of select="$NEXT-PAGE"/>&amp;scope=<xsl:value-of select="$SCOPE"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:attribute>
                   <img src="/engresources/images/np.gif" border="0" />
                   </a>
                 </xsl:if>
                 <img width="16" height="1" src="/engresources/images/s.gif" border="0" />
               </td>
               <td align="right" valign="middle" width="20" height="24">
                 <xsl:if test="string(/PAGE/PAGE-SELECTOR)">
                   <xsl:value-of disable-output-escaping="yes" select="gui:getPagingComponent('COUNT',$CURRENT-PAGE,25,$TAGSET)"/>
                 </xsl:if>
               </td>
               <td align="right" valign="middle" width="36" height="24">
                 <xsl:if test="string(/PAGE/PAGE-SELECTOR)">
                  <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
                  <input type="image" name="go" src="/engresources/images/go1.gif" border="0" height="20" />
                  <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
                 </xsl:if>
               </td>
             </tr>
           </table>
         </td>
      </form>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
