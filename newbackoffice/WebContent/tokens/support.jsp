<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<app:checkLogon/>

<html:html>
<head>
	<title><bean:message key="global.title"/></title>
	<script language="JavaScript" src='<html:rewrite page="/js/stylesheet.js"/>'/>
	
</head>

<body>

<!-- top logo area-->
<center>
	<table border="1" width="99%" cellspacing="0" cellpadding="0">
		<tr><td valign="top"><html:img border="0" pageKey="images.logo" /></td></tr>
		<tr><td valign="top" height="2"><html:img height="2" border="0" pageKey="images.spacer" /></td></tr>
		<tr><td valign="top" height="2" bgcolor="#000000"><html:img height="2" border="0" pageKey="images.spacer" /></td></tr>
	</table>
</center>

<html:errors/>

<!-- Start of body portion -->
<center>

	<table border="0" cellspacing="0" cellpadding="0" width="450">
		<tr><td valign="top" colspan="3" height="20"><html:img height="20" border="0" pageKey="images.spacer" /></td></tr>
		<tr><td valign="top" height="1" bgcolor="#000000" colspan="3"><html:img height="1" border="0" pageKey="images.spacer" /></td></tr>
		<tr>
			<td valign="top" width="1" bgcolor="#000000"><html:img width="1" border="0" pageKey="images.spacer" /></td>
			<td valign="top">
				<!-- Start of inner table for names -->
				<table border="0" cellspacing="0" cellpadding="0" width="450">
					<tr>
						<th valign="middle" bgcolor="#CECF9C">
					    &nbsp;
						</td>
						<td valign="top" width="1" bgcolor="#000000"></td>
						<th valign="middle" bgcolor="#CECF9C">
							&nbsp;Email token to User.
						</td>
						<td valign="top" width="1" bgcolor="#000000"></td>
						<th valign="middle" bgcolor="#CECF9C">
							&nbsp;
						</td>
						<td valign="top" width="1" bgcolor="#000000"></td>
						<th valign="middle" bgcolor="#CECF9C">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td valign="top" colspan="7" bgcolor="#000000" height="1"><html:img height="1" border="0" pageKey="images.spacer" /></td>
					</tr>
					<tr>
						<td valign="top" bgcolor="#EFEFD6">
							<br>&nbsp;
						</td>
						<td valign="top" width="1" bgcolor="#CCCCCC"></td>
						<td valign="top" bgcolor="#EFEFD6">

							<div style="margin-top: 0.25in; display: block;" >
                <span class="formheading">Email Token</span>
                <br>
                Please add any relevant information below. DO NOT include ANY Credit Card number, user name or password in this form.
							</div>

							<div style="margin-top: 0.25in; display: block;" >
								<html:form action="/tokensearch">
									<html:hidden property="tokenId" value='<%= request.getParameter("tokenId") %>'/>
									<html:textarea property="note" rows="10" cols="40"/>
										<div style="margin-top: 0.25in; display: block;" >
											<html:submit property="callmethod" ><bean:message key="action.emailtoken"/></html:submit>
											<html:submit property="callmethod" ><bean:message key="action.cancel"/></html:submit>
										</div>
								</html:form>
							</div>

						</td>
						<td valign="top" width="1" bgcolor="#CCCCCC"></td>
						<td valign="top" bgcolor="#EFEFD6">
							<br>&nbsp;
						</td>
						<td valign="top" width="1" bgcolor="#CCCCCC"></td>
						<td valign="top" bgcolor="#EFEFD6">
							<br>&nbsp;
						</td>
					</tr>
				</table>
				<!-- End of inner table for names -->
			</td>
			<td valign="top" width="1" bgcolor="#000000"><html:img width="1" border="0" pageKey="images.spacer" /></td>
		</tr>
		<tr><td valign="top" colspan="3" height="1" bgcolor="#000000"><html:img height="1" border="0" pageKey="images.spacer" /></td></tr>
	</table>
</center>
<br>
<!-- End of body portion -->

<logic:present scope="request" name="authToken">
		<center>
			<table border="0" cellspacing="0" cellpadding="0" width="450">
				<tr><td valign="top" colspan="3" height="20"><html:img height="20" border="0" pageKey="images.spacer" /></td></tr>
				<tr><td valign="top" height="1" bgcolor="#000000" colspan="3"><html:img height="1" border="0" pageKey="images.spacer" /></td></tr>
				<tr>
					<td valign="top" width="1" bgcolor="#000000"><html:img width="1" border="0" pageKey="images.spacer" /></td>
					<td valign="top">
						<!-- Start of inner table for names -->
						<table border="0" cellspacing="0" cellpadding="0" width="450">
							<tr>
								<th valign="middle" bgcolor="#CECF9C">
							    &nbsp;
								</td>
							</tr>
							<tr>
								<td valign="top" bgcolor="#000000" height="1"><html:img height="1" border="0" pageKey="images.spacer" /></td>
							</tr>
							<tr>
								<td valign="middle" bgcolor="#EFEFD6">
									<p>
									<bean:write name="authToken" property="authStatus" /><br>
									<bean:write name="authToken" property="creationDate"/><br>
									<bean:write name="authToken" property="authLength"/><br>
								</td>
							</tr>
						</table>
						<!-- End of inner table for names -->
					</td>
					<td valign="top" width="1" bgcolor="#000000"><html:img width="1" border="0" pageKey="images.spacer" /></td>
				</tr>
				<tr><td valign="top" colspan="3" height="1" bgcolor="#000000"><html:img height="1" border="0" pageKey="images.spacer" /></td></tr>
			</table>
		</center>
</logic:present>

</body>
</html:html>
