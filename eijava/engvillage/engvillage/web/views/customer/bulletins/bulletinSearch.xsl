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
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Bulletin.js"/>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/stylesheets/ev2ie.css"/>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/stylesheets/ev2net.css"/>
  <title>EnCompass Bulletins</title>

 

  </head>

 <body>
 <table width="100%" align="center" cellspacing="0" cellpadding="0">
 <tr>
 <td>
    <div style="text-align: center">
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
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
		  <img src="/engresources/images/s.gif" border="0" height="5"/>
	      </td>
	   </tr>
       </table>
    </div>
</td>
</tr>
<tr>
<td>
  <div style="text-align: center">
    <!-- INCLUDE THE GLOBAL LINKS BAR -->
    <xsl:apply-templates select="GLOBAL-LINKS">
		  <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		  <xsl:with-param name="SELECTED-DB" select ="$SELECTED-DB"/>
		  <xsl:with-param name="LINK">Bulletins</xsl:with-param>
		  <xsl:with-param name="RESOURCE-PATH" select="$RESOURCE-PATH"/>
    </xsl:apply-templates>
  </div>
</td>
</tr>
<tr>
<td>
<table width="99%" bgcolor="#C3C8D1" cellspacing="0" cellpadding="0" align="center" border="0">
<tr>
    <td width="1" bgcolor="#C3C8D1">
	<img src="/engresources/images/s.gif"></img>
    </td>
    <td width="100%">
       <div style="text-align: center">
       <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
       <tr>
 	  <td valign="top" height="20">
	    	<img src="/engresources/images/s.gif" height="20"></img>
	  </td>
       </tr>
       </table>
       <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#F5F5F5">
       <tr>
	  <td valign="top" height="20">
		<img src="/engresources/images/s.gif" height="20"/>
	  </td>
       </tr>
       </table>
       </div>

       <div style="text-align: center">
       <table bgcolor="#F5F5F5" border="0" cellspacing="0" cellpadding="0" width="100%">
       <tr>
      	   <td width="20">
      		<img src="/engresources/images/s.gif" width="20"></img>
      	   </td>
	   <td width="250" valign="top" cellspacing="0" cellpadding="0">
	   <!-- Right side table for search form -->
	   <form name="search" method="get" action="/controller/servlet/Controller">
	   <input type="hidden" name="docIndex" value="1"/>
	   <input type="hidden" name="litcr" value="{$LITCR}"/>
	   <input type="hidden" name="patcr" value="{$PATCR}"/>
	   <input type="hidden" name="database" value="{$SELECTED-DB}"/>
	   <input type="hidden" name="CID" value="bulletinResults"/>
           <table border="0" cellspacing="0" cellpadding="0" width="100%" bgcolor="#C3C8D1">
           <tr>
	       <td width="10" bgcolor="#C3C8D1">
		  <img src="/engresources/images/s.gif" width="10"></img>
	       </td>
               <td bgcolor="#C3C8D1" cellspacing="0" cellpadding="0">
               <table border="0" cellspacing="0" cellpadding="0" width="220">
	       <tr>
		    <td>
			<img src="/engresources/images/s.gif" width="4"></img>
		    </td>
		</tr>
		<tr>
		    <td valign="top" align="left">
			<a class="DBlueText"><b>DISPLAY ARCHIVES</b></a>
		    </td>
	        </tr>

	        <tr>
		   <td valign="top" height="20">
			<img src="/engresources/images/s.gif" height="20"></img>
		   </td>
	        </tr>

	        <tr>
		   <td valign="top">
			<a class="SmBlackText">In order to view the Bulletins Archives, please select Database, Year of Publication and Category and hit the "Display" button.</a>
		   </td>
	        </tr>

	        <tr>
		   <td valign="top" height="15">
			<img src="/engresources/images/s.gif" height="15"></img>
		   </td>
	        </tr>

	        <tr>
		   <td valign="top">
			<a class="SmBlackText "><b>Select Database</b></a>
		   </td>
	        </tr>

	        <tr>      
		   <td valign="top">
			<input type="radio" name="db" onclick="refreshCategories()" checked="true" value="1"></input>
			<a class="SmBlackText">EnCompassLIT</a> &nbsp;
		   </td>
	       </tr>
	       <tr>
		   <td valign="top">
			<input type="radio" name="db" onclick="refreshCategories()" value="2"></input> 
			<a class="SmBlackText">EnCompassPAT</a> &nbsp; 
		   </td>
		  <!-- end of search form -->  
	       </tr>

	       <tr>
		   <td valign="top" height="10">
			<img src="/engresources/images/s.gif" height="10"></img>
		   </td>
	       </tr>

	       <tr>
		   <td valign="top" >
		       <a class="SmBlackText"><b>Select Year of Publication</b></a>
		   </td>
	       </tr>

	       <tr>
		   <td valign="top">
			<a class="SmBlackText">
			<!-- Start of table for search form -->
			<xsl:value-of disable-output-escaping="yes" select="gui:createYearLb()"/>
			<!-- end of search form -->        
			</a> 
		   </td>
	       </tr>

	       <tr>
		   <td valign="top" height="10">
			<img src="/engresources/images/s.gif" height="10"></img>
		   </td>
	       </tr>

	       <tr>
		   <td valign="top">
			<a class="SmBlackText"><b>Select Category</b></a>
		   </td>
	       </tr>

	       <tr>
		   <td valign="top">
		   <!-- Start of table for search form -->
		   <xsl:value-of disable-output-escaping="yes" select="gui:createCategoryLb($CARTRIDGES,$QSTR)"/>
		   <!-- end of search form -->
		   </td>
	       </tr>

	       <tr>
		   <td valign="top" height="15">
			<img src="/engresources/images/s.gif" height="15"></img>
		   </td>
	       </tr>

	       <tr>
		   <td valign="top" align="right">
			<input type="image" name="display" value="Display" src="/engresources/images/display.gif" border="0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</input>
		   </td>
	       </tr>
	       <tr>
		   <td>
			<img src="/engresources/images/s.gif" width="20"></img>
		   </td>
	       </tr>
	       </table>
	       </td>
	       <td width="10" bgcolor="#C3C8D1">
	       	    <img src="/engresources/images/s.gif" width="10"></img>
	       </td>
	   </tr>
           </table>
           </form>
           </td>         
           <td>
           <table border="0" cellspacing="0" cellpadding="0" width="100%">
   	   <tr>
		<td colspan="3" height="5" bgcolor="#3173B5">
		     <img src="/engresources/images/s.gif" height="5"/>
		</td>
		<td width="20" align="right" >
		     <img src="/engresources/images/s.gif" width="20" height="5"/>
		</td>
	   </tr>
           <tr>
               <td width="20" bgcolor="#3173B5" align="left">
	    	   <img src="/engresources/images/s.gif" width="20"></img>
               </td>
	       <td align="left" bgcolor="#3173B5" height="20" colspan="2">       	
		   <a CLASS="SmWhiteText"><b>MOST RECENT BULLETINS</b></a>
	       </td>
	       <td width="20">
	   	    <img src="/engresources/images/s.gif" width="20"></img>
               </td>
	    </tr>
	    <tr>
		<td colspan="3" height="5" bgcolor="#3173B5">
		     <img src="/engresources/images/s.gif" height="5"/>
		</td>
		<td width="20" align="right" >
		     <img src="/engresources/images/s.gif" width="20" height="5"/>
		</td>
	   </tr>
	   <xsl:if test="boolean($HAS-LIT='true')">
           <tr>
                <td width="20" >
            	   <img src="/engresources/images/s.gif" width="20"></img>
                </td>

     	        <td valign="top" align="left" colspan="2">
      	        <!-- Left side table for bulletins display -->
	        <table border="0" cellspacing="0" cellpadding="0" width="100%">	       
	        <tr>
		    <td height="15" colspan="2">
		   	<img src="/engresources/images/s.gif" height="15"></img>
		    </td>
	        </tr>
	      
	        <tr>
		   <td valign="top" colspan="2">
		   	<a class="LgBlackText"><b>EnCompassLIT</b></a>
		   </td>	
	        </tr>
	        
	        <tr>		
		   <td valign="top" colspan="2" height="10" >
			<img src="/engresources/images/s.gif" height="10"></img>
		   </td>		  
	        </tr>
	       
	        <tr>		
		   <td valign="top" colspan="2" height="1" bgcolor="#3173B5">
			<img src="/engresources/images/s.gif" height="1"></img>
		   </td>		  
	        </tr>

	        <tr>
		   <td valign="top">
		   <table border="0" cellspacing="0" cellpadding="0">
		   <tr>
		       <td valign="top" colspan="2" height="10">
		    	     <img src="/engresources/images/s.gif" height="1"></img>
		       </td>
		   </tr>

		   <tr>
		       <td valign="top" align="left">
		       <table border="0" cellspacing="0" cellpadding="0">
		       <tr>
		           <td valign="top" width="200" align="left">
				<a class="SmBlackText"><b>Category</b></a>
		           </td>

		           <td width="5">
				<img src="/engresources/images/s.gif" width="5" height="20"></img>
		           </td>

		           <td valign="top" width="120" nowrap="true">
				<a class="SmBlackText"><b>Published Date</b></a>
		           </td>
		           <td width="5">
				<img src="/engresources/images/s.gif" width="5" height="20"></img>
		           </td>
		           <td valign="top" width="90">
				<a class="SmBlackText"><b>PDF</b></a>
		           </td>

		           <td width="50">
				<img src="/engresources/images/s.gif" width="50" height="20"></img>
		           </td>
		           <td valign="top" width="90">
		           	<img src="/engresources/images/s.gif" width="90" height="20"></img>
		           </td>
		       </tr>

		       <tr>
			   <td colspan="7">
			   	<img src="/engresources/encompass/images/s.gif" height="1"></img>
			   </td>
		       </tr>
		       <!-- First match -->
		       <xsl:apply-templates select="BULLETINS" mode="LT"/>

		       <tr>
			   <td height="6" colspan="7">
				<img src="/engresources/images/s.gif" height="6"></img>
			   </td>
		       </tr>
		       </table>
		       </td>
		       <td>
		      	   <img src="/engresources/images/s.gif"></img>
		       </td>
		   </tr>

		   <tr>
		       <td valign="top" colspan="2" height="1">
		      	   <img src="/engresources/images/s.gif" height="1"></img>
		       </td>
		   </tr>		 
		   </table>
		   </td>
		   <td width="5">
		   	<img src="/engresources/images/s.gif" width="5"></img>
		   </td>
	      </tr>
	      </table>
	      </td>
          </tr>
          </xsl:if>
          <tr>
             <td colspan="3" height="20">
        	<img src="/engresources/images/s.gif" height="20"></img>
             </td>
          </tr>
       
          <xsl:if test="boolean($HAS-PAT='true')">
          <tr>
             <td width="20">
	        <img src="/engresources/images/s.gif" width="20"></img>
             </td>
             <td valign="top" colspan="2">
        	<a class="LgBlackText"><b>EnCompassPAT</b></a>
             </td>
          </tr>
      
          <tr>
             <td width="20">
	         <img src="/engresources/images/s.gif" width="20"></img>
             </td>
             <td valign="top">
                 <table border="0" cellspacing="0" cellpadding="0" width="100%">
	         <tr>
		      <td valign="top" colspan="2" height="1">
			   <img src="/engresources/images/s.gif" height="1"></img>
		      </td>
		 </tr>
		 <tr>		
		      <td valign="top" colspan="2" height="10" >
			   <img src="/engresources/images/s.gif" height="1"></img>
		      </td>		  
		 </tr>

		 <tr>		
		      <td valign="top" colspan="2" height="1" bgcolor="#3173B5">
			   <img src="/engresources/images/s.gif" height="1"></img>
		      </td>		  
		 </tr>

		 <tr>		
		      <td valign="top" colspan="2" height="10" >
				<img src="/engresources/images/s.gif" height="10"></img>
		      </td>		  
		 </tr>

		 <tr>
		      <td valign="top">
			 <table border="0" cellspacing="0" cellpadding="0">
			 <tr>
			     <td valign="top" width="200" align="left" height="20">
				<a class="SmBlackText"><b>Category</b></a>
			     </td>

			     <td width="5">
				<img src="/engresources/images/s.gif" width="5" height="20"></img>
			     </td>

			     <td valign="top" width="120" nowrap="true">
				<a class="SmBlackText"><b>Published Date</b></a>
			     </td>

			     <td width="5">
				<img src="/engresources/images/s.gif" width="5" height="20"></img>
			     </td> 
			     <td valign="top" width="90">
				<a class="SmBlackText"><b>PDF</b></a>
			     </td>

			     <td width="50">
				<img src="/engresources/images/s.gif" width="50" height="20"></img>
			     </td>

			     <td valign="top" width="90">
				<a class="SmBlackText"><b>GIF</b></a>
			     </td>
			  </tr>
			  <tr>
			     <td colspan="7" height="1">
				<img src="/engresources/images/s.gif" height="1"></img>
			     </td>
			  </tr>
			  <!-- second match-->
			  <xsl:apply-templates select="BULLETINS" mode="PT"/>

			  <tr>
			      <td height="6" colspan="7">
				   <img src="/engresources/images/s.gif" height="6"></img>
			      </td>
			  </tr>
			  </table>
			  </td>

			  <td width="1">
				<img src="/engresources/images/s.gif" width="1"></img>
			  </td>
		     </tr>

		     <tr>
			  <td valign="top" colspan="2" height="1">
				<img src="/engresources/images/s.gif" height="1"></img>
			  </td>
		     </tr>
		     </table>
		     </td>

		     <td>
			   <img src="/engresources/images/s.gif" width="5"></img>
		     </td>
		  </tr>
          </xsl:if>     
          </table>  
          </td>
          <td>
	       	<img src="/engresources/images/s.gif"></img>
          </td>
      </tr>

      <tr>
          <td height="20" colspan="3">
     		<img src="/engresources/images/s.gif" height="20"></img>
          </td>
      </tr>
      </table>
   </div>
   </td>
   <td>
       <img src="/engresources/images/s.gif"></img>
   </td>
   <td width="1" bgcolor="#C3C8D1" >
           <img src="/engresources/images/s.gif"></img>
   </td>
   </tr>
   <tr>
        <td height="1" bgcolor="#C3C8D1" colspan="4">
	     <img src="/engresources/images/s.gif"></img>
   	</td>
   </tr>
</table>
</td>
</tr>
<tr>
<td>
<!-- Insert the Footer table -->
<xsl:apply-templates select="FOOTER">
	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
	<xsl:with-param name="SELECTED-DB" select="$SELECTED-DB"/>
	<xsl:with-param name="RESOURCE-PATH" select="$RESOURCE-PATH"/>
</xsl:apply-templates>
</td>
</tr>
</table>
</body>
</html>
 </xsl:template>
</xsl:stylesheet>

