package org.dataloading.sharedsearch;

import java.io.BufferedWriter;

import org.apache.log4j.Logger;


public class ConcurrentSharedSearch implements Runnable{

	String thread;
	String value;
	String searchField;
	SharedSearchSearch sharedSearch;
	BufferedWriter bw;
	Logger logger;
	String midReturn;
	
	public ConcurrentSharedSearch(String thread, String value, String searchField, SharedSearchSearch sharedSearch, BufferedWriter bw, Logger logger, String midReturn) {
		this.thread = thread;
		this.value = value;
		this.searchField = searchField;
		this.sharedSearch = sharedSearch;
		this.bw = bw;
		this.logger = logger;
		this.midReturn = midReturn;
	}
	@Override
	public void run() {
		
		String query = "";
		if(midReturn.equalsIgnoreCase("none"))
			query = sharedSearch.buildESQuery(value,searchField);
		else if(midReturn.equalsIgnoreCase("mid"))
			query = sharedSearch.buildESQueryWithMIDReturn(value,searchField);
		
		sharedSearch.runESQuery(value, query, bw, logger);
		
	}

	
}
