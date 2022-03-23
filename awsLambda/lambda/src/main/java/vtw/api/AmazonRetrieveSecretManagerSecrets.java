package vtw.api;

import java.util.Map;
import java.util.HashMap;

import java.util.Base64;

import org.apache.log4j.Logger;
import org.ei.dataloading.aws.secretmanager.AmazonSecretManager;

import com.amazonaws.services.applicationdiscovery.model.InvalidParameterException;
import com.amazonaws.services.applicationdiscovery.model.ResourceNotFoundException;
import com.amazonaws.services.cloudhsm.model.InvalidRequestException;
import com.amazonaws.services.datapipeline.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 
 * @author TELEBH
 * @Date: 03/15/2022
 * @Description: Same as dataloading-core "RetrieveSecretManagerSecrets.java" but slightly different, as this code what was originated in AWS console when created the secrets
 * 
 * This secrets are for VTW prod messages download from VTW Queue which also used for querying VTW API to download patent updates
 * 
 * 
 */
public class AmazonRetrieveSecretManagerSecrets {
	
	Logger logger;
	AWSSecretsManager client = null;
	
	Map<String, String> credentials = new HashMap<>();
	

	public AmazonRetrieveSecretManagerSecrets()
	{
		logger = Logger.getLogger(AmazonRetrieveSecretManagerSecrets.class);
		init();
	}
	public void init()
	{
		//initiate AwsSecretManager client to be created only once and be used from diff classes
		 // Create aand get Secrets Manager client
  		client = AmazonSecretManager.getInstance().getAmazonSecretManagerClient();
  		
    
	}
	// Use this code snippet in your app.
	// If you need more information about configurations or implementing the sample code, visit the AWS docs:
	// https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-samples.html#prerequisites

	public void getSecret(String secret_name) {

	    //String secretName = "arn:aws:secretsmanager:us-east-1:230521890328:secret:Prod/VTW/Credentials-w5kHZN";
		String secretName = secret_name;
	   
	   
	    // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
	    // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
	    // We rethrow the exception by default.
	    
	    String secret = null, decodedBinarySecret = null;
	    GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
	                    .withSecretId(secretName);
	    GetSecretValueResult getSecretValueResult = null;

	    try {
	        getSecretValueResult = client.getSecretValue(getSecretValueRequest);
	    } catch (DecryptionFailureException e) {
	        // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    } catch (InternalServiceErrorException e) {
	        // An error occurred on the server side.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    } catch (InvalidParameterException e) {
	        // You provided an invalid value for a parameter.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    } catch (InvalidRequestException e) {
	        // You provided a parameter value that is not valid for the current state of the resource.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    } catch (ResourceNotFoundException e) {
	        // We can't find the resource that you asked for.
	        // Deal with the exception here, and/or rethrow at your discretion.
	        throw e;
	    }

	    // Decrypts secret using the associated KMS key.
	    // Depending on whether the secret is a string or binary, one of these fields will be populated.
	    if (getSecretValueResult.getSecretString() != null) {
	        secret = getSecretValueResult.getSecretString();
	    }
	    else {
	        decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
	    }

	    // Your code goes here.
	    if(!(secret == null))
	    	parseSecrets(secret);
	    else if(!(decodedBinarySecret == null))
	    	parseSecrets(secret);
	}
	

	private void parseSecrets(String secret)
	{
		JsonNode secretsJson = null;
		ObjectMapper objectMapper = new ObjectMapper();
		if(secret != null && ! secret.isEmpty())
		{
			try
			{
				secretsJson = objectMapper.readTree(secret);
				credentials.put("username", secretsJson.get("username").textValue());
				credentials.put("password", secretsJson.get("password").textValue());
			}
			catch(Exception e)
			{
				logger.error("Faled to pars JSON Secrets!!!!");
				logger.error(e.getCause());
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			//System.out.println("Credentials: " + credentials.get("userName") + ", " + credentials.get("password"));   // only for debugging
		}
	}
	public Logger getLogger() {
		return logger;
	}

	public Map<String, String> getCredentials() {
		return credentials;
	}
	
	
}
