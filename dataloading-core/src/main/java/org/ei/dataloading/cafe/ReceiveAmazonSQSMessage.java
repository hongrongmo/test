package org.ei.dataloading.cafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.mail.search.ReceivedDateTerm;

import org.apache.http.config.MessageConstraints;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
//import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticbeanstalk.model.Queue;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;








import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
/*
 * Date: 01/15/2016
 * Description: Receive and Parse Message from CAFE' Feed using Amazon SQS for processing:
 * - CPX Abstract
 * - Author Profile
 * - Affiliation Profile
 * 
 *  AU: Hteleb
 */
public class ReceiveAmazonSQSMessage implements MessageListener {

	static ReceiveAmazonSQSMessage sqsMessage = null;
	javax.jms.Message receivedMessage = null;
	
	boolean ANIRecord;
	boolean cpxdbCollection;
	
	private Perl5Util perl = new Perl5Util();
	
	String bucketName = "";
	String documentType="";
	
	//ArrayList<String> messageFieldKeys = new ArrayList<>();
	HashMap<String,String> messageFieldKeys = new HashMap<String,String>();
	
	
	public static void main(String[] args) {
		
		sqsMessage = new ReceiveAmazonSQSMessage();
		sqsMessage.ReceiveMessage();

	}

	public void ReceiveMessage()
	{
	
	//Create the connection factory using providedcredentials
			// this factory creates can talk to the queues in "us-east-1" region

			//BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAJ6F62FH2H7AKJVRQ", "6IQ5TG1abov49mq3V7sQodRXOCXSxMqhVq3/GViQ");

			SQSConnectionFactory connectionFactory = SQSConnectionFactory.builder().
					withRegion(Region.getRegion(Regions.US_EAST_1)).
					withAWSCredentialsProvider(new EnvironmentVariableCredentialsProvider()).build();

			//Create the connection
			SQSConnection connection = null;
			
			try
			{
				connection = connectionFactory.createConnection();

				//Get the wrapped client
				AmazonSQSMessagingClientWrapper  client = connection.getWrappedAmazonSQSClient();

				// Check if Queue exist
				if(!client.queueExists("EVCAFE"))
				{
					System.out.println("Queue not exist");
				}
				else
				{
					System.out.println("Yes, Queue exist");

					//create non-transacted session with "AUTO_AKNOWLEDGE"
					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					// get the queue identiy with name "EVCAFE" in the session
					javax.jms.Queue queue = session.createQueue("EVCAFE");

					//Creates consumer for the "EVCAFE" SQS
					MessageConsumer consumer = session.createConsumer(queue);

					//start receiving messages
					connection.start();

					//Receive message from Queue "EVCAFE", and set time-out to 1 second
					for(int i=0;i<10;i++)
					{
						receivedMessage = consumer.receive(1000);

						// cast received message as textMessage and print info

						if(receivedMessage !=null)
						{
							System.out.println("Received " + ((TextMessage)receivedMessage).getText());
							sqsMessage.ParseSQSMessage(((TextMessage)receivedMessage).getText());
							
							// download file from s3 bucket
							
							downloadFileFromS3();
							
							//convert file from S3 bucket
							
							parseS3AbstractFile();
						}
					}
					
					
			}

			}
			catch(JMSException ex)
			{
				ex.printStackTrace();
			}

			finally
			{
				//close connection & session
				if(connection !=null)
				{
					try
					{
						connection.close();
					}
					catch (JMSException e)
					{
						e.printStackTrace();
					}
				}
			}

	}

	public boolean ParseSQSMessage(String message)
	{
		ANIRecord = false;
		cpxdbCollection = false;
		// clean out messageFieldKeys for each message to be clean for the current one (only hold current message fields).
		
				if(messageFieldKeys.size() >0)
				{
					messageFieldKeys.clear();
					
					System.out.println("messageFieldKeys was cleared out, now the size " + messageFieldKeys.size());
				}
				
		//case received Message as TextMessage & Parse Fields
		
			if(message !=null && message.length() >0)
			{
				//System.out.println("SQS Message: " +  message);   //temp comment, as it is in archiveEVCafeRefeed.java
				StringTokenizer tokens = new StringTokenizer(message, ",");
				while(tokens.hasMoreTokens())
				{
					String fieldKey = tokens.nextToken();
					
					getFieldsKeys(fieldKey);
				}
			}
			
			//if bucket not "ANI" for abstract, and dbcollcodes NOT contains "CPX" do not print
			
			bucketName = getMessageField("bucket");
			cpxdbCollection = checkCpxDBCollection();
			if(bucketName.length() >0 && bucketName.contains("ani") && cpxdbCollection)
			{
				/*for(String key : messageFieldKeys.keySet())
				{
					System.out.println(key + " # " + messageFieldKeys.get(key));
				}*/    //HH 04/19/2016 for local debugging
				ANIRecord = true;
				
			}
			/*else
			{
				if(cpxdbCollection)
				{
					System.out.println("NOT ANI SQS MSG " + messageFieldKeys.get("bucket"));
				}
				
			}*/   //HH 04/19/2016 for local debugging
					
			
			return ANIRecord && cpxdbCollection;
		
	}
	
	public void getFieldsKeys(String fieldKeys)
	{
		String key = "";
		String value = "";
		String key_Value = "";
		String substr = "";


		StringTokenizer token = new StringTokenizer(fieldKeys, ",");
		while(token.hasMoreTokens())
		{
			if(token !=null)
			{
				key_Value = formateString(token.nextToken());
				if(key_Value !=null)
				{
						if(key_Value.contains("entries"))
						{
							substr = key_Value.substring(key_Value.indexOf(":")+1,key_Value.length()).trim();
							key = formateString(substr.substring(0, substr.indexOf(":")));
							value = formateString(substr.substring(substr.indexOf(":")+1, substr.length()).trim());
						}
						
						else
						{
							key = formateString(key_Value.substring(0, key_Value.indexOf(":")));
							value = formateString(key_Value.substring(key_Value.indexOf(":") +1, key_Value.length())).trim();
						}
						
						messageFieldKeys.put(key, value);
					
				}  
				
			}  
		}  

	}
	
	//03/30/2016 check dbcollection code from SNS message to determin whether it is CPX record
	
	private boolean checkCpxDBCollection()
	{
		boolean cpxCollection = false;
		String dbcollcodes = getMessageField("dbcollcodes");
		String dbcode = null;
		
		
		if(dbcollcodes !=null && dbcollcodes.length() >0)
		{
			if(dbcollcodes.contains("|"))
			{
				StringTokenizer dbcodes = new StringTokenizer(dbcollcodes, "|");
				while(dbcodes.hasMoreTokens())
				{
					dbcode=dbcodes.nextToken().trim();
					
					if(dbcode != null && dbcode.length() >0 && dbcode.equalsIgnoreCase("CPX"))
					{
						cpxCollection =true;
						System.out.println("CPX file");
						return cpxCollection;
					}
				}
			}
			
			else if (dbcollcodes.equalsIgnoreCase("CPX"))
			{
				cpxCollection =true;
				System.out.println("CPX file");
			}
			else
			{
				System.out.println("Skip this Key as it belongs to db collection: " +  dbcollcodes);
			}
		}
		
		return cpxCollection;
	}

	@Override
	public void onMessage(javax.jms.Message arg0) {
		try {
            // Cast the received message as TextMessage and print the text to screen.
            if (receivedMessage != null) {
                System.out.println("Received: " + ((TextMessage) receivedMessage).getText());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
		
	}
	
	public String formateString(String str)
	{
		if(str !=null)
		{
			if(str.contains("{"))
			{
				str = perl.substitute("s/{//g", str);
			}
			if(str.contains("}"))
			{
				str = perl.substitute("s/}//g", str);
			}
			if(str.contains("\""))
			{
				str = perl.substitute("s/\"//g", str);
			}
			if(str.contains("["))
			{
				str = perl.substitute("s/\\[//g", str);
			}
			if(str.contains("]"))
			{
				str = perl.substitute("s/\\]//g", str);
			}
			
		}
		return str.trim();
	}
	
	public Set<String> getMessageFieldKeys()
	{
		Set<String> keys= null;
		if(messageFieldKeys !=null && messageFieldKeys.size() >0)
		{
			for(String key: messageFieldKeys.keySet())
			{
				keys.add(key);
			}
		}
		
		return keys;
	}
	
	public String getMessageField(String key)
	{
		String field = null;
		if(messageFieldKeys !=null && messageFieldKeys.size() >0 && messageFieldKeys.containsKey(key))
		{
			field = messageFieldKeys.get(key);
		}
		return field;
	}
	
	public void downloadFileFromS3()
	{
		String s3BucketName = getMessageField("bucket");
		String key = getMessageField("key");
		
		if(s3BucketName !=null && key !=null)
		{
			try
			{
				DownloadloadFileFromS3.getFileFromS3(s3BucketName, key);
			}
			catch(InterruptedException iex)
			{
				iex.printStackTrace();
			}
			
			
		}
	}
	
	// parse cpx ANI (abstract datatype) file from S3 bucket
	
	public void parseS3AbstractFile()
	{
		String s3BucketName = getMessageField("bucket");
		
		// only Abstract files
		if(s3BucketName !=null && s3BucketName.length() >0 && s3BucketName.contains("sc-ani"))
		{
			String key = getMessageField("key");
		}
	}

}
