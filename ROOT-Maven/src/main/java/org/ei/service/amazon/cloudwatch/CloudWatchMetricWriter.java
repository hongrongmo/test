package org.ei.service.amazon.cloudwatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;
import org.ei.config.ApplicationProperties;
import org.ei.service.amazon.AmazonServiceHelper;
import org.perf4j.GroupedTimingStatistics;
import org.perf4j.TimingStatistics;

import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

/**
 * 
 * @author HaroveTM Writes metrics to CloudWatch
 */
public class CloudWatchMetricWriter {
	String namespace = "EV-WEB-METRICS-"
			+ (StringUtils.isBlank(System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL)) ? "NOENVSET" : System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL).toUpperCase());

	// Ctor
	public CloudWatchMetricWriter(String namespace) {
		if (StringUtils.isNotBlank(namespace)) {
			this.namespace = namespace;
		}
	}

	/**
	 * Write metrics from a GroupedTimingStatistics object. This is meant to be
	 * used in conjuntion with perf4j!
	 * 
	 * @param groupedTimingStatistics
	 */
	public void putMetrics(GroupedTimingStatistics groupedTimingStatistics) {
		if (groupedTimingStatistics == null) {
			throw new RuntimeException("GroupedTimingStatistics are null!");
		}

		SortedMap<String, TimingStatistics> statisticsbytag = groupedTimingStatistics.getStatisticsByTag();
		for (String key : statisticsbytag.keySet()) {
			TimingStatistics timing = statisticsbytag.get(key);
			if (timing != null) {
				// Build metric data
				PutMetricDataRequest putmetricdataRequest = new PutMetricDataRequest();
				List<MetricDatum> datalist = new ArrayList<MetricDatum>();
				
				// Create metric for elapsed time
				MetricDatum datum = new MetricDatum();
				datum.setMetricName(key);
				datum.setTimestamp(new Date());
				datum.setUnit(StandardUnit.Milliseconds);
				datum.setValue((double) timing.getMean());
				datalist.add(datum);

				putmetricdataRequest.setMetricData(datalist);
				putmetricdataRequest.setNamespace(this.namespace);

				// Fire and forget!
				AmazonServiceHelper.getInstance().getAmazonCloudWatchAsyncClient().putMetricData(putmetricdataRequest);

			}
		}
	}
}
