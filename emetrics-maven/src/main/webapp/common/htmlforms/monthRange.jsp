<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/META-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="allMonths" name="reportForm" property="allMonths" type="java.util.Collection"/>

<html:select property="endmonth" size="1" onchange="setMax();">
	<html:options collection="allMonths" property="value" labelProperty="label"/>
</html:select>
&nbsp;<span class="MedBlackText">to</span>&nbsp;
<html:select property="month" size="1" onchange="setMin();">
	<html:options collection="allMonths" property="value" labelProperty="label"/>
</html:select>

<html:submit/>