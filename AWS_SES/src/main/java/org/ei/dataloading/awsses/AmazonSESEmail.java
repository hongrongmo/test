package org.ei.dataloading.awsses;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Transport;

/**
 * 
 * @author TELEBH
 * @Description: Send AWS SES Email to simulate Newstar SMTP 
 * @Date: 04/28/2020
 */
public class AmazonSESEmail {

	
	enum EMAIL
	{
		FROM("NewStar@Elsevier.com"),
		FRONAME("Encompass Admin"),
		TO("h.teleb@elsevier.com"),
		SMTP_USERNAME("xxxx"),					// Make sure to replace it with actual accessKey value
		SMTP_PASSWORD("xxxx"),					// Make sure to replace it with actual secret key value
		HOST("email-smtp.us-east-1.amazonaws.com"),
		PORT("587"),
		SUBJECT("Amazon SES/SMTP test for NewStar...."),
		BODY(String.join(
	    	    System.getProperty("line.separator"),
	    	    "<h1>Amazon SES SMTP Email Test from NewSTar EC2</h1>",
	    	    "<p>This email was sent with Amazon SES using the ", 
	    	    "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
	    	    " for <a href='https://www.java.com'>Java</a>."
	    	));
		
		
		String name;
		private EMAIL(String name)
		{
			this.name = name;
		}
		public String getName()
		{
			return name;
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		// creat email config properties
		Properties properties = System.getProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.port", EMAIL.PORT.getName());
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth","true");
		
		// Create mail session with defined properties
		Session session = Session.getDefaultInstance(properties);
		
		// Create Message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(EMAIL.FROM.getName(), EMAIL.FRONAME.getName()));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL.TO.getName()));;
		message.setSubject(EMAIL.SUBJECT.getName());
		message.setContent(EMAIL.BODY.getName(), "text/html");
		
		
		// Creat Transport Layer
		Transport transport = session.getTransport();
		
		// Send Message
		
		try
		{
			//Connect to SES using SMTP
			transport.connect(EMAIL.HOST.getName(), EMAIL.SMTP_USERNAME.getName(), EMAIL.SMTP_PASSWORD.getName());
			
			// Send Mail
			transport.sendMessage(message, message.getAllRecipients());
			
			System.out.println("Email sent Syccessfylly!");
		}
		catch(Exception e)
		{
			System.out.println("Exception sending SES Mail!!!!");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			//Close Connection
			transport.close();
		}
	}
	
}
