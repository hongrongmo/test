package org.ei.dataloading.cafe.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.ei.dataloading.cafe.GetANIFileFromCafeS3Bucket;
import org.ei.dataloading.cafe.ReceiveAmazonSQSMessage;
import org.ei.dataloading.cafe.SQSConfiguration;
import org.ei.dataloading.cafe.SQSExistenceCheck;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

public class TestWhyEmptyFirstOutFile {

	/**
	 * @author TELEBH
	 * An example class to demonstrate the behavior of CLIENT_ACKNOWLEDGE mode for received messages. 
	 *
	 * First, a session, a message producer, and a message consumer are created. Then, two messages are sent. Next, two messages
	 * are received but only the second one is acknowledged. After waiting for the visibility time out period, an attempt to
	 * receive another message is made. It is shown that no message is returned for this attempt since in CLIENT_ACKNOWLEDGE mode,
	 * as expected, all the messages prior to the acknowledged messages are also acknowledged.
	 */

	public TestWhyEmptyFirstOutFile(){}

	static int numberOfRuns = 0;


	static String url="jdbc:oracle:thin:@localhost:1521:eid";
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction1";
	static String password="ei3it";
	static String database;
	static int updateNumber=0;
	static String sqlldrFileName = null;


	// Visibility time-out for the queue. It must match to the one set for the queue for this example to work.
	private static final long TIME_OUT_SECONDS = 30;
	private static final int MESSAGE_VISIBILITY_TIME_OUT_SECONDS = 1200;
	private static final int NUM_OF_MESSAGES_TO_FETCH = 10;


	private String queueName = "";
	private static AmazonSQS sqs = null;
	private static String myQueueUrl = null;
	private static GetANIFileFromCafeS3Bucket objectFromS3 = null;
	private static AmazonS3 s3Client = null; 
	private DeleteMessageRequest deleteRequest = null;
	private ReceiveMessageResult receiveMessageResult = null;

	private ReceiveAmazonSQSMessage obj = null;

	private String action = null;
	private long msgEpoch;


	//Configuration&Settings
	SQSConnection connection = null;

	private static HashMap<String, String> queueArgs = new HashMap<String,String>();


	public static void main(String args[]) throws JMSException, InterruptedException {

		/* queueArgs[0] = "--queue EVCAFE";
	 	     queueArgs[1] = "--region US_EAST_1";*/

		queueArgs.put("--queue", "EVCAFE");
		queueArgs.put("--region","us-east-1");

		if(args !=null)
		{
			if(args[0] !=null)
				numberOfRuns = Integer.parseInt(args[0]);

			if(args.length >2)
			{
				if(args[1]!=null && args[1].length()>0)
				{
					Pattern pattern = Pattern.compile("^\\d*$");
					Matcher matcher = pattern.matcher(args[1]);
					if (matcher.find())
					{
						updateNumber = Integer.parseInt(args[1]);
					}
					else
					{
						System.out.println("did not find updateNumber or updateNumber has wrong format");
						System.exit(1);
					}
				}

				if(args[2] !=null)
				{
					database = args[2];
				}
				if(args[3] !=null)
				{
					url = args[3];
				}
				if(args[4] !=null)
				{
					driver = args[4];
				}
				if(args[5] !=null)
				{
					username = args[5];
				}
				if(args[6] !=null)
				{
					password = args[6];
				}
			}

			if(args.length >7)
			{
				if(args[7] !=null)
					sqlldrFileName = args[7];

			}
		}
		// create Class instance 

		TestWhyEmptyFirstOutFile msgReceiveClientAknowledge = new TestWhyEmptyFirstOutFile();

		msgReceiveClientAknowledge.SQSCreationAndSetting();


	}

	public void SQSCreationAndSetting() throws JMSException, InterruptedException
	{
		try {

			// Create the configuration for the example
			SQSConfiguration config = SQSConfiguration.parseConfig("SyncMessageReceiverClientAcknowledge", queueArgs);

			// create object of GetANIFileFromCafeS3Bucket to convert the CPX record
			s3Client = config.getAmazonS3Cleint();
			//objectFromS3 = GetANIFileFromCafeS3Bucket.getInstance(s3Client);
			//objectFromS3 = new GetANIFileFromCafeS3Bucket(s3Client,updateNumber,database,url,driver,username,password,sqlldrFileName);  //good one to use
			
			File s3dir = new File("s3dir");
			if(! (s3dir.exists()))
			{
				s3dir.mkdir();
			}

			objectFromS3 = new GetANIFileFromCafeS3Bucket(s3Client,updateNumber,database,url,driver,username,password, sqlldrFileName);
			
			


			// Setup logging for the example
			SQSExistenceCheck.setupLogging();

			// Create the connection factory based on the config
			SQSConnectionFactory connectionFactory =SQSConnectionFactory.builder()
					.withRegion(config.getRegion())
					.withAWSCredentialsProvider(config.getCredentialsProvider())
					.build();

			// Create the connection
			connection = connectionFactory.createConnection();

			queueName = config.getQueueName();
			// Create the queue if needed
			SQSExistenceCheck.ensureQueueExists(connection, queueName);


			// AMazonSQS queue
			System.out.println("===========================================");
			System.out.println("Getting Started with Amazon SQS");
			System.out.println("===========================================\n");

			//clone JMSwrapped amazonSQSClient to AMazonSQS queue
			sqs = connection.getWrappedAmazonSQSClient().getAmazonSQSClient();
			// get current queue
			myQueueUrl = sqs.getQueueUrl(queueName).getQueueUrl();

			// List queues
			System.out.println("Listing all queues in your account.\n");
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				System.out.println("  QueueUrl: " + queueUrl);
			}
			System.out.println();
			// END of AmazonSQS queue		


			// Create the session  with client acknowledge mode
			Session session = connection.createSession(false, SQSSession.UNORDERED_ACKNOWLEDGE);

			// Create the consumer 
			MessageConsumer consumer = session.createConsumer(session.createQueue(queueName));

			// Open the connection
			connection.start();

			// Receive a message parse it
			receiveMessage(consumer,true);


		}

		catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it " +
					"to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered " +
					"a serious internal problem while trying to communicate with SQS, such as not " +
					"being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		finally
		{
			if(connection !=null)
			{
				try
				{
					// Close the connection. This will close the session automatically
					connection.close();
					System.out.println("Connection closed.");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Receives a message through the consumer synchronously with the default timeout (TIME_OUT_SECONDS).
	 * If a message is received, the message is printed if it is ANI & CPX Record. If no message is received, "Queue is empty!" is
	 * printed.
	 *
	 * @param consumer Message consumer
	 * @param acknowledge If true and a message is received, the received message is acknowledged.
	 * @throws JMSException
	 */
	private void receiveMessage(MessageConsumer consumer, boolean acknowledge) throws JMSException,InterruptedException {

		obj = new ReceiveAmazonSQSMessage(); 

		try
		{

			String []strMsg= new String[81];
			strMsg[0] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84960540873\", \"issn\" : \"00160032\", \"epoch\" : \"1459960918350\", \"xocs-timestamp\" : \"2016-04-06T12:53:53.242088Z\", \"pui\" : \"609030981\", \"eid\" : \"2-s2.0-84960540873\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\", \"pii\" : \"S0016003216000831\", \"modification\" : \"CONTENT\", \"prefix\" : \"2-s2.0\", \"document-type\" : \"core\", \"action\" : \"u\", \"sort-year\" : \"2016\", \"dbcollcodes\" : \"CPX|REAXYSCAR|Scopusbase\", \"doi\" : \"10.1016/j.jfranklin.2016.02.015\" } ] }";
			strMsg[1] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"77952191016\", \"modification\" : \"CONTENT\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918409\", \"action\" : \"u\", \"pui\" : \"358794829\", \"xocs-timestamp\" : \"2016-04-06T12:55:05.258273Z\", \"sort-year\" : \"2010\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-77952191016\", \"doi\" : \"10.1109/ACHI.2010.44\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[2] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"73949145671\", \"modification\" : \"CONTENT\", \"issn\" : \"15563758\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918334\", \"action\" : \"u\", \"pui\" : \"358088170\", \"xocs-timestamp\" : \"2016-04-06T12:53:53.226795Z\", \"sort-year\" : \"2009\", \"dbcollcodes\" : \"CABS|CPX|EMBIO|Scopusbase\", \"eid\" : \"2-s2.0-73949145671\", \"doi\" : \"10.2202/1556-3758.1669\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[3] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84886833383\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918409\", \"action\" : \"u\", \"pui\" : \"370170251\", \"xocs-timestamp\" : \"2016-04-06T12:55:05.258273Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84886833383\", \"doi\" : \"10.4028/www.scientific.net/AMM.397-400.2406\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[4] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"80053101974\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918390\", \"action\" : \"u\", \"pui\" : \"362614613\", \"xocs-timestamp\" : \"2016-04-06T12:55:05.258273Z\", \"sort-year\" : \"2011\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-80053101974\", \"doi\" : \"10.4028/www.scientific.net/AMR.317-319.446\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[5] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84905748534\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918307\", \"action\" : \"u\", \"pui\" : \"373735434\", \"xocs-timestamp\" : \"2016-04-06T12:53:53.226795Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84905748534\", \"doi\" : \"10.4028/www.scientific.net/AMR.998-999.522\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[6] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84906462643\", \"modification\" : \"CONTENT\", \"issn\" : \"00431397\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918420\", \"action\" : \"u\", \"pui\" : \"53313021\", \"xocs-timestamp\" : \"2016-04-06T12:55:09.259566Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"EMBIO|CPX|FLX|GEO|CABS|Scopusbase\", \"eid\" : \"2-s2.0-84906462643\", \"doi\" : \"10.1002/2014WR015409\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[7] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84871342953\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918400\", \"action\" : \"u\", \"pui\" : \"366299676\", \"xocs-timestamp\" : \"2016-04-06T12:55:02.258861Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84871342953\", \"doi\" : \"10.4028/www.scientific.net/AMM.226-228.2344\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[8] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84871342953\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918400\", \"action\" : \"u\", \"pui\" : \"366299676\", \"xocs-timestamp\" : \"2016-04-06T12:55:02.258861Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84871342953\", \"doi\" : \"10.4028/www.scientific.net/AMM.226-228.2344\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[9] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84903398833\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918360\", \"action\" : \"u\", \"pui\" : \"373402234\", \"xocs-timestamp\" : \"2016-04-06T12:53:53.242088Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84903398833\", \"doi\" : \"10.4028/www.scientific.net/AMR.945-949.2762\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[10] = " { \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84906988563\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918409\", \"action\" : \"u\", \"pui\" : \"373918538\", \"xocs-timestamp\" : \"2016-04-06T12:55:05.258273Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84906988563\", \"doi\" : \"10.4028/www.scientific.net/AMR.1022.261\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[11] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84914673049\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961258790\", \"action\" : \"u\", \"pui\" : \"600702399\", \"xocs-timestamp\" : \"2016-04-06T12:57:02.320831Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84914673049\", \"doi\" : \"10.4028/www.scientific.net/AMM.651-653.260\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			
			strMsg[12] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84872973822\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963208212\", \"action\" : \"u\", \"pui\" : \"368203685\", \"xocs-timestamp\" : \"2016-04-06T13:08:52.281411Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84872973822\", \"doi\" : \"10.4028/www.scientific.net/AMR.629.79\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[13] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"79954524255\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963305465\", \"action\" : \"u\", \"pui\" : \"361613642\", \"xocs-timestamp\" : \"2016-04-06T13:09:05.286588Z\", \"sort-year\" : \"2011\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-79954524255\", \"doi\" : \"10.4028/www.scientific.net/AMM.52-54.457\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[14] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84902106656\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963184099\", \"action\" : \"u\", \"pui\" : \"373259020\", \"xocs-timestamp\" : \"2016-04-06T13:07:33.247549Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84902106656\", \"doi\" : \"10.4028/www.scientific.net/AMM.553.344\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[15] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84904052606\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459964329988\", \"action\" : \"u\", \"pui\" : \"373498088\", \"xocs-timestamp\" : \"2016-04-06T13:11:06.295529Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84904052606\", \"doi\" : \"10.4028/www.scientific.net/AMR.917.244\", \"load-unit-id\" : \"swd_uC43700445444.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[16] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84869849645\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459964330045\", \"action\" : \"u\", \"pui\" : \"366104471\", \"xocs-timestamp\" : \"2016-04-06T13:12:21.300279Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84869849645\", \"doi\" : \"10.4028/www.scientific.net/AMM.204-208.2309\", \"load-unit-id\" : \"swd_uC43700445444.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[17] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84876932287\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963305465\", \"action\" : \"u\", \"pui\" : \"368831640\", \"xocs-timestamp\" : \"2016-04-06T13:09:05.286588Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84876932287\", \"doi\" : \"10.4028/www.scientific.net/AMM.291-294.738\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[18] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84875041415\", \"modification\" : \"CONTENT\", \"issn\" : \"17433509\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459964330045\", \"action\" : \"u\", \"pui\" : \"368533581\", \"xocs-timestamp\" : \"2016-04-06T13:12:19.297085Z\", \"sort-year\" : \"2011\", \"dbcollcodes\" : \"CPX|GEO|Scopusbase\", \"eid\" : \"2-s2.0-84875041415\", \"doi\" : \"10.2495/UT110271\", \"load-unit-id\" : \"swd_uC43700445444.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[19] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"80054989298\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963224291\", \"action\" : \"u\", \"pui\" : \"362813217\", \"xocs-timestamp\" : \"2016-04-06T13:08:52.281411Z\", \"sort-year\" : \"2011\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-80054989298\", \"doi\" : \"10.4028/www.scientific.net/AMM.71-78.1375\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[20] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84884760601\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459964329949\", \"action\" : \"u\", \"pui\" : \"369920611\", \"xocs-timestamp\" : \"2016-04-06T13:11:06.296798Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84884760601\", \"doi\" : \"10.4028/www.scientific.net/AMM.387.231\", \"load-unit-id\" : \"swd_uC43700445444.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[21] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84913556192\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963208212\", \"action\" : \"u\", \"pui\" : \"600659347\", \"xocs-timestamp\" : \"2016-04-06T13:08:52.281411Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84913556192\", \"doi\" : \"10.4028/www.scientific.net/AMR.1040.113\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[22] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"80053058753\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962858286\", \"action\" : \"u\", \"pui\" : \"362605108\", \"xocs-timestamp\" : \"2016-04-06T13:06:32.223997Z\", \"sort-year\" : \"2011\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-80053058753\", \"doi\" : \"10.4028/www.scientific.net/AMR.306-307.1773\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[23] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84887544151\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459960918303\", \"action\" : \"u\", \"pui\" : \"370260061\", \"xocs-timestamp\" : \"2016-04-06T12:53:53.226921Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84887544151\", \"doi\" : \"10.4028/www.scientific.net/AMM.456.90\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[24] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84855393384\", \"issn\" : \"00220248\", \"epoch\" : \"1459960918383\", \"xocs-timestamp\" : \"2016-04-06T12:55:00.255888Z\", \"pui\" : \"51801887\", \"eid\" : \"2-s2.0-84855393384\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\", \"pii\" : \"S0022024811010724\", \"modification\" : \"CONTENT\", \"prefix\" : \"2-s2.0\", \"document-type\" : \"core\", \"action\" : \"u\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|CHEM|Scopusbase\", \"doi\" : \"10.1016/j.jcrysgro.2011.11.093\" } ] }";
			strMsg[25] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84869388766\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961274880\", \"action\" : \"u\", \"pui\" : \"366090891\", \"xocs-timestamp\" : \"2016-04-06T12:57:04.32415Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84869388766\", \"doi\" : \"10.4028/www.scientific.net/AMR.578.158\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[26] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"77951009194\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961219768\", \"action\" : \"u\", \"pui\" : \"358638302\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298137Z\", \"sort-year\" : \"2010\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-77951009194\", \"doi\" : \"10.4028/www.scientific.net/AMR.97-101.286\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[27] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84904529739\", \"modification\" : \"CONTENT\", \"issn\" : \"16167341\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961258790\", \"action\" : \"u\", \"pui\" : \"53258150\", \"xocs-timestamp\" : \"2016-04-06T12:57:02.320831Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"GEO|FLX|CPX|Scopusbase\", \"eid\" : \"2-s2.0-84904529739\", \"doi\" : \"10.1007/s10236-014-0743-4\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[28] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84893982901\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961202708\", \"action\" : \"u\", \"pui\" : \"372372771\", \"xocs-timestamp\" : \"2016-04-06T12:55:57.298012Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84893982901\", \"doi\" : \"10.4028/www.scientific.net/AMM.505-506.184\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[29] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84902295605\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961205712\", \"action\" : \"u\", \"pui\" : \"373291281\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298345Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84902295605\", \"doi\" : \"10.4028/www.scientific.net/AMR.926-930.4328\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[30] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84905017870\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961205712\", \"action\" : \"u\", \"pui\" : \"373650366\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298345Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84905017870\", \"doi\" : \"10.4028/www.scientific.net/AMM.592-594.591\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[31] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"80052113404\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961219768\", \"action\" : \"u\", \"pui\" : \"362429300\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298137Z\", \"sort-year\" : \"2011\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-80052113404\", \"doi\" : \"10.4028/www.scientific.net/AMM.83.197\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[32] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84896086396\", \"modification\" : \"CONTENT\", \"issn\" : \"1743355X\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961207714\", \"action\" : \"u\", \"pui\" : \"372600168\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298345Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84896086396\", \"doi\" : \"10.2495/BEM360371\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[33] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84886259062\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961226770\", \"action\" : \"u\", \"pui\" : \"370098361\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298137Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84886259062\", \"doi\" : \"10.4028/www.scientific.net/AMR.779-780.1643\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[34] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84905921757\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961274880\", \"action\" : \"u\", \"pui\" : \"373752831\", \"xocs-timestamp\" : \"2016-04-06T12:57:04.32415Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84905921757\", \"doi\" : \"10.4028/www.scientific.net/AMR.986-987.243\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[35] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84868588890\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961258790\", \"action\" : \"u\", \"pui\" : \"365989224\", \"xocs-timestamp\" : \"2016-04-06T12:57:02.320831Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84868588890\", \"doi\" : \"10.4028/www.scientific.net/AMR.535-537.1678\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[36] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84856063654\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961264799\", \"action\" : \"u\", \"pui\" : \"364119283\", \"xocs-timestamp\" : \"2016-04-06T12:57:04.32415Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84856063654\", \"doi\" : \"10.4028/www.scientific.net/AMR.428.24\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[37] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84880254919\", \"modification\" : \"CONTENT\", \"issn\" : \"09613218\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961226770\", \"action\" : \"u\", \"pui\" : \"52694950\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298137Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84880254919\", \"doi\" : \"10.1080/09613218.2013.808864\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[38] ="{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84906973882\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961226770\", \"action\" : \"u\", \"pui\" : \"373918682\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298137Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84906973882\", \"doi\" : \"10.4028/www.scientific.net/AMR.1024.201\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[39] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84903168096\", \"modification\" : \"CONTENT\", \"issn\" : \"17433517\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961207714\", \"action\" : \"u\", \"pui\" : \"373384670\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298345Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84903168096\", \"doi\" : \"10.2495/ICTE130711\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[40] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84896827230\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961251785\", \"action\" : \"u\", \"pui\" : \"372586522\", \"xocs-timestamp\" : \"2016-04-06T12:56:06.31516Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84896827230\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[41] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84902089717\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961205712\", \"action\" : \"u\", \"pui\" : \"373259319\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298345Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84902089717\", \"doi\" : \"10.4028/www.scientific.net/AMM.556-562.940\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[42] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84884797180\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961863857\", \"action\" : \"u\", \"pui\" : \"369920865\", \"xocs-timestamp\" : \"2016-04-06T12:58:57.429176Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84884797180\", \"doi\" : \"10.4028/www.scientific.net/AMM.389.957\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[43] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84886285607\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961205712\", \"action\" : \"u\", \"pui\" : \"370103137\", \"xocs-timestamp\" : \"2016-04-06T12:56:02.298345Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84886285607\", \"doi\" : \"10.4028/www.scientific.net/AMM.416-417.776\", \"load-unit-id\" : \"swd_uC43700445441.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[44] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84869414162\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961863849\", \"action\" : \"u\", \"pui\" : \"366089657\", \"xocs-timestamp\" : \"2016-04-06T12:58:57.429176Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84869414162\", \"doi\" : \"10.4028/www.scientific.net/AMR.546-547.1270\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[45] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84896288291\", \"modification\" : \"CONTENT\", \"issn\" : \"17265479\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961863913\", \"action\" : \"u\", \"pui\" : \"372621366\", \"xocs-timestamp\" : \"2016-04-06T13:00:04.423488Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84896288291\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[46] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84893957570\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961863826\", \"action\" : \"u\", \"pui\" : \"372373497\", \"xocs-timestamp\" : \"2016-04-06T12:58:57.418475Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84893957570\", \"doi\" : \"10.4028/www.scientific.net/AMR.879.7\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[47] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84920824802\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963208212\", \"action\" : \"u\", \"pui\" : \"601298106\", \"xocs-timestamp\" : \"2016-04-06T13:08:52.281411Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84920824802\", \"doi\" : \"10.4028/www.scientific.net/AMM.678.720\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[48] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84869426050\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962858245\", \"action\" : \"u\", \"pui\" : \"366090806\", \"xocs-timestamp\" : \"2016-04-06T13:05:20.216666Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84869426050\", \"doi\" : \"10.4028/www.scientific.net/AMR.576.552\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[49] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84896301367\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961863887\", \"action\" : \"u\", \"pui\" : \"372620032\", \"xocs-timestamp\" : \"2016-04-06T13:00:04.423488Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84896301367\", \"doi\" : \"10.4028/www.scientific.net/AMM.530-531.281\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[50] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84905020510\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961863853\", \"action\" : \"u\", \"pui\" : \"373650329\", \"xocs-timestamp\" : \"2016-04-06T12:58:57.420922Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84905020510\", \"doi\" : \"10.4028/www.scientific.net/AMM.592-594.391\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[51] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84892700645\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961863880\", \"action\" : \"u\", \"pui\" : \"372174384\", \"xocs-timestamp\" : \"2016-04-06T13:00:01.421404Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84892700645\", \"doi\" : \"10.4028/www.scientific.net/AMR.886.105\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[52] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84856976628\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962176615\", \"action\" : \"u\", \"pui\" : \"364226625\", \"xocs-timestamp\" : \"2016-04-06T13:01:31.465222Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84856976628\", \"doi\" : \"10.4028/www.scientific.net/AMR.445.27\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[53] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84921689204\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459961863940\", \"action\" : \"u\", \"pui\" : \"601733808\", \"xocs-timestamp\" : \"2016-04-06T13:00:09.426731Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84921689204\", \"doi\" : \"10.4028/www.scientific.net/AMM.660.162\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[54] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84915819625\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962200644\", \"action\" : \"u\", \"pui\" : \"600715460\", \"xocs-timestamp\" : \"2016-04-06T13:02:53.507571Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84915819625\", \"doi\" : \"10.4028/www.scientific.net/AMM.644-650.5308\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[55] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84880299840\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962183622\", \"action\" : \"u\", \"pui\" : \"369345224\", \"xocs-timestamp\" : \"2016-04-06T13:01:31.465222Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84880299840\", \"doi\" : \"10.4028/www.scientific.net/AMR.710.575\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[56] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84904969393\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962176615\", \"action\" : \"u\", \"pui\" : \"373650464\", \"xocs-timestamp\" : \"2016-04-06T13:01:31.465222Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84904969393\", \"doi\" : \"10.4028/www.scientific.net/AMM.592-594.1109\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[57] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"78650727998\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962195847\", \"action\" : \"u\", \"pui\" : \"361016186\", \"xocs-timestamp\" : \"2016-04-06T13:02:41.500695Z\", \"sort-year\" : \"2010\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-78650727998\", \"doi\" : \"10.4028/www.scientific.net/AMR.139-141.681\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[58] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"78651261019\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962222649\", \"action\" : \"u\", \"pui\" : \"361073814\", \"xocs-timestamp\" : \"2016-04-06T13:03:04.515204Z\", \"sort-year\" : \"2010\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-78651261019\", \"doi\" : \"10.4028/www.scientific.net/AMR.135.424\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[59] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84886854891\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962200644\", \"action\" : \"u\", \"pui\" : \"370168958\", \"xocs-timestamp\" : \"2016-04-06T13:02:41.500695Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84886854891\", \"doi\" : \"10.4028/www.scientific.net/AMR.791-793.1064\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[60] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84859935350\", \"issn\" : \"18785352\", \"epoch\" : \"1459962168610\", \"xocs-timestamp\" : \"2016-04-06T13:01:31.465222Z\", \"pui\" : \"51978853\", \"eid\" : \"2-s2.0-84859935350\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\", \"pii\" : \"S1878535211002164\", \"modification\" : \"CONTENT\", \"prefix\" : \"2-s2.0\", \"document-type\" : \"core\", \"action\" : \"u\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"doi\" : \"10.1016/j.arabjc.2011.08.003\" } ] }";
			strMsg[61] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84886868850\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962858192\", \"action\" : \"u\", \"pui\" : \"370169171\", \"xocs-timestamp\" : \"2016-04-06T13:05:20.216985Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84886868850\", \"doi\" : \"10.4028/www.scientific.net/AMR.791-793.2023\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[62] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84886892744\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962183622\", \"action\" : \"u\", \"pui\" : \"370064087\", \"xocs-timestamp\" : \"2016-04-06T13:01:31.465222Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84886892744\", \"doi\" : \"10.4028/www.scientific.net/AMR.774-776.1414\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[63] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84880460261\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962168610\", \"action\" : \"u\", \"pui\" : \"369368094\", \"xocs-timestamp\" : \"2016-04-06T13:01:27.463104Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84880460261\", \"doi\" : \"10.4028/www.scientific.net/AMR.712-715.317\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[64] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84896282488\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962200644\", \"action\" : \"u\", \"pui\" : \"372620041\", \"xocs-timestamp\" : \"2016-04-06T13:02:41.500695Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84896282488\", \"doi\" : \"10.4028/www.scientific.net/AMM.530-531.320\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[65] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84905818630\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962195847\", \"action\" : \"u\", \"pui\" : \"373748096\", \"xocs-timestamp\" : \"2016-04-06T13:02:41.500695Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84905818630\", \"doi\" : \"10.4028/www.scientific.net/AMR.989-994.809\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[66]= "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84884881759\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962154602\", \"action\" : \"u\", \"pui\" : \"369936526\", \"xocs-timestamp\" : \"2016-04-06T13:01:17.461172Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84884881759\", \"doi\" : \"10.4028/www.scientific.net/AMM.380-384.3994\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[67] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"83755188375\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962209659\", \"action\" : \"u\", \"pui\" : \"363116283\", \"xocs-timestamp\" : \"2016-04-06T13:03:04.515204Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-83755188375\", \"doi\" : \"10.4028/www.scientific.net/AMR.399-401.886\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[68] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84907867518\", \"modification\" : \"CONTENT\", \"issn\" : \"1743355X\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962858279\", \"action\" : \"u\", \"pui\" : \"600172760\", \"xocs-timestamp\" : \"2016-04-06T13:06:32.223997Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84907867518\", \"doi\" : \"10.2495/SMTA141242\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[69] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84887046893\", \"modification\" : \"CONTENT\", \"issn\" : \"17265479\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962176615\", \"action\" : \"u\", \"pui\" : \"370191514\", \"xocs-timestamp\" : \"2016-04-06T13:01:31.465222Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84887046893\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[70] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"81255198743\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962858286\", \"action\" : \"u\", \"pui\" : \"362933874\", \"xocs-timestamp\" : \"2016-04-06T13:06:32.223997Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-81255198743\", \"doi\" : \"10.4028/www.scientific.net/AMM.121-126.2994\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[71] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84873510188\", \"issn\" : \"00399140\", \"epoch\" : \"1459962230652\", \"xocs-timestamp\" : \"2016-04-06T13:03:14.516464Z\", \"pui\" : \"52440741\", \"eid\" : \"2-s2.0-84873510188\", \"load-unit-id\" : \"swd_uC43700445442.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\", \"pii\" : \"S0039914013000428\", \"modification\" : \"CONTENT\", \"prefix\" : \"2-s2.0\", \"document-type\" : \"core\", \"action\" : \"u\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|CHEM|Scopusbase\", \"doi\" : \"10.1016/j.talanta.2013.01.032\" } ] }";
			strMsg[72] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84874936680\", \"issn\" : \"08905401\", \"epoch\" : \"1459963206217\", \"xocs-timestamp\" : \"2016-04-06T13:08:36.281877Z\", \"pui\" : \"52491653\", \"eid\" : \"2-s2.0-84874936680\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\", \"pii\" : \"S0890540113000242\", \"modification\" : \"CONTENT\", \"prefix\" : \"2-s2.0\", \"document-type\" : \"core\", \"action\" : \"u\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"doi\" : \"10.1016/j.ic.2013.03.006\" } ] }";
			strMsg[73] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84885046899\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962858309\", \"action\" : \"u\", \"pui\" : \"369962681\", \"xocs-timestamp\" : \"2016-04-06T13:06:33.235482Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84885046899\", \"doi\" : \"10.4028/www.scientific.net/AMR.765-767.1151\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[74] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84870435243\", \"issn\" : \"01697722\", \"epoch\" : \"1459962858265\", \"xocs-timestamp\" : \"2016-04-06T13:06:22.238771Z\", \"pui\" : \"52342946\", \"eid\" : \"2-s2.0-84870435243\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\", \"pii\" : \"S0169772212001519\", \"modification\" : \"CONTENT\", \"prefix\" : \"2-s2.0\", \"document-type\" : \"core\", \"action\" : \"u\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"EMBASE|CABS|GEO|FLX|CPX|EMBIO|Scopusbase\", \"doi\" : \"10.1016/j.jconhyd.2012.11.002\" } ] }";
			strMsg[75] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84886811803\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963208209\", \"action\" : \"u\", \"pui\" : \"370168878\", \"xocs-timestamp\" : \"2016-04-06T13:08:36.281877Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84886811803\", \"doi\" : \"10.4028/www.scientific.net/AMR.791-793.710\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[76] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84873851571\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962858235\", \"action\" : \"u\", \"pui\" : \"368225678\", \"xocs-timestamp\" : \"2016-04-06T13:05:20.213368Z\", \"sort-year\" : \"2013\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-84873851571\", \"doi\" : \"10.4028/www.scientific.net/AMR.641-642.398\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[77] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"79960454018\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963208212\", \"action\" : \"u\", \"pui\" : \"362158032\", \"xocs-timestamp\" : \"2016-04-06T13:08:52.281411Z\", \"sort-year\" : \"2011\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-79960454018\", \"doi\" : \"10.4028/www.scientific.net/AMR.266.151\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[78] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"84906362796\", \"modification\" : \"CONTENT\", \"issn\" : \"1616301X\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459963305465\", \"action\" : \"u\", \"pui\" : \"53309754\", \"xocs-timestamp\" : \"2016-04-06T13:09:05.286588Z\", \"sort-year\" : \"2014\", \"dbcollcodes\" : \"BSTEIN|CPX|Scopusbase\", \"eid\" : \"2-s2.0-84906362796\", \"doi\" : \"10.1002/adfm.201401547\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[79] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"80053091633\", \"modification\" : \"CONTENT\", \"issn\" : \"10226680\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962858245\", \"action\" : \"u\", \"pui\" : \"362605110\", \"xocs-timestamp\" : \"2016-04-06T13:05:20.216666Z\", \"sort-year\" : \"2011\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-80053091633\", \"doi\" : \"10.4028/www.scientific.net/AMR.306-307.1785\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			strMsg[80] = "{ \"bucket\" : \"sc-ani-xml-prod\", \"entries\" : [{ \"key\" : \"81055137349\", \"modification\" : \"CONTENT\", \"issn\" : \"16609336\", \"document-type\" : \"core\", \"prefix\" : \"2-s2.0\", \"epoch\" : \"1459962858218\", \"action\" : \"u\", \"pui\" : \"362916946\", \"xocs-timestamp\" : \"2016-04-06T13:05:20.213368Z\", \"sort-year\" : \"2012\", \"dbcollcodes\" : \"CPX|Scopusbase\", \"eid\" : \"2-s2.0-81055137349\", \"doi\" : \"10.4028/www.scientific.net/AMM.117-119.1602\", \"load-unit-id\" : \"swd_uC43700445443.dat\", \"version\" : \"2016-04-06T10:32:33.000033+01:00\" } ] }";
			
			
			
			
			
			
			for(int i=0;i<strMsg.length;i++)
			{
				if(obj.ParseSQSMessage(strMsg[i]))   //for testing
				{

					action = obj.getMessageField("action");
					msgEpoch = Long.parseLong(obj.getMessageField("epoch"));

					// give time for each of the Converted CPX content be written in out file
					//Thread.currentThread().sleep(100);

					if(action !=null)
					{

						/*objectFromS3.getFile(obj.getMessageField("bucket"), obj.getMessageField("key"),
								updateNumber,database,action,msgEpoch,url,driver,username,password, sqlldrFileName);*/
						objectFromS3.getFile(obj.getMessageField("bucket"), obj.getMessageField("key"),action);
					}

				}
			}

		}
		//catch(JMSException jmsex)   // for JMS
		catch(Exception sqsex)
		{
			/*System.err.println("Error receiving from SQS: " + jmsex.getMessage());
			jmsex.printStackTrace();*/

			System.err.println("Error receiving from SQS: " + sqsex.getMessage());
			sqsex.printStackTrace();
		}



	}

	private void deleteMessage(String messageHandle)
	{
		if(messageHandle !=null && messageHandle.length() >0)
		{
			System.out.println("Deleting a message: " + messageHandle);
			deleteRequest = new DeleteMessageRequest(myQueueUrl, messageHandle);
			sqs.deleteMessage(deleteRequest);
		}
	}

	

}
