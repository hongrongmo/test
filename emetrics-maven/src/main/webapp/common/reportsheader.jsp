<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/META-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>

<%@ page import="org.ei.struts.emetrics.Constants"%>
<%@ page import="org.ei.struts.emetrics.customer.view.UserView"%>
<%@ page import="java.util.*"%>

<tiles:importAttribute/>
<logic:notPresent name="menu_customers" >
<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#00449E">
<tr><td valign="top" height="4" colspan="2"><html:img pageKey="images.spacer" border="0" height="4"/></td></tr>
<tr><td valign="top"><html:img styleClass="logo" pageKey="images.logo" border="0"/><span id="logoheading"><bean:message key="global.title"/></span></td><td valign="top" align="right"><html:link styleClass="LgWhiteText" forward="logout">Log out >></html:link>&nbsp;</td></tr>
<tr><td valign="top" colspan="2">
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr><td valign="top" width="100%"><html:img pageKey="images.spacer" border="0"/></td>
<td valign="top"><a href="main.do"><img src="/emetrics/images/<tiles:getAsString name="menu_main" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer" width="2" border="0"/></td>
<td valign="top"><a href="reports.do"><img src="/emetrics/images/<tiles:getAsString name="menu_report" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer" width="2" border="0"/></td>
<td valign="top"><a href="description.do"><img src="/emetrics/images/<tiles:getAsString name="menu_description" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer" width="2" border="0"/></td>
<td valign="top"><a href="support.do"><img src="/emetrics/images/<tiles:getAsString name="menu_support" />" border="0"></a></td>
</tr>
<tr><td colspan="9" height="20" bgcolor="#CCCCCC" align="right"><A CLASS="BoldBlueText">Reports updated until <bean:write name="<%=Constants.LASTUPDATE%>" scope="application"/></td></tr>
</table>
</td></tr>
</table>
</logic:notPresent>
<logic:present name="menu_customers" >
<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#00449E">
<tr><td valign="top" height="4" colspan="2"><html:img pageKey="images.spacer" border="0" height="4"/></td></tr>
<tr><td valign="top"><html:img styleClass="logo" pageKey="images.logo" border="0"/><span id="logoheading"><bean:message key="global.title"/></span></td><td valign="top" align="right"><html:link styleClass="LgWhiteText" forward="logout">Log out >></html:link>&nbsp;</td></tr>
<tr><td valign="top" colspan="2">
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr><td valign="top" width="100%"><html:img pageKey="images.spacer" border="0"/></td>
<td valign="top"><a href="main.do"><img src="/emetrics/images/<tiles:getAsString name="menu_main" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer" width="2" border="0"/></td>
<td valign="top"><a href="reports.do"><img src="/emetrics/images/<tiles:getAsString name="menu_report" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer" width="2" border="0"/></td>
<td valign="top"><a href="customers.do"><img src="/emetrics/images/<tiles:getAsString name="menu_customers" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer" width="2" border="0"/></td>
<td valign="top"><a href="description.do"><img src="/emetrics/images/<tiles:getAsString name="menu_description" />" border="0"></a></td>
<td valign="top" width="2"><html:img pageKey="images.spacer" width="2" border="0"/></td>
<td valign="top"><a href="support.do"><img src="/emetrics/images/<tiles:getAsString name="menu_support" />" border="0"></a></td>
</tr>
<tr><td colspan="10" height="20" bgcolor="#CCCCCC" align="right"><A CLASS="BoldBlueText">Reports updated until <bean:write name="<%=Constants.LASTUPDATE%>" scope="application"/></td></tr>
</table>
</td></tr>
</table>
</logic:present>


<logic:equal name="user" property="channel" value="consortium">
<!-- end of toplogo and navigation bar -->

<html:form method="POST" action="/reports.do">
  <html:select name="<%=Constants.CUSTOMER_VIEW%>" value="<%=request.getAttribute("cid").toString()%>" property="<%=Constants.CUSTOMER_ID%>" onchange='submit(); return true' >
     <html:options collection="customers" labelProperty="name" property="cust_id" />
  </html:select>
  <html:select name="<%=Constants.MONTHS%>" value="<%=session.getAttribute(Constants.MONTH).toString()%>" property="month" onchange='submit(); return true' >
     <html:options collection="<%=Constants.MONTHS%>" labelProperty="VMonth" property="month" />
  </html:select>
</html:form>
</logic:equal>

<logic:notEqual name="user" property="channel" value="consortium">
<!-- end of toplogo and navigation bar -->
<html:form method="POST" action="/reports.do">

	<html:select property="month" size="1" onchange="submit(); return true;">
		<bean:define id="allMonths" name="reportForm" property="allMonths" type="java.util.Collection"/>
		<html:options collection="allMonths" property="value" labelProperty="label"/>
	</html:select>

	<html:hidden name="reportForm" property="repid"/>

</html:form>
</logic:notEqual>