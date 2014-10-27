<%@ page session="false" %>
<%@ page import="org.ei.controller.ControllerClient, org.ei.session.*,java.util.*" %>
<%
	int number = 0;
	ControllerClient client = new ControllerClient(request, response);
	UserSession usession = client.getUserSession();

	String snumber = usession.getProperty("SNUMBER");

	if(snumber != null)
	{
		number = Integer.parseInt(snumber);
	}

	++number;
	usession.setProperty("SNUMBER", Integer.toString(number));
	User user = usession.getUser();

	client.updateUserSession(usession);
	client.log("TEST", "Test the logger");
	client.setRemoteControl();

%>


<doc>
Hello Joel <%= usession.getID() %>  <%= number %> Test <%= user.getCustomerID() %>
</doc>
