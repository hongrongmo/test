<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:equal name="regionForm" property="action" scope="request" value="Edit">
</logic:equal>

<html:html>
<head>
	<logic:equal name="regionForm" property="action" scope="request" value="Create">
		<title><bean:message key="region.title.create"/></title>
	</logic:equal>
	<logic:equal name="regionForm" property="action" scope="request" value="Edit">
		<title><bean:message key="region.title.edit"/></title>
	</logic:equal>
	<script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
</head>
<body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

<html:errors/>

<!-- top logo area-->
<center>
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" height="45" bgcolor="#CCCCCC">
				<html:img src="images/spacer.gif" border="0" height="45"/>
			</td>
		</tr>
	</table>
</center>
<!-- End of top logo area -->

<!-- Start of body portion -->
<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" width="190" bgcolor="#CCCCCC">&nbsp;</td>
		<td valign="top" width="10"><html:img src="images/spacer.gif" border="0" width="10"/></td>
		<td valign="top">
			<!-- Start of table to put the form -->
			<table border="0" cellspacing="0" cellpadding="0" width="600">
				<tr><td valign="top" colspan="3" height="20"><html:img src="images/spacer.gif" border="0" height="20"/></td></tr>
				<tr><td valign="top" colspan="3"><html:img src="images/salesregion.gif" border="0"/></td></tr>
				<tr><td valign="top" colspan="3"><a class="MedBlackText"><bean:message key="region.help"/></td></tr>
				<tr><td valign="top" colspan="3" height="4"><html:img src="images/spacer.gif" border="0" height="4"/></td></tr>
				<tr><td valign="top" colspan="3" height="2" bgcolor="#BCBD78" ><html:img src="images/spacer.gif" border="0" height="2"/></td></tr>
				<tr>
					<td valign="top" width="1" bgcolor="#BCBD78">&nbsp;</td>
					<td valign="top" width="99%">
						<!-- Start of table just for the form -->
						<table border="0" cellspacing="0" cellpadding="0" width="100%" bgcolor="#EFEFD6">
							<html:form action="/saveRegion" onsubmit="return validateUserForm(this);">
							<html:hidden property="action"/>

							<tr>
								<td valign="top" colspan="4" height="4"><img src="images/spacer.gif" border="0" height="4"></td>
							</tr>
							<tr>
								<td valign="top" width="5"><img src="images/spacer.gif" border="0" width="5"></td>
								<td valign="top" align="right">
									<a CLASS="MedBlackText"><bean:message key="prompt.region"/>:</a>
								</td>
								<td valign="top" width="8"><img src="images/spacer.gif" border="0" width="8"></td>
								<td valign="top">

<html:select multiple="multiple" property="regions" size="5">
	<bean:define id="allRegions" name="regionForm" property="regions" type="java.util.Collection"/>
	<html:options collection="allRegions" property="regionID" labelProperty="regionName"/>
</html:select>


								</td>
							</tr>
							<tr><td valign="top" colspan="4" height="8"><img src="images/spacer.gif" border="0" height="8"></td></tr>
							<tr>
								<td valign="top" width="5"><img src="images/spacer.gif" border="0" width="5"></td><td valign="middle" align="right">
									<a CLASS="MedBlackText"><bean:message key="region.prompt.create"/>:</a>
								</td>
								<td valign="top" width="8"><img src="images/spacer.gif" border="0" width="8"></td>
								<td valign="top">
									<a CLASS="MedBlackText"><html:text property="regionName" size="28" maxlength="32"/></a>
								</td>
							</tr>
							<tr>
								<td valign="top" colspan="4" height="8"><img src="images/spacer.gif" border="0" height="8"></td>
							</tr>
							<tr>
								<td valign="top" colspan="3"><img src="images/spacer.gif" border="0"></td>
								<td valign="top"><img src="images/save_olive.gif" border="0">&nbsp; <img src="images/reset_olive.gif" border="0"></td>
							</tr>

							</html:form>
						</table>
						<!-- End of table just for the form -->
					</td>
					<td valign="top" width="1" bgcolor="#BCBD78">&nbsp;</td>
				</tr>
				<tr>
					<td valign="top" colspan="3" height="2" bgcolor="#BCBD78"><html:img src="images/spacer.gif" border="0" height="2"/></td>
				</tr>
			</table>
			<!-- End of table to put the form -->
		</td>
	</tr>
</table>
</center>

<html:javascript formName="regionForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src="staticJavascript.jsp"></script>

</body>
</html:html>
