package awses;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;

import com.google.common.base.Supplier;


import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;




import vc.inreach.aws.request.AWSSigner;
import vc.inreach.aws.request.AWSSigningRequestInterceptor;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * 
 * @author TELEBH
 * @version: 1.0
 * @Date: 07/20/2016
 * @Description: Connect to AWS ES using Jest HTTP Rest Client 
 */
public class ESJestClient {

	// HH 08/01/2017 Temp commented out because it complains when run "Maven->build" @ line 57, uncomment when needed
	/*
	private static final String SERVICE = "es";
	private static final String REGION = "us-east-1";
	
	public static void main(String[] args) throws IOException {
		
		// Construct a new Jest Client via factory
//		JestClientFactory factory = new JestClientFactory();   //original, works fine
		
		Supplier<LocalDateTime> clock = () -> LocalDateTime.now(ZoneOffset.UTC);
		
		AWSCredentialsProvider credsProvider = new DefaultAWSCredentialsProviderChain();
		AWSCredentials credentials=	credsProvider.getCredentials();
		System.out.println(credentials.getAWSAccessKeyId());
		System.out.println(credentials.getAWSSecretKey());
		
		
		final AWSSigner awsSigner = new AWSSigner(credsProvider, REGION, SERVICE, clock);
		
		 AWS4Signer signer = new AWS4Signer();
	       signer.setServiceName(SERVICE);
	       signer.setRegionName(REGION);      
	       AWSCredentials creds = credsProvider.getCredentials();

	      
	       

		final AWSSigningRequestInterceptor requestInterceptor = new AWSSigningRequestInterceptor(awsSigner);

		
		JestClientFactory factory = new JestClientFactory()
		{
			@Override
			protected HttpClientBuilder configureHttpClient(HttpClientBuilder builder)
			{
				
				builder.addInterceptorLast(requestInterceptor);
				return builder;
			}
			
		};
		factory.setHttpClientConfig(new HttpClientConfig
				.Builder("http://search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com:80")
				.multiThreaded(true)
				.build()
				);
		
		factory.setHttpClientConfig(new HttpClientConfig
				.Builder("http://search-evcafe-prod-h7xqbezrvqkb5ult6o4sn6nsae.us-east-1.es.amazonaws.com:80")
				.multiThreaded(true)
				.build()
				);
		
		
		// Index ES Document
		JestClient client = factory.getObject();
		
		String esDocument = "{\n\"docproperties\":\n"+
				"{\n"+
		"\"doc_type\": \"apr\",\n"+
		"\"status\": \"update\",\n"+
		"\"loaddate\": \"20160601\",\n"+
		"\"itemtransactionid\": \"2015-09-01T04:32:52.537345Z\",\n"+
		"\"indexeddate\": \"1441081972\",\n"+
		"\"esindextime\": \"2016-07-19T17:52:43.404Z\",\n"+
		"\"loadnumber\": \"401600\"\n"+
	"},\n"+
	"\"audoc\":\n"+ 
	"{\n"+
		"\"docid\": \"aut_M22aaa18f155dfa29a2bM7f9b10178163171\",\n"+
		"\"eid\": \"9-s2.0-56798528800\",\n"+
		"\"auid\": \"56798528800\",\n"+
		"\"orcid\": \"null\",\n"+
		"\"author_name\":\n"+ 
			"{\n"+
				"\"variant_name\":\n"+ 
				"{\n"+
					"\"variant_first\": [  ],\n"+
					"\"variant_ini\": [  ],\n"+
					"\"variant_last\": [  ]\n"+
				"},\n"+
				"\"preferred_name\":\n"+ 
				"{\n"+
					"\"preferred_first\": \"Iv&aacute;n J.\",\n"+
					"\"preferred_ini\": \"I.J.\",\n"+
					"\"preferred_last\": \"Bazany-Rodr&iacute;guez\"\n"+
				"}\n"+
			"},\n"+
		"\"subjabbr\":\n"+ 
		"[\n"+
			"{ \"frequency\": \"3\" , \"code\": \"PHYS\" },\n"+
			"{ \"frequency\": \"5\" , \"code\": \"MATE\" },\n"+
			"{ \"frequency\": \"1\" , \"code\": \"CHEM\" },\n"+
			"{ \"frequency\": \"1\" , \"code\": \"ENGI\" }\n"+
		"],\n"+
		"\"subjclus\": [ \"PHYS\" , \"MATE\" , \"CHEM\" , \"ENGI\" ],\n"+
		"\"pubrangefirst\": \"2015\",\n"+
		"\"pubrangelast\": \"2016\",\n"+
		"\"srctitle\": [ \"Acta Crystallographica Section E: Crystallographic Communications\" , \"Sensors and Actuators, B: Chemical\" ],\n"+
		"\"issn\": [ \"20569890\" , \"09254005\" ],\n"+
		"\"email\": \"\",\n"+
		"\"author_affiliations\":\n"+ 
		"{\n"+
			"\"current\":\n"+ 
			"{\n"+
				"\"afid\": \"60032442\",\n"+
				"\"display_name\": \"Universidad Nacional Autonoma de Mexico\",\n"+
				"\"display_city\": \"Mexico City\",\n"+
				"\"display_country\": \"Mexico\",\n"+
				"\"sortname\": \"National Autonomous University of Mexico\"\n"+
			"},\n"+
			"\"history\":\n"+ 
			"{\n"+
				"\"afhistid\": [  ],\n"+
				"\"history_display_name\": [  ],\n"+
				"\"history_city\": [  ],\n"+
				"\"history_country\": [  ]\n"+
			"},\n"+
			"\"parafid\": [ \"60032442\" ],\n"+
			"\"affiliation_name\":\n"+ 
			"{\n"+
				"\"affilprefname\": [ \"Universidad Nacional Autonoma de Mexico\" ],\n"+
				"\"affilnamevar\": [ \"UNAM\" , \"Universidad Nacional Aut&oacute;noma de M&eacute;xico\" ]\n"+
			"},\n"+
			"\"city\": [ \"Mexico City\" ],\n"+
			"\"country\": [ \"Mexico\" ],\n"+
			"\"nameid\": [ \"Universidad Nacional Autonoma de Mexico#60032442\" ],\n"+
			"\"deptid\": \"104652099\",\n"+
			"\"dept_display_name\": \"Universidad Nacional Autonoma de Mexico, Institute of Chemistry\",\n"+
			"\"dept_city\": \"Mexico City\",\n"+
			"\"dept_country\": \"Mexico\"\n"+
		"}\n"+
	"}\n"+
"}";

		
		Index index = new Index.Builder(esDocument).index("cafe").type("author").id("aut_M22aaa18f155dfa29a2bM7f9b10178163171").build();
		client.execute(index);		// for index
		
	}
	
	
	// aws credentials

		static byte[] HmacSHA256(String data, byte[] key) throws Exception  {
		     String algorithm="HmacSHA256";
		     Mac mac = Mac.getInstance(algorithm);
		     mac.init(new SecretKeySpec(key, algorithm));
		     return mac.doFinal(data.getBytes("UTF8"));
		}

		static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) 
		{
			 byte[] kSigning = null;
			try
			{
		     byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
		     byte[] kDate    = HmacSHA256(dateStamp, kSecret);
		     byte[] kRegion  = HmacSHA256(regionName, kDate);
		     byte[] kService = HmacSHA256(serviceName, kRegion);
		     kSigning = HmacSHA256("aws4_request", kService);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		     return kSigning;
		}
		
*/
}
