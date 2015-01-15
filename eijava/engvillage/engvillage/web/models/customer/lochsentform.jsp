<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp" %>
<%@ page buffer="20kb"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.email.*"%>
<%@ page import="org.ei.util.*"%>

<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.mail.internet.*"%>

<%

		String title = request.getParameter("articletitle");
		String author = request.getParameter("author");
		String stitle = request.getParameter("serialtitle");
		String conftitle = request.getParameter("conftitle");
		String source = request.getParameter("source");
		String issn = request.getParameter("issn");
		String isbn = request.getParameter("isbn");
		String vol = request.getParameter("volume");
		String issue = request.getParameter("issue");
		String StartPage = request.getParameter("startpage");
		String year = request.getParameter("year");
		String comments = request.getParameter("comments");
		String address = request.getParameter("email");

		StringBuffer message = new StringBuffer();

		if ((title != null)&& (!title.trim().equals("")))
		{
			message.append("ARTICLE TITLE: ");
			message.append(title);
			message.append("\r\n");

		}
		if ((author != null)&& (!author.trim().equals("")))
		{
			message.append("AUTHOR: ").append(author).append("\r\n");
		}

		if ((stitle != null)&& (!stitle.trim().equals("")))
		{
			message.append("SOURCE TITLE: ").append(stitle).append("\r\n");
		}

		if ((source != null)&& (!source.trim().equals("")))
		{
			message.append("SOURCE: ").append(source).append("\r\n");
		}
		if ((conftitle != null)&& (!conftitle.trim().equals("")))
		{
			message.append("CONFERENCE NAME: ").append(conftitle).append("\r\n");
		}
		if ((issn != null ) && (!issn.trim().equals("")))
		{
			message.append("ISSN:  ").append(issn).append("\r\n");
		}

		if ((isbn != null ) && (!isbn.trim().equals("")))
		{
			message.append("ISBN:  ").append(isbn).append("\r\n");
		}

		if ((vol != null ) && (!vol.trim().equals("")))
		{
			message.append("VOLUME:  ").append(vol).append("\r\n");
		}

		if ((issue != null) && (!issue.trim().equals("")))
		{
			message.append("ISSUE:  ").append(issue).append("\r\n");
		}

		if ((StartPage != null ) && (!StartPage.trim().equals("")))
		{
			message.append("START PAGE:  ").append(StartPage).append("\r\n");
		}

		if ((year != null) && (!year.trim().equals("")))
		{
			message.append("YEAR:   ").append(year).append("\r\n");
		}

		if ((comments != null) && (!comments.trim().equals("")))
		{
			message.append("COMMENTS:   ").append(comments).append("\r\n");
		}

    // -------------------------- Create and Send Email ----------------------


    String to = address;
    String from = "";
    String subject = "EV2 Full text request";
    String strmessage = message.toString();
    from = request.getParameter("emailaddress");

    long lo = System.currentTimeMillis();
    java.util.Date d = new java.util.Date(lo);

    EMail email = EMail.getInstance();
    EIMessage eimessage = new EIMessage();
    //setSender no longer sets from address - use setFrom for From and setSender for Sender
    //eimessage.setSender(from);
    eimessage.setSender(EIMessage.DEFAULT_SENDER);
    eimessage.setFrom(from);

    List l = new ArrayList();
    l.add(to);
    eimessage.addTORecepients(l);
    eimessage.setSubject(subject);
    eimessage.setSentDate(d);
    eimessage.setMessageBody(strmessage);
    email.sendMessage(eimessage);
    out.write("<LOCH>");
    out.write("<EMAIL>"+address+"</EMAIL>");
    out.write("</LOCH>");

%>