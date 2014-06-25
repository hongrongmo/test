<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
    


<xsl:template match="PAGE">
	<html>
	<body>
	<table border="1">
		<tr>
			<td colspan="20">
				<h2> Trial User Account</h2>
			</td>
		</tr>
		<tr>
			<td nowrap="true">
				<b><xsl:text>FIRST-NAME</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>LAST-NAME</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>JOB-TITLE</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>WEB-SITE</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>COMPANY</xsl:text></b>
			</td>
			<td >
				<b><xsl:text>ADDRESS1</xsl:text></b>
			</td>
			<td>
				<b><xsl:text>ADDRESS2</xsl:text></b>
			</td>
			<td>
				<b><xsl:text>CITY</xsl:text></b>
			</td>
			<td>
				<b><xsl:text>STATE</xsl:text></b>
			</td>
			<td>
				<b><xsl:text>COUNTRY</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>PHONE-NUMBER</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>EMAIl-ADDRESS</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>HOW-HEAR</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>HOW-HEAR-EXPLAIN</xsl:text></b>
			</td>
			<td>
				<b><xsl:text>PRODUCT</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>BY-MAIL</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>BY-EMAIL</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>REFERRING-URL</xsl:text></b>
			</td>
			<td nowrap="true">
				<b><xsl:text>TIME-STAMP</xsl:text></b>
			</td>
		</tr>
		
		<xsl:apply-templates select="TRIAL-USER" />
		<tr>
		<td colspan="20">
		<form action="/controller/servlet/Controller" method="post">
			<input type="hidden" name="CID" value="downloadTrial"/>
			<input type="hidden" name="startYear">
				<xsl:attribute name="value">
					<xsl:value-of select="START-YEAR"/>
				</xsl:attribute>
			</input>
			<input type="hidden" name="startMonth">
				<xsl:attribute name="value">
					<xsl:value-of select="START-MONTH"/>
				</xsl:attribute>
			</input>
			<input type="hidden" name="startDay">
				<xsl:attribute name="value">
					<xsl:value-of select="START-DAY"/>
				</xsl:attribute>
			</input>
			<input type="hidden" name="endYear">
				<xsl:attribute name="value">
					<xsl:value-of select="END-YEAR"/>
				</xsl:attribute>
			</input>
			<input type="hidden" name="endMonth">
				<xsl:attribute name="value">
					<xsl:value-of select="END-MONTH"/>
				</xsl:attribute>
			</input>
			<input type="hidden" name="endDay">
				<xsl:attribute name="value">
					<xsl:value-of select="END-DAY"/>
				</xsl:attribute>
			</input>
			<input type="submit" value="Save as text file"/>
		</form>
		</td>
		</tr>
	</table>
	</body>
	</html>
</xsl:template>

<xsl:template match="TRIAL-USER">
	<tr>
		<xsl:choose>
			<xsl:when test="position() mod 2">
				<xsl:attribute name="bgcolor">#EEEEEE</xsl:attribute> 
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute> 
			</xsl:otherwise>
		</xsl:choose>
		
		<td>
			<xsl:value-of select="FIRST-NAME" />
		</td>
		<td>
			<xsl:value-of select="LAST-NAME" />
		</td>
		<td>
			<xsl:value-of select="JOB-TITLE" />
		</td>
		<td>
			<xsl:value-of select="WEB-SITE" />
		</td>
		<td>
			<xsl:value-of select="COMPANY" />
		</td>
		<td>
			<xsl:value-of select="ADDRESS1" />
		</td>
		<td>
			<xsl:value-of select="ADDRESS2" />
		</td>
		<td>
			<xsl:value-of select="CITY" />
		</td>
		<td>
			<xsl:value-of select="STATE" />
		</td>
		<td>
			<xsl:value-of select="COUNTRY" />
		</td>
		<td>
			<xsl:value-of select="PHONE-NUMBER" />
		</td>
		<td>
			<xsl:value-of select="EMAIL-ADDRESS" />
		</td>
		<td>
			<xsl:value-of select="HOW-HEAR" />
		</td>
		<td>
			<xsl:value-of select="HOW-HEAR-EXPLAIN" />
		</td>
		<td>
			<xsl:value-of select="PRODUCT" />
		</td>
		<td>
			<xsl:value-of select="BY-MAIL" />
		</td>
		<td>
			<xsl:value-of select="BY-EMAIL" />
		</td>
		<td>
			<xsl:value-of select="REFERRING-URL" />
		</td>
		<td>
			<xsl:value-of select="TIME-STAMP" />
		</td>
	</tr>
	
</xsl:template>

</xsl:stylesheet>