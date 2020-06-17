package org.ei.dataloading.correction.lambda.knovel;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


/**
 * 
 * @author TELEBH
 * @Date: 05/13/2020
 * @Description, Automatically Triggers monthly Knovel Correction after download is complete
 * 
 *original package was: //package org.ei.dataloading.dl_correction_lambda.knovel;
 */

public class KnovelCorrectionLambdaHandler implements RequestHandler<SNSEvent, String> {
	
	private static final String SYSTEM_ENVIRONMENT_HOST = "hostIp";
	private static final String SYSTEM_ENVIRONMENT_CMD = "cmd";
	
	private String privateKey = "Prod.pem";
	private String user = "ec2-user";
	private String host;
	private String cmd;
	private String scriptName;
	private String home_dir;
	
	private String database;
	private String fileName;
	private String action;
	private String s3BucketName;
	private String s3BucketPath;
	private String loadnumber;

	
	protected void init()
	{
		String envHost = System.getenv(SYSTEM_ENVIRONMENT_HOST);
		if(envHost == null)
			envHost = "10.178.163.190";
		if(envHost.isEmpty())
		{
			throw new NullPointerException(database + " correction Lambda hostIP is not set");
		}
		this.host = envHost;
		System.out.println("HostIP: " + host);
		
		String envCmd = System.getenv(SYSTEM_ENVIRONMENT_CMD);
		if(envCmd == null)
			envCmd = "cd /ebs_scratch/New_TM_eilib_build_Hanan/Current_weekly_conv/knovel; pwd; ls -ltr";
		if(envCmd.isEmpty())
			throw new NullPointerException(database + " correction Lambda cmd is not set");
		
		this.cmd = envCmd; 
		System.out.println("Cmd: " + this.cmd);
		
		
	}
	

	public String handleRequest(SNSEvent event, Context context)
	{
		init();
		
		context.getLogger().log("Received " + event.toString() + " correction SNS event");
		String timestamp = new SimpleDateFormat("MM-dd-yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
		context.getLogger().log("SNS request received at : " + timestamp);
		
		// Get SNS object from the event and pull its contents
		context.getLogger().log(event.getRecords().get(0).getSNS().getMessage());
		database= event.getRecords().get(0).getSNS().getMessageAttributes().get("database").getValue();
		fileName = event.getRecords().get(0).getSNS().getMessageAttributes().get("filename").getValue();
		action= event.getRecords().get(0).getSNS().getMessageAttributes().get("action").getValue();
		s3BucketName = event.getRecords().get(0).getSNS().getMessageAttributes().get("s3bucket").getValue();
		s3BucketPath= event.getRecords().get(0).getSNS().getMessageAttributes().get("s3path").getValue();
		loadnumber = event.getRecords().get(0).getSNS().getMessageAttributes().get("loadnumber").getValue();
		
		//Current Month for log filename
		Calendar cal = Calendar.getInstance();
		String logFileName = cal.get(Calendar.MONTH)+ "_" + String.valueOf(cal.get(Calendar.YEAR));
		
		
		context.getLogger().log("Received SNS topic for " + database + " , filename: " + fileName
				+ ", running: " + action + " for current Week's loadnumber: " + loadnumber);
		
		
		try
		{
			// check if it is valid archive date 
			if(!(database.isEmpty()) && database.equalsIgnoreCase("knovel") && 
					!(action.isEmpty()) && action.equalsIgnoreCase("update") && !(s3BucketName.isEmpty()) &&
							s3BucketName.equalsIgnoreCase("ev-data") && !(s3BucketPath.isEmpty()) && s3BucketPath.equalsIgnoreCase("archive/KNOVEL/")
			)
			{
				context.getLogger().log("cmd Sys Variable is " + cmd);
		     	
		     	//String command = cmd.substring(0,cmd.indexOf("sh") + 2);
				context.getLogger().log("Command: " + cmd);
				
				
					home_dir = cmd.substring(cmd.indexOf('/'), cmd.length()).replaceAll("\"", "");
				
					context.getLogger().log("homeDir: " + home_dir);
					scriptName = "knovel_operations_all.sh";
					
					//./knovel_operations_all.sh group-2020-02.xml update ev-data archive/KNOVEL/ 202013 |tee -a ./processed/202013-group-2020-02.txt
					cmd = cmd + "; ./" + scriptName + " " + fileName + " "+ action + " " + s3BucketName + " " + s3BucketPath + " " + loadnumber + " >./processed/knovel_" + logFileName + " 2>&1";

				}
			else
			{
				System.out.println("Something is missing, check all parameters in command, exit!!!!");
				System.out.println("Command: " + cmd);
				System.exit(1);
			}
				
				runCommand(context);
				context.getLogger().log("Processing " + database + " correction by running the command: " + cmd);
			
			return "Done running " + database + " correction lambda";
		}
		catch(Exception e)
		{
			context.getLogger().log("An exception was thrown in " + database  + " correction handleRequest");
			e.printStackTrace();
			throw e;
		}
		
	}

	
	
	private void runCommand(Context context)
	{
		try
		{
			context.getLogger().log("Start running command: " + cmd);
			File keyFile = new File(KnovelCorrectionLambdaHandler.class.getResource(privateKey).toURI());
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
						
			
			// 1. source .bash_profile
			// source .bash_profile before running correction in order to capture recent week#
			ChannelExec channelExec0 = (ChannelExec)session.openChannel("exec");
			channelExec0.setCommand("source ~/.bash_profile");
			channelExec0.connect();
			// close connection
			channelExec0.disconnect();
			
			// source .bash_profile before running correction in order to capture new update#, otherwise prev update# still cached
			ChannelExec channelExec4 = (ChannelExec)session.openChannel("exec");
			channelExec4.setCommand("pwd");
			channelExec4.connect();
			channelExec4.disconnect();
						
						
			
			
			//2. run Knoel correction command
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
			context.getLogger().log("Error Message:" + e.getMessage());
			e.printStackTrace();
		}
	}

	
}
