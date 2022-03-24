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
import org.apache.log4j.Logger;
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
		//try {
			//encodedUserName = URLEncoder.encode(credentials.get("userName"), StandardCharsets.UTF_8.toString());
			//encodedPassword = URLEncoder.encode(credentials.get("password"), StandardCharsets.UTF_8.toString());
			
			encodedUserName = credentials.get("userName");
			encodedPassword = credentials.get("password");
			
			auth = encodedUserName + ":" + encodedPassword;
			
			System.out.println("Encoded VTW API UserName: " + encodedUserName);
			System.out.println("Encoded VTW API Password: " + encodedPassword);
			
		//} 
		/*catch (UnsupportedEncodingException e) 
		{
			System.out.println("Exception at encoding VTW API credentials");
			e.printStackTrace();
		}*/
		
		
	}
	public void SubmiteAPIGetRequest(String patentId)
	{
		String query = VTW_API_HOST + "?query=identifier:pat:" + "EP3933456A1";
		
		
		try
		{
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
			String authHeader = "Basic " + new String(encodedAuth);
			
			
			
			url = new URL(query);
			HttpURLConnection httpCon = (HttpURLConnection)url.openConnection();
			httpCon.setRequestMethod("GET");
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setRequestProperty("X-ELS-VTW-Version", "3.0");
			 
			httpCon.setRequestProperty("Authorization", authHeader);
			//httpCon.setRequestProperty("Password", encodedPassword);
			int responseCode = httpCon.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				int length = -1;
				FileOutputStream out = new FileOutputStream(new File("EP3933456A1.xml"));
				InputStream inStream = httpCon.getInputStream();
				byte[] buffer = new byte[1024];
				while((length = inStream.read(buffer)) > -1)
				{
					out.write(buffer,0,length);
				}
				
				out.close();
				inStream.close();
				System.out.println("Downloaded the pdf file");
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
