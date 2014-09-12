/**
 *
 */
package org.ei.biz.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.ei.config.EVProperties;

import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

/**
 * Class to hold contents of a message meant to be used with Amazon SES.
 * @author harovetm
 *
 */
public class SESMessage {

    private Destination destination;
    private Message message;
    private List<String> replyto;
    private String returnPath;
    private String from;

    public static final String DEFAULT_SENDER = "eicustomersupport@elsevier.com";
    public static final String NOREPLY_SENDER = "ei-noreply@elsevier.com";

    public SESMessage() {
        this.returnPath = EVProperties.getRuntimeProperties().getProperty("mail.smtp.noreply", "einoreply@elsevier.com");
    }

    // Simple constructor
    public SESMessage(String to, String from, String subject, String message, boolean ishtmlBody) {
        this();
        if (GenericValidator.isBlankOrNull(from)) {
            throw new IllegalArgumentException("'From' address cannot be empty!");
        }
        this.setMessage(subject, message,ishtmlBody);
        this.setDestination(to);
        this.from = from;
    }



    public SendEmailRequest buildRequest() {
        SendEmailRequest request = new SendEmailRequest().withSource(this.from).withDestination(this.destination).withMessage(this.message);
        if (this.replyto != null && this.replyto.size() > 0) {
            request.setReplyToAddresses(replyto);
        }
        request.setReturnPath(this.returnPath);
        return request;
    }

    public void setFrom(String from) {
        if (GenericValidator.isBlankOrNull(from)) {
            throw new IllegalArgumentException("'From' address cannot be empty!");
        }
        this.from = from;
    }

    public String getFrom() {
        return this.from;
    }

    public void setReplyTo(String replyto) {
        this.replyto = new ArrayList<String>();
        this.replyto.add(replyto);
    }

    public void setReplyTo(List<String> replyto) {
        this.replyto = replyto;
    }

    public List<String> getReplyTo() {
        return this.replyto;
    }

    public void setMessage(String subject, String message, boolean ishtmlBody) {
        if (GenericValidator.isBlankOrNull(subject) || GenericValidator.isBlankOrNull(message)) {
            throw new IllegalArgumentException("'Subject' or 'Message' cannot be empty!");
        }
        Content subjectcontent = new Content(subject);
        Body body = null;
        if(ishtmlBody){
        	Content html = new Content().withData(message);
        	body = new Body().withHtml(html);
        }else{
        	body = new Body(new Content(message));
        }
        this.message = new Message(subjectcontent, body);
    }

    public Message getMessage() {
        return this.message;
    }

    public void setDestination(String to) {
        if (GenericValidator.isBlankOrNull(to)) {
            throw new IllegalArgumentException("'To' address cannot be empty!");
        }
        setDestination(Arrays.asList(to.split(",")));
    }

    public void setDestination(List<String> to) {
        this.setDestination(to, null, null);
    }

    public void setDestination(List<String> to, List<String> cc) {
        this.setDestination(to, cc, null);
    }

    public void setDestination(List<String> to, List<String> cc, List<String> bcc) {

    	this.destination = new Destination();

    	if (to != null && to.size() > 0) {
            this.destination.setToAddresses(to);
        }
    	if (cc != null && cc.size() > 0) {
            this.destination.setCcAddresses(cc);
        }
        if (bcc != null && bcc.size() > 0) {
            this.destination.setBccAddresses(bcc);
        }
    }


    public Destination getDestination() {
        return this.destination;
    }

    public String getReturnPath() {
        return returnPath;
    }

    public void setReturnPath(String returnPath) {
        this.returnPath = returnPath;
    }
}
