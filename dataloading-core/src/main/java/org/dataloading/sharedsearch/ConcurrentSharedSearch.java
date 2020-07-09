package org.dataloading.sharedsearch;

import java.io.BufferedWriter;

public class ConcurrentSharedSearch implements Runnable{

	String thread;
	String value;
	String searchField;
	SharedSearchSearch sharedSearch;
	BufferedWriter bw;
	
	public ConcurrentSharedSearch(String thread, String value, String searchField, SharedSearchSearch sharedSearch, BufferedWriter bw) {
		this.thread = thread;
		this.value = value;
		this.searchField = searchField;
		this.sharedSearch = sharedSearch;
		this.bw = bw;
		
	}
	@Override
	public void run() {
		String query = sharedSearch.buildESQuery(value,searchField);
		sharedSearch.runESQuery(value, query, bw);
		
	}

	
}
