<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  exclude-result-prefixes="java html xsl"
>

<xsl:include href="Header.xsl" />
<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="PersonalGrayBar.xsl" />
<xsl:include href="Footer.xsl" />
<xsl:include href="groupsNavigationBar.xsl" />

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template match="PAGE">

  <xsl:variable name="PERSONALIZATION">
    <xsl:value-of select="//PERSONALIZATION"/>
  </xsl:variable>

  <xsl:variable name="PERSONALIZATION-PRESENT">
    <xsl:value-of select="//PERSONALIZATION-PRESENT"/>
  </xsl:variable>

  <xsl:variable name="SESSION-ID">
    <xsl:value-of select="SESSION-ID"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-ID">
    <xsl:value-of  select="SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="COUNT">
    <xsl:value-of  select="COUNT"/>
  </xsl:variable>

  <xsl:variable name="DATABASE">
    <xsl:value-of  select="DBMASK"/>
  </xsl:variable>

  <xsl:variable name="DOCIDS">
    <xsl:value-of  select="DOCIDS"/>
  </xsl:variable>

  <xsl:variable name="QUERY-ID">
    <xsl:value-of  select="QUERY-ID"/>
  </xsl:variable>

  <xsl:variable name="SOURCE">
    <xsl:value-of  select="SOURCE"/>
  </xsl:variable>

  <xsl:variable name="OPTION-VALUE">
    <xsl:value-of  select="OPTION-VALUE"/>
  </xsl:variable>

  <xsl:variable name="RESULTS-FORMAT">
    <xsl:value-of  select="RESULTS-FORMAT"/>
  </xsl:variable>

  <xsl:variable name="DISPLAY-FORM">
    <xsl:value-of  select="LOGIN-FORM/DISPLAY-FORM"/>
  </xsl:variable>

  <xsl:variable name="LOGIN-STATUS">
    <xsl:value-of  select="LOGIN-FORM/LOGIN-STATUS"/>
  </xsl:variable>

  <xsl:variable name="EMAIL-EXISTS">
    <xsl:value-of select="LOGIN-FORM/EMAIL-EXISTS"/>
  </xsl:variable>

  <xsl:variable name="CID">
    <xsl:value-of select="CID"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-TYPE">
    <xsl:value-of select="SEARCH-TYPE"/>
  </xsl:variable>

  <xsl:variable name="SHOW-TAGS">
    <xsl:value-of select="SHOW-TAGS"/>
  </xsl:variable>

  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="//PAGE/CURR-PAGE-ID"/>
  </xsl:variable>

  <xsl:variable name="DEFAULT-DB-MASK">
    <xsl:value-of select="DEFAULT-DB-MASK"/>
  </xsl:variable>

  <xsl:variable name="USERID">
    <xsl:value-of select="/PAGE/USERID"/>
  </xsl:variable>

  <html>
    <head>
      <title>Engineering Village - Tags &amp; Groups</title>
      <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
      <SCRIPT LANGUAGE="Javascript" SRC="/static/js/AutocompleteTagSearch.js"/>
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
    <center>

    <!-- APPLY HEADER -->
    <xsl:apply-templates select="HEADER">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
    </xsl:apply-templates>

    <!-- Insert the Global Links -->
    <!-- logo, search history, selected records, my folders. end session -->
    <xsl:apply-templates select="GLOBAL-LINKS">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      <xsl:with-param name="LINK">Tags</xsl:with-param>
    </xsl:apply-templates>

    <!-- Gray bar -->
    <xsl:call-template name="P-GRAY-BAR">
      <xsl:with-param name="SESSIONID" select="$SESSION-ID"/>
      <xsl:with-param name="SEARCHID" select="$SEARCH-ID"/>
      <xsl:with-param name="COUNT" select="$COUNT"/>
      <xsl:with-param name="SEARCHTYPE" select="$SEARCH-TYPE"/>
      <xsl:with-param name="DATABASE" select="$DATABASE"/>
      <xsl:with-param name="CID" select="$CID"/>
    </xsl:call-template>

    <xsl:apply-templates select="GROUPS-NAVIGATION-BAR">
      <xsl:with-param name="LOCATION">Tags</xsl:with-param>
    </xsl:apply-templates>

    <xsl:variable name="BACKURL">
      <xsl:value-of select="/PAGE/BACKURL"/>
    </xsl:variable>
    <xsl:variable name="RESULTS-COUNT">
      <xsl:value-of select="//RESULTS-COUNT"/>
    </xsl:variable>

      <!-- Table below the logo area -->
    <p/>
    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        <xsl:comment>
          <script language="javascript">
            
            function checklogin(sel, transmit, database)
            {
           	var groups = sel.value;
		if(groups == "6")
		{
			url = "/controller/servlet/Controller?CID=personalLoginForm&displaylogin=true&database="+database+
			"&nexturl=CID%3DtagsLoginForm&backurl=CID%3DtagsLoginForm";
			document.location = url;
		}
		else
		{
			if(transmit == 'true')
			{
				document.forms["cloudselect"].submit();
			}
		}
		return false;
            }
            
          </script>
        </xsl:comment>
        ]]>
    </xsl:text>
    
    
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
      <tr>
        <td width="35%" valign="top">
          <form name="searchtagsform" action="/search/results/tags.url?EISESSION=$SESSIONID&amp;CID=tagSearch&amp;searchtype=TagSearch&amp;database={$DEFAULT-DB-MASK}" METHOD="POST" >
            <input type="hidden" name="puserid" value="{$USERID}"/>
            <table border="0" width="100%" cellspacing="0" cellpadding="0">
              <tr>
                <td width="100%">
                  <div class="t" style="width: 215px;">
                  <div class="b">
                  <div class="l">
                  <div class="rc">
                  <div class="bl">
                  <div class="br">
                  <div class="tl">
                  <div class="trc">
                  <div class="im">
                    <br/>
                    <div id="searchtitle"><label for="searchgrouptags">Search Tags</label></div>
                    <div id="dropdown">
                      <select name="scope" onchange="checklogin(this,'false',{$DATABASE})">
                        <xsl:apply-templates select="SCOPES"/>
                      </select>
                    </div>
                    <input style="vertical-align: middle;" class="SmBlackText" autocomplete="off" size="20" name="searchgrouptags" id="searchgrouptags" onkeyup="findTags();" />
                    <div style="position:absolute;" id="popup" >
                      <table id="name_table" bgcolor="#FFFAFA" border="0" cellspacing="0" cellpadding="0" CLASS="SmBlackText">
                      <tbody id="name_table_body"></tbody>
                      </table>
                    </div>
                    &#160;<input style="vertical-align: middle;" alt="Search Tags" type="image" src="/engresources/tagimages/searchbutton.gif" border="0" width="54" height="19"/>
                    <br/>
                    <br/>
                  </div></div></div></div></div></div></div></div></div>
                </td>
              </tr>
              <tr>
                <td width="100%">&#160;</td>
              </tr>
            </table>
          </form>
        </td>
        <td valign="top" align="left">
          <form name="cloudselect" action="/controller/servlet/Controller">
            <input type="hidden" name="CID" value="tagsLoginForm"/>
            <b><a class="MedGreyTextTag">View:</a></b>&nbsp;
            <select name="scope" onchange="checklogin(this, 'true',{$DATABASE})">
              <xsl:apply-templates select="SCOPES"/>
            </select>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <xsl:apply-templates select="SORT"/>
          </form>
          <div style="text-align: justify"><xsl:apply-templates select="CLOUD"/><xsl:apply-templates select="NOTAGS"/></div>
        </td>
        <td width="22%">&#160;</td>
      </tr>
    </table>
    <!-- Insert the Footer table -->
    <xsl:apply-templates select="FOOTER">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    </xsl:apply-templates>

    </center>
    </body>
  </html>
</xsl:template>

<xsl:template match="TAG">
  <xsl:apply-templates select="TAGNAME" />
</xsl:template>

<xsl:template match="TAGNAME">
  <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
  <xsl:value-of select="$NAME"/>
  <br/>
</xsl:template>

<xsl:template match="CLOUD">
  <xsl:apply-templates select="CTAG" />
</xsl:template>

<xsl:template match="NOTAGS">
  <span class="MedGreyTextTag">No tags found</span>
</xsl:template>

<xsl:template match="CTAG">
  <xsl:variable name="CLOUD-SCOPE"><xsl:value-of select="CSCOPE"/></xsl:variable>
  <xsl:variable name="GROUP-ID"><xsl:value-of select="TGROUP/GID"/></xsl:variable>
  <xsl:if test="(@class)">
    <xsl:variable name="SIZE"><xsl:value-of select="@class"/></xsl:variable>
    <xsl:variable name="NAME1"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
    <xsl:variable name="ENCODED-TAG"><xsl:value-of select="java:encode(normalize-space(text()))"/></xsl:variable>
    <xsl:variable name="SCO">
      <xsl:choose>
        <xsl:when test="($CLOUD-SCOPE = '3')"><xsl:value-of select="$CLOUD-SCOPE"/>:<xsl:value-of select="$GROUP-ID"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="$CLOUD-SCOPE"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="BG">
      <xsl:choose>
        <xsl:when test="($CLOUD-SCOPE='1')">#002D68</xsl:when>
        <xsl:when test="($CLOUD-SCOPE='2')">#A3B710</xsl:when>
        <xsl:when test="($CLOUD-SCOPE='4')">#A56500</xsl:when>
        <xsl:when test="($CLOUD-SCOPE='3')"><xsl:value-of select="TGROUP/COLOR/CODE"/></xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:text> </xsl:text><a class="{$SIZE}" target="_top" onmouseover="style.backgroundColor='#CCCCCC'" onmouseout="style.backgroundColor='#FFFFFF'" href="/search/results/tags.url?EISESSION=$SESSIONID&amp;CID=tagSearch&amp;searchtype=TagSearch&amp;searchtags={$ENCODED-TAG}&amp;scope={$SCO}"><xsl:text> </xsl:text><font color="{$BG}"><xsl:value-of select="$NAME1"/></font></a>
  </xsl:if>
</xsl:template>

<xsl:template match="SCOPES">
  <xsl:apply-templates select="SCOPE"/>
  <xsl:if test="not(string(/PAGE/USERID))">
  	<option value="6">Private/Groups</option>
  </xsl:if>
</xsl:template>

<xsl:template match="SORT"><b><a class="MedGreyTextTag">Sort:</a></b>&nbsp;
  <xsl:variable name="SSCOPE"><xsl:value-of select="@scope"/></xsl:variable>
  <xsl:choose>
      <xsl:when test="(@type = 'alpha')">
        <a class="MedGreyTextTag">Alphabetical</a>
      </xsl:when>
      <xsl:otherwise>
        <a class="LgBlueLink" href="/controller/servlet/Controller?CID=tagsLoginForm&amp;sort=alpha&amp;scope={$SSCOPE}">Alphabetical</a>
      </xsl:otherwise>
  </xsl:choose>
  &nbsp;&nbsp;
  <xsl:choose>
      <xsl:when test="(@type = 'size')">
        <a class="MedGreyTextTag">Popularity</a>
      </xsl:when>
      <xsl:otherwise>
        <a class="LgBlueLink" href="/controller/servlet/Controller?CID=tagsLoginForm&amp;sort=size&amp;scope={$SSCOPE}">Popularity</a>
      </xsl:otherwise>
  </xsl:choose>
  &nbsp;&nbsp;
  <xsl:choose>
        <xsl:when test="(@type = 'time')">
          <a class="MedGreyTextTag">Most recent</a>
        </xsl:when>
      <xsl:otherwise>
        <a class="LgBlueLink" href="/controller/servlet/Controller?CID=tagsLoginForm&amp;sort=time&amp;scope={$SSCOPE}">Most recent</a>
      </xsl:otherwise>
  </xsl:choose>
  <p/>
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
