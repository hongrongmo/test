<!--
//////////////////////////////////////////////////////////////////////////////
//
// File name : exception.jsp
// Author: Rajuk
//
///////////////////////////////////////////////////////////////////////////////
// All rights reserved.
//
///////////////////////////////////////////////////////////////////////////////
// Revision  Information for this file.
// $Header: $
// $Project:$
// $Revision: $
// $Log: $
//
/////////////////////////////////////////////////////////////////////////////////
-->
<%@ page language="java" %>
<%@ page session="false" %>

<%@ page import="java.util.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%


	out.print("<root>");
	out.print("<MESSAGE>");
	if(request.getParameter("exception") != null &&
	  (request.getParameter("exception")).indexOf("<DISPLAY>") == 0)
	{
		out.print(request.getParameter("exception"));
	}
	else
	{
		out.print("<![CDATA[");
		out.print(request.getParameter("exception"));
		out.print("]]>");
	}

	out.print("</MESSAGE>");
	out.print("<FOOTER/>");
	out.print("</root>");
	
%>