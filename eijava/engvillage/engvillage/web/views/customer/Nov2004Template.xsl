<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="html xsl"
  >

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

  <xsl:variable name="SESSION-ID">
  	<xsl:value-of select="SESSION-ID"/>
  </xsl:variable>
  
  <xsl:variable name="DBMASK">
  	<xsl:value-of select="DBMASK"/>
  </xsl:variable>

  <html>
  <head>
  	<title> PAGE TITLE GOES HERE </title>
    <!-- JAVASCRIPT INCLUDES CODE -->
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
    <!-- CUSTOM JAVASCRIPT CODE -->
    <xsl:text disable-output-escaping="yes">
    <![CDATA[
      <xsl:comment>
      <SCRIPT LANGUAGE="JavaScript">
      </SCRIPT>
      </xsl:comment>
    ]]>
    </xsl:text>
  </head>
  
  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
  
    <!-- Center Entire Page / DO NOT CHANGE -->
    <center>
    
    	<!-- Insert the Header -->
      <xsl:apply-templates select="HEADER"/>
    	
    	<!-- Insert the Global Link table -->
    	<xsl:apply-templates select="GLOBAL-LINKS">
    		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    	 	<xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
    	</xsl:apply-templates>
    
      <!-- Empty Gray Navigation Bar / DO NOT CHANGE -->
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
      	<tr><td valign="top" height="24" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" border="0"/></td></tr>
      </table>
    
      <!-- Center Main / DO NOT CHANGE -->
      <center>
        <!-- Outer Template Table / Blue Border / DO NOT CHANGE -->
        <table border="0" width="80%" cellspacing="0" cellpadding="0">
          <!-- Spacer Row / Provides space below Navigation Bar / DO NOT CHANGE -->
          <tr><td colspan="4" height="20"><img src="/engresources/images/s.gif" height="20" /></td></tr>
          <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" height="1" /></td></tr>
          <tr>
            <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
            <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1" /></td>
            <!-- Content Cel / White (default) Background / DO NOT CHANGE -->
            <td valign="top">
              <!-- Inner Template Table / DO NOT CHANGE -->
              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <!-- Title Row / Indented with Spacer / Bold White Text on Blue Background -->
                <tr>
                  <!-- Indentation Cel / DO NOT CHANGE -->
                  <td width="2" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="2" /></td>
                  <!-- Title Cel / EDIT THIS -->
                  <td height="10" bgcolor="#3173B5" colspan="3">
                    <a CLASS="LgWhiteText"><b>Title Goes Here</b></a>
                  </td>
                </tr>
                <!-- Spacer Row / White Background / DO NOT CHANGE -->
                <tr><td valign="top" height="10" colspan="4"><img src="/engresources/images/s.gif" border="0" height="10" /></td></tr>
                <!-- Content Row / Indented with Spacer / Content cell/should can contain table 
                  to acheive specific content layout within template -->
                <tr>
                  <!-- Indentation Cel / DO NOT CHANGE -->
                  <td width="2"><img src="/engresources/images/s.gif" width="2" /></td>
                  <!-- Content Goes Here -->
                  <td colspan="3">
                    <!-- Content Goes Here -->
                  </td>
                </tr>
                <!-- Sopacer Row Below Content / DO NOT CHANGE -->
                <tr><td valign="top" colspan="4" height="4"><img src="/engresources/images/s.gif" border="0" height="4" /></td></tr>
              </table>
            </td>
            <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
            <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1" /></td>
          </tr>
          <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" height="1" /></td></tr>
        </table>
      </center>
    
    	<!-- Insert the Footer -->
      <xsl:apply-templates select="FOOTER">
      	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      	<xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
      </xsl:apply-templates>
    
    </center>
  
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>
