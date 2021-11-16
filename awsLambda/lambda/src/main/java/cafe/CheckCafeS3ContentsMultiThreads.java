package cafe;

/**
 * Author: Telebh
 * DATE: 08/24/2018
 * Description: this class is in Prod use in Cafe dataloading workspace a while ago, 
 * i just copied it over to awsLambda workspace to test check cafe ANI content in S3 bucket
 * on the "FLY" for checking files contains grant funding/openAccess with Multithreading 
 * as Girish suggested during meeting in NYC office 08/22/2018
 * 
 * filtering criteria provided by Hongrong as per today Tuesday 08/28/2018, week#: 201836]
 * 
 * If the grant funding information is from meta data, we should check element “(<xocs:funding>)”. 
 * If the grant funding information is from old way, we should check element “<grantlist”.

 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;

import awslambda.AmazonS3Service;





import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;


public class CheckCafeS3ContentsMultiThreads{

	static String bucketName = "";
	String key = "";
	boolean cafe = true;
	String action = "normal";
	String msgAction="";  // Action in SQS' SNS Metadata
	long msgEpoch;
	String s3FileLoc = "";  //in format of Bucket/Key for later tracing loaded files

	//HH 04/28/2016 id_start and id_end for out filename
	int id_start = 0;
	int id_end = 0;

	//HH 05/02/2016 to download Cafe Keys from S3 bucket to filesystem
	File s3Dir = null;
	File file = null;
	//PrintWriter out = null;  // 06/01/2017 temp comment out in multithreading env, return it back when no multithreading


	static BufferedReader reader = null; 
	BufferedReader S3Filecontents = null;


	String singleLine = null;
	String itemInfoStart = "";
	String itemInfo = "";
	String cpxIDInfo = "";
	String accessNumber = null;
	String epoch = "";
	String eid = "";

	byte[] bytes= null;


	AmazonS3 s3Client = null;

	int loadNumber = 0;
	String database="cpx";
	Connection con = null;
	String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";  // for localhost 
	String driver = "oracle.jdbc.driver.OracleDriver";
	String username = "ap_correction1";
	String password = "";
	String sqlldrFileName ="";
	String filename="";




	StringBuffer strb = new StringBuffer();   //04/29/2016 to combine multiple key's content before converting

	Runtime rt;

	static int numOfThreads;
	static String doc_type;
	static String keysFileName;

	List<String> keysList;


	PrintWriter fund,oa;

	public static void main(String[] args)
	{
		if(args.length >3)
		{
			if(args[0] !=null)
				numOfThreads = Integer.parseInt(args[0]);

			if(args[1] !=null)
				doc_type = args[1];

			if(args[2] !=null)
				keysFileName = args[2];

			if(args[3] !=null)
				bucketName = args[3];
		}
		else
		{
			System.out.println("Not enough Parameters!");
			System.exit(1);
		}

		CheckCafeS3ContentsMultiThreads instance = new CheckCafeS3ContentsMultiThreads();
		instance.init();
		instance.readKeyfromFile();   // fetch keys EID into a list to loop through in multithreads 
		instance.startDownloadThreads();

	}
	private void init()
	{
		try
		{
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
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
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readKeyfromFile()
	{
		BufferedReader br = null;
		keysList = new ArrayList<String>();
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

	public void startDownloadThreads()
	{
		PrintWriter grantFunding = null, openAccess=null;
		//try
		//{
		try 
		{
			grantFunding = new PrintWriter(System.getProperty("user.dir") + "/funding.txt");
			openAccess = new PrintWriter(System.getProperty("user.dir") + "/oa.txt");
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		}


		// download files from s3 bucket using thread (s)
		if(keysList.size() >0)
		{
			double listSize = keysList.size()/numOfThreads;
			int subListSize = (int)listSize;
			int start = 0;
			int last = (subListSize -1);
			CountDownLatch latch = new CountDownLatch(numOfThreads);


			System.out.println("list size to download: " + keysList.size());

			System.out.println("STARTING................." + new Date().getTime());

			if(numOfThreads ==1)
				last = subListSize -1;

			for(int i=0;i<numOfThreads;i++)
			{

				/*CheckCafeGrantAndOA obj = new CheckCafeGrantAndOA(bucketName,s3Client);
					CheckS3FilesConetntsAndDownload thread = new CheckS3FilesConetntsAndDownload("Thread " + i,latch, start,last,obj);
				 */
				CheckS3FilesConetntsAndDownload thread = new CheckS3FilesConetntsAndDownload("Thread " + i,latch, start,last,bucketName,s3Client,
						grantFunding,openAccess);
				thread.start();

				//this ONLY if # of threads #2
				if(numOfThreads >=2)
				{
					synchronized (thread) {
						start = last + 1;
						if(i<(numOfThreads-2))
							last = start + (subListSize -1);
						else
							last = keysList.size() -1;

						System.out.println("***********************");	
					}
				}

			}

			try 
			{
				latch.await();
				System.out.println("In Main thread after completion of " + numOfThreads + " threads");
				System.out.println("FINISHED................." + new Date().getTime());
			} 
			catch (InterruptedException e) 
			{

				e.printStackTrace();
			}

			finally
			{
				try
				{
					if(grantFunding !=null)
					{
						System.out.println("Closing the Funding file");
						grantFunding.flush();
						grantFunding.close();
					}
				}

				catch(Exception e)
				{
					System.out.println("Problem closing grantFunding filewriter!!");
					e.printStackTrace();
				}

				try
				{
					if(openAccess !=null)
					{
						System.out.println("Closing the OA file");
						openAccess.flush();
						openAccess.close();
					}

				}
				catch(Exception e)
				{
					System.out.println("Problem closing openAccess filewriter!!");
					e.printStackTrace();
				}
			}
		}

	}
		class CheckS3FilesConetntsAndDownload implements Runnable
		{
			private Thread th;
			private String threadName = null;
			private CountDownLatch latch;

			int startIndex;
			int lastIndex;

			String bucketName;

			PrintWriter grantFunding, openAccess;
			String currDir, fundigDir, oaDir;

			S3Object object = null;
			InputStream objectData = null;
			AmazonS3 s3Client = null;

			public CheckS3FilesConetntsAndDownload (String name, CountDownLatch latch, int start, int last, String bucket, AmazonS3 s3Client,
					PrintWriter grantFunding, PrintWriter openAccess)
			{
				threadName = name;
				this.latch = latch;


				startIndex = start;
				lastIndex = last;

				bucketName = bucket;

				this.grantFunding = grantFunding;
				this.openAccess = openAccess;

				if(startIndex <-1 || lastIndex <-1)
				{
					System.out.println("invalid startIndex: " + startIndex + " or lastIndex: " + lastIndex + "!! exit...");
					System.exit(1);
				}

				try
				{
					String currDir = System.getProperty("user.dir");
					File file =new File (currDir);
					if(!file.exists())
						file.mkdir();
					fundigDir = currDir + "/funding";
					file =new File (fundigDir);
					if(!file.exists())
						file.mkdir();


					oaDir = currDir + "/openAccess";
					file =new File (oaDir);
					if(!file.exists())
						file.mkdir();



					this.s3Client = s3Client;

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}


			public void start()
			{
				if(th ==null)
				{
					try 
					{
						th = new Thread(this, threadName);
						th.start();
					} 
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}


			public void run() 
			{	
				String key = null;

				for(int i=startIndex;i<=lastIndex;i++)
				{
					try
					{
						key = keysList.get(i);
						object = s3Client.getObject(new GetObjectRequest (bucketName, keysList.get(i)));
						if(object !=null)
						{
							objectData = object.getObjectContent();

							String fileContents = IOUtils.toString(objectData);
							// only parse ANI S3 files contains Funding Info or OpenAccess, skip any other files

							if(fileContents.contains("<xocs:funding>") || fileContents.contains("<grantlist") ||
									fileContents.contains("<xocs:open-access>"))
							{	
								// only 1 thread can write to files
								synchronized(this)
								{
									if(fileContents.contains("<xocs:open-access>"))
									{
										openAccess.println(keysList.get(i));
										currDir = oaDir;
									}


									if(fileContents.contains("<xocs:funding>") || fileContents.contains("<grantlist"))
									{
										grantFunding.println(keysList.get(i));
										currDir = fundigDir;

									}
									saveContentToFile(fileContents,currDir,keysList.get(i));

								}

							}
							else
							{
								//only for debugging
								//System.out.println("File: " + keysList.get(i) +" does not contain grant/openaccess info threadName: " + threadName);
							}

						}
						else
						{

							System.out.println("Key: " + key + " Not exist in specified bucket: " +  bucketName);
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

						System.out.println("Key:    " + key + " , thread: " + threadName);

					}
					catch(AmazonClientException ace)
					{
						System.out.println("Caught an AmazonClientException, which " +
								"means the client encountered " +
								"an internal error while trying to " +
								"communicate with S3, " +
								"such as not being able to access the network.");
						System.out.println("Error Message: " + ace.getMessage());
						System.out.println("Key:    " + key + " , thread: " + threadName);

					} 
					catch (Exception e) 
					{
						System.out.println("Thread: " + threadName + " having issue!!!!");
						System.out.println("Error Message: " + e.getMessage());
						e.printStackTrace();
					}


					finally
					{
						if(objectData !=null)
						{
							try
							{
								objectData.close();
							}
							catch(IOException ioex)
							{
								ioex.printStackTrace();
							}
						}
					}
					
				}

				latch.countDown();

			}

			public void saveContentToFile (String objectData, String dir, String fileName) throws IOException
			{
				PrintWriter out = null;
				try
				{
					// check contents & download the file
					File file = new File(dir + "/" + fileName+".xml");
					out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(),false)));
					out.println(objectData.replaceAll("><", ">\n<"));
				}

				catch(Exception e)
				{
					System.out.println("Fialed to save contents of file" + filename);
					System.out.println("Reason: " + e.getMessage());
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if(out !=null)
						{
							out.flush();
							out.close();
						}
					}

					catch(Exception ioex)
					{
						ioex.printStackTrace();
					}
				}
			}

		}}
