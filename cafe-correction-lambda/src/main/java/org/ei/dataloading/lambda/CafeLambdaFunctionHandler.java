package org.ei.dataloading.lambda;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.ei.dataloading.cafe_correction_lambda.CafeCorrectionLambdaHandler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class CafeLambdaFunctionHandler implements RequestHandler<SNSEvent, String> {

	private static final String SYSTEM_ENVIRONMENT_HOST = "HOSTIP";
	private static final String SYSTEM_ENVIRONMENT_CMD = "CMD";
	
	private String privateKey = "Prod.pem";
	private String user = "ec2-user";
	private String host;
	private String cmd;

	
    @Override
    public String handleRequest(SNSEvent event, Context context) {
        context.getLogger().log("Received event: " + event);
        String message = event.getRecords().get(0).getSNS().getMessage();
        context.getLogger().log("From SNS: " + message);
        
     // Get SNS object from the event and pull its contents
		
		 String doc_type = event.getRecords().get(0).getSNS().getMessageAttributes().get("document_type" ).getValue();
     	context.getLogger().log("doc type: " + doc_type);	
     	
     	cmd = System.getenv("CMD");
     	context.getLogger().log("cmd Sys Variable is " + cmd);
     	
     	String command = cmd.substring(0,cmd.indexOf("sh") + 2);
		context.getLogger().log("Command: " + command);
		
		// init env variables 
		
		init();
		// run command
		runCommand(context);  // temp comment out when deploy to Prod bc it raise exception 
		
        return message;
    }
    
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
		
		String envCmd = System.getenv(SYSTEM_ENVIRONMENT_CMD);
		if(envCmd == null)
			envCmd = "nohub /ebs_dev/Hanan/run_lambda.sh";
		if(envCmd.isEmpty())
			throw new NullPointerException("cafe correction Lambda cmd is not set");
		this.cmd = envCmd; 
	}
    
    private void runCommand(Context context)
	{
		try
		{
			context.getLogger().log("Start running command");
			File keyFile = new File(CafeLambdaFunctionHandler.class.getResource(privateKey).toURI());
			String privateKeyAbsolutePath = keyFile.getAbsolutePath();
			
			// create Java secure SSH2 channel
			JSch jsch = new JSch();
			jsch.addIdentity(privateKeyAbsolutePath);
			Session session = jsch.getSession(user, host, 22);
			context.getLogger().log("ssh session created.");
			
	        java.util.Properties config = new java.util.Properties();
	        //config.put("StrictHostKeyChecking", "no");  //bypass authenticity of
	        session.setConfig(config);
	    	// ssh connect
	        session.connect();

		
			context.getLogger().log("ssh session connected, about to run " + cmd);
			
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
		catch(Exception e) 
		{
			context.getLogger().log("Exception was thrown in runCommand");
			context.getLogger().log(e.getMessage());
			e.getCause();
			e.printStackTrace();
		}
	}
    
}
