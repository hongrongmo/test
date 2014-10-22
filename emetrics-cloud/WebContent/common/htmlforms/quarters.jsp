<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.ei.struts.emetrics.Constants"%>

<html:select property="quarter" size="1">
    <bean:define id="allQtrs" name="reportForm" property="allQuarters" type="java.util.Collection"/>
    <html:options collection="allQtrs" property="value" labelProperty="label"/>
</html:select>
<br/>
<%-- if there are no sortable columns (collection size == 0)
	do not display sort related form elements --%>
<bean:size id="sortablesize" name="reportForm" property="sortableColumns"/>
<logic:notEqual name="sortablesize" value="0">
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
</logic:notEqual>

<html:submit/>