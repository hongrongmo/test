<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY copy '<xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text>'>
]>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:gui="java:org.ei.bulletins.BulletinGUI"
	xmlns:java="java:java.net.URLEncoder"
>
 <xsl:variable name="RESOURCE-PATH">
        <xsl:value-of select="/PAGE/RESOURCE-PATH"/>
 </xsl:variable>
 <xsl:variable name="SESSION-ID">
        <xsl:value-of select="/PAGE/SESSION-ID"/>
 </xsl:variable>
 <xsl:variable name="CARTRIDGES">
        <xsl:value-of select="/PAGE/CARTRIDGES"/>
 </xsl:variable>
  <xsl:variable name="LITCR">
        <xsl:value-of select="/PAGE/LITCR"/>
 </xsl:variable>
  <xsl:variable name="PATCR">
        <xsl:value-of select="/PAGE/PATCR"/>
 </xsl:variable>
  <xsl:variable name="ENCODED-QSTR">
        <xsl:value-of select="java:encode(/PAGE/QTOP/QSTR)"/>
  </xsl:variable>
  <xsl:variable name="QSTR">
        <xsl:value-of select="/PAGE/QTOP/QSTR"/>
  </xsl:variable>
    <xsl:variable name="LIT-HTML">
        <xsl:value-of select="/PAGE/LIT-HTML"/>
  </xsl:variable>
  <xsl:variable name="LIT-PDF">
        <xsl:value-of select="/PAGE/LIT-PDF"/>
  </xsl:variable>
  <xsl:variable name="PAT-HTML">
        <xsl:value-of select="/PAGE/PAT-HTML"/>
  </xsl:variable>
  <xsl:variable name="PAT-PDF">
        <xsl:value-of select="/PAGE/PAT-PDF"/>
  </xsl:variable>
  <xsl:variable name="HAS-LIT">
        <xsl:value-of select="/PAGE/HAS-LIT"/>
  </xsl:variable>
  <xsl:variable name="HAS-PAT">
        <xsl:value-of select="/PAGE/HAS-PAT"/>
  </xsl:variable>
  <xsl:variable name="SHOW-HTML">
        <xsl:value-of select="/PAGE/SHOW-HTML"/>
  </xsl:variable>
  <xsl:variable name="SHOW-PDF">
        <xsl:value-of select="/PAGE/SHOW-PDF"/>
  </xsl:variable>
  <xsl:variable name="SELECTED-DB">
        <xsl:value-of select="/PAGE/SELECTED-DB"/>
  </xsl:variable>
   
<xsl:include href="bulletinTemplates.xsl"/>
<xsl:include href="../Header.xsl" />
<xsl:include href="../GlobalLinks.xsl"/>
<xsl:include href="../Footer.xsl" />


<xsl:template match="PAGE">
<html>
 <head>
  <SCRIPT LANGUAGE="Javascript" SRC="{$RESOURCE-PATH}/js/Bulletin.js"/>
  <SCRIPT LANGUAGE="Javascript" SRC="{$RESOURCE-PATH}/js/StylesheetLinks.js"/>
  
  <title>EnCompass Bulletins</title>

 

  </head>

 <body>
  
      <div style="text-align: center">
      <table class="T" border="0" width="99%" cellspacing="0" cellpadding="0">
                      <tr>
                        <td valign="top" width="100%">
                          <img src="{$RESOURCE-PATH}/i/logo.gif" border="0"/>
                        </td>
                        <td valign="middle" align="right" width="68">
                          <a href="/c/s/C?CID=endSession&amp;SYSTEM_LOGOUT=true"><img src="{$RESOURCE-PATH}/i/end.gif"  border="0"/></a>
                        </td>
                      </tr>
                      <tr>
                        <td valign="top" height="5" colspan="2">
                          <img src="{$RESOURCE-PATH}/i/s.gif" border="0" height="5"/>
                        </td>
                      </tr>
       </table>
       </div>

  <div style="text-align: center">
   <!-- INCLUDE THE GLOBAL LINKS BAR -->
    <xsl:apply-templates select="GLOBAL-LINKS">
		  <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		  <xsl:with-param name="SELECTED-DB" select ="$SELECTED-DB"/>
		  <xsl:with-param name="LINK">Bulletins</xsl:with-param>
		  <xsl:with-param name="RESOURCE-PATH" select="$RESOURCE-PATH"/>
    </xsl:apply-templates>
  </div>

  <div style="text-align: center">
   <table border="0" width="99%" cellspacing="0" cellpadding="0">
    <tr>
     <td valign="top" colspan="0" height="20" class="F"><img src="{$RESOURCE-PATH}/i/s.gif" height="20"></img></td>
    </tr>
   </table>
  </div>

  <div style="text-align: center">
   <table class="F" border="0" cellspacing="0" cellpadding="0" width="99%">
    <tr>
     <td width="24%" valign="top">
      <!-- Right side table for search form -->
      <form name="search" method="post" action="/c/s/C">
      <input type="hidden" name="docIndex" value="1"/>
      <input type="hidden" name="litcr" value="{$LITCR}"/>
      <input type="hidden" name="patcr" value="{$PATCR}"/>
      <input type="hidden" name="database" value="{$SELECTED-DB}"/>
      <input type="hidden" name="CID" value="bulletinResults"/>
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top" align="middle"><img src="{$RESOURCE-PATH}/i/arc.gif" border="0"></img></td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top" height="20"><img src="{$RESOURCE-PATH}/i/s.gif" height="20"></img></td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top"><a class="BT">In order to view the Bulletins Archives, please select Database, Year of Publication and Category and hit the "Display" button.</a></td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top" height="15"><img src="{$RESOURCE-PATH}/i/s.gif" height="15"></img></td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top"><a class="S"><b>Select Database</b></a></td>
       </tr>

       <tr>
       
        
         <!-- Start of table for search form -->
         <xsl:value-of disable-output-escaping="yes" select="gui:createDbOpt1($CARTRIDGES,$QSTR)"/>
          
          <!-- end of search form -->
        
           
       
       
       </tr>
       
       <tr>
       
        
        <!-- Start of table for search form -->
         <xsl:value-of disable-output-escaping="yes" select="gui:createDbOpt2($CARTRIDGES,$QSTR)"/>
        <!-- end of search form -->
       
       
       
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top" height="10"><img src="{$RESOURCE-PATH}/i/s.gif" height="10"></img></td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top"><a class="S"><b>Select Year of Publication</b></a></td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top">&nbsp; &nbsp; &nbsp; <a class="S">
        <!-- Start of table for search form -->
         <xsl:value-of disable-output-escaping="yes" select="gui:createYearLb()"/>
          <!-- end of search form -->
        
        </a> </td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top" height="10"><img src="{$RESOURCE-PATH}/i/s.gif" height="10"></img></td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top"><a class="S"><b>Select Category</b></a></td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top">&nbsp; &nbsp; &nbsp;
         <!-- Start of table for search form -->
         <xsl:value-of disable-output-escaping="yes" select="gui:createCategoryLb($CARTRIDGES,$QSTR)"/>
          <!-- end of search form -->
        </td>
       </tr>

       <tr>
        <td valign="top" height="15" colspan="2"><img src="{$RESOURCE-PATH}/i/s.gif" height="15"></img></td>
       </tr>

       <tr>
        <td width="6"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

        <td valign="top">&nbsp; &nbsp; &nbsp; <input type="image" name="display" value="Display" src="{$RESOURCE-PATH}/i/display.gif" border="0"></input></td>
       </tr>
      </table>
      </form>
     </td>

     <td width="25"><img src="{$RESOURCE-PATH}/i/s.gif" width="25"></img></td>

     <td background="{$RESOURCE-PATH}/i/dots1.gif" width="4"><img src="{$RESOURCE-PATH}/i/s.gif" width="4"></img></td>

     <td width="15"><img src="{$RESOURCE-PATH}/i/s.gif" width="15"></img></td>

     <td valign="top" width="76%">
      <!-- Left side table for bulletins display -->

      <table border="0" cellspacing="0" cellpadding="0" width="100%">
       <tr>
        <td valign="top" align="middle" colspan="3"><img src="{$RESOURCE-PATH}/i/bul1.gif" border="0"></img></td>
       </tr>

       <tr>
        <td height="15" colspan="3"><img src="{$RESOURCE-PATH}/i/s.gif" height="15"></img></td>
       </tr>
       <xsl:if test="boolean($HAS-LIT='true')">
       <tr>
        <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>

        <td valign="top" colspan="2"><img src="{$RESOURCE-PATH}/i/lit.gif" border="0"></img></td>
       </tr>

       <tr>
        <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>

        <td valign="top">
         <table border="0" cellspacing="0" cellpadding="0" width="100%">
          <tr>
           <td class="RMC" valign="top" colspan="11" height="1"><img src="{$RESOURCE-PATH}/i/s.gif" height="1"></img></td>
          </tr>

          <tr>
           <td class="RMC" width="1"><img src="{$RESOURCE-PATH}/i/s.gif" width="1"></img></td>

           <td valign="top">
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
             <tr>
              <td width="3"><img src="{$RESOURCE-PATH}/i/s.gif" width="3"></img></td>

              <td valign="top" width="30%"><a class="M"><b>Category</b></a></td>

              <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>

              <td valign="top" width="15%"><a class="M"><b>Published Date</b></a></td>

             
              <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td> 
              <td valign="top" width="15%"><a class="M"><b>HTML</b></a></td>
             
            
              <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>
              <td valign="top" width="15%"><a class="M"><b>PDF</b></a></td>
            

              <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>
              <td valign="top" width="15%"></td>
             </tr>

             <tr>
              <td class="RMC" colspan="11" height="1"><img src="{$RESOURCE-PATH}/i/s.gif" height="1"></img></td>
             </tr>
               <!-- First match -->
               <xsl:apply-templates select="BULLETINS" mode="LT"/>
                   
             <tr>
              <td height="6" colspan="9"><img src="{$RESOURCE-PATH}/i/s.gif" height="6"></img></td>
             </tr>
            </table>
           </td>

           <td class="RMC" width="1"><img src="{$RESOURCE-PATH}/i/s.gif" width="1"></img></td>
          </tr>

          <tr>
           <td class="RMC" valign="top" colspan="3" height="1"><img src="{$RESOURCE-PATH}/i/s.gif" height="1"></img></td>
          </tr>
         </table>
        </td>

        <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>
       </tr>

       <tr>
        <td colspan="3" height="20"><img src="{$RESOURCE-PATH}/i/s.gif" height="20"></img></td>
       </tr>
       </xsl:if>
 <xsl:if test="boolean($HAS-PAT='true')">
       <tr>
        <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>

        <td valign="top" colspan="2"><img src="{$RESOURCE-PATH}/i/pat.gif" border="0"></img></td>
       </tr>

       <tr>
        <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>

        <td valign="top">
         <table border="0" cellspacing="0" cellpadding="0" width="100%">
          <tr>
           <td class="RMC" valign="top" colspan="11" height="1"><img src="{$RESOURCE-PATH}/i/s.gif" height="1"></img></td>
          </tr>

          <tr>
           <td class="RMC" width="1"><img src="{$RESOURCE-PATH}/i/s.gif" width="1"></img></td>

           <td valign="top">
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
             <tr>
              <td width="3"><img src="{$RESOURCE-PATH}/i/s.gif" width="3"></img></td>

              <td valign="top" width="30%"><a class="M"><b>Category</b></a></td>

              <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>

              <td valign="top" width="15%"><a class="M"><b>Published Date</b></a></td>

             
              <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td> 
              <td valign="top" width="15%"><a class="M"><b>HTML</b></a></td>
             

             
              <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td> 
              <td valign="top" width="15%"><a class="M"><b>PDF</b></a></td>
             

              <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>

              <td valign="top" width="15%"><a class="M"><b>GIF</b></a></td>
             </tr>

             <tr>
              <td class="RMC" colspan="11" height="1"><img src="{$RESOURCE-PATH}/i/s.gif" height="1"></img></td>
             </tr>
             <!-- second match-->
              <xsl:apply-templates select="BULLETINS" mode="PT"/>
             
             <tr>
              <td height="6" colspan="9"><img src="{$RESOURCE-PATH}/i/s.gif" height="6"></img></td>
             </tr>
            </table>
           </td>

           <td class="RMC" width="1"><img src="{$RESOURCE-PATH}/i/s.gif" width="1"></img></td>
          </tr>

          <tr>
           <td class="RMC" valign="top" colspan="3" height="1"><img src="{$RESOURCE-PATH}/i/s.gif" height="1"></img></td>
          </tr>
         </table>
        </td>

        <td width="5"><img src="{$RESOURCE-PATH}/i/s.gif" width="5"></img></td>
       </tr>
        </xsl:if>
      
      </table>
    
     </td>
    </tr>

    <tr>
     <td height="20"><img src="{$RESOURCE-PATH}/i/s.gif" height="20"></img></td>
    </tr>
   </table>
  </div>
  <!-- Insert the Footer table -->
		<xsl:apply-templates select="FOOTER">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="SELECTED-DB" select="$SELECTED-DB"/>
			<xsl:with-param name="RESOURCE-PATH" select="$RESOURCE-PATH"/>
		</xsl:apply-templates>
 </body>
</html>
 </xsl:template>
</xsl:stylesheet>

