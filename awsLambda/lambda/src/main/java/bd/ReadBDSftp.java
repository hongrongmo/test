package bd;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;

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
		long mtime;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd-yy");
		List<String> sftpFilesList = new ArrayList<String>();


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
			sftpchannel.cd("/data");

			//***** Comment below part as it was only for testing, uncomment when needed ******/

			/*
			sftpchannel.cd("data/72");
			byte[] buffer = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(sftpchannel.get("20180621_282_240_1.zip"));
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

			 */


			// get current date to compare with BD's files' date to check if file age older than 8 days
			Instant now = Instant.now();
			Long current = now.getMillis();
			
			// list files in BD's sftp dir

			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> dirList = sftpchannel.ls("*");

			// loop through subdir, and for each subdir list the files it contains
			for(ChannelSftp.LsEntry subdir: dirList)
			{
				// exclude some dir that we do not need to check thier files as they are meant for adhoc projects

				if(!(subdir.getFilename().equalsIgnoreCase("cpxback") || subdir.getFilename().equalsIgnoreCase("geoback") || subdir.getFilename().equalsIgnoreCase("cpxreindex") ||
						subdir.getFilename().equalsIgnoreCase("CONFQC") || subdir.getFilename().equalsIgnoreCase("chemback") || subdir.getFilename().equalsIgnoreCase("cpxbulkpool")||
						subdir.getFilename().equalsIgnoreCase("mai")))
				{
					sftpchannel.cd("/data/" + subdir.getFilename());

					Vector<ChannelSftp.LsEntry> fileList = sftpchannel.ls("*.zip");
					System.out.println("List files listed in BD's sftp @" + sftpchannel.pwd());
					for(ChannelSftp.LsEntry entry: fileList)
					{
						//System.out.println("SFTP FileName: " + entry.getFilename());		// for debugging
						mtime = entry.getAttrs().getMTime();
						//Date ftimestamp = dateFormat.parse(entry.getAttrs().getMtimeString());
						Long milli = current - mtime;
						int seconds = (int) (milli / 1000000);
						
						int hours = seconds / 3600;
						/*long diff = TimeUnit.DAYS.convert(milli, TimeUnit.DAYS);
						long diffDays = (diff) / (24 * 60 * 60 * 1000);
						System.out.println("diff days: " + diff);*/
						//long diffDays = (current_epoch - mtime) / (24 * 60 * 60 * 1000);
						
						sftpFilesList.add(entry.getFilename());
					
							System.out.println("File: " + entry.getFilename() + " timestamp is older than 8 days, so skip it");
					}
				}

			}

			//list all file's in BD's sftp "/data" sub-directories
			System.out.println("BD's sftp files list: ");
			for(String file: sftpFilesList)
			{
				System.out.println(file);
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
