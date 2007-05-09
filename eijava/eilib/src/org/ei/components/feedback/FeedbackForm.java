package org.ei.components.feedback;


/**
 * Represents feedback form
 * @author  Varma Saripalli
 * @see org.ei.components.feedback.FeedbackComponent
 **/

public class FeedbackForm {



    /**
     * User Name from the feedback form
     **/
    private String username = "";

    /**
     * User email address from the feedback form
     **/
    private String userMailAddress = "";

    /**
     * Topic from the feedback form
     **/
    private String topic = "";

    /**
     * Feedback text from the feedback form
     **/
    private String feedbackText = "";

    public FeedbackForm() {

    }


    /**
     * Sets the User Name  from the feedback form
     **/
    public void setUsername(String username) {
        this.username = username;
    }

   /**
     * Return the User Name from the feedback form
     * @return The User Name from the feedback form
     **/
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user email address from the feedback form
     **/
    public void setUserMailAddress(String userMailAddress) {
        this.userMailAddress = userMailAddress;
    }

    /**
     * Return the User Email Address from the feedback form
     * @return The User Email Address from the feedback form
     **/
    public String getUserMailAddress() {
        return userMailAddress;
    }

    /**
     * Sets the topic from the feedback form
     **/
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Return the topic of the feed back form
     * @return the topic of the feed back form
     **/
    public String getTopic() {
        return topic;
    }

    /**
     * Sets the Feedback text from the feedback form
     * @parm feedbackText Text entered by the user in the feedback form
     **/
    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }


    /**
     * Return the feed back text from the feedback form
     * @return the feed back text from the feedback form
     **/
    public String getFeedbackText() {
        return feedbackText;
    }

    /**
     * Returns A String representation of this object
     * @return A String representation of this object
     **/
    public String toString(){

        String ls = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer();
        buf.append("User:").append(getUsername()).append(ls);
        buf.append("Email:").append(getUserMailAddress()).append(ls);
        buf.append("Topic:").append(getTopic()).append(ls);
        buf.append("Feedback Text:").append(ls).append(getFeedbackText()).append(ls);
        return buf.toString();

    }

    /**
     * The main method is used to test this class
     **/
    public static void main(String[] args) {
        FeedbackForm ff = new FeedbackForm();
        ff.setUsername("A Happy User");
        ff.setUserMailAddress("John Doe <JohnDoe@yahoo.com>");
        ff.setTopic("Customer Service");
        ff.setFeedbackText("Thank you very much");
    }

}
