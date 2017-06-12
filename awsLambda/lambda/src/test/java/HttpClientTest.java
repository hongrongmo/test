import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import vtw.VTWAssetAPI.MyHttpResponseHandler;


public class HttpClientTest {

	private static final String HOST = "acc.vtw.elsevier.com";
	private static final String username = "engineering-village";
	private static final String password = "elCome29347";
	

	
	static MyHttpResponseHandler<String> myResponseHandler = null;
	static CloseableHttpClient client = null;
	
	public static void main(String[] args)
	{
		String output;
		String id = "US7368003B2";
		
		HttpClientTest test = new HttpClientTest();
		test.getClient();
		test.downloadPatent();
		
		// create HttpClient
		
		/*CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(HOST, 443), 
				new UsernamePasswordCredentials(username, password));

		//client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();    // works well for the metadata download

		client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
				.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();   // works well to download Patent XML, as it has redirect 
		//responseHandler = new BasicResponseHandler();
		//MyHttpResponseHandler<String> myResponseHandler = new MyHttpResponseHandler<String>();
		myResponseHandler = new MyHttpResponseHandler<String>();*/
		
		
		HttpGet request = null;
		//String Url = "https://acc.vtw.elsevier.com/asset/pat/" + id + "?type=MAIN&fmt=application/xml";
		
		
		/*try
		{*/
			/*// create request
			URIBuilder uriBuilder = new URIBuilder(Url);
			request = new HttpGet(uriBuilder.build());
			
			System.out.println("Executing request: " + request.getRequestLine());*/
			
			//request = createRequest();
			
			/*ResponseHandler<String> myResponseHandler = new ResponseHandler<String>() {
				
				@Override
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					
					String responseEntity = null;
					int status = response.getStatusLine().getStatusCode();
					
					if(status ==200)
					{
						responseEntity = EntityUtils.toString(response.getEntity());
					}
					else
						throw new ClientProtocolException("Unexpected response with status code : " + status);
					return responseEntity;
				}
			};*/
			
			// send request
			//sendRequest(request);
			
			
		/*	output = client.execute(request,myResponseHandler);
			System.out.println("Output is: " +  output);*/
			
		//}
		/*catch(URISyntaxException ex)
		{
			ex.printStackTrace();
		}
		catch(ClientProtocolException ex)
		{
			ex.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
		
		/*finally
		{
			try {
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}*/
		
		
	}

	public void downloadPatent()
	{
		HttpGet request = null;
		
		try
		{
			// create request
			request = createRequest();
			
			// send request
			sendRequest(request);
			
		}
		
		
		finally
		{
			try {
				client.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
	}
	// create HttpClient
	public void getClient()
	{
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(HOST, 443), 
				new UsernamePasswordCredentials(username, password));

		//client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();    // works well for the metadata download

		client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
				.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();   // works well to download Patent XML, as it has redirect 
		//responseHandler = new BasicResponseHandler();
		//MyHttpResponseHandler<String> myResponseHandler = new MyHttpResponseHandler<String>();
		myResponseHandler = new MyHttpResponseHandler<String>();
	}
	
	// create the request
	public static HttpGet createRequest()
	{
		String Url = "https://acc.vtw.elsevier.com/asset/pat/US7368003B2" + "?type=MAIN&fmt=application/xml";
		HttpGet request = null;
		try
		{
			// create request
			URIBuilder uriBuilder = new URIBuilder(Url);
			request = new HttpGet(uriBuilder.build());
			
			System.out.println("Executing request: " + request.getRequestLine());
		}
		catch(URISyntaxException ex)
		{
			ex.printStackTrace();
		}
		/*catch(ClientProtocolException ex)
		{
			ex.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}*/
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return request;
		
	}
	
	// send the request
	
	public static void sendRequest(HttpGet request)
	{
		String output = null;
		try
		{
			// send request
			output = client.execute(request,myResponseHandler);
			System.out.println("Output is: " +  output);
		}
		catch(ClientProtocolException ex)
		{
			ex.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
					
	}
	// for AssetMetadata Request Handler
	public static class MyHttpResponseHandler<T> implements ResponseHandler<String> {


		@Override
		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			
			InputStream responseStream = null;
			
			//String responseEntity = null;
			int responseCode = response.getStatusLine().getStatusCode();
			
			BOMInputStream  bomStream = null;
			OutputStream out = null;
			
			try
			{
				responseStream = response.getEntity().getContent();
				
				System.out.println("download response code for PatentId is :" +  responseCode);
				if(responseCode ==200)
				{
					out = new FileOutputStream(new File("US7368003B2.xml"));
					
					bomStream = new BOMInputStream(responseStream,false,
							ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE,
							ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);
					
					IOUtils.copy(bomStream,out);
					
					
				}
				else
				{
					System.out.println("ResponseCode :" + responseCode + " is not 200!!!");
				}
			}
			catch(ClientProtocolException ex)
			{
				System.out.println("Unexpected response with status code : " + responseCode);
			}
			
			finally
			{
				try
				{
					if(responseStream !=null)
					{
						responseStream.close();
					}
				}
				catch(IOException ex)
				{
					System.out.println("Failed tp close Inputstream!");
				}
				
				try
				{
					if(bomStream !=null)
					{
						bomStream.close();
					}
				}
				catch(IOException ex)
				{
					System.out.println("Failed to close BomStream!");
				}
				
				try
				{
					if(out !=null)
					{
						out.close();
					}
				}
				catch(IOException ex)
				{
					System.out.println("Failed to close OutputStream!");
				}
			}
			
			return Integer.toString(responseCode);
		}
		
		
	/*	
		public String handleResponse(HttpResponse response)
		{

			InputStream responseStream = null;
			int responseCode;
			String responseErrorMessage = null;
			
			String ResponseResult = "";
			
			BOMInputStream  bomStream = null;
			OutputStream out = null;

			
			try
			{
				responseStream = response.getEntity().getContent();
				responseCode = response.getStatusLine().getStatusCode();

				System.out.println("download Respons Code for PatnetID: " + "US7368003B2" + " is :" + responseCode);
				ResponseResult = Integer.toString(responseCode);


				if(responseCode ==200)
				{
					out = new FileOutputStream(new File("US7368003B2" + ".xml"));
					
					*//**
					 * This class is used to wrap a stream that includes an encoded ByteOrderMark as its first bytes. 
					 * This class detects these bytes and, if required, can automatically skip them and return the subsequent byte as the first byte in the stream. 
					 *//*
					//BOMInputStream  bomStream = new BOMInputStream(responseStream,false);
					bomStream = new BOMInputStream(responseStream,false,
							ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE,
							ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);

					ByteOrderMark bom = bomStream.getBOM();
					String charSet = bom.getCharsetName();

					if(bomStream.hasBOM())
					{
						System.out.println("BOM value is : " + charSet);						
					}
					else if (bomStream.hasBOM(ByteOrderMark.UTF_16BE)) {
						System.out.println("BOM value is : " + charSet);						
					}
					else if (bomStream.hasBOM(ByteOrderMark.UTF_16LE)) {						
						System.out.println("BOM value is : " + charSet);						
					} 
					else if (bomStream.hasBOM(ByteOrderMark.UTF_32BE)) {						
						System.out.println("BOM value is : " + charSet);						
					} 
					else if (bomStream.hasBOM(ByteOrderMark.UTF_32LE)) {						
						System.out.println("BOM value is : " + charSet);						
					} 

					IOUtils.copy(bomStream, out);



				}
				else
				{
					//System.out.println("download response code " + responseCode  + " is not 200, so skip this file:" +  patentId);
					responseErrorMessage = IOUtils.toString(response.getEntity().getContent());
					System.out.println("Response Message: " + responseErrorMessage);
					//ResponseResult[1] = responseErrorMessage;
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

				if(bomStream !=null)
				{
					try
					{
						bomStream.close();
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
						out.flush();
						out.close();
					}
					catch(IOException ex)
					{
						System.out.println("Failed to close outputstream");
						ex.printStackTrace();
					}

				}

			}
			return  ResponseResult;

		}*/
	}

	
}
