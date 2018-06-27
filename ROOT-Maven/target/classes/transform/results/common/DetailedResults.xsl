<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    xmlns:ibfab="java:org.ei.data.insback.runtime.InspecArchiveAbstract"   
    xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
    xmlns:rfx="java:org.ei.books.collections.ReferexCollection"
    xmlns:searchresult="java:org.ei.stripes.view.SearchResult"
    xmlns:abstractterm="java:org.ei.stripes.view.AbstractTerm"
    xmlns:otheraffil="java:org.ei.stripes.view.OtherAffil"
    xmlns:category="java:org.ei.stripes.view.Category"
    xmlns:abstractrecord="java:org.ei.stripes.view.AbstractRecord"
    exclude-result-prefixes="schar java hlight ibfab html xsl crlkup ctd rfx otheraffil category"
>

	<!-- Include abstract stylesheet -->
	<xsl:include href="AbstractResults.xsl"/>

	<!-- ************************************************** -->
	<!-- Templates below populate the SearchResult object   -->
	<!-- but only with info that's needed for detailed view -->
	<!-- ************************************************** -->
	
	<!-- Publisher info:                                         -->
	<!-- <PN|I_PN> - publisher - is handled in AbstractResults.xsl) -->
	<!-- <PD> - publication date - is handled in CitationResults.xsl) -->
	<xsl:template match="PL"> <!-- Country of publication -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPl($sr, text())" />
	</xsl:template>
	<xsl:template match="PLA"> <!-- Conference location -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPla($sr, text())" />
	</xsl:template>
	<xsl:template match="PF"> <!-- Conference ???/Author affiliation -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPf($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<!-- Patent info -->
	<xsl:template match="PAPX"> <!-- Application date-->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPapx($sr, text())" />
	</xsl:template>
	<xsl:template match="PAN"> <!-- Application number -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPan($sr, text())" />
	</xsl:template>
	<xsl:template match="AUTHCD"> <!-- Application number -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setAuthcd($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	<xsl:template match="DERW"> <!-- DERWENT accession no. -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setDerwent($sr, text())" />
	</xsl:template>
	<xsl:template match="DSM"> <!-- Designated states -->
		<xsl:param name="sr"/>
		<xsl:for-each select="DS">
			<xsl:value-of select="searchresult:addDesignatedstate($sr, text())" />
		</xsl:for-each>
	</xsl:template>
	
	<!-- Discipline -->
	<xsl:template match="DISPS">
		<xsl:param name="sr"/>
		<xsl:variable name="disciplines">
		    <xsl:for-each select="DISP">
		        <xsl:choose>
		            <xsl:when test="position() = 1">
		                <xsl:value-of select="normalize-space(text())"/>
		            </xsl:when>
		            <xsl:otherwise>; <xsl:value-of select="normalize-space(text())"/>
		            </xsl:otherwise>
		        </xsl:choose>
		    </xsl:for-each>
		</xsl:variable>
		<xsl:value-of select="searchresult:setDiscipline($sr, $disciplines)" />
	</xsl:template>

	<!-- Monograph title -->
	<xsl:template match="MT">
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setMt($sr, text())" />
	</xsl:template>

	<!-- ISBN (link) -->
	<xsl:template match="BN">
		<xsl:param name="sr"/>
		
		<xsl:variable name="LINK">
		<xsl:call-template name="LINK">
		  <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
		  <xsl:with-param name="FIELD">BN</xsl:with-param>
		  <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
		  <xsl:with-param name="TITLE">Search <xsl:value-of select="@label"/></xsl:with-param>
		</xsl:call-template>
		</xsl:variable>
        <!--  
		<xsl:variable name="BOOK_SUMMARY_CID"><xsl:value-of select="//CID"/></xsl:variable>
		<xsl:if test="($BOOK_SUMMARY_CID!='') and ($BOOK_SUMMARY_CID!='bookSummary')">
			<xsl:value-of select="searchresult:setIsbnlink($sr, $LINK)" />
		</xsl:if>
		-->
        <xsl:value-of select="searchresult:setIsbnlink($sr, $LINK)" />
	</xsl:template>

	<!-- ISBN13 (link) -->
	<xsl:template match="BN13">
		<xsl:param name="sr"/>

		<xsl:variable name="LINK">
		<xsl:call-template name="LINK">
		  <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
		  <xsl:with-param name="FIELD">BN</xsl:with-param>
		  <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
		  <xsl:with-param name="TITLE">Search <xsl:value-of select="@label"/></xsl:with-param>
		</xsl:call-template>
		</xsl:variable>
		<!--  
		<xsl:variable name="BOOK_SUMMARY_CID"><xsl:value-of select="//CID"/></xsl:variable>
		<xsl:if test="($BOOK_SUMMARY_CID!='') and ($BOOK_SUMMARY_CID!='bookSummary')">
			<xsl:value-of select="searchresult:setIsbn13link($sr, $LINK)" />
		</xsl:if>
		-->
        <xsl:value-of select="searchresult:setIsbn13link($sr, $LINK)" />
	</xsl:template>

	<!-- Conference code (link) -->
	<xsl:template match="CC">
		<xsl:param name="sr"/>
		
		<xsl:variable name="LINK">
		<xsl:call-template name="LINK">
		  <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
		  <xsl:with-param name="FIELD">CC</xsl:with-param>
		  <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
		  <xsl:with-param name="TITLE"><xsl:value-of select="concat('Search ',@label)"/></xsl:with-param>
		</xsl:call-template>
		</xsl:variable>
		
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
		<xsl:value-of select="searchresult:setCc($sr, $LINK)" />
	</xsl:template>

	<!-- ************************************************************ -->
	<!-- Template for inspec original controlled terms                -->
	<!-- ************************************************************ -->
	<xsl:template match="OCVS">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
	    
		<xsl:variable name="LABEL">
			<xsl:choose>
				<xsl:when test="@label">
					<xsl:value-of select="@label" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="../PVD" /> controlled terms
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="searchresult:addLabel($sr,name(),$LABEL)"></xsl:value-of>
	    <xsl:for-each select="ORGC/CV">
		    <xsl:variable name="LINK">
				<xsl:call-template name="LINK">
					<xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())" /></xsl:with-param>
					<xsl:with-param name="FIELD">CV</xsl:with-param>
					<xsl:with-param name="NAME"><xsl:value-of select="name(.)" /></xsl:with-param>
				</xsl:call-template>
			</xsl:variable>
	
			<xsl:variable name="abstractterm" select="abstractterm:new()"/>
			<xsl:value-of select="abstractterm:setValue($abstractterm,text())"/>
			<xsl:value-of select="abstractterm:setField($abstractterm,name())"/>
			<xsl:value-of select="abstractterm:setSearchlink($abstractterm,normalize-space($LINK))"/>
	
			<xsl:value-of select="abstractrecord:addTerm($abstractrecord, 'OCVS', $abstractterm)"></xsl:value-of>
		</xsl:for-each>	    
	    
		
	</xsl:template>
	
	<!-- ************************************************************ -->
	<!-- Templates for Inspec classification codes                    -->
	<!-- ************************************************************ -->
	<xsl:template match="OCLS">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
		<xsl:variable name="label">
		<xsl:choose>
			<xsl:when test="string(@label)"><xsl:value-of select="@label"/></xsl:when>
			<xsl:otherwise>Inspec classification codes</xsl:otherwise>
		</xsl:choose>
		</xsl:variable>

		<xsl:for-each select="ORGC/OC">
			<xsl:variable name="ID" select="text()"/>
			<xsl:variable name="TITLE" select="text()"/>
			<xsl:variable name="LINK">
		    <xsl:call-template name="LINK">
				<xsl:with-param name="TERM"><xsl:value-of select="normalize-space($ID)"/></xsl:with-param>
				<xsl:with-param name="FIELD">CL</xsl:with-param>
		    </xsl:call-template>
			</xsl:variable>
			
			<xsl:value-of select="abstractrecord:addInspecclassificationcode($abstractrecord, $label, $ID, $TITLE, normalize-space($LINK))"></xsl:value-of>

		</xsl:for-each>

	</xsl:template>
	
	<!-- Country of origin -->
	<xsl:template match="COS"> 
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setCos($sr, text())" />
	</xsl:template>
	
	<!-- *************************************************************** -->
	<!-- Parse the other affiliation information                         -->
	<!-- *************************************************************** -->
	<xsl:template match="OAF">
		<xsl:param name="sr"/> 
		<xsl:choose>
		   <xsl:when test="count(//OA)>0">
		       <xsl:for-each select="OA">
					<xsl:variable name="otherAffil" select="otheraffil:new()"/>
			    	<xsl:value-of select="otheraffil:setName($otherAffil,text())"/>
					<xsl:value-of select="searchresult:addOtherAffils($sr,$otherAffil)"/>
			 	</xsl:for-each>
		    </xsl:when>
			<xsl:otherwise>
		        <xsl:variable name="otherAffil" select="otheraffil:new()"/>
		    	<xsl:value-of select="otheraffil:setName($otherAffil,text())"/>
				<xsl:value-of select="searchresult:addOtherAffils($sr,$otherAffil)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- *************************************************************** -->
	<!-- Parse the category                                              -->
	<!-- *************************************************************** -->
	<xsl:template match="CAT">
		<xsl:param name="sr"/>
		<xsl:choose>
		   <xsl:when test="count(//CA)>0">
		       <xsl:for-each select="CA">
				<xsl:variable name="category" select="category:new()"/>
		    	<xsl:value-of select="category:setName($category,text())"/>
				<xsl:value-of select="searchresult:addCategory($sr,$category)"/>
			</xsl:for-each>
		    </xsl:when>
			<xsl:otherwise>
		        <xsl:variable name="category" select="category:new()"/>
		    	<xsl:value-of select="category:setName($category,text())"/>
				<xsl:value-of select="searchresult:addCategory($sr,$category)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Illustrations -->
	<xsl:template match="ILLUS"> 
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setIllus($sr, text())" />
	</xsl:template>
	
		<!-- Abstract type -->
	<xsl:template match="AT"> 
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setAt($sr, text())" />
	</xsl:template>
	
	<!-- URL -->
	<xsl:template match="GURL"> 
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setGurl($sr,text())" />
	</xsl:template>
	
	<!-- Annotation -->
	<xsl:template match="ANT"> 
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setAnt($sr,text())" />
	</xsl:template>
	
	<xsl:template match="CAC"> <!-- Author affiliation codes -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setCac($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="VI"> <!-- Journal announcement -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setVi($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="TA"> <!-- Title annotation -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setTa($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="CO"> <!-- Country of origin -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setCo($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="SU"> <!-- Notes -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setSu($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="CTS"> <!-- Contract Numbers -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setCts($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="RPGM"> <!-- Research program -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setRpgm($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="MED"> <!-- Source medium -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setMed($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="PC"> <!-- Page count -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPc($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="CAF"> <!--Corr. author affiliation -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setCaf($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="KC"> <!--Kind -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setKc($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="ATT"> <!--Attorney, Agent or Firm -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setAtt($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="FSM"> <!-- Field of search -->
		<xsl:param name="sr"/>
		<xsl:for-each select="FS">
			<xsl:value-of select="searchresult:addFieldofsearch($sr, text())" />
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="PEXM">
		<xsl:param name="sr"/>
		<xsl:variable name="LABEL"><xsl:value-of select="@label" /></xsl:variable>
		<xsl:choose>
			<xsl:when test="PEX">
				<xsl:for-each select="PEX">
					<xsl:variable name="LINK">
						<xsl:call-template name="LINK">
						  <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
						  <xsl:with-param name="FIELD">PEX</xsl:with-param>
						  <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
						  <xsl:with-param name="TITLE">Search <xsl:value-of select="$LABEL"/></xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
				   <xsl:value-of select="searchresult:addPrimaryexaminer($sr,$LINK)"/>
			   </xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
			    <xsl:value-of select="searchresult:addPrimaryexaminer($sr,text())"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="AE"> <!--Assistant examiner -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setAe($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="PANUS"> <!--Unstandardized application number -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPanus($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="CHI"> <!-- Chemical indexing -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setChi($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="TTJ"> <!-- Translation abbreviated serial title -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setTtj($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="SNT"> <!-- Translation ISSN -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setSnt($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="CNT"> <!-- Translation CODEN -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setCnt($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="CPUBT"> <!-- Translation country of publication -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setCpubt($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="NDI"> <!-- Numerical data indexing -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setNdi($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="VOLT"> <!-- Translation VOLUME -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setVolt($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

	<xsl:template match="ISST"> <!-- Translation ISSUE -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setIsst($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

	<xsl:template match="IPNT"> <!-- Translation PAGES -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setIpnt($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

	<xsl:template match="TDATE"> <!-- Translation PUB DATE -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setTdate($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

	<xsl:template match="PNUM"> <!-- NTIS Project number -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPnum($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

	<xsl:template match="TNUM"> <!-- NTIS Task number-->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setTnum($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

	<xsl:template match="AGS"> <!-- Monitoring Agency -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setAgs($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="LSTM"> <!-- Linked Terms -->
		<xsl:param name="sr"/>
		<xsl:variable name="LST">
			<xsl:value-of select="hlight:removeMarkup(.)" disable-output-escaping="yes"/>
		</xsl:variable>
		<xsl:value-of select="searchresult:setLstm($sr, $LST)" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="LTH"> <!-- Linked Terms Holder -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setLth($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="ATM"> <!-- Indexing template -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setAtm($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

	<xsl:template match="MLT"> <!-- Manually linked terms -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setMlt($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>
	
	<xsl:template match="PAPD"> <!-- Application date-->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPapd($sr, text())" />
	</xsl:template>
	
	<xsl:template match="PANS"> <!-- Application Number-->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPans($sr, text())" />
	</xsl:template>
	
	<xsl:template match="PAPCO"> <!-- Application date-->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setPapco($sr, text())" />
	</xsl:template>
	
</xsl:stylesheet>