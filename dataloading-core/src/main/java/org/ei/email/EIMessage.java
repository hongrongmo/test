package org.ei.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * 
 * This class models an email message customized to EI needs.
 * 
 * Usage pattern.
 * 
 * EMail mail = EMail.getInstance();
 * 
 * EIMessage msg = new EIMessage();
 * 
 * msg.setMessageBody("This is the info filled in the user feed back form"); 
 * msg.setSubject("User feed back"); 
 * msg.setSender("savnvarma@yahoo.com");
 * msg.addTORecepient("vsaripalli@choice-solutions.com"); 
 * msg.addCCRecepient("savnvarma@usa.com"); 
 * msg.addBCCRecepient("savnv@usa.net"); 
 * EMail mail = EMail.getInstance(); mail.sendMessage(msg);
 * 
 * @author Varma Saripalli
 * @see Email
 * 
 **/
public class EIMessage {

    public static final String DEFAULT_SENDER = "eicustomersupport@elsevier.com";
    public static final String NOREPLY_SENDER = "ei-noreply@elsevier.com";

    /**
     * The message body text..
     **/
    private String messageBody;

    /**
     * The message subject line.
     **/
    private String subject;

    /**
     * The message sender address.
     **/
    private InternetAddress sender;

    private InternetAddress from;

    /**
     * The message sender address.
     **/
    private java.util.Date sentDate;

    private String sentContentType;

    /**
     * The TO recepients InternetAddress List
     **/
    private List<InternetAddress> tORecepients = new ArrayList<InternetAddress>();

    /**
     * The CC recepients InternetAddress List
     **/
    private List<InternetAddress> cCRecepients = new ArrayList<InternetAddress>();

    /**
     * The BCC recepients InternetAddress List
     **/
    private List<InternetAddress> bCCRecepients = new ArrayList<InternetAddress>();

    /**
     * defauls constructor
     **/
    private List<InternetAddress> replyToRecepients = new ArrayList<InternetAddress>();

    public EIMessage() {

    }

    public void addReplyToRecepients(List<String> recepients) throws AddressException {
        for (Iterator<String> iter = recepients.iterator(); iter.hasNext();) {

            this.replyToRecepients.add(new InternetAddress(iter.next().toLowerCase()));
        }
    }

    public List<InternetAddress> getReplyToRecepients() {
        return this.replyToRecepients;
    }

    /**
     * Sets the message body text..
     **/
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * Returns the message body text
     **/
    public String getMessageBody() {
        return messageBody;
    }

    /**
     * Sets the subject
     * 
     * @param subject
     *            The subject line in the message
     **/
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Returns the subject
     * 
     * @return The subject line in the message
     **/
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the sender
     * 
     * @param sender
     *            Sender of the message. This parameter should conform to an RFC822 address.
     **/
    public void setSender(String sender) throws AddressException {
        this.sender = new InternetAddress(sender.toLowerCase());
    }

    public void setFrom(String from) throws AddressException {
        this.from = new InternetAddress(from.toLowerCase());
    }

    /**
     * Returns the sender
     * 
     * @return the sender
     **/
    public String getSender() {
        return sender.getAddress();
    }

    public String getFrom() {
        return from.getAddress();
    }

    /**
     * Sets the sent date
     * 
     * @param sentDate
     *            .
     **/
    public void setSentDate(java.util.Date sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * Returns the sentDate
     * 
     * @return the sentDate.
     **/
    public Date getSentDate() {
        return sentDate;
    }

    /**
     * Returns the sentContentType
     * 
     * @return the sentContentType.
     **/
    public void setContentType(String contentType) {
        sentContentType = contentType;
    }

    /**
     * Returns the sentContentType
     * 
     * @return the sentContentType.
     **/
    public String getContentType() {
        return sentContentType;
    }

    /**
     * Adds the recepient to the list of recepients for this message
     * 
     * @param recepient
     *            of the message. This parameter should conform to an RFC822 address.
     **/
    public void addTORecepient(String recepient) throws AddressException {
        this.tORecepients.add(new InternetAddress(recepient.toLowerCase()));
    }

    /**
     * Adds all the recepient in this list to the list of recepients for this message
     * 
     * @parm List of recepients as Strings
     **/
    public void addTORecepients(List<String> recepients) throws AddressException {
        for (Iterator<String> iter = recepients.iterator(); iter.hasNext();) {
            this.tORecepients.add(new InternetAddress(((String) iter.next()).toLowerCase()));
        }
    }

    /**
     * Returns the List of recepients in this message
     * 
     * @return List of recepients as Strings
     **/
    public List<InternetAddress> getTORecepients() {
        return this.tORecepients;
    }

    /**
     * Adds the recepient to the list of CC recepients for this message
     * 
     * @param recepient
     *            of the message. This parameter should conform to an RFC822 address.
     **/
    public void addCCRecepient(String recepient) throws AddressException {
        this.cCRecepients.add(new InternetAddress(recepient.toLowerCase()));
    }

    /**
     * Adds all the recepient in this list to the list of CC recepients for this message
     * 
     * @parm recepients List of CC recepients as Strings. Each object in the list should be a String conforming to an RFC822 address.
     **/
    public void addCCRecepients(List<String> recepients) throws AddressException {
        for (Iterator<String> iter = recepients.iterator(); iter.hasNext();) {
            this.cCRecepients.add(new InternetAddress(((String) iter.next()).toLowerCase()));
        }
    }

    /**
     * Returns the List of CC recepients in this message
     * 
     * @return List of CC recepients as Strings
     **/
    public List<InternetAddress> getCCRecepients() {
        return this.cCRecepients;
    }

    /**
     * Adds the recepient to the list of BCC recepients for this message
     * 
     * @param recepient
     *            of the message. This parameter should conform to an RFC822 address.
     **/
    public void addBCCRecepient(String recepient) throws AddressException {
        this.bCCRecepients.add(new InternetAddress(recepient.toLowerCase()));
    }

    /**
     * Adds all the recepient in this list to the list of BCC recepients for this message
     * 
     * @parm recepients List of BCC recepients as Strings. Each object in the list should be a String conforming to an RFC822 address.
     **/
    public void addBCCRecepients(List<String> recepients) throws AddressException {
        for (Iterator<String> iter = recepients.iterator(); iter.hasNext();) {
            this.bCCRecepients.add(new InternetAddress(((String) iter.next()).toLowerCase()));
        }
    }

    /**
     * Returns the List of BCC recepients in this message
     * 
     * @return List of BCC recepients as Strings
     **/
    public List<InternetAddress> getBCCRecepients() {
        return this.bCCRecepients;
    }

    /**
     * Returns A String representation of the data in this message
     * 
     * @return A String representation of the data in this message
     **/
    public String toString() {

        String ls = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer();

        buf.append("From:");
        buf.append(getFrom().toString());
        buf.append(ls);

        buf.append("To:");
        buf.append(ls);
        for (Iterator<InternetAddress> iter = getTORecepients().iterator(); iter.hasNext();) {
            buf.append(iter.next().toString());
            if (iter.hasNext()) {
                buf.append(",");
                buf.append(ls);
            } else {
                buf.append(ls);
            }
        }

        buf.append("CC:");
        buf.append(ls);
        for (Iterator<InternetAddress> iter = getCCRecepients().iterator(); iter.hasNext();) {
            buf.append(iter.next().toString());
            if (iter.hasNext()) {
                buf.append(",");
                buf.append(ls);
            } else {
                buf.append(ls);
            }
        }

        buf.append("BCC:");
        buf.append(ls);
        for (Iterator<InternetAddress> iter = getBCCRecepients().iterator(); iter.hasNext();) {
            buf.append(iter.next().toString());
            if (iter.hasNext()) {
                buf.append(", ");
                buf.append(ls);
            } else {
                buf.append(ls);
            }
        }

        buf.append("Reply-to:");
        buf.append(ls);
        for (Iterator<InternetAddress> iter = getReplyToRecepients().iterator(); iter.hasNext();) {
            buf.append(iter.next().toString());
            if (iter.hasNext()) {
                buf.append(", ");
                buf.append(ls);
            } else {
                buf.append(ls);
            }
        }

        buf.append("Sender:");
        buf.append(getSender().toString());
        buf.append(ls);

        buf.append("Subject:");
        buf.append(getSubject());
        buf.append(ls);

        buf.append("Message Body:");
        buf.append(getMessageBody());
        buf.append(ls);

        return buf.toString();
    }

    /*
     * The main method is used to test this class
     */
    public static void main(String[] args) {

        try {

            EIMessage message = new EIMessage();

            message.setSender("vsaripalli@choice-solutions.com");

            message.addTORecepient("Recepient1");
            message.addTORecepient("Recepient2");
            message.addTORecepient("Recepient3");
            message.addTORecepient("Recepient4");

            message.addCCRecepient("CCRece/pient1");
            message.addCCRecepient("CCRecepient2");
            message.addCCRecepient("CCRecepient3");
            message.addCCRecepient("CCRecepient4");

            message.addBCCRecepient("BCCRecepient1");
            message.addBCCRecepient("BCCRecepient2");
            message.addBCCRecepient("BCCRecepient3");
            message.addBCCRecepient("BCCRecepient4");

            message.setSubject("This is the Subject");
            message.setMessageBody("This is the Message\nNext Line in message\nNext Line in message");

            List<String> list = new Vector<String>();
            list.add("RecepientfromList-1");
            list.add("RecepientfromList-2");
            list.add("RecepientfromList-3");
            message.addTORecepients(list);

            List<String> list1 = new ArrayList<String>();
            list1.add("CCRecepientfromList-1");
            list1.add("CCRecepientfromList-2");
            list1.add("CCRecepientfromList-3");
            message.addCCRecepients(list);

            List<String> list2 = new LinkedList<String>();
            list2.add("BCCRecepientfromList-1");
            list2.add("BCCRecepientfromList-2");
            list2.add("BCCRecepientfromList-3");
            message.addBCCRecepients(list);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}
