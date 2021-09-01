package org.dataloading.dynamodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import org.ei.dataloading.aws.AmazonDynamodbService;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ItemCollectionMetrics;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

/**
 * 
 * @author TELEBH
 * @Date: 02/08/2021
 * @Description: Write DynamoDB DB Metadata using BatchWrite
 */
public class DynamoDbBatchWrite extends Thread {

	AmazonDynamoDB dynamodbClient = null;
	DynamoDB dynamodb = null;
	private Thread th;
	private String threadName = null;
	private String dynamoDbTableName = null;
	List<Item> dynamodbBatchWriteList = new ArrayList<>();
	private CountDownLatch latch;
	
	
	public DynamoDbBatchWrite()
	{
		
	}
	
	public DynamoDbBatchWrite (String threadName, String tableName, List<Item> items)
	{
		this.threadName = threadName;
		this.dynamoDbTableName = tableName;
		this.dynamodbBatchWriteList = items;
		//System.out.println("List Size for batchWrite: " + items.size());
	}
	
	public DynamoDbBatchWrite (String threadName, String tableName, List<Item> items, AmazonDynamoDB client, DynamoDB dynamodb)
	{
		this(threadName, tableName, items);
		this.dynamodbClient = client;
		this.dynamodb = dynamodb;
	}
	
	//Initializations	
	public void init()
	{
		try
		{
			dynamodbClient = AmazonDynamodbService.getInstance().getAmazonDynamodbClient();
			dynamodb = new DynamoDB(dynamodbClient);
			
		}
		catch(Exception ex)
		{
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void start()
	{
		if(th ==null)
		{
			try 
			{
				th = new Thread(this, threadName);
				System.out.println("Started thread: " + th.getName());
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
		try
		{
			int retryCount = 1;
			TableWriteItems bdMasterTableWriteItems = new TableWriteItems(dynamoDbTableName)
					.withItemsToPut(dynamodbBatchWriteList);
			
			//System.out.println("Making DynamoDB WriteBatch Request");
			BatchWriteItemOutcome outcome = dynamodb.batchWriteItem(bdMasterTableWriteItems);
			
		
			// Resend unprocessed Items, re-try for only 4 times, if still not sent write to out file
			Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
			do
			{
				// check unprocessed keys if exceeded provisioned throuput
				if(outcome.getUnprocessedItems().size() ==0)
				{
					System.out.println("All items processed, no unprocessed items found: " + threadName);
				}
				else
				{
					System.out.println(threadName + ": " + outcome.getUnprocessedItems().size() + " items not written");
					System.out.println("Retrieveing Unprocessed Item ....");
					outcome = dynamodb.batchWriteItemUnprocessed(unprocessedItems);
				}
				retryCount++;
			} while(outcome.getUnprocessedItems().size() >0 & retryCount <5);
			
			if(outcome.getUnprocessedItems().size() >0)
			{
				Map<String, List<WriteRequest>> unprocessedList = outcome.getUnprocessedItems();
				for(Map.Entry<String,List<WriteRequest>> unprocessedItem: unprocessedList.entrySet())
				{
					List<WriteRequest> l = unprocessedItem.getValue();
					for(WriteRequest r: l)
					{
						Map<String,AttributeValue> item = r.getPutRequest().getItem();
						for(Map.Entry<String, AttributeValue> entry: item.entrySet())
						{
							System.out.println(entry.getKey() + " " + entry.getValue());
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Exception wtiting batches with thread: " + threadName);
			e.printStackTrace();
		}
		
		
	}

	public static void main(String[] args)
	{
		DynamoDbBatchWrite obj = new DynamoDbBatchWrite();
		obj.startEntry(args);
	}
	public void startEntry(String[] args)
	{
		init();
		runBatchWrite();
	}
	
	/**
	 * @Description: Used for main method testing purpose
	 */
	public void runBatchWrite()
	{
		//create BD Master item to write
	
		
		
		TableWriteItems bdMasterTableWriteItems = new TableWriteItems("dl_bd_master_dev")
				.withItemsToPut(new Item().
						withPrimaryKey("M_ID", "cpx_5e75829e17763838d83M788010178163190")
						.withString("PUI", "633980964")
						.withString("ACCESSNUMBER", "20210409826690")
						.withString("LOADNUMBER", "202106")
						.withString("UPDATENUMBER", "202106"));
		
		System.out.println("Making DynamoDB WriteBatch Request");
		BatchWriteItemOutcome outcome = dynamodb.batchWriteItem(bdMasterTableWriteItems);
		
		do
		{
			// check unprocessed keys if exceeded provisioned throuput
			Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
			if(outcome.getUnprocessedItems().size() ==0)
			{
				System.out.println("All items processed, no unprocessed items found");
			}
			else
			{
				System.out.println("Retrieveing Unprocessed Item ....");
				outcome = dynamodb.batchWriteItemUnprocessed(unprocessedItems);
			}
		} while(outcome.getUnprocessedItems().size() >0);
		
				
	}
	
	
}
