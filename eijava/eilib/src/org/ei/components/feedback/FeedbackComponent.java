package org.ei.components.feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.ei.config.ConfigService;
import org.ei.config.RuntimeProperties;
import org.ei.email.EIMessage;
import org.ei.email.EMail;
import org.ei.exception.ValueNotFoundException;

/**
 *  This class handles the submission of Feedback form.
 *
 *  Usage pattern is
 *  FeedbackForm feedbackForm = new FeedbackForm();
 *  feedbackForm.setUsername(username);
 *  feedbackForm.setUserMailAddress(userMailAddress);
 *  feedbackForm.setTopic(topic);
 *  feedbackForm.setFeedbackText(feedbackText);
 *
 *  Create a FeedbackForm object named say feedbackkForm;
 *  FeedbackComponent fc= ComponentFactory.getFeedbackComponent(Class pointer);
 *  fc.init();
 *  fc.submit(feedbackkForm);
 *
 * @author  Varma Saripalli
 * @see org.ei.components.feedback.FeedbackForm
 **/
public class FeedbackComponent {

    /**
     * EMail instance used to mail a feedback form
     **/
    EMail mail;
    RuntimeProperties eiProps;

    /**
     * Initialize the component
     **/
    public FeedbackComponent() {
    }


    /**
     * Initialize the component
     **/
    public void init() throws MessagingException{
        mail = EMail.getInstance();
        try{
           eiProps = ConfigService.getRuntimeProperties();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Build the EIMessage object from the elements of the feedback form.
     * @see org.ei.email.EIMessage;
     **/
    private EIMessage buildEIMessage(FeedbackForm feedbackForm) throws AddressException{

        EIMessage msg = new EIMessage();
        msg.setMessageBody(getMessageBody(feedbackForm));
        msg.setSubject(getMessageSubject(feedbackForm));
        msg.setSender(EIMessage.DEFAULT_SENDER);
        msg.setFrom(feedbackForm.getUserMailAddress());

        msg.addTORecepients(getTORecepients());
        msg.addCCRecepients(getCCRecepients());
        msg.addBCCRecepients(getBCCRecepients());

        return msg;
    }


    /**
     * Loads the configuration information and return the
     * value for the named property.
     **/
    private String getConfig(String propertyName) throws ValueNotFoundException{
        String retStr="";
	    try{
		    retStr=eiProps.getProperty(propertyName);
		}catch(Exception e){
		    e.printStackTrace();
		    throw new ValueNotFoundException(propertyName);
		}
		return retStr;

    }


    /**
     * submits the feedback form as email
     * @parm feedbackForm The feedback form submitted by the user
     **/
    public void submit(FeedbackForm feedbackForm) throws MessagingException{

            EIMessage msg = buildEIMessage(feedbackForm);

            mail.sendMessage(msg);
    }


    /**
     * Formats and returns the messsage body from the Feedback form
     * @parm feedbackForm The feedback form submitted by the user
     **/
    private String getMessageBody(FeedbackForm feedbackForm){

        String ls = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer();
        buf.append("From: ").append(feedbackForm.getUsername()).append(ls);
        buf.append("E-mail: ").append(feedbackForm.getUserMailAddress()).append(ls);
        buf.append("Area: ").append(feedbackForm.getTopic()).append(ls);
        buf.append("Feedback: ").append(ls).append(feedbackForm.getFeedbackText()).append(ls);

        return buf.toString();
    }

    /**
     * Formats and returns the messsage subject from the Feedback form
     * @parm feedbackForm The feedback form submitted by the user
     **/
    private String getMessageSubject(FeedbackForm feedbackForm){

        String feedbackSubject = "Engineering Village Customer Feedback";
        return feedbackSubject;
        //return feedbackForm.getTopic();

    }

    /**
     * Returns a list of TO recepients
     * @return List of recepients
     **/
    private List getTORecepients(){
        List list=null;
        try{
            list = new ArrayList();
            String toRecepients=eiProps.getProperty("TORecepients");
            if(!(toRecepients.trim()=="")){
                StringTokenizer st=new StringTokenizer(toRecepients,",");
                while(st.hasMoreTokens()){
                    String tempStr=st.nextToken();
                    list.add(tempStr);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns a list of CC recepients
     * @return List of recepients
     **/
    private List getCCRecepients(){
        List list=null;
        try{
            list = new ArrayList();
            String ccRecepients=eiProps.getProperty("CCRecepients");
            if(ccRecepients != null)
            {
              if(!(ccRecepients.trim()=="")){
                  StringTokenizer st=new StringTokenizer(ccRecepients,",");
                  while(st.hasMoreTokens()){
                      String tempStr=st.nextToken();
                      list.add(tempStr);
                  }
              }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns a list of BCC recepients
     * @return List of recepients
     **/
    private List getBCCRecepients(){
       List list=null;
        try{
            list = new ArrayList();
            String bccRecepients=eiProps.getProperty("BCCRecepients");
            if(!(bccRecepients.trim()=="")){
                StringTokenizer st=new StringTokenizer(bccRecepients,",");
                while(st.hasMoreTokens()){
                    String tempStr=st.nextToken();
                    list.add(tempStr);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * The main method is used to test this class and show the usage patterns
     **/
    public static void main(String[] args) {
        try{
            FeedbackForm feedbackForm = new FeedbackForm();
            feedbackForm.setUsername("A Happy User");
            feedbackForm.setUserMailAddress("");
            feedbackForm.setTopic("Customer Service");
            feedbackForm.setFeedbackText("Thank you very much");

            //TODO: replace with factory instantiation
            FeedbackComponent feedbackComponent =
                (FeedbackComponent) Class.forName("org.ei.components.feedback.FeedbackComponent").newInstance();
            feedbackComponent.init();
            feedbackComponent.submit(feedbackForm);
        }catch(Exception e){
            e.printStackTrace();

        }

    }

}
