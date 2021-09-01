package org.ei.dataloading.bd.loadtime.nosql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ei.dataloading.aws.AmazonDynamodbService;
import org.ei.dataloading.awss3.AmazonS3Service;
import org.ei.dataloading.awss3.UploadFileToS3;
import org.ei.dataloading.awss3.UploadFileToS3Thread;
import org.ei.util.GUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * 
 * @author TELEBH
 * @Date: 01/28/2021
 * @Description: Entry class for parsing incoming BD/OpsBankII Zip/XML file
 * and for each item within Abstract file generate an individual xml file in the format of
 * m_id.xml similar as cafe but using m_id instead of EID
 * 
 * Purpose for this change is exploring the option of migrating out of Oracle to 
 * Aws S3 as document store & DynamoDB for metadata
 * 
 * Input: OpsBankII Zip/Xml file
 * Output: many M_ID.xml files where each file maps to <item></item>
 */
public class ParseInputXmlFileEntry {
	
	String bucketName = null;
	private int loadNumber;
	private String database;
	private String action;
	private String inFile;
	private PrintWriter outFile = null;
	private boolean open = false;
	
	File outDir;
	
	
	Map<String,String> dynamoDBPuiMidList = new HashMap<>();
	Map<String,String> dynamoDBAnMidList = new HashMap<>();
	
	public enum db
	{
		CPX,
		GEO,
		CHM,
		PCH,
		ELT,
		INS,
		UPT,
		GRF,
		CBN,
		NTI,
		C84,
		IBF,
		KNC,
		KNA
	}
	
	
	public ParseInputXmlFileEntry() {}
	public ParseInputXmlFileEntry(int loadNumber, String databaseName)
	{
		
		this.loadNumber = loadNumber;
		this.database = databaseName;
		this.action = "normal";
	}

	public ParseInputXmlFileEntry(int loadNumber, String databaseName, String action)
	{
		this(loadNumber, databaseName);
		this.action = action;	
		
	}
	
	// Scan DynamoDB table and cache it for all records check to retain 
	public void ScanDynamoDBTable()
	{
		AmazonDynamoDB dynamodbClient = AmazonDynamodbService.getInstance().getAmazonDynamodbClient();
		DynamoDB dynamoDB = new DynamoDB(dynamodbClient);
		Table table = dynamoDB.getTable("dl_bd_master_dev");
		ScanSpec scanSpec =  new ScanSpec().withProjectionExpression("M_ID, AccessNumber, PUI");

		try
		{
			ItemCollection<ScanOutcome> items = table.scan(scanSpec);
			Iterator<Item> itr = items.iterator();
			while(itr.hasNext())
			{
				Item item = itr.next();
				//System.out.println(item.toJSONPretty());   only for testing
				parseDynamoDBJsonResponse(item.toJSONPretty());
			}
			System.out.println("pui list size: " + dynamoDBPuiMidList.size());
			System.out.println("pui list size: " + dynamoDBAnMidList.size());
		}
		catch(Exception e)
		{
			System.err.println("Unable to scan the table:");
			e.printStackTrace();
		}
	}
	
	// Search DynamoDB table for specific AN/PUI 
		public void ScanDynamoDBTable(String id)
		{
			AmazonDynamoDB dynamodbClient = AmazonDynamodbService.getInstance().getAmazonDynamodbClient();
			DynamoDB dynamoDB = new DynamoDB(dynamodbClient);
			Table table = null;
			Index index = null;
			QuerySpec spec = null;
			
			if(database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("geo") || database.equalsIgnoreCase("pch")
					|| database.equalsIgnoreCase("chm") || database.equalsIgnoreCase("elt"))
			{
				dynamoDB.getTable("dl_bd_master_dev");
				index = table.getIndex("ACCESSNUMBER-index"); 
				spec = new QuerySpec().withKeyConditionExpression("ACCESSNUMBER  = :v_an")
						//.withNameMap(new NameMap().with("#d", "accessnumber"))
						.withValueMap(new ValueMap().withString(":v_an", "201806"));
				
			}
					
			else if(database.equalsIgnoreCase("ins"))
			{
				table = dynamoDB.getTable("dl_ins_master_dev");
				index = table.getIndex("ANUM-index"); 
				spec = new QuerySpec().withKeyConditionExpression("ANUM  = :v_an")
						//.withNameMap(new NameMap().with("#d", "accessnumber"))
						.withValueMap(new ValueMap().withString(":v_an", id));
				
			}
			
			try
			{
				ItemCollection<QueryOutcome> items = index.query(spec);
				Iterator<Item> itr = items.iterator();
				while(itr.hasNext())
				{
					Item item = itr.next();
					//System.out.println(item.toJSONPretty());   only for testing
					parseDynamoDBJsonResponse(item.toJSONPretty());
				}
				
			}
			catch(Exception e)
			{
				System.err.println("Unable to scan the table:");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	
	// Initialization
	public void init()
	{
		try
		{
			outDir = new File(System.getProperty("user.dir") + File.separator + loadNumber);
			if(!outDir.exists())
				outDir.mkdir();
			
			//Fetch Metadata from DynamoDB table
			//ScanDynamoDBTable();    // WOrks for scanning whole table
		}
		catch(Exception ex)
		{
			System.err.println("OutDir not found!!!!");
			System.err.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	public void begin()
			throws Exception
	{

		outFile = new PrintWriter(new FileWriter(inFile));
		String path="";
		String name="";
		int pathSeperator = inFile.lastIndexOf("/");
		System.out.println("pathSeperator "+pathSeperator);
		if(pathSeperator>=0)
		{
			path=inFile.substring(0,pathSeperator+1);			
		}

		open = true;
	} 
	public void ReadInputFile(String inFile)
	{
		BufferedReader br = null;
		this.inFile = inFile;
		
		try
		{
			if(inFile.toLowerCase().endsWith(".zip"))
			{
				System.out.println("Input Is Zip file: " + inFile);
				ZipFile zipFile = new ZipFile(inFile);
				Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
				Iterator<? extends ZipEntry> itr = zipEntries.asIterator();
				while(itr.hasNext())
				{
					ZipEntry entry = itr.next();
					if(!entry.getName().toLowerCase().startsWith("cpxni"))
					{
						br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
						determineWriteFunction(br);
					}
					else
					{
						System.out.println("Entry " + entry.getName() + " not CPX Abstract/ANI file, skip it");
					}
					
				}
				
			}
			else if(inFile.toLowerCase().endsWith("xml"))
			{
				System.out.println("Input Is Xml file: " + inFile);
				File file = new File(inFile);
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				determineWriteFunction(br);
			}
		}
		catch(IOException ex)
		{
			System.out.println("Issue reading inFile!!!!");
			System.out.println("Error: " + ex.getMessage());
			System.out.println("Exit");
			System.exit(1);
		}
		catch(IllegalStateException ex)
		{
			System.out.println("Issue reading Zip file entries!!!!");
			System.out.println("Exit");
			System.exit(1);
		}
		catch(Exception ex)
		{
			System.out.println("Issue reading Input File!!!!");
			System.exit(1);
		}
	}
	
	public void determineWriteFunction(BufferedReader br)
	{
		
		if(database != null && ! database.isBlank())
		{
			if(database.toUpperCase().equals(db.CPX.name()))
				writeRecs(br);
			else if(database.toUpperCase().equals(db.INS.name()))
				writeINSRecs(br);
		}
	
	}
	public void writeRecs(BufferedReader xmlReader)
	{
		int recordCount = 0;
		boolean start = false;
		String line;
		String m_id = null;
		
		StringBuilder sb = new StringBuilder();
		
		String pui = null, accessNumber = null; 
		FileWriter outFile = null;
		List<File> filesList = new ArrayList<>();
		
		long startTime = System.currentTimeMillis();
		long midTime = 0, endTime = 0;
		
		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();
			
			
			
			while((line = xmlReader.readLine()) != null)
			{
				// If identified beginning of file, next few lines are for the item record
				if(start)
				{
					sb.append(line + "\n");
				}
				// Beginning of Record, start reading
				if(line.indexOf("<item>") > -1)
				{
					start = true;
					sb.append(SchemaConstants.BD_ENCODING + "\n");
					sb.append(SchemaConstants.BD_STARTROOTELEMENT + "\n");
					sb.append(line + "\n");
				}
				if(line.contains("<itemid idtype=\"PUI\">"))
				{
					pui = line.substring(line.indexOf(">") +1, line.indexOf("</itemid>"));
					System.out.println("PUI: " + pui);
					continue;
				}
				
				// If record didnot match DynamoDB record based on PUI, try accessnumber
				if(line.contains("<itemid idtype=\"CPX\">"))
				{
					accessNumber = line.substring(line.indexOf(">") +1, line.indexOf("</itemid>"));
					System.out.println("AN: " + accessNumber);
					continue;
				}
					
				// End of record, stop reading & retain M_ID for naming out file
				if(line.indexOf("</item>") > -1)
				{
					start = false;
					sb.append(SchemaConstants.BD_ENDROOTELEMENT);
					
					// Get M_ID for naming file
					m_id = getMid(pui, accessNumber);
					File file = new File(outDir + File.separator +m_id + ".xml");
					outFile = new FileWriter(file);
					outFile.write(sb.toString());
					
					recordCount ++;
					
					// Reset/clear out SB for next record
					sb.delete(0, sb.length());
					
					//close fileWriter
					close(outFile);
					filesList.add(file);
					
					//endTime = System.currentTimeMillis();
					//System.out.println("Total Time to generate xml file: " + (endTime - startTime)/1000 + " seconds....");
					//Write record to S3 bucket
					
					
					//[1] Thread option for individual file upload to S3, slower than transfermanager
					/*@SuppressWarnings("static-access")
					//UploadFileToS3Thread thread = new UploadFileToS3Thread("thread" + recordCount, s3Client, bucketName, file.getName(), file.getAbsolutePath(), loadNumber);
					thread.run(); */
					
					
					
					
					
				}
			}

			midTime = System.currentTimeMillis();
			System.out.println("Total time to parse inFile: " + (midTime - startTime)/1000 + " seconds");
			//[2] dir option for whole dir
			UploadFileToS3 s3upload = new UploadFileToS3();
			s3upload.uploadDirToS3Bucket(outDir.getAbsolutePath(), s3Client,bucketName, database, Integer.toString(loadNumber), filesList);
		} 
			
		catch (IOException e) 
		{
			System.err.println("Problem reading XML FILE!!!!");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		
		catch(AmazonServiceException ase)
		{
			System.out.println("Exception in parsingInput XML file!!!!");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch (SdkClientException e) {
			System.out.println("Exception in parsingInput XML file!!!!");
            e.printStackTrace();
        }
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Total Record Count uploaded for file: " + inFile + " : " + recordCount);
			endTime = System.currentTimeMillis();
			System.out.println("Total Time used: " + (endTime - midTime) /1000 + " seconds");
		}
	}
	
	
	public void writeINSRecs(BufferedReader xmlReader)
	{
		
		int recordCount = 0;
		boolean start = false;
		String line;
		String m_id = null;
		
		StringBuilder sb = new StringBuilder();
		
		String accessNumber = null; 
		FileWriter outFile = null;
		List<File> filesList = new ArrayList<>();
		
		long startTime = System.currentTimeMillis();
		long midTime = 0, endTime = 0;
		
		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();
			
			
			
			while((line = xmlReader.readLine()) != null)
			{
				// If identified beginning of file, next few lines are for the item record
				if(start)
				{
					sb.append(line + "\n");
				}
				// Beginning of Record, start reading
				if(line.indexOf("<article type=\"current\">") > -1 || (line.indexOf("<article type=\"backfill\">") > -1))
				{
					start = true;
					sb.append(SchemaConstants.INS_ENCODING + "\n");
					sb.append(SchemaConstants.INS_SCHEMA + "\n");
					sb.append(SchemaConstants.INS_STARTROOTELEMENT + "\n");
					sb.append(line + "\n");
				}
				
				
				// If record didnot match DynamoDB record based on PUI, try accessnumber
				if(line.contains("<accn>"))
				{
					accessNumber = line.substring(line.indexOf(">") +1, line.indexOf("</accn>"));
					//scan DynamoDB table to fetch M_ID
					ScanDynamoDBTable(accessNumber);
					//System.out.println("AN: " + accessNumber);
					continue;
				}
					
				// End of record, stop reading & retain M_ID for naming out file
				if(line.indexOf("</article>") > -1)
				{
					start = false;
					sb.append(SchemaConstants.INS_ENDROOTELEMENT);
					
					// Get M_ID for naming file
					m_id = getMid(null, accessNumber);
					File file = new File(outDir + File.separator +m_id + ".xml");
					outFile = new FileWriter(file);
					outFile.write(sb.toString());
					
					recordCount ++;
					
					// Reset/clear out SB for next record
					sb.delete(0, sb.length());
					
					//close fileWriter
					close(outFile);
					filesList.add(file);
					
					//endTime = System.currentTimeMillis();
					//System.out.println("Total Time to generate xml file: " + (endTime - startTime)/1000 + " seconds....");
					//Write record to S3 bucket
					
					
					//[1] Thread option for individual file upload to S3, slower than transfermanager
					/*@SuppressWarnings("static-access")
					//UploadFileToS3Thread thread = new UploadFileToS3Thread("thread" + recordCount, s3Client, bucketName, file.getName(), file.getAbsolutePath(), loadNumber);
					thread.run(); */

					
				}
			}

			midTime = System.currentTimeMillis();
			System.out.println("Total time to parse inFile: " + (midTime - startTime)/1000 + " seconds");
			//[2] dir option for whole dir
			UploadFileToS3 s3upload = new UploadFileToS3();
			s3upload.uploadDirToS3Bucket(outDir.getAbsolutePath(), s3Client,bucketName, database, Integer.toString(loadNumber), filesList);
		} 
			
		catch (IOException e) 
		{
			System.err.println("Problem reading XML FILE!!!!");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		
		catch(AmazonServiceException ase)
		{
			System.out.println("Exception in parsingInput XML file!!!!");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch (SdkClientException e) {
			System.out.println("Exception in parsingInput XML file!!!!");
            e.printStackTrace();
        }
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Total Record Count uploaded for file: " + inFile + " : " + recordCount);
			endTime = System.currentTimeMillis();
			System.out.println("Total Time used: " + (endTime - midTime) /1000 + " seconds");
		}
	}
	
	// GET M_ID for given PUI/AccessNumber
	
	public String getMid(String pui, String accessNumber) throws Exception {
		String m_id = null;
		try {
				m_id = database.trim().toLowerCase()+"_"+ (new GUID().toString());
			
			if (pui != null && !pui.isEmpty()) {
				if (dynamoDBPuiMidList.containsKey(pui))
					m_id = dynamoDBPuiMidList.get(pui);
				else if (dynamoDBAnMidList.containsKey(accessNumber))
					m_id = dynamoDBAnMidList.get(accessNumber);
			}
			else if(accessNumber != null && !accessNumber.isEmpty())
				if (dynamoDBAnMidList.containsKey(accessNumber))
					m_id = dynamoDBAnMidList.get(accessNumber);
			
		} catch (Exception e) {

			e.printStackTrace();
		}

		return m_id;
		
	}
	
	public void close(FileWriter outFile)
	{
		try
		{
			if(outFile != null)
			{
				outFile.flush();
				outFile.close();
			}
		}
		catch(Exception e)
		{
			System.out.println("Failed to close OutFile!!!");
			e.printStackTrace();
		}
	}
	
	public void run(String[] args)
	{
		String inFile = null;
		
		if(args.length >3)
		{
			if(args[0] != null && !args[0].isEmpty())
			{
				inFile = args[0];
				System.out.println("Input File: " + inFile);
			}
			else
			{
				System.out.println("Not enough parameters!!!");
				System.exit(1);
			}
			
			if(args[1] != null && !args[1].isEmpty())
			{
				database = args[1];
				System.out.println("database: " + database);
			}
			
			if(args[2] != null && !args[2].isEmpty())
			{
				bucketName = args[2];
				System.out.println("S3 Bucket: " + bucketName);
			}
			else
			{
				System.out.println("Not enough parameters!!!");
				System.exit(1);
			}
			
			if(args[3] != null && ! args[3].isEmpty())
			{
				if(args[3].matches("[0-9]+"))
				{
					loadNumber = Integer.parseInt(args[3]);
					System.out.println("LoadNumber: " + loadNumber);
				}
					
				else
				{
					System.out.println("Invalid loaNumber");
				}
			}
				
		}
		else
		{
			System.out.println("Not enough parameters!!!");
			System.out.println("Rerun the process with inFile, database, S3 bucket name & loadNumber");
			System.exit(1);
		}
		
		init();
		//ScanDynamoDBTable();			// test DynamoDB read
		ReadInputFile(inFile);
	}
	public static void main(String[] args)
	{
		
		ParseInputXmlFileEntry obj = new ParseInputXmlFileEntry();
		obj.run(args);
		
	}
	
	public void parseDynamoDBJsonResponse(String response) {

		String pui = null, accessNumber = null, m_id = null;;
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(response);

			if(json.containsKey("PUI"))
				pui  =json.get("PUI").toString().trim();
			
			if(json.containsKey("ACCESSNUMBER"))
				accessNumber = json.get("ACCESSNUMBER").toString().trim();
			if(json.containsKey("ANUM"))
				accessNumber = json.get("ANUM").toString().trim();
			
			if(json.containsKey("M_ID"))
				m_id = json.get("M_ID").toString().trim();
			if(pui != null)
				dynamoDBPuiMidList.put(pui, m_id);
			if(accessNumber != null)
				dynamoDBAnMidList.put(accessNumber, m_id);

		} 
		catch (ParseException e) 
		{
			System.err.println("Issue parsing DynamoDB JSON Response!!!!");
			e.printStackTrace();
		}

	}
}
