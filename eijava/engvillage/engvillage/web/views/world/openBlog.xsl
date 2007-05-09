<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html"/>

<xsl:template match="root">

  <xsl:variable name="DOC-ID">
   <xsl:value-of select="//DOC-ID"/>
  </xsl:variable>
  <xsl:variable name="DATABASE">
   <xsl:value-of select="//DATABASE"/>
  </xsl:variable>
  <xsl:variable name="TITLE">
   <xsl:value-of select="//TITLE"/>
  </xsl:variable>
  <html>
  <head>
     <script language="javascript">
      <xsl:comment>
        function selectText()
        {
      	  document.theForm.link.focus();
          document.theForm.link.select();
        }
      // </xsl:comment>
     </script>
      <title>Engineering Village</title>
  		<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
  </head>

      <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

      <center>
      <table border="0" cellspacing="0" cellpadding="0" width="99%">
      	<tr><td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td><td valign="middle" align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td></tr>
      	<tr><td colspan="2" height="5"><img src="/engresources/images/s.gif" height="5"/></td></tr>
      	<tr><td colspan="2" height="2" bgcolor="#3173B5"><img src="/engresources/images/s.gif" border="0"/></td></tr>
      	<tr><td colspan="2" height="25"><img src="/engresources/images/s.gif" height="25"/></td></tr>
      </table>
      </center>

  		<form name="theForm">
  		<center>
      			<table border="0" cellspacing="0" cellpadding="0" width="80%">
    			<tr><td>
    				<a class="MedBlackText">Use Blog This to create a link and share the abstract of this record on your Blog or website.
    				<br/><br/>
    				Copy and paste the text below into your blog or website. </a>
    			</td></tr>
    			<tr><td height="10"><img src="/engresources/images/s.gif" height="10"/></td></tr>
    			<tr><td>
          		<textarea name="link" cols="50" rows="6">
          			<a><xsl:attribute name="href">http://<xsl:value-of select="//SERVER-NAME"/>/controller/servlet/Controller?CID=blogDocument&amp;MID=<xsl:value-of select="$DOC-ID"/>&amp;DATABASE=<xsl:value-of select="$DATABASE"/></xsl:attribute><xsl:value-of select="$TITLE"/></a>
          			<table border="0" cellspacing="0" cellpadding="0" width="99%">
          			<tr><td valign="top"><img alt="Engineering Village" src="http://www.engineeringvillage.com/engresources/images/newev.gif" border="0"/></td></tr>
          			</table>
          		</textarea>
    			</td></tr>
    			<tr><td height="8"><img src="/engresources/images/s.gif" height="8"/></td></tr>
    			<tr><td><input type="button" name="select"  value="Select Content" onClick="javascript:selectText();return true"/></td></tr>
	    	</table>
	    	</center>
  		</form>
    </body>
  </html>

</xsl:template>

</xsl:stylesheet>