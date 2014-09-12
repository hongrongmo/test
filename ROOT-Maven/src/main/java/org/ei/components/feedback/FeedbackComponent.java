package org.ei.components.feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import org.ei.biz.email.SESEmail;
import org.ei.biz.email.SESMessage;
import org.ei.config.ConfigService;
import org.ei.config.RuntimeProperties;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;

/**
 * This class handles the submission of Feedback form.
 *
 * Usage pattern is FeedbackForm feedbackForm = new FeedbackForm(); feedbackForm.setUsername(username); feedbackForm.setUserMailAddress(userMailAddress);
 * feedbackForm.setTopic(topic); feedbackForm.setFeedbackText(feedbackText);
 *
 * Create a FeedbackForm object named say feedbackkForm; FeedbackComponent fc= ComponentFactory.getFeedbackComponent(Class pointer); fc.init();
 * fc.submit(feedbackkForm);
 *
 * @author Varma Saripalli
 * @see org.ei.components.feedback.FeedbackForm
 **/
public class FeedbackComponent {

    RuntimeProperties eiProps;

    /**
     * Initialize the component
     **/
    public FeedbackComponent() {
    }

    /**
     * Initialize the component
     **/
    public void init() throws MessagingException {
        try {
            eiProps = ConfigService.getRuntimeProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SESMessage buildSESMessage(FeedbackForm feedbackForm) {

        SESMessage sesmessage = new SESMessage();
        sesmessage.setMessage(getMessageSubject(feedbackForm), getMessageBody(feedbackForm), false);
        sesmessage.setFrom(SESMessage.DEFAULT_SENDER);
        List<String> ccList = getCCRecepients();
        ccList.add(feedbackForm.getUserMailAddress());
        sesmessage.setDestination(getTORecepients(), ccList, getBCCRecepients());
        return sesmessage;
    }

    /**
     * Loads the configuration information and return the value for the named property.
     **/
    private String getConfig(String propertyName) throws InfrastructureException {
        String retStr = "";
        try {
            retStr = eiProps.getProperty(propertyName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InfrastructureException(SystemErrorCodes.UNKNOWN_INFRASTRUCTURE_ERROR, propertyName, e);
        }
        return retStr;

    }

    /**
     * submits the feedback form as email
     *
     * @throws ServiceException
     *
     * @parm feedbackForm The feedback form submitted by the user
     **/
    public void submit(FeedbackForm feedbackForm) throws ServiceException {

        SESMessage sesmessage = buildSESMessage(feedbackForm);
        SESEmail.getInstance().send(sesmessage);
    }

    /**
     * Formats and returns the messsage body from the Feedback form
     *
     * @parm feedbackForm The feedback form submitted by the user
     **/
    private String getMessageBody(FeedbackForm feedbackForm) {

        String ls = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer();
        buf.append("From: ").append(feedbackForm.getUsername()).append(ls);
        buf.append("Email: ").append(feedbackForm.getUserMailAddress()).append(ls);
        buf.append("Area: ").append(feedbackForm.getTopic()).append(ls);
        buf.append("Feedback: ").append(ls).append(feedbackForm.getFeedbackText()).append(ls);

        return buf.toString();
    }

    /**
     * Formats and returns the messsage subject from the Feedback form
     *
     * @parm feedbackForm The feedback form submitted by the user
     **/
    private String getMessageSubject(FeedbackForm feedbackForm) {

        String feedbackSubject = "Engineering Village Customer Feedback";
        return feedbackSubject;
        // return feedbackForm.getTopic();

    }

    /**
     * Returns a list of TO recepients
     *
     * @return List of recepients
     **/
    private List<String> getTORecepients() {
        List<String> list = null;
        try {
            list = new ArrayList<String>();
            String toRecepients = eiProps.getProperty("TORecepients");
            if (!(toRecepients.trim() == "")) {
                StringTokenizer st = new StringTokenizer(toRecepients, ",");
                while (st.hasMoreTokens()) {
                    String tempStr = st.nextToken();
                    list.add(tempStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns a list of CC recepients
     *
     * @return List of recepients
     **/
    private List<String> getCCRecepients() {
        List<String> list = null;
        try {
            list = new ArrayList<String>();
            String ccRecepients = eiProps.getProperty("CCRecepients");
            if (ccRecepients != null) {
                if (!(ccRecepients.trim() == "")) {
                    StringTokenizer st = new StringTokenizer(ccRecepients, ",");
                    while (st.hasMoreTokens()) {
                        String tempStr = st.nextToken();
                        list.add(tempStr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns a list of BCC recepients
     *
     * @return List of recepients
     **/
    private List<String> getBCCRecepients() {
        List<String> list = null;
        try {
            list = new ArrayList<String>();
            String bccRecepients = eiProps.getProperty("BCCRecepients");
            if (!(bccRecepients.trim() == "")) {
                StringTokenizer st = new StringTokenizer(bccRecepients, ",");
                while (st.hasMoreTokens()) {
                    String tempStr = st.nextToken();
                    list.add(tempStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * The main method is used to test this class and show the usage patterns
     **/
    public static void main(String[] args) {
        try {
            FeedbackForm feedbackForm = new FeedbackForm();
            feedbackForm.setUsername("A Happy User");
            feedbackForm.setUserMailAddress("");
            feedbackForm.setTopic("Customer Service");
            feedbackForm.setFeedbackText("Thank you very much");

            // TODO: replace with factory instantiation
            FeedbackComponent feedbackComponent = (FeedbackComponent) Class.forName("org.ei.components.feedback.FeedbackComponent").newInstance();
            feedbackComponent.init();
            feedbackComponent.submit(feedbackForm);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
