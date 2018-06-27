<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
	exclude-result-prefixes="java html xsl DD"
>

<xsl:output method="xml" cdata-section-elements="NEXT-PAGE"/>
<xsl:output method="xml" cdata-section-elements="PREV-PAGE"/>
<xsl:output method="xml" cdata-section-elements="QUERY"/>
<xsl:output method="xml" cdata-section-elements="TITLE"/>
<xsl:output method="xml" cdata-section-elements="SOURCE"/>
<xsl:output method="xml" cdata-section-elements="PUBLISHER"/>
<xsl:output method="xml" cdata-section-elements="TRANSLATION-TITLE"/>
<xsl:output method="xml" cdata-section-elements="ABBREVIATED-SERIAL-TITLE"/>
<xsl:output method="xml" cdata-section-elements="ABSTRACT-LINK"/>
<xsl:output method="xml" cdata-section-elements="CREATOR"/>
<xsl:output method="xml" cdata-section-elements="AFFILIATION"/>
<xsl:output method="xml" cdata-section-elements="CONTRIBUTOR"/>
<xsl:output method="xml" cdata-section-elements="DETAILED-LINK"/>
<xsl:output method="xml" cdata-section-elements="SUBJECT"/>
<xsl:output method="xml" cdata-section-elements="RELATION"/>
<xsl:output method="xml" cdata-section-elements="RIGHTS"/>
<xsl:output method="xml" cdata-section-elements="DOC-DATABASE"/>
<xsl:output method="xml" cdata-section-elements="IDENTIFIER"/>
<xsl:output method="xml" cdata-section-elements="DATE"/>
<xsl:output method="xml" cdata-section-elements="LANGUAGE"/>
<xsl:output method="xml" cdata-section-elements="TIME"/>
<xsl:output method="xml" cdata-section-elements="ID"/>
<xsl:output method="xml" cdata-section-elements="NAME"/>
<xsl:output method="xml" cdata-section-elements="RESULTCOUNT"/>
<xsl:output method="xml" cdata-section-elements="CURR-PAGE"/>
<xsl:output method="xml" cdata-section-elements="PAGE-RANGE"/>
<xsl:output method="xml" cdata-section-elements="PAGE-COUNT"/>
<xsl:output method="xml" cdata-section-elements="VOLUME"/>
<xsl:output method="xml" cdata-section-elements="ISSUE"/>
<xsl:output method="xml" cdata-section-elements="ISSN"/>
<xsl:output method="xml" cdata-section-elements="ISBN"/>

<xsl:template match="PAGE">

	<xsl:variable name="SESSION-ID">
		<xsl:value-of select="SESSION-ID"/>
	</xsl:variable>

	<xsl:variable name="SERVER">
		<xsl:value-of select="SERVER"/>
	</xsl:variable>

	<xsl:variable name="EXCEPTION">
			<xsl:value-of select="EXCEPTION" />
	</xsl:variable>

	<xsl:variable name="SEARCH-ID">
		<xsl:value-of select="SEARCH-ID"/>
	</xsl:variable>

	<xsl:variable name="CURRENT-PAGE">
		<xsl:value-of select="//CURR-PAGE-ID"/>
	</xsl:variable>

	<xsl:variable name="RESULTS-COUNT">
		<xsl:value-of select="//RESULTS-COUNT"/>
	</xsl:variable>

  	<xsl:variable name="DATABASE-DISPLAYNAME">
		<xsl:value-of select="DD:getDisplayName(DBMASK)"/>
  	</xsl:variable>

	<xsl:variable name="DATABASE">
		<xsl:value-of select="DBMASK"/>
	</xsl:variable>

	<xsl:variable name="PREV-PAGE-ID">
		<xsl:value-of select="//PREV-PAGE-ID"/>
	</xsl:variable>

	<xsl:variable name="NEXT-PAGE-ID">
		<xsl:value-of select="//NEXT-PAGE-ID"/>
	</xsl:variable>

	<xsl:variable name="FORMAT">
			<xsl:value-of select="//EI-DOCUMENT/@VIEW"/>
	</xsl:variable>

	<xsl:variable name="DISPLAY-QUERY">
	    <xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/>
	  </xsl:variable>

	<xsl:variable name="LASTFOURUPDATES">
		<xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/>
	</xsl:variable>

	<PAGE>
		<xsl:apply-templates select="EI-DOCUMENT"/>
		<xsl:apply-templates select="EXCEPTION"/>
		<TIME>
			<xsl:value-of select="TIME"/>
		</TIME>
		<DATABASE>
			<ID><xsl:value-of select="$DATABASE"/></ID>
			<NAME><xsl:value-of select="$DATABASE-DISPLAYNAME"/></NAME>
		</DATABASE>
		<xsl:apply-templates select="//DISPLAY-QUERY">
			<xsl:with-param name="DISPLAY-QUERY" select="$DISPLAY-QUERY" />
			<xsl:with-param name="LASTFOURUPDATES" select="$LASTFOURUPDATES" />
		</xsl:apply-templates>

		<xsl:apply-templates select="RESULTS-COUNT"/>
		<xsl:apply-templates select="//PREV-PAGE"/>
		<xsl:apply-templates select="//NEXT-PAGE"/>

		<CURR-PAGE><xsl:value-of select="//CURR-PAGE-ID"/></CURR-PAGE>

		<PAGE-RESULTS>
			<xsl:apply-templates select="PAGE-RESULTS">
				<xsl:with-param name="SERVER" select="$SERVER" />
				<xsl:with-param name="SEARCHID" select="$SEARCH-ID" />
				<xsl:with-param name="SESSIONID" select="$SESSION-ID" />
				<xsl:with-param name="PAGEINDEX" select="$CURRENT-PAGE" />
				<xsl:with-param name="RESULTS-COUNT" select="$RESULTS-COUNT" />
			</xsl:apply-templates>
		</PAGE-RESULTS>
	</PAGE>
</xsl:template>

<xsl:template match="PAGE-ENTRY">
	<xsl:param name="SERVER" />
	<xsl:param name="SEARCHID" />
	<xsl:param name="SESSIONID" />
	<xsl:param name="PAGEINDEX" />
	<xsl:param name="RESULTS-COUNT" />
	<DOC>
		<INDEX><xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/></INDEX>
		<DOC-DATABASE><xsl:value-of select="EI-DOCUMENT/DOC/DB/DBNAME"/></DOC-DATABASE>
		<IDENTIFIER><xsl:value-of select="EI-DOCUMENT/AN"/></IDENTIFIER>
		<xsl:for-each select="EI-DOCUMENT/AUS/AU|EI-DOCUMENT/IVS/IV|EI-DOCUMENT/EDS/ED">
			<xsl:choose>
				<xsl:when test="position()=1">
					<CREATOR>
						<xsl:value-of select="./text()"/>
					</CREATOR>
				</xsl:when>
				<xsl:otherwise>
					<CONTRIBUTOR>
						<xsl:value-of select="."/>
					</CONTRIBUTOR>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>

		<xsl:for-each select="EI-DOCUMENT/AFS/AF|EI-DOCUMENT/EFS/EF">
			<AFFILIATION>
				<xsl:value-of select="./text()"/>
			</AFFILIATION>
		</xsl:for-each>

		<TITLE>
			<xsl:value-of select="EI-DOCUMENT/TI"/> <xsl:if test="EI-DOCUMENT/TT">(<xsl:value-of select="EI-DOCUMENT/TT"/>)</xsl:if>
		</TITLE>
		<xsl:choose>
			<xsl:when test="EI-DOCUMENT/DO">
				<RELATION>
					<xsl:value-of select="EI-DOCUMENT/DO"/>
				</RELATION>
			</xsl:when>
		</xsl:choose>

		<xsl:if test="EI-DOCUMENT/CPRT">
			<RIGHTS>
				<xsl:value-of select="EI-DOCUMENT/CPRT"/>
			</RIGHTS>
		</xsl:if>
		<xsl:if test="EI-DOCUMENT/SO|EI-DOCUMENT/PF|EI-DOCUMENT/PN">
			<SOURCE>
				<xsl:choose>
					<xsl:when test="EI-DOCUMENT/SO">
							<xsl:value-of select="EI-DOCUMENT/SO"/>
					</xsl:when>
					<xsl:when test="EI-DOCUMENT/PF">
							<xsl:value-of select="EI-DOCUMENT/PF"/>
					</xsl:when>
					<xsl:when test="EI-DOCUMENT/PN">
							<xsl:value-of select="EI-DOCUMENT/PN"/>
					</xsl:when>
				</xsl:choose>
			</SOURCE>
		</xsl:if>
		<xsl:if test="EI-DOCUMENT/IS">
			<ISSUE><xsl:value-of select="EI-DOCUMENT/IS"/></ISSUE>
		</xsl:if>
		<xsl:if test="EI-DOCUMENT/VOM">
			<VOLUME><xsl:value-of select="EI-DOCUMENT/VOM"/></VOLUME>
		</xsl:if>

		<xsl:if test="EI-DOCUMENT/PP">
			<PAGE-RANGE><xsl:value-of select="EI-DOCUMENT/PP" /></PAGE-RANGE>
		</xsl:if>
		<xsl:if test="EI-DOCUMENT/PC">
			<PAGE-COUNT><xsl:value-of select="EI-DOCUMENT/PC" /></PAGE-COUNT>
		</xsl:if>

		<xsl:if test="EI-DOCUMENT/SN">
			<ISSN><xsl:value-of select="EI-DOCUMENT/SN"/></ISSN>
		</xsl:if>

		<xsl:choose>
			<xsl:when test="EI-DOCUMENT/BN13">
				<ISBN><xsl:value-of select="EI-DOCUMENT/BN13"/></ISBN>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="EI-DOCUMENT/BN">
					<ISBN><xsl:value-of select="EI-DOCUMENT/BN"/></ISBN>
				</xsl:if>
			</xsl:otherwise>		
		</xsl:choose>
		
		<xsl:apply-templates select="EI-DOCUMENT/PN|EI-DOCUMENT/I_PN"/>
		<xsl:choose>
			<xsl:when test="EI-DOCUMENT/LA">
				<LANGUAGE>
					<xsl:value-of select="EI-DOCUMENT/LA" />
				</LANGUAGE>
			</xsl:when>
			<xsl:otherwise>
				<LANGUAGE>
					<xsl:text>English</xsl:text>
				</LANGUAGE>
			</xsl:otherwise>
		</xsl:choose>
		 <xsl:choose>
		    	<xsl:when test="(EI-DOCUMENT/SD)">
				<DATE><xsl:value-of select="EI-DOCUMENT/SD"/></DATE>
			</xsl:when>
		    	<xsl:otherwise>
		    	     <xsl:choose>
		    	     	<xsl:when test="EI-DOCUMENT/YR">
			     		<DATE><xsl:value-of select="EI-DOCUMENT/YR"/></DATE>
		    		</xsl:when>
				<xsl:otherwise>
					<xsl:if test="(EI-DOCUMENT/PD-YR)">
						<DATE><xsl:value-of select="EI-DOCUMENT/PD-YR"/></DATE>
					</xsl:if>
				 </xsl:otherwise>
			    </xsl:choose>
			</xsl:otherwise>
		</xsl:choose>

		<DESCRIPTION>
			<ABSTRACT-LINK>
			   	<xsl:text>http://</xsl:text>
				<xsl:value-of select="$SERVER" />
				<xsl:text>/search/doc/abstract.url?</xsl:text>
				<xsl:text>CID=expertSearchAbstractFormat&amp;</xsl:text>
				<xsl:text>&amp;SEARCHID=</xsl:text>
				<xsl:value-of select="$SEARCHID" />
				<xsl:text>&amp;DOCINDEX=</xsl:text>
				<xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/>
				<xsl:text>&amp;PAGEINDEX=</xsl:text>
				<xsl:value-of select="$PAGEINDEX" />
				<xsl:text>&amp;RESULTSCOUNT=</xsl:text>
				<xsl:value-of select="$RESULTS-COUNT" />
				<xsl:text>&amp;database=</xsl:text>
				<xsl:value-of select="//DBMASK"/>
                <xsl:text>&amp;format=expertSearchAbstractFormat</xsl:text>
                <xsl:text>&amp;pageType=expertSearch</xsl:text>
                <xsl:text>&amp;searchtype=Expert</xsl:text>
			</ABSTRACT-LINK>
			<DETAILED-LINK>
				<xsl:text>http://</xsl:text>
				<xsl:value-of select="$SERVER" />
				<xsl:text>/search/doc/detailed.url?</xsl:text>
				<xsl:text>CID=expertSearchDetailedFormat</xsl:text>
				<xsl:text>&amp;SEARCHID=</xsl:text>
				<xsl:value-of select="$SEARCHID" />
				<xsl:text>&amp;DOCINDEX=</xsl:text>
				<xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/>
				<xsl:text>&amp;PAGEINDEX=</xsl:text>
				<xsl:value-of select="$PAGEINDEX" />
				<xsl:text>&amp;RESULTSCOUNT=</xsl:text>
				<xsl:value-of select="$RESULTS-COUNT" />
				<xsl:text>&amp;database=</xsl:text>
				<xsl:value-of select="//DBMASK"/>
				<xsl:text>&amp;format=expertSearchDetailedFormat</xsl:text>
                <xsl:text>&amp;pageType=expertSearch</xsl:text>
                <xsl:text>&amp;searchtype=Expert</xsl:text>
			</DETAILED-LINK>
		</DESCRIPTION>
		<xsl:call-template name="CITATION-CVS"/>
	</DOC>
</xsl:template>

<xsl:template match="EI-DOCUMENT/PPD|EI-DOCUMENT/PD_YR">
		<PUBLICATION-DATE><xsl:value-of select="." disable-output-escaping="yes"/></PUBLICATION-DATE>
</xsl:template>

 <xsl:template match="VO">
 	<xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> </xsl:text>
 </xsl:template>

<xsl:template match="NV">
	<xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
</xsl:template>

<xsl:template match="EXCEPTION">
	<EXCEPTION>
		<xsl:apply-templates />
	</EXCEPTION>
</xsl:template>

<xsl:template match="SESSION-ID">
	<EISESSION>
		<xsl:apply-templates />
	</EISESSION>
</xsl:template>

<xsl:template match="RESULTS-COUNT">
	<RESULTCOUNT>
		<xsl:apply-templates />
	</RESULTCOUNT>
</xsl:template>

<xsl:template match="SEARCH-ID">
	<SEARCH-ID>
		<xsl:apply-templates />
	</SEARCH-ID>
</xsl:template>

<xsl:template match="EI-DOCUMENT/PP|EI-DOCUMENT/PA">
	<PAGE-NUMBER>
		<xsl:apply-templates />
	</PAGE-NUMBER>
</xsl:template>

<xsl:template match="EI-DOCUMENT/CVS/MH|EI-DOCUMENT/CVS/CV">
	<CONTROLLED-TERM>
		<xsl:apply-templates />
	</CONTROLLED-TERM>
</xsl:template>

<xsl:template match="EI-DOCUMENT/CN">
	<CODEN>
		<xsl:apply-templates />
	</CODEN>
</xsl:template>

<xsl:template match="//DISPLAY-QUERY">
	<xsl:param name="DISPLAY-QUERY" />
	<xsl:param name="LASTFOURUPDATES" />
	<QUERY>
		<xsl:value-of select="$DISPLAY-QUERY"/>
		<xsl:choose>
		    <xsl:when test="string-length($LASTFOURUPDATES) &lt; 1">
		   		<xsl:text>,  </xsl:text>
				<xsl:value-of select="//START-YEAR"/>
				<xsl:text>-</xsl:text>
				<xsl:value-of select="//END-YEAR"/>
		 	</xsl:when>
		    <xsl:otherwise>
		      	<xsl:apply-templates select="//LASTFOURUPDATES"/>
		  	</xsl:otherwise>
		</xsl:choose>
	</QUERY>
</xsl:template>

<xsl:template match="//LASTFOURUPDATES">
	<xsl:text>, Last four updates</xsl:text>
</xsl:template>

<xsl:template match="EI-DOCUMENT/VT">
	<VOLUME-TITLE>
		<xsl:apply-templates />
	</VOLUME-TITLE>
</xsl:template>


<xsl:template name="CITATION-CVS">
	<xsl:for-each select="EI-DOCUMENT/CVS/CV">
		<SUBJECT>
			<xsl:value-of select="."/>
		</SUBJECT>
	</xsl:for-each>
</xsl:template>

<xsl:template match="EI-DOCUMENT/PN|EI-DOCUMENT/I_PN">
	<PUBLISHER>
		<xsl:apply-templates />
	</PUBLISHER>
</xsl:template>



<xsl:template match="PAGE-RESULTS">
	<xsl:param name="SERVER" />
	<xsl:param name="SEARCHID" />
	<xsl:param name="SESSIONID" />
	<xsl:param name="PAGEINDEX" />
	<xsl:param name="RESULTS-COUNT" />
	<xsl:apply-templates select="PAGE-ENTRY">
		<xsl:with-param name="SERVER" select="$SERVER" />
		<xsl:with-param name="SEARCHID" select="$SEARCHID" />
		<xsl:with-param name="SESSIONID" select="$SESSIONID" />
		<xsl:with-param name="PAGEINDEX" select="$PAGEINDEX" />
		<xsl:with-param name="RESULTS-COUNT" select="$RESULTS-COUNT" />
	</xsl:apply-templates>
</xsl:template>


<xsl:template match="EI-DOCUMENT/AUS/AU|EI-DOCUMENT/IVS/IV|EI-DOCUMENT/EDS/ED">

	<AUTHOR>
		<xsl:value-of select="normalize-space(text())"/>
		<xsl:if test="AFF|EF">
			<AUTHOR-AFFILIATION>
				<xsl:value-of select="AFF|EF"/>
			</AUTHOR-AFFILIATION>
		</xsl:if>
	</AUTHOR>

</xsl:template>

<xsl:template match="EI-DOCUMENT">

	<FORMAT>
		<xsl:value-of select="@VIEW"/>
	</FORMAT>

</xsl:template>

<xsl:template match="//NEXT-PAGE">

	<NEXT-PAGE>
		<xsl:value-of select="."/>
	</NEXT-PAGE>

</xsl:template>

<xsl:template match="//PREV-PAGE">

	<PREV-PAGE>
		<xsl:value-of select="."/>
	</PREV-PAGE>

</xsl:template>



<xsl:template match="EI-DOCUMENT/IVIP">

	<FIRST-VOLUME>
		<xsl:value-of select="@firstVolume"/>
	</FIRST-VOLUME>

	<FIRST-ISSUE>
		<xsl:value-of select="@firstIssue"/>
	</FIRST-ISSUE>

	<FIRST-PAGE>
		<xsl:value-of select="@firstPage"/>
	</FIRST-PAGE>

</xsl:template>

</xsl:stylesheet>













