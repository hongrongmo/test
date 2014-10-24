<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:searchresult="java:org.ei.stripes.view.SearchResult"
	xmlns:java="java:java.net.URLEncoder" exclude-result-prefixes="java html xsl searchresult">

	<xsl:output method="html" indent="no" />
	<xsl:strip-space elements="html:* xsl:*" />



	<!-- ********************************************************* -->
	<!-- Apply local holdings links for CITATION results -->
	<!-- ********************************************************* -->
	<xsl:template name="LOCAL-HOLDINGS">
		<xsl:param name="sr" />
		<xsl:param name="vISSN" />
		<xsl:param name="LOCAL-HOLDINGS" />
       
		<xsl:for-each select="$LOCAL-HOLDINGS/LOCAL-HOLDING">
			<xsl:apply-templates select=".">
				<xsl:with-param name="sr" select="$sr" />
				<xsl:with-param name="vISSN" select="$vISSN" />
			</xsl:apply-templates>
		</xsl:for-each>
	</xsl:template>

	<!-- ********************************************************* -->
	<!-- Template to create the links per LOCAL-HOLDING element    -->
	<!-- ********************************************************* -->
	<xsl:template match="LOCAL-HOLDING">
	
		<xsl:param name="sr" />
		<xsl:param name="vISSN" />
		
		<xsl:choose>
			<xsl:when test="POSITION">
				<xsl:variable name="URL">
					<xsl:choose>
						<xsl:when test="not($vISSN='') and not($vISSN='null')">DYNAMIC-URL</xsl:when>
						<xsl:when test="not(DEFAULT-URL='')">DEFAULT-URL</xsl:when>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="POSITION"><xsl:value-of select="POSITION" /></xsl:variable>
				<xsl:variable name="IMAGEURL"><xsl:value-of select="FUTURE-URL" /></xsl:variable>
				<xsl:variable name="LABEL"><xsl:value-of select="LINK-LABEL" /></xsl:variable>
				<xsl:value-of select="searchresult:addLhlinkObjects($sr,$URL,$POSITION,$IMAGEURL,$LABEL)"></xsl:value-of>
			</xsl:when>
			<!-- <xsl:otherwise>
				<xsl:variable name="LINK">
					<xsl:choose>
						<xsl:when test="not($vISSN='') and not($vISSN='null')">
							<xsl:choose>
								<xsl:when test="not(FUTURE-URL='')">
									&lt;a CLASS="LgBlueLink" href="<xsl:value-of select="DYNAMIC-URL" />" target="new"&gt;
										&lt;img src="<xsl:value-of select="FUTURE-URL" />" alt="<xsl:value-of select="LINK-LABEL" />" border="0" /&gt;
									&lt;/a&gt;
								</xsl:when>
								<xsl:otherwise>
									&lt;a CLASS="LgBlueLink" href="<xsl:value-of select="DYNAMIC-URL" />" target="new"&gt;
										<xsl:value-of select="LINK-LABEL" />
									&lt;/a&gt;
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="not(DEFAULT-URL='')">
							<xsl:choose>
								<xsl:when test="not(FUTURE-URL='')">
									&lt;a CLASS="LgBlueLink" href="<xsl:value-of select="DEFAULT-URL" />" target="new"&gt;
										&lt;img src="<xsl:value-of select="FUTURE-URL" />" alt="<xsl:value-of select="LINK-LABEL" />" border="0" /&gt;
									&lt;/a&gt;
								</xsl:when>
								<xsl:otherwise>
									&lt;a CLASS="LgBlueLink" href="<xsl:value-of select="DEFAULT-URL" />" target="new"&gt;
										<xsl:value-of select="LINK-LABEL" />
									&lt;/a&gt;
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
					</xsl:choose>
				</xsl:variable>
				
				*************** Add the link to the list in the SearchResult object
				<xsl:value-of select="searchresult:addLhlink($sr,$LINK)"></xsl:value-of>
			</xsl:otherwise> -->
		</xsl:choose>


		
		
	</xsl:template>
</xsl:stylesheet>