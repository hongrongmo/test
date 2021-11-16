package awses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.HttpResponseHandler;


/*
 * NOTE: out.println (the new line " not accepted when upload file to linux as it does not seem to get newline same as in windows, so replaced with out.print and explicitly add "\n" it worked
 */
public class GetEsIdAndDocCountScroll 
{
	// Parameters
		private static String doc_type = "apr";
		private static int size = 5000;
		private static String esDomain = "search-evcafe5-ucqg6c7jnb4qbvppj2nee4muwi.us-east-1.es.amazonaws.com";
		private static String sqlldrFileName = "";
		//-----
		
		
		private static String esDocIdFieldName, esDocCountFieldName;
		private static String fileName = "";
		private static PrintWriter out =  null;
		private static String tempTable = "";
		
		
		
		private static String host = "localhost:8040";
		private static String endpoint_root = "http://" + host;

		private static String path = "/cafe/author/_search/?";
		private static String endpoint = endpoint_root + path;


		private static final String SERVICE_NAME = "es";
		private static final String REGION = "us-east-1";
		
		
		private static CloseableHttpClient client;
		private static MyHttpResponseHandler<Void> responseHandler;
		private static MyErrorHandler errorHandler;
		
		//ES Open To Public: search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com
		
		private static StringBuffer bulkQuery = new StringBuffer();
		private final static Logger logger = Logger.getLogger(GetEsIdAndDocCountScroll.class);	

		public static HttpClient getInstance() throws Exception
		{
			synchronized (GetEsIdAndDocCountScroll.class)
			{
				if(client == null)
				{
					try
					{
						CredentialsProvider credsProvider = new BasicCredentialsProvider();
						
						BasicAWSCredentials creds = new BasicAWSCredentials
								(new DefaultAWSCredentialsProviderChain().getCredentials().getAWSAccessKeyId(), 
										new DefaultAWSCredentialsProviderChain().getCredentials().getAWSSecretKey());

						client = HttpClients.custom().setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();  
						
						responseHandler = new MyHttpResponseHandler<Void>();
						errorHandler = new MyErrorHandler();

					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}

				}
			}

			return client;
		}

		// initialize the host
		public static void init()
		{
			host = esDomain;
			endpoint_root = "http://" + host;

			if(doc_type.equalsIgnoreCase("apr"))
			{
				path = "/cafe/author/_search/?";
				esDocIdFieldName = "audoc.auid";
				esDocCountFieldName = "audoc.doc_count";
				tempTable = "ES_APR";
				fileName = "apr-all.out";
				
			}
				
			else if (doc_type.equalsIgnoreCase("ipr"))
			{
				path = "/cafe/affiliation/_search/?";
				esDocIdFieldName = "afdoc.afid";
				esDocCountFieldName = "afdoc.doc_count";
				tempTable = "ES_IPR";
				fileName = "ipr-all.out";
			}
				
			endpoint = endpoint_root + path;
			
			try {
				out = new PrintWriter(new File(fileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}

		// Set up scroll request
			private static HttpPost generateScrollRequest(String scroll_id) {

				//HttpPost request = null;

				HttpPost request = null;
				try {
					
					path = "/_search/scroll";
					endpoint = endpoint_root + path;
					
					URIBuilder uriBuilder = new URIBuilder(endpoint);
					uriBuilder.setParameter("filter_path", "hits.hits.fields,_scroll_id");
					

					
					bulkQuery .append("{\"scroll\" : \"5m\", \"scroll_id\" : \"" + scroll_id + "\"}");
					request = new HttpPost(uriBuilder.build());
					HttpEntity body = new ByteArrayEntity(bulkQuery.toString().getBytes());
					request.addHeader("Content-Type", "application/json");
					request.setEntity(body);
					
							     
					
					//request = new HttpGet(uriBuilder.build());
				} catch (URISyntaxException e) {
					
					System.out.println("Request exception handler!");
					System.out.println(e.getMessage());
					System.out.println(e.getReason());
					System.out.println(e.hashCode());
					
					e.printStackTrace();
				}  

				return request;
			}	
			

			
		// Set up the request
		private static HttpPost generateRequest() {

			//HttpPost request = null;

			HttpPost request = null;
			try {
				
				
				URIBuilder uriBuilder = new URIBuilder(endpoint);
				uriBuilder.setParameter("filter_path", "hits.hits.fields,_scroll_id");
				uriBuilder.setParameter("scroll", "5m");

				
				request = new HttpPost(uriBuilder.build());
				HttpEntity body = new ByteArrayEntity(bulkQuery.toString().getBytes());
				request.addHeader("Content-Type", "application/json");
				request.setEntity(body);
				
						     
				
				//request = new HttpGet(uriBuilder.build());
			} catch (URISyntaxException e) {
				
				System.out.println("Request exception handler!");
				System.out.println(e.getMessage());
				System.out.println(e.getReason());
				System.out.println(e.hashCode());
				
				e.printStackTrace();
			}  

			return request;
		}	
		


		

		/// Send the request to the server
		private static String sendRequest(HttpPost request) {

			String response = null;
			try {
				
				// using ResponseHandler class below
				
				response = client.execute(request,responseHandler);
				
				//System.out.println("Response is:  " +  response);  // only for debugging
				
				
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
			return response;
			
		}
		
		public static void main(String[] args) 
		{

			Pattern pattern = Pattern.compile("\\d+");
			Matcher match;
			String scroll_id = null;
			
			if(args.length >3)
			{
				if(args[0] !=null)
				{
					if(args[0].equalsIgnoreCase("apr") || args[0].equalsIgnoreCase("ipr"))
					{
						doc_type = args[0];
						System.out.println("Pull all IDS from ES for: " + doc_type);
					}
					else
					{
						System.out.println("Invalid doc_type, re-try with doc_type as apr or ipr");
						System.exit(1);
					}
						
					
				}
				if(args[1] != null)
				{
					match = pattern.matcher(args[1]);
					if(match.matches())
						size = Integer.parseInt(args[1]);
				}
				if(args[2] !=null)
				{
					esDomain = args[2];
					System.out.println("ES domain: " + esDomain);
				}
				if(args[3] != null)
				{
					sqlldrFileName = args[3];
					System.out.println("SqlldrFileName: " + sqlldrFileName);
				}
			}
			else
			{
				System.out.println("not enough parameters! retry with doc_type, size, ES domain, and sqlldrFileName");
				System.exit(1);
			}
			
			try 
			{
				// create Apache HttpClient
				GetEsIdAndDocCountScroll.getInstance();
				
				init();
				
				// generate ES 1st query
				bulkQuery .append("{\"size\":" + size + ",\"from\":0, \"stored_fields\": [\"" + esDocIdFieldName + "\",\"" + esDocCountFieldName + "\"], \"query\" : { \"match_all\" : {}}}");
				
				
				// Generate the request
				HttpPost request = generateRequest();

				
				// Send the request to the server
				if(request !=null)
				{
					scroll_id = sendRequest(request);		// initial request
					
					//reset bulk query 
					
					bulkQuery.delete(0, bulkQuery.length());
					
				
				  while(scroll_id != null) // later scroll requests 
				  { 
					  request = generateScrollRequest(scroll_id); 
					  scroll_id = sendRequest(request); 
				  }
				 
					// close the connection
					end();
					
					GetEsIdAndDocCountScroll obj = new GetEsIdAndDocCountScroll();
					
					// truncate ES count tables and load the outfile to oracle table
					obj.cleanUpTempTables();
					
				}
			} 

			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		
		
		// for AssetMetadata Request Handler
		public static class MyHttpResponseHandler<T> implements ResponseHandler<String> {

			public String handleResponse(HttpResponse response) {

				String responseString = null;
				 InputStream responseStream = null;
				try {
	            responseStream = response.getEntity().getContent();

	            
	            responseString = parseESIndexJSONResponse(responseStream);
	            
	          
	            
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if(responseStream !=null)
							responseStream.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}

	           
	           
				 return responseString;
	           
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
			
			try
			{
				if(out !=null)
				{
					out.flush();
					out.close();
					
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		@SuppressWarnings("unchecked")
		
		// parse JSON response to fetch doc_id and doc_count from httpResponse, by passing httprespoonse as string
		public static String parseESIndexJSONResponse(String esResponseString)
		{
			JSONObject index;
			Iterator<String> docIdIterator;
			Iterator<Long> docCountIterator;
			
			String doc_id = "";
			long doc_count = 0;
			String scroll_id = null;

			JSONParser parser = new JSONParser();
					
			try
			{
					//System.out.println(esResponseString);
					Object obj = parser.parse(esResponseString);

					JSONObject jsonObject = (JSONObject) obj;

					//_scroll_id
					
					scroll_id = (String) jsonObject.get("_scroll_id");
					//System.out.println("scroll_id: " + scroll_id);
					
					if(esResponseString.contains("hits"))
					{
						//Hits
						jsonObject = (JSONObject) jsonObject.get("hits");
						
						//Items
						JSONArray hits1 = (JSONArray) jsonObject.get("hits");
						

						@SuppressWarnings("unchecked")
						Iterator<JSONObject> indexesIterator = (Iterator<JSONObject>)hits1.iterator();
						while(indexesIterator.hasNext())
						{
							//indexes list
							index = indexesIterator.next();
							jsonObject =  (JSONObject) index.get("fields");
							
							// doc_ids returned as JSONArray
							JSONArray doc_ids = (JSONArray)jsonObject.get(esDocIdFieldName);
							if(doc_ids !=null)
							{
								docIdIterator = (Iterator<String>)doc_ids.iterator();
								doc_id = docIdIterator.next();
							}
							else
								doc_id = "not exist";
							
							
							// doc_counts returned as JSONArray
							JSONArray doc_counts = (JSONArray)jsonObject.get(esDocCountFieldName);
							if(doc_counts !=null)
							{
								docCountIterator = (Iterator<Long>)doc_counts.iterator();
								doc_count = docCountIterator.next();
							}
							else
								doc_count = 0;
						
							out.print(doc_id + "\t" + Long.toString(doc_count)+"\n");
							//System.out.println("doc_id: " + doc_id + " , doc_count: " + doc_count);
						}
					}
					
					else
					{
						System.out.println("no hits for scroll_id:  " + scroll_id);
						System.out.println("Reset the scroll and stop");
						scroll_id = null;
					}
					
				//}
				
				
			}
			
			  catch (org.json.simple.parser.ParseException e) {
			  logger.info("Failed to parse AWS ES JSON Response String!!!!");
			  logger.error(e.getMessage()); e.printStackTrace();
			  
			  }
			 
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return scroll_id;

		}
		
		
		
		// parse JSON response to fetch doc_id and doc_count from httpResponse by passing httprespoonse as inputstream
		
		@SuppressWarnings("unchecked")
		public static String parseESIndexJSONResponse(InputStream esResponseInputStream)
		{

			JSONObject index;
			Iterator<String> docIdIterator;
			Iterator<Long> docCountIterator;

			String doc_id = "";
			long doc_count = 0;
			String scroll_id = null;

			BufferedReader breader = null;
			JSONParser parser = new JSONParser();

			try
			{

				breader = new BufferedReader(new InputStreamReader(esResponseInputStream));
				// breader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(breader.readLine().replace("},{","},\n{").getBytes())));



				//BufferedReader breader = new BufferedReader(new
				String reponseLine;
				while((reponseLine = breader.readLine()) !=null) 
				{

					//System.out.println(reponseLine);


					Object obj = parser.parse(reponseLine);

					JSONObject jsonObject = (JSONObject) obj;


					//_scroll_id

					scroll_id = (String) jsonObject.get("_scroll_id");
					//System.out.println("scroll_id: " + scroll_id);

					if(reponseLine != null && reponseLine.contains("hits"))
					{
						//Hits
						jsonObject = (JSONObject) jsonObject.get("hits");

						//Items
						JSONArray hits1 = (JSONArray) jsonObject.get("hits");


						@SuppressWarnings("unchecked")
						Iterator<JSONObject> indexesIterator = (Iterator<JSONObject>)hits1.iterator();
						while(indexesIterator.hasNext())
						{
							//indexes list
							index = indexesIterator.next();
							jsonObject =  (JSONObject) index.get("fields");

							// doc_ids returned as JSONArray
							JSONArray doc_ids = (JSONArray)jsonObject.get(esDocIdFieldName);
							if(doc_ids !=null)
							{
								docIdIterator = (Iterator<String>)doc_ids.iterator();
								doc_id = docIdIterator.next();
							}
							else
								doc_id = "not exist";


							// doc_counts returned as JSONArray
							JSONArray doc_counts = (JSONArray)jsonObject.get(esDocCountFieldName);
							if(doc_counts !=null)
							{
								docCountIterator = (Iterator<Long>)doc_counts.iterator();
								doc_count = docCountIterator.next();
							}
							else
								doc_count = 0;

							out.print(doc_id + "\t" + Long.toString(doc_count)+"\n");
							//System.out.println("doc_id: " + doc_id + " , doc_count: " + doc_count);
						}
					}
					else
					{
						System.out.println("no hits for scroll_id:  " + scroll_id);
						System.out.println("Reset the scroll and stop");
						scroll_id = null;
					}

				}


			}



			
			  catch (UnsupportedEncodingException e) { e.printStackTrace(); } catch
			  (IOException e) { e.printStackTrace(); }
			 
			 
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(breader != null)
						breader.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				
			}
			
			
			return scroll_id;

		}
		
		
		public void loadESDocIDAndDocCount(List<String> ids)
		{
			try
			{

				/* using for loop caused "ORA-01000: maximum open cursors exceeded" wnen # of APR records to update was 6k+ so have to load to 
							table instead

				 */
				/************** load data into temp table ****************/
				System.out.println("about to load ES DOC_IDs and Doc_Counts to temp table: " + tempTable);

				Runtime r = Runtime.getRuntime();
				Process p = r.exec(sqlldrFileName + " " + fileName);
				int t = p.waitFor();

				//the value 0 indicates normal termination.
				System.out.println(doc_type + "Sqlldr process complete with exit status: " + t);

			}
			catch(Exception e)
			{
				System.out.println("Error updating profile's es_status!! " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		private void cleanUpTempTables()
		{
			
		}
	/*
	 * protected Connection getConnection (String connectionURL, String driver,
	 * String username, String password) throws Exception {
	 * System.out.println("connectionURL: " + connectionURL);
	 * System.out.println("Driver: " + driver); System.out.println("username: " +
	 * username); System.out.println("password: " + password);
	 * 
	 * Class.forName(driver); Connection con =
	 * DriverManager.getConnection(connectionURL, username, password); return con; }
	 */
		
}
