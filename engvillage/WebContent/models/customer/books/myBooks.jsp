<%@ page language="java" %>
<%@ page session="false" %>

<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.books.*"%>
<%@ page import="org.ei.books.collections.*"%>
<%@ page import="org.ei.books.library.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>
<PAGE>
<%
    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession = client.getUserSession();
    IEVWebUser user = ussession.getUser();
/*
creds=BPE&creds=CHE&creds=CHE1&creds=CHE2&creds=CHE3&creds=ELE&creds=ELE1&creds=ELE2&creds=ELE3&creds=MAT&creds=MAT1&creds=MAT2&creds=MAT3&creds=CIV&creds=CIV3&creds=COM&creds=COM3&creds=SEC&creds=SEC3
*/
    String[] creds = user.getCartridge();
    if(request.getParameterValues("creds") != null) {
      creds = request.getParameterValues("creds");
    }
    Arrays.sort(creds);
    boolean perpetual =  (Arrays.binarySearch(creds, "BPE") >= 0);

    Library library = Library.getInstance();
    CustomerBooksVisitor libraryVisitor = new CustomerBooksVisitor(creds,perpetual);
    library.accept(libraryVisitor);
    libraryVisitor.toXML(out);
%>
</PAGE>
<%! void log(ControllerClient client,String action,String link) {

    client.log("context", "referex");
    client.log("type", "ebook");
    client.log("log_action",action);
}
%>