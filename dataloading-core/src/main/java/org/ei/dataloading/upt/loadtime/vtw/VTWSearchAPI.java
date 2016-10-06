package org.ei.dataloading.upt.loadtime.vtw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 
 * @author TELEBH
 * @Date: 10/05/2016
 * @Description: 1. issue HTTP request for VTW searchAPI to retain Patent's Json Metadata
 * 2. parse JSON Metadata, to fetch patent downloadable url/href @ 
 * ['entries']['entry'][0]['links']['link'][0]['href']
 * 3. issue another HTTP request to download the patent using href, and save it as xml file
 */
public class VTWSearchAPI {


	private static final String HOST = "acc.vtw.elsevier.com";
	private static final String ENDPOINT_ROOT = "https://" + HOST;

	private static final String PATH = "/search/?";
	private static final String ENDPOINT = ENDPOINT_ROOT + PATH;


	private static final String username = "engineering-village";
	private static final String password = "elCome29347";

	private static CloseableHttpClient client;
	private static ResponseHandler<String> responseHandler;



	int loadNumber;
	private static File downloadDir;

	public static String status = "success";


	public VTWSearchAPI()
	{

	}

	public VTWSearchAPI(int loadnumber)
	{
		loadNumber = loadnumber;
		init();
	}


	public void init()
	{
		try
		{
			downloadDir = new File(System.getProperty("user.dir") + "/raw_data");
			if(!downloadDir.exists())
			{
				downloadDir.mkdir();
			}

			downloadDir = new File(downloadDir.getAbsolutePath() + "/" + Integer.toString(loadNumber));
			if(!(downloadDir.exists()))
			{
				downloadDir.mkdir();
			}

			// create Apache HttpClient
			VTWSearchAPI.getInstance();

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	//loop through paten ids, download their JSON Metadata

	public void downloadPatentMetadata(Map<String,String> patentIds)
	{
		int signedAssetUrl_count = 0;
		String downloadable_url = null;

		if(patentIds.size() >0)
		{
			System.out.println("Total PatentIds List: " + patentIds.size());
			for(String key: patentIds.keySet())
			{
				if(!(key.isEmpty()))
				{
					if(patentIds.get(key) !=null && !(patentIds.get(key).isEmpty()))
					{
						signedAssetUrl_count ++;

						downloadable_url = patentIds.get(key);
						System.out.println("Total PatentIds with SignedAssetURL: " +  signedAssetUrl_count);
					}
					else
					{
						try
						{
							// Generate the request
							HttpGet request = generateRequest(key);

							// Send the request to the server
							if(request !=null)
							{
								downloadable_url = sendRequest(request);
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
							status = "failed";
						}

					}

					// download the file
					if(downloadable_url !=null)
					{
						DownloadVtwFile.downloadPatent(key, downloadable_url, downloadDir.getAbsolutePath());
					}
					else
					{
						status = "failed";
					}
				}

			}
		}
		
		try
		{
			// close the connection
			end();
		}
		catch(Exception e)
		{
			System.out.println("Failed to close http connection!");
			System.out.println("Reason: " + e.getMessage());
			e.printStackTrace();
		}
		
	}

	// get httpClient

	public static CloseableHttpClient getInstance() throws Exception
	{
		synchronized (VTWSearchAPI.class)
		{
			if(client == null)
			{
				try
				{
					CredentialsProvider credsProvider = new BasicCredentialsProvider();
					credsProvider.setCredentials(new AuthScope(HOST, 443), 
							new UsernamePasswordCredentials(username, password));

					//client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();    // works well for the metadata download

					client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
							.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();   // works well to download Patent XML, as it has redirect 
					//responseHandler = new BasicResponseHandler();
					responseHandler = new MyHttpResponseHandler<String>();

				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}

			}
		}

		return client;
	}

	// Set up the request
	private static HttpGet generateRequest(String patentId) {

		HttpGet request = null;

		String query = "identifier:pat:" + patentId;
		//String query = "identifier:pat:EP1412238A1";   // only for testing
		
		
		System.out.println("Download Metadata for: " +  query);
		try {
			URIBuilder uriBuilder = new URIBuilder(ENDPOINT);
			uriBuilder.setParameter("query", query)
			.setParameter("Accept", "application/json");



			request = new HttpGet(uriBuilder.build());
		} catch (URISyntaxException e) {

			System.out.println("Request exception handler!");
			System.out.println(e.getMessage());
			System.out.println(e.getReason());
			System.out.println(e.hashCode());

			e.printStackTrace();
		}  

		return request;
	}	


	/// Send the request to the server
	private static String sendRequest(HttpGet request) {

		String response = null;
		try {

			// using ResponseHandler class below

			response = client.execute(request,responseHandler);
			System.out.println("Response is:  " +  response);


		} catch (ClientProtocolException e) {
			System.out.println("clientProtocolException happened!");
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Cause: " + e.getCause());
			e.printStackTrace();
			
			
		} catch (IOException e) {
			System.out.println("IOException happened!");
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Cause: " + e.getCause());
			e.printStackTrace();
			e.printStackTrace();
		}

		return response;
	}


	// for AssetMetadata Request Handler
	public static class MyHttpResponseHandler<T> implements ResponseHandler<String> {


		@Override
		//public CloseableHttpResponse handleResponse(HttpResponse response)
		public String handleResponse(HttpResponse response)
		{

			InputStream responseStream = null;
			String responseString = "";
			String url = null;

			try
			{
				responseStream = response.getEntity().getContent();
				responseString =  IOUtils.toString(responseStream);

				System.out.println("Metadata Search Response: " + responseString);
				System.out.println("Respons Code: " + response.getStatusLine().getStatusCode());

				//look up the required URL for the Asset/Patent XML
				if(!(responseString.isEmpty()))
				{
					url = ReadJsonMetadata.parseJsonMetadata(responseString);
				}


			}

			catch(ClientProtocolException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}


			finally
			{
				if(responseStream !=null)
				{
					try
					{
						responseStream.close();
					}
					catch(IOException ex)
					{
						System.out.println("Failed to close inputstream");
						ex.printStackTrace();
					}

				}
			}
			return url;

		}
	}


	private static void end()
	{
		try
		{
			if(client !=null)
			{
				client.close();
				System.out.println("HttpClient Connection was successfully closed!");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
