package org.ei.dataloading.cafe.test;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.ei.dataloading.cafe.SQSConfiguration;
import org.ei.dataloading.cafe.SQSExistenceCheck;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.model.Message;

public class TestSyncMessageReceiverClientAcknowledge {

	/**
	 * An example class to demonstrate the behavior of CLIENT_ACKNOWLEDGE mode for received messages. 
	 *
	 * First, a session, a message producer, and a message consumer are created. Then, two messages are sent. Next, two messages
	 * are received but only the second one is acknowledged. After waiting for the visibility time out period, an attempt to
	 * receive another message is made. It is shown that no message is returned for this attempt since in CLIENT_ACKNOWLEDGE mode,
	 * as expected, all the messages prior to the acknowledged messages are also acknowledged.
	 */

	    // Visibility time-out for the queue. It must match to the one set for the queue for this example to work.
	    private static final long TIME_OUT_SECONDS = 30;
	    
	   
	    private static HashMap<String, String> queueArgs = new HashMap<String,String>();
	    public static void main(String args[]) throws JMSException, InterruptedException {
	    	
	    	/* queueArgs[0] = "--queue EVCAFE";
	 	     queueArgs[1] = "--region US_EAST_1";*/

	    	queueArgs.put("--queue", "EVCAFE");
	    	queueArgs.put("--region","us-east-1");
	 	    		
	        // Create the configuration for the example
	    	SQSConfiguration config = SQSConfiguration.parseConfig("TestSyncMessageReceiverClientAcknowledge", queueArgs);

	        // Setup logging for the example
	        SQSExistenceCheck.setupLogging();

	        // Create the connection factory based on the config
	        SQSConnectionFactory connectionFactory =SQSConnectionFactory.builder()
	                        .withRegion(config.getRegion())
	                        .withAWSCredentialsProvider(config.getCredentialsProvider())
	                        .build();

	        // Create the connection
	        SQSConnection connection = connectionFactory.createConnection();

	        // Create the queue if needed
	        SQSExistenceCheck.ensureQueueExists(connection, config.getQueueName());

	        // Create the session  with client acknowledge mode
	        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

	        // Create the consumer 
	        MessageConsumer consumer = session.createConsumer(session.createQueue(config.getQueueName()));

	        // Open the connection
	        connection.start();

	        
	        // Receive a message and don't acknowledge it
	        receiveMessage(consumer, false);

	        // Receive another message and acknowledge it
	        receiveMessage(consumer, true);

	        // Wait for the visibility time out, so that unacknowledged messages reappear in the queue
	        System.out.println("Waiting for visibility timeout...");
	        Thread.sleep(TimeUnit.SECONDS.toMillis(TIME_OUT_SECONDS));

	        // Attempt to receive another message and acknowledge it. This will result in receiving no messages since
	        // we have acknowledged the second message. Although we did not explicitly acknowledge the first message,
	        // in the CLIENT_ACKNOWLEDGE mode, all the messages received prior to the explicitly acknowledged message
	        // are also acknowledged. Therefore, we have implicitly acknowledged the first message.
	        receiveMessage(consumer, true);

	        // Close the connection. This will close the session automatically
	        connection.close();
	        System.out.println("Connection closed.");
	    }

	  
	    /**
	     * Receives a message through the consumer synchronously with the default timeout (TIME_OUT_SECONDS).
	     * If a message is received, the message is printed. If no message is received, "Queue is empty!" is
	     * printed.
	     *
	     * @param consumer Message consumer
	     * @param acknowledge If true and a message is received, the received message is acknowledged.
	     * @throws JMSException
	     */
	    private static void receiveMessage(MessageConsumer consumer, boolean acknowledge) throws JMSException {
	        // Receive a message
	        javax.jms.Message message = consumer.receive(TimeUnit.SECONDS.toMillis(TIME_OUT_SECONDS));

	        if (message == null) {
	            System.out.println("Queue is empty!");
	        } else {
	            // Since this queue has only text messages, cast the message object and print the text
	            System.out.println("Received: " + ((TextMessage) message).getText());

	            // Acknowledge the message if asked
	            if (acknowledge) message.acknowledge();
	        }
	    }
	
}
