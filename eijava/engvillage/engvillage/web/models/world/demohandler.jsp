<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="org.ei.email.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="java.io.*"%>

<%@ page buffer="20kb"%>
<%
	
	// Get user's info from demo form
	
	String datetime 	= request.getParameter("datetime");
	String firstName 	= request.getParameter("firstname");
	String lastName 	= request.getParameter("lastname");
	String position 	= request.getParameter("position");
	String dept 		= request.getParameter("dept");
	String organization 	= request.getParameter("organization");
	String address 		= request.getParameter("address");
	String city 		= request.getParameter("city");
	String state 		= request.getParameter("state");
	String zip 		= request.getParameter("zip");
	String country 		= request.getParameter("country");
	String phone 		= request.getParameter("phone");
	String fax 		= request.getParameter("fax");
	String email 		= request.getParameter("email");
	
	
	// Send Email to Customer and to Customer Service.

	EIMessage msg = new EIMessage();
	msg.setSubject("Engineering Village 2 Web Demo");	
	ByteArrayOutputStream b = new ByteArrayOutputStream();
	PrintStream e1Body = new PrintStream(b);	
	e1Body.println("Dear "+ firstName + " " + lastName +",");
	e1Body.println();
	e1Body.println("Below is the result of your demo registration form. It was submitted to j.deangelis@elsevier.com.");
//	e1Body.println("Below is the result of your demo registration form. It was submitted to v.mettu@elsevier.com.");	
	e1Body.println();
	e1Body.println("Date Time: "+ datetime);
	e1Body.println("First Name: "+ firstName);
	e1Body.println("Last Name: "+ lastName);
	e1Body.println("Position: "+ position);
	e1Body.println("Department: "+ dept);
	e1Body.println("Organization: "+ organization);
	e1Body.println("Address: "+ address);
	e1Body.println("City: "+ city);
	e1Body.println("State: "+ state);
	e1Body.println("Zip: "+ zip);
	e1Body.println("Country: "+ country);	
	e1Body.println("phone: "+ phone);
	e1Body.println("Fax: "+ fax);
	e1Body.println("email: "+ email);
	e1Body.println();
	e1Body.println("Thank you for your interest in Engineering Village 2. At the scheduled time of the conference, please visit the demonstration website at http://us8.webex.com/eei/site and call 1-800-221-1044 ext 721 for the audio.");
	e1Body.println();
	e1Body.println("Ei Customer Support");	
	msg.setMessageBody(b.toString());
	
	msg.setSender("eicustomersupport@elsevier.com");


	msg.addTORecepient(email);
		
	EMail mail = EMail.getInstance();

 	mail.sendMessage(msg);

	EIMessage msg2 = new EIMessage();
	msg2.setSubject("Engineering Village 2 Demo User");

	ByteArrayOutputStream b2 = new ByteArrayOutputStream();
	PrintStream e2Body = new PrintStream(b2);
	e2Body.println("Date Time: "+ datetime);
	e2Body.println("First Name: "+ firstName);
	e2Body.println("Last Name: "+ lastName);
	e2Body.println("Position: "+ position);
	e2Body.println("Department: "+ dept);
	e2Body.println("Organization: "+ organization);
	e2Body.println("Address: "+ address);
	e2Body.println("City: "+ city);
	e2Body.println("State: "+ state);
	e2Body.println("Zip: "+ zip);
	e2Body.println("Country: "+ country);	
	e2Body.println("phone: "+ phone);
	e2Body.println("Fax: "+ fax);
	e2Body.println("email: "+ email);
	

	msg2.addTORecepient("j.deangelis@elsevier.com");
	msg2.addTORecepient("K.berryman@elsevier.com");
//	msg2.addTORecepient("v.mettu@elsevier.com");
	msg2.setSender(email);
	msg2.setMessageBody(b2.toString());
	mail.sendMessage(msg2);
	
%>

<ROOT>

</ROOT>


