package org.ei.dataloading.cafe;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Vector;

import org.ei.dataloading.bd.loadtime.BaseTableDriver;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * 
 * @author TELEBH
 * Date: 04/28/2016
 * Description: chain multiple InputStreams into one continual InputStream for Cafe canocatenating multiple Key's Content before passing to converting program
 */
public class TestConcatenateMultipleReaders {

	Vector<InputStream> v;
	
	boolean cafe = true;
	
	private static Connection con = null;
	static String url="jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction1";
	static String password="ei3it";
	static String database ="cpx";
	//static String action;
	static int loadNumber=0;
	static String sqlldrFileName="cafeANIFileLoader.sh";
	static int id_start;
	static int id_end;
	

	
	
	public static void main(String[] args) {

		TestConcatenateMultipleReaders obj = new TestConcatenateMultipleReaders();
		obj.getFileFromS3Bucket();
		//obj.chainInputstreams();

	}

	public void getFileFromS3Bucket()
	{
		InputStream objectData =null;
		v = new Vector<InputStream>(3);
		String [] keys = new String[3];
		keys[0] = "0000124574";
		keys[1] = "0000000122";
		keys[2] = "0000000130";
		try
		{
			AmazonS3 s3Client = AmazonS3Service.getInstance().getAmazonS3Service();

			for(int i = 0; i<3;i++)
			{
				// works well for one single file from S3 bucket
				S3Object object = s3Client.getObject(new GetObjectRequest ("sc-ani-xml-prod", "0000124574"));

				objectData = object.getObjectContent();

				v.add(objectData);
				//objectData.close();
			}

			chainInputstreams();



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

		finally
		{
			try
			{
				if(objectData !=null)
				{
					objectData.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	public void chainInputstreams()
	{
		BufferedReader reader=null;
		try
		{
			if(v!=null && v.size() >0)
			{
				Enumeration <InputStream>e = v.elements();
				SequenceInputStream sis = new SequenceInputStream(e);
				InputStreamReader isr = new InputStreamReader(sis);
				reader = new BufferedReader(isr);

				reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(reader.readLine().replaceAll("><", ">\n<").getBytes())));
				String str="";
				/*while((str=reader.readLine()) !=null)
				{
					System.out.println(str);
				}
				*/  // only for verifying the concatenation of multiple key's contents
				
				
				 BaseTableDriver c = new BaseTableDriver(loadNumber,database,"normal","sc-ani-xml-prod");
				 con = c.getConnection(url, driver, username, password);
				 //HH 04/21/2016 similar as correction, check block ISSN/EISSN only for Add/Update
				 //HH 04/25/2016 as per Frank request, temporarirly disable ISSN/E-ISSN for Cafe Initial Refeed
				/* if(msgAction !=null && !(msgAction.equalsIgnoreCase("d")))
				 {
		             c.setBlockedIssnList(con);
				 }*/
	             c.writeBaseTableFile("0000124574",con,reader,cafe);
	             String dataFile="0000124574."+loadNumber+".out";
	             File f = new File(dataFile);
	             if(!f.exists())
	             {
	                 System.out.println("datafile: "+dataFile+" does not exists");
	                 System.exit(1);
	             }
	             
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			try
			{
				if(reader !=null)
				{
					reader.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	

}
