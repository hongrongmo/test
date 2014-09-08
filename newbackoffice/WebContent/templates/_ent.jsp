<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import ="org.ei.struts.backoffice.contract.item.ItemForm" %>


	<logic:iterate id="anItem" name="allItems" type="org.ei.struts.backoffice.contract.item.Item">
    <%
      ItemForm itemForm = new ItemForm();
      itemForm.setItem(anItem);
      pageContext.setAttribute("anItemForm", itemForm);
    %>
    <tr>
      <td valign="top">&nbsp;</td>
      <td valign="top">
        <html:form action="/deleteItem" onsubmit="javascript:return confirm('Are You Sure?');">
          <html:hidden name="anItemForm" property="item.itemID"/>
          <html:hidden name="anItemForm" property="item.contract.contractID"/>
          <html:hidden name="anItemForm" property="item.contract.customerID"/>
          <html:image altKey="alt.remove.contract" pageKey="images.action.x" border="0" />
          <%-- These next two elements are not part of the form, but IE insisted on wrapping unless they were inside the form tag --%>
          <span CLASS="SmBlackText"><bean:write name="anItem" property="itemID"/>.</span>
  				<html:link forward="editItem" name="anItem" property="linkParams" styleClass="MedBlueLink"><bean:write name="anItem" property="product.name" filter="true"/></html:link>
        </html:form>
      </td>
      <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
      <td valign="top">&nbsp;<span CLASS="SmBlackText">&nbsp;</span></td>
      <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
      <td valign="top">&nbsp;<span CLASS="SmBlackText"><bean:write name="anItemForm" property="abbrevNotes"/></span></td>
      <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
      <td valign="top">&nbsp;</td>
		</tr>
		<tr>
      <td valign="top" colspan="8" height="1" bgcolor='<bean:write name="Border"/>'><html:img src="images/spacer.gif" border="0" height="1"/></td>
		</tr>
	</logic:iterate>
