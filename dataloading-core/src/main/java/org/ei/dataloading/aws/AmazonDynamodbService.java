package org.ei.dataloading.aws;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

/**
 * 
 * @author TELEBH
 * @Date: 01/28/2021
 * @Description: AWS DynamoDB service Instance
 */
public class AmazonDynamodbService {
	private static AmazonDynamodbService instance;
	private AmazonDynamoDB dynamodbclient;
	private AmazonDynamodbService() {
		
	}

	public static AmazonDynamodbService getInstance()
	{
		synchronized (AmazonDynamodbService.class) {
			if(instance == null)
				instance = new AmazonDynamodbService();
			
			instance.dynamodbclient = AmazonDynamoDBClientBuilder.standard()
					//.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("us-east-1"))
					.withRegion(Regions.US_EAST_1)
					.build();
			return instance;
		}
	}
	
	public AmazonDynamoDB getAmazonDynamodbClient()
	{
		return this.dynamodbclient;
	}
}
