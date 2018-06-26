package org.ei.dataloading.cafe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.ei.dataloading.awss3.AmazonS3Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;


import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;


public class DownloadloadFileFromS3 {



	static String bucketName = "datafabrication-reports";
	static String key = "2015/reports.css";
	static String uploadFileName = "reports.css";

	static String keysFileName = null;

	static List<String> keysList = new ArrayList<String>();

	static AmazonS3 s3Client = null;
	static String currDir;

	public DownloadloadFileFromS3()
	{

	}

	public DownloadloadFileFromS3(String s3BucketName, String key)
	{
		this.bucketName = s3BucketName;
		this.key = key;
	}

	public static void init()
	{
		try
		{
			currDir = System.getProperty("user.dir");
			File file =new File (currDir);
			if(!file.exists())
				file.mkdir();
			currDir = currDir + "/cafe_ani_files";
			file =new File (currDir);
			if(!file.exists())
				file.mkdir();
			
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
		}
		catch(AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	// Added 07/13/2017 "init()" to download Cafe ANI files/records that contains "SecondaryPUI" for Hongrong to capture this tag and load to new column in BD "SecondaryPUI"
	public static void getKey()
	{
		InputStream s3objectData = null;
		S3Object s3object = null;


		for(String key: keysList)
		{
			try
			{
				s3object = s3Client.getObject(new GetObjectRequest (bucketName, key));
				s3objectData = s3object.getObjectContent();
				//parseS3File(objectData);  // for single file testing

				if(s3objectData !=null)
				{
					saveContentToFile(s3objectData, key);
				}
			}

			catch(IOException ex)
			{
				ex.printStackTrace();
			}

			catch(AmazonServiceException ase)
			{
				System.out.println("Caught an AmazonServiceException, which " +"means your request made it " +
						"to Amazon S3, but was rejected with an error response" +
						" for some reason.");
				System.out.println("Error Message:    " + ase.getMessage());
				System.out.println("HTTP Status Code: " + ase.getStatusCode());
				System.out.println("AWS Error Code:   " + ase.getErrorCode());
				System.out.println("Error Type:       " + ase.getErrorType());
				System.out.println("Request ID:       " + ase.getRequestId());
			}


			finally
			{
				try
				{
					if(s3objectData !=null)
					{
						s3objectData.close();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	public static void getFileFromS3(String s3BucketName, String fileKey) throws AmazonClientException,AmazonServiceException, InterruptedException
	{	

		try
		{
			AmazonS3 s3client = AmazonS3Service.getInstance().getAmazonS3Service();

			System.out.println("downloading CAFE FILE:"+fileKey+" from S3 bucket: "+s3BucketName);
			System.out.println("S3FilePathe: " + "s3://"+s3BucketName+"/"+fileKey);

			S3Object request = s3client.getObject(new GetObjectRequest(s3BucketName, fileKey));

			System.out.println("etag "+request.getObjectMetadata());

			// download the file
			File file = new File(fileKey+".xml");
			TransferManager tm = new TransferManager(s3client);
			Download downloadFile = tm.download(s3BucketName, fileKey, file);

			// check transfer's status to check its progress
			if(downloadFile.isDone() ==false)
			{
				System.out.println("Transfer: " + downloadFile.getDescription());
				System.out.println(" - State: " + downloadFile.getState());
				System.out.println(" - Progress: " + downloadFile.getProgress().getBytesTransferred());
			}

			// block the current thread and wait for the transfer to complete. if transfer fails; this method will throw 
			//AmazonClientException or AmazonServiceException 
			downloadFile.waitForCompletion();



			//after download is complete, call shutdownNow to release resources
			tm.shutdownNow();	
		}

		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		catch(AmazonServiceException ase)
		{
			System.out.println("Caught an AmazonServiceException, which " +"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch(AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}



	// Added 07/13/2017 to download Cafe ANI files/records that contains "SecondaryPUI" for Hongrong to capture this tag and load to new column in BD "SecondaryPUI"

	public static void saveContentToFile (InputStream objectData, String key) throws IOException
	{
		BufferedReader breader = null;
		PrintWriter out = null;
		try
		{
			breader = new BufferedReader(new InputStreamReader(objectData));
			breader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(breader.readLine().replaceAll("><", ">\n<").getBytes())));

			//File file = new File(s3Dir.getName()+"/"+key+".xml");
			//File file = new File("/ebs_scratch/Hanan_AUAF/EVCAFE_SQS/cafe_pui_wzSecondaryPUI/"+key+".xml");

			

			File file = new File(currDir + "/" + key + ".xml");

			if (!file.exists()) 
			{
				System.out.println("Downloaded: "+file.getName());

			}
			else
			{
				System.out.println("file:" +  file.getName() + "already exist");
			}

			String line = null;
			out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(),true)));
			while ((line = breader.readLine()) !=null)
			{
				out.println(line);

			}

		}
		catch (IOException e) {

			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			try
			{
				if(breader !=null)
				{
					breader.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}


			try
			{
				if(out !=null)
				{
					out.flush();
					out.close();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

	}



	public static void readKeyfromFile()
	{
		BufferedReader br = null;
		try 
		{
			br = new BufferedReader(new FileReader(keysFileName));
			for(String line; (line = br.readLine()) !=null; )
			{
				keysList.add(line);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error reading Keys from S3 bucket!!!!");
			System.out.println("Reason: " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(br !=null)
				{
					br.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args)
	{

		if(args.length >1)
		{
			bucketName = args[0];
			keysFileName = args[1];

		}
		else
		{
			System.out.println("Not enough parameters!");
			System.exit(1);
		}


		readKeyfromFile();   // for downloading bulk of keys

		//keysList.add("0000124574");  // for testing individual Key

		try
		{
			init();
			getKey();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
