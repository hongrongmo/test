<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:cal="java:java.util.GregorianCalendar"
	xmlns:str="java:org.ei.util.StringUtil"
	xmlns:security="java:org.ei.security.utils.SecureID"
	xmlns:htmlmanipulator="java:org.ei.util.HtmlManipulator"
	xmlns:date="http://exslt.org/dates-and-times"
	exclude-result-prefixes="cal java xsl fo"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<!--
	This xsl file will display the list of citation results  in an PDF format.
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
					margin-top="1.5cm"
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
						<fo:table-column column-width="proportional-column-width(20)"/>
						<fo:table-column column-width="proportional-column-width(45)"/>
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
									<fo:block>Citation results: <xsl:value-of select="count(PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT)"/></fo:block>
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
						<fo:inline>Page <fo:page-number/> of <fo:page-number-citation-last ref-id="last-page"/></fo:inline>
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
	  <fo:block  page-break-inside="avoid">
	  	<xsl:if test="BTI">
	  		<fo:block font-size="12pt" space-after="1mm" ><fo:inline font-weight="bold"><xsl:number value="position()" format="1" />. <xsl:apply-templates select="BTI" /></fo:inline><fo:inline><xsl:apply-templates select="BPP"/></fo:inline></fo:block>
	  	</xsl:if>
	  	<xsl:if test="TI">
	  		<fo:block font-size="12pt" space-after="1mm" font-weight="bold"><xsl:number value="position()" format="1" />. <xsl:apply-templates select="TI"/></fo:block>
	  	</xsl:if>
        <fo:block  font-size="10pt" space-after="1mm">
      		<xsl:if test="BCT">
      			<fo:block>
      				<xsl:if test="DOC/DB/DBMASK='131072'">
		        		<xsl:apply-templates select="BCT"/>
		      		</xsl:if>
      			</fo:block>
      		</xsl:if>
      		<xsl:apply-templates select="AUS"/>
      		<xsl:apply-templates select="EDS"/>
      		<xsl:apply-templates select="IVS"/>
      		<xsl:apply-templates select="SO"/>
      	    <xsl:if test="not(DOC/DB/DBMASK='131072')">
        		<xsl:apply-templates select="BN"/>
      		</xsl:if>
      		<xsl:apply-templates select="BN13"/>
      		<xsl:apply-templates select="BPN"/>
      		<xsl:apply-templates select="BYR"/>
      		<xsl:apply-templates select="PF"/>
      		<xsl:apply-templates select="RSP"/>
      		<xsl:apply-templates select="RN_LABEL"/>
      		<xsl:apply-templates select="RN"/>
      		<xsl:apply-templates select="VO"/>
			<xsl:apply-templates select="PP"/>
			<xsl:apply-templates select="ARN"/>
			<xsl:apply-templates select="p_PP"/>
			<xsl:apply-templates select="PP_pp"/>
      		<xsl:apply-templates select="SD"/>
		    <xsl:apply-templates select="MT"/>
      		<xsl:apply-templates select="VT"/>
		    <xsl:if test="not(string(SD))">
		       <xsl:apply-templates select="YR"/>
		    </xsl:if>
	      	<xsl:apply-templates select="PD_YR"/>
	      	<xsl:apply-templates select="NV"/>
	      	<xsl:apply-templates select="PA"/>
      		<xsl:apply-templates select="PAS"/>
      		<xsl:apply-templates select="PASM"/>
      		<xsl:apply-templates select="EASM"/>
      		<xsl:apply-templates select="PAN"/>
      		<xsl:apply-templates select="PAP"/>
      		<xsl:apply-templates select="PIM"/>
      		<xsl:apply-templates select="PINFO"/>
      		<xsl:apply-templates select="PM"/>
      		<xsl:apply-templates select="PM1"/>
      		<xsl:apply-templates select="UPD"/>
      		<xsl:apply-templates select="KD"/>
          	<xsl:apply-templates select="PFD"/>
      		<xsl:apply-templates select="PIDD"/>
      		<xsl:apply-templates select="PPD"/>
      		<xsl:apply-templates select="PID"/>
      		<xsl:apply-templates select="COPA"/>
      		<xsl:apply-templates select="LA"/>
      		<xsl:apply-templates select="NF"/>
      		<xsl:apply-templates select="AV"/>
           	<xsl:apply-templates select="DT"/>
           	<fo:block>
           		<xsl:apply-templates select="DOC/DB/DBNAME"/>
      			<xsl:apply-templates select="COL"/>
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
    <xsl:template match="TI">
        <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
        <xsl:if test="../TT">
          <xsl:text> </xsl:text>(<xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(../TT))" disable-output-escaping="yes"/>)
        </xsl:if>
        <xsl:if test="string(../DOC/TAGDATE)"> (Tag applied on <xsl:value-of select="../DOC/TAGDATE"/>)</xsl:if>
    </xsl:template>
    <xsl:template match="BTI">
      <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(.))" disable-output-escaping="yes"/>
      <xsl:if test="string(../BTST)">: <xsl:value-of select="str:stripHtml(htmlmanipulator:replaceHtmlEntities(../BTST))" disable-output-escaping="yes"/></xsl:if>
    </xsl:template>
    <xsl:template match="BCT"><fo:inline font-weight="bold" > Chapter:</fo:inline><xsl:text> </xsl:text><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/></xsl:template>
    <xsl:template match="BPP">
      <xsl:if test="not(string(.)='0')">, Page <xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text></xsl:if>
    </xsl:template>
    <xsl:template match="BYR|BPN">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <!-- Book ISBN / ISBN13 -->
    <!-- UGLY- since BN may be present in other doctypes and we only want it for Books, check the DBMASK -->
    <xsl:template match="BN|BN13">
      <xsl:if test="../DOC/DB/DBMASK='131072'"><xsl:text> </xsl:text><fo:inline font-weight="bold" ><xsl:value-of select="@label"/>: </fo:inline><xsl:value-of select="." disable-output-escaping="yes"/></xsl:if>
    </xsl:template>
    <xsl:template match="KD">
      <xsl:text> </xsl:text><fo:inline font-weight="bold" >Kind:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/>
      <xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="AUS|IVS|EDS">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="RSP">
        <xsl:text> </xsl:text><fo:inline font-weight="bold" >Sponsor:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="RN_LABEL">
        <xsl:text> </xsl:text><fo:inline font-weight="bold" >Report:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SO">
        <fo:inline font-weight="bold" > Source:</fo:inline><xsl:text> </xsl:text><fo:inline font-style="italic"><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/></fo:inline>
    </xsl:template>
    <xsl:template match="VO">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SD">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MT">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><fo:inline font-style="italic"><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/></fo:inline>
    </xsl:template>
    <xsl:template match="VT">
        <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="YR">
      <xsl:if test="../DOC/DB/DBNAME='NTIS'">
	      <xsl:if test="(string(../PF)) or (string(../RSP)) or (string(../RN_LABEL))">
	            <xsl:text>, </xsl:text>
	      </xsl:if>
      	  <xsl:value-of select="." disable-output-escaping="yes"/>
      </xsl:if>
      <xsl:if test="not(../DOC/DB/DBNAME='NTIS')">
            <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
      </xsl:if>
    </xsl:template>
    <xsl:template match="PASM">
	 <fo:inline font-weight="bold" > Assignee:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="EASM">
    	 <fo:inline font-weight="bold" > Patent assignee: </fo:inline><xsl:apply-templates />
    </xsl:template>
    <xsl:template match="PAN">
         <fo:inline font-weight="bold" > Application number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAP">
         <fo:inline font-weight="bold" > Patent number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PIM|PINFO">
         <fo:inline font-weight="bold" > Patent information:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PM">
         <fo:inline font-weight="bold" > Publication Number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PM1">
         <fo:inline font-weight="bold" > Publication Number:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="AV">
        <fo:inline font-weight="bold" > Availability:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="str:stripHtml(.)" disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PFD">
         <fo:inline font-weight="bold" > Filing date: </fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PIDD">
         <fo:inline font-weight="bold" > Patent issue date: </fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PPD">
         <fo:inline font-weight="bold" > Publication date: </fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="UPD">
       <xsl:choose>
       		<xsl:when test="not(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2048')">
       			<fo:inline font-weight="bold" > Publication date: </fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    		</xsl:when>
       		<xsl:otherwise>
       		    <fo:inline font-weight="bold" > Publication year: </fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    		</xsl:otherwise>
       </xsl:choose>
    </xsl:template>
    <xsl:template match="PID">
         <fo:inline font-weight="bold" > Patent issue date:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="COPA">
         <fo:inline font-weight="bold" > Country of application:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PD_YR">
         <fo:inline font-weight="bold" > Publication date: </fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="NV">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <!-- publisher will ONLY show if there is no other leading information
        so this will follow 'Source' label - no comma needed                -->
    <xsl:template match="PN">
        <xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="RN">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PA">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="ARN">
    	<xsl:text>, art. no. </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="p_PP">
        <xsl:text>, p </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP_pp">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> pp</xsl:text>
    </xsl:template>
    <xsl:template match="LA">
        <fo:inline font-weight="bold" > Language:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="NF">
      <xsl:text> </xsl:text><fo:inline font-weight="bold" >Figures:<xsl:text> </xsl:text></fo:inline><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="DT">
        <xsl:if test="text()='Article in Press'">
             <xsl:text> Article in Press</xsl:text>
        </xsl:if>
        <xsl:if test="text()='In Process'">
             <xsl:text> In Process</xsl:text>
        </xsl:if>
    </xsl:template>
    <xsl:template match="DBNAME">
        <fo:inline font-weight="bold">Database:</fo:inline><xsl:text> </xsl:text><xsl:value-of select="."/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="COL">
        <xsl:text> </xsl:text><fo:inline font-weight="bold">Collection:</fo:inline><xsl:text> </xsl:text><xsl:value-of select="text()"/><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="PF">
    <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/><xsl:text>)</xsl:text>
    </xsl:template>
    <xsl:template match="AFF">
    	<xsl:if test="../DOC/DB/DBMASK='2'" >
      		<xsl:apply-templates/>
      	</xsl:if>
    </xsl:template>
    <xsl:template match="AF">
    	<xsl:if test="../DOC/DB/DBMASK='2'" >
            <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/><xsl:text>)</xsl:text>
        </xsl:if>
    </xsl:template>
    <xsl:template match="EF">
            <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="htmlmanipulator:replaceHtmlEntities(.)" disable-output-escaping="yes"/><xsl:text>)</xsl:text>
    </xsl:template>
    <xsl:template match="AU|ED|IV|EAS">
    	<xsl:if test="name(.)='ED' and position()=1">
               	<fo:inline font-weight="bold">Editors: </fo:inline>
         </xsl:if>
        <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
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
	             	 <xsl:value-of select="htmlmanipulator:replaceHtmlEntities($NAME)" disable-output-escaping="yes"/>
	            </xsl:when>
	            <xsl:otherwise>
	            	<xsl:value-of select="htmlmanipulator:replaceHtmlEntities($NAME)" disable-output-escaping="yes"/>
	            </xsl:otherwise>
            </xsl:choose>
           	<!-- <xsl:if test="position()=1 and (position()=last())">
               	<xsl:if test="name(.)='ED'"><xsl:text>, ed.</xsl:text></xsl:if>
           	</xsl:if> -->
           	<xsl:apply-templates select="AF"/>
           	<xsl:apply-templates select="EF"/>
        </xsl:if>
        <!-- If the name contains Anon text -->
        <xsl:if test="boolean(contains($NAME,'Anon'))"><xsl:value-of select="htmlmanipulator:replaceHtmlEntities($NAME)"/></xsl:if>
        <xsl:if test="not(position()=last())">
        	<xsl:if test="not((position()=1 and (string(../../AFS/AF)))) ">;<xsl:text> </xsl:text></xsl:if>
        </xsl:if>
        <!-- <xsl:if test="position()=last()">
            <xsl:if test="name(.)='ED' and not(position()=1)"><xsl:text> eds.</xsl:text>
            </xsl:if>
            <xsl:text> </xsl:text>
        </xsl:if> -->
        <xsl:if test="not(../DOC/DB/DBMASK='2')  and (position()=1) and (string(../../AFS/AF))" >

			<xsl:if test="not(name(.)='ED')">
				<xsl:text> </xsl:text><xsl:text>(</xsl:text>
				<xsl:value-of select="htmlmanipulator:replaceHtmlEntities(../../AFS/AF)" disable-output-escaping="yes"/>
				<xsl:text>)</xsl:text>
			</xsl:if>
			<xsl:if test="not(position()=last())">
				<xsl:text>;</xsl:text>
			</xsl:if>
			<xsl:text> </xsl:text>
		</xsl:if>
		<xsl:if test="name(.)='AU' and position()=last() and (string(../../EDS/ED))">
				<xsl:text>, </xsl:text>
		</xsl:if>
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

</xsl:stylesheet>