<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://cars-services.elsevier.com/cars/server"
xmlns:userPreferences="org.ei.session.UserPreferences">
<xsl:output method="xml" indent="no" />   
<xsl:param name="userPreferences"/>


 <xsl:template name="manra_enabled">
       <!-- <xsl:element name="manra_enabled" xmlns="http://cars-services.elsevier.com/cars/server">
       <xsl:value-of select="userPreferences:getManraEnabled($userPreferences)"></xsl:value-of>
       </xsl:element> -->
       <xsl:apply-templates select="manra_enabled//text()" ></xsl:apply-templates>
</xsl:template>

<xsl:template match="*/manra_enabled//text()">
<xsl:element name="manra_enabled">
    <xsl:value-of select="'change it all together'"/>
</xsl:element>     
</xsl:template>

   <xsl:template match="@*|node()">
     <xsl:choose>
        <xsl:when test="local-name()='manra_enabled11'">
            <xsl:call-template name="manra_enabled"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:copy>
                <xsl:apply-templates select="@*"/>
                <xsl:apply-templates/>
            </xsl:copy>
        </xsl:otherwise>
    </xsl:choose>  
 </xsl:template>
 



</xsl:stylesheet>