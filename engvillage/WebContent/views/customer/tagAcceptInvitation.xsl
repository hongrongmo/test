<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/static/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java xsl">

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

	<xsl:variable name="BACKURL">
		<xsl:value-of select="BACKURL"/>
	</xsl:variable>   
	<xsl:variable name="NEXTURL">
		<xsl:value-of select="NEXTURL"/>
	</xsl:variable>
	<xsl:variable name="ENCODED-BACKURL">
		<xsl:value-of select="java:encode($BACKURL)"/>
	</xsl:variable>   
	<xsl:variable name="ENCODED-NEXTURL">
		<xsl:value-of select="java:encode($NEXTURL)"/>
	</xsl:variable>

	<xsl:variable name="GROUPID">
		<xsl:value-of select="TGROUP/GID"/>
	</xsl:variable>
	<xsl:variable name="DATABASE">
		<xsl:value-of select="DBMASK"/>
	</xsl:variable>
    <xsl:variable name="SESSION-ID">
      <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>
    
    <html>
    <head>
    <title>Accept Invitations</title>    
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
    
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
   
   <!--  <a href="/controller/servlet/Controller?CID=home"><img src="/static/images/ev2logo5.gif" border="0"/></a> -->
    
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
	<form name="acceptinvitation" action="/controller/servlet/Controller?CID=personalLoginConfirm"  method="POST">
      <input type="hidden" name="nexturl" VALUE ="{$NEXTURL}"/>     
      <input type="hidden" name="backurl" VALUE ="{$BACKURL}"/>

	<tr>
		<td>&nbsp;</td>
		<td height="15"><img src="/static/images/s.gif" height="15"/></td>
		<td>&nbsp;</td>
	</tr>
	
	<tr>
		<td>&nbsp;</td>
		<td valign="top">
		<div CLASS="MedBlackText"><b>Welcome to Engineering Village.</b><p/>
		<xsl:value-of select="FULL-NAME"/> has invited you to join the Engineering Village Tag Group: <xsl:value-of select="TGROUP/GTITLE"/> 
		</div>
        	<p/>
		<xsl:if test="string(TGROUP/DESCRIPTION)">
			<span CLASS="MedBlackText">
				Group Description : <xsl:value-of select="TGROUP/DESCRIPTION"/>
			</span>
		</xsl:if>
        	</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td valign="top">
			<br/>
			<span CLASS="MedBlackText">
				To accept this invitation, please login to your existing personal account or
			</span>
			<a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=personalAccountForm&amp;newregistration=true&amp;nexturl={$ENCODED-NEXTURL}&amp;backurl={$ENCODED-BACKURL}&amp;database={$DATABASE}">register</a>
			<span CLASS="MedBlackText"> to create a new account.</span>
       		 </td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td></td>
		<td height="8"><img src="/static/images/s.gif" height="8"/></td>
		<td></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td valign="top">
			<a CLASS="MedBlackText">
			<b>			
			Username :		
			<br/>
			<input type="text" name="email"/>
			<br/>
			<br/>
			Password :
			<br/>
			<input type="password" name = "password"/>
			</b>
			<br/>
			</a>
        </td>
		<td>&nbsp;</td>
	</tr>

	<tr>
		<td></td>
		<td height="5"><img src="/static/images/s.gif" height="5"/></td>
		<td></td>
	</tr>
	<tr>
		<td></td>
		<td height="4"><img src="/static/images/s.gif" height="4"/></td>
		<td></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td valign="top">           
        	<input type="hidden" name="acceptinvitaition" value="'true'"/>
		<input type="hidden" name="invitaitiongroupid" value="{$GROUPID}"/>
		<input type="image" src="/static/images/login.gif" alt="Accept Invitation" border="0"/>
        </td>
		<td>&nbsp;</td>
	</tr>
	
	<tr>
		<td>&nbsp;</td>
		<td height="100"><img src="/static/images/s.gif" height="100	"/></td>
		<td>&nbsp;</td>
	</tr>
	</form>
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

