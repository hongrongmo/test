package org.ei.util.kafka;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.*;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.*;
import org.ei.util.kafka.*;


public class KafkaService {
	public String KAFKA_BROKERS="localhost:9092";
	public String TOPIC_NAME="EV";
	public Integer MESSAGE_COUNT=1000;
    public String CLIENT_ID="client1";
    public String GROUP_ID_CONFIG="consumerGroup1";
    public Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
    public String OFFSET_RESET_LATEST="latest";
    public String OFFSET_RESET_EARLIER="earliest";
    public Integer MAX_POLL_RECORDS=1;
    public HashMap<String,String> problemRecords;
    Producer<String, String> producer=null;
    Logger logger = Logger.getLogger(KafkaService.class.getName());
    FileHandler fh; 
    String propertyFileName;
    String logFileName;
    
    public KafkaService() 
    {
    	propertyFileName = "./lib/config.properties";
    	logFileName = "MyLogFile.log";
    	init(logFileName,propertyFileName);
    }
    
    public KafkaService(String processInfo) 
    {
    	propertyFileName = "./lib/config.properties";
    	logFileName = processInfo+"_kafka.log";
    	init(logFileName,propertyFileName);
    }
    
    public KafkaService(String processInfo, String propertyFileName) 
    {
    	this.logFileName = processInfo+"_kafka.log";
    	this.propertyFileName = propertyFileName;
    	init(logFileName,propertyFileName);
    }
    
    public void init(String logFileName, String propertyFileName)
    {
    	getParameterFromPropertiesFile(propertyFileName);	
    	producer  = ProducerCreator.createProducer(this.KAFKA_BROKERS);
    	System.out.println("create Kafka Prodcuer");
    	try
    	{
	    	problemRecords = new HashMap<String,String>();
	    	fh = new FileHandler(logFileName);  
	        logger.addHandler(fh);	        
    	}
    	catch (Exception e) 
    	{
    		 e.printStackTrace();
    	}
    }
    
    public Producer<String, String> getProducer()
    {
    	Producer<String, String> producer  = ProducerCreator.createProducer(this.KAFKA_BROKERS);
    	return producer;
    }
    public static void main(String[] args) {
    	KafkaService kTest= new KafkaService();
    	//kTest.getParameterFromPropertiesFile("./lib/config.properties");
    	if(args!=null && args[0].equals("producer"))
    	{
    		kTest.runProducer();
    	}
    	else if (args!=null && args[0].equals("consumer"))
		{
			kTest.runConsumer();
		}
		else
		{
			System.out.println("invalid input");
			System.out.println("please enter either producer or consumer");
		}
    }
    public void runConsumer() {
        Consumer<String, String> consumer = ConsumerCreator.createConsumer();
        int noMessageFound = 0;
        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(1000);
            // 1000 is the time in milliseconds consumer will wait if no record is found at broker.
            if (consumerRecords.count() == 0) {
                noMessageFound++;
                if (noMessageFound > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
                    // If no message found count is reached to threshold exit loop.
                    break;
                else
                    continue;
            }
            //print each record.
            consumerRecords.forEach(record -> {
                System.out.println("Record Key " + record.key());
                System.out.println("Record value " + record.value());
                System.out.println("Record partition " + record.partition());
                System.out.println("Record offset " + record.offset());
            });
            // commits the offset of record to broker.
            consumer.commitAsync();
        }
        consumer.close();
    }
    public void runProducer() {
        Producer<String, String> producer = ProducerCreator.createProducer(this.KAFKA_BROKERS);
        for (int index = 0; index < IKafkaConstants.MESSAGE_COUNT; index++) {
            
        	ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.TOPIC_NAME,"key-"+Integer.toString(index), "This is test record " + index);
        	try {
                RecordMetadata metadata = producer.send(record).get();
               
                System.out.println("Record sent with key " + Integer.toString(index) + " to partition " + metadata.partition() + " with offset " + metadata.offset());
            }
            catch (ExecutionException e) {
                System.out.println("Error in sending record");
                System.out.println(e);
            }
            catch (InterruptedException e) {
                System.out.println("Error in sending record");
                System.out.println(e);
            }
        	producer.flush();
        	
        }
        
        producer.close();
    }
    
    public void getParameterFromPropertiesFile(String filename)
    {
	    
		try (InputStream input = new FileInputStream(filename)) {
	
	        Properties prop = new Properties();
	
	        // load a properties file
	        prop.load(input);
	        this.KAFKA_BROKERS=prop.getProperty("KAFKA_BROKERS");
	        this.TOPIC_NAME=prop.getProperty("TOPIC_NAME");
	        // get the property value and print it out
	      
	
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
    }
    
    //public void runProducer(String recordString, String key, Producer<String, String> producer) {
    public void runProducer(String recordString, String key,int reSending) throws Exception
    {
        //Producer<String, String> producer = ProducerCreator.createProducer(this.KAFKA_BROKERS);
    	
    	if(this.producer==null)
    	{
    		System.out.println("producer is null");
    		this.producer = ProducerCreator.createProducer(this.KAFKA_BROKERS);
    	}
    	
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.TOPIC_NAME,key, recordString);
		
		try {
		    //RecordMetadata metadata = this.producer.send(record).get();    
		    this.producer.send(record, new Callback() {

				@Override
				public void onCompletion(RecordMetadata metadata, Exception exception)
				{
					if (exception == null) 
					{
						logger.info("Received record "+key+" metadata(" + "Topic: " + metadata.topic() + "\t"
								+ "Partition: " + metadata.partition() + "\t" + "Offset: " + metadata.offset()+")");
					} 
					else 
					{
						if(reSending>4)
						{
							problemRecords.put(key, recordString);
						}
						else
						{
							try
							{
								runProducer(recordString,key,reSending+1);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
						
						logger.info("Resend record "+key+ " for "+reSending+1+" times");
					}

				}
			});
		}		
		catch(Exception e)
		{	
			problemRecords.put(key, recordString);
			logger.info("Exception in sending record; "+key);
			e.printStackTrace();			
		}
		
    }
    
    public void flush()
    {
    	this.producer.flush();
    }
    
    public void close() {
    	reSendProblemRecords();   	
    	this.producer.flush();
    	//this.producer.close();
    }
    
    private void reSendProblemRecords()
    {
    	if(problemRecords !=null && problemRecords.size()>0)
    	{
    		System.out.println("\n**** PROBLEM FOUND, Resending "+problemRecords.size()+" records ****\n");
    		for (Map.Entry me : problemRecords.entrySet()) 
    		{
    			try
    			{
	    			System.out.println("\nSending problem record; Key:: "+me.getKey() + " and Value:: " + me.getValue());
	    			runProducer((String)me.getValue(),(String)me.getKey(),5);
	    			this.producer.flush();
    			}
    			catch(Exception e)
    			{
    				System.out.println(e);
    			}
    	        
    	    }
    	}
    	else
    	{
    		System.out.println("No problem record to resend");
    	}
    }
    
}
