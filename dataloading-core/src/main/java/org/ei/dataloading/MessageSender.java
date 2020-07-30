package org.ei.dataloading;

import org.ei.util.kafka.KafkaService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;


public class MessageSender implements Runnable {
  EVCombinedRec[] recArray;
  EVCombinedRec rec;
  KafkaService kafka;
  String key;
  String message;
  CombinedWriter writer;
  Map<String,String> batchData = new ConcurrentHashMap<String,String>();   
  Map<String,String> missedData = new ConcurrentHashMap<String,String>();
  
  public MessageSender(EVCombinedRec[] recArray, KafkaService kafka, CombinedWriter writer) {
    this.rec = null; 
    this.recArray = recArray;
    this.kafka = kafka;
    this.writer = writer;
  } 
 
  
  public MessageSender(EVCombinedRec rec, KafkaService kafka, CombinedWriter writer) {
    this.rec = rec;
    this.kafka = kafka;
    this.writer = writer;
  }

  public MessageSender( KafkaService kafka, String key, String message) {
	  this.kafka = kafka;
	  this.key=key;
	  this.message=message;
  }
  
  public MessageSender( KafkaService kafka, Map batchData, Map missedData) {
	  this.kafka = kafka;
	  this.batchData = batchData;
	  this.missedData = missedData;
  } 
  
  public void run() {
	  try
	  {
		  //this.kafka.runProducer(this.message,"\""+this.key+"\"",0,new HashMap());
		  this.kafka.runBatch(batchData,missedData);
	  }
	  catch (Exception e) 
	  {	      
	      e.printStackTrace();
	  } 
	  finally
	  {
		  this.kafka.flush();
	  }
  }
  
  /*
  public void run() {
    try {
      if (this.rec != null)
      {
        this.writer.writeRec(this.rec, this.kafka);
        this.kafka.flush();
      }
      else if (this.recArray != null)
      {
        this.writer.writeRec(this.recArray, this.kafka);
        this.kafka.flush();
      }
      else
      {
        System.out.println("NOTHING to SEND");
      }
    
    }
    catch (Exception e) {
      
      e.printStackTrace();
    } 
  }
  */
}

