<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.ei.struts.emetrics.Constants"%>

<!-- <bean:define id="allMembers" name="reportForm" property="allMembers" type="java.util.Collection"/> -->

&nbsp;<span class="MedBlackText">Member</span>&nbsp;
<html:select property="member" size="1" onchange="setMin();">
	<html:option value="<%= Constants.ALL %>"><%= Constants.ALL %></html:option>
	<html:options collection="allMembers" property="value" labelProperty="label"/>
</html:select>

<html:submit/>