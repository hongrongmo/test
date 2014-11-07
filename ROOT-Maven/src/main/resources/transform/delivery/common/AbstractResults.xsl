<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    xmlns:ibfab="java:org.ei.data.insback.runtime.InspecArchiveAbstract"
    xmlns:ctd="java:org.ei.domain.ClassTitleDisplay"
    xmlns:pid="java:org.ei.util.GetPIDDescription" 
    xmlns:rfx="java:org.ei.books.collections.ReferexCollection"
    xmlns:crlkup="java:org.ei.data.CRNLookup"
    exclude-result-prefixes="hlight schar ibfab java html xsl crlkup ctd rfx"
>

<xsl:output method="html" indent="no"/>
  <xsl:strip-space elements="html:* xsl:*" />
  <xsl:param name="s3figurl" />	
  <xsl:template match="EI-DOCUMENT">
      <xsl:param name="ascii">false</xsl:param>
      <xsl:apply-templates select="BTI"/>
      <xsl:apply-templates select="BPP"/>
      <xsl:apply-templates select="TI"/>
      <xsl:if test="$ascii='true'">
          <xsl:text>&#xD;&#xA;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="AUS"/>
      <xsl:apply-templates select="EDS"/>
      <xsl:apply-templates select="IVS"/>
      <xsl:variable name="ISBN">
      	   <xsl:value-of select="BN"/>
      </xsl:variable>
      
      <xsl:variable name="ISSN">
           <xsl:value-of select="SN"/>
      </xsl:variable>
      <span CLASS="SmBlackText">
      <xsl:choose>
        <xsl:when test ="string(NO_SO)">
          <xsl:text></xsl:text>
        </xsl:when>
        <xsl:when test="string(SO)">
          <xsl:apply-templates select="SO"/>
        </xsl:when>
        <xsl:otherwise>
          <b>Source: </b>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:apply-templates select="PF"/>
      <xsl:apply-templates select="RSP"/>
      <xsl:apply-templates select="RN_LABEL"/>

      <xsl:apply-templates select="PN"/>
      <xsl:apply-templates select="VO"/>
	
      <xsl:if test="not(string(p_PP) or string(PP_pp))">
	        <xsl:apply-templates select="PP"/>
      </xsl:if>

      <xsl:apply-templates select="p_PP"/>
      <xsl:apply-templates select="PP_pp"/>
      <xsl:apply-templates select="ARN"/>

      <xsl:apply-templates select="RN"/>
      <xsl:apply-templates select="SD"/>
      <xsl:apply-templates select="PA"/>

      <xsl:apply-templates select="MT"/>
      <xsl:apply-templates select="VT"/>
      <xsl:apply-templates select="CITEDBY"/>
      <xsl:apply-templates select="PAS"/>
      <xsl:apply-templates select="PASM"/>

 	   <!-- EPT fields -->
      <xsl:apply-templates select="EASM"/>
      <xsl:apply-templates select="PINFO"/>
      <xsl:apply-templates select="PAPIM"/>
      <!--end of  EPT fields -->

  <!--    <xsl:apply-templates select="UPD"/> -->
      <xsl:apply-templates select="PAN"/>
      <xsl:apply-templates select="PM"/>
      <xsl:apply-templates select="PM1"/>
      <xsl:apply-templates select="PIM"/>


      <xsl:apply-templates select="UPD"/>

 <!--  <xsl:apply-templates select="KC"/> -->
      <xsl:apply-templates select="KD"/>
      <xsl:apply-templates select="PAP"/>
      <xsl:apply-templates select="PFD"/>
      <xsl:apply-templates select="PPD"/>
      <!-- added for 1884 -->
      <xsl:apply-templates select="PIDD"/>
      <xsl:apply-templates select="COPA"/>

      <xsl:if test="not(string(SD))">
        <xsl:apply-templates select="YR"/>
      </xsl:if>
      <xsl:apply-templates select="PD_YR"/>

     <!-- upt fields -->
      <xsl:apply-templates select="PI"/>
      <xsl:apply-templates select="PPN"/>
      <xsl:apply-templates select="AIN"/>
      <xsl:apply-templates select="IPC"/>
      <xsl:apply-templates select="ECL"/>
  <!-- end of upt fields -->

      


      <xsl:apply-templates select="LA"/>

      <xsl:if test="string(SN) or string(CN) or string(BN)">
        <!--<br/>-->
        <xsl:if test="$ascii='true'">
          <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
      </xsl:if>

      <!--<xsl:text> </xsl:text>-->
      
      <xsl:apply-templates select="SN"/>
      	 
      <xsl:apply-templates select="E_ISSN">
      <xsl:with-param name="ISSN" select= "$ISSN" />
      </xsl:apply-templates>
     <!-- <xsl:apply-templates select="CN"/> -->
      <xsl:apply-templates select="BN"/>
      	
      <xsl:apply-templates select="BN13">
      <xsl:with-param name="ISBN" select= "$ISBN" />
      </xsl:apply-templates>
      <xsl:apply-templates select="DO"/>
      <xsl:apply-templates select="ARTICLE_NUMBER"/>
    <!-- Book/Page Details (Abstract View)  -->
    <!-- BN will output ISBN: ######## -->
    <!-- Book Page Count -->
      <xsl:apply-templates select="BPC"/>
    <!-- Book Year -->
      <xsl:apply-templates select="BYR"/>
    <!-- Book Publisher -->
      <xsl:apply-templates select="BPN"/>
      <xsl:apply-templates select="CF"/>
      <xsl:apply-templates select="MD"/>
      <xsl:apply-templates select="ML"/>
      <xsl:apply-templates select="SP"/>

      <xsl:apply-templates select="CLOC"/>

      <xsl:apply-templates select="I_PN"/>
      
      <!-- <xsl:apply-templates select="PL"/> -->

      <xsl:apply-templates select="CPUB"/>

      <xsl:apply-templates select="FTTJ"/>
      <!-- moved DT, SC and AV for CBNB inot 'Citation' area of abstract view -->
      <xsl:apply-templates select="DT"/>
      <xsl:apply-templates select="AV"/>
      <xsl:apply-templates select="SC"/>
      <!-- put in a small font sized line break following the citation section of the abstract -->
      <br/>
      </span>

      <xsl:apply-templates select="AFS"/>

      <xsl:apply-templates select="AB"/>
      <xsl:apply-templates select="BAB"/>
      <xsl:apply-templates select="AB2"/>
      <xsl:apply-templates select="IMG"/>
      <xsl:apply-templates select="NR"/>
      <xsl:apply-templates select="AT"/>
      <xsl:apply-templates select="BKYS"/>

      <xsl:apply-templates select="CPO"/>
      <xsl:apply-templates select="CMS"/>

      <xsl:apply-templates select="MJSM">
        <xsl:with-param name="DBNAME" select="DOC/DB/DBNAME"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="MH"/>
      <xsl:apply-templates select="CVS">
        <xsl:with-param name="DBNAME" select="DOC/DB/DBNAME"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="CHS">
        <xsl:with-param name="DBNAME" select="DOC/DB/DBNAME"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="CRM"/>
      <xsl:apply-templates select="FLS"/>
      <xsl:apply-templates select="CLS"/>
      <xsl:apply-templates select="RGIS"/>
      <xsl:apply-templates select="PIDM"/>
      <xsl:apply-templates select="PIDEPM"/>
      <xsl:apply-templates select="PIDM8"/>
      <xsl:apply-templates select="PUCM"/>
      <xsl:apply-templates select="PECM"/>
      <xsl:apply-templates select="COL"/>

      <!-- <xsl:apply-templates select="CRDN"/> -->
      <xsl:apply-templates select="LOCS"/>
      
      <xsl:apply-templates select="TRS"/>

      <BR/>
      <xsl:if test="$ascii='true'">
        <xsl:text>&#xD;&#xA;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="DB"/>
      <xsl:apply-templates select="DOC/DB/DBNAME"/>
    	<xsl:if test="(DOC/DB/DBMASK='2097152')">
        <xsl:apply-templates select="CPR"/>
      </xsl:if>
      <P/>
      

    <script language="JavaScript" type="text/javascript" src="/static/js/wz_tooltip.js"></script>

    </xsl:template>

    <xsl:template match="CPR">
      <br/><br/><span class="MedBlackText"><xsl:value-of disable-output-escaping="yes" select="."/></span>
    </xsl:template>

    <!-- GeoRef Templates -->
    <xsl:template match="CRDN">
      <br/><br/>
      <!-- <xsl:if test="string(@label)">
        <span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span>
      </xsl:if> -->
      <span CLASS="MedBlackText">
        <div id="map" style="display:block;">
          <div id="map_canvas" style="float:left; width: 100%; height: 300px;"></div>
        </div>
      </span>
      <span class="SpLink" href="javascript:resetCenterAndZoom();">Reset view</span>&#160;
      <span class="SpLink" href="javascript:resetAllMarkers(true);">Show all Markers</span>&#160;
      <span class="SpLink" href="javascript:resetAllMarkers();">Hide all Markers</span>&#160;
      <span class="SpLink" href="javascript:invertMarkers();">Invert Markers</span>
    </xsl:template>

    <xsl:template match="LOC[@ID]">
      <span class="MedBlackText">
      <!-- <xsl:attribute name="href">javascript:toggleMarker('<xsl:value-of select="@ID"/>');</xsl:attribute> -->
      <!-- <xsl:attribute name="title">Toggle marker on map</xsl:attribute> -->
      <xsl:value-of select="@ID"/> - <xsl:value-of disable-output-escaping="yes" select="."/></span>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="COMMA_SPACER"/>
      </xsl:if>
    </xsl:template>

    <xsl:template match="LOC">
      <span class="MedBlackText">
      <!-- <xsl:attribute name="href">javascript:toggleMarker('<xsl:value-of select="@ID"/>');</xsl:attribute> -->
      <!-- <xsl:attribute name="title">Toggle marker on map</xsl:attribute> -->
      <xsl:value-of disable-output-escaping="yes" select="."/></span>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="COMMA_SPACER"/>
      </xsl:if>
    </xsl:template>

    <xsl:template match="LOCS">
      <br/><br/><span class="MedBlackText"><b><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></b></span><xsl:text> </xsl:text><xsl:apply-templates />
    </xsl:template>
    
     <xsl:template match="TRS">
	  <xsl:if test="string(@label)">
	      <br/><br/><span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:</b> </span><xsl:text> </xsl:text>
	  </xsl:if>
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
	
    </xsl:template>
    
     <xsl:template match="TR|TR/TTI">
     	<xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
     	<xsl:if test="not(position()=last())">
     	     <xsl:text>; </xsl:text>
     	</xsl:if>
     </xsl:template>

    <!-- EPT Templates -->
    <xsl:template match="PINFO">
      <br/><b> Patent information:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="PAPIM">
      <b> Application information:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    
    <xsl:template match="ARN">
	<xsl:text>, art. no. </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    
    <xsl:template match="PIM">
      <b> Priority information:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="EASM">
      <b> Patent assignee: </b><xsl:apply-templates />
    </xsl:template>

    <xsl:template match="EAS">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">PPA</xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>

<!-- End of  EPT Templates -->
<!-- Book Templates -->
    <xsl:template match="BTI">
      <span CLASS="MedBlackText"><b><xsl:value-of select="." disable-output-escaping="yes"/></b></span>
      <xsl:if test="string(../BTST)"><span CLASS="MedBlackText"><b>: <xsl:value-of select="../BTST" disable-output-escaping="yes"/></b></span></xsl:if>
    </xsl:template>

    <xsl:template match="BPP">
      <xsl:if test="not(string(.)='0')">
        <span CLASS="MedBlackText">, <xsl:value-of select="." disable-output-escaping="yes"/></span>
      </xsl:if>
      <br/>
    </xsl:template>

    <xsl:template match="BYR">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="BPC">
    <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
        <xsl:text> p</xsl:text>
    </xsl:template>

    <xsl:template match="BPN">
      <xsl:text>; </xsl:text><b>Publisher:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><br/>
    </xsl:template>

  <!--
    <xsl:template match="CVR">
      <img border="0" width="100" height="145" style="float:left; margin-right:10px;">
        <xsl:attribute name="SRC"><xsl:value-of select="."/></xsl:attribute>
        <xsl:attribute name="ALT"><xsl:value-of select="ancestor::EI-DOCUMENT/TI"/></xsl:attribute>
      </img>
    </xsl:template>
-->

    <xsl:variable name="BOOK-SORT-PARAMS">&amp;sortdir=up&amp;sort=stsort</xsl:variable>

    <xsl:template match="COL">
      <br/><br/><span CLASS="SmBlackText"><b><xsl:value-of select="@label"/>:<xsl:call-template name="DOUBLE_SPACER"/></b></span>
<!-- DO not use LINK template - since we have a special search embedded on the COL link for Boooks -->
      <span class="SpLink">
      <xsl:if test="not(/PAGE/LINK ='false') and not(/SECTION-DELIM/LINK ='false')">
        <xsl:attribute name="href">/search/results/quick.url?CID=quickSearchCitationFormat&amp;searchtype=Book&amp;col=<xsl:value-of select="rfx:translateCollection(text())"/>&amp;database=131072&amp;yearselect=yearrange<xsl:value-of select="$BOOK-SORT-PARAMS"/></xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="text()"/></xsl:attribute>
      </xsl:if>
      <xsl:value-of select="text()"/></span>

    </xsl:template>


    <xsl:template match="BKYS">
      <br/><br/><span CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:<xsl:call-template name="DOUBLE_SPACER"/></b></span>
      <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="BKY">
      <xsl:call-template name="LINK">
      <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
      <xsl:with-param name="FIELD">KY</xsl:with-param>
      <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>

<!-- Book Templates -->

  <xsl:template match="PIDM8|PIDM|PUCM|PECM|PIDEPM">
    <br/><br/>
    <span class="MedBlackText"><b>
    <xsl:choose>
      <xsl:when test="name(.)='PIDM'">IPC Code:</xsl:when>
      <xsl:when test="name(.)='PIDEPM'">IPC Code:</xsl:when>
      <xsl:when test="name(.)='PIDM8'">IPC-8 Code:</xsl:when>
      <xsl:when test="name(.)='PUCM'">US Classification:</xsl:when>
      <xsl:when test="name(.)='PECM'">ELCA Code:</xsl:when>
    </xsl:choose>
    </b></span><xsl:text> </xsl:text>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="PID|PUC|PEC|PIDEP">
      <xsl:variable name="CLASS">
         <xsl:choose>
            <xsl:when test="(@level='A') and (@type='I')">SpBoldItalicLink</xsl:when>
            <xsl:when test="(@level='A') and (@type='N')">SpItalicLink</xsl:when>
            <xsl:when test="(@level='C') and (@type='I')">SpBoldLink</xsl:when>
            <xsl:otherwise>SpLink</xsl:otherwise>
         </xsl:choose>
      </xsl:variable>

      <xsl:variable name="FIELDNAME">

      	<xsl:choose>
      		<xsl:when test="name()='PIDEP'">PID</xsl:when>
      		<xsl:otherwise><xsl:value-of select="name(.)"/></xsl:otherwise>
      	</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="IPC-DESCRIPTION">
            <xsl:value-of select="pid:getDescription(./CID, ./CTI)" />
      </xsl:variable>

      <xsl:call-template name="LINK">
         <xsl:with-param name="TERM"><xsl:value-of select="./CID"/></xsl:with-param>
         <xsl:with-param name="FIELD"><xsl:value-of select="$FIELDNAME"/></xsl:with-param>
         <xsl:with-param name="CLASS"><xsl:value-of select="$CLASS"/></xsl:with-param>
        <!-- <xsl:with-param name="ONMOUSEOVER">this.T_WIDTH=450;return escape('<xsl:value-of select="ctd:getDisplayTitle(hlight:addMarkup(./CTI))"/>')</xsl:with-param> -->
    
        <xsl:with-param name="ONMOUSEOVER">this.T_WIDTH=450;return escape('<xsl:value-of select="$IPC-DESCRIPTION"/>')</xsl:with-param> 
      </xsl:call-template>
      <xsl:if test="position()!=last()">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
  </xsl:template>

    <xsl:template match="KC">
        <xsl:text> </xsl:text><b>Kind:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="KD">
        <xsl:text> </xsl:text><b>Kind:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
        <xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="TI">
      <span class="MedBlackText"><b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></b></span>
      <xsl:if test="../TT">
        <span class="MedBlackText"><xsl:text> </xsl:text><b>(<xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>)</b></span>
      </xsl:if>
      <br/>
    </xsl:template>

    <xsl:template match="AUS|EDS|IVS">
    <!-- if EPT display Inventor(s): -->
    	<xsl:if test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2048')">
        	<span CLASS="MedBlackText"><b> Inventor(s): </b></span>
      	</xsl:if>
      <xsl:apply-templates />
    </xsl:template>


    <!-- CLASSIFICATION CODE  -->
	<xsl:template match="CID">
	    <xsl:call-template name="LINK">
		<xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
		<xsl:with-param name="FIELD">CL</xsl:with-param>
	    </xsl:call-template>
	</xsl:template>

    <!-- CLASSIFICATION DESCRIPTION  -->
    <xsl:template match="CTI">
    	<span CLASS="MedBlackText"><xsl:text> </xsl:text><xsl:value-of select="normalize-space(text())" disable-output-escaping="yes"/></span>
    </xsl:template>

    <xsl:template match="RSP">
     <xsl:text>; </xsl:text><b>Sponsor:</b><xsl:text> </xsl:text> <xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="RN_LABEL">
      <xsl:text>; </xsl:text><b>Report:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="SO">
      <b>Source: </b><i><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VO">
      <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SD">
      <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MT">
      <xsl:text>,</xsl:text><xsl:text> </xsl:text><i><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VT">
      <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <!-- publisher will ONLY show if there is no other leading information
        so this will follow 'Source' label - no comma needed                -->
    <xsl:template match="PN">
      <xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>


    <xsl:template match="PAS">
      <b> Assignee:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PASM">
    	<xsl:choose>
      		<xsl:when test="not(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2048')">
      		<b> Assignee:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
      </xsl:when>
  <!--
      <xsl:when test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2048')">
        <b> Pat Assignee:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
      </xsl:when>
  -->
    </xsl:choose>
    </xsl:template>
 <!-- upt fields  -->
    <xsl:template match="PAP">
    <br/><b> Patent number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:variable name ="PPN" >
      <xsl:value-of select="PPN"/>
    </xsl:variable>

    <xsl:variable name ="PAP" >
      <xsl:value-of select="PAP"/>
    </xsl:variable>

    <xsl:variable name ="PAN" >
      <xsl:value-of select="PAN"/>
    </xsl:variable>

    <xsl:template match="PPN">
      <xsl:if test="not($PAP)">
        <br/>
      </xsl:if>
       <span CLASS="MedBlackText">
      <b> Priority Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></span>
    </xsl:template>
    <!-- upt field -->

    <xsl:template match="PI">
     <span CLASS="MedBlackText">
      <b> Priority Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
      </span>
    </xsl:template>


    <xsl:template match="PAN">
      <xsl:if test="(not($PAP) and not($PPN))" >
        <br/>
      </xsl:if>
      <b> Application number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="PM">
      <xsl:if test="(not($PAP) and not($PPN) and not($PAN))" >
        <br/>
      </xsl:if>
      <b> Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PM1">
      <b>Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:variable name = "IPC">
      <xsl:value-of select = "IPC"/>
    </xsl:variable>

    <xsl:template match="IPC">
      <br/><b> IPE Code:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="ECL">
      <xsl:if test="not($IPC)">
        <br/>
      </xsl:if>
      <b> ECLA Classes:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <!-- upt fields -->

    <xsl:template match="PFD">
    <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b> Filing date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PPD">
      <b> Publication date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <!-- upt pub date -->
    <xsl:template match="UPD">
       <xsl:choose>
       		<xsl:when test="string(@label)">
      			<b> <xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
       		</xsl:when>
       		<xsl:otherwise>
       			<b> Publication date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
       		</xsl:otherwise>
       </xsl:choose>
    </xsl:template>

    <xsl:template match="YR">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PD_YR">
      <b> Publication date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="RN">
      <xsl:text>, </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PA">
      <xsl:text>, </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PR">
      <xsl:text>, </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <!-- PAGE RANGES -->
    <xsl:template match="PP">
      <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="p_PP">
      <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP_pp">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>pp </xsl:text>
    </xsl:template>

    <xsl:template match="LA">
      <xsl:text>; </xsl:text><b>Language:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="SN">   
       <xsl:text>;  </xsl:text><b>ISSN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>  
    </xsl:template>

    <xsl:template match="E_ISSN">
       <xsl:param name="ISSN"/>
       <xsl:choose>
                   <xsl:when test="boolean(string-length(normalize-space($ISSN))>0)">
                        <xsl:text>,  </xsl:text>
                   </xsl:when>
                   <xsl:otherwise>
                   	<xsl:text>;  </xsl:text>
                   </xsl:otherwise>
      </xsl:choose>
      <b>E-ISSN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <!--  Document type, only show AIP -->
    <xsl:template match="DT">
      <xsl:if test="text()='Article in Press'">
          <span CLASS="MedBlackText"><br/><img src="/static/images/btn_aip.gif" border="0" style="vertical-align:bottom" title="Articles not published yet, but available online" alt="Articles not published yet, but available online"/><xsl:text> Article in Press</xsl:text></span>
      </xsl:if>    
      <xsl:if test="text()='In Process'">
          <span CLASS="MedBlackText"><br/><img src="/static/images/btn_aip.gif" border="0" style="vertical-align:bottom" title="Records still in the process of being indexed, but available online." alt="Records still in the process of being indexed, but available online."/><xsl:text> In Process</xsl:text></span>
      </xsl:if>    
    </xsl:template>
    <!-- AV may have <span> anchor in it so we cannot have a nested anchor so we use a span here -->
    <xsl:template match="AV|SC">
      <xsl:text>; </xsl:text><span class="SmBlackText"><b><xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/></span>
    </xsl:template>

    <xsl:template match="CN">
      <b>CODEN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="BN">
     
      <xsl:text>;  </xsl:text><b><xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    
    <xsl:template match="BN13">
      <xsl:param name="ISBN"/>
      <xsl:choose>
      	  <xsl:when test="boolean(string-length(normalize-space($ISBN))>0)">
      		<xsl:text>,  </xsl:text>
      	  </xsl:when>
      	  <xsl:otherwise>
      	       <xsl:text>;  </xsl:text>
      	  </xsl:otherwise>
      </xsl:choose>
      <b><xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    
    <xsl:template match="DO">
          <xsl:text>; </xsl:text><b><xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    
    <xsl:template match="ARTICLE_NUMBER">
          <xsl:text>; </xsl:text><b><xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="CF">
      <!--<xsl:text>&#xD;&#xA;</xsl:text>-->
      <xsl:text>; </xsl:text><b>Conference:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MD">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="ML">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SP">
      <xsl:text>; </xsl:text><b> Sponsor:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="I_PN">
      <xsl:text>; </xsl:text><xsl:text>&#xD;&#xA;</xsl:text>
      <b>Publisher:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PL">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PLA">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b>Place of publication: </b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>

    <!-- jam added for 1884 -->
    <xsl:template match="PIDD">
      <b> Patent issue date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="COPA">
      <b> Country of application:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CPUB">
      <xsl:text>;&#xD;&#xA;</xsl:text>
      <b>Country of publication: </b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="FTTJ">
      <xsl:text>;&#xD;&#xA;</xsl:text><br/>
      <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="AB|BAB">
      <xsl:text>&#xD;&#xA;</xsl:text>
      <span class="SmBlackText"><br/></span><span class="MedBlackText"><b><xsl:value-of select="@label"/>: </b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></span>
    </xsl:template>
    <xsl:template match="AB2">
      <xsl:text>&#xD;&#xA;</xsl:text>
      <br/><br/><span class="MedBlackText"><b>Abstract: </b><xsl:text> </xsl:text><xsl:value-of select="ibfab:getHTML(hlight:addMarkupCheckTagging(.))" disable-output-escaping="yes"/></span>
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
      <xsl:variable name="cap" select= "text()"/>
      <xsl:variable name="figureUrl"><xsl:value-of select="$s3figurl"/><xsl:value-of select="@img"/></xsl:variable>
      <td valign="top" align="middle"><span href="javascript:window.open('{$figureUrl}','newwind','width=650,height=600,scrollbars=yes,resizable,statusbar=yes');void('');"><img src="{$figureUrl}" alt="{$cap}" border="1" width="100" height="100"/></span>
      <br/><span class="SmBlackText"><xsl:value-of select="$cap" disable-output-escaping="yes"/></span></td><td valign="top" width="15"></td>
    </xsl:template>

    <xsl:template match="NR">
      <span CLASS="MedBlackText"><xsl:text> (</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> refs.)</xsl:text></span>
    </xsl:template>
    <xsl:template match="AT">
      <span CLASS="MedBlackText"><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/></span>
    </xsl:template>

    <xsl:template match="CVS|MJSM|CRM|CMS|CPO">
    	<xsl:param name="DBNAME"/>
      <br/><br/><span CLASS="MedBlackText"><b>
        <xsl:choose>
          <xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
          <xsl:otherwise><xsl:text>&#xD;&#xA;</xsl:text><xsl:value-of select="../PVD"/> controlled terms</xsl:otherwise>
        </xsl:choose>:<xsl:call-template name="DOUBLE_SPACER"/></b></span>
      <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="MH"> 
        <br/><br/><span class="SmBlackText"><b>Main Heading:</b></span><xsl:text> </xsl:text>
        <xsl:call-template name="LINK">
        	<xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        	<xsl:with-param name="FIELD">MH</xsl:with-param>
        	<xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template> 
    </xsl:template>


    <xsl:template match="CR">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">CR</xsl:with-param>
      </xsl:call-template>
      <span CLASS="SmBlackText">
      <xsl:value-of select="crlkup:getName(normalize-space(text()))" disable-output-escaping="yes"/></span>
      <xsl:if test="position()!=last()">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>


    <xsl:template match="FLS">
      <xsl:choose>
        <xsl:when test="string(@label)">
          <span CLASS="MedBlackText"><br/><br/><b><xsl:value-of select="@label"/>:<xsl:call-template name="DOUBLE_SPACER"/></b></span>
        </xsl:when>
        <xsl:otherwise>
          <span CLASS="MedBlackText"><br/><br/><b>Species term:<xsl:call-template name="DOUBLE_SPACER"/></b></span>
        </xsl:otherwise>
      </xsl:choose>
  	<xsl:apply-templates/>
    </xsl:template>


    <xsl:template match="RGIS">
	<span CLASS="MedBlackText"><br/><br/><b>Regional term: <xsl:call-template name="DOUBLE_SPACER"/></b></span>
      	<xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="CLS">
	<span CLASS="MedBlackText"><br/><br/><b>Classification Code: <xsl:call-template name="DOUBLE_SPACER"/></b></span>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="DBNAME">
      <br/><span class="MedBlackText"><b>Database:</b><xsl:text> </xsl:text><xsl:value-of select="."/></span>
    </xsl:template>

    <xsl:template match="STT">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <span CLASS="SmBlackText"><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></span>
    </xsl:template>

    <xsl:template match="PF">
      <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/><xsl:text>)</xsl:text>
    </xsl:template>

    <xsl:template match="AFS">
      <span class="SmBlackText"><br/><b>Author affiliation:</b></span><xsl:text> </xsl:text><br/>
      <table border="0" cellspacing="0" cellpadding="1">
        <xsl:apply-templates/>
      </table>
    </xsl:template>

    <xsl:template match="AF|EF">
      <tr>
        <td valign="top">
          <xsl:if test="(@id)">
            <xsl:if test="not(@id='0')">
              <span class="SmBlackText"><sup><xsl:value-of select="@id"/></sup></span>
            </xsl:if>
          </xsl:if>
          <xsl:text> </xsl:text>
        </td>
        <td>
          <span class="SmBlackText"><xsl:value-of select="hlight:addMarkup(normalize-space(text()))" disable-output-escaping="yes"/></span>
        </td>
      </tr>
    </xsl:template>

    <xsl:template match="AU|ED|IV">
      <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>

      <!-- If the name does not contain the text Anon -->

      <xsl:if test="not(position()=1)"><span CLASS="SmBlackText">;</span><xsl:text> </xsl:text></xsl:if>
      <xsl:if test="boolean(not(contains($NAME,'Anon')))">
        <xsl:call-template name="LINK">
          <xsl:with-param name="TERM"><xsl:value-of select="$NAME"/></xsl:with-param>
          <xsl:with-param name="FIELD">AU</xsl:with-param>
        </xsl:call-template>
        <xsl:if test="position()=1 and (position()=last())">
          <xsl:if test="name(.)='ED'">
            <span CLASS="SmBlackText"><xsl:text>, ed.</xsl:text></span>
          </xsl:if>
        </xsl:if>
      </xsl:if>
      <!-- If the name contains Anon text -->
      <xsl:if test="boolean(contains($NAME,'Anon'))"><span CLASS="SmBlackText"><xsl:value-of select="$NAME"/></span></xsl:if>
      <xsl:if test="./AFS">
        <xsl:if test="not(@id='0')">
          <span class="SmBlackText"><sup><xsl:apply-templates select="./AFS/AFID"/></sup></span>
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

      <xsl:if test="position()=last()">
        <xsl:if test="name(.)='ED' and not(position()=1)">
          <span CLASS="SmBlackText"><xsl:text> eds.</xsl:text></span>
        </xsl:if>
        <xsl:text> </xsl:text>
      </xsl:if>

    </xsl:template>

	<xsl:template match="AFID">
		<xsl:value-of select="."/>
		<xsl:if test="not(position()=last())">,<xsl:text> </xsl:text></xsl:if>
	</xsl:template>

    <xsl:template match="CM|CP">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>

    <xsl:template match="GC">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">CL</xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>


    <xsl:template match="CV">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">CV</xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>

      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
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


    <xsl:template match="MJS">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">CVM</xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>

    <xsl:template match="FL">
          <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD">FL</xsl:with-param>
            <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
          </xsl:call-template>
          <xsl:if test="not(position()=last())">
            <xsl:call-template name="DASH_SPACER"/>
          </xsl:if>
    </xsl:template>

    <xsl:template match="RGI">
    	<xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD">RGI</xsl:with-param>
            <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      	</xsl:call-template>
       	<xsl:if test="not(position()=last())">
            <xsl:call-template name="DASH_SPACER"/>
       	</xsl:if>
    </xsl:template>

    <xsl:template match="CL">
           <xsl:apply-templates/>
           <xsl:if test="not(position()=last())">
	   	<xsl:call-template name="DASH_SPACER"/>
      	   </xsl:if>
    </xsl:template>

    <xsl:template name="DASH_SPACER"><span CLASS="SmBlackText">&#160; - &#160;</span></xsl:template>

    <xsl:template name="COMMA_SPACER"><span CLASS="SmBlackText">&#160;, &#160;</span></xsl:template>

    <xsl:template name="DOUBLE_SPACER">&#160;&#160;</xsl:template>

    <xsl:template name="LINK">

      <xsl:param name="TERM"/>
      <xsl:param name="FIELD"/>
      <xsl:param name="NAME"/>
      <xsl:param name="ONMOUSEOVER"/>
      <xsl:param name="CLASS"/>

      <xsl:variable name="ENCODED-SEARCH-TERM">
        <xsl:value-of select="java:encode(schar:preprocess(hlight:removeMarkup($TERM)))" disable-output-escaping="yes"/>
      </xsl:variable>

        <xsl:variable name="SEARCH-FIELD">
          <xsl:choose>
            <xsl:when test="($FIELD='BKY')">KY</xsl:when>
            <xsl:otherwise><xsl:value-of select="$FIELD"/></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

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
<!--            <xsl:when test="string(//SESSION-DATA/START-YEAR)">&amp;startYear=<xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:when> -->
            <xsl:otherwise></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="END-YEAR-PARAM">
          <xsl:choose>
            <xsl:when test="($FIELD='AU')"></xsl:when>
<!--                        <xsl:when test="string(//SESSION-DATA/END-YEAR)">&amp;endYear=<xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:when> -->
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

      <xsl:variable name="SEARCH-TYPE"><xsl:value-of select="//SEARCH-TYPE"/></xsl:variable>

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

      <xsl:variable name="ENCODED-DATABASE">
        <xsl:value-of select="java:encode($DATABASE)" />
      </xsl:variable>

        <!-- Default Sort Options for these links -->
        <!-- make changes here(like field or adding direction) -->
        <!-- <xsl:value-of select="$DEFAULT-LINK-SORT"/> is appended to HREF attribute -->
        <xsl:variable name="DEFAULT-LINK-SORT">
          <xsl:choose>
            <xsl:when test="(($SEARCH-TYPE='Book') or ($DATABASE='131072')) and ($FIELD='BKY')">&amp;sortdir=dw&amp;sort=relevance</xsl:when>
            <xsl:when test="($SEARCH-TYPE='Book') or ($DATABASE='131072')"><xsl:value-of select="$BOOK-SORT-PARAMS"/></xsl:when>
            <xsl:otherwise>&amp;sort=yr</xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

      <span CLASS="SpLink">
      <xsl:if test="string($CLASS)">
        <xsl:attribute name="CLASS"><xsl:value-of select="$CLASS"/></xsl:attribute>
      </xsl:if>
        <xsl:if test="not(/PAGE/LINK ='false') and not(/SECTION-DELIM/LINK ='false')">
          <xsl:choose>
            <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">
              <xsl:variable name="ENCODED-SEARCH-SECTION"><xsl:value-of select="java:encode(' WN ')"/><xsl:value-of select="$SEARCH-FIELD"/></xsl:variable>
              <xsl:attribute name="href">/search/results/expert.url?CID=expertSearchCitationFormat&amp;searchWord1=({<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}<xsl:value-of select="$ENCODED-SEARCH-SECTION"/>)&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:when>
            <xsl:when test="($SEARCH-TYPE='Book') or ($DATABASE='131072')">
              <xsl:attribute name="href">/search/results/quick.url?CID=quickSearchCitationFormat&amp;searchtype=Book&amp;searchWord1={<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}&amp;section1=<xsl:value-of select="$SEARCH-FIELD"/>&amp;database=131072<xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="href">/search/results/quick.url?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}&amp;section1=<xsl:value-of select="$SEARCH-FIELD"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="string($ONMOUSEOVER)">
          <xsl:attribute name="onmouseover"><xsl:value-of select="$ONMOUSEOVER"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="$NAME='MH'"><xsl:value-of select="hlight:addMarkup($TERM)" disable-output-escaping="yes"/></xsl:if>
        <xsl:if test="not($NAME='MH')"><xsl:value-of select="hlight:addMarkup($TERM)" disable-output-escaping="yes"/></xsl:if>
      </span>

    </xsl:template>
    


</xsl:stylesheet>