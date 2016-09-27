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

import org.apache.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.services.waf.model.HTTPRequest;
import com.amazonaws.util.IOUtils;

/**
 * 
 * @author TELEBH
 * @Date: 09/16/2016
 * @Description: get the pre-signed URL from VTW SQS message to download the corresponding asset (Patent XML record) and then upload to store in S3 bucket in
 * an organized S3 subfolder for later Processing (i.e. converting)
 */
public class DownloadVtwFile {

	private static final String SERVICE_NAME = "sqs";
	private static final String REGION = "us-east-1";
	
	private static final String HOST = "dev-ucs-content-store-eu-west.s3.amazonaws.com";
	private static final String ENDPOINT_ROOT = "http://" + HOST;
	private static final String PATH = "/content/pat%3AAU2010281317A1/MAIN/application/xml/02d84460608e91d8510f8725fe109b7a/AU2010281317A1.xml?AWSAccessKeyId=AKIAIKW4U6PKMIE3KSLQ&Expires=1471522524&Signature=u1zyiRsWNXf5DlgT5d7zR9iadlY%3D";		
	//private static final String ENDPOINT = ENDPOINT_ROOT + PATH;
	private static String URL = null;
	private static String xml_fileName;
	private static String Patent_resourceID;
	
	private static int loadNumber;
	
	private final static Logger logger = Logger.getLogger(DownloadVtwFile.class);
	
	private static AmazonHttpClient client;
	private static MyHttpResponseHandler<Void> responseHandler;
	private static MyErrorHandler errorHandler;
	private static ExecutionContext context;
	
	// download status (either success or fail)
	public static String status = "successed";
	private static InputStream responseStream;
	
	private static File loadnumberDir;
	
	public static AmazonHttpClient getInstance(int load_number) throws AmazonServiceException
	{
		synchronized (DownloadVtwFile.class)
		{
			if(client == null)
			{
				try
				{
					loadNumber = load_number;
					
					 context = new ExecutionContext(true);

				       ClientConfiguration clientConfiguration = new ClientConfiguration();
				       clientConfiguration.setConnectionTimeout(ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT);
				       clientConfiguration.setConnectionMaxIdleMillis(ClientConfiguration.DEFAULT_CONNECTION_MAX_IDLE_MILLIS);
				       client = new AmazonHttpClient(clientConfiguration);

				       responseHandler = new MyHttpResponseHandler<Void>();
				       errorHandler = new MyErrorHandler();
				       
				       // create download directory
				      	String currDir = System.getProperty("user.dir");
						String root= currDir+"/" + loadNumber;
						loadnumberDir = new File (root);
						if(!loadnumberDir.exists())
						{
							loadnumberDir.mkdir();
						}
						

				}
				catch(AmazonServiceException ex)
				{
					ex.printStackTrace();
				}
				 
			}
		}
		
		return client;
	}
	
	
	/// Perform Signature Version 4 signing
		private static void performSigningSteps(Request<?> requestToSign) {
		       AWS4Signer signer = new AWS4Signer();
		       signer.setServiceName(SERVICE_NAME);
		       signer.setRegionName(REGION);      

		       // Get credentials
		       // NOTE: *Never* hard-code credentials
		       //       in source code
		       AWSCredentialsProvider credsProvider = new DefaultAWSCredentialsProviderChain();

		       AWSCredentials creds = credsProvider.getCredentials();

		       // Sign request with supplied creds
		       signer.sign(requestToSign, creds);
		}

	/// Set up the request
	private static Request <HTTPRequest> generateRequest() {
	       Request<HTTPRequest> request = new DefaultRequest<HTTPRequest>(SERVICE_NAME);      
	       //request.setEndpoint(URI.create(ENDPOINT));
	       request.setEndpoint(URI.create(URL));
	       request.setHttpMethod(HttpMethodName.GET);
	      
	       return request;
	}	

	/// Send the request to the server
	private static void sendRequest(Request<?> request) {
	     
	       Response<Void> response = client.execute(request, responseHandler, errorHandler, context);
	       System.out.println(response.getAwsResponse());
	}
	
	public static void main(String[] args) {
				
			       // Generate the request
			       Request<?> request = generateRequest();

			       // Perform Signature Version 4 signing
			      // performSigningSteps(request);    // needed when uing IAM role for ES access Policy

			       // Send the request to the server
			       sendRequest(request);

	}
	
	public void retrieveAsset(String signedAssetURL, String xmlFileName, String PatentSourceID, int load_number)
	{
		URL = signedAssetURL;
		xml_fileName = xmlFileName;
		Patent_resourceID = PatentSourceID;
		
		System.out.println("build HTTPRequest for URL: " + signedAssetURL);
		// get HttpClient Instance
		getInstance(load_number);
		// Generate the request
		Request<?> request = generateRequest();
		// Send the request to the server
		sendRequest(request);
	}
	
	public static class MyHttpResponseHandler<T> implements HttpResponseHandler<AmazonWebServiceResponse<T>> {

		@Override
	       public AmazonWebServiceResponse<T> handle(com.amazonaws.http.HttpResponse response) throws Exception {

	               responseStream = response.getContent();

	               String responseString = IOUtils.toString(responseStream);
	              
	               System.out.println(responseString);

	               //write returned Input Stream into XML Patent file on EC2
	               if(responseStream !=null)
	               {
	            	   writeContentsToFile();
	               }
	               AmazonWebServiceResponse<T> awsResponse = new AmazonWebServiceResponse<T>();
	               return awsResponse;
	       }

	       @Override
	       public boolean needsConnectionLeftOpen() {
	               return false;
	       }
	}

	public static class MyErrorHandler implements HttpResponseHandler<AmazonServiceException> {

		
		@Override
	       public AmazonServiceException handle(com.amazonaws.http.HttpResponse response) throws Exception, IOException {
	               
				   System.out.println("Request exception handler!");

				   logger.error("What is wrong!.");
				   logger.error(IOUtils.toString(response.getContent()));
				   logger.error(response.getStatusText());
				   logger.error(response.getStatusCode());
				   
				   status = "failed";
	               AmazonServiceException ase = new AmazonServiceException("What is wrong!.");
	               ase.setRawResponse(IOUtils.toByteArray(response.getContent()));
	               ase.setStatusCode(response.getStatusCode());
	               ase.setErrorCode(response.getStatusText());
	               /*System.out.println(ase.getErrorMessage());
	               System.out.println(ase.getRawResponseContent());
	               System.out.println(ase.getErrorCode());*/
	               return ase;
	         }

		@Override
	       public boolean needsConnectionLeftOpen() {
	               return false;
	         }
	}
	
private static void writeContentsToFile()
{
	OutputStream outPutStream = null;
	
	File patentResourceDir = new File(loadnumberDir.getName() + "/" + Patent_resourceID);
	if(!patentResourceDir.exists())
	{
		patentResourceDir.mkdir();
	}
	String filename = patentResourceDir+"/"+xml_fileName+".xml";
	try {
		outPutStream = new FileOutputStream(new File(filename));
		IOUtils.copy(responseStream, outPutStream);
	} catch (IOException e) {
		e.printStackTrace();
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
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	try
	{
		if(outPutStream !=null)
		{
			outPutStream.close();
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}

}

