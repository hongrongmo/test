package org.ei.dataloading.cafe_correction_lambda;

import com.amazonaws.services.sns.*;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelExec;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;


/*
 * @author: telebh
 * @Date: 08/13/2019
 * @Description: Invoke Lambda Function via Amazon SNS to 
 */
public class CafeCorrectionLambdaHandler implements RequestHandler<SNSEvent,String>
{

	private static final String SYSTEM_ENVIRONMENT_HOST = "hostIp";
	private static final String SYSTEM_ENVIRONMENT_CMD = "cmd";
	
	private String privateKey = "Prod.pem";
	private String user = "ec2-user";
	private String host;
	private String cmd;
	private String scriptName;
	private String home_dir;
	private String doc_type;
	private String archive_date;
	private boolean isZipFileExist;
	private String corrArchiveDateFileName;
	private String dayOfWeek;
	
	protected void init()
	{
		String envHost = System.getenv(SYSTEM_ENVIRONMENT_HOST);
		if(envHost == null)
			envHost = "10.178.165.161";
		if(envHost.isEmpty())
		{
			throw new NullPointerException("Cafe Correction Lambda hostIP is not set");
		}
		this.host = envHost;
		System.out.println("HostIP: " + host);
		
		String envCmd = System.getenv(SYSTEM_ENVIRONMENT_CMD);
		if(envCmd == null)
			envCmd = "cd /ebs_dev/Hanan; pwd; ls -ltr";
		if(envCmd.isEmpty())
			throw new NullPointerException("cafe correction Lambda cmd is not set");
		
		this.cmd = envCmd; 
		
		Date currDate = new Date();
		
	}
	
	public String handleRequest(SNSEvent event, Context context)
	{
		init();
		context.getLogger().log("Received Cafe Correction SNS event");
		String timestamp = new SimpleDateFormat("MM-dd-yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
		context.getLogger().log("Received Cafe download SNS request at : " + timestamp);
		
		
		// Get SNS object from the event and pull its contents
		context.getLogger().log(event.getRecords().get(0).getSNS().getMessage());
		doc_type= event.getRecords().get(0).getSNS().getMessageAttributes().get("document_type").getValue();
		archive_date = event.getRecords().get(0).getSNS().getMessageAttributes().get("archive_date").getValue();
		context.getLogger().log("isZipFileExist : "+ event.getRecords().get(0).getSNS().getMessageAttributes().get("isZipFileExist").getValue());
		isZipFileExist = Boolean.parseBoolean(event.getRecords().get(0).getSNS().getMessageAttributes().get("isZipFileExist").getValue());
		
		context.getLogger().log("Received SNS topic for doc_type: " + doc_type + ", archive_date: " + archive_date
				+ ", Checkd if zipfile exist: " + isZipFileExist);
		
		
		try
		{
			// check if it is valid archive date 
			if(isValidArchiveDate() && (doc_type.equalsIgnoreCase("ani") || doc_type.equalsIgnoreCase("apr") || doc_type.equalsIgnoreCase("ipr"))
					 && isZipFileExist)
			{
				context.getLogger().log("cmd Sys Variable is " + cmd);
		     	
		     	String command = cmd.substring(0,cmd.indexOf("sh") + 2);
				context.getLogger().log("Command: " + command);
				
				
				home_dir = cmd.substring(cmd.indexOf('/'), cmd.length());
				
				if(doc_type.contentEquals("ani"))
				{
					scriptName = "all_ani_corrections.sh";
					
					cmd = cmd + ";nohup ./" + scriptName + " > " + home_dir + "/processed/ani_conv_"+ archive_date + ".txt 2>&1";
					
					corrArchiveDateFileName = "ani_corr_list.txt";
				}
				else if(doc_type.equalsIgnoreCase("apr"))
				{
					scriptName = "all_apr_corrections.sh";
					cmd = cmd + ";nohup ./" + scriptName + " > " + home_dir+ "/processed/apr_conv_" + archive_date + ".txt 2>&1";
					context.getLogger().log("apr");
					
					corrArchiveDateFileName = "apr_corr_list.txt";
				}
				else if(doc_type.equalsIgnoreCase("ipr"))
				{
					scriptName = "all_ipr_corrections.sh";
					cmd = cmd + ";nohup ./" + scriptName + " > " + home_dir + "/processed/ipr_conve_" + archive_date + ".txt 2>&1";
					context.getLogger().log("ipr");
					
					corrArchiveDateFileName = "ipr_corr_list.txt";
				}
				
				runCommand(context);
				context.getLogger().log("Processing " + doc_type + " correction by running the command: " 
						+ cmd);
			}
			
			return "Done running " + doc_type + " correction lambda";
		}
		catch(Exception e)
		{
			context.getLogger().log("An exception was thrown in " + doc_type  + " correction handleRequest");
			e.printStackTrace();
			throw e;
		}
		
	}

	private boolean isValidArchiveDate()
	{
		SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yy");
		try
		{
			format.parse(archive_date);
		}
		catch(ParseException ex)
		{
			return false;
		}
		return true;
		
	}
	
	private void runCommand(Context context)
	{
		try
		{
			context.getLogger().log("Start running command: " + cmd);
			File keyFile = new File(CafeCorrectionLambdaHandler.class.getResource(privateKey).toURI());
			context.getLogger().log("privateKeyFile: " + keyFile);
			String privateKeyAbsolutePath = keyFile.getAbsolutePath();
			
			// create Java secure SSH2 channel
			JSch jsch = new JSch();
			jsch.addIdentity(privateKeyAbsolutePath);
			Session session = jsch.getSession(this.user, this.host, 22);
			context.getLogger().log("ssh session created.");
			
			Properties config = new Properties();
			config.setProperty("StrictHostKeyChecking", "no");  //bypass authenticity of the host
			session.setConfig(config);
			
			// ssh connect
			session.connect();
			context.getLogger().log("ssh session connected, about to run " + cmd);
			
			// get updatenumber from shell script file
			
			ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
			InputStream instr = channelExec.getInputStream();
			channelExec.setCommand("grep -E \"loadnum=\" " + home_dir + "/" + scriptName + "| grep -v \"#loadnum\"");
			channelExec.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(instr));
			String str;
			if(( str = br.readLine()) !=null)
			{
				context.getLogger().log("gerp command output: " + str);
			}
			
			// increase updatenumber by one and update correction script

			int origUpdateNum = (Integer.parseInt(str.substring(str.indexOf("=")+1,str.trim().length())));
			int tempUpdateNum = (Integer.parseInt(str.substring(str.indexOf("=")+1,str.trim().length() - 1)));
			tempUpdateNum ++;
			String newUpdateNum  = tempUpdateNum + "1"; 
			// append 1 to new loadnum
			
			context.getLogger().log("Current updatenum: " + origUpdateNum);	
			context.getLogger().log("New updatenum to be used: " + newUpdateNum);
			
			ChannelExec channelExec2 = (ChannelExec)session.openChannel("exec");
			
			channelExec2.setCommand("sed -i -e \"/loadnum=" + origUpdateNum + "/s//loadnum=" + newUpdateNum + "/g\" " + home_dir + "/" + scriptName);
			channelExec2.connect();
			
			// set the next corr_date for the correction
			
			ChannelExec channelExec3 = (ChannelExec)session.openChannel("exec");
			channelExec3.setCommand("sed -i \"1s/.*/" + archive_date  +" /\" " + home_dir + "/" + corrArchiveDateFileName);
			channelExec3.connect();
			
			
			// set the shell script command to run in the ssh session
			Channel channel = session.openChannel("exec");
			((ChannelExec)channel).setCommand(cmd);
			channel.setInputStream(null);
			((ChannelExec)channel).setErrStream(System.err);
			channel.setOutputStream(System.out);
			InputStream in = channel.getInputStream();
			channel.connect();
			context.getLogger().log("ssh shell channel created");
			
			// get the command output
			byte[] cmdout = new byte[1024];
			while(true)
			{
				while(in.available() > 0)
				{
					int i = in.read(cmdout,0,1024);
					if(i<0)
						break;
					context.getLogger().log("Command output: " + new String(cmdout,0,i));
				}
				if(channelExec2.isClosed())
				{
					context.getLogger().log("setting updatenum in correction script completed with exit status: " + channelExec2.getExitStatus());;
					break;
				}
				
				if(channelExec3.isClosed())
				{
					context.getLogger().log("setting corr archive_date in correction text file completed with exit status: " + channelExec3.getExitStatus());;
					break;
				}
				
				if(channel.isClosed())
				{
					context.getLogger().log("ssh completed with exit status: " + channel.getExitStatus());;
					break;
				}
				try
				{
					// sleep for few seconds before disconnecting the ssh  session
					Thread.sleep(2000);
				}
				catch(Exception e)
				{
					System.out.println("Error occurred closing ssh session!!");
				}
				
			}
			
			
			channelExec.disconnect();
			channelExec2.disconnect();
			channelExec3.disconnect();
			channel.disconnect();
			session.disconnect();
			context.getLogger().log("Disconnected from ssh session");
			
			
		}
		catch(JSchException ex)
		{
			context.getLogger().log("JschException: " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			context.getLogger().log("Exception was thrown in runCommand");
			e.printStackTrace();
		}
	}
}
