package org.ei;

import java.io.File;
import java.io.IOException;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.PropertiesCredentials;


import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public final class AmazonS3Service {

	private static AmazonS3Service instance;

	private AmazonS3 s3service;
	
	private static int S3_CONN_TIMEOUT = 60 * 1000;  //1 minute timeout
	private static int S3_SOCK_TIMEOUT = 60 * 1000;  // 1 minute timeout
			
	private static ClientConfiguration clientConfig;
	
	public AmazonS3Service()
	{
		
	}
	
	public static AmazonS3Service getInstance()
	{
		if(instance == null)
		{
			clientConfig = new ClientConfiguration();
			clientConfig.setConnectionTimeout(S3_CONN_TIMEOUT);
			clientConfig.setSocketTimeout(S3_SOCK_TIMEOUT);
			
			
			instance = new AmazonS3Service();
			 Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		
			
			 System.out.println("Creating Amazon S3 client...");
			 try
			 {

				 instance.s3service = new AmazonS3Client(new PropertiesCredentials(new File("credentials.properties")),clientConfig);
				 instance.s3service.setRegion(usEast1);
				 
			 } catch (IllegalArgumentException e) {
				 
				 e.printStackTrace();
			 } catch (IOException e) {
				 
				 e.printStackTrace();
			 }
		}

		return instance;
	}
	
	
	
	 public AmazonS3 getAmazonS3Service() {
			return this.s3service;
		    }
	 
}
