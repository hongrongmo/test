package org.aws;

import java.awt.RenderingHints.Key;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.cloudwatch.*;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;


public class AwsCloudWatch{

	private static String endpoint = "";
	private static LinkedHashMap<String,String> data = new LinkedHashMap<String, String>();
	
	/**
	 * ENDpoint for all aws resources is the same "monitoring.us-east-1.amazonaws.com"
	 * @param args
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IllegalArgumentException, IOException
	{
		if(args[0] !=null)
		{
			endpoint = args[0];
		}
		AmazonCloudWatchClient cloudWatch = new AmazonCloudWatchClient(new PropertiesCredentials(new File ("cloudwatchcredentials.properties")));
		cloudWatch.setEndpoint(endpoint);
		
		long offMilliSeconds = 1000 * 60 * 60 * 24;
		
		Dimension instanceDimesnion = new Dimension();
		instanceDimesnion.setName("DBInstanceIdentifier");
		instanceDimesnion.setValue("eid");
		
		/*  EC2
		instanceDimesnion.setName("InstanceId");
		instanceDimesnion.setValue("i-60e325c0");
		*/
		
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest();
		
		request.withStartTime(new Date(new Date().getTime() - offMilliSeconds));
		request.withNamespace("AWS/RDS");
		//request.withNamespace("AWS/EC2");
        request.withPeriod(60 * 60);
        request.withMetricName("FreeStorageSpace");
        //request.withMetricName("CPUUtilization");
        
        request.withStatistics("Average");
        request.withDimensions(Arrays.asList(instanceDimesnion));
        request.withEndTime(new Date());
        
        GetMetricStatisticsResult getMetricStatisticsResult = cloudWatch.getMetricStatistics(request);
		
        List<Datapoint> dataPoints = getMetricStatisticsResult.getDatapoints();
        
       
        for(Datapoint db: getMetricStatisticsResult.getDatapoints())
        {
        	data.put(db.getTimestamp().toString(), db.getAverage() + db.getUnit());
        	//System.out.println(db.getTimestamp() + " : " + db.getAverage() + " : " + db.getUnit());
        }
        
        Collections.sort(dataPoints, new AwsCloudWatch.sortingList());
        
        
        for(Datapoint db : dataPoints)
        {
        	
        	System.out.println(db.getTimestamp() + " : " + db.getAverage() + " : " + db.getUnit());
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
   

}
