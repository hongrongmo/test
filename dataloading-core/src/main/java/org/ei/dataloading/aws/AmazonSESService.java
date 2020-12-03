package org.ei.dataloading.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;



public class AmazonSESService{
	
	private static AmazonSESService instance;
	private AmazonSimpleEmailService snsclient;
	
	public AmazonSESService() {
	}

	public static AmazonSESService getInstance() {
		synchronized (AmazonSNSService.class) {
			if (instance == null)
				instance = new AmazonSESService();
			
			  instance.snsclient =  AmazonSimpleEmailServiceClientBuilder.standard()
		                 //.withCredentials(new EnvironmentVariableCredentialsProvider())
		                 .withRegion(Regions.US_EAST_1)
		                 .build();
			  
			 
			return instance;
		}
	}
	
	public AmazonSimpleEmailService getAmazonSNSClient() {
		return this.snsclient;
	}
}