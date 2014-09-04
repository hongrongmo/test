<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="comp" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.ei.struts.emetrics.businessobjects.reports.*"%>
<%@ page import="org.ei.struts.emetrics.Constants"%>
<%@ page import="org.ei.struts.framework.util.IConstants"%>
<%@ page import="org.ei.struts.framework.UserContainer"%>
<%@ page import="org.ei.struts.emetrics.customer.view.UserView"%>
<%@ page import="org.ei.struts.emetrics.actions.CustomerBean"%>
<%@ page import="java.util.*"%>

<bean:parameter id="param1" name="param1" value="0"/>

<%

	
	UserView user = ((UserView)( (UserContainer)session.getAttribute(IConstants.USER_CONTAINER_KEY) ).getUserView());
	request.setAttribute("user", user);
	request.setAttribute("customers", user.getAll());
	
	int custid = user.getCustomerId();

	request.setAttribute("custid", new Integer(custid));


	
%>


<comp:insert definition="customersLayout" flush="false" />
