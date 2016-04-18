package org.ei.dataloading.cafe;

import java.io.IOException;
import java.util.HashMap;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.*;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;

public class SQSConfiguration {

	public static final String DEFAULT_QUEUE_NAME = "EVCAFE";
	public static final Region DEFAULT_REGION = Region.getRegion(Regions.US_EAST_1);


	private static String getParameter( String args[], int i ) 
	{
		if( i + 1 >= args.length )
		{
			throw new IllegalArgumentException( "Missing parameter for " +args[i] );
		}
		return args[i+1];
	}
	/**
	 * Parse the command line and return the resulting config. If the config
	parsing fails
	 * print the error and the usage message and then call System.exit
	 *
	 * @param app the app to use when printing the usage string
	 * @param args the command line arguments
	 * @return the parsed config
	 */
	public static SQSConfiguration parseConfig(String app, HashMap<String, String> args)
	{
		try {
			return new SQSConfiguration(args);
		} catch (IllegalArgumentException e) {
			System.err.println( "ERROR: " + e.getMessage() );
			System.err.println();
			System.err.println( "Usage: " + app + " [--queue EVCAFE] [--region US_EAST_1] ");
			System.err.println( " or" );
			System.err.println( " " + app + " <spring.xml>" );
			System.exit(-1);
			return null;
		}
	}
	private SQSConfiguration(HashMap<String, String> args) throws AmazonClientException,AmazonServiceException
	{
		if(args !=null && args.size() >0)
		{
			for( String key: args.keySet() ) 
			{
				if( key.equals( "--queue" ) ) 
				{
					setQueueName(args.get(key));
					System.out.println("Queue Name to process: " + getQueueName());
				} 
				else if( key.equals( "--region" ) ) 
				{
					String regionName = args.get(key);
					try 
					{
						setRegion(Region.getRegion(Regions.fromName(regionName)));
					} catch( IllegalArgumentException e ) 
					{
						throw new IllegalArgumentException( "Unrecognized region " + regionName );
					}
				} 
				else 
				{
					throw new IllegalArgumentException("Unrecognized option " +key);
				}
			} 

			try 
			{
				//setCredentialsProvider(new EnvironmentVariableCredentialsProvider());   //for local testing
				setInstanceCredentialsProvider(new InstanceProfileCredentialsProvider());   //for dataload EC2
				setAmazonS3Cleint(AmazonS3Service.getInstance().getAmazonS3Service());
				
			} 
			catch (AmazonClientException e) 
			{
				System.out.println("Caught an AmazonClientException, which " +
	            		"means the client encountered " +
	                    "an internal error while creating EnviornmentVariableCredentialsProviderreading OR trying to " +
	                    "communicate with S3, " +
	                    "such as not being able to access the network.");
	            System.out.println("Error Message: " + e.getMessage());
	            
				throw new IllegalArgumentException("Error creating EnviornmentVariableCredentialsProviderreading");
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}


	}

	private String queueName = DEFAULT_QUEUE_NAME;
	private Region region = DEFAULT_REGION;
	private AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
	private AWSCredentialsProvider instanceProfileCredentialsProvider = new InstanceProfileCredentialsProvider();
	AmazonS3 s3Client = null;

	//setters/getters
	public String getQueueName() 
	{
		return queueName;
	}
	public void setQueueName(String queueName) 
	{
		this.queueName = queueName;
	}
	public Region getRegion() 
	{
		return region;
	}
	public void setRegion(Region region) 
	{
		this.region = region;
	}
	//EnvironmentVariables CredentialsProvider
	public AWSCredentialsProvider getCredentialsProvider()
	{
		return credentialsProvider;	
	}
	public void setCredentialsProvider(AWSCredentialsProvider credentialsProvider) 
	{
		// Make sure they're usable first
		credentialsProvider.getCredentials();
		this.credentialsProvider = credentialsProvider;
	}
	
	//EC2 InstanceCredentialsProfile
	public AWSCredentialsProvider getInstanceCredentialsProvider()
	{
		return instanceProfileCredentialsProvider;
	}
	
	public void setInstanceCredentialsProvider(AWSCredentialsProvider credentialsProvider)
	{
		
				this.instanceProfileCredentialsProvider = credentialsProvider;
	}
	
	public void setAmazonS3Cleint(AmazonS3 s3Cleint)
	{
		this.s3Client = s3Cleint;
	}
	
	public AmazonS3 getAmazonS3Cleint()
	{
		return s3Client;
	}
}


