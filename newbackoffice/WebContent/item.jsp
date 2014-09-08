<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ page import="org.ei.struts.backoffice.user.User" %>

<html:html>
<head>
  <title><bean:write name="itemForm" scope="request" property="action" filter="true"/> <bean:message key="displayname.contract"/></title>
  <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
<!--  <link rel="stylesheet" href='<html:rewrite page="/css/ui.all.css"/>' type="text/css">
  <script type="text/javascript" src='<html:rewrite page="/js/jquery-1.3.2.min.js"/>'></script>
  <script type="text/javascript" src='<html:rewrite page="/js/ui.core.js"/>'></script>
  <script type="text/javascript" src='<html:rewrite page="/js/ui.accordion.js"/>'></script>
-->
<script>
  function togglenotes() {
    var atoggleDiv = document.getElementById("note_history");
    var atoggleAnchor = document.getElementById("note_prompt");

    atoggleAnchor.removeChild(atoggleAnchor.firstChild);

    if(atoggleDiv.style.display == "block") {
      atoggleDiv.style.display = "none";
      atoggleAnchor.appendChild(document.createTextNode("Show History"));

    }
    else {
      atoggleDiv.style.display = "block";
      atoggleAnchor.appendChild(document.createTextNode("Hide History"));
    }
  }
</script>

<style>
html fieldset {
  position: relative;
  margin-top:1em;
  padding-top:1em;
  padding-bottom: 2em;
}

/* Form Styles */
fieldset {
  background: #E5E5F1;
  border:2px solid #8E92BF;
}

.form_row {
  white-space: nowrap;
  padding-bottom: .5em;
}

.note_label {
  width: 160;
  float: left;
}
.note_text {
  width: 420;
  float: left;
  text-align: left;
}


label {
  width: 10em;
  float: left;
  text-align: right;
  margin-right: 0.2em;
  display: block;
}

</style>
</head>

<body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

<jsp:useBean id="Constants" class="org.ei.struts.backoffice.Constants" scope="application"/>

<bean:define toScope="request" id="Border" value="#8E92BF"/>
<bean:define toScope="request" id="Heading" value="#C1C3DB"/>
<bean:define toScope="request" id="Inner" value="#E5E5F1"/>

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

	        <bean:define toScope="request" id="anItem" name="itemForm" property="item" type="org.ei.struts.backoffice.contract.item.Item"/>
	        <bean:define toScope="request" id="aContract" name="anItem" property="contract" type="org.ei.struts.backoffice.contract.Contract"/>
	        <bean:define toScope="request" id="aCustomer" name="aContract" property="customer" />

	        <template:insert template='/templates/nav.jsp'>
	          <template:put name='links' content='/templates/_links.jsp'/>
	        </template:insert>

		</td>
		<td valign="top" width="10">
			<html:img width="10" border="0" pageKey="images.spacer"/>
		</td>
		<td valign="top" width="600">

			<logic:notEmpty name="itemForm" property="item.itemID">
				<H2><bean:write name="itemForm" property="item.product.name" filter="true"/> -  Contract Item Information</H2>
				<H3><bean:message key="displayname.contract"/> ID&nbsp;<bean:write name="aContract" property="contractID" filter="true"/></H3>

				<%--
					START ITEM Properties
				--%>

<%
    User user = (User) session.getAttribute(Constants.USER_KEY);
    String userid = user.getUsername();
%>

<html:form action='/saveNote' onsubmit="return validateItemForm(this);">
	<html:hidden name="itemForm" property="action" value="<%= Constants.ACTION_CREATE %>" />
	<html:hidden name="itemForm" property="note.contractID" value="<%= aContract.getContractID() %>"/>
	<html:hidden name="itemForm" property="note.itemID" value="<%= anItem.getItemID() %>"/>
	<html:hidden name="itemForm" property="note.userID" value="<%= userid %>"/>
	<html:hidden name="itemForm" property="item.itemID"/>
	<html:hidden name="itemForm" property="item.contract.contractID"/>
	<html:hidden name="itemForm" property="item.contract.customerID"/>

  <fieldset>

    <logic:notEmpty name="itemForm" property="item.notes">
      <div style="clear:both;">
        <div class="note_label"><label><span class="MedBlackText">Original Note:</span></label></div>
        <div class="note_text">
          <span class="MedBlackText">[<bean:write name="itemForm" property="item.notes" filter="true"/>]</span>
        </div>
      </div>
    </logic:notEmpty>

    <bean:size id="noteHistoryCount" name="itemForm" property="item.noteHistory" scope="request"/>

    <logic:greaterThan name="noteHistoryCount" value="1">
      <div class="form_row">
        <label><a id="note_prompt" class="MedBlueLink" href="javascript:togglenotes();">Show History</a></label>
      </div>
    </logic:greaterThan>

      <div id="note_history" style="display: none;">
        <bean:define toScope="request" id="RESULT" name="itemForm" property="item.noteHistory" type="java.util.Collection"/>
        <logic:iterate indexId="idxNote" id="aNote" name="RESULT" type="org.ei.struts.backoffice.contract.notes.Note">
          <%-- do not display most recent note in history block --%>
          <logic:notEqual name="noteHistoryCount" value="<%= String.valueOf(idxNote.intValue() + 1) %>">
            <div style="clear:both;">
              <div class="note_label"><label><span class="MedBlackText"><bean:write name="aNote" property="createdDate.displaydate" filter="true"/> - <bean:write name="aNote" property="userID" filter="true"/></span></label></div>
              <div class="note_text">
                <span class="MedBlackText">[<bean:write name="aNote" property="note" filter="true"/>]</span>
              </div>
            </div>
          </logic:notEqual>
        </logic:iterate>
      </div>

    <logic:present name="aNote">
      <div style="clear:both;">
        <%-- <div class="note_label"><label><span class="MedBlackText">Most Recent Note:</span></label></div> --%>
        <div class="note_label"><label><span class="MedBlackText"><bean:write name="aNote" property="createdDate.displaydate" filter="true"/> - <bean:write name="aNote" property="userID" filter="true"/></span></label></div>
        <div class="note_text">
          <span class="MedBlackText">[<bean:write name="aNote" property="note" filter="true"/>]</span>
        </div>
      </div>
    </logic:present>

    <div style="clear:both;"></div>

    <div class="form_row">
      <label for="notes"><span class="MedBlackText"><bean:message key="prompt.notes"/>:</span></label>
      <html:textarea cols="80" rows="7" name="itemForm" styleId="notes" property="note.note"/>
    </div>

    <div class="form_row">
      <label>&nbsp;</label>
      <html:image altKey="alt.save" pageKey="images.action.save.lilac" border="0"/>
      <html:image altKey="alt.reset" pageKey="images.action.reset.lilac" border="0" onclick="javascript:reset(); return false;" />
    </div>

  </fieldset>
</html:form>

				<%--
					END ITEM Properties
				--%>

				<logic:equal name="itemForm" property="item.hasOptions" value="true">
				<%--
					START OPTIONS Listings
				--%>
				<!-- Start of table to put the form -->
				<table border="0" cellspacing="0" cellpadding="0" width="600">
					<tr><td valign="top" colspan="3" height="20"><html:img height="20" border="0" pageKey="images.spacer"/></td></tr>
					<tr>
						<td valign="top" colspan="3">

							<table border="0" cellspacing="0" cellpadding="0" width="600" >
								<tr>
									<H3>Product Options</H3>
									<td align="left">
										<ul>
										<li>
										<html:link forward="editOptions" name="itemForm" property="item.linkParams" styleClass="MedBlueLink">
											Edit <bean:write name="itemForm" property="item.product.name" filter="true"/> Options
										</html:link>
										</li>
										</ul>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<!-- End of table to put the form -->
				</logic:equal>

				<%--
					START ACCESS Listings
				--%>
				<bean:define toScope="request" id="Border" value="#BCBD78"/>
				<bean:define toScope="request" id="Inner" value="#EFEFD6"/>
				<bean:define toScope="request" id="Heading" value="#DEDEAE"/>

				<!-- Start of table to put the form -->
				<table border="0" cellspacing="0" cellpadding="0" width="600">
					<tr><td valign="top" colspan="3" height="20"><html:img height="20" border="0" pageKey="images.spacer"/></td></tr>
					<tr>
            <td valign="top" colspan="3">
              <H3>Access Methods</H3>

              <table border="0" cellspacing="0" cellpadding="0" width="600" >
              <tr>
                <td align="center" width="200">
                  <logic:notEqual name="itemForm" property="item.productID" value="<%= Constants.USAGE_ID %>">
                    <html:link forward="createAccess" name="itemForm" property="createIpAccess" >
                    <span class="MedBlueLink">Add IP Access</span>
                    </html:link>
                  </logic:notEqual>
                </td>
                <td align="center" width="200">
                  <logic:notEqual name="itemForm" property="item.productID" value="<%= Constants.USAGE_ID %>">
                    <html:link forward="createAccess" name="itemForm" property="createGatewayAccess" >
                    <span class="MedBlueLink">Add Gateway Access</span>
                    </html:link>
                  </logic:notEqual>
                </td>
                <td align="center" width="200">
                  <html:link forward="createAccess" name="itemForm" property="createUsernameAccess" >
                  <span class="MedBlueLink">Add Username/Password Access</span>
                  </html:link>
                </td>
              </tr>
              </table>
            </td>
            </tr>
          			<%--
    					START IP ACCESS Listings
    				--%>
					<bean:define toScope="request" id="RESULT" name="itemForm" property="item.ipAccess" type="java.util.Collection"/>
					<template:insert template='/templates/aAccess.jsp'>
						<template:put name='body' content='/templates/_edit_access.jsp'/>
					</template:insert>
          			<%--
    					START GATEWAY ACCESS Listings
    				--%>
          			<bean:define toScope="request" id="RESULT" name="itemForm" property="item.gatewayAccess" type="java.util.Collection"/>
					<template:insert template='/templates/aAccess.jsp'>
						<template:put name='body' content='/templates/_edit_access.jsp'/>
					</template:insert>
          			<%--
    					START USERNAME ACCESS Listings
    				--%>
					<bean:define toScope="request" id="RESULT" name="itemForm" property="item.usernameAccess" type="java.util.Collection"/>
					<template:insert template='/templates/aAccess.jsp'>
						<template:put name='body' content='/templates/_edit_access.jsp'/>
					</template:insert>

				</table>
				<!-- End of table to put the form -->

				<%--
					END ACCESS Listings
				--%>


				<logic:equal name="itemForm" property="item.hasLocalHoldings" value="true">
					<%--
						START Local Linking Options Listings
					--%>

					<!-- Start of table to put the form -->
					<table border="0" cellspacing="0" cellpadding="0" width="600">
						<tr><td valign="top" colspan="3" height="20"><html:img height="20" border="0" pageKey="images.spacer"/></td></tr>
						<tr>
							<td valign="top" colspan="3">
								<H3>Local Linking Options</H3>

								<table border="0" cellspacing="0" cellpadding="0" width="600" >
									<tr>
										<td align="left">
											<ul>
											<li>
											<html:link forward="createLocalHoldings" name="itemForm" property="createLocalHoldings" styleClass="MedBlueLink">
												Add new local linking option
											</html:link>
											</li>
											</ul>
										</td>
									</tr>
								</table>

								<table border="0" cellspacing="0" cellpadding="0" width="600" >
									<tr>
										<td align="left">
											<bean:define toScope="request" id="RESULT" name="itemForm" property="item.localHoldings" type="java.util.Collection"/>

											<template:insert template='/templates/aLocalHoldings.jsp'>
												<template:put name='body' content='/templates/_edit_localholdings.jsp'/>
											</template:insert>

										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					<!-- End of table to put the form -->
					<%--
						END Local Linking Options Listings
					--%>
				</logic:equal>

			</logic:notEmpty>
			<p/>

		</td>
	</tr>
</table>
</center>
<!-- End of body portion -->

<html:javascript formName="itemForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>

</body>
</html:html>
