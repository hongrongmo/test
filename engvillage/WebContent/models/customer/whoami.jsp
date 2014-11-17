<%@ page language="java" %>
<%@ page contentType="text/html" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%
    try
    {
      DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
      ControllerClient client = new ControllerClient(request, response);
      UserSession ussession = (UserSession) client.getUserSession();

      String sessionId = ussession.getSessionid();
      String customerId = ussession.getCustomerid();
      String contractId = ussession.getContractid();
      String username  = ussession.getUsername();
      String[] credentials = ussession.getCartridge();

      out.write("<pre>");
      out.write("<br/>");
      out.write("You are logged in as custid: " + customerId + " (" + contractId + ")");
      out.write("<br/>");
      if(username != null) {
        out.write("username: " + username);
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
