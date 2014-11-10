<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
  xmlns:SU="java:org.ei.util.StringUtil"
  exclude-result-prefixes="xsl DD SU">
<xsl:output method="html"/>

<xsl:template match="RSS">
	<rss version="2.0">
       		<channel>
        		<title><xsl:value-of select="CATEGORY-TITLE"/></title>
  			<xsl:text disable-output-escaping="yes"><![CDATA[<link>]]></xsl:text>
				http://daypass.engineeringvillage.com
  			<xsl:text disable-output-escaping="yes"><![CDATA[</link>]]></xsl:text>
  			<description>Engineering Village RSS results for database
    				<xsl:value-of select="DD:getDisplayName(DBMASK)"/>
    				and search query of <xsl:value-of select="CATEGORY-TITLE"/>
  			</description>
  			<language>en-us</language>
  			<ttl>2880</ttl>
  			<image>
			    http://localhost/engresources/images/ev2logo5.gif
    			</image>
  			<copyright>Copyright 2010 Elsevier Inc. All rights reserved.</copyright>
  			<xsl:apply-templates select="EI-DOCUMENT"/>
  		</channel>
  	</rss>
</xsl:template>

<xsl:template match="EI-DOCUMENT">
  <item>
    <title>
    	<xsl:value-of select="TI"/>
    </title>
    <xsl:apply-templates select="AUS"/>
    <description>
    	<xsl:value-of select="SU:teaser(AB)"/>...


    	<p/>
    	For more information about this article and other similar
    	articles subscribe to Engineering Village Day Pass.
    </description>

    <xsl:text disable-output-escaping="yes"><![CDATA[<link>]]></xsl:text>
	http://daypass.engineeringvillage.com
    <xsl:text disable-output-escaping="yes"><![CDATA[</link>]]></xsl:text>
    <guid>
    	<xsl:value-of select="DOC/DOC-ID"/>
    </guid>
  </item>
</xsl:template>

<xsl:template match="AUS">
	<author><xsl:apply-templates select="AU"/> </author>
</xsl:template>

<xsl:template match="AFS"></xsl:template>

<xsl:template match="AU">
	<xsl:value-of select="text()"/>;
</xsl:template>

</xsl:stylesheet>













