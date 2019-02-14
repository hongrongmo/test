package org.aws;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudsearch.model.DescribeIndexFieldsResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.s3.AmazonS3Client;

public class DescribeEc2 {

	HashMap<String, String> instanceIds = new HashMap<String, String>();
	
	private static DescribeEc2 instance;
	
	public DescribeEc2()
	{
		
	}
	
	public static DescribeEc2 getInstance()
	{
		if(instance == null)
		{
			
			instance = new DescribeEc2();
		}
		return instance;
	}
	
	public HashMap<String, String> fetchInstanceIds(String filePath) throws IllegalArgumentException, IOException
	{
		String FileName = filePath+"\\resources\\cloudwatchcredentials.properties";
		AmazonEC2 amazonEc2 = new AmazonEC2Client(new PropertiesCredentials(new File (FileName)));
		amazonEc2.setRegion(Region.getRegion(Regions.US_EAST_1));
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		
		DescribeInstancesResult result = amazonEc2.describeInstances(request);
		
		for(Reservation res: result.getReservations())
		{
			for(Instance instance: res.getInstances())
			{
				for(Tag tag: instance.getTags())
				{
					if(tag.getKey().equalsIgnoreCase("Name"))
					{
						//System.out.println(instance.getInstanceId() + " : " + tag.getValue());
						
						instanceIds.put(instance.getInstanceId(), tag.getValue());
					}
					
				}
				
			}
		}
		
		return instanceIds;
	}
	public static void main(String [] args) throws IllegalArgumentException, IOException
	{
		
		
	}
	
}
