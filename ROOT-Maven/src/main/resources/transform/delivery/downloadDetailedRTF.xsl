<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:pid="java:org.ei.util.GetPIDDescription"
	xmlns:cal="java:java.util.GregorianCalendar"
	xmlns:str="java:org.ei.util.StringUtil"
	xmlns:htmlmanipulator="java:org.ei.util.HtmlManipulator"
	xmlns:security="java:org.ei.security.utils.SecureID"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="cal java xsl fo"
	xmlns:ibfab="java:org.ei.data.insback.runtime.InspecArchiveAbstract"
	xmlns:crlkup="java:org.ei.data.CRNLookup"
	xmlns:ctd="java:org.ei.domain.ClassTitleDisplay"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<!--
	This xsl file will display the list of detailed results  in an RTF format.
	Author: kamaramx
	-->
	<xsl:output method="text" indent="no" encoding="iso-8859-1" />
	<xsl:preserve-space elements="text"/>
	<xsl:variable name="now" select="date:date-time()"/>

	<xsl:template match="/">
	  <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	      <fo:layout-master-set>
	        <fo:simple-page-master	master-name="simpleA4" page-height="29.7cm"
				page-width="21.0cm"	margin-top="0.5cm" margin-bottom="1cm"
				margin-left="1.2cm" margin-right="1.2cm">
				<fo:region-body
					margin-top="2cm"
					margin-bottom="1cm"/>
				<fo:region-before
					extent="0cm"/>
				<fo:region-after
					extent="0.5cm"/>
			</fo:simple-page-master>
	      </fo:layout-master-set>
	      <fo:page-sequence master-reference="simpleA4">
	        	<fo:static-content flow-name="xsl-region-before" >
		        	<fo:table color="#424242" table-layout="fixed" width="100%" font-size="8pt">
						<fo:table-column column-width="proportional-column-width(30)"/>
						<fo:table-column column-width="proportional-column-width(35)"/>
						<fo:table-column column-width="proportional-column-width(20)"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell text-align="left" display-align="top">
									<fo:block>
										<fo:external-graphic content-height="scale-to-fit" height="1cm"  content-width="5cm" scaling="non-uniform" src="url(static/images/EV-logo.gif)" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center">
									<fo:block space-before="3mm"/>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" >
									<fo:block>
										<fo:basic-link external-destination="http://www.engineeringvillage.com">www.engineeringvillage.com</fo:basic-link>
									</fo:block>
									<fo:block>Detailed results: <xsl:value-of select="count(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT)"/></fo:block>
									<fo:block>Downloaded: <xsl:value-of select="concat(date:month-in-year($now),'/',date:day-in-month($now),'/',date:year($now))"/></fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>
				<fo:static-content	flow-name="xsl-region-after">
					<fo:block color="#848484"  font-size="8pt" font-style="italic" text-align-last="justify">
						<fo:inline>Content provided by Engineering Village.  Copyright <xsl:value-of select="date:year($now)"/></fo:inline>
						<fo:leader/>
						<fo:inline>Page <fo:page-number/> of <fo:page-number-citation format="1" ref-id="last-page"/></fo:inline>
					</fo:block>
				</fo:static-content>
	        	<fo:flow flow-name="xsl-region-body">
	        		<xsl:apply-templates select="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT" />
					<fo:block id="last-page"/>
	        	</fo:flow>
	       </fo:page-sequence>
	  </fo:root>
	</xsl:template>
	<xsl:template match="PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT" >
		 <fo:block font-size="10pt" space-after="1mm">
		 	<xsl:if test="TI">
		 		<fo:block font-size="12pt" space-after="1mm"  font-weight="bold"><xsl:number value="position()" format="1" />. <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(TI))" disable-output-escaping="yes"/></fo:block>
	  		</xsl:if>
	  		<xsl:if test="BTI">
		 		<fo:block font-size="12pt" space-after="1mm"  font-weight="bold"><xsl:number value="position()" format="1" />. <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(BTI))" disable-output-escaping="yes"/></fo:block>
	  		</xsl:if>
		 	<xsl:apply-templates />
		 	<xsl:if test="DOC/DB/DBNAME">
				<fo:block><fo:inline font-weight="bold">Database:<xsl:text> </xsl:text></fo:inline><fo:inline><xsl:value-of select="DOC/DB/DBNAME" disable-output-escaping="yes"/></fo:inline></fo:block>
        	</xsl:if>
			<xsl:choose>
				<xsl:when test="string(CPRT)"><fo:block><xsl:value-of select="str:replaceString(CPRT,'2011',cal:get(cal:getInstance(), 1))"/></fo:block></xsl:when>
				<xsl:otherwise><fo:block><xsl:value-of select="CPR"/></fo:block></xsl:otherwise>
			</xsl:choose>
		 	<fo:block>
           		<fo:inline font-weight="bold" >Data Provider:<xsl:text> </xsl:text></fo:inline>Engineering Village<xsl:text> </xsl:text>
      		</fo:block>
		 	<fo:block><fo:leader/></fo:block>
		 </fo:block>

	</xsl:template>

	<xsl:template match="NO_SO"/>

	<xsl:template match="CA">
        <xsl:apply-templates />
        <xsl:if test="not(position()=last())"> - </xsl:if>
    </xsl:template>

   <xsl:template match="AN" >
     <xsl:if test="string(@label)">
		<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
	 </xsl:if>
		<xsl:value-of select="."/><xsl:text></xsl:text>
   </xsl:template>
    <xsl:template match="YR" >
    	<xsl:if test="count(preceding-sibling::*[name() = name(current())]) = 0">
    		<xsl:if test="string(@label)">
				<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
	 		</xsl:if>
			<xsl:value-of select="."/><xsl:text></xsl:text>
    	</xsl:if>
    </xsl:template>
    <xsl:template match="OA|P|RS|RPG">
        <xsl:apply-templates />
        <xsl:if test="not(position()=last())">; </xsl:if>
    </xsl:template>

	<xsl:template match="L|A|D|R|GUR">
        <xsl:apply-templates />
        <xsl:if test="not(position()=last())">, </xsl:if>
    </xsl:template>

	<xsl:template match="MBN">
	  <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))" disable-output-escaping="yes"/>
      <xsl:if test="not(position()=last())">, </xsl:if>
    </xsl:template>

    <xsl:template match="CRDN">
      <fo:block>
            <xsl:if test="string(@label)">
				<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
            </xsl:if>
       </fo:block>
    </xsl:template>
	<xsl:template match="LOC[@ID]">
      <fo:inline>
      	<xsl:value-of select="@ID"/> - <xsl:value-of disable-output-escaping="yes" select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))"/>
      </fo:inline>
    </xsl:template>
    <xsl:template match="LOC">
      	<fo:inline><xsl:value-of disable-output-escaping="yes" select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))"/></fo:inline>
    </xsl:template>

	<!-- top level elements with labels and nested value children -->
    <xsl:template match="MBNS|LA|SRCNT|SUM|MED|EDI|RPGM|DEG|UNV|HOLD|AUD|CAT|OAF|ANT|MPS|MPT|ILLUS|DGS|SC|DT|MJSM|CRM|CLGM|PIDEPM|BKYS|AGS|AUS|EDS|IVS|CLS|FLS|LLS|CVS|RGIS|DISPS|CTS|OCVS|OCLS|NDI|CHI|AOI|EFS|PASM|PEXM|PIM|PAPIM">
        <fo:block>
            <xsl:if test="string(@label)">
				<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
            </xsl:if>
           	<xsl:apply-templates />
       </fo:block>
    </xsl:template>

    <xsl:template match="AV">
        <fo:block>
            <xsl:if test="string(@label)">
				<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><fo:inline><xsl:value-of select="str:stripHtml(.)" disable-output-escaping="yes"/></fo:inline>
            </xsl:if>
       </fo:block>
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
          <fo:block>
           <xsl:if test="string(@label)">
			<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
           </xsl:if>
           <xsl:apply-templates />
       	</fo:block>
      </xsl:if>
    </xsl:template>

	<xsl:template match="PI|PAPI">
        <xsl:apply-templates />
        <xsl:if test="not(position()=last())"> - </xsl:if>
    </xsl:template>
	<xsl:template match="CAUS">
        <xsl:choose>
	    	<xsl:when test="CAU/EMAIL">
			<fo:block>
			    <fo:inline font-weight="bold">Corresponding author:<xsl:text> </xsl:text></fo:inline><xsl:apply-templates />
			</fo:block>

	        </xsl:when>
	        <xsl:otherwise>
				<xsl:if test="CAU">
					<fo:block>
					    <fo:inline font-weight="bold">Corresponding author:<xsl:text> </xsl:text></fo:inline><xsl:apply-templates />
					</fo:block>

			    </xsl:if>
	        </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="CAU">
       	<xsl:if test="string(text())">
       		<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))" disable-output-escaping="yes"/>
       		</xsl:if><xsl:if test="EMAIL">(<xsl:value-of select="EMAIL"/>)</xsl:if>
   </xsl:template>
    <xsl:template match="TI">

    </xsl:template>
    <xsl:template match="BTI">

    </xsl:template>
    <xsl:template match="TT">
      <fo:block>
       	<fo:inline  font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
      </fo:block>
    </xsl:template>
    <xsl:template match="PAN|PAPD|PAPX|PANS|PANUS|PAPCO|PM|PM1|PA|VT">
      <fo:block>
        <fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
      </fo:block>

    </xsl:template>
    <xsl:template match="BN|SN|BN13|CN|CC|MH|MI|PNUM|E_ISSN">
      <fo:block>
         <fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
      </fo:block>

    </xsl:template>
    <xsl:template match="DG|MJS|BKY|FL|CV|AG|CT|OC|PS|RGI|CM|IC|GC|GD|CP|CE|LL">
      <xsl:if test="name()='LST'">
	      <xsl:if test="position()=1">
	      	<xsl:text>  </xsl:text>
	      </xsl:if>
      </xsl:if>
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))" disable-output-escaping="yes"/>
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
      <xsl:if test="not(position()=last())"><xsl:value-of select="$SEPARATOR"/></xsl:if>
    </xsl:template>
    <xsl:template match="CVN|CVP|CVA|CVMN|CVMP|CVMA">
      	<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/><xsl:value-of select="crlkup:getName(normalize-space(text()))" disable-output-escaping="yes"/>
      	<xsl:if test="not(position()=last())">
        	<xsl:call-template name="DASH_SPACER"/>
      	</xsl:if>
    </xsl:template>

    <xsl:template match="C_SUBJECT">
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
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
      <xsl:if test="not(position()=last())"><xsl:value-of select="$SEPARATOR"/></xsl:if>
    </xsl:template>

   <xsl:template match="PID|PUC|PEC" priority="1">
		<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(./CID))" />
    	<xsl:if test="not(position()=last())"><xsl:call-template name="DASH_SPACER"/></xsl:if>
   </xsl:template>

    <xsl:template match="FS">
        <xsl:text> </xsl:text><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/><xsl:text> </xsl:text>
        <xsl:if test="not(position()=last())">
            <xsl:call-template name="DASH_SPACER"/>
        </xsl:if>
        <xsl:if test="position()=last()">
            <xsl:text> </xsl:text>
        </xsl:if>
     </xsl:template>

    <xsl:template match="AU|ED|IV" priority="1">
    	<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
    		<xsl:if test="name()='IV'">
    			<xsl:if test="CO"><xsl:text> (</xsl:text><xsl:value-of select="CO" /><xsl:text>) </xsl:text></xsl:if>
    		</xsl:if>
    		<xsl:if test="AFS"><xsl:text> </xsl:text><xsl:apply-templates select="AFS"><xsl:with-param name="affiCode">true</xsl:with-param></xsl:apply-templates></xsl:if>
    		<xsl:if test="not(position()=last())">;</xsl:if>
    		<xsl:text> </xsl:text>
  	</xsl:template>

    <xsl:template match="AFID">
		<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))"/>
		<xsl:if test="not(position()=last())">,<xsl:text> </xsl:text></xsl:if>
	</xsl:template>

    <xsl:template match="PAS">
        <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
        <xsl:if test="(@label)"><xsl:value-of select="@label"/><xsl:text> </xsl:text></xsl:if>
        <xsl:if test="not(position()=last())">; </xsl:if>
    </xsl:template>

    <xsl:template match="PEX">
        <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
        <xsl:if test="(@label)"><xsl:value-of select="@label"/><xsl:text> </xsl:text></xsl:if>
        <xsl:if test="not(position()=last())">; </xsl:if>
    </xsl:template>

    <xsl:template match="CL">
      <xsl:apply-templates />
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>

    <xsl:template match="DISP|ND|CI|AI|TTI|TR">
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
      <xsl:if test="not(position()=last())">; </xsl:if>
    </xsl:template>

    <xsl:template match="TRS">
       <fo:block>
          <xsl:if test="string(@label)">
          	<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
          </xsl:if>
          <xsl:choose>
				<xsl:when test="TR/TTI"><xsl:apply-templates select="TR/TTI"/></xsl:when>
              <xsl:otherwise><xsl:apply-templates select="TR"/></xsl:otherwise>
          </xsl:choose>
       </fo:block>

    </xsl:template>

    <xsl:template match="AF|EF" >
    	<xsl:if test="(@id)">(<xsl:value-of select="@id"/>)<xsl:text> </xsl:text></xsl:if>
    	<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))" disable-output-escaping="yes"/>
    	<xsl:if test="not(position()=last())">;</xsl:if>
    	<xsl:text> </xsl:text>
  	</xsl:template>

    <xsl:template match="CPAGE">
        <xsl:apply-templates />
    </xsl:template>

	<xsl:template match="CID|OPT">
       <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
   </xsl:template>

   <xsl:template match="CTI">
    	<xsl:text> </xsl:text><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"  disable-output-escaping="yes" />
   </xsl:template>
   <xsl:template match="ABS" >
  	  <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(ibfab:getPLAIN(.)))" disable-output-escaping="yes"/>
   </xsl:template>
   <xsl:template match="ABS2">
     <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(ibfab:getHTML(.)))" disable-output-escaping="yes"/>
   </xsl:template>

   <xsl:template match="IMGGRP">
       <xsl:variable name="cap" select= "text()" />
       <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities($cap))" disable-output-escaping="yes"/>
   </xsl:template>
   <xsl:template match="IROW">
    <fo:block>
    	<xsl:apply-templates />
    </fo:block>
   </xsl:template>

  <xsl:template match="IMAGE">
    <xsl:variable name="cap" select= "text()" />
    <fo:block>
    	<xsl:value-of select="$cap" disable-output-escaping="yes"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="SOURCE">
      <xsl:if test="@NO"><xsl:value-of select="@NO"/><xsl:text>.</xsl:text></xsl:if><xsl:apply-templates /><xsl:if test="boolean(contains(@DOI,'10'))">, DOI:</xsl:if><xsl:value-of select="@DOI"/>
      <fo:block></fo:block>
  </xsl:template>
    <xsl:template match="ORGC">
      <xsl:apply-templates />
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>

	<xsl:template match="text()">
    	<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
    </xsl:template>



	<xsl:template match="KC">
       	<fo:block>
            <xsl:if test="string(@label)">
            	<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
            </xsl:if>
            <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" /> - <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(../KD))" />
        </fo:block>

  	</xsl:template>

	<xsl:template match="*">
       <fo:block>
          <xsl:if test="string(@label)">
             <fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
          </xsl:if>
          <xsl:apply-templates/>
       </fo:block>

  	</xsl:template>

  	<xsl:template match="MT">
       <fo:block>
          <xsl:if test="string(@label)">
             <fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/></fo:inline>
          </xsl:if>
       </fo:block>
    </xsl:template>

  	<xsl:template match="PIDM|PUCM|PECM|PIDM8">
       	<fo:block>
          <xsl:if test="string(@label)">
             <fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
          </xsl:if>
          <xsl:apply-templates/>
       </fo:block>
    </xsl:template>

  	<xsl:variable name="SEARCHID">
		<xsl:value-of select="//SEARCH-ID"/>
 	</xsl:variable>
 	<xsl:variable name="ISTAG">
 		<xsl:value-of select="/PAGE/TAG-BUBBLE/CUSTID"/>
 	</xsl:variable>
 	<xsl:variable name="SEARCH-CONTEXT">
 		<xsl:value-of select="/PAGE/SEARCH-CONTEXT"/>
 	</xsl:variable>

  	<xsl:template match="LTH|LSTM">

    </xsl:template>





    <xsl:template match="MLT">

    </xsl:template>


	<xsl:template match="ATM">

    </xsl:template>


	<xsl:template match="PIDEP">
      <xsl:variable name= "CCID">
        <xsl:value-of select="./CID" />
      </xsl:variable>
		<xsl:value-of select="$CCID"/>
        <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
        <xsl:text> </xsl:text>
        <xsl:if test="position()!=last()">
           <xsl:call-template name="DASH_SPACER"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="CLG">
		<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
        <xsl:text> </xsl:text>
        <xsl:if test="position()!=last()">
          <xsl:call-template name="DASH_SPACER"/>
        </xsl:if>
   </xsl:template>

    <xsl:template match="CR">
         <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
         <xsl:text> </xsl:text>
         <xsl:value-of select="crlkup:getName(normalize-space(text()))" disable-output-escaping="yes"/>
         <xsl:if test="position()!=last()">
           <xsl:call-template name="DASH_SPACER"/>
         </xsl:if>
    </xsl:template>

    <xsl:template match="FSM|DSM">
      <fo:block>
       	<xsl:if test="string(@label)">
           	<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
        </xsl:if>
        <xsl:apply-templates />
      </fo:block>

    </xsl:template>

    <xsl:template match="DS">
      <xsl:text> </xsl:text><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
      <xsl:if test="position()=last()"><xsl:text> </xsl:text></xsl:if>
    </xsl:template>





















	<xsl:template match="THMBS">
        <fo:block>
            <xsl:if test="string(@label)">
              <fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:apply-templates />
            </xsl:if>
       	</fo:block>

    </xsl:template>

	<!-- do not display these BOOK elements> -->
    <xsl:template match="THMB|CVR|BSPG">
    </xsl:template>

	 <xsl:template match="COL">
      <fo:block>
        <fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="text()"/>
      </fo:block>

    </xsl:template>

	<xsl:template match="BPP">
        <xsl:if test="text()!= '0'">
          <fo:block>
              <xsl:if test="string(@label)">
             	<fo:inline font-weight="bold"><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline>
              </xsl:if>
              <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))"/>
          </fo:block>

        </xsl:if>
   </xsl:template>


	<!-- do not display these elements> -->
    <xsl:template match="TCO|PVD|FT|CPRT|KD|ID|NPRCT|RCT|CCT|VIEWS|PII|CPR|DOC">
    </xsl:template>



	<xsl:template name="SPACER">
       <fo:block><fo:leader/></fo:block>
    </xsl:template>

	<xsl:template name="DASH_SPACER"> - </xsl:template>







    <xsl:template match="date">
	     <xsl:element name="date">
	         <xsl:call-template name="formatdate">
	             <xsl:with-param name="datestr" select="."/>
	        </xsl:call-template>
	     </xsl:element>
  	</xsl:template>

    <xsl:template name="formatdate">
     <xsl:param name="datestr" />
     <!-- input format ddmmyyyy -->
     <!-- output format dd/mm/yyyy -->

    <xsl:variable name="dd">
       <xsl:value-of select="substring($datestr,1,2)" />
    </xsl:variable>

    <xsl:variable name="mm">
       <xsl:value-of select="substring($datestr,3,2)" />
    </xsl:variable>

    <xsl:variable name="yyyy">
       <xsl:value-of select="substring($datestr,5,4)" />
    </xsl:variable>

    <xsl:value-of select="$dd" />
    <xsl:value-of select="'/'" />
    <xsl:value-of select="$mm" />
    <xsl:value-of select="'/'" />
    <xsl:value-of select="$yyyy" />
   </xsl:template>
   <xsl:template name="DOUBLE_SPACER" >
	  <xsl:text>  </xsl:text>
   </xsl:template>

</xsl:stylesheet>