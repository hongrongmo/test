<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLDecoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java xsl">

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="addTagNavigationBar.xsl"/>
<xsl:include href="groupsNavigationBar.xsl"/> 
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE/TAGS">
   <xsl:apply-templates />
</xsl:template>

<xsl:template match="TAG">
	<xsl:variable name="TAG"><xsl:value-of select="." disable-output-escaping="yes"/></xsl:variable>
	<option value="{$TAG}"><xsl:value-of select="$TAG"/>
	</option>
</xsl:template>

<xsl:template match="GMEMBER">
<option>
    <xsl:attribute name="value">
	<xsl:value-of select="java:encode(.)"/>
    </xsl:attribute>
    <xsl:value-of select="."/>
</option>
</xsl:template>

<xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
        <xsl:value-of select="DBMASK"/>
    </xsl:variable>		
    
    <xsl:variable name="GCOLOR">
        <xsl:value-of select="CGROUP/GCOLOR"/>
    </xsl:variable>	
    
    <xsl:variable name="GTITLE">
      <xsl:value-of select="CGROUP/GTITLE"/>      	
    </xsl:variable>	
    
    
    <xsl:variable name="DESCRIPTION">
        <xsl:value-of select="CGROUP/DESCRIPTION"/>
    </xsl:variable>				
    <xsl:variable name="CGROUPID">
        <xsl:value-of select="CGROUP/CGROUPID"/>
    </xsl:variable>	

    <html>
    <head>
    <title>Add Group</title>
    
    
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
    <xsl:text disable-output-escaping="yes">
	<![CDATA[
	<xsl:comment>
	<script language="javascript">

		function validate()
		{	       	
			if (createGroup.title.value.length == 0)
			{
				window.alert("Group title must be given");
				return false;
			}

			else  if ( createGroup.title.value.length == '')
			{
					window.alert("Group title must be given");
					return false;
			}
		    else  if ( createGroup.title.value.length > 30)
			{
					window.alert("Group title should not exceed 30 characters");
					return false;
			}

		} // function                             
	</script>
	</xsl:comment>
	]]>
	</xsl:text>
    
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

    <!-- APPLY HEADER -->
    <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SESSION-ID" select="SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="DATABASE"/>
    </xsl:apply-templates>

    <center>

    <!-- Insert the Global Links -->
  <!-- logo, search history, selected records, my folders. end session -->
  <xsl:apply-templates select="GLOBAL-LINKS">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    <xsl:with-param name="LINK">Tags</xsl:with-param>
  </xsl:apply-templates>

  <xsl:apply-templates select="ADDTAGSELECTRANGE-NAVIGATION-BAR"/>
  
  <xsl:apply-templates select="GROUPS-NAVIGATION-BAR">
    <xsl:with-param name="LOCATION">CreateGroup</xsl:with-param>
  </xsl:apply-templates>


  <table border="0" width="70%" cellspacing="0" cellpadding="0">
    <tr>
      <td height="10" bgcolor="#FFFFFF" colspan="4"><img src="/engresources/images/s.gif" height="20"/></td>
    </tr>
    <tr>
    </tr>
    <tr>
      <td valign="top">

      <form name="createGroup" action="/controller/servlet/Controller?EISESSION={$SESSION-ID}&amp;CID=tagGroups&amp;database={$DATABASE}"  method="POST">
       
		<input type="hidden" name="EISESSION" value="{$SESSION-ID}"/>
		<input type="hidden" name="ADD" value="Y"/>
		<input type="hidden" name="DATABASE" value="{$DATABASE}"/>
		<input type="hidden" name="CGROUPID" value="{$CGROUPID}"/>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td height="6"><img src="/engresources/images/s.gif" height="6"/></td>
	</tr>  
        <tr>
          <td width="4"><img src="/engresources/images/s.gif" width="4"/></td>
          <td valign="top" width="100%">
            <table border="0" cellspacing="0" cellpadding="0">
			<tr>
			  <td height="6"><img src="/engresources/images/s.gif" height="6"/></td>
		       </tr>         
                	<tr>
				<a class="MedGreyTextTag">
				<b>Group Name:  </b></a>
				<br/>
				<input type="text" name="title" size="20" value="{$GTITLE}"/>			
                <tr/>
			<br/>
			<tr>
				<a class="MedGreyTextTag">
				<b>Description: </b>
				</a>
				<br/>
				<TEXTAREA NAME="description" ROWS="6" COLS="50" value="{$DESCRIPTION}"> </TEXTAREA>
			</tr>	
			<br/>
			<tr>
				<a class="MedGreyTextTag">
				<b>Color:</b>
				</a>
				<br/>
				<select size="1" name="gcolor">
				<xsl:for-each select="COLORS/COLOR">
				  <option>
					<xsl:variable name="COLOR-ID">
						<xsl:value-of select="ID" />
					</xsl:variable>
					<xsl:variable name="COLOR-NAME">
						<xsl:value-of select="NAME" />
					</xsl:variable>

					<xsl:attribute name="value"><xsl:value-of select="$COLOR-ID"/></xsl:attribute>
				    <xsl:value-of select="$COLOR-NAME"/>					
				  </option>
				</xsl:for-each>
				</select>
			</tr>
			<br/>
			<tr>
				<a class="MedGreyTextTag">
				<b>Invite Members(seperate email addresses by commas):</b>
				</a>
				<br/>
				<TEXTAREA name="invitelist" ROWS="6" COLS="50"> </TEXTAREA>
			</tr>	
			<br/>
            </tr>
		<tr>
		  <td height="6"><img src="/engresources/images/s.gif" height="6"/></td>
		</tr>              
              <tr>
                <td valign="top">
		        <a CLASS="MedGreyTextTag" onClick="return validate();">
                <input type="image" src="/engresources/images/creategroup_button.gif" name="CreateGroup" value="Create Group/Send Invitations" border="0"/>
                </a>&#160; &#160;
              </td>
              </tr>
            </table>
          </td>
          <td width="10"><img src="/engresources/images/s.gif" width="10"/></td>          
        </tr>
        <tr>
          <td height="6"><img src="/engresources/images/s.gif" height="6"/></td>
        </tr>
      </table>
      </form>
      </td>
    </tr>
    <tr>
    </tr>
  	</table>

  	<br/>

	<!-- Start of footer-->
	<xsl:apply-templates select="FOOTER">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
	</xsl:apply-templates>

  </center>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
