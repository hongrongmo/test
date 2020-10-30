package org.dataloading.sharedsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.ei.dataloading.aws.AmazonSNSService;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
/**
 * 
 * @author TELEBH
 * @Date: 09/10/2020
 * @Description: Publish SNS message mainly for ES Weekly QA report as we do now in FAST
 */
public class PublishSNS{
	
	private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:230521890328:EVDataLoading";
	private static String subject = "ES Weekly QA Report";
	Logger logger;
	
	String searchField;
	String searchValue;
	String facetField;
	String fileName;
	
	PublishSNS(String searchField, String searchValue, String facetField, String fileName)
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
	
	public void publishSNSMessage(String message)
	{
		// for QA email need to have a summary report 
		
		logger = Logger.getLogger(PublishSNS.class);
		
		//1.  log sns 
		
		//2. get SNS client
		
		AmazonSNS sns = AmazonSNSService.getInstance().getAmazonSNSClient();
		
		 PublishRequest request = new PublishRequest();
		 request.setMessage(message);
		 request.setTargetArn(TOPIC_ARN);
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
							if(row[0].contains("upa") || row[0].contains("eup") || row[0].contains("wop"))
							{
								patents =  row[0] + ", " + patents;
								if(row.length > 1)
									patentCount += Integer.parseInt(row[1]);
							}
							else
								messageBody.append(line + " \n");
									
						}
					}
					messageBody.append(patents.substring(0, patents.lastIndexOf(",") -1).trim() + "\t" + patentCount);
					
				}
				catch(IOException ex)
				{
					logger.error("Failed to Open File to read from in PublishSNS");
				}
				return messageBody.toString();

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
