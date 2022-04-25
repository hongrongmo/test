package vtw.api;

import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
/**
 * 
 * @author TELEBH
 * @Date: 03/15/2022
 * @Description: Since week of [202151] we started to notice big drop in number of patents we receive every week. The problem lasted till early March 2022.
 * Also we have noticed that many records not processed due to missing title
 * 
 * These two issues has been reported to Univentio/IPDD/VTW.. Univentio/IPDD confirmed they will deleiver both cases files as update
 * 
 * but because EV subscribed to VTW to only receive new patents (generation = 10) so we will not be able to downlod those updated
 * 
 * After discussion with VTW team (Srikanth) I have been instructed to use VTW API to download the files by searching for 2 key elements
 * 
 * a. generation (will provide most recenet generation
 * b. 1st link in links array will include the download URL
 *

Also can use VTW discopery API in order to verify the most recent generation of the record

Paramter:identifier:pat:EP3933456A1

Where patID should be in the format of (Patent Code + Patent ID number + Kind Code), but also tested if can take off kind code and add "*" so it search all different kind codes

If "generation": "10:final:public" : that means no recent updates to the record and so can ignore it still

 */



public class ConnectToVtwSearchApi {
	

	//private final String VTW_API_HOST = "https://vtw.elsevier.com/search";
	private final String VTW_API_HOST = "https://vtw.elsevier.com/search";
	private final ObjectMapper mapperJson = new ObjectMapper();

	
	private String secretArn = null;
	
	private String encodedUserName;
	private String encodedPassword;
	private URL url;
	private Logger logger;
	
	private String auth;
	
	public void init()
	{
		AmazonRetrieveSecretManagerSecrets secretManagerObj = new AmazonRetrieveSecretManagerSecrets();
		logger = secretManagerObj.getLogger();
		secretArn = "arn:aws:secretsmanager:us-east-1:230521890328:secret:Prod/VTW/Credentials-w5kHZN";
		secretManagerObj.getSecret(secretArn);
		
		
		Map<String, String> credentials = secretManagerObj.getCredentials();
		
			encodedUserName = credentials.get("username");
			encodedPassword = credentials.get("password");
			
			auth = encodedUserName + ":" + encodedPassword;
			
			
		
	}
	public void SubmiteAPIGetRequest(String patentId)
	{
		
		String url = "https://vtw.elsevier.com/search?query=identifier:pat:WO2012012496A2";
			
		//String url = VTW_API_HOST + "?query=identifier:pat:" + "WO2012012496A2";
		ObjectNode existingMetadata = null;
		HttpResponse response = null;
		HttpClient client = null;
		
		int responseCode = 0;
		try
		{
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
			String authHeader = "Basic " + new String(encodedAuth);
			
			CredentialsProvider provider = new BasicCredentialsProvider();
	        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(encodedUserName, encodedPassword);

	        provider.setCredentials(AuthScope.ANY, credentials);
	        
	        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
	        
	       
	       HttpGet request = new HttpGet(url);
	        request.addHeader("accept", "application/json");
	        request.addHeader("X-ELS-VTW-Version", "3.0");
	        response = client.execute(request);

	        if (response.getStatusLine().getStatusCode() == 200) {
	        	existingMetadata  = (ObjectNode) mapperJson.readTree(response.getEntity().getContent());
	        	System.out.println(mapperJson.writerWithDefaultPrettyPrinter().writeValueAsString(existingMetadata));
	        	responseCode = response.getStatusLine().getStatusCode();
	        }
			
			
	      
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				/*
				 * int length = -1; FileOutputStream out = new FileOutputStream(new
				 * File("WO2012012496A2.xml")); InputStream inStream = request. byte[] buffer =
				 * new byte[1024]; while((length = inStream.read(buffer)) > -1) {
				 * out.write(buffer,0,length); }
				 * 
				 * out.close(); inStream.close(); System.out.println("Downloaded the pdf file");
				 */
			}
		}
		catch(Exception e)
		{
			logger.error("Excption Getting Patent PDF using URL!!!!");
			logger.error(e.getCause());
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		

	}
	
	public static void main(String[] args)
	{
		ConnectToVtwSearchApi obj = new ConnectToVtwSearchApi();
		obj.init();
		obj.SubmiteAPIGetRequest("EP3933456A1");
	}

}
