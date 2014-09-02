<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

	<logic:iterate id="aCredential" name="RESULT" type="org.ei.struts.backoffice.credentials.Credentials">
		<tr>
			<td colspan="2" align="center" >
				&nbsp;
				<html:link onclick="javascript:return confirm('Are you sure?');" forward="deleteAccess" property="linkParameters" name="aCredential" styleClass="MedBlueLink">
					<span CLASS="SmBlackText">Delete ?<span>
				</html:link>
			</td>
			<td width="1" valign="top" bgcolor='<bean:write name="Border"/>' ><html:img pageKey="images.spacer" border="0" width="1"/></td>
			<td valign="top" colspan="5">&nbsp;
				<html:link forward="editAccess" property="linkParameters" name="aCredential" styleClass="MedBlueLink">
					<span CLASS="SmBlackText"><bean:write name="aCredential" property="displayString" filter="true"/><span>
				</html:link>
			</td>
		</tr>
		<tr>
			<td colspan="8" height="1" bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" border="0" height="1"/></td>
		</tr>
	</logic:iterate>
