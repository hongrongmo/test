<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<app:checkLogon/>

<bean:define toScope="request" id="Border" value="#BCBD78"/>
<bean:define toScope="request" id="Inner" value="#EFEFD6"/>

<html:html>
  <HEAD>
<title>Reports</title>

      <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
  </HEAD>

<body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

<jsp:useBean id="Constants" class="org.ei.struts.backoffice.Constants" scope="application"/>

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

      <table border="0" cellSpacing=0 cellPadding=0 width="100%" >
        <tr>
          <td valign="top" colspan=3 height=20><html:img height="20"  pageKey="images.spacer" border="0"/></td>
        </tr>
        <tr>
          <td valign="top" colspan=3><html:img page="/images/searchcustomer.gif" border="0"/></td>
        </tr>
        <tr>
          <td valign="top" colSpan=3 height=4>
          <html:img height="4"  pageKey="images.spacer" border="0"/>
          <html:errors/>
          </td>
        </tr>
     <%--
        <tr>
          <td valign="top" colSpan=3><span class="MedBlackText">
          	<B>To search please enter one or more fields below and click on "Search".</B>
          	Please use '%' for truncation. For example, New% yields New Jersey and New York. </span></td>
        </tr>
     --%>
        <tr>
          <td valign="top" height="2" bgcolor='<bean:write name="Border"/>' colspan="3"><html:img height="2" border="0" pageKey="images.spacer"/></td>
        </tr>
        <tr>
          <td valign="top"width=2 bgcolor='<bean:write name="Border"/>'><html:img pageKey="images.spacer" width="2" border="0"/></td>
          <td vAlign=top>

            <!-- Start of table just for the form -->
            <table border="0" cellSpacing=0 cellPadding=0 width="100%" bgcolor='<bean:write name="Inner"/>'>

              <tr>
                <td valign="top" colSpan="5" >
                	<html:img height="4"  pageKey="images.spacer" border="0" />
                </td>
              </tr>

              <tr>
                <td valign="top" colSpan="5" >
                	<html:form action="/reportCustomer" method="post">
               		<html:hidden  property="action" value="report"/>
                	<html:img height="4"  pageKey="images.spacer" border="0"/>
                </td>
              </tr>
              <html:hidden  property="contactName"/>
              <html:hidden  property="customerName"/>
        <%--
              <tr>
                <td valign="top"width="3"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
                <td vAlign=center align="left" width="150"><span CLASS="MedBlackText">Search Contact Name, customer ID, contract ID, username or IP Address:</span></td>
                <td valign="top"width="10"><html:img  pageKey="images.spacer" width="10" border="0"/></td>
                <td vAlign=top><html:text size="28"  property="contactName"/></span></td>
                <td valign="top"width="3"><html:img pageKey="images.spacer" width="3" border="0"/></td>
              </tr>



              <tr>
                <td valign="top" colspan=3 height=4><html:img height="4"  pageKey="images.spacer" border="0"/></td>
                <td colspan="2"><span class="MedBlackText">This field is exclusive with all the below</span></td>
              </tr>

              <tr>
                <td valign="top" colspan=5 height=10><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>

              <tr>
                <td valign="top"width="3"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
                <td vAlign=center align="right" width="150"><span CLASS="MedBlackText">Search customer, consortium:</span></td>
                <td valign="top"width="10"><html:img  pageKey="images.spacer" width="10" border="0"/></td>
                <td vAlign=top width="200"><span class="MedBlackText"><html:text size="28" property="customerName"/></span></td>
                <td valign="top"width="3"><html:img pageKey="images.spacer" width="3" border="0"/></td>
              </tr>
      --%>

              <tr>
                <td valign="top" colSpan="5" ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>
              <tr>
                <td valign="top"width="50"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
                <td vAlign=center align="right" width="150"><span CLASS="MedBlackText"><bean:message key="displayname.customer"/><bean:message key="prompt.type"/>:</span></td>
                <td valign="top"width="50"><html:img pageKey="images.spacer" width="10" border="0"/></td>
                <td vAlign=top width="150">
                  <html:select multiple="true" property="customerType">
                  <option value="">All</option>
                  <bean:define id="allType" name="searchCustomerForm" property="allType" type="java.util.Collection"/>
                  <html:options collection="allType" property="value" labelProperty="label"/>
                  </html:select>
                </td>
                <td valign="top"width="3"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
              </tr>


              <tr>
                <td valign="top" colSpan="5" ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>
              <tr>
                <td valign="top"width="3"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
                <td vAlign=center align="right" width="150"><span CLASS="MedBlackText"><bean:message key="prompt.region"/>:</span></td>
                <td valign="top"width="10"><html:img pageKey="images.spacer" width="10" border="0"/></td>
                <td vAlign=top>
                  <html:select multiple="true" property="salesRegion">
                  	<option value="">All</option>
                  	<bean:define id="allRegions" name="searchCustomerForm" property="allRegions" type="java.util.Collection"/>
                  	<html:options collection="allRegions" property="regionID" labelProperty="regionName"/>
                  </html:select>
                </td>
                <td valign="top"width="3"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
              </tr>
               <tr>
	      	           <td valign="top" colSpan="5"  ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>
              <tr>
                <td valign="top"width="3">
                <html:img  pageKey="images.spacer" width="3" border="0"/></td>
                <td vAlign=center align="right" width="150"><span class="MedBlackText"><bean:message key="displayname.contract"/> <bean:message key="prompt.type"/>:</span></td>
                <td valign="top"width="10"><html:img pageKey="images.spacer" width="10" border="0"/></td>
                <td vAlign=top>
                <html:select multiple="true" property="contractType">
                <option value="">All</option>
                <bean:define id="allContractType" name="searchCustomerForm" property="allContractType" type="java.util.Collection"/>
                <html:options collection="allContractType" property="label" labelProperty="value"/>
                <%--
			<logic:iterate id="aContractType" name="allContractType" type="org.apache.struts.util.LabelValueBean">
				<logic:notEqual  name="aContractType" property="value"  value="">
					<option value="<bean:write name="aContractType" property="value" filter="true"/>">
						<bean:write name="aContractType" property="label" filter="true"/>
					</option>
				</logic:notEqual>
			</logic:iterate>
		--%>
                </html:select>
                </td>
                <td valign="top"width="3">
                <html:img  pageKey="images.spacer" width="3" border="0"/></td>
              </tr>
               <tr>
	      	           <td valign="top" colSpan="5"  ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>
              <tr>
                <td valign="top"width="3">
                <html:img  pageKey="images.spacer" width="3" border="0"/></td>
                <td vAlign=center align="right" width="150">
                <span CLASS="MedBlackText" ><bean:message key="prompt.products"/>:</span></td>
                <td valign="top"width="10">
                <html:img pageKey="images.spacer" width="10" border="0"/></td>
                <td vAlign=top>
                <html:select  multiple="true" size="4" property="products">
                <option value="">All</option>
                <bean:define id="allProducts" name="searchCustomerForm" property="allProducts" type="java.util.Collection"/>
                <html:options collection="allProducts" property="productID" labelProperty="name"/>
                </html:select>
                </td>
                <td valign="top"width="3">
                <html:img  pageKey="images.spacer" width="3" border="0"/></td>
              </tr>
	      <tr>
			<td valign="top" colSpan="5"  ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>
              <tr>
		<td valign="top" width="3">
			<html:img width="3" border="0" src="images/spacer.gif"/>
		</td>
		<td valign="middle" align="right" width="150">
			<span class="MedBlackText"><bean:message key="prompt.accessType"/>:</span>
		</td>
		<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">
		   <html:select  multiple="true" size="4" property="accessType">
		     <bean:define id="allAccessType" name="searchCustomerForm" property="allAccessType" type="java.util.Collection"/>
		     <option value="">All</option>
		     <html:options collection="allAccessType" property="value" labelProperty="label"/>
		     <%--
		     	<logic:iterate id="aAccessType" name="allAccessType" type="org.apache.struts.util.LabelValueBean">
		     		<logic:notEqual  name="aAccessType" property="value"  value="">
					<option value="<bean:write name="aAccessType" property="value" filter="true"/>">
						<bean:write name="aAccessType" property="label" filter="true"/>
					</option>
				</logic:notEqual>
			</logic:iterate>
		      --%>
		   </html:select>
		</td>
		<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
	      </tr>
               <tr>
	      	           <td valign="top" colSpan="5"  ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>
	      <tr>
		<td valign="top" width="3">
			<html:img width="3" border="0" src="images/spacer.gif"/>
		</td>
		<td valign="middle" align="right" width="150">
			<span class="MedBlackText"><bean:message key="prompt.status"/>:</span>
		</td>
		<td valign="top" width="10"><html:img width="10" border="0" src="images/spacer.gif"/></td><td valign="top">

		<html:select name="searchCustomerForm" property="status" size="4" multiple="true">
			<bean:define toScope="request" id="allStatus" name="searchCustomerForm" property="allStatus" type="java.util.Collection"/>
			<option value="">All</option>
			<html:options collection="allStatus" property="value" labelProperty="label"/>
		</html:select>

		</td>
		<td valign="top" width="3"><html:img width="3" border="0" src="images/spacer.gif"/></td>
	      </tr>


              <tr>
                <td valign="top" colSpan="5"  ><html:img height="4"  pageKey="images.spacer" border="0"/></td>
              </tr>

              <tr><td valign="top" colSpan="5" ><html:img height="4"  pageKey="images.spacer" border="0"/></td></tr>
              <tr>
                <td valign="top"width="3"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
                <td valign="top"align="right" width="150"><span class="MedBlackText">Show active records only</span></td>
                <td valign="top"width="10"><html:img pageKey="images.spacer" width="10" border="0"/></td>
                <td valign="top"width="10"><html:checkbox  value="yes" property="active"/>&nbsp;</td>
                <td valign="top"width="3"><html:img  pageKey="images.spacer" width="3" border="0"/></td>
              </tr>
              <tr><td valign="top" colSpan="5"><html:img height="4"  pageKey="images.spacer" border="0"/></td></tr>

				<%--
					Define 'static' collections used for populating dropdowns
				--%>
				<bean:define id="allMonths" name="searchCustomerForm" property="allMonths" type="java.util.Collection"/>
				<bean:define id="allDays" name="searchCustomerForm" property="allDays" type="java.util.Collection"/>
				<bean:define id="allYears" name="searchCustomerForm" property="allYears" type="java.util.Collection"/>

              <tr>
                <td valign="top" width="3"><html:img  pageKey="images.spacer" border="0" width="3"/></td>
                <td valign="middle" align="right" width="150"><span CLASS="MedBlackText" ><bean:message key="prompt.startDate"/>: From</span> </td>
                <td valign="top" width="10"><html:img height="4"  pageKey="images.spacer" border="0"/></td>
                <td valign="top">

					<html:select property="startFromMonth" size="1">
						<html:options collection="allMonths" property="value" labelProperty="label"/>
					</html:select>

					<html:select  property="startFromDay" size="1">
						<html:options collection="allDays" property="value" labelProperty="label"/>
					</html:select>

	                <html:select  property="startFromYear" size="1">
						<html:options collection="allYears" property="value" labelProperty="label"/>
	                </html:select>

                </td>
                <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
              </tr>
              <tr>
                <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
                <td valign="middle" align="right" width="150"><span CLASS="MedBlackText">To</span></td>
                <td valign="top" width="10"><html:img height="4"  pageKey="images.spacer" border="0"/></td>
                <td valign="top">

					<html:select property="startToMonth" size="1">
						<html:options collection="allMonths" property="value" labelProperty="label"/>
					</html:select>

					<html:select  property="startToDay" size="1">
						<html:options collection="allDays" property="value" labelProperty="label"/>
					</html:select>

					<html:select  property="startToYear" size="1">
						<html:options collection="allYears" property="value" labelProperty="label"/>
					</html:select>

                </td>
                <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
              </tr>
              <tr>
                <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
                <td valign="middle" align="right" width="150"><span CLASS="MedBlackText"><bean:message key="prompt.endDate"/>: From</span> </td>
                <td valign="top" width="10"><html:img height="4"  pageKey="images.spacer" border="0"/></td>
                <td valign="top">

					<html:select property="endFromMonth" size="1">
						<html:options collection="allMonths" property="value" labelProperty="label"/>
					</html:select>

					<html:select  property="endFromDay" size="1">
						<html:options collection="allDays" property="value" labelProperty="label"/>
					</html:select>

					<html:select  property="endFromYear" size="1">
						<html:options collection="allYears" property="value" labelProperty="label"/>
					</html:select>

                </td>
                <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
              </tr>
              <tr>
                <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
                <td valign="middle" align="right" width="150"><span CLASS="MedBlackText">To</span></td>
                <td valign="top" width="10">
                <html:img height="4"  pageKey="images.spacer" border="0"/>
                </td>
                <td valign="top">

					<html:select property="endToMonth" size="1">
						<html:options collection="allMonths" property="value" labelProperty="label"/>
					</html:select>

					<html:select  property="endToDay" size="1">
						<html:options collection="allDays" property="value" labelProperty="label"/>
					</html:select>

					<html:select  property="endToYear" size="1">
						<html:options collection="allYears" property="value" labelProperty="label"/>
					</html:select>

                </td>
                <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
              </tr>
              <tr>
                <td valign="top" colspan=5 height=15><html:img height="15"  pageKey="images.spacer" border="0"/></td>
              </tr>
              <tr>
                <td valign="top" colspan=1>
                <html:img   pageKey="images.spacer" border="0"/></td>
                <td valign="top" colspan=3 align="left" >
                  <table>
                     <tr>
			  <td width="150">&nbsp;</td>
			  <td>
				  <html:image altKey="alt.save" pageKey="images.action.search" border="0"/>
				  <html:image altKey="alt.reset" pageKey="images.action.reset" border="0" onclick="javascript:reset(); return false;" />
			  </td>
                     </tr>
                  </table>
                </td>
                <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
              </tr>
              </html:form>
            </table>
            <!-- End of table just for the form -->
          </td>
          <td vAlign="top" width="2" bgColor="#bcbd78"><html:img  pageKey="images.spacer" width="2" border="0"/></td>
        </tr>
        <tr><td valign="top" height="2" bgcolor='<bean:write name="Border"/>' colspan="3"><html:img height="2" border="0" pageKey="images.spacer"/></td></tr>
      </table>
      <!-- End of table to put the form -->

    	<!-- End of table to put the form -->
		</td>
	</tr>
</table>
</center>
<!-- End of body portion -->

</body>
</html:html>
