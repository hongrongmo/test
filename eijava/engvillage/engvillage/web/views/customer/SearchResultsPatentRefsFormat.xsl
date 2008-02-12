<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet
[
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:decoder="java:java.net.URLDecoder"
  xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
  xmlns:srt="java:org.ei.domain.Sort"
  xmlns:bit="java:org.ei.util.BitwiseOperators"
  xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
  xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
  exclude-result-prefixes="java html xsl DD srt bit xslcid custoptions">

  <xsl:output method="html" indent="no" />
  <xsl:strip-space elements="html:* xsl:*" />
  <xsl:include href="Header.xsl" />
  <xsl:include href="GlobalLinks.xsl" />
  <xsl:include href="AbstractDetailedNavigationBar.xsl" />
  <xsl:include href="template.SEARCH_RESULTS.xsl" />
  <xsl:include href="Footer.xsl" />
  <xsl:include href="DeDupControl.xsl" />
  <xsl:include href="common/CitationResults.xsl" />
  <xsl:include href="LocalHolding.xsl" />

  <xsl:param name="CUST-ID">0</xsl:param>

  <xsl:variable name="CID">
    <xsl:value-of select="//PAGE/CID" />
  </xsl:variable>
  <xsl:variable name="FULLTEXT">
    <xsl:value-of select="//FULLTEXT" />
  </xsl:variable>
  <xsl:variable name="PERSONALIZATION-PRESENT">
    <xsl:value-of select="//PAGE/PERSONALIZATION" />
  </xsl:variable>
  <xsl:variable name="LOCALHOLDINGS-CITATION">
    <xsl:value-of select="//LOCALHOLDINGS-CITATION" />
  </xsl:variable>
  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="//PAGE/CURR-PAGE-ID" />
  </xsl:variable>
  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="//PAGE/SEARCH-ID" />
  </xsl:variable>
  <xsl:variable name="SESSION-ID">
    <xsl:value-of select="//PAGE/SESSION-ID" />
  </xsl:variable>
  <xsl:variable name="DISPLAY-QUERY">
    <xsl:value-of select="//PAGE/SESSION-DATA/DISPLAY-QUERY" />
  </xsl:variable>
  <xsl:variable name="ENCODED-DISPLAY-QUERY">
    <xsl:value-of select="java:encode($DISPLAY-QUERY)" />
  </xsl:variable>

  <xsl:variable name="RESULTS-NAV">
      <xsl:value-of disable-output-escaping="yes" select="/PAGE/PAGE-NAV/RESULTS-NAV" />
  </xsl:variable>

  <xsl:variable name="ENCODED-RESULTS-NAV">
        <xsl:value-of select="java:encode($RESULTS-NAV)" />
  </xsl:variable>


  <xsl:variable name="BACKURL">
    <xsl:value-of select="//PAGE//BACKURL" />
  </xsl:variable>
  <xsl:variable name="DATABASE">
    <xsl:value-of select="//PAGE/DBMASK" />
  </xsl:variable>
  <xsl:variable name="SEARCH-TYPE">
    <xsl:value-of select="//PAGE/SESSION-DATA/SEARCH-TYPE" />
  </xsl:variable>
  <xsl:variable name="CID-PREFIX">
    <xsl:choose>
      <xsl:when test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus')">quickSearch</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">expertSearch</xsl:when>
      <xsl:otherwise>expertSearch</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="MORE-CID">
      <xsl:value-of select="$CID-PREFIX"/>ReferencesFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>
  </xsl:variable>
  <xsl:variable name="ROOT-DOCID">
      <xsl:value-of select="//PAGE//UPT-DOC/EI-DOCUMENT/DOC/DOC-ID"/>
  </xsl:variable>
  <xsl:variable name="RESULTS-COUNT">
    <xsl:value-of select="//RESULTS-COUNT" />
  </xsl:variable>
  <xsl:variable name="SEL-COUNT">
    <xsl:value-of select="//SEL-COUNT" />
  </xsl:variable>
  <xsl:variable name="RESULTS-PER-PAGE">
    <xsl:value-of select="//PAGE/RESULTS-PER-PAGE" />
  </xsl:variable>
  <xsl:template match="PAGE">
  <xsl:variable name="RESULTS-PER-PAGE">
    <xsl:value-of select="//RESULTS-PER-PAGE" />
  </xsl:variable>
  <xsl:variable name="DATABASE-DISPLAYNAME">
    <xsl:value-of select="DD:getDisplayName(SELECTED-DATABASE)" />
  </xsl:variable>
  <xsl:variable name="DATABASE">
    <xsl:value-of select="DBMASK" />
  </xsl:variable>
  <xsl:variable name="DATABASE-ID">
    <xsl:value-of select="//SESSION-DATA/DATABASE/ID" />
  </xsl:variable>
    <html>
      <head>
        <title>
          <xsl:value-of select="$SEARCH-TYPE" /> Search Patent References Format</title>

        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js" />
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/SearchResults_V7.js" />
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>

        <xsl:text disable-output-escaping="yes">
          <![CDATA[
            <xsl:comment>
            <script language="javascript">
              function printFormat(sessionid, database ,formvalue)
              {
                    var now = new Date() ;
                    var milli = now.getTime() ;
                    //alert(formvalue);
                    var url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=printCitationSelectedSet"+"&timestamp="+milli;
                    new_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=700,height=400');
                    new_window.focus();
              }

              // THIS FUNCTION BASICALLY CONSTRUCT ALL THE REQUIRED PARAMETERS FOR EMAIL.THE VALUES SO CONSTRUCTED ARE SENT TO
              // EMAIL FORM.(emailSelectedRecords,emailSelectedFormatResults.jsp)
              function emailFormat(sessionid,searchtype,searchid,count,resultscount,database,databaseid,pagesize,formvalue)
              {
                    var displaytype = 'citation';


                    var cbsize = 0;
                    var cbcheckvalue = false;
                    var i=0;
                    var url = null;
                    var docidstring  = "&docidlist=";
                    var handlestring = "&handlelist=";
                    var handlecount=0;
                    var docidcount=0;

                    if( resultscount == 1)
                    {
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

                            url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=emailForm"+"&searchid="+searchid+"&database="+database+"&displayformat="+displaytype+docidstring+handlestring;
                    }
                    else
                    {
                            cbsize = document.quicksearchresultsform.cbresult.length;


                            for( i =0; i < cbsize; i++)
                            {
                               if(document.quicksearchresultsform.cbresult[i].checked)
                               {
                                            cbcheckvalue = true;
                                            break;
                               }
                            }

                            if(cbcheckvalue)
                            {

                                    var arrDocID = new Array(pagesize);
                                    var arrHandle = new Array(pagesize);
                                    var hiddensize = document.quicksearchresultsform.elements.length;

                                    for(var i=0 ; i < hiddensize ; i++)
                                    {
                                            var nameOfElement=document.quicksearchresultsform.elements[i].name;
                                            var valueOfElement = document.quicksearchresultsform.elements[i].value;

                                            if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
                                            {
                                                            arrHandle[handlecount++] = valueOfElement ;
                                            }

                                            if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
                                            {
                                                            arrDocID[docidcount++] = valueOfElement ;
                                            }
                                    }

                                    for(index = 0 ; index < cbsize ; index++)
                                    {
                                            cbcheckvalue=document.quicksearchresultsform.cbresult[index].checked;


                                            if(cbcheckvalue)
                                            {
                                                    var subdocstring = arrDocID[index]+",";
                                                    docidstring +=subdocstring;
                                                    var subhandlestring = arrHandle[index]+",";
                                                    handlestring += subhandlestring;
                                            }
                                    }

                                    url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=emailForm"+"&searchid="+searchid+"&database="+database+"&displayformat="+displaytype+docidstring+handlestring;
                            }
                            else
                            {

                               url = "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=emailForm"+"&searchid="+searchid+"&count="+count+"&resultscount="+resultscount+"&database="+database+"&pagesize="+pagesize+"&displayformat="+displaytype;
                            }
                    }
                    new_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
                    new_window.focus();

                }

            function downloadFormat(sessionid, database)
            {
              url= "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=downloadform"+"&database="+database+"&displayformat=citation"+"&allselected=true";
              new_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
              new_window.focus();
            }


            function selectedRangeRefs(_docid, _backurl, _resultscount)
            {

              var docid = _docid;
              var backurl = _backurl;
              var resultscount = _resultscount;

              var startrange = document.quicksearchresultsform.selectrangefrom.value;
              var endrange = document.quicksearchresultsform.selectrangeto.value;

              if( ( startrange == '') || ( endrange == ''))
              {
            	window.alert("Start Range and End Range must be given");
              }
              else if((isNaN(startrange)) || (isNaN(startrange)))
              {
            	window.alert("Please enter numbers only");
              }
              else
              {
                var selectFromLength= document.quicksearchresultsform.selectrangefrom.value.length;
                var tempFromWord = document.quicksearchresultsform.selectrangefrom.value;
                var tempFromLength=0;
                while (tempFromWord.substring(0,1) == ' ')
                {
                  tempFromWord = tempFromWord.substring(1);
                  tempFromLength = tempFromLength + 1;
                }
                var selectToLength = document.quicksearchresultsform.selectrangeto.value.length;
                var tempToWord = document.quicksearchresultsform.selectrangeto.value;
                var tempToLength=0;

                while (tempToWord.substring(0,1) == ' ')
                {
                  tempToWord = tempToWord.substring(1);
                  tempToLength = tempToLength + 1;
                }
                if (( selectFromLength == tempFromLength) || ( selectToLength == tempToLength) )
                {
                  window.alert("Spaces are not allowed in the range");
                }
                else
                {
                  startrange=parseInt(document.quicksearchresultsform.selectrangefrom.value);
                  endrange = parseInt(document.quicksearchresultsform.selectrangeto.value);

                  if( ( startrange <= 0) || ( endrange <= 0))
                  {
                    window.alert("Range starts with 1");
                  }
                  else if( startrange >= endrange )
                  {
                    window.alert("Start range should be less than end range");
                  }
                  else if( ( startrange >= resultscount) || ( endrange > resultscount))
                  {
                    window.alert("Please enter the range within the Results range");
                  }
                  else if( ( endrange - startrange ) > 400 )
                  {
                    window.alert("Maximum number of selected records cannot exceed 400");
                  }
                  else
                  {
                    if (startrange == "")
                    {
                      startrange = 0;
                    }
                    if (endrange == "")
                    {
                      endrange = 0;
                    }
                    document.location = "/controller/servlet/Controller?CID=addSelectedRangeRefs&backurl="+escape(backurl)+"&docid="+docid+"&resultscount="+resultscount+"&startrange="+startrange+"&endrange="+endrange;
                  }
                }
              }
            }



                function selectUnselectRecord(thebox,handle,docid,searchquery,database,sessionid,searchid,resultscount)
                {
                    var now = new Date() ;
                    var milli = now.getTime() ;
                    var img = new Image() ;
                    var cbcheck = false;
                    var cbsize = document.quicksearchresultsform.cbresult.length;


                    if(thebox.checked)
                    {
                      document.images['image_basket'].src="/engresources/Basket.jsp?"+
                      "select=mark&handle="+handle+"&docid="+docid+"&database="+escape(database)+"&sessionid="+
                      sessionid+"&searchquery="+escape(searchquery)+"&searchid="+searchid+"&timestamp="+ milli;
                    } else {
                      //window.alert(" deleting from the basket");
                      document.images['image_basket'].src="/engresources/Basket.jsp?"+
                      "select=unmark&handle="+handle+"&docid="+docid+"&database="+escape(database)+
                      "&sessionid="+sessionid+"&searchquery="+escape(searchquery)+"&searchid="+searchid+"&timestamp="+ milli ;
                    }
                }
                function selectUnselectAllRecords(searchquery,database,sessionid,searchid,pagesize,resultscount,selectoption,dbid)
                {

                    searchquery = truncateQuery(searchquery);

                    var now = new Date() ;
                    var milli = now.getTime() ;
                    var img = new Image() ;

                    var docidstring  = "";
                    var handlestring = "";
                    var count = 0;
                    var index = 0;
                    var handlecount=0;
                    var docidcount=0;

                    var arrDocID = new Array(pagesize);
                    var arrHandle = new Array(pagesize);

                    var hiddensize = document.quicksearchresultsform.elements.length;

                    for(var i=0 ; i < hiddensize ; i++)
                    {
                                    var nameOfElement=document.quicksearchresultsform.elements[i].name;
                                    var valueOfElement = document.quicksearchresultsform.elements[i].value;

                                    if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
                                    {
                                                    arrHandle[handlecount++] = valueOfElement ;
                                    }

                                    if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
                                    {
                                                    arrDocID[docidcount++] = valueOfElement ;
                                    }
                    }


                    var cbsize = 0;
                    var cbcheckvalue= false;

                    if( resultscount == 1)
                    {
                            cbcheckvalue=document.quicksearchresultsform.cbresult.checked;
                            if (selectoption == 'markall')
                            {
                                    if(!cbcheckvalue)
                                    {
                                            var subdocstring = "&docid="+arrDocID[0];
                                            docidstring +=subdocstring;
                                            var subhandlestring = "&handle="+arrHandle[0];
                                            handlestring += subhandlestring;
                                    }
                            }
                            else
                            {
                                    var subdocstring = "&docid="+arrDocID[0];
                                    docidstring +=subdocstring;
                                    var subhandlestring = "&handle="+arrHandle[0];
                                    handlestring += subhandlestring;
                            }
                    }
                    else
                    {
                            cbsize = document.quicksearchresultsform.cbresult.length;

                            for(index = 0 ; index < cbsize ; index++)
                            {
                                            cbcheckvalue=document.quicksearchresultsform.cbresult[index].checked;

                                            if (selectoption == 'markall')
                                            {
                                                    if(!cbcheckvalue)
                                                    {
                                                            var subdocstring = "&docid="+arrDocID[index];
                                                            docidstring +=subdocstring;
                                                            var subhandlestring = "&handle="+arrHandle[index];
                                                            handlestring += subhandlestring;
                                                    }
                                            }
                                            else
                                            {
                                                    var subdocstring = "&docid="+arrDocID[index];
                                                    docidstring +=subdocstring;
                                                    var subhandlestring = "&handle="+arrHandle[index];
                                                    handlestring += subhandlestring;
                                            }
                            }
                    }

                    img.src="/engresources/Basket.jsp?select="+selectoption+"&database="+escape(database)+"&searchquery="+escape(searchquery)+"&sessionid="+sessionid+"&searchid="+searchid+docidstring+handlestring+"&timestamp=" + milli;
                    /*
                    document.images['image_basket'].src = "/engresources/Basket.jsp?select="+selectoption+"&database="+escape(database)+"&searchquery="+escape(searchquery)+"&sessionid="+sessionid+"&searchid="+searchid+docidstring+handlestring+"&timestamp=" + milli;
                    */

                    if( selectoption == 'markall' )
                    {
                            if( resultscount == 1)
                            {
                                            document.quicksearchresultsform.cbresult.checked = true;
                            }
                            else
                            {
                                    for(index = 0 ; index < cbsize ; index++)
                                    {
                                            document.quicksearchresultsform.cbresult[index].checked = true;
                                    }
                            }
                     }
                     else
                     {
                            if( resultscount == 1)
                            {
                                    document.quicksearchresultsform.cbresult.checked = false;
                            }
                            else
                            {
                                    for(index = 0 ; index < cbsize ; index++)
                                    {
                                            document.quicksearchresultsform.cbresult[index].checked = false;
                                    }
                            }
                     }

                }
                function truncateQuery(searchquery)
                {

                    //alert("Query="+searchquery);
                    if(searchquery.length > 300)
                    {
                            searchquery = searchquery.substring(0,300);
                            searchquery = searchquery+"...";
                    }

                    return searchquery;
                }
            </script>
            </xsl:comment>
          ]]>
        </xsl:text>
        <xsl:if test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus') or ($SEARCH-TYPE='Combined')">
          <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/QuickResults.js" />
        </xsl:if>
        <xsl:if test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Easy')">
          <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/ExpertResults.js" />
        </xsl:if>
        <!-- End of javascript -->
      </head>

      <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" >



        <!-- APPLY HEADER -->
        <xsl:apply-templates select="HEADER">
          <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE" />
        </xsl:apply-templates>
        <center>
          <!-- Insert the Global Links -->
          <!-- logo, search history, selected records, my folders. end session -->
          <xsl:apply-templates select="GLOBAL-LINKS">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID" />
            <xsl:with-param name="SELECTED-DB" select="$DATABASE" />
            <xsl:with-param name="LINK" select="$SEARCH-TYPE" />
          </xsl:apply-templates>
          <!-- Insert the navigation bar -->
          <!-- tabbed navigation -->
          <xsl:apply-templates select="PAGE-NAV">
            <xsl:with-param name="HEAD" select="$SEARCH-ID" />
          </xsl:apply-templates>
          <table border="0" width="99%" cellspacing="0" cellpadding="0">
            <tr>
              <td valign="top">
                <xsl:if test="not($SEARCH-TYPE='Easy')">
                  <xsl:apply-templates select="//SESSION-DATA/SC" />
                </xsl:if>
                <xsl:apply-templates select="PAGE-RESULTS" />
                <!-- Display of Bottom 'Next Page' Navigation Bar -->
                <xsl:apply-templates select="NAVIGATION-BAR">
                  <xsl:with-param name="HEAD" select="$SEARCH-ID" />
                  <xsl:with-param name="LOCATION">Bottom</xsl:with-param>
                </xsl:apply-templates>
                <p align="right">
                  <a class="MedBlueLink" href="#">Back to top</a>
                  <a href="#">
                    <img src="/engresources/images/top.gif" border="0" />
                  </a>
                </p>
              </td>
              <td width="220" valign="top">
                <xsl:apply-templates select="NAVIGATORS"/>
              </td>
            </tr>
          </table>
        </center>

        <!-- Insert the Footer table -->
        <xsl:apply-templates select="FOOTER">
          <xsl:with-param name="SESSION-ID" select="$SESSION-ID" />
          <xsl:with-param name="SELECTED-DB" select="$DATABASE" />
        </xsl:apply-templates>

        <br/>

        <script language="JavaScript" type="text/javascript" src="/engresources/js/wz_tooltip.js"></script>

      </body>
    </html>
  </xsl:template>

  <!-- This xsl displays the results in Citation Format when the database is Compendex -->
  <xsl:template match="PAGE-RESULTS">
    <!-- Start of  Citation Results  -->
    <FORM name="quicksearchresultsform">
      <table border="0" cellspacing="0" cellpadding="0" width="100%" >
        <tr>
          <td>
            <xsl:apply-templates select="UPT-DOC" />
          </td>
        </tr>
      </table>
    </FORM>
    <!-- END of  Citation Results  -->
  </xsl:template>


  <xsl:template match="UPT-DOC">
    <xsl:variable name="REF-CNT">
      <xsl:value-of select="EI-DOCUMENT/RCT" />
    </xsl:variable>
    <xsl:variable name="NONREF-CNT">
      <xsl:value-of select="EI-DOCUMENT/NPRCT" />
    </xsl:variable>
    <xsl:variable name="DATABASE-ID">
      <xsl:value-of select="EI-DOCUMENT/DOC/DB/ID" />
    </xsl:variable>
    <xsl:variable name="SELECTED-DB">
      <xsl:value-of select="//PAGE/DBMASK" />
    </xsl:variable>
    <xsl:variable name="CIT-CNT">
      <xsl:value-of select="EI-DOCUMENT/CCT" />
    </xsl:variable>
    <xsl:variable name="DOC-ID">
      <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID" />
    </xsl:variable>
    <xsl:variable name="AUTH-CODE">
      <xsl:value-of select="EI-DOCUMENT/AUTHCD" />
    </xsl:variable>
    <xsl:variable name="PATENT-NUM">
      <xsl:value-of select="EI-DOCUMENT/PAP" />
    </xsl:variable>
    <xsl:variable name="EPPNUM">
      <xsl:value-of select="$AUTH-CODE" />
      <xsl:value-of select="$PATENT-NUM" />
    </xsl:variable>
    <xsl:variable name="TOP-ABSTRACT-LINK">
    	<xsl:value-of select="/PAGE/PAGE-NAV/ABS-NAV"/>
    </xsl:variable>
    <xsl:variable name="TOP-DETAILED-LINK">
    	<xsl:value-of select="/PAGE/PAGE-NAV/DET-NAV"/>
    </xsl:variable>
     <xsl:variable name="TOP-NONPATENTREFS-LINK">
        <xsl:value-of select="/PAGE/PAGE-NAV/NONPATREF-NAV"/>
    </xsl:variable>

    <xsl:variable name="KIND-CODE">
      <xsl:value-of select="EI-DOCUMENT/KC" />
    </xsl:variable>
    <xsl:variable name="INDEX">
      <xsl:value-of select="$CURRENT-PAGE" />
    </xsl:variable>
    <xsl:variable name="SEARCH-CID"></xsl:variable>
    <xsl:variable name="QSTR"><xsl:value-of select="$AUTH-CODE" /><xsl:value-of select="$PATENT-NUM" /> WN PCI</xsl:variable>
    <xsl:variable name="ENCODED-QSTR">
      <xsl:value-of select="java:encode($QSTR)" />
    </xsl:variable>

<!-- "&amp;pcinav=0~<patent#>~Patents that cite <patent#>" encoded into links -->
    <xsl:variable name="CITEDBY-PM">
      <xsl:value-of select="EI-DOCUMENT/PM"/>
    </xsl:variable>

    <xsl:variable name="CITEDBY-QSTR">
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Quick')">searchWord1=<xsl:value-of select="$CITEDBY-PM"/>&amp;section1=PCI&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:when>
        <xsl:otherwise>searchWord1=<xsl:value-of select="$CITEDBY-PM"/><xsl:value-of select="java:encode(' WN PCI')"/>&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>


    <xsl:variable name="FULLTEXT-LINK">
      <xsl:value-of select="EI-DOCUMENT/FT/@FTLINK" />
    </xsl:variable>

    <center>
    <table border="0" cellspacing="0" cellpadding="0" width="99%">
      <tr>
        <td valign="top">
          <img src="/engresources/images/s.gif" border="0" height="10" />
        </td>
      </tr>
      <!-- all top of page links -->
      <tr>
        <td align="left">
          <a class="LgBlueLink" HREF="/controller/servlet/Controller?{$TOP-ABSTRACT-LINK}">Abstract</a>
          <A CLASS="MedBlackText">&#160; - &#160;</A>
          <a class="LgBlueLink" HREF="/controller/servlet/Controller?{$TOP-DETAILED-LINK}">Detailed</a>
          <xsl:if test="($REF-CNT) and not($REF-CNT ='') and not($REF-CNT ='0')">
            <A CLASS="MedBlackText">&#160; - &#160;</A>
            <a title="Show patents that are referenced by this patent" class="BrownText">Patent Refs</a>&nbsp;<a class="MedBlackText">(<xsl:value-of select="$REF-CNT" />)</a>
          </xsl:if>
          <xsl:if test="($NONREF-CNT) and not($NONREF-CNT ='') and not($NONREF-CNT ='0')">
            <A CLASS="MedBlackText">&#160; - &#160;</A>
            <a class="LgBlueLink" HREF="/controller/servlet/Controller?{$TOP-NONPATENTREFS-LINK}">Other Refs</a>&nbsp;<a class="MedBlackText">(<xsl:value-of select="$NONREF-CNT" />)</a>
          </xsl:if>
          <xsl:if test="($CIT-CNT) and not($CIT-CNT ='') and not($CIT-CNT ='0')">
            <A CLASS="MedBlackText">&#160; - &#160;</A>
            <a title="Show patents that reference this patent" class="LgBlueLink" HREF="/controller/servlet/Controller?CID={$CID-PREFIX}CitationFormat&amp;{$CITEDBY-QSTR}&amp;yearselect=yearrange&amp;searchtype={$SEARCH-TYPE}&amp;sort=yr">Cited by</a>&nbsp;<a class="MedBlackText">(<xsl:value-of select="$CIT-CNT" />)</a>
          </xsl:if>

	  <xsl:variable name="CHECK-CUSTOM-OPT">
		<xsl:value-of select="custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, EI-DOCUMENT/DO, EI-DOCUMENT/DOC/DB/DBMASK)" />
	  </xsl:variable>

           <xsl:if test="($CHECK-CUSTOM-OPT ='true')">
            <a class="MedBlackText">&#160; - &#160;</a>
            <a title="Full-text" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false">
              <img id="ftimg" src="/engresources/images/av.gif" alt="Full-text"/></a>
          </xsl:if>
          <xsl:if test="($LOCALHOLDINGS-CITATION='true')">
            <A CLASS="MedBlackText">&#160; - &#160;</A>
            <xsl:apply-templates select="LOCAL-HOLDINGS" mode="CIT">
              <xsl:with-param name="vISSN">
                <xsl:value-of select="EI-DOCUMENT/SN" />
              </xsl:with-param>
            </xsl:apply-templates>
          </xsl:if>
        </td>
      </tr>
      <tr>
        <td valign="top">
          <img src="/engresources/images/s.gif" height="10" />
        </td>
      </tr>
    </table>
  </center>
    <!-- end of links -->

  <center>
    <table border="0" cellspacing="0" cellpadding="0" width="99%">
      <tr>
        <td valign="top">
        <xsl:variable name="AREIS">
          <xsl:if test="($REF-CNT) and ($REF-CNT ='1')">
            <xsl:text> is </xsl:text>
          </xsl:if>
          <xsl:if test="($REF-CNT) and not($REF-CNT ='1')">
            <xsl:text> are </xsl:text>
          </xsl:if>
        </xsl:variable>
        <xsl:variable name="REFS">
          <xsl:if test="($REF-CNT) and ($REF-CNT ='1')">
            <xsl:text> reference </xsl:text>
          </xsl:if>
          <xsl:if test="($REF-CNT) and not($REF-CNT ='1')">
            <xsl:text> references </xsl:text>
          </xsl:if>
        </xsl:variable>
        <a class="BlueText"><b>There <xsl:value-of select="$AREIS" /> <xsl:value-of select="$REF-CNT"/> patent <xsl:value-of select="$REFS" /> for the following patent:</b></a>
        </td>
      </tr>
      <tr><td><img src="/engresources/images/s.gif" height="20" /></td></tr>
      <tr>
        <td valign="top" align="left">
          <!-- Here we display the Citation for the document -->
          <xsl:apply-templates select="EI-DOCUMENT" />
        </td>
      </tr>
      <tr>
        <td>
          <img src="/engresources/images/s.gif" height="10" />
        </td>
      </tr>
      <tr><td><a CLASS="BigBlackTxt3"><b>References</b></a></td></tr>
      <tr>
        <td>
          <img src="/engresources/images/s.gif" height="10" />
        </td>
      </tr>
      <xsl:variable name="OFFSET">
        <xsl:value-of select="//PAGE/OFFSET" />
      </xsl:variable>
      <tr>
        <td valign="top" align="left">
          <a href="javascript:emailFormat('$SESSIONID','{$SEARCH-TYPE}','{$SEARCH-ID}',{$CURRENT-PAGE},{$RESULTS-COUNT},'{$DATABASE}','{$DATABASE-ID}','{$CIT-CNT}')">
            <img src="/engresources/images/em.gif" border="0" />
          </a>&#160; &#160;
          <a href="javascript:printFormat('$SESSIONID','{$DATABASE}')">
            <img src="/engresources/images/pr.gif" border="0" />
          </a>&#160; &#160;
          <a href="javascript:downloadFormat('$SESSIONID','{$DATABASE}')">
            <img src="/engresources/images/dw.gif" border="0" />
          </a>
          <xsl:if test="($PERSONALIZATION-PRESENT='true')">
            <xsl:variable name="NEXTURL">CID=viewSavedFolders&amp;database=<xsl:value-of select="$DATABASE" />&amp;count=<xsl:value-of select="$CURRENT-PAGE" />&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID" />&amp;source=selectedset</xsl:variable>
            &#160; &#160;
            <A>
              <xsl:attribute name="HREF">
                <xsl:choose>
                  <xsl:when test="($PERSONALIZATION-PRESENT='true')">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=viewSavedFolders&amp;database=<xsl:value-of select="$DATABASE" />&amp;backurl=<xsl:value-of select="$BACKURL" />&amp;source=selectedset</xsl:when>
                  <xsl:otherwise>/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID" />&amp;count=<xsl:value-of select="$CURRENT-PAGE" />&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE" />&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE" />&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)" />&amp;backurl=<xsl:value-of select="$BACKURL" /></xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <img src="/engresources/images/sv.gif" border="0" />
            </A>
          </xsl:if>
          <!-- <img src="/engresources/images/s.gif" height="10" /> -->
          <a CLASS="MedBlackText">&#160; - &#160;</a>
          <xsl:variable name="RANGE-BACK-URL">
                    <xsl:value-of select="/PAGE/BACKURL"/>
          </xsl:variable>
          <a CLASS="SmBlackText"><label for="txtFrm">Select range:</label></a><input id="txtFrm" type="text" name="selectrangefrom" CLASS="SmBlackText" size="1"/><a CLASS="SmBlackText"><label for="txtTo"> to </label></a><input id="txtTo" type="text" name="selectrangeto" CLASS="SmBlackText" size="1"/>&#160;
          <a href="javascript:selectedRangeRefs('{$ROOT-DOCID}','{$RANGE-BACK-URL}','{$REF-CNT}')"><img align="top" src="/engresources/images/go.gif" border="0" /></a>
          <a CLASS="MedBlackText">&#160; - &#160;</a>
          <a CLASS="MedBlueLink" href="javascript:selectUnselectAllRecords('NA','{$DATABASE}','$SESSIONID','{$SEARCH-ID}',25,'{$SEL-COUNT}','markall')">Select all on page</a>
          <a CLASS="MedBlackText">&#160; - &#160;</a>
          <!-- &#160;&#160;<A CLASS="MedBlackText"> - </A>&#160;&#160; -->
          <a CLASS="MedBlueLink" href="javascript:selectUnselectAllRecords('{$ENCODED-DISPLAY-QUERY}','{$DATABASE}','$SESSIONID','{$SEARCH-ID}',25,'{$CIT-CNT}','unmarkall')">Clear all on page</a>
         </td>
      </tr>
      <!-- end of refs manager -->
      <tr>
        <td>
          <img src="/engresources/images/s.gif" height="10" />
        </td>
      </tr>
      <xsl:apply-templates select="REF-DOCS" />
    </table>
    </center>
  </xsl:template>

  <xsl:template match="REF-DOCS">

    <xsl:variable name="PAGESIZE">25</xsl:variable>
    <xsl:variable name="NEXTREF-NAV">
          <xsl:value-of select="/PAGE/PAGE-NAV/NEXTREF-NAV" />
    </xsl:variable>

    <xsl:variable name="PREVREF-NAV">
          <xsl:value-of select="/PAGE/PAGE-NAV/PREVREF-NAV" />
    </xsl:variable>


    <tr>
      <td valign="top" width="100%" align="left">
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
          <xsl:apply-templates select="PAGE-ENTRY" />
        </table>
      </td>
    </tr>
    <tr>
      <td align="right">

        <img src="/engresources/images/s.gif" height="10" />
        <xsl:if test="(@OFFSET &gt; 0)">
          <a href="/controller/servlet/Controller?{$PREVREF-NAV}">
          <img src="/engresources/images/pp.gif" border="0" /></a>
          &spcr8;
        </xsl:if>
        <xsl:if test="(/PAGE/PAGE-RESULTS/UPT-DOC/EI-DOCUMENT/RCT &gt; (@OFFSET + $PAGESIZE))">
          <a href="/controller/servlet/Controller?{$NEXTREF-NAV}">
          <img src="/engresources/images/np.gif" border="0" /></a>
        </xsl:if>
      </td>
    </tr>
  </xsl:template>

  <xsl:variable name="HREF-PREFIX" />

  <xsl:template match="PAGE-ENTRY">
    <xsl:param name="SEARCH-CID" />
    <xsl:variable name="FULLTEXT-LINK">
      <xsl:value-of select="EI-DOCUMENT/FT/@FTLINK" />
    </xsl:variable>
    <xsl:variable name="SESSION-ID">
      <xsl:value-of select="//SESSION-DATA/SESSION-ID" />
    </xsl:variable>
    <xsl:variable name="SEARCH-ID">
      <xsl:value-of select="//SEARCH-ID" />
    </xsl:variable>
    <xsl:variable name="CURRENT-PAGE">
      <xsl:value-of select="//CURR-PAGE-ID" />
    </xsl:variable>
    <xsl:variable name="RESULTS-COUNT">
      <xsl:value-of select="//RESULTS-COUNT" />
    </xsl:variable>
    <xsl:variable name="RESULTS-PER-PAGE">
      <xsl:value-of select="//RESULTS-PER-PAGE" />
    </xsl:variable>
    <xsl:variable name="SELECTED-DB">
      <xsl:value-of select="//DBMASK" />
    </xsl:variable>
    <xsl:variable name="DATABASE-ID">
      <xsl:value-of select="EI-DOCUMENT/DOC/DB/ID" />
    </xsl:variable>
    <xsl:variable name="INDEX">
      <xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX" />
    </xsl:variable>
    <xsl:variable name="DOC-ID">
      <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID" />
    </xsl:variable>
    <xsl:variable name="AUTH-CODE">
      <xsl:value-of select="EI-DOCUMENT/AUTHCD" />
    </xsl:variable>
    <xsl:variable name="REF-PAT-NUM">
      <xsl:value-of select="EI-DOCUMENT/PM1" />
    </xsl:variable>
    <xsl:variable name="UPT-PAT-NUM">
      <xsl:value-of select="EI-DOCUMENT/PAP" />
    </xsl:variable>
    <xsl:variable name="UPT-EPPNUM">
      <xsl:value-of select="$AUTH-CODE" />
      <xsl:value-of select="$UPT-PAT-NUM" />
    </xsl:variable>
    <xsl:variable name="KIND-CODE">
      <xsl:value-of select="EI-DOCUMENT/KC" />
    </xsl:variable>
    <xsl:variable name="REF-CNT">
      <xsl:value-of select="EI-DOCUMENT/RCT" />
    </xsl:variable>
    <xsl:variable name="CIT-CNT">
      <xsl:value-of select="EI-DOCUMENT/CCT" />
    </xsl:variable>

<!-- 2nd location of CITEDBY-QSTR in document reference "citation" listings -->
<!-- "&amp;pcinav=0~<patent#>~Patents that cite <patent#>" encoded into links -->
    <xsl:variable name="REF-CITEDBY-PM">
      <xsl:value-of select="EI-DOCUMENT/PM"/>
    </xsl:variable>

    <xsl:variable name="REF-CITEDBY-QSTR">
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Quick')">searchWord1=<xsl:value-of select="$REF-CITEDBY-PM"/>&amp;section1=PCI&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$REF-CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$REF-CITEDBY-PM"/></xsl:when>
        <xsl:otherwise>searchWord1=<xsl:value-of select="$REF-CITEDBY-PM"/><xsl:value-of select="java:encode(' WN PCI')"/>&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$REF-CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$REF-CITEDBY-PM"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>


    <xsl:variable name="ABSTRACT-LINK-CID">
      <xsl:choose>
        <xsl:when test="$SEARCH-CID=''">CID=referenceAbstractFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID" /></xsl:when>
        <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID" /></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="DETAILED-LINK-CID">
      <xsl:choose>
        <xsl:when test="$SEARCH-CID=''">CID=referenceDetailedFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID" /></xsl:when>
        <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID" /></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="REFERENCES-LINK-CID">
      <xsl:choose>
        <xsl:when test="$SEARCH-CID=''">CID=referenceReferencesFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID" /></xsl:when>
        <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID" /></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="NP-REFERENCES-LINK-CID">
      <xsl:choose>
        <xsl:when test="$SEARCH-CID=''">CID=referenceNonPatentReferencesFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID" /></xsl:when>
        <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID" /></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="NONREF-CNT">
      <xsl:value-of select="EI-DOCUMENT/NPRCT" />
    </xsl:variable>
    <tr>
      <td valign="top" colspan="4" height="5">
        <img src="/engresources/images/s.gif" />
      </td>
    </tr>
    <tr>
      <td valign="top" align="left" width="3">
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
        <xsl:variable name="IS-SELECTED">
          <xsl:value-of select="IS-SELECTED" disable-output-escaping="yes" />
        </xsl:variable>
        <xsl:variable name="CB-NO">
          <xsl:number />
        </xsl:variable>
        <input type="checkbox" name="cbresult" onClick="selectUnselectRecord( this,{$INDEX},'{$DOC-ID}','NA','{$DATABASE-ID}','$SESSIONID','{$SEARCH-ID}',{$RESULTS-COUNT})">
          <xsl:if test="$IS-SELECTED='true'">
            <xsl:attribute name="checked">checked</xsl:attribute>
          </xsl:if>
        </input>
      </td>
      <td valign="top" width="3">
        <img src="/engresources/images/s.gif" />
        <A CLASS="MedBlackText">
        <xsl:value-of select="$INDEX" />.</A>
      </td>
      <td valign="top" width="3">
        <img src="/engresources/images/s.gif" width="3" />
      </td>
      <td valign="top">
        <img src="/engresources/images/s.gif" name="image_basket" />
        <xsl:apply-templates select="EI-DOCUMENT" />
        <br/>
        <xsl:choose>
          <xsl:when test="not((substring($DOC-ID,0,4)) ='ref')">
            <A CLASS="LgBlueLink" HREF="/controller/servlet/Controller?{$ABSTRACT-LINK-CID}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;database={$SELECTED-DB}&amp;format=referenceAbstractFormat&amp;docid={$DOC-ID}&amp;searchnav={$ENCODED-RESULTS-NAV}">Abstract</A>
            <A CLASS="MedBlackText">&#160; - &#160;</A>
            <A CLASS="LgBlueLink" HREF="/controller/servlet/Controller?{$DETAILED-LINK-CID}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;database={$SELECTED-DB}&amp;format=referenceDetailedFormat&amp;docid={$DOC-ID}&amp;searchnav={$ENCODED-RESULTS-NAV}">Detailed</A>
            <xsl:if test="($REF-CNT) and not($REF-CNT ='0') and not($REF-CNT ='')">
              <A CLASS="MedBlackText">&#160; - &#160;</A>
              <A title="Show patents that are referenced by this patent" CLASS="LgBlueLink" HREF="/controller/servlet/Controller?{$REFERENCES-LINK-CID}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;database={$SELECTED-DB}&amp;format=patentReferencesFormat&amp;docid={$DOC-ID}&amp;searchnav={$ENCODED-RESULTS-NAV}">Patent Refs</A>&nbsp;<A CLASS="MedBlackText">(<xsl:value-of select="$REF-CNT" />)</A>
            </xsl:if>
            <xsl:if test="($NONREF-CNT) and not($NONREF-CNT ='0') and not($NONREF-CNT ='')">
              <A CLASS="MedBlackText">&#160; - &#160;</A>
              <A CLASS="LgBlueLink" HREF="/controller/servlet/Controller?{$NP-REFERENCES-LINK-CID}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;database={$SELECTED-DB}&amp;format=ReferencesFormat&amp;docid={$DOC-ID}&amp;searchnav={$ENCODED-RESULTS-NAV}">Other Refs</A>&nbsp;<A CLASS="MedBlackText">(<xsl:value-of select="$NONREF-CNT" />)</A>
            </xsl:if>
            <xsl:if test="($CIT-CNT) and not($CIT-CNT ='0') and not($CIT-CNT ='')">
              <A CLASS="MedBlackText">&#160; - &#160;</A>
              <A title="Show patents that reference this patent" CLASS="LgBlueLink" HREF="/controller/servlet/Controller?CID={$CID-PREFIX}CitationFormat&amp;{$REF-CITEDBY-QSTR}&amp;yearselect=yearrange&amp;searchtype={$SEARCH-TYPE}&amp;sort=yr">Cited by</A>&nbsp;<A CLASS="MedBlackText">(<xsl:value-of select="$CIT-CNT" />)</A>
            </xsl:if>
          </xsl:when>
        </xsl:choose>
        <xsl:variable name="CHECK-CUSTOM-OPT">
 		<xsl:value-of select="custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, EI-DOCUMENT/DO, EI-DOCUMENT/DOC/DB/DBMASK)" />
 	</xsl:variable>
        <xsl:if test="($CHECK-CUSTOM-OPT ='true')">
          <xsl:if test="not((substring($DOC-ID,0,4)) ='ref')">
            <A CLASS="MedBlackText">&#160; - &#160;</A>
          </xsl:if>
        <a href="" onclick="window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID={$DOC-ID}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable');return false">
          <img src="/engresources/images/av.gif" border="0" align="absbottom"/>
        </a></xsl:if>

        <xsl:if test="($LOCALHOLDINGS-CITATION='true')">
          <xsl:apply-templates select="LOCAL-HOLDINGS" mode="CIT">
            <xsl:with-param name="vISSN">
              <xsl:value-of select="EI-DOCUMENT/SN" />
            </xsl:with-param>
          </xsl:apply-templates>
        </xsl:if>

      </td>
    </tr>
    <tr>
      <td valign="top" colspan="4" height="5">
        <img src="/engresources/images/s.gif" />
      </td>
    </tr>
  </xsl:template>

  <!--
    The following templates use the following global variables
    <xsl:variable name="SEARCH-TYPE">
  -->
  <xsl:variable name="RERUN-CID">
    <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
  </xsl:variable>

  <xsl:template match="NAVIGATORS">
    <!-- jam turkey - added for Help link to have proper anchor to reflect page context -->
   <xsl:variable name="REFCTX">
	<xsl:choose>
            <xsl:when test="(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">Refine_Within_Easy_Search.htm</xsl:when>
            <xsl:otherwise>Refining_within_Quick_Search_or_Expert_Search_or_Thesaurus_Search.htm</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <form name="navigator" method="POST">
      <xsl:attribute name="action">/controller/servlet/Controller?CID=expertSearchCitationFormat&amp;database=<xsl:value-of select="/PAGE/DBMASK" />&amp;RERUN=<xsl:value-of select="$SEARCH-ID" />&amp;resultsorall=all&amp;COUNT=<xsl:value-of select="//PAGE/CURR-PAGE-ID"/></xsl:attribute>
      <!-- preserve sort data -->
      <input type="hidden" name="sort">
        <xsl:attribute name="value">
          <xsl:value-of select="//SESSION-DATA/SORT-OPTION" />
        </xsl:attribute>
      </input>
      <input type="hidden" name="sortdir">
        <xsl:attribute name="value">
          <xsl:value-of select="//SESSION-DATA/SORT-DIRECTION" />
        </xsl:attribute>
      </input>
      <!-- jam  This refine search form ALWAYS becomes an expert search -->
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
          <td colspan="3" height="4">
            <img src="/engresources/images/s.gif" height="4" />
          </td>
        </tr>
        <tr>
          <td colspan="3" height="1" bgcolor="#3173B5">
            <img src="/engresources/images/s.gif" border="0" height="1" />
          </td>
        </tr>
        <tr>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" height="3" width="1" />
          </td>
          <td valign="top">
            <img src="/engresources/images/s.gif" border="0" height="3" width="1" />
          </td>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" height="3" width="1" />
          </td>
        </tr>
        <tr>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" width="1" />
          </td>
          <td valign="middle">
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
              <tr>
                <td valign="bottom" align="left">
                  <a CLASS="LgOrangeText">&#160;
                  <b>Find Similar</b></a>
                </td>
                <td valign="top" align="right">
                <a CLASS="SmBlueText">
                  <b>?</b>
                </a>
                <a class="DecLink" href="javascript:makeUrl('{$REFCTX}')">
			    Help</a>&#160;&#160;</td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" width="1" />
          </td>
        </tr>
        <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
          <tr>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" width="1" />
            </td>
            <td valign="top" align="right">
              <input onclick="javascript:return navigatorsOnsubmit('limit');" type="image" src="/engresources/images/sch.gif" align="absbottom" name="search" value="Search" border="0" />&#160;&#160;
              </td>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" width="1" />
            </td>
          </tr>
        </xsl:if>
        <tr>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" width="1" />
          </td>
          <td valign="top">
            <xsl:apply-templates />
          </td>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" width="1" />
          </td>
        </tr>
        <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
          <tr>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" width="1" />
            </td>
            <td valign="top">
              <!-- use same format as a Navigator/Modifier for Add a term checkbox and input -->
              <table border="0" width="100%" cellspacing="0" cellpadding="0">
                <tr>
                  <td>
                    <table border="0" width="100%" cellspacing="0" cellpadding="0">
                      <tr>
                        <td colspan="3">
                          <img src="/engresources/images/s.gif" height="2" />
                        </td>
                      </tr>
                      <tr>
                        <td width="4" valign="top">
                          <img src="/engresources/images/s.gif" width="4" />
                        </td>
                        <td valign="top" colspan="2">
                          <a class="MedOrangeText">
                            <b><label for="txtAdd">Add a term</label></b>
                          </a>
                        </td>
                      </tr>
                      <tr>
                        <td width="4">
                          <img src="/engresources/images/s.gif" width="4" />
                        </td>
                        <td width="4" align="left">
                          <!-- jam replaced checkbox with spacer -->
                          <img src="/engresources/images/s.gif" width="4" />
                        </td>
                        <td width="100%" valign="top" align="left">
                          <a class="SmBlackText">
                            <input id="txtAdd" type="text" name="append" class="SmBlackText" size="15" maxlength="30" />
                          </a>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="3">
                          <img src="/engresources/images/s.gif" height="2" />
                        </td>
                      </tr>
                      <tr>
                        <td colspan="3">
                          <img src="/engresources/images/s.gif" height="2" />
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </td>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" width="1" />
            </td>
          </tr>
          <tr>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" width="1" />
            </td>
            <td valign="middle" align="left">

            </td>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" width="1" />
            </td>
          </tr>
          <tr>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" height="2" width="1" />
            </td>
            <td valign="top">
              <img src="/engresources/images/s.gif" border="0" height="2" width="1" />
            </td>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" height="2" width="1" />
            </td>
          </tr>
          <tr>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" width="1" />
            </td>
            <td valign="middle" align="right">
              <input type="image" onclick="javascript:return navigatorsOnsubmit('limit');" src="/engresources/images/sch.gif" name="search" align="absbottom" value="Search" border="0" />&#160;&#160;
            </td>
            <td width="1" bgcolor="#3173b5">
              <img src="/engresources/images/s.gif" border="0" width="1" />
            </td>
          </tr>
        </xsl:if>
        <tr>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" height="4" width="1" />
          </td>
          <td valign="top">
            <img src="/engresources/images/s.gif" border="0" height="4" width="1" />
          </td>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" height="4" width="1" />
          </td>
        </tr>
        <tr>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" height="5" width="1" />
          </td>
          <td valign="top" align="right">
            <a CLASS="SmBlueText">
              <b>?</b>
            </a>
            <a class="DecLink" href="javascript:makeUrl('{$REFCTX}')">
			    Help</a>&#160;&#160;</td>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" height="5" width="1" />
          </td>
        </tr>
        <tr>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" width="1" />
          </td>
          <td valign="top">
            <xsl:apply-templates select="../NAVPAGER" />
          </td>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" width="1" />
          </td>
        </tr>
        <tr>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" height="3" width="1" />
          </td>
          <td valign="top">
            <img src="/engresources/images/s.gif" border="0" height="3" width="1" />
          </td>
          <td width="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" border="0" height="3" width="1" />
          </td>
        </tr>
        <tr>
          <td colspan="3" height="1" bgcolor="#3173b5">
            <img src="/engresources/images/s.gif" height="1" border="0" />
          </td>
        </tr>
      </table>
    </form>
  </xsl:template>

  <xsl:template match="NAVPAGER">
    <p align="right">
      <xsl:apply-templates />
    </p>
  </xsl:template>

  <xsl:template match="PAGERS">
    <tr>
      <td width="4">
        <img src="/engresources/images/s.gif" width="4" />
      </td>
      <td width="12" align="left" height="10">
        <img src="/engresources/images/s.gif" height="10" border="0" />
      </td>
      <td width="100%" valign="top" align="right" height="10">
        <xsl:apply-templates />
      </td>
    </tr>
  </xsl:template>

  <!-- jam - added search anchors so the more and less links return you to the same navigator -->
  <xsl:template match="MORE">
    <a class="MedBlueLink">
      <xsl:attribute name="title">expand selections</xsl:attribute>
      <xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="/PAGE/BACKURL-DECODED"/>&amp;navigator=MORE&amp;FIELD=<xsl:value-of select="../@FIELD" />:<xsl:value-of select="@COUNT" />#<xsl:value-of select="../@FIELD" /></xsl:attribute>more...
    </a>
    <img src="/engresources/images/s.gif" width="5" height="1" />
  </xsl:template>

  <xsl:template match="LESS">
    <a class="MedBlueLink">
      <xsl:attribute name="title">collapse selections</xsl:attribute>
      <xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="/PAGE/BACKURL-DECODED"/>&amp;navigator=MORE&amp;FIELD=<xsl:value-of select="../@FIELD" />:<xsl:value-of select="@COUNT" />#<xsl:value-of select="../@FIELD" /></xsl:attribute>
      ...less
    </a>
    <img src="/engresources/images/s.gif" width="5" height="1" />
  </xsl:template>

  <xsl:template match="COMPMASK">
  </xsl:template>

  <!-- jam - added name attribute to navigator for more and less href anchors -->
  <xsl:template match="NAVIGATOR">
    <table border="0" width="100%" cellspacing="0" cellpadding="0">
      <tr>
        <td>
          <table border="0" width="100%" cellspacing="0" cellpadding="0">
            <tr>
              <td width="4" valign="top">
                <img src="/engresources/images/s.gif" width="4" />
              </td>
              <td valign="top" colspan="2">&#160;
                <a class="MedOrangeText" name="{@FIELD}">
                  <b>
                    <xsl:value-of select="@LABEL" />
                  </b>
                </a>
              </td>
            </tr>
            <xsl:apply-templates />
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="MODIFIER">
    <tr>
      <td width="4">
        <img src="/engresources/images/s.gif" width="4" />
      </td>
      <td width="12" valign="top" align="left">
        <xsl:if test="(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
          <img src="/engresources/images/s.gif" width="12" />
        </xsl:if>
        <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
          <input type="checkbox">
            <xsl:attribute name="id"><xsl:value-of select="../@NAME" /><xsl:value-of select="position()" /></xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="../@NAME" /></xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="@COUNT" />~<xsl:value-of select="VALUE" />~<xsl:value-of select="LABEL" /></xsl:attribute>
          </input>
        </xsl:if>
      </td>
      <td width="100%" valign="middle" align="left">
        <!-- [<xsl:value-of select="(normalize-space(VALUE)='')"/>] -->
        <!-- [<xsl:value-of select="count(../MODIFIER) = 1"/>] -->
          <a>
            <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
              <xsl:attribute name="class">SmBlackText</xsl:attribute>
            </xsl:if>
            <xsl:if test="string(TITLE)">
              <xsl:attribute name="class">SmBoldBlueText2</xsl:attribute>
              <xsl:attribute name="onmouseover">Tip('<xsl:value-of select="TITLE"/>',WIDTH,450)</xsl:attribute>
            </xsl:if>
            <xsl:if test="(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
              <xsl:attribute name="class">MedBlueLink</xsl:attribute>
            </xsl:if>
            <!-- suppress HREF when VALUE is missing -->
            <xsl:if test="((/PAGE/SESSION-DATA/SEARCH-TYPE='Easy') and not(normalize-space(VALUE)=''))">
              <xsl:if test="not(@COUNT=0)">
                <xsl:attribute name="HREF">/controller/servlet/Controller?CID=<xsl:value-of select="$RERUN-CID" />&amp;database=<xsl:value-of select="/PAGE/DBMASK" />&amp;RERUN=<xsl:value-of select="//SESSION-DATA/QUERY-ID" />&amp;append=<xsl:value-of select="@COUNT" />~<xsl:value-of select="java:encode(VALUE)" />~<xsl:value-of select="java:encode(LABEL)" />&amp;section=<xsl:value-of select="../@NAME" /></xsl:attribute>
              </xsl:if>
            </xsl:if>
              <label>
                <xsl:attribute name="for"><xsl:value-of select="../@NAME"/><xsl:value-of select="position()" /></xsl:attribute>
                <xsl:value-of select="LABEL" />
              </label>
            </a>
            <img src="/engresources/images/s.gif" width="4" height="1" />
            <a class="SmBlackText">(<xsl:value-of select="@COUNT" />)</a>
      </td>
    </tr>
    <!--  CS added if statement to give more space inbetween queries in easy search -->
    <tr>
      <td colspan="3">
        <xsl:if test="(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
          <img src="/engresources/images/s.gif" height="6" />
        </xsl:if>
        <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
          <img src="/engresources/images/s.gif" height="1" />
        </xsl:if>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
