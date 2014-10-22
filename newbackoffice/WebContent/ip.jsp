<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
<head>
  <title><bean:write name="accessForm" scope="request" property="action" filter="true"/> </title>
  <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
</head>

<body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

<jsp:useBean id="Constants" class="org.ei.struts.backoffice.Constants" scope="application"/>

<html:errors/>

<%@ include file="includes/_conflicts.jsp" %>

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

		<bean:define toScope="request" id="anItem" name="accessForm" property="credentials.access.item" />
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

<html:form focus="credentials.lowIp" action='/saveIpAccess' onsubmit="return validateIpForm(this);">

		<html:hidden name="accessForm" property="action"/>
		<html:hidden name="accessForm" property="credentials.indexID"/>
		<html:hidden name="accessForm" property="credentials.type"/>
		<html:hidden name="accessForm" property="credentials.access.contractID"/>
		<html:hidden name="accessForm" property="credentials.access.itemID"/>

    <table border="0" cellspacing="0" cellpadding="0" width="600">
      <tr>
      <td valign="top" colspan="3" ><html:img height="20" border="0" pageKey="images.spacer"/></td>
      </tr>
      <tr>
      <td valign="top" colspan="3">
        <H3>Access Options</H3>
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
          <td valign="top" colspan="4" >
            <ul>
            <li><span class="MedBlackText">To enter an entire Class C IP range, end Low IP with .* and leave High IP blank</span></li>
            <li><span class="MedBlackText">To enter a single IP, fill in Low IP and leave High IP blank</span></li>
          </ul>
          </td>
        </tr>
        <tr>
          <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
        </tr>
        <tr>
          <td valign="top" align="left" colspan="1" width="10%">
            <html:img border="0" pageKey="images.spacer"/>
          </td>
          <td valign="top" align="left" colspan="1" width="33%">
            <label for="credentials.lowIp"><span class="MedBlackText"><bean:message key="prompt.lowip"/> :</span></label>&nbsp;
          </td>
          <td valign="top" colspan="2" >
            <label for="credentials.highIp"><span class="MedBlackText"><bean:message key="prompt.highip"/> :</span></label>&nbsp;
          </td>
        </tr>
        <tr>
          <td valign="top" align="left" colspan="1" width="10%">
            <html:img border="0" pageKey="images.spacer"/>
          </td>
          <td valign="top" align="left" colspan="1" width="33%">
            <html:img border="0" pageKey="images.spacer"/><html:text styleId="credentials.lowIp" name="accessForm" property="credentials.lowIp"/>
          </td>
          <td valign="top" colspan="2" >
            <html:text styleId="credentials.highIp" name="accessForm" property="credentials.highIp"/>
          </td>
        </tr>
        <tr>
          <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
        </tr>
        <logic:equal name="accessForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
          <tr>
            <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
          </tr>
          <tr>
            <td valign="top" colspan="4" align="center"><b>OR</b></td>
          </tr>
          <tr>
            <td valign="top" colspan="4" >
              <ul>
                <li><span class="MedBlackText">Enter multiple IPs separated by semicolons or newlines.  DO NOT use wildcards. Enter complete ranges separated by a '-' with no spaces. you may also enter single IPs.</span></li>
              </ul>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
          </tr>
          <tr>
            <td valign="top" align="right" colspan="1" width="15%">
              <span class="MedBlackText"><bean:message key="prompt.ipblock"/> :</span>&nbsp;
            </td>
            <td valign="top" colspan="3" >
              <html:textarea cols="80" rows="12" name="accessForm" property="credentials.ipBlock" />
            </td>
          </tr>
        </logic:equal>
        <tr>
          <td valign="top" colspan="3">
            <html:img border="0" pageKey="images.spacer"/>
          </td>
				<tr>
					<td valign="top" >
						<html:img border="0" pageKey="images.spacer"/>
					</td>
					<td valign="top" colspan="3">

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

<html:javascript formName="ipForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>


</body>
</html:html>
