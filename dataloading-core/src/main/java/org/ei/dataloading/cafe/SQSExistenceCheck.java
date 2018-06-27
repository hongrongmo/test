package org.ei.dataloading.cafe;


import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.*;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazonaws.services.sqs.model.Message;

/**
 * A utility function to check the queue exists and create it if needed.For most
 * use cases this will usually be done by an administrator before the application
 * is run.
 */

public class SQSExistenceCheck {


	public static void ensureQueueExists(SQSConnection connection, String queueName) throws JMSException {
		AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
		/**
		 * For most cases this could be done with just a createQueue call, but GetQueueUrl
		 * (called by queueExists) is a faster operation for the common case where the queue
		 * already exists. Also many users and roles have permission to call GetQueueUrl
		 * but do not have permission to call CreateQueue.
		 */
		if( !client.queueExists(queueName) )
		{
			System.out.println("Queue not exist");
		}
		else
		{
			System.out.println("Queue Exist");
		}
	}
	public static void setupLogging() {
		// Setup logging
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
	}
	public static void handleMessage(Message message) throws JMSException {
		System.out.println( "Got message " + message.getMessageId() );
		System.out.println( "Content: ");
		if( message instanceof TextMessage ) {
			TextMessage txtMessage = ( TextMessage ) message;
			System.out.println( "\t" + txtMessage.getText() );
		} 
		else if( message instanceof BytesMessage ){
			BytesMessage byteMessage = ( BytesMessage ) message;
			// Assume the length fits in an int - SQS only supports sizes up to 256k so that
			// should be true
			byte[] bytes = new byte[(int)byteMessage.getBodyLength()];

			byteMessage.readBytes(bytes);
			System.out.println( "\t" + Base64.decodeBase64(bytes));
		} 
		else if( message instanceof ObjectMessage ) {
			ObjectMessage objMessage = (ObjectMessage) message;
			System.out.println( "\t" + objMessage.getObject() );
		}
	}


}
