<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<app:checkLogon/>

<html:html>
<head>
	<link href="<html:rewrite page="/favicon.ico"/>" rel="shortcut icon" type="image/x-icon"/>
	<title><bean:message key="global.title"/></title>
  <script language="Javascript1.1" src="<html:rewrite page="/js/stylesheet.jsp"/>"></script>
</head>

<body>

<!-- top logo area-->
<html:img style="margin-left:0.25in;" border="0" pageKey="images.logo"/>
<!-- End of top logo area -->

<!-- Start of body portion -->
<center>

	<table border="0" cellspacing="0" cellpadding="0" width="550">
		<tr><td valign="top" colspan="3" height="20"><html:img height="20" border="0" pageKey="images.spacer" /></td></tr>
		<tr><td valign="top" height="1" bgcolor="#000000" colspan="3"><html:img height="1" border="0" pageKey="images.spacer" /></td></tr>
		<tr>
			<td valign="top" width="1" bgcolor="#000000"><html:img width="1" border="0" pageKey="images.spacer" /></td>
			<td valign="top">
				<!-- Start of inner table for names -->
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<th valign="middle" bgcolor="#CECF9C">
					    &nbsp; <a class="MedBlackText"><b>Search</b></a>
						</th>
						<td valign="top" width="1" bgcolor="#000000"></td>
						<th valign="middle" bgcolor="#CECF9C">
							&nbsp; <a class="MedBlackText"><b>Customer Maintenance</b></a>
						</th>
						<td valign="top" width="1" bgcolor="#000000"></td>
						<th valign="middle" bgcolor="#CECF9C">
							&nbsp; <a class="MedBlackText"><b>Reports</b></a>
						</th>
						<td valign="top" width="1" bgcolor="#000000"></td>
						<th valign="middle" bgcolor="#CECF9C">
							&nbsp; <a class="MedBlackText"><b>Admin</b></a>
						</th>
					</tr>
					<tr>
						<td valign="top" colspan="7" bgcolor="#000000" height="1"><html:img height="1" border="0" pageKey="images.spacer" /></td>
					</tr>
					<tr>
						<td valign="top" bgcolor="#EFEFD6">
							<br>&nbsp; <html:link page="/search.do" styleClass="LgBlueLink">Search</html:link>
							<!-- <br>&nbsp; <html:link page="/tokenmenu.do" styleClass="LgBlueLink">Token Mgmt</html:link> -->
						</td>
						<td valign="top" width="1" bgcolor="#CCCCCC"></td>
						<td valign="top" bgcolor="#EFEFD6">
							<br>&nbsp; <html:link styleClass="LgBlueLink" forward="createCustomer"><bean:message key="action.create"/> a <bean:message key="displayname.customer"/></html:link>
							<br>&nbsp; <html:link styleClass="LgBlueLink" forward="listCustomers"><bean:message key="action.list"/> All <bean:message key="displayname.customer.plural"/></html:link>
							<br>&nbsp; <html:link styleClass="LgBlueLink" forward="listConsortia"><bean:message key="action.list"/> All <bean:message key="displayname.consortium.plural"/></html:link>
	            <br>
	            <br>&nbsp; <html:link styleClass="LgBlueLink" page="/editCustomer.do?action=Edit&customerid=923358">Ei Account</html:link>
	            <br>&nbsp; <html:link styleClass="LgBlueLink" page="/editCustomer.do?action=Edit&customerid=923038">ELSCIENCE Account</html:link>
						</td>
						<td valign="top" width="1" bgcolor="#CCCCCC"></td>
						<td valign="top" bgcolor="#EFEFD6">
							<br>&nbsp; <html:link page="/report.do" styleClass="LgBlueLink">Contract Report</html:link>
							<%-- <br><br>&nbsp; <html:link page="/territory.do" styleClass="LgBlueLink">Territory Report</html:link> --%>
						</td>
						<td valign="top" width="1" bgcolor="#CCCCCC"></td>
						<td valign="top" bgcolor="#EFEFD6">
							<br>&nbsp; <html:link styleClass="LgBlueLink" forward="createUser"><bean:message key="index.user.create"/></html:link>
							<br>&nbsp; <html:link styleClass="LgBlueLink" page="/editUser.do?action=List"><bean:message key="index.user.list"/></html:link>
							<P/>
							<br>&nbsp; <html:link styleClass="LgBlueLink" forward="logout"><bean:message key="index.logoff"/></html:link></td>
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
</body>
</html:html>

