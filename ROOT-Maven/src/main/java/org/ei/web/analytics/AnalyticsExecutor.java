package org.ei.web.analytics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * singleton instance to executor queue for analytics events recorded from the server side
 * @author robbinrs
 *
 */
public class AnalyticsExecutor {
	private final static Logger log4j = Logger.getLogger(AnalyticsExecutor.class);
	private static AnalyticsExecutor instance = null;
	private ExecutorService executor = null;
	private static final int NTHREADS = 5;
	
	private AnalyticsExecutor(){
		if(this.executor == null){
			this.executor = Executors.newFixedThreadPool(NTHREADS);
		}
		
	}
	
	public static AnalyticsExecutor getInstance(){
		synchronized (AnalyticsExecutor.class){
			if(instance == null){
				instance = new AnalyticsExecutor();
			}
		}
		return instance;
	}
	/**
	 * Adds event to the executor queue
	 */
	public void addEvent(Runnable event){

		this.executor.execute(event);
		
	}
	/**
	 * shutdown executor and wait for all threads to complete or 20 seconds
	 * whichever happens first
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException{
		if(this.executor != null){
			this.executor.shutdown();
			this.executor.awaitTermination(30, TimeUnit.SECONDS);
			
			
		}
	}	
}
