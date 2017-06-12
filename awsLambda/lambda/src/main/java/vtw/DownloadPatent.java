package vtw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;







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

public class DownloadPatent {


	private static final String HOST = "acc.vtw.elsevier.com";
	private static final String ENDPOINT_ROOT = "https://" + HOST;
	
	private static final String PATH = "/asset/pat/EP1412238A1?";
	private static final String ENDPOINT = ENDPOINT_ROOT + PATH;

	private static final String username = "engineering-village";
	private static final String password = "elCome29347";

	private static CloseableHttpClient client;
	private static ResponseHandler<String> responseHandler;


	static String patentId = "EP1412238A1";
	static int loadnumber = 20164101;

	@SuppressWarnings("deprecation")
	public static HttpClient getInstance() throws Exception
	{
		synchronized (DownloadPatent.class)
		{
			if(client == null)
			{
				try
				{
					CredentialsProvider credsProvider = new BasicCredentialsProvider();
					credsProvider.setCredentials(new AuthScope(HOST, 443), 
							new UsernamePasswordCredentials(username, password));

					client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).
							setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
							.build();
					
					
					// using ResponseHandler class below
					responseHandler =  new PatentHttpResponseHandler<String>();

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
	private static HttpGet generateRequest(String url) {

		HttpGet request = null;

		try {

			request = new HttpGet(url);
			/*URIBuilder uriBuilder = new URIBuilder(ENDPOINT);
			uriBuilder.setParameter("fmt", "application/xml")
			.setParameter("type", "MAIN")
			.setParameter("ver", "10e3df31a52450fca396982f864e6eef");
			//request = new HttpGet(url);
			request = new HttpGet(uriBuilder.build());*/

		} catch (Exception e) {

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

	public static void downloadPatentFile(String patentUrl)
	{

		try 
		{
			// create Apache HttpClient
			DownloadPatent.getInstance();


			// Generate the request
			HttpGet request = generateRequest(patentUrl);


			// Send the request to the server
			if(request !=null)
			{
				sendRequest(request);

				// close the connection
				end();
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


