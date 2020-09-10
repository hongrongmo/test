package org.dataloading.sharedsearch;

import java.io.BufferedWriter;

import org.apache.log4j.Logger;

/**
 * 
 * @author TELEBH
 * @Date: 09/01/2020
 * @Description: Same logic as ConcurrentSharedSearch except that running it sequentially, only one thread as multi-threads caused exception 
 * "javax.net.ssl.SSLHandshakeException: Remote host terminated the handshake", in order to run it for QA to run query for each individual batchinfo
 * 
 */
public class SequentialSharedSearch {

	String searchField;
	SharedSearchSearch sharedSearch;
	BufferedWriter bw;
	Logger logger;
	String midReturn;
	boolean isFacet;
	String prefix = "";
	
	public SequentialSharedSearch(String searchField, SharedSearchSearch sharedSearch, BufferedWriter bw, Logger logger, boolean isFacet) {
		this.searchField = searchField;
		this.sharedSearch = sharedSearch;
		this.bw = bw;
		this.logger = logger;
		this.isFacet = isFacet;
		
	}
	
	
	public SequentialSharedSearch(String searchField, SharedSearchSearch sharedSearch, BufferedWriter bw, Logger logger, String midReturn, String prefix) {
		this.searchField = searchField;
		this.sharedSearch = sharedSearch;
		this.bw = bw;
		this.logger = logger;
		this.midReturn = midReturn;
		this.prefix = prefix;
	}
	
	public void start(String value) {
		
		String query = "";
		if(isFacet)
			query = sharedSearch.buildESQueryFacet(value,searchField,"");
		else if(midReturn== null)
			query = sharedSearch.buildESQuery(value,searchField);   // for individual search
		else if(!midReturn.isBlank() && midReturn.equalsIgnoreCase("mid"))
			query = sharedSearch.buildESQueryWithMIDReturn(value,searchField);
		
		sharedSearch.runESQuery(value, query, bw, prefix);
		
	}
}
