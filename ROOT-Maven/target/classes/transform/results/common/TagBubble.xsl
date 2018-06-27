<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:ti="java:org.ei.query.base.HitHighlightFinisher"
  xmlns:actionbean="java:org.ei.stripes.action.results.ICitationAction"
  xmlns:tagbubble="java:org.ei.tags.TagBubble"
  xmlns:taggroup="java:org.ei.tags.TagGroup"
  xmlns:tag="java:org.ei.tags.Tag"
  xmlns:edittag="java:org.ei.tags.EditTag"
  exclude-result-prefixes="java html ti xsl">

<xsl:template match="TAG-BUBBLE">

	<xsl:variable name="tagbubble" select="tagbubble:new()"/>
	
	<xsl:value-of select="tagbubble:setEdittagurl($tagbubble, URLS/EDIT-TAG-URL)"/>
	<xsl:value-of select="tagbubble:setAddtagurl($tagbubble, URLS/ADD-TAG-URL)"/>
	<xsl:value-of select="tagbubble:setBackurl($tagbubble, /PAGE/BACKURL)"/>
	<xsl:value-of select="tagbubble:setDocid($tagbubble, TAG-DOCID)"/>
	<xsl:value-of select="tagbubble:setPuserID($tagbubble, LOGGED-IN)"/>
	<xsl:value-of select="tagbubble:setJs($tagbubble,/PAGE/EDIT-TAGS/JS)"/>
	<xsl:value-of select="tagbubble:setSetEdit($tagbubble,boolean(/PAGE/EDIT-TAGS/SET-EDIT))"/>
	
	<!-- Create the tag groups -->
	<xsl:apply-templates select="TGROUPS/TGROUP">
		<xsl:with-param name="tagbubble" select="$tagbubble"></xsl:with-param>
	</xsl:apply-templates>

	<!-- Add displayable tags for current record -->
	<xsl:apply-templates select="TAGS">
		<xsl:with-param name="tagbubble" select="$tagbubble"></xsl:with-param>
	</xsl:apply-templates>
	
	<xsl:apply-templates select="/PAGE/EDIT-TAGS/EDIT-TAG">
		<xsl:with-param name="tagbubble" select="$tagbubble"></xsl:with-param>
	</xsl:apply-templates>
	
	<xsl:value-of select="actionbean:setTagbubble($actionbean,$tagbubble)"/>

  </xsl:template>

	<!-- ******************************************* -->
	<!-- Create tag groups                           -->
	<!-- ******************************************* -->
  <xsl:template match="TGROUPS/TGROUP">
	  <xsl:param name="tagbubble"/>

		<xsl:variable name="taggroup" select="taggroup:new()"/>

		<xsl:value-of select="taggroup:setGroupID($taggroup, GID)"/>
		<xsl:value-of select="taggroup:setTitle($taggroup, GTITLE)"/>
		<xsl:value-of select="taggroup:setDescription($taggroup, DESCRIPTION)"/>
		<xsl:value-of select="taggroup:setOwnerid($taggroup, OWNERID)"/>
		<xsl:value-of select="taggroup:setColor($taggroup, COLOR/CODE)"/>
		<xsl:value-of select="tagbubble:addTaggroup($tagbubble, $taggroup)"/>
  </xsl:template>

	<!-- ******************************************* -->
	<!-- Find all tags applied to the current record -->
	<!-- ******************************************* -->
  <xsl:template match="TAGS">
  <xsl:param name="tagbubble"/>
    <xsl:apply-templates>
	<xsl:with-param name="tagbubble" select="$tagbubble"></xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="TAG">
  <xsl:param name="tagbubble"/>
	
    <xsl:variable name="SCO">
      <xsl:choose>
        <xsl:when test="(SCOPE = '3')"><xsl:value-of select="SCOPE"/>:<xsl:value-of select="TGROUP/GID"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="SCOPE"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

	<xsl:variable name="tag" select="tag:new()"/>
	<xsl:value-of select="tag:setScope($tag, SCOPE)"/>
	<xsl:value-of select="tag:setTag($tag, TAGNAME)"/>
	<xsl:if test="TGROUP">
		<xsl:variable name="taggroup" select="taggroup:new()"/>
		<xsl:value-of select="tag:setGroupID($tag, TGROUP/GID)"/>
		<xsl:value-of select="taggroup:setGroupID($taggroup, TGROUP/GID)"/>
		<xsl:value-of select="taggroup:setTitle($taggroup, TGROUP/GTITLE)"/>
		<xsl:value-of select="taggroup:setDescription($taggroup, TGROUP/DESCRIPTION)"/>
		<xsl:value-of select="taggroup:setOwnerid($taggroup, TGROUP/OWNERID)"/>
		<xsl:value-of select="taggroup:setColor($taggroup, TGROUP/COLOR/CODE)"/>
		<xsl:value-of select="tag:setGroup($tag, $taggroup)"/>
	</xsl:if>
	<xsl:value-of select="tagbubble:addTag($tagbubble, $tag)"/>
		
  </xsl:template>
  
  <xsl:template match="/PAGE/EDIT-TAGS/EDIT-TAG">
	  <xsl:param name="tagbubble"/>
		<xsl:variable name="edittag" select="edittag:new()"/>
		<xsl:value-of select="edittag:setEditFieldName($edittag, EDIT-FIELD-NAME)"/>
		<xsl:value-of select="edittag:setEditHFieldName($edittag, EDIT-HFIELD-NAME)"/>
		<xsl:value-of select="edittag:setEditLable($edittag, EDIT-LABEL)"/>
		<xsl:value-of select="edittag:setEditTagsName($edittag, EDIT-TAGS-NAMES)"/>
		<xsl:value-of select="edittag:setEditScope($edittag, EDIT-SCOPE)"/>
		<xsl:value-of select="edittag:setEditGroupID($edittag, EDIT-GROUPID)"/>
		<xsl:value-of select="tagbubble:addEditTags($tagbubble, $edittag)"/>
  </xsl:template>

</xsl:stylesheet>
