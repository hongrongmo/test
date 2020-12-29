package org.ei.dataloading.aws.secretmanager;

import org.apache.log4j.Logger;

import com.amazonaws.services.applicationdiscovery.model.InvalidParameterException;
import com.amazonaws.services.applicationdiscovery.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;

/**
 * 
 * @author TELEBH
 * @Date: 12/28/2020
 * @Description: Retrieve AWS SecretManager secrets for Patent PDF URL credentials
 */
public class RetrieveSecretManagerSecrets {
	
	Logger logger;
	
	public RetrieveSecretManagerSecrets()
	{
		logger = Logger.getLogger(RetrieveSecretManagerSecrets.class);
	}
	public void getSecret()
	{
		String secretName = "patent/pdf/prod/credentials";
		String secret;
		
		//get AWS SecretManager client
		//AWSSecretsManager client = AmazonSecretManager.getInstance().getAmazonSecretManagerClient();
		
		AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
		AWSSecretsManager client = clientBuilder.build(); 
		
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
				System.out.println("Secret Value: " + secret);		// Plaintext key & value
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
	}

	public static void main(String[] args)
	{
		RetrieveSecretManagerSecrets obj = new RetrieveSecretManagerSecrets();
		
		obj.getSecret();
	}
}
