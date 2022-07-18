package awslambda;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;

/**
 * 
 * @author TELEBH
 * @Date: 06/28/2022
 * @Description: This is a singleton class assumes Data Platform (DP) AWS IAM role to get Dynamic AWS credentials (that refresh when Session expires)
 * to download Patent files (US, EP, and WO) from DP S3 bucket.
 * Create AWSS3Client once and use it for all later connections
 */
public class AmazonS3ServiceDp {
	
	private static AmazonS3ServiceDp instance;
	private AmazonS3 s3Client;
	
	private AmazonS3ServiceDp()
	{}
	
	public static AmazonS3ServiceDp getInstance()
	{
		synchronized (AmazonS3Service.class){
		if(instance == null)
		{
			instance = new AmazonS3ServiceDp();
			
			String roleArn = "arn:aws:iam::831790613400:role/dp-patent-access-engineering-village";
			try
			{
				//STS client to obtain temp security credentials 
				AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
						.withCredentials(new ProfileCredentialsProvider())
						.withRegion(Regions.US_EAST_2)
						.build();
				
				
				
				//Create/retain Dynamic session credentials, will refresh the credentials when they expire. 
				STSAssumeRoleSessionCredentialsProvider sessionCredentials = new STSAssumeRoleSessionCredentialsProvider.Builder(roleArn, "dp_session")
						.withStsClient(stsClient).build();
				instance.s3Client = AmazonS3ClientBuilder.standard().withCredentials(sessionCredentials)
						.withRegion(Regions.US_EAST_2)
						.build();
				
				
			}
			catch(AmazonServiceException e)
			{
				System.out.println("Error Occurred consuming IAM role!!");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			catch(SdkClientException e)
			{
				System.out.println(e.getMessage());
				e.getStackTrace();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
		
		return instance;
	
}
	 public AmazonS3 getAmazonS3Client() {
			return this.s3Client;
		    }
}
