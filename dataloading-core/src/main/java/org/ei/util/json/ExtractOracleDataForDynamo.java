package org.ei.util.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dataloading.dynamodb.DBMetadata;
import org.dataloading.dynamodb.DynamoDbBatchWrite;
import org.ei.dataloading.aws.AmazonDynamodbService;
import org.ei.util.db.DbConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * 
 * @author TELEBH
 * @Date: 02/04/2021
 * @Description: Extract Existing Oracle DB data/columns into JSON files for BatchWrite into DynamoDB
 * for the POC for migration out of Oracle by storing XML files from source vendor into S3 bucket &
 * Using DynamoDB for Metadata
 * 
 * Result: Found out that writing batchItems using AWS CLI and providing Metadata in .json file as input can only upload 25 items/batchwrite
 */

public class ExtractOracleDataForDynamo {
	String dynamoDbTableName;
	String database;
	String userName;
	String password;
	String url = "jdbc:oracle:thin:@localhost:1521:eid";
	String driver = "oracle.jdbc.driver.OracleDriver";
	String loadNumber;
	
	
	Connection con = null;
	AmazonDynamoDB dynamodbClient = null;
	DynamoDB dynamodb = null;
	
	public static void main(String[] args)
	{
		if(args != null && args.length >0)
		{
			ExtractOracleDataForDynamo obj = new ExtractOracleDataForDynamo();
			obj.run(args);
		}
		else
		{
			System.out.println("Not enough parameters!!!");
			System.out.println("Rerun the process with url, driver, username , password, db, and/Or loadNumber");
			System.exit(1);
		}
	}
	
	public void run(String[] args)
	{
		if(args.length >3)
		{
			if(args[0] != null && !args[0].isBlank())
			{
				dynamoDbTableName = args[0];
				System.out.println("DynamoDB Table: " + dynamoDbTableName);
			}
				
			if(args[1] != null && !args[1].isBlank())
			{
				database = args[1];
				System.out.println("Database: " + database);
			}
			if(args[2] != null && !args[2].isBlank())
			{
				userName = args[2];
				System.out.println("UserName: " + userName);
			}
			if(args[3] != null && !args[3].isBlank())
			{
				password = args[3];
			}
			
		}
		if(args.length >5)
		{
			if(args[4] != null && !args[4].isBlank())
			{
				url = args[4];
				System.out.println("URL: " + url);
			}
			if(args[5] != null && !args[5].isBlank())
			{
				driver = args[5];
				System.out.println(driver);
			}
		}
		if(args.length >6)
		{
			if(args[6] != null && !args[6].isBlank())
			{
				if(args[6].matches("[0-9]+"))
				{
					loadNumber = args[6];
					System.out.println("LoadNumber: " + loadNumber);
				}
			}
		}
		else
		{
			System.out.println("Not enough parameters!!!!!");
			System.out.println("Rerun the process with userName, password and optional url & driver");
			System.exit(1);
		}
		init();
		ReadDataFromOracle();
	}
	
	/** 
	 * Initializer
	 */
	public void init()
	{
		try
		{
			con = DbConnection.getConnection(url, driver, userName, password);
			dynamodbClient = AmazonDynamodbService.getInstance().getAmazonDynamodbClient();
			dynamodb = new DynamoDB(dynamodbClient);
		}
		catch(Exception ex)
		{
			System.err.println("SQL Connection Exception!!!!");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	public void ReadDataFromOracle()
	{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String query;
		try
		{
			if(con != null)
			{
				if(database != null && !database.isBlank())
				{
					if(loadNumber != null)
					{
						if(database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("geo")
								|| database.equalsIgnoreCase("pch") || database.equalsIgnoreCase("elt")
								|| database.equalsIgnoreCase("chm"))
						{
							
								query = "select m_id, pui, accessNumber, loadnumber, updatenumber, doi from bd_master where database=? and updatenumber=?";
								stmt = con.prepareStatement(query);
								stmt.setString(1, database);
								stmt.setString(2, loadNumber);
						}
						else if(database.equalsIgnoreCase("c84"))
						{
							query = "select m_id, an, load_number, do from bd_master where load_number=?";
							stmt = con.prepareStatement(query);
							stmt.setString(1, loadNumber);
						}
							
						else if(database.equalsIgnoreCase("ins"))
						{
							query = "select m_id, anum, load_number, updatenumber,pdoi from ins_master where load_number=?";
							stmt = con.prepareStatement(query);
							stmt.setString(1, loadNumber);
						}
						else if(database.equalsIgnoreCase("ibf"))
						{
							query = "select m_id, anum, load_number, doi from ins_master where load_number=?";
							stmt = con.prepareStatement(query);
							stmt.setString(1, loadNumber);
						}
						else if(database.equalsIgnoreCase("upa") || database.equalsIgnoreCase("eup")
								|| database.equalsIgnoreCase("wop"))
						{
							query = "select m_id, pn, load_number from ins_master where load_number=?";
							stmt = con.prepareStatement(query);
							stmt.setString(1, loadNumber);
						}
						else if(database.equalsIgnoreCase("grf"))
						{
							query = "select m_id, id_number, load_number, doi from georef_master where load_number=?";
							stmt = con.prepareStatement(query);
							stmt.setString(1, loadNumber);
						}
					}
					
					// No loaNumber provided
					else
					{
						if(database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("geo")
								|| database.equalsIgnoreCase("pch") || database.equalsIgnoreCase("elt")
								|| database.equalsIgnoreCase("chm"))
						{
							
								query = "select m_id, pui, accessNumber, loadnumber, doi from bd_master where database=?";
								stmt = con.prepareStatement(query);
								stmt.setString(1, database);
						}
						else if(database.equalsIgnoreCase("c84"))
						{
							query = "select m_id, an, load_number, do from bd_master";
							stmt = con.prepareStatement(query);
						}
							
						else if(database.equalsIgnoreCase("ins"))
						{
							query = "select m_id, anum, load_number, updatenumber from ins_master";
							stmt = con.prepareStatement(query);
						}
						else if(database.equalsIgnoreCase("ibf"))
						{
							query = "select m_id, anum, load_number, doi from ins_master";
							stmt = con.prepareStatement(query);
						}
						else if(database.equalsIgnoreCase("upa") || database.equalsIgnoreCase("eup")
								|| database.equalsIgnoreCase("wop"))
						{
							query = "select m_id, pn, load_number from ins_master";
							stmt = con.prepareStatement(query);
						}
						else if(database.equalsIgnoreCase("grf"))
						{
							query = "select m_id, id_number, load_number, doi from georef_master";
							stmt = con.prepareStatement(query);
						}
					}
					
					//Run the query
					
					rs = stmt.executeQuery();
					rs.setFetchSize(25);
					if(rs != null)
					{
						//writeDynamoDBJsonRecs(rs);			// worked well for AWS CLI where it output data to out file
						writeDynamoDBMetadataRecs(rs);		// work for writebatch with threads, no output to out file
					}
					
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Exception Reading Data from oracle!!!!");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				if(stmt != null)
					stmt.close();
			}
			catch(Exception e)
			{
				System.err.println("Exception closing sql stmt!!!");
				e.printStackTrace();
			}
			
			try
			{
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{
				System.err.println("Exception closing resultSet!!!");
				e.printStackTrace();
			}
			
			try
			{
				if(con != null)
					con.close();
			}
			catch(Exception e)
			{
				System.err.println("Exception closing sqlConnection!!!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param rs
	 * @Description: Generates .json file with PutRequest Items for WriteBatch, where out.json file used as input for AWS CLI putrequest command
	 * It works fine as long as # of items/putrequest in out.json file <=25 records
	 */
	@SuppressWarnings("unchecked")
	public void writeDynamoDBJsonRecs(ResultSet rs)
	{
		BufferedWriter writer = null;
		int recCount = 0;
		try {
			
			JSONObject  dynamoBatchWrite = new JSONObject();
			JSONArray dynamoDbTableObj = new JSONArray();
			
			if(loadNumber != null)
				writer = new BufferedWriter(new FileWriter(new File(database + "_dynamodb_metadata_" + loadNumber + ".json")));
			else
				writer = new BufferedWriter(new FileWriter(new File(database + "_dynamodb_metadata.json")));
			
			while(rs.next())
			{
				recCount++;
				JSONObject dynamoDbputRequest = new JSONObject();
				JSONObject item = new JSONObject();
				JSONObject rec = new JSONObject();
				
				if(rs.getString(DBMetadata.M_ID.toString()) != null && !rs.getString(DBMetadata.M_ID.toString()).isBlank())
				{
					JSONObject obj = new JSONObject();
					obj.put("S", rs.getString(DBMetadata.M_ID.toString()));
					rec.put(DBMetadata.M_ID.toString(), obj);
				}
				if(rs.getString(DBMetadata.ACCESSNUMBER.toString()) != null && !rs.getString(DBMetadata.ACCESSNUMBER.toString()).isBlank())
				{
					JSONObject obj = new JSONObject();
					obj.put("S", rs.getString(DBMetadata.ACCESSNUMBER.toString()));
					rec.put(DBMetadata.ACCESSNUMBER.toString(), obj);
				}
				if(rs.getString(DBMetadata.PUI.toString()) != null && !rs.getString(DBMetadata.PUI.toString()).isBlank())
				{
					JSONObject obj = new JSONObject();
					obj.put("S", rs.getString(DBMetadata.PUI.toString()));
					rec.put(DBMetadata.PUI.toString(), obj);
				}
				if(rs.getString(DBMetadata.LOADNUMBER.toString()) != null && !rs.getString(DBMetadata.LOADNUMBER.toString()).isBlank())
				{
					JSONObject obj = new JSONObject();
					obj.put("S", rs.getString(DBMetadata.LOADNUMBER.toString()));
					rec.put(DBMetadata.LOADNUMBER.toString(), obj);
				}
				if(rs.getString(DBMetadata.UPDATENUMBER.toString()) != null && !rs.getString(DBMetadata.UPDATENUMBER.toString()).isBlank())
				{
					JSONObject obj = new JSONObject();
					obj.put("S", rs.getString(DBMetadata.UPDATENUMBER.toString()));
					rec.put(DBMetadata.UPDATENUMBER.toString(), obj);
				}
				if(rs.getString(DBMetadata.DOI.toString()) != null && !rs.getString(DBMetadata.DOI.toString()).isBlank())
				{
					JSONObject obj = new JSONObject();
					obj.put("S", rs.getString(DBMetadata.DOI.toString()));
					rec.put(DBMetadata.DOI.toString(), obj);
				}
				
				// Add Item Record
				item.put("Item", rec);
				dynamoDbputRequest.put("PutRequest", item);
				dynamoDbTableObj.add(dynamoDbputRequest);
			}
			
			System.out.println("Total Records: " + recCount);
			//Write JSON Object
			
			dynamoBatchWrite.put(database + "_master", dynamoDbTableObj);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jparser = new JsonParser();
			JsonElement jelement = jparser.parse(dynamoBatchWrite.toString());
			String jsonPrettyPrint = gson.toJson(jelement);
			
			//Write BatchWrite to JSON File
			writer.write(jsonPrettyPrint);
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			System.err.println("Issue creating DynamoDB Metadata JSON file!!!!");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(writer != null)
					writer.close();
			}
			catch(Exception e)
			{
				System.err.println("Issue closing DynamoDB Metadata JSON file!!!!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param rs
	 * @Description: WriteBatch for putRequest to DynamoDB tables, using multithreading. A list of 25 records sent on fly for BatchWrite thread "DynamoDBBatchWrite.java"
	 * This is alternative for out.json file method "writeDynamoDBJsonRecs(rs)" above 
	 */
	public void writeDynamoDBMetadataRecs(ResultSet rs)
	{
		List<Item> dynamodbBatchWriteList = new ArrayList<>();
		int threadCount = 1;
		int counter = 1;
		long startTime = System.currentTimeMillis();
		
		try {
			
			while(rs.next())
			{
				Item item = new Item();
				
				if(rs.getString(DBMetadata.M_ID.toString()) != null && !rs.getString(DBMetadata.M_ID.toString()).isBlank())
					item.withString(DBMetadata.M_ID.toString(), rs.getString(DBMetadata.M_ID.toString()));
				
				
				
				if(database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("geo") || database.equalsIgnoreCase("pch")
						|| database.equalsIgnoreCase("chm") || database.equalsIgnoreCase("elt"))
				{
					if(rs.getString(DBMetadata.PUI.toString()) != null && !rs.getString(DBMetadata.PUI.toString()).isBlank())
						item.withString(DBMetadata.PUI.toString(), rs.getString(DBMetadata.PUI.toString()));
					
					if(rs.getString(DBMetadata.ACCESSNUMBER.toString()) != null && !rs.getString(DBMetadata.ACCESSNUMBER.toString()).isBlank())
						item.withString(DBMetadata.ACCESSNUMBER.toString(), rs.getString(DBMetadata.ACCESSNUMBER.toString()));
					
					if(rs.getString(DBMetadata.LOADNUMBER.toString()) != null && !rs.getString(DBMetadata.LOADNUMBER.toString()).isBlank())
						item.withString(DBMetadata.LOADNUMBER.toString(), rs.getString(DBMetadata.LOADNUMBER.toString()));
				}
				else if (database.equalsIgnoreCase("ins"))
				{
					if(rs.getString(DBMetadata.ANUM.toString()) != null && !rs.getString(DBMetadata.ANUM.toString()).isBlank())
						item.withString(DBMetadata.ANUM.toString(), rs.getString(DBMetadata.ANUM.toString()));
				}
				
				if(rs.getString(DBMetadata.LOAD_NUMBER.toString()) != null && !rs.getString(DBMetadata.LOAD_NUMBER.toString()).isBlank())
					item.withString(DBMetadata.LOAD_NUMBER.toString(), rs.getString(DBMetadata.LOAD_NUMBER.toString()));
				
				
				if(rs.getString(DBMetadata.UPDATENUMBER.toString()) != null && !rs.getString(DBMetadata.UPDATENUMBER.toString()).isBlank())
					item.withString(DBMetadata.UPDATENUMBER.toString(), rs.getString(DBMetadata.UPDATENUMBER.toString()));
				
				if(counter %25 == 0)
				{	
					dynamodbBatchWriteList.add(item);
					DynamoDbBatchWrite th = new DynamoDbBatchWrite("Thread" + threadCount, dynamoDbTableName, dynamodbBatchWriteList, dynamodbClient, dynamodb);
					th.start();

					Thread.sleep(200);
					dynamodbBatchWriteList = new ArrayList<>();
					++threadCount;
				}
				else
					dynamodbBatchWriteList.add(item);
				
				counter++;	
			}
			if(dynamodbBatchWriteList.size() >0)
			{
				DynamoDbBatchWrite th = new DynamoDbBatchWrite("Thread" + threadCount, dynamoDbTableName, dynamodbBatchWriteList, dynamodbClient, dynamodb);
				th.start();
			}
	
		}
		catch(Exception e)
		{
			System.err.println("Exception extracting Metedata from DB to write int dynamodb in batchWrite !!!!");
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Time to write " + counter + " : " + (endTime - startTime)/1000 + " seconds");
	}

	
}
