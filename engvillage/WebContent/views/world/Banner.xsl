<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="Footer.xsl"/>

<xsl:template match="/PAGE/BANNER">

<xsl:variable name="SOURCE-HITS">
	<xsl:value-of select="/PAGE/SOURCE-HITS"/>
</xsl:variable>

<xsl:variable name="RESULT-COUNT">
	<xsl:value-of select="/PAGE/RESULT-COUNT"/>
</xsl:variable>



<html>

<head>

<title>Welcome to Engineering Village</title>

<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
<SCRIPT LANGUAGE="Javascript" SRC="/static/js/banner.js"/>

</head>

<body bgcolor="#FFFFFF" marginwidth="0" marginheight="0" topmargin="0">
<center>
<table border="0" width="650" cellspacing="0" cellpadding="0">
	<tr><td valign="top"><img src="/static/images/ev2logo5.gif" border="0"/></td></tr>
	<tr><td valign="top" height="8"><img src="/static/images/spacer.gif" border="0" height="8"/></td></tr>
</table>
</center>

<center>
<table border="0" width="650" cellspacing="0" cellpadding="0">
	<tr><td valign="top" width="444" bgcolor="#FEC048">
			<!-- Table for left side information-->
			<table border="0" width="444" cellspacing="0" cellpadding="0" bgcolor="#FEC048">
				<tr><td valign="top" height="10" colspan="3"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
				<tr><td valign="top" colspan="3"><img src="/static/images/welcome_orange.gif" border="0"/></td></tr>
				<tr><td valign="top" height="20" colspan="3"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>

			<xsl:if test="($RESULT-COUNT>0)">
				<tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top"><a class="MedBlackText">
					<xsl:choose>
						<xsl:when test="($SOURCE-HITS>0)">
							Your search in <b>Engenius found <xsl:value-of select="$SOURCE-HITS"></xsl:value-of></b> records. The same
						</xsl:when>
						<xsl:otherwise>
							This
						</xsl:otherwise>
					</xsl:choose>
					search in <b>Engineering Village would produce <xsl:value-of select="$RESULT-COUNT"></xsl:value-of></b> records from the <xsl:value-of select="/PAGE/DATABASE"></xsl:value-of> database.</a></td>
					<td valign="top" width="8"><img src="/static/images/spacer.gif" border="0" width="8"/></td></tr>
					<tr><td valign="top" colspan="3" height="15"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
			</xsl:if>

				<tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td>
					<td valign="top"><a class="MedBlackText">Engineering Village is an online research solution empowering researchers with the content and discovery tools you need.  Engineering Village offers subscribers access to premium data  sources through a single, easy to use interface.  Available sources covering engineering and related disciplines include:</a></td>
					<td valign="top" width="8"><img src="/static/images/spacer.gif" border="0" width="8"/></td>
				</tr>
				<tr><td colspan="3" align="middle">
						<table border="0" width="375" cellspacing="0" cellpadding="0" align="middle">
							<tr><td colspan="5" height="15"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
							<tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top" colspan="2">&#160; &#160; &#160; <a class="MedBlackText" href="#" onmouseover="doTooltip(event,0)" onmouseout="hideTip()"><b>Compendex&#174; </b></a></td><td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td><td valign="top">&#160; &#160; &#160; <a class="MedBlackText" href="#" onmouseover="doTooltip(event,4)" onmouseout="hideTip()"><b>esp@cenet</b></a></td></tr>
							<tr><td valign="top" height="1" colspan="5"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
							<tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top" colspan="2">&#160; &#160; &#160; <a class="MedBlackText" href="#" onmouseover="doTooltip(event,1)" onmouseout="hideTip()"><b>INSPEC&#174; </b></a></td><td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td><td valign="top">&#160; &#160; &#160; <a class="MedBlackText" href="#" onmouseover="doTooltip(event,5)" onmouseout="hideTip()"><b>Techstreet Standards</b></a></td></tr>
							<tr><td valign="top" height="1" colspan="5"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
							<tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top" colspan="2">&#160; &#160; &#160; <a class="MedBlackText" href="#" onmouseover="doTooltip(event,2)" onmouseout="hideTip()"><b>ENGnetBASE</b></a></td><td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td><td valign="top">&#160; &#160; &#160; <a class="MedBlackText" href="#" onmouseover="doTooltip(event,6)" onmouseout="hideTip()"><b>Scirus</b></a></td></tr>
							<tr><td valign="top" height="1" colspan="5"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
							<tr><td valign="top" width="3"><img src="/static/images/spacer.gif" border="0" width="3"/></td><td valign="top" colspan="2">&#160; &#160; &#160; <a class="MedBlackText" href="#" onmouseover="doTooltip(event,3)" onmouseout="hideTip()"><b>USPTO</b></a></td><td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td><td valign="top">&#160; &#160; &#160; <a class="MedBlackText" href="#" onmouseover="doTooltip(event,7)" onmouseout="hideTip()"><b>LexisNexis News</b></a></td></tr>
							<tr><td valign="top" colspan="5" height="35"><img src="/static/images/spacer.gif" border="0" height="35"/></td></tr>
						</table>
				</td></tr>
				<tr><td valign="top" colspan="3" align="middle">
					<map name="bottombanner1_Map">
						<area shape="rect" alt="Tour our site" coords="303,111,386,126" href="#" onClick="window.open('/engresources/tour/tour_databases.html', 'newpg', 'status=yes,resizable,scrollbars=1,width=740,height=500')"/>
						<area shape="rect" alt="Register Now" coords="7,17,91,33" href="#" onClick="window.open('http://www.ei.org/eicorp/eicorp?menu=ev2freetrialmenu&amp;display=ev2freetrial', 'newpg', 'status=yes,resizable,scrollbars=1,width=740,height=500')"/>
					</map>
					<img src="/static/images/bottombanner1.gif" width="400" height="150" border="0" alt="" usemap="#bottombanner1_Map"/>
				</td></tr>
			</table>
		</td>
		<td valign="top" width="205" bgcolor="#C3C8D1">
			<!-- Outer table for right side to keep all the small tables -->
			<table border="0" width="205" bgcolor="#C3C8D1" cellspacing="0" cellpading="0">
				<tr><td valign="top">
						<!-- Small table for Username and Password box -->
						<center>
						<table border="0" width="190" cellspacing="0" cellpadding="0">
						<form target="_top" name="passwordval" method="post" action="/controller/servlet/Controller">
							<input type="hidden" name="CID" value="login"/>
							<tr><td valign="top" colspan="3" height="8"><img src="/static/images/spacer.gif" border="0" height="8"/></td></tr>
							<tr><td valign="top" colspan="3" height="8" bgcolor="#3173B5"><a class="MedWhiteText">&#160; <b>Subscriber log-in</b></a></td></tr>
							<tr><td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
								<td valign="top"  bgcolor="#FFFFFF">
									<table border="0" cellspacing="0" cellpadding="0"  bgcolor="#FFFFFF">
									<tr><td valign="middle"><a class="SmBlackText">&#160; Username:</a></td><td valign="top"><a class="SmBlackText"><input type="text" name="SYSTEM_USERNAME" size="12"/></a></td></tr>
									<tr><td valign="middle"><a class="SmBlackText">&#160; Password:</a></td><td valign="top"><a class="SmBlackText"><input type="password" name="SYSTEM_PASSWORD" size="12"/></a></td></tr>
									<tr><td valign="top" colslpan="2" height="8"><img src="/static/images/spacer.gif" border="0" height="8"/></td></tr>
									<tr><td valign="top" colspan="2" align="right"><a href="#" onClick="return validForm(passwordval)"><img src="/static/images/login1.gif" border="0"/></a></td></tr>
							<!--	<tr><td valign="top" colspan="2" align="right"><a CLASS="SmBlackText"><input type="submit" name="submit" value="Submit"/></a></td></tr> -->
									<tr><td valign="top" colslpan="2" height="6"><img src="/static/images/spacer.gif" border="0" height="6"/></td></tr>
									</table>
								</td>
								<td valign="top" width="1" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="1"/></td></tr>
							<tr><td valign="top" colspan="3" height="2" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" height="2"/></td></tr>
						</form>
						</table>
						</center>
					</td>
				</tr>
				<tr><td valign="top" height="8"><img src="/static/images/spacer.gif" border="0" height="8"/></td></tr>
				<tr><td valign="top">
						<!-- Table for features banner -->
						<center>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr><td valign="top"><img src="/static/images/featuresbanner.gif" border="0"/></td></tr>
						</table>
						</center>
				</td></tr>
				<tr><td valign="top" height="8"><img src="/static/images/spacer.gif" border="0" height="8"/></td></tr>
				<tr>
					<td valign="top">
						<!-- Categories banner -->
						<center>
						<table border="0" cellspacing="0" cellpadding="0" width="190">
							<tr><td valign="top"><img src="/static/images/categoriesbanner.gif" border="0"/></td></tr>
						</table>
						</center>
					</td>
				</tr>
			</table>
		</td>
		<td valign="top" width="1" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
	</tr>
</table>
</center>

<br/>

<xsl:apply-templates select="FOOTER"/>

<div id="tipDiv" style="position:absolute; visibility:hidden; z-index:100"></div>

</body>

</html>

</xsl:template>

<xsl:template match="text()">
</xsl:template>

</xsl:stylesheet>
