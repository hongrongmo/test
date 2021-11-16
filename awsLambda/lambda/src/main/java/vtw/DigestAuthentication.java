package vtw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;















import org.apache.http.util.EntityUtils;

import vtw.DownloadVtwFile.MyHttpResponseHandler;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.util.IOUtils;

/**
 * 
 * @author TELEBH
 *
 * @Date: 10/03/2016
 * @Description: A simple example that uses HttpClient to execute an HTTP request against
 * a VTW site that requires Digest authentication.
 * 
 * @NOTE: if i put header type as "http" and port "80", it automatically at the backend redirect to "https" which listen on port "443", and still reuires authentication
 * but since no authentication is provided for the redirect, it raise "auauthorized" error and so can  not retrive asset metadata.
 * but when set header type from beginning as "https" and port "443" i can get the AssetMetadata
 * 
 *
 * NOte: VTW realm : "VTWRealm"
 */
public class DigestAuthentication {


	private static final String HOST = "acc.vtw.elsevier.com";
	private static final String ENDPOINT_ROOT = "https://" + HOST;

	private static final String PATH = "/search/?";
	private static final String ENDPOINT = ENDPOINT_ROOT + PATH;


	private static final String username = "engineering-village";
	private static final String password = "";

	private static CloseableHttpClient client;
	private static ResponseHandler<String> responseHandler;

	
	static String patentId = "EP1412238A1";
	static int loadnumber = 20164101;

	public static HttpClient getInstance() throws Exception
	{
		synchronized (DigestAuthentication.class)
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
	private static HttpGet generateMetadataRequest() {

		HttpGet request = null;

		try {
			URIBuilder uriBuilder = new URIBuilder(ENDPOINT);
			uriBuilder.setParameter("query", "identifier:pat:EP1885125B1")
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
	
	// Set up the request
	private static HttpGet generateRequest(String url) {

		HttpGet request = null;

		try {
			
			request = new HttpGet(url);
			
		} catch (Exception e) {
			
			System.out.println("Patent URL Request exception!");
			System.out.println(e.getMessage());
			System.out.println(e.hashCode());
			
			e.printStackTrace();
		}  

		return request;
	}	

	

	/// Send the request to the server
	private static void sendMetadataRequest(HttpGet request) {

		try {
			
			// using ResponseHandler class below
			
			String response = client.execute(request,responseHandler);
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
		
		
	}
	
	/// Send the request to the server
		private static void sendRequest(HttpGet request) {

			try {
				
				// using ResponseHandler class below
				responseHandler =  new PatentHttpResponseHandler<String>();
				
				String response = client.execute(request,responseHandler);
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
			
			
		}

	public static void main(String[] args) {

		
		try 
		{
			// create Apache HttpClient
			DigestAuthentication.getInstance();
			
			
			// Generate the request
			HttpGet request = generateMetadataRequest();

			
			// Send the request to the server
			if(request !=null)
			{
				sendMetadataRequest(request);
				
				// close the connection
				end();
			}
		} 

		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	
	// for AssetMetadata Request Handler
	public static class MyHttpResponseHandler<T> implements ResponseHandler<String> {

		
		//@Override
		//public CloseableHttpResponse handleResponse(HttpResponse response)
		public String handleResponse(HttpResponse response)
		{
			
			InputStream responseStream = null;
			String responseString = "";
			
			try
			{
			responseStream = response.getEntity().getContent();
			responseString =  IOUtils.toString(responseStream);
			
			System.out.println(responseString);
			System.out.println("Respons Code: " + response.getStatusLine().getStatusCode());
			
			//list all headers to verify "Content-Type"  is of type "application/json"
			Header[] headers = response.getAllHeaders();
			for(int i=0;i<headers.length;i++)
			{
				System.out.println("Header: " + headers[i] + " " + headers[i].getName() + " " + headers[i].getValue()); 
			}
			
			
			
			//look up the required URL for the Asset/Patent XML
			if(!(responseString.isEmpty()) && (response.getStatusLine().getStatusCode() == 200 
					&& responseString.contains("href") && responseString.startsWith("{") && responseString.endsWith("}")))
			{
				lookupUrl(responseString);
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
			return responseString;
			
		}

	}
	
	
	// for AssetMetadata Request Handler
	public static class PatentHttpResponseHandler<T> implements ResponseHandler<String> {

		
		
		//@Override
		public String handleResponse(HttpResponse response)
		{
			
			InputStream responseStream = null;
			OutputStream out = null;
			
			
			try
			{
			responseStream = response.getEntity().getContent();
			
			File downloadDir = new File(System.getProperty("user.dir") + "/zips");
			if(!downloadDir.exists())
			{
				downloadDir.mkdir();
			}
			
			downloadDir = new File(downloadDir.getAbsolutePath() + "/" + Integer.toString(loadnumber));
			if(!(downloadDir.exists()))
			{
				downloadDir.mkdir();
			}
			

			out = new FileOutputStream(new File(downloadDir.getAbsolutePath() + "/" + patentId + ".xml"));
			IOUtils.copy(responseStream, out);
			
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
				if(out !=null)
				{
					try
					{
						out.close();
					}
					catch(IOException ex)
					{
						System.out.println("Failed to close outputstream");
						ex.printStackTrace();
					}
					
				}
			}
			return "1";
		}

		
	}
	
	public static void lookupUrl(String assetMetadata)
	{
		
		if(!(assetMetadata.isEmpty()))
		{
			String url = ReadJsonFile.parseJsonMetadata(assetMetadata);
			downloadPatentFile(url);
			//DownloadPatent.downloadPatentFile(url);   // for debugging and testing download issue, that later worked well and patent xml file was downloaded
		}
	}
	
	
	public static void downloadPatentFile(String patentUrl)
	{

		try 
		{
			// create Apache HttpClient
			//DigestAuthentication.getInstance();
			
			
			// Generate the request
			HttpGet request = generateRequest(patentUrl);

			
			// Send the request to the server
			if(request !=null)
			{
				sendRequest(request);
				

			}
		} 

		catch (Exception e) 
		{
			e.printStackTrace();
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
