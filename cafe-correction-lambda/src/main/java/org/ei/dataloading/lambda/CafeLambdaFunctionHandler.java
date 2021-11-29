package org.ei.dataloading.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public class CafeLambdaFunctionHandler implements RequestHandler<SNSEvent, String> {
  private static final String SYSTEM_ENVIRONMENT_HOST = "HOSTIP";
  
  private static final String SYSTEM_ENVIRONMENT_CMD = "CMD";
  
  private String privateKey = "Prod.pem";
  
  private String user = "ec2-user";
  
  private String host;
  
  private String cmd;
  
  public String handleRequest(SNSEvent event, Context context) {
    context.getLogger().log("Received event: " + event);
    String message = ((SNSEvent.SNSRecord)event.getRecords().get(0)).getSNS().getMessage();
    context.getLogger().log("From SNS: " + message);
    String doc_type = ((SNSEvent.MessageAttribute)((SNSEvent.SNSRecord)event.getRecords().get(0)).getSNS().getMessageAttributes().get("document_type")).getValue();
    context.getLogger().log("doc type: " + doc_type);
    this.cmd = System.getenv("CMD");
    context.getLogger().log("cmd Sys Variable is " + this.cmd);
    String command = this.cmd.substring(0, this.cmd.indexOf("sh") + 2);
    context.getLogger().log("Command: " + command);
    init();
    runCommand(context);
    return message;
  }
  
  protected void init() {
    String envHost = System.getenv("HOSTIP");
    if (envHost == null)
      envHost = "10.178.165.161"; 
    if (envHost.isEmpty())
      throw new NullPointerException("Cafe Correction Lambda hostIP is not set"); 
    this.host = envHost;
    String envCmd = System.getenv("CMD");
    if (envCmd == null)
      envCmd = "nohub /ebs_dev/Hanan/run_lambda.sh"; 
    if (envCmd.isEmpty())
      throw new NullPointerException("cafe correction Lambda cmd is not set"); 
    this.cmd = envCmd;
  }
  
  private void runCommand(Context context) {
    try {
      context.getLogger().log("Start running command");
      File keyFile = new File(CafeLambdaFunctionHandler.class.getResource(this.privateKey).toURI());
      String privateKeyAbsolutePath = keyFile.getAbsolutePath();
      JSch jsch = new JSch();
      jsch.addIdentity(privateKeyAbsolutePath);
      Session session = jsch.getSession(this.user, this.host, 22);
      context.getLogger().log("ssh session created.");
      Properties config = new Properties();
      config.put("StrictHostKeyChecking", "yes");
      session.setConfig(config);
      session.connect();
      context.getLogger().log("ssh session connected, about to run " + this.cmd);
      Channel channel = session.openChannel("exec");
      ((ChannelExec)channel).setCommand(this.cmd);
      channel.setInputStream(null);
      ((ChannelExec)channel).setErrStream(System.err);
      channel.setOutputStream(System.out);
      InputStream in = channel.getInputStream();
      channel.connect();
      context.getLogger().log("ssh shell channel created");
      byte[] cmdout = new byte[1024];
      while (true) {
        if (in.available() > 0) {
          int i = in.read(cmdout, 0, 1024);
          if (i >= 0) {
            context.getLogger().log("Command output: " + new String(cmdout, 0, i));
            continue;
          } 
        } 
        if (channel.isClosed()) {
          context.getLogger().log("ssh completed with exit status: " + channel.getExitStatus());
          break;
        } 
        try {
          Thread.sleep(2000L);
        } catch (Exception e) {
          System.out.println("Error occurred closing ssh session!!");
        } 
      } 
      channel.disconnect();
      session.disconnect();
      context.getLogger().log("Disconnected from ssh session");
    } catch (Exception e) {
      context.getLogger().log("Exception was thrown in runCommand");
      context.getLogger().log(e.getMessage());
      e.getCause();
      e.printStackTrace();
    } 
  }
}
