<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY bull '<xsl:text disable-output-escaping="yes">&amp;#8226;</xsl:text>'>
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
    xmlns:ibfab="java:org.ei.data.insback.runtime.InspecArchiveAbstract"
    xmlns:ctd="java:org.ei.domain.ClassTitleDisplay"
    xmlns:rfx="java:org.ei.books.collections.ReferexCollection"
    xmlns:crlkup="java:org.ei.data.CRNLookup"
    xmlns:pid="java:org.ei.util.GetPIDDescription"
    xmlns:cvt="java:org.ei.data.CVSTermBuilder"
    xmlns:detail="java:org.ei.domain.TermDetail"
    exclude-result-prefixes="schar hlight java html ibfab xsl rfx ctd crlkup cvt detail"
>

    <xsl:output method="html" indent="no"/>
    <xsl:strip-space elements="html:* xsl:* AN" />
	<xsl:param name="s3figurl" />
    <xsl:template match="EI-DOCUMENT">	
        <table border="0" width="99%" cellspacing="0" cellpadding="0">      
        <xsl:apply-templates />
      </table>
    </xsl:template>

    <!-- sit on this -->
    <xsl:template match="NO_SO"/>

    <!-- These are fixes for GeoREF - Fields that are mutli-value which are not multi value in other databases -->
    <!-- we need to create special templates for them so they display correctly -->
    <xsl:template match="CA"> <!-- Category Field -->
      <span CLASS="MedBlackText">
        <xsl:apply-templates />
        <xsl:if test="not(position()=last())"> - </xsl:if>
      </span>
    </xsl:template>
    <xsl:template match="AN"> <!-- accessnumber -->
    	<xsl:variable name="DOCTYPE" ><xsl:value-of select="//DT"/></xsl:variable>
    	<tr>
	        <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
	        <td xsl:use-attribute-sets="r-align-label">
	        <xsl:if test="string(@label)">
	            <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
	        </xsl:if>
	        </td>
	        <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
	        
	        
	        <xsl:choose>
            <xsl:when test="$DOCTYPE='Article in Press'">
            <td valign="top" align="left">
            <table border='0' cellspacing="0" cellpadding="0">
            <tr>
               <td align='left' valign='top'><span style="vertical-align:top" CLASS="MedBlackText"><xsl:value-of select="."/></span><img src="/static/images/s.gif" width='10' border="0"/>          
               <span href="javascript:window.open('/engresources/aip_help.html','newwind','width=310,height=300,scrollbars=yes,resizable,statusbar=yes');void('');"><img src="/static/images/i3.gif" border="0" style="vertical-align:top" /></span></td>
            </tr>
            <tr>
               <td  colspan='2' align='left' valign='top' height='25'><img src="/static/images/btn_aip.gif" border="0" style="vertical-align:top" title="Articles not published yet, but available online"/><span CLASS="MedBlackText" style="vertical-align:text-top"><xsl:text> Article in Press</xsl:text></span></td>
            </tr>
            </table>
             </td>  
            </xsl:when>  
            <xsl:when test="$DOCTYPE='In Process'">
            <td valign="top" align="left">
            <table border='0' cellspacing="0" cellpadding="0">
            <tr>
               <td align='left' valign='top'><span style="vertical-align:top" CLASS="MedBlackText"><xsl:value-of select="."/></span><img src="/static/images/s.gif" width='10' border="0"/>          
               <span href="javascript:window.open('/engresources/aip_help.html','newwind','width=310,height=300,scrollbars=yes,resizable,statusbar=yes');void('');"><img src="/static/images/i3.gif" border="0" style="vertical-align:top" /></span></td>
            </tr>
            <tr>
               <td  colspan='2' align='left' valign='top' height='25'><img src="/static/images/btn_aip.gif" border="0" style="vertical-align:top" title="Records still in the process of being indexed, but available online." alt="Records still in the process of being indexed, but available online."/><span CLASS="MedBlackText" style="vertical-align:text-top"><xsl:text> In Process</xsl:text></span></td>
            </tr>
            </table>
             </td>  
            </xsl:when>  
			<xsl:otherwise>
			   <td height='25' valign="top"><span CLASS="MedBlackText"><xsl:value-of select="."/></span><br/></td>	
			</xsl:otherwise>
	        </xsl:choose>
	             
	               
      </tr>    
    </xsl:template>
    <xsl:template match="OA|P|RS|RPG"> <!-- Other Affiliation / Publisher / Sponsor / Research Program -->
      <span CLASS="MedBlackText">
        <xsl:apply-templates />
        <xsl:if test="not(position()=last())">; </xsl:if>
      </span>
    </xsl:template>
    <xsl:template match="L|A|D|R|GUR"> <!-- Language Field / Availability / Document type / Report Number / GeoRef URLs -->
      <span CLASS="MedBlackText">
        <xsl:apply-templates />
        <xsl:if test="not(position()=last())">, </xsl:if>
      </span>
    </xsl:template>
    <xsl:template match="MBN"> <!-- 'Multi' ISBN (parent key is MBNS -->
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())" disable-output-escaping="yes"/></xsl:with-param>
        <xsl:with-param name="FIELD">BN</xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())"><span CLASS="SmBlackText">, </span></xsl:if>
    </xsl:template>
    <!-- End of fixes for GeoREF -->


    <xsl:template match="CRDN">
      <tr>
        <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label">
        <xsl:if test="string(@label)">
            <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
        </xsl:if>
        </td>
        <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left">
        <span CLASS="MedBlackText">
          <div id="map" style="display:block;border:1px solid black;">
            <div id="map_canvas" style="float:left; width: 100%; height: 300px;"></div>
          </div>
        </span>
        <span class="SpLink" href="javascript:resetCenterAndZoom();">Reset view</span>
        </td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>


    <xsl:template match="LOC[@ID]">
      <span>
      <!-- <xsl:attribute name="href">javascript:toggleMarker('<xsl:value-of select="@ID"/>');</xsl:attribute> -->
      <xsl:value-of select="@ID"/> - <xsl:value-of disable-output-escaping="yes" select="."/></span><br/>
    </xsl:template>
    <xsl:template match="LOC">
      <span>
      <!-- <xsl:attribute name="href">javascript:toggleMarker('<xsl:value-of select="@ID"/>');</xsl:attribute> -->
      <xsl:value-of disable-output-escaping="yes" select="."/></span><br/>
    </xsl:template>

    <!-- top level elements with labels and nested value children -->
    <xsl:template match="MBNS|LA|SRCNT|SUM|MED|EDI|RPGM|DEG|UNV|HOLD|AUD|CAT|OAF|ANT|MPS|MPT|ILLUS|DGS|SC|AV|DT|MJSM|CRM|CLGM|PIDEPM|BKYS|AGS|AUS|EDS|IVS|CLS|FLS|LLS|CVS|RGIS|DISPS|CTS|OCVS|OCLS|NDI|CHI|AOI|AFS|EFS|PASM|PEXM|PIM|PAPIM">
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
            <xsl:if test="string(@label)">              
		<span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b></span>
            </xsl:if>
            </td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
              <span CLASS="MedBlackText"><xsl:apply-templates /></span>
            </td>
        </tr>
        <xsl:call-template name="SPACER"/>
    </xsl:template>

    <xsl:template match="PI|PAPI">
    <span CLASS="MedBlackText">
        <xsl:apply-templates />
        <xsl:if test="not(position()=last())"> - </xsl:if>
   </span>
    </xsl:template>

    <!-- top level elements for correspondence -->
    <xsl:template match="CAUS">

        <xsl:choose>
    	<xsl:when test="CAU/EMAIL">
		<tr>
		    <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
		    <td xsl:use-attribute-sets="r-align-label">
			<span CLASS="MedBlackText"><b>Corresponding author:</b> </span>
		    </td>
		    <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
		    <td valign="top" align="left">
			<span CLASS="MedBlackText"><xsl:apply-templates /></span>
		    </td>
		</tr>
        <xsl:call-template name="SPACER"/>
        </xsl:when>
        <xsl:otherwise>
	<xsl:if test="CAU">
		<tr>
		    <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
		    <td xsl:use-attribute-sets="r-align-label">
			<span CLASS="MedBlackText"><b>Corresponding author:</b> </span>
		    </td>
		    <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
		    <td valign="top" align="left">
			<span CLASS="MedBlackText"><xsl:apply-templates /></span>
		    </td>
		</tr>
		<xsl:call-template name="SPACER"/>
        </xsl:if>
        </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="CAU">
    	<xsl:if test="string(text())">
   		  <xsl:value-of select="normalize-space(text())" disable-output-escaping="yes"/>
   	  </xsl:if>

   	  <xsl:if test="EMAIL">
   	  (<span CLASS="SpLink">
		  <xsl:attribute name="href">mailto:<xsl:value-of select="EMAIL"/></xsl:attribute>
		  <xsl:attribute name="title">Corresponding author</xsl:attribute>
		  <xsl:value-of select="EMAIL"/>
	    </span>)
	   </xsl:if>
    </xsl:template>

    <xsl:template match="TI|TT">
      <tr>
        <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label"><span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b></span></td>
        <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left"><span CLASS="MedBlackText"><b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></b></span></td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>


    <xsl:template match="PAN|PAPD|PAPX|PANS|PANUS|PAPCO|PM|PM1|PA|VT">
      <tr>
        <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label"><span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b></span></td>
        <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left"><span CLASS="MedBlackText"><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></span></td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>

  <!-- top level LINKED node(s) with simple label/value children -->
    <xsl:template match="BN|SN|BN13|CN|CC|MH|MI|PNUM|E_ISSN">
      <tr>
        <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label"><span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b></span></td>
        <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left">
          <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
          </xsl:call-template>
        </td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>

  <!-- Controlled/Uncontrolled child node(s) within VALUE under FLS/CVS/AGS-->
    <xsl:template match="DG|MJS|BKY|FL|CV|AG|CT|OC|PS|RGI|CM|IC|GC|GD|CP|CE|LL">
      <xsl:if test="name()='LST'">
      <xsl:if test="position()=1">
      <img src="/static/images/separator.gif" border="0" width="6" height="6"/>
      <span CLASS="SmBlackText">&nbsp;&nbsp; </span>
      </xsl:if>
      </xsl:if>
      <xsl:variable name="FIELDNAME">
      	<xsl:choose>
      		<xsl:when test="(name(.)='GC')">CL</xsl:when>
      		<xsl:when test="(name(.)='MJS')">CVM</xsl:when>
      		<xsl:otherwise><xsl:value-of select="name(.)"/></xsl:otherwise>
      	</xsl:choose>
      </xsl:variable>

      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())" disable-output-escaping="yes"/></xsl:with-param>
        <xsl:with-param name="FIELD"><xsl:value-of select="$FIELDNAME"/></xsl:with-param>
      </xsl:call-template>

      <xsl:variable name="SEPARATOR">
         <xsl:if test="(position()mod 10) = 0">
          <xsl:text> </xsl:text>
         </xsl:if>
        <xsl:choose>
          <xsl:when test="name(.)='AGS'">;</xsl:when>
          <xsl:when test="name()='PA'"> </xsl:when>
          <xsl:when test="name(..)='ORGC'">-</xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="DASH_SPACER"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:if test="not(position()=last())"><span CLASS="SmBlackText"><xsl:value-of select="$SEPARATOR"/></span></xsl:if>

    </xsl:template>

    <xsl:template match="CVN|CVP|CVA|CVMN|CVMP|CVMA">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <span CLASS="SmBlackText">
      <xsl:value-of select="hlight:addMarkup(crlkup:getName(normalize-space(text())))" disable-output-escaping="yes"/></span>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>

    <xsl:template match="C_SUBJECT">
      <xsl:value-of select="normalize-space(text())"/>
      <xsl:variable name="SEPARATOR">
        <xsl:if test="(position()mod 10) = 0">
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:choose>
          <xsl:when test="name(.)='AGS'">;</xsl:when>
          <xsl:when test="name()='PA'"> </xsl:when>
          <xsl:when test="name(..)='ORGC'">-</xsl:when>
          <xsl:otherwise>
                <xsl:call-template name="DASH_SPACER"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:if test="not(position()=last())"><span CLASS="SmBlackText"><xsl:value-of select="$SEPARATOR"/></span></xsl:if>
    </xsl:template>


    <xsl:template match="PID|PUC|PEC">
      <xsl:variable name= "CCID">
        <xsl:value-of select="./CID" />
      </xsl:variable>

      <xsl:variable name="CLASS">
        <xsl:choose>
          <xsl:when test="(@level='A') and (@type='I')">SpBoldItalicLink</xsl:when>
          <xsl:when test="(@level='A') and (@type='N')">SpItalicLink</xsl:when>
          <xsl:when test="(@level='C') and (@type='I')">SpBoldLink</xsl:when>
          <xsl:otherwise>SpLink</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="IPC-DESCRIPTION">
          <xsl:value-of select="pid:getDescription(./CID, ./CTI)" />
      </xsl:variable>
 	  <!--
      	<xsl:variable name="FIELDNAME">
      	<xsl:choose>
      		<xsl:when test="not(./DOC/DB/DBMASK='2048')">
      			<xsl:value-of select="name(.)"/>
      		</xsl:when>
      		<xsl:otherwise>IP</xsl:otherwise>
      	</xsl:choose>
      </xsl:variable>
   	  -->


      <tr>
        <td valign="top">
          <xsl:call-template name="LINK">
           <xsl:with-param name="TERM"><xsl:value-of select="$CCID"/></xsl:with-param>
           <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
           <xsl:with-param name="CLASS"><xsl:value-of select="$CLASS"/></xsl:with-param>
          </xsl:call-template>
        </td>
        <td valign="top">
        <xsl:choose>
      		<xsl:when test="not(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2')">
			  <span CLASS="MedBlackText"><xsl:value-of select="ctd:getDisplayTitle2(hlight:addMarkup(./CTI))" disable-output-escaping="yes"/></span>
		</xsl:when>
      		<xsl:otherwise>
        		<span CLASS="MedBlackText"><xsl:value-of select="$IPC-DESCRIPTION" disable-output-escaping="yes"/></span>
        	</xsl:otherwise>
         </xsl:choose>
         </td>
      </tr>
    </xsl:template>

     <xsl:template match="FS">
            <xsl:text> </xsl:text>
    <!--      <xsl:value-of select="normalize-space(text())" /> -->

            <span CLASS="MedBlackText"><xsl:value-of select="normalize-space(text())"/></span>
				<!--  <img src="/static/images/plus.gif" border="0"/>  -->
            <xsl:text> </xsl:text>

      <!--
        <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
        </xsl:call-template>
      -->

        <xsl:if test="not(position()=last())">
            <xsl:call-template name="DASH_SPACER"/>
        </xsl:if>
        <xsl:if test="position()=last()">
            <xsl:text> </xsl:text>
        </xsl:if>
     </xsl:template>

    <!-- SPECIAL child node(s) within VALUE under AUS, EDS, IVS -->
    <xsl:template match="AU|ED|IV">
        <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
        <!-- If the name does not contain the text Anon -->
        <xsl:if test="boolean(not(contains($NAME,'Anon')))">
            <xsl:call-template name="LINK">
                <xsl:with-param name="TERM"><xsl:value-of select="$NAME"/></xsl:with-param>
                <xsl:with-param name="FIELD">AU</xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <!-- If the name contains Anon text -->
        <xsl:if test="boolean(contains($NAME,'Anon'))"><span CLASS="SmBlackText"><xsl:value-of select="$NAME"/></span></xsl:if>
        <xsl:if test="(@label)">
            <span CLASS="MedBlackText"><Sup><xsl:value-of select="@label"/></Sup><xsl:text> </xsl:text></span>
        </xsl:if>

        <xsl:if test="name()='IV'">
            <xsl:if test="CO">
                <xsl:text> (</xsl:text>
                <xsl:value-of select="CO" />
                <xsl:text>) </xsl:text>
            </xsl:if>
        </xsl:if>

        <xsl:if test="not(./AFS)">
        <xsl:if test="(@id)">
        <xsl:if test="not(@id='0')">
       		<span CLASS="MedBlackText"><Sup><xsl:value-of select="@id"/></Sup><xsl:text> </xsl:text></span>
        </xsl:if>
        </xsl:if>
        </xsl:if>

    <xsl:if test="./AFS">
      <xsl:if test="not(./AFS/AFID='0')">
      <span CLASS="SmBlackText">
      	<Sup>
      		<xsl:apply-templates select="./AFS/AFID"/>
      	</Sup>
      	<xsl:text> </xsl:text></span>
      </xsl:if>

    </xsl:if>
    
    <xsl:if test="./EMAIL">
         <xsl:variable name="emailaddress"><xsl:value-of select="./EMAIL"/></xsl:variable>
         <span CLASS="SmBlackText">
              <Sup>
    		  <span>
    		    <xsl:attribute name="href">mailto:<xsl:value-of select="./EMAIL" /></xsl:attribute>
    		    <img src="/static/images/emailfolder.gif" border="0" padding="0" valigh="bottom"/>
    		  </span>
              </Sup>
         </span>
    </xsl:if>

    <!--    <xsl:if test="not(name()='IV')">    -->
            <xsl:if test="not(position()=last())"><span CLASS="SmBlackText">;</span><xsl:text> </xsl:text></xsl:if>
    <!--
        </xsl:if>
    -->
       

        <xsl:if test="position()=last()"><xsl:text> </xsl:text></xsl:if>
    </xsl:template>


	<xsl:template match="AFID">
		<xsl:value-of select="."/>
		<xsl:if test="not(position()=last())">,<xsl:text> </xsl:text></xsl:if>
	</xsl:template>





    <xsl:template match="PAS">
        <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
        <!-- If the name does not contain the text Anon -->
        <xsl:call-template name="LINK">
           <xsl:with-param name="TERM"><xsl:value-of select="$NAME" /></xsl:with-param>
           <xsl:with-param name="FIELD">AF</xsl:with-param>
        </xsl:call-template>
        <!-- If the name contains Anon text -->
        <xsl:if test="(@label)">
            <span CLASS="MedBlackText"><Sup><xsl:value-of select="@label"/></Sup><xsl:text> </xsl:text></span>
        </xsl:if>
        <xsl:if test="not(position()=last())">; </xsl:if>
        <!-- <xsl:text> </xsl:text> -->
    </xsl:template>

    <xsl:template match="PEX">
        <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
        <!-- If the name does not contain the text Anon -->
        <xsl:call-template name="LINK">
           <xsl:with-param name="TERM"><xsl:value-of select="$NAME"/></xsl:with-param>
           <xsl:with-param name="FIELD">PEX</xsl:with-param>
        </xsl:call-template>
        <!-- If the name contains Anon text -->
        <xsl:if test="(@label)">
            <span CLASS="MedBlackText"><Sup><xsl:value-of select="@label"/></Sup><xsl:text> </xsl:text></span>
        </xsl:if>

        <xsl:if test="not(position()=last())"> <span CLASS="SmBlackText">; </span><xsl:text> </xsl:text></xsl:if>
    </xsl:template>
  <!-- CLassification child node(s) within VALUE (under CLS) -->
    <xsl:template match="CL">

      <xsl:apply-templates />
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>

  <!-- NON-HIGHLIGHTED child node(s) within VALUE -->
      <xsl:template match="DISP|ND|CI|AI|TTI|TR">
      <span CLASS="MedBlackText"><xsl:value-of select="normalize-space(text())"/></span>
        <xsl:if test="not(position()=last())">; </xsl:if>
     </xsl:template>

     <xsl:template match="TRS">
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
                <xsl:if test="string(@label)">
                    <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
                </xsl:if>
            </td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
                <span CLASS="MedBlackText">
  				<xsl:choose>
  					<xsl:when test="TR/TTI"> 			
                		<xsl:apply-templates select="TR/TTI"/>
               		</xsl:when>
                	<xsl:otherwise>	 				
                		<xsl:apply-templates select="TR"/>                
                	</xsl:otherwise>
            	</xsl:choose>               
                </span>
            </td>
        </tr>
        <xsl:call-template name="SPACER"/>
    </xsl:template>

    <!-- NON-HIGHLIGHTED child node(s) within VALUE -->
    <xsl:template match="AF|EF">
            <table>
            <tr>
            <td valign="top">
            <xsl:if test="(@id)">
            <xsl:if test="not(@id='0')">
        		<span CLASS="MedBlackText">
        		<Sup><xsl:value-of select="@id"/></Sup><xsl:text> </xsl:text></span>
      		</xsl:if>
      		</xsl:if>
      		</td>
      		<td>
        	<span CLASS="MedBlackText"><xsl:value-of select="hlight:addMarkup(normalize-space(text()))" disable-output-escaping="yes"/></span>
         <!--   <span CLASS="MedBlackText"><xsl:value-of select="normalize-space(text())"/></span> -->
            <xsl:if test="not(position()=last())"><span CLASS="SmBlackText"><br/></span><xsl:text> </xsl:text></xsl:if>
            </td>
            </tr>
            </table>
    </xsl:template>

  <!-- Classification code 'page' -->
    <xsl:template match="CPAGE">
        <xsl:apply-templates />
    </xsl:template>

  <!-- ???????  -->
    <xsl:template match="CID|OPT">


        <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD">CL</xsl:with-param>
        </xsl:call-template>

    </xsl:template>

  <!-- ???????  -->
    <xsl:template match="CTI">
    <span CLASS="MedBlackText"><xsl:text> </xsl:text><xsl:value-of select="normalize-space(text())"  disable-output-escaping="yes" /></span>
    </xsl:template>
   <xsl:template match="ABS">
     <span CLASS="MedBlackText"><xsl:value-of select="ibfab:getHTML(hlight:addMarkupCheckTagging(.))" disable-output-escaping="yes"/></span>
   </xsl:template>

   <xsl:template match="ABS2">
     <span CLASS="MedBlackText"> <xsl:value-of select="ibfab:getHTML(hlight:addMarkupCheckTagging(.))" disable-output-escaping="yes"/></span>
   </xsl:template>

  <!-- START OF IMAGES from INSPEC BACKFILE -->
  <xsl:template match="IMGGRP">
    <p/>
    <table border="0" cellspacing="0" cellpadding="0">
      <xsl:apply-templates />
    </table>
   </xsl:template>
   <xsl:template match="IROW">
    <tr>
    <xsl:apply-templates />
    </tr>
   </xsl:template>

  <xsl:template match="IMAGE">
    <xsl:variable name="cap" select= "text()" />
    <xsl:variable name="figureUrl"><xsl:value-of select="$s3figurl"/><xsl:value-of select="@img"/></xsl:variable>
    <td valign="top" align="middle"><span href="javascript:window.open('{$figureUrl}','newwind','width=650,height=600,scrollbars=yes,resizable,statusbar=yes');void('');"><img src="{$figureUrl}" alt="{$cap}" border="1" width="100" height="100"/></span>
    <br/><span CLASS="SmBlackText"><xsl:value-of select="$cap" disable-output-escaping="yes"/></span></td><td valign="top" width="15"></td>
  </xsl:template>

   <xsl:template match="SOURCE">
       <xsl:if test="@NO"><sup><xsl:value-of select="@NO"/><xsl:text>.</xsl:text></sup></xsl:if><xsl:apply-templates /><xsl:if test="boolean(contains(@DOI,'10'))">, DOI:</xsl:if><span CLASS="SpLink" href="javascript:window.open('http://dx.doi.org/{@DOI}','newwind','width=650,height=600,scrollbars=yes,resizable,statusbar=yes');void('');"><xsl:value-of select="@DOI"/></span><br/>
    </xsl:template>


    <xsl:template match="ORGC">
      <xsl:apply-templates />
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>

 

  <!-- default rule when displaying text values in this DETAILED stylesheet ONLY - markup/highlight matched terms -->
    <xsl:template match="text()">
    <xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="DOC">
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
                <span CLASS="MedBlackText"><b>Database:</b> </span>
            </td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left"><span CLASS="MedBlackText"><xsl:value-of select="DB/DBNAME" disable-output-escaping="yes"/></span></td>
        </tr>
        <xsl:call-template name="SPACER"/>
    </xsl:template>


    <!--
    <xsl:template match="KC">
        <tr>
        <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label"><span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b></span></td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
                <xsl:call-template name="LINK">
                <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
                <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
        <xsl:call-template name="SPACER"/>
    </xsl:template>
    -->


    <xsl:template match="KC">
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
                <xsl:if test="string(@label)">
                    <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
                </xsl:if>
            </td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
                <span CLASS="MedBlackText"><xsl:value-of select="." /> - <xsl:value-of select="../KD" /></span>
            </td>
        </tr>
        <xsl:call-template name="SPACER"/>
  </xsl:template>
  <!-- DEFAULT RULE - simple display label/value fields-->
    <xsl:template match="*">
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
                <xsl:if test="string(@label)">
                    <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
                </xsl:if>
                <!-- Debugging to see what elements are triggering this template/rule
                [<xsl:value-of select="name()"/>] -->
            </td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
                <span CLASS="MedBlackText"><xsl:apply-templates/></span>
            </td>
        </tr>
        <xsl:call-template name="SPACER"/>
  </xsl:template>

    <!-- DEFAULT RULE - simple display label/value fields-->

    <!-- <xsl:template match="PIDM|PUCM|PECM|PIDM8|PIDEPM"> -->
    <xsl:template match="PIDM|PUCM|PECM|PIDM8">
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
                <xsl:if test="string(@label)">
                    <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
                </xsl:if>
            </td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
                <table border="0" >
                    <xsl:apply-templates />
                </table>
            </td>
        </tr>
        <!--<xsl:call-template name="SPACER"/>-->
  </xsl:template>


<!-- ENCompass fileds -->

 	<xsl:variable name="SEARCHID">
		<xsl:value-of select="//SEARCH-ID"/>
 	</xsl:variable>
 	<xsl:variable name="ISTAG">
 		<xsl:value-of select="/PAGE/TAG-BUBBLE/CUSTID"/>
 	</xsl:variable>
 	<xsl:variable name="SEARCH-CONTEXT">
 		<xsl:value-of select="/PAGE/SEARCH-CONTEXT"/>
 	</xsl:variable>


  	<xsl:template match="LTH">

  	<xsl:variable name="DOCID">
      <xsl:value-of select="ancestor::EI-DOCUMENT/DOC/DOC-ID"/>
    </xsl:variable>
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
            <span CLASS="MedBlackText"><b>Linked terms: </b> </span>
            </td>
            <td valign="top" width="10">
            <img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
            	<div class="longltdiv" id="longltdiv{$DOCID}">
            	<input type="hidden" id="istag{$DOCID}" name="istag{$DOCID}"  value='{$SEARCH-CONTEXT}' disable-output-escaping = "yes"/>
                	<input type="hidden" name="longltfield{$DOCID}" id="longltfield{$DOCID}" disable-output-escaping = "yes"/>
                	<input type="hidden" name="schid{$DOCID}" id="schid{$DOCID}"  value='{$SEARCHID}' disable-output-escaping = "yes"/>
   					<span href="" class="encompassOpenClose splink" isopen='false' termtype='longlt' data='{$DOCID}' docid='{$DOCID}' title="Display Linked terms">
   					<span class="open" border="0">Open all linked terms view</span><span class="close" border="0" style="display:none">Close all linked terms view</span>&nbsp;&nbsp;<span class="plusminus">+</span><span class="plusminus" style="display:none">-</span>
   					</span>

   					<table style="margin:0px; padding:0px; border:0px black solid; width:100%;font-size:11px" id="longlt_table{$DOCID}">
						<tbody id="longlt_table_body{$DOCID}">
						</tbody>
					</table>
				</div>
     		</td>
        </tr>
        <xsl:call-template name="SPACER"/>
    </xsl:template>

    <xsl:template match="LSTM">

    <xsl:variable name="DOCID">
      <xsl:value-of select="ancestor::EI-DOCUMENT/DOC/DOC-ID"/>
    </xsl:variable>
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
			<span CLASS="MedBlackText"><b> Linked Terms: </b> </span>
            </td>

            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
            	<xsl:variable name="LST">
            		<xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
            	</xsl:variable>
            	<div class="lstdiv" id="lstdiv{$DOCID}">
                	<input type="hidden" name="lstfield{$DOCID}" id="lstfield{$DOCID}" disable-output-escaping = "yes"/>
   					<span href="" class="encompassOpenClose splink" isopen='false' termtype='lst' data='{$LST}' docid='{$DOCID}' title="Display Linked terms">
   					<span class="open" border="0">Open all linked terms view</span><span class="close" border="0" style="display:none">Close all linked terms view</span>&nbsp;&nbsp;<span class="plusminus">+</span><span class="plusminus" style="display:none">-</span>
   					</span>

   					<table style="margin:0px; padding:0px; border:0px black solid; width:100%;font-size:11px" id="lst_table{$DOCID}">
						<tbody id="lst_table_body{$DOCID}">
						</tbody>
					</table>
				</div>
     		</td>
        </tr>
        <xsl:call-template name="SPACER"/>
    </xsl:template>

    <xsl:template match="MLT">

    <xsl:variable name="DOCID">
      <xsl:value-of select="ancestor::EI-DOCUMENT/DOC/DOC-ID"/>
    </xsl:variable>
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
            <span CLASS="MedBlackText"><b> Manually linked terms: </b> </span>
            </td>

            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <td valign="top" align="left">
            	<xsl:variable name="ML">
            		<xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
            	</xsl:variable>
            	<div class="mltdiv" id="mltdiv{$DOCID}">
                	<input type="hidden" name="mltfield{$DOCID}" id="mltfield{$DOCID}" disable-output-escaping = "yes"/>
   					<span href="" class="encompassOpenClose splink" isopen='false' id="mltOpenClose" termtype='mlt' data='{$ML}' docid='{$DOCID}' title="Display Manually linked terms">
   					<span class="open" border="0">Open manually linked terms</span><span class="close" border="0" style="display:none">Close manually linked terms</span>&nbsp;&nbsp;<span class="plusminus">+</span><span class="plusminus" style="display:none">-</span>
   					</span>

   					<table style="margin:0px; padding:0px; border:0px black solid; width:100%;font-size:11px" id="mlt_table{$DOCID}">
						<tbody id="mlt_table_body{$DOCID}">
						</tbody>
					</table>
				</div>
     		</td>
        </tr>
        <xsl:call-template name="SPACER"/>
    </xsl:template>

    <xsl:template match="ATM">

    <xsl:variable name="DOCID">
      <xsl:value-of select="ancestor::EI-DOCUMENT/DOC/DOC-ID"/>
    </xsl:variable>
        <tr>
            <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
            <td xsl:use-attribute-sets="r-align-label">
            <xsl:if test="string(@label)">
                <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
            </xsl:if>
            </td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            <xsl:message><xsl:value-of select = '$SEARCH-CONTEXT'/></xsl:message>
            <xsl:variable name="AT">
            	<xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
            </xsl:variable>

            <td valign="top" align="left">
	      	<div class="atmdiv" id="atmdiv{$DOCID}">
          	<input type="hidden" name="atmfield{$DOCID}" id="atmfield{$DOCID}" disable-output-escaping = "yes"/>
			<span href="" class="encompassOpenClose splink" isopen='false' id="atmOpenClose" termtype='atm' data='{$AT}' docid='{$DOCID}' title="Display Indexing template">
				<span class="open" border="0">Open all templates view</span><span class="close" border="0" style="display:none">Close all templates view</span>&nbsp;&nbsp;<span class="plusminus">+</span><span class="plusminus" style="display:none">-</span>
			</span>
			<table style="margin:0px; padding:0px; border:0px black solid; width:100%;font-size:11px" id="atm_table{$DOCID}">
				<tbody id="atm_table_body{$DOCID}">
				</tbody>
			</table>
		 	</div>
     		</td>
           <!-- <span CLASS="MedBlackText"><xsl:value-of select="detail:getOneDetail('ATM',$AT)" disable-output-escaping="yes"/></span> -->
        </tr>
       <xsl:call-template name="SPACER"/>
    </xsl:template>

<!-- ENCompass fileds -->

   <xsl:template match="PIDEP">
      <xsl:variable name= "CCID">
        <xsl:value-of select="./CID" />
      </xsl:variable>

      <xsl:variable name="CLASS">
        <xsl:choose>
          <xsl:when test="(@level='A') and (@type='I')">SpBoldItalicLink</xsl:when>
          <xsl:when test="(@level='A') and (@type='N')">SpItalicLink</xsl:when>
          <xsl:when test="(@level='C') and (@type='I')">SpBoldLink</xsl:when>
          <xsl:otherwise>SpLink</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>


          <xsl:call-template name="LINK">
           <xsl:with-param name="TERM"><xsl:value-of select="$CCID"/></xsl:with-param>
           <xsl:with-param name="FIELD">PID</xsl:with-param>
           <xsl:with-param name="CLASS"><xsl:value-of select="$CLASS"/></xsl:with-param>
          <!--    <xsl:with-param name="ONMOUSEOVER">this.T_WIDTH=450;return escape('<xsl:value-of select="ctd:getDisplayTitle(hlight:addMarkup(./CTI))"/>')</xsl:with-param>            -->
          </xsl:call-template>
          <span CLASS="MedBlackText"><xsl:value-of select="normalize-space(text())"/></span>
         <!--   <img src="/static/images/plus.gif" border="0"/>  -->
            <xsl:text> </xsl:text>
          <xsl:if test="position()!=last()">
             <xsl:call-template name="DASH_SPACER"/>
          </xsl:if>

        <!--
        <td valign="top">
          <span CLASS="MedBlackText"><xsl:value-of select="ctd:getDisplayTitle2(hlight:addMarkup(./CTI))" disable-output-escaping="yes"/></span>
        </td>
        -->

    </xsl:template>


   <xsl:template match="CLG">

           <xsl:call-template name="LINK">
           <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
           <xsl:with-param name="FIELD">CL</xsl:with-param>

       </xsl:call-template>
       <!--
            <span CLASS="MedBlackText"><xsl:value-of select="normalize-space(text())"/></span>
           <img src="/static/images/plus.gif" border="0"/>
        -->
           <xsl:text> </xsl:text>
           <xsl:if test="position()!=last()">
           <xsl:call-template name="DASH_SPACER"/>
           </xsl:if>

        <!--
        <td valign="top">
          <span CLASS="MedBlackText"><xsl:value-of select="ctd:getDisplayTitle2(hlight:addMarkup(./CTI))" disable-output-escaping="yes"/></span>
        </td>
        -->

  </xsl:template>

    <xsl:template match="CR">

            <xsl:call-template name="LINK">
           <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
           <xsl:with-param name="FIELD">CR</xsl:with-param>

       </xsl:call-template>
       <!--
            <span CLASS="MedBlackText"><xsl:value-of select="normalize-space(text())"/></span>
           <img src="/static/images/plus.gif" border="0"/>
        -->
           <xsl:text> </xsl:text><span CLASS="MedBlackText">
           <xsl:value-of select="crlkup:getName(normalize-space(text()))" disable-output-escaping="yes"/></span>
           <xsl:if test="position()!=last()">
           <xsl:call-template name="DASH_SPACER"/>
           </xsl:if>

  </xsl:template>


    <xsl:template match="FSM|DSM">
      <tr>
        <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label">
          <xsl:if test="string(@label)">
            <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
          </xsl:if>
        </td>
        <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left">
        <span CLASS="MedBlackText"><xsl:apply-templates /></span>
        </td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>

    <xsl:template match="DS">
        <span CLASS="MedBlackText"><xsl:text> </xsl:text><xsl:value-of select="normalize-space(text())"/></span>
        <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
      <xsl:if test="position()=last()"><xsl:text> </xsl:text></xsl:if>
    </xsl:template>



  <!-- START OF UTILITIES named template(s), attribute-set(s) -->
    <xsl:template name="DASH_SPACER"><span CLASS="SmBlackText">&#160; - &#160;</span></xsl:template>

  <!-- NAMED TEMPLATE, aka method -->
    <xsl:template name="SPACER">
      <tr><td valign="top" colspan="4" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
      <xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:template>

  <!-- NAMED TEMPLATE, aka method -->
    <xsl:template name="LINK">
      <xsl:param name="TERM"/>
      <xsl:param name="FIELD"/>
      <xsl:param name="CLASS"/>

      <xsl:variable name="ENCODED-SEARCH-TERM">
        <xsl:value-of select="java:encode(schar:preprocess(hlight:removeMarkup($TERM)))" disable-output-escaping="yes"/>
      </xsl:variable>

      <xsl:variable name="TAG-RESULT-DBMASK"><xsl:value-of select="ancestor::EI-DOCUMENT/DOC/DB/DBMASK"/></xsl:variable>

      <xsl:variable name="THIS-DOCUMENT-DB">
        <xsl:choose>
          <xsl:when test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK)='32'">1</xsl:when>
	        <xsl:when test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK)='4096'">2</xsl:when>
          <xsl:otherwise><xsl:value-of select="ancestor::EI-DOCUMENT/DOC/DB/DBMASK"/></xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="YEARRANGE">
        <xsl:choose>
          <xsl:when test="string(//SESSION-DATA/LASTFOURUPDATES)"><xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES" /></xsl:when>
          <xsl:otherwise>yearrange</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

        <xsl:variable name="START-YEAR-PARAM">
          <xsl:choose>
            <xsl:when test="($FIELD='AU')"></xsl:when>
<!--      <xsl:when test="string(//SESSION-DATA/START-YEAR)">&amp;startYear=<xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:when> -->
            <xsl:otherwise></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="END-YEAR-PARAM">
          <xsl:choose>
            <xsl:when test="($FIELD='AU')"></xsl:when>
<!--      <xsl:when test="string(//SESSION-DATA/END-YEAR)">&amp;endYear=<xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:when> -->
            <xsl:otherwise></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <!-- the variable $SEARCH-TYPE can be empty so string($SEARCH-TYPE) can be FALSE -->
        <!-- when there is no search in the XML output -->
        <!-- -->
        <!-- //SEARCH-TYPE is defined in a SEARCHRESULT as the type of the current search -->
        <!-- //SEARCH-TYPE is defined in a SELECTEDSET by the type of the last search  -->
        <!-- //SEARCH-TYPE is defined in a FOLDER by the type of the last search  -->
        <!--  if there is no search before viewing a folder, the XML <SEARCH-TYPE>NONE</SEARCH-TYPE> -->
        <!--  is output by viewSavedRecordsOfFolder.jsp when recentXMLQuery==null -->
        <xsl:variable name="SEARCH-TYPE-TAG">
		      <xsl:value-of select="//SEARCH-TYPE-TAG"/>
        </xsl:variable>

        <xsl:variable name="SEARCH-TYPE">
          <xsl:choose>
      	    <xsl:when test="string($SEARCH-TYPE-TAG)">
		          <xsl:value-of select="//SEARCH-TYPE-TAG"/>
	          </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="//SEARCH-TYPE"/>
            </xsl:otherwise>
	        </xsl:choose>
        </xsl:variable>

        <xsl:variable name="DATABASE">
          <xsl:choose>
          <xsl:when test="(/PAGE/SEARCH-CONTEXT='tag')">
	  	<xsl:value-of select="$THIS-DOCUMENT-DB"/>
	  </xsl:when>

            <!-- View Saved Records -->
            <xsl:when test="string(/PAGE/FOLDER-ID)">
              <xsl:value-of select="$THIS-DOCUMENT-DB"/>
            </xsl:when>
            <!-- View Selected Set -->
            <xsl:when test="contains(/PAGE/CID,'SelectedSet')">
              <xsl:choose>
                <!-- View Selected Set - Multiple Queries -->
                <xsl:when test="(/PAGE/HAS-MULTIPLE-QUERYS='true')">
                  <xsl:value-of select="$THIS-DOCUMENT-DB"/>
                </xsl:when>
                <!-- View Selected Set - Single Query -->
                <xsl:otherwise>
                  <xsl:value-of select="/PAGE/DBMASK"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <!-- Search Results Records -->
            <xsl:otherwise>
              <xsl:value-of select="/PAGE/DBMASK"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <!-- Default Sort Options for these links -->
        <!-- make changes here(like field or adding direction) -->
        <xsl:variable name="DEFAULT-LINK-SORT">
          <xsl:choose>
            <xsl:when test="(($SEARCH-TYPE='Book') or ($DATABASE='131072')) and ($FIELD='BKY')">&amp;sortdir=dw&amp;sort=relevance</xsl:when>
            <xsl:when test="($SEARCH-TYPE='Book') or ($DATABASE='131072')"><xsl:value-of select="$BOOK-SORT-PARAMS"/></xsl:when>
            <xsl:otherwise>&amp;sort=yr</xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <!-- &amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/> is appended to HREF attribute -->

        <span CLASS="SpLink">
        <xsl:if test="string($CLASS)">
          <xsl:attribute name="CLASS"><xsl:value-of select="$CLASS"/></xsl:attribute>
        </xsl:if>

        <xsl:variable name="INDEX-FIELD">
          <xsl:choose>
            <xsl:when test="($FIELD='BN13')">BN</xsl:when>
            <xsl:when test="($FIELD='PNUM')">CT</xsl:when>
            <xsl:when test="($FIELD='E_ISSN')">SN</xsl:when>
            <xsl:when test="($FIELD='BKY')">KY</xsl:when>
            <xsl:otherwise><xsl:value-of select="$FIELD"/></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:if test="not(/PAGE/LINK ='false') and not(/SECTION-DELIM/LINK ='false')">
          <xsl:choose>
            <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">
              <xsl:variable name="ENCODED-SEARCH-SECTION"><xsl:value-of select="java:encode(' WN ')"/><xsl:value-of select="$INDEX-FIELD"/></xsl:variable>
              <xsl:attribute name="href">/search/results/expert.url?CID=expertSearchCitationFormat&amp;searchWord1=({<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}<xsl:value-of select="$ENCODED-SEARCH-SECTION"/>)&amp;database=<xsl:value-of select="$DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:when>
            <xsl:when test="($SEARCH-TYPE='Book') or ($DATABASE='131072')">
              <xsl:attribute name="href">/search/results/quick.url?CID=quickSearchCitationFormat&amp;searchtype=Book&amp;searchWord1={<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}&amp;section1=<xsl:value-of select="$INDEX-FIELD"/>&amp;database=<xsl:value-of select="$DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:choose>
                <xsl:when test="($FIELD='PNUM')">
                  <xsl:attribute name="href">/search/results/quick.url?CID=quickSearchCitationFormat&amp;searchWord1=<xsl:value-of select="$ENCODED-SEARCH-TERM"/>&amp;section1=<xsl:value-of select="$INDEX-FIELD"/>&amp;database=<xsl:value-of select="$DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="($FIELD='E_ISSN')">
			           <xsl:attribute name="href">/search/results/quick.url?CID=quickSearchCitationFormat&amp;searchWord1=<xsl:value-of select="$ENCODED-SEARCH-TERM"/>&amp;section1=<xsl:value-of select="$INDEX-FIELD"/>&amp;database=<xsl:value-of select="$DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="($FIELD='CL')">
                  <xsl:attribute name="href">/search/results/quick.url?CID=quickSearchCitationFormat&amp;searchWord1=<xsl:value-of select="$ENCODED-SEARCH-TERM"/>&amp;section1=<xsl:value-of select="$INDEX-FIELD"/>&amp;database=<xsl:value-of select="$DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:attribute name="href">/search/results/quick.url?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}&amp;section1=<xsl:value-of select="$INDEX-FIELD"/>&amp;database=<xsl:value-of select="$DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:value-of select="hlight:addMarkup($TERM)" disable-output-escaping="yes"/></span>
        <!--
        <xsl:if test="($FIELD='KC')">
            <span CLASS="MedBlackText"><xsl:text>&#xD;&#xA;</xsl:text>
            <xsl:value-of select="//KD" />
            </span>
        </xsl:if>
        -->
    </xsl:template>

    <xsl:template match="THMBS">
        <tr>
          <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
          <td xsl:use-attribute-sets="r-align-label">
            <xsl:if test="string(@label)">
              <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
            </xsl:if>
          </td>
          <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
          <td valign="top" align="left">
            <xsl:apply-templates/>
          </td>
        </tr>
        <xsl:call-template name="SPACER"/>
    </xsl:template>

    <!-- do not display these BOOK elements> -->
    <xsl:template match="THMB|CVR|BSPG">
    </xsl:template>

    <xsl:variable name="BOOK-SORT-PARAMS">&amp;sortdir=up&amp;sort=stsort</xsl:variable>

    <xsl:template match="COL">
      <tr>
        <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
        <td xsl:use-attribute-sets="r-align-label"><span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b></span></td>
        <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
        <td valign="top" align="left">
        <!-- DO not use LINK template - since we have a special search embedded on the COL link for Boooks -->
            <span class="SpLink">
            <xsl:attribute name="title"><xsl:value-of select="text()"/></xsl:attribute>
            <xsl:attribute name="href">/search/results/quick.url?CID=quickSearchCitationFormat&amp;searchtype=Book&amp;col=<xsl:value-of select="rfx:translateCollection(text())"/>&amp;database=131072&amp;yearselect=yearrange<xsl:value-of select="$BOOK-SORT-PARAMS"/></xsl:attribute>
            <xsl:value-of select="text()"/></span>
        </td>
      </tr>
      <xsl:call-template name="SPACER"/>
    </xsl:template>


    <!-- BOOK PAGE RULE -->
    <xsl:template match="BPP">
        <xsl:if test="text()!= '0'">
          <tr>
              <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
              <td xsl:use-attribute-sets="r-align-label">
                  <xsl:if test="string(@label)">
                      <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
                  </xsl:if>
              </td>
              <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
              <td valign="top" align="left">
                  <span class="MedBlackText"><xsl:value-of select="."/></span>
              </td>
          </tr>
          <xsl:call-template name="SPACER"/>
        </xsl:if>
  </xsl:template>
  
   <!-- COPYRIGHT NOTICE - 'Utitlity'-type element  -->
    <!-- top level node(s) with label/value children -->
      <xsl:template match="CPR">
          <xsl:call-template name="SPACER"/>
          <xsl:call-template name="SPACER"/>
          <tr>
              <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
              <td valign="top" ><img src="/static/images/s.gif" border="0"/></td>
              <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
              <td valign="top" align="left"><span CLASS="MedBlackText"><xsl:value-of select="." disable-output-escaping="yes"/></span></td>
          </tr>
          <xsl:call-template name="SPACER"/>
    </xsl:template>

    <!-- do not display these elements> -->
    <xsl:template match="TCO|PVD|FT|CPRT|KD|ID|NPRCT|RCT|CCT|VIEWS|PII">
    </xsl:template>

    <!-- attribute-set -->
    <xsl:attribute-set name="r-align-label">
      <xsl:attribute name="valign">top</xsl:attribute>
      <xsl:attribute name="align">right</xsl:attribute>
      <xsl:attribute name="width">150</xsl:attribute>
    </xsl:attribute-set>

  <!-- END   OF UTILITIES named template(s), attribute-set(s) -->

</xsl:stylesheet>