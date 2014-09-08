<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
<head>
  <title><bean:write name="localholdingsForm" scope="request" property="action" filter="true"/> </title>
  <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
</head>

<body onload="javascript:loadPreview();" topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

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

		<bean:define toScope="request" id="anItem" name="localholdingsForm" property="localHoldings.item" />
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

<html:form action='/saveLocalHoldings' onsubmit="return validateLocalholdingsForm(this);">

<html:hidden name="localholdingsForm" property="action" />

<html:hidden name="localholdingsForm" property="localHoldings.localHoldingsID" />
<html:hidden name="localholdingsForm" property="localHoldings.item.itemID" />
<html:hidden name="localholdingsForm" property="localHoldings.item.productID" />
<html:hidden name="localholdingsForm" property="localHoldings.item.contract.contractID" />
<html:hidden name="localholdingsForm" property="localHoldings.item.contract.customerID" />

        <table border="0" cellspacing="0" cellpadding="0" width="600">
          <tr>
            <td valign="top" colspan="3" ><html:img height="20" border="0" pageKey="images.spacer"/></td>
          </tr>
          <tr>
            <td valign="top" colspan="3">
                <H3>Local Holdings Options</H3>
            </td>
          </tr>
          <tr><td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td></tr>
          <tr><td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
          <tr><td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
            <td valign="top">
            <!-- Start of table just for the form -->
            <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                <tr>
                	<td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                </tr>
                <tr>
					<td valign="top" align="right" colspan="2" width="33%">
						<span class="MedBlackText"><bean:message key="prompt.linkLabel"/>:</span>&nbsp;
					</td>
	                <td valign="top" colspan="2" >
						<html:text name="localholdingsForm" property="localHoldings.linkLabel" size="64" maxlength="128" />
					</td>
				</tr>
				<tr>
					<td valign="top" align="right" colspan="2" width="33%">
						<span class="MedBlackText"><bean:message key="prompt.dynamicUrl"/>:</span>&nbsp;
					</td>
					<td valign="top" colspan="2" >
						<html:textarea name="localholdingsForm" property="localHoldings.dynamicUrl" rows="5" cols="60" />
					</td>
				</tr>
				<tr>
					<td valign="top" align="right" colspan="2" width="33%">
						<span class="MedBlackText"><bean:message key="prompt.defaultUrl"/>:</span>&nbsp;
					</td>
					<td valign="top" colspan="2" >
						<html:textarea name="localholdingsForm" property="localHoldings.defaultUrl" rows="5" cols="60" />
					</td>
				</tr>
<tr>
	<td valign="top" align="right" colspan="2" width="33%">
		<span class="MedBlackText"><bean:message key="prompt.futureUrl"/>:</span>&nbsp;
	</td>
	<td valign="top" colspan="2" >
		<html:textarea onchange="loadPreview()" name="localholdingsForm" property="localHoldings.futureUrl" rows="5" cols="60" />
	</td>
</tr>
<tr>
	<td valign="top" align="right" colspan="2" width="33%">
		&nbsp;
	</td>
	<td valign="top" colspan="2" >
		<html:img imageName="preview" altKey="" pageKey="images.spacer" border="0"/>
	</td>
</tr>
                <tr>
        			<td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                </tr>
                <tr>
					<td valign="top" colspan="3">
						<html:img border="0" pageKey="images.spacer"/>
					</td>
					<td valign="top">
						<html:image altKey="" pageKey="images.save_olive" border="0"/>
						<html:image altKey="" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
					</td>
				</tr>
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
</html:form>

      </td>
    </tr>
  </table>
</center>
<!-- End of body portion -->

<html:javascript formName="localholdingsForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>

<script language="Javascript1.1">
<!--
function loadPreview() {

	if(typeof(document.forms["localholdingsForm"]["localHoldings.futureUrl"]) != 'undefined') {
		var imgurl = document.forms["localholdingsForm"]["localHoldings.futureUrl"].value;
		if((imgurl != '') && (imgurl.search(/^http/))) {
      document.images["preview"].src="http://www.engineeringvillage2.org"+imgurl;
			alert("EV2 Image Preview below.");
    }
		else if(imgurl != '') {
			document.images["preview"].src=imgurl;
			alert("Image Preview below.");
		}
		else {
			document.images["preview"].src='<html:rewrite page="/images/spacer.gif"/>';
		}
	}
	return true;
}
// -->
</script>

</body>
</html:html>
