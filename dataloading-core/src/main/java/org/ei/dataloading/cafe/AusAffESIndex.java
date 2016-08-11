package org.ei.dataloading.cafe;

import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Delete;
import io.searchbox.core.BulkResult.BulkResultItem;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;


import java.io.PrintWriter;
import java.net.URI;
import java.util.List;

import javax.json.JsonObject;

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
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.services.waf.model.HTTPRequest;
import com.amazonaws.util.IOUtils;

public class AusAffESIndex {

	private final String SERVICE_NAME = "es";
	private final String REGION = "us-east-1";
	private final String HOST = "search-evcafe-prod-h7xqbezrvqkb5ult6o4sn6nsae.us-east-1.es.amazonaws.com";
	private final String ENDPOINT_ROOT = "http://" + HOST;
	private final String PATH = "/cafe/_bulk";
	private final String ENDPOINT = ENDPOINT_ROOT + PATH;

	
	private int recsPerbulk = 10;
	private int curRecNum = 1;

	private int status = 0;
		
	private StringBuffer bulkIndexContents = new StringBuffer();


	 AmazonHttpClient client = null;
	 
	 
	public AusAffESIndex()
	{
		
	}
	
	public AusAffESIndex(int bulkSize)
	{
		recsPerbulk = bulkSize;
		
	}
	
	// initialize the Client 
	private void init()
	{
		/*context = new ExecutionContext(true);

		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setConnectionTimeout(ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT);
		clientConfiguration.setConnectionMaxIdleMillis(ClientConfiguration.DEFAULT_CONNECTION_MAX_IDLE_MILLIS);
		client = new AmazonHttpClient(clientConfiguration);

		responseHandler = new MyHttpResponseHandler<Void>();
		errorHandler = new MyErrorHandler();*/
		
		//System.out.println("do nothing");
	}

	// shutdown the Client 
	public void end()
	{
		try
		{
			if(client !=null)
			{
				client.shutdown();
			}
		}
		catch(Exception e)
		{
			System.out.println("error occurred during client shutdown");
			System.out.println("Reson: " +  e.getMessage());
			e.printStackTrace();
		}
	}

	/// Set up the request
	private Request <HTTPRequest> generateRequest() {
		//System.out.println(bulkIndexContents.toString());   // only for local testing
		Request<HTTPRequest> request = new DefaultRequest<HTTPRequest>(SERVICE_NAME);
		request.setContent(new ByteArrayInputStream(bulkIndexContents.toString().getBytes()));
		request.setEndpoint(URI.create(ENDPOINT));
		request.setHttpMethod(HttpMethodName.POST);

		return request;
	}

	/// Perform Signature Version 4 signing
	private void performSigningSteps(Request<?> requestToSign) {
		AWS4Signer signer = new AWS4Signer();
		signer.setServiceName(SERVICE_NAME);
		signer.setRegionName(REGION);      

		// Get credentials using IAM ROLE in EC2, but DefaultAWSCredentials on localhost
		// NOTE: *Never* hard-code credentials in source code
		//AWSCredentialsProvider credsProvider = new DefaultAWSCredentialsProviderChain();   // localhost
		AWSCredentialsProvider credsProvider = new InstanceProfileCredentialsProvider();
		AWSCredentials credentials = credsProvider.getCredentials();

		// Sign request with supplied credentials
		signer.sign(requestToSign, credentials);
	}

	/// Send the request to the ES server
	private void sendRequest(Request<?> request) 
	{
		 ExecutionContext context = new ExecutionContext(true);

	       ClientConfiguration clientConfiguration = new ClientConfiguration();
	       clientConfiguration.setConnectionTimeout(ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT);
	       clientConfiguration.setConnectionMaxIdleMillis(ClientConfiguration.DEFAULT_CONNECTION_MAX_IDLE_MILLIS);
	       client = new AmazonHttpClient(clientConfiguration);

	       MyHttpResponseHandler<Void> responseHandler = new MyHttpResponseHandler<Void>();
	       MyErrorHandler errorHandler = new MyErrorHandler();

	       try
	       {
	    	   Response<Void> response = client.execute(request, responseHandler, errorHandler, context);
		       System.out.println(response.getAwsResponse());
	       }
	       catch(Exception e)
	       {
	    	   System.out.println(e.getMessage());
	    	   e.printStackTrace();
	       }
		
	}

	public void ProcessBulk()
	{
		
		Request<?> request = generateRequest();

		// Perform Signature Version 4 signing
		performSigningSteps(request);

		// Send the request to the server
		sendRequest(request);
		
		// Shutdown client
		end();

	}
	public static void main(String[] args) {

		AusAffESIndex es = new AusAffESIndex();
		//initialize Client
		es.init();

		// generate the request Body/Contents for index
		//createBulkIndex();

		// generate the request Body/Contents for delete
		//createBulkDelete();

		// Generate the request
		Request<?> request = es.generateRequest();

		// Perform Signature Version 4 signing
		es.performSigningSteps(request);

		// Send the request to the server
		es.sendRequest(request);

		// end
		es.end();
		

	}

	public class MyHttpResponseHandler<T> implements HttpResponseHandler<AmazonWebServiceResponse<T>> {


		public AmazonWebServiceResponse<T> handle(
				com.amazonaws.http.HttpResponse response) throws Exception {

			InputStream responseStream = response.getContent();

			String responseString = IOUtils.toString(responseStream);

			System.out.println(responseString);
			// set status for DB deletion
			setStatusCode(Integer.toString(response.getStatusCode()));
			

			AmazonWebServiceResponse<T> awsResponse = new AmazonWebServiceResponse<T>();
			return awsResponse;
		}


		public boolean needsConnectionLeftOpen() {
			return false;
		}
	}

	public class MyErrorHandler implements HttpResponseHandler<AmazonServiceException> {


		public AmazonServiceException handle(
				com.amazonaws.http.HttpResponse response) throws Exception {
			System.out.println("Request exception handler!");

			AmazonServiceException ase = new AmazonServiceException("What is wrong!.");
			ase.setStatusCode(response.getStatusCode());
			ase.setErrorCode(response.getStatusText());
			
			// set status for DB deletion
			setStatusCode(Integer.toString(response.getStatusCode()));
			
			//write Json doc content to console for checking the error
			System.out.println("Check Json Bulk Docs if there is syntax error: ");
			System.out.println(bulkIndexContents);
			
			
			System.out.println(response.getStatusText());
			System.out.println(response.getStatusCode());
			System.out.println(ase.getErrorMessage());
			System.out.println(ase.getRawResponseContent());
			System.out.println(ase.getErrorCode());
			return ase;
		}


		public boolean needsConnectionLeftOpen() {
			return false;
		}
	}

	// Bulk Index 
	public void createBulkIndex(String doc_type, String doc_id, JsonObject profile_content)
	{
		if(curRecNum > recsPerbulk)
		{
			// index
			ProcessBulk();
			bulkIndexContents = new StringBuffer();
			curRecNum = 1;
		}

		bulkIndexContents.append("{ \"index\" : { \"_type\" : \""+ doc_type + "\", \"_id\" : \"" + doc_id + "\" } }");
		bulkIndexContents.append("\n");
		bulkIndexContents.append(profile_content.toString());
		bulkIndexContents.append("\n");
		
		curRecNum ++;
	}

	// Bulk delete
	public int createBulkDelete(String doc_type, List<String>docIds)
	{
		
		bulkIndexContents = new StringBuffer();

		for(int i=0;i<docIds.size();i++)
		{
			bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"" + doc_type + "\", \"_id\" : \"" + docIds.get(i) + "\" } }");
			bulkIndexContents.append("\n");
		}
		
		
		// delete
		ProcessBulk();
		return getStatusCode();

	}
	

	// send back Bulk deletion status to verify before DB deletion
public void setStatusCode(String statusCode)
{

	if(statusCode.contains("}"))
	{
		status = Integer.parseInt(statusCode.substring(0, statusCode.indexOf("}")));
	}
	else
	{
		status = Integer.parseInt(statusCode);
	}
	
}

public int getStatusCode()
{
	return status;
}
}
