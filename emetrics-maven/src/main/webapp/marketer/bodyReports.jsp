<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/META-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/META-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/META-INF/display.tld" prefix="display" %>
<%@ page import="org.ei.struts.emetrics.Constants"%>

<!-- Start of table for the tables and graphics, bar charts -->
<table border="0" cellspacing="0" cellpadding="3">
    <tr><td valign="top" height="20"><html:img pageKey="images.spacer" border="0" height="20"/></td></tr>
    <tr>
        <td valign="top">

            <logic:equal name="reportForm" property="action" scope="request" value="<%= Constants.ACTION_DISPLAY_REPORT %>">
                <a class="pageheading">
                    <bean:write name="reportForm" property="report.title"/>
                </a>
                <logic:notPresent scope="request" name="printerfriendly" >

                    <tiles:insert page="/layout/reportFormLayout.jsp" >
                        <tiles:put name="htmlform" beanName="reportForm" beanProperty="htmlformpage" type="page"/>
                    </tiles:insert>

                </logic:notPresent>

            </logic:equal>

        </td>
    </tr>
    <tr>
        <td valign="top" height="2" bgcolor="#396DA5"><html:img pageKey="images.spacer" border="0" height="2"/></td>
    </tr>
    <tr>
        <td valign="top" height="15"><html:img pageKey="images.spacer" border="0" height="15"/></td>
    </tr>
    <tr>
        <td valign="top">

            <logic:notEqual name="reportForm" property="action" scope="request" value="<%= Constants.ACTION_DISPLAY_REPORT %>">
							<ul>
								<li>This page allows you to view reports for total combined usage for all of the Engineering Village Platform(s). To view any of these reports, click on the report title on the left.
								<li>The reports provide an overview of key usage statistics for a selected month, month range or one day.
								<li>Hold your mouse over the report link for a brief description of the reports output.
								<li>All report data is updated daily.
							</ul>
            </logic:notEqual>

            <logic:equal name="reportForm" property="action" scope="request" value="<%= Constants.ACTION_DISPLAY_REPORT %>">
              <div id="reportdata">
                <display:table export="true" border="1" name="<%=Constants.REPORT_DATA_KEY%>" requestURI="/emetrics/reports.do">
                  <logic:iterate name="<%=Constants.DISPLAYTABLE_KEY %>" id="aColumnMap" type="org.ei.struts.emetrics.businessobjects.reports.Result">
                    <display:column sort="false" property='<%= (String) aColumnMap.getColumn() %>' title='<%= (String) aColumnMap.getName() %>' decorator='<%= (String) aColumnMap.getType() %>'/>
                  </logic:iterate>
                </display:table>
                Last updated: <bean:write name="reportForm" property="report.lastTouched" formatKey="format.lasttouched"/>
	             </div>
            </logic:equal>

        </td>
    </tr>
</table>
