<?xml version="1.0" ?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java html xsl"
>
  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  <xsl:strip-space elements="html:* xsl:*" />

  <xsl:include href="Header.xsl" />
  <xsl:include href="GlobalLinks.xsl" />
  <xsl:include href="Footer.xsl" />
  <xsl:include href="DeDupControl.xsl" />
  <xsl:include href="TagBubble.xsl" />
  <xsl:include href="AbstractDetailedNavigationBar.xsl" />
  <xsl:include href="DedupAbstractDetailedNavigationBar.xsl" />
  <xsl:include href="TagSearchAbstractDetailedNavigationBar.xsl" />
  <xsl:include href="AbstractDetailedResultsManager.xsl" />

  <xsl:include href="LocalHolding.xsl" />

  <xsl:include href="common/DetailedResults.xsl" />

<xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID" />
    </xsl:variable>

    <xsl:variable name="SEARCH-ID">
        <xsl:value-of select="SEARCH-ID"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
	<xsl:value-of select="DBMASK" />
    </xsl:variable>

    <xsl:variable name="SEARCH-TYPE">
        <xsl:value-of select="SESSION-DATA/SEARCH-TYPE"/>
    </xsl:variable>

    <html>
    <head>
        <title><xsl:value-of select="$SEARCH-TYPE"/> Search Detailed Format</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/lindaHall.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Autocomplete.js"/>

    	<xsl:text disable-output-escaping="yes">
    	<![CDATA[
    	<xsl:comment>
       <script language="javascript">
                function printFormat(sessionid,searchtype,searchid,database,databaseid)
                {

                    var i=0;
                    var url = null;
                    var docidstring  = "&docidlist=";
                    var handlestring = "&handlelist=";
                    var hiddensize = document.quicksearchresultsform.elements.length;

                    for(var i=0 ; i < hiddensize ; i++)
                    {
                        var nameOfElement = document.quicksearchresultsform.elements[i].name;
                        var valueOfElement = document.quicksearchresultsform.elements[i].value;

                        if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
                        {
                        handlestring += valueOfElement ;
                        }

                        if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
                        {
                        docidstring += valueOfElement ;
                        }
                    }

                    url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=printDetailedSelectedRecords&searchid="+searchid+"&database="+database+"&displayformat=detailed"+docidstring+handlestring;
                    new_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
                    new_window.focus();

                }

                // THIS FUNCTION BASICALLY CONSTRUCT ALL THE REQUIRED PARAMETERS FOR EMAIL.THE VALUES SO CONSTRUCTED ARE SENT TO
                //EMAIL FORM.(emailSelectedRecords,emailSelectedFormatResults.jsp)
                function emailFormat(sessionid,searchtype,searchid,database,databaseid)
                {

                    var i=0;
                    var url = null;
                    var docidstring  = "&docidlist=";
                    var handlestring = "&handlelist=";
                    var hiddensize = document.quicksearchresultsform.elements.length;

                    for(var i=0 ; i < hiddensize ; i++)
                    {
                        var nameOfElement = document.quicksearchresultsform.elements[i].name;
                        var valueOfElement = document.quicksearchresultsform.elements[i].value;

                        if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
                        {
                        handlestring += valueOfElement ;
                        }

                        if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
                        {
                        docidstring += valueOfElement ;
                        }
                    }

                    url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=emailForm&searchid="+searchid+"&database="+database+"&displayformat=detailed&absfullselected=true"+docidstring+handlestring;
                    new_window=window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
                    new_window.focus();

                }


                function downloadFormat(sessionid,searchtype,searchid,database,databaseid)
                {

                    var i=0;
                    var url = null;
                    var docidstring  = "&docidlist=";
                    var handlestring = "&handlelist=";
                    var hiddensize = document.quicksearchresultsform.elements.length;

                    for(var i=0 ; i < hiddensize ; i++)
                    {
                        var nameOfElement = document.quicksearchresultsform.elements[i].name;
                        var valueOfElement = document.quicksearchresultsform.elements[i].value;

                        if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
                        {
                        handlestring += valueOfElement ;
                        }

                        if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
                        {
                        docidstring += valueOfElement ;
                        }
                    }

                    // jam 9/27/2002
                    // displayformat is not detailed - but fullDoc for downloading
                    // DownloadForm.xsl will use this value for CID creation
                    // changed from detailed
                    url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=downloadform&searchid="+searchid+"&database="+database+"&displayformat=fullDoc&absfullselected=true"+docidstring+handlestring;
                    new_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
                    new_window.focus();

                }

                //Script for saved records format
                function savedrecordsFormat(sessionid,searchtype,searchid,database,databaseid,displayformat,source)
                {

                        var i=0;
                        var url = null;
                        var docidstring  = "&docidlist=";
                        var handlestring = "&handlelist=";
                        var hiddensize = document.quicksearchresultsform.elements.length;
                        var cbcheckvalue= false;
                        if(document.quicksearchresultsform.cbresult.checked)
                        {
                            cbcheckvalue = true;
                        }


                        // jam 10/4/2002
                        // bug - single view of document can be 'Saved to Folder'
                        // regardless of whether or not the document is selected (in basket)
                        //if(cbcheckvalue)
                        //{
                            for(var i=0 ; i < hiddensize ; i++)
                            {
                                var nameOfElement = document.quicksearchresultsform.elements[i].name;
                                var valueOfElement = document.quicksearchresultsform.elements[i].value;


                                if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
                                {
                                handlestring += valueOfElement ;
                                }

                                if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
                                {
                                docidstring += valueOfElement ;
                                }
                            }
                        //}

                        if(displayformat == 'addrecord')
                        {
                            url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=viewSavedFolders&databaseid="+databaseid+docidstring+"&database="+database;
                        }
                        else
                        {
                            url="/controller/servlet/Controller?EISESSION="+sessionid+"&CID=personalLoginForm&displaylogin=true&displayform=addrecords&database="+database+"&databaseid="+databaseid+"&source="+source+docidstring;
                        }
                        NewWindow = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
                        NewWindow.focus();
                     }

                // This function called when you want to add the document to the selected set and when
                // you want to delete the document from the selected set.In this two
                // functions will be performed based on checkbox checked value.If the checked value is true
                // the document will add to the selected set otherwise the document will be removed from
                // the selected set.
                function selectUnselectRecord(cbno,handle,docid,searchquery,database,sessionid,searchid)
                {
                  var now = new Date() ;
                  var milli = now.getTime() ;
                  var img = new Image() ;
                  var cbcheck = document.quicksearchresultsform.cbresult.checked;
                  if(cbcheck)
                  {
                      document.images['image_basket'].src="/engresources/Basket.jsp?select=mark&handle="+handle+"&docid="+docid+"&database="+escape(database)+"&sessionid="+sessionid+"&searchquery="+escape(searchquery)+"&searchid="+searchid+"&timestamp="+ milli;
                  } else {
                      document.images['image_basket'].src="/engresources/Basket.jsp?select=unmark&handle="+handle+"&docid="+docid+"&database="+escape(database)+"&sessionid="+sessionid+"&searchquery="+escape(searchquery)+"&searchid="+searchid+"&timestamp="+ milli ;
                  }
                 }

        </script>

        </xsl:comment>

        ]]>

        </xsl:text>

        <!-- End of javascript -->

    </head>
      <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
        <xsl:apply-templates select="HEADER">
          <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
        </xsl:apply-templates>
        <center>
          <!-- Insert the Global Link table -->
          <xsl:apply-templates select="GLOBAL-LINKS">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
            <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
            <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
          </xsl:apply-templates>
          <!-- Insert the navigation bar -->
          <xsl:apply-templates select="PAGE-NAV"/>
          <table border="0" width="99%" cellspacing="0" cellpadding="0">
            <tr>
              <td valign="top">
                <!-- Insert the Results manager -->
            		<xsl:apply-templates select="ABSTRACT-DETAILED-RESULTS-MANAGER">
                  <xsl:with-param name="FORMAT">DETAILED</xsl:with-param>
            		</xsl:apply-templates>
              	<xsl:apply-templates select="PAGE-RESULTS"/>
              </td>
            </tr>
          </table>
        </center>
        <!-- Insert the Footer -->
        <xsl:apply-templates select="FOOTER">
          <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
          <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        </xsl:apply-templates>
        <br/>
      </body>
    </html>
    </xsl:template>

    <xsl:template match="PAGE-RESULTS">
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
      	<xsl:apply-templates select="PAGE-ENTRY"/>
      </table>
    </xsl:template>

    <xsl:variable name="SEARCH-TYPE">
      <xsl:value-of select="//SESSION-DATA/SEARCH-TYPE"/>
    </xsl:variable>

    <xsl:variable name="CITATION-CID">
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchCitationFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Book')">quickSearchCitationFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchCitationFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Combined')">expertSearchCitationFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Thesaurus')">quickSearchCitationFormat</xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="ABSTRACT-CID">
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchAbstractFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Book')">quickSearchAbstractFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchAbstractFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Combined')">expertSearchAbstractFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Thesaurus')">quickSearchAbstractFormat</xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="DETAILED-CID">
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchDetailedFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Book')">quickSearchDetailedFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchDetailedFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Combined')">expertSearchDetailedFormat</xsl:when>
        <xsl:when test="($SEARCH-TYPE='Thesaurus')">quickSearchDetailedFormat</xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:template match="PAGE-ENTRY">

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

    	<xsl:variable name="SEARCH-CONTEXT">
    		<xsl:value-of select="/PAGE/SEARCH-CONTEXT" />
    	</xsl:variable>

    	<xsl:variable name="DISPLAY-QUERY">
    		<xsl:choose>
    			<xsl:when test="($SEARCH-CONTEXT='tag')">
    				[Tag] <xsl:value-of select="/PAGE/ALT-DISPLAY-QUERY" />
    			</xsl:when>
    			<xsl:otherwise>
    				<xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/>
    			</xsl:otherwise>
    		</xsl:choose>
    	</xsl:variable>

      <xsl:variable name="ENCODED-DISPLAY-QUERY">
        <xsl:value-of select="java:encode($DISPLAY-QUERY)"/>
      </xsl:variable>

      <xsl:variable name="LAST-FOUR-UPDATES">
        <xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/>
      </xsl:variable>

      <xsl:variable name="SELECTED-DB">
        <xsl:value-of select="//SESSION-DATA/DATABASE/SHORTNAME"/>
      </xsl:variable>
      <xsl:variable name="ENCODED-DATABASE">
        <xsl:value-of select="java:encode($SELECTED-DB)"/>
      </xsl:variable>

      <xsl:variable name="DATABASE">
        <xsl:value-of select="EI-DOCUMENT/DOC/DB/DBNAME"/>
      </xsl:variable>
      <xsl:variable name="DATABASE-ID">
        <xsl:value-of select="EI-DOCUMENT/DOC/DB/ID"/>
      </xsl:variable>

      <xsl:variable name="INDEX">
        <xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/>
      </xsl:variable>

      <xsl:variable name="DOC-ID">
        <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
      </xsl:variable>

    	<xsl:variable name="DEDUP">
  	    <xsl:value-of select="//DEDUP" />
    	</xsl:variable>

      <tr>
        <td valign="top" align="left" colspan="7">
          <xsl:choose>
            <xsl:when test="not(/PAGE/HIDE-NAV)">
              <a CLASS="SmBlueText">Record <xsl:value-of select="$INDEX"/> from <xsl:value-of select="$DATABASE" /> for: <xsl:value-of select="$DISPLAY-QUERY" /></a>
  			      <xsl:choose>
          	    <xsl:when test="string(//SESSION-DATA/EMAILALERTWEEK)"><a CLASS="SmBlueText">, Week <xsl:value-of select="//SESSION-DATA/EMAILALERTWEEK"/></a></xsl:when>
          	    <xsl:when test="not($LAST-FOUR-UPDATES='')"><a CLASS="SmBlueText">, Last <xsl:value-of select="$LAST-FOUR-UPDATES"/> update(s)</a></xsl:when>
          	    <xsl:when test="//SESSION-DATA/SEARCH-TYPE!='Combined'">
          	    	<xsl:if test="not($SEARCH-CONTEXT='tag')">
      	        	  <a CLASS="SmBlueText">, <xsl:value-of select="//PAGE/SESSION-DATA/START-YEAR"/>-<xsl:value-of select="//PAGE/SESSION-DATA/END-YEAR"/></a>
          	    	</xsl:if>
          	    </xsl:when>
            	</xsl:choose>
            </xsl:when>
          </xsl:choose>
        </td>
      </tr>
      <tr>
        <td colspan="7"><img src="/engresources/images/s.gif" border="0" height="10"/></td>
      </tr>
      <tr>
        <td align="left" colspan="7"><a CLASS="SmBlackText">Check record to add to Selected Records</a></td>
      </tr>
      <tr>
        <td colspan="7"><img src="/engresources/images/s.gif" border="0" height="10"/></td>
      </tr>
      <tr>
        <td valign="top" align="right">
          <xsl:variable name="IS-SELECTED"><xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/IS-SELECTED" disable-output-escaping="yes"/></xsl:variable>
          <xsl:variable name="CB-NO"><xsl:number/></xsl:variable>
          <form name="quicksearchresultsform">
         		<input type="hidden" name="HANDLE">
        			<xsl:attribute name="value">
        				<xsl:value-of select="$INDEX" />
        			</xsl:attribute>
        		</input>
        		<input type="hidden" name="DOC-ID">
        			<xsl:attribute name="value">
        				<xsl:value-of select="$DOC-ID" />
        			</xsl:attribute>
        		</input>
            <input type="checkbox" name="cbresult" onClick="selectUnselectRecord( {$CB-NO},{$INDEX},'{$DOC-ID}','{$ENCODED-DISPLAY-QUERY}','{$DATABASE-ID}','$SESSIONID','{$SEARCH-ID}')">
              <xsl:if test="$IS-SELECTED='true'">
                <xsl:attribute name="checked">checked</xsl:attribute>
              </xsl:if>
            </input>
          </form>
        </td>
        <td valign="top"><img src="/engresources/images/s.gif" border="0" width="3" name="image_basket"/></td>
        <td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</a></td>
        <td valign="top"><img src="/engresources/images/s.gif" border="0" width="4"/></td>
        <td valign="top" width="100%" align="left">
          <xsl:apply-templates select="EI-DOCUMENT" />
        </td>
        <!-- THIS DEFINES TWO MORE COLUMNS IN THIS TABLE -->
        <xsl:if test="not($SEARCH-CONTEXT='dedup')">
        	<xsl:if test="//GLOBAL-LINKS/TAGGROUPS">
            <xsl:apply-templates select="/PAGE/TAG-BUBBLE"/>
        	</xsl:if>
        </xsl:if>
      </tr>
<!--  end of ajax -->
<!-- end -->
      <xsl:if test="(string(//LOCALHOLDINGS) or string(//FULLTEXT) or string(//LHL))">
        <tr>
            <td colspan="4" ><img src="/engresources/images/s.gif" height="15" border="0"/></td>
            <td bgcolor="#C3C8D1" colspan="3">
              <a CLASS="MedBlackText"><b>&#160; Full-text and Local Holdings Links</b></a>
            </td>
        </tr>
        <tr>
          <td colspan="7" ><img src="/engresources/images/s.gif" border="0"/></td>
        </tr>
      </xsl:if>

        <xsl:variable name="LOCALHOLDINGS">
          <xsl:value-of select="//LOCALHOLDINGS"/>
        </xsl:variable>
        <xsl:if test="($LOCALHOLDINGS='true')">
          <tr>
            <td colspan="4"><img src="/engresources/images/s.gif" border="0" width="3"/></td>
            <td align="left" colspan="3">
              <xsl:apply-templates select="//LOCAL-HOLDINGS" >
                <xsl:with-param name="vISSN"><xsl:value-of select="EI-DOCUMENT/SN"/></xsl:with-param>
              </xsl:apply-templates>
            </td>
          </tr>
        </xsl:if>

        <xsl:variable name="FULLTEXT">
          <xsl:value-of select="//FULLTEXT"/>
        </xsl:variable>

        <xsl:variable name="FULLTEXT-LINK">
          <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/FT/@FTLINK"/>
        </xsl:variable>

        <xsl:choose>
          <xsl:when test="(($FULLTEXT='true') and ($FULLTEXT-LINK = 'Y')) or ($FULLTEXT-LINK = 'A')">
          <tr>
            <td colspan="4" ><img src="/engresources/images/s.gif" border="0" height="30"/></td>
            <td align="left" colspan="3">
              <a title="Full-text" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false"><img id="ftimg" src="/engresources/images/av.gif" alt="Full-text" /></a>
            </td>
          </tr>
          </xsl:when>
          <xsl:otherwise>
            <tr>
              <td colspan="7"><img src="/engresources/images/s.gif" border="0" height="30"/></td>
            </tr>
          </xsl:otherwise>
        </xsl:choose>

        <xsl:variable name="LHL">
          <xsl:value-of select="//LHL"/>
        </xsl:variable>
        <xsl:if test="($LHL='true')">
          <tr>
            <td colspan="4" ><img src="/engresources/images/s.gif" border="0" width="3"/></td>
            <td align="left" colspan="3" >
              <a CLASS="MedBlueLink" href="javascript:lindaHall('$SESSIONID','{$DOC-ID}','{$DATABASE-ID}')">Linda Hall Library document delivery service</a>
            </td>
          </tr>
        </xsl:if>

    </xsl:template>

</xsl:stylesheet>

