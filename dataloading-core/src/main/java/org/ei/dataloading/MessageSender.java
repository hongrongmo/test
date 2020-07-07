package org.ei.dataloading;

import org.ei.util.kafka.KafkaService;


public class MessageSender implements Runnable {
  EVCombinedRec[] recArray;
  EVCombinedRec rec;
  KafkaService kafka;
  String key;
  String message;
  
  public MessageSender(EVCombinedRec[] recArray, KafkaService kafka, CombinedWriter writer) {
    this.recArray = null;
    this.rec = null;
    this.kafka = null;
    this.writer = null;

    
    this.recArray = recArray;
    this.kafka = kafka;
    this.writer = writer;
  } 
 
  CombinedWriter writer; 
  public MessageSender(EVCombinedRec rec, KafkaService kafka, CombinedWriter writer) {
    this.recArray = null;
    this.rec = null;
    this.kafka = null;
    this.writer = null;
    this.rec = rec;
    this.kafka = kafka;
    this.writer = writer;
  }

  public MessageSender( KafkaService kafka, String key, String message) {
	  this.kafka = kafka;
	  this.key=key;
	  this.message=message;
  }
  
  public void run() {
	  try
	  {
		  this.kafka.runProducer(this.message,"\""+this.key+"\"",false);
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

