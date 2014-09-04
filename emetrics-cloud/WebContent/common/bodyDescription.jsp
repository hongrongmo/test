<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/display.tld" prefix="display" %>
<%@ page import="org.ei.struts.emetrics.Constants"%>

<!-- Start of table for the tables and graphics, bar charts -->


			<table border="0" width="100%" cellspacing="0" cellpadding="0">
			<tr>
				<td width="190" bgcolor="#999999">&nbsp;<a href="/emetrics/reports.do" class="LgWhiteText"><bean:message key="label.searchoverview"/></a></td>
				<td width="80%"><html:img pageKey="images.spacer" border="0" width="2" height="2"/></td>
			</tr>


				<logic:iterate indexId="rownum" name="reportForm" property="allReportLinks" id="aReport" type="java.util.Map">

					<% if(rownum != 0) { %>
					<tr>
						<td width="190" bgcolor="#CCCCCC" >
	  					<hr align="center" noshade width="80%">
						</td>
						<td width="80%" >
							&nbsp;
						</td>
					</tr>
					<% } %>

					<tr>
						<td width="190" bgcolor="#CCCCCC">
						  &nbsp;
  						<html:link forward="reports" name="aReport" property="linkparams" styleClass="LgBlueLink">
  							<bean:write name="aReport" property="title"/>
  						</html:link>
						</td>
						<td width="80%">
							&nbsp;<bean:write name="aReport" property="description"/>
						</td>
					</tr>
				</logic:iterate>

			<tr>
				<td width="190" bgcolor="#CCCCCC">&nbsp;</td>
				<td width="80%"><html:img pageKey="images.spacer" border="0" width="2" height="2"/></td>
			</tr>

			</table>

