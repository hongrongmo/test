<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:svdsrch="java:org.ei.domain.personalization.SavedSearches"
    xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
    exclude-result-prefixes="java html xsl DD xslcid"
>

<!-- including xsl for header-->
<xsl:include href="Header.xsl"/>

<!-- including xsl for Navigation bar-->
<xsl:include href="NavigationBar.xsl" />
<!-- including xsl for Global links-->
<xsl:include href="GlobalLinks.xsl" />
<!-- including xsl for footer-->
<xsl:include href="Footer.xsl" />

<xsl:template match="PAGE">
  <!-- these are variable getting from xml-->
  <xsl:variable name="RESULTS-DATABASE">
    <xsl:value-of select="DBMASK"/>
  </xsl:variable>

  <xsl:variable name="SESSIONID">
    <xsl:value-of select="SESSION-ID"/>
  </xsl:variable>

  <xsl:variable name="CUSTOMIZED-PERSONALIZATION">
    <xsl:value-of select="PERSONALIZATION-PRESENT"/>
  </xsl:variable>

  <xsl:variable name="EMAILALERTS-PRESENT">
    <xsl:value-of select="EMAILALERTS-PRESENT"/>
  </xsl:variable>

  <xsl:variable name="CCEMAILALERTS-PRESENT">
    <xsl:value-of select="CCEMAILALERTS-PRESENT"/>
  </xsl:variable>

  <xsl:variable name="SHOW">
    <xsl:value-of select="SHOW"/>
  </xsl:variable>


  <xsl:variable name="TITLE">
    <xsl:choose>
      <xsl:when test="($SHOW = 'alerts')">My Alerts</xsl:when>
      <xsl:otherwise>My Saved Searches</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="HEADERTEXT">
    <xsl:choose>
      <xsl:when test="($SHOW = 'alerts')">My Alerts</xsl:when>
      <xsl:otherwise>My Saved Searches and Email Alerts</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <html>
  <head>
    <title><xsl:value-of select="$TITLE"/></title>

    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
    <xsl:text disable-output-escaping="yes">
    <![CDATA[
      <xsl:comment>
        <script language="javascript">
            var EMAIL_ALERT_COUNT = 125;
            var SAVED_SEARCH_COUNT = 125;

      function openWindowAddEdit(display,sessionid,queryid,database)
      {
        var now = new Date() ;
        var milli = now.getTime() ;
        var url = null;

        if( display == 'editcclist' )
        {
          url="/controller/servlet/Controller?EISESSION="+sessionid+"&CID=editcclist&editdisplay=true&database="+database+"&savedsearchid="+queryid+"&timestamp="+milli;
          NewWindow = window.open(url,'NewWindow','status=yes,resizable,scrollbars=1,width=450,height=450');
          NewWindow.focus();
        }

       }


            // wrapper to call to open window test to see if
            // someone has clicked on empty spaer gif when email alert is
            // not selected (function openWindow is in Header.xsl)
            function openCCWindow(display,sessionid,queryid,database)
            {
              if(typeof(document.images[queryid]) != 'undefined')
              {
                if(document.images[queryid].src.indexOf("/cc") != -1)
                {
                  openWindowAddEdit(display,sessionid,queryid,database);
                }
              }
            }

            /*
              This function called when you want to updates the emailAlert of the SavedSearches.
              In this functions will be performed based on checkbox checked value .
              If the checked value is true  then emailAlert will add to the savedSearch otherwise
              emailAlert will be removed from  the savedSearches.
            */

            function selectUnselectEmailAlert(cbno,searchid,historycount,sessionid,option)
            {
              var now = new Date() ;
              var milli = now.getTime() ;
              var img = new Image() ;
              var cbcheck = false;
              var checkboxcount=0;
              var cbsize = document.searchhistoryform.emailalert.length;

              if( historycount == 1)
              {
                  cbcheck = document.searchhistoryform.emailalert.checked;
              }
              else
              {
                  cbcheck = document.searchhistoryform.emailalert[cbno-1].checked;
              }

              for(var i=0;i<cbsize;i++)
              {
                if(document.searchhistoryform.emailalert[i].checked)
                {
                   if(document.searchhistoryform.emailalert[i].type !='hidden')
                   {
                      ++checkboxcount;
                   }
                }
              }

              if(option=='EmailAlert')
              {
                  if(cbcheck)
                  {
                    if(checkboxcount > EMAIL_ALERT_COUNT)
                    {
                      window.alert("You have reached the maximum number of Email Alerts that can be saved.");
                      document.searchhistoryform.emailalert[cbno-1].checked=false;
                      return false;
                    }
                    else
                    {
                      document.images['saved_searches'].src="/engresources/Searches.jsp?"+
                          "select=marksearches&emailoption=emailon&sessionid="+sessionid+"&searchid="+searchid+"&option="+option+"&timestamp="+milli;
                      if(typeof(document.images[searchid]) != 'undefined')
                      {
                        document.images[searchid].src="/static/images/cco.gif";
                      }
                    }
                  }
                  else
                  {
                    //when image is saved_blue.gif and checkbox is uncheacked
                    //window.alert(" deleting from the saved seaches");
                    document.images['saved_searches'].src="/engresources/Searches.jsp?"+
                          "select=marksearches&emailoption=emailoff&sessionid="+sessionid+"&searchid="+searchid+"&option="+option+"&timestamp="+milli;

                    if(typeof(document.images[searchid]) != 'undefined')
                    {
                      document.images[searchid].src="/static/images/spacer.gif";
                    }
                  }
              }
            }
            function confirmClear(database, showalerts)
            {
              var confirmMessage;
              if(showalerts=='alerts')
              {
                confirmMessage = 'You are about to unselect all email alerts.';
              }
              else
              {
                confirmMessage = 'You are about to erase all saved searches and email alerts.'
              }
              if(confirm(confirmMessage) == true)
              {
                document.location = '/controller/servlet/Controller?EISESSION=$SESSIONID&CID=removeSavedSearch&option=all&database=' + database +"&show="+showalerts;
                return true;
              }
            }
        </script>
      </xsl:comment>
      ]]>
      </xsl:text>

  <!-- Java script ends -->

  </head>

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

  <xsl:variable name="SEARCHTYPE"><xsl:value-of select="SESSION-DATA/SEARCH-TYPE"/></xsl:variable>

  <xsl:apply-templates select="HEADER">
    <xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
    <xsl:with-param name="SEARCH-TYPE" select="$SEARCHTYPE"/>
    <xsl:with-param name="COMBINED-SEARCH">SavedSearches</xsl:with-param>
  </xsl:apply-templates>

  <xsl:apply-templates select="GLOBAL-LINKS">
    <xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
  </xsl:apply-templates>

  <!-- empty navigation bar -->
  <center>
  <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
  <tr>
  <td height="24" bgcolor="#C3C8D1" ><img src="/static/images/s.gif" /></td>
  </tr>
  </table>
  </center>

  <!-- End of logo table -->

  <xsl:variable name="NUMSEARCHES"><xsl:value-of select="count(SESSION-HISTORY/SESSION-DATA)"/></xsl:variable>

  <xsl:choose>
  <xsl:when test="boolean($NUMSEARCHES > 0)">
    <!-- Start of saved searches -->
    <center>
    <form name="searchhistoryform">
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <tr>
            <td valign="top" colspan="3" height="15">
                 <img src="/static/images/s.gif" border="0" height="15"/>
            </td>
        </tr>
        <tr>
            <td valign="top" align="left" colspan="3">
              <a class="EvHeaderText"><xsl:value-of select="$HEADERTEXT"/></a>
            </td>
        </tr>
        <tr><td valign="top" height="3" colspan="3"><img src="/static/images/spacer.gif" border="0" height="3"/></td></tr>

        <tr>
            <td valign="top" width="1" bgcolor="#3173B5">
                <img src="/static/images/s.gif" border="0" width="1"/>
            </td>
            <td valign="top">
            <table border="0" width="100%" cellspacing="0" cellpadding="0">

                <tr>
                    <td valign="top" bgcolor="#3173B5" height="1" colspan="22">
                        <img src="/static/images/s.gif" border="0" height="1"/>
                    </td>
                </tr>
                <tr>
                <td valign="top" width="2" bgcolor="#3173B5">
                <img src="/static/images/s.gif" width="2" border="0"/></td>
                <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>No.</b></A></td>
                <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0"/></td>
                <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Type</b></A></td>
                <td valign="top" width="8" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0"/></td>
                <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Search</b></A></td>
                <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0"/></td>
                <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Autostem</b></A></td>
                <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0"/></td>
                <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Sort</b></A></td>
                <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
                <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Results</b></A></td>
                <td valign="top" width="10" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0" width="10"/></td>
                <td valign="top" bgcolor="#3173B5" width="66"><A CLASS="SmWhiteText"><b>Year(s)</b></A></td>
                <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0"/></td>
                <td valign="top" bgcolor="#3173B5" width="78"><A CLASS="SmWhiteText"><b>Database</b></A></td>
                <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0"/></td>
                <td valign="top" bgcolor="#3173B5" width="66"><A CLASS="SmWhiteText"><b>Email Alert</b></A></td>
                <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0"/></td>
                <td valign="top" bgcolor="#3173B5" width="72"><A CLASS="SmWhiteText"><b>Date Saved</b></A></td>
                <td valign="top" width="2" bgcolor="#3173B5"><img src="/static/images/s.gif" width="2" border="0"/></td>
                <td valign="top" bgcolor="#3173B5"><img src="/static/images/s.gif" height="1"/></td>
                </tr>
                <tr>
                    <td valign="top" colspan="22" height="15"><img src="/static/images/s.gif" border="0" height="15"/></td>
                </tr>

                <xsl:for-each select="SESSION-HISTORY/SESSION-DATA">


                <!-- Show the most recent three search quieries in the session history -->

                <xsl:variable name="SEARCH-PHRASE-1"><xsl:value-of select="SEARCH-PHRASE/SEARCH-PHRASE-1"/></xsl:variable>
                <xsl:variable name="SEARCH-OPTION-1"><xsl:value-of select="SEARCH-PHRASE/SEARCH-OPTION-1"/></xsl:variable>
                <xsl:variable name="SEARCH-PHRASE-2"><xsl:value-of select="SEARCH-PHRASE/SEARCH-PHRASE-2"/></xsl:variable>
                <xsl:variable name="SEARCH-OPTION-2"><xsl:value-of select="SEARCH-PHRASE/SEARCH-OPTION-2"/></xsl:variable>
                <xsl:variable name="SEARCH-PHRASE-3"><xsl:value-of select="SEARCH-PHRASE/SEARCH-PHRASE-3"/></xsl:variable>
                <xsl:variable name="SEARCH-OPTION-3"><xsl:value-of select="SEARCH-PHRASE/SEARCH-OPTION-3"/></xsl:variable>
                <xsl:variable name="BOOLEAN-1"><xsl:value-of select="SEARCH-PHRASE/BOOLEAN-1"/></xsl:variable>
                <xsl:variable name="BOOLEAN-2"><xsl:value-of select="SEARCH-PHRASE/BOOLEAN-2"/></xsl:variable>
                <xsl:variable name="START-YEAR"><xsl:value-of select="START-YEAR"/></xsl:variable>
                <xsl:variable name="END-YEAR"><xsl:value-of select="END-YEAR"/></xsl:variable>
                <xsl:variable name="LANGUAGE"><xsl:value-of select="LANGUAGE"/></xsl:variable>
                <xsl:variable name="AUTOSTEMMING">
                  <xsl:choose>
                      <xsl:when test="(AUTOSTEMMING='on')"><xsl:text>off</xsl:text></xsl:when>
                      <xsl:otherwise><xsl:text>on</xsl:text></xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <xsl:variable name="SEARCH-TYPE"><xsl:value-of select="SEARCH-TYPE"/></xsl:variable>
                <xsl:variable name="DOCUMENT-TYPE"><xsl:value-of select="DOCUMENT-TYPE"/></xsl:variable>
                <xsl:variable name="TREATMENT-TYPE"><xsl:value-of select="TREATMENT-TYPE"/></xsl:variable>
                <xsl:variable name="LAST-FOUR-UPDATES"><xsl:value-of select="LASTFOURUPDATES"/></xsl:variable>
                <xsl:variable name="DATABASE"><xsl:value-of select="DATABASE/NAME"/></xsl:variable>
                <xsl:variable name="DATABASE-MASK"><xsl:value-of select="DATABASE-MASK"/></xsl:variable>
                <xsl:variable name="QUERY-ID"><xsl:value-of select="QUERY-ID"/></xsl:variable>
                <xsl:variable name="DATABASE-SHORTNAME"><xsl:value-of select="DATABASE/SHORTNAME"/></xsl:variable>
              <xsl:variable name="EMAILALERTWEEK"><xsl:value-of select="EMAILALERTWEEK"/></xsl:variable>

                <xsl:variable name="ENCODED-SEARCH-PHRASE-1">
                   <xsl:if test="function-available('java:encode')">
                         <xsl:value-of select="java:encode($SEARCH-PHRASE-1)"/>
                    </xsl:if>
                </xsl:variable>
                <xsl:variable name="ENCODED-SEARCH-PHRASE-2">
                    <xsl:if test="function-available('java:encode')">
                        <xsl:value-of select="java:encode($SEARCH-PHRASE-2)"/>
                    </xsl:if>
                </xsl:variable>
                <xsl:variable name="ENCODED-SEARCH-PHRASE-3">
                    <xsl:if test="function-available('java:encode')">
                        <xsl:value-of select="java:encode($SEARCH-PHRASE-3)"/>
                    </xsl:if>
                </xsl:variable>
                <xsl:variable name="RESULTS-COUNT"><xsl:value-of select="RESULTS-COUNT"/></xsl:variable>
                <xsl:variable name="DISPLAY-QUERY"><xsl:value-of select="DISPLAY-QUERY"/></xsl:variable>
                <xsl:variable name="PHYSICAL-QUERY"><xsl:value-of select="PHYSICAL-QUERY"/></xsl:variable>
                <xsl:variable name="SAVEDSEARCH"><xsl:value-of select="SAVEDSEARCH"/></xsl:variable>
                <xsl:variable name="SAVEDSEARCH-ID"><xsl:value-of select="QUERY-ID"/></xsl:variable>
                <xsl:variable name="SAVEDSEARCH-DATE"><xsl:value-of select="SAVEDSEARCH-DATE"/></xsl:variable>
                <xsl:variable name="EMAIL-ALERT"><xsl:value-of select="EMAIL-ALERT"/></xsl:variable>
                <xsl:variable name="CC-LIST"><xsl:value-of select="CC-LIST"/></xsl:variable>

          <xsl:variable name="ENCODED-DATABASE">
            <xsl:value-of select="java:encode($DATABASE-MASK)"/>
          </xsl:variable>

              <xsl:variable name="REMOTE-QUERY"><xsl:value-of select="REMOTE-QUERY"/></xsl:variable>

                <!-- jam - bug #12.1 year range for USPTO not showing in saved searches screen -->
                <xsl:variable name="YEAR-RANGE"><xsl:value-of select="YEAR-RANGE"/></xsl:variable>

                <tr>
                    <xsl:variable name="SEARCH-INDEX"><xsl:number/></xsl:variable>
                    <td valign="top" width="2"><img src="/static/images/s.gif" width="2" border="0"/></td>
                    <td valign="top"><A CLASS="MedBlackText"><xsl:value-of select ="$SEARCH-INDEX"/>.</A></td>

                    <td valign="top" width="4"><img src="/static/images/s.gif" border="0"/></td>
                    <td valign="top"><A CLASS="MedBlackText"><xsl:value-of select ="$SEARCH-TYPE"/></A></td>

                    <td valign="top" width="8"><img src="/static/images/s.gif" border="0"/></td>

                    <td valign="top">
                        <xsl:choose>
                            <xsl:when test="boolean($DATABASE-MASK='8')">
                              <A CLASS="LgBlueLink" ><xsl:attribute name="href">/controller/servlet/Controller?CID=uspto<xsl:value-of select="$SEARCH-TYPE"/>&amp;database=<xsl:value-of select="$DATABASE-MASK"/>&amp;location=SAVEDSEARCH&amp;searchid=<xsl:value-of select="QUERY-ID"/></xsl:attribute><xsl:value-of select="$DISPLAY-QUERY"/></A>
                            </xsl:when>
                            <xsl:when test="boolean($DATABASE-MASK='16')">
                              <A CLASS="LgBlueLink" ><xsl:attribute name="href">/controller/servlet/Controller?CID=engnetbase<xsl:value-of select="$SEARCH-TYPE"/>&amp;database=<xsl:value-of select="$DATABASE-MASK"/>&amp;location=SAVEDSEARCH&amp;searchid=<xsl:value-of select="QUERY-ID"/></xsl:attribute><xsl:value-of select="$DISPLAY-QUERY"/></A>
                            </xsl:when>
                            <xsl:otherwise>
                              <!-- jam 8/19/2003 removed ISPRESENT tests -->
                              <!-- jam 8/19/2003 added when EMAILALERTWEEK for saved searches -->
                              <!-- URL in this case is the same as 'otherwise' with '&amp;emailweek={$EMAILALERTWEEK}' appended -->
                              <xsl:variable name="SEARCHCID">
                                <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
                              </xsl:variable>
                              <A CLASS="LgBlueLink" href="/controller/servlet/Controller?CID={$SEARCHCID}&amp;location=SAVEDSEARCH&amp;searchid={$QUERY-ID}&amp;navigator=PREV&amp;COUNT=1&amp;database={$ENCODED-DATABASE}"><xsl:value-of select="$DISPLAY-QUERY"/></A>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                    <td valign="top" width="4"><img src="/static/images/s.gif" border="0"/></td>
                    <td valign="top">
                        <!-- jam 5/16/05 - Added Autostemming to expert search -->
                        <!-- bug fix - do not show Autostemming value if search type is/was Thesaurus -->
                        <xsl:choose>
                            <xsl:when test="$DATABASE-MASK='8'">
                                <!-- USPTO - do nothing / no stemming option-->
                            </xsl:when>
                            <xsl:when test="($SEARCH-TYPE='Thesaurus')">
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                            </xsl:when>
                            <xsl:when test="boolean($AUTOSTEMMING='on')">
                                <A CLASS="MedBlackText">Off</A>
                            </xsl:when>
                            <xsl:when test="boolean($AUTOSTEMMING='off')">
                                <A CLASS="MedBlackText">On</A>
                            </xsl:when>
                        </xsl:choose>
                    </td>
                    <td valign="top" width="4"><img src="/static/images/s.gif" border="0"/></td>

                    <!-- jam SORT -->
                    <xsl:variable name="sortfield"><xsl:value-of select="./SORT-OPTION"/></xsl:variable>
                    <td valign="top" >
                    <img src="/static/images/{SORT-DIRECTION}ar.gif"/>
                    <A CLASS="MedBlackText"><xsl:value-of select="/PAGE/SORTABLE-FIELDS/SORTBYFIELD[@value=$sortfield]"/></A>
                    </td>
                    <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>

                    <td valign="top" ><A CLASS="MedBlackText"><xsl:value-of select="$RESULTS-COUNT"/></A></td>
                    <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
                    <td valign="top">
                        <!-- jam 8/19/2003 - change Emailalerts to display 'Week..' String -->
                        <!-- jam 8/19/2003 - changed A(nchor) to SPAN -->
                        <span CLASS="MedBlackText">
                        <xsl:choose>
                            <xsl:when test="boolean($DATABASE-MASK='16' or $DATABASE-MASK='8')">
                                <!-- do nothing -->
                                <xsl:text>-</xsl:text>
                            </xsl:when>
                            <xsl:when test="string($EMAILALERTWEEK)">
                                  Week <xsl:value-of select="$EMAILALERTWEEK"/>
                            </xsl:when>
                            <!-- jam 12/6/2002 USPTO no showing year range #12.1 -->
                            <!-- when clause was missing, see SessionHistory.xsl -->
                            <xsl:when test="string($YEAR-RANGE)">
                                <xsl:value-of select="$YEAR-RANGE"/>
                            </xsl:when>
                            <xsl:when test="string($LAST-FOUR-UPDATES)">
                              Last <xsl:value-of select="$LAST-FOUR-UPDATES"/> update(s)
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$START-YEAR"/>-<xsl:value-of select="$END-YEAR" />
                            </xsl:otherwise>
                        </xsl:choose>
                        </span>

                    </td>
                    <td valign="top" width="4"><img src="/static/images/s.gif" border="0" /></td>
                    <td valign="top"><A CLASS="MedBlackText"><xsl:value-of select="DD:getDisplayName(DATABASE-MASK)"/></A></td>
                    <td valign="top" width="8"><img src="/static/images/s.gif" border="0"/></td>
                    <td valign="top">
                    <xsl:if test="boolean($EMAILALERTS-PRESENT='true')">
                      <xsl:choose>
                        <xsl:when test="boolean($DATABASE-MASK='16' or $DATABASE-MASK='8')">
                          <input type="hidden" name="emailalert" />
                        </xsl:when>
                        <xsl:when test="not(boolean($DATABASE-MASK='16' or $DATABASE-MASK='8'))">
                          <input type="checkbox" name="emailalert" onClick="selectUnselectEmailAlert({$SEARCH-INDEX},'{$QUERY-ID}',{$NUMSEARCHES},'$SESSIONID','EmailAlert')">
                            <xsl:if test="(boolean($EMAIL-ALERT='On'))">
                              <xsl:attribute name="checked"/>
                            </xsl:if>
                          </input>
                            <xsl:if test="boolean($CCEMAILALERTS-PRESENT='true')">
                            <a href="javascript:openCCWindow('editcclist','$SESSIONID','{$QUERY-ID}','{$DATABASE-MASK}')">
                            <img name="{$QUERY-ID}" width="24" height="14" alt="Alert Cc: List" border="0">
                              <xsl:if test="($EMAIL-ALERT='On')">
                                <xsl:if test="($CC-LIST='')">
                                  <xsl:attribute name="src">/static/images/cco.gif</xsl:attribute>
                                </xsl:if>
                                <xsl:if test="not($CC-LIST='')">
                                  <xsl:attribute name="src">/static/images/ccg.gif</xsl:attribute>
                                </xsl:if>
                              </xsl:if>
                              <xsl:if test="not($EMAIL-ALERT='On')">
                                <xsl:attribute name="src">/static/images/spacer.gif</xsl:attribute>
                              </xsl:if>
                            </img>
                            </a>
                          </xsl:if>
                        </xsl:when>
                        <xsl:otherwise>&#160;<input type="hidden" name="emailalert"/></xsl:otherwise>
                      </xsl:choose>
                      <input type="hidden" name="savedsearch-id">
                        <xsl:attribute name="value"><xsl:value-of select="$QUERY-ID"/></xsl:attribute>
                      </input>
                      <img src="/static/images/s.gif" border="0" width="0"/>
                    </xsl:if>
                    </td>
                    <td valign="top" width="4"><img src="/static/images/s.gif" border="0"   name="saved_searches"/></td>
                    <td valign="top">
                        <a CLASS="SmBlackText"><xsl:value-of select="$SAVEDSEARCH-DATE"/></a>
                        <br/>
                        <a href="/controller/servlet/Controller?CID=removeSavedSearch&amp;option=single&amp;savedsearchid={$QUERY-ID}&amp;database={$RESULTS-DATABASE}&amp;show={$SHOW}">
                        <img src="/static/images/remove.gif" border="0"/>
                        </a>
                    </td>

                    <td valign="top" width="2"><img src="/static/images/s.gif" width="2" border="0"/></td>
                </tr>
                </xsl:for-each>

                <tr>
                    <td valign="top" colspan="22" height="15"><img src="/static/images/s.gif" border="0" height="15"/></td>
                </tr>


                <tr>
                    <td valign="top"><img src="/static/images/s.gif" border="0"/></td>
                    <td valign="top" colspan="20" align="left">

                    <xsl:if test="boolean($NUMSEARCHES>0)">
                    <a href="javascript:confirmClear({$RESULTS-DATABASE},'{$SHOW}');"><img src="/static/images/ca.gif" border="0"/></a>
                    </xsl:if>
                    </td>
                    <td valign="top" width="2"><img src="/static/images/s.gif" width="2" border="0"/></td>
               </tr>

                <tr>
                    <td valign="top" colspan="22" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td>
                </tr>
                <tr>
                    <td valign="top" bgcolor="#3173B5" height="1" colspan="22"><img src="/static/images/s.gif" border="0" height="1"/></td>
                </tr>
            </table>
        </td>
        <td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0" width="1"/></td>
        </tr>
        <tr>
            <td valign="top" height="8" colspan="3"><img src="/static/images/s.gif" border="0" height="8"/></td>
        </tr>
    </table>
    </form>

    </center>


    <!-- End of saved searches-->
  </xsl:when>
  <xsl:otherwise>

      <!--When saved searches is empty -->
    <!-- Start of saved searches empty page -->
    <center>
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
            <tr><td valign="top" height="15"><img src="/static/images/s.gif" border="0" height="15"/></td></tr>
            <tr><td valign="top" align="left">
              <a class="EvHeaderText"><xsl:value-of select="$HEADERTEXT"/></a>
            </td></tr>
            <tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
            <tr><td valign="top"><a CLASS="MedBlackText">
            <xsl:choose>
              <xsl:when test="$SHOW='alerts'">You have no Email Alerts.</xsl:when>
              <xsl:otherwise>You have no saved searches.<P>To save a search, go to Search History and click on the "Save" button next to the search query you wish to save. You may save up to <xsl:value-of select="svdsrch:getSavedSearchCount()"/> searches.</P><P>You can also set up Email Alerts for saved searches.</P>
              </xsl:otherwise>
            </xsl:choose>
            <P>An Email alert is a weekly email message with updated search results from your saved search. To create an Email Alert, go to Search History and check the Email Alert checkbox next to the "Save" button. You may create up to <xsl:value-of select="svdsrch:getEmailAlertCount()"/> Email Alerts.</P>
            </a></td></tr>
        </table>
    </center>
    <!-- End of saved searches empty page -->
  </xsl:otherwise>
  </xsl:choose>

  <!-- Insert the Footer table -->
  <xsl:apply-templates select="FOOTER">
    <xsl:with-param name="SESSION-ID" select="$SESSIONID"/>
    <xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
  </xsl:apply-templates>

  </body>
  </html>

</xsl:template>

</xsl:stylesheet>
