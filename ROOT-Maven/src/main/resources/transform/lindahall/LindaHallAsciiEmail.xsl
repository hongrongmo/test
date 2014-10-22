<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:cal="java:java.util.GregorianCalendar"
    exclude-result-prefixes="cal java xsl"
>

<!--
This xsl file will display the data of selected records from the  Search Results in an Citation format.
All the business rules which govern the display format are also provided.
-->

<xsl:include href="common/LindaHallResults.xsl" />

<xsl:output method="text" indent="no"/>

<xsl:preserve-space elements="text"/>


    <xsl:template match="text()">
    </xsl:template>

    <xsl:template match="PAGE">
        <xsl:apply-templates select="PAGE-RESULTS"/>
    </xsl:template>

    <xsl:template match="PAGE-RESULTS">
        <xsl:apply-templates select="PAGE-ENTRY"/>
    </xsl:template>

    <xsl:template match="PAGE-ENTRY">
        <xsl:apply-templates select="EI-DOCUMENT"/>
    </xsl:template>

</xsl:stylesheet>
