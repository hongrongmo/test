package org.ei.email;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;

public class EMail {
    private Logger log4j = Logger.getLogger(EMail.class);

    private static EMail instance;

    private Properties mailprops = new Properties();
    private String mailhost;
    private String username;
    private String password;

    private Session session;
    boolean debug;

    /**
     * Constructor for EMail object
     */
    private EMail() {
        try {
            ApplicationProperties runtimeprops = ApplicationProperties.getInstance();
            if (runtimeprops == null) {
                throw new RuntimeException("RuntimeProperties object could not be built!!!");
            }

            this.mailprops.clear();
            this.mailprops.put("mail.transport.protocol", "smtp");

            // Debug?
            String debug = runtimeprops.getProperty("mail.smtp.debug");
            if (!GenericValidator.isBlankOrNull(debug)) {
                this.mailprops.put("mail.debug", debug);
            }

            // Set the mailhost
            this.mailhost = runtimeprops.getProperty("mail.smtp.host");
            if (GenericValidator.isBlankOrNull(this.mailhost)) {
                this.mailhost = "localhost";
            }
            this.mailprops.put("mail.smtp.host", this.mailhost);

            // Get username/password for service
            this.username = runtimeprops.getProperty("mail.smtp.username");
            this.password = runtimeprops.getProperty("mail.smtp.password");
            if (GenericValidator.isBlankOrNull(this.username) || GenericValidator.isBlankOrNull(this.password)) {
                throw new IllegalArgumentException("SMTP setup is missing username and/or password for SMTP connection!");
            }

            // Set properties indicating that we want to use STARTTLS to encrypt the connection.
            // The SMTP session will begin on an unencrypted connection, and then the client
            // will issue a STARTTLS command to upgrade to an encrypted connection.
            this.mailprops.put("mail.smtp.auth", "true");
            this.mailprops.put("mail.smtp.starttls.enable", "true");
            this.mailprops.put("mail.smtp.starttls.required", "true");


            // Get email session
            this.session = Session.getDefaultInstance(this.mailprops);

        } catch (Exception e) {
            throw new RuntimeException("Unable to create EMail object!", e);
        }
    }

    /**
     * Singleton instance fetcher
     *
     * @return
     * @throws MessagingException
     */
    public static EMail getInstance() throws MessagingException {
        if (instance == null) {
            instance = new EMail();
        }
        return instance;
    }

    /*
     * The method below does nothing useful. Take this out after email alerts are sorted out.
     */

    public static EMail getInstance(String strFilename) throws MessagingException {
        return getInstance();
    }

    /**
     * Create a Mimemessage and set its proeprties using values from the EIMessage object. Used by sendMessage and sendMultiPartMessage see Below
     **/
    private Message createAndSetMessageProperties(EIMessage message) throws MessagingException {
        // Create message
        MimeMessage msg = new MimeMessage(session);
        msg.setSender(new InternetAddress(message.getSender()));
        msg.setFrom(new InternetAddress(message.getFrom()));

        if (!message.getTORecepients().isEmpty()) {
            msg.setRecipients(Message.RecipientType.TO, (Address[]) message.getTORecepients().toArray(new Address[1]));
        }

        if (!message.getReplyToRecepients().isEmpty()) {
            msg.setReplyTo((Address[]) message.getReplyToRecepients().toArray(new Address[1]));
        }

        if (!message.getCCRecepients().isEmpty()) {
            msg.setRecipients(Message.RecipientType.CC, (Address[]) message.getCCRecepients().toArray(new Address[1]));
        }

        if (!message.getBCCRecepients().isEmpty()) {
            msg.setRecipients(Message.RecipientType.BCC, (Address[]) message.getBCCRecepients().toArray(new Address[1]));
        }

        // Setting the Subject and Content Type
        msg.setSubject(message.getSubject());
        msg.setSentDate(message.getSentDate());

        return msg;
    }

    /**
     * Send a message using the attributes of the EIMessage argument
     **/
    public void sendMessage(EIMessage message) throws MessagingException {
        Message msg = createAndSetMessageProperties(message);
        // addUseOfCookielinkWithEmailText(message);
        if (message.getContentType() != null) {
            msg.setContent(message.getMessageBody(), message.getContentType());
        } else {
            msg.setContent(message.getMessageBody(), "text/plain");
        }

        // Send message!
        Transport transport = this.session.getTransport();
        try
        {
            log4j.info("Attempting to send an email through the Amazon SES SMTP interface.  Host: '" + this.mailhost + "', username: '" + this.username + "', password: '" + this.password + "'");

            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(this.mailhost, this.username, this.password);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            log4j.info("Email sent!");
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
    }

    private void addUseOfCookielinkWithEmailText(EIMessage message) {
        message.setMessageBody(message.getMessageBody().concat(
            "\n\nTo know about Cookie use on website, please got to www.engineeringvillage.com/controller/servlet/Controller?CID=cookie page."));
    }

    /**
     * Send a multipart message using the attributes of the EIMessage argument Add a spacer gif as a body part for HTML viewing
     **/
    public void sendMultiPartMessage(EIMessage message) throws MessagingException {
        Message msg = createAndSetMessageProperties(message);
        BodyPart messageBodyPart = new MimeBodyPart();

        if (message.getContentType() != null) {
            messageBodyPart.setContent(message.getMessageBody(), message.getContentType());
        } else {
            messageBodyPart.setContent(message.getMessageBody(), "text/plain");
        }

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        msg.setContent(multipart);

        // Send message!
        Transport transport = this.session.getTransport();
        try
        {
            log4j.info("Attempting to send multipart email through the Amazon SES SMTP interface...");

            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(this.mailhost, this.username, this.password);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            log4j.info("Multipart email sent!");
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
    }

    /**
     * Send the message using the arguments
     **/
    public void sendMessage(String recipient, String subject, String message, String sender, String replyto, String from) throws MessagingException {
        // Create meassage
        MimeMessage msg = new MimeMessage(session);

        // Set from address
        msg.setFrom(new InternetAddress(from));
        msg.setSender(new InternetAddress(sender));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        InternetAddress iaddress = new InternetAddress(replyto);

        Address[] address = new InternetAddress[1];
        address[0] = iaddress;
        msg.setReplyTo(address);

        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }

    /**
     * Send the message using the arguments
     **/
    public void sendMessage(String recipient, String subject, String message, String sender) throws MessagingException {

    	 // Create message
        MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(sender));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));


        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/html;charset=utf-8");

        // Send message!
        Transport transport = this.session.getTransport();
        try
        {
            log4j.info("Attempting to send an email through the Amazon SES SMTP interface.  Host: '" + this.mailhost + "', username: '" + this.username + "', password: '" + this.password + "'");

            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(this.mailhost, this.username, this.password);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            log4j.info("Email sent!");
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
    }

    /**
     * Send the message using the arguments
     **/
    public void sendMessage(String[] recipient, String subject, String message, String sender, String replyto, String from) throws MessagingException {
        // Create meassage
        MimeMessage msg = new MimeMessage(session);

        // Set from address
        msg.setFrom(new InternetAddress(from));
        msg.setSender(new InternetAddress(sender));
        for (String to : recipient) {
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        }

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        InternetAddress iaddress = new InternetAddress(replyto);

        Address[] address = new InternetAddress[1];
        address[0] = iaddress;
        msg.setReplyTo(address);

        msg.setContent(message, "text/plain");
     // Send message!
        Transport transport = this.session.getTransport();
        try
        {
            log4j.info("Attempting to send an email through the Amazon SES SMTP interface.  Host: '" + this.mailhost + "', username: '" + this.username + "', password: '" + this.password + "'");

            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(this.mailhost, this.username, this.password);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            log4j.info("Email sent!");
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
        //Transport.send(msg);
    }

    /**
     * Please use sendMessage with recipient instead.  Email sending requires authentication now with Cloud Migration.
     * @param instream
     * @throws MessagingException
     */
    @Deprecated
    public void sendMessage(InputStream instream) throws MessagingException {
        MimeMessage message = new MimeMessage(session, instream);
        Transport.send(message);
    }

    /**
     * Returns A String representation of this object
     *
     * @return A String representation of this object
     **/
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        printWriter.println("Email");
        session.getProperties().list(printWriter);
        return sw.toString();
    }
    
    public static void main(String args[]) throws Exception
    {
    	String host = "email-smtp.us-east-1.amazonaws.com";
    	String username = "AKIAISNCJA3QM32ZUFYA";
        String Password = "AnYW09dHySrs6LT4Q7uf6LohcZuXFoSN++Mzi3MRllLu";
        //String from = "eiemailalert@elsevier.com";
        String from = args[0];
        String toAddress = "h.mo@elsevier.com";
        String filename = "C:/Users/hp/Desktop/Write.txt";
        // Get system properties
        Properties props = System.getProperties();
        props.put("mail.smtp.port", 25);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, null);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));

        message.setRecipients(Message.RecipientType.TO, toAddress);

        message.setSubject("JavaMail Attachment");

        BodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setText("Here's the file");



        message.setContent("Here is test message", "text/plain");

        try {
            Transport tr = session.getTransport("smtp");
            tr.connect(host, username, Password);
            tr.sendMessage(message, message.getAllRecipients());
            System.out.println("Mail Sent Successfully");
            tr.close();

        } catch (Exception sfe) {

            System.out.println(sfe);
        }
       finally
       {
    	   System.exit(1);;
       }
    }

}
