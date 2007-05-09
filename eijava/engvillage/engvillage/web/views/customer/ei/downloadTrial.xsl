<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:cal="java:java.util.GregorianCalendar"
	xmlns:str="java:org.ei.util.StringUtil"
	exclude-result-prefixes="cal java xsl">

<xsl:output method="text" media-type="text" indent="no"/>
<xsl:preserve-space elements="text"/>

<xsl:template match="PAGE">	
		<xsl:text>INDEX-ID	</xsl:text>
		<xsl:text>FIRST-NAME	</xsl:text>
		<xsl:text>LAST-NAME	</xsl:text>
		<xsl:text>JOB-TITLE	</xsl:text>
		<xsl:text>WEB-SITE	</xsl:text>
		<xsl:text>COMPANY	</xsl:text>
		<xsl:text>ADDRESS1	</xsl:text>
		<xsl:text>ADDRESS2	</xsl:text>
		<xsl:text>CITY	</xsl:text>
		<xsl:text>STATE	</xsl:text>
		<xsl:text>COUNTRY	</xsl:text>
		<xsl:text>PHONE-NUMBER	</xsl:text>
		<xsl:text>EMAIl-ADDRESS	</xsl:text>
		<xsl:text>HOW-HEAR	</xsl:text>
		<xsl:text>HOW-HEAR-EXPLAIN	</xsl:text>
		<xsl:text>PRODUCT	</xsl:text>
		<xsl:text>BY-MAIL	</xsl:text>
		<xsl:text>BY-EMAIL	</xsl:text>
		<xsl:text>REFERRING-URL	</xsl:text>
		<xsl:text>TIME-STAMP	</xsl:text>
		<xsl:text>&#xD;&#xA;</xsl:text>
		<xsl:apply-templates select="TRIAL-USER" />
</xsl:template>

<xsl:template match="TRIAL-USER">
	<xsl:value-of select="INDEX-ID" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="FIRST-NAME" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="LAST-NAME" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="JOB-TITLE" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="WEB-SITE" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="COMPANY" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="ADDRESS1" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="ADDRESS2" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="CITY" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="STATE" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="COUNTRY" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="PHONE-NUMBER" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="EMAIL-ADDRESS" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="HOW-HEAR" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="HOW-HEAR-EXPLAIN" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="PRODUCT" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="BY-MAIL" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="BY-EMAIL" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="REFERRING-URL" />
	<xsl:text>	</xsl:text>
	<xsl:value-of select="TIME-STAMP" />
	<xsl:text>&#xD;&#xA;</xsl:text>
</xsl:template>
</xsl:stylesheet>