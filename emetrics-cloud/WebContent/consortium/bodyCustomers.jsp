<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/display.tld" prefix="display" %>

<%@ page import="org.ei.struts.emetrics.Constants"%>
<%@ page import="org.ei.struts.emetrics.customer.view.UserContainer"%>
<%@ page import="org.ei.struts.emetrics.customer.view.UserView"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>

<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" align="left">

			<table border="0" width="90%" cellspacing="0" cellpadding="0">
			<tr>
				<td valign="top" width="2" bgcolor="#CCCCCC" height="20"><html:img pageKey="images.spacer" border="0" width="2" height="20"/></td>
				<td width="190" valign="middle" bgcolor="#CCCCCC"><html:img pageKey="images.spacer" border="0" width="2" height="20"/></td>
				<td valign="top" width="6"><html:img pageKey="images.spacer" border="0" width="6"/></td>
				<td width="75%">

<%
	if(session.getAttribute(Constants.SWITCH_VIEW_KEY) != null)
	{
		List x = ((List) session.getAttribute(Constants.SWITCH_VIEW_KEY));
%>
		<p align="center">
		<html:link styleClass="LgBlueText" forward="switchback"><bean:message key="label.link.return"/></html:link>
		</p>
<%
	}
%>

					<table border="0" width="100%" cellspacing="0" cellpadding="0">
						<tr><td valign="top" height="2"><img src="/emetrics/images/spacer.gif" height="2" border="0"></td></tr>
						<tr><td valign="top" height="2"><img src="/emetrics/images/spacer.gif" height="2" border="0"></td></tr>
						<tr><td valign="top" height="2" bgcolor="#396DA5"><img src="/emetrics/images/spacer.gif" height="2" border="0"></td></tr>
						<tr><td valign="top" height="2"><img src="/emetrics/images/spacer.gif" height="2" border="0"></td></tr>
					</table>
				</td>
			</tr>

			<tr>
				<td valign="top" width="2" bgcolor="#CCCCCC" height="20"><html:img pageKey="images.spacer" border="0" width="2" height="20"/></td>
				<td width="190" valign="middle" bgcolor="#CCCCCC"><html:img pageKey="images.spacer" border="0" width="2" height="20"/></td>
				<td valign="top" width="6"><html:img pageKey="images.spacer" border="0" width="6"/></td>
				<td width="75%">

					<table border="0" cellspacing="1" cellpadding="1">
						<tr>
							<th>&nbsp;</th>
							<th>Id</th>
							<th>Member name</th>
							<th>Last access</th>
						</tr>
<%
// create bean with parent consortium id
    int thisparentid = ((UserContainer) session.getAttribute(Constants.USER_CONTAINER_KEY)).getUserView().getCustomerId();
    request.setAttribute("parentid", String.valueOf(thisparentid));

%>
							<tr>
								<td> <html:link forward="switchparent" paramId="customerId" paramName="parentid" styleClass="LgBlueLink"><bean:message key="label.link.switch"/></html:link> </td>
								<td> <%= ((UserContainer) session.getAttribute(Constants.USER_CONTAINER_KEY)).getUserView().getCustomerId() %> </td>
								<td> <%= ((UserContainer) session.getAttribute(Constants.USER_CONTAINER_KEY)).getUserView().getName() %> <B>(Parent Data Only)</B> </td>
								<td> N/A </td>
							</tr>

						<%
							Collection members = ((UserContainer) session.getAttribute(Constants.USER_CONTAINER_KEY)).getUserView().getConsortium();
							request.setAttribute("members", members);
						%>
						<logic:iterate id="aMember" name="members" type="java.util.Map">
							<tr>
								<td> <html:link forward="switch" paramId="customerId" paramName="aMember" paramProperty="custid" styleClass="LgBlueLink"><bean:message key="label.link.switch"/></html:link> </td>
								<td> <bean:write name="aMember" property="custid"/></td>
								<td> <bean:write name="aMember" property="name"/></td>
								<td> <bean:write name="aMember" property="last"/></td>
							</tr>
						</logic:iterate>
					</table>

				</td>
			</tr>

			</table>

		</td>
	</tr>
</table>

