<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
	<head>
  <title><bean:write name="userForm" scope="request" property="action" filter="true"/> <bean:message key="displayname.user"/></title>
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
            <logic:equal name="userForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
              <H3><I><bean:message key="action.create"/> <bean:message key="displayname.user"/></I></H3>
            </logic:equal>
            <logic:equal name="userForm" property="action" scope="request" value="<%= Constants.ACTION_EDIT %>">
              <H3><I><bean:message key="action.edit"/> <bean:message key="displayname.user"/></I></H3>
            </logic:equal>
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

              <logic:equal name="userForm" property="action" scope="request" value="<%= Constants.ACTION_LIST %>">

                <bean:define toScope="request" id="RESULT" name="userForm" property="allUsers" />

                <logic:iterate name="RESULT" property="iterator" id="aUser" offset="offset" length="length" type="org.ei.struts.backoffice.user.User">
                <tr>
                  <td valign="top" colspan="1" >&nbsp;</td>
                  <td valign="top" colspan="3" >
                    <html:link forward="editUser" paramName="aUser" paramProperty="salesRepId" paramId="salesrepid" styleClass="LgBlueLink" >
                      User - <bean:write name="aUser" property="name" filter="true"/>
                    </html:link>
                  </td>
                </tr>
                </logic:iterate>

                <tr>
                  <td valign="top" colspan="1" width="25%">
                    &nbsp;<logic:greaterEqual name="prev" value="0">
                      <html:link forward="listUsers" paramName="prev" paramId="offset" styleClass="LgBlueLink" >Prev</html:link>
                    </logic:greaterEqual>
                  </td>
                  <td align="left" colspan="1" width="25%">
                    &nbsp;<logic:greaterThan name="RESULT" property="size" value='<%= request.getAttribute("next").toString() %>'>
                      <html:link forward="listUsers" paramName="next" paramId="offset" styleClass="LgBlueLink" >Next</html:link>
                    </logic:greaterThan>
                  </td>
                  <td align="left" colspan="2" >
                    &nbsp;
                  </td>
                </tr>

              </logic:equal>

              <%--
                START User Edit and Creation
              --%>
              <logic:notEqual name="userForm" property="action" scope="request" value="<%= Constants.ACTION_LIST %>">

<tr><td colspan="5">
<P ALIGN="CENTER"><H1><B>Stop</B></H1></P>
<P CLASS="MedBlackText">DO NOT create a Back Office account in order to give usage report access to a customer.<BR>
ONLY Elsevier employees and their approved agents should have Back Office accounts.<BR>
To give out usage report access to a customer, add the Usage product to the active contract in the Contract Maintenance screen.<BR>
If you are unsure, contact the Engineering Village product team.<BR>
Thank You
</P>
</td></tr>

                 <html:form action="/saveUser" onsubmit="return validateUserForm(this);">
                  	<html:hidden property="action"/>
					<html:hidden name="userForm" property="user.salesRepId"/>

                  	<tr>
                  	  <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td>
                  	  <td valign="middle" align="right"><span CLASS="MedBlackText"><bean:message key="prompt.name"/>:</span></td>
                    	<td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10" /></td>
                    	<td valign="top"><html:text property="user.name" size="32" maxlength="64"/></td>
                    	<td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td>
                    </tr>
                  	<tr><td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4" /></td></tr>

                  	<tr><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td><td valign="middle" align="right">
                  		<a CLASS="MedBlackText"><bean:message key="prompt.email"/>:</a>
                  	</td><td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10" /></td><td valign="top">
                  		<html:text property="user.email" size="32" maxlength="128" />
                  	</td><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td></tr>
                  	<tr><td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4" /></td></tr>

                  	<tr><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td><td valign="middle" align="right">
                  		<a CLASS="MedBlackText"><bean:message key="prompt.phone"/>:</a>
                  	</td><td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10" /></td><td valign="top">
                  		<html:text property="user.phone" size="16" maxlength="16"/>
                  	</td><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td></tr>
                  	<tr><td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4" /></td></tr>

                  	<tr><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td><td valign="middle" align="right">
                  		<a CLASS="MedBlackText"><bean:message key="prompt.role"/>:</a>
                  	</td><td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10" /></td><td valign="top">

                      <bean:define id="allRoles" name="userForm" property="allRoles" />

                  		<html:select property="user.roles" size="4" >
                  			<html:options collection="allRoles" property="roleID" labelProperty="roleName"/>
                  		</html:select>

                  	</td><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td></tr>
                  	<tr><td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4" /></td></tr>

                  	<tr><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td><td valign="middle" align="right">
                  		<a CLASS="MedBlackText"><bean:message key="prompt.region"/>:</a>
                  	  </td><td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10" /></td>
                  	  <td valign="top">

                      <bean:define id="allRegions" name="userForm" property="allRegions" />
                  		<html:select property="user.regions" size="5" >
                  			<html:options collection="allRegions" property="regionID" labelProperty="regionName"/>
                  		</html:select>

                  	  </td><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td>
                  	</tr>
                  	<tr>
                  	  <td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4" /></td>
                  	</tr>
                  	<tr>
                  	  <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td><td valign="middle" align="right">
                  		<a CLASS="MedBlackText"><bean:message key="prompt.username"/>:</a>
                  	  </td><td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10" /></td><td valign="top">
                  		<html:text property="user.username" size="16" maxlength="16"/>
                  	  </td><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td>
                  	</tr>
                  	<tr>
                  	  <td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4" /></td>
                  	</tr>
                  	<tr>
                  	  <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td><td valign="middle" align="right">
                  		<a CLASS="MedBlackText"><bean:message key="prompt.password"/>:</a>
                  	  </td><td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10" /></td><td valign="top">
                  		<html:password property="user.password" size="16" maxlength="16"/>
                  	  </td><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td>
                  	</tr>
                  	<tr>
                  	  <td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4" /></td>
                  	</tr>
                  	<tr>
                  	  <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td><td valign="middle" align="right">
                  		<a CLASS="MedBlackText"><bean:message key="prompt.access"/>:</a>
                  	  </td><td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10" /></td><td valign="top">

                        <bean:define id="allAccessOptions" name="userForm" property="accessOptions"/>
      									<html:select property="user.isEnabled" size="1" >
      										<html:options collection="allAccessOptions" property="value" labelProperty="label"/>
      									</html:select>

                  	  </td><td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3" /></td>
                  	</tr>
                  	<tr>
                  	  <td valign="top" colspan="3"><html:img pageKey="images.spacer" border="0" /></td>
                  	  <td valign="top">
                  	      <html:image altKey="" pageKey="images.save_olive" border="0"/>
                  	   		<html:image altKey="alt.reset" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
                  	  </td>
                  	</tr>
                 </html:form>

              </logic:notEqual>
              <%--
                END User Edit and Creation
              --%>

              <tr>
                <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
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

      <P/>

    </td>
  </tr>
</table>
</center>
<!-- End of body portion -->

<html:javascript formName="userForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>

</body>
</html:html>
