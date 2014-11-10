 <%-- This jsp is used by a Non customer.Basically submits the feedback form  --%>
 
<%@ page language = "java" import="org.ei.components.feedback.*,org.ei.email.*" %>
<%@ page session="false" %>
  <%
    
    String username = request.getParameter("fullname");
    String userMailAddress = request.getParameter("email");
    String topic = request.getParameter("comment");
    String feedbackText = request.getParameter("suggestions");

       try{
	    FeedbackForm feedbackForm = new FeedbackForm();
	    feedbackForm.setUsername(username);
	    feedbackForm.setUserMailAddress(userMailAddress);
	    feedbackForm.setTopic(topic);
	    feedbackForm.setFeedbackText(feedbackText);

	    FeedbackComponent feedbackComponent = new FeedbackComponent();
	    feedbackComponent.init();
	    feedbackComponent.submit(feedbackForm);
	}catch(Exception e){
	    e.printStackTrace();
	}

  %>

<root>

<FOOTER/>

</root>


