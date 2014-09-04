<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<tr>
  <td valign="top" width="1" bgcolor='<bean:write name="Heading"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
  <td valign="top" bgcolor='<bean:write name="Heading"/>'>&nbsp;<span class="MedBoldBlackText"><bean:message key="prompt.productName"/></span></td>
  <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
  <td valign="top" bgcolor='<bean:write name="Heading"/>'>&nbsp;<span class="MedBoldBlackText">&nbsp;</span></td>
  <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
  <td valign="top" bgcolor='<bean:write name="Heading"/>'>&nbsp;<span class="MedBoldBlackText"><bean:message key="prompt.notes"/></span></td>
  <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
  <td valign="top" bgcolor='<bean:write name="Heading"/>'>&nbsp;<span class="MedBoldBlackText"><bean:message key="prompt.statusactions"/></span></td>
</tr>
<tr><td valign="top" colspan="8" height="1" bgcolor='<bean:write name="Border"/>'><html:img src="images/spacer.gif" border="0" height="1"/></td></tr>
