package org.ei.logging;

import java.util.LinkedList;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

import org.apache.log4j.Logger;
import org.ei.logservice.LogServer;
import org.junit.Assert;
import org.junit.Test;

public class CLFLoggerTest {
	
	 @Test
	 @SuppressWarnings({ "unused" })
	 public void enqueueTest() throws Exception{
		 new MockUp<CLFLogger>(){
			@Mock
			   public void $init(String name) {
				  // Do nothing
			   }
		 };
		 CLFLogger clfLogger = new CLFLogger();
		 Deencapsulation.setField(clfLogger, "logQueue",new LinkedList<CLFMessage>());
		
		 CLFMessage clfMessage1 = new CLFMessage("testMessage1");
		 CLFMessage clfMessage2 = new CLFMessage("testMessage2");
		 clfLogger.enqueue(clfMessage1);
		 clfLogger.enqueue(clfMessage2);
		 
		 LinkedList<CLFMessage>  clfMessages = Deencapsulation.getField(clfLogger, "logQueue");
		 Assert.assertEquals(2, clfMessages.size());
	 }
	 
	 @Test
	 @SuppressWarnings({ "unused"})
	 public void dequeueTest( ) throws Exception{
		 new MockUp<CLFLogger>(){
				@Mock
				   public void $init(String name) {
					  // Do nothing
				   }
		 };
		 final CLFLogger clfLogger = new CLFLogger();
		 new Expectations(){
			Logger logger;
		 {
			 logger.info(withSubstring("Checking the log queue, SIZE:"));
			 times = 1;
			 
			 Deencapsulation.setField(clfLogger, "log4j",logger);
		 }
		};
		LinkedList<CLFMessage> clfMessages = new LinkedList<CLFMessage>();
		clfMessages.add(new CLFMessage("testMessage1"));
		clfMessages.add(new CLFMessage("testMessage2"));
		Deencapsulation.setField(clfLogger, "logQueue",clfMessages);
		 
		CLFMessage[] messages = clfLogger.dequeue();
		Assert.assertEquals(2, messages.length);
		 
		LinkedList<CLFMessage> clfMssgs = Deencapsulation.getField(clfLogger, "logQueue");
		Assert.assertEquals(0, clfMssgs.size());
		 
	 }
	 
	 @Test
	 public void examplTes( ) throws Exception{
		 boolean isTrue = true;
		 Assert.assertTrue(isTrue);
	 }
	 
	 //@Mocked CLFLogger clogger;
	 
//	 @Test
//	 public void logServerExample( ) throws Exception{
//		
//		 final LogServer  logServer =  new  LogServer();
//		 new Expectations() {
//			 CLFLogger clogger;
//			 {
//				 clogger.shutdown();
//				 times =1;
//				 Deencapsulation.setField(logServer, "logger",clogger);
//			 }
//		};
//		 
//		 
//		 logServer.destroy();
//		 boolean isTrue = true;
//		 Assert.assertTrue(isTrue);
//	 }
}
