<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="xsl html"
  >

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="xsl:* html:*" />

<!-- This xsl displays  Navigation Bar for both quick REMOTE and expert REMOTE searchs. -->

<xsl:template match="NAVIGATION-BAR">
		<xsl:param name="SESSION-ID"/>
		<xsl:param name="SELECTED-DB"/>

		<xsl:variable name="SEARCH-TYPE">
  		<xsl:value-of select="//SESSION-DATA/SEARCH-TYPE"/>
		</xsl:variable>

			<!-- Table to display the refine search and new search buttons -->
			<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
				<tr>
          <td align="left" valign="middle" height="24">
            <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
						<a href="#searchAnchor"><img src="/engresources/images/rs.gif" border="0" /></a>
            <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
						<xsl:choose>
							<xsl:when test="($SEARCH-TYPE='Quick')">
									<a href="/controller/servlet/Controller?CID=quickSearch&amp;database={$SELECTED-DB}">
									  <img src="/engresources/images/ns.gif" border="0" />
								  </a>
							</xsl:when>
							<xsl:otherwise>
								<a href="/controller/servlet/Controller?CID=expertSearch&amp;database={$SELECTED-DB}">
									<img src="/engresources/images/ns.gif" border="0" />
								</a>
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
			</table>

		</xsl:template>

</xsl:stylesheet>