<?xml version="1.0"  encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
    version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:book="java:org.ei.books.BookDocument"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
    xmlns:security="java:org.ei.security.utils.SecureID"
    xmlns:searchresult="java:org.ei.stripes.view.SearchResult"
    xmlns:patentrefsummary="java:org.ei.stripes.view.PatentrefSummary"
    xmlns:searchresultdoc="java:org.ei.stripes.view.Doc"
    xmlns:author="java:org.ei.stripes.view.Author"
    xmlns:affil="java:org.ei.stripes.view.Affil"
    xmlns:citedby="java:org.ei.stripes.view.CitedBy"
    xmlns:actionbean="java:org.ei.stripes.action.results.ICitationAction"
    xmlns:abstractrecord="java:org.ei.stripes.view.AbstractRecord"
    exclude-result-prefixes="html hlight xsl java book custoptions security searchresult searchresultdoc author affil citedby actionbean"
>
	<xsl:variable name="LOCALHOLDINGS-CITATION"><xsl:value-of select="//LOCALHOLDINGS-CITATION"/></xsl:variable>
	<xsl:include href="LocalHolding.xsl"/>
	
        <xsl:variable name="dquote">"</xsl:variable>
        <xsl:variable name="squote">'</xsl:variable>

    
	<!-- *************************************************************** -->
	<!-- Parse the page results                                          -->
	<!-- *************************************************************** -->
	<xsl:template match="PAGE-RESULTS/PAGE-ENTRY">

		<xsl:variable name="sr" select="searchresult:new()"/>
		<xsl:variable name="ISDUP"><xsl:value-of select="@DUP"/></xsl:variable>
		<xsl:value-of select="searchresult:setDup($sr, boolean('true'=$ISDUP))" />

		<xsl:variable name="dupids"><xsl:value-of select="DUPIDS"/></xsl:variable>
		<xsl:value-of select="searchresult:setDupids($sr, $dupids)" />
		<xsl:variable name="DOCBSHITIDX">
            <xsl:value-of select="DOCUMENTBASKETHITINDEX"/>
        </xsl:variable>
        <xsl:value-of select="searchresult:setDocumentbaskethitindex($sr, $DOCBSHITIDX)" />

		<xsl:apply-templates select="EI-DOCUMENT">
			<xsl:with-param name="sr" select="$sr"></xsl:with-param>
		</xsl:apply-templates>

		<!-- Build local holding links -->
		<xsl:if test="($LOCALHOLDINGS-CITATION='true' or /PAGE/LOCAL-HOLDINGS)">
			<xsl:call-template name="LOCAL-HOLDINGS">
			<xsl:with-param name="sr" select="$sr"/>
			<xsl:with-param name="vISSN" select="SN"/>
			<xsl:with-param name="LOCAL-HOLDINGS" select="LOCAL-HOLDINGS|/PAGE/LOCAL-HOLDINGS"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="PAGE-RESULTS/UPT-DOC/REF-DOCS/PAGE-ENTRY">
		<xsl:variable name="sr" select="searchresult:new()"/>
		<xsl:variable name="ISDUP"><xsl:value-of select="@DUP"/></xsl:variable>
		<xsl:value-of select="searchresult:setDup($sr, boolean('true'=$ISDUP))" />

		<xsl:variable name="dupids"><xsl:value-of select="DUPIDS"/></xsl:variable>
		<xsl:value-of select="searchresult:setDupids($sr, $dupids)" />
		<xsl:variable name="DOCBSHITIDX">
            <xsl:value-of select="DOCUMENTBASKETHITINDEX"/>
        </xsl:variable>
        <xsl:value-of select="searchresult:setDocumentbaskethitindex($sr, $DOCBSHITIDX)" />

		<xsl:apply-templates select="EI-DOCUMENT">
			<xsl:with-param name="sr" select="$sr"></xsl:with-param>
		</xsl:apply-templates>

		<!-- Build local holding links -->
		<xsl:if test="($LOCALHOLDINGS-CITATION='true' or /PAGE/LOCAL-HOLDINGS)">
			<xsl:call-template name="LOCAL-HOLDINGS">
			<xsl:with-param name="sr" select="$sr"/>
			<xsl:with-param name="vISSN" select="SN"/>
			<xsl:with-param name="LOCAL-HOLDINGS" select="LOCAL-HOLDINGS|/PAGE/LOCAL-HOLDINGS"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="PAGE-RESULTS/UPT-DOC/REF-DOCS/PAGE-ENTRY/NP-DOC">
		<xsl:variable name="sr" select="searchresult:new()"/>
		<xsl:value-of select="searchresult:setIndex($sr, INDEX)" />
        <xsl:value-of select="searchresult:setSource($sr, SOURCE)" />


        <!-- Create external links -->
		<xsl:call-template name="BUILD-LINKS">
			<xsl:with-param name="sr" select="$sr"></xsl:with-param>
		</xsl:call-template>

		<xsl:value-of select="actionbean:addSearchResult($actionbean,$sr)" />
	</xsl:template>


	<!-- *************************************************************** -->
	<!-- Parse the main document info                                    -->
	<!-- *************************************************************** -->
	<xsl:template match="EI-DOCUMENT">
		<xsl:param name="sr" />

		<xsl:variable name="FULLTEXT-LINK"><xsl:value-of select="FT/@FTLINK" /></xsl:variable>

        <xsl:value-of select="searchresult:setSelected($sr, boolean('true'=../IS-SELECTED))" />
        <xsl:value-of select="searchresult:setBasketsearchid($sr, ../BASKET-SEARCHID)" />

		<xsl:value-of select="searchresult:addLabel($sr,'AN',AN/@label)" />
		<xsl:value-of select="searchresult:setAccnum($sr, AN)" />
		<xsl:value-of select="searchresult:setCollection($sr, COL)" />

		<!-- Set the title -->
		<xsl:value-of select="searchresult:addLabel($sr,'TI',hlight:removeMarkup((TI|TT)/@label))" />
		<xsl:value-of select="searchresult:setTitle($sr, hlight:addMarkup(TI|TT))" />


		<!-- Set the doctype -->
		<xsl:value-of select="searchresult:addLabel($sr,'DT',DT/@label)" />
	   <xsl:value-of select="searchresult:setDoctype($sr,DT/text())"/>

		<!-- Set the source and no source values -->
		<xsl:value-of select="searchresult:setSource($sr,hlight:addMarkup(SO))"/>
		<xsl:value-of select="searchresult:setRil($sr, RIL)"/>
		<xsl:value-of select="searchresult:setNosource($sr, boolean(NO_SO))" />
		<xsl:value-of select="searchresult:setSourceabbrev($sr, SE)" />

		<xsl:value-of select="searchresult:setPf($sr, PF)" />
		<xsl:value-of select="searchresult:setSp($sr, SP)" />
		<xsl:value-of select="searchresult:setRsp($sr, RSP)" />
		<xsl:value-of select="searchresult:setRnlabel($sr, RN_LABEL)" />
		<xsl:value-of select="searchresult:setRn($sr, RN)" />
        <xsl:value-of select="searchresult:setVo($sr, (VO|VOM))" />
        <xsl:value-of select="searchresult:setSn($sr, (SN))" />
		<xsl:value-of select="searchresult:setIs($sr, IS)" />
		<xsl:value-of select="searchresult:setSd($sr, hlight:addMarkup(SD))" />
		<xsl:value-of select="searchresult:setMt($sr, hlight:addMarkup(MT))" />
		<xsl:value-of select="searchresult:setVt($sr, hlight:addMarkup(VT))" />
		<xsl:value-of select="searchresult:setArn($sr, ARN)" />
		<xsl:value-of select="searchresult:setPages($sr, PP)" />
		<xsl:value-of select="searchresult:setPage($sr, p_PP)" />
		<xsl:value-of select="searchresult:setPagespp($sr, PP_pp)" />
		<xsl:value-of select="searchresult:setSd($sr, hlight:addMarkup(SD))" />
		<xsl:value-of select="searchresult:setYr($sr, YR)" />
		<xsl:value-of select="searchresult:setPd($sr, PD_YR)" />
		<xsl:value-of select="searchresult:setVt($sr, NV)" />
		<xsl:value-of select="searchresult:setPa($sr, PA)" />
		<xsl:value-of select="searchresult:setPan($sr,PAN)" />
		<xsl:value-of select="searchresult:setPpn($sr,PPN)" />
		<xsl:value-of select="searchresult:setPap($sr,PAP)" />
		<xsl:value-of select="searchresult:setPinfo($sr,PINFO)" />
		<xsl:value-of select="searchresult:setPm($sr,PM)" />
		<xsl:value-of select="searchresult:setPm1($sr,PM1)" />
		<xsl:value-of select="searchresult:addLabel($sr,'UPD',UPD/@label)" />
		<xsl:value-of select="searchresult:setUpd($sr,UPD)" />
		<xsl:value-of select="searchresult:setKd($sr,KD)" />
		<xsl:value-of select="searchresult:setPfd($sr,PFD)" />
		<xsl:value-of select="searchresult:setPidd($sr,PIDD|PID)" />
		<xsl:value-of select="searchresult:setPpd($sr,PPD)" />
		<xsl:value-of select="searchresult:setLa($sr,LA)" />
		<xsl:value-of select="searchresult:setNv($sr,NF)" />
		<xsl:value-of select="searchresult:setAv($sr,hlight:addMarkup(AV))" />
		<xsl:value-of select="searchresult:setSc($sr,SC)" />
		<xsl:value-of select="searchresult:setCpr($sr, CPR)" />
		<xsl:value-of select="searchresult:setCprt($sr, CPRT)" />
		<xsl:value-of select="searchresult:setDoi($sr, DO)" />
		<xsl:value-of select="searchresult:setPn($sr,PN)"/>
		<xsl:value-of select="searchresult:setIpn($sr,I_PN)"/>
		<xsl:value-of select="searchresult:setPi($sr,PI)"/>
		<xsl:value-of select="searchresult:setIpc($sr,IPC)"/>
		<xsl:value-of select="searchresult:setEcl($sr,ECL)"/>
        <xsl:value-of select="searchresult:setCpub($sr,CPUB)"/>
        <xsl:value-of select="searchresult:setPii($sr,PII)"/>

		<!-- added for 1884 -->
		<xsl:value-of select="searchresult:setCopa($sr,COPA)" />

		<!-- Add ebook info if present -->
		<xsl:value-of select="searchresult:setBti($sr, BTI/text())" />
		<xsl:value-of select="searchresult:setBpp($sr, BPP)" />
		<xsl:value-of select="searchresult:setBct($sr, BCT)" />
		<xsl:value-of select="searchresult:setBtst($sr, BTST)" />
		<xsl:value-of select="searchresult:addLabel($sr,'BN',BN/@label)" />
		<xsl:value-of select="searchresult:setIsbn($sr, BN)" />
		<xsl:value-of select="searchresult:setIsbn13($sr, BN13)" />
		<xsl:value-of select="searchresult:setBpn($sr, BPN)" />
		<xsl:value-of select="searchresult:setByr($sr, BYR)" />
		<xsl:if test="BTI">
			<xsl:value-of select="searchresult:setBimg($sr, //BOOKIMGS)" />
			<xsl:variable name="BOOKINGSIMG"><xsl:value-of select="//BOOKIMGS"/>/images/<xsl:value-of select="BN13"/>/<xsl:value-of select="BN13"/>small.jpg
                        </xsl:variable>
			<xsl:value-of select="searchresult:setBimgfullpath($sr, $BOOKINGSIMG)" />
		</xsl:if>
		<xsl:value-of select="searchresult:setBpc($sr, BPC)" />
		<xsl:value-of select="searchresult:setBst($sr, BST)" />
		<xsl:value-of select="searchresult:setPr($sr, PR)" />
		<xsl:value-of select="searchresult:setPapim($sr, PAPIM)" />
		<!-- <xsl:value-of select="searchresult:setPim($sr, PIM|PI)" />-->

        <!-- Set the title of translation -->
		<xsl:value-of select="searchresult:addLabel($sr,'TT',TT/@label)" />
		<xsl:value-of select="searchresult:setTt($sr, hlight:addMarkup(TT))" />


		<!--
			Set the fulltext link. This has 2 parts, 1) check the back office to
			see if fulltext is enabled and 2) see if there is a fulltext link
			present
		-->
		<xsl:variable name="ISFULLTEXT"
			select="'true' = custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, DO, DOC/DB/DBMASK)" />
		<xsl:value-of select="searchresult:setFulltext($sr, $ISFULLTEXT)" />
		<xsl:if test="$ISFULLTEXT">
			<xsl:value-of select="searchresult:setFulltextlink($sr, $FULLTEXT-LINK)" />
		</xsl:if>

		<!--  Build the Doc object -->
		<xsl:variable name="srdoc" select="searchresultdoc:new()" />
		<xsl:value-of select="searchresultdoc:setHitindex($srdoc, DOC/HITINDEX)" />
		<xsl:value-of select="searchresultdoc:setDocid($srdoc, DOC/DOC-ID)" />
		<xsl:value-of select="searchresultdoc:setDbid($srdoc, DOC/DB/ID)" />
		<xsl:value-of select="searchresultdoc:setDbmask($srdoc, DOC/DB/DBMASK)" />
		<xsl:value-of select="searchresultdoc:setDbname($srdoc, DOC/DB/DBNAME)" />
		<xsl:value-of select="searchresultdoc:setDbsname($srdoc, DOC/DB/DBSNAME)" />
		<xsl:value-of select="searchresultdoc:setDbiname($srdoc, DOC/DB/DBINAME)" />
		<xsl:value-of select="searchresult:setDoc($sr, $srdoc)" />

		<!-- Create external links -->
		<xsl:call-template name="BUILD-LINKS">
			<xsl:with-param name="sr" select="$sr"></xsl:with-param>
		</xsl:call-template>

	    <!-- Add the abstract! -->
	    <xsl:apply-templates>
			<xsl:with-param name="sr" select="$sr"></xsl:with-param>
	    </xsl:apply-templates>

		<!-- ****************************************************************** -->
		<!-- Add search result object to action bean!                           -->
		<!-- ****************************************************************** -->
		<xsl:value-of select="actionbean:addSearchResult($actionbean,$sr)" />

	</xsl:template>

	<!-- *************************************************************** -->
	<!-- Parse the affiliation information                                    -->
	<!-- *************************************************************** -->
	<xsl:template match="AFS">
		<xsl:param name="sr"/>
		<xsl:for-each select="AF">
			<xsl:variable name="affil" select="affil:new()"/>
	    	<xsl:value-of select="affil:setId($affil,@id)"/>
			<xsl:value-of select="affil:setName($affil,hlight:addMarkup(text()))"/>
			<xsl:value-of select="searchresult:addAffil($sr,$affil)"/>
		</xsl:for-each>
	</xsl:template>

	<!-- *************************************************************** -->
	<!-- Parse the author information                                    -->
	<!-- *************************************************************** -->
	<xsl:template match="AUS|IVS|EDS">
		<xsl:param name="sr"/>
		<xsl:variable name="FIELD">
        	<xsl:choose>
        		<xsl:when test="name(.)='IVS'">IVS</xsl:when>
        		<xsl:when test="name(.)='AUS'">AUS</xsl:when>
        		<xsl:when test="name(.)='EDS'">EDS</xsl:when>
        		<xsl:otherwise>name()</xsl:otherwise>
        	</xsl:choose>
        </xsl:variable>
		<xsl:value-of select="searchresult:addLabel($sr,$FIELD,@label)" />
		<xsl:apply-templates select="AU|ED|IV">
			<xsl:with-param name="sr" select="$sr"></xsl:with-param>
			<xsl:with-param name="AFS" select="../AFS"></xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="AU|ED|IV">
		<xsl:param name="sr"/>
		<xsl:param name="AFS"/>
        <xsl:variable name="NAME">
		<xsl:value-of select="normalize-space(text())"/>
		</xsl:variable>
        <xsl:variable name="author" select="author:new()"/>
		<xsl:value-of select="author:setId($author,@id)"/>
		<xsl:value-of select="author:setName($author, $NAME)"/>
		<xsl:value-of select="author:setEmail($author, ./EMAIL)"/>

		  <xsl:if test="name()='IV'">
            <xsl:if test="CO">
               	<xsl:variable name="CO">
					<xsl:text> (</xsl:text>
	                <xsl:value-of select="CO" />
	                <xsl:text>) </xsl:text>
				</xsl:variable>
                <xsl:value-of select="author:setCo($author,$CO)"/>
            </xsl:if>
        </xsl:if>

		<!-- ****************************************************** -->
		<!-- Create the author search link                          -->
		<!-- ****************************************************** -->
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
                <xsl:when test="string(/PAGE/FOLDER-ID)"><xsl:value-of select="$THIS-DOCUMENT-DB"/></xsl:when>
                <!-- View Selected Set -->
                <xsl:when test="contains(/PAGE/CID,'SelectedSet')">
                    <xsl:choose>
                        <!-- View Selected Set - Multiple Queries -->
                        <xsl:when test="(/PAGE/HAS-MULTIPLE-QUERYS='true') or (/PAGE/DBMASK = '')"><xsl:value-of select="$THIS-DOCUMENT-DB"/></xsl:when>
                        <!-- View Selected Set - Single Query -->
                        <xsl:otherwise><xsl:value-of select="/PAGE/DBMASK"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <!-- Search Results Records -->
                <xsl:otherwise><xsl:value-of select="/PAGE/DBMASK"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="ENCODED-DATABASE"><xsl:value-of select="java:encode($DATABASE)" /></xsl:variable>
		<xsl:variable name="DEFAULT-LINK-SORT">sort=yr</xsl:variable>

		<xsl:if test="not(/PAGE/LINK ='false') and not(/SECTION-DELIM/LINK ='false')">
			<xsl:choose>
				<xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">
					<xsl:variable name="href"><xsl:value-of select="$HREF-PREFIX" />/search/submit.url?CID=expertSearchCitationFormat&amp;searchWord1={<xsl:value-of
						select="java:encode($NAME)" />}<xsl:value-of select="java:encode(' WN ')" /><xsl:value-of
						select="$FIELD" />&amp;database=<xsl:value-of select="$ENCODED-DATABASE" />&amp;yearselect=yearrange&amp;searchtype=<xsl:value-of
						select="$SEARCH-TYPE" />&amp;<xsl:value-of select="$DEFAULT-LINK-SORT" /></xsl:variable>
						<xsl:value-of select="author:setSearchlink($author,$href)"/>
				</xsl:when>
				<xsl:when test="($SEARCH-TYPE='TagSearch')">
					<xsl:variable name="href"><xsl:value-of select="$HREF-PREFIX" />/search/submit.url?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of
						select="java:encode($NAME)" />}&amp;section1=<xsl:value-of
						select="$FIELD" />&amp;database=<xsl:value-of select="$THIS-DOCUMENT-DB" />&amp;yearselect=yearrange&amp;searchtype=Quick&amp;<xsl:value-of
						select="$DEFAULT-LINK-SORT" /></xsl:variable>
						<xsl:value-of select="author:setSearchlink($author,$href)"/>
				</xsl:when>
				<xsl:when test="($SEARCH-TYPE='Book')">
					<xsl:variable name="href"><xsl:value-of select="$HREF-PREFIX" />/search/submit.url?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of
						select="java:encode($NAME)" />}&amp;section1=<xsl:value-of
						select="$FIELD" />&amp;database=131072&amp;searchtype=<xsl:value-of
						select="$SEARCH-TYPE" />&amp;yearselect=yearrange&amp;<xsl:value-of
						select="$DEFAULT-LINK-SORT" /></xsl:variable>
						<xsl:value-of select="author:setSearchlink($author,$href)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="href"><xsl:value-of select="$HREF-PREFIX" />/search/submit.url?CID=quickSearchCitationFormat&amp;searchtype=Quick&amp;searchWord1={<xsl:value-of
						select="java:encode($NAME)" />}&amp;section1=<xsl:value-of
						select="$FIELD" />&amp;database=<xsl:value-of select="$ENCODED-DATABASE" />&amp;yearselect=yearrange&amp;<xsl:value-of
						select="$DEFAULT-LINK-SORT" /></xsl:variable>
						<xsl:value-of select="author:setSearchlink($author,$href)"/>
				</xsl:otherwise>
			</xsl:choose>
		   </xsl:if>

		<!-- ************** ADD the affiliation info **************** -->
		<xsl:if test="$AFS">
			<xsl:value-of select="searchresult:addLabel($sr,'AFS',$AFS/@label)" />
			<xsl:choose>
			<xsl:when test="AFS/AFID">
			<xsl:for-each select="AFS/AFID">
		    	<xsl:variable name="affil" select="affil:new()"/>
		    	<xsl:variable name="affilid" select="number()"/>
		    	<xsl:value-of select="affil:setId($affil,number())"/>
				<xsl:choose>
					<xsl:when test="number(.) > 0">
						<xsl:value-of select="affil:setName($affil,hlight:addMarkup($AFS/AF[@id=$affilid]/text()))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="affil:setName($affil,hlight:addMarkup($AFS/AF/AF/text()))"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="author:addAffil($author,$affil)"/>
			</xsl:for-each>
			</xsl:when>
			<xsl:when test="AF">
		    	<xsl:variable name="affil" select="affil:new()"/>
				<xsl:value-of select="affil:setId($affil,@id)"/>
				<xsl:value-of select="affil:setName($affil,hlight:addMarkup(AF/text()))"/>
				<xsl:value-of select="author:addAffil($author,$affil)"/>
			</xsl:when>
			</xsl:choose>
		</xsl:if>

		<xsl:choose>
			<xsl:when test="name(.)='ED'"><xsl:value-of select="searchresult:addEditor($sr,$author)"/></xsl:when>
			<xsl:when test="name(.)='IV'"><xsl:value-of select="searchresult:addInventors($sr,$author)"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="searchresult:addAuthor($sr,$author)"/></xsl:otherwise>
		</xsl:choose>


 	</xsl:template>

	<!-- ************************************************************** -->
	<!-- Cited By creation -->
	<!-- ************************************************************** -->
	<xsl:template match="CITEDBY">
		<xsl:param name="sr"/>

		<xsl:variable name="citedby" select="citedby:new()" />

		<xsl:value-of select="citedby:setMd5($citedby, security:getCitedbyMD5($SESSION-ID,@AN))" />
		<xsl:value-of select="citedby:setIssn($citedby, @ISSN)" />
		<xsl:value-of select="citedby:setIsbn($citedby, @ISBN)" />
		<xsl:value-of select="citedby:setIsbn13($citedby, @ISBN13)" />
		<xsl:value-of select="citedby:setFirstissue($citedby, @firstIssue)" />
		<xsl:value-of select="citedby:setFirstvolume($citedby, @firstVolume)" />
		<xsl:value-of select="citedby:setFirstpage($citedby, @firstPage)" />
		<xsl:value-of select="citedby:setDoi($citedby, @DOI)" />
		<xsl:value-of select="citedby:setPii($citedby, @PII)" />
		<xsl:value-of select="citedby:setAn($citedby, @AN)" />
		
		<xsl:value-of select="searchresult:setCitedby($sr, $citedby)" />
	</xsl:template>

	<!-- ************************************************************* -->
	<!-- Build the links for the page - Abstract, Detailed, Page       -->
	<!-- and Book Details                                              -->
	<!-- NOTE: Call template from PAGE-ENTRY node!!!                   -->
	<!-- ************************************************************* -->
	<xsl:template name="BUILD-LINKS">
		<xsl:param name="SEARCH-CID"/>
		<xsl:param name="sr"/>
		<xsl:variable name="PII"><xsl:if test="string(PII)">&amp;pii=<xsl:value-of select="PII"/></xsl:if></xsl:variable>
		<xsl:variable name="INDEX"><xsl:value-of select="DOC/HITINDEX"/></xsl:variable>
		<xsl:variable name="DOC-ID"><xsl:value-of select="DOC/DOC-ID"/></xsl:variable>
		<xsl:variable name="SELECTED-DB"><xsl:value-of select="/PAGE/DBMASK"/></xsl:variable>
		<xsl:variable name="CID-PREFIX">
		<xsl:choose>
			<xsl:when test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus')">quickSearch</xsl:when>
			<xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">expertSearch</xsl:when>
			<xsl:when test="($SEARCH-TYPE='TagSearch')">tagSearch</xsl:when>
			<xsl:otherwise>expertSearch</xsl:otherwise>
		</xsl:choose>
		</xsl:variable>
		<xsl:variable name="BOOKS_OPEN_WINDOW_PARAMS">height=800,width=700,status=yes,resizable,scrollbars=1,menubar=no</xsl:variable>
        <xsl:variable name="SCOPE"><xsl:value-of select="//SCOPE"/></xsl:variable>

        <xsl:value-of select="searchresult:setCidPrefix($sr, $CID-PREFIX)"/>

		<!--
		ABSTRACT LINK
		-->
		<xsl:variable name="CID-LOCAL"><xsl:value-of select="//CID"/></xsl:variable>
		<!--  TMH - fix problem with double quotes; for now just make single quotes, couldn't figure out how to escape to &quot; here.  :( -->
        <xsl:variable name="TITLE-TRANSLATED" select="translate(hlight:removeMarkup(TI|TT),$dquote,$squote)"/>
		<xsl:variable name="ABSTRACT-LINK">
		<xsl:choose>
				<xsl:when test="$CID-LOCAL='quickSearchReferencesFormat'">
						&lt;a class="externallink" href="/search/doc/abstract.url?&amp;pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;docid=<xsl:value-of select="DOC/DOC-ID" />&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>AbstractFormat<xsl:if test="$SCOPE">&amp;tagscope=<xsl:value-of select="$SCOPE"/></xsl:if>&amp;displayPagination=no" title="Abstract for <xsl:value-of select="$TITLE-TRANSLATED"/>" aria-labelledby="resulttitle_<xsl:value-of select="$INDEX"/>" id="abslink_<xsl:value-of select="$INDEX"/>"&gt;Abstract&lt;/a&gt;
					</xsl:when>
					<xsl:otherwise>
						&lt;a class="externallink" href="/search/doc/abstract.url?&amp;pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>AbstractFormat<xsl:if test="$SCOPE">&amp;tagscope=<xsl:value-of select="$SCOPE"/></xsl:if>&amp;displayPagination=yes" title="Abstract for <xsl:value-of select="$TITLE-TRANSLATED"/>" aria-labelledby="resulttitle_<xsl:value-of select="$INDEX"/>" id="abslink_<xsl:value-of select="$INDEX"/>"&gt;Abstract&lt;/a&gt;
					</xsl:otherwise>
				</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="searchresult:setAbstractlink($sr, $ABSTRACT-LINK)"/>

		<!--
		DETAILED LINK
		-->
		<xsl:variable name="DETAILED-LINK">
		<xsl:choose>
				<xsl:when test="$CID-LOCAL='quickSearchReferencesFormat'">
						&lt;a class="externallink" href="/search/doc/detailed.url?SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;CID=<xsl:value-of select="$CID-PREFIX" />DetailedFormat&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;docid=<xsl:value-of select="DOC/DOC-ID" />&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat<xsl:if test="$SCOPE">&amp;tagscope=<xsl:value-of select="$SCOPE"/></xsl:if>&amp;displayPagination=no" title="Detailed for <xsl:value-of select="$TITLE-TRANSLATED"/>" aria-labelledby="resulttitle_<xsl:value-of select="$INDEX"/>" id="detaillink_<xsl:value-of select="$INDEX"/>"&gt;Detailed&lt;/a&gt;
					</xsl:when>
					<xsl:otherwise>
						&lt;a class="externallink" href="/search/doc/detailed.url?SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;CID=<xsl:value-of select="$CID-PREFIX" />DetailedFormat&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat<xsl:if test="$SCOPE">&amp;tagscope=<xsl:value-of select="$SCOPE"/></xsl:if>&amp;displayPagination=yes" title="Detailed for <xsl:value-of select="$TITLE-TRANSLATED"/>" aria-labelledby="resulttitle_<xsl:value-of select="$INDEX"/>" id="detaillink_<xsl:value-of select="$INDEX"/>"&gt;Detailed&lt;/a&gt;
					</xsl:otherwise>
				</xsl:choose>

		</xsl:variable>
		<xsl:value-of select="searchresult:setDetailedlink($sr, $DETAILED-LINK)"/>

		<!--
		BOOK DETAILS LINK
		-->
		<xsl:variable name="BOOKDETAILS-LINK">
&lt;a title="Book Details" class="externallink"
		<xsl:if test="not(BPP = '0')">
href="/search/book/bookdetailed.url?pageType=book&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;docid=<xsl:value-of select="$DOC-ID"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat<xsl:value-of select="$PII"/>&amp;RESULTCOUNT=<xsl:value-of select="//RESULTS-COUNT"/>&amp;INTERNALSEARCH=no"
		</xsl:if>
		<xsl:if test="(BPP = '0')">
href="/search/book/bookdetailed.url?pageType=book&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;docid=<xsl:value-of select="$DOC-ID"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat<xsl:value-of select="$PII"/>&amp;RESULTCOUNT=<xsl:value-of select="//RESULTS-COUNT"/>&amp;INTERNALSEARCH=yes"
		</xsl:if>
&gt;Book Details&lt;/a&gt;
		</xsl:variable>
		<xsl:value-of select="searchresult:setBookdetailslink($sr, $BOOKDETAILS-LINK)"/>

		<!--
		PAGE DETAILS LINK
		-->
		<xsl:if test="not(BPP = '0')">
			<xsl:variable name="PAGEDETAILS-LINK">
&lt;a title="Page Details" class="externallink" href="/search/book/detailed.url?pageType=page&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat" title="Page Details"&gt;Page Details&lt;/a&gt;
			</xsl:variable>
			<xsl:value-of select="searchresult:setPagedetailslink($sr, $PAGEDETAILS-LINK)"/>
		</xsl:if>

		<xsl:variable name="CID"><xsl:value-of select="//CID"/></xsl:variable>

		<xsl:variable name="BUTTON-ALIGN-PARAM">
		<xsl:choose>
			<xsl:when test="$CID='pageDetailedFormat' or $CID='bookSummary'">top</xsl:when>
			<xsl:otherwise>middle</xsl:otherwise>
		</xsl:choose>
		</xsl:variable>

		<xsl:variable name="BOOKSUMMARY-PII">
		<xsl:choose>
			<xsl:when test="$CID='pageDetailedFormat' or $CID='bookSummary'"><xsl:value-of select="//PII"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="PII"/></xsl:otherwise>
		</xsl:choose>
		</xsl:variable>

		<!--
		READ PAGE LINK
		-->
		<xsl:if test="not(BPP = '0') or $CID='bookSummary'">
		<xsl:variable name="READPAGE-LINK">
&lt;a title="Read a page from this book" href="javascript:_referex=window.open('/controller/servlet/Controller?CID=bookFrameset&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;docid=<xsl:value-of select="$DOC-ID"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>','_referex','<xsl:value-of select="$BOOKS_OPEN_WINDOW_PARAMS"/>');_referex.focus();void('');"&gt;
&lt;img alt="Read a page from this book" src="/static/images/read_page.png" style="border:0px; vertical-align:<xsl:value-of select="$BUTTON-ALIGN-PARAM"/>"/&gt;
&lt;/a&gt;
		</xsl:variable>
		<xsl:value-of select="searchresult:setReadpagelink($sr, $READPAGE-LINK)"/>
		</xsl:if>

		<xsl:if test="string(PII)  or ($CID='bookSummary' and $BOOKSUMMARY-PII!='' )">
		<!--
		READ CHAPTER LINK
		-->
		<xsl:variable name="READCHAPTER-LINK">
&lt;a target="_referex" title="Read a chapter from this book" href="<xsl:value-of select="book:getReadChapterLink(/PAGE/PAGE-RESULTS/PAGE-ENTRY/WOBLSERVER, BN13,$BOOKSUMMARY-PII, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=<xsl:value-of select="$SESSION-ID"/>"&gt;
&lt;img alt="Read a chapter from this book" src="/static/images/read_chapter.png" style="border:0px; vertical-align:<xsl:value-of select="$BUTTON-ALIGN-PARAM"/>"/&gt;&lt;/a&gt;
		</xsl:variable>
		<xsl:value-of select="searchresult:setReadchapterlink($sr, $READCHAPTER-LINK)"/>
		</xsl:if>
		<!--
 		READ BOOK LINK
		-->
		<xsl:if test="string(BN13)">
		<xsl:variable name="READBOOK-LINK">
&lt;a target="_referex" title="Read this book" href="<xsl:value-of select="book:getReadBookLink(/PAGE/PAGE-RESULTS/PAGE-ENTRY/WOBLSERVER, BN13, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=<xsl:value-of select="$SESSION-ID"/>"&gt;
&lt;img alt="Read this book" src="/static/images/read_book.png" style="border:0px; vertical-align:<xsl:value-of select="$BUTTON-ALIGN-PARAM"/>"/&gt;&lt;/a&gt;
		</xsl:variable>
		<xsl:value-of select="searchresult:setReadbooklink($sr, $READBOOK-LINK)"/>
		</xsl:if>

		<!-- ******************************************** -->
		<!-- PATENT LINKS                                 -->
		<!-- ******************************************** -->
		<xsl:variable name="KIND"><xsl:value-of select="KC"/></xsl:variable>
		<xsl:variable name="INS-REF-CNT"><xsl:value-of select="NR"/></xsl:variable>
		<xsl:variable name="CIT-CNT"><xsl:value-of select="CCT"/></xsl:variable>
		<xsl:variable name="REF-CNT"><xsl:value-of select="RCT"/></xsl:variable>
		<xsl:variable name="NONREF-CNT"><xsl:value-of select="NPRCT" /></xsl:variable>
		<xsl:variable name="AUTHCD"><xsl:value-of select="AUTHCD"/></xsl:variable>
		<xsl:variable name="REFERENCES-LINK-CID">
		  <xsl:choose>
		    <xsl:when test="$SEARCH-CID=''">CID=<xsl:value-of select="$CID-PREFIX"/>ReferencesFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/></xsl:when>
		    <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID"/></xsl:otherwise>
		  </xsl:choose>
		</xsl:variable>
		<xsl:variable name="NP-REFERENCES-LINK-CID">
		  <xsl:choose>
		    <xsl:when test="$SEARCH-CID=''">CID=<xsl:value-of select="$CID-PREFIX"/>NonPatentReferencesFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/></xsl:when>
		    <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID"/></xsl:otherwise>
		  </xsl:choose>
		</xsl:variable>
		<!-- "&amp;pcinav=0~<patent#>~Patents that cite <patent#>" encoded into links -->
		<xsl:variable name="CITEDBY-PM"><xsl:value-of select="PM"/></xsl:variable>
		<!-- ZY 9/15/09 Append patent kind in searchword1 for EP patents -->
		<xsl:variable name="CITEDBY-QSTR">
		  <xsl:choose>
		    <xsl:when test="($SEARCH-TYPE='Quick')">searchWord1=<xsl:value-of select="$CITEDBY-PM"/><xsl:if test="$AUTHCD='EP'"><xsl:value-of select="$KIND"/></xsl:if>&amp;section1=PCI&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:when>
		    <xsl:otherwise>searchWord1=<xsl:value-of select="$CITEDBY-PM"/><xsl:if test="$AUTHCD='EP'"><xsl:value-of select="$KIND"/></xsl:if><xsl:value-of select="java:encode(' WN PCI')"/>&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:otherwise>
		  </xsl:choose>
		</xsl:variable>


        <xsl:variable name="abstractrecord" select="searchresult:getAbstractrecord($sr)"/>
		<xsl:if test="($REF-CNT) and not($REF-CNT ='0') and not($REF-CNT ='')">
            <xsl:value-of select="abstractrecord:setPatrefcount($abstractrecord,number($REF-CNT))"/>
			<xsl:variable name="PATENT-REFS-LINK">
			/search/results/patentref.url?<xsl:value-of select="$REFERENCES-LINK-CID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;PAGEINDEX=<xsl:value-of select="$CURRENT-PAGE"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=patentReferencesFormat&amp;docid=<xsl:value-of select="$DOC-ID"/>
			</xsl:variable>
			<xsl:value-of select="abstractrecord:setPatentrefslink($abstractrecord, $PATENT-REFS-LINK)"/>
		</xsl:if>

		<xsl:if test="($INS-REF-CNT) and not($INS-REF-CNT ='0') and not($INS-REF-CNT ='')">
            <xsl:value-of select="abstractrecord:setRefcount($abstractrecord,number($INS-REF-CNT))"/>
			<xsl:variable name="INSPEC-REFS-LINK">
			/search/results/patentref.url?<xsl:value-of select="$REFERENCES-LINK-CID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;PAGEINDEX=<xsl:value-of select="$CURRENT-PAGE"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=patentReferencesFormat&amp;docid=<xsl:value-of select="$DOC-ID"/>
			</xsl:variable>
			<xsl:value-of select="abstractrecord:setInspecrefslink($abstractrecord, $INSPEC-REFS-LINK)"/>
		</xsl:if>

		<xsl:if test="($NONREF-CNT) and not($NONREF-CNT ='0') and not($NONREF-CNT ='')">
            <xsl:value-of select="abstractrecord:setNonpatrefcount($abstractrecord,number($NONREF-CNT))"/>
			<xsl:variable name="OTHER-REFS-LINK">
			  /search/results/otherref.url?<xsl:value-of select="$NP-REFERENCES-LINK-CID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;PAGEINDEX=<xsl:value-of select="$CURRENT-PAGE"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=ReferencesFormat&amp;docid=<xsl:value-of select="$DOC-ID"/>
			</xsl:variable>
			<xsl:value-of select="abstractrecord:setOtherrefslink($abstractrecord, $OTHER-REFS-LINK)"/>
		</xsl:if>

		<xsl:if test="($CIT-CNT) and not($CIT-CNT ='0') and not($CIT-CNT ='')">
	       <xsl:value-of select="abstractrecord:setCiterefcount($abstractrecord,number($CIT-CNT))"/>
			<xsl:variable name="CITEDBY-REFS-LINK">
			/search/results/<xsl:value-of select="translate($SEARCH-TYPE, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" />.url?CID=<xsl:value-of select="$CID-PREFIX"/>CitationFormat&amp;<xsl:value-of select="$CITEDBY-QSTR"/>&amp;yearselect=yearrange&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;CITEDBYFLAG=yes&amp;sort=yr
			</xsl:variable>
			<xsl:value-of select="abstractrecord:setCitedbyrefslink($abstractrecord, $CITEDBY-REFS-LINK)"/>
		</xsl:if>


		<xsl:variable name="CPX-DOCID"><xsl:value-of select="CPX-DOCID" /></xsl:variable>

		<xsl:if test="count(CPX-DOCID)>0">
			<xsl:variable name="COMPENDEX-LINK">
			&lt;a class="externallink" href="/search/doc/detailed.url?SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;CID=<xsl:value-of select="$CID-PREFIX" />DetailedFormat&amp;DOCINDEX=<xsl:value-of select="INDEX"/>&amp;docid=<xsl:value-of select="$CPX-DOCID" />&amp;database=1&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat&amp;displayPagination=no" title="Compendex"&gt;Compendex&lt;/a&gt;
			</xsl:variable>
			<xsl:value-of select="searchresult:setCpxlink($sr, $COMPENDEX-LINK)"/>
		</xsl:if>

		<xsl:variable name="INS-DOCID"><xsl:value-of select="INS-DOCID" /></xsl:variable>

		<xsl:if test="count(INS-DOCID)>0">
			<xsl:variable name="INSPEC-LINK">
			&lt;a class="externallink" href="/search/doc/detailed.url?SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;CID=<xsl:value-of select="$CID-PREFIX" />DetailedFormat&amp;DOCINDEX=<xsl:value-of select="INDEX"/>&amp;docid=<xsl:value-of select="$INS-DOCID" />&amp;database=2&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat&amp;displayPagination=no" title="Inspec"&gt;Inspec&lt;/a&gt;
			</xsl:variable>
			<xsl:value-of select="searchresult:setInspeclink($sr, $INSPEC-LINK)"/>
		</xsl:if>

	</xsl:template>

	<xsl:template match="PIM">
		<xsl:param name="sr"/>
		<xsl:variable name="LABEL"><xsl:value-of select="@label" /></xsl:variable>
		<xsl:choose>
			<xsl:when test="PI">
				<xsl:for-each select="PI">
					<xsl:value-of select="searchresult:addPim($sr,text())"/>
			   </xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
			    <xsl:value-of select="searchresult:addPim($sr,text())"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="DT">
		<xsl:param name="sr"/>
		<xsl:variable name="LABEL"><xsl:value-of select="@label" /></xsl:variable>
		<xsl:choose>
			<xsl:when test="D">
				<xsl:for-each select="D">
					<xsl:value-of select="searchresult:addDt($sr,text())"/>
			   </xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
			    <xsl:value-of select="searchresult:addDt($sr,text())"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>