<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />
<xsl:template name="HEADERNULL">
<xsl:param name="PRINT" select="boolean(false)"></xsl:param>
<div id="header" style="width:100%; min-width: 100%">
	<div id="logoEV">
		<a href="/controller/servlet/Controller?CID=home"><img src="/static/images/EV-logo.gif"/></a>
<xsl:if test="$PRINT">
	<div style="float:right; padding: 7px">
       <A href="javascript:window.print()"><IMG src="/static/images/Print.png" border="0"/></A>&#160;&#160;
	</div>
	<div class="clear"></div>
</xsl:if>
	</div>

	<div id="headerLink">
		<div class="clearfix" id="suites">&#160;</div>
	</div>

	<div class="navigation txtLarger clearfix">&#160;</div>
</div>
</xsl:template>

</xsl:stylesheet>