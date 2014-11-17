<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/META-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>

<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#00449E">
<tr><td valign="top" height="4" colspan="2"><html:img pageKey="images.spacer" border="0" height="4"/></td></tr>
<tr><td valign="top"><html:img styleClass="logo" pageKey="images.logo" border="0"/><span id="logoheading"><bean:message key="global.title"/></span></td><td valign="top" align="right"><html:link styleClass="LgWhiteText" forward="logout">Log out >></html:link>&nbsp;</td></tr>
<tr><td valign="top" colspan="2">
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr><td valign="top" width="100%"><html:img pageKey="images.spacer" border="0"/></td>
<td valign="top"><a href="main.do"><img src="/emetrics/images/<tiles:getAsString name="menu_main" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer"width="2" border="0"/></td>
<td valign="top"><a href="reports.do"><img src="/emetrics/images/<tiles:getAsString name="menu_report" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer"width="2" border="0"/></td>
<td valign="top"><a href="customers.do"><img src="/emetrics/images/<tiles:getAsString name="menu_customers" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer"width="2" border="0"/></td>
<td valign="top"><a href="description.do"><img src="/emetrics/images/<tiles:getAsString name="menu_description" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer"width="2" border="0"/></td>
<td valign="top"><a href="support.do"><img src="/emetrics/images/<tiles:getAsString name="menu_support" />" border="0"></a></td>
</tr>
<tr><td colspan="10" height="20" bgcolor="#CCCCCC" align="right"><A CLASS="BoldBlueText"></td></tr>
</table>
</td></tr>
</table>