<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
<head>
	<title><bean:write name="accessForm" scope="request" property="action" filter="true"/> <bean:message key="displayname.access"/></title>
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
			<!-- Start of table for the left hand navigation -->

<%--
>><bean:write name="accessForm" property="access.contactID" /><BR/>
[[<bean:write name="accessForm" property="access.entitlementID" /><BR/>
--%>
      <logic:notEqual name="accessForm" property="access.contactID" value="0">
        <bean:define toScope="request" id="aContact" name="accessForm" property="access.contact" />
        <bean:define toScope="request" id="aCustomer" name="aContact" property="customer" />
      </logic:notEqual>

      <logic:notEqual name="accessForm" property="access.entitlementID" value="0">
        <bean:define toScope="request" id="aEntitlement" name="accessForm" property="access.entitlement" />
        <bean:define toScope="request" id="aContract" name="aEntitlement" property="contract" />
        <bean:define toScope="request" id="aCustomer" name="aContract" property="customer" />
      </logic:notEqual>

      <template:insert template='/templates/nav.jsp'>
        <template:put name='links' content='/templates/_links.jsp'/>
      </template:insert>

			<!-- End of table for the left hand navigation -->
		</td>
		<td valign="top" width="10">
			<html:img width="10" border="0" pageKey="images.spacer"/>
		</td>
		<td valign="top">
			<!-- Start of table to put the form -->

			<bean:define id="Border" value="#BCBD78"/>
			<bean:define id="Inner" value="#EFEFD6"/>

			<table border="0" cellspacing="0" cellpadding="0" width="600">
				<tr>
					<td valign="top" colspan="3" height="20"><html:img height="20" border="0" pageKey="images.spacer"/></td>
				</tr>
				<tr>
					<td valign="top" colspan="3">
							<H3><I><bean:write name="accessForm" property="access.accessType"/> Access</I></H3>
					</td>
				</tr>
				<tr><td valign="top" colspan="3" height="10"><html:img height="10" border="0" pageKey="images.spacer"/></td></tr>
				<tr><td valign="top" height="2" bgcolor='<bean:write name="Border"/>' colspan="3"><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
				<tr><td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
					<td valign="top">

						<!-- Start of table just for the form -->
						<table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
							<tr>
								<td valign="top" colspan="8" height="4"><html:img height="4" border="0" pageKey="images.spacer"/></td>
							</tr>

							<%--
								START Acess Edit and Creation
							--%>

							  <html:form action="/saveAccess" >

									<html:hidden property="action"/>
									<html:hidden property="access.productID"/>
									<html:hidden property="access.accessID"/>
									<html:hidden property="access.accessType"/>

                <logic:notEqual name="accessForm" property="access.entitlementID" value="0">
									<html:hidden property="access.entitlementID"/>
                </logic:notEqual>
                <logic:notEqual name="accessForm" property="access.contactID" value="0">
									<html:hidden property="access.contactID"/>
                </logic:notEqual>

									<html:hidden property="forward"/>


								<logic:equal name="accessForm" property="access.accessType" value="<%= Constants.IP %>">
                    <tr>
                    	<td valign="top" width="3">
                    		<html:img border="0" pageKey="images.spacer"/>
                    	</td>
                    	<td valign="top" align="right">
                    		<span CLASS="MedBlackText"><bean:message key="prompt.lowip"/>:</span> &nbsp;
                    	</td>
                    	<td valign="middle">
                    	  <span CLASS="MedBlackText"><html:text property="access.credentials.lowIp"/></span>
                    	</td>
                    	<td valign="top" width="3">
                    		<html:img border="0" pageKey="images.spacer"/>
                    	</td>
                    	<td valign="middle" align="right">
                    		<span CLASS="MedBlackText"><bean:message key="prompt.highip"/>:</span> &nbsp;
                    	</td>
                    	<td valign="middle">
                    	  <span CLASS="MedBlackText"><html:text property="access.credentials.highIp"/></span>
                    	</td>
                    </tr>
                </logic:equal>
								<logic:equal name="accessForm" property="access.accessType" value="<%= Constants.GATEWAY %>">
                    <tr>
                    	<td valign="top" width="3">
                    		<html:img border="0" pageKey="images.spacer"/>
                    	</td>
                    	<td valign="middle" align="right">
                    		<span CLASS="MedBlackText"><bean:message key="prompt.gateway"/>:</span> &nbsp;
                    	</td>
                    	<td valign="middle" colspan="6">
                    	  <span CLASS="MedBlackText"><html:text property="access.credentials.gateway" size="64" maxlength="256"/></span>
                    	</td>
                    </tr>
                </logic:equal>
		            <logic:equal name="accessForm" property="access.accessType" value="<%= Constants.USERNAME_PASSWORD %>">
                    <tr>
                    	<td valign="top" width="3">
                    		<html:img border="0" pageKey="images.spacer"/>
                    	</td>
                    	<td valign="middle" align="right">
                    		<span CLASS="MedBlackText"><bean:message key="prompt.username"/>:</span> &nbsp;
                    	</td>
                    	<td valign="middle">
                    	  <span CLASS="MedBlackText"><html:text property="access.credentials.username"/></span>
                    	</td>
                    	<td valign="top" width="3">
                    		<html:img border="0" pageKey="images.spacer"/>
                    	</td>
                    	<td valign="top" align="right">
                    		<span CLASS="MedBlackText"><bean:message key="prompt.password"/>:</span> &nbsp;
                    	</td>
                    	<td valign="middle">
                    	  <span CLASS="MedBlackText"><html:text property="access.credentials.password"/></span>
                    	</td>
                    </tr>
                </logic:equal>
            		<tr>
            			<td valign="top" colspan="8" height="4">
            				<html:img height="4" border="0" pageKey="images.spacer"/>
            			</td>
            		</tr>
            		<tr>
            			<td valign="top" colspan="2">
            				<html:img border="0" pageKey="images.spacer"/>
            			</td>
            			<td valign="top">
            				<html:image altKey="alt.save" pageKey="images.save_olive" border="0"/>
            				<html:image altKey="alt.reset" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
            			</td>
            			<td valign="top" colspan="5">
            				<html:img border="0" pageKey="images.spacer"/>
            			</td>
            		</tr>
		        </html:form>

        		<%--
        		END Access Edit and Creation
        		--%>

		<tr>
			<td valign="top" colspan="8" height="4"><html:img height="4" border="0" pageKey="images.spacer"/></td>
		</tr>
		</table>
	<!-- End of table just for the form -->
	</td>
	<td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
	</tr>
	<tr>
		<td valign="top" colspan="3" height="2" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td>
	</tr>
	</table>

	<!-- End of table to put the form -->

	<P/>
		</td>
	</tr>
</table>
</center>
<!-- End of body portion -->

</body>
</html:html>
