package org.dataloading.sharedsearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;


public class ConcurrentSharedSearch implements Runnable{

	String thread;
	String value;
	String searchField;
	SharedSearchSearch sharedSearch;
	BufferedWriter bw;
	String outFileName;
	Logger logger;
	String midReturn;
	boolean isFacet;
	String prefix = "";
	String queryString;
	String after = "0";
	int counter = 0;
	
	public ConcurrentSharedSearch(String thread, String searchField, SharedSearchSearch sharedSearch, BufferedWriter bw, Logger logger, boolean isFacet) {
		this.thread = thread;
		this.searchField = searchField;
		this.sharedSearch = sharedSearch;
		this.bw = bw;
		this.logger = logger;
		this.isFacet = isFacet;
		
	}
	
	
	public ConcurrentSharedSearch(String thread, String value, String searchField, SharedSearchSearch sharedSearch, BufferedWriter bw, Logger logger, String midReturn, String queryString) {
		this.thread = thread;
		this.value = value;
		this.searchField = searchField;
		this.sharedSearch = sharedSearch;
		this.bw = bw;
		this.logger = logger;
		this.midReturn = midReturn;
		this.queryString = queryString;
	}
	
	/* for authorid and affiliationID Facet Search*/
	public ConcurrentSharedSearch(String thread, String value, String searchField, SharedSearchSearch sharedSearch, String outFileName, Logger logger, String midReturn, String queryString) {
		this.thread = thread;
		this.value = value;
		this.searchField = searchField;
		this.sharedSearch = sharedSearch;
		this.outFileName = outFileName;
		this.logger = logger;
		this.midReturn = midReturn;
		this.queryString = queryString;
	}
	
	
	@Override
	public void run()
	{
		System.out.println("Thread: " + thread);
		String prefix = value;
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName)))
		{
			while(after != null && !(after.isBlank()))
			{
				++counter;
				String query = sharedSearch.buildESQueryFacet(after,searchField, queryString);
				logger.info(query);
				
				after = sharedSearch.runESQuery("", query, bw, prefix);
				logger.info("after: " + after);
				
			}
			logger.info("quering SharedsSearch for : " + searchField + " are now complete with total# of iterations: " + counter);
			if(bw != null)
			{
				 bw.flush();
				 bw.close();
			}
			 
		}
		catch(IOException ex)
		{
			System.out.println("IOException in ConcurrentSharedSearch!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception ex)
		{
			System.out.println("Exception to run ConcurrentSharedSearch!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}		
	}
	
	/*
	 * @Override public void run() {
	 * 
	 * String query = ""; if(isFacet) query =
	 * sharedSearch.buildESQueryFacet(value,searchField,""); else if(midReturn ==
	 * null || midReturn.isBlank()) query =
	 * sharedSearch.buildESQuery(value,searchField); // for individual auid search
	 * else if(midReturn.equalsIgnoreCase("mid")) query =
	 * sharedSearch.buildESQueryWithMIDReturn(value,searchField);
	 * 
	 * sharedSearch.runESQuery(value, query, bw, prefix);
	 * 
	 * }
	 */

	
}
