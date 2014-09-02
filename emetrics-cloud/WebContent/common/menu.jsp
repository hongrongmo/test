<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/display.tld" prefix="display" %>

<%@ page import="org.ei.struts.emetrics.Constants"%>

<!-- Start of table for leftside links -->
<table width="190" border="0" cellspacing="0" cellpadding="0">
	<tr bgcolor="#999999">
		<td>&nbsp;<html:link styleClass="LgWhiteText" forward="reports">Search Overview</html:link></td>
	</tr>

	<logic:iterate indexId="rownum" name="reportForm" property="allReportLinks" id="aReport" type="java.util.Map">

		<% if(rownum != 0) { %>
		<tr>
			<td bgcolor="#CCCCCC">
				<hr align="center" noshade width="80%">
			</td>
		</tr>
		<% } %>

		<tr>
			<td bgcolor="#CCCCCC" >

      <bean:define toScope="page" id="repdesc" name="aReport" property="description"/>
        <%
          repdesc = "this.T_WIDTH=450;return escape('" + repdesc + "');";
        %>
				&nbsp;
				<html:link onmouseover="<%= (String) repdesc %>" forward="reports" name="aReport" property="linkparams" styleClass="LgBlueLink">
					<bean:write name="aReport" property="title"/>
				</html:link>
			</td>
		</tr>

	</logic:iterate>

			<tr>
				<td bgcolor="#CCCCCC">
					&nbsp;
				</td>
			</tr>
  </table>
<!-- End of table for left side links -->
