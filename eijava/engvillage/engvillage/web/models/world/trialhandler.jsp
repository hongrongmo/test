<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="org.ei.email.*"%>
<%@ page import="java.io.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%
    String firstName = request.getParameter("firstname");
    String lastName = request.getParameter("lastname");
    String company = request.getParameter("company");
    String address1 = request.getParameter("address1");
    String address2 = request.getParameter("address2");
    String city = request.getParameter("city");
    String state = request.getParameter("state");
    String country = request.getParameter("country");
    String zip = request.getParameter("zip");
    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    String howHear = request.getParameter("hearus");
    String explain = request.getParameter("explanation");

    EMail mail = EMail.getInstance();
    EIMessage msg = new EIMessage();

    msg.setSubject("Engineering Village 2 Free 30-day Trial");

    ByteArrayOutputStream b = new ByteArrayOutputStream();
    PrintStream e1Body = new PrintStream(b);
    e1Body.println("Dear "+ firstName + " " + lastName +",");
    e1Body.println();
    e1Body.println("Thank you for your interest in Engineering Village 2. You will be contacted by an Elsevier Engineering Information representative shortly to establish a trial of Engineering Village 2. ");
    e1Body.println();
    e1Body.println("Ei Customer Support");
    msg.setMessageBody(b.toString());
    msg.setSender("eicustomersupport@elsevier.com");
    msg.addTORecepient(email);
    mail.sendMessage(msg);

    msg = new EIMessage();
    msg.setSubject("New Engineering Village 2 Trial Request");
    b = new ByteArrayOutputStream();
    e1Body = new PrintStream(b);
    e1Body.println("First Name: "+ firstName);
    e1Body.println("Last Name: "+ lastName);
    e1Body.println("Company: "+ company);
    e1Body.println("Address1: "+ address1);
    e1Body.println("Address2: "+ address2);
    e1Body.println("City: "+ city);
    e1Body.println("State: "+ state);
    e1Body.println("Country: "+ country);
    e1Body.println("Zip: "+ zip);
    e1Body.println("Phone: "+ phone);
    e1Body.println("Email: "+ email);
    e1Body.println("How Hear: "+ howHear);
    e1Body.println("How Hear Explained: "+ explain);

    msg.setSender("eicustomersupport@elsevier.com");
    msg.addCCRecepient("eicustomersupport@elsevier.com");
	msg.addTORecepient("l.fiszer@elsevier.com");
    msg.setMessageBody(b.toString());
    mail.sendMessage(msg);
%>
<ROOT/>