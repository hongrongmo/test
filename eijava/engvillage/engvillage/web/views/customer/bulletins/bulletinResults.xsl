<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY copy '<xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text>'>
]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40" xmlns:java="java:java.net.URLEncoder" xmlns:gui="java:org.ei.bulletins.BulletinGUI">
    <xsl:variable name="RESOURCE-PATH">
        <xsl:value-of select="/PAGE/RESOURCE-PATH"/>
    </xsl:variable>
    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="/PAGE/SESSION-ID"/>
    </xsl:variable>
    <xsl:variable name="ENCODED-QSTR">
        <xsl:value-of select="java:encode(/PAGE/QTOP/QSTR)"/>
    </xsl:variable>
    <xsl:variable name="QSTR">
        <xsl:value-of select="/PAGE/QTOP/QSTR"/>
    </xsl:variable>
    <xsl:variable name="QCO">
        <xsl:value-of select="/PAGE/QTOP/QCO"/>
    </xsl:variable>
    <xsl:variable name="QDIS">
        <xsl:value-of select="/PAGE/QTOP/QDIS"/>
    </xsl:variable>
    <xsl:variable name="DB">
        <xsl:value-of select="/PAGE/DB"/>
    </xsl:variable>
    <xsl:variable name="NXT">
        <xsl:value-of select="/PAGE/NAV/@NEXT"/>
    </xsl:variable>
    <xsl:variable name="PRV">
        <xsl:value-of select="/PAGE/NAV/@PREV"/>
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
  <xsl:variable name="SELECTED-DB">
        <xsl:value-of select="/PAGE/SELECTED-DB"/>
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
  <xsl:variable name="SHOW-HTML">
        <xsl:value-of select="/PAGE/SHOW-HTML"/>
  </xsl:variable>
  <xsl:variable name="SHOW-PDF">
        <xsl:value-of select="/PAGE/SHOW-PDF"/>
  </xsl:variable>
   
    
    <xsl:include href="../Header.xsl"/>
    <xsl:include href="../GlobalLinks.xsl"/>
    <xsl:include href="../Footer.xsl" />
    <xsl:include href="bulletinTemplates.xsl"/>
    <xsl:template match="PAGE">
        <html>
            <head>
                <title>EnCompass Bulletins</title>
                <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Bulletin.js"/>
                <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
            </head>
            <body>
               <div style="text-align: center">
	             <table class="T" border="0" width="99%" cellspacing="0" cellpadding="0">
	       	      <tr>
	       		<td valign="top" width="100%">
	       		  <img src="/engresources/images/ev2logo5.gif" border="0"/>
	       		</td>
	       		<td valign="middle" align="right" width="68">
	       		  <a href="/controller/servlet/Controller?CID=endSession&amp;SYSTEM_LOGOUT=true">
	       		  <img src="/engresources/images/endsession.gif"  border="0"/></a>
	       		</td>
	       	      </tr>
	       	      <tr>
	       		<td valign="top" height="5" colspan="2">
	       		  <img src="/engresources/images/encompass/s.gif" border="0" height="5"/>
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
	          <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
	           <tr>
	            <td valign="top" colspan="0" height="20" class="F">
	            <img src="/engresources/images/encompass/s.gif" height="20"></img>
	            </td>
	           </tr>
	          </table>
	          <table border="0" width="99%" cellspacing="0" cellpadding="0">
	       	    <tr>
	       		<td valign="top" colspan="0" height="20" class="F">
	       		    <img src="/engresources/images/encompass/s.gif" height="20"/>
	       		</td>
	       	    </tr>
	           </table>
  		</div>
                <div style="text-align: center">
                    <table class="F" border="0" cellspacing="0" cellpadding="0" width="99%">
                        <tr>
                            <td width="24%" valign="top">
                                <!-- Right side table for search form -->
                                <form name="search" method="get" action="/controller/servlet/Controller">
                                     <input type="hidden" name="CID" value="bulletinResults"/>
                                     <input type="hidden" name="docIndex" value="1"/>
                                     <input type="hidden" name="litcr" value="{$LITCR}"/>
                                     <input type="hidden" name="patcr" value="{$PATCR}"/>
                                     <input type="hidden" name="database" value="{$SELECTED-DB}"/>
                                     
                                    <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top" align="middle">
                                                <img src="/engresources/images/encompass/arc.gif" border="0"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top" height="20">
                                                <img src="/engresources/images/encompass/s.gif" height="20"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top">
                                                <a class="BT">In order to view the Bulletins Archives, please select
                                                    Database, Year and Category and hit the "Display" button.</a>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top" height="15">
                                                <img src="/engresources/images/encompass/s.gif" height="15"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top">
                                                <a class="S">
                                                    <b>Select Database</b>
                                                </a>
                                            </td>
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
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top" height="10">
                                                <img src="/engresources/images/encompass/s.gif" height="10"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top">
                                                <a class="S">
                                                    <b>Select Year of Publication</b>
                                                </a>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top">&nbsp; &nbsp; &nbsp; <a class="S">
                                                    <!-- Start of table for search form -->
                                                    <xsl:value-of disable-output-escaping="yes" select="gui:createYearLb($QSTR)"/>
                                                    <!-- end of search form -->
                                                </a>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top" height="10">
                                                <img src="/engresources/images/encompass/s.gif" height="10"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top">
                                                <a class="S">
                                                    <b>Select Category</b>
                                                </a>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top">&nbsp; &nbsp; &nbsp;
                                                <!-- Start of table for search form -->
                                                <xsl:value-of disable-output-escaping="yes" select="gui:createCategoryLb($CARTRIDGES,$QSTR)"/>
                                                <!-- end of search form -->
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" height="15" colspan="2">
                                                <img src="/engresources/images/encompass/s.gif" height="15"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="6">
                                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                                            </td>
                                            <td valign="top">&nbsp; &nbsp; &nbsp; <input type="image"
                                                    name="display" value="Display" src="/engresources/images/encompass/display.gif" border="0"/>
                                            </td>
                                        </tr>
                                    </table>
                                </form>
                            </td>
                            <td width="25">
                                <img src="/engresources/images/encompass/s.gif" width="25"/>
                            </td>
                            <td background="/engresources/images/encompass/dots1.gif" width="4">
                                <img src="/engresources/images/encompass/s.gif" width="4"/>
                            </td>
                            <td width="15">
                                <img src="/engresources/images/encompass/s.gif" width="15"/>
                            </td>
                            <td valign="top" width="76%">
                                <!-- Left side table for bulletins display -->
                                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                    <tr>
                                        <td valign="top" align="middle" colspan="3">
                                            <img src="/engresources/images/encompass/abl.gif" border="0"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td height="15" colspan="2" align="right">
                                            <a  class="L" href="/controller/servlet/Controller?CID=bulletinSearch&amp;queryStr={$ENCODED-QSTR}&amp;EISESSION={$SESSION-ID}&amp;database={$SELECTED-DB}">
                                                <img src="/engresources/images/encompass/rec.gif" border="0"/>
                                            </a>
                                        </td>
                                        <td width="5" height="1">
                                            <img src="/engresources/images/encompass/s.gif" width="5"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td width="10">
                                            <img src="/engresources/images/encompass/s.gif" width="10"/>
                                        </td>
                                        <td valign="top" colspan="2">
                                            <a class="S">
                                                <b>
                                                    <xsl:value-of select="$QDIS" disable-output-escaping="yes"/>
                                                </b>
                                                <br/>
                                                <b>
                                                    <xsl:choose>
                                                        <xsl:when test="$QCO = 0"> 0 bulletins found. </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:value-of select="$QCO"/> bulletins found. 
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </b>
                                            </a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td height="8" colspan="3">
                                            <img src="/engresources/images/encompass/s.gif" height="8"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td width="10">
                                            <img src="/engresources/images/encompass/s.gif" width="10"/>
                                        </td>
                                        <td valign="top">
                                            <!-- table for archive table -->
                                            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                                <tr>
                                                    <td class="RMC" colspan="3" height="1">
                                                        <img src="/engresources/images/encompass/s.gif" height="1"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="RMC" width="1">
                                                        <img src="/engresources/images/encompass/s.gif" width="1"/>
                                                    </td>
                                                    <td valign="top">
                                                        <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                                            <tr>
                                                            <td width="4">
                                                            <img src="/engresources/images/encompass/s.gif" width="4"/>
                                                            </td>
                                                            <td valign="top">
                                                            <a class="M">
                                                            <b>Bulletin</b>
                                                            </a>
                                                            </td>
                                                            <td valign="top" width="10">
                                                            <img src="/engresources/images/encompass/s.gif" width="10"/>
                                                            </td>
                                                            <td valign="top">
                                                            <a class="M">
                                                            <b>Published Date</b>
                                                            </a>
                                                            </td>
                                                            
                                                            
                                                            <td valign="top" width="4">
                                                            <img src="/engresources/images/encompass/s.gif" width="10"/>
                                                            </td>
                                                            <td valign="top">
                                                            <a class="M">
                                                            <b>HTML Format</b>
                                                            </a>
                                                            </td>
                                                            
                                                            
                                                            
                                                            <td valign="top" width="4">
                                                            <img src="/engresources/images/encompass/s.gif" width="4"/>
                                                            </td>
                                                            <td valign="top">
                                                            <a class="M">
                                                            <b>PDF Format</b>
                                                            </a>
                                                            </td>
                                                            
                                                            
                                                            <td valign="top" width="4">
                                                            <img src="/engresources/images/encompass/s.gif" width="4"/>
                                                            </td>
                                                            <xsl:choose>
                                                            <xsl:when test="($DB = '1')">
                                                            <tr>
                                                            <td class="RMC" colspan="8" height="1">
                                                            <img src="/engresources/images/encompass/s.gif" height="1"/>
                                                            </td>
                                                            </tr>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                            <td valign="top">
                                                            <a class="M">
                                                            <b>GIF Format</b>
                                                            </a>
                                                            </td>
                                                            <tr>
                                                            <td class="RMC" colspan="16" height="1">
                                                            <img src="/engresources/images/encompass/s.gif" height="1"/>
                                                            </td>
                                                            </tr>
                                                            </xsl:otherwise>
                                                            </xsl:choose>
                                                            </tr>
                                                            <xsl:apply-templates select="BULLETINS" mode="RESULTS"/>
                                                            <tr>
                                                            <td colspan="6" height="6">
                                                            <img src="/engresources/images/encompass/s.gif" height="6"/>
                                                            </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                    <td class="RMC" width="1">
                                                        <img src="/engresources/images/encompass/s.gif" width="1"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="RMC" colspan="3" height="1">
                                                        <img src="/engresources/images/encompass/s.gif" height="1"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td colspan="3" height="15">
                                                        <img src="/engresources/images/encompass/s.gif" height="15"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td colspan="3" align="middle">
                                                        <xsl:if test="($QCO &gt; 0)">
                                                            <xsl:if test="($PRV &gt; 0)">
                                                            <a href="/controller/servlet/Controller?CID=bulletinResults&amp;docIndex={$PRV}&amp;queryStr={$ENCODED-QSTR}&amp;EISESSION={$SESSION-ID}&amp;database={$SELECTED-DB}">
                                                            <img src="/engresources/images/encompass/pp.gif" border="0"/>
                                                            </a>
                                                            </xsl:if> &nbsp; &nbsp; &nbsp; <xsl:if test="($NXT &gt; 0)">
                                                            <a href="/controller/servlet/Controller?CID=bulletinResults&amp;docIndex={$NXT}&amp;queryStr={$ENCODED-QSTR}&amp;EISESSION={$SESSION-ID}&amp;database={$SELECTED-DB}">
                                                            <img src="/engresources/images/encompass/np.gif" border="0"/>
                                                            </a>
                                                            </xsl:if>
                                                        </xsl:if>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                        <td width="5">
                                            <img src="/engresources/images/encompass/s.gif" width="5"/>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td height="20">
                                <img src="/engresources/images/encompass/s.gif" height="20"/>
                            </td>
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
