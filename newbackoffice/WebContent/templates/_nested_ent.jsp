<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<jsp:useBean id="Constants" class="org.ei.struts.backoffice.Constants" scope="application"/>

        <logic:iterate indexId="idxContract" id="acontract" name="RESULT" property="iterator" type="org.ei.struts.backoffice.contract.Contract">
          <tr>
<td valign="top" width="1" ><html:img src="images/spacer.gif" border="0" width="1"/></td>
<td>

	&nbsp;<span class="MedBoldBlackText"><bean:message key="prompt.contractID"/>:</span>&nbsp;<html:link forward="editContract" paramName="acontract" paramProperty="contractID" paramId="contract.contractID" styleClass="MedBlueLink" ><bean:write name="acontract" property="contractID" filter="true"/></html:link>
	<logic:notEqual name="acontract" property="renewalRefID" value="0">
	    <br/>&nbsp;<span class="MedBoldBlackText">Renewed from <bean:write name="acontract" property="renewalRefID" filter="true"/></span>
	</logic:notEqual>

</td>

<td valign="top" width="1" bgcolor='<bean:write name="Heading"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
<td>&nbsp;</td>

<td valign="top" width="1" bgcolor='<bean:write name="Heading"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
            <td width="45%" ><span class="MedBlackText"><bean:write name="acontract" property="startdate.displaydate" filter="true"/> <b>to</b> <bean:write name="acontract" property="enddate.displaydate" filter="true"/></span>
            </td>
<td valign="top" width="1" bgcolor='<bean:write name="Heading"/>'><html:img src="images/spacer.gif" border="0" width="1"/></td>
            <td valign="middle" >
                <html:form action="/turnOnOffContract" >
                    <input type="hidden" name="contract.contractID" value='<bean:write name="acontract" property="contractID"/>'/>
                    <input type="hidden" name="contract.customerID" value='<bean:write name="acontract" property="customerID"/>'/>
                    <logic:equal name="acontract" property="isEnabled" value="true">
                      <span class="MedBoldBlackText">Access Enabled</span>
                      <html:hidden property="action" value='<%= Constants.ACTION_DISABLE %>'/>
                      <html:submit value="Turn Off"/>
                    </logic:equal>
                    <logic:equal name="acontract" property="isEnabled" value="false">
                      <span class="MedBoldRedText">Access Disabled</span>
                      <html:hidden property="action" value='<%= Constants.ACTION_ENABLE %>'/>
                      <html:submit value="Turn On"/>
                    </logic:equal>
                </html:form>

<%--
                <logic:equal name="acontract" property="isRenewable" value="true">
                  <html:form action="/renewContract" >
                    <html:hidden property="action" value='<%= Constants.ACTION_RENEW %>'/>
                    <input type="hidden" name="contractid" value='<bean:write name="acontract" property="contractID"/>'/>
                    <html:submit onclick="this.disabled = true; this.form.submit(); return true;">
                      <bean:message key="prompt.renew"/>
                    </html:submit>
                  </html:form>
                </logic:equal>
--%>

            </td>
        </tr>
        <tr>
            <td valign="top" colspan="8" height="1" bgcolor='<bean:write name="Border"/>'><html:img src="images/spacer.gif" border="0" height="1"/></td>
        </tr>

            <%-- Only show Items from Enabled Contracts --%>
            <logic:equal name="acontract" property="isEnabled" value="true">
              <bean:define toScope="request" id="allItems" name="acontract" property="allItems" />
              <template:insert template='/templates/asset.jsp'>
                  <template:put name='submain' content='/templates/_ent.jsp'/>
              </template:insert>
            </logic:equal>

        </logic:iterate>
