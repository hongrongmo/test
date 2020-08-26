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
	boolean isFacet;
	String prefix = "";
	
	public ConcurrentSharedSearch(String thread, String searchField, SharedSearchSearch sharedSearch, BufferedWriter bw, Logger logger, boolean isFacet) {
		this.thread = thread;
		this.searchField = searchField;
		this.sharedSearch = sharedSearch;
		this.bw = bw;
		this.logger = logger;
		this.isFacet = isFacet;
		
	}
	
	
	public ConcurrentSharedSearch(String thread, String value, String searchField, SharedSearchSearch sharedSearch, BufferedWriter bw, Logger logger, String midReturn, String prefix) {
		this.thread = thread;
		this.value = value;
		this.searchField = searchField;
		this.sharedSearch = sharedSearch;
		this.bw = bw;
		this.logger = logger;
		this.midReturn = midReturn;
		this.prefix = prefix;
	}
	@Override
	public void run() {
		
		String query = "";
		if(isFacet)
			query = sharedSearch.buildESQueryFacet(value,searchField,"");
		else if(midReturn.equalsIgnoreCase("none"))
			query = sharedSearch.buildESQuery(value,searchField);   // for individual auid search
		else if(midReturn.equalsIgnoreCase("mid"))
			query = sharedSearch.buildESQueryWithMIDReturn(value,searchField);
		
		sharedSearch.runESQuery(value, query, bw, prefix);
		
	}

	
}
