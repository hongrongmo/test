<?xml version="1.0" ?>
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
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
        <xsl:value-of select="DBMASK"/>
    </xsl:variable>

    <xsl:variable name="COUNT">
        <xsl:value-of select="COUNT"/>
    </xsl:variable>
    
    <xsl:variable name="RANGEVALFROM">
        <xsl:value-of select="RANGE-FROM-VAL"/>
    </xsl:variable>
    
    <xsl:variable name="RANGEVALTO">
        <xsl:value-of select="RANGE-TO-VAL"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-ID">
        <xsl:value-of select="SEARCH-ID"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
        <xsl:value-of select="SEARCH-TYPE"/>
    </xsl:variable>
<!--
    <xsl:variable name="RESULTS-COUNT">
        <xsl:value-of select="RESULTS-COUNT"/>
    </xsl:variable>
-->
    <html>
    <head>
    <title>Add tag for Selected range</title>
    
    <style type="text/css">
    
	.mouseOut 
	{
		background: #708090;
		color: #FFFAFA;
	}

	.mouseOver 
	{
		background: #FFFAFA;
		color: #000000;
	}
	
    </style>
    
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

  <xsl:apply-templates select="ADDTAGSELECTRANGE-NAVIGATION-BAR">
    <xsl:with-param name="HEAD" select="$SEARCH-ID"/>
    <xsl:with-param name="LOCATION">Top</xsl:with-param>
  </xsl:apply-templates>


  <table border="0" width="50%" cellspacing="0" cellpadding="0">
    <tr>
      <td height="20" bgcolor="#FFFFFF" colspan="2"><img src="/engresources/images/s.gif" height="20"/></td>
    </tr>
    <tr>
      <td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" height="1"/></td>
    </tr>
    <tr>
      <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1"/></td>
      <td valign="top">

      <form name="addtag" action="/controller/servlet/Controller?EISESSION={$SESSION-ID}&amp;CID=tag&amp;SEARCHID={$SEARCH-ID}&amp;COUNT={$COUNT}&amp;database={$DATABASE}"  method="POST">
       
      <input type="hidden" name="EISESSION" value="{$SESSION-ID}"/>
      <input type="hidden" name="SEARCHID" value="{$SEARCH-ID}"/>
      <input type="hidden" name="DATABASE" value="{$DATABASE}"/>
      <input type="hidden" name="SEARCHTYPE" value="{$SEARCH-TYPE}"/>
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
          <td width="2" bgcolor="#3173B5"><img src="/engresources/images/s.gif" width="2"/></td>
          <td height="10" colspan="3" bgcolor="#3173B5"><a CLASS="LgWhiteText">
          <b>Add Tag For Search Results Selected Range</b></a></td>
        </tr>
        <tr>
          <td colspan="4" height="8"><img src="/engresources/images/s.gif" height="8"/></td>
        </tr>
        <tr>
          <td width="2"><img src="/engresources/images/s.gif" width="2"/></td>
          <td valign="top" colspan="3"><a CLASS="MedBlackText">Select Range of Records</a></td>
        </tr>
        <tr>
          <td colspan="4" height="4"><img src="/engresources/images/s.gif" height="4"/></td>
        </tr>
        <tr>
          <td width="4"><img src="/engresources/images/s.gif" width="4"/></td>
          <td valign="top" width="100%">
            <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top"><a class="MedBlackText"> 
                <br/>
                From</a>&#160;
				<input size="16" name="rangefrom" value = "{$RANGEVALFROM}" />
				<img src="/engresources/images/s.gif" width="4"/><a class="MedBlackText">To</a><img src="/engresources/images/s.gif" width="4"/>
				<input class="SmBlackText" size="16" name="rangeto" value = "{$RANGEVALTO}" />
                </td>
              </tr>
              <tr>
                <td valign="middle"> 
                <br/>               
               <a class="MedBlackText">Add Tag </a>
			<!-- ajax updates here -->
                <input autocomplete="off" size="38" name="tagname" id="tagname"  onkeyup="findTags();" />
				<div style="position:absolute;" id="popup" >
				<table id="name_table" bgcolor="#FFFAFA" border="0" cellspacing="0" cellpadding="0" >
						<tbody id="name_table_body">
						</tbody>
				</table>
				</div>
			<!-- end ajax updates here -->

                <br/>
                <br/>
                <a class="MedBlackText">Choose access type: </a>
                <input type="radio" name="scope" value="G" checked="checked"/><a class="MedBlackText">Public</a><img src="/engresources/images/s.gif" width="4"/>
				<input type="radio" name="scope" value="P"/><a class="MedBlackText">Private</a>	
				<br/>
				<br/>
				<input type="submit" value="Submit" name="go" onClick="return selectedRange();" />
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
      <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1"/></td>
    </tr>
    <tr>
      <td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1"/></td>
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

