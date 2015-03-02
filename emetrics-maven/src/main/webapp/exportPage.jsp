<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/META-INF/display.tld" prefix="display" %>
<%@ page import="org.ei.struts.emetrics.Constants"%>
<%@ page import="org.apache.taglibs.display.TableTag"%>

<%
	String exportFile = "exportfile";
	String exportType = request.getParameter("exportType");
	try {	
		switch(Integer.parseInt(exportType)) {
			case TableTag.EXPORT_TYPE_CSV:
				exportFile = exportFile.concat(".csv"); break;
			case TableTag.EXPORT_TYPE_EXCEL:
				exportFile = exportFile.concat(".xls"); break;
			case TableTag.EXPORT_TYPE_XML:
				exportFile = exportFile.concat(".xml"); break;			default: 			
				exportFile = exportFile.concat(".txt"); break;
		}
	}
	catch (NumberFormatException nfe) {
	}
	response.setHeader("Content-Disposition","inline; filename=" + exportFile);
%>
<display:table width="75%" export="true" border="1" name="<%=Constants.REPORT_DATA_KEY%>" requestURI="/emetrics/reports.do">
	<logic:iterate name="<%=Constants.DISPLAYTABLE_KEY %>" id="aColumnMap" type="org.ei.struts.emetrics.businessobjects.reports.Result">
		<display:column property='<%= (String) aColumnMap.getColumn() %>' title='<%= (String) aColumnMap.getName() %>' decorator='<%= (String) aColumnMap.getType() %>'/>
	</logic:iterate>	
</display:table>
