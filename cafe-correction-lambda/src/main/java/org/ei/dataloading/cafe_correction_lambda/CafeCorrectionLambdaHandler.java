package org.ei.dataloading.cafe_correction_lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;


/*
 * @author: telebh
 * @Date: 08/13/2019
 * @Description: Invoke Lambda Function via Amazon SNS to 
 */



public class CafeCorrectionLambdaHandler implements RequestHandler<SNSEvent, String> {
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
  
  private String loadnumber;
  
  private String corrArchiveDateFileName;
  
  private int dayOfWeek;
  
  protected void init() {
    String envHost = System.getenv("hostIp");
    if (envHost == null)
      envHost = "10.178.165.161"; 
    if (envHost.isEmpty())
      throw new NullPointerException("Cafe Correction Lambda hostIP is not set"); 
    this.host = envHost;
    System.out.println("HostIP: " + this.host);
    String envCmd = System.getenv("cmd");
    if (envCmd == null)
      envCmd = "cd /ebs_dev/Hanan; pwd; ls -ltr"; 
    if (envCmd.isEmpty())
      throw new NullPointerException("cafe correction Lambda cmd is not set"); 
    this.cmd = envCmd;
    Date currDate = new Date();
  }
  
  public String handleRequest(SNSEvent event, Context context) {
    init();
    context.getLogger().log("Received Cafe Correction SNS event");
    String timestamp = (new SimpleDateFormat("MM-dd-yyyy_HH:mm:ss")).format(Calendar.getInstance().getTime());
    context.getLogger().log("Received Cafe download SNS request at : " + timestamp);
    context.getLogger().log(((SNSEvent.SNSRecord)event.getRecords().get(0)).getSNS().getMessage());
    this.doc_type = ((SNSEvent.MessageAttribute)((SNSEvent.SNSRecord)event.getRecords().get(0)).getSNS().getMessageAttributes().get("document_type")).getValue();
    this.archive_date = ((SNSEvent.MessageAttribute)((SNSEvent.SNSRecord)event.getRecords().get(0)).getSNS().getMessageAttributes().get("archive_date")).getValue();
    context.getLogger().log("isZipFileExist : " + ((SNSEvent.MessageAttribute)((SNSEvent.SNSRecord)event.getRecords().get(0)).getSNS().getMessageAttributes().get("isZipFileExist")).getValue());
    this.isZipFileExist = Boolean.parseBoolean(((SNSEvent.MessageAttribute)((SNSEvent.SNSRecord)event.getRecords().get(0)).getSNS().getMessageAttributes().get("isZipFileExist")).getValue());
    this.loadnumber = ((SNSEvent.MessageAttribute)((SNSEvent.SNSRecord)event.getRecords().get(0)).getSNS().getMessageAttributes().get("loadnumber")).getValue();
    context.getLogger().log("Received SNS topic for doc_type: " + this.doc_type + ", archive_date: " + this.archive_date + ", Checkd if zipfile exist: " + this.isZipFileExist + " Current Week's loadnumber: " + this.loadnumber);
    try {
      if (isValidArchiveDate() && (this.doc_type.equalsIgnoreCase("ani") || this.doc_type.equalsIgnoreCase("apr") || this.doc_type.equalsIgnoreCase("ipr")) && this.isZipFileExist) {
        context.getLogger().log("cmd Sys Variable is " + this.cmd);
        context.getLogger().log("Command: " + this.cmd);
        this.home_dir = this.cmd.substring(this.cmd.indexOf('/'), this.cmd.length());
        if (this.doc_type.contentEquals("ani")) {
          this.scriptName = "all_ani_corrections.sh";
          this.cmd += ";nohup ./" + this.scriptName + " > " + this.home_dir + "/processed/ani_conv_" + this.archive_date + ".txt 2>&1";
          this.corrArchiveDateFileName = "ani_corr_list.txt";
        } else if (this.doc_type.equalsIgnoreCase("apr")) {
          this.scriptName = "all_apr_corrections.sh";
          this.cmd += ";nohup ./" + this.scriptName + " > " + this.home_dir + "/processed/apr_conv_" + this.archive_date + ".txt 2>&1";
          context.getLogger().log("apr");
          this.corrArchiveDateFileName = "apr_corr_list.txt";
        } else if (this.doc_type.equalsIgnoreCase("ipr")) {
          this.scriptName = "all_ipr_corrections.sh";
          this.cmd += ";nohup ./" + this.scriptName + " > " + this.home_dir + "/processed/ipr_conve_" + this.archive_date + ".txt 2>&1";
          context.getLogger().log("ipr");
          this.corrArchiveDateFileName = "ipr_corr_list.txt";
        } 
        runCommand(context);
        context.getLogger().log("Processing " + this.doc_type + " correction by running the command: " + this.cmd);
      } 
      return "Done running " + this.doc_type + " correction lambda";
    } catch (Exception e) {
      context.getLogger().log("An exception was thrown in " + this.doc_type + " correction handleRequest");
      e.printStackTrace();
      throw e;
    } 
  }
  
  private boolean isValidArchiveDate() {
    SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yy");
    try {
      format.parse(this.archive_date);
    } catch (ParseException ex) {
      return false;
    } 
    return true;
  }
  
  private void runCommand(Context context) {
    try {
      context.getLogger().log("Start running command: " + this.cmd);
      File keyFile = new File(CafeCorrectionLambdaHandler.class.getResource(this.privateKey).toURI());
      context.getLogger().log("privateKeyFile: " + keyFile);
      String privateKeyAbsolutePath = keyFile.getAbsolutePath();
      JSch jsch = new JSch();
      jsch.addIdentity(privateKeyAbsolutePath);
      Session session = jsch.getSession(this.user, this.host, 22);
      context.getLogger().log("ssh session created.");
      Properties config = new Properties();
      config.setProperty("StrictHostKeyChecking", "no");
      session.setConfig(config);
      session.connect();
      context.getLogger().log("ssh session connected, about to run " + this.cmd);
      ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
      InputStream instr = channelExec.getInputStream();
      channelExec.setCommand("grep -E \"loadnum=\" " + this.home_dir + "/" + this.scriptName + "| grep -v \"#loadnum\"");
      channelExec.connect();
      BufferedReader br = new BufferedReader(new InputStreamReader(instr));
      String str;
      if ((str = br.readLine()) != null)
        context.getLogger().log("grep command output: " + str.trim()); 
      str = str.trim();
      int origUpdateNum = Integer.parseInt(str.substring(str.indexOf("=") + 1, str.trim().length()));
      int tempUpdateNum = Integer.parseInt(str.substring(str.indexOf("=") + 1, str.trim().length() - 1));
      int originUpdateNumeDay = Integer.parseInt(str.substring(str.length() - 1, str.length()));
      Calendar cal = Calendar.getInstance();
      this.dayOfWeek = cal.get(7);
      //HH updated 11/29/2021, Adding 1 to the weeknumber caused that Cafe always has 1 week ahead of CPX and that cause confusion when run freezingwindow, so take off +1
      //this.loadnumber = this.loadnumber.substring(0, 4) + Integer.toString(cal.get(3) + 1);
      this.loadnumber = this.loadnumber.substring(0, 4) + Integer.toString(cal.get(3));
      String newUpdateNum = this.loadnumber + Integer.toString(this.dayOfWeek);
      context.getLogger().log("Current updatenum: " + origUpdateNum);
      context.getLogger().log("New updatenum to be used: " + newUpdateNum);
      ChannelExec channelExec2 = (ChannelExec)session.openChannel("exec");
      channelExec2.setCommand("sed -i -e \"/loadnum=" + origUpdateNum + "/s//loadnum=" + newUpdateNum + "/g\" " + this.home_dir + "/" + this.scriptName);
      channelExec2.connect();
      ChannelExec channelExec3 = (ChannelExec)session.openChannel("exec");
      channelExec3.setCommand("sed -i \"1s/.*/" + this.archive_date + " /\" " + this.home_dir + "/" + this.corrArchiveDateFileName);
      channelExec3.connect();
      ChannelExec channelExec4 = (ChannelExec)session.openChannel("exec");
      channelExec4.setCommand("source ~/.bash_profile");
      channelExec4.connect();
      channelExec.disconnect();
      channelExec2.disconnect();
      channelExec3.disconnect();
      channelExec4.disconnect();
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
        if (channelExec2.isClosed()) {
          context.getLogger().log("setting updatenum in correction script completed with exit status: " + channelExec2.getExitStatus());
          break;
        } 
        if (channelExec3.isClosed()) {
          context.getLogger().log("setting corr archive_date in correction text file completed with exit status: " + channelExec3.getExitStatus());
          break;
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
    } catch (JSchException ex) {
      context.getLogger().log("JschException: " + ex.getMessage());
      ex.printStackTrace();
    } catch (Exception e) {
      context.getLogger().log("Exception was thrown in runCommand");
      context.getLogger().log("Error Message:" + e.getMessage());
      e.printStackTrace();
    } 
  }
}
