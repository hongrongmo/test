package org.ei.dataloading.aws.secretmanager;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;

/**
 * 
 * @author TELEBH
 * @Date: 12/28/2020
 * @Description: AWS Secret Manager Client
 */

public class AmazonSecretManager {

	private static AmazonSecretManager instance;
	private AWSSecretsManager smClient;
	
	private AmazonSecretManager()
	{}
	
	public static AmazonSecretManager getInstance()
	{
		String region = "us-east-1";
		synchronized (AmazonSecretManager.class) {
			if (instance == null)
				instance = new AmazonSecretManager();
			
			instance.smClient = AWSSecretsManagerClientBuilder.standard()
					.withRegion(region)
					.build();
			return instance;
		}
	}
	
	public AWSSecretsManager getAmazonSecretManagerClient()
	{
		return this.smClient;
	}
}
