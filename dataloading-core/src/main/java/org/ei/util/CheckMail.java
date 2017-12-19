package org.ei.util;
 
/*
 *  This is the code for read the unread gmail from your mail account.
 *  your gmail need to use less secure sign-in technology
 *
 */
import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.search.FlagTerm;
 
public class CheckMail
{
	 Folder inbox;
	 
	 //Constructor of the calss.
	 public CheckMail(String emailaddress,String password)
	 {
		 /*  Set the mail properties  */
		 Properties props = System.getProperties();
		 props.setProperty("mail.store.protocol", "imaps");
		 //props.setProperty("mail.debug", "true");
		 try
		 {
			 /*  Create the session and get the store for read the mail. */
			 Session session = Session.getDefaultInstance(props, null);
			 Store store = session.getStore("imaps");
			 store.connect("imap.gmail.com","evdataloading", "230Nycparkave");
			 //store.connect("imap.gmail.com",emailaddress, password);
			 
			 /*  Mention the folder name which you want to read. */
			 inbox = store.getFolder("Inbox");
			 System.out.println("No of Unread Messages : " + inbox.getUnreadMessageCount());
			 
			 /*Open the inbox using store.*/
			 inbox.open(Folder.READ_ONLY);
			 
			 /*  Get the messages which is unread in the Inbox*/
			 Message messages[] = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
			 
			 /* Use a suitable FetchProfile    */
			 FetchProfile fp = new FetchProfile();
			 fp.add(FetchProfile.Item.ENVELOPE);
			 fp.add(FetchProfile.Item.CONTENT_INFO);
			 inbox.fetch(messages, fp);
			 
			 try
			 {
				 printAllMessages(messages);
				 inbox.close(true);
				 store.close();
			 }
			 catch (Exception ex)
			 {
				 System.out.println("Exception arise at the time of read mail");
				 ex.printStackTrace();
			 }
		 }
		 catch (NoSuchProviderException e)
		 {
			 e.printStackTrace();
			 System.exit(1);
		 }
		 catch (MessagingException e)
		 {
			 e.printStackTrace();
			 System.exit(2);
		 }
	 }
	 
	 public void printAllMessages(Message[] msgs) throws Exception
	 {
		 for (int i = 0; i < msgs.length; i++)
		 {
			 //System.out.println("MESSAGE #" + (i + 1) + ":");
			 printEnvelope(msgs[i]);
		 }
	 }
	 
	 /*  Print the envelope(FromAddress,ReceivedDate,Subject)  */
	 public void printEnvelope(Message message) throws Exception
	 {
		 Address[] a;
		 /*
		 // FROM
		 if ((a = message.getFrom()) != null)
		 {
			 for (int j = 0; j < a.length; j++)
			 {
				 System.out.println("FROM: " + a[j].toString());
			 }
		 }
		 // TO
		 if ((a = message.getRecipients(Message.RecipientType.TO)) != null)
		 {
			 for (int j = 0; j < a.length; j++)
			 {
			 System.out.println("TO: " + a[j].toString());
			 }
		 }
		 */
		 String subject = message.getSubject();
		 Date receivedDate = message.getReceivedDate();
		 String content = message.getContent().toString();
		 if(subject.indexOf("Files for order")>-1)
		 {
			 //System.out.println("Subject : " + subject);
			 if(content.indexOf("Engineering Information")>-1)
			 {
				 //System.out.println("----------------------------------------------------------------");
				 String[] contentArray = content.split("\n");
				 for(int i=0;i<contentArray.length;i++)
				 {
					 if(i>7)
					 {
						 if(contentArray[i].indexOf("With best regards,")<0 && 
							contentArray[i].indexOf("Elsevier Bibliographic Databases Operations")<0 && 
							contentArray[i].indexOf("*** External email: use caution ***")<0 && 
							contentArray[i].indexOf("System generated mail. Do not reply.")<0 )
						 {
							 System.out.println(contentArray[i]);
						 }
					 }
				 }
				 System.out.println("----------------------------------------------------------------");
			 }
		 }
		 //System.out.println("Received Date : " + receivedDate.toString());
		 //System.out.println("Content : " + content);
		 //getContent(message);
	 }
	 
	 public void getContent(Message msg)
	 {
		 try
		 {
			 String contentType = msg.getContentType();
			 System.out.println("Content Type : " + contentType);
			 Multipart mp = (Multipart) msg.getContent();
			 int count = mp.getCount();
			 for (int i = 0; i < count; i++)
			 {
				 dumpPart(mp.getBodyPart(i));
			 }
		 }
		 catch (Exception ex)
		 {
			 System.out.println("Exception arise at get Content");
			 ex.printStackTrace();
		 }
	 }
	 
	 public void dumpPart(Part p) throws Exception
	 {
		 // Dump input stream ..
		 InputStream is = p.getInputStream();
		 // If "is" is not already buffered, wrap a BufferedInputStream
		 // around it.
		 if (!(is instanceof BufferedInputStream))
		 {
			 is = new BufferedInputStream(is);
		 }
		 int c;
		 System.out.println("Message : ");
		 while ((c = is.read()) != -1)
		 {
			 System.out.write(c);
		 }
	 }
	 
	 public static void main(String args[])
	 {
		 String emailaddress = args[0];
		 String password = args[1];
		 new CheckMail(emailaddress,password);
	 }
}

/*
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
 

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
//import javax.mail.MailSSLSocketFactory;
 
public class CheckMail {
 
    public static void main(String[] args) {
 
        CheckMail gmail = new CheckMail();
        String emailaddress = args[0];
        String password = args[1];
        gmail.read(emailaddress,password);
 
    }
 
    public void read(String emailaddress, String password) {
 
        Properties props = new Properties();
 
        try {
            //props.load(new FileInputStream(new File("C:\\smtp.properties")));
        	
        	props.put("mail.transport.protocol", "smtp");
        	props.put("mail.smtp.host", "smtp.gmail.com");
        	props.put("mail.smtp.auth", "true");
        	props.put("mail.smtp.starttls.enable", "true");
        	props.put("mail.smtp.starttls.required", "true");
        	props.put("mail.smtp.port","465");
        	props.put("mail.smtp.socketFactory.port","465");
        	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        	
        	//MailSSLSocketFactory sf = new MailSSLSocketFactory();
        	//sf.setTrustAllHosts(true);
        	props.setProperty("mail.imap.host", "imap.gmail.com");
    	    props.setProperty("mail.imap.user", emailaddress);
    	    props.setProperty("mail.imap.password", password);
    	    props.setProperty("mail.imap.port", "993");
    	    props.setProperty("mail.imap.auth", "true");
    	    props.setProperty("mail.imap.starttls.enable", "true");
    	    props.put("mail.imap.starttls.enable", "true");
    	    //props.put("mail.imap.ssl.socketFactory", sf);
    	    props.setProperty("mail.debug", "true");
        	
            Session session = Session.getDefaultInstance(props, null);
 
            Store store = session.getStore("imaps");
            System.out.println("EMAILADDRESS " +emailaddress);
            System.out.println("PASSWORD " +password);
            store.connect("imap.gmail.com",emailaddress, password);
 
            Folder inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);
            int messageCount = inbox.getMessageCount();
 
            System.out.println("Total Messages:- " + messageCount);
 
            Message[] messages = inbox.getMessages();
            System.out.println("------------------------------");
 
            for (int i = 0; i < 10; i++) {
                System.out.println("Mail Subject:- " + messages[i].getSubject());
            }
 
            inbox.close(true);
            store.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
}
*/
