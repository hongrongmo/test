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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.json.JsonObject;

import org.apache.log4j.Logger;
import org.ei.dataloading.upt.loadtime.vtw.ArchiveVTWPatentRefeed;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import com.amazonaws.http.IdleConnectionReaper;
import com.amazonaws.services.waf.model.HTTPRequest;
import com.amazonaws.util.IOUtils;

/*
 * @author: TELEBH: Friday 07/26/2019, wk: [201931] due restricted Mapping type removal in ES 6.7, I have to modify all classes taking off the combined/multi type mapping index "cafe" and replace it
 * with the 2 new indices "author" , "affiliation" 
 */

public class AusAffESIndex {

	private final String SERVICE_NAME = "es";
	private final String REGION = "us-east-1";
	
	
	private String HOST = "vpc-ev-cafe-cert-j6hoqgea5hcjdqphkjw3xzknqy.us-east-1.es.amazonaws.com";		//Added 05/10/18 ES CERT V 6.2 
	private String ENDPOINT_ROOT = "http://" + HOST;				// change to https when move to PROD
	private String PATH = "/cafe/_bulk";		//PROD up to before ES V 6.2
	private String ENDPOINT = ENDPOINT_ROOT + PATH;
	
	
	private int recsPerbulk = 10;
	private String action;
	private String index_name;		// either author or affiliation
	private int curRecNum = 1;

	private int status = 0;
		
	private StringBuffer bulkIndexContents = new StringBuffer();

	private final static Logger logger = Logger.getLogger(AusAffESIndex.class);

	 AmazonHttpClient client = null;
	 ExecutionContext context;
	 MyHttpResponseHandler<Void> responseHandler;
	 MyErrorHandler errorHandler;
	 
	 
	 
	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();
		
	private List<String> esIndexed_docs_list = new ArrayList<String>();	
	 
	public AusAffESIndex()
	{
		
	}
	
	public AusAffESIndex(int bulkSize, String esDomain, String esAction, String indexName)
	{
		recsPerbulk = bulkSize;
		action = esAction;
		index_name = indexName;		// Added 05/10/2018 as ES 6 and up does not combine diff types in one index
		
		
		HOST = esDomain;
		ENDPOINT_ROOT = "http://" + HOST;		// change to https when move to PROD
		//PATH = "/cafe/_bulk";   // PROD up to before ES V 6.2
		PATH = "/" + index_name + "/_bulk";		// Added 05/10/2018 due to ES V 6.2 split diff types to diff indices
		ENDPOINT = ENDPOINT_ROOT + PATH;
		
		midTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
		System.out.println("Time after initializing AusAffIndex class "+(endTime-startTime)/1000.0+" seconds");
		System.out.println("total Time used "+(endTime-startTime)/1000.0+" seconds");

		init();
	}
	
	// initialize the Client 
	private void init()
	{
		try {
			context = new ExecutionContext(true);
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

	// shutdown the Client 
	public void end()
	{
		try
		{
			AmazonHttpClientService.getInstance().end();
			
			System.out.println("Total # of records in indexed/deleted list: " + esIndexed_docs_list.size());
			// uncomment when initialize and create HTTP client in this class instead of AmazonHttpClientService
			/*if(client !=null)
			{
				client.shutdown();
				System.out.println("AmazonHttpClient Client was shutdown successfully");
			}*/
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
		request.addHeader("Content-Type", "application/json");
		
		return request;
	}

	/// Perform Signature Version 4 signing
	private void performSigningSteps(Request<?> requestToSign) {
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

	/// Send the request to the ES server
	private void sendRequest(Request<?> request) 
	{
	       try
	       {
	    	   Response<Void> response = client.execute(request, responseHandler, errorHandler, context);
		       //System.out.println(response.getAwsResponse());  // only for debugging
	    	   
	       }
	       catch(Exception e)
	       {
	    	   System.out.println(e.getMessage());
	    	   e.printStackTrace();
	       }
		
	}

	public void ProcessBulk()
	{
		
		// send request ONLY if there is contents to index/delete
		
		if(bulkIndexContents.length() >0)
		{
			Request<?> request = generateRequest();

			// Perform Signature Version 4 signing
			//performSigningSteps(request);    // used when having iam role for ES accesspolicy, but not in use for IP range access policy

			// Send the request to the server
			sendRequest(request);   
			
			// Shutdown client, Uncomment when initialize and create HTTP client in this class instead of AmazonHttpClientService
			//end();

		}
		
		
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

	public class MyHttpResponseHandler<T> implements HttpResponseHandler<AmazonWebServiceResponse<T>> 
	{

		public AmazonWebServiceResponse<T> handle(com.amazonaws.http.HttpResponse response) throws Exception 
		{

			InputStream responseStream = response.getContent();

			String responseString = IOUtils.toString(responseStream);

			//logger.info(responseString); // only for debugging
			//System.out.println(responseString);
			// set status for DB deletion
			setStatusCode(Integer.toString(response.getStatusCode()));
			
			// save list of successfully indexed profiles for lated DB update status="indexed" so do not re-index unless there is later update
			parseESIndexJSONResponse(responseString);
			
			
			AmazonWebServiceResponse<T> awsResponse = new AmazonWebServiceResponse<T>();
			return awsResponse;
		}


		public boolean needsConnectionLeftOpen() {
			return false;
		}
	}

	public class MyErrorHandler implements HttpResponseHandler<AmazonServiceException> 
	{

		public AmazonServiceException handle(com.amazonaws.http.HttpResponse response) throws Exception 
		{
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
			//System.out.println(ase.getRawResponseContent());
			System.out.println(ase.getErrorCode());
			
			logger.error(ase.getErrorMessage());
			logger.error(ase.getRawResponseContent());
			
			
			//06/25/2018 Re-try Re-process bulk again (re-build http request & re-send)
			ProcessBulk();
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
			System.out.println("Starting Index " + recsPerbulk);
			// index
			ProcessBulk();
			bulkIndexContents = new StringBuffer();
			curRecNum = 1;
		}

		
		bulkIndexContents.append("{ \"index\" : { \"_type\" : \""+ index_name + "\", \"_id\" : \"" + doc_id + "\" } }");
		//bulkIndexContents.append("{ \"index\" : { \"_type\" : \""+ doc_type + "\", \"_id\" : \"" + doc_id + "\" } }"); 
		bulkIndexContents.append("\n");
		bulkIndexContents.append(profile_content.toString());
		bulkIndexContents.append("\n");
		
		curRecNum ++;
	}

	// Bulk delete by doc id "_id"
	public int createBulkDelete(String doc_type, List<String>docIds)
	{
		
		bulkIndexContents = new StringBuffer();
		
		// HH commented 07/26/2019 used for ES6.0 when had one index, multi doc_type (author, affiliation)		 

		for(int i=0;i<docIds.size();i++)
		{
			
			if(curRecNum > recsPerbulk)
			{
				System.out.println("Starting Delete " + recsPerbulk);
				// delete
				ProcessBulk();
				bulkIndexContents = new StringBuffer();
				curRecNum = 1;
			}
			
			
			bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"" + index_name + "\", \"_id\" : \"" + docIds.get(i) + "\" } }");
			//bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"" + document_type + "\", \"_id\" : \"" + docIds.get(i) + "\" } }");
			bulkIndexContents.append("\n");
			
			curRecNum++;
		}
		
		
		// delete
		ProcessBulk();
		//end();   commented on 11/29/2017 bc it is already valled from calling calss not here, otherwise cause "ScheduledThreadPoolExecutor" exception
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


public void parseESIndexJSONResponse(String esResponseString)
{
	int count = 0;
	JSONObject index;
	
	JSONParser parser = new JSONParser();
	try
	{
		Object obj = parser.parse(esResponseString);

		JSONObject jsonObject = (JSONObject) obj;

		//Items
		JSONArray items = (JSONArray) jsonObject.get("items");


		@SuppressWarnings("unchecked")
		Iterator<JSONObject> indexesIterator = (Iterator<JSONObject>)items.iterator();

		while(indexesIterator.hasNext())
		{
			//indexes list
			 JSONObject indexes = indexesIterator.next();
			
			 if(action !=null && !(action.equalsIgnoreCase("delete")))
				 index = (JSONObject)indexes.get("index");
			 else
				 index = (JSONObject)indexes.get("delete");
			String _id = (String)index.get("_id");
			String result = (String)index.get("result");
			Long status = (Long)index.get("status");
			if(status != null && (status == 200 || status == 201 || status == 404))
			{
				esIndexed_docs_list.add(_id);
				count ++;
			}
				
			else
				System.out.print("doc: " + _id + " with status: " + status + " and result: " + result + " failed to index/delete to/from ES!!!!");
	
		}
		
		System.out.println("Total indexed/deleted count: " + count);
		
	}
	catch (org.json.simple.parser.ParseException e) 
	{
		logger.info("Failed to parse AWS ES JSON Response String!!!!");
		logger.error(e.getMessage());
		e.printStackTrace();
	}

}

public List<String> getESIndexedDocsList()
{
	return esIndexed_docs_list;
}

}
