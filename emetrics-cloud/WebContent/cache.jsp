<%@ page  session="false"%>

<%@ page  import="javax.naming.*"%>
<%@ page  import="javax.sql.*"%>
<%@ page  import="java.util.Collection"%>
<%@ page  import="java.sql.*"%>
<%@ page  import="java.text.NumberFormat"%>

<%@ page  import="org.apache.commons.dbcp.*"%>

<%@ page  import="org.ei.struts.emetrics.Constants"%>
<%@ page  import="org.ei.struts.emetrics.service.EmetricsServiceImpl"%>
<%@ page  import="org.ei.struts.framework.service.IFrameworkServiceFactory"%>
<%@ page  import="org.ei.struts.framework.service.IFrameworkService"%>

<%

    IFrameworkServiceFactory serviceFactory = (IFrameworkServiceFactory) getServletContext().getAttribute(Constants.SERVICE_FACTORY_KEY);

	EmetricsServiceImpl serviceImpl = (EmetricsServiceImpl) serviceFactory.createService();

	String strReportKey = "REPORTS".concat("marketer");
	if(getServletContext().getAttribute(strReportKey ) != null)
	{
		out.write("<pre>LOADING " + strReportKey  + " INTO APPLICATION CONTEXT</pre>");
		Collection allreports = serviceImpl.getReports("marketer");
		getServletContext().setAttribute( strReportKey, allreports);
		out.write("<pre>Done.</pre>");
    }


	strReportKey = "REPORTS".concat("customer");
	if(getServletContext().getAttribute(strReportKey ) != null)
	{
		out.write("<pre>LOADING " + strReportKey  + " INTO APPLICATION CONTEXT</pre>");
		Collection allreports = serviceImpl.getReports("customer");
		getServletContext().setAttribute( strReportKey, allreports);
		out.write("<pre>Done.</pre>");
    }


%>
