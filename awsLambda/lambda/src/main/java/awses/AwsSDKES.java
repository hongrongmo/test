package awses;

import java.io.File;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

//import org.elasticsearch.index.Index;












import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.elasticsearch.*;
import com.amazonaws.services.elasticsearch.model.DescribeElasticsearchDomainRequest;
import com.amazonaws.services.elasticsearch.model.ListDomainNamesRequest;
import com.amazonaws.services.elasticsearch.model.ListDomainNamesResult;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.handlers.RequestHandler2;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;


public class AwsSDKES{

	public static void main(String[] args) {

		JestClient jclient = null;
		try
		{


			/*// aws java sdk
		//ClientConfiguration config = new ClientConfiguration().withClientExecutionTimeout(1000).withMaxConnections(1000);
		ClientConfiguration config = new ClientConfiguration().withMaxConnections(1000);

		AWSElasticsearchClient client = new AWSElasticsearchClient(new EnvironmentVariableCredentialsProvider(), config);
		client.setEndpoint("search-evcafe-prod-h7xqbezrvqkb5ult6o4sn6nsae.us-east-1.es.amazonaws.com");
		client.describeElasticsearchDomain(new DescribeElasticsearchDomainRequest().withDomainName("search-evcafe-prod-h7xqbezrvqkb5ult6o4sn6nsae.us-east-1.es.amazonaws.com"));
		client.shutdown();
			 */


            AWSElasticsearchClient esClient = new AWSElasticsearchClient(new PropertiesCredentials(new File("AwsCredentials.properties")));	
            ListDomainNamesResult esDomainList = new ListDomainNamesResult();		
            ListDomainNamesRequest esDomainRequest = new ListDomainNamesRequest();		
            esDomainList = esClient.listDomainNames(esDomainRequest);		
            for (int i = 0; i < esDomainList.getDomainNames().size(); i++)
            {
            	System.out.println(esDomainList.getDomainNames().get(i));
            }	

	
            
			
			String secretKey="6IQ5TG1abov49mq3V7sQodRXOCXSxMqhVq3/GViQ";
			String dateStamp = "08-05-2016";
			String regionName = "us-east-1";
			String serviceName = "es";
			byte[] signedKey = getSignatureKey(secretKey, dateStamp, regionName, serviceName);
			
			
			
			// jest

			JestClientFactory factory = new JestClientFactory();
			factory.setHttpClientConfig(new HttpClientConfig
					.Builder("http://search-evcafe-prod-h7xqbezrvqkb5ult6o4sn6nsae.us-east-1.es.amazonaws.com:80")
			.multiThreaded(true)
			.build()
					);

			jclient = factory.getObject();

			String esDocument = "{\"key\": \"ES\"}";

			Index index = new Index.Builder(esDocument).index("cafe").type("author").id("aut_M22aaa18f155dfa29a2bM7f9b10178163171").build();
			DocumentResult result = jclient.execute(index);

			if(result.isSucceeded())
			{
				System.out.println(result.getJsonString());
			}
			else
			{
				System.out.println(result.getJsonString());
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			if(jclient !=null)
			{
				jclient.shutdownClient();
			}
		}

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


}
