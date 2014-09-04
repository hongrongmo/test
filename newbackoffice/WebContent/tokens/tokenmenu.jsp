<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page import="org.ei.ppd.util.IAuthentication" %>
<%@ page import="org.ei.ppd.Constants" %>

<app:checkLogon/>

<html:html>
<head>
	<title><bean:message key="global.title"/></title>
    <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
</head>

<body>

<!-- top logo area-->
<center>
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
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
							&nbsp;<label for="idtokentxt">Token search by Token Id or Email address.</label>
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
							<div style="top-margin: 0.25in;">
							<html:form action="/tokensearch">
								<html:text styleId="idtokentxt" property="tokenId" size="64" maxlength="64"/>
								<html:submit property="callmethod" ><bean:message key="action.search"/></html:submit>
							</html:form>
							<div>
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

<logic:present scope="request" name="allAuths">
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
								<th bgcolor="#CECF9C">
							    &nbsp;
								</td>
							</tr>
							<tr>
								<td valign="top" bgcolor="#000000" height="1"><html:img height="1" border="0" pageKey="images.spacer" /></td>
							</tr>
							<tr>
								<td bgcolor="#EFEFD6">

										<div style="margin-top: 0.25in; display: block;" >

											<logic:iterate indexId="idx" id="authToken" name="allAuths" type="org.ei.ppd.util.IAuthentication">

												Token id: <bean:write name="authToken" property="authString" /><br>
												Token Length: <bean:write name="authToken" property="authLength"/><br>
												Created On: <bean:write name="authToken" property="creationDate"/><br>

												<logic:equal name="authToken" property="authStatus" value="<%= String.valueOf(IAuthentication.INACTIVE) %>">
													Inactive<br>
													Expires On: <bean:write name="authToken" property="expirationDate" formatKey="format.tokendate"/><br>
												</logic:equal>
												<logic:equal name="authToken" property="authStatus" value="<%= String.valueOf(IAuthentication.ACTIVE) %>">
													Active<br>
													Start Date/Time: <bean:write name="authToken" property="activationDate" formatKey="format.tokendate"/> <br>
													Time Remaining: <bean:write name="authToken" property="timeRemaining"/> <br>
												</logic:equal>
												<logic:equal name="authToken" property="authStatus" value="<%= String.valueOf(IAuthentication.EXPIRED) %>">
													Expired/Token has been Used<br>
											  	Start Date/Time: <bean:write name="authToken" property="activationDate" formatKey="format.tokendate"/> <br>
													End Date/Time: <bean:write name="authToken" property="expirationDate" formatKey="format.tokendate"/> <br>
												</logic:equal>


													<span style="display:inline;">
														<html:form action="/support">
															<html:hidden property="tokenId" value="<%= authToken.getAuthString() %>"/>
															<html:submit property="callmethod" ><bean:message key="action.emailtoken"/></html:submit>
														</html:form>
														&nbsp;
														<html:form action="/edittoken">
															<html:hidden property="tokenId" value="<%= authToken.getAuthString() %>"/>
															<html:submit property="callmethod" ><bean:message key="action.edittoken"/></html:submit>
														</html:form>
														&nbsp;
														<%-- TO BE IMPLEMENTED -- end active session link --%>
														<logic:notEqual name="authToken" property="activeSession" value="<%= Constants.EMPTY_STRING %>">
															Kill Active Session
															<%-- <html:link paramId="tokenId" paramName="authToken" paramProperty="authString" forward="confirmtoken"><bean:message key="prompt.endsession"/></html:link> --%>
														</logic:notEqual>
														<html:form action="/tokensearch">
															<html:submit property="callmethod" ><bean:message key="action.cancel"/></html:submit>
														</html:form>
													</span>

											</logic:iterate>

										</div>

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

