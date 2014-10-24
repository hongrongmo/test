<?xml version="1.0" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java xsl html">

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
<xsl:strip-space elements="html:* xsl:*" />

  <xsl:include href="Header.xsl"/>
  <xsl:include href="GlobalLinks.xsl"/>
  <xsl:include href="addTagNavigationBar.xsl"/>
  <xsl:include href="groupsNavigationBar.xsl"/>
  <xsl:include href="Footer.xsl"/>

  <xsl:variable name ="PUSERID">
  	<xsl:value-of select="/PAGE/PUSERID" />
  </xsl:variable>
  <xsl:template match="MEMBERS">
  	<xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="MEMBER">
    <a class="MedBlackText">
      <xsl:variable name="GM"><xsl:value-of select="FULL-NAME" disable-output-escaping="yes"/></xsl:variable>
      <xsl:value-of select="$GM"/><xsl:if test="position() != last()"> , </xsl:if>
    </a>
  </xsl:template>

  <xsl:template match="TAGS">
  	<xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="TAG">
    <xsl:variable name= "SCOPE">
    <xsl:value-of select="SCOPE"/>
    </xsl:variable>
    <xsl:variable name="SCO">
    <xsl:value-of select="$SCOPE"/>:<xsl:value-of select="TGROUP/GID"/>
    </xsl:variable>
    <xsl:variable name= "ENCODED-TAGNAME">
    <xsl:value-of select="java:encode(TAGNAME)"/>
    </xsl:variable>

    <xsl:variable name= "BG">
    <xsl:value-of select="TGROUP/COLOR/CODE"/>
    </xsl:variable>

    <xsl:text> </xsl:text>
    <a class="TagSimple" onmouseover="style.backgroundColor='#CCCCCC'" onmouseout="style.backgroundColor='#FFFFFF'" href="/search/results/tags.url?CID=tagSearch&amp;searchtype=TagSearch&amp;searchtags={$ENCODED-TAGNAME}&amp;scope={$SCO}">
    <font color="{$BG}"><xsl:value-of select="TAGNAME" disable-output-escaping="yes" /></font>
    </a>&#160;

  </xsl:template>
  
  <xsl:template match="TGROUPS">
  	<xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="TGROUP">

    <xsl:variable name ="GRID">
      <xsl:value-of select="GID" />
    </xsl:variable>
    <xsl:variable name ="OWNERID">
          <xsl:value-of select="OWNERID" />
    </xsl:variable>
    <xsl:variable name = "BG">
      <xsl:value-of select="COLOR/CODE"/>
    </xsl:variable>
    <xsl:variable name ="EDIT">
      <xsl:value-of select="'/controller/servlet/Controller?CID=tagEditGroup'"/>
    </xsl:variable>

    <br/>
    <font color="{$BG}"><a class="TextGroups"><xsl:value-of select="GTITLE" /></a></font>
    <br/>
    
    <xsl:choose>
    	<xsl:when test="($OWNERID = $PUSERID)">
		<a CLASS="LgBlueLink"  title= "Edit Group" href="/controller/servlet/Controller?CID=tagEditGroup&amp;groupid={$GRID}&amp;edit=setedit">Edit Group</a>
		<a CLASS="SmBlackText">&#160;|&#160;</a>
		<a onclick="javascript:confirmDelete('{$GRID}'); return false;">
		<img src="/static/images/grDelete.gif" border="0" alt="Delete Group" />
		</a>
		<br/>
    	</xsl:when>
    	<xsl:otherwise>
    		<a CLASS="LgBlueLink" href="#" onclick="javascript:confirmMemberDelete('{$GRID}', '{$PUSERID}'); return false;">Cancel membership</a>
    		<br/>
    	</xsl:otherwise>
    </xsl:choose>
    <a class="MedBlackText">
      <xsl:value-of select="DATEFOUND" />
    </a>
    <br/>
    <xsl:if test="DESCRIPTION">
      <a class="MedBlackText">
      	<xsl:value-of select="DESCRIPTION" />
      </a>
    	<br/>
    </xsl:if>
    <xsl:if test="MEMBERS">
    	<xsl:apply-templates select="MEMBERS"/>
    	<!-- <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param> -->
    </xsl:if>
    <xsl:if test="TAGS">
    	<br/>
    	<xsl:apply-templates select="TAGS"/>
    </xsl:if>
    <br/>
  </xsl:template>

  <xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
      <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
      <xsl:value-of select="DBMASK"/>
    </xsl:variable>

    <html>
    <head>
    <title>View/Edit Tag Groups</title>

    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
    <xsl:text disable-output-escaping="yes">
        <![CDATA[
            <xsl:comment>
            <script language="javascript">

      				function confirmDelete(groupID)
      				{
      					var agree=confirm("Are you sure you wish to delete this group?");
      					if (agree)
      					{
      						var url = "/controller/servlet/Controller?CID=tagGroupForward&groupID="+groupID;
      						document.location = url;
      					}
      					else
      					{
      						return false ;
      					}
      				}
      				
				function confirmMemberDelete(groupID, memberID)
				{
					var agree=confirm("Are you sure you wish to cancel your membership?");
					if (agree)
					{
						var url = "/controller/servlet/Controller?CID=tagMemberDeleteForward&groupID="+groupID+"&memberID="+memberID;
						document.location = url;
					}
					else
					{
						return false ;
					}
				}
      				
          		</script>
            </xsl:comment>
            ]]>
        </xsl:text>
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

    <center>
      <!-- APPLY HEADER -->
      <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SESSION-ID" select="SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="DATABASE"/>
      </xsl:apply-templates>

      <!-- Insert the Global Links -->
      <!-- logo, search history, selected records, my folders. end session -->
      <xsl:apply-templates select="GLOBAL-LINKS">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        <xsl:with-param name="LINK">Tags</xsl:with-param>
      </xsl:apply-templates>

      <xsl:apply-templates select="ADDTAGSELECTRANGE-NAVIGATION-BAR"/>

      <xsl:apply-templates select="GROUPS-NAVIGATION-BAR">
      	<xsl:with-param name="LOCATION" select="'GroupView'"/>
      </xsl:apply-templates>

      <form style="margin:0px; padding:0px;" name="taggroup" action="/controller/servlet/Controller?CID=tagGroup&amp;database={$DATABASE}"  method="POST">
        <input type="hidden" name="DATABASE" value="{$DATABASE}"/>
        <table border="0" width="99%" cellpadding="0" cellspacing="0">
          <tr colspan="3">
            <td width ="20%" >&#160;</td>
            <td height="40"><img src="/static/images/s.gif" height="40"/></td>
            <td width ="10%">&#160;</td>
          </tr>
          <tr colspan="3">
            <td width ="20%"><img width="18" height="1" src="/static/images/s.gif" border="0" /><a title= "Create a New Group" CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=tagCreateGroup">Create a New Group (+)</a></td>
            <td>&#160;</td>
            <td width="10%">&#160;</td>
          </tr>
          <tr colspan="3">
            <td width ="20%">&#160;</td>
            <td ><xsl:apply-templates select="TGROUPS" /></td>
            <td width ="10%">&#160;</td>
          </tr>
          <tr colspan="3">
            <td width ="20%">&#160;</td>
            <td height="5"><img src="/static/images/s.gif" height="5"/></td>
            <td width ="10%">&#160;</td>
          </tr>
        </table>
      </form>

    </center>

        <!-- Start of footer-->
        <xsl:apply-templates select="FOOTER">
          <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
          <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        </xsl:apply-templates>
    </body>
    </html>
</xsl:template>
</xsl:stylesheet>

