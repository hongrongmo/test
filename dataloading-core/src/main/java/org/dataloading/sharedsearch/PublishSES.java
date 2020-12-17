package org.dataloading.sharedsearch;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;

// JavaMail libraries. Download the JavaMail API 
// from https://javaee.github.io/javamail/
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

// AWS SDK libraries. Download the AWS SDK for Java 
// from https://aws.amazon.com/sdk-for-java
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;




import org.ei.dataloading.aws.AmazonSESService;

import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;

import com.amazonaws.services.simpleemail.model.SendEmailRequest;



/**
 * 
 * @author TELEBH
 * @Date: 11/13/2020
 * @Description: Send SES service for dataloading adhoc requests (i.e. CAFE-BD Gap)
 */
public class PublishSES {
	private String ses_arn = "";

	//Sender & receiver email
	static final String SENDER = " Hanan Teleb <h.teleb@elsevier.com>";
	//static final String TO = "J.Salk@elsevier.com";
	static final String RECEIPIENT = "h.teleb@elsevier.com";
	
	//ConfigurationSet
	static final String CONFIGSET = "ConfigSet";
	
	//Subject
	static final String SUBJECT = "CAFE_BD-GAP";
	static final String BODY_HTML = "<html>"
			+ " <head></head>"
			+ "<body>"
			+ " <h4> Hi Judy and TM,</h4>"
			+ "<p>Please see the attached file for CAFE BD GAP as per today's date: " 
			+ " Including the very first 1000 records as a reference for further checking</p>"
			+ "</br></br></br>"
			+ "Thanks,"
			+"</br>"
			+"'Hanan"
			+ "</body>"
			+ "</html>";
	
	
	static final String BODY_TXT = "Hi Judy and TM, \r\n" + 
			"Please see the attached file for CAFE BD GAP as per today's date: " 
			+ " Including the very first 1000 records as a reference for further checking\r\n\n\n"
			+ "Thanks,\r\n"
			+ "Hanan";
	
	public void publishSESMessage()
	{
		try
		{
			//Get AMazon SE cleint
			AmazonSimpleEmailService client = AmazonSESService.getInstance().getAmazonSESClient();
			
			SendEmailRequest request = new SendEmailRequest()
					.withDestination(new Destination().withToAddresses(RECEIPIENT))
					.withMessage(new com.amazonaws.services.simpleemail.model.Message().withSubject(new Content().withCharset("UTF-8").withData(SUBJECT))
							.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(BODY_HTML))))
					.withSource(SENDER);
			
			client.sendEmail(request);
			System.out.println("Email Sent");
		}
		catch(Exception e)
		{
			System.out.println("Exception happened sending email!");
			System.out.println("Reason: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/* Sending Raw SES EMail with attachment using AWS SES API */
	
	public void publishRawSESMessageWithAttachment(String fileName1) throws AddressException, javax.mail.MessagingException, IOException
	{
		//System.out.println("FIleName: " + fileName1 + " , " + fileName2);
		System.out.println("FIleName: " + fileName1);
		Session session = Session.getDefaultInstance(new Properties());
		
		//Create a new MimeMessage object
		MimeMessage message = new MimeMessage(session);
		
		
		// Set Subject, From and To
		message.setSubject(SUBJECT, "UTF-8");
		message.setFrom(new InternetAddress(SENDER));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RECEIPIENT));
		
		// Create a multipart/alternative child container
		MimeMultipart message_body = new MimeMultipart("alternative");
		
		//Wrapper for HTML and TXT Parts
		MimeBodyPart wrap = new MimeBodyPart();
		
		//Text Part for message body
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(BODY_TXT, "text/plain; charset=UTF-8");
		
		// HTML Part for message body
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(BODY_HTML,"text/html; charset=UTF-8");
		
		//Add text and html part to the child container
		message_body.addBodyPart(textPart);
		message_body.addBodyPart(htmlPart);
		
		//Add child container to wrapper
		wrap.setContent(message_body);
		
		//Create multipart/mixed parent container
		MimeMultipart parentMessage = new MimeMultipart("mixed");
		
		//Add parent container to the message
		message.setContent(parentMessage);
		
		//Add multipart/alternative warpper to the message
		parentMessage.addBodyPart(wrap);
		
		//Add Attachment1
		MimeBodyPart attachment1 = new MimeBodyPart();
		//DataSource ds = new FileDataSource("C:\\github\\github\\engvillage-dataloading\\dataloading-core\\BD-Cafe-Gap_20201118.csv");
		DataSource ds1 = new FileDataSource(fileName1);
		attachment1.setDataHandler(new DataHandler(ds1));
		attachment1.setFileName(ds1.getName());
		
		
		//Add Attachment2 
		
		/*
		  MimeBodyPart attachment2 = new MimeBodyPart(); 
		  DataSource ds2 = new FileDataSource(fileName2); 
		  attachment2.setDataHandler(new DataHandler(ds2));
		  attachment2.setFileName(ds2.getName());
		 */
		
		
		//Add attachment to the message
		parentMessage.addBodyPart(attachment1);
		//parentMessage.addBodyPart(attachment2);
		
		//Send email
		try
		{
			System.out.println("Send Raw email through AWS SES including attachment.... ");
			
			// Get Amazon SES client
			AmazonSimpleEmailService client = AmazonSESService.getInstance().getAmazonSESClient();
			 
			//Print raw email content to console ONLY for testing
			//PrintStream out = System.out;
			//parentMessage.writeTo(out);
			
			//Send the email
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.writeTo(outputStream);
			RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
			
			//EmailMessageRequest
			SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
			
			//Send RawEmail
			client.sendRawEmail(rawEmailRequest);
			System.out.println("Email successfully sent!");
		}
		catch(Exception e)
		{
			System.out.println("Exception happened sending email!");
			System.out.println("Reason: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
