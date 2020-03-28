package org.ei.dataloading.cafe.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

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
import org.ei.dataloading.cafe.DownloadloadFileFromS3;
/*
 * Date: 01/27/2016
 * Description: Receive and Parse Message from CAFE' Feed using Amazon SQS for processing:
 * - CPX Abstract
 * - Author Profile
 * - Affiliation Profile
 * 
 *  AU: Hteleb
 */
public class TestReceiveAmazonSQSMessage implements MessageListener {

	static TestReceiveAmazonSQSMessage sqsMessage = null;
	javax.jms.Message receivedMessage = null;
	
	private Perl5Util perl = new Perl5Util();
	
	//ArrayList<String> messageFieldKeys = new ArrayList<>();
	HashMap<String,String> messageFieldKeys = new HashMap<String,String>();
	
	
	public static void main(String[] args) {
		
		sqsMessage = new TestReceiveAmazonSQSMessage();
		sqsMessage.ReceiveMessage();

	}

	public void ReceiveMessage()
	{
	
	//Create the connection factory using providedcredentials
			// this factory creates can talk to the queues in "us-east-1" region

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

					//Receive message from Queue "EVCAFE", and set time-out to 1 second & cast received message as textMessage and print info
					receiveMessages(session, consumer);
						
						

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

	private void receiveMessages(Session session, MessageConsumer consumer)
	{
		try
		{
			while(true)
			{
				System.out.println("Waiting for messages");
				//wait 1 minute for a message
				javax.jms.Message message = consumer.receive(TimeUnit.MINUTES.toMillis(1));
				if(message ==null)
				{
					System.out.println("shutting down after 1 minute of silence");
					break;
				}
				ParseSQSMessage(((TextMessage)message).getText());
				message.acknowledge();
				System.out.println("Aknowledged message " + message.getJMSMessageID());
			}
		}
		catch(JMSException jmsex)
		{
			System.err.println("Error receiving from SQS: " + jmsex.getMessage());
			jmsex.printStackTrace();
		}
	}
	public void ParseSQSMessage(String message)
	{
		//case received Message as TextMessage & Parse Fields
		
			if(message !=null && message.length() >0)
			{
				StringTokenizer tokens = new StringTokenizer(message, ",");
				while(tokens.hasMoreTokens())
				{
					String fieldKey = tokens.nextToken();
					
					getFieldsKeys(fieldKey);
				}
			}
			
			for(String key : messageFieldKeys.keySet())
			{
				System.out.println(key + " # " + messageFieldKeys.get(key));
			}
		
	}
	
	public void getFieldsKeys(String fieldKeys)
	{
		String key = "";
		String value = "";
		
		StringTokenizer token = new StringTokenizer(fieldKeys, ":");
		while(token.hasMoreTokens())
		{
			if(token !=null)
			{
				key = formateString(token.nextToken());
				if(key.equalsIgnoreCase("entries"))
				{
					key = formateString(token.nextToken(":"));
				}
				if(key.equalsIgnoreCase("version") || key.equalsIgnoreCase("xocs-timestamp"))
				{
					value = formateString(token.nextToken(";"));
					value = value.substring(value.indexOf(":"), value.length());
				}
				else
				{
					value = formateString(token.nextToken(":"));
				}

				
				if(! (key.equalsIgnoreCase("entries")))
				{
					messageFieldKeys.put(key, value);
				}
				
			}
		}
		
		
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
		/*str = str.replace("{", "");
		str = str.replace("}", "");
		str = str.replace("\"\"", "");*/
		
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
