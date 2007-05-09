<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="html xsl"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>
<xsl:include href="AbstractDetailedNavigationBar.xsl" />
<xsl:include href="DedupAbstractDetailedNavigationBar.xsl" />
<xsl:include href="TagSearchAbstractDetailedNavigationBar.xsl" />

<xsl:template match="PAGE">
<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="COUNT">
	<xsl:value-of select="COUNT"/>
</xsl:variable>
<xsl:variable name="DOC-ID">
	<xsl:value-of select="DOC-ID"/>
</xsl:variable>
<xsl:variable name="SEARCH-ID">
	<xsl:value-of select="SEARCH-ID"/>
</xsl:variable>
<xsl:variable name="SELECTED-DB">
	<xsl:value-of select="SELECTED-DB"/>
</xsl:variable>
 <xsl:variable name="SEARCH-TYPE">
      <xsl:value-of select="SEARCH-TYPE" />
    </xsl:variable>
<xsl:variable name="PUBLIC-TAGS">
        <xsl:value-of select="PUBLIC-TAGS" />
</xsl:variable>
<xsl:variable name="PRIVATE-TAGS">
        <xsl:value-of select="PRIVATE-TAGS" />
</xsl:variable>
<xsl:variable name="GROUP-TAGS">
        <xsl:value-of select="GROUP-TAGS" />
</xsl:variable>
<xsl:variable name="FORMAT">
        <xsl:value-of select="FORMAT" />
</xsl:variable>
<xsl:variable name="DOCINDEX">
        <xsl:value-of select="DOCINDEX" />
</xsl:variable>
<xsl:variable name="PAGEINDEX">
        <xsl:value-of select="PAGEINDEX" />
</xsl:variable>
<xsl:variable name="TAG-SEARCH-FLAG">
        <xsl:value-of select="TAG-SEARCH-FLAG" />
</xsl:variable>
<xsl:variable name="SCOPE">
        <xsl:value-of select="SCOPE-REC" />
</xsl:variable>
<xsl:variable name="GROUPID">
        <xsl:value-of select="GROUP-ID" />
</xsl:variable>
<xsl:variable name="DEFAULT-DB-MASK">
    	<xsl:value-of select="DEFAULT-DB-MASK"/>
 </xsl:variable>

<xsl:variable name="DATABASE">
<xsl:choose>
  <xsl:when test="($TAG-SEARCH-FLAG='true')">
	<xsl:value-of select="DEFAULT-DB-MASK"/>
   </xsl:when>
   <xsl:otherwise>
	<xsl:value-of select="DBMASK" />
   </xsl:otherwise>   
</xsl:choose>
</xsl:variable>    
    
<html>
<head>
<title>Edit Tags</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
    
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Autocomplete.js"/>    
 
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
    <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
 </xsl:apply-templates>
            
    <xsl:choose>
     <xsl:when test="($TAG-SEARCH-FLAG='true')">
	<!-- Insert the navigation bar -->
	<xsl:call-template name="TAGSEARCH-AB-DETAILED-NAVBAR">
	  <xsl:with-param name="HEAD" select="$SEARCH-ID" />
	</xsl:call-template>
       </xsl:when>
       <xsl:otherwise>
	 <!-- Insert the navigation bar -->
	 <xsl:apply-templates select="ABSTRACT-DETAILED-NAVIGATION-BAR">
	   <xsl:with-param name="HEAD" select="$SEARCH-ID" />
	 </xsl:apply-templates>
       </xsl:otherwise>
  </xsl:choose>
           
              
        
  <xsl:variable name="CITATION-CID">
    <xsl:choose>
      <xsl:when test="($SEARCH-TYPE='Book')">quickSearchCitationFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchCitationFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchCitationFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Combined')">expertSearchCitationFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Thesaurus')">quickSearchCitationFormat</xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="ABSTRACT-CID">
    <xsl:choose>
      <xsl:when test="($SEARCH-TYPE='Book')">quickSearchAbstractFormat</xsl:when>    
      <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchAbstractFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchAbstractFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Combined')">expertSearchAbstractFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Thesaurus')">quickSearchAbstractFormat</xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="DETAILED-CID">
    <xsl:choose>
      <xsl:when test="($SEARCH-TYPE='Book')">quickSearchDetailedFormat</xsl:when>    
      <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchDetailedFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchDetailedFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Combined')">expertSearchDetailedFormat</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Thesaurus')">quickSearchDetailedFormat</xsl:when>
    </xsl:choose>
  </xsl:variable>

  
       
      <form name="edittagsform" action="/controller/servlet/Controller?CID={$FORMAT}&amp;docid={$DOC-ID}&amp;tagSearchFlag={$TAG-SEARCH-FLAG}&amp;SCOPE={$SCOPE}&amp;groupID={$GROUPID}&amp;searchtype={$SEARCH-TYPE}&amp;database={$SELECTED-DB}&amp;DOCINDEX={$DOCINDEX}&amp;PAGEINDEX={$PAGEINDEX}&amp;SEARCHID={$SEARCH-ID}&amp;format={$FORMAT}" METHOD="POST" >

	<xsl:variable name="PERSONALIZATION">
		<xsl:value-of select="//PERSONALIZATION" />
	</xsl:variable>

	<xsl:variable name="BACKURL">
		<xsl:value-of select="//BACKURL" />
	</xsl:variable>

	<xsl:variable name="CURRENT-PAGE-INDEX">
		<xsl:value-of select="//PAGE-INDEX" />
	</xsl:variable>
	<!-- <xsl:variable name="EDITTAGURL">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=quickSearchAbstractFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID" />&amp;database=<xsl:value-of select="$SELECTED-DB" />&amp;format=quickSearchAbstractFormat</xsl:variable>   
		<xsl:message> xsl message back url <xsl:value-of select="//BACKURL" /> </xsl:message>
		<input type = "hidden"  name="nexturl"  value ="{$BACKURL}" />
		<input type = "hidden"  name="edittagurl"  value ="{$EDITTAGURL}" /> -->
		

         <table border="0" cellspacing="0" cellpadding="0">
            <tr>
	          <td height="20" bgcolor="#FFFFFF" colspan="2"><img src="/engresources/images/s.gif" height="20"/></td>
             </tr>
             <xsl:if test="string(PUBLIC-TAGS)">
		      <tr>
			<td valign="middle" align="right" colspan="1"><a CLASS="MedBlackText">Public Tags &nbsp;&nbsp;   </a></td> 
			<td valign="top" colspan="1"><a class="MedBlackText"> 
				<input type="hidden" name="initialpublictags" value = "{$PUBLIC-TAGS}" />			
				<xsl:text> </xsl:text><input type="text" size="80" name="publictags" value = "{$PUBLIC-TAGS}" /></a>
			</td>
		      </tr>
                 <tr>
	          <td height="12" bgcolor="#FFFFFF" colspan="2"><img src="/engresources/images/s.gif" height="12"/></td>
                </tr>     
             </xsl:if>
             <xsl:if test="string(PRIVATE-TAGS)">
	        <tr>
                <td valign="middle" align="right" colspan="1"><a CLASS="MedBlackText">Private Tags &nbsp;&nbsp;   </a><xsl:text> </xsl:text></td>
                <td valign="top" colspan="1"><a class="MedBlackText"> 
			<input type="hidden" name="initialprivatetags" value = "{$PRIVATE-TAGS}" />			
			<xsl:text> </xsl:text><input type="text" size="80" name="privatetags" value = "{$PRIVATE-TAGS}" /></a>
                </td>
                </tr>  
                 <tr>
	          <td height="12" bgcolor="#FFFFFF" colspan="2"><img src="/engresources/images/s.gif" height="12"/></td>
                </tr>  
             </xsl:if>   
             <tr>
		<xsl:for-each select="GROUP-TAGS/GROUP-TAG">
			<tr>
			<xsl:variable name="GROUP-TAG">
				<xsl:value-of select="."/>
			</xsl:variable>
			<xsl:variable name="GROUP-ID">
				<xsl:value-of select="@id"/>
			</xsl:variable>			
                       <td valign="middle" align="right" colspan="1"><a CLASS="MedBlackText"><xsl:value-of select="@title"/>&nbsp;&nbsp;   </a><xsl:text> </xsl:text></td>
			<td valign="top" colspan="1"><a class="MedBlackText"> 
				<input type="hidden" name="groupid" value = "{$GROUP-ID}" />			
				<input type="hidden" name="initialgrouptags" value = "{$GROUP-TAG}" />			
				<xsl:text> </xsl:text><input type="text" size="80" name="grouptags" value = "{$GROUP-TAG}" /></a>
			</td>	
			</tr>
			<tr>
			    <td height="12" bgcolor="#FFFFFF" colspan="2"><img src="/engresources/images/s.gif" height="12"/></td>
                       </tr> 
		</xsl:for-each>                      
              </tr>  
	      <tr>
		  <td height="12" bgcolor="#FFFFFF" colspan="2"><img src="/engresources/images/s.gif" height="12"/></td>
              </tr>               
              <tr>
                <td valign="middle" align="right" colspan="1"></td>
                <td valign="top" colspan="1"> 
	           <xsl:text>  </xsl:text><a><input type="image" src="/engresources/images/savechanges_button.gif" name="edit" value="Edit" border="0"/></a>
                 </td>
              </tr>
              </table>
	<a> <input type="hidden" name="EditTags" value = "TRUE" /></a>
      </form>
    
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


