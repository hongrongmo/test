<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/static/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLDecoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java xsl">

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />


<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">
    
    <xsl:variable name="SESSION-ID">
      <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>
    <xsl:variable name="DATABASE">
      <xsl:value-of select="DBMASK"/>
    </xsl:variable>
       
    <html>
    <head>
    <title>Accept Invitations</title>    
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
    
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
     
    
    
    <!-- Start of logo table -->
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
	<td valign="top">
		<a href="/controller/servlet/Controller?CID=home"><img src="/static/images/ev2logo5.gif" border="0"/></a>
	</td>
	</tr>
	<tr>
	   <td valign="top" height="5">
	   <img src="/static/images/spacer.gif" border="0" height="5"/>
	   </td>
	</tr>
	<tr>	
		<td valign="top" height="2" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" height="2"/></td>
	</tr>
	<tr>
		<td valign="top" height="20"><img src="/static/images/spacer.gif" border="0" height="20"/></td>
	</tr>
	</table>
	<!-- End of logo table -->
	
	<center>
    <table border="0" width="50%" >
    	
    <tr>
    	<td>
    		<xsl:variable name="GROUPID">
			<xsl:value-of select="TGROUP/GID"/>
		</xsl:variable>
    		
    		<center>
    		<br/><br/><br/>
    		<span CLASS="MedBlackText">You have been invited to join the group <xsl:value-of select="TGROUP/GTITLE"/>.</span>
    		<p/>
		<form name="acceptinvitation" action="/tagsgroups/invite.url" method="POST">
		<input type="hidden" name="CID" value="invitationAccepted"/>
		<input type="hidden" name="groupid" value="{$GROUPID}"/>
		<input type="submit" value="Accept Invitation"/>
		</form>
    		</center>
    	</td>  
    </tr>
    </table>

    <xsl:apply-templates select="FOOTER">

        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>

    </xsl:apply-templates>

  </center>
</body>
</html>
</xsl:template>
</xsl:stylesheet>

