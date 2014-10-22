<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>



	<bean:size id="resultsize" name="RESULT" />
	
	<logic:greaterThan name="resultsize" value="0">
		<tr><td valign="top" colspan="3" height="10"><html:img height="10" border="0" pageKey="images.spacer"/></td></tr>
		<tr><td valign="top" colspan="3" height="2" bgcolor='<bean:write name="Border"/>' ><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
		<tr><td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
			<td valign="top">
				<!-- Start of table just for the form -->
				<table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
					<tr>
						<td width="74" colspan="2" bgcolor='<bean:write name="Heading"/>'>&nbsp;</td>
						<td width="1" bgcolor='<bean:write name="Border"/>' ><html:img pageKey="images.spacer" border="0" width="1"/></td>
						<td width="425" colspan="5" bgcolor='<bean:write name="Heading"/>'>&nbsp;<span class="MedBoldBlackText"><bean:message key="prompt.value"/></span></td>
					</tr>
					<tr>
						<td colspan="8" height="2" bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" border="0" height="1"/></td>
					</tr>
					<template:get name='body'/>
				</table>
            	<!-- End of table just for the form -->
			</td>
			<td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
		</tr>
		<tr><td valign="top" colspan="3" height="2" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
    </logic:greaterThan>
