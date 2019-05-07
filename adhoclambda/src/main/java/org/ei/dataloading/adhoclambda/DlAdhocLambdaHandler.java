package org.ei.dataloading.adhoclambda;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class DlAdhocLambdaHandler implements RequestHandler<S3Event, String> {
    
	private static final String SYSTEM_ENVIRONMENT_HOST = "hostIp";	
	private static final String SYSTEM_ENVIRONMENT_CMD = "cmd";
	
	private String privateKey = "prod.pem";
	private String user="ec2-user";
	private String host;
	private String cmd;
	

    public DlAdhocLambdaHandler() {
    	
    }
    
	protected void init() {
		String envHost = System.getenv(SYSTEM_ENVIRONMENT_HOST);
		if (envHost == null) {
			// for testing only
			envHost = "10.178.163.167";
		}
		if (envHost == null || envHost.isEmpty())
		{
			throw new NullPointerException("lambda environment host is not set");
		}
		this.host = envHost;
		
		String envCmd = System.getenv(SYSTEM_ENVIRONMENT_CMD);
		if (envCmd == null) {
			// for testing only
			envCmd = "cd /ebs_dev/Aaron; pwd; ls -ltr";
		}
		if (envCmd == null || envCmd.isEmpty())
		{
			throw new NullPointerException("lambda environment cmd is not set");
		}
		this.cmd = envCmd;
	}

    @Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Received event from S3 bucket: " + event);
        
        // Get the lambda env values
        init();
        
        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        Path p = Paths.get(key);
        String fileUploaded = p.getFileName().toString();
        try {
        	context.getLogger().log("Received file from bucket " + bucket + " with file name of " + fileUploaded);
        	if (!fileUploaded.endsWith(".zip")) 
        	{
        		context.getLogger().log(fileUploaded + " is not a zip file.  Exiting lambda.");
        	}
        	else
        	{
        		// need to add file uploaded after script
        		String firstPartCmd = cmd.substring(0, cmd.indexOf(".sh") + 3);
        		String secondPartCmd = cmd.substring(cmd.indexOf(".sh") + 3);
        		firstPartCmd += " " + fileUploaded;
        		cmd = firstPartCmd + secondPartCmd;
                runCommand(context);
        	}
            return "Done running dl adhoc lambda";
        } catch (Exception e) {
        	context.getLogger().log("An exception was thrown in handleRequest: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private void runCommand(Context context) {
        try
        {
        	context.getLogger().log("Retrieving key info");
        	File file = new File( DlAdhocLambdaHandler.class.getResource( privateKey ).toURI() );
            String privateKeyabsolutePath = file.getAbsolutePath();
            
        	JSch jsch = new JSch();    	
            jsch.addIdentity(privateKeyabsolutePath);
            Session session = jsch.getSession(user, host, 22);
            context.getLogger().log("ssh session created.");
            
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            session.connect();
            context.getLogger().log("ssh session connected.  Getting ready to run " + cmd);
            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(cmd);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
            channel.setOutputStream(System.out);
            InputStream in=channel.getInputStream();
            channel.connect();
            context.getLogger().log("ssh shell channel created.");
	        byte[] tmp=new byte[1024];
	        while(true){
	          while(in.available()>0){
	            int i=in.read(tmp, 0, 1024);
	            if(i<0)
	            	break;
	            context.getLogger().log("command output: " + new String(tmp, 0, i));
	          }
	          if(channel.isClosed()){
	            context.getLogger().log("ssh exit-status: "+channel.getExitStatus());
	            break;
	          }
	          try{
	        	  Thread.sleep(1000);
	          }catch(Exception ee){
	        	  System.out.println("An error has occurred");
	          }
	        }
	        channel.disconnect();	        
	        session.disconnect();          
	        context.getLogger().log("Disconnected from SSH session.");           
        }
        catch (Exception e)
        {
        	context.getLogger().log("An exception was thrown in runCommand: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}