<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%
//	Report report = (Report) session.getAttribute(Constants.REPORTS_KEY);
//	request.setAttribute("title", report.getTitle());

	
	//request.setAttribute("rid", new Integer(report.getReportId()));
	//request.setAttribute("REPID", new Integer(report.getReportId()));
	
//	UserView user = ((UserView)( (UserContainer) session.getAttribute(Constants.USER_CONTAINER_KEY) ).getUserView());
//	request.setAttribute("user", user);
//	request.setAttribute("customers", user.getAll());
//	
//	int custid = user.getCustomerId();
//	int cid = 0;
//	CustomerBean customerview = (CustomerBean) session.getAttribute(Constants.CUSTOMER_VIEW);
//	cid = customerview.getCustId();
//	
//	request.setAttribute("cid", new Integer(cid));
//	request.setAttribute("custid", new Integer(custid));
//	request.setAttribute("body", customerview.getView());

	
%>

<tiles:insert definition="reportLayout" flush="false" />
