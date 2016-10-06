package org.ei.dataloading.upt.loadtime.vtw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;









import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.apache.wink.common.model.wadl.HTTPMethods;

/**
 * 
 * @author TELEBH
 * @Date: 09/16/2016
 * @Description: get the pre-signed URL from VTW SQS message to download the corresponding asset (Patent XML record) and then upload to store in S3 bucket in
 * an organized S3 subfolder for later Processing (i.e. converting)
 */
public class DownloadVtwFile {


	

	private final static Logger logger = Logger.getLogger(DownloadVtwFile.class);

	static String patentID = null;
	static String URL = null;
	static String downloadDir = null;
	
	private static CloseableHttpClient client;
	private static ResponseHandler<String> responseHandler;


	/// Set up the request
	private static HttpGet generateRequest() {

		HttpGet request = null;
		try
		{
			request = new HttpGet(URI.create(URL)); 
		}
		catch (Exception e) 
		{

			System.out.println("Patent URL Request exception!");
			System.out.println(e.getMessage());
			System.out.println(e.hashCode());

			e.printStackTrace();
		}  

		return request;
	}	

	/// Send the request to the server
	private static void sendRequest(HttpGet request) {

		try {	
			// using ResponseHandler class below
			responseHandler =  new MyHttpResponseHandler<String>();

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

		// Generate the request
		HttpGet request = generateRequest();

		// Send the request to the server
		sendRequest(request);

	}

	
	public static void downloadPatent(String patentid, String url, String download_dir) {

		patentID = patentid;
		URL = url;
		downloadDir = download_dir;
		
		try
		{
			// create Apache HttpClient
			client = VTWSearchAPI.getInstance();
			
			
			// Generate the request
			HttpGet request = generateRequest();

			// Send the request to the server
			if(request !=null)
			{
				sendRequest(request);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	
	
public static class MyHttpResponseHandler<T> implements ResponseHandler<String> {
		
		@Override
		public String handleResponse(HttpResponse response)
		{
			
			InputStream responseStream = null;
			OutputStream out = null;
			
			
			try
			{
			responseStream = response.getEntity().getContent();

			out = new FileOutputStream(new File(downloadDir + "/" + patentID + ".xml"));
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
			return  Integer.toString(response.getStatusLine().getStatusCode());
		}

	}
}

