package bd;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.Vector;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


/*
 * @author: HT
 * @Date: 12/21/2017
 * @Description: because reading BD's email notifications from Outlook seems not applicable
 * as we need to approval from infosec as Eric provided to be able to run the app on EC2
 * and also running it on Local machine not even working yet
 * 
 * Instead we (NYC) decided to get file infrom direvtly from BD's sftp directly
 * Kees confirmed that they send Email notification for each file they upload to SFTP, 
 * he also confirmed that there is no possibilities that EMail notification sent out
 * while file not sent to SFTP, but vide versa is right, that file uploaded to SFT
 * but no emial notifications, which is good and confirms that we can even trust sftp than 
 * email notifications. also Kees confirmed BD send email notifications ONLY for regular weekly files
 * not adhoc files
 * 
 */
public class ReadBDSftp 
{

	public static void main(String[] args)
	{
		//BD's sftp Info
		
		String hostName = "sftp-opsbank2.elsevier.com";
		String userName = "eei";
		String password = "Eei7240b2C";
		
		
		ChannelSftp  sftpchannel = null;
		
		try
		{
			JSch jsch = new JSch();
			Session session = jsch.getSession(userName, hostName, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			System.out.println("Establishing the connection.....");
			session.connect();
			
			// create sftp channel & connect
			sftpchannel = (ChannelSftp)session.openChannel("sftp");
			sftpchannel.connect();
			 System.out.println("SFTP Channel created.");
			 sftpchannel.cd("data/test");
			byte[] buffer = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(sftpchannel.get("20171221_12345_0_1.zip"));
			File outFile = new File(System.getProperty("user.dir") + "/bdfile.zip");
			if(!(outFile.exists()))
			{
				outFile.createNewFile();
				System.out.println("Outfile: " + outFile + " was created");
			}
			OutputStream out = new FileOutputStream(outFile);
			BufferedOutputStream os = new BufferedOutputStream(out);
			int readCount = 0;
			
			while ((readCount = bis.read(buffer)) >0)
			{
				os.write(buffer,0,readCount);
			}		
			os.close();
			out.close();
			bis.close();
			
			


			// read file's Contents
			String fileContents = IOUtils.toString(sftpchannel.get("20171221_12345_0_1.zip"), "UTF-8"); 
			
			String item = "<item>";
			int lastIndex = 0;
			int recCount = 0;
			while(lastIndex !=-1)
			{
				lastIndex = fileContents.indexOf(item, lastIndex);
				if(lastIndex != -1)
				{
					recCount ++;
					lastIndex += item.length();
				}
			}
			System.out.println("Total Items in file: " + outFile.getName()+ ": " + recCount);
			
			// list files in BD's sftp dir
			
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> fileList = sftpchannel.ls("*.zip");
			System.out.println("List files listed in BD's sftp @" + sftpchannel.pwd());
			for(ChannelSftp.LsEntry entry: fileList)
			{
				System.out.println("SFTP FileName: " + entry.getFilename());
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				if(sftpchannel !=null)
				{
					sftpchannel.exit();
					sftpchannel.disconnect();
				}
			}
			catch(Exception e)
			{
				System.out.println("Failed to close sftpchannel connection!!!!");
				System.out.println("Reason: " + e.getMessage());
				e.printStackTrace();
			}
		}

	}
}
