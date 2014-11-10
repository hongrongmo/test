<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:DD="org.ei.domain.DatabaseDisplayHelper"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  exclude-result-prefixes="java html xsl DD xslcid"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template match="SESSION-TABLE">
  <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Robohelp.js"/>

  <xsl:text disable-output-escaping="yes">
      <![CDATA[
      <xsl:comment>
        <script language="javascript">

        /*
          This function called when you want to add the document to the SavedSearches and when
          you want to delete/updates the document from the SavedSearches.In this two
          functions will be performed based on checkbox checked value and Image src.If the checked value is true
          the document will add to the selected set otherwise the document will be removed from
          the selected set.Ig image is saved or unmark it addes to savedSearches otherwise it deletes.
        */

        function selectUnselectEmailAlert(cbno,databaseid,historycount,searchid,sessionid,option)
        {
          var now = new Date() ;
          var milli = now.getTime() ;
          var img = new Image() ;
          var cbcheck = false;
          var ecount=document.searchhistoryform.alertcount.value;
          var scount=document.searchhistoryform.searchcount.value;
          var cbsize;

          var EMAIL_ALERT_COUNT = 125;
          var SAVED_SEARCH_COUNT = 125;


          if(databaseid!='CRC' && databaseid!='USPTO')
          {
            cbsize=document.searchhistoryform.emailalert.length;

            if( historycount == 1)
            {
              // window.alert("inside if");
              cbcheck = document.searchhistoryform.emailalert.checked;
            }
            else
            {
              // window.alert("inside else");
              cbcheck = document.searchhistoryform.emailalert[cbno-1].checked;
            }
          }

          //window.alert(ecount+":"+scount);
          var url_str = document.images['image'+cbno].src ;

          if(option=='EmailAlert')
          {
            if(cbcheck)
            {

              if((parseInt(ecount)+1) > EMAIL_ALERT_COUNT)
              {
                window.alert("You have reached the maximum number of Email Alerts that can be saved.");
                if( historycount == 1)
                {
                  document.searchhistoryform.emailalert.checked = false;
                }
                else
                {
                  document.searchhistoryform.emailalert[cbno-1].checked = false;
                }
                return false;
              }

              //window.alert("entered into adding to the saved searches");
              //when image is save_blue.gif and checkbox is checked
              if(url_str.indexOf("/save_blue.gif") != -1 || url_str.indexOf("unmark") != -1)
              {
                if((parseInt(scount)+1) > SAVED_SEARCH_COUNT)
                {
                  window.alert("You have reached the maximum number of Searches that can be saved.");
                  if( historycount == 1)
                  {
                    document.searchhistoryform.emailalert.checked = false;
                  }
                  else
                  {
                    document.searchhistoryform.emailalert[cbno-1].checked = false;
                  }
                  return false;
                }

                document.searchhistoryform.alertcount.value=parseInt(ecount)+1;
                document.searchhistoryform.searchcount.value=parseInt(scount)+1;

                //window.alert("entered into adding to the saved searches:"+ecount+":"+scount);
                document.images['image'+cbno].src="/engresources/Searches.jsp?"+
                  "select=mark&emailoption=On&sessionid="+sessionid+"&databaseid="+escape(databaseid)+"&option="+option+"&searchid="+searchid+"&timestamp="+milli;
              }
              else
              {
                document.searchhistoryform.alertcount.value=parseInt(ecount)+1;
                document.searchhistoryform.searchcount.value=parseInt(scount);
                //window.alert("entered into updating email to the saved searches:"+ecount+":"+scount);
                document.images['image'+cbno].src="/engresources/Searches.jsp?"+
                "select=mark&emailoption=emailon&sessionid="+sessionid+"&databaseid="+escape(databaseid)+"&option="+option+"&searchid="+searchid+"&timestamp="+milli;
              }
            }
            else
            {
              document.searchhistoryform.alertcount.value=parseInt(ecount)-1;
              document.searchhistoryform.searchcount.value=parseInt(scount);

              //when image is saved_blue.gif and checkbox is uncheacked
              //window.alert(" deleting from the basket:"+ecount+":"+scount);
              document.images['image'+cbno].src="/engresources/Searches.jsp?"+
                "select=mark&emailoption=emailoff&sessionid="+sessionid+"&databaseid="+escape(databaseid)+"&option="+option+"&searchid="+searchid+"&timestamp="+milli;
            }
          }
          if(option=='SavedSearch')
          {
            if (url_str.indexOf("/save_blue.gif") != -1 || url_str.indexOf("unmark") != -1)
            {
              if((parseInt(scount)+1) > SAVED_SEARCH_COUNT)
              {
                window.alert("You have reached the maximum number of Searches that can be saved.");
                //return false;
              }
              else
              {
                if(databaseid!='CRC'  && databaseid!='USPTO')
                {
                  document.searchhistoryform.alertcount.value=parseInt(ecount);
                }
                document.searchhistoryform.searchcount.value=parseInt(scount)+1;

                //window.alert("inside If of saved search:"+ecount+":"+scount);
                document.images['image'+cbno].src="/engresources/Searches.jsp?select=mark&emailoption=Off&sessionid="+sessionid+"&databaseid="+escape(databaseid)+"&option="+option+"&searchid="+searchid+"&timestamp="+milli;
              }
            }
            else
            {
              //document.searchhistoryform.alertcount.value=parseInt(ecount)-1;
              document.searchhistoryform.searchcount.value=parseInt(scount)-1;
              //window.alert("inside else of saved search:"+ecount+":"+scount);
              if( historycount == 1)
              {
                if(databaseid!='CRC'  && databaseid!='USPTO')
                {
                  if(document.searchhistoryform.emailalert.checked)
                  {
                    document.searchhistoryform.alertcount.value=parseInt(ecount)-1;
                  }
                  else
                  {
                    document.searchhistoryform.alertcount.value=parseInt(ecount);
                  }
                  document.searchhistoryform.emailalert.checked = false;
                }
              }
              else
              {
                if(databaseid!='CRC'  && databaseid!='USPTO')
                {
                  if(document.searchhistoryform.emailalert.checked)
                  {
                      document.searchhistoryform.alertcount.value=parseInt(ecount)-1;
                  }
                  else
                  {
                    document.searchhistoryform.alertcount.value=parseInt(ecount);
                  }
                  document.searchhistoryform.emailalert[cbno-1].checked = false;
                }
              }
              document.images['image'+cbno].src="/engresources/Searches.jsp?select=unmark&emailoption=Off&sessionid="+sessionid+"&databaseid="+escape(databaseid)+"&option="+option+"&searchid="+searchid+"&timestamp="+milli;
            }
          }
        }
      </script>
      </xsl:comment>
      ]]>
  </xsl:text>
  <!-- end of javascript -->
  <xsl:param name="COMPLETE-SEARCH-HISTORY"/>

  <xsl:variable name="SESSION-ID">
    <xsl:value-of select="//SESSION-ID"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="//SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="/PAGE/CURR-PAGE-ID"/>
  </xsl:variable>

  <xsl:variable name="DATABASENAME">
    <xsl:value-of select="//SESSION-DATA/DATABASE/NAME"/>
  </xsl:variable>

  <xsl:variable name="DATABASE-MASK-PAGE">
    <xsl:value-of select="/PAGE/DBMASK"/>
  </xsl:variable>

  <xsl:variable name="CURR-SEARCH-TYPE">
    <xsl:value-of select="//SESSION-DATA/SEARCH-TYPE"/>
  </xsl:variable>

  <xsl:variable name="PERSONALIZATION">
    <xsl:value-of select="//PERSONALIZATION"/>
  </xsl:variable>

  <xsl:variable name="CUSTOMIZED-PERSONALIZATION">
    <xsl:value-of select="//PERSONALIZATION-PRESENT"/>
  </xsl:variable>

  <xsl:variable name="EMAILALERTS-PRESENT">
    <xsl:value-of select="//EMAILALERTS-PRESENT"/>
  </xsl:variable>

  <xsl:variable name="BACKURL">
    <xsl:value-of select="/PAGE/BACKURL"/>
  </xsl:variable>

  <xsl:variable name="USER-ID">
    <xsl:value-of select="//PERSON-USER-ID" />
  </xsl:variable>

<xsl:variable name="SESSION">
  <xsl:choose>
    <xsl:when test="($COMPLETE-SEARCH-HISTORY='true')">/engvillage/models/customer/sessionHistory.jsp?sessionId=<xsl:value-of select="$SESSION-ID"/>&amp;personalization=<xsl:value-of select="$PERSONALIZATION"/>&amp;userId=<xsl:value-of select="$USER-ID"/>&amp;completehistory=true</xsl:when>
    <xsl:otherwise>/engvillage/models/customer/sessionHistory.jsp?sessionId=<xsl:value-of select="$SESSION-ID"/>&amp;personalization=<xsl:value-of select="$PERSONALIZATION"/>&amp;userId=<xsl:value-of select="$USER-ID"/></xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:variable name="CID">
  <xsl:choose>
    <xsl:when test="($DATABASE-MASK-PAGE='8' or $DATABASE-MASK-PAGE='16')">remoteRedirect</xsl:when>
    <xsl:otherwise><xsl:value-of select="xslcid:searchResultsCid($CURR-SEARCH-TYPE)"/></xsl:otherwise>
  </xsl:choose>
</xsl:variable>


<!-- Start of Session history -->
<center>
  <xsl:variable name="SEACHHISTORYCOUNT"><xsl:value-of select="count(/PAGE/SESSION-HISTORY/SESSION-DATA)"/></xsl:variable>

  <form name="searchhistoryform">
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
      <xsl:variable name="EMAIL-ALERT-COUNT"><xsl:value-of select="/PAGE/SESSION-HISTORY/EMAIL-ALERT-COUNT"/></xsl:variable>
      <xsl:variable name="SAVEDSEARCHES-COUNT"><xsl:value-of select="/PAGE/SESSION-HISTORY/SAVEDSEARCHES-COUNT"/></xsl:variable>

      <input type="hidden" name="alertcount">
        <xsl:attribute name="value"><xsl:value-of select="$EMAIL-ALERT-COUNT"/></xsl:attribute>
      </input>
      <input type="hidden" name="searchcount">
          <xsl:attribute name="value"><xsl:value-of select="$SAVEDSEARCHES-COUNT"/></xsl:attribute>
      </input>

      <tr>
        <td valign="top" align="left" colspan="3">
          <a class="EvHeaderText">Search History</a> &#160;
           <a href="javascript:makeUrl('Session_Information.htm')">
	  <img src="/static/images/blue_help.gif" border="0"/>
	   </a>
        </td>
      </tr>
      <tr>
        <td valign="top" height="3" colspan="7">
          <img src="/static/images/s.gif" border="0" height="3"/>
        </td>
      </tr>

      <tr>
        <td valign="top" width="1" bgcolor="#3173B5">
          <img src="/static/images/s.gif" width="1"/>
        </td>
        <td valign="top">

        <table border="0" width="100%" cellspacing="0" cellpadding="0">
        <tr>
          <td valign="top" bgcolor="#3173B5" height="1" colspan="21">
            <img src="/static/images/s.gif" height="1"/>
          </td>
        </tr>
        <tr>
          <td valign="top" width="4" bgcolor="#3173B5">
            <img src="/static/images/s.gif" width="4"/>
          </td>
          <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>No.</b></A></td>
          <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
          <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Type</b></A></td>
          <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
          <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Search</b></A><img src="/static/images/s.gif" height="1" width="125"/></td>
          <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
          <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Autostem</b></A></td>
          <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
          <td valign="top" bgcolor="#3173B5"><img src="/static/images/s.gif" width="12" /></td>
          <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Sort</b></A></td>
          <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
          <td valign="top" bgcolor="#3173B5"><A CLASS="SmWhiteText"><b>Results</b></A></td>
          <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
          <td valign="top" bgcolor="#3173B5" width="75"><A CLASS="SmWhiteText"><b>Year(s)</b></A></td>
          <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
          <td valign="top" bgcolor="#3173B5" width="90"><A CLASS="SmWhiteText"><b>Database</b></A></td>
          <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
          <td valign="top" bgcolor="#3173B5" width="88"><A CLASS="SmWhiteText"><b>Email Alert</b></A></td>
          <td valign="top" width="4" bgcolor="#3173B5"><img src="/static/images/s.gif"/></td>
          <td valign="top" bgcolor="#3173B5" width="88"><A CLASS="SmWhiteText"><b>Save Search</b></A></td>
        </tr>
        <tr>
          <td valign="top" colspan="21" height="15"><img src="/static/images/s.gif" height="15"/></td>
        </tr>

        <xsl:for-each select="/PAGE/SESSION-HISTORY/SESSION-DATA">

          <!-- Show the most recent three search quieries in the session history -->

          <xsl:variable name="START-YEAR"><xsl:value-of select="START-YEAR"/></xsl:variable>
          <xsl:variable name="END-YEAR"><xsl:value-of select="END-YEAR"/></xsl:variable>
          <xsl:variable name="AUTOSTEMMING"><xsl:value-of select="AUTOSTEMMING"/></xsl:variable>
          <xsl:variable name="SEARCH-TYPE"><xsl:value-of select="SEARCH-TYPE"/></xsl:variable>
          <xsl:variable name="LAST-FOUR-UPDATES"><xsl:value-of select="LASTFOURUPDATES"/></xsl:variable>
          <xsl:variable name="DATABASE"><xsl:value-of select="DATABASE/NAME"/></xsl:variable>
          <xsl:variable name="DATABASE-ID"><xsl:value-of select="DATABASE/ID"/></xsl:variable>
          <xsl:variable name="QUERY-ID"><xsl:value-of select="QUERY-ID"/></xsl:variable>
          <xsl:variable name="YEAR-RANGE"><xsl:value-of select="YEAR-RANGE"/></xsl:variable>
          <xsl:variable name="DATABASE-MASK"><xsl:value-of select="DATABASE-MASK"/></xsl:variable>
          <xsl:variable name="EMAILALERT"><xsl:value-of select="EMAILALERTWEEK"/></xsl:variable>
          <xsl:variable name="RESULTS-COUNT">
            <xsl:choose>
              <xsl:when test="RESULTS-COUNT &lt; 0">0</xsl:when>
              <xsl:otherwise><xsl:value-of select="RESULTS-COUNT"/></xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <xsl:variable name="DISPLAY-QUERY">
            <xsl:choose>
              <xsl:when test="count(REFINE-STACK/NAVIGATOR/MODIFIER) = 1"><xsl:value-of select="REFINE-STACK/NAVIGATOR/MODIFIER/LABEL"/></xsl:when>
              <xsl:otherwise><xsl:value-of select="DISPLAY-QUERY"/></xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <xsl:variable name="SAVEDSEARCH"><xsl:value-of select="SAVEDSEARCH"/></xsl:variable>
          <xsl:variable name="EMAIL-ALERT"><xsl:value-of select="EMAIL-ALERT"/></xsl:variable>
          <xsl:variable name="SERIAL-NUMBER"><xsl:value-of select="SERIAL-NUMBER"/></xsl:variable>

          <xsl:variable name="CID2">
          <xsl:choose>
            <xsl:when test="boolean($DATABASE-MASK='8')">uspto<xsl:value-of select="$SEARCH-TYPE"/></xsl:when>
            <xsl:when test="boolean($DATABASE-MASK='16')">engnetbase<xsl:value-of select="$SEARCH-TYPE"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/></xsl:otherwise>
          </xsl:choose>
          </xsl:variable>

          <tr>
            <xsl:variable name="SEARCH-INDEX"><xsl:number/></xsl:variable>
            <td valign="top" width="4"><img src="/static/images/s.gif" width="4"/></td>
            <td valign="top"><A CLASS="MedBlackText">
              <xsl:choose>
                <xsl:when test="boolean(string-length(normalize-space($SERIAL-NUMBER))>0)">
                  <xsl:value-of select ="$SERIAL-NUMBER"/>.
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select ="$SEARCH-INDEX"/>.
                </xsl:otherwise>
              </xsl:choose>
            </A></td>
            <td valign="top" width="4"><img src="/static/images/s.gif"/></td>
            <td valign="top"><A CLASS="MedBlackText"><xsl:value-of select ="$SEARCH-TYPE"/></A></td>
            <td valign="top" width="4"><img src="/static/images/s.gif"/></td>

            <td valign="top">
                <a class="LgBlueLink" href="/controller/servlet/Controller?CID={$CID2}&amp;searchid={$QUERY-ID}&amp;navigator=PREV&amp;COUNT=1&amp;database={$DATABASE-MASK}">
                  <xsl:value-of select="$DISPLAY-QUERY"/>
                </a>
                <xsl:if test="DUPSET/CRITERIA/DBPREF">
                  <table border="0" width="100%" cellspacing="0" cellpadding="0">
                    <tr>
                        <td align="left" colspan="3"><a CLASS="SmBlackText">Deduplicated Sets</a></td>
                    </tr>
                  <xsl:for-each select="DUPSET/CRITERIA">
                    <xsl:variable name="FIELDPREF">
                      <xsl:value-of select="FIELDPREF"/>
                    </xsl:variable>
                    <xsl:variable name="DBPREF">
                      <!-- <xsl:value-of select="DBPREF"/> -->
                      <xsl:choose>
                        <xsl:when test="(DBPREF='1')">cpx</xsl:when>
                        <xsl:when test="(DBPREF='2')">ins</xsl:when>
                        <xsl:when test="(DBPREF='8192')">geo</xsl:when>
                        <xsl:when test="(DBPREF='2097152')">grf</xsl:when>
                        <xsl:when test="(DBPREF='128')">chm</xsl:when>
                        <xsl:when test="(DBPREF='64')">pch</xsl:when>
                        <xsl:when test="(DBPREF='1024')">elt</xsl:when>                        
                        <xsl:otherwise>cpx</xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>
                    <xsl:variable name="PREF-FIELD">
                      <xsl:choose>
                        <xsl:when test="($FIELDPREF='0')">No Field</xsl:when>
                        <xsl:when test="($FIELDPREF='1')">Abstract</xsl:when>
                        <xsl:when test="($FIELDPREF='2')">Index Terms</xsl:when>
                        <xsl:when test="($FIELDPREF='4')">Full Text</xsl:when>
                        <xsl:otherwise>No Field</xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>
                    <tr>
                        <td >
                            <a class="SrtLink"><xsl:attribute name="href">/search/results/dedup.url?CID=dedup&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;COUNT=1&amp;database=<xsl:value-of select="$DATABASE-MASK"/>&amp;origin=history&amp;fieldpref=<xsl:value-of select="$FIELDPREF"/>&amp;dbpref=<xsl:value-of select="$DBPREF"/></xsl:attribute><xsl:number/>.</a>
                        </td>
                        <td >
                        <a CLASS="SmBlackText"><xsl:value-of select="$PREF-FIELD"/></a>
                        </td>
                        <td >
                            <a CLASS="SmBlackText"><xsl:value-of select="DD:getDisplayName(DBPREF)"/></a>
                        </td>
                    </tr>
                </xsl:for-each>
                </table>
              </xsl:if>
            </td>

            <td valign="top" width="4"><img src="/static/images/s.gif"/></td>
            <td valign="top">
              <!-- jam 5/16/05 - Added Autostemming to expert search -->
              <!-- bug fix - do not show Autostemming value only if search type is/was Thesaurus -->
              <xsl:choose>
                <xsl:when test="($SEARCH-TYPE='Thesaurus')">
                  <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:when>
                <xsl:when test="boolean($AUTOSTEMMING='on')">
                  <A CLASS="MedBlackText">On</A>
                </xsl:when>
                <xsl:when test="boolean($AUTOSTEMMING='off')">
                  <A CLASS="MedBlackText">Off</A>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </td>
            <td valign="top" width="4"><img src="/static/images/s.gif"/></td>

            <!-- jam SORT show arrow and replace field name with display label -->
            <td align="middle" valign="top" ><img src="/static/images/{SORT-DIRECTION}ar.gif"/></td>
            <td valign="top"><a CLASS="MedBlackText">
            <xsl:variable name="sortfield"><xsl:value-of select="./SORT-OPTION"/></xsl:variable>
            <xsl:value-of select="/PAGE/SORTABLE-FIELDS/SORTBYFIELD[@value=$sortfield]"/></a></td>
            <td valign="top" width="4"><img src="/static/images/s.gif" width="4"/></td>
            <td valign="top" ><A CLASS="MedBlackText"><xsl:value-of select="$RESULTS-COUNT"/></A></td>
            <td valign="top" width="4"><img src="/static/images/s.gif" width="4"/></td>
            <td valign="top">
              <!-- jam 8/19/2003 - change Emailalerts to display 'Week..' String -->
              <!-- jam 8/19/2003 - changed A(nchor) to SPAN -->
              <span CLASS="MedBlackText">
              <xsl:choose>
                <xsl:when test="($DATABASE-MASK='8') or ($DATABASE-MASK='16')">
                  <!-- do nothing -->
                  <xsl:text>-</xsl:text>
                </xsl:when>
                <xsl:when test="($SEARCH-TYPE='Combined')">
                  <!-- do nothing -->&#160;
                </xsl:when>
                <xsl:when test="string($EMAILALERT)">
                    Week <xsl:value-of select="$EMAILALERT"/>
                </xsl:when>
                <!-- Display all years for the USPTO -->
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
            <td valign="top" width="4"><img src="/static/images/s.gif"/></td>
            <td valign="top">
              <A CLASS="MedBlackText">
              <xsl:value-of select="DD:getDisplayName($DATABASE-MASK)"/>
              </A>
            </td>
            <td valign="top" width="4"><img src="/static/images/s.gif"/></td>
            <td valign="top">
              <xsl:if test="($CUSTOMIZED-PERSONALIZATION='true')">
                <xsl:choose>
                  <xsl:when test="not($DATABASE-MASK='8') and not($DATABASE-MASK='16') and ($EMAILALERTS-PRESENT='true')">
                    <xsl:choose>
                      <xsl:when test="($PERSONALIZATION='true')">
                        <!-- LOGGED IN to personalization NO CHANGES for NOV 2004 release -->
                        <input type="checkbox" name="emailalert" onClick="selectUnselectEmailAlert({$SEARCH-INDEX},'{$DATABASE-MASK}',{$SEACHHISTORYCOUNT},'{$QUERY-ID}','{$SESSION-ID}','EmailAlert')">
                          <xsl:if test="(boolean($EMAIL-ALERT='On'))">
                            <xsl:attribute name="CHECKED"/>
                          </xsl:if>
                        </input>
                      </xsl:when>
                      <xsl:otherwise>
                        <!-- NOT logged in to personalization Nov 2004 changes made here -->
                        <!-- jam - QUERY-ID is the id of this history element
                          SEARCH-ID is the id of the search the user may
                          have executed last -->
                        <!-- this will contain the redirect for saving the alert -->
                        <xsl:variable name="NEXTURL">CID=addDeleteSavedSearch&amp;selectvalue=mark&amp;option=EmailAlert&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>

                        <input type="checkbox" name="emailalert">
                          <xsl:attribute name="onClick">javascript:document.location='/controller/servlet/Controller?EISESSION=<xsl:value-of select="$SESSION-ID"/>&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$CURRENT-PAGE"/>&amp;searchtype=<xsl:value-of select="$CURR-SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE-MASK"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/>&amp;backurl=<xsl:value-of select="$BACKURL"/>'</xsl:attribute>
                        </input>

                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:when>
                  <xsl:otherwise>&#160;<input type="hidden" name="emailalert" /></xsl:otherwise>
                </xsl:choose>
              </xsl:if>
            </td>
            <td valign="top" width="4"><img src="/static/images/s.gif"/></td>
            <td valign="top">
              <xsl:if test="($CUSTOMIZED-PERSONALIZATION='true')">
                <xsl:choose>
                  <xsl:when test="($PERSONALIZATION='true')">
                    <!-- LOGGED IN to personalization NO CHANGES for NOV 2004 release -->
                    <a href="javascript:selectUnselectEmailAlert({$SEARCH-INDEX},'{$DATABASE-MASK}',{$SEACHHISTORYCOUNT},'{$QUERY-ID}','{$SESSION-ID}','SavedSearch')">
                    <xsl:choose>
                      <xsl:when test="($SAVEDSEARCH='On')">
                        <img src="/static/images/saved_blue.gif" border="0">
                        <xsl:attribute name="name"><xsl:value-of select="concat('image',$SEARCH-INDEX)"/></xsl:attribute>
                        </img>
                      </xsl:when>
                      <xsl:otherwise>
                        <img src="/static/images/save_blue.gif" border="0">
                        <xsl:attribute name="name"><xsl:value-of select="concat('image',$SEARCH-INDEX)"/></xsl:attribute>
                        </img>
                      </xsl:otherwise>
                    </xsl:choose>
                    </a>
                  </xsl:when>
                  <xsl:otherwise>
                    <!-- NOT logged in to personalization Nov 2004 changes made here -->
<!-- jam - QUERY-ID is the id of this history element
  SEARCH-ID is the id of the search the user may
  have executed last -->
                    <!-- this will contain the redirect for saving the search -->
                    <xsl:variable name="NEXTURL">CID=addDeleteSavedSearch&amp;selectvalue=mark&amp;option=SavedSearch&amp;searchid=<xsl:value-of select="$QUERY-ID"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:variable>

                    <a>
                      <xsl:attribute name="HREF">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$CURRENT-PAGE"/>&amp;searchtype=<xsl:value-of select="$CURR-SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE-MASK"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/>&amp;backurl=<xsl:value-of select="$BACKURL"/></xsl:attribute>
                      <img src="/static/images/save_blue.gif" border="0">
                        <xsl:attribute name="name"><xsl:value-of select="concat('image',$SEARCH-INDEX)"/></xsl:attribute>
                      </img>
                    </a>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:if>
            </td>
          </tr>
        </xsl:for-each>

      <tr>
        <td valign="top" colspan="21" height="15"><img src="/static/images/s.gif" height="15"/></td>
      </tr>
      <xsl:choose>
      <xsl:when test="boolean($COMPLETE-SEARCH-HISTORY='true')">
        <tr>
          <td valign="top">&#160;</td>
          <td valign="top" colspan="10" align="left">
            <xsl:if test="boolean($SEACHHISTORYCOUNT>0)">
            <a href="/controller/servlet/Controller?CID=clearSearchHistory&amp;COUNT={$CURRENT-PAGE}&amp;database={$DATABASE-MASK-PAGE}&amp;searchid={$SEARCH-ID}&amp;searchResultsFormat={$CID}&amp;SEARCHTYPE={$CURR-SEARCH-TYPE}"><img src="/static/images/clearsessionhistory.gif" border="0"/></a>
            </xsl:if>
          </td>
          <td valign="top" align="right" colspan="10">
            <xsl:if test="($CUSTOMIZED-PERSONALIZATION='true')">
              <xsl:choose>
                <xsl:when test="boolean($PERSONALIZATION='true')">
                  <a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=viewSavedSearches&amp;database={$DATABASE-MASK-PAGE}">View Saved Searches</a>&#160; &#160;
                </xsl:when>
                <xsl:when test="boolean($PERSONALIZATION='false')">
                    <xsl:variable name="NEXTURL">CID=viewSavedSearches&amp;database=<xsl:value-of select="$DATABASE-MASK-PAGE"/></xsl:variable>
                    <a CLASS="LgBlueLink">
                    <xsl:attribute name="HREF">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$CURRENT-PAGE"/>&amp;searchtype=<xsl:value-of select="$CURR-SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE-MASK-PAGE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/></xsl:attribute>View Saved Searches</a>&#160; &#160;
                </xsl:when>
              </xsl:choose>
            </xsl:if>
          </td>
        </tr>

        </xsl:when>
        <xsl:otherwise>
          <tr>
          <td valign="top">&#160; </td>
          <td valign="top" colspan="20" align="left">
            <A CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=viewCompleteSearchHistory&amp;COUNT={$CURRENT-PAGE}&amp;searchid={$SEARCH-ID}&amp;database={$DATABASE-MASK-PAGE}&amp;SEARCHTYPE={$CURR-SEARCH-TYPE}&amp;searchResultsFormat={$CID}">View Complete Search History</A>
          </td>
        </tr>
       </xsl:otherwise>
       </xsl:choose>



       <tr>
        <td valign="top" colspan="21" height="8"><img src="/static/images/s.gif" height="8"/></td>
       </tr>
       <tr>
        <td valign="top" bgcolor="#3173B5" height="1" colspan="21"><img src="/static/images/s.gif" height="1"/></td>
       </tr>
    </table>
  </td>
  <td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/s.gif" width="1"/></td>
  </tr>
  <tr>
    <td valign="top" height="8" colspan="3"><img src="/static/images/s.gif" height="8" border="0"/></td>
  </tr>
</table>
</form>

</center>
<!-- End of Session History -->

</xsl:template>

</xsl:stylesheet>