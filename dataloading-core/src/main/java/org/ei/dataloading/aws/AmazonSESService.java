package org.ei.dataloading.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

/**
 * 
 * @author TELEBH
 * @Date: 11/13/2020
 * @Description :AWS SES Service Instance
 */

public class AmazonSESService{
	
	private static AmazonSESService instance;
	private AmazonSimpleEmailService sesClient;
	
	public AmazonSESService() {
	}

	public static AmazonSESService getInstance() {
		synchronized (AmazonSNSService.class) {
			if (instance == null)
				instance = new AmazonSESService();
			
			  instance.sesClient =  AmazonSimpleEmailServiceClientBuilder.standard()
		                 //.withCredentials(new EnvironmentVariableCredentialsProvider())
		                 .withRegion(Regions.US_EAST_1)
		                 .build();
			  
			 
			return instance;
		}
	}
	
	public AmazonSimpleEmailService getAmazonSESClient() {
		return this.sesClient;
	}
}