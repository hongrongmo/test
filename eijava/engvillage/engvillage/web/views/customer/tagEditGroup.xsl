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
	  
    <xsl:variable name="GCOLOR-CODE">
        <xsl:value-of select="TGROUP/COLOR/CODE"/>
    </xsl:variable>
    
     <xsl:variable name="GCOLOR-ID">
            <xsl:value-of select="TGROUP/COLOR/ID"/>
    </xsl:variable>
    
    <xsl:variable name="OWNER-ID">
                <xsl:value-of select="TGROUP/OWNERID"/>
    </xsl:variable>
    <xsl:variable name="GTITLE">
      <xsl:value-of select="TGROUP/GTITLE"/>      	
    </xsl:variable>	
   
    <xsl:variable name="DESCRIPTION">
        <xsl:value-of select="TGROUP/DESCRIPTION"/>
    </xsl:variable>
    
    <xsl:variable name="CGROUPID">
        <xsl:value-of select="TGROUP/GID"/>
    </xsl:variable>	

    <html>
    <head>
    <title>Edit Group</title>
    
    
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
    <xsl:text disable-output-escaping="yes">
	<![CDATA[
	<xsl:comment>
	<script language="javascript">
		function validate()
		{	 
			if (createGroup.title.value.length == 0)
			{
				window.alert("Title must be given");
				return false;
			}

			else  if ( createGroup.title.value.length == '')
			{
					window.alert("Title must be given");
					return false;
			}
			
		}
                          
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
    <xsl:with-param name="LOCATION">EditGroup</xsl:with-param>
  </xsl:apply-templates>


<table border="0" width="70%" cellspacing="0" cellpadding="0">
    <tr>
      <td height="20" bgcolor="#FFFFFF" colspan="2"><img src="/engresources/images/s.gif" height="20"/></td>
    </tr>
    <tr>
    </tr>
    <tr>
    	<td valign="top">
    	  <form name="createGroup" action="/controller/servlet/Controller?EISESSION={$SESSION-ID}&amp;CID=tagGroups&amp;database={$DATABASE}"  method="POST">       
		<input type="hidden" name="EISESSION" value="{$SESSION-ID}"/>
		<input type="hidden" name="DATABASE" value="{$DATABASE}"/>
		<input type="hidden" name="EDIT" value="Y"/>
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
	   	 	<td>
				<a class="MedGreyTextTag">
				<b>Group Name:  </b></a>
				<font color="{$GCOLOR-CODE}"><b><xsl:value-of select="$GTITLE" /></b></font>
        			<input type="hidden" name="title" size="20" value="{$GTITLE}"/> 
	   			<br/> 
	   			<br/>
	   		</td>
	   	</tr>
	   	<tr>
	   		<td>
				<a class="MedGreyTextTag">
				<b>Description: </b>
				</a>
				<br/>
			
				<TEXTAREA NAME="description" ROWS="6" COLS="50" value="{$DESCRIPTION}"><xsl:value-of select="$DESCRIPTION"/> </TEXTAREA>
	   			<br/>
	   			<br/>
	   		</td>
	   	</tr>	
	   	<tr>
	   		<td>
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

					<xsl:if test="$COLOR-ID=$GCOLOR-ID">
						<xsl:attribute name="selected">true</xsl:attribute>
					</xsl:if>

					<xsl:attribute name="value"><xsl:value-of select="$COLOR-ID"/></xsl:attribute>
						<xsl:value-of select="$COLOR-NAME"/>					
				</option>
				</xsl:for-each>
				</select>
				<br/>
			        <br/>

			</td>
	  	</tr>
	  	<xsl:if test="not(count(TGROUP/MEMBERS/MEMBER) = 1)">
	  	<tr>
	  		<td>
				<a class="MedGreyTextTag">
				<b>Remove Members:</b>
				</a>
				<br/>
				
				<xsl:for-each select="TGROUP/MEMBERS/MEMBER">
					<xsl:variable name="GMEMBERID">
						<xsl:value-of select="MEMBER-ID" />
					</xsl:variable>
					
					<xsl:if test="not($OWNER-ID = $GMEMBERID)">
						<a class="MedBlackText">
						<input type="checkbox" name="member" value="{$GMEMBERID}"/><xsl:value-of select="FULL-NAME"/>
						</a>
						<br/>
					</xsl:if>
				</xsl:for-each>
				<br/>
			</td>
	  	</tr>
	  	</xsl:if>

          	<tr>
	  	   <td height="6"><img src="/engresources/images/s.gif" height="6"/></td>
	  	   <br/>
	  	</tr> 
	  	<tr>
	  		<td>
				<a class="MedGreyTextTag">
				<b>Invite Members(seperate email addresses by commas):</b>
				</a>
				<br/>
				<TEXTAREA name="invitelist" ROWS="6" COLS="50"> </TEXTAREA>
	  		</td>
	  	</tr>	
		<tr>
		  <td height="6"><img src="/engresources/images/s.gif" height="6"/></td>
		</tr>  
		<tr>
	    		<td valign="top">
	    			<a CLASS="MedGreyTextTag" onClick="return validate();">
				    <input type="image" src="/engresources/images/savechanges_button.gif" name="CreateGroup" value="Edit Group/Send Invitations" border="0"/>
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

