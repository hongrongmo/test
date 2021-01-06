package org.ei.dataloading.aws.secretmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.applicationdiscovery.model.InvalidParameterException;
import com.amazonaws.services.applicationdiscovery.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author TELEBH
 * @Date: 12/28/2020
 * @Description: Retrieve AWS SecretManager secrets for Patent PDF URL credentials
 */
public class RetrieveSecretManagerSecrets {
	
	Logger logger;
	Map<String,String> pdfCredentials = new LinkedHashMap<>();
	
	public RetrieveSecretManagerSecrets()
	{
		logger = Logger.getLogger(RetrieveSecretManagerSecrets.class);
	}
	public String getSecret()
	{
		String secretName = "patent/pdf/prod/credentials";
		String secret = null;
		
		//get AWS SecretManager client
		AWSSecretsManager client = AmazonSecretManager.getInstance().getAmazonSecretManagerClient();
		
		
		//get AWS SecretManager secret
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
				.withSecretId(secretName);
		
		GetSecretValueResult getSecretValueResult = null;
		
		try
		{
			getSecretValueResult = client.getSecretValue(getSecretValueRequest);
			
			//Decrypt Secret Using the associated KMS CMK
			
			if(getSecretValueResult.getSecretString() != null)
			{
				secret = getSecretValueResult.getSecretString();
//				System.out.println("Secret Value: " + secret);		// ONLY for testing Plaintext key & value
			}
		}
		catch(DecryptionFailureException ex)
		{
			logger.error("AWS SecretManager Decryption Failuer!!!!");
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		catch(InternalServerErrorException ex)
		{
			logger.error("AWS SecretManager InternalServerErrorException!!!!");
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		catch(InvalidParameterException ex)
		{
			logger.error("AWS SecretManager InvalidParameterException!!!!");
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		catch(InvalidRequestException ex)
		{
			logger.error("AWS SecretManager InvalidRequestException!!!!");
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		catch(ResourceNotFoundException ex)
		{
			logger.error("AWS SecretManager ResourceNotFoundException!!!!");
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			logger.error("AWS SecretManager RetrieveSecretManagerSecrets!!!!");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
			return secret;
		
	}
	
	public void parseSecrets(String secret)
	{
		JsonNode secretsJson = null;
		ObjectMapper objectMapper = new ObjectMapper();
		if(secret != null && ! secret.isEmpty())
		{
			try
			{
				secretsJson = objectMapper.readTree(secret);
				pdfCredentials.put("userName", secretsJson.get("patentPdfUserName").textValue());
				pdfCredentials.put("password", secretsJson.get("patentPdfPassword").textValue());
			}
			catch(Exception e)
			{
				logger.error("Faled to pars JSON Secrets!!!!");
				logger.error(e.getCause());
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public void getPatentPdf(String ac, String patentPN, String kc)
	{
		String count = "";
		try
		{
			String encodedUserName, encodedPassword;
			URL urlObj;
			
			parseSecrets(getSecret());
			if(pdfCredentials.size() > 1)
			{
				/*
				byte[] userName = Base64.getEncoder().encode(pdfCredentials.get("userName").getBytes());
				byte[] password = Base64.getEncoder().encode(pdfCredentials.get("password").getBytes());
				*/
				encodedUserName = URLEncoder.encode(pdfCredentials.get("userName"), StandardCharsets.UTF_8.toString());
				encodedPassword = URLEncoder.encode(pdfCredentials.get("password"), StandardCharsets.UTF_8.toString());
				
				
				urlObj = new URL("http://ipdatadirect.lexisnexis.com/downloadpdf.aspx?lg=" + encodedUserName + "&pw="
						+ encodedPassword + "&pdf=" + ac + "," + patentPN + "," + kc); 
				

				System.out.println(urlObj.getHost() + urlObj.getPath());

				HttpURLConnection httpCon = (HttpURLConnection)urlObj.openConnection();
				httpCon.setRequestMethod("GET");
				httpCon.setRequestProperty("Content-Type", "application/json");
				int responseCode = httpCon.getResponseCode();
				if(responseCode == HttpURLConnection.HTTP_OK)
				{
					int length = -1;
					FileOutputStream out = new FileOutputStream(new File(ac.toUpperCase() + patentPN + kc.toUpperCase() + ".pdf"));
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
		RetrieveSecretManagerSecrets obj = new RetrieveSecretManagerSecrets();
		
		//obj.getSecret();   // to get secrets and print in plaintext
		
		String ac = null,patentPN = null ,kc = null ;
		
		if(args.length >2)
		{
			if(args[0] != null && !(args[0].isEmpty()))
				ac = args[0].trim();
			if(args[1] != null && !(args[1].isEmpty()))
				patentPN = args[1].trim();
			if(args[2] != null && !(args[2].isEmpty()))
				kc = args[2].trim();
			
			obj.getPatentPdf(ac,patentPN,kc);
		}
		else
		{
			System.out.println("Not ENough parameters!!!!!");
			System.exit(1);
		}
		
		
	}
}
