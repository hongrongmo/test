<%@ page language="java" %>
<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.session.*"%>
<%
    try
    {
      DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
      ControllerClient client = new ControllerClient(request, response);
      UserSession ussession = (UserSession) client.getUserSession();
      User user = ussession.getUser();
      
      String sessionId = ussession.getID();
      String customerId = user.getCustomerID();
      String contractId = user.getContractID();
      String username  = user.getUsername();
      String[] credentials = user.getCartridge();

      out.write("<pre>");
      out.write("<br/>");
      out.write("You are logged in as custid: " + customerId + " (" + contractId + ")");
      out.write("<br/>");
      if(username != null) {
        out.write("username: " + user.getUsername());
      }
      out.write("<br/>");
      out.write("You credentials are: " + Arrays.asList(credentials));
      out.write("<br/>");
      out.write("Your default mask is: " + databaseConfig.getMask(credentials));
      out.write("<br/>");
      out.write("Your session id is: " + sessionId);
      out.write("<br/>");
      out.write("Remote address: " + request.getRemoteAddr());
      out.write("<br/>");
      out.write("Remote Forwarded address: " + request.getHeader("x-forwarded-for"));
      out.write("<br/>");
      out.write("</pre>");

		} catch (Exception e) {
			
		}

%>
