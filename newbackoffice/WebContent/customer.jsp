<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ page import ="org.ei.struts.backoffice.contact.ContactForm" %>

<html:html>
<head>
    <title><bean:write name="customerForm" scope="request" property="action" filter="true"/> <bean:message key="displayname.customer"/></title>
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
                    <logic:equal name="customerForm" property="action" scope="request" value="<%= Constants.ACTION_CREATE %>">
                        <html:img pageKey="images.customer.create" border="0"/>
                    </logic:equal>
                    <logic:equal name="customerForm" property="action" scope="request" value="<%= Constants.ACTION_EDIT %>">
                        <H3><I>Modify Customer</I></H3>
                    </logic:equal>
                </td>
            </tr>
            <tr>
                <td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td>
            </tr>
            <tr>
                <td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td>
            </tr>
            <tr>
                <td valign="top" width="2" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
                <td valign="top">
                    <!-- Start of table just for the form -->
                    <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                        <tr>
                            <td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td>
                        </tr>

                        <%--
                        START Customer Listing and  Navigation
                        --%>
                        <logic:equal name="customerForm" property="action" scope="request" value="<%= Constants.ACTION_LIST %>">
                            <bean:define toScope="request" id="RESULT" name="customerForm" property="allCustomers" />
                            <logic:iterate name="RESULT" property="iterator" id="aCustomer" type="org.ei.struts.backoffice.customer.Customer">
                                <%-- Copy the customer object to request scope, the iterator creates it in page scope --%>
                                <bean:define toScope="request" id="aCustomer" name="aCustomer" type="org.ei.struts.backoffice.customer.Customer"/>
                                <template:insert template='/templates/aCustomer.jsp'>
                                    <template:put name='selector' content='/templates/_del_customer.jsp'/>
                                    <template:put name='link' content='/templates/_link_customer.jsp'/>
                                </template:insert>
                            </logic:iterate>
                            <tr>
                                <td valign="top" colspan="4">
                                &nbsp;
                                <logic:greaterEqual name="prev" value="0">
                                    <html:link forward="listCustomers" paramName="prev" paramId="offset" styleClass="LgBlueLink" >Prev</html:link>
                                </logic:greaterEqual>
                                <logic:greaterEqual name="next" value="0">
                                    <html:link forward="listCustomers" paramName="next" paramId="offset" styleClass="LgBlueLink" >Next</html:link>
                                </logic:greaterEqual>
                                </td>
                            </tr>
                        </logic:equal>
                        <%--
                        END Customer Listing and  Navigation
                        --%>

                        <%--
                        START Customer Edit and Creation
                        --%>
                        <logic:notEqual name="customerForm" property="action" scope="request" value="<%= Constants.ACTION_LIST %>">

                            <html:form action="/saveCustomer" onsubmit="return validateCustomerForm(this);">

                                <html:hidden name="customerForm" property="action"/>
                                <html:hidden property="customer.customerID"/>
                                <input type="hidden" name="memberid">

                                <tr>
                                    <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                    <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="displayname.customer"/>&nbsp;<bean:message key="prompt.id"/>:</span></td>
                                    <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                    <td valign="top"><span class="MedBlackText"><bean:write name="customerForm" property="customer.customerID" /></span></td>
                                </tr>
                                <tr>
                                    <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                    <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.name"/>:</span></td>
                                    <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                    <td valign="top"><span class="MedBlackText"><html:text property="customer.name" size="56" maxlength="128" /></span></td>
                                </tr>

                                <tr><td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.address"/>:</span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top"><span class="MedBlackText"><html:text property="customer.address1" size="56" maxlength="128" /></span></td>
                                </tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.address2"/>:</span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top"><span class="MedBlackText"><html:text property="customer.address2" size="56" maxlength="128" /></span></td>
                                </tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.city"/>:</span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top"><span class="MedBlackText"><html:text property="customer.city" size="56" maxlength="128" /></span></td>
                                </tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.state"/>:</span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top"><span class="MedBlackText"><html:text property="customer.state" size="56" maxlength="128" /></span></td>
                                </tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.zip"/>:</span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top"><span class="MedBlackText"><html:text property="customer.zip" size="56" maxlength="128" /></span></td>
                                </tr>
                                <%--
                                <tr>
                                    <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                    <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.location"/>:</span></td>
                                    <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                    <td valign="top"><span class="MedBlackText">
                                </tr>
                                --%>
                                <tr>
                                    <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                    <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.country"/>:</span></td>
                                    <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                    <td valign="top">
                                      <span class="MedBlackText">
                                        <html:select property="customer.country" size="1" >
                                          <bean:define id="allCountries" name="customerForm" property="allCountries" type="java.util.Collection"/>
                                          <html:options collection="allCountries" property="value" labelProperty="label"/>
                                        </html:select>
                                      </span>
                                    </td>
                                </tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.phone"/>:</span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top"><span class="MedBlackText"><html:text property="customer.phone" size="30" maxlength="30" /></span></td>
                                </tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.fax"/>:</span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top"><span class="MedBlackText"><html:text property="customer.fax" size="30" maxlength="30" /></span></td>
                                </tr>
                                <tr><td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.type"/></span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top">
                                    <span class="MedBlackText">
                                      <html:select property="customer.type" size="1" >
                                        <bean:define id="allTypes" name="customerForm" property="allTypes" type="java.util.Collection"/>
                                        <html:options collection="allTypes" property="value" labelProperty="label"/>
                                      </html:select>
                                    </span>
                                  </td>
                                </tr>
                                <tr><td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.region"/>:</span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top">
                                    <span class="MedBlackText">
                                      <html:select property="customer.region" size="1" >
                                        <bean:define id="allRegions" name="customerForm" property="allRegions" type="java.util.Collection"/>
                                        <html:options collection="allRegions" property="regionID" labelProperty="regionName"/>
                                      </html:select>
                                    </span>
                                  </td>
                                </tr>
                                <tr><td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
                                <tr>
                                    <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                    <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.industry"/>:</span></td>
                                    <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                    <td valign="top">
                                      <span class="MedBlackText">
                                        <html:select property="customer.industry" size="1" >
                                          <bean:define id="allIndustries" name="Constants" property="industries" type="java.util.Collection"/>
                                          <html:options collection="allIndustries" property="value" labelProperty="label"/>
                                        </html:select>
                                      </span>
                                    </td>
                                </tr>

                                <%-- CONSORTIUM ADD TO / REMOVE FROM --%>
                                <logic:equal name="customerForm" property="action" scope="request" value="<%= Constants.ACTION_EDIT%>">
                                    <tr><td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
                                    <tr>
                                        <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                        <td valign="middle" align="right">
                                            <span class="MedBlackText">
                                                <logic:equal name="customerForm" property="customer.consortium" value="<%= Constants.NO %>">
                                                    Member of <bean:message key="displayname.consortium"/>:
                                                </logic:equal>
                                                <logic:equal name="customerForm" property="customer.consortium" value="<%= Constants.YES %>">
                                                    <bean:message key="prompt.members"/>:
                                                </logic:equal>
                                            </span>
                                        </td>
                                        <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                        <td valign="top">

                                            <logic:equal name="customerForm" property="customer.consortium" value="<%= Constants.NO %>">
                                                <logic:notEqual name="customerForm" property="customer.parentID" value="0">
                                                    <bean:define id="aCustomer" name="customerForm" property="customer.parentCompany" />
                                                    <a href='javascript:doRemove();' Class="LgBlueLink" >Remove</a>
                                                    &nbsp;
                                                    <html:link forward="editCustomer" paramName="aCustomer" paramProperty="customerID" paramId="customerid" styleClass="LgBlueLink" >
                                                        <bean:write name="aCustomer" property="customerID" filter="true" />&nbsp;-&nbsp;<bean:write name="aCustomer" property="name" filter="true"/>
                                                    </html:link>
                                                    <%-- jam - bug fix - overwrites parent ID with 0 - need to hide
                                                        value when parent has been assigned so it is not overwritten. --%>
                                                    <html:hidden name="customerForm" property="customer.parentID"/>
                                                </logic:notEqual>
                                                <logic:equal name="customerForm" property="customer.parentID" value="0">
                                                    <html:text name="customerForm" property="customer.parentID"/>
                                                </logic:equal>

                                            </logic:equal>

                                            <logic:equal name="customerForm" property="customer.consortium" value="<%= Constants.YES %>">
                                                <table border="0" cellspacing="0" cellpadding="0" >
                                                    <bean:define id="RESULT" name="customerForm" property="consortiumMembers"/>
                                                    <logic:iterate name="RESULT" property="iterator" id="aCustomer" type="org.ei.struts.backoffice.customer.Customer">
                                                        <html:link forward="editCustomer" paramName="aCustomer" paramProperty="customerID" paramId="customerid" styleClass="LgBlueLink" >
                                                            <bean:write name="aCustomer" property="customerID" filter="true"/>&nbsp;-&nbsp;<bean:write name="aCustomer" property="name" filter="true"/>&nbsp;<bean:write name="aCustomer" property="country" filter="true"/>
                                                        </html:link>
                                                        <br/>
                                                    </logic:iterate>
                                                </table>
                                            </logic:equal>
                                        </td>
                                    </tr>
                                </logic:equal>

                                <tr><td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
                                <tr>
                                    <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                    <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.since"/>:</span></td>
                                    <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                    <td valign="top">
                                        <span class="MedBlackText"><bean:write name="customerForm" property="customer.displayStartDate" /></span>
                                    </td>
                                </tr>
                                <tr><td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
                                <tr>
                                  <td valign="top" width="3"><html:img width="3" border="0" pageKey="images.spacer"/></td>
                                  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="prompt.otherid"/>:</span></td>
                                  <td valign="top" width="10"><html:img width="10" border="0" pageKey="images.spacer"/></td>
                                  <td valign="top">
                                    <span class="MedBlackText"><html:text property="customer.otherID" size="15" maxlength="15" /></span>
                                  </td>
                                </tr>
                                <tr><td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
                                <tr>
                                    <td valign="top" colspan="3">
                                        <html:img border="0" pageKey="images.spacer"/>
                                    </td>
                                    <td valign="top">
                                        <html:image altKey="alt.save" pageKey="images.save_olive" border="0"/>
                                        <html:image altKey="alt.reset" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
                                    </td>
                                </tr>

                            </html:form>
                        </logic:notEqual>
                        <%--
                        END Customer Edit and Creation
                        --%>
                        <tr><td valign="top" colspan="4" ><html:img height="4" border="0" pageKey="images.spacer"/></td></tr>
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

      <%--
        START CONTACT Listings
      --%>
      <logic:equal name="customerForm" property="action" scope="request" value="<%= Constants.ACTION_EDIT %>">

        <logic:equal name="customerForm" property="contacts" scope="request" value="false">
          <html:form action="/editCustomer" >
            <input type="hidden" name="action" value="<%= Constants.ACTION_EDIT %>"/>
            <input type="hidden" name="contacts" value="true"/>
            <input type="hidden" name="customerid" value='<bean:write name="customerForm" property="customer.customerID"/>'/>
            <html:image pageKey="images.action.viewcontacts" property="contacts" />
          </html:form>
        </logic:equal>

        <logic:equal name="customerForm" property="contacts" scope="request" value="true">

          <html:form action="/editCustomer" >
            <input type="hidden" name="action" value="<%= Constants.ACTION_EDIT %>"/>
            <input type="hidden" name="customerid" value='<bean:write name="customerForm" property="customer.customerID"/>'/>
            <input type="hidden" name="contacts" value="false"/>
            <html:image pageKey="images.action.hidecontacts" property="contacts" />
          </html:form>

          <bean:define toScope="request" id="RESULT" name="customerForm" property="customerContacts" />
          <bean:define toScope="request" id="Border" value="#BCBD78"/>
          <bean:define toScope="request" id="Inner" value="#EFEFD6"/>
          <bean:define toScope="request" id="Heading" value="#DEDEAE"/>

          <!-- Start of table to put the form -->
          <table border="0" cellspacing="0" cellpadding="0" width="600">
            <tr><td valign="top" colspan="3" ><html:img height="20" border="0" pageKey="images.spacer"/></td></tr>
            <tr>
              <td valign="top" colspan="3">
                <html:img pageKey="images.heading.contacts" border="0"/>
                <html:link forward="createContact" paramName="customerForm" paramProperty="customer.customerID" paramId="customerid" >
                  <html:img pageKey="images.action.addnewcontact" border="0"/>
                </html:link>
              </td>
            </tr>
            <tr><td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td></tr>
            <tr><td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>' ><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
            <tr>
              <td valign="top" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
              <td valign="top">
                <!-- Start of table just for the form -->
                <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>
                  <tr>
                    <td valign="top" colspan="2" bgcolor='<bean:write name="Heading"/>' >&nbsp;<span class="MedBoldBlackText"><bean:message key="prompt.name"/></span></td>
                    <td valign="top" width="1" bgcolor='<bean:write name="Border"/>' ><html:img pageKey="images.spacer" border="0" width="1"/></td>
                    <td valign="top" bgcolor='<bean:write name="Heading"/>'>&nbsp;<span class="MedBoldBlackText"> &nbsp; </span></td>
                    <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" border="0" width="1"/></td>
                    <td valign="top" bgcolor='<bean:write name="Heading"/>'>&nbsp;<span class="MedBoldBlackText"><bean:message key="prompt.email"/></span></td>
                    <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" border="0" width="1"/></td>
                    <td valign="top" bgcolor='<bean:write name="Heading"/>'>&nbsp;<span class="MedBoldBlackText"><bean:message key="prompt.phone"/></span></td>
                  </tr>
                  <tr>
                    <td valign="top" colspan="8" bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" border="0" height="1"/></td>
                  </tr>

                  <logic:iterate scope="request" indexId="idxContact" id="aContact" name="RESULT" property="iterator" type="org.ei.struts.backoffice.contact.Contact">
                    <tr>
                      <td valign="top" width="15">&nbsp;</td>
                      <td valign="top">
                        <%
                          ContactForm contactForm = new ContactForm();
                          contactForm.setContact(aContact);
                          pageContext.setAttribute("aContactForm", contactForm);
                        %>
                        <html:form action="/deleteContact" onsubmit="javascript:return confirm('Are You Sure?');">
                          <html:hidden name="aContactForm" property="action" value="Delete"/>
                          <html:hidden property="method" value="Delete"/>
                          <html:hidden name="aContactForm" property="contact.contactID"/>
                          <html:hidden name="aContactForm" property="contact.customerID"/>

                          <html:image altKey="alt.remove.contact" pageKey="images.action.x" border="0" />
                          <%-- These elements are not part of the form, but IE insisted on wrapping unless they were inside the form tag --%>
                            <html:link forward="editContact" paramName="aContact" paramProperty="contactID" paramId="contactid" styleClass="LgBlueLink" >
                              <bean:write name="aContact" property="displayName" filter="true"/>
                            </html:link>
                        </html:form>
                      </td>
                      <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" border="0" width="1"/></td>
                      <td valign="top">&nbsp;<span CLASS="SmBlackText"> &nbsp; <span></td>
                      <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" border="0" width="1"/></td>
                      <td valign="top">&nbsp;<span CLASS="SmBlackText"><bean:write name="aContact" property="email" filter="true"/></span></td>
                      <td valign="top" width="1" bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" border="0" width="1"/></td>
                      <td valign="top">&nbsp;<span CLASS="SmBlackText"><bean:write name="aContact" property="PHONE" filter="true"/></span></td>
                    </tr>
                    <tr>
                      <td valign="top" colspan="8" height="1" bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" border="0" height="1"/></td>
                    </tr>

                  </logic:iterate>

                </table>
                <!-- End of table just for the form -->
              </td>
              <td valign="top" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
            </tr>
            <tr><td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>' ><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
          </table>
          <!-- End of table to put the form -->

        </logic:equal>

      </logic:equal>

      <%--
        END CONTACT Listings
      --%>

      <P/>

      <%--
        START Contract Listings
      --%>
      <logic:equal name="customerForm" property="action" scope="request" value="<%= Constants.ACTION_EDIT %>">

        <bean:define toScope="request" id="RESULT" name="customerForm" property="customer.allContracts" />

        <bean:define toScope="request" id="Border" value="#8E92BF"/>
        <bean:define toScope="request" id="Heading" value="#C1C3DB"/>
        <bean:define toScope="request" id="Inner" value="#E5E5F1"/>

        <!-- Start of table to put the form -->
        <table border="0" cellspacing="0" cellpadding="0" width="600">
          <tr><td valign="top" colspan="3" ><html:img height="20" border="0" pageKey="images.spacer"/></td></tr>
          <tr>
            <td valign="top" colspan="3">
              <html:img pageKey="images.heading.contracts" border="0"/>
              <html:link forward="createContract" name="customerForm" property="createContract">
                <html:img pageKey="images.action.addcontract" border="0"/>
              </html:link>
            </td>
          </tr>
          <tr><td valign="top" colspan="3" ><html:img height="10" border="0" pageKey="images.spacer"/></td></tr>
          <tr><td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>' ><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
          <tr>
            <td valign="top" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
            <td valign="top">

              <!-- Start of table just for the form -->
              <table border="0" cellspacing="0" cellpadding="0" width="600" bgcolor='<bean:write name="Inner"/>'>

                  <template:insert template='/templates/table.jsp'>
                    <template:put name='header' content='/templates/_header.jsp'/>
                    <template:put name='body'   content='/templates/_nested_ent.jsp'/>
                  </template:insert>

              </table>
            <!-- End of table just for the form -->
            </td>
            <td valign="top" bgcolor='<bean:write name="Border"/>'><html:img width="2" border="0" pageKey="images.spacer"/></td>
          </tr>
          <tr>
            <td valign="top" colspan="3" bgcolor='<bean:write name="Border"/>'><html:img height="2" border="0" pageKey="images.spacer"/></td>
          </tr>
        </table>
        <!-- End of table to put the form -->

      </logic:equal>
      <%--
        END Contract Listings
      --%>

      <P/>

    </td>
  </tr>
</table>
</center>
<!-- End of body portion -->

<html:javascript formName="customerForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>

<script language="Javascript1.1">
function doRemove(memberid) {
    document.forms[0].action.value = "Remove";
    document.forms[0].submit();
}
</script>



</body>
</html:html>
