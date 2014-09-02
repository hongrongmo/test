<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
<head>
    <title><bean:write name="contactForm" scope="request" property="action" filter="true"/> <bean:message key="displayname.contact"/></title>
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
        <logic:notEqual name="contactForm" property="action" scope="request" value="<%= Constants.ACTION_LIST %>">
          <bean:define toScope="request" id="aCustomer" name="contactForm" property="contact.customer" />
        </logic:notEqual>

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
                <td valign="top" colspan="3" height="20"><html:img height="20" border="0" pageKey="images.spacer"/></td>
              </tr>
              <tr>
                <td valign="top" colspan="3">
                  <logic:equal name="contactForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
                    <html:img pageKey="images.heading.contact.create" border="0"/>
                  </logic:equal>
                  <logic:equal name="contactForm" property="action" scope="request" value="<%= Constants.ACTION_EDIT %>">
                    <H3><I>Modify Contact</I></H3>
                  </logic:equal>
                </td>
              </tr>
              <tr><td valign="top" colspan="3" height="10"><html:img height="10" border="0" pageKey="images.spacer"/></td></tr>
              <tr><td valign="top" height="2" bgcolor='<bean:write name="Border"/>' colspan="3"><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
              <tr><td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
                  <td valign="top">

                      <!-- Start of table just for the form -->
                      <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                          <tr>
                            <td valign="top" colspan="6" height="4"><html:img height="4" border="0" pageKey="images.spacer"/></td>
                          </tr>

                          <%--
                              START Contact Listing and  Navigation
                          --%>
                          <logic:equal name="contactForm" property="action" scope="request" value="<%= Constants.ACTION_LIST %>">

                          <bean:define id="RESULT" name="contactForm" property="allContacts" />
                              <logic:iterate name="RESULT" property="iterator" id="aContact" indexId="idxContact" offset="offset" length="length" type="org.ei.struts.backoffice.contact.Contact">
                              <tr>
                                <td valign="top" colspan="1" >&nbsp;</td>
                                <td valign="top" colspan="3" >
                                <html:link forward="editContact" paramName="aContact" paramProperty="contactID" paramId="contactid" styleClass="LgBlueLink" >
                                  <bean:write name="aContact" property="displayName" filter="true"/>
                                </html:link>
                                </td>
                              </tr>
                              </logic:iterate>
                              <tr>
                                <td valign="top" colspan="1" >
                                &nbsp;
                                <logic:greaterEqual name="prev" value="0">
                                  <html:link forward="listConsortia" paramName="prev" paramId="offset" styleClass="LgBlueLink" >Prev</html:link>
                                </logic:greaterEqual>
                                </td>
                                <td align="left" colspan="1" >
                                  &nbsp;<logic:greaterThan name="RESULT" property="size" value='<%= request.getAttribute("next").toString() %>'>
                                  <html:link forward="listContacts" paramName="next" paramId="offset" styleClass="LgBlueLink" >Next</html:link>
                                  </logic:greaterThan>
                                </td>
                                <td align="left" colspan="2" >
                                  &nbsp;
                                </td>
                              </tr>
                          </logic:equal>
                          <%--
                              END Contact Listing and  Navigation
                          --%>

                          <%--
                              START Contact Edit and Creation
                          --%>
                          <logic:notEqual name="contactForm" property="action" scope="request" value="<%= Constants.ACTION_LIST %>">

                              <logic:equal name="contactForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">

                                  <bean:define toScope="request" id="allContacts" name="contactForm" property="customerContacts" type="java.util.Collection"/>

                                  <logic:notEqual name="allContacts" property="size" scope="request" value="0">
                                    <html:form action="/editContact" onsubmit="if (this.copyContactID.value=='') { return false; }">
                                    <html:hidden property="action" value="<%= Constants.ACTION_EDIT %>"/>
                                    <tr>
                                      <td valign="middle" align="right" colspan="3">
                                        <span class="MedBlackText">Pre-Populate Form from Contact:</span>&nbsp;
                                      </td>
                                      <td valign="middle" colspan="3">
                                        <html:select property="copyContactID" size="1" >
                                          <html:option value=""></html:option>
                                          <html:options collection="allContacts" property="contactID" labelProperty="displayName"/>
                                        </html:select>
                                        <html:image altKey="" pageKey="images.action.submit" border="0"/>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td valign="top" colspan="6" height="4">
                                        <html:img height="4" border="0" pageKey="images.spacer"/>
                                      </td>
                                    </tr>
                                    </html:form>
                                  </logic:notEqual>
                              </logic:equal>

                              <!-- form HERE -->
                              <tr>
                                  <td valign="top" colspan="6" height="4"><html:img height="4" border="0" pageKey="images.spacer"/></td>
                              </tr>

                              <html:form focus="contact.title" action="/saveContact" onsubmit="return validateContactForm(this);">
                                  <html:hidden property="action"/>
                                  <html:hidden property="contact.contactID"/>
                                  <html:hidden property="contact.customerID"/>
                                  <tr>
                                    <td width="3"><html:img border="0" pageKey="images.spacer"/></td>
                                    <td align="right"><span CLASS="MedBlackText"><bean:message key="displayname.customer"/>&nbsp;<bean:message key="prompt.name"/>:</span>&nbsp;</td>
                                    <td colspan="4"><span CLASS="MedBlackText"><bean:write name="contactForm" property="contact.customer.name"/></span></td>
                                  </tr>
                                  <tr>
                                    <td width="3"><html:img border="0" pageKey="images.spacer"/></td>
                                    <td valign="middle" align="right"><span CLASS="MedBlackText"><bean:message key="prompt.title"/>:</span> &nbsp;</td>
                                    <td valign="middle">
  						                       <html:text property="contact.title" size="25" maxlength="50" />
                                    </td>
                                    <td colspan="3"><html:img border="0" pageKey="images.spacer"/></td>
                                  </tr>
                                  <tr>
                                    <td width="3">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td align="right">
                                      <span CLASS="MedBlackText"><bean:message key="prompt.firstName"/>:</span> &nbsp;
                                    </td>
                                    <td>
                                      <html:text property="contact.firstName" size="25" maxlength="128" />
                                    </td>
                                    <td width="3">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td align="right"><span CLASS="MedBlackText"><bean:message key="prompt.lastName"/>:</span>&nbsp;</td>
                                    <td>
                                      <html:text property="contact.lastName" size="25" maxlength="128" />
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" width="3">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="middle" align="right">
                                      <span CLASS="MedBlackText"><bean:message key="prompt.address"/>:</span> &nbsp;
                                    </td>
                                    <td valign="top">
                                      <html:text property="contact.ADDRESS" size="25" maxlength="128" />
                                    </td>
                                    <td valign="top" width="10">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="top" align="right">
                                      <span CLASS="MedBlackText"><bean:message key="prompt.address"/> 2:</span> &nbsp;
                                    </td>
                                    <td valign="middle" >
                                      <html:text property="contact.ADDRESS2" size="25" maxlength="128" />
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" width="3">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="middle" align="right">
                                      <span class="MedBlackText"><bean:message key="prompt.city"/>:</span> &nbsp;
                                    </td>
                                    <td valign="top">
                                      <html:text property="contact.CITY" size="25" maxlength="128" />
                                    </td>
                                    <td valign="top" width="10">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="top" align="right">
                                      <span CLASS="MedBlackText"><bean:message key="prompt.state"/>:</span> &nbsp;
                                    </td>
                                    <td valign="middle" >
                                      <html:text property="contact.STATE" size="25" maxlength="128" />
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" width="3">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="middle" align="right">
                                      <span CLASS="MedBlackText"><bean:message key="prompt.zip"/>:</span> &nbsp;
                                    </td>
                                    <td valign="top">
                                      <html:text property="contact.ZIP" size="25" maxlength="128" />
                                    </td>
                										<td colspan="3">&nbsp;</td>
                									</tr>
                									<tr>
                  									<td valign="top" width="10">
                  										<html:img border="0" pageKey="images.spacer"/>
                  									</td>
                  									<td valign="top" align="right">
                  										<span CLASS="MedBlackText"><bean:message key="prompt.country"/>:</span> &nbsp;
                  									</td>
                  									<td colspan="4" >
                    									<html:select property="contact.COUNTRY" size="1" >
                    										<bean:define id="allCountries" name="contactForm" property="allCountries" type="java.util.Collection"/>
                    										<html:options collection="allCountries" property="value" labelProperty="label"/>
                    									</html:select>
                  									</td>
                									</tr>
                                  <tr>
                                    <td valign="top" width="3">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="middle" align="right">
                                      <span CLASS="MedBlackText"><bean:message key="prompt.phone"/>:</span> &nbsp;
                                    </td>
                                    <td valign="top">
                                      <html:text property="contact.PHONE" size="25" maxlength="128" />
                                    </td>
                                    <td valign="top" width="10">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="top" align="right">
                                      <span CLASS="MedBlackText"><bean:message key="prompt.fax"/>:</span> &nbsp;
                                    </td>
                                    <td valign="middle" >
                                      <html:text property="contact.FAX" size="25" maxlength="30" />
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" width="3">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="middle" align="right">
                                      <span CLASS="MedBlackText"><bean:message key="prompt.email"/>:</span> &nbsp;
                                    </td>
                                    <td valign="middle">
                                      <html:text property="contact.email" size="25" maxlength="100" />
                                    </td>
                                    <td valign="top" width="10">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="middle" align="right">
                                      &nbsp;
                                    </td>
                                    <td valign="middle">
                                      &nbsp;
                                    </td>
                                  </tr>
                                  <!-- form HERE -->
                                  <tr>
                                    <td valign="top" colspan="6" height="4">
                                      <html:img height="4" border="0" pageKey="images.spacer"/>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td valign="top" colspan="3">
                                      <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="top" colspan="3">
<!--
                                      <html:image property="method" altKey="" pageKey="images.save_olive" border="0"><bean:message key="action.save"/></html:image>
                                      <html:image altKey="" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
                                      <html:image property="method" altKey="" pageKey="images.delete_olive" border="0" onclick="javascript:return confirm('Are you sure?');" ><bean:message key="action.delete"/></html:image>
-->
                                      <html:submit property="method" ><bean:message key="action.save"/></html:submit>
                                      <html:submit onclick="javascript:reset(); return false;" >Reset</html:submit>
                                      <logic:notEqual name="contactForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
                                        <html:submit property="method" onclick="javascript:return confirm('Are you sure?');" ><bean:message key="action.delete"/></html:submit>
                                      </logic:notEqual>
                                    </td>
                                  </tr>
                              </html:form>

                          </logic:notEqual>
                      <%--
                          END Contact Edit and Creation
                      --%>

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

          <p/>
      </td>
    </tr>
  </table>
</center>
<!-- End of body portion -->

<html:javascript formName="contactForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>

</body>
</html:html>
