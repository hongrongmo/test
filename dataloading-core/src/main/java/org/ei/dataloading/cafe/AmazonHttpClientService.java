package org.ei.dataloading.cafe;

import java.io.FileNotFoundException;
import java.io.IOException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.http.AmazonHttpClient;



/**
 * 
 * @author TELEBH
 * @Date: 06/19/2017
 * @Description: creating one single instance of AmazonHttpClient to be shared with subsequent HttpRequests for ES index
 * instead of creating multiple clients for each #of AU/AF bulks to index to ES, create only one single instance and share it with subsequent Http Requests
 * after all ES index is complete, close the connection 
 */
public class AmazonHttpClientService 
{



	private static AmazonHttpClientService instance;

	private AmazonHttpClient client;

	private static int SOCK_TIMEOUT = 500 * 1000;  // 500 minutes timeout
	private static final int REQUEST_TIMEOUT = 100 * 1000;		//Sets the amount of time to wait (in milliseconds) for the request to complete before giving up and timing out


	private static ClientConfiguration clientConfiguration;

	public AmazonHttpClientService()
	{

	}

	public static AmazonHttpClientService getInstance() throws FileNotFoundException, IOException
	{
		synchronized (AmazonHttpClientService.class){


			if(instance == null)
			{	
				clientConfiguration = new ClientConfiguration();
				clientConfiguration.setConnectionTimeout(ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT);
				clientConfiguration.setConnectionMaxIdleMillis(ClientConfiguration.DEFAULT_CONNECTION_MAX_IDLE_MILLIS);
				clientConfiguration.setSocketTimeout(SOCK_TIMEOUT);
				clientConfiguration.setRequestTimeout(REQUEST_TIMEOUT);		// sets Request timeout to to "100" seconds 
				clientConfiguration.setMaxConnections(ClientConfiguration.DEFAULT_MAX_CONNECTIONS);
				clientConfiguration.setMaxErrorRetry(6);

				instance = new AmazonHttpClientService();
				
				try
				{
					instance.client = new AmazonHttpClient(clientConfiguration);
					System.out.println("Creating Amazon HttpClient ...");


				} catch (IllegalArgumentException e) {

					System.out.println("Failed to create AmazonHttpClient!!!!");
					System.out.println("Reason: " + e.getMessage());
					e.printStackTrace();
				} 
			}
		}

		return instance;
	}



	public AmazonHttpClient getAmazonHttpClient() {
		return this.client;
	}

	// shutdown the Client 
	public void end()
	{

		try
		{
			if(this.client !=null)
			{
				this.client.shutdown();
				System.out.println("AmazonHttpClient Client was shutdown successfully");
			}
		}

		catch(Exception e)
		{
			System.out.println("error occurred during client shutdown");
			System.out.println("Reson: " +  e.getMessage());
			e.printStackTrace();
		}


	}




}
