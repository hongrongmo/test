package org.ei.dataloading.cafe;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


import java.io.PrintWriter;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;





import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.waf.model.HTTPRequest;
import com.amazonaws.util.IOUtils;


/******
 * 
 * @author telebh
 * @Date: 06/16/2017
 * @Description: for large # of AU/AF records to index in ES, one thread will have bad performance and can't manahe error handling as if process stopped at some point
 * 				will need to re-index whole thing again (not from ceratin point). so AusAffEsIndex is good for small # of records, but for big number 
 * 				need to use multithreading. i did keep all classes of single thread & creating ES doc on Fly and index to ES as it is that is working fine.
 *   
 */
public class AusAffESIndexMultiThreads extends Thread{

	private final String SERVICE_NAME = "es";
	private final String REGION = "us-east-1";


	private String HOST = "search-evcafe5-ucqg6c7jnb4qbvppj2nee4muwi.us-east-1.es.amazonaws.com";
	private String ENDPOINT_ROOT = "http://" + HOST;
	private String PATH = "/cafe/_bulk";
	private String ENDPOINT = ENDPOINT_ROOT + PATH;

	private final int REQUEST_TIMEOUT = 100 * 1000;		//Sets the amount of time to wait (in milliseconds) for the request to complete before giving up and timing out



	private int recsPerbulk = 10;
	private String action;
	private int curRecNum = 1;

	private String esFileName;


	WriteEsDocToFile w;
	private Thread th;
	private String threadName = null;
	private CountDownLatch latch;


	// Amazon HttpClient confirguration variables
	AmazonHttpClient client = null;
	ExecutionContext context;
	MyHttpResponseHandler<Void> responseHandler;
	MyErrorHandler errorHandler;


	int startIndex;
	int lastIndex;


	PrintWriter out;


	private int status = 0;

	private StringBuffer bulkIndexContents;

	private final static Logger logger = Logger.getLogger(AusAffESIndexMultiThreads.class);





	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();

	public AusAffESIndexMultiThreads()
	{

	}

	public AusAffESIndexMultiThreads(String thName, CountDownLatch latch, int bulkSize, String esDomain, String esAction, WriteEsDocToFile docWrite, int start, int last)
	{
		threadName = thName;
		this.latch = latch;
		recsPerbulk = bulkSize;
		action = esAction;

		esFileName = "";
		w = docWrite;
		startIndex = start;
		lastIndex = last;


		HOST = esDomain;
		ENDPOINT_ROOT = "http://" + HOST;
		PATH = "/cafe/_bulk";
		ENDPOINT = ENDPOINT_ROOT + PATH;

		midTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
		System.out.println("Time after initializing AusAffIndex class "+(endTime-startTime)/1000.0+" seconds");
		System.out.println("total Time used "+(endTime-startTime)/1000.0+" seconds");

	}

	// initialize the ESIndexFile
	public void init()
	{
		/*synchronized(this)
		{*/
			try {
				if(context ==null)
				{
					context = new ExecutionContext(true);
					client = AmazonHttpClientService.getInstance().getAmazonHttpClient();

					responseHandler = new MyHttpResponseHandler<Void>();
					errorHandler = new MyErrorHandler();

				}
				else
				{
					System.out.println(threadName + ": context already created, nothing to do");
				}
			} catch (IOException e) 
			{
				System.out.println("Failed to create AmazonHttpClient!!!!!");
				System.out.println("Reason: " + e.getMessage());
				e.printStackTrace();
			}

		//}

	}



	public void start()
	{
		if(th ==null)
		{
			try 
			{
				th = new Thread(this, threadName);
				th.start();
			} 
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void run() 
	{

		for(int j=startIndex; j<=lastIndex;j++)
		{
			esFileName = WriteEsDocToFile.esFilesList.get(j);
			getEsFileContents();
			ProcessBulk();
		}
		latch.countDown();

	}

	// close ESindexFile 
	public void close()
	{
		try
		{
			if(out !=null)
			{
				out.close();
				out.flush();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Failed to close ESIndex File!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}





	// send back Bulk deletion status to verify before DB deletion
	public synchronized void setStatusCode(String statusCode)
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



	public void getEsFileContents()
	{
		/*synchronized(AusAffESIndexMultiThreads.class)
		{*/
			Scanner scan = null;
			try
			{
				System.out.println(threadName + ": fetching Contents of file: " + esFileName);
				scan = new Scanner(new File(esFileName));
				scan.useDelimiter("\\Z");

				String fileContent = scan.next();

				bulkIndexContents = new StringBuffer();
				bulkIndexContents.append(fileContent);
				bulkIndexContents.append("\n");

				System.out.println("File Content length: " + bulkIndexContents.length());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			finally
			{
				try
				{
					if(scan !=null)
					{
						scan.close();
					}
				}
				catch(Exception e)
				{
					System.out.println("Failed to close the scanner!!!!!");
					e.printStackTrace();
				}
			}
		//}
	}

	/// Set up the request
	private Request <HTTPRequest> generateRequest() 
	{
		/*synchronized(this)
		{*/
			Request<HTTPRequest> request = new DefaultRequest<HTTPRequest>(SERVICE_NAME);
			request.setContent(new ByteArrayInputStream(bulkIndexContents.toString().getBytes()));
			request.setEndpoint(URI.create(ENDPOINT));
			request.setHttpMethod(HttpMethodName.POST);
			request.addHeader("Content-Type", "application/json");

			return request;
		//}

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

		Request<?> request = generateRequest();

		// Send the request to the server
		sendRequest(request);   

		// Shutdown client, moved to AmazonHttpClientService
		//end();

	}

	// Bulk delete
	public int createBulkDelete(String doc_type, List<String>docIds)
	{

		bulkIndexContents = new StringBuffer();
		String document_type = null;

		if(doc_type !=null && doc_type.equalsIgnoreCase("apr"))
		{
			document_type="author";
		}
		else if(doc_type !=null && doc_type.equalsIgnoreCase("ipr"))
		{
			document_type="affiliation";
		}
		else
			System.out.println("Invalid document type!!!");

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


			bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"" + document_type + "\", \"_id\" : \"" + docIds.get(i) + "\" } }");
			bulkIndexContents.append("\n");

			curRecNum++;
		}


		// delete
		ProcessBulk();
		return getStatusCode();

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
				
				//JSONObject index = (JSONObject)indexes.get("index");
				String _id = (String)index.get("_id");
				String result = (String)index.get("result");
				Long status = (Long)index.get("status");
				if(status != null && (status == 200 || status == 201))
					AuthorCombinerMultiThreads.esIndexed_docs_list.add(_id);
				else
					System.out.print("doc: " + _id + " with status: " + status + " and result: " + result + " failed to index to ES!!!!");

				count ++;
			}

			System.out.println("Total indexed count: " + count);
		}
		catch (org.json.simple.parser.ParseException e) 
		{
			logger.info("Failed to parse AWS ES JSON Response String!!!!");
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	// shutdown the Client 
	public void end()
	{

		try
		{
			if(client !=null)
			{
				client.shutdown();
				System.out.println("AmazonHttpClient Client was shutdown successfully");
			}
		}

		catch(Exception e)
		{
			System.out.println("error occurred during client shutdown");
			System.out.println("Reson: " +  e.getMessage());
			e.printStackTrace();
		}


	}

	public class MyHttpResponseHandler<T> implements HttpResponseHandler<AmazonWebServiceResponse<T>> 
	{

		public AmazonWebServiceResponse<T> handle(com.amazonaws.http.HttpResponse response) throws Exception 
		{

			InputStream responseStream = response.getContent();

			String responseString = IOUtils.toString(responseStream);

			//logger.info(responseString);  // only for debugging
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

		@SuppressWarnings("unchecked")
		public AmazonServiceException handle(com.amazonaws.http.HttpResponse response) throws Exception 
		{
			System.out.println("Request exception handler!");

			AmazonServiceException ase = new AmazonServiceException("What is wrong!.");
			ase.setStatusCode(response.getStatusCode());
			ase.setErrorCode(response.getStatusText());
			ase.setErrorMessage(((Request <HTTPRequest>)response.getHttpRequest()).getContent().toString());

			// set status for DB deletion
			setStatusCode(Integer.toString(response.getStatusCode()));

			//write Json doc content to console for checking the error
			System.out.println("Check Json Bulk Docs if there is syntax error: ");
			System.out.println(bulkIndexContents);


			System.out.println(response.getStatusText());
			System.out.println(response.getStatusCode());
			//System.out.println(ase.getErrorMessage());
			//System.out.println(ase.getRawResponseContent());
			System.out.println(ase.getErrorCode());

			logger.error(ase.getErrorMessage());
			logger.error(ase.getRawResponseContent());


			return ase;
		}


		public boolean needsConnectionLeftOpen() {
			return false;
		}
	}


}
