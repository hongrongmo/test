<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>

<!-- Body Part -->
<span class="SmBlackText">
    <table border="0" width="100%" cellspacing="0" cellpadding="0" >
        <tr>
            <td valign="top" align="left" width="25%" bgcolor="#CCCCCC">
				<html:form action="/customers.do" method="POST" focus="value(searchName)">

					<br/>
					&nbsp;Customer Name:
					<br/>
					&nbsp;<html:text property="value(searchName)" size="14"/>

					<br/>
					&nbsp;Sales region:
					<br/>

	                <bean:define toScope="request" id="salesRegions" name="customersForm" property="salesRegions"  type="java.util.Collection"/>
	                &nbsp;<html:select name="customersForm" property="value(salesregion)" size="1" >
	                    <html:options collection="salesRegions" property="value" labelProperty="label"/>
	                </html:select>

					<br/>
					&nbsp;Contract products:
					<br/>
					<bean:define toScope="request" id="allProducts" name="customersForm" property="allProducts"  type="java.util.Collection"/>
					&nbsp;<html:select name="customersForm" property="products" multiple="true">
						<html:options collection="allProducts" property="value" labelProperty="label"/>
					</html:select>

<br/>
&nbsp;Access type:
<br/>
<bean:define toScope="request" id="allStatuses" name="customersForm" property="statuses"  type="java.util.Collection"/>
&nbsp;<html:select name="customersForm" property="value(contractstatus)" size="1">
	<html:options collection="allStatuses" property="value" labelProperty="label"/>
</html:select>

<br/>
&nbsp;Access status:
<br/>
<bean:define toScope="request" id="allAccess" name="customersForm" property="allAccess"  type="java.util.Collection"/>
&nbsp;<html:select name="customersForm" property="value(access)" size="1">
	<html:options collection="allAccess" property="value" labelProperty="label"/>
</html:select>
<br/>
&nbsp;Consortiums only:&nbsp;
	<html:checkbox name="customersForm" value="yes" property="value(consortiums)" />


					<p align="center"/>
					<html:submit value="Find Customers"/></html:form>
					</p>

					<html:form action="/customers.do">
					<p align="center"/>
					<html:submit value="Clear Search"/>
					</p>
					</html:form>
            </td>
            <td valign="top">

                &nbsp;<span Class="RedText"><html:errors/></span>

                <table border="0" width="100%" cellspacing="1" cellpadding="1">

					<logic:present name="customersForm">
                        <bean:size id="resultsize" name="customersForm" property="customers" />
                        <logic:notEqual name="resultsize" value="0">
	                    <tr>
							<td colspan="3">&nbsp;
                                <A CLASS="MedBlackText"><bean:write name="resultsize"/>&nbsp; Search Results</A>
							</td>
	                    </tr>
                        </logic:notEqual>
					</logic:present>

                    <logic:iterate id="aCustomer" name="customersForm" property="customers" type="java.util.Map">
                        <tr>
                            <td>&nbsp;</td>
                            <td><html:link forward="switch" paramId="customerId" paramName="aCustomer" paramProperty="custid" styleClass="LgBlueLink"><bean:write name="aCustomer" property="custid"/></html:link></td>
                            <td><html:link forward="switch" paramId="customerId" paramName="aCustomer" paramProperty="custid" styleClass="LgBlueLink"><bean:write name="aCustomer" property="name"/></html:link>

                            <logic:equal name="aCustomer" property="consortium" value="true">
                                &nbsp;&nbsp;
                                <html:link forward="switchparent" paramId="customerId" paramName="aCustomer" paramProperty="custid" styleClass="LgBlueLink"><bean:write name="aCustomer" property="name"/> (Parent Only)</html:link>
                            </logic:equal>

                        </tr>
                    </logic:iterate>
					<tr>
						<td colspan="3">&nbsp;

							<A CLASS="MedBlackText">Use this form to find and view usage for any EI customer.
							<P>
							<ul>
							<b>Customer Name</b> Enter all or part of the customers name, this field is not case sensitive and will find all customer names that contain this string.  For example <b>Georgia</b> matches both <b>Georgia</b> Institute of Technology and Univ of <b>Georgia</b> - Athens GA.  Leave this field blank to return all customer names that match the following criteria.<br/><br/>
							<b>Sales Region</b> Select the sales region the customer is assigned to, leave this selection blank (top option) for customers in any sales region.<br/><br/>
							<b>Contract Products</b> Select one or more products to include in the search, hold down Ctrl. to make multiple selections.  Note that even though customer searching has a product option reporting is performed by database and all databases will be shown for every customer.<br/><br/>
							<b>Access Type</b> Select the type of customer to search for, leave this selection blank (top option) for all customer types.<br/><br/>
							<b>Access Status</b> Limit the search to customer that are enabled (Yes) or turned off (No), leave this option blank for both statuses.<br/><br/>
							<b>Consortiums Only</b> Check this option to limit the search to consortiums.<br/><br/>
							<b>Find Customers</b> Execute your search<br/><br/>
							<b>Clear Search</b> Clear the form and previous search.
							</ul>
							</A>

						</td>
					</tr>
                </table>
            </td>
        </tr>
    </table>
</span>
<!-- End of Midle table for information -->
