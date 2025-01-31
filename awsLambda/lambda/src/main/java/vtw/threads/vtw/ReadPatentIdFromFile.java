package vtw.threads.vtw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 
 * @author TELEBH
 * @Date: 10/14/2016
 *  @Description: if we ever need to re-download Patents from SQS Archive text file (i.e. re-download files that failed to download when got from SQS message, 
 *  for other reason, for example when 1st downloaded files with Byte Order Marker, and our process failed to convert, we had to modify download program
 *  taking off these BOM, then we need to download these files again, since the message were deleted from the QUEUE, so to get these Patents IDS again to re-download
 *  we can get them form archive file, in this case we use this program to do so
 *  
 *   this class is to read SQS archive text file, get the PatentID, keep it in a list, pass it to VTWSearchAPI for download 
 *   
 */
public class ReadPatentIdFromFile {

	public static final char FIELDDELIM = '\t';

	// get the list of Patent-Ids; with their signedAssetURL if any;  to download 
	private static Map<String,String> patentIds = new LinkedHashMap<String,String>();
	private static int recsPerSingleConnection = 1000;

	
	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();
	

	public ReadPatentIdFromFile()
	{

	}
	public static void main(String[] args) 
	{
		String fileName = null;
		int loadNumber = 0;
		int recsPerZipFile = 20000;

		if(args[0] !=null)
		{
			fileName = args[0];

			System.out.println("read PatentIDs from file: " +  fileName);
		}

		if(args.length >3)
		{
			if(args[1] !=null)
			{
				if(Pattern.matches("^\\d*$", args[1]))
				{
					loadNumber = Integer.parseInt(args[1]);
				}
				else
				{
					System.out.println("loadNumber has wrong format");
					System.exit(1);
				}
			}
			if(args[2] !=null)
			{
				recsPerZipFile = Integer.parseInt(args[2]);
				System.out.println("Number of Keys per ZipFile: " + recsPerZipFile);
			}
			if(args[3] !=null)
			{
				recsPerSingleConnection = Integer.parseInt(args[3]);
				System.out.println("Number of keys per one HttpConnection: " + recsPerSingleConnection);
			}
		}

		else
		{
			System.out.println("not enough parameters!");
			System.exit(1);
		}

		
		midTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
		System.out.println("Time before reading archive file "+(endTime-startTime)/1000.0+" seconds");
		System.out.println("total Time used "+(endTime-startTime)/1000.0+" seconds");
		
		
		ReadPatentIdFromFile obj = new ReadPatentIdFromFile();
		obj.readFile(fileName);
		
		
		midTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
		System.out.println("Time after reading archive file "+(endTime-startTime)/1000.0+" seconds");
		System.out.println("total Time used "+(endTime-startTime)/1000.0+" seconds");
		
		

		// get PatentID's metadata

		if(!(patentIds.isEmpty()))
		{
			try 
			{

				//Zip downloaded files (each in it's corresponding dir)
				ArchiveVTWPatentRefeed archivePatent= new ArchiveVTWPatentRefeed();
				archivePatent.readZipFileNameFromFile(loadNumber);

				
				// to make each instance running of this class does not conflict with each other (not using same downloads dir,so can be deleted once zip process is complete)
				DateFormat dateFormat = new SimpleDateFormat("E, MM/dd/yyyy-hh:mm:ss a");
				Date date = dateFormat.parse(dateFormat.format(new Date()));
				long epoch = date.getTime();
				
				
				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time before downloading files "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
				
				
				VTWSearchAPI vtwSearchAPI = new VTWSearchAPI(Long.toString(epoch), recsPerSingleConnection);
				vtwSearchAPI.downloadPatentMetadata(patentIds);

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time after downloading files "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
				
				

				archivePatent.zipDownloads(loadNumber, Long.toString(epoch));
				
				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time after zip downloaded files "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
				

			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

	private void readFile(String filename)
	{
		String patentID = null;


		if(filename !=null)
		{
			try 
			{
				BufferedReader br = new BufferedReader(new FileReader(filename));
				for(String line; (line = br.readLine()) !=null; )
				{
					patentID = line.substring(0, line.indexOf(FIELDDELIM));
					if(patentID !=null &&
							(patentID.substring(0, 2).equalsIgnoreCase("US") ||	patentID.substring(0, 2).equalsIgnoreCase("EP")
									|| patentID.substring(0, 2).equalsIgnoreCase("WO")))
					{
						patentIds.put(patentID, "");
					}

				}

			} 
			catch (IOException e) {
				System.out.println("What is Wrong with the file!");
				e.printStackTrace();
				e.printStackTrace();
			}

		}
	}

}
