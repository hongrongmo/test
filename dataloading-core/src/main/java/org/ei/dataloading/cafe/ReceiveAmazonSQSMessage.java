package org.ei.dataloading.cafe;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
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
import com.amazonaws.AmazonServiceException;
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












//HH 09/19/2016 added for VTW JSON SQS message parsing
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;




import org.apache.log4j.Logger;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
import org.ei.dataloading.upt.loadtime.vtw.DownloadVtwFile;
import org.ei.dataloading.upt.loadtime.vtw.VTWSearchAPI;
/*
 * Date: 01/15/2016
 * Description: Receive and Parse Message from CAFE' Feed using Amazon SQS for processing:
 * - CPX Abstract
 * - Author Profile
 * - Affiliation Profile
 * 
 *  AU: TELEBH
 */
public class ReceiveAmazonSQSMessage implements MessageListener {

	static ReceiveAmazonSQSMessage sqsMessage = null;
	javax.jms.Message receivedMessage = null;
	
	boolean ANIRecord;
	boolean cpxdbCollection;
	
	boolean APRRecord;
	boolean IPRRecord;
	
	
	private Perl5Util perl = new Perl5Util();
	
	String bucketName = "";
	String documentType="";
	
	//ArrayList<String> messageFieldKeys = new ArrayList<>();
	HashMap<String,String> messageFieldKeys = new HashMap<String,String>();
	
	int loadNumber;
	
	
	private final static Logger logger = Logger.getLogger(ReceiveAmazonSQSMessage.class);
	
	
	public ReceiveAmazonSQSMessage()
	{
		
	}
	
	public ReceiveAmazonSQSMessage(int load_number)
	{
		loadNumber = load_number;
	}
	public static void main(String[] args) {
		
		sqsMessage = new ReceiveAmazonSQSMessage();
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
		
		//HH 05/23/2016 for AU/AFF profile
		APRRecord = false;
		IPRRecord = false;
		
		// clean out messageFieldKeys for each message to be clean for the current one (only hold current message fields).
		
				if(messageFieldKeys.size() >0)
				{
					messageFieldKeys.clear();
				}
				
		//case received Message as TextMessage & Parse Fields
		
			if(message !=null && message.length() >0)
			{
				System.out.println("SQS Message: " +  message);
				StringTokenizer tokens = new StringTokenizer(message, ",");
				while(tokens.hasMoreTokens())
				{
					String fieldKey = tokens.nextToken();
					
					if(!(fieldKey.contains("SubscriptionConfirmation")))
						getFieldsKeys(fieldKey);
					else
					{
						System.out.println("It is a subscription Message, Skip it");
						break;
					}
				}
			}
			
			//if bucket not "ANI" for abstract, and dbcollcodes NOT contains "CPX" do not print
			
			bucketName = getMessageField("bucket");
			cpxdbCollection = checkCpxDBCollection();
			if(bucketName !=null && bucketName.length() >0 && bucketName.contains("ani") && cpxdbCollection)
			{
				/*for(String key : messageFieldKeys.keySet())
				{
					System.out.println(key + " # " + messageFieldKeys.get(key));
				}*/
				ANIRecord = true;
				
				return ANIRecord && cpxdbCollection;
				
			}
			// 05/23/2016: modify for Author/Affiliation profile
			else if(cpxdbCollection)
			{
					System.out.println("NOT ANI SQS MSG " + messageFieldKeys.get("bucket"));
					return ANIRecord && cpxdbCollection;
			}
			else if(ANIRecord)
			{
				System.out.println("NOT CPX ANI SQS MSG " + messageFieldKeys.get("dbcollcodes"));
				return ANIRecord && cpxdbCollection;
			}
			else if(bucketName.length() >0 && bucketName.contains("apr"))
			{
				System.out.println("Author Profile SQS MSG " + messageFieldKeys.get("bucket"));
				APRRecord = true;
				return APRRecord;
				
			}
			else if(bucketName.length() >0 && bucketName.contains("ipr"))
			{
				System.out.println("Institution Profile SQS MSG " + messageFieldKeys.get("bucket"));
				IPRRecord = true;
				return IPRRecord;
			}
					
			return false;
			
		
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
				if(key_Value !=null && !(key_Value.contains("SubscriptionConfirmation")))
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
	
	//HH 09/19/2016, Parse VTW JSON-based SQS Message to download Patents
	
	public boolean ParseJsonSQSMessage(String message)
	{
		String message_type = null;
		String prefix = null;
		String Patent_resourceUrl = null;
		String patentId = null, urlExpirationDate = null, patGeneration =null;
		
		
		String signedAssetURL = null;
		
		boolean evContributer = false;
		
		try
		{
			
			//DownloadVtwFile instance = new DownloadVtwFile();  //uncomment this for old way of using "Search API" only
			
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(message);
			
			JSONObject jsonObject = (JSONObject) obj;
			String msgId = (String) jsonObject.get("@id");
			String msgType = (String)jsonObject.get("msg:type");
			String msgTo = (String)jsonObject.get("msg:to");
			
			
			// clean out messageFieldKeys for each message to be clean for the current one (only hold current message fields).
			
			if(messageFieldKeys.size() >0)
			{
				messageFieldKeys.clear();
			}
			
			messageFieldKeys.put("message_id", msgId);
			//messageFieldKeys.put("message_type",msgType);
			messageFieldKeys.put("message_to", msgTo);
			
			// only process SQS Message that meant to EV ONLY, added "E-Village" for WO backfill 04/14/2017
			if(msgTo !=null && (msgTo.contains("/EV") || msgTo.contains("E-Village"))) 
			{
				evContributer = true;
				
				// based on msgtype, check whether to process Service Call or EVentNotification Message
				
				if(msgType !=null)
				{
					if(msgType.contains("EventNotification"))
					{
						message_type= "msg:event";
						prefix="evt";
					}
					else if (msgType.equalsIgnoreCase("ServiceCall"))
					{
						message_type="msg:service";
						prefix = "svc";
					}
					else
					{
						logger.error("Unknown Message Type: " + msgType + "!");
						System.exit(1);
					}
					
					
					/* get Asset Pre-signed url for each of eventNotification/ServiceCall for later download directly instead of Asset API 
						pre-signed url is provided only for backfill (unless url expired, so will need to download as forward flow),
						but for forwardflow need to use Asset API, unl
					*/
					
					//Assets
					JSONObject assets = (JSONObject)jsonObject.get(message_type);
					
					//EventNotification ID
					if(prefix !=null && prefix.equals("evt"))
						messageFieldKeys.put("event_id", (String)assets.get("@id"));
					
					//ServiceCall ID
					if(prefix !=null && prefix.equals("svc"))
						messageFieldKeys.put("service_id", (String)assets.get("@id"));
					
					
					//Resource
					JSONArray resource = (JSONArray)assets.get(prefix+":resource");
					@SuppressWarnings("unchecked")
					Iterator<String> resourceIterator = (Iterator<String>)resource.iterator();
					
					while(resourceIterator.hasNext())
					{
						Patent_resourceUrl = resourceIterator.next();
						//System.out.println(prefix + " Resource: " + Patent_resourceUrl);  // only for debugging
						
						messageFieldKeys.put("resource", Patent_resourceUrl);
						
						if(Patent_resourceUrl !=null && Patent_resourceUrl.contains("pat"))
						{
							patentId = Patent_resourceUrl.substring(Patent_resourceUrl.indexOf("pat/") + 4, Patent_resourceUrl.lastIndexOf("/"));
							messageFieldKeys.put("patentid", patentId);
							
							//Patent generation
							patGeneration = Patent_resourceUrl.substring(Patent_resourceUrl.lastIndexOf("/")+1, Patent_resourceUrl.length());
							messageFieldKeys.put("generation", patGeneration);
						}
						else
						{
							System.out.println("resource does not contain patent id!!!");
						}
						
					}
					
					//evt:detailes or svc:detailes
					
					if (assets.containsKey(prefix+":details"))
					{
						JSONArray detailes = (JSONArray)assets.get(prefix+":details");
						
						
						/* this way when following message in Ruud's documentation
						JSONObject detailes = (JSONObject)assets.get(prefix+":details");
						signedAssetURL = (String)detailes.get(prefix+":signedAssetURL");
						messageFieldKeys.put("signedAssetUrl", signedAssetURL);
						//System.out.println("SignedAssetURL: " + signedAssetURL);   // only for debugging
						*/
						
						// this way when following real message in SQS
						@SuppressWarnings("unchecked")
						Iterator<JSONObject> detailesIterator = (Iterator<JSONObject>)detailes.iterator();
						while(detailesIterator.hasNext())
						{
							JSONObject signedUrl = detailesIterator.next();
							signedAssetURL = (String)signedUrl.get(prefix+":signedURL");
							messageFieldKeys.put("signedAssetUrl", signedAssetURL);
						}
					}
					
					
					// SignedAssetURL expiration date
					if(signedAssetURL !=null)
					{
						// get Patent XML filename
						/*String xmlFileName = signedAssetURL.substring(signedAssetURL.lastIndexOf("/") + 1, signedAssetURL.indexOf("?"));
						messageFieldKeys.put("xmlFileName", xmlFileName);*/
						
						urlExpirationDate = signedAssetURL.substring(signedAssetURL.indexOf("Expires=") + 8, signedAssetURL.lastIndexOf("&"));
						messageFieldKeys.put("urlExpirationDate", urlExpirationDate);
						
						
						
						//Download the XML file
						//instance.retrieveAsset(signedAssetURL,xmlFileName,Patent_resourceID,loadNumber);
					}
					
				}
			}
			
			//System.out.println("msg id is: " +  msgId + "message type: " + msgType);
		}
		
		catch(ParseException ex)
		{
			logger.error("Json Parser exception type: " + ex.getErrorType() + ": " +  ex.getMessage());
			//System.out.println("Json Parser exception type: " + ex.getErrorType() + ": " +  ex.getMessage());
			
			ex.printStackTrace();
		}
		catch (AmazonServiceException ase) 
		{
			logger.error("Caught an AmazonServiceException, which means your request made it " +
					"to Amazon SQS, but was rejected with an error response for some reason.");
			
			logger.error("Error Message:    " + ase.getMessage());
			logger.error("HTTP Status Code: " + ase.getStatusCode());
			logger.error("AWS Error Code:   " + ase.getErrorCode());
			logger.error("Error Type:       " + ase.getErrorType());
			logger.error("Request ID:       " + ase.getRequestId());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return evContributer;
	}
}
