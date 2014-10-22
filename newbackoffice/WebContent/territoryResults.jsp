<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ page import="org.ei.struts.backoffice.customer.*" %>
<%@ page import="org.ei.struts.backoffice.contract.*" %>
<%@ page import="org.ei.struts.backoffice.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="Constants" class="org.ei.struts.backoffice.Constants" scope="application"/>

<bean:size id="searchResultsCount" name="territoryForm" property="searchResults" scope="request"/>

    <html:html>
      <head>
        <title>Territory Report</title>
        <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
      </head>

      <body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

        <!-- top logo area-->

        <center>
          <table border="0" width="99%" cellspacing="0" cellpadding="0">
            <tr><td valign="top" height="45" bgcolor="#CCCCCC"><img src="/backoffice/images/spacer.gif" border="0" height="45"></td></tr>
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
              <td valign="top" width="10"><img src="/backoffice/images/spacer.gif" border="0" width="10"></td>
              <td valign="top">

                <bean:define toScope="page" id="columns" value="7"/>

                <!-- Start of table to put the form -->
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                  <tr>
                    <td valign="top" colspan='<bean:write name="columns"/>'>
                      <img src="/backoffice/images/spacer.gif" border="0" height="20">
                    </td>
                  </tr>
                  <tr>
                    <td valign="top" colspan='<bean:write name="columns"/>'>
                      <html:link page="/territory.do" styleClass="LgBlueLink">Territory Report</html:link> - <bean:write name="territoryForm" property="territory.label" />
                    </td>
                  </tr>
                  <tr>
                    <td valign="top" colspan='<bean:write name="columns"/>'>
                      <img src="/backoffice/images/spacer.gif" border="0" height="10">
                    </td>
                  </tr>
                  <tr>
                    <td valign="top" height="20" colspan='<bean:write name="columns"/>'>
                      <span class="MedBlackText"><b>Total <bean:write name="searchResultsCount"/> found:</b></span>
                    </td>
                  </tr>
                  <tr>
                    <td valign="top" height="10" colspan='<bean:write name="columns"/>'><img src="/backoffice/images/spacer.gif" border="0" height="10"></td>
                  </tr>
                  <tr>
                    <td valign="top" height="1" colspan='<bean:write name="columns"/>' bgColor="black"></td>
                  </tr>
                  <tr>
                    <td valign="top" height="2" colspan='<bean:write name="columns"/>'><img src="/backoffice/images/spacer.gif" border="0" height="2"></td>
                  </tr>
                  <tr>
                    <td width="25"><b>ID</b></td>
                    <td width="125"><b>Name</b></td>
                    <td width="100"><b>Sales Region</b></td>
                    <td width="100"><b>Contract ID</b></td>
                    <td width="100"><b>Product</b></td>
                    <td width="50"><b>Start Date</b></td>
                    <td width="50"><b>End Date</b></td>
                  </tr>
                  <tr>
                    <td valign="top" height="2" colspan='<bean:write name="columns"/>'><img src="/backoffice/images/spacer.gif" border="0" height="2"></td>
                  </tr>
                  <tr>
                    <td valign="top" height="1" colspan='<bean:write name="columns"/>' bgColor="black"></td>
                  </tr>

                  <logic:iterate indexId="custIndex" id="aCustomer" name="territoryForm" property="searchResults">

                    <%
                    int mod = ((((Integer) pageContext.getAttribute("custIndex")).intValue()) % 2);
                    pageContext.setAttribute("mod", (new Integer(mod)));
                    %>

                    <logic:equal name="mod" value="0">
                      <bean:define id="bkgd" value="#CCCCCC"/>
                    </logic:equal>
                    <logic:equal name="mod" value="1">
                      <bean:define id="bkgd" value="#EEEEEE"/>
                    </logic:equal>
                    <tr bgcolor='<bean:write name="bkgd"/>'>
                      <td width="50">
                        <span class="MedBlackText"><%= ((((Integer) pageContext.getAttribute("custIndex")).intValue())+1) %>.</span>&nbsp;<html:link forward="editCustomer" paramName="aCustomer" paramProperty="customerID" paramId="customerid" styleClass="LgBlueLink"><bean:write name="aCustomer" property="customerID"/></html:link>
                      </td>
                      <td width="100"><span class="MedBlackText"><bean:write name="aCustomer" property="name"/></span></td>
                      <td width="100"><span class="MedBlackText">&nbsp;</span></td>

                        <bean:define toScope="request" id="RESULT" name="aCustomer" property="allContracts" />
                        <logic:equal name="RESULT" property="size" value="0">
                            <td width="100">&nbsp;</td>
                            <td width="100">&nbsp;</td>
                            <td width="50">&nbsp;</td>
                            <td width="50">&nbsp;</td>
                          </tr>
                        </logic:equal>

                        <logic:iterate indexId="cntctIndex" id="aContract" name="RESULT" property="iterator" scope="request">

                          <jsp:setProperty name="aContract" property="itemFilter" value='<%= request.getParameterValues("products") %>'/>
                          <bean:define toScope="request" id="NESTED_RESULT" name="aContract" property="allItems" />

                          <bean:size id="itemSize" name="NESTED_RESULT"/>
                          <logic:notEqual name="itemSize" value="0">

                            <logic:notEqual name="cntctIndex" value="0">
                              <tr bgcolor='<bean:write name="bkgd"/>'>
                                <td width="100">&nbsp;</td>
                                <td width="100">&nbsp;</td>
                                <td width="100">&nbsp;</td>
                            </logic:notEqual>

                            <td width="100"><SPAN class="MedBlackText"><bean:write name="aContract" property="contractID"/>&nbsp;/<logic:equal name="aContract" property="isEnabled" value="true">ACTIVE</logic:equal><logic:notEqual name="aContract" property="isEnabled" value="true">inactive</logic:notEqual></SPAN></td>

                            <logic:iterate indexId="itemIndex" id="aItem" name="NESTED_RESULT"  scope="request">

                              <logic:notEqual name="itemIndex" value="0">
                                <tr bgcolor='<bean:write name="bkgd"/>'>
                                  <td width="50">&nbsp;</td>
                                  <td width="100">&nbsp;</td>
                                  <td width="100">&nbsp;</td>
                                  <td width="100">&nbsp;</td>
                              </logic:notEqual>

                                <td width="100"><SPAN class="MedBlackText"><bean:write name="aItem" property="product.name"/></SPAN></td>
                                <td width="50"><SPAN class="MedBlackText"><bean:write name="aContract" property="startdate.displaydate"/></SPAN></td>
                                <td width="50"><SPAN class="MedBlackText"><bean:write name="aContract" property="enddate.displaydate"/></SPAN></td>
                              </tr>

                            </logic:iterate>
                          </logic:notEqual>
                      </logic:iterate>
                  </logic:iterate>
                  <%-- Spacer.Margin so end of page doesn't look cut off --%>
                  <tr>
                    <td valign="top" colspan="6"><img src="/backoffice/images/spacer.gif" border="0" height="25"></td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </center>
        <p/>
      </body>

    </html:html>
