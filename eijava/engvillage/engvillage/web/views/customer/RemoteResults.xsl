<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
	xmlns:DD="org.ei.domain.DatabaseDisplayHelper"
  exclude-result-prefixes="java xsl html DD"
>

  <xsl:include href="Header.xsl" />
  <xsl:include href="GlobalLinks.xsl" />
  <xsl:include href="RemoteNavigationBar.xsl" />

  <xsl:include href="engnetbase/ENGnetBASEQSForm.xsl" />
  <xsl:include href="engnetbase/ENGnetBASEESForm.xsl" />

  <xsl:include href="uspto/USPTOQSForm.xsl"/>
  <xsl:include href="uspto/USPTOESForm.xsl" />

  <xsl:include href="Footer.xsl" />

  <xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
      <xsl:value-of select="SESSION-ID" />
    </xsl:variable>

    <xsl:variable name="DATABASE">
      <xsl:value-of select="SELECTED-DATABASE"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
      <xsl:value-of select="SEARCH-TYPE"/>
    </xsl:variable>
    <xsl:variable name="REMOTE-URL">
      <xsl:value-of select="REMOTE-QUERY"/>
    </xsl:variable>
    <xsl:variable name="SEARCH-TITLE">
      <xsl:value-of select="$SEARCH-TYPE"/> Search Results
    </xsl:variable>

    <html>
      <head>
        <title><xsl:value-of select="DD:getDisplayName($DATABASE)"/><xsl:text> </xsl:text> <xsl:value-of select="$SEARCH-TITLE"/></title>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
        <xsl:text disable-output-escaping="yes">
          <![CDATA[
            <xsl:comment>
              <script language="javascript">
                function openNewWindow(remoteURL,database)
                {
                  var newWind;
                  if((database=='8') || (database=='16'))
                  {
                    newWind = window.open(remoteURL,'newWind','width=500,height=500,toolbar=yes,location=yes,status=yes,menubar=yes,scrollbars=yes,resizable');
                    if(typeof(newWind) != 'undefined') {
                      newWind.focus();
                    }
                  }
                }
              </script>
            </xsl:comment>
          ]]>
        </xsl:text>
      </head>

      <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" >
        <xsl:if test="string($REMOTE-URL)">
            <xsl:attribute name="onLoad">javascript:openNewWindow('<xsl:value-of select="$REMOTE-URL"/>','<xsl:value-of select="$DATABASE"/>'); return false;</xsl:attribute>
        </xsl:if>

        <center>

          <xsl:apply-templates select="HEADER">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
            <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
            <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
          </xsl:apply-templates>

          <!-- Insert the Global Link table -->
          <xsl:apply-templates select="GLOBAL-LINKS">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
            <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
            <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
          </xsl:apply-templates>

          <xsl:apply-templates select="NAVIGATION-BAR">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
            <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
          </xsl:apply-templates>

        <!-- Display of Session History -->

        <a NAME="searchAnchor"/>
  			<table border="0" width="99%" cellspacing="0" cellpadding="0">
          	<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
          <tr>
    	      <td valign="top" bgcolor="#FFFFFF" width="1"><img src="/engresources/images/spacer.gif" width="100"/></td>
            <td valign="top" >
              <img src="/engresources/images/spacer.gif" width="10"/>
              <a class="EvHeaderText">Refine Search</a>
              <xsl:choose>
                <xsl:when test="($DATABASE='16')">
                  <xsl:choose>
                    <xsl:when test="boolean($SEARCH-TYPE='Quick')">
                      <xsl:apply-templates select="ENGnetBASE-QUICK-SEARCH">
                        <xsl:with-param name="SESSION-ID" select="$SESSION-ID" />
                        <xsl:with-param name="SELECTED-DB" select="$DATABASE" />
                        <xsl:with-param name="NEWS">false</xsl:with-param>
                        <xsl:with-param name="TIPS">false</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:when>
                    <xsl:when test="boolean($SEARCH-TYPE='Expert')">
                      <xsl:apply-templates select="ENGnetBASE-EXPERT-SEARCH">
                        <xsl:with-param name="SESSION-ID" select="$SESSION-ID" />
                        <xsl:with-param name="SELECTED-DB" select="$DATABASE" />
                        <xsl:with-param name="NEWS">false</xsl:with-param>
                        <xsl:with-param name="TIPS">false</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:when>
                  </xsl:choose>
                </xsl:when>
                <xsl:when test="($DATABASE='8')">
                  <xsl:choose>
                    <xsl:when test="($SEARCH-TYPE='Quick')">
                      <xsl:apply-templates select="USPTO-QUICK-SEARCH">
                        <xsl:with-param name="SESSION-ID" select="$SESSION-ID" />
                        <xsl:with-param name="SELECTED-DB" select="$DATABASE" />
                        <xsl:with-param name="NEWS">false</xsl:with-param>
                        <xsl:with-param name="TIPS">false</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:when>
                    <xsl:when test="($SEARCH-TYPE='Expert')">
                      <xsl:apply-templates select="USPTO-EXPERT-SEARCH">
                        <xsl:with-param name="SESSION-ID" select="$SESSION-ID" />
                        <xsl:with-param name="SELECTED-DB" select="$DATABASE" />
                        <xsl:with-param name="NEWS">false</xsl:with-param>
                        <xsl:with-param name="TIPS">false</xsl:with-param>
                      </xsl:apply-templates>
                    </xsl:when>
                  </xsl:choose>
                </xsl:when>
              </xsl:choose>
            </td>
    	      <td valign="top" bgcolor="#FFFFFF" width="1"><img src="/engresources/images/spacer.gif" width="180"/></td>
          </tr>
        </table>
        </center>

        <!-- Insert the Footer table -->
        <xsl:apply-templates select="FOOTER"/>
        <br/>

      </body>
    </html>

  </xsl:template>

</xsl:stylesheet>