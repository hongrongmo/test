<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/META-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>


<html:form method="GET" action="/reports.do">
	
	<tiles:insert attribute="htmlform" />
	
	<html:hidden name="reportForm" property="repid"/>

</html:form>