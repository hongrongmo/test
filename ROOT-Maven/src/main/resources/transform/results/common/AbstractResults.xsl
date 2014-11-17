<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:schar="java:org.ei.query.base.SpecialCharHandler"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    xmlns:ctd="java:org.ei.domain.ClassTitleDisplay"
    xmlns:pid="java:org.ei.util.GetPIDDescription"
    xmlns:rfx="java:org.ei.books.collections.ReferexCollection"
    xmlns:crlkup="java:org.ei.data.CRNLookup"
    xmlns:author="java:org.ei.stripes.view.Author"
    xmlns:abstractterm="java:org.ei.stripes.view.AbstractTerm"
    xmlns:searchresult="java:org.ei.stripes.view.SearchResult"
    xmlns:abstractrecord="java:org.ei.stripes.view.AbstractRecord"
    exclude-result-prefixes="hlight schar java html xsl crlkup ctd rfx"
>

	<!-- Include abstract stylesheet -->
	<xsl:include href="CitationResults.xsl"/>

	<xsl:variable name="BOOK-SORT-PARAMS">&amp;sortdir=up&amp;sort=stsort</xsl:variable>

	<!-- *************************************************************** -->
	<!-- Parse the patent assignee information                           -->
	<!-- *************************************************************** -->
     <xsl:template match="EASM">
		<xsl:param name="sr"/>
		<xsl:variable name="LABEL"><xsl:value-of select="@label" /></xsl:variable>
		<xsl:for-each select="EAS">
		<xsl:variable name="LINK">
		<xsl:call-template name="LINK">
		  <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
		  <xsl:with-param name="FIELD">PPA</xsl:with-param>
		  <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
		  <xsl:with-param name="TITLE">Search <xsl:value-of select="$LABEL"/></xsl:with-param>
		</xsl:call-template>
		</xsl:variable>
	   <xsl:value-of select="searchresult:addPatassigneelink($sr,$LINK)"/>
	   </xsl:for-each>
	</xsl:template>

	<xsl:template match="PASM">
		<xsl:param name="sr"/>
		<xsl:variable name="LABEL"><xsl:value-of select="@label" /></xsl:variable>
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
		<xsl:choose>
			<xsl:when test="PAS">
				<xsl:for-each select="PAS">
					<xsl:variable name="LINK">
						<xsl:call-template name="LINK">
						  <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
						  <xsl:with-param name="FIELD">AF</xsl:with-param>
						  <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
						  <xsl:with-param name="TITLE">Search <xsl:value-of select="$LABEL"/></xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
				   <xsl:value-of select="searchresult:addAssigneelink($sr,$LINK)"/>
			   </xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
			    <xsl:value-of select="searchresult:addAssigneelink($sr,text())"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- ************************************************************ -->
	<!-- Templates for various data pieces: DOI, Publisher, etc.      -->
	<!-- ************************************************************ -->
	<xsl:template match="DO">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
	   <xsl:value-of select="abstractrecord:setDoi($abstractrecord,text())"/>
	</xsl:template>

	<xsl:template match="SN">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
	   <xsl:value-of select="abstractrecord:setIssn($abstractrecord,text())"/>

		<xsl:variable name="LINK">
          <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
			<xsl:with-param name="TITLE">Search <xsl:value-of select="@label"/></xsl:with-param>
          </xsl:call-template>
		</xsl:variable>
	   <xsl:value-of select="abstractrecord:setIssnlink($abstractrecord,$LINK)"/>
	</xsl:template>

	<xsl:template match="CN">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
	   <xsl:value-of select="abstractrecord:setCoden($abstractrecord,text())"/>

		<xsl:variable name="LINK">
          <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
  		    <xsl:with-param name="TITLE">Search <xsl:value-of select="@label"/></xsl:with-param>
          </xsl:call-template>
		</xsl:variable>
	   <xsl:value-of select="abstractrecord:setCodenlink($abstractrecord,$LINK)"/>
	</xsl:template>

	<xsl:template match="MI">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
	   <xsl:value-of select="abstractrecord:setMi($abstractrecord,text())"/>

		<xsl:variable name="LINK">
          <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD"><xsl:value-of select="name(.)"/></xsl:with-param>
		  <xsl:with-param name="TITLE">Search <xsl:value-of select="@label"/></xsl:with-param>
          </xsl:call-template>
		</xsl:variable>
	   <xsl:value-of select="abstractrecord:setMilink($abstractrecord,$LINK)"/>
	</xsl:template>

	<xsl:template match="E_ISSN">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
	   <xsl:value-of select="abstractrecord:setEissn($abstractrecord,text())"/>

		<xsl:variable name="LINK">
          <xsl:call-template name="LINK">
            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
            <xsl:with-param name="FIELD">SN</xsl:with-param>
		  <xsl:with-param name="TITLE">Search <xsl:value-of select="@label"/></xsl:with-param>
          </xsl:call-template>
		</xsl:variable>
	   <xsl:value-of select="abstractrecord:setEissnlink($abstractrecord,$LINK)"/>
	</xsl:template>
	<!-- Conference information -->
	<xsl:template match="CF"> <!-- Conference name -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setCf($sr, hlight:addMarkup(text()))" />
	</xsl:template>
	<xsl:template match="MD"> <!-- Conference date -->
		<xsl:param name="sr"/>
        <xsl:value-of select="searchresult:setMd($sr, hlight:addMarkup(text()))" />
	</xsl:template>
	<xsl:template match="ML"> <!-- Conference location -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setMl($sr, hlight:addMarkup(text()))" />
	</xsl:template>


	<!-- ************************************************************ -->
	<!-- Templates for abstract text                                  -->
	<!-- ************************************************************ -->
	<xsl:template match="AB">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
		<xsl:value-of select="abstractrecord:setText($abstractrecord,text())"/>
		<xsl:value-of select="abstractrecord:setLabel($abstractrecord,@label)"/>
	</xsl:template>
	<xsl:template match="AB2">
		<xsl:param name="sr"/>
		<xsl:variable name="absText" >
			<xsl:value-of select="." disable-output-escaping="yes"/>
		</xsl:variable>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
	   <xsl:value-of select="abstractrecord:setV2($abstractrecord,true())"/>
	   <xsl:value-of select="abstractrecord:setText($abstractrecord,$absText)"/>
		<xsl:value-of select="abstractrecord:setLabel($abstractrecord,@label)"/>
	</xsl:template>
	<xsl:template match="BAB">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
		<xsl:value-of select="abstractrecord:setBookdescription($abstractrecord,text())"/>
		<xsl:value-of select="abstractrecord:setLabel($abstractrecord,@label)"/>
	</xsl:template>

	<!-- ************************************************************ -->
	<!-- Templates for miscellaneous label/value pair info            -->
	<!-- ************************************************************ -->
	<xsl:template match="MH">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>

		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>

		<xsl:variable name="LINK">
		<xsl:call-template name="LINK">
			<xsl:with-param name="TERM"><xsl:value-of select="text()"/></xsl:with-param>
			<xsl:with-param name="FIELD">MH</xsl:with-param>
			<xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
		  <xsl:with-param name="TITLE">Search <xsl:value-of select="@label"/></xsl:with-param>
		</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="abstractterm" select="abstractterm:new()"/>
		<xsl:value-of select="abstractterm:setValue($abstractterm,text())"/>
		<xsl:value-of select="abstractterm:setField($abstractterm,name())"/>
		<xsl:value-of select="abstractterm:setSearchlink($abstractterm,normalize-space($LINK))"/>

		<xsl:value-of select="abstractrecord:addTerm($abstractrecord, name(), $abstractterm)"></xsl:value-of>
	</xsl:template>

	<xsl:template match="LOCS">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>

		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>

		<xsl:for-each select="LOC">
			<xsl:variable name="abstractterm" select="abstractterm:new()"/>
			<xsl:choose>
				<xsl:when test="@ID"><xsl:value-of select="abstractterm:setValue($abstractterm,concat(@ID,' - ', text()))"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="abstractterm:setValue($abstractterm,text())"/></xsl:otherwise>
			</xsl:choose>

			<xsl:value-of select="abstractrecord:addTerm($abstractrecord, 'LOCS', $abstractterm)"></xsl:value-of>
		</xsl:for-each>
	</xsl:template>

	<!-- ************************************************************ -->
	<!-- Templates for controlled terms/Companies/Chemicals/
		 Chemical Acronyms/SIC Codes/CAS registry number(s)/
		 Geographical indexing /Industrial Sector Codes /
		 Industrial Sectors 										  -->
	<!-- ************************************************************ -->
	<xsl:template match="CVS|MJSM|CRM|CMS|CPO|BKYS|CES|ICS|DGS|GCI|GDI|CLGM">
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

		<xsl:choose>
			<xsl:when test="$LABEL='NTIS price code'">
				<xsl:value-of select="searchresult:addLabel($sr,'NTISP',$LABEL)"></xsl:value-of>
				<xsl:value-of select="searchresult:setNtispricecode($sr, text())" />
			</xsl:when>
			<xsl:when test="MH">
		        <xsl:value-of select="searchresult:addLabel($sr,name(),$LABEL)"></xsl:value-of>
		        <xsl:call-template name="CVSMH">
				 	<xsl:with-param name="key" select="name()"></xsl:with-param>
					<xsl:with-param name="label" select="$LABEL"></xsl:with-param>
					<xsl:with-param name="abstractrecord" select="$abstractrecord"></xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
			   <xsl:value-of select="searchresult:addLabel($sr,name(),$LABEL)"></xsl:value-of>
				<xsl:apply-templates>
					<xsl:with-param name="key" select="name()"></xsl:with-param>
					<xsl:with-param name="label" select="$LABEL"></xsl:with-param>
					<xsl:with-param name="abstractrecord" select="$abstractrecord"></xsl:with-param>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>



	</xsl:template>

	<!-- Controlled vocab terms -->
	<xsl:template match="CV|MJS|BKY|CVN|CVA|CVP|CVMP|CVMA|CVMP|CLG">
		<xsl:param name="key"/>
		<xsl:param name="label"/>
		<xsl:param name="abstractrecord"/>

		<xsl:variable name="LINK">
		<xsl:call-template name="LINK">
			<xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())" /></xsl:with-param>
			<xsl:with-param name="FIELD"><xsl:value-of select="name(.)" /></xsl:with-param>
			<xsl:with-param name="NAME"><xsl:value-of select="name(.)" /></xsl:with-param>
  		    <xsl:with-param name="TITLE">Search <xsl:value-of select="$label"/></xsl:with-param>
		</xsl:call-template>
			<xsl:if test="name()!='CV'">
				<xsl:value-of select="crlkup:getName(normalize-space(text()))" disable-output-escaping="yes" />
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="abstractterm" select="abstractterm:new()"/>
		<xsl:value-of select="abstractterm:setValue($abstractterm,text())"/>
		<xsl:value-of select="abstractterm:setField($abstractterm,name())"/>
		<xsl:value-of select="abstractterm:setSearchlink($abstractterm,normalize-space($LINK))"/>

		<xsl:value-of select="abstractrecord:addTerm($abstractrecord, $key, $abstractterm)"></xsl:value-of>

	</xsl:template>

	<!-- CAS Registry -->
	<xsl:template match="CR">
		<xsl:param name="key" />
		<xsl:param name="label"/>
		<xsl:param name="abstractrecord" />

		<xsl:variable name="LINK">
			<xsl:call-template name="LINK">
				<xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())" /></xsl:with-param>
				<xsl:with-param name="FIELD">CR</xsl:with-param>
  		        <xsl:with-param name="TITLE">Search <xsl:value-of select="$label"/></xsl:with-param>
			</xsl:call-template>
			<xsl:value-of select="crlkup:getName(normalize-space(text()))" disable-output-escaping="yes" />
		</xsl:variable>

		<xsl:variable name="abstractterm" select="abstractterm:new()"/>
		<xsl:value-of select="abstractterm:setValue($abstractterm,text())"/>
		<xsl:value-of select="abstractterm:setField($abstractterm,name())"/>
		<xsl:value-of select="abstractterm:setSearchlink($abstractterm,normalize-space($LINK))"/>

		<xsl:value-of select="abstractrecord:addTerm($abstractrecord, $key, $abstractterm)"></xsl:value-of>
	</xsl:template>

    <!-- Companies/Chemicals/Chemical Acronyms/SIC Codes/Geographical indexing
    														/Industrial Sector Codes/Industrial Sectors  -->
 	<xsl:template match="CM|CP|CE|IC|DG|GC|GD">
		<xsl:param name="key" />
		<xsl:param name="label"/>
		<xsl:param name="abstractrecord" />

		<xsl:variable name="LINK">
			<xsl:call-template name="LINK">
				<xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())" /></xsl:with-param>
				<xsl:with-param name="FIELD"><xsl:value-of select="name(.)" /></xsl:with-param>
				<xsl:with-param name="NAME"><xsl:value-of select="name(.)" /></xsl:with-param>
  		        <xsl:with-param name="TITLE">Search <xsl:value-of select="$label"/></xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="ORDER">
		<xsl:choose><xsl:when test="name()='CP'">1</xsl:when><xsl:otherwise>2</xsl:otherwise></xsl:choose>
		</xsl:variable>

		<xsl:variable name="abstractterm" select="abstractterm:new()"/>
		<xsl:value-of select="abstractterm:setValue($abstractterm,text())"/>
		<xsl:value-of select="abstractterm:setField($abstractterm,name())"/>
		<xsl:value-of select="abstractterm:setSearchlink($abstractterm,normalize-space($LINK))"/>

		<xsl:value-of select="abstractrecord:addTerm($abstractrecord, $key, $abstractterm)"></xsl:value-of>

	</xsl:template>

    <xsl:template match="RGIS">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>

		<xsl:value-of select="searchresult:addLabel($sr,name(),'Regional terms')"></xsl:value-of>

		<xsl:apply-templates>
			<xsl:with-param name="key" select="name()"></xsl:with-param>
			<xsl:with-param name="abstractrecord" select="$abstractrecord"></xsl:with-param>
		</xsl:apply-templates>
    </xsl:template>

    <xsl:template match="RGI">
		<xsl:param name="key" />
		<xsl:param name="abstractrecord" />

		<xsl:variable name="LINK">
			<xsl:call-template name="LINK">
	            <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
	            <xsl:with-param name="FIELD">RGI</xsl:with-param>
	            <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
  		        <xsl:with-param name="TITLE">Search Regional terms</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="abstractterm" select="abstractterm:new()"/>
		<xsl:value-of select="abstractterm:setValue($abstractterm,text())"/>
		<xsl:value-of select="abstractterm:setField($abstractterm,name())"/>
		<xsl:value-of select="abstractterm:setSearchlink($abstractterm,normalize-space($LINK))"/>

		<xsl:value-of select="abstractrecord:addTerm($abstractrecord, $key, $abstractterm)"></xsl:value-of>
    </xsl:template>

	<!-- ************************************************************ -->
	<!-- Templates for uncontrolled terms                             -->
	<!-- ************************************************************ -->
	<xsl:template match="FLS">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
		<xsl:variable name="LABEL">
			<xsl:choose>
			  <xsl:when test="string(@label)"><xsl:value-of select="@label"/></xsl:when>
			  <xsl:otherwise>Species term</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:value-of select="searchresult:addLabel($sr,name(),$LABEL)"></xsl:value-of>

		<xsl:apply-templates>
			<xsl:with-param name="key" select="name()"></xsl:with-param>
			<xsl:with-param name="abstractrecord" select="$abstractrecord"></xsl:with-param>
			<xsl:with-param name="label" select="$LABEL"></xsl:with-param>
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="FL">
		<xsl:param name="key"/>
		<xsl:param name="abstractrecord"/>
		<xsl:param name="label"/>

		<xsl:variable name="LINK">
		<xsl:call-template name="LINK">
			<xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())" /></xsl:with-param>
			<xsl:with-param name="FIELD">FL</xsl:with-param>
			<xsl:with-param name="NAME"><xsl:value-of select="name(.)" /></xsl:with-param>
			<xsl:with-param name="TITLE">Search <xsl:value-of select="$label"/></xsl:with-param>
		</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="abstractterm" select="abstractterm:new()"/>
		<xsl:value-of select="abstractterm:setValue($abstractterm,text())"/>
		<xsl:value-of select="abstractterm:setField($abstractterm,name())"/>
		<xsl:value-of select="abstractterm:setSearchlink($abstractterm,normalize-space($LINK))"/>

		<xsl:value-of select="abstractrecord:addTerm($abstractrecord, $key, $abstractterm)"></xsl:value-of>

	</xsl:template>

	<!-- ************************************************************ -->
	<!-- Templates for classification codes                           -->
	<!-- ************************************************************ -->
	<xsl:template match="CLS">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
		<xsl:variable name="label">
		<xsl:choose>
			<xsl:when test="string(@label)"><xsl:value-of select="@label"/></xsl:when>
			<xsl:otherwise>Classification codes</xsl:otherwise>
		</xsl:choose>
		</xsl:variable>

		<xsl:for-each select="CL/CID">
			<xsl:variable name="ID" select="text()"/>
			<xsl:variable name="TITLE" select="../CTI/text()"/>
			<xsl:variable name="LINK">
		    <xsl:call-template name="LINK">
				<xsl:with-param name="TERM"><xsl:value-of select="normalize-space($ID)"/></xsl:with-param>
				<xsl:with-param name="FIELD">CL</xsl:with-param>
				<xsl:with-param name="TITLE">Search <xsl:value-of select="$label"/></xsl:with-param>
		    </xsl:call-template>
			</xsl:variable>

			<xsl:value-of select="abstractrecord:addClassificationcode($abstractrecord, $label, $ID, $TITLE, normalize-space($LINK))"></xsl:value-of>

		</xsl:for-each>

		<xsl:for-each select="GC">
			<xsl:variable name="ID" select="normalize-space(text())"/>
			<xsl:variable name="TITLE" select="normalize-space(text())"/>
			<xsl:variable name="LINK">
			<xsl:call-template name="LINK">
				<xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())" /></xsl:with-param>
				<xsl:with-param name="FIELD">CL</xsl:with-param>
				<xsl:with-param name="NAME"><xsl:value-of select="name(.)" /></xsl:with-param>
			</xsl:call-template>
			</xsl:variable>

			<xsl:value-of select="abstractrecord:addClassificationcode($abstractrecord, $label, $TITLE, $TITLE, normalize-space($LINK))"></xsl:value-of>

		</xsl:for-each>
	</xsl:template>


	<!-- ************************************************************ -->
	<!-- Templates for IPC codes                                      -->
	<!-- ************************************************************ -->
	<xsl:template match="PIDM8|PIDM|PUCM|PECM|PIDEPM">
		<xsl:param name="sr"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>

	    <xsl:variable name="LABEL">
		    <xsl:choose>
				<xsl:when test="name(.)='PIDM'">IPC Code</xsl:when>
				<xsl:when test="name(.)='PIDM8'">IPC-8 Code</xsl:when>
				<xsl:when test="name(.)='PUCM'">US Classification</xsl:when>
				<xsl:when test="name(.)='PECM'">ELCA Code</xsl:when>
			   <xsl:otherwise><xsl:value-of select="@label"/></xsl:otherwise>

		    </xsl:choose>
	    </xsl:variable>

		<xsl:value-of select="searchresult:addLabel($sr,name(),$LABEL)"></xsl:value-of>

		<xsl:apply-templates>
			<xsl:with-param name="key" select="name()"></xsl:with-param>
			<xsl:with-param name="abstractrecord" select="$abstractrecord"></xsl:with-param>
			<xsl:with-param name="label" select="$LABEL"></xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="PID|PUC|PEC|PIDEP">
		<xsl:param name="key" />
		<xsl:param name="abstractrecord" />
		<xsl:param name="label"/>

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
        <xsl:choose>
      		<xsl:when test="not(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2')">
			  <xsl:value-of select="ctd:getDisplayTitle2(./CTI)" disable-output-escaping="yes"/>
			</xsl:when>
      		<xsl:otherwise>
        		<xsl:value-of select="pid:getDescription(./CID, ./CTI)" />
        	</xsl:otherwise>
         </xsl:choose>
		</xsl:variable>

		<xsl:variable name="MOUSEOVER">
		<xsl:if test="contains(/PAGE/CID,'Abstract') or contains(/PAGE/CID,'abstract')">
			this.T_WIDTH=450;return escape('<xsl:value-of select="$IPC-DESCRIPTION"/>')
		</xsl:if>
		</xsl:variable>
		<xsl:variable name="LINK">
			<xsl:call-template name="LINK">
				<xsl:with-param name="TERM"><xsl:value-of select="./CID"/></xsl:with-param>
				<xsl:with-param name="FIELD"><xsl:value-of select="$FIELDNAME"/></xsl:with-param>
				<xsl:with-param name="CLASS"><xsl:value-of select="$CLASS"/></xsl:with-param>
				<xsl:with-param name="ONMOUSEOVER"><xsl:value-of select="$MOUSEOVER"/></xsl:with-param>
				<xsl:with-param name="TITLE">Search <xsl:value-of select="$label"/></xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="abstractterm" select="abstractterm:new()"/>
		<xsl:value-of select="abstractterm:setValue($abstractterm,text())"/>
		<xsl:value-of select="abstractterm:setCssclass($abstractterm,$CLASS)"/>
		<xsl:value-of select="abstractterm:setField($abstractterm,$FIELDNAME)"/>
		<xsl:value-of select="abstractterm:setSearchlink($abstractterm,normalize-space($LINK))"/>
		<xsl:value-of select="abstractterm:setIpcdescription($abstractterm,$IPC-DESCRIPTION)"/>

		<xsl:value-of select="abstractrecord:addTerm($abstractrecord, $key, $abstractterm)"></xsl:value-of>

	</xsl:template>

	<!-- ************************************************************ -->
	<!-- This template creates links for the various terms that       -->
	<!-- may be present on the abstract/detailed page                 -->
	<!-- ************************************************************ -->
	<xsl:template name="LINK">

		<xsl:param name="TERM" />
		<xsl:param name="FIELD" />
		<xsl:param name="NAME" />
		<xsl:param name="ONMOUSEOVER" />
		<xsl:param name="CLASS" />
		<xsl:param name="TITLE" />

		<xsl:variable name="ENCODED-SEARCH-TERM">
			<xsl:value-of
				select="java:encode(schar:preprocess(hlight:removeMarkup($TERM)))"
				disable-output-escaping="yes" />
		</xsl:variable>

		<xsl:variable name="SEARCH-FIELD">
			<xsl:choose>
				<xsl:when test="($FIELD='BKY')">KY</xsl:when>
				<xsl:when test="($FIELD='MJS')">CVM</xsl:when>
				<xsl:when test="($FIELD='CLG')">CL</xsl:when>
				<xsl:when test="($FIELD='GC')">CL</xsl:when>
				<xsl:otherwise><xsl:value-of select="$FIELD" /></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="THIS-DOCUMENT-DB">
			<xsl:choose>
				<xsl:when test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK)='32'">1</xsl:when>
				<xsl:when test="(ancestor::EI-DOCUMENT/DOC/DB/DBMASK)='4096'">2</xsl:when>
				<xsl:otherwise><xsl:value-of select="ancestor::EI-DOCUMENT/DOC/DB/DBMASK" /></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="YEARRANGE">
			<xsl:choose>
				<xsl:when test="string(//SESSION-DATA/LASTFOURUPDATES)">
					<xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES" />
				</xsl:when>
				<xsl:otherwise>
					yearrange
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="START-YEAR-PARAM">
			<xsl:choose>
				<xsl:when test="($FIELD='AU')"></xsl:when>
				<!-- <xsl:when test="string(//SESSION-DATA/START-YEAR)">&amp;startYear=<xsl:value-of
					select="//SESSION-DATA/START-YEAR"/></xsl:when> -->
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="END-YEAR-PARAM">
			<xsl:choose>
				<xsl:when test="($FIELD='AU')"></xsl:when>
				<!-- <xsl:when test="string(//SESSION-DATA/END-YEAR)">&amp;endYear=<xsl:value-of
					select="//SESSION-DATA/END-YEAR"/></xsl:when> -->
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- the variable $SEARCH-TYPE can be empty so string($SEARCH-TYPE) can
			be FALSE -->
		<!-- when there is no search in the XML output -->
		<!-- -->
		<!-- //SEARCH-TYPE is defined in a SEARCHRESULT as the type of the current
			search -->
		<!-- //SEARCH-TYPE is defined in a SELECTEDSET by the type of the last
			search -->
		<!-- //SEARCH-TYPE is defined in a FOLDER by the type of the last search -->
		<!-- if there is no search before viewing a folder, the XML <SEARCH-TYPE>NONE</SEARCH-TYPE> -->
		<!-- is output by viewSavedRecordsOfFolder.jsp when recentXMLQuery==null -->

		<xsl:variable name="SEARCH-TYPE"><xsl:value-of select="//SEARCH-TYPE" /></xsl:variable>

		<xsl:variable name="DATABASE">
			<xsl:choose>
				<xsl:when test="(/PAGE/SEARCH-CONTEXT='tag')"><xsl:value-of select="$THIS-DOCUMENT-DB" /></xsl:when>
				<!-- View Saved Records -->
				<xsl:when test="string(/PAGE/FOLDER-ID)"><xsl:value-of select="$THIS-DOCUMENT-DB" /></xsl:when>
				<!-- View Selected Set -->
				<xsl:when test="contains(/PAGE/CID,'SelectedSet')">
					<xsl:choose>
						<!-- View Selected Set - Multiple Queries -->
						<xsl:when test="(/PAGE/HAS-MULTIPLE-QUERYS='true') or (/PAGE/DBMASK = '')"><xsl:value-of select="$THIS-DOCUMENT-DB" /></xsl:when>
						<!-- View Selected Set - Single Query -->
						<xsl:otherwise><xsl:value-of select="/PAGE/DBMASK" /></xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<!-- Search Results Records -->
				<xsl:otherwise><xsl:value-of select="/PAGE/DBMASK" /></xsl:otherwise>
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
				<xsl:when test="($SEARCH-TYPE='Book') or ($DATABASE='131072')"><xsl:value-of select="$BOOK-SORT-PARAMS" /></xsl:when>
				<xsl:otherwise>&amp;sort=yr</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		&lt;A CLASS="SpLink"
			<xsl:if test="string($CLASS)"> class="<xsl:value-of select="$CLASS" />"</xsl:if>
			<xsl:if test="string($TITLE)"> title="<xsl:value-of select="$TITLE" />" alt="<xsl:value-of select="$TITLE" />"</xsl:if>
			<xsl:if
				test="not(/PAGE/LINK ='false') and not(/SECTION-DELIM/LINK ='false')">
				<xsl:choose>
					<xsl:when
						test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">
						<xsl:variable name="ENCODED-SEARCH-SECTION">
							<xsl:value-of select="java:encode(' WN ')" />
							<xsl:value-of select="$SEARCH-FIELD" />
						</xsl:variable>
						href="/search/submit.url?CID=expertSearchCitationFormat&amp;searchWord1=({<xsl:value-of
							select="$ENCODED-SEARCH-TERM" />}<xsl:value-of
							select="$ENCODED-SEARCH-SECTION" />)&amp;database=<xsl:value-of
							select="$ENCODED-DATABASE" /><xsl:value-of select="$START-YEAR-PARAM" /><xsl:value-of
							select="$END-YEAR-PARAM" />&amp;yearselect=<xsl:value-of
							select="$YEARRANGE" />&amp;searchtype=<xsl:value-of
							select="$SEARCH-TYPE" /><xsl:value-of select="$DEFAULT-LINK-SORT" />"
					</xsl:when>
					<xsl:when test="($SEARCH-TYPE='Book') or ($DATABASE='131072')">
						<xsl:choose>
							<xsl:when test="($FIELD='COL')">href="/search/submit.url?CID=quickSearchCitationFormat&amp;searchtype=Book&amp;col=<xsl:value-of
								select="$ENCODED-SEARCH-TERM" />&amp;database=131072<xsl:value-of
								select="$START-YEAR-PARAM" /><xsl:value-of select="$END-YEAR-PARAM" />&amp;yearselect=<xsl:value-of
								select="$YEARRANGE" /><xsl:value-of select="$DEFAULT-LINK-SORT" />"</xsl:when>
							<xsl:otherwise>href="/search/submit.url?CID=quickSearchCitationFormat&amp;searchtype=Book&amp;searchWord1={<xsl:value-of
								select="$ENCODED-SEARCH-TERM" />}&amp;section1=<xsl:value-of select="$SEARCH-FIELD" />&amp;database=131072<xsl:value-of
								select="$START-YEAR-PARAM" /><xsl:value-of select="$END-YEAR-PARAM" />&amp;yearselect=<xsl:value-of
								select="$YEARRANGE" /><xsl:value-of select="$DEFAULT-LINK-SORT" />"</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						href="/search/submit.url?CID=quickSearchCitationFormat&amp;searchtype=Quick&amp;searchWord1={<xsl:value-of
							select="$ENCODED-SEARCH-TERM" />}&amp;section1=<xsl:value-of
							select="$SEARCH-FIELD" />&amp;database=<xsl:value-of
							select="$ENCODED-DATABASE" /><xsl:value-of select="$START-YEAR-PARAM" /><xsl:value-of
							select="$END-YEAR-PARAM" />&amp;yearselect=<xsl:value-of
							select="$YEARRANGE" /><xsl:value-of select="$DEFAULT-LINK-SORT" />"
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="string($ONMOUSEOVER)">onmouseover="<xsl:value-of select="$ONMOUSEOVER" />"</xsl:if>
			</xsl:if>&gt;
			<xsl:if test="$NAME='MH'">
				<xsl:value-of select="$TERM"
					disable-output-escaping="yes" />
			</xsl:if>
			<xsl:if test="not($NAME='MH')">
				<xsl:value-of select="$TERM"
					disable-output-escaping="yes" />
			</xsl:if>
		&lt;/A&gt;

	</xsl:template>


	<!-- ************************************************************ -->
    <!-- Top level elements for correspondence                        -->
	<!-- ************************************************************ -->
    <xsl:template match="CAUS">
		<xsl:param name="sr"/>
		<xsl:apply-templates select="CAU">
			<xsl:with-param name="sr" select="$sr"/>
		</xsl:apply-templates>
    </xsl:template>

    <xsl:template match="CAU">
		<xsl:param name="sr"/>

    	<xsl:variable name="cauthor" select="author:new()"/>
		<xsl:value-of select="author:setName($cauthor, text())"/>
		<xsl:value-of select="author:setEmail($cauthor, EMAIL)"/>
		<xsl:value-of select="searchresult:addCauthor($sr,$cauthor)"/>
    </xsl:template>

	<!-- ************************************************************ -->
    <!-- Top level elements for Treatments                            -->
	<!-- ************************************************************ -->
     <xsl:template match="TRS">
     		<xsl:param name="sr"/>
     	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>

			<xsl:variable name="LABEL">
			<xsl:choose>
				<xsl:when test="string(@label)"><xsl:value-of select="@label"/></xsl:when>
				<xsl:otherwise>Treatment</xsl:otherwise>
			</xsl:choose>
			</xsl:variable>
     		<xsl:value-of select="searchresult:addLabel($sr,name(),$LABEL)"></xsl:value-of>

     		<xsl:for-each select="TR">
				<xsl:choose>
				<xsl:when test="TTI">
					<xsl:value-of select="abstractrecord:addTreatment($abstractrecord,TTI)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="abstractrecord:addTreatment($abstractrecord,.)"/>
				</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
    </xsl:template>

	<!-- ************************************************************ -->
    <!-- START OF IMAGES from INSPEC BACKFILE                         -->
	<!-- ************************************************************ -->
    <xsl:template match="IMG">
		<xsl:param name="sr"/>

		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"/>
	    <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
		<xsl:for-each select="IMGGRP/IROW/IMAGE">
			<xsl:value-of select="abstractrecord:addFigure($abstractrecord, @img)"/>
		</xsl:for-each>
    </xsl:template>

    <xsl:template name="CVSMH">
     	<xsl:param name="key"/>
		<xsl:param name="label"/>
		<xsl:param name="abstractrecord"/>

        <xsl:variable name="textvalue"><xsl:value-of select="MH" /></xsl:variable>

		<xsl:variable name="LINK">
		<xsl:call-template name="LINK">
			<xsl:with-param name="TERM"><xsl:value-of select="normalize-space($textvalue)" /></xsl:with-param>
			<xsl:with-param name="FIELD">MH</xsl:with-param>
			<xsl:with-param name="NAME"><xsl:value-of select="name(.)" /></xsl:with-param>
  		    <xsl:with-param name="TITLE">Search <xsl:value-of select="$label"/></xsl:with-param>
		</xsl:call-template>
		<xsl:value-of select="crlkup:getName(normalize-space($textvalue))" disable-output-escaping="yes" />
		</xsl:variable>

		<xsl:variable name="abstractterm" select="abstractterm:new()"/>
		<xsl:value-of select="abstractterm:setValue($abstractterm,$textvalue)"/>
		<xsl:value-of select="abstractterm:setField($abstractterm,name())"/>
		<xsl:value-of select="abstractterm:setSearchlink($abstractterm,normalize-space($LINK))"/>

		<xsl:value-of select="abstractrecord:addTerm($abstractrecord, $key, $abstractterm)"></xsl:value-of>

     </xsl:template>

     <xsl:template match="ARTICLE_NUMBER"> <!--Article number -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setArticlenumber($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

	<!-- COLLECTION (link) -->
	<xsl:template match="COL">
		<xsl:param name="sr"/>

		<xsl:variable name="LINK">
		<xsl:call-template name="LINK">
		  <xsl:with-param name="TERM"><xsl:value-of select="normalize-space(text())"/></xsl:with-param>
		  <xsl:with-param name="FIELD">COL</xsl:with-param>
		  <xsl:with-param name="NAME"><xsl:value-of select="name(.)"/></xsl:with-param>
		  <xsl:with-param name="TITLE">Search <xsl:value-of select="@label"/></xsl:with-param>
		</xsl:call-template>
		</xsl:variable>

		<xsl:value-of select="searchresult:setCollection($sr, $LINK)" />
	</xsl:template>

	<xsl:template match="FTTJ"> <!-- Translation serial title -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setFttj($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

	<xsl:template match="STT"> <!--  -->
		<xsl:param name="sr"/>
		<xsl:value-of select="searchresult:setStt($sr, text())" />
		<xsl:value-of select="searchresult:addLabel($sr,name(),@label)"></xsl:value-of>
	</xsl:template>

</xsl:stylesheet>