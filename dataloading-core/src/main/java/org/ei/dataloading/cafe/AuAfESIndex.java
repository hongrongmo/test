package org.ei.dataloading.cafe;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;


import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.BulkResult.BulkResultItem;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.gson.GsonBuilder;

import com.amazonaws.AmazonWebServiceClient;


/**
 * 
 * @author TELEBH
 * @Date: 07/20/2016
 * @Description: Upload Extracted ElasticSearch Author/Institution Profiles to S3 bucket for index in Elastic Search
 * Each S3 Put/delete operation will trigger Lambda Function index/delete document 
 */
public class AuAfESIndex {

	private static AmazonS3 s3Client;
	private static JestClient jclient;


	private static String s3BucketName = "evcafe";
	private static String key = "";

	private byte[] bytes;
	ByteArrayInputStream contentsAsStream = null;
	ObjectMetadata md;

	JestClientFactory factory;
	PrintWriter out;

	Builder build = null;
	int currRec = 1;
	Bulk bulk = null;


	public AuAfESIndex(String doc_type){


		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateFormat = "yyyy-MM-dd HH:mm:ss";


		try {

			// Construct a new Jest Client via factory
			factory = new JestClientFactory();
			/*factory.setHttpClientConfig(new HttpClientConfig
					.Builder("http://search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com:80")
			.defaultCredentials("f.huang@elsevier.com", "PWD")
			.connTimeout(10000)
			.multiThreaded(true)
			.build()
					);*/

			factory.setHttpClientConfig(new HttpClientConfig
					.Builder("search-evcafe-prod-h7xqbezrvqkb5ult6o4sn6nsae.us-east-1.es.amazonaws.com:80")
			.defaultCredentials("f.huang@elsevier.com", "PWD")
			.connTimeout(10000)
			.multiThreaded(true)
			.build()
					);
			
			System.out.println("doc type: " + doc_type);

			if(doc_type.equalsIgnoreCase("ipr"))
			{
				build = new Bulk.Builder().defaultIndex("cafe").defaultType("affiliation");
			}
			else if(doc_type.equalsIgnoreCase("apr"))
			{
				build = new Bulk.Builder().defaultIndex("cafe").defaultType("author");
			}


		} 
		catch (Exception e) {

			e.printStackTrace();
		}
	}

	//public static void UploadFileToS3(String esdir_name, String bucket_name)
	public void UploadFileToS3(String doc_type, String bucket_name, String key, String profile_content)
	{
		TransferManager tm = null;

		if(doc_type !=null)
		{
			if(doc_type.contains("ipr"))
			{
				key = "affiliation/" + key;
			}
			else if (doc_type.contains("apr"))
			{
				key = "author/" + key;
			}
			try
			{
				bytes = profile_content.getBytes();
				contentsAsStream = new ByteArrayInputStream(bytes);

				md = new ObjectMetadata();
				md.setContentType("text/json");
				md.setContentLength(bytes.length);

				// upload AU/AF extracted ES contents to S3
				PutObjectResult response = s3Client.putObject(s3BucketName, key+".json", new ByteArrayInputStream(profile_content.getBytes()), md);

				System.out.println("Key: " + key + " successfully uploaded to S3, Etag: " + response.getETag());

				/*	// upload ES dir to S3

				tm = new TransferManager(s3Client);
				MultipleFileUpload uploadFile = tm.uploadDirectory(s3BucketName, key, new File(esdir_name), true);

				// check transfer's status to check its progress
				if(uploadFile.isDone() ==false)
				{
					System.out.println("Transfer: " + uploadFile.getDescription());
					System.out.println(" - State: " + uploadFile.getState());
					System.out.println(" - Progress: " + uploadFile.getProgress().getBytesTransferred());
				}

				// block the current thread and wait for the transfer to complete. if transfer fails; this method will throw 
				//AmazonClientException or AmazonServiceException 
				uploadFile.waitForCompletion();*/

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
				System.out.println("HTTP Status Code: " + ace.getCause());
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			finally
			{
				if(tm !=null)
				{
					//after download is complete, call shutdownNow to release resources
					tm.shutdownNow();  
				}

			}
		}
	}




	public static void DeleteFilesFromS3(LinkedHashSet<String> documentIds, String bucket_name)
	{
		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		int recsPerDeleteRequest = 1000;
		int curRecNum = 1;

		if(documentIds !=null && documentIds.size() >0)
		{
			try
			{

				// connect to AmazonS3
				DeleteObjectsResult delObjResult;

				DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucket_name);

				for(String key: documentIds)
				{
					if(curRecNum >= recsPerDeleteRequest)
					{
						multiObjectDeleteRequest = new DeleteObjectsRequest(bucket_name);
						multiObjectDeleteRequest.setKeys(keys);

						// Delete from S3 which will trigger Lambda Function to delete from ES

						delObjResult = s3Client.deleteObjects(multiObjectDeleteRequest);
						System.out.format("Successfully deleted all the %s items.\n", delObjResult.getDeletedObjects().size());

						keys = new ArrayList<KeyVersion>();
						curRecNum = 0;

					}
					keys.add(new KeyVersion(key));
					curRecNum++;
				}
				multiObjectDeleteRequest.setKeys(keys);

				// Delete from S3 which will trigger Lambda Function to delete from ES

				delObjResult = s3Client.deleteObjects(multiObjectDeleteRequest);
				System.out.format("Successfully deleted all the %s items.\n", delObjResult.getDeletedObjects().size());


			} 
			catch(MultiObjectDeleteException  ex)
			{
				DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucket_name);
				System.out.format("%s \n", ex.getMessage());
				System.out.format("No. of objects successfully deleted = %s\n", ex.getDeletedObjects().size());
				System.out.format("No. of objects failed to delete = %s\n",  ex.getErrors().size());
				System.out.format("Printing error data........\n");
				for(DeleteError delete_error : ex.getErrors())
				{
					System.out.format("Object Key: %s\t%s\t%s\t%s\n" , delete_error.getKey() , delete_error.getCode() , delete_error.getMessage() , delete_error.getVersionId());
					keys.add(new KeyVersion(delete_error.getKey()));	
				}

				s3Client.deleteObjects(multiObjectDeleteRequest);

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
				System.out.println("HTTP Status Code: " + ace.getCause());
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	}


	public void EsDocumentIndex(String doc_type, String doc_id, String profile_content, String esdirName) throws IOException
	{
		Map<String,Object> docSource = new HashMap<String, Object>();
		docSource.put(profile_content, new Date());

		jclient = (JestHttpClient) factory.getObject();

		Index index = new Index.Builder(profile_content).index("cafe").type(doc_type).id(doc_id).build();
		try {
			DocumentResult result = jclient.execute(index);

			if(result.isSucceeded())
			{
				System.out.println("Document: " +  result.getValue("_id") + " was successfully Indexed in ES, Response Code:  " +  result.getResponseCode());
				System.out.println(result.getJsonString());					
			}
			else
			{
				System.out.println("Document: " + doc_id + "  Not indexed in ES");
				System.out.println(result.getErrorMessage());
				System.out.println("Error Message: " + result.getJsonObject());

				out = new PrintWriter(new FileWriter(esdirName + "/" + doc_id + ".json"));
				out.write(profile_content);
			}
		} 
		catch (IOException e) 
		{

			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			out = new PrintWriter(new FileWriter(esdirName + "/" + doc_id + ".json"));
			out.write(profile_content);
			System.out.println("Wrote bad Json Profile to: " + esdirName + "/" + doc_id + ".json");
		}
		finally
		{
			if(jclient !=null)
			{
				jclient.shutdownClient();
			}

			if(out !=null)
			{
				try
				{
					out.flush();
					out.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}


		}

	}


	//Jest does not map \n or \t to newline or tab, but just mapped as it is a character '\n', '\t'
	public void EsBulkIndex(String doc_type, String doc_id, String profile_content, String esdirName) throws IOException
	{

		Map<String, Object> source = new HashMap<String, Object>();
		source.put(profile_content, (Date)new Date());

		jclient = (JestHttpClient) factory.getObject();

		build.addAction(new Index.Builder(source).id(doc_id).build());
		//build.addAction(new Index.Builder(profile_content).id(doc_id).build());

		if(currRec>=2)
		{
			bulk = build.build();
			BulkResult rs = jclient.execute(bulk);
			if(rs.isSucceeded())
			{
				System.out.println("all " + currRec + "have been completed");
				System.out.println(rs.getJsonString());
			}
			else
			{
				List <BulkResultItem>items = rs.getFailedItems();
				for(BulkResultItem item: items)
				{
					System.out.println(item.id + " error: " + item.error + "not indexed in ES");
				}
			}
			build = new Bulk.Builder().defaultIndex("cafe").defaultType("affiliation");
			source = new HashMap<String, Object>();
			currRec = 0;
		}

		currRec++;
	}


	public boolean EsBulkDelete(List<String>docIds) throws IOException
	{	
		

		boolean status = false;
		
		try
		{
			jclient = (JestHttpClient) factory.getObject();
			for(int i=0;i<docIds.size();i++)
			{
				build.addAction(new Delete.Builder(docIds.get(i)).build());
			}

			bulk = build.build();
			BulkResult rs = jclient.execute(bulk);
			if(rs.isSucceeded())
			{
				System.out.println("all " + docIds.size() + " documents have been removed from ES");
				System.out.println(rs.getJsonString());
				status = true;
			}
			else
			{
				List <BulkResultItem>items = rs.getFailedItems();
				for(BulkResultItem item: items)
				{
					System.out.println(item.id + " error: " + item.error + "not indexed in ES");
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error Occurred during Doc Deletion from ES: " + e.getMessage());
			e.printStackTrace();
		}
		
		return status;
	}


	public void end()
	{
		if(jclient !=null)
		{
			jclient.shutdownClient();
		}

	}
	
	public void AwsSdkES()
	{
		
	}


}

