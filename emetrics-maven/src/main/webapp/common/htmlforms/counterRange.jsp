<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/META-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>

<html:select property="oneyear" size="1">
    <bean:define id="twoYears" name="reportForm" property="twoYears" type="java.util.Collection"/>
    <html:options collection="twoYears" property="value" labelProperty="label"/>
</html:select>

<html:submit/>