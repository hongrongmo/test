<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:DD="org.ei.domain.DatabaseDisplayHelper"
  exclude-result-prefixes="java html xsl DD"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template name="SEARCH-RESULTS">

  <xsl:variable name="RESULTS-COUNT">
    <xsl:value-of select="//RESULTS-COUNT"/>
  </xsl:variable>

  <xsl:variable name="COUNT">
    <xsl:value-of select="//CURR-PAGE-ID"/>
  </xsl:variable>

  <xsl:variable name="DISPLAY-QUERY">
    <xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/>
  </xsl:variable>

  <xsl:variable name="I-QUERY">
      <xsl:value-of select="//SESSION-DATA/I-QUERY"/>
  </xsl:variable>

  <xsl:variable name="QUERY-ID">
    <xsl:value-of select="//SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="DATABASE">
    <xsl:value-of select="/PAGE/DBMASK"/>
  </xsl:variable>

  <xsl:variable name="EMAILALERTWEEK">
    <xsl:value-of select="//SESSION-DATA/EMAILALERTWEEK"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-TYPE">
    <xsl:value-of select="//SESSION-DATA/SEARCH-TYPE"/>
  </xsl:variable>

  <xsl:variable name="LAST-FOUR-UPDATES">
    <xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/>
  </xsl:variable>

  <xsl:variable name="BACKURL">
    <xsl:value-of select="/PAGE/BACKURL"/>
  </xsl:variable>

  <xsl:variable name="PERSONALIZATION">
    <xsl:value-of select="/PAGE/PERSONALIZATION"/>
  </xsl:variable>

  <xsl:variable name="PERSONALIZATION-PRESENT">
        <xsl:value-of select="/PAGE/PERSONALIZATION-PRESENT"/>
  </xsl:variable>

  <xsl:variable name="RSS-LINK">
          <xsl:value-of select="/PAGE/RSSLINK"/>
  </xsl:variable>

  <xsl:variable name="SAVEDSEARCH">
    <xsl:value-of select="//SESSION-DATA/SAVEDSEARCH"/>
  </xsl:variable>

  <xsl:variable name="EMAILALERT">
    <xsl:value-of select="//SESSION-DATA/EMAIL-ALERT"/>
  </xsl:variable>


  <xsl:variable name="COMPMASK">
    <xsl:value-of select="/PAGE/NAVIGATORS/COMPMASK"/>
  </xsl:variable>
  
    <br/>
    <a class="EvHeaderText">Search Results</a>
    <br/>
    <a CLASS="SmBlackText">
      <xsl:choose>
        <xsl:when test="($RESULTS-COUNT=1)"><xsl:value-of select="$RESULTS-COUNT"/> record</xsl:when>
        <xsl:otherwise><xsl:value-of select="$RESULTS-COUNT"/> records</xsl:otherwise>
      </xsl:choose>
      in <xsl:value-of select="DD:getDisplayName($DATABASE)"/>
        <xsl:if test="not($SEARCH-TYPE='Easy')">
        for
        <!-- jam 5/23/2005 - removed display query -->
        <!-- : <xsl:value-of select="$DISPLAY-QUERY"/> -->

        <!-- jam 5/23/2005 - removed leading commas -->
        <!-- jam 8/19/2003 - change Emailalerts to display 'Week..' String -->
        <!-- jam 8/19/2003 - changed A(nchor) to SPAN -->
        <xsl:choose>
          <!-- display email alerts - priority over last four updates! -->
          <xsl:when test="string($EMAILALERTWEEK)"> Week <xsl:value-of select="$EMAILALERTWEEK"/></xsl:when>
          <xsl:when test="string($LAST-FOUR-UPDATES)"> Last <xsl:value-of select="$LAST-FOUR-UPDATES"/> update(s)</xsl:when>
          <!-- do nothing - year ranges are meaningless with combined searches -->
          <xsl:when test="($SEARCH-TYPE='Combined')"></xsl:when>
          <xsl:otherwise> <xsl:value-of select="//PAGE/SESSION-DATA/START-YEAR"/>-<xsl:value-of select="//PAGE/SESSION-DATA/END-YEAR"/></xsl:otherwise>
        </xsl:choose>
        </xsl:if>
    </a>

        &#160; &#160;
        <xsl:if test="DEDUPABLE='true'">
          <a class="DecLink" href="/controller/servlet/Controller?CID=dedupForm&amp;database={$DATABASE}&amp;SEARCHID={$QUERY-ID}&amp;COUNT={$COUNT}&amp;SEARCHTYPE={$SEARCH-TYPE}&amp;RESULTSCOUNT={$RESULTS-COUNT}">Remove Duplicates</a>
      <A CLASS="MedBlackText">&#160; - &#160;</A>
        </xsl:if>

        <xsl:if test="($PERSONALIZATION-PRESENT='true')">
            <!-- this will contain the redirect for saving the search -->
            <xsl:variable name="NEXTURL">CID=addDeleteSavedSearch&amp;selectvalue=mark&amp;option=SavedSearch&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>
            <A target="_top">
              <xsl:attribute name="class">DecLink</xsl:attribute>
              <xsl:attribute name="HREF">
                <xsl:choose>
                  <xsl:when test="($SAVEDSEARCH='On')">/controller/servlet/Controller?CID=addDeleteSavedSearch&amp;selectvalue=unmark&amp;option=SavedSearch&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;backurl=<xsl:value-of select="$BACKURL"/></xsl:when>
                  <xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?CID=addDeleteSavedSearch&amp;selectvalue=mark&amp;option=SavedSearch&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;backurl=<xsl:value-of select="$BACKURL"/></xsl:when>
                  <xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/>&amp;backurl=<xsl:value-of select="$BACKURL"/></xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <xsl:attribute name="title">
                <xsl:choose>
                  <xsl:when test="($SAVEDSEARCH='On')">Click here to remove saved search</xsl:when>
                  <xsl:otherwise>Click here to save search</xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <xsl:choose>
                <xsl:when test="($SAVEDSEARCH='On')"><font color="#FF3300"><b>Saved Search</b></font></xsl:when>
                <xsl:otherwise>Save Search</xsl:otherwise>
              </xsl:choose>
            </A>
        <A CLASS="MedBlackText">&#160; - &#160;</A>
        </xsl:if>


        <!-- May or May not be available for all databases! -->
        <xsl:if test="($PERSONALIZATION-PRESENT='true')">
        <!-- this will contain the redirect for saving the search -->
          <xsl:variable name="NEXTURL">CID=addDeleteSavedSearch&amp;selectvalue=mark&amp;option=EmailAlert&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>
                <A target="_top">
                <xsl:attribute name="class">DecLink</xsl:attribute>
                <xsl:attribute name="HREF">
                  <xsl:choose>
                    <xsl:when test="($EMAILALERT='On')">/controller/servlet/Controller?CID=addDeleteSavedSearch&amp;selectvalue=unmark&amp;option=EmailAlert&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;backurl=<xsl:value-of select="$BACKURL"/></xsl:when>
                    <xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?CID=addDeleteSavedSearch&amp;selectvalue=mark&amp;option=EmailAlert&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/>&amp;backurl=<xsl:value-of select="$BACKURL"/></xsl:when>
                    <xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/>&amp;backurl=<xsl:value-of select="$BACKURL"/></xsl:otherwise>
                  </xsl:choose>
                </xsl:attribute>
                <xsl:attribute name="title">
                  <xsl:choose>
                    <xsl:when test="($EMAILALERT='On')">Click here to remove alert</xsl:when>
                    <xsl:otherwise>Click here to create alert</xsl:otherwise>
                  </xsl:choose>
                </xsl:attribute>
                <xsl:choose>
                  <xsl:when test="($EMAILALERT='On')"><font color="#FF3300"><b>Remove Alert</b></font></xsl:when>
                  <xsl:otherwise>Create Alert</xsl:otherwise>
                </xsl:choose>
                </A>
        </xsl:if>

  <!-- this will contain the link for RSS-->
  <xsl:if test="($PERSONALIZATION-PRESENT='true')">
    <xsl:if test="($RSS-LINK='true')">
    <A CLASS="MedBlackText">&#160; - &#160;</A>
    </xsl:if>
  </xsl:if>
  <xsl:if test="($RSS-LINK='true')">
    <div class="wrap">
      <A class="DecLink" HREF="#" onClick="window.open('/controller/servlet/Controller?CID=displayRSSQuery&amp;database={$DATABASE}&amp;term1={java:encode($I-QUERY)}','newwindow','width=700,height=500,toolbar=no,location=no,scrollbars,resizable');return false;">
        <img src="/engresources/images/rss.gif" align="absbottom" border="0"/>
      </A>
      <xsl:text>  </xsl:text>
         <A class="DecLink" href="javascript:makeUrl('RSS_Feature.htm')">
	    <img src="/engresources/images/blue_help.gif" align="absbottom" border="0"/>
	 </A>
	 <xsl:if test="($COMPMASK='8192' or $COMPMASK='2097152' or $COMPMASK='2105344')">
	   <div style="float:right" width="120"><a id="mapToggle"><img src="/engresources/images/Map_icon_beta.png" align="middle" border="1" alt="Show Geographic Map"/></a></div>
	 </xsl:if>  
    </div>
  </xsl:if>
<div id="map" style="clear:both;display:none;">
                <a class="SpLink" id="resetcenter" href="javascript:resetCenterAndZoom()">Reset Map Center and Zoom</a><br/>
                <div id="map_spacer" style="float:left; border:0px solid black; width: 20px; height: 300px"></div>
                <div id="map_canvas" style="float:left; width: 600px; height: 300px">&#160;</div>
                <div id="map_sidebar" class="SmBlackText" style="display:none; float:left; border:1px solid black; width: 110px; height: 300px"><form name="formlegend"><fieldset><legend>Legend</legend><ul style="list-style-type:none; margin:0; padding:0; margin-bottom:1px;" id="legend"></ul></fieldset></form></div>
              </div>  
  <br/>

</xsl:template>

<xsl:template name="RESULTS-MANAGER">
  <xsl:param name="SESSION-ID"/>
  <xsl:param name="LOCATION"/>
  <xsl:param name="DISPLAY-FORMAT"/>
  <xsl:param name="CURRENT-VIEW">searchresults</xsl:param>


    <xsl:variable name="DB-PREF">
        <xsl:value-of select="DBPREF"/>
    </xsl:variable>

    <xsl:variable name="FIELD-PREF">
        <xsl:value-of select="FIELDPREF"/>
    </xsl:variable>

    <xsl:variable name="DEDUP">
        <xsl:value-of select="DEDUP"/>
    </xsl:variable>

    <xsl:variable name="ORIGIN">
        <xsl:value-of select="ORIGIN"/>
    </xsl:variable>

    <xsl:variable name="DBLINK">
        <xsl:value-of select="DBLINK"/>
    </xsl:variable>

    <xsl:variable name="LINKSET">
        <xsl:value-of select="LINKSET"/>
    </xsl:variable>

    <xsl:variable name="DEFAULT-DB-MASK">
	<xsl:value-of select="//DEFAULT-DB-MASK"/>
    </xsl:variable>

   <xsl:variable name="TAG-RESULT-DBNAME"><xsl:value-of select="EI-DOCUMENT/DOC/DB/DBMASK"/></xsl:variable>

    <xsl:variable name="TAG-SEARCH-FLAG">
        <xsl:value-of select="//TAG-SEARCH"/>
    </xsl:variable>

    <xsl:variable name="SCOPE">
        <xsl:value-of select="/PAGE/SCOPE"/>
    </xsl:variable>

        <xsl:variable name="GROUP-ID">
          <xsl:value-of select="//GROUP-ID"/>
    </xsl:variable>

  <xsl:variable name="BACKURL">
    <xsl:value-of select="/PAGE/BACKURL"/>
  </xsl:variable>

  <xsl:variable name="OPPOSITE-LOCATION">
    <xsl:choose>
      <xsl:when test="($LOCATION='top')">bottom</xsl:when>
      <xsl:otherwise>top</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="CLEAR-ON-VALUE">
    <xsl:value-of select="//CLEAR-ON-VALUE"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="//SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="//CURR-PAGE-ID"/>
  </xsl:variable>

  <xsl:variable name="RESULTS-COUNT">
    <xsl:value-of select="//RESULTS-COUNT"/>
  </xsl:variable>

  <xsl:variable name="RESULTS-PER-PAGE">
    <xsl:value-of select="//RESULTS-PER-PAGE"/>
  </xsl:variable>

  <xsl:variable name="DISPLAY-QUERY">
    <xsl:choose>
      <xsl:when test="($TAG-SEARCH-FLAG='true')">
        <xsl:value-of select="//SEARCH-ID"/>
      </xsl:when>
      <xsl:otherwise>
    	<xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="ENCODED-DISPLAY-QUERY">
    <xsl:value-of select="java:encode($DISPLAY-QUERY)"/>
  </xsl:variable>

  <xsl:variable name="DATABASE">
        <xsl:value-of select="/PAGE/DBMASK"/>
  </xsl:variable>

  <xsl:variable name="DATABASE-ID">
    <xsl:value-of select="//SESSION-DATA/DATABASE/ID"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-TYPE">
    <xsl:choose>
      <xsl:when test="($CURRENT-VIEW='selectedset')">
        <xsl:value-of select="/PAGE/SEARCH-TYPE"/>
      </xsl:when>
      <xsl:when test="($TAG-SEARCH-FLAG='true')">
        <xsl:value-of select="//SEARCH-TYPE"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="//SESSION-DATA/SEARCH-TYPE"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="PERSONALIZATION">
    <xsl:value-of select="//PERSONALIZATION"/>
  </xsl:variable>

  <xsl:variable name="PERSONALIZATION-PRESENT">
        <xsl:value-of select="//PERSONALIZATION-PRESENT"/>
  </xsl:variable>

  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/CitationResults_V7.js"/>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>

    <!-- Results manager -->
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <tr><td valign="top" height="4" colspan="3"><img src="/engresources/images/s.gif" height="4" /></td></tr>
        <tr><td valign="top">

        <table border="0" width="100%" cellspacing="0" cellpadding="0" align="center">
            <tr><td valign="top" bgcolor="#3173B5" height="1" colspan="3"><img src="/engresources/images/s.gif" height="1" /></td></tr>
            <tr><td valign="top" width="1" bgcolor="#3173B5"><img src="/engresources/images/s.gif" width="1" /></td><td valign="top">

                <table border="0" width="100%" cellspacing="0" cellpadding="0">
                    <tr><td colspan="4">
                    <table border="0" cellspacing="0" cellpadding="0" width="100%">
                        <tr><td valign="middle" bgcolor="#3173B5"><a CLASS="SmWhiteText"><b>&#160; Results Manager</b></a></td></tr>
                    </table>
                    </td></tr>

	<xsl:variable name="RESULTS-NAV">
		<xsl:value-of select="/PAGE/PAGE-NAV/RESULTS-NAV"/>
	</xsl:variable>
	<xsl:variable name="NEWSEARCH-NAV">
		<xsl:value-of select="/PAGE/PAGE-NAV/NEWSEARCH-NAV"/>
	</xsl:variable>

            <xsl:if test="($CURRENT-VIEW='searchresults')">
                    <tr><td colspan="4" height="4"><img src="/engresources/images/s.gif" height="4" /></td></tr>
                    <tr>
                      <FORM name="{$LOCATION}selectrangeform">
                        <INPUT TYPE="HIDDEN" NAME="searchquery" VALUE="{$ENCODED-DISPLAY-QUERY}"/>
                        <INPUT TYPE="HIDDEN" NAME="database" VALUE="{$DATABASE}"/>
                        <INPUT TYPE="HIDDEN" NAME="sessionid" VALUE="$SESSIONID"/>
                        <INPUT TYPE="HIDDEN" NAME="searchid" VALUE="{$SEARCH-ID}"/>
                        <INPUT TYPE="HIDDEN" NAME="pagesize" VALUE="{$RESULTS-PER-PAGE}"/>
                        <INPUT TYPE="HIDDEN" NAME="resultscount" VALUE="{$RESULTS-COUNT}"/>
                        <INPUT TYPE="HIDDEN" NAME="searchtype" VALUE="{$SEARCH-TYPE}"/>
                        <INPUT TYPE="HIDDEN" NAME="currentpage" VALUE="{$CURRENT-PAGE}"/>
                        <INPUT TYPE="HIDDEN" NAME="databaseid" VALUE="{$DATABASE}"/>
                        <input type="hidden" name="dbpref" value="{$DB-PREF}"/>
                        <input type="hidden" name="fieldpref" value="{$FIELD-PREF}"/>
                        <input type="hidden" name="DupFlag" value="{$DEDUP}"/>
                        <input type="hidden" name="origin" value="{$ORIGIN}"/>
                        <input type="hidden" name="dbLink" value="{$DBLINK}"/>
                        <input type="hidden" name="linkSet" value="{$LINKSET}"/>
                        <input type="hidden" name="tagSearchFlag" value="{$TAG-SEARCH-FLAG}"/>
                        <input type="hidden" name="scope" value="{$SCOPE}"/>

                       <!-- &amp;database={$DEFAULT-DB-MASK}&amp;searchtype=tagSearch -->
                        <td width="5"><img src="/engresources/images/s.gif" width="5" /></td>
                        <td valign="top" colspan="3">
                            <a CLASS="MedBlueLink" href="javascript:selectUnselectAllRecords('markall')">Select all on page</a>
                            <A CLASS="MedBlackText">&#160; - &#160;</A>
                            <a CLASS="SmBlackText"><label for="{$LOCATION}txtFrm">Select range:</label><span class="SmBlackText">
                            <input id="{$LOCATION}txtFrm" type="text" name="selectrangefrom" CLASS="SmBlackText" size="3"/></span><label for="{$LOCATION}txtTo"> to </label><span class="SmBlackText">
                            <input id="{$LOCATION}txtTo" type="text" name="selectrangeto" CLASS="SmBlackText" size="3"/></span></a>&#160;
                            <a href="javascript:selectedRange(document.forms.{$LOCATION}selectrangeform,document.forms.{$LOCATION}resultsmanagerform)">
                            <img align="top" src="/engresources/images/go.gif" border="0" /></a>
                            <A CLASS="MedBlackText">&#160; - &#160;</A>
                            <a CLASS="MedBlueLink" href="javascript:selectUnselectAllRecords('unmarkall')">Clear all on page</a>
                            <A CLASS="MedBlackText">&#160; - &#160;</A>
                            <a CLASS="MedBlueLink" href="javascript:clearAllSelectedRecords('clearall','$SESSIONID',{$RESULTS-COUNT})">Clear all selections</a>
                        </td>
                    </FORM>
                  </tr>
            </xsl:if>
                    <tr><td colspan="4" height="4"><img src="/engresources/images/s.gif" height="4" /></td></tr>

                <FORM name="{$LOCATION}resultsmanagerform">
                    <tr><td valign="top" width="5" height="20"><img src="/engresources/images/s.gif" width="5" /></td><td width="19" height="16">
                    <a href="javascript:makeUrl('Managing_Your_Results.htm')">
		    	    <img src="/engresources/images/blue_help.gif" border="0"/>
	            </a>
                    </td><td><A CLASS="SmBlackText"><b>Choose format: </b></A>
                        <input id="{$LOCATION}rdCit" type="radio" name="selectoption" value="citation" onClick="setOppositeSelectOptionValues(document.{$LOCATION}resultsmanagerform,document.{$OPPOSITE-LOCATION}resultsmanagerform)">
                        <xsl:if test="boolean($DISPLAY-FORMAT='citation')">
                         <xsl:attribute name="checked">checked</xsl:attribute>
                        </xsl:if>
                    </input>
                    <A CLASS="SmBlackText"><label for="{$LOCATION}rdCit">Citation</label></A>&#160;

                    <input id="{$LOCATION}rdAbs" type="radio" name="selectoption" value="abstract" onClick="setOppositeSelectOptionValues(document.{$LOCATION}resultsmanagerform,document.{$OPPOSITE-LOCATION}resultsmanagerform)">
                    <xsl:if test="boolean($DISPLAY-FORMAT='abstract')">
                       <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                    </input>
                    <A CLASS="SmBlackText"><label for="{$LOCATION}rdAbs">Abstract</label></A>&#160;

                    <input id="{$LOCATION}rdDet" type="radio" name="selectoption" value="detailed" onClick="setOppositeSelectOptionValues(document.{$LOCATION}resultsmanagerform,document.{$OPPOSITE-LOCATION}resultsmanagerform)">
                    <xsl:if test="boolean($DISPLAY-FORMAT='detailed')">
                       <xsl:attribute name="checked">checked</xsl:attribute>
                    </xsl:if>
                    </input>
                    <A CLASS="SmBlackText"><label for="{$LOCATION}rdDet">Detailed record</label></A>

                    <xsl:if test="not($CURRENT-VIEW='savedrecords')">
                        &#160; &#160; &#160;  &#160; &#160;
                        <input id="{$LOCATION}chkClr" type="checkbox" name="cbclear" onClick="setOppositeClearOnSearchValue(document.{$LOCATION}resultsmanagerform,document.{$OPPOSITE-LOCATION}resultsmanagerform,'{$SESSION-ID}')">
                    <xsl:if test="($CLEAR-ON-VALUE='true')"><xsl:attribute name="checked">checked</xsl:attribute></xsl:if>
                    </input>
                        <a CLASS="SmBlackText"><label for="{$LOCATION}chkClr">Clear selected records on new search</label></a>
                    </xsl:if>
                    </td>
                    <td valign="top" width="5" height="20"><img src="/engresources/images/s.gif" width="5" /></td>
                    </tr>
                </FORM>

                    <tr><td valign="top" colspan="4" height="4"><img src="/engresources/images/s.gif" height="4" /></td></tr>
                    <tr><td valign="top" width="5" height="20"><img src="/engresources/images/s.gif" width="5" /></td><td valign="top">&#160; </td><td valign="top" height="20" colspan="2">&#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160;

            <!-- the view, email, print, download and save method signatures
                are different for searchresults, selectedset, savedrecords -->
            <xsl:if test="($CURRENT-VIEW='searchresults')">

              <A href="javascript:viewFormat('$SESSIONID','{$DATABASE}',document.{$LOCATION}resultsmanagerform, '{$RESULTS-NAV}', '{$NEWSEARCH-NAV}')">
                    <img src="/engresources/images/vs.gif" border="0" /></A>&#160; &#160;

              <A href="javascript:emailFormat('$SESSIONID','{$SEARCH-TYPE}','{$SEARCH-ID}',{$CURRENT-PAGE},{$RESULTS-COUNT},'{$DATABASE}','{$DATABASE-ID}',{$RESULTS-PER-PAGE},document.{$LOCATION}resultsmanagerform)">
                    <img src="/engresources/images/em.gif" border="0" /></A>&#160; &#160;

                    <A href="javascript:printFormat('$SESSIONID' , '{$DATABASE}' , document.{$LOCATION}resultsmanagerform)">
                    <img src="/engresources/images/pr.gif" border="0" /></A>&#160; &#160;

                    <A href="javascript:downloadFormat('$SESSIONID','{$DATABASE}',document.{$LOCATION}resultsmanagerform)">
                    <img src="/engresources/images/dw.gif" border="0" /></A>&#160; &#160;


                    <xsl:if test="($PERSONALIZATION-PRESENT='true')">

                  <xsl:variable name="NEXTURL">CID=viewSavedFolders&amp;database=<xsl:value-of select="$DATABASE"/>&amp;count=<xsl:value-of select="$CURRENT-PAGE"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;source=selectedset</xsl:variable>
                            <A>
                    <xsl:attribute name="HREF">
                            <xsl:choose>
                                <xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=viewSavedFolders&amp;database=<xsl:value-of select="$DATABASE"/>&amp;backurl=<xsl:value-of select="$BACKURL"/>&amp;source=selectedset</xsl:when>
                                <xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$CURRENT-PAGE"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/>&amp;backurl=<xsl:value-of select="$BACKURL"/></xsl:otherwise>
                            </xsl:choose>
                    </xsl:attribute>
                    <img src="/engresources/images/sv.gif" border="0" />
                            </A>
                    </xsl:if>

            </xsl:if>

            <xsl:if test="($CURRENT-VIEW='selectedset')">

                <xsl:variable name="BASKET-COUNT">
                    <xsl:value-of select="/PAGE/BASKET-NAVIGATION/PAGE-INDEX"/>
                </xsl:variable>


              	<xsl:variable name="COUNT">
                    <xsl:value-of select="/PAGE/SEARCH-RESULTS/PAGE-INDEX"/>
                </xsl:variable>

                    <A href="javascript:viewSelectedSetFormat('$SESSIONID',{$BASKET-COUNT},'{$DATABASE}','{$RESULTS-NAV}','{$NEWSEARCH-NAV}',document.{$LOCATION}resultsmanagerform)">
                    <img src="/engresources/images/vs.gif" border="0"/></A>&#160; &#160;

                    <A href="javascript:emailSelectedSetFormat('$SESSIONID',{$BASKET-COUNT},'{$SEARCH-TYPE}','{$SEARCH-ID}','{$COUNT}','{$RESULTS-COUNT}','{$DATABASE}','{$DATABASE-ID}',document.{$LOCATION}resultsmanagerform)">
                    <img src="/engresources/images/em.gif" border="0"/></A>&#160; &#160;

                    <!-- this link for displaying the documents in the printerfriendly format based on the selected format which calls the javascript function -->
                    <A href="javascript:printSelectedSetFormat('$SESSIONID' , document.{$LOCATION}resultsmanagerform)">
                    <img src="/engresources/images/pr.gif" border="0"/></A>&#160; &#160;

                    <A href="javascript:downloadFormat('$SESSIONID',{$BASKET-COUNT},'{$SEARCH-TYPE}','{$SEARCH-ID}','{$COUNT}','{$RESULTS-COUNT}','{$DATABASE}','{$DATABASE-ID}',document.{$LOCATION}resultsmanagerform)">
                    <img src="/engresources/images/dw.gif" border="0"/></A>&#160; &#160;

                    <xsl:if test="($PERSONALIZATION-PRESENT='true')">

                <xsl:variable name="NEXTURL">CID=viewSavedFolders&amp;database=<xsl:value-of select="$DATABASE"/>&amp;count=<xsl:value-of select="$CURRENT-PAGE"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;source=selectedset</xsl:variable>
                        <A>
                <xsl:attribute name="HREF">
                        <xsl:choose>
                            <xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=viewSavedFolders&amp;database=<xsl:value-of select="$DATABASE"/>&amp;backurl=<xsl:value-of select="$BACKURL"/>&amp;source=selectedset</xsl:when>
                            <xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$CURRENT-PAGE"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/>&amp;backurl=<xsl:value-of select="$BACKURL"/></xsl:otherwise>
                        </xsl:choose>
                </xsl:attribute>
                <img src="/engresources/images/sv.gif" border="0" />
                        </A>
                    </xsl:if>

                    &#160; &#160; &#160; &#160; &#160; &#160;<A href="/controller/servlet/Controller?CID=deleteAllFromSelectedSet&amp;SEARCHID={$SEARCH-ID}&amp;SEARCHTYPE={$SEARCH-TYPE}&amp;COUNT={$COUNT}&amp;DATABASETYPE={$DATABASE}&amp;DATABASEID={$DATABASE-ID}">
              <img src="/engresources/images/removeall.gif" border="0"/></A>

            </xsl:if>

            <xsl:if test="($CURRENT-VIEW='savedrecords')">

                <xsl:variable name="FOLDER-ID">
                    <xsl:value-of select="/PAGE/FOLDER-ID"/>
                </xsl:variable>

                <xsl:variable name="SESSION-DATABASE-ID">
                  <xsl:value-of select="//DBMASK"/>
                </xsl:variable>


                                <!-- this link for displaying the documents in the selected format which calls the javascript function -->
                                <A href="javascript:updateDisplayOfSavedRecords('$SESSIONID','{$FOLDER-ID}','{$SESSION-DATABASE-ID}',document.{$LOCATION}resultsmanagerform)">
                                <img src="/engresources/images/updatefolder.gif" border="0"/></A>&#160; &#160;

                                <A href="javascript:emailSavedRecordsFormat('$SESSIONID','{$FOLDER-ID}',document.{$LOCATION}resultsmanagerform)">
                                <img src="/engresources/images/em.gif" border="0"/></A>&#160; &#160;

                                <!-- this link for displaying the documents in the printerfriendly format based on the selected format which calls the javascript function -->
                                <A href="javascript:printSavedRecords('$SESSIONID','{$FOLDER-ID}',document.{$LOCATION}resultsmanagerform)">
                                <img src="/engresources/images/pr.gif" border="0"/></A>&#160; &#160;

                                <A href="javascript:downloadSavedRecords('$SESSIONID','{$FOLDER-ID}',document.{$LOCATION}resultsmanagerform)">
                                <img src="/engresources/images/dw.gif" border="0"/></A>&#160; &#160;  &#160; &#160; &#160; &#160;

                                <A href="/controller/servlet/Controller?CID=deleteAllFromFolder&amp;format=citation&amp;folderid={$FOLDER-ID}&amp;database={$SESSION-DATABASE-ID}">
                <img src="/engresources/images/removeall.gif" border="0"/></A>

            </xsl:if>

                    </td></tr>
                    <tr><td valign="top" colspan="4" height="4"><img src="/engresources/images/s.gif" height="4" /></td></tr>
                </table>

            </td><td valign="top" width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1" /></td></tr>
            <tr><td valign="top" bgcolor="#3173b5" height="1" colspan="3"><img src="/engresources/images/s.gif" height="1" /></td></tr>
        </table>


        </td></tr>
    </table>

    <!-- bury this form at bottom of page to avoid it causing extra spacing -->
    <xsl:if test="($LOCATION = 'bottom')">
      <FORM NAME="selectUnselectAllRecords" >
        <INPUT TYPE="HIDDEN" NAME="searchquery" VALUE="{$ENCODED-DISPLAY-QUERY}"/>
        <INPUT TYPE="HIDDEN" NAME="sessionid" VALUE="$SESSIONID"/>
        <INPUT TYPE="HIDDEN" NAME="database" VALUE="{$DATABASE}"/>
        <INPUT TYPE="HIDDEN" NAME="searchid" VALUE="{$SEARCH-ID}"/>
        <INPUT TYPE="HIDDEN" NAME="pagesize" VALUE="{$RESULTS-PER-PAGE}"/>
        <INPUT TYPE="HIDDEN" NAME="resultscount" VALUE="{$RESULTS-COUNT}"/>
      </FORM>
    </xsl:if>

</xsl:template>

</xsl:stylesheet>