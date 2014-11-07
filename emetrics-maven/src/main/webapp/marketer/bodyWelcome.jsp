<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>

<!-- end of toplogo and navigation bar -->

<!-- Start of the lower area below the navigation bar -->
<table border="0" width="100%" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
    <tr>
        <td valign="top" colspan="2" height="25"><html:img pageKey="images.spacer" border="0" height="25"/></td>
    </tr>
    <tr>
        <td valign="top" width="490" bgcolor="#FFFFFF">

            <!-- start of right side table for content -->
            <table border="0" width="490" cellspacing="0" cellpadding="0">
                <tr><td valign="top" height="15" colspan="2"><html:img pageKey="images.spacer" border="0" height="15"/></td></tr>
                <tr><td valign="top" width="4"><html:img pageKey="images.spacer" border="0" width="4"/></td><td valign="top">
                    <P CLASS="MedBlackText">To view and print the reports, please select the <html:link forward="reports">Reports</html:link> tab above.</P>
                    <P CLASS="MedBlackText">The <bean:message key="global.title"/> provides a comprehensive view of customer activity against all of Ei's databases.  Listed under the reports tab are views detailing total page impressions, document views and searches broken down by database.  These reports are available as both daily and monthly totals.  In addition four new summary reports allow you to easily view and compare site usage between, customers, customer types and sales regions.</P>
                    <P CLASS="MedBlackText">Under the search tab you can quickly locate customers based on their type, sales region, name and the products on their contract.  Once you have located a customer simply click on the customer name or ID to see detailed usage reporting for this customer.</P>
                    <!-- body message area -->
                    </td>
                </tr>
            </table>
            <!-- end of right side table for content -->

        </td>
        <td valign="top" width="175">
        <br>
