<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
<head>
  <title><bean:write name="optionsForm" scope="request" property="action" filter="true"/> </title>
  <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
</head>

<body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

<jsp:useBean id="Constants" class="org.ei.struts.backoffice.Constants" scope="application"/>

<html:errors/>

<!-- top logo area-->
<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
  <tr>
	<td valign="top" height="45" bgcolor="#CCCCCC">
	  <html:img height="45" border="0" pageKey="images.spacer"/>
	</td>
  </tr>
</table>
</center>
<!-- End of top logo area -->

<!-- Start of body portion -->
<center>
  <table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
	  <td valign="top" width="190" bgcolor="#CCCCCC">

		<bean:define toScope="request" id="anItem" name="optionsForm" property="options.contractItem" />
		<bean:define toScope="request" id="aContract" name="anItem" property="contract" />
		<bean:define toScope="request" id="aCustomer" name="aContract" property="customer" />

		<template:insert template='/templates/nav.jsp'>
		  <template:put name='links' content='/templates/_links.jsp'/>
		</template:insert>

	  </td>
	  <td valign="top" width="10">
		<html:img width="10" border="0" pageKey="images.spacer"/>
	  </td>
	  <td valign="top">
		<!-- Start of table to put the form -->

		<bean:define toScope="request" id="Border" value="#BCBD78"/>
		<bean:define toScope="request" id="Inner" value="#EFEFD6"/>

		<table border="0" cellspacing="0" cellpadding="0" width="600">
		  <tr>
			<td valign="top" colspan="3" ><html:img height="20" border="0" pageKey="images.spacer"/></td>
		  </tr>
		  <tr>
			<td valign="top" colspan="3">
				<H3><bean:write name="anItem" property="product.name" filter="true"/>&nbsp;Product Options</H3>
			</td>
		  </tr>
		  <tr><td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td></tr>
		  <tr><td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
		  <tr><td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
			<td valign="top">
			  <!-- Start of table just for the form -->
			  <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>

<html:form action="/saveDDS" onsubmit="return validateDdsForm(this);">
	<html:hidden name="optionsForm" property="action"/>
	<html:hidden name="optionsForm" property="options.contractID"/>
	<html:hidden name="optionsForm" property="options.customerID"/>
	<html:hidden name="optionsForm" property="options.product"/>

<tr>
	<td valign="top" width="3">
		<html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
	<td valign="middle" align="right">
		<span CLASS="MedBlackText">Customer ID:</span>
	</td>
	<td valign="top" width="10">
		<html:img pageKey="images.spacer" border="0" width="10"/>
	</td>
	<td valign="top">
		<span CLASS="MedBlackText"><bean:write name="optionsForm" property="options.customerID"/></span>
	</td>
	<td valign="top" width="3">
		<html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
</tr>
<tr>
	<td valign="top" width="3">
		<html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
	<td valign="middle" align="right">
		<span CLASS="MedBlackText"><bean:message key="prompt.firstName"/>:</span> &nbsp;
	</td>
	<td valign="top" width="10">
		<html:img pageKey="images.spacer" border="0" width="10"/>
	</td>
	<td valign="top">
		<span CLASS="MedBlackText"><html:text name="optionsForm" property="options.firstName"/></span>
	</td>
	<td valign="top" width="3">
		<html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
	<td valign="top" width="3">
		<html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
	<td valign="middle" align="right">
		<span CLASS="MedBlackText"><bean:message key="prompt.lastName"/>:</span> &nbsp;
	</td>
	<td valign="top" width="10">
		<html:img pageKey="images.spacer" border="0" width="10"/>
	</td>
	<td valign="top">
		<span CLASS="MedBlackText"><html:text name="optionsForm" property="options.lastName"/></span>
	</td>
	<td valign="top" width="3">
		<html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText"><bean:message key="prompt.shipping"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:radio property="options.shipping" value="c" />
	<span CLASS="MedBlackText">Centralized</span>&nbsp;
	<html:radio property="options.shipping" value="D" />
	<span CLASS="MedBlackText">Decentralized</span>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText"><bean:message key="prompt.email"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:text property="options.email" size="28"/>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText"><bean:message key="prompt.company"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:text property="options.company"  size="28"/>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	  <html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText"><bean:message key="prompt.address"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:text property="options.address1"  size="28"/>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	  <html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText">&nbsp;</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:text property="options.address2"  size="28"/>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	  <html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
  <td valign="middle" align="right">
		<span CLASS="MedBlackText"><bean:message key="prompt.city"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:text property="options.city"  size="28"/>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	  <html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText"><bean:message key="prompt.state"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:text property="options.state"  size="28"/>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText"><bean:message key="prompt.zip"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:text property="options.zip"  size="28"/>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	  <html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText"><bean:message key="prompt.country"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
		<html:select property="options.country" size="1" >
			<bean:define id="allCountries" name="optionsForm" property="allCountries" type="java.util.Collection"/>
			<html:options collection="allCountries" property="value" labelProperty="label"/>
		</html:select>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	  <html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText"><bean:message key="prompt.phone"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:text property="options.phone"  size="28"/>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" width="3">
	  <html:img pageKey="images.spacer" border="0" width="3"/>
	</td>
  <td valign="middle" align="right">
	<span CLASS="MedBlackText"><bean:message key="prompt.fax"/>:</span>
  </td>
  <td valign="top" width="10">
	<html:img pageKey="images.spacer" border="0" width="10"/>
  </td>
  <td valign="top">
	<html:text property="options.fax" size="28"/>
  </td>
  <td valign="top" width="3">
	<html:img pageKey="images.spacer" border="0" width="3"/>
  </td>
</tr>
<tr>
  <td valign="top" colspan="5" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
</tr>
<tr>
  <td valign="top" colspan="5" height="8">
	  <html:img pageKey="images.spacer" border="0" height="8"/>
	</td>
</tr>

<tr>
	<td valign="top" colspan="3"><html:img pageKey="images.spacer" border="0"/></td>
	<td valign="top" >
		<html:image altKey="" pageKey="images.save_olive" border="0"/>
		<html:image altKey="" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
	</td>
	<td valign="top" ><html:img pageKey="images.spacer" border="0"/></td>
</tr>
</html:form>

			  </table>
			<!-- End of table just for the form -->
			</td>
			<td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
		  </tr>
		  <tr>
			<td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td>
		  </tr>
		</table>
		<!-- End of table to put the form -->

	  </td>
	</tr>
  </table>
</center>
<!-- End of body portion -->

<html:javascript formName="ddsForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>

</body>
</html:html>





