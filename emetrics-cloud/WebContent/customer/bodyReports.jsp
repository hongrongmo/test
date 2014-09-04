<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/display.tld" prefix="display" %>

<%@ page import="java.util.List"%>
<%@ page import="org.ei.struts.emetrics.Constants"%>
<%@ page import="org.ei.struts.emetrics.customer.view.UserContainer"%>
<%@ page import="org.ei.struts.emetrics.customer.view.UserView"%>

<logic:notPresent scope="request" name="printerfriendly" >
<%
    if(session.getAttribute(Constants.SWITCH_VIEW_KEY) != null)
    {
        List x = ((List) session.getAttribute(Constants.SWITCH_VIEW_KEY));
%>
        <p align="center">
        <html:link styleClass="LgBlueLink" forward="switchback"><bean:message key="label.link.return"/></html:link>
        </p>
<%
    }
%>
</logic:notPresent>

        <!-- Start of table for the tables and graphics, bar charts -->
        <table border="0" cellspacing="0" cellpadding="3">
            <tr>
                <td valign="top" height="20"><html:img pageKey="images.spacer" border="0" height="20"/></td></tr>
            <tr>
                <td valign="top">

                    <logic:equal name="reportForm" property="action" scope="request" value="<%= Constants.ACTION_DISPLAY_REPORT %>">
                        <a class="pageheading">
                            <%= ((UserContainer) session.getAttribute(Constants.USER_CONTAINER_KEY)).getUserView().getCustomerId() %>
                            <%= ((UserContainer) session.getAttribute(Constants.USER_CONTAINER_KEY)).getUserView().getName() %>

                            <%
                                boolean parentlonly = ((UserContainer) session.getAttribute(Constants.USER_CONTAINER_KEY)).getUserView().getParentonly();
                                request.setAttribute("parentlonly", String.valueOf(parentlonly));
                            %>
                            <logic:equal name="parentlonly" value="true">
                                &nbsp;&nbsp;<B>(Parent Data Only)</B>
                            </logic:equal>
                        </a>

                        <br/>
                        <a class="pageheading">
                            <bean:write name="reportForm" property="report.title"/>
                        </a>

                        <logic:notPresent scope="request" name="printerfriendly" >

                            <logic:notPresent scope="request" name="printerfriendly" >

                                <tiles:insert page="/layout/reportFormLayout.jsp" >
                                    <tiles:put name="htmlform" beanName="reportForm" beanProperty="htmlformpage" type="page"/>
                                </tiles:insert>

                            </logic:notPresent>

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
											<li>This page allows you to view usage reports for <b><%= ((UserContainer) session.getAttribute(Constants.USER_CONTAINER_KEY)).getUserView().getName() %></b>. To view any of these reports, click on the report title on the left.
											<li>The reports provide an overview of key usage statistics for a selected month, month range or one day.
											<li>Hold your mouse over the report link for a brief description of the reports output.
											<li>All report data is updated daily.  Your contact(s) on file with Engineering Information will receive an e-mail alert once a month in accordance with COUNTER report delivery standards. See  <u>COUNTER Code of Practice, Release 3</u>, <i>Section 4.3</i>.
											<li>To make changes or if you have any other inquiries, please contact our <html:link forward="support">customer support</html:link>.
										</ul>
										<sup>1</sup><A title="External link" href="http://www.projectcounter.org/r3/Release3D9.pdf" target="_new">http://www.projectcounter.org/r3/Release3D9.pdf</A>
									</logic:notEqual>

                  <logic:equal name="reportForm" property="action" scope="request" value="<%= Constants.ACTION_DISPLAY_REPORT %>">
                    <div id="reportdata">
  	                  <display:table export="true" border="1" name="<%=Constants.REPORT_DATA_KEY%>" requestURI="/emetrics/reports.do">
  	                    <logic:iterate name="<%=Constants.DISPLAYTABLE_KEY %>" id="aColumnMap" type="org.ei.struts.emetrics.businessobjects.reports.Result">
  	                      <display:column sort="false" property='<%= (String) aColumnMap.getColumn() %>' title='<%= (String) aColumnMap.getName() %>' decorator='<%= (String) aColumnMap.getType() %>'/>
  	                    </logic:iterate>
  	                  </display:table>
                    </div>
                    Last updated: <bean:write name="reportForm" property="report.lastTouched" formatKey="format.lasttouched"/>
                  </logic:equal>

                </td>
            </tr>
        </table>
