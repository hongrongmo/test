package vtw.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
//import org.ei.dataloading.upt.loadtime.vtw.VTWAssetAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vtw.VTWAssetAPI;

public class QueryVtwMetadataApi {
	
	private final ObjectMapper mapperJson = new ObjectMapper();

	private final String PROD_METADATA_URL = "https://vtw.elsevier.com/metadata/";
	private Logger logger;
	private String userName = null;
	private String password = null;
    

    Map<String, String> credentials = new HashMap<>();


	HttpClient client = null;
	
	//List of PatentID and AssetURL to download XML file
	Map<String, String> patentIds = new HashMap<>();
	
    public QueryVtwMetadataApi(){
        //client = getHttpClient();
    	logger = Logger.getLogger(QueryVtwMetadataApi.class);
    	logger.setLevel(Level.ERROR);
    	
    }

 
    public void retrieveCredentials(String secretARN) {
    	AmazonRetrieveSecretManagerSecrets secretManagerObj = new AmazonRetrieveSecretManagerSecrets();
		
		secretManagerObj.getSecret(secretARN);
		setCredentials(secretManagerObj.getCredentials());

		userName = credentials.get("username");
		password = credentials.get("password");

		client = getHttpClient();
    }
    public ObjectNode retrieveExistingMetadata(String type, String identifier) throws Exception {
        ObjectNode existingMetadata = null;
       

        System.out.println("Patent Identifier: " + identifier);
        String url = PROD_METADATA_URL + type + "/" + identifier + "?mode=full";
        HttpResponse response = sendHttpRequest(url);

        if (response.getStatusLine().getStatusCode() == 200) {
            existingMetadata = (ObjectNode) mapperJson.readTree(response.getEntity().getContent());
        }
        else
        	logger.error("Metadata response code: " + response.getStatusLine().getStatusCode());
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
    	int generation;
    	String assetUrl;
    	String patentId = "";
    	
    	try
    	{
    		if(metadataNode != null)
        	{
        		//JSON Node to Pretty String
        		//String prettyString = mapperJson.writerWithDefaultPrettyPrinter().writeValueAsString(metadataNode);
        		//System.out.println("Metadata: \\n" + prettyString);
        		
        		
        		if(metadataNode.isObject())
        		{
        			patentId = metadataNode.findValue("ecm:identifier").textValue().trim();
        			patentId = patentId.substring(patentId.indexOf(":") +1, patentId.length());

        			Iterator<String> fieldNamesItr = metadataNode.fieldNames();
        			while(fieldNamesItr.hasNext())
        			{
        				String key = fieldNamesItr.next();
        				
        					
        				
        				if(key.equalsIgnoreCase("bam:hasGeneration"))
        				{
        					ArrayNode arrNode = (ArrayNode)metadataNode.get(key);
        					for(int i=0; i< arrNode.size(); i++)
        					{
        						JsonNode arr = arrNode.get(i);
            					
            					List<JsonNode> bam_gen = arr.findValues("bam:generation");
            	        		List<JsonNode> bam_asset = arr.findValues("bam:hasAsset");
            	        		
            	        		generation = Integer.parseInt(bam_gen.get(0).asText());
            	        		ArrayNode assetArrNode = (ArrayNode)bam_asset.get(0);
            	        		generations.put(generation, assetArrNode.get(0).textValue());
            	        		max = (generation > max? generation: max);
            	        		
        					}
        					
        					/*max = Integer.parseInt(metadataNode.get(key).toString()) > max? Integer.parseInt(metadataNode.get(key).toString()): max;
        					generations.put(Integer.parseInt(metadataNode.get(key).toString()), "");
        					*/
        				}
        				/*
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
        				*/
        			}
        		}
        		
        		/*
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
        		*/
        		
        		generation = max;
        		assetUrl = generations.get(generation);
        		
        		
        		// Add patents with Only Generation > 10 to be downloaded and process
        		if(generation > 10)
        		{
        			patentIds.put(patentId, assetUrl);
        			
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
    
    public void downloadPatents()
    {
    	DateFormat dateFormat = new SimpleDateFormat(
				"E, MM/dd/yyyy-hh:mm:ss a");
    	Date date;
		try {
			date = dateFormat.parse(dateFormat.format(new Date()));

			Long epoch = date.getTime();

			if (patentIds.size() > 0) {
				// Call existing VTW downloadPatent function
				//VTWAssetAPI vtwAssetAPI = new VTWAssetAPI(Long.toString(epoch), 2000, "thread0", credentials);
				VTWAssetAPI vtwAssetAPI = new VTWAssetAPI(Long.toString(epoch), 2000, "thread0");
				vtwAssetAPI.downloadPatent(patentIds, vtwAssetAPI.getInstance(), Long.toString(epoch), "thread0",
						"forward");
			}

		} catch (Exception e) {
			logger.error("Exception at downloading VTWAsset API, calling from class QueryVtwMetadata");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		
    }
    
	public void setCredentials(Map<String, String> credentials) {
		this.credentials = credentials;
	}

	public Map<String, String> getCredentials() {
		return credentials;
	}

   
}
