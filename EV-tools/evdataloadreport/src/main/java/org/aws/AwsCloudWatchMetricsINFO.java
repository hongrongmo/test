package org.aws;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

public class AwsCloudWatchMetricsINFO {

	private static String endpoint = "monitoring.us-east-1.amazonaws.com";
	private LinkedHashMap<String,String> data = new LinkedHashMap<String, String>();
	
	private static AwsCloudWatchMetricsINFO instance;
	
	public AwsCloudWatchMetricsINFO(){}
	public AwsCloudWatchMetricsINFO(String filePath, String nameSpace, String instanceId, String metricName, String statisticName)
	{
	
	}
	
	//public static AwsCloudWatchMetricsINFO getInstance(String filePath, String nameSpace, String instanceId,String metricName)
	public static AwsCloudWatchMetricsINFO getInstance()
	{
		if(instance ==null)
		{
			instance = new AwsCloudWatchMetricsINFO();
		}
		
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void fetchMetricsInfo(String filePath,String nameSpace, String instanceId,String metricName, String statisticName) throws IllegalArgumentException, IOException
	{
		
		//String FileName = filePath+"\\resources\\cloudwatchcredentials.properties";
		String FileName = filePath+"//resources//cloudwatchcredentials.properties";
		AmazonCloudWatchClient cloudWatch = new AmazonCloudWatchClient(new PropertiesCredentials(new File (FileName)));
		//AmazonCloudWatchClient cloudWatch = new AmazonCloudWatchClient(new PropertiesCredentials(AwsCloudWatchMetricsINFO.class.getResourceAsStream(FileName)));
		
		if(data.size() >0)
		{
			data.clear();
		}
		
		
		cloudWatch.setEndpoint(endpoint);
		
		long offMilliSeconds = 1000 * 60 * 60 * 24;
		
		Dimension instanceDimesnion = new Dimension();
		
		if(nameSpace !=null && !(nameSpace.equalsIgnoreCase("default")))
		{
			if(nameSpace.equalsIgnoreCase("AWS/RDS"))
			{
				instanceDimesnion.setName("DBInstanceIdentifier");
			}
			else if(nameSpace.equalsIgnoreCase("AWS/EC2"))
			{
				instanceDimesnion.setName("InstanceId");
			}
		}

		instanceDimesnion.setValue(instanceId);
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest();
		
		request.withStartTime(new Date(new Date().getTime() - offMilliSeconds));
		request.withNamespace(nameSpace);
        //request.withPeriod(60 * 60);
		
		//make it every 5 minutes, so it matches AWS CloudWatch default period
		request.withPeriod(300);   //seconds
  
        request.withMetricName(metricName);
       // request.withStatistics("Average");
       // System.out.println("StatiscName:  " + statisticName);
        request.withStatistics(statisticName);
        request.withDimensions(Arrays.asList(instanceDimesnion));
        request.withEndTime(new Date());
        
        GetMetricStatisticsResult getMetricStatisticsResult = cloudWatch.getMetricStatistics(request);
		
        List<Datapoint> dataPoints = getMetricStatisticsResult.getDatapoints();
        
        Collections.sort(dataPoints, new AwsCloudWatchMetricsINFO.sortingList());

        if(statisticName !=null)
        {
        	if(statisticName.equalsIgnoreCase("Minimum"))
        	{
        		for(Datapoint db : dataPoints)
                {
                	data.put(db.getTimestamp().toString(), db.getMinimum() + " " + db.getUnit());
                }
        		
        	}
        	else if (statisticName.equalsIgnoreCase("Maximum"))
        	{
        		 for(Datapoint db : dataPoints)
        	        {
        	        	data.put(db.getTimestamp().toString(), db.getMaximum() + " " + db.getUnit());
        	        }
        	}
        	else if (statisticName.equalsIgnoreCase("Average"))
        	{
        		for(Datapoint db : dataPoints)
                {
                	data.put(db.getTimestamp().toString(), db.getAverage() + " " + db.getUnit());
                }
        	}
        	else if (statisticName.equalsIgnoreCase("Sum"))
        	{
        		for(Datapoint db : dataPoints)
                {
                	data.put(db.getTimestamp().toString(), db.getSum() + " " + db.getUnit());
                }
        	}
        }
        

	}
	
	public static class sortingList implements Comparator {
		public int compare(Object o1, Object o2) {
			Datapoint dp1 = (Datapoint) o1;
			Datapoint dp2 = (Datapoint) o2;
			String startTime1 = dp1.getTimestamp().toString();
			String startTime2 = dp2.getTimestamp().toString();
			return startTime1.compareTo(startTime2);
		}

		public Comparator reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator thenComparing(Comparator other) {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator thenComparing(Function keyExtractor,
				Comparator keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator thenComparing(Function keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator thenComparingInt(ToIntFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator thenComparingLong(ToLongFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public <T> Comparator<T> nullsFirst(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public <T> Comparator<T> nullsLast(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public <T, U> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public <T, U extends Comparable<? super U>> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public <T> Comparator<T> comparingInt(
				ToIntFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public <T> Comparator<T> comparingLong(
				ToLongFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public <T> Comparator<T> comparingDouble(
				ToDoubleFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public HashMap<String, String> getMetricsInfoList()
	{
		return data;
	}
	
}
