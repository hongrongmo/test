package vtw.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class QueryVtwMetadataApi {
	
	private final ObjectMapper mapperJson = new ObjectMapper();

	private final String PROD_METADATA_URL = "https://vtw.elsevier.com/metadata/";
	private Logger logger;
	private String userName = null;
    private String password = null;
    

	HttpClient client = null;
	
    public QueryVtwMetadataApi(){
        //client = getHttpClient();
    	logger = Logger.getLogger(QueryVtwMetadataApi.class);
    	logger.setLevel(Level.ERROR);
    	retrieveCredentials();
    }

    public void retrieveCredentials() {
    	AmazonRetrieveSecretManagerSecrets secretManagerObj = new AmazonRetrieveSecretManagerSecrets();
		
		secretManagerObj.getSecret();
		
		
		Map<String, String> credentials = secretManagerObj.getCredentials();
		
			userName = credentials.get("userName");
			password = credentials.get("password");
    }
    public ObjectNode retrieveExistingMetadata(String type, String identifier) throws Exception {
        ObjectNode existingMetadata = null;
        client = getHttpClient();

        String url = PROD_METADATA_URL + type + "/" + identifier + "?mode=full";
        HttpResponse response = sendHttpRequest(url);

        if (response.getStatusLine().getStatusCode() == 200) {
            existingMetadata = (ObjectNode) mapperJson.readTree(response.getEntity().getContent());
        }
        return existingMetadata;
    }

    private HttpResponse sendHttpRequest(String url) throws Exception {
        HttpGet request = new HttpGet(url);
        request.addHeader("accept", "application/ld+json");
        request.addHeader("X-ELS-VTW-Version", "3.0");
        return client.execute(request);
    }

    private HttpClient getHttpClient(){
       
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);

        provider.setCredentials(AuthScope.ANY, credentials);
        return HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
    }

    public void traverseMetadata(JsonNode metadataNode)
    {
    	int max = Integer.MIN_VALUE;
    	Map<Integer, String> generations = new HashMap<>();
    	
    	try
    	{
    		if(metadataNode != null)
        	{
        		//JSON Node to Pretty String
        		String prettyString = mapperJson.writerWithDefaultPrettyPrinter().writeValueAsString(metadataNode);
        		System.out.println("Metadata: \\n" + prettyString);
        		
        		if(metadataNode.isObject())
        		{
        			Iterator<String> fieldNamesItr = metadataNode.fieldNames();
        			while(fieldNamesItr.hasNext())
        			{
        				String key = fieldNamesItr.next();
        				if(key.equalsIgnoreCase("bam:generation"))
        				{
        					max = Integer.parseInt(metadataNode.get(key).toString()) > max? Integer.parseInt(metadataNode.get(key).toString()): max;
        					generations.put(Integer.parseInt(metadataNode.get(key).toString()), "");
        				}
        				if(key.equalsIgnoreCase("bam:hasAsset"))
        				{
        					JsonNode value = metadataNode.get(key);
        					String url = value.get(0).asText();
        					System.out.println("URL: " + url);
        				}
        				if(key.equalsIgnoreCase("Metadata") || key.equalsIgnoreCase("bam:hasGeneration"))
        				{
        					JsonNode value = metadataNode.get(key);
        					//recursive call
            				traverseMetadata(value);
        				}	
        			}
        		}
        		
        		// If it is an array
        		else if(metadataNode.isArray())
        		{
        			ArrayNode arrNode = (ArrayNode)metadataNode;
        			for(int i=0; i<arrNode.size(); i++)
        			{
        				JsonNode arr = arrNode.get(i);
        				//recursive call
        				traverseMetadata(arr);
        			}
        		}
        		//if it is a single value field
        		else
        		{
        			System.out.println("fieldName =" +  metadataNode.asText());
        		}
        	}
    	}
    	catch(Exception e)
    	{
    		logger.error("Exception at parsing Returned Json Metadata!");
    		logger.error("Reason: " + e.getMessage());
    		e.printStackTrace();
    	}
    }
    public static void main(String[] args) throws Exception
    {
    	QueryVtwMetadataApi obj = new QueryVtwMetadataApi();
    	ObjectNode node = obj.retrieveExistingMetadata("pat", "EP3933456A1");
    	obj.traverseMetadata(node);
    }
}
