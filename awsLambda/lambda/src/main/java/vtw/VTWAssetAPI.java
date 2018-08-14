package vtw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

/**
 * 
 * @author TELEBH
 * @Date: 02/08/2017
 * @ Description: as per Bart request is to use AssetAPI call instead of Search API as it will save
 * "1" time call ; in search API we had to issue request to get metadata first to get downloadable url
 * for most recent xml patent version, then issue another request to download patent 
 * but with "AssetAPI" we will issue one single request using PatentID to download directly 
 * to get the latest MAIN xml for a patent.
 * 
 * NOTE: this class is a little different than the one in Dataloading-code workspace because this is meant for regular process without multithreading. so i CAN NOT copy for dataloading-core
 * the procedure will be as follows:

    1. Read the message from the queue to capture Patent ID (i.e. US20150000020A1)
	2. Call asset API to download the latest main xml (by embedding Patent ID from #1 in the url)
https://vtw.elsevier.com/asset/pat/<VTW patent id>?type=MAIN&fmt=application/xml

 */
public class VTWAssetAPI {

	private static final String HOST = "acc.vtw.elsevier.com";   // VTW UAT Testing  
	//private static final String HOST = "vtw.elsevier.com";     // VTW Prod
	
	private static final String ENDPOINT_ROOT = "https://" + HOST;

	private static final String PATH = "/asset/pat/";

	private static final String TYPE = "MAIN";
	private static final String FORMAT = "application/xml";


	private static final String username = "engineering-village";
	private static final String password = "elCome29347";		//for testing queue, UAT
	//private static final String password = "evVtw!23";		// for PROD

	private static final int REQUEST_CONNECTION_TIMEOUT = 100 * 1000;		//100 seconds
	private static final int SOCKET_TIMEOUT = 1000 * 1000;


	CloseableHttpClient client;
	MyHttpResponseHandler<String> responseHandler;
	private static HttpGet request = null;
	private static URIBuilder uriBuilder;
	private String Url = null;
	
	

	private static String patentId; 



	String downloadDirName = "1";
	private static File downloadDir;
	int recsPerSingleConnection = 1;
	String threadName;


	static RequestConfig requestConfig = null;

	int curRecNum = 0;
	static int signedAssetUrl_count = 0;

	// to keep list of PatentIDS failed to download
	private static PrintWriter out;
	

	private final static Logger logger = Logger.getLogger(VTWAssetAPI.class);


	// get the list of Patent-Ids; with their signedAssetURL if any;  to download 
	private static Map<String,String> patentIds = new LinkedHashMap<String,String>();   // only for testing
			
	public VTWAssetAPI()
	{

	}

	
	public VTWAssetAPI(String dirName, int recsPerConn)
	{

		downloadDirName = dirName;
		recsPerSingleConnection = recsPerConn;
		
		createHttpConn();
		
		System.out.println("Download Dir Name: " +  downloadDirName + " recordsPerConn: " +  recsPerSingleConnection);
	}

	
	public void createHttpConn()
	{
		try
		{
			CredentialsProvider credsProvider = null;
			credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(HOST, 443), 
					new UsernamePasswordCredentials(username, password));

			//client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();    // works well for the metadata download

			this.client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
					.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();   // works well to download Patent XML, as it has redirect 
			//responseHandler = new BasicResponseHandler();
			this.responseHandler = new MyHttpResponseHandler<String>();

			System.out.println(responseHandler.toString());

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	//loop through paten ids, download patent wither with AssetAPI Url or Pre-signed URL

	public void downloadPatent(Map<String,String> patentIds, String type)
	{

		String out = "";
		HttpGet req = null;
		
		
		// create downloadDir
		
		downloadDir = new File(System.getProperty("user.dir") + "/raw_data/" + type + "_" + downloadDirName);  
		if(!(downloadDir.exists()))
		{
			downloadDir.mkdir();
		}
		
		
		
		// create HttpClient connection
		
		//createHttpConn();
		
		if(patentIds.size() >0)
		{
			for(String id: patentIds.keySet())
			{
				patentId = id;
				
				if(patentIds.get(id) !=null && !(patentIds.get(id).isEmpty()))
				{
					Url = patentIds.get(id);
					signedAssetUrl_count ++;
				}
				else
				{

					Url = ENDPOINT_ROOT + PATH + id + "?type=" + TYPE + "&fmt=" + FORMAT;
				}
				
				
				req = generateRequest(id,Url);
				if(req !=null)
				{
					out = sendRequest(req);
					System.out.println("Output is: " +  out);
				}
			}
		}
		
		// close HttpClient connection
		end();

		
	}

	public HttpGet generateRequest(String patentId, String Url)
	{
		HttpGet request = null;
		//String Url = ENDPOINT_ROOT + PATH + patentId + "?type=" + TYPE + "&fmt=" + FORMAT;
		
		try
		{
			URIBuilder uriBuilder = new URIBuilder(Url);
			request = new HttpGet(uriBuilder.build());
			request.setConfig(requestConfig);
		}
		catch(URISyntaxException ex)
		{
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return request;
	}
	
	
	public String sendRequest(HttpGet request)
	{
		String output = "";
		
		
		try
		{
			output = client.execute(request,responseHandler);
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
		}
		return output;
	}
	


	// for AssetMetadata Request Handler
	public static class MyHttpResponseHandler<T> implements ResponseHandler<String> {


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

				System.out.println("download Respons Code for PatnetID: " + patentId + " is :" + responseCode);
				ResponseResult = Integer.toString(responseCode);


				if(responseCode ==200)
				{
					out = new FileOutputStream(new File(downloadDir + "/" + patentId + ".xml"));
					
					/**
					 * This class is used to wrap a stream that includes an encoded ByteOrderMark as its first bytes. 
					 * This class detects these bytes and, if required, can automatically skip them and return the subsequent byte as the first byte in the stream. 
					 */
					//BOMInputStream  bomStream = new BOMInputStream(responseStream,false);
					bomStream = new BOMInputStream(responseStream,false,
							ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE,
							ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);

					ByteOrderMark bom = bomStream.getBOM();
					//String charSet = bom.getCharsetName();

					
					// this what was raising NullPointerException
					
				/*	if(bomStream.hasBOM())
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
					} */

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
						logger.error("Failed to close inputstream");
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
						logger.error("Failed to close inputstream");
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

		}
	}


	private synchronized void end()
	{
		try
		{
			if(client !=null)
			{
				// close connection
				client.close();
				System.out.println("Connection was successfully closed!");
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			if(out !=null)
			{
				out.close();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

	// only for testing
	public static void main(String [] args)
	{
		CredentialsProvider credsProvider = null;
		
		VTWAssetAPI api = new VTWAssetAPI(args[0], Integer.parseInt(args[1]));
		
		credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(HOST, 443), 
				new UsernamePasswordCredentials(username, password));
		
		api.client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
				.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();   // works well to download Patent XML, as it has redirect 
		//responseHandler = new BasicResponseHandler();
		api.responseHandler = new MyHttpResponseHandler<String>();
		
		
		
		
		//patentIds.put("AU2010281317A1", "http://dev-ucs-content-store-eu-west.s3.amazonaws.com/content/pat%3AAU2010281317A1/MAIN/application/xml/02d84460608e91d8510f8725fe109b7a/AU2010281317A1.xml?AWSAccessKeyId=AKIAIKW4U6PKMIE3KSLQ&Expires=1471522524&Signature=u1zyiRsWNXf5DlgT5d7zR9iadlY%3D");
		/*patentIds.put("EP2831534A4", "");
		patentIds.put("EP2670359B1", "");*/
		
		patentIds.put("WO2011149034A1", "");
		
		
		
		patentIds.put("US20090280091A1", "");
		patentIds.put("US20130241553A1", "");
		patentIds.put("US20030218103A1", "");
		patentIds.put("US8705496B2", "");
		patentIds.put("US9322510B2", "");
		patentIds.put("US20040163418A1", "");
		patentIds.put("US9311257B2", "");
		patentIds.put("US20020168750A1", "");
		patentIds.put("US9219499B2", "");
		patentIds.put("US7714994B2", "");
		patentIds.put("US7904723B2", "");
		patentIds.put("US20090044426A1", "");
		patentIds.put("US7950124B2", "");
		patentIds.put("US20040130815A1", "");
		patentIds.put("USD0668937S", "");
		patentIds.put("US20070284484A1", "");
		patentIds.put("US20080051507A1", "");
		patentIds.put("US20120209491A1", "");
		patentIds.put("US8644581B2", "");
		patentIds.put("US20110124254A1", "");
		
		
		
		
		api.downloadPatent(patentIds,"forward");
	}
	
	
	
}


