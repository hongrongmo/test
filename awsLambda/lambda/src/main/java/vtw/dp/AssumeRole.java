package vtw.dp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;


import awslambda.AmazonS3ServiceDp;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author TELEBH
 * @Date: 06/27/2022
 * @Description: In regard to Switching out of VTW to DP for Patent Feed, we need to Assume IAM Role DP/VTW team created for Getting Objects from DP S3 buckets
 * 
 * The ARN of the IAM role is:

arn:aws:iam::831790613400:role/dp-patent-access-engineering-village
 
This role has access to get and list from:
s3://com-elsevier-rdp-dataconfidential-prod-useast2-1/dp-patent/US/
s3://com-elsevier-rdp-dataconfidential-prod-useast2-1/dp-patent/EP/
s3://com-elsevier-rdp-dataconfidential-prod-useast2-1/dp-patent/WO/

 */
public class AssumeRole {

	Log log = LogFactory.getLog(AssumeRole.class);
	AmazonS3 s3Client = null;
	 private  final int MAX_RETRY = 3;
	 private  final int MAX_DELETE = 1000;
	 private  final int WAIT_TIME = 2000;
	 private  final String S3_PREFIX = "s3://";
	    
	
	public static void main(String[] args)
	{
		AssumeRole obj = new AssumeRole();
		obj.run();
	}
	
	public void run()
	{
		
		try
		{
			/**** This way works well, but it will retain a static session that will never refresh, we will need dynamis session that referesh each time session ends 
			 * so it does not cause interruption to S3 access/connectivity
			 *******/
			/**
			//obtain credentials by assuming IAM role
			AssumeRoleRequest request = new AssumeRoleRequest()
					.withRoleArn(roleArn)
					.withRoleSessionName("dp_session");
			
			//assume role and get credentilas, expiring credentials are automatically refreshed via a background thread
			AssumeRoleResult response = stsClient.assumeRole(request);
			Credentials sessionCredentials = response.getCredentials();
			
			// How long the session last, norally session last 1 hr
			System.out.println("Session Expiration date: " + sessionCredentials.getExpiration());
			
			
			//Create session Object with credentials just obtained
			BasicSessionCredentials credentials = new BasicSessionCredentials(sessionCredentials.getAccessKeyId(),
					sessionCredentials.getSecretAccessKey(), sessionCredentials.getSessionToken());
			
			
			
			//Use temp credentials to create Amazon S3 Client, in order to run s3 calls,i.e. getObject
			s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
					.withRegion(Regions.US_EAST_2)
					.build();
			
			**/
			
			//Get S3Client with Dynamic session credentials for the assumed role, will refresh the credentials when they expire. 
			
			s3Client = AmazonS3ServiceDp.getInstance().getAmazonS3Client();
			
			
			
			//Verify assuming role by listing objects in S3 bucket
			ObjectListing objects = s3Client.listObjects("com-elsevier-rdp-dataconfidential-prod-useast2-1", "dp-patent/US/");
			System.out.println("No of Objects in Bucket: " + objects.getObjectSummaries().size());
			
			
			//Store Object Names in List for later download
			Set<AmazonS3URI> s3URIList = new HashSet<>();
			objects.getObjectSummaries().forEach(s3objSummary -> s3URIList.add(new AmazonS3URI(S3_PREFIX + s3objSummary.getBucketName() + "/" + s3objSummary.getKey())));
			getS3Objects(s3URIList);
			
		}
		catch(AmazonServiceException e)
		{
			log.error("Error Occurred consuming IAM role!!");
			//log.error(e);
			e.printStackTrace();
		}
		catch(SdkClientException e)
		{
			log.error(e);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


	}
	
	//Get S3 Object using BucketName and Key
	public void getS3Objects(Set<AmazonS3URI> s3URIList)
	{
		Set<AmazonS3URI> s3XmlURIList = s3URIList.stream().filter(s3Obj -> s3Obj.getKey().endsWith(".xml")).collect(Collectors.toSet());
		s3XmlURIList.forEach(s3Obj -> { 
			try {
				getS3Object(s3Obj.getBucket(), s3Obj.getKey());
			} catch (InterruptedException | IOException e) {
				System.out.print("Failed to getS3Object: " + s3Obj.getKey());
				e.printStackTrace();
			}
		});
	}
	
	//get S3Objects
	
	public void getS3Object(String bucketName, String key) throws InterruptedException, IOException {
        boolean success = false;
        int tries = 0;
        String output = "";
        SdkClientException excep = null;
        while(!success && tries < MAX_RETRY) {
            try (S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key))) {
                output = IOUtils.toString(object.getObjectContent(), StandardCharsets.UTF_8);
                //write key/patent ANI contents to a file of the same name
                writeS3KeyContents(output, key.substring(key.lastIndexOf("/")+1));
                success = true;
            } catch (SdkClientException e) {
                if (e instanceof AmazonServiceException) {
                    log.error("There was a problem when getting object from s3: " + e);
                    tries++;
                    log.info("Retry...");
                    try {
                        Thread.sleep(tries * WAIT_TIME);
                    } catch (InterruptedException ie) {
                        log.error("Encountered an error during sleeping when getting objects from s3: " + ie);
                        throw ie;
                    }
                    excep = e;
                } else {
                    log.error("The exception has been thrown when retrieving " + key + " key:\n" + e);
                    throw e;
                }
            }
        }
        if(!success) {
            throw new SdkClientException("Error occurred when getting " + key + " key:\n" + excep);
		} /*
			 * else { return output; }
			 */
    }
	
	/**
	 * 
	 */
	
	public void writeS3KeyContents(String s3KeyXmlContents, String key)
	
	{
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(key))))
		{
			bw.write(s3KeyXmlContents);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
