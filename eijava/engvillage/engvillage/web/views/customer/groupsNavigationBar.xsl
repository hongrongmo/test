<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:gui="java:org.ei.gui.PagingComponents"
  exclude-result-prefixes="gui java html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="xsl:*" />

<xsl:template match="GROUPS-NAVIGATION-BAR">
  <xsl:variable name="PERSONALIZATION">
	<xsl:value-of select="//PERSONALIZATION"/>
  </xsl:variable>

  <xsl:variable name="PERSONALIZATION-PRESENT">
  		<xsl:value-of select="//PERSONALIZATION-PRESENT"/>
  </xsl:variable>

  <xsl:variable name="SESSION-ID">
  	<xsl:value-of select="SESSION-ID"/>
  </xsl:variable>
  <xsl:variable name="BACKURL">
    <xsl:value-of select="/PAGE/BACKURL"/>
  </xsl:variable>  
<xsl:param name="LOCATION"/>
    <table border="0" width="99%" cellspacing="0" cellpadding="0" >
      <tr>  
      	<td colspan="2"> 
      		<img width="8" height="7" src="/engresources/images/s.gif" border="0" />   
      	</td> 
      </tr>
      <tr> 
      <td width="50%"> 
      	<img width="18" height="1" src="/engresources/images/s.gif" border="0" />
	  <xsl:choose>
	    <xsl:when test="($LOCATION='CreateGroup')">
	          <span CLASS="taggroup_title">Create Groups</span>    
	    </xsl:when>
	    <xsl:when test="($LOCATION='EditGroup')">
	          <span CLASS="taggroup_title">Edit Groups</span>    
	    </xsl:when>
	    <xsl:when test="($LOCATION='Tags')">
	    	  <span CLASS="taggroup_title">Tags + Groups</span> 
	    </xsl:when>	  
	    <xsl:when test="($LOCATION='RenameTags')">
	    	  <span CLASS="taggroup_title">Rename Tags</span>    
	    </xsl:when>	
	    <xsl:when test="($LOCATION='DeleteTags')">
	    	  <span CLASS="taggroup_title">Delete Tags</span>    
	    </xsl:when>
	    <xsl:otherwise>
      		<span CLASS="taggroup_title">View/Edit Groups</span>    
	    </xsl:otherwise>
          </xsl:choose>
      </td>
		
      <td width="50%" align="right">
      	<xsl:choose>
      		<xsl:when test="not($LOCATION='Tags')">
			<a title="Tags Home" CLASS="LgBlueLink" target="_top">
			<xsl:attribute name="HREF">/controller/servlet/Controller?CID=tagsLoginForm</xsl:attribute>
			<xsl:attribute name="title">Tags + Groups</xsl:attribute>Tags + Groups</a>
		</xsl:when>
		<xsl:otherwise>
			<span class="MedGreyTextTag">Tags + Groups</span>
		</xsl:otherwise>
	</xsl:choose>
		
		
	 <img width="7" height="1" src="/engresources/images/s.gif" border="0" />
	 <xsl:choose>
      		<xsl:when test="not($LOCATION='GroupView')">
			<a title= "Edit" CLASS="LgBlueLink" target="_top">
			<xsl:attribute name="HREF">/controller/servlet/Controller?CID=tagGroups </xsl:attribute>
			<xsl:attribute name="title">View/Edit Groups</xsl:attribute>View/Edit Groups</a>
       		</xsl:when>
       		<xsl:otherwise>
			<span class="MedGreyTextTag">View/Edit Groups</span>
		</xsl:otherwise>
       	</xsl:choose>
      <img width="7" height="1" src="/engresources/images/s.gif" border="0" />
      <xsl:choose>
      		<xsl:when test="not($LOCATION='RenameTags')">
			<a title= "Edit" CLASS="LgBlueLink" target="_top">
			<xsl:attribute name="HREF">/controller/servlet/Controller?CID=renameTag</xsl:attribute>
			<xsl:attribute name="title">Rename Tags</xsl:attribute>Rename Tags</a>
      		 </xsl:when>
      		 <xsl:otherwise>
		 	<span class="MedGreyTextTag">Rename Tags</span>
		 </xsl:otherwise>
      </xsl:choose>
      <img width="7" height="1" src="/engresources/images/s.gif" border="0" />
      <xsl:choose>
      		<xsl:when test="not($LOCATION='DeleteTags')">
			<a title= "Edit" CLASS="LgBlueLink" target="_top">
			<xsl:attribute name="HREF">/controller/servlet/Controller?CID=deleteTag</xsl:attribute>
			<xsl:attribute name="title">Delete Tags</xsl:attribute>Delete Tags</a>
		</xsl:when>
		<xsl:otherwise>
			<span class="MedGreyTextTag">Delete Tags</span>
		</xsl:otherwise>
      </xsl:choose>
      </td>
      </tr>
    </table>
</xsl:template>

</xsl:stylesheet>
