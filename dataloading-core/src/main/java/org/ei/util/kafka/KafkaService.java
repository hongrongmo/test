package org.ei.util.kafka;


import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.*;
import java.util.Iterator;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.*;
import org.ei.util.kafka.*;
import java.util.concurrent.ConcurrentHashMap;

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
    public ConcurrentHashMap<String,String> problemRecords;
    
    Producer<String, String> producer=null;
    Logger logger = Logger.getLogger(KafkaService.class.getName());
    FileHandler fh; 
    String propertyFileName;
    String logFileName;
    
    public KafkaService() 
    {
    	this.propertyFileName = "./lib/config.properties";
    	this.logFileName = "MyLogFile.log";
    	//init(logFileName,propertyFileName);
    }
    
    public KafkaService(String processInfo) 
    {
    	this.propertyFileName = "./lib/config.properties";
    	this.logFileName = processInfo+"_kafka.log";
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
    	System.out.println("create Kafka Prodcuer:: "+this.KAFKA_BROKERS);
    	try
    	{
	    	problemRecords = new ConcurrentHashMap<String,String>();
	    	//fh = new FileHandler(logFileName);  
	        //logger.addHandler(fh);	        
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
                saveIntoFile(record.key(),record.value());
            });
            // commits the offset of record to broker.
            consumer.commitAsync();
        }
        consumer.close();
    }
    
    private void saveIntoFile(String key, String value) 
    {
    	FileWriter out = null;
    	try
    	{
	    	 out = new FileWriter(key+".json");
	    	 out.write(value);
	    	 out.flush();
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		if(out!=null)
    		{
    			try
    			{
    				out.close();
    			}
    			catch(Exception er)
    	    	{
    	    		er.printStackTrace();
    	    	}
    		}
    	}
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
    
    public void runLastBatch(Map batchData, Map missedData)
    {
    	Set keys = batchData.keySet();
    	Iterator i = keys.iterator();
    	try
    	{
	    	while (i.hasNext()) {
	    	   String key = (String)i.next();
	    	   if(key!=null)
	    	   {
	    		   System.out.println("run key "+key+" again");
		    	   String value=(String)batchData.get(key);		    	  
		    	   runProducer(value,key,0,missedData);
		    	   
	    	   }
	    	   else
	    	   {
	    		   System.out.println("run null key again");
	    	   }
	    	}
	    	
	    	//flush();
	    	
	    	if(missedData.size()>0)
	    	{
	    		//System.out.println("rerun again size="+missedData.size());
	    		Map<String, String> copyData = new ConcurrentHashMap<String, String>();
	    		keys = missedData.keySet();
	    		i = keys.iterator();
	    		while (i.hasNext()) {
	 	    	  String key = (String)i.next();
	 	    	 System.out.println("rerun k "+key);
	 	    	  if(key!=null)
		    	   {
		    		   System.out.println("missed data key "+key+" again");
			    	   String value=(String)missedData.get(key);
			    	   if(value!=null)
			    		   copyData.put(key,value);	
			    	   else
			    		   System.out.println("no value for key "+key);
			    	   
		    	   }
		    	   else
		    	   {
		    		   System.out.println("no key");
		    	   }
	    		}
	    		
	    		if(copyData.size()>0)
	    			runLastBatch(copyData,missedData);
	    		else
	    			System.out.println("no more data");
	    		
	    	}
    	}
    	catch (Exception ex) 
    	{
	        ex.printStackTrace();
	    }
    	
    	
    }
    
    public void runBatch(Map batchData, Map missedData) throws Exception
    {
    	//System.out.println("inside runBatch");
    	Set keys = batchData.keySet();
    	Iterator i = keys.iterator();
    	while (i.hasNext()) {
    	   String key = (String)i.next();
    	   //System.out.println("inside while key="+key);
    	   if(key!=null)
    	   {
	    	   String value=(String)batchData.get(key);
	    	   runProducer(value,key,0,missedData);
    	   }
    	}
    	
    }
    public void runProducer(String recordString, String key,int reSending, Map missedData) throws Exception
    {
        //Producer<String, String> producer = ProducerCreator.createProducer(this.KAFKA_BROKERS);
    	
    	if(this.producer==null)
    	{
    		System.out.println("producer is null");
    		this.producer = ProducerCreator.createProducer(this.KAFKA_BROKERS);
    	}
    	
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.TOPIC_NAME,key, recordString);
		
		try {
			//System.out.println("inside runProducer.send");    
		    this.producer.send(record, new Callback() {		    	
				@Override
				public void onCompletion(RecordMetadata metadata, Exception exception)
				{
					//HH commented 11/03/2020 for making log file readable
					/*if (exception == null) 
					{
						//missedData.remove(key);
						//logger.info("Received record "+key+" metadata(" + "Partition: " + metadata.partition() + "\t" + "Offset: " + metadata.offset()+")");
					} */
					//else
					if(exception != null)
					{
						logger.info("Resend record "+key+ " for "+reSending+1+" times because Exception"+exception);
						if(reSending>4)
						{
							problemRecords.put(key, recordString);
						}
						else
						{
							try
							{
								runProducer(recordString,key,reSending+1,new ConcurrentHashMap());
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
						
						
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
    
    public void close(Map missedData) 
    { 
    	System.out.println("missed "+missedData.size()+" records");
    	Map<String,String> copyData = new ConcurrentHashMap<String,String>();
    	copyData.putAll(missedData);
    	//runLastBatch(copyData, missedData);   	
    	reSendProblemRecords();   	
    	this.producer.flush();
    	//this.producer.close();
    }
    
    private void reSendProblemRecords()
    {
    	if(problemRecords !=null && problemRecords.size()>0)
    	{
    		System.out.println("\n**** PROBLEM FOUND, Resending "+problemRecords.size()+" records ****\n");
    		Map<String,String> missedData = new ConcurrentHashMap<String,String>();
    		missedData.putAll(problemRecords);
    		for (Map.Entry me : problemRecords.entrySet()) 
    		{
    			try
    			{
	    			System.out.println("\nSending problem record; Key:: "+me.getKey() + " and Value:: " + me.getValue());
	    			runProducer((String)me.getValue(),(String)me.getKey(),5,missedData);
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
