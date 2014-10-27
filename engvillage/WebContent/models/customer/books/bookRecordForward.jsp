<%@ page language="java" %><%@ page session="false" %><%@ page import="org.engvillage.biz.controller.ControllerClient"%><%@ page errorPage="/error/errorPage.jsp"%><%@ page buffer="20kb"%><%

    String isbn = null;
    isbn = request.getParameter("isbn");

    ControllerClient client = new ControllerClient(request, response);
    client.setRedirectURL("/controller/servlet/OpenURL?genre=book&isbn=" + isbn);
    client.setRemoteControl();

%>