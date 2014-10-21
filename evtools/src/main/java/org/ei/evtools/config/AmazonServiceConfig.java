package org.ei.evtools.config;

import org.apache.log4j.Logger;
import org.ei.evtools.exception.AWSAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Configuration
@PropertySource("classpath:AwsCredentials.properties")
public class AmazonServiceConfig {

	static Logger logger = Logger.getLogger(AmazonServiceConfig.class);
	
	private static int S3_CONN_TIMEOUT = 5 * 1000;     // 5 second timeout
    private static int S3_SOCK_TIMEOUT = 5 * 1000;     // 5 second timeout
	
	@Autowired
	private Environment environment; 
	
	@Bean(name="amazonDynamoDBService")
    public AmazonDynamoDBClient getAmazonDynamoDBService() throws AWSAccessException{
		
		AmazonDynamoDBClient amazonDynamoDBClient = null;
		try{
			amazonDynamoDBClient = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider("/AwsCredentials.properties"));
			logger.info("Amazon DynamoDB Service bean has been successfully created!");
		}catch(Exception exp){
			logger.error("Amazon DynamoDB Service bean creation failed, exception occured: "+exp.getMessage());
			throw new AWSAccessException("AWS access exception thrown while creating dynamo db service bean -------"+exp.getMessage(),  exp);
		}
		return amazonDynamoDBClient;
	}
	
	@Bean(name="amazonS3Service")
    public AmazonS3Client getAmazonS3Service() throws AWSAccessException{
		
		AmazonS3Client amazonS3Client = null;
		try{
			Region region = Region.getRegion(Regions.US_EAST_1);
			ClientConfiguration  clientconfig = new ClientConfiguration();
	        clientconfig.setConnectionTimeout(S3_CONN_TIMEOUT);
	        clientconfig.setSocketTimeout(S3_SOCK_TIMEOUT);
	        amazonS3Client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider("/AwsCredentials.properties"), clientconfig);
	        amazonS3Client.setRegion(region);
			logger.info("Amazon S3 Service bean has been successfully created!");
		}catch(Exception exp){
			logger.error("Amazon S3 Service bean creation failed, exception occured: "+exp.getMessage());
			throw new AWSAccessException("AWS access exception thrown while creating S3 service bean -------"+exp.getMessage(),  exp);
		}
		return amazonS3Client;
	}
	
	@Bean(name="amazonSESClient")
    public AmazonSimpleEmailServiceClient getAmazonSESClient()  throws AWSAccessException{
		
		AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient = null;
		try{
			Region region = Region.getRegion(Regions.US_EAST_1);
			ClientConfiguration  clientconfig = new ClientConfiguration();
	        clientconfig.setConnectionTimeout(S3_CONN_TIMEOUT);
	        clientconfig.setSocketTimeout(S3_SOCK_TIMEOUT);
	        amazonSimpleEmailServiceClient = new AmazonSimpleEmailServiceClient(new ClasspathPropertiesFileCredentialsProvider("/AwsCredentials.properties"));;
	        amazonSimpleEmailServiceClient.setRegion(region);
			logger.info("Amazon Simple Email Service bean has been successfully created!");
		}catch(Exception exp){
			logger.error("Amazon Simple Email Service bean creation failed, exception occured: "+exp.getMessage());
			throw new AWSAccessException("AWS access exception thrown while creating simple email service bean -------"+exp.getMessage(),  exp);
		}
		return amazonSimpleEmailServiceClient;
	}
}
