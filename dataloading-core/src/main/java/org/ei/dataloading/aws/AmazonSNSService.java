package org.ei.dataloading.aws;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

/**
 * 
 * @author TELEBH
 * @Date: 09/10/2020
 * @Description :AWS SNS Service Instance
 */
public class AmazonSNSService {

	private static AmazonSNSService instance;
	private AmazonSNS snsclient;

	private AmazonSNSService() {
	}

	public static AmazonSNSService getInstance() {
		synchronized (AmazonSNSService.class) {
			if (instance == null)
				instance = new AmazonSNSService();
			
			  instance.snsclient =  AmazonSNSClientBuilder
		                 .standard()
		                 //.withCredentials(new EnvironmentVariableCredentialsProvider())
		                 .withRegion(Regions.US_EAST_1)
		                 .build();
			  
			 
			return instance;
		}
	}

	public AmazonSNS getAmazonSNSClient() {
		return this.snsclient;
	}

}
