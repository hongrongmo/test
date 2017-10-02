package vtw;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import awses.TestESSign.MyErrorHandler;
import awses.TestESSign.MyHttpResponseHandler;

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
	
	private static final String HOST = "sqs.eu-west-1.amazonaws.com/790640479873/acc-contributor-event-queue-EV";
	//private static final String HOST = "dev-ucs-content-store-eu-west.s3.amazonaws.com";
	//private static final String ENDPOINT_ROOT = "http://" + HOST;
	private static final String ENDPOINT_ROOT = "https://" + HOST;
	//private static final String PATH = "/content/pat%3AAU2010281317A1/MAIN/application/xml/02d84460608e91d8510f8725fe109b7a/AU2010281317A1.xml?AWSAccessKeyId=AKIAIKW4U6PKMIE3KSLQ&Expires=1471522524&Signature=u1zyiRsWNXf5DlgT5d7zR9iadlY%3D";
	private static final String PATH = "/?Action=ReceiveMessage&WaitTimeSeconds=10&MaxNumberOfMessages=5&VisibilityTimeout=15&AttributeName=All";
	//private static final String ENDPOINT = ENDPOINT_ROOT + PATH;
	//private static final String ENDPOINT = "http://acc.vtw.elsevier.com/content/pat/EP1412238A1/10";
	//private static final String ENDPOINT = "https://acc.vtw.elsevier.com/content/pat/EP1412238A1/10";
	//private static final String ENDPOINT = "https://acc.vtw.elsevier.com/assetMetadata/pat/EP1412238A1/10";
	private static final String ENDPOINT = "https://acc.vtw.elsevier.com/assetMetadata/pat/EP1412238A1/10?fmt=application/json";
	private static String URL = null;
	
	
	
		
	private static AmazonHttpClient client;
	private static MyHttpResponseHandler<Void> responseHandler;
	private static MyErrorHandler errorHandler;
	private static ExecutionContext context;
	
	public static AmazonHttpClient getInstance() throws AmazonServiceException
	{
		synchronized (DownloadVtwFile.class)
		{
			if(client == null)
			{
				try
				{
					 context = new ExecutionContext(true);

				       ClientConfiguration clientConfiguration = new ClientConfiguration();
				       clientConfiguration.setConnectionTimeout(ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT);
				       clientConfiguration.setConnectionMaxIdleMillis(ClientConfiguration.DEFAULT_CONNECTION_MAX_IDLE_MILLIS);
				       client = new AmazonHttpClient(clientConfiguration);

				       responseHandler = new MyHttpResponseHandler<Void>();
				       errorHandler = new MyErrorHandler();

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
		       AWSCredentialsProvider credsProvider =
		                     new DefaultAWSCredentialsProviderChain();

		       AWSCredentials creds = credsProvider.getCredentials();

		       // Sign request with supplied creds
		       signer.sign(requestToSign, creds);
		}

	/// Set up the request
	private static Request <HTTPRequest> generateRequest() {
		URL = ENDPOINT;  // temp for testing VTW sqs using HTTP request instead of aws-java-sdk
		
	       Request<HTTPRequest> request = new DefaultRequest<HTTPRequest>(SERVICE_NAME);      
	       //request.setEndpoint(URI.create(ENDPOINT));
	       request.setEndpoint(URI.create(URL));
	       request.setHttpMethod(HttpMethodName.GET);
	      
	       return request;
	}	

	/// Send the request to the server
	private static void sendRequest(Request<?> request) {
	     
	       Response<Void> response = client.execute(request, responseHandler, errorHandler, context);
	       System.out.println("Response" + response.getAwsResponse());
	}
	
	public static void main(String[] args) {
				
					// create AmazonHttpClient
					DownloadVtwFile.getInstance();
					
					
			       // Generate the request
			       Request<?> request = generateRequest();

			       // Perform Signature Version 4 signing
			       performSigningSteps(request);    // needed when uing IAM role for ES access Policy

			       // Send the request to the server
			       sendRequest(request);

	}
	
	public void retrieveAsset(String signedAssetURL)
	{
		//URL = signedAssetURL;   //temp comment for testing VTW queue
		URL = ENDPOINT;
		
		
		System.out.println("build HTTPRequest for URL: " + signedAssetURL);
		// get HttpClient Instance
		getInstance();
		// Generate the request
		Request<?> request = generateRequest();
		// Send the request to the server
		sendRequest(request);
	}
	
	public static class MyHttpResponseHandler<T> implements HttpResponseHandler<AmazonWebServiceResponse<T>> {

		public AmazonWebServiceResponse<T> handle(
	                        com.amazonaws.http.HttpResponse response) throws Exception {

	               InputStream responseStream = response.getContent();

	               String responseString = IOUtils.toString(responseStream);
	              
	               System.out.println(responseString);

	               AmazonWebServiceResponse<T> awsResponse = new AmazonWebServiceResponse<T>();
	               return awsResponse;
	       }

	       public boolean needsConnectionLeftOpen() {
	               return false;
	       }
	}

	public static class MyErrorHandler implements HttpResponseHandler<AmazonServiceException> {


		public AmazonServiceException handle(com.amazonaws.http.HttpResponse response) throws Exception 
			{
	               System.out.println("Request exception handler!");

	               AmazonServiceException ase = new AmazonServiceException("What is wrong!.");
	               ase.setStatusCode(response.getStatusCode());
	               ase.setErrorCode(response.getStatusText());
	               System.out.println(ase.getErrorMessage());
	               System.out.println(ase.getRawResponseContent());
	               System.out.println(ase.getErrorCode());
	               return ase;
	         }

		public boolean needsConnectionLeftOpen() {
	               return false;
	         }
	}
	


}
