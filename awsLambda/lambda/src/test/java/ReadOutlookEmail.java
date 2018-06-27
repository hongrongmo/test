
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

/**
 * 
 * @author telebh
 * @Date: 11/15/2017
 * @Descroption: reading outlook email trying to pull BD's notification of weekly
 * BD file's delivery to be able easy compare with actual count after process the files
 * as per Frank request.
 */
public class ReadOutlookEmail 
{

	private static void outlook()
	{
		String protocol = "imaps";
		String host = "outlook.office365.com";
		String username = "h.teleb@science.regn.net";
		String password = "February2012";
		String encryptionType = "tls";
		String port = "993";  
		
		readEMail(protocol,host,username,password,encryptionType,port);
	}
	
	public static void readEMail(String protocol, String hostName, String userName, String pwd, String encryption_type, String portNum)
	{
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", protocol);
		
		if(encryption_type.length() >0)
		{
			//set properties
			if(protocol.equalsIgnoreCase("imaps"))
			{
				props.setProperty("mail.imap.socketFactory.class", "java.net.ssl.SSLSocketFactory");
				props.setProperty("mail.imap.socketFactory.fallback","false");
				props.setProperty("mail.imap.ssl.enable", "true");
				props.setProperty("mail.imap.socketFactory.port", portNum);
				props.setProperty( "mail.imap.starttls.enable", "true");
			}
			
			// read email
			try
			{
				Session session = Session.getInstance(props,null);
				Store store = session.getStore();
				store.connect(hostName, userName,pwd);
				
				Folder [] emailFolders = store.getDefaultFolder().list();
				for(Folder folder: emailFolders)
				{
					System.out.println("Folder Name: " + folder.getFullName());
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String [] args)
	{
		outlook();
	}
}
