package awses;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
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




















import vtw.DigestAuthentication;
import awses.TestESSign.MyErrorHandler;
import awses.TestESSign.MyHttpResponseHandler;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.services.waf.model.HTTPRequest;
import com.amazonaws.util.IOUtils;

/****
 * 
 * @author telebh
 * @Date: 11/01/2016
 * @Description: current way of ES Index using amazon HTTPClient seems to be not functioning properly, no idea why
 * so i am going to try other httpclient which is apache HTTp Client as for VTW
 * it did not work as it was unable to cast from AWSCredentials to Credentials, SO IGNORE ABOUT THIS CLASS IT DOES NOT WORK,
 * IT IS NOT IN USE, IT WAS FOR TESTING CERATIN FUNCYIONALITY AND IT DID NOT WORK
 * 
 */
public class ESHttp {

	
	private static final String HOST = "search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com";
	private static final String ENDPOINT_ROOT = "https://" + HOST;

	private static final String PATH = "/search/?";
	private static final String ENDPOINT = ENDPOINT_ROOT + PATH;


	private static final String SERVICE_NAME = "es";
	private static final String REGION = "us-east-1";
	
	
	private static CloseableHttpClient client;
	private static MyHttpResponseHandler<Void> responseHandler;
	private static MyErrorHandler errorHandler;
	
	//ES Open To Public: search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com
	
	private static StringBuffer bulkIndexContents;
		

	public static HttpClient getInstance() throws Exception
	{
		synchronized (ESHttp.class)
		{
			if(client == null)
			{
				try
				{
					CredentialsProvider credsProvider = new BasicCredentialsProvider();
					
					BasicAWSCredentials creds = new BasicAWSCredentials
							(new DefaultAWSCredentialsProviderChain().getCredentials().getAWSAccessKeyId(), 
									new DefaultAWSCredentialsProviderChain().getCredentials().getAWSSecretKey());
					
					
					credsProvider.setCredentials(new AuthScope(HOST, 443), (Credentials)creds);
					
					//client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();    // works well for the metadata download
					
					client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
							.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();  
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


	// Set up the request
	private static HttpPost generateRequest() {

		//HttpPost request = null;

		HttpPost request = null;
		try {
			
			
			URIBuilder uriBuilder = new URIBuilder(ENDPOINT);
			uriBuilder.setParameter("Accept", "application/json");



			request = new HttpPost(uriBuilder.build());
			HttpEntity body = new ByteArrayEntity(bulkIndexContents.toString().getBytes());
			request.setEntity(body);
			
			
		       System.out.println(bulkIndexContents);
		       
		     
			
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
	private static void sendRequest(HttpPost request) {

		try {
			
			// using ResponseHandler class below
			
			AmazonWebServiceResponse<Void> response = client.execute(request,responseHandler);
			
			System.out.println("Response is:  " +  response.getResponseMetadata());
			
			
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
		
		
	}
	
	public static void main(String[] args) {

		
		try 
		{
			// create Apache HttpClient
			ESHttp.getInstance();
			
			
			// Generate the request
			HttpPost request = generateRequest();

			
			// Send the request to the server
			if(request !=null)
			{
				sendRequest(request);
				
				// close the connection
				end();
			}
		} 

		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	
	// for AssetMetadata Request Handler
	public static class MyHttpResponseHandler<T> implements ResponseHandler<AmazonWebServiceResponse<T>> {

		
		//@Override
		//public CloseableHttpResponse handleResponse(HttpResponse response)
		public AmazonWebServiceResponse<T> handleResponse(HttpResponse response) {

			AmazonWebServiceResponse<T> awsResponse = null;
			try {
            InputStream responseStream = ((com.amazonaws.http.HttpResponse) response).getContent();

            String responseString;
			
				responseString = IOUtils.toString(responseStream);
			
            System.out.println(responseString);

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
	}
	
	
	private static void createBulkIndex()
	{
		bulkIndexContents = new StringBuffer();
		
		bulkIndexContents.append("{ \"index\" : { \"_type\" : \"author\", \"_id\" : \"aut_M22aaa18f155dfa29a2bM7f9b10178163171\" } }");
		bulkIndexContents.append("\n");
		
		String es_Document = "{\"docproperties\":"+
				"{"+
		"\"doc_type\": \"apr\","+
		"\"status\": \"update\","+
		"\"loaddate\": \"20160601\","+
		"\"itemtransactionid\": \"2015-09-01T04:32:52:537345Z\","+
		"\"indexeddate\": \"1441081972\","+
		"\"esindextime\": \"2016-07-19T17:52:43:404Z\","+
		"\"loadnumber\": \"401600\""+
	"},"+
	"\"audoc\":"+ 
	"{"+
		"\"doc_id\": \"aut_M22aaa18f155dfa29a2bM7f9b10178163171\","+
		"\"eid\": \"9-s2.0-56798528800\","+
		"\"auid\": \"56798528800\","+
		"\"orcid\": \"null\","+
		"\"author_name\":"+ 
			"{"+
				"\"variant_name\":"+ 
				"{"+
					"\"variant_first\": [  ],"+
					"\"variant_ini\": [  ],"+
					"\"variant_last\": [  ]"+
				"},"+
				"\"preferred_name\":"+ 
				"{"+
					"\"preferred_first\": \"Iv&aacute;n J.\","+
					"\"preferred_ini\": \"I.J.\","+
					"\"preferred_last\": \"Bazany-Rodr&iacute;guez\""+
				"}"+
			"},"+
		"\"subjabbr\":"+ 
		"["+
			"{ \"frequency\": \"3\" , \"code\": \"PHYS\" },"+
			"{ \"frequency\": \"5\" , \"code\": \"MATE\" },"+
			"{ \"frequency\": \"1\" , \"code\": \"CHEM\" },"+
			"{ \"frequency\": \"1\" , \"code\": \"ENGI\" }"+
		"],"+
		"\"subjclus\": [ \"PHYS\" , \"MATE\" , \"CHEM\" , \"ENGI\" ],"+
		"\"pubrangefirst\": \"2015\","+
		"\"pubrangelast\": \"2016\","+
		"\"srctitle\": [ \"Acta Crystallographica Section E: Crystallographic Communications\" , \"Sensors and Actuators, B: Chemical\" ],"+
		"\"issn\": [ \"20569890\" , \"09254005\" ],"+
		"\"email\": \"\","+
		"\"author_affiliations\":"+ 
		"{"+
			"\"current\":"+ 
			"{"+
				"\"afid\": \"60032442\","+
				"\"display_name\": \"Universidad Nacional Autonoma de Mexico\","+
				"\"display_city\": \"Mexico City\","+
				"\"display_country\": \"Mexico\","+
				"\"sortname\": \"National Autonomous University of Mexico\""+
			"},"+
			"\"history\":"+ 
			"{"+
				"\"afhistid\": [  ],"+
				"\"history_display_name\": [  ],"+
				"\"history_city\": [  ],"+
				"\"history_country\": [  ]"+
			"},"+
			"\"parafid\": [ \"60032442\" ],"+
			"\"affiliation_name\":"+ 
			"{"+
				"\"affilprefname\": [ \"Universidad Nacional Autonoma de Mexico\" ],"+
				"\"affilnamevar\": [ \"UNAM\" , \"Universidad Nacional Aut&oacute;noma de M&eacute;xico\" ]"+
			"},"+
			"\"city\": [ \"Mexico City\" ],"+
			"\"country\": [ \"Mexico\" ],"+
			"\"nameid\": [ \"Universidad Nacional Autonoma de Mexico#60032442\" ],"+
			"\"deptid\": \"104652099\","+
			"\"dept_display_name\": \"Universidad Nacional Autonoma de Mexico, Institute of Chemistry\","+
			"\"dept_city\": \"Mexico City\","+
			"\"dept_country\": \"Mexico\""+
		"}"+
	"}"+
	"}";

		bulkIndexContents.append(es_Document);
		bulkIndexContents.append("\n");
		
		// add the 2nd doc
		
		bulkIndexContents.append("{ \"index\" : { \"_type\" : \"author\", \"_id\" : \"aut_M22aaa18f155dfa29a2bM5f1e10178163171\" } }");
		bulkIndexContents.append("\n");
		es_Document = "{"+
				"\"docproperties\":"+
				"{"+
			"\"doc_type\": \"apr\","+
			"\"status\": \"update\","+
			"\"loaddate\": \"20160601\","+
			"\"itemtransactionid\": \"2015-07-23T19:13:56:000001Z\","+
			"\"indexeddate\": \"1437678836\","+
			"\"esindextime\": \"2016-07-28T01:33:56:180Z\","+
			"\"loadnumber\": \"401600\""+
				"},"+
				"\"audoc\": "+
				"{"+
					"\"doc_id\": \"aut_M22aaa18f155dfa29a2bM5f1e10178163171\","+
					"\"eid\": \"9-s2.0-18435772600\","+
					"\"auid\": \"18435772600\","+
					"\"orcid\": \"\","+
					"\"author_name\":"+ 
					"{"+
					"\"variant_name\":"+ 
					"{"+
						"\"variant_first\": [ \"J.\" ],"+
						"\"variant_ini\": [ \"J.\" ],"+
						"\"variant_last\": [ \"Charoud-Got\" ]"+
					"},"+
					"\"preferred_name\": "+
					"{"+
						"\"preferred_first\": \"Jean\","+
						"\"preferred_ini\": \"J.\","+
						"\"preferred_last\": \"Charoud-Got\""+
					"}"+
					"},"+
					"\"subjabbr\": "+
						"["+
						 "{ \"frequency\": \"1\" , \"code\": \"BIOC\" },"+
						 "{ \"frequency\": \"5\" , \"code\": \"PHAR\" },"+
						 "{ \"frequency\": \"6\" , \"code\": \"ENER\" },"+
						 "{ \"frequency\": \"2\" , \"code\": \"PHYS\" },"+
						 "{ \"frequency\": \"1\" , \"code\": \"MATE\" },"+
						 "{ \"frequency\": \"6\" , \"code\": \"MEDI\" },"+
						 "{ \"frequency\": \"4\" , \"code\": \"ENVI\" },"+
						 "{ \"frequency\": \"4\" , \"code\": \"CENG\" },"+
						 "{ \"frequency\": \"8\" , \"code\": \"AGRI\" },"+
						 "{ \"frequency\": \"10\" , \"code\": \"CHEM\" },"+
						 "{ \"frequency\": \"1\" , \"code\": \"ENGI\" }"+
						 "],"+
						 "\"subjclus\": [ \"BIOC\" , \"PHAR\" , \"ENER\" , \"PHYS\" , \"MATE\" , \"MEDI\" , \"ENVI\" , \"CENG\" , \"AGRI\" , \"CHEM\" , \"ENGI\" ],"+
						 "\"pubrangefirst\": \"2007\","+
						 "\"pubrangelast\": \"2015\","+
						 "\"srctitle\": [ \"Food Additives and Contaminants - Part A Chemistry&#x0002C; Analysis&#x0002C; Control&#x0002C; Exposure and Risk Assessment\" , \"Food Chemistry\" , \"Particle and Particle Systems Characterization\" , \"Energy and Fuels\" , \"Accreditation and Quality Assurance\" , \"Journal of pharmaceutical sciences\" , \"Food Chemistry\" , \"Analytical and bioanalytical chemistry\" , \"Analytical and Bioanalytical Chemistry\" , \"Food Additives and Contaminants - Part A Chemistry&#x0002C; Analysis&#x0002C; Control&#x0002C; Exposure and Risk Assessment\" , \"Energy and Fuels\" , \"Journal of AOAC International\" , \"Frontiers in Chemistry\" , \"Journal of Pharmaceutical Sciences\" , \"Food chemistry\" ],"+
						 "\"issn\": [ \"14645122\" , \"18737072\" , \"09340866\" , \"15205029\" , \"09491775\" , \"15206017\" , \"03088146\" , \"16182650\" , \"16182650\" , \"19440049\" , \"08870624\" , \"10603271\" , \"22962646\" , \"15206017\" , \"03088146\" ],"+
						 "\"email\": \"\","+
						 "\"author_affiliations\":"+ 
						 "{"+
						"\"current\":"+ 
						"{"+
						"\"afid\": \"60029640\","+
						"\"display_name\": \"European Commission\","+
						"\"display_city\": \"Brussels\","+
						"\"display_country\": \"Belgium\","+
						"\"sortname\": \"European Commission\""+
						"},"+
						"\"history\": "+
						"{"+
							"\"afhistid\": [ \"60070032\" ],"+
							"\"history_display_name\": [ \"European Commission Joint Research Centre&#x0002C; Institute for Reference Materials and Measurements\" ],"+
							"\"history_city\": [ \"Geel\" ],"+
							"\"history_country\": [ \"Belgium\" ]"+
						"},"+
						"\"affiliation_name\": "+
						"{"+
							"\"affilprefname\": [ \"European Commission\" , \"European Commission Joint Research Centre&#x0002C; Institute for Reference Materials and Measurements\" ],"+
							"\"affilnamevar\": [ \"European Commission\" , \"Commission of the European Communities\" , \"IRMM\" , \"Institute for Reference Materials and Measurements\" , \"Central Bureau for Nuclear Measurements\" ]"+
						"},"+
						"\"nameid\": [ \"European Commission#60029640\" , \"European Commission Joint Research Centre&#x0002C; Institute for Reference Materials and Measurements#60070032\" ],"+
						"\"deptid\": \"103778060\","+
						"\"dept_display_name\": \"University of Nottingham&#x0002C; Faculty of Engineering\","+
						"\"dept_city\": \"Nottingham\","+
						"\"dept_country\": \"United Kingdom\""+
						 "}"+
				"}"+
			"}";
		
		bulkIndexContents.append(es_Document);
		bulkIndexContents.append("\n");
		
	}
	
}
