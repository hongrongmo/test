<?xml version="1.0" ?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template name="TAG-SUMMARY">

  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="RESULTS-COUNT">
    <xsl:value-of select="//PAGE/RESULTS-COUNT"/>
  </xsl:variable>
  <br/>
	<div class="t" style="width: 325px;">
	<div class="b">
	<div class="l">
	<div class="rc">
	<div class="bl">
	<div class="br">
	<div class="tl">
	<div class="trc">
	<div style="margin-left:5px;" class="im">
    <form style="margin:0px; padding:0px;" name="searchtagsform"  action="/search/results/tags.url?CID=tagSearch&amp;searchtype=TagSearch" METHOD="POST" >
        <table style="padding:0px; margin:0px; border:0px solid black;">
          <tbody>
            <tr>
              <td>
                <span id="searchtitle"><label for="searchgrouptags">Search Tags</label></span>
              </td>
              <td colspan="2">
                <span class="SmBlueText"><xsl:value-of select="$RESULTS-COUNT"/> Total record(s) for:</span>
              </td>
            </tr>
            <tr>
              <td>
                <div id="dropdown">
                  <xsl:apply-templates select="SCOPES"/>
                </div>
              </td>
              <td>
                <input style="vertical-align: middle;" class="SmBlackText" autocomplete="off" size="20" name="searchgrouptags" id="searchgrouptags" value="{$SEARCH-ID}" onkeyup="findTags();" />
                <div style="position:absolute;" id="popup" >
                  <table id="name_table" bgcolor="#FFFAFA" border="0" cellspacing="0" cellpadding="0" CLASS="SmBlackText">
                  <tbody id="name_table_body"></tbody>
                  </table>
                </div>
              </td>
              <td><input style="vertical-align: middle;" alt="Search Tags" type="image" border="0" src="/engresources/tagimages/searchbutton.gif" width="54" height="19"/></td>
            </tr>
          </tbody>
        </table>
    </form>
  </div></div></div></div></div></div></div></div></div>
  <br/>
</xsl:template>

<xsl:template match="SCOPES">
  <select style="vertical-align: middle;" name="scope">
  	<xsl:apply-templates select="SCOPE"/>
  </select>
</xsl:template>

<xsl:template match="SCOPE">
  <xsl:variable name="OPTIONVALUE"><xsl:value-of select="@value"/></xsl:variable>
	<xsl:choose>
		<xsl:when test="(@selected = 'true')">
			<option value="{$OPTIONVALUE}" SELECTED="true"><xsl:value-of select="@name"/></option>
		</xsl:when>
		<xsl:otherwise>
			<option value="{$OPTIONVALUE}"><xsl:value-of select="@name"/></option>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>