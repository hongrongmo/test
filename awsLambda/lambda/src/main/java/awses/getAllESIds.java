package awses;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.services.waf.model.HTTPRequest;
import com.amazonaws.util.IOUtils;

import awses.ESHttp.MyErrorHandler;
import awses.ESHttp.MyHttpResponseHandler;


/*
 * @author: hteleb
 * @Date: 04/09/2019
 * Description: this calss is to pull all AUID from fast, but it still get "_source" too, not able to only get specified fields (i.e. audoc.doc_id")
 */
public class getAllESIds {

		private static final String HOST = "localhost:8040";		// testing ES domain "ev-cafe-cert6"
		private static final String ENDPOINT_ROOT = "http://" + HOST;

		private static final String PATH = "/cafe/author/_search";
		private static final String ENDPOINT = ENDPOINT_ROOT + PATH;


		private static final String SERVICE_NAME = "es";
		private static final String REGION = "us-east-1";
		
		
		private static AmazonHttpClient client = null;
		private ExecutionContext context = null;

		private static MyHttpResponseHandler<Void> responseHandler;
		private static MyErrorHandler errorHandler;
		
		
		private static StringBuffer bulkIndexContents = new StringBuffer();
		private StringBuffer esQuery = new StringBuffer();
		
		
		
		// initialize the Client 
		private void init()
		{
			try {
				ExecutionContext context = new ExecutionContext(true);
				AWSCredentialsProvider creds = new EnvironmentVariableCredentialsProvider();
				context.setCredentialsProvider(creds);
				client = AmazonHttpClientService.getInstance().getAmazonHttpClient();
				

				responseHandler = new MyHttpResponseHandler<Void>();
				errorHandler = new MyErrorHandler();
			} 
			catch (IOException e) 
			{
				System.out.println("Failed to create AmazonHttpClient!!!!!");
				System.out.println("Reason: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
			
		private Request<HTTPRequest> generateRequest() 
		{
			Request<HTTPRequest> request = null;
			try 
			{
				Map<String,List<String>> parameters = new HashMap<String,List<String>>();
				List<String> values = new ArrayList<String>();
				
				
				bulkIndexContents.append("\"stored_fields\": [\"audoc.doc_id\"],\"query\": {\"match_all\": {}}");
				
				
				values.add(bulkIndexContents.toString());
				//values.add("docproperties.doc_type:\"apr\"");
				
				
				//esQuery.append("{\"stored_fields\": [\"audoc.doc_id\"],\"query\": {\"match_all\": {}}}");
				//bulkIndexContents.append("docproperties.doc_type:apr");
				
				
				request = new DefaultRequest<HTTPRequest>(SERVICE_NAME);
				request.setContent(new ByteArrayInputStream(bulkIndexContents.toString().getBytes()));
				request.setEndpoint(URI.create(ENDPOINT));
				request.setHttpMethod(HttpMethodName.GET);
				request.addHeader("Conetnt-Type","application/json");

				//parameters.put("q",values);
				//request.setParameters(parameters);
			}
			catch (Exception e) {
			
			System.out.println("Request exception handler!");
			System.out.println(e.getMessage());
			System.out.println(e.hashCode());
			
			e.printStackTrace();
		}  

			return request;
		}
		private void performSigningSteps(Request<?> requestToSign) {
			try
			{
				AWS4Signer signer = new AWS4Signer();
				signer.setServiceName(SERVICE_NAME);
				signer.setRegionName(REGION);      

				// Get credentials using IAM ROLE in EC2, but DefaultAWSCredentials on localhost
				// NOTE: *Never* hard-code credentials in source code
				AWSCredentialsProvider credsProvider = new DefaultAWSCredentialsProviderChain();   // localhost
				//AWSCredentialsProvider credsProvider = new InstanceProfileCredentialsProvider();   // dataloading Ec2
				AWSCredentials credentials = credsProvider.getCredentials();

				// Sign request with supplied credentials
				signer.sign(requestToSign, credentials);
			}
			catch(Exception e)
			{
				System.out.println("Exception signing the request");
				System.out.println(e.getMessage());
				System.out.println(e.getCause());
				e.printStackTrace();
			}
			
		}

		

		/// Send the request to the server
		private void sendRequest(Request<?> request) {

			try {
				 
				// using ResponseHandler class below
				Response<Void> response = client.execute(request, responseHandler, errorHandler, context);
				//AmazonWebServiceResponse<Void> response = client.execute(request,responseHandler);
				
				System.out.println("Response is:  " +  response.toString());
				
				
			} catch (Exception e) {
				System.out.println("Exception happened!");
				System.out.println("Error message: " + e.getMessage());
				System.out.println("Cause: " + e.getCause());
				e.printStackTrace();
				e.printStackTrace();
			}
			
			
		}
		
		
		public static void main(String[] args) {

			
			try 
			{
				getAllESIds obj = new getAllESIds();
				
				// create Apache HttpClient
				obj.init();
				
				
				// Generate the request
				Request<?> request = obj.generateRequest();

				// Sign the request with AWS credentials
				
				//obj.performSigningSteps(request);   // with signing it raise forbidden access?! that's strange need to check later
				
				// Send the request to the server
				if(request !=null)
				{
					obj.sendRequest(request);
					
					// close the connection
					AmazonHttpClientService.getInstance().end();
				}
			} 

			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		
		
		// for AssetMetadata Request Handler
		public static class MyHttpResponseHandler<T> implements HttpResponseHandler<AmazonWebServiceResponse<T>> {

			
			//@Override
			//public CloseableHttpResponse handleResponse(HttpResponse response)
			public AmazonWebServiceResponse<T> handle(com.amazonaws.http.HttpResponse response) {

				AmazonWebServiceResponse<T> awsResponse = null;
				try {
	            InputStream responseStream = ((com.amazonaws.http.HttpResponse) response).getContent();

	            String responseString;
				
					responseString = IOUtils.toString(responseStream);
				
	            System.out.println(responseString.substring(0, responseString.length()-20000));

	            awsResponse = new AmazonWebServiceResponse<T>();
	           
	            
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				 return awsResponse;
	           
	    }


	    public boolean needsConnectionLeftOpen() {
	            return false;
	    }
		}
		
		
		public static class MyErrorHandler implements HttpResponseHandler<AmazonServiceException> {


		       public AmazonServiceException handle(
		                        com.amazonaws.http.HttpResponse response) throws Exception {
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
