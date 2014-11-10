<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:cal="java:java.util.GregorianCalendar"
	xmlns:str="java:org.ei.util.StringUtil"
	xmlns:htmlmanipulator="java:org.ei.util.HtmlManipulator"
	xmlns:security="java:org.ei.security.utils.SecureID"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="cal java xsl fo"
	xmlns:crlkup="java:org.ei.data.CRNLookup"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<!--
	This xsl file will display the list of abstract results  in an RTF format.
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
									<fo:block>Abstract results: <xsl:value-of select="count(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT)"/></fo:block>
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
	  <fo:block  >
	  	<xsl:if test="BTI">
	  		<fo:block font-size="12pt" space-after="1mm" font-weight="bold"><xsl:number value="position()" format="1" />. <xsl:apply-templates select="BTI" />, <xsl:apply-templates select="BPP"/></fo:block>
	  	</xsl:if>
	  	<xsl:if test="TI">
	  		<fo:block font-size="12pt" space-after="1mm" font-weight="bold"><xsl:number value="position()" format="1" />. <xsl:apply-templates select="TI"/></fo:block>
	  	</xsl:if>
        <fo:block  font-size="10pt" space-after="1mm">
<!--       		<xsl:if test="(BPP | BCT )"> -->
<!--       			<fo:block> -->
<!--       				<xsl:apply-templates select="BPP"/> -->
<!--       				<xsl:if test="DOC/DB/DBMASK='131072'"> -->
<!-- 		        		<xsl:apply-templates select="BCT"/> -->
<!-- 		      		</xsl:if> -->
<!--       			</fo:block> -->
<!--       		</xsl:if> -->
      		<xsl:apply-templates select="AUS"/>
      		<xsl:apply-templates select="EDS"/>
      		<xsl:apply-templates select="IVS"/>
      		<xsl:apply-templates select="PF"/>
      		<fo:block></fo:block>
      		<xsl:apply-templates select="SO"/>
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
			<xsl:apply-templates select="PAS"/>
      		<xsl:apply-templates select="PASM"/>
			<xsl:apply-templates select="EASM"/>
			<xsl:apply-templates select="PINFO"/>
			<xsl:apply-templates select="PAPIM"/>
			<xsl:apply-templates select="PAN"/>
			<xsl:apply-templates select="PM"/>
			<xsl:apply-templates select="PM1"/>
			<xsl:apply-templates select="PIM"/>
			<xsl:apply-templates select="UPD"/>
			<xsl:apply-templates select="KD"/>
			<xsl:apply-templates select="PAP"/>
			<xsl:apply-templates select="PFD"/>
			<xsl:apply-templates select="PPD"/>
			<xsl:apply-templates select="PIDD"/>
			<xsl:apply-templates select="COPA"/>
			<xsl:if test="not(string(SD))">
		       <xsl:apply-templates select="YR"/>
		    </xsl:if>
			<xsl:apply-templates select="PD_YR"/>
			<xsl:apply-templates select="PI"/>
			<xsl:apply-templates select="PPN"/>
			<xsl:apply-templates select="IPC"/>
			<xsl:apply-templates select="ECL"/>
			<xsl:apply-templates select="LA"/>
			<xsl:apply-templates select="SN"/>
			<xsl:variable name="ISSN">
           		<xsl:value-of select="SN"/>
      		</xsl:variable>
			<xsl:apply-templates select="E_ISSN">
				<xsl:with-param name="ISSN" select= "$ISSN" />
		    </xsl:apply-templates>
			<xsl:apply-templates select="BN"/>
			<xsl:variable name="ISBN">
      	   		<xsl:value-of select="BN"/>
      		</xsl:variable>
      		<xsl:apply-templates select="BN13">
      			<xsl:with-param name="ISBN" select= "$ISBN" />
      		</xsl:apply-templates>
      		<xsl:apply-templates select="DO"/>
			<xsl:apply-templates select="ARTICLE_NUMBER"/>
			<xsl:apply-templates select="BPC"/>
      		<xsl:apply-templates select="BYR"/>
      		<xsl:apply-templates select="BPN"/>
      		<xsl:apply-templates select="CF"/>
      		<xsl:apply-templates select="MD"/>
      		<xsl:apply-templates select="ML"/>
      		<xsl:apply-templates select="SP"/>
      		<xsl:apply-templates select="I_PN"/>
      		<xsl:apply-templates select="CPUB"/>
      		<xsl:apply-templates select="FTTJ"/>
      		<xsl:apply-templates select="DT"/>
      		<xsl:apply-templates select="AV"/>
      		<xsl:apply-templates select="SC"/>
      		<fo:block></fo:block>
      		<xsl:apply-templates select="AFS"/>
      		<xsl:apply-templates select="AB"/>
      		<xsl:apply-templates select="BAB"/>
      		<xsl:apply-templates select="AB2"/>
      		<xsl:apply-templates select="AT"/>
      		<xsl:apply-templates select="BKYS"/>
      		<xsl:apply-templates select="CPO"/>
      		<xsl:apply-templates select="CMS"/>
      		<xsl:apply-templates select="MJSM"/>
		    <xsl:apply-templates select="MH"/>
		    <xsl:apply-templates select="CVS"/>
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
      		<xsl:apply-templates select="LOCS"/>
      		<xsl:apply-templates select="TRS"/>
      		<fo:block></fo:block>
      		<fo:block>
           		<xsl:apply-templates select="DOC/DB/DBNAME"/>
      		</fo:block>
      		<xsl:choose>
				<xsl:when test="string(CPRT)"><fo:block><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(CPRT)"/></fo:block></xsl:when>
				<xsl:otherwise><fo:block><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(CPR)"/></fo:block></xsl:otherwise>
			</xsl:choose>
			<fo:block>
           		<fo:inline font-weight="bold" >Data Provider:<xsl:text> </xsl:text></fo:inline>Engineering Village<xsl:text> </xsl:text>
      		</fo:block>
			</fo:block>
		</fo:block>
		<fo:block><fo:leader/></fo:block>
	</xsl:template>
	<xsl:template match="BTI">
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
      <xsl:if test="string(../BTST)">: <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(../BTST))" disable-output-escaping="yes"/></xsl:if>
    </xsl:template>
    <xsl:template match="TI">
        <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
        <xsl:if test="../TT">
          <xsl:text> </xsl:text>(<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(../TT))" disable-output-escaping="yes"/>)
        </xsl:if>
        <xsl:if test="string(../DOC/TAGDATE)"> (Tag applied on <xsl:value-of select="../DOC/TAGDATE"/>)</xsl:if>
    </xsl:template>
    <xsl:template match="BPP">
      <xsl:if test="not(string(.)='0')"><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/><xsl:text> </xsl:text></xsl:if>
    </xsl:template>
    <xsl:template match="BCT">Chapter:<xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/></xsl:template>
    <xsl:template match="AUS|IVS">
    	<xsl:if test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2048')">
        	<fo:inline font-weight="bold" ><xsl:text>Inventor(s):</xsl:text></fo:inline><xsl:call-template name="DOUBLE_SPACER"/>
      	</xsl:if>
        <xsl:apply-templates /><xsl:text>  </xsl:text>
    </xsl:template>
     <xsl:template match="EDS">
    	<fo:block>
    		<fo:inline font-weight="bold" ><xsl:text>Editors:</xsl:text></fo:inline><xsl:call-template name="DOUBLE_SPACER"/><xsl:apply-templates />
    	</fo:block>
    </xsl:template>
    <xsl:template match="AU|ED|IV" >
    	<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
    		<xsl:if test="name()='IV'">
    			<xsl:if test="CO"><xsl:text> (</xsl:text><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(CO))" /><xsl:text>) </xsl:text></xsl:if>
    		</xsl:if>
    		<xsl:if test="AFS"><xsl:text> </xsl:text><xsl:apply-templates select="AFS"><xsl:with-param name="affiCode">true</xsl:with-param></xsl:apply-templates></xsl:if>
    		<xsl:if test="not(position()=last())">;</xsl:if>
    		<!-- <xsl:if test="(name(.)='ED' and position()=1 and (position()=last()))"><xsl:text>, ed.</xsl:text></xsl:if>
           	<xsl:if test="(name(.)='ED' and position()=last())"><xsl:text> eds.</xsl:text></xsl:if> -->
           	<xsl:text> </xsl:text>
  	</xsl:template>
    <xsl:template match="EAS">
        <xsl:variable name="NAME"><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/></xsl:variable>
        <xsl:variable name="FIELD">
        	<xsl:choose>
        		<xsl:when test="name(.)='EAS'">AF</xsl:when>
        		<xsl:otherwise>AU</xsl:otherwise>
        	</xsl:choose>
        </xsl:variable>
        <xsl:variable name="THIS-DOCUMENT-DB">
            <xsl:choose>
                <xsl:when test="(../../DOC/DB/DBMASK)='32'">1</xsl:when>
                <xsl:when test="(../../DOC/DB/DBMASK)='4096'">2</xsl:when>
                <xsl:otherwise><xsl:value-of select="../../DOC/DB/DBMASK"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="DATABASE">
            <xsl:choose>
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
        <xsl:variable name="SEARCH-TYPE">
            <xsl:value-of select="//SEARCH-TYPE"/>
        </xsl:variable>
        <xsl:variable name="DOC-ID">
	    	<xsl:value-of select="../../DOC/DOC-ID" />
    	</xsl:variable>
    	<!-- If the name does not contain the text Anon -->
        <xsl:if test="boolean(not(contains($NAME,'Anon')))">
            <xsl:choose>
	            <xsl:when test="(substring($DOC-ID,0,4) ='ref')">
	             	 <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities($NAME))" disable-output-escaping="yes"/>
	            </xsl:when>
	            <xsl:otherwise>
	            	<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities($NAME))" disable-output-escaping="yes"/>
	            </xsl:otherwise>
            </xsl:choose>
           	<xsl:if test="position()=1 and (position()=last())">
               	<xsl:if test="name(.)='ED'"><xsl:text>, ed.</xsl:text></xsl:if>
           	</xsl:if>
           	<xsl:apply-templates select="AF"/>
           	<xsl:apply-templates select="EF"/>
        </xsl:if>
        <!-- If the name contains Anon text -->
        <xsl:if test="boolean(contains($NAME,'Anon'))"><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities($NAME))"/></xsl:if>
        <xsl:if test="not(position()=last())">
        	<xsl:if test="not((position()=1 and (string(../../AFS/AF)))) ">;<xsl:text> </xsl:text></xsl:if>
        </xsl:if>
        <xsl:if test="position()=last()">
            <xsl:if test="name(.)='ED' and not(position()=1)"><xsl:text> eds.</xsl:text>
            </xsl:if>
            <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:if test="not(../DOC/DB/DBMASK='2')  and (position()=1) and (string(../../AFS/AF))" >
			<xsl:text> </xsl:text><xsl:text>(</xsl:text>
			<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(../../AFS/AF))" disable-output-escaping="yes"/>
			<xsl:text>)</xsl:text>
			<xsl:if test="not(position()=last())">
				<xsl:text>;</xsl:text>
			</xsl:if>
			<xsl:text> </xsl:text>
		</xsl:if>
	</xsl:template>
    <xsl:template match="SO">
      <fo:inline font-weight="bold" >Source:<xsl:text> </xsl:text></fo:inline><fo:inline font-style="italic"><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/></fo:inline>
    </xsl:template>
    <xsl:template match="PF">
    <xsl:text>(</xsl:text><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/><xsl:text>)</xsl:text>
    </xsl:template>
    <xsl:template match="RSP">
        <xsl:text>; </xsl:text><fo:inline font-weight="bold" >Sponsor:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="RN_LABEL">
        <xsl:text> </xsl:text><fo:inline font-weight="bold" >Report:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PN">
        <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="VO">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="p_PP">
        <xsl:text>, p </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP_pp">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> pp</xsl:text>
    </xsl:template>
   	<xsl:template match="ARN">
    	<xsl:text>, art. no. </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="RN">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SD">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PA">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MT">
        <xsl:text>, </xsl:text><fo:inline font-style="italic"><xsl:value-of select="." disable-output-escaping="yes"/></fo:inline>
    </xsl:template>
    <xsl:template match="VT">
        <xsl:text>,</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAS">
      <xsl:text>, </xsl:text><fo:inline font-weight="bold" >Assignee:</fo:inline><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PASM">
    	<xsl:choose>
      		<xsl:when test="not(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2048')">
      			<xsl:text> </xsl:text><fo:inline font-weight="bold" >Assignee:</fo:inline><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
      		</xsl:when>
    	</xsl:choose>
    </xsl:template>
    <xsl:template match="EASM">
    	 <xsl:text> </xsl:text><fo:inline font-weight="bold" >Patent assignee:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/><xsl:apply-templates />
    </xsl:template>
    <xsl:template match="PINFO">
      <fo:block></fo:block>
      <fo:inline font-weight="bold" >Patent information:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAPIM">
      <xsl:text> </xsl:text><fo:inline font-weight="bold" >Application information:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
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
    <xsl:template match="PAN">
    	<xsl:choose>
			<xsl:when test="(not($PAP) and not($PPN))">
				<fo:block><fo:inline font-weight="bold" >Application number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text></fo:block>
			</xsl:when>
			<xsl:otherwise><xsl:text> </xsl:text><fo:inline font-weight="bold" >Application number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/></xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    <xsl:template match="PM">
    	<xsl:choose>
			<xsl:when test="(not($PAP) and not($PPN) and not($PAN))">
				<fo:block><fo:inline font-weight="bold" >Publication Number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text></fo:block>
			</xsl:when>
			<xsl:otherwise><xsl:text> </xsl:text><fo:inline font-weight="bold" >Publication Number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/></xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    <xsl:template match="PM1">
         <xsl:text> </xsl:text><fo:inline font-weight="bold" >Publication Number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PIM">
         <xsl:text> </xsl:text><fo:inline font-weight="bold" >Priority information:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="UPD">
       <xsl:choose>
       		<xsl:when test="string(@label)">
      			<xsl:text> </xsl:text><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
       		</xsl:when>
       		<xsl:otherwise>
       			<xsl:text> </xsl:text><fo:inline font-weight="bold" >Publication date:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
       		</xsl:otherwise>
       </xsl:choose>
    </xsl:template>
    <xsl:template match="KD">
      <xsl:text> </xsl:text><fo:inline font-weight="bold" >Kind:<xsl:text> </xsl:text> </fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAP">
         <fo:block><fo:inline font-weight="bold" >Patent number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text></fo:block>
    </xsl:template>
    <xsl:template match="PFD">
         <fo:block><fo:inline font-weight="bold" >Filing date: </fo:inline><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text></fo:block>
    </xsl:template>
    <xsl:template match="PPD">
         <xsl:text> </xsl:text><fo:inline font-weight="bold" >Publication date:<xsl:call-template name="DOUBLE_SPACER"/></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PIDD">
         <xsl:text> </xsl:text><fo:inline font-weight="bold" >Patent issue date:<xsl:call-template name="DOUBLE_SPACER"/></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="COPA">
         <xsl:text> </xsl:text><fo:inline font-weight="bold" >Country of application:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="YR">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PD_YR">
         <xsl:text> </xsl:text><fo:inline font-weight="bold" >Publication date:<xsl:call-template name="DOUBLE_SPACER"/></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PI">
       <xsl:text> </xsl:text><fo:inline font-weight="bold" >Priority Number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PPN">
    	<xsl:choose>
			<xsl:when test="not($PAP)">
				<fo:block><fo:inline font-weight="bold" >Priority Number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text></fo:block>
			</xsl:when>
			<xsl:otherwise><xsl:text> </xsl:text><fo:inline font-weight="bold" >Priority Number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/></xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    <xsl:variable name = "IPC">
      <xsl:value-of select = "IPC"/>
    </xsl:variable>
    <xsl:template match="IPC">
      <fo:block><fo:inline font-weight="bold" >IPE Code:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/><xsl:text> </xsl:text></fo:block>
    </xsl:template>
    <xsl:template match="ECL">
    	<xsl:choose>
			<xsl:when test="not($IPC)">
				<fo:block><fo:inline font-weight="bold" >ECLA Classes:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/></fo:block>
			</xsl:when>
			<xsl:otherwise><xsl:text> </xsl:text><fo:inline font-weight="bold" >ECLA Classes:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/></xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    <xsl:template match="LA">
        <xsl:text>; </xsl:text><fo:inline font-weight="bold" >Language:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SN">
       <xsl:text>; </xsl:text><fo:inline font-weight="bold" >ISSN:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="E_ISSN">
       <xsl:param name="ISSN"/>
       <xsl:choose>
          <xsl:when test="boolean(string-length(normalize-space($ISSN))>0)">
             <xsl:text>,  </xsl:text>
          </xsl:when>
          <xsl:otherwise>
          	<xsl:text>; </xsl:text>
          </xsl:otherwise>
      </xsl:choose>
      <fo:inline font-weight="bold" >E-ISSN:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <!-- Book ISBN / ISBN13 -->
    <!-- UGLY- since BN may be present in other doctypes and we only want it for Books, check the DBMASK -->
    <xsl:template match="BN">
      <xsl:text>; </xsl:text><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="BN13">
      <xsl:param name="ISBN"/>
      <xsl:choose>
      	  <xsl:when test="boolean(string-length(normalize-space($ISBN))>0)">
      		<xsl:text>, </xsl:text>
      	  </xsl:when>
      	  <xsl:otherwise>
      	  <xsl:text>; </xsl:text>
      	  </xsl:otherwise>
      </xsl:choose>
      <fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="DO">
          <xsl:text>; </xsl:text><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="ARTICLE_NUMBER">
          <xsl:text>; </xsl:text><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="BPC">
    	<xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> p</xsl:text>
    </xsl:template>
    <xsl:template match="BYR">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="BPN">
      <xsl:text>, </xsl:text><fo:inline font-weight="bold" ><xsl:text> </xsl:text>Publisher:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
      <fo:block></fo:block>
    </xsl:template>
    <xsl:template match="CF">
      <xsl:text>; </xsl:text><fo:inline font-weight="bold" >Conference:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MD|ML">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SP">
      <xsl:text>; </xsl:text><fo:inline font-weight="bold" >Sponsor:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="I_PN">
      <xsl:text>; </xsl:text><fo:inline font-weight="bold" >Publisher:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="CPUB">
      <xsl:text>; </xsl:text><fo:inline font-weight="bold" >Country of publication:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="FTTJ">
        <fo:block><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text></fo:block>
    </xsl:template>
    <xsl:template match="DT">
        <xsl:if test="text()='Article in Press'">
             <xsl:text>, </xsl:text><xsl:text> Article in Press</xsl:text>
        </xsl:if>
        <xsl:if test="text()='In Process'">
             <xsl:text>, </xsl:text><xsl:text> In Process</xsl:text>
        </xsl:if>
    </xsl:template>
    <xsl:template match="AV|SC">
        <xsl:text>; </xsl:text><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(.)" disable-output-escaping="yes"/>
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
          <fo:block><fo:inline font-weight="bold" >Author affiliation:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/><xsl:apply-templates/></fo:block>
      </xsl:if>
    </xsl:template>
    <xsl:template match="AFID">
		<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))"/>
		<xsl:if test="not(position()=last())">,<xsl:text> </xsl:text></xsl:if>
	</xsl:template>
    <xsl:template match="AF">
     	<xsl:if test="(@id)"><xsl:if test="not(@id='0')">(<xsl:value-of select="@id"/>) </xsl:if></xsl:if><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(text()))" disable-output-escaping="yes" /><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="AB|BAB">
      <fo:block>
      	<fo:inline font-weight="bold" ><xsl:value-of select="@label"/>: <xsl:text> </xsl:text></fo:inline>
      	<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
      	<xsl:if test="string(../NR)"><xsl:text> (</xsl:text><xsl:value-of select="../NR" disable-output-escaping="yes"/><xsl:text> refs)</xsl:text><xsl:text> </xsl:text></xsl:if>
      	<xsl:text> </xsl:text>
      </fo:block>
    </xsl:template>
    <xsl:template match="AB2">
      <fo:block><fo:inline font-weight="bold" >Abstract:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/><xsl:text> </xsl:text></fo:block>
    </xsl:template>
    <xsl:template match="AT">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="BKYS">
      <fo:block></fo:block>
      <fo:block><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/>
      	<xsl:apply-templates/>
      </fo:block>
    </xsl:template>
    <xsl:template match="BKY">
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="CPO|CMS|MJSM|CVS|CRM">
    	<fo:block></fo:block>
    	<fo:block>
	        <xsl:choose>
	          <xsl:when test="@label">
	          	<fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:</fo:inline>
	          </xsl:when>
	          <xsl:otherwise>
	          	<fo:inline font-weight="bold" ><xsl:value-of select="../PVD"/>Controlled terms:</fo:inline>
	          </xsl:otherwise>
	        </xsl:choose>
	        <xsl:call-template name="DOUBLE_SPACER"/>
	      	<xsl:apply-templates/>
      	</fo:block>
    </xsl:template>
    <xsl:template match="CP|CM|MJS|CV|CR|FL">
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="CR">
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
      <xsl:text> </xsl:text><xsl:value-of select="crlkup:getName(normalize-space(text()))" disable-output-escaping="yes"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
   	<xsl:template match="MH">
      <fo:block></fo:block>
      <fo:block><fo:inline font-weight="bold" >Main heading:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/></fo:block>
    </xsl:template>
    <xsl:template match="FLS">
      <fo:block></fo:block>
      <fo:block>
	      <xsl:choose>
	        <xsl:when test="string(@label)">
	          <fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/>
	        </xsl:when>
	        <xsl:otherwise>
	         <fo:inline font-weight="bold" >Species term:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/>
	        </xsl:otherwise>
	      </xsl:choose>
	  	  <xsl:apply-templates/>
  	  </fo:block>
     </xsl:template>
 	<xsl:template match="CLS">
         <fo:block><fo:inline font-weight="bold" >Classification Code:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/><xsl:apply-templates/></fo:block>
    </xsl:template>
    <xsl:template match="CL">
      <xsl:apply-templates/>
      <xsl:if test="not(position()=last())">
 	  	<xsl:call-template name="DASH_SPACER"/>
   	  </xsl:if>
    </xsl:template>
    <xsl:template match="CID"><xsl:text> </xsl:text><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))" /></xsl:template>
    <xsl:template match="CTI"><xsl:text> </xsl:text><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))" disable-output-escaping="yes"/></xsl:template>
    <xsl:template match="AN" ><xsl:text> </xsl:text><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))"/></xsl:template>
    <xsl:template match="RGIS">
         <fo:block><fo:inline font-weight="bold" >Regional term:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/><xsl:apply-templates/><xsl:text> </xsl:text></fo:block>
    </xsl:template>
    <xsl:template match="RGI">
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(normalize-space(text())))"/>
      <xsl:if test="not(position()=last())">
       	<xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
   	<xsl:template match="PIDM8|PIDM|PUCM|PECM|PIDEPM">
    	<fo:block></fo:block>
    	<fo:block>
    		<fo:inline font-weight="bold" >
		    <xsl:choose>
		      <xsl:when test="name(.)='PIDM'">IPC Code:</xsl:when>
		      <xsl:when test="name(.)='PIDEPM'">IPC Code:</xsl:when>
		      <xsl:when test="name(.)='PIDM8'">IPC-8 Code:</xsl:when>
		      <xsl:when test="name(.)='PUCM'">US Classification:</xsl:when>
		      <xsl:when test="name(.)='PECM'">ELCA Code:</xsl:when>
		    </xsl:choose>
		    </fo:inline>
			<xsl:text> </xsl:text>
    		<xsl:apply-templates/>
    		<xsl:text> </xsl:text>
    	</fo:block>
  	</xsl:template>
  	<xsl:template match="PID|PUC|PEC|PIDEP">
  		<xsl:apply-templates select="CID" />
 	  	<xsl:if test="not(position()=last())">
			<xsl:call-template name="DASH_SPACER"/>
 	  	</xsl:if>
  	</xsl:template>
    <xsl:template match="COL">
    	<fo:block></fo:block>
        <fo:block><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:<xsl:text>  </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(text()))"/><xsl:text> </xsl:text></fo:block>
    </xsl:template>
    <xsl:template match="LOCS">
  		<fo:block></fo:block>
        <fo:block><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/><xsl:apply-templates/></fo:block>
    </xsl:template>
    <xsl:template match="LOC">
      <xsl:value-of select="@ID"/><xsl:text> </xsl:text><xsl:value-of disable-output-escaping="yes" select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))"/>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="TRS">
	  <fo:block></fo:block>
       <fo:block>
       	<xsl:if test="string(@label)"><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>:</fo:inline><xsl:call-template name="DOUBLE_SPACER"/></xsl:if>
        <xsl:choose>
		    <xsl:when test="TR/TTI">
			  <xsl:apply-templates select="TR/TTI"/>
		    </xsl:when>
		    <xsl:otherwise>
			  <xsl:apply-templates select="TR"/>
		    </xsl:otherwise>
	  	</xsl:choose>
  	   </fo:block>
    </xsl:template>
    <xsl:template match="TR|TR/TTI">
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
      <xsl:if test="not(position()=last())">
        <xsl:call-template name="DASH_SPACER"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="DBNAME">
        <fo:inline font-weight="bold">Database:</fo:inline><xsl:text> </xsl:text><xsl:value-of select="."/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="EF">
            <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>)</xsl:text>
    </xsl:template>
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
	  <xsl:text> </xsl:text>
   </xsl:template>
   <xsl:template name="DASH_SPACER">
	  <xsl:text> - </xsl:text>
   </xsl:template>
</xsl:stylesheet>