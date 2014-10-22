<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
<head>
	<title><bean:write name="contractForm" scope="request" property="action" filter="true"/> <bean:message key="displayname.contract"/></title>
	<script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
</head>

<body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

<jsp:useBean id="Constants" class="org.ei.struts.backoffice.Constants" scope="application"/>

<bean:define toScope="request" id="Border" value="#8E92BF"/>
<bean:define toScope="request" id="Heading" value="#C1C3DB"/>
<bean:define toScope="request" id="Inner" value="#E5E5F1"/>
<bean:define toScope="request" id="saveKey" value="images.action.saveproduct.lilac"/>
<bean:define toScope="request" id="resetKey" value="images.action.reset.lilac"/>

<html:errors/>

<!-- top logo area-->
<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" height="45" bgcolor="#CCCCCC">
			<html:img height="45" border="0" src="images/spacer.gif"/>
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

      <logic:notEmpty name="contractForm" property="contract.contractID" >
        <bean:define toScope="request" id="aContract" name="contractForm" property="contract" />
      </logic:notEmpty>

      <bean:define toScope="request" id="aCustomer" name="contractForm" property="contract.customer" />

      <template:insert template='/templates/nav.jsp'>
        <template:put name='links' content='/templates/_links.jsp'/>
      </template:insert>

		</td>
		<td valign="top" width="10">
			<html:img width="10" border="0" src="images/spacer.gif"/>
		</td>
		<td valign="top">
			<!-- Start of table to put the form -->
			<table border="0" cellspacing="0" cellpadding="0" width="600">
				<tr><td valign="top" colspan="3" height="10"><html:img height="10" border="0" src="images/spacer.gif"/></td></tr>
				<tr>
					<td valign="top" colspan="3">
						<H3>Contract Maintenance</H3>
					</td>
				</tr>
				<tr><td valign="top" colspan="3" height="10"><html:img height="10" border="0" src="images/spacer.gif"/></td></tr>
				<tr><td valign="top" height="2" bgcolor='<bean:write name="Border"/>' colspan="3"><html:img height="2" border="0" src="images/spacer.gif"/></td></tr>
				<tr>
					<td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" src="images/spacer.gif"/></td>
					<td valign="top">
						<!-- Start of table just for the form -->
						<table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>

							<bean:define toScope="request" id="allYears" name="contractForm" property="allYears" type="java.util.Collection"/>
							<bean:define toScope="request" id="allDays" name="contractForm" property="allDays" type="java.util.Collection"/>
							<bean:define toScope="request" id="allMonths" name="contractForm" property="allMonths" type="java.util.Collection"/>
							<bean:define toScope="request" id="allStatus" name="contractForm" property="allStatus" type="java.util.Collection"/>
							<bean:define toScope="request" id="allAccessType" name="contractForm" property="allAccessType" type="java.util.Collection"/>
							<bean:define toScope="request" id="allContractType" name="contractForm" property="allContractType" type="java.util.Collection"/>
							<bean:define toScope="request" id="allSiteLicense" name="contractForm" property="allSiteLicense"  type="java.util.Collection"/>

							<html:form action='/saveContract' onsubmit="return validateContractForm(this);">

								<html:hidden name="contractForm" property="action" />
								<html:hidden name="contractForm" property="contract.contractID" />
								<html:hidden name="contractForm" property="contract.customerID" />

								<tr>
									<td valign="top" colspan="5" height="4"><html:img height="4" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="displayname.customer"/>&nbsp;<bean:message key="prompt.id"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">
										<span class="MedBlackText"><bean:write name="contractForm" property="contract.customerID" filter="true"/></span>
									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.customerName"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">
										<span class="MedBlackText"><bean:write name="contractForm" property="contract.customer.name" filter="true"/></span>
									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.contractID"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">
										<span class="MedBlackText"><bean:write name="contractForm" property="contract.contractID" filter="true"/></span>
									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>

								<logic:notEqual name="contractForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
									<logic:notEqual name="aContract" property="renewalRefID" value="0">
										<tr>
											<td valign="top" width="3">
												<html:img width="3" border="0" src="images/spacer.gif"/>
											</td>
											<td valign="middle" align="right">
												<span class="MedBoldBlackText">Renewed from :</span>
											</td>
											<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td>
											<td valign="top">

<html:link forward="editContract" paramName="aContract" paramProperty="renewalRefID" paramId="contractid" styleClass="LgBlueLink" >
	<span class="MedBlackText"><bean:write name="aContract" property="renewalRefID" filter="true"/></span>
</html:link>

											</td>
											<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
										</tr>
									</logic:notEqual>
								</logic:notEqual>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.contract"/>&nbsp;<bean:message key="prompt.startDate"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td>
									<td valign="top">

										<html:select name="contractForm" property="contract.contractStartdate.month" size="1" >
											<html:options collection="allMonths" property="value" labelProperty="label"/>
										</html:select>

										<html:select name="contractForm" property="contract.contractStartdate.day" size="1" >
											<html:options collection="allDays" property="value" labelProperty="label"/>
										</html:select>

										<html:select name="contractForm" property="contract.contractStartdate.year" size="1" >
											<html:options collection="allYears" property="value" labelProperty="label"/>
										</html:select>

									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.startDate"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td>
									<td valign="top">

										<html:select name="contractForm" property="contract.startdate.month" size="1" >
											<html:options collection="allMonths" property="value" labelProperty="label"/>
										</html:select>

										<html:select name="contractForm" property="contract.startdate.day" size="1" >
											<html:options collection="allDays" property="value" labelProperty="label"/>
										</html:select>

										<html:select name="contractForm" property="contract.startdate.year" size="1" >
											<html:options collection="allYears" property="value" labelProperty="label"/>
										</html:select>

									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.endDate"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td>
									<td valign="top">

										<html:select name="contractForm" property="contract.enddate.month" size="1" >
											<html:options collection="allMonths" property="value" labelProperty="label"/>
										</html:select>

										<html:select name="contractForm" property="contract.enddate.day" size="1" >
											<html:options collection="allDays" property="value" labelProperty="label"/>
										</html:select>

										<html:select name="contractForm" property="contract.enddate.year" size="1" >
											<html:options collection="allYears" property="value" labelProperty="label"/>
										</html:select>

									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.contract"/>&nbsp;<bean:message key="prompt.endDate"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td>
									<td valign="top">

										<html:select name="contractForm" property="contract.contractEnddate.month" size="1" >
											<html:options collection="allMonths" property="value" labelProperty="label"/>
										</html:select>

										<html:select name="contractForm" property="contract.contractEnddate.day" size="1" >
											<html:options collection="allDays" property="value" labelProperty="label"/>
										</html:select>

										<html:select name="contractForm" property="contract.contractEnddate.year" size="1" >
											<html:options collection="allYears" property="value" labelProperty="label"/>
										</html:select>

									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>

								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.contractType"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">

									<html:select name="contractForm" property="contract.contractType" size="1" >
										<html:options collection="allContractType" property="value" labelProperty="label"/>
									</html:select>

									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.license"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">

										<html:select name="contractForm" property="contract.siteLicense">
											<html:options collection="allSiteLicense" property="value" labelProperty="label"/>
										</html:select>

									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.status"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">

									<html:select name="contractForm" property="contract.status" size="1" >
										<html:options collection="allStatus" property="value" labelProperty="label"/>
									</html:select>

									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr>
									<td valign="top" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="middle" align="right">
										<span class="MedBlackText"><bean:message key="prompt.accessType"/>:</span>
									</td>
									<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">

									<html:select name="contractForm" property="contract.accessType" size="1" >
										<html:options collection="allAccessType" property="value" labelProperty="label"/>
									</html:select>

									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
								<tr><td valign="top" colspan="5" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>

								<%-- DO show form elements to add product when Creating Contract --%>
								<logic:equal name="contractForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
									<tr>
										<td valign="top" width="3">
											<html:img width="3" border="0" src="images/spacer.gif"/>
										</td>
										<td valign="middle" align="right">
											<span class="MedBlackText"><bean:message key="displayname.product"/>:</span>
										</td>
										<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">
											<bean:define toScope="request" id="allProducts" name="contractForm" property="allProducts" type="java.util.Collection"/>
											<html:select name="contractForm" property="item.productID" size="1" >
												<html:options collection="allProducts" property="productID" labelProperty="name"/>
											</html:select>
										</td>
										<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
									</tr>
									<tr>
										<td valign="top" width="3">
											<html:img width="3" border="0" src="images/spacer.gif"/>
										</td>
										<td valign="middle" align="right">
											<span class="MedBlackText"><bean:message key="prompt.notes"/>:</span>
										</td>
										<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">
											<html:textarea cols="80" rows="7" name="contractForm" property="item.notes" />
										</td>
										<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
									</tr>
								</logic:equal>
								<tr>
									<td valign="top" colspan="3">
										<html:img border="0" pageKey="images.spacer"/>
									</td>
									<td valign="top">
										<html:image altKey="alt.save" pageKey="images.action.save.lilac" border="0"/>
										<html:image altKey="alt.reset" pageKey="images.action.reset.lilac" border="0" onclick="javascript:reset(); return false;" />
									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>

							</html:form>

							<%-- DO NOT show button to change 'Access' property when Creating Contract --%>
							<logic:notEqual name="contractForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
								<tr><td valign="top" colspan="5" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
								<tr>
									<td valign="middle" width="3">
										<html:img width="3" border="0" src="images/spacer.gif"/>
									</td>
									<td valign="top" align="right">
										<span class="MedBlackText"><bean:message key="prompt.access"/>:</span>
									</td>
									<td valign="middle" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td>
									<td valign="top">

											<logic:equal name="aContract" property="isEnabled" value="true">
											  <span class="MedBoldBlackText"><bean:message key="prompt.yes"/></span>
											</logic:equal>
											<logic:equal name="aContract" property="isEnabled" value="false">
											  <span class="MedBoldRedText"><bean:message key="prompt.no"/></span>
											</logic:equal>

									</td>
									<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
								</tr>
							</logic:notEqual>
							<%-- DO NOT show button to change 'Access' property when Creating Contract --%>
						</table>
					</td>
					<td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" src="images/spacer.gif"/></td>
				</tr>
				<tr><td valign="top" colspan="3" height="2" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" src="images/spacer.gif"/></td></tr>

				<%-- DO NOT show add contract item form when Creating Contract --%>
				<logic:notEqual name="contractForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
					<tr><td valign="top" colspan="3" height="10"><html:img height="10" border="0" src="images/spacer.gif"/></td></tr>
					<tr>
						<td valign="top" colspan="3">
							<html:img pageKey="images.heading.contract.addproduct" border="0"/>
						</td>
					</tr>
					<tr><td valign="top" colspan="3" height="10"><html:img height="10" border="0" src="images/spacer.gif"/></td></tr>
					<tr><td valign="top" height="2" bgcolor='<bean:write name="Border"/>' colspan="3"><html:img height="2" border="0" src="images/spacer.gif"/></td></tr>
					<tr>
						<td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" src="images/spacer.gif"/></td>
						<td valign="top">
							<table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
								<html:form action='/addProductToContract' >
									<html:hidden name="contractForm" property="contract.contractID"/>
									<html:hidden name="contractForm" property="contract.customerID" />
									<html:hidden name="contractForm" property="action" value="<%= Constants.ACTION_ADD %>"/>
									<tr>
										<td valign="top" colspan="5" height="4"><html:img height="4" border="0" src="images/spacer.gif"/></td>
									</tr>
									<tr>
										<td valign="top" width="3">
											<html:img width="3" border="0" src="images/spacer.gif"/>
										</td>
										<td valign="middle" align="right">
											<span class="MedBlackText"><bean:message key="displayname.product"/>:</span>
										</td>
										<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">

										<bean:define toScope="request" id="allProducts" name="contractForm" property="allProducts" type="java.util.Collection"/>

										<html:select name="contractForm" property="item.productID" size="1" >
											<html:options collection="allProducts" property="productID" labelProperty="name"/>
										</html:select>

										</td>
										<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
									</tr>
									<tr>
										<td valign="top" width="3">
											<html:img width="3" border="0" src="images/spacer.gif"/>
										</td>
										<td valign="middle" align="right">
											<span class="MedBlackText"><bean:message key="prompt.notes"/>:</span>
										</td>
										<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">
											<html:textarea cols="40" rows="5" name="contractForm" property="item.notes" />
										</td>
										<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
									</tr>
									<tr>
										<td valign="top" colspan="5" height="4"><html:img height="4" border="0" src="images/spacer.gif"/></td>
									</tr>
									<tr>
										<td valign="top" colspan="3">
											<html:img border="0" src="images/spacer.gif"/>
										</td>
										<td valign="top">
											<html:image pageKey='<%= (String) request.getAttribute("saveKey") %>' property="saveAndGotoOptions" border="0"/>
											<html:image pageKey='<%= (String) request.getAttribute("resetKey") %>' border="0" onclick="javascript:reset(); return false;" />
										</td>
										<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
									</tr>
								</html:form>
							</table>
							<!-- End of table just for the form -->
						</td>
						<td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" src="images/spacer.gif"/></td>
					</tr>
					<tr><td valign="top" colspan="3" height="2" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" src="images/spacer.gif"/></td></tr>
					<tr><td valign="top" colspan="3" height="10"><html:img height="10" border="0" src="images/spacer.gif"/></td></tr>
				</logic:notEqual>
				<%-- DO NOT show add contract item form when Creating Contract --%>

				<%-- DO NOT show contract items property when Creating Contract --%>
				<logic:notEqual name="contractForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
					<tr>
						<td valign="top" colspan="3"><H3>Contract Items</H3></td>
					</tr>
					<tr><td valign="top" colspan="3" height="10"><html:img height="10" border="0" src="images/spacer.gif"/></td></tr>
					<tr><td valign="top" height="2" bgcolor='<bean:write name="Border"/>' colspan="3"><html:img height="2" border="0" src="images/spacer.gif"/></td></tr>
					<tr>
						<td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" src="images/spacer.gif"/></td>
						<td valign="top">

							<logic:equal name="contractForm" property="action" scope="request" value="<%= Constants.ACTION_EDIT %>">

								<bean:define toScope="request" id="allItems" name="contractForm" property="contract.allItems" />

								<!-- Start of table to put the form -->
								<table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
									<tr>
										<td valign="top" colspan="5" height="4">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">

												<template:insert template='/templates/table.jsp'>
													<template:put name='header' content='/templates/_header.jsp'/>
													<template:put name='body'   content='/templates/_ent.jsp'/>
												</template:insert>

											</table>
										</td>
									</tr>
								</table>
								<!-- End of table to put the form -->

							</logic:equal>

						</td>
						<td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" src="images/spacer.gif"/></td>
					</tr>
					<tr>
						<td valign="top" colspan="3" height="2" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" src="images/spacer.gif"/></td>
					</tr>
				</logic:notEqual>
				<%-- DO NOT show contract items property when Creating Contract --%>

			</table>
			<p/>
		</td>
	</tr>
</table>
</center>
<!-- End of body portion -->

<html:javascript formName="contractForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>

</body>
</html:html>
