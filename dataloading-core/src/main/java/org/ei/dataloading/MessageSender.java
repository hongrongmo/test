package org.ei.dataloading;

import org.ei.util.kafka.KafkaService;

<<<<<<< HEAD
//Thread class          
public class MessageSender implements Runnable
{
	EVCombinedRec[] recArray=null;
	EVCombinedRec rec=null;
	KafkaService kafka=null;
	CombinedWriter writer=null;
    public MessageSender(EVCombinedRec[] recArray,KafkaService kafka, CombinedWriter writer)
    {
    	 this.recArray=recArray;
    	 this.kafka = kafka;
    	 this.writer = writer;
    //store in class level variable in thread class
    }
    
    public MessageSender(EVCombinedRec rec,KafkaService kafka, CombinedWriter writer)
    {
   	 this.rec=rec;
   	 this.kafka = kafka;
   	 this.writer = writer;
   //store in class level variable in thread class
   }
    public void run(){
    	try
    	{
    		if(this.rec!=null)
    		{
    			this.writer.writeRec(this.rec,this.kafka);
	    		this.kafka.flush();
    		}
    		else if(this.recArray!=null)
    		{
	    		this.writer.writeRec(this.recArray,this.kafka);
	    		this.kafka.flush();
    		}
    		else
    		{
    			System.out.println("NOTHING to SEND");
    		}
    		//System.out.println("runing thread");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
=======
public class MessageSender implements Runnable {
  EVCombinedRec[] recArray;
  EVCombinedRec rec;
  
  public MessageSender(EVCombinedRec[] recArray, KafkaService kafka, CombinedWriter writer) {
    this.recArray = null;
    this.rec = null;
    this.kafka = null;
    this.writer = null;

    
    this.recArray = recArray;
    this.kafka = kafka;
    this.writer = writer;
  } KafkaService kafka; CombinedWriter writer; public MessageSender(EVCombinedRec rec, KafkaService kafka, CombinedWriter writer) {
    this.recArray = null;
    this.rec = null;
    this.kafka = null;
    this.writer = null;
    this.rec = rec;
    this.kafka = kafka;
    this.writer = writer;
  }

  
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
}
>>>>>>> branch 'NYC-staging' of https://moh@gitlab.et-scm.com/rap-ev/EngineeringVillage.git
