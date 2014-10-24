<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->

<%@ page  import=" java.util.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="java.net.*"%>
<%@ page import="org.apache.oro.text.regex.MatchResult"%>
<%@ page import="org.ei.books.*"%>
<%@ page import="java.io.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>
<%

    ControllerClient client = new ControllerClient(request, response);
    String bookSiteUrl =  "http://books.elsevier.com/bookscat/links/details.asp?isbn=";
    StringBuffer fullUrl = new StringBuffer();

    String isbn = request.getParameter("isbn");

   	fullUrl.append(bookSiteUrl).append(isbn);

    client.log("referex","buyTheBook");
    client.log("isbn",isbn);

    client.setRedirectURL(fullUrl.toString());
    client.setRemoteControl();


%>
