package org.ei.web.analytics;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

public class GAPostThread implements Runnable {
	private final PostMethod urlParameters;
	private final static Logger log4j = Logger.getLogger(GAPostThread.class);
	
	GAPostThread(PostMethod urlParams){
		this.urlParameters = urlParams;
	}
	@Override
	public void run() {
		    try{
		    	
				HttpClient httpClient = new HttpClient();
		    
				httpClient.executeMethod(urlParameters);
				String response = urlParameters.getResponseBodyAsString();
				if(response != null){
					log4j.info("SUCCESS GA Event recorded: " + urlParameters.getParameter("ec") + " with response: " + response);
				}
			}
			catch(IOException e)
			{
				log4j.error("FAILED to record Google Analytics Event");
			}finally{
				urlParameters.releaseConnection();
			}
		    
	}

}
