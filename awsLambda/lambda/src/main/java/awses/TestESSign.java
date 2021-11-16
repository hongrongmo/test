package awses;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class TestESSign {

	
	private static final String SERVICE_NAME = "es";
	private static final String REGION = "us-east-1";
	//private static final String HOST = "search-evcafe-prod-h7xqbezrvqkb5ult6o4sn6nsae.us-east-1.es.amazonaws.com";   // when using IAM rol in ES Access Policy
	private static final String HOST = "localhost:8060";  // for ES with IP range access policy
	
	//private static final String HOST = "search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com";
	private static final String ENDPOINT_ROOT = "http://" + HOST;
	//private static final String PATH = "/cafe/author/aut_M22aaa18f155dfa29a2bM5efd10178163171";  // works well for single doc
	//private static final String PATH = "/cafe/_bulk";
	private static final String PATH = "/cafe/author/_search";		// author search
	//private static final String PATH = "/cafe/author/aut_M22aaa18f155dfa29a2bM6a7f10178163171";  // search single doc
	//private static final String PATH = "/cafe/affiliation/_search";
	private static final String ENDPOINT = ENDPOINT_ROOT + PATH;
	
	
	//ES Open To Public: search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com
	
	private static StringBuffer bulkIndexContents;
	
	
	
	
	
	/// Set up the request
	private static Request <HTTPRequest> generateRequest() {
	       Request<HTTPRequest> request = new DefaultRequest<HTTPRequest>(SERVICE_NAME);
	      // request.setContent(new ByteArrayInputStream("".getBytes()));
	       //request.setContent(new ByteArrayInputStream(esDocument.getBytes()));   // works well for single doc
	       //request.setContent(new ByteArrayInputStream(bulkIndexContents.toString().getBytes()));
	       request.setEndpoint(URI.create(ENDPOINT));
	       request.setHttpMethod(HttpMethodName.GET);
	       
	       System.out.println(bulkIndexContents);
	       
	      Map<String, List<String>> parameters = new HashMap<String, List<String>>();
	       
	       List <String> query = new ArrayList<String>();
	       List <String> start = new ArrayList<String>();
	       List <String> end = new ArrayList<String>();
	       //query.add("audoc.author_affiliations.affiliation_name.affilnamevar:\"Universita di bologna\"");
	       query.add("audoc.affil:\"united\"");
	       start.add("0");
	       end.add("25");
	       
	       parameters.put("q", query);
	       parameters.put("from", start);
	       parameters.put("size", end);
	       request.setParameters(parameters);
	       return request;
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

	/// Send the request to the server
	private static void sendRequest(Request<?> request) {
	       ExecutionContext context = new ExecutionContext(true);

	       ClientConfiguration clientConfiguration = new ClientConfiguration();
	       clientConfiguration.setConnectionTimeout(ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT);
	       clientConfiguration.setConnectionMaxIdleMillis(ClientConfiguration.DEFAULT_CONNECTION_MAX_IDLE_MILLIS);
	       AmazonHttpClient client = new AmazonHttpClient(clientConfiguration);

	       MyHttpResponseHandler<Void> responseHandler = new MyHttpResponseHandler<Void>();
	       MyErrorHandler errorHandler = new MyErrorHandler();

	       Response<Void> response =
	                     client.execute(request, responseHandler, errorHandler, context);
	       System.out.println(response.getAwsResponse());
	}

	public static void main(String[] args) {
		// generate the request Body Contents for index
			//createBulkIndex();
		
		// generate the request Body Contents for delete
			//createBulkDelete();
		
	       // Generate the request
	       Request<?> request = generateRequest();

	       // Perform Signature Version 4 signing
	      // performSigningSteps(request);    // needed when uing IAM role for ES access Policy

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
	
	private static void createBulkDelete()
	{
		bulkIndexContents = new StringBuffer();
		
		bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"author\", \"_id\" : \"aut_M22aaa18f155dfa29a2bM763610178163171\" } }");
		bulkIndexContents.append("\n");
		/*bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"author\", \"_id\" : \"aut_M22aaa18f155dfa29a2bM773010178163171\" } }");
		bulkIndexContents.append("\n");
		bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"author\", \"_id\" : \"aut_M22aaa18f155dfa29a2bM76f010178163171\" } }");
		bulkIndexContents.append("\n");
		bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"author\", \"_id\" : \"aut_M22aaa18f155dfa29a2bM76f010178163171\" } }");
		bulkIndexContents.append("\n");
		bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"author\", \"_id\" : \"aut_M22aaa18f155dfa29a2bM6b0310178163171\" } }");
		bulkIndexContents.append("\n");
		bulkIndexContents.append("{ \"delete\" : { \"_type\" : \"author\", \"_id\" : \"aut_M22aaa18f155dfa29a2bM5efd10178163171\" } }");
		bulkIndexContents.append("\n");*/
		
	}
}
