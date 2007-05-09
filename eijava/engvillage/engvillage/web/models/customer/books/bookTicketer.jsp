<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->

<%@ page import=" java.util.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="java.net.*"%>
<%@ page import="org.ei.data.books.BookPart"%>
<%@ page import="org.apache.oro.text.perl.Perl5Util"%>
<%@ page import="org.apache.oro.text.regex.MatchResult"%>
<%@ page import="org.ei.books.*"%>
<%@ page import="java.io.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>
<%


    String secretName = "Elsevier";
    String secret = "35738437";
    String id = "Authorised";
    String baseUrl = "http://referexengineering.elsevier.com";
    long expireIn = 86400000L;

    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();
    User user = ussession.getUser();

    String link = request.getParameter("link");
    String isbn = request.getParameter("isbn");
    BookPartBuilder builder = new BookPartBuilder();
    BookPart part = builder.buildBookPart(isbn);
    part.setCredentials(user.getCartridge());
    Perl5Util perl = new Perl5Util();


    String dir = isbn.substring(0,8);
    StringBuffer buf = new StringBuffer();
    buf.append(baseUrl);

    buf.append("/");
    if(link != null)
    {
    	String parsedLink = perl.substitute("s/\\s+//g", link);
        buf.append(parsedLink);
        log(client,"read_section",parsedLink);
    }
    else
    {
        buf.append("e");
        buf.append(isbn);
        buf.append(".pdf");
        log(client,"read_book",new StringBuffer().append("e").append(isbn).append(".pdf").toString());
    }




    if(part.showLink())
    {


        String ticketedUrl = AdmitOneTicketer.getTicketedUrl(buf.toString(),
                                                             expireIn,
                                                             secretName,
                                                             secret,
                                                             id);
  //      System.out.println("Ticketed URL:"+ ticketedUrl);

        client.setRedirectURL(ticketedUrl);
        client.setRemoteControl();
    }
    else
    {
        throw new Exception("<DISPLAY>You do not have permission to download this book</DISPLAY>");
    }


%>
<%! void log(ControllerClient client,String action,String link) {

    client.log("context", "referex");
    client.log("type", "ebook");
    client.log("log_action",action);



}
%>