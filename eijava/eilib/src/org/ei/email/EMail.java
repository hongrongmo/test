package org.ei.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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


public class EMail
{

	public static String MAIL_HOST;
	public static boolean DEBUG = false;
	public static String WEBAPPRESOURCEPATH;
	private Session session;
	boolean debug;

	public EMail()
	{
		this.session = getSession(MAIL_HOST, DEBUG);
	}

   	public static EMail getInstance()
    		throws MessagingException
	{
		return new EMail();
	}

	/*
		The method below does nothing useful. Take this out after email alerts are sorted out.
	*/

   	public static EMail getInstance(String strFilename)
    		throws MessagingException
	{
		return getInstance();
	}


	/**
	* Create a Mimemessage and set its proeprties using values
	* from the EIMessage object.  Used by sendMessage and sendMultiPartMessage
	* see Below
	**/
	private Message createAndSetMessageProperties(EIMessage message)
		throws MessagingException
	{
	        // Create message
	        Message msg = new MimeMessage(session);

	        // Set from address
	        msg.setFrom(new InternetAddress(message.getSender()));

	        if(!message.getTORecepients().isEmpty())
	        {
	            msg.setRecipients(Message.RecipientType.TO, (Address[]) message.getTORecepients().toArray(new Address[1]));
	        }

	        if(!message.getCCRecepients().isEmpty())
	        {
	            msg.setRecipients(Message.RecipientType.CC, (Address[]) message.getCCRecepients().toArray(new Address[1]));
	        }

	        if(!message.getBCCRecepients().isEmpty())
	        {
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
	public void sendMessage( EIMessage message) throws MessagingException
	{
	        Message msg = createAndSetMessageProperties(message);

	        if(message.getContentType()!=null) {
	        	msg.setContent(message.getMessageBody(), message.getContentType());
	        } else {
		        msg.setContent(message.getMessageBody(), "text/plain");
	        }
	        Transport.send(msg);
	}


	/**
	* Send a multipart message using the attributes of the EIMessage argument
	* Add a spacer gif as a body part for HTML viewing
	**/
	public void sendMultiPartMessage( EIMessage message) throws MessagingException
	{
		Map mapBodyParts = new Hashtable();
		mapBodyParts.put("spacergif",WEBAPPRESOURCEPATH + "s.gif");

	        Message msg = createAndSetMessageProperties(message);

	        BodyPart messageBodyPart = new MimeBodyPart();

	        if(message.getContentType()!=null)
	        {
	        	messageBodyPart.setContent(message.getMessageBody(), message.getContentType());
	        } else {
		        messageBodyPart.setContent(message.getMessageBody(), "text/plain");
	        }

		MimeMultipart multipart = new MimeMultipart("related");
		multipart.addBodyPart(messageBodyPart);

		Iterator itrBodyParts = mapBodyParts.keySet().iterator();
		while(itrBodyParts.hasNext()) {
			String strCID = (String) itrBodyParts.next();
			String strFielname = (String) mapBodyParts.get(strCID);

			// add body part to header
			messageBodyPart = new MimeBodyPart();

			DataSource fds = new FileDataSource(strFielname);
			messageBodyPart.setDataHandler(new DataHandler(fds));
			messageBodyPart.setHeader("Content-id",strCID);
			multipart.addBodyPart(messageBodyPart);

			// add content to message
			msg.setContent(multipart);
		}

	        Transport.send(msg);
	}

    /**
     * Send the message using the arguments
     **/
    public void sendMessage( String recipient, String subject, String message, String sender) throws MessagingException
    {
        // Create meassage
        Message msg = new MimeMessage(session);

        // Set from address
        msg.setFrom(new InternetAddress(sender));

        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }

    public void sendMessage(InputStream instream)
    	throws MessagingException
    {
		MimeMessage message = new MimeMessage(session,
											  instream);
    	Transport.send(message);
	}


    /**
     * Gets the javamail session for the SMTP host
     **/
    private Session getSession(String smtpHost, boolean debug)
    {

         Properties props = new Properties();
         //Set the host smtp address
         props.put("mail.transport.protocol","smtp");
         props.put("mail.smtp.host", smtpHost);

         // create some properties and get the default Session
         Session session = Session.getDefaultInstance(props, null);

         session.setDebug(debug);

         return session;

	}

    /**
     * Returns A String representation of this object
     * @return A String representation of this object
     **/
    public String toString(){
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        printWriter.println("Email");
        session.getProperties().list(printWriter);
        return sw.toString();
    }


}
