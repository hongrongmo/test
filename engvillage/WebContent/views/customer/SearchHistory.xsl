<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/static/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLDecoder"
    xmlns:resolver="org.ei.gui.InternalResolver"
    exclude-result-prefixes="java html xsl"
>

<xsl:output method="html"/>

<!-- including xsl for header-->
<xsl:include href="Header.xsl"/>

<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="Footer.xsl" />

<xsl:include href="SessionHistory.xsl" />

<xsl:template match="PAGE">

	<!-- these are variable getting from xml-->
	<xsl:variable name="CID"><xsl:value-of select="CID"/></xsl:variable>
	<xsl:variable name="COUNT"><xsl:value-of select="CURR-PAGE-ID"/></xsl:variable>
	<xsl:variable name="SEARCHID"><xsl:value-of select="SEARCH-ID"/></xsl:variable>
	<xsl:variable name="RESULTS-DATABASE"><xsl:value-of select="DBMASK"/></xsl:variable>
	<xsl:variable name="SESSIONID"><xsl:value-of select="SESSION-ID"/></xsl:variable>
	<xsl:variable name="SEARCHTYPE"><xsl:value-of select="SESSION-DATA/SEARCH-TYPE"/></xsl:variable>
	<xsl:variable name="ISHISTORYEMPTY"><xsl:value-of select="ISHISTORY-EMPTY"/></xsl:variable>
	<xsl:variable name="SORTOPTION"><xsl:value-of select="SESSION-DATA/SORT-OPTION"/></xsl:variable>
	<xsl:variable name="DECODED-RESULTS-NAV">
	    <xsl:value-of select="java:decode(/PAGE/PAGE-NAV/RESULTS-NAV)"/>
	</xsl:variable>
	<xsl:variable name="ENCODED-RESULTS-NAV">
		<xsl:value-of select="/PAGE/PAGE-NAV/RESULTS-NAV"/>
	</xsl:variable>
	<xsl:variable name="DECODED-NEWSEARCH-NAV">
		<xsl:value-of select="java:decode(/PAGE/PAGE-NAV/NEWSEARCH-NAV)"/>
	</xsl:variable>
	<xsl:variable name="ENCODED-NEWSEARCH-NAV">
		<xsl:value-of select="/PAGE/PAGE-NAV/NEWSEARCH-NAV"/>
	</xsl:variable>



  <!--
    This page displays all search queries for the current session.

  -->

  <html>
  <head>
  <title>Search History</title>

    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Robohelp.js"/>

      <xsl:text disable-output-escaping="yes">
      <![CDATA[
      <xsl:comment>
      <script language="javascript">

      function doReset()
      {
          document.combineSearch.combine.value="";
          document.combineSearch.sort[0].checked="true";
          self.focus();
      }

      function Validate(numberofsearchs)
      {

          if(numberofsearchs==0)
          {
              window.alert("Search history is empty, combine search is not possible.");
              return false;
          }
          var combineString=document.combineSearch.combine.value;
          if((combineString=="") || (combineString==null))
          {
              window.alert("Enter values to combine searches.");
              return false;
          }

          if(!(combineString==""))
          {
              var searchLength= combineString.length;
              var tempword = combineString;
              var tempLength=0;

              while (tempword.substring(0,1) == ' ')
              {
                  tempword = tempword.substring(1);
                  tempLength = tempLength + 1;
              }

              if ( searchLength == tempLength)
              {
                  window.alert("Enter values to combine searches.");
                  return (false);
              }
          }

          var searchLength = combineString.length;
          var tempword = combineString;

          var hashindex=tempword.indexOf('#');
          if(hashindex<0)
          {
              window.alert("Must contain atleast one #.");
              return false;
          }

          for( var i=0;i<searchLength;i++)
          {

              var hashindex=tempword.indexOf('#');

              if(hashindex>=0 && tempword.charAt(hashindex+1)==' ')
              {
                  window.alert("Space is not allowed between # and search query number.");
                  return false;
              }


              if(hashindex>=0 && isNaN(tempword.charAt(hashindex+1)))
              {
                  window.alert("only number is allowed after #.");
                  return false;
              }

              tempword = tempword.substring(1);
              searchLength=tempword.length;
          }

          var lasthashindex=combineString.lastIndexOf('#');

          if(lasthashindex==(combineString.length-1))
          {
              window.alert("Please enter a number after #.");
              return false;
          }

          return true;

      }
      </script>
      </xsl:comment>
      ]]>
      </xsl:text>
      <!-- Java script ends -->

  </head>

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

  <center>

    <xsl:apply-templates select="HEADER">
      <xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
      <xsl:with-param name="SEARCH-TYPE" select="$SEARCHTYPE"/>
      <xsl:with-param name="COMBINED-SEARCH">Combined</xsl:with-param>
    </xsl:apply-templates>

    <xsl:apply-templates select="GLOBAL-LINKS">
      <xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
      <xsl:with-param name="LINK" select="resolver:resolveSearchType($ENCODED-NEWSEARCH-NAV)"/>
    </xsl:apply-templates>

    <xsl:variable name="NEWSEARCHTYPE">
      <xsl:choose>
        <xsl:when test="boolean($SEARCHTYPE='Thesaurus')">thesSearch</xsl:when>
        <xsl:when test="boolean($SEARCHTYPE='Quick')">quickSearch</xsl:when>
        <xsl:when test="boolean($SEARCHTYPE='Expert')">expertSearch</xsl:when>
        <xsl:when test="boolean($SEARCHTYPE='Easy')">easySearch</xsl:when>
        <xsl:otherwise>quickSearch</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- Start of navigation bar -->
    <!-- This could include a 'template' Navigation Bar with some modifications -->
    <center>
      <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
        <tr>
		<td align="left" valign="middle" height="24">
			<xsl:if test="string($DECODED-RESULTS-NAV)">
				&spcr8;
				<a href="/controller/servlet/Controller?{$DECODED-RESULTS-NAV}"><img src="/static/images/sr.gif" border="0"/></a>
				&spcr8;
				<a href="/controller/servlet/Controller?{$DECODED-NEWSEARCH-NAV}"><img src="/static/images/ns.gif" border="0" /></a>
			</xsl:if>
		</td>
        </tr>
      </table>
    </center>

  <xsl:variable name="NUMSEARCHS"><xsl:value-of select="count(SESSION-HISTORY/SESSION-DATA)"/></xsl:variable>

  <xsl:choose>
    <xsl:when test="($NUMSEARCHS>0)">

      <!-- Start of Session history -->
      <xsl:apply-templates select="SESSION-TABLE">
        <xsl:with-param name="COMPLETE-SEARCH-HISTORY">true</xsl:with-param>
      </xsl:apply-templates>
      <!-- End of Session History -->

      <center>
        <!-- Start of table for combine search form -->
        <table border="0" cellspacing="0" cellpadding="0" width="99%" bgcolor="#C3C8D1">
          <tr>
              <td valign="top" height="10" colspan="7" bgcolor="#FFFFFF"><img src="/static/images/s.gif" height="10" border="0"/></td>
          </tr>
          <tr>
            <td valign="top" colspan="7" bgcolor="#FFFFFF">
              <a class="EvHeaderText">Combine Previous Searches</a>
            </td>
          </tr>
          <tr>
            <td valign="top" height="3" colspan="7" bgcolor="#FFFFFF">
              <img src="/static/images/s.gif" border="0" height="3"/>
            </td>
          </tr>
          <FORM name="combineSearch" method="post" action="/controller/servlet/Controller?CID=combineSearchHistory" onSubmit="return Validate({$NUMSEARCHS})">
          <tr>
            <td valign="top" height="5" colspan="7">
              <img src="/static/images/s.gif" border="0" height="5"/>
            </td>
          </tr>
          <tr>
            <td valign="top" width="4" bgcolor="#C3C8D1">
              <img src="/static/images/s.gif" border="0" width="4"/>
            </td>
            <td valign="top" colspan="2" bgcolor="#C3C8D1" width="165">
              <A CLASS="SmBlueTableText"><B>ENTER SEARCHES TO COMBINE</B></A>
            </td>
            <td valign="top" width="15">
              <img src="/static/images/s.gif" border="0" width="15"/>
            </td>
            <td valign="top" colspan="3" bgcolor="#C3C8D1">
              <A CLASS="SmBlueTableText"><B>SORT BY</B></A>
            </td>
          </tr>
          <tr>
            <td valign="top" width="4" bgcolor="#C3C8D1">
              <img src="/static/images/s.gif" border="0" width="4"/>
            </td>
            <td valign="top" width="15" bgcolor="#C3C8D1">
              <img src="/static/images/s.gif" border="0" width="15"/>
            </td>
            <td valign="top" bgcolor="#C3C8D1" width="195">
              <A CLASS="MedBlackText">
              <input type="text" name="combine" size="28"/></A>
              &#160;
              <a href="javascript:makeUrl('Search_History_Features.htm')">
	      	  <img src="/static/images/blue_help1.gif" border="0"/>
	      </a>  
            </td>
            <td valign="top" width="15">
              <img src="/static/images/s.gif" border="0" width="15"/>
            </td>
            <td valign="top" width="15" bgcolor="#C3C8D1">
              <img src="/static/images/s.gif" border="0" width="15"/>
            </td>
            <td valign="top" width="200">

                <!-- jam - "Turkey" added context sensitive help icon to relevance -->
                <input type="radio" name="sort" value="relevance">
                  <xsl:if test="($SORTOPTION='relevance') or not(($SORTOPTION='publicationYear') or ($SORTOPTION='yr'))">
                    <xsl:attribute name="checked"/>
                  </xsl:if>
                </input>
                <a CLASS="SmBlackText">Relevance</a>&#160;
		<a href="javascript:makeUrl('Sorting_from_the_Search_Form.htm')">
			<img src="/static/images/blue_help1.gif" border="0"/>
	         </a>                
                <input type="radio" name="sort" value="yr">
                  <xsl:if test="($SORTOPTION='publicationYear') or ($SORTOPTION='yr')">
                    <xsl:attribute name="checked"/>
                  </xsl:if>
                </input>
                <a CLASS="SmBlackText">Publication year</a>

            </td>
            <td valign="top">
              <input type="image" src="/static/images/search_orange1.gif" border="0" name="search" value="Search"/> &#160;
              <a href="javascript:doReset()"><img src="/static/images/reset_orange1.gif" border="0"/></a>
            </td>
          </tr>
          <tr>
            <td valign="top" height="4" colspan="7"><img src="/static/images/s.gif" border="0" height="4"/></td>
          </tr>
          </FORM>
        </table>
      <!-- End search for combine search form -->
      </center>
      <center>
      <!-- Start of table news -->
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top" height="10" colspan="3">
            <img src="/static/images/s.gif" border="0" height="10"/>
            </td>
          </tr>
          <tr>
            <td valign="top" height="15" bgcolor="#3173B5" colspan="3">
            <a CLASS="LgWhiteText"><b>&#160; Combined Search</b></a>
            </td>
          </tr>
          <tr>
            <td valign="top" bgcolor="#3173B5" width="1">
            <img src="/static/images/s.gif" border="0" width="1"/>
            </td>
            <td valign="top" bgcolor="#FFFFFF">
              <!-- Tiny table for news features -->
              <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td valign="top" height="10" colspan="3"><img src="/static/images/s.gif" border="0" height="10"/></td>
                </tr>
                <tr>
                  <td valign="top" width="4">
                  <img src="/static/images/s.gif" border="0" width="4"/>
                  </td>
                  <td valign="top">
                  <a CLASS="SmBlackText">
                  Combine searches listed in the Search History as follows:<br/>
                  (&#35;1 AND &#35;2)<br/>
                  (&#35;1 AND &#35;2) OR (&#35;3 AND &#35;4)<br/>
                  (&#35;1 OR &#35;3) NOT &#35;2
                  <P>
                  Combine searches executed in the same database only.
                  </P>
                  </a>
                  </td>
                  <td valign="top" width="2">
                    <img src="/static/images/s.gif" border="0" width="2"/>
                  </td>
                </tr>
                <tr>
                  <td valign="top" height="10" colspan="3"><img src="/static/images/s.gif" border="0" height="10"/></td>
                </tr>
              </table>
            <!-- End of Tiny table for news features -->
            </td>
            <td valign="top" bgcolor="#3173B5" width="1">
            <img src="/static/images/s.gif" border="0" width="1"/>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan="3" bgcolor="#3173B5" height="1">
            <img src="/static/images/s.gif" border="0" height="1"/>
            </td>
          </tr>
        </table>
      </center>
      <!-- End of table for news item -->
    </xsl:when>

    <xsl:when test="($NUMSEARCHS=0)">

      <!-- Message -->
      <center>
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <tr><td valign="top" height="20"><img src="/static/images/s.gif" border="0" height="20"/></td></tr>
          <tr><td valign="top"><a class="EvHeaderText">Search History</a></td></tr>
          <tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
          <tr><td valign="top"><a CLASS="MedBlackText">Currently there are no searches in the Search History.</a></td></tr>
        </table>
      </center>
      <br/>

    </xsl:when>
  </xsl:choose>

  <!-- Insert the Footer table -->
  <xsl:apply-templates select="FOOTER">
    <xsl:with-param name="SESSION-ID"><xsl:value-of select="$SESSIONID"/></xsl:with-param>
    <xsl:with-param name="SELECTED-DB"><xsl:value-of select="$RESULTS-DATABASE"/></xsl:with-param>
  </xsl:apply-templates>

  </center>

  </body>
  </html>

</xsl:template>

</xsl:stylesheet>
