<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    xmlns:ibfab="java:org.ei.data.inspec.InspecArchiveAbstract"
    xmlns:ctd="java:org.ei.domain.ClassTitleDisplay"
    xmlns:rfx="java:org.ei.books.collections.ReferexCollection"
    xmlns:crlkup="java:org.ei.data.CRNLookup"
    exclude-result-prefixes="hlight schar ibfab java html xsl crlkup ctd rfx"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />
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

      <xsl:apply-templates select="RN"/>
      <xsl:apply-templates select="SD"/>
      <xsl:apply-templates select="PA"/>

      <xsl:apply-templates select="MT"/>
      <xsl:apply-templates select="VT"/>

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

      <xsl:if test="not(string(p_PP) or string(PP_pp))">
        <xsl:apply-templates select="PP"/>
      </xsl:if>

      <xsl:apply-templates select="p_PP"/>
      <xsl:apply-templates select="PP_pp"/>


      <xsl:apply-templates select="LA"/>

      <xsl:if test="string(SN) or string(CN) or string(BN)">
        <br/>
        <xsl:if test="$ascii='true'">
          <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
      </xsl:if>

      <xsl:apply-templates select="SN"/>
      <xsl:apply-templates select="E_ISSN"/>
      <xsl:apply-templates select="CN"/>
      <xsl:apply-templates select="BN"/>
      <xsl:apply-templates select="BN13"/>
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
      </span>
      <xsl:apply-templates select="DT"/>
      <xsl:apply-templates select="AV"/>
      <xsl:apply-templates select="SC"/>

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
      <xsl:apply-templates select="CVS">
        <xsl:with-param name="DBNAME" select="DOC/DB/DBNAME"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="CRM"/>
      <xsl:apply-templates select="FLS"/>
      <xsl:apply-templates select="CLS"/>
      <xsl:apply-templates select="RGIS"/>
      <xsl:apply-templates select="PIDM"/>
      <xsl:apply-templates select="PIDM8"/>
      <xsl:apply-templates select="PUCM"/>
      <xsl:apply-templates select="PECM"/>
      <xsl:apply-templates select="COL"/>

      <BR/>
      <xsl:if test="$ascii='true'">
        <xsl:text>&#xD;&#xA;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="DB"/>
      <xsl:apply-templates select="DOC/DB/DBNAME"/>
      <P/>

      <script language="JavaScript" type="text/javascript" src="/engresources/js/wz_tooltip.js"></script>

    </xsl:template>
 <!-- EPT Templates -->

     <xsl:template match="PINFO">
        <a CLASS="MedBlackText"><br/><b> Patent information:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></a>
    </xsl:template>

    <xsl:template match="PAPIM">
        <a CLASS="MedBlackText"><b> Application information:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></a>
    </xsl:template>

    <xsl:template match="PIM">
       <a CLASS="MedBlackText"><b> Priority information:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/> </a>
    </xsl:template>

    <xsl:template match="EASM">
    	<a CLASS="MedBlackText"><b> Patent assignee: </b></a><xsl:apply-templates />
    </xsl:template>

    <xsl:template match="EAS">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">PPA</xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <A CLASS="SmBlackText">&#160; - &#160;</A>
      </xsl:if>
    </xsl:template>

<!-- End of  EPT Templates -->
<!-- Book Templates -->
    <xsl:template match="BTI">
      <a CLASS="MedBlackText"><b><xsl:value-of select="." disable-output-escaping="yes"/></b></a>
      <xsl:if test="string(../BTST)"><a CLASS="MedBlackText"><b>: <xsl:value-of select="../BTST" disable-output-escaping="yes"/></b></a></xsl:if>
    </xsl:template>

    <xsl:template match="BPP">
      <xsl:if test="not(string(.)='0')">
        <a CLASS="MedBlackText">, <xsl:value-of select="." disable-output-escaping="yes"/></a>
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
      <br/><b>Publisher:</b>&#160;<xsl:value-of select="." disable-output-escaping="yes"/><br/>
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
      <br/><br/><a CLASS="SmBlackText"><b><xsl:value-of select="@label"/>:&#160;&#160;</b></a>
<!-- DO not use LINK template - since we have a special search embedded on the COL link for Boooks -->
      <a class="SpLink">
      <xsl:if test="not(/PAGE/LINK ='false') and not(/SECTION-DELIM/LINK ='false')">
        <xsl:attribute name="href">/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;searchtype=Book&amp;col=<xsl:value-of select="rfx:translateCollection(text())"/>&amp;database=131072&amp;yearselect=yearrange<xsl:value-of select="$BOOK-SORT-PARAMS"/></xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="text()"/></xsl:attribute>
      </xsl:if>
      <xsl:value-of select="text()"/></a>

    </xsl:template>


    <xsl:template match="BKYS">
      <br/><br/><a CLASS="MedBlackText"><b><xsl:value-of select="@label"/>:&#160;&#160;</b></a>
      <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="BKY">
      <xsl:call-template name="LINK">
      <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
      <xsl:with-param name="FIELD">KY</xsl:with-param>
      <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <A CLASS="SmBlackText">&#160; - &#160;</A>
      </xsl:if>
    </xsl:template>

<!-- Book Templates -->

  <xsl:template match="PIDM8|PIDM|PUCM|PECM">
    <br/><br/>
    <a class="MedBlackText"><b>
    <xsl:choose>
      <xsl:when test="name(.)='PIDM'">IPC Code:</xsl:when>
      <xsl:when test="name(.)='PIDM8'">IPC-8 Code:</xsl:when>
      <xsl:when test="name(.)='PUCM'">US Classification:</xsl:when>
      <xsl:when test="name(.)='PECM'">ELCA Code:</xsl:when>
    </xsl:choose>
    </b></a><xsl:text> </xsl:text>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="PID|PUC|PEC">
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
      		<xsl:when test="not(../../DOC/DB/DBMASK='2048')">
      			<xsl:value-of select="name(.)"/>
      		</xsl:when>
      		<xsl:otherwise>IP</xsl:otherwise>
      	</xsl:choose>
      </xsl:variable>

      <xsl:call-template name="LINK">
         <xsl:with-param name="TERM"><xsl:value-of select="./CID"/></xsl:with-param>
         <xsl:with-param name="FIELD"><xsl:value-of select="$FIELDNAME"/></xsl:with-param>
          <xsl:with-param name="CLASS"><xsl:value-of select="$CLASS"/></xsl:with-param>
         <xsl:with-param name="ONMOUSEOVER">this.T_WIDTH=450;return escape('<xsl:value-of select="ctd:getDisplayTitle(hlight:addMarkup(./CTI))"/>')</xsl:with-param>
      </xsl:call-template>
      <xsl:if test="position()!=last()">
        <a class="SmBlkText">&#160; - &#160;</a>
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
      <a CLASS="MedBlackText"><b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></b></a><br/>
    </xsl:template>

    <xsl:template match="AUS|EDS|IVS">
    <!-- if EPT display Inventor(s): -->
    	<xsl:if test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2048')">
        	<a CLASS="MedBlackText"><b> Inventor(s): </b></a>
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
    	<a CLASS="MedBlackText"><xsl:text> </xsl:text><xsl:value-of select="normalize-space(text())" disable-output-escaping="yes"/></a>
    </xsl:template>

    <xsl:template match="RSP">
     <xsl:text> </xsl:text><b>Sponsor:</b><xsl:text> </xsl:text> <xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="RN_LABEL">
      <xsl:text> </xsl:text><b>Report:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
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
    <a CLASS="MedBlackText">
    <br/><b> Patent number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </a>
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
       <a CLASS="MedBlackText">
      <b> Priority Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></a>
    </xsl:template>
    <!-- upt field -->

    <xsl:template match="PI">
     <a CLASS="MedBlackText">
      <b> Priority Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
      </a>
    </xsl:template>


    <xsl:template match="PAN">
      <xsl:if test="(not($PAP) and not($PPN))" >
        <br/>
      </xsl:if>
      <a CLASS="MedBlackText">
      <b> Application number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
     </a>
    </xsl:template>

    <xsl:template match="PM">
    <a CLASS="MedBlackText">
      <xsl:if test="(not($PAP) and not($PPN) and not($PAN))" >
        <br/>
      </xsl:if>
      <b> Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </a>
    </xsl:template>
      <xsl:template match="PM1">
      <a CLASS="MedBlackText">
        <b>Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
       </a>
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
    <a CLASS="MedBlackText"><xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b> Filing date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </a>
    </xsl:template>
    <xsl:template match="PPD">
     <a CLASS="MedBlackText">
      <b> Publication date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
     </a>
    </xsl:template>

    <!-- upt pub date -->
    <xsl:template match="UPD">
     <a CLASS="MedBlackText">
      <b> Publication date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
     </a>
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
      <xsl:text> </xsl:text><b>Language:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="SN">
      <b>ISSN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="E_ISSN">
      <b>Electronic ISSN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>

    <!-- CBNB Document type, Availability and Scope -->
    <xsl:template match="DT">
      <br/><a class="SmBlackText"><b><xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/></a><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="AV|SC">
      <a class="SmBlackText"><b><xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/></a><xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="CN">
      <b>CODEN:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="BN">
      <b><xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="BN13">
      <b><xsl:value-of select="@label"/>:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="CF">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b>Conference:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MD">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="ML">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="SP">
      <b> Sponsor:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="I_PN">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
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
	<a CLASS="MedBlackText">
      <b> Country of application:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </a>
    </xsl:template>
    <xsl:template match="CPUB">
    <a  CLASS="MedBlackText">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <b>Country of publication: </b><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </a>
    </xsl:template>

    <xsl:template match="FTTJ">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="AB|BAB">
      <xsl:text>&#xD;&#xA;</xsl:text>
      <span CLASS="MedBlackText"><a CLASS="MedBlackText"><br/><br/><b><xsl:value-of select="@label"/>: </b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></a></span>
    </xsl:template>
    <xsl:template match="AB2">
      <xsl:text>&#xD;&#xA;</xsl:text>
      <span CLASS="MedBlackText"><a CLASS="MedBlackText"><br/><br/><b>Abstract: </b><xsl:text> </xsl:text><xsl:value-of select="ibfab:getHTML(hlight:addMarkupCheckTagging(.))" disable-output-escaping="yes"/></a></span>
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
      <td valign="top" align="middle"><a href="javascript:window.open('/fig/{@img}','newwind','width=650,height=600,scrollbars=yes,resizable,statusbar=yes');void('');"><img src="/fig/{@img}" alt="{$cap}" border="1" width="100" height="100"/></a>
      <br/><a class="SmBlackText"><xsl:value-of select="$cap" disable-output-escaping="yes"/></a></td><td valign="top" width="15"></td>
    </xsl:template>

    <xsl:template match="NR">
      <a CLASS="MedBlackText"><xsl:text> (</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> refs.)</xsl:text></a>
    </xsl:template>
    <xsl:template match="AT">
      <a CLASS="MedBlackText"><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/></a>
    </xsl:template>

    <xsl:template match="CVS|MJSM|CRM|CMS|CPO">
    	<xsl:param name="DBNAME"/>
      <br/><br/><a CLASS="MedBlackText"><b>
        <xsl:choose>
          <xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
          <xsl:otherwise><xsl:text>&#xD;&#xA;</xsl:text><xsl:value-of select="../PVD"/> controlled terms</xsl:otherwise>
        </xsl:choose>:&#160;&#160;</b></a>
      <xsl:apply-templates/>
    </xsl:template>


    <xsl:template match="CR">

            <xsl:call-template name="LINK">
           <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
           <xsl:with-param name="FIELD">CR</xsl:with-param>

       </xsl:call-template>
       <!--
            <span CLASS="MedBlackText"><xsl:value-of select="normalize-space(text())"/></span>
           <img src="/engresources/images/plus.gif" border="0"/>
        -->
           <xsl:text> </xsl:text><span CLASS="SmBlackText">
           <xsl:value-of select="crlkup:getName(normalize-space(text()))" disable-output-escaping="yes"/></span>
           <xsl:if test="position()!=last()">
           <a class="SmBlkText">&#160; - &#160;</a>
           </xsl:if>

  </xsl:template>
<!--  end  -->

    <xsl:template match="FLS">
      <xsl:choose>
        <xsl:when test="string(@label)">
          <a CLASS="MedBlackText"><br/><br/><b><xsl:value-of select="@label"/>:&#160;&#160;</b></a>
        </xsl:when>
        <xsl:otherwise>
          <a CLASS="MedBlackText"><br/><br/><b>Species term:&#160;&#160;</b></a>
        </xsl:otherwise>
      </xsl:choose>
  	<xsl:apply-templates/>
    </xsl:template>


    <xsl:template match="RGIS">
	<a CLASS="MedBlackText"><br/><br/><b>Regional term: &#160;&#160;</b></a>
      	<xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="CLS">
	<a CLASS="MedBlackText"><br/><br/><b>Classification Code: &#160;&#160;</b></a>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="DBNAME">
      <br/><a CLASS="SmBlackText"><b>Database:</b><xsl:text> </xsl:text><xsl:value-of select="."/></a>
    </xsl:template>

    <xsl:template match="STT">
      <xsl:text>&#xD;&#xA;</xsl:text><br/>
      <a CLASS="SmBlackText"><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/></a>
    </xsl:template>

    <xsl:template match="PF">
      <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/><xsl:text>)</xsl:text>
    </xsl:template>

    <xsl:template match="AFF">
      <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="AF">
      <span CLASS="SmBlackText"><xsl:text> (</xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/><xsl:text>)</xsl:text></span>
    </xsl:template>

      <xsl:template match="AU|ED|IV">
      <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>

      <!-- If the name does not contain the text Anon -->
      <xsl:if test="boolean(not(contains($NAME,'Anon')))">
        <xsl:call-template name="LINK">
          <xsl:with-param name="TERM"><xsl:value-of select="$NAME"/></xsl:with-param>
          <xsl:with-param name="FIELD">AU</xsl:with-param>
        </xsl:call-template>
        <xsl:if test="position()=1 and (position()=last())">
          <xsl:if test="name(.)='ED'">
            <a CLASS="SmBlackText"><xsl:text>, ed.</xsl:text></a>
          </xsl:if>
        </xsl:if>
        <xsl:apply-templates select="AF"/>
      </xsl:if>
      <!-- If the name contains Anon text -->
      <xsl:if test="boolean(contains($NAME,'Anon'))"><A CLASS="SmBlackText"><xsl:value-of select="$NAME"/></A></xsl:if>
      <xsl:if test="not(position()=last())"><A CLASS="SmBlackText">;</A><xsl:text> </xsl:text></xsl:if>
      <xsl:if test="position()=last()">
        <xsl:if test="name(.)='ED' and not(position()=1)">
          <a CLASS="SmBlackText"><xsl:text> eds.</xsl:text></a>
        </xsl:if>
        <xsl:text> </xsl:text>
      </xsl:if>
    </xsl:template>



    <xsl:template match="CM|CP">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <A CLASS="SmBlackText">&#160; - &#160;</A>
      </xsl:if>
    </xsl:template>

    <xsl:template match="GC">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">CL</xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <A CLASS="SmBlackText">&#160; - &#160;</A>
      </xsl:if>
    </xsl:template>


    <xsl:template match="CV|MH">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">CV</xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <A CLASS="SmBlackText">&#160; - &#160;</A>
      </xsl:if>
    </xsl:template>

    <xsl:template match="MJS">
      <xsl:call-template name="LINK">
        <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
        <xsl:with-param name="FIELD">CVM</xsl:with-param>
        <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      </xsl:call-template>
      <xsl:if test="not(position()=last())">
        <A CLASS="SmBlackText">&#160; - &#160;</A>
      </xsl:if>
    </xsl:template>

    <xsl:template match="FL">
          <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD">FL</xsl:with-param>
            <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
          </xsl:call-template>
          <xsl:if test="not(position()=last())">
            <A CLASS="SmBlackText">&#160; - &#160;</A>
          </xsl:if>
    </xsl:template>

    <xsl:template match="RGI">
    	<xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD">RGI</xsl:with-param>
            <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
      	</xsl:call-template>
       	<xsl:if test="not(position()=last())">
            <A CLASS="SmBlackText">&#160; - &#160;</A>
       	</xsl:if>
    </xsl:template>

    <xsl:template match="CL">
           <xsl:apply-templates/>
           <xsl:if test="not(position()=last())">
	   	<A CLASS="SmBlackText">&#160; - &#160;</A>
      	   </xsl:if>
    </xsl:template>

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

      <A CLASS="SpLink">
      <xsl:if test="string($CLASS)">
        <xsl:attribute name="CLASS"><xsl:value-of select="$CLASS"/></xsl:attribute>
      </xsl:if>
        <xsl:if test="not(/PAGE/LINK ='false') and not(/SECTION-DELIM/LINK ='false')">
          <xsl:choose>
            <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">
              <xsl:variable name="ENCODED-SEARCH-SECTION"><xsl:value-of select="java:encode(' WN ')"/><xsl:value-of select="$SEARCH-FIELD"/></xsl:variable>
              <xsl:attribute name="href">/controller/servlet/Controller?CID=expertSearchCitationFormat&amp;searchWord1=({<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}<xsl:value-of select="$ENCODED-SEARCH-SECTION"/>)&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:when>
            <xsl:when test="($SEARCH-TYPE='Book') or ($DATABASE='131072')">
              <xsl:attribute name="href">/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;searchtype=Book&amp;searchWord1={<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}&amp;section1=<xsl:value-of select="$SEARCH-FIELD"/>&amp;database=131072<xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="href">/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of select="$ENCODED-SEARCH-TERM"/>}&amp;section1=<xsl:value-of select="$SEARCH-FIELD"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/><xsl:value-of select="$START-YEAR-PARAM"/><xsl:value-of select="$END-YEAR-PARAM"/>&amp;yearselect=<xsl:value-of select="$YEARRANGE"/><xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="string($ONMOUSEOVER)">
          <xsl:attribute name="onmouseover"><xsl:value-of select="$ONMOUSEOVER"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="$NAME='MH'"><b><xsl:value-of select="hlight:addMarkup($TERM)" disable-output-escaping="yes"/></b></xsl:if>
        <xsl:if test="not($NAME='MH')"><xsl:value-of select="hlight:addMarkup($TERM)" disable-output-escaping="yes"/></xsl:if>
      </A>

    </xsl:template>


</xsl:stylesheet>