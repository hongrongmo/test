package org.aws;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

public class DescribeEc2 {

	public static void main(String [] args) throws IllegalArgumentException, IOException
	{
		AmazonEC2 amazonEc2 = new AmazonEC2Client(new PropertiesCredentials(new File ("cloudwatchcredentials.properties")));
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
						System.out.println(instance.getInstanceId() + " : " + tag.getValue());
					}
					
				}
				
			}
		}
		//List<String> instanceIds = request.getInstanceIds();
		
		/*for(int i=0;i<instanceIds.size();i++)
		{
			System.out.println("Instance-ID: " + instanceIds.get(i));
		}*/
	}
	
	
}
