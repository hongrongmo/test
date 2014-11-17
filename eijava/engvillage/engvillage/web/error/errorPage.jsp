
<%@ page isErrorPage="true" %>
<%@ page session="false" %>
<%
	ServletContext context = config.getServletContext();
	context.log("MODEL ERROR:", exception);

	response.setHeader("EXCEPTION", exception.getMessage());
%>
