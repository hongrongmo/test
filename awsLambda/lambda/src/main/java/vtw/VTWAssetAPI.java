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

	//private static final String HOST = "acc.vtw.elsevier.com";   // VTW UAT Testing  that seems to be changed when testing CN,JP,KR in UAT
	private static final String HOST = "uatnp.vtw-np.elsevier.com";
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

	
	public VTWAssetAPI(String dirName, int recsPerConn, String thread_name)
	{
		recsPerSingleConnection = recsPerConn;
		
	}

	public synchronized String[] init(String downloadDir_name,String thread_name, String type)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");		 
		Date date = new Date();

		File downloadDir = null;

		String[] downloadDirNames = new String[2];

		try
		{
			downloadDir = new File(System.getProperty("user.dir") + "/raw_data");
			if(!downloadDir.exists())
			{
				downloadDir.mkdir();
			}

			downloadDir = new File(downloadDir.getAbsolutePath() + "/" + type + "_" + downloadDir_name);
			if(!(downloadDir.exists()))
			{
				downloadDir.mkdir();
			}

			//Added 10/02/2017 to hold WO forwardflow files separate from US/EUP files
			File wo_downloadDir = new File(System.getProperty("user.dir") + "/raw_data/" + type + "_wo_" + downloadDir_name);
			if(!(wo_downloadDir.exists()))
			{
				wo_downloadDir.mkdir();
			}
			
			//Added 10/02/2017 for supporting download of WO in separate dir
			downloadDirNames[0] = downloadDir.getAbsolutePath();
			downloadDirNames[1] = wo_downloadDir.getAbsolutePath();
			
			//responseHandler = new MyHttpResponseHandler<String>();

			// create Apache HttpClient
			//client = getInstance();

			requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT)
					.setConnectionRequestTimeout(REQUEST_CONNECTION_TIMEOUT).build();

			// save list of PatentIDS that failed to download to a file for later re-download
			String currDir = System.getProperty("user.dir");

			String root= currDir+"/failed";
			File failDir = new File (root);
			if(!failDir.exists())
			{
				failDir.mkdir();
			}

			String filename = failDir+"/" + thread_name + "failed_ids"+dateFormat.format(date)+".txt";

			try {
				out = new PrintWriter(new FileWriter(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Failed Output Filename "+filename);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		//return downloadDir.getAbsolutePath();
		return downloadDirNames;
	}
	
	public CloseableHttpClient getInstance() throws Exception
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
		return client;

	}
	
	private static void end(CloseableHttpClient client, String thread_name)
	{
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

		try
		{
			if(client !=null)
			{
				System.out.println("close HttpClient Connection for Thread: " + thread_name);
				client.close();
				System.out.println("HttpClient Connection was successfully closed!");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	//loop through paten ids, download patent wither with AssetAPI Url or Pre-signed URL

	public void downloadPatent(Map<String,String> patentIds, CloseableHttpClient client, String downloadDirName, String thread_name, String type)
	{

		String[] response = new String[2];
		String threadName = thread_name;
		ResponseHandler<String[]> responseHandler = null;

		String[] downloadDir = init(downloadDirName,thread_name, type);

		if(patentIds.size() >0)
		{
			System.out.println("Total PatentIds List: " + patentIds.size());
			for(String key: patentIds.keySet())
			{
				if(!(key.isEmpty()))
				{
					synchronized(VTWAssetAPI.class)
					{
						responseHandler = new MyHttpResponseHandler<String[]>(key, downloadDir, thread_name);

						//patentId = key;
					}

					try
					{
						if(patentIds.get(key) !=null && !(patentIds.get(key).isEmpty()))
						{
							Url = patentIds.get(key);
							signedAssetUrl_count ++;
						}
						else
						{

							Url = ENDPOINT_ROOT + PATH + key + "?type=" + TYPE + "&fmt=" + FORMAT;
						}


						// Generate the request
						HttpGet request = generateRequest(key, Url);

						// Send the request to the server
						if(request !=null)
						{
							// retry download two times in case it is failed the 1st time
							for(int i=1;i<=2;i++)
							{
								// send the request
								response = sendRequest(request, client, responseHandler);
								if(response[0] !=null)
								{
									if(Integer.parseInt(response[0]) ==200)
									break;
									
									else if(Integer.parseInt(response[0]) !=200 && i<2)
									{
										System.out.println("download response code " + response[0]  + " is not 200, re-try download:" +  key);
										Thread.sleep(500);
									}
									else
									{
										System.out.println("download response code " + response[0]  + " is not 200, so skip this file:" +  key);
										out.println(key);
									}

								}
								
							}

							//Thread.sleep(50);
						}

					}
					catch(Exception e)
					{
						e.printStackTrace();
					}	
				}
			}

			System.out.println("Total PatentIds with SignedAssetURL: " +  signedAssetUrl_count);

		}

		try
		{
			// close the connection
			end(client, threadName);

		}
		catch(Exception e)
		{
			logger.error("Failed to close http connection!");
			logger.error("Reason: " + e.getMessage());
			e.printStackTrace();
		}

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
	
	
	/// Send the request to the server
	private static String[] sendRequest(HttpGet request, CloseableHttpClient client, ResponseHandler<String[]> responseHandler) {

		String response[] = new String[2];

		try {

			// using ResponseHandler class below
			response = client.execute(request,responseHandler);
			//System.out.println("Response is:  " +  response);


		} catch (ClientProtocolException e) {
			logger.error("clientProtocolException happened!");
			logger.error("Error message: " + e.getMessage());
			logger.error("Cause: " + e.getCause());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException happened!");
			logger.error("Error message: " + e.getMessage());
			logger.error("Cause: " + e.getCause());
			e.printStackTrace();
			e.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("exception: " + e.getMessage());
			e.printStackTrace();
		}

		return response;
	}
	


	// for AssetMetadata Request Handler
	// for AssetMetadata Request Handler
		public static class MyHttpResponseHandler<T> implements ResponseHandler<String[]> {

			private String patentId = "";
			private String downloadDir = null;
			private String wo_downloadDir = null;
			private String threadName = null;
			
			public MyHttpResponseHandler()
			{

			}

			public MyHttpResponseHandler(String patent_id, String[] downloadDirName, String thread_name)
			{
				patentId = patent_id;
				downloadDir = downloadDirName[0];
				wo_downloadDir = downloadDirName[1];
				threadName = thread_name;
			}

			
			public String[] handleResponse(HttpResponse response)
			{

				InputStream responseStream = null;
				int responseCode;
				String responseErrorMessage = null;

				String[] ResponseResult = new String[2];

				BOMInputStream  bomStream = null;
				OutputStream out = null;

				/*synchronized (VTWAssetAPI.class)
				{*/

				try
				{
					responseStream = response.getEntity().getContent();
					responseCode = response.getStatusLine().getStatusCode();

					System.out.println(threadName + " :download Respons Code for PatnetID: " + patentId + " is :" + responseCode);
					ResponseResult[0] = Integer.toString(responseCode);


					if(responseCode ==200)
					{
						if(patentId.substring(0, 2).equalsIgnoreCase("wo"))
							out = new FileOutputStream(new File(wo_downloadDir + "/" + patentId + ".xml"));
						else
							out = new FileOutputStream(new File(downloadDir + "/" + patentId + ".xml"));

						/**
						 * This class is used to wrap a stream that includes an encoded ByteOrderMark as its first bytes. 
						 * This class detects these bytes and, if required, can automatically skip them and return the subsequent byte as the first byte in the stream. 
						 */
						//BOMInputStream  bomStream = new BOMInputStream(responseStream,false);
						bomStream = new BOMInputStream(responseStream,false,
								ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE,
								ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);

						IOUtils.copy(bomStream, out);

					}
					else
					{
						//System.out.println("download response code " + responseCode  + " is not 200, so skip this file:" +  patentId);
						responseErrorMessage = IOUtils.toString(response.getEntity().getContent());
						System.out.println("Response Message: " + responseErrorMessage);
						ResponseResult[1] = responseErrorMessage;
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
				//}

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
		
		VTWAssetAPI api = new VTWAssetAPI(args[0], Integer.parseInt(args[1]),"thread1");
		
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
		
		
		
		
		// for testing need to change a lottle bit to work as before
		//api.downloadPatent(patentIds,"forward");
	}
	
	
	
}


