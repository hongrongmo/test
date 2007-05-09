<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY copy '<xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text>'>
]>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:gui="java:org.ei.books.BookGUI"
    exclude-result-prefixes="gui java html xsl">


<xsl:variable name="SEARCH-FORM">
    <xsl:choose>
        <xsl:when test="/PAGE/QTOP/QTY='Q'">keyWordSearch</xsl:when>
        <xsl:otherwise>advancedSearch</xsl:otherwise>
    </xsl:choose>
</xsl:variable>

<xsl:variable name="QTY">
        <xsl:value-of select="/PAGE/QTOP/QTY"/>
</xsl:variable>

 <xsl:variable name="QDIS">
            <xsl:value-of select="/PAGE/QTOP/QDIS"/>
        </xsl:variable>
 <xsl:variable name="QCO">
            <xsl:value-of select="/PAGE/QTOP/QCO"/>
        </xsl:variable>
         <xsl:variable name="DBASE">
            <xsl:value-of select="/PAGE/QTOP/DBASE"/>
        </xsl:variable>
 <xsl:variable name="QSTR">
            <xsl:value-of select="/PAGE/QTOP/QSTR"/>
        </xsl:variable>
 <xsl:variable name="ENCODED-QSTR">
             <xsl:value-of select="java:encode($QSTR)"/>
 </xsl:variable>
 <xsl:variable name="NXT">
            <xsl:value-of select="/PAGE/NAV/@NEXT"/>
        </xsl:variable>
        <xsl:variable name="PRV">
            <xsl:value-of select="/PAGE/NAV/@PREV"/>
        </xsl:variable>
        <xsl:variable name="INDEX">
            <xsl:value-of select="/PAGE/NAV/@INDEX"/>
        </xsl:variable>
         <xsl:variable name="ISBN">
        <xsl:value-of select="/PAGE/BP/BN"/>
    </xsl:variable>

 <xsl:variable name="SCY">
       DISABLE
 </xsl:variable>
 <xsl:variable name="FRM">REFINE_SEARCH</xsl:variable>

<xsl:include href="bookTemplates.xsl" />
<xsl:include href="../GlobalLinks.xsl" />
<xsl:include href="../Footer.xsl" />
<xsl:include href="bookHeader.xsl"/>


    <xsl:template match="PAGE">


<html>
  <head>
    <title>Referex Search Results</title>
    <SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/engresources/js/ReferexSearch.js"/>
    <SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
  </head>
  <body topmargin="0" marginheight="0" marginwidth="0">

        <!-- Start of logo table -->
        <xsl:call-template name="BOOK-HEADER"/>
        <!-- End of logo table -->

        <!-- Start of Global links -->
        <xsl:apply-templates select="GLOBAL-LINKS">
            <xsl:with-param name="SELECTED-DB"><xsl:value-of select="DBMASK"/></xsl:with-param>
            <xsl:with-param name="LINK">Book</xsl:with-param>
        </xsl:apply-templates>
        <!-- End of global links -->

         <center>
            <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1" height="20">
                <tr>
                  <form name="selectresults">
                  <td valign="bottom" align="left">
                    &nbsp; <a href="#qsearchform"><img src="/engresources/images/rs.gif" border="0"></img></a>
                    &nbsp; <a href="/controller/servlet/Controller?CID={$SEARCH-FORM}"><img src="/engresources/images/ns.gif" border="0"></img></a>
                  </td>
                  <td valign="bottom" height="20" align="right">
                   <xsl:if test="($QCO > 0)">
                     <xsl:if test="($PRV > 0)">
                     <a href="/controller/servlet/Controller?CID=resultsKeyWord&amp;docIndex={$PRV}&amp;queryToString={$ENCODED-QSTR}"><img src="/engresources/images/pp.gif" border="0"></img></a> &nbsp;
                     </xsl:if>

                      <xsl:if test="($NXT > 0)">
                     <a href="/controller/servlet/Controller?CID=resultsKeyWord&amp;docIndex={$NXT}&amp;queryToString={$ENCODED-QSTR}"><img src="/engresources/images/np.gif" border="0"></img></a>&nbsp;
                     </xsl:if>

                     <a class="MedBlackText">
                    <input type="hidden" name="CID" value="resultsKeyWord"/>
                    <input type="hidden" name="queryToString" value="{$QSTR}"/>

                    <xsl:value-of disable-output-escaping="yes" select="gui:getPagingComponent($INDEX,25,$QCO)"/>

                    &nbsp; <input type="image" name="go" src="/engresources/images/go1.gif" border="0"/></a>&nbsp;
                   </xsl:if>
                  </td>
                  </form>
                 </tr>
             </table>
          </center>
        <!-- End of topnavigation bar -->


    <!-- Start of table for search results -->
    <center>
      <form name="results1" action="#" method="POST">
      </form>
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
      	<tr>
	  <td colspan="7" height="2">
	     <img src="/engresources/images/s.gif" height="2"></img>
	  </td>
	</tr>
	<tr>
	  <td colspan="7" align="right"><a class="LgBlueLink" href="javascript:window.open('/engresources/downloads.html','newwind','width=550,height=500,scrollbars=yes,resizable,taskbar=1,status=yes');void('');">Viewing Requirements</a></td>
      	</tr>  
        <tr>
          <td colspan="7" height="8">
            <img src="/engresources/images/s.gif" height="8"></img>
          </td>
        </tr>
        <tr>
          <td valign="top" colspan="5">
            <a class="EvHeaderText">Search Results</a>
          </td>
        </tr>
        <tr>
            <td valign="top" height="5" colspan="5"><img src="/engresources/images/s.gif" border="0" height="5"/></td>
        </tr>


           <tr>
          <td valign="top" colspan="5">

            <xsl:if test="$DBASE = 'PART'">
            <a class="SmBlackText"><xsl:value-of select="$QCO"/> <b>  section(s)</b> found in Referex for: <xsl:value-of select="$QDIS"/></a>
           </xsl:if>
           <xsl:if test="$DBASE = 'BOOK'">
            <a class="SmBlackText"><xsl:value-of select="$QCO"/> <b>  book(s)</b> found in Referex for: <xsl:value-of select="$QDIS"/></a>
           </xsl:if>
          </td>
        </tr>
      </table>
      <table width="100%">
        <tr>
          <td colspan="5" height="20" bgcolor="#FFFFFF">
            <img src="/engresources/images/s.gif" border="0" height="20"></img>
          </td>
        </tr>
        <xsl:apply-templates select="BOOKS"/>

      </table>
    </center>

    <!-- End of search results -->
    <!-- start of bottom blue bar for navigation-->
    <center>
        <table border="0" width="99%" cellspacing="0" cellpadding="0" height="20" bgcolor="#C3C8D1">
          <tr>
            <form name="selectresults">

            <td valign="bottom" height="20" align="right">
            <xsl:if test="($QCO > 0)">
                <xsl:if test="($PRV > 0)">
                <a href="/controller/servlet/Controller?CID=resultsKeyWord&amp;docIndex={$PRV}&amp;queryToString={$ENCODED-QSTR}"><img src="/engresources/images/pp.gif" border="0"></img></a> &nbsp;
                </xsl:if>

                <xsl:if test="($NXT > 0)">
                <a href="/controller/servlet/Controller?CID=resultsKeyWord&amp;docIndex={$NXT}&amp;queryToString={$ENCODED-QSTR}"><img src="/engresources/images/np.gif" border="0"></img></a>&nbsp;
                </xsl:if>


            <a class="MedBlackText">
            <input type="hidden" name="CID" value="resultsKeyWord"/>
            <input type="hidden" name="queryToString" value="{$QSTR}"/>
            <xsl:value-of disable-output-escaping="yes" select="gui:getPagingComponent($INDEX,25,$QCO)"/>

                &nbsp; <input type="image" name="go" src="/engresources/images/go1.gif" border="0"/></a>&nbsp;

        </xsl:if>
            </td>
            </form>
          </tr>
        </table>
    </center>
    <!-- End of bottom blue bar for navigation-->
    <!-- Table for refine search form -->


         <center>
          <a name="qsearchform"></a><table  border="0" width="99%" cellspacing="0" cellpadding="0">
      <xsl:value-of disable-output-escaping="yes" select="gui:buildForm($FRM,$QSTR)"/>
          </table>
         </center>


     <br/>

    <br/>

    <xsl:apply-templates select="FOOTER">
            <xsl:with-param name="SELECTED-DB"><xsl:value-of select="DBMASK"/></xsl:with-param>
    </xsl:apply-templates>

  </body>
</html>

    </xsl:template>

</xsl:stylesheet>


