<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.ei.struts.emetrics.Constants"%>

<bean:define id="allMonths" name="reportForm" property="allMonths" type="java.util.Collection"/>
<bean:define id="allDays" name="reportForm" property="days" type="java.util.Collection"/>

<html:hidden property="searchform" value="<%= Constants.CUSTOM %>"/>

<html:radio property="searchtype" value="<%= Constants.RANGE %>"/>

<html:select property="startday" size="1" onchange="setMaxD();">
	<html:options collection="allMonths" property="value" labelProperty="label"/>
</html:select>
&nbsp;<span class="MedBlackText">to</span>&nbsp;
<html:select property="endday" size="1" onchange="setMinD();">
	<html:options collection="allMonths" property="value" labelProperty="label"/>
</html:select>

<br/>

<html:radio property="searchtype" value="<%= Constants.SINGLEDAY %>"/>
<html:select property="singleday" size="1" >
	<html:options collection="allDays" property="value" labelProperty="label"/>
</html:select>

<br/>
<%-- if there are no sortable columns (collection size == 0)
	do not display sort related form elements --%>
<bean:size id="sortablesize" name="reportForm" property="sortableColumns"/>
<logic:notEqual name="sortablesize" value="0">
	<span class="MedBlackText">
	Order by:&nbsp;
	<logic:iterate id="item" name="reportForm" property="sortableColumns">
		<html:radio property="orderby" idName="item" value="value"/>&nbsp;
		<bean:write name="item" property="label"/>
	</logic:iterate>
	<br/>
		Direction:&nbsp;
		<html:radio property="direction" value="<%= Constants.ASCENDING %>"/>&nbsp;Ascending
		<html:radio property="direction" value="<%= Constants.DESCENDING %>"/>&nbsp;Descending
		<p/>
	<span/>
</logic:notEqual>

<html:submit/>