<xsl:stylesheet 
	version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html"/>

<xsl:include href="PrintHeader.xsl"/>
<xsl:include href="Footer.xsl"/>


<xsl:template match="PAGE">

    	<xsl:apply-templates select="HEADER"/>

	<!-- Start of the lower area below the navigation bar -->
	<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
		<tr><td valign="top" height="15" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
		<tr>
			<td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td>
			<td valign="top" colspan="2"><a class="EvHeaderText">Print Selected Records</a></td>
		</tr>
		<tr><td valign="top" height="2" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
		<tr>
			<td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td>
			<td valign="top">
				<A CLASS="MedBlackText">
					You did not select any records to print. Please select records from the search results and try again.
				</A>
			</td>
			<td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td>
		</tr>
	</table>
	<!-- end of the lower area below the navigation bar -->
	<br/>
	<!-- Start of footer-->
	<xsl:apply-templates select="FOOTER"/>
</xsl:template>

</xsl:stylesheet>
