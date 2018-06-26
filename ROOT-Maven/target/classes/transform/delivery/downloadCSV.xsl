<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns:ibfab="java:org.ei.data.insback.runtime.InspecArchiveAbstract"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  	xmlns:strutil="java:org.ei.util.StringUtil"
  	xmlns:htmlmanipulator="java:org.ei.util.HtmlManipulator"
  	xmlns:crlkup="java:org.ei.data.CRNLookup"
  	xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
  	exclude-result-prefixes="xsl strutil">

<!--
This xsl file will display the data of selected records from the  Search Results in an CSV format.
-->	
	<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
	<xsl:strip-space elements="text"/>
   	<xsl:variable name="remove">"</xsl:variable> 
   	<xsl:variable name="replaceText">'""'</xsl:variable>
   
   	<xsl:template match="BTI">
   		<xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="BTST">
   		<xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   
   	<xsl:template match="BPP">
   		<xsl:if test="not(string(.)='0')"><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/></xsl:if>
   	</xsl:template>
   
   	<xsl:template match="BCT"><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/></xsl:template>
   
   	<xsl:template match="TI"><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/></xsl:template>
   	
   	<xsl:template match="TT"><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/></xsl:template>
   
   	<xsl:template match="AUS">
   		<xsl:apply-templates />
   	</xsl:template>
   	
    <xsl:template match="EDS">
   		<xsl:apply-templates />
   	</xsl:template>
   	
   	<xsl:template match="IVS">
   		<xsl:apply-templates />
   	</xsl:template>
   	
   	<xsl:template match="AU|ED|IV" priority="1">
	 	<xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
		<xsl:if test="name()='IV'">
			<xsl:if test="CO"><xsl:text> (</xsl:text><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(CO)),$remove,$replaceText)" /><xsl:text>) </xsl:text></xsl:if>
		</xsl:if>
		<xsl:if test="AFS"><xsl:text> </xsl:text><xsl:apply-templates select="AFS"><xsl:with-param name="affiCode">true</xsl:with-param></xsl:apply-templates></xsl:if>
		<xsl:if test="not(position()=last())">;</xsl:if>
		<xsl:text> </xsl:text>
	</xsl:template>
   	
   	<xsl:template match="SO">
   		<xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="BN|BN13">
   		<xsl:value-of select="." disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="BYR|BPN">
      	<xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PF">
    	<xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="RSP">
        <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="RN_LABEL">
        <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="RN">
        <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="VO">
        <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PP">
        <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template  match="ARN">
        <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="p_PP">
        <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PP_pp"><xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/><xsl:text> pp</xsl:text></xsl:template>
   	<xsl:template match="SD">
       <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="YR" > 
    	<xsl:if test="count(preceding-sibling::*[name() = name(current())]) = 0">
    		<xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
    	</xsl:if>
    </xsl:template>
   	<xsl:template match="MT">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="VT">
       <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PD_YR">
       <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="NV">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PA">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PASM">
   		 <xsl:apply-templates />
   	</xsl:template>
   	<xsl:template match="PAS">
   	   <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
       <xsl:if test="not(position()=last())"><xsl:text>; </xsl:text></xsl:if>
    </xsl:template>
   	<xsl:template match="EASM">
       <xsl:apply-templates />
   	</xsl:template>
   	<xsl:template match="EAS">
   	   <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
       <xsl:if test="not(position()=last())"><xsl:text>; </xsl:text></xsl:if>
   	</xsl:template>
    <xsl:template match="PAN">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAP">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PIM">
   		<xsl:choose>
			<xsl:when test="PI"><xsl:apply-templates /></xsl:when>
			<xsl:otherwise><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/></xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    <xsl:template match="PI">
        <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
        <xsl:if test="not(position()=last())"> - </xsl:if>
    </xsl:template>
   	<xsl:template match="PINFO">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PM">
       <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PM1">
       <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
    <xsl:template match="UPD">
       <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="KD">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PFD">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PIDD">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="PPD">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="COPA">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="LA">
       <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="NF">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
   	<xsl:template match="AV">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
  	<xsl:template match="DT">    
       <xsl:value-of select="translate(.,$remove,$replaceText)"/>
    </xsl:template>
    <xsl:template match="DOC/DB/DBNAME">
       <xsl:value-of select="translate(.,$remove,$replaceText)"/>
    </xsl:template>
   	<xsl:template match="COL">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(text())),$remove,$replaceText)" disable-output-escaping="yes" />
    </xsl:template>
    <xsl:template match="FTTJ|STT">
       <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="AB|BAB"><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/></xsl:template>
   	<xsl:template match="AB2"><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(ibfab:getHTML(.))),$remove,$replaceText)" disable-output-escaping="yes"/></xsl:template>
   	<xsl:template match="BKYS"><xsl:apply-templates/></xsl:template>
    <xsl:template match="BKY">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template name="DASH_SPACER" >
	  <xsl:text> - </xsl:text>
  	</xsl:template>
    <xsl:template match="CVS">
      <xsl:apply-templates select="CV"/>
    </xsl:template>
    <xsl:template match="CV">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="CPRT">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CPR">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SN">   
      <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>  
    </xsl:template>
    <xsl:template match="E_ISSN">
      <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="AFS">
      <xsl:param name="affiCode">false</xsl:param>
      <xsl:if test="$affiCode='true'">
      	<xsl:variable name="firstElmVal">
	        <xsl:value-of select="AFID[1]" />
      	</xsl:variable>
      	<xsl:if test="(AFID[1] and not($firstElmVal = '0'))">(<xsl:apply-templates select="AFID" />)</xsl:if>
     </xsl:if>
     <xsl:if test="$affiCode='false'">
       <xsl:apply-templates/>
     </xsl:if>
    </xsl:template>
    <xsl:template match="AF|EF">
      <xsl:if test="(@id)"><xsl:if test="not(@id='0')">(<xsl:value-of select="@id"/>) </xsl:if></xsl:if><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)" disable-output-escaping="yes" /><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="FLS">
   	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="FL">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
      	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="DO">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="ARTICLE_NUMBER">
      <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CF">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MD">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="NR">
      <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SP">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
   	<xsl:template match="CLS">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="CL">
      <xsl:apply-templates/>
      <xsl:if test="not(position()=last())">
 	  	<xsl:call-template name="DASH_SPACER"/>
   	  </xsl:if>
    </xsl:template>
    <xsl:template match="CID"><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)" /></xsl:template>
    <xsl:template match="CTI"><xsl:text> </xsl:text><xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)" disable-output-escaping="yes"/></xsl:template>
    <xsl:template match="AN" ><xsl:value-of select="translate(.,$remove,$replaceText)"/></xsl:template>
    <xsl:template match="CAUS"><xsl:apply-templates /></xsl:template>
    <xsl:template match="CAU">
      <xsl:if test="string(text())"><xsl:value-of select="strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))" disable-output-escaping="yes"/></xsl:if><xsl:if test="EMAIL">(<xsl:value-of select="EMAIL"/>)</xsl:if><xsl:text>  </xsl:text>
    </xsl:template>
    <xsl:template match="RIL">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SE">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="ML">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CC">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MH">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="BPC">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
   	<xsl:template match="PIDM8|PIDM|PUCM|PECM|PIDEPM">
      <xsl:apply-templates/>
   	</xsl:template>
   	<xsl:template match="PID">
   	  <xsl:apply-templates select="CID" />
 	  <xsl:if test="not(position()=last())">
	  	<xsl:call-template name="DASH_SPACER"/>
 	  </xsl:if>
    </xsl:template>
   	<xsl:template match="PEC">
   	  <xsl:apply-templates select="CID" />
 	  <xsl:if test="not(position()=last())">
		<xsl:call-template name="DASH_SPACER"/>
 	  </xsl:if>
   	</xsl:template>
   	<xsl:template match="PUC">
   	  <xsl:apply-templates select="CID" />
 	  <xsl:if test="not(position()=last())">
		<xsl:call-template name="DASH_SPACER"/>
 	  </xsl:if>
   	</xsl:template>
   	<xsl:template match="PIDEP">
   	  <xsl:apply-templates select="CID" />
 	  <xsl:if test="not(position()=last())">
		<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
   	</xsl:template>
   	<xsl:template match="AUTHCD">
	  <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(text())),$remove,$replaceText)" disable-output-escaping="yes"/>
   	</xsl:template>
    <xsl:template match="DSM">
      <xsl:apply-templates />
      <xsl:call-template name="DASH_SPACER"/>
    </xsl:template>
    <xsl:template match="DS">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
      	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="PAPX|PANS">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="VOM">
      <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="IS">
      <xsl:value-of select="translate(.,$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CN">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PR">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="GURL">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PN">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="I_PN">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="AT">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CAT">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="LOCS">
      <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="LOC">
    	<xsl:if test="string(@ID)"><xsl:value-of select="@ID"/><xsl:text> - </xsl:text></xsl:if>
      	<xsl:value-of disable-output-escaping="yes" select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)"/>
	    <xsl:if test="not(position()=last())">; </xsl:if>
    </xsl:template>
	<xsl:template match="RGIS">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="RGI">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="PAPIM">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MJSM">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="MJS">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="DERW"> 
	  <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
	</xsl:template>
	<xsl:template match="CLGM">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="CLG">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="LSTM"> 
	  <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
	</xsl:template>
	<xsl:template match="CRM">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="CR">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:text> </xsl:text><xsl:value-of select="crlkup:getName(normalize-space(text()))" disable-output-escaping="yes"/>
      <xsl:if test="not(position()=last())"><xsl:call-template name="DASH_SPACER"/></xsl:if>
    </xsl:template>
    <xsl:template match="SC"> 
	  <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
	</xsl:template>
	<xsl:template match="CPO">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="CP">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="CMS">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="CM">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="ICS">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="IC">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="COS">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="DGS">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="DG">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="GCI">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="GC">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="GDI">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="GD">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="TRS">
	  <xsl:choose>
	    <xsl:when test="TR/TTI"> 			
		  <xsl:apply-templates select="TR/TTI"/>
	    </xsl:when>
	    <xsl:otherwise>	 				
		  <xsl:apply-templates select="TR"/>			
	    </xsl:otherwise>
	  </xsl:choose>   
    </xsl:template>
    <xsl:template match="TR|TR/TTI">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
      <xsl:if test="not(position()=last())">; </xsl:if>
    </xsl:template>
    <xsl:template match="SEC">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CAF">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CAC">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="VI">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CPUB|PL|PLA">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MI">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(.)),$remove,$replaceText)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="DISPS">
	  <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="DISP">
      <xsl:value-of select="translate(strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text()))),$remove,$replaceText)"/>
      <xsl:if test="not(position()=last())">; </xsl:if>
    </xsl:template>
    <xsl:template match="AFID">
		<xsl:value-of select="."/>
		<xsl:if test="not(position()=last())">,<xsl:text> </xsl:text></xsl:if>
	</xsl:template>
	<xsl:template match="FS">
    	<xsl:value-of select="strutil:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
    	<xsl:if test="not(position()=last())">
            <xsl:call-template name="DASH_SPACER"/>
        </xsl:if>
        <xsl:if test="position()=last()">
            <xsl:text> </xsl:text>
        </xsl:if>
     </xsl:template>
     <xsl:template match="FSM">
	  	<xsl:apply-templates/>
     </xsl:template>
  <xsl:template match="KC">
       	<xsl:value-of select="." /> - <xsl:value-of select="../KD" />
  </xsl:template>
</xsl:stylesheet>