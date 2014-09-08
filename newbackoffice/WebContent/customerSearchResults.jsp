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

<bean:define toScope="request" id="customerResults" name="searchCustomerForm" property="searchResults" />

<bean:size id="searchResultsCount" name="searchCustomerForm" property="searchResults" scope="request"/>

<%
  Collection activeProducts = Constants.getActiveProducts();
  String[] activeProductFilter = (String[]) activeProducts.toArray(new String[]{});
%>

<html:html>
  <head>
    <title>Search Results</title>
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

      <!-- Start of table to put the results -->
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
          <td valign="top" colspan="6">
            <img src="/backoffice/images/spacer.gif" border="0" height="20">
          </td>
        </tr>
        <tr>
          <td valign="top" colspan="6">
            <img src="/backoffice/images/searchresults.gif" border="0">
          </td>
        </tr>
        <tr>
          <td valign="top" colspan="6">
            <img src="/backoffice/images/spacer.gif" border="0" height="10">
          </td>
        </tr>
        <tr>
          <td valign="top" height="20" colspan="6">
            <logic:equal name="searchCustomerForm" property="possibleMatches" value="true">
              Your Search found no results, but here are <bean:write name="searchResultsCount"/> <b>possible</b> matches: </td>
            </logic:equal>
            <logic:equal name="searchCustomerForm" property="possibleMatches" value="false">
              Your Search found <bean:write name="searchResultsCount"/> results: </td>
            </logic:equal>
        </tr>
        <tr>
          <td valign="top" height="10" colspan="6"><img src="/backoffice/images/spacer.gif" border="0" height="10"></td>
        </tr>
        <tr>
          <td valign="top" height="1" colspan="6" bgColor="black"></td>
        </tr>
        <tr>
          <td valign="top" height="2" colspan="6"><img src="/backoffice/images/spacer.gif" border="0" height="2"></td>
        </tr>
        <tr>
          <td width="50"><b>ID</b></td>
          <td width="100"><b>Name</b></td>
          <td width="100"><b>Contract ID</b></td>
          <td width="100"><b>Product</b></td>
          <td width="100"><b>Start Date</b></td>
          <td width="100"><b>End Date</b></td>
        </tr>
        <tr>
          <td valign="top" height="2" colspan="6"><img src="/backoffice/images/spacer.gif" border="0" height="2"></td>
        </tr>
        <tr>
          <td valign="top" height="1" colspan="6" bgColor="black"></td>
        </tr>

        <logic:iterate indexId="custIndex" id="aCustomer" name="searchCustomerForm" property="searchResults">

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
              <SPAN class="MedBlackText"><%= ((((Integer) pageContext.getAttribute("custIndex")).intValue())+1) %>.</SPAN>&nbsp;<html:link forward="editCustomer" paramName="aCustomer" paramProperty="customerID" paramId="customerid" styleClass="LgBlueLink"><bean:write name="aCustomer" property="customerID"/></html:link>
            </td>
            <td width="100"><SPAN class="MedBlackText"><bean:write name="aCustomer" property="name"/></SPAN></td>
            <bean:define toScope="request" id="RESULT" name="aCustomer" property="allContracts" />

            <logic:equal name="RESULT" property="size" value="0">
                <td width="100">&nbsp;</td>
                <td width="100">&nbsp;</td>
                <td width="100">&nbsp;</td>
                <td width="100">&nbsp;</td>
              </tr>
            </logic:equal>

            <logic:iterate indexId="cntctIndex" id="aContract" name="RESULT" property="iterator" scope="request">

              <jsp:setProperty name="aContract" property="itemFilter" value='<%= activeProductFilter %>'/>

              <logic:notEqual name="cntctIndex" value="0">
                <tr bgcolor='<bean:write name="bkgd"/>'>
                  <td width="100">&nbsp;</td>
                  <td width="100">&nbsp;</td>
              </logic:notEqual>

              <td style="border-bottom:1 solid black;" width="100">
                  <SPAN class="MedBlackText"><bean:write name="aContract" property="contractID"/> /</SPAN>
                  <logic:equal name="aContract" property="isEnabled" value="true">
                    <span class="MedBoldBlackText">Access Enabled</span>
                  </logic:equal>
                  <logic:equal name="aContract" property="isEnabled" value="false">
                    <span class="MedBoldRedText">Access Disabled</span>
                  </logic:equal>
              </td>

              <bean:define toScope="request" id="NESTED_RESULT" name="aContract" property="allItems" />

              <logic:iterate indexId="entIndex" id="aItem" name="NESTED_RESULT"  scope="request">
                <logic:notEqual name="entIndex" value="0">
                  <tr bgcolor='<bean:write name="bkgd"/>'>
                    <td width="50">&nbsp;</td>
                    <td width="100">&nbsp;</td>
                    <td width="100">&nbsp;</td>
                </logic:notEqual>

                <td style="border-bottom:1 solid black;" width="100"><SPAN class="MedBlackText"><bean:write name="aItem" property="product.name"/></SPAN></td>
                <td style="border-bottom:1 solid black;" width="100"><SPAN class="MedBlackText"><bean:write name="aContract" property="startdate.displaydate"/></SPAN></td>
                <td style="border-bottom:1 solid black;" width="100"><SPAN class="MedBlackText"><bean:write name="aContract" property="enddate.displaydate"/></SPAN></td>
              </logic:iterate>

              <bean:size id="itemSize" name="NESTED_RESULT"/>
              <logic:equal name="itemSize" value="0">
                <td style="border-bottom:1 solid black;" width="100">&nbsp;</td>
                <td style="border-bottom:1 solid black;" width="100">&nbsp;</td>
                <td style="border-bottom:1 solid black;" width="100">&nbsp;</td>
              </logic:equal>
            </tr>
          </logic:iterate>
        </logic:iterate>
        <%-- Spacer.Margin so end of page doesn't look cut off --%>
        <tr>
          <td valign="top" colspan="6"><img src="/backoffice/images/spacer.gif" border="0" height="25"></td>
        </tr>
  </table>
</body>

</html:html>
