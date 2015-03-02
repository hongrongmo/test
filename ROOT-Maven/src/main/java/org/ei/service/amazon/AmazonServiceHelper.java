package org.ei.service.amazon;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;

/**
 * Helper class to get Amazon Service references
 */
public final class AmazonServiceHelper {

	private final static Logger log4j = Logger.getLogger(AmazonServiceHelper.class);

	private static AmazonServiceHelper instance;
	private AmazonDynamoDBClient dynamodbclient;
    private AmazonS3 s3service;
    private AmazonSimpleEmailServiceClient sesclient;
    private AmazonSNSAsyncClient snsclient;
    private AmazonCloudWatchAsyncClient cloudwatchclient;

    private static int S3_CONN_TIMEOUT = 5 * 1000;     // 5 second timeout
    private static int S3_SOCK_TIMEOUT = 5 * 1000;     // 5 second timeout
    private static ClientConfiguration clientconfig;

	private AmazonServiceHelper() {};

	public static AmazonServiceHelper getInstance() {
	    if (instance == null) {
	        clientconfig = new ClientConfiguration();
	        clientconfig.setConnectionTimeout(S3_CONN_TIMEOUT);
	        clientconfig.setSocketTimeout(S3_SOCK_TIMEOUT);

	        instance = new AmazonServiceHelper();
            Region usEast1 = Region.getRegion(Regions.US_EAST_1);

	        log4j.info("Creating Amazon DynamoDB client...");
	        instance.dynamodbclient = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider("/AwsCredentials.properties"));

	        log4j.info("Creating Amazon S3 client...");
	        instance.s3service = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider("/AwsCredentials.properties"), AmazonServiceHelper.clientconfig);
	        instance.s3service.setRegion(usEast1);

            log4j.info("Creating Amazon SES client...");
	        instance.sesclient = new AmazonSimpleEmailServiceClient(new ClasspathPropertiesFileCredentialsProvider("/AwsCredentials.properties"));
            instance.sesclient.setRegion(usEast1);

            log4j.info("Creating Amazon SNS client...");
            instance.snsclient = new AmazonSNSAsyncClient(new ClasspathPropertiesFileCredentialsProvider("/AwsCredentials.properties"));

            log4j.info("Creating Amazon CloudWatch client...");
            instance.cloudwatchclient = new AmazonCloudWatchAsyncClient(new ClasspathPropertiesFileCredentialsProvider("/AwsCredentials.properties"));
	    }
	    return instance;
	}

	public AmazonS3 getAmazonS3Service() {
        return this.s3service;
	}

	public AmazonDynamoDBClient getAmazonDynamoDBClient() {
		return this.dynamodbclient;
	}

    public AmazonSimpleEmailServiceClient getAmazonSESClient() {
        return this.sesclient;
    }

    public AmazonSNSAsyncClient getAmazonSNSAsyncClient() {
        return this.snsclient;
    }

    public AmazonCloudWatchAsyncClient getAmazonCloudWatchAsyncClient() {
        return this.cloudwatchclient;
    }

    public void shutdown() {
    	// Shutdown SNS client
    	try {
    	if (this.snsclient != null) {
    		this.snsclient.getExecutorService().shutdown();
    		this.snsclient.getExecutorService().awaitTermination(10, TimeUnit.SECONDS);
    	}
    	} catch (InterruptedException e) {
    		log4j.error("Unable to shutdown SNS client", e);
    	}

    	// Shutdown cloudwatch client
    	try {
    	if (this.cloudwatchclient != null) {
    		this.cloudwatchclient.getExecutorService().shutdown();
    		this.cloudwatchclient.getExecutorService().awaitTermination(10, TimeUnit.SECONDS);
    	}
    	} catch (InterruptedException e) {
    		log4j.error("Unable to shutdown CloudWatch client", e);
    	}
}
}
