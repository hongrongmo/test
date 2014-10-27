<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl">

<xsl:template name="SMALL-LOGIN-FORM">

<xsl:param name="DB"/>
<xsl:param name="NEXT-URL"/>

<!-- Table for Personal Profile -->
<tr><td valign="top" height="15" bgcolor="#3173B5" colspan="3"><a CLASS="LgWhiteText"><b>&#160; Personal Account</b></a></td></tr>
<tr><td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
	<td valign="top" bgcolor="#FFFFFF">
		  <!-- personal tiny form -->
		  <table border="0" cellspacing="0" cellpadding="0">
			  <form name="personallogin" method="post" action="/controller/servlet/Controller?CID=personalLoginConfirm" onSubmit="return validateLogin(personallogin)">
  		  	  <input type="hidden" name="nexturl"><xsl:attribute name="value"><xsl:value-of select="java:encode($NEXT-URL)"/></xsl:attribute></input>
		  	  <input type="hidden" name="database"><xsl:attribute name="value"><xsl:value-of select="$DB"/></xsl:attribute></input>
			  <tr><td valign="top" height="6" colspan="3"><img src="/static/images/spacer.gif" border="0" height="6"/></td></tr>
			  <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td>
				  <td valign="top">
					<a CLASS="ExSmRedText">
					<xsl:attribute name="href">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=personalAccountForm&amp;newregistration=true&amp;database=<xsl:value-of select="$DB"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXT-URL)"/></xsl:attribute>
					Register</a>
					<a class="ExSmBlackText"> or Login:</a></td>
				  <td valign="top" width="2"><img src="/static/images/spacer.gif" border="0" width="2"/></td></tr>
			  <tr><td colspan="3" height="4"><img src="/static/images/spacer.gif" height="4"/></td></tr>
			  <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td>
				  <td valign="top"><a class="ExSmBlackText"><label for="txtUsr">Username:</label></a><br/><span class="ExSmBlackText"><input id="txtUsr" type="text" size="16" class="ExSmBlackText" name="email"/></span></td>
				  <td valign="top" width="2"><img src="/static/images/spacer.gif" border="0" width="2"/></td></tr>
			  <tr><td colspan="3" height="4"><img src="/static/images/spacer.gif" height="4"/></td></tr>
			  <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td>
				  <td valign="top"><a class="ExSmBlackText"><label for="txtPwd">Password:</label></a><br/><span class="ExSmBlackText"><input id="txtPwd" type="password" size="16" class="ExSmBlackText" name="password"/></span></td>
				  <td valign="top" width="2"><img src="/static/images/spacer.gif" border="0" width="2"/></td></tr>
			  <tr><td colspan="3" height="4"><img src="/static/images/spacer.gif" height="4"/></td></tr>
			  <tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td>
				  <td valign="top"><input type="image" src="/static/images/login.gif" border="0"/></td>
				  <td valign="top" width="2"><img src="/static/images/spacer.gif" border="0" width="2"/></td></tr>
			  <tr><td valign="top" height="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
			  </form>
		 </table>
		  <!-- end of tiny form -->
	</td>
	<td valign="top" bgcolor="#3173B5" width="1"><img src="/static/images/spacer.gif" border="0" width="1"/></td></tr>
<tr><td valign="top" colspan="3" bgcolor="#3173B5" height="1"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
<tr><td valign="top" colspan="3" height="20"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>
<!-- End of Table for personal profile -->


</xsl:template>
</xsl:stylesheet>