<?xml version="1.0" ?>

<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
    exclude-result-prefixes="java html xsl custoptions"
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
  <!-- 
<xsl:param name="CUST-ID">0</xsl:param>
 -->
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
       
  <!-- started here-->
 	
	var mltTableBody;
	var mltTable;
	var mltDiv;
	var mltTerms;
	var mltFlag = "1";
	var mltInputField;
	var mltTerms;
	
	var lstTableBody;
	var lstTable;
	var lstDiv;
	var lstTerms;
	var lstFlag = "1";
	var lstInputField;
	var lstTerms;
	
	var atmTableBody;
	var atmTable;
	var atmDiv;
	var atmTerms;		
	var atmFlag = "1";
	var atmInputField;
	var atmTerms;

    var longltTableBody;
    var longltTable;
    var longltDiv;
    var longltTerms;
    var longltFlag = "1";
    var longltInputField;
    var longltTerms;
    var mid;    
    var xmlHttp;
	


	function createXMLHttpRequest()
	{
    	try
    	{
        	xmlHttp = new XMLHttpRequest();
    	}
    	catch (trymicrosoft)
    	{
        	try
        	{
            	xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
        	}
        	catch (othermicrosoft)
        	{
            	try
            	{
                	xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
            	}
            	catch (failed)
            	{
                	xmlHttp = null;
            	}
        	}
    	}
    	if (xmlHttp == null)
    	alert("Error creating request object!");
	}

	function findLongLinkTerms(mid)
	{ 
    	var date = new Date();
    	createXMLHttpRequest();  
    	var url = "/controller/servlet/Controller?CID=encLongterms&docid="+mid+"&timestamp="+date.getTime();
    	xmlHttp.open("GET", url, true);
    	xmlHttp.onreadystatechange = callback;
    	xmlHttp.send(null);
	}


	function callback()
	{
    	if (xmlHttp.readyState == 4)
    	{
        	if (xmlHttp.status == 200)
        	{
            	var allterms = xmlHttp.responseXML;
            	var terms = allterms.getElementsByTagName("body");
            	if (terms != null && terms[0].firstChild != null)
            	{
                	if (terms[0].firstChild.nodeValue != null)
                	{                              
                    	longltTerms = terms[0].firstChild.nodeValue; 
                    	drawTerms("longlt");
                	}
            	}
        	}
    	}
	}


	function flipImg(terms, termtype)
	{
		if(termtype == "mlt")
		{
			if(mltFlag == "1")
			{
				draw(terms, termtype);				
				document.mlt.src="/engresources/images/encMinus.gif";				
				document.mltOpenClose.src="/engresources/images/encMltClose.gif";				
				mltFlag = "2";
			}
			else if(mltFlag == "2")
			{
				clearTerms(termtype);
				document.mlt.src="/engresources/images/encPlus.gif";
				document.mltOpenClose.src="/engresources/images/encMltOpen.gif";
				mltFlag = "1";
			}					
		}		
		else if(termtype == "lst")
		{
			if(lstFlag == "1")
			{
				draw(terms, termtype);				
				document.lst.src="/engresources/images/encMinus.gif";				
				document.lstOpenClose.src="/engresources/images/encLinkedClose.gif";				
				lstFlag = "2";
			}
			else if(lstFlag == "2")
			{
				clearTerms(termtype);
				document.lst.src="/engresources/images/encPlus.gif";
				document.lstOpenClose.src="/engresources/images/encLinkedOpen.gif";
				lstFlag = "1";
			}					
		}
		else if (termtype == "atm")
		{
			if(atmFlag == "1")
			{
		 		draw(terms, termtype);				
		 		document.atm.src="/engresources/images/encMinus.gif";				
				document.atmOpenClose.src="/engresources/images/encTemplatesClose.gif";				
				atmFlag = "2";
			}
			else if(atmFlag == "2")
			{
				clearTerms(termtype);
				document.atm.src="/engresources/images/encPlus.gif";
				document.atmOpenClose.src="/engresources/images/encTemplatesOpen.gif";
				atmFlag = "1";
			}		
		}
		else if (termtype == "longlt")
        {
            if(longltFlag == "1")
            {
                draw(terms, termtype);              
                document.longlt.src="/engresources/images/encMinus.gif";                
                document.longltOpenClose.src="/engresources/images/encLinkedClose.gif";               
                longltFlag = "2";
            }
            else if(longltFlag == "2")
            {
                clearTerms(termtype);
                document.longlt.src="/engresources/images/encPlus.gif";
                document.longltOpenClose.src="/engresources/images/encLinkedOpen.gif";
                longltFlag = "1";
            }       
        }
	}
				
	function initVars(terms,termtype)
	{		
		if(termtype == "mlt")
		{				
			mltTerms = terms;
			mltDiv = document.getElementById("mltdiv");
			mltInputField = document.getElementById("mltfield");
			mltTableBody = document.getElementById("mlt_table_body");
			mltTable = document.getElementById("mlt_table");
		}
		else if (termtype == "lst")
		{
			lstTerms = terms;
			lstDiv = document.getElementById("lstdiv");
			lstInputField = document.getElementById("lstfield");
			lstTableBody = document.getElementById("lst_table_body");
			lstTable = document.getElementById("lst_table");
		}
		else if (termtype == "atm")
		{
			atmTerms = terms;
			atmDiv = document.getElementById("atmdiv");
			atmInputField = document.getElementById("atmfield");
			atmTableBody = document.getElementById("atm_table_body");
			atmTable = document.getElementById("atm_table");
		}
		else if (termtype == "longlt")
        {
 
 			//findLongLinkTerms();
            longltTerms = "Retrieving...";
            longltDiv = document.getElementById("longltdiv");
            longltInputField = document.getElementById("longltfield");
            longltTableBody = document.getElementById("longlt_table_body");
            longltTable = document.getElementById("longlt_table");
            //mid = document.getElementById("longlt_mid");

        }

	}
 
 	function draw(terms, termtype)
	{
		initVars(terms,termtype);
        if(termtype == "longlt")
        {
            findLongLinkTerms(terms);  
        }
		setOffsetTerms(termtype);
		drawTerms(termtype);
		return false;
	}
       
	function setOffsetTerms(termtype)
	{

		if(termtype =="mlt")
		{		
			var gend = mltInputField.offsetWidth;
			var gleft = calculateOffsetLeftGroup1(mltInputField)+gend;
			var gtop = calculateOffsetTopGroup1(mltInputField);					
			mltDiv.style.border = "black 0px solid";	
			mltDiv.style.left = gleft + "px";
			mltDiv.style.top = gtop + "px";		
			mltTable.style.width = "560px"; 				
		}	
		else if (termtype == "atm")
		{
			var gend = atmInputField.offsetWidth;
			var gleft = calculateOffsetLeftGroup1(atmInputField)+gend;
			var gtop = calculateOffsetTopGroup1(atmInputField);	
			atmDiv.style.border = "black 0px solid";	
			atmDiv.style.left = gleft + "px";
			atmDiv.style.top = gtop + "px";		
			atmTable.style.width = "560px"; 			
		}	
		else if (termtype == "lst")
		{
			var gend = lstInputField.offsetWidth;
			var gleft = calculateOffsetLeftGroup1(lstInputField)+gend;
			var gtop = calculateOffsetTopGroup1(lstInputField);	
			lstDiv.style.border = "black 0px solid";	
			lstDiv.style.left = gleft + "px";
			lstDiv.style.top = gtop + "px";		
			lstTable.style.width = "560px"; 			
		}	
		else if (termtype == "longlt")
        {
            var gend = longltInputField.offsetWidth;
            var gleft = calculateOffsetLeftGroup1(longltInputField)+gend;
            var gtop = calculateOffsetTopGroup1(longltInputField);  
            longltDiv.style.border = "black 0px solid"; 
            longltDiv.style.left = gleft + "px";
            longltDiv.style.top = gtop + "px";      
            longltTable.style.width = "560px";          
        }

	}
	
	function clearTerms(termtype)
	{

		if (termtype == "mlt")
		{
			if (mltTableBody != null )
			{
				var gi = mltTableBody.childNodes.length;
				for (var i = gi - 1; i >= 0 ; i--)
				{
					mltTableBody.removeChild(mltTableBody.childNodes[i]);
				}
				mltDiv.style.border = "none";
			}
		}
		else if(termtype == "atm")
		{
			if (atmTableBody != null )
			{
				var gi = atmTableBody.childNodes.length;
				for (var i = gi - 1; i >= 0 ; i--)
				{
					atmTableBody.removeChild(atmTableBody.childNodes[i]);
				}
				atmDiv.style.border = "none";
			}		
		}
	 	else if(termtype == "lst")
		{
			if (lstTableBody != null )
			{
				var gi = lstTableBody.childNodes.length;
				for (var i = gi - 1; i >= 0 ; i--)
				{
					lstTableBody.removeChild(lstTableBody.childNodes[i]);
				}
				lstDiv.style.border = "none";
			}		
		}
		else if(termtype == "longlt")
        {
            if (longltTableBody != null )
            {
                var gi = longltTableBody.childNodes.length;
                for (var i = gi - 1; i >= 0 ; i--)
                {
                    longltTableBody.removeChild(longltTableBody.childNodes[i]);
                }
                longltDiv.style.border = "none";
            }       
        }

	}
	
	// fhis function is drawing link terms
	function drawTerms(termtype)
	{
	
		clearTerms(termtype);
		var allterms = new Array();
		if(termtype == "atm")
		{		
			allterms= atmTerms.split("</br>");
		}
		else if(termtype == "mlt")
		{
			allterms = mltTerms.split("|");			
		}
		else if(termtype == "lst")
		{
			allterms = lstTerms.split("|");			
		}
		else if(termtype == "longlt")
        {
            allterms = longltTerms.split("|");                     
        }

		var size = allterms.length;
		var row, cell, txtNode;
		for (var i = 0; i < size; i++)
		{
			var nextNode = allterms[i];
	//		if (termtype == "lst")
	//		{
	//			nextNode="/engresources/images/separator.gif"+nextNode;
	//		}
			row = document.createElement("tr");
			cell = document.createElement("td");
			cell.tabIndex =1;
			cell.style.paddingLeft="5px";				
			cell.setAttribute("className", "SmBlackText");
			if (nextNode != "")
			{
				var sepimg = document.createElement("img");
				sepimg.setAttribute("src", "/engresources/images/separator.gif");
	    		sepimg.setAttribute("height", "7");
	    		sepimg.setAttribute("width", "7");
	    		sepimg.setAttribute("alt", "separ");
	    		cell.appendChild(sepimg);

	    		var spaceimg = document.createElement("img");
	    		spaceimg.setAttribute("src", "/engresources/images/s.gif");
	    		spaceimg.setAttribute("height", "7");
	    		spaceimg.setAttribute("width", "5");
	    		cell.appendChild(spaceimg);
	    	}

			txtNode = document.createTextNode(nextNode);
			cell.appendChild(txtNode);
			row.appendChild(cell);
			if(termtype == "atm")
			{
				atmTableBody.appendChild(row);
			}
			else if (termtype == "mlt")
			{
				mltTableBody.appendChild(row);
			}
			else if (termtype == "lst")
			{
				lstTableBody.appendChild(row);
			}
			else if (termtype == "longlt")
			{
			
				longltTableBody.appendChild(row);
			}
		}			
		return false;		
	}
		
	function calculateOffsetLeftGroup1(gfield)
	{
		return calculateOffsetGroup1(gfield, "offsetLeft");
	}
	function calculateOffsetTopGroup1(gfield)
	{
		return calculateOffsetGroup1(gfield, "offsetTop");
	}
	function calculateOffsetGroup1(gfield, gattr)
	{
		var goffset = 0;
		while(gfield)
		{
			goffset += gfield[gattr];
			gfield = gfield.offsetParent;
		}
		return goffset;
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

	    <xsl:variable name="CHECK-CUSTOM-OPT">
			<xsl:value-of select="custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, EI-DOCUMENT/DO , EI-DOCUMENT/DOC/DB/DBMASK)" />
	    </xsl:variable>

        <xsl:choose>
          <xsl:when test="($CHECK-CUSTOM-OPT ='true')">
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

