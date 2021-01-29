package org.ei.dataloading.bd.loadtime.nosql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.ei.dataloading.aws.AmazonDynamodbService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;

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
	
	private int loadNumber;
	private String database;
	private String action;
	private String inFile;
	private PrintWriter outFile = null;
	private boolean open = false;
	
	Map<String,String> dynamoDBPuiMidList;
	Map<String,String> dynamoDBAnMidList;
	
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
		
		dynamoDBPuiMidList = new HashMap<>();
		dynamoDBAnMidList = new HashMap<>();
		try
		{
			ItemCollection<ScanOutcome> items = table.scan(scanSpec);
			Iterator<Item> itr = items.iterator();
			while(itr.hasNext())
			{
				Item item = itr.next();
				System.out.println(item.toJSONPretty());
				parseDynamoDBJsonResponse(item.toJSONPretty());
			}
			System.out.println("pui list size: " + dynamoDBPuiMidList.size());
			System.out.println("pui list size: " + dynamoDBAnMidList.size());
		}
		catch(Exception e)
		{
			System.err.println("Unable to scan the table:");
			System.err.println(e.getMessage());
			e.printStackTrace();
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
					br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
					writeRecs(br);
				}
				
			}
			else if(inFile.toLowerCase().endsWith("xml"))
			{
				System.out.println("Input Is Xml file: " + inFile);
				File file = new File(inFile);
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				writeRecs(br);
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
	
	public void writeRecs(BufferedReader xmlReader)
	{
		
	}
	
	public static void main(String[] args)
	{
		ParseInputXmlFileEntry obj = new ParseInputXmlFileEntry();
		obj.ScanDynamoDBTable();
	}
	
	public void parseDynamoDBJsonResponse(String response) {

		String pui = null;
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(response);

			pui  =json.get("PUI").toString().trim();
			String accessNumber = json.get("AccessNumber").toString().trim();
			String m_id = json.get("M_ID").toString().trim();
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
