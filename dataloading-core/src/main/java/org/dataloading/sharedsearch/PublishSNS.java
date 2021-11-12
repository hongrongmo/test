package org.dataloading.sharedsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.ei.dataloading.aws.AmazonSNSService;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
/**
 * 
 * @author TELEBH
 * @Date: 09/10/2020
 * @Description: Publish SNS message mainly for ES Weekly QA report as we do now in FAST
 */
public class PublishSNS{
	
	private String topic_arn = "arn:aws:sns:us-east-1:230521890328:EVDataLoading";
	private String subject = "ES Weekly QA Report";
	Logger logger;
	
	String searchField;
	String searchValue;
	String facetField;
	String fileName;
	
	public PublishSNS(String topicARN, String subject)
	{
		this.topic_arn = topicARN;
		this.subject = subject;
		
	}
	public PublishSNS(String searchField, String searchValue, String facetField, String fileName)
	{
		this.searchField = searchField;
		this.searchValue = searchValue;
		this.facetField = facetField;
		this.fileName = fileName;
	}
	public void dispatchMessage()
	{
		String message = "";
		if(facetField.equalsIgnoreCase("database"))
		{
			if(Pattern.compile("\\d{6}").matcher(searchValue).matches())
				message = generateWeeklyQASNSMessage();
		}
		else if(facetField.equalsIgnoreCase("batchinfo"))
			
			message = generatebatchInfoSNSMessage();

		publishSNSMessage(message);		
	}
	/*To add filter policy for messages sent to other teams than ei.operation group*/
	public void publishSNSMessageWithAttributes(String message)
	{
		
		/* for SNS messages sent to Judy and TM for some cases, as 1. cafe isopenaccess types when other than 0,1,2
		2. BD cittype mm,pp 
		*/
		
		logger = Logger.getLogger(PublishSNS.class);
		//1. log sns
		//2. get SNS client
		AmazonSNS sns = AmazonSNSService.getInstance().getAmazonSNSClient();
		PublishRequest request = new PublishRequest();
		 request.setMessage(message);
		 request.setTargetArn(topic_arn);
		 request.setSubject(subject);
		            
		
		 //3. Set message Attributes 
		 Map<String, MessageAttributeValue> attributes = new HashMap<>();
		 MessageAttributeValue attr_new_bd_cittype = new MessageAttributeValue().withDataType("String")
				 .withStringValue("New Doctype");
		
		 MessageAttributeValue attr_isOpenAccess_non_012 = new MessageAttributeValue().withDataType("String")
				 .withStringValue(">2");
		
		 
		 attributes.put("bd_new_cittype", attr_new_bd_cittype);
		 attributes.put("cafe_gt_2_oa", attr_isOpenAccess_non_012);
		 
		//3. publish SNS Message
		
		 System.out.println(message);
		 
		sns.publish(request.withMessageAttributes(attributes));
	}
	
	public void publishSNSMessage(String message)
	{
		// for QA email need to have a summary report 
		
		logger = Logger.getLogger(PublishSNS.class);
		
		//1.  log sns 
		
		//2. get SNS client
		
		AmazonSNS sns = AmazonSNSService.getInstance().getAmazonSNSClient();
		
		 PublishRequest request = new PublishRequest();
		 request.setMessage(message);
		 request.setTargetArn(topic_arn);
		 request.setSubject(subject);
		            
		
		//3. publish SNS Message
		
		 System.out.println(message);
		 
		sns.publish(request);
		
	}

	public String generateWeeklyQASNSMessage()
	{
		StringBuilder messageBody = new StringBuilder();
		subject = "ES Weekly QA Report: " + searchValue;
		messageBody.append("ES Weekly QA Report for Week: " + searchValue + "\n");
		messageBody.append("-------------- \n");

		// Holds all stats in file to Compare ES Prod/Cert results
		Map<String,String> esEnvsCounts = new HashMap<>();
			int patentCount = 0;
				try(BufferedReader reader = new BufferedReader(new FileReader(new File(fileName))))
				{
					String line;
					String patents = "";
					while((line = reader.readLine()) != null)
					{
						String[] row = line.split("\t");
						if(row.length >0)
						{
							if(esEnvsCounts.containsKey(row[0]) && row.length >1)
								esEnvsCounts.put(row[0], esEnvsCounts.get(row[0]) + "," + row[1]);
							else if(row.length >1)
								esEnvsCounts.put(row[0].trim(),row[1].trim());
							
							if(row[0].contains("upa") || row[0].contains("eup") || row[0].contains("wop"))
							{
								patents =  row[0] + ", " + patents;
								if(row.length > 1)
									patentCount += Integer.parseInt(row[1]);
							}
							else if(!line.trim().contains("END"))
								messageBody.append(line + " \n");
							
							if(line.contains("-----"))
							{
								patents = "";
								patentCount = 0;
								
							}
						}
						
						if(line.trim().contains("END"))
							if(!patents.isEmpty() && patents.contains(","))
								messageBody.append(patents.substring(0, patents.lastIndexOf(",") -1).trim() + "\t" + patentCount + "\n\n");
					}
					
					
				}
				catch(IOException ex)
				{
					logger.error("Failed to Open File to read from in PublishSNS");
				}
				
				//Compare results before return
				String dbCountsDiff = CompareWeeklyQAESCounts(esEnvsCounts);
				messageBody.append("\n\n" + dbCountsDiff);
				
				return messageBody.toString();
	}
	
	public String CompareWeeklyQAESCounts(Map<String,String> esEnvsCounts)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("DB Differences \n");
		sb.append("-------------- \n");
		if(esEnvsCounts.size() >0)
		{
			for(String db: esEnvsCounts.keySet())
			{
				if(esEnvsCounts.get(db).contains(","))
				{
					String[] dbCounts = esEnvsCounts.get(db).split(",");
					int diff = 0;
					for(String count: dbCounts)
					{
						diff = Math.abs(Integer.parseInt(count.trim()) - diff);
					}
					if(diff != 0)
						sb.append(db + "\t" + diff + "\n");
				}
				//else means db only exist in one ES env 
				else
				{
					sb.append(db + "\t" + esEnvsCounts.get(db) + "\n");
				}
			}
		}
		return sb.toString();
	}
	
	public String generatebatchInfoSNSMessage()
	{
		StringBuilder messageBody = new StringBuilder();
		subject = "BatchInfo Report: " + searchValue;
		messageBody.append("ES BatchInfo Report for: " + searchValue + "\n");
		messageBody.append("-------------- \n");

				try(BufferedReader reader = new BufferedReader(new FileReader(new File(fileName))))
				{
					String line;
					while((line = reader.readLine()) != null)
					{
						String[] row = line.split("\t");
						for(String str: row)
						{
							messageBody.append(str + "\t");
						}
						if(row.length == 3)
						{
							//1599796452040-12722-47-cpx-202013-20203802-202038
							String[] processInfoContents = row[2].split("-");
							if(processInfoContents.length>2)
							{
								if(!processInfoContents[1].isEmpty() && !row[1].isEmpty())
								{
									int diff = Integer.parseInt(processInfoContents[1]) - Integer.parseInt(row[1]);
									messageBody.append(diff + "\t");
								}
								
							}
								
						}
						messageBody.append("\n");
					}
					
					
				}
				catch(IOException ex)
				{
					logger.error("Failed to Open File to read from in PublishSNS");
				}
				return messageBody.toString();

	}
			
}
