/* Copyright (c) 2008-2009 HomeAway, Inc.
 * All rights reserved.  http://www.perf4j.org 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.ei.service.amazon.cloudwatch; 
 
import java.io.Flushable;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;
import org.ei.config.ApplicationProperties;
import org.perf4j.GroupedTimingStatistics;
import org.perf4j.StopWatch;
 
/**
 * This log4j Appender groups StopWatch log messages together to form GroupedTimingStatistics. At a scheduled interval 
 * the StopWatch log messages that currently exist in the buffer are pulled to create a single 
 * GroupedTimingStatistics instance that is then sent to any attached appenders. 
 * <p/> 
 * Note that any LoggingEvents which do NOT contain StopWatch objects are discarded. Also, this appender stores logged 
 * messages in a bounded buffer before sending those messages to downstream appenders. If the buffer becomes full then 
 * subsequent logs will be discarded until the buffer has time to clear. You can access the number of discarded 
 * messages using the getNumDiscardedMessages() method. 
 * 
 * @author Alex Devine 
 */ 
public class CloudWatchAppender extends AppenderSkeleton implements AppenderAttachable, Flushable { 
    /**
     * This class keeps track of all appenders of this type that have been created. This allows static access to
     * the appenders from the org.perf4j.log4j.servlet.GraphingServlet class.
     */
    protected final static Map<String, CloudWatchAppender> APPENDERS_BY_NAME =
            Collections.synchronizedMap(new LinkedHashMap<String, CloudWatchAppender>());

    // --- configuration options ---
    /**
     * The type of data to display on the graph. Defaults to "Mean" to display mean values. Acceptable values are any
     * constant name from the {@link org.perf4j.helpers.StatsValueRetriever} class, such as Mean, Min, Max, Count,
     * StdDev or TPS.
     */
    private String nameSpace;

    // --- contained objects/state variables ---
    /**
     * Used to write metrics to AWS CloudWatch
     */
    private CloudWatchMetricWriter cloudwatchMetricWriter;
    /**
     * Keeps track of whether there is existing data that hasn't yet been flushed to downstream appenders.
     */
    private volatile boolean hasUnflushedData = false;
    /**
     * Keeps track of the Level of the last appended event. This is just used to determine what level we send to OUR
     * downstream events.
     */
    private Level lastAppendedEventLevel = Level.INFO;
    /**
     * Any downstream appenders are contained in this AppenderAttachableImpl
     */
    private final AppenderAttachableImpl downstreamAppenders = new AppenderAttachableImpl();

    // --- options ---

    /**
     * The <b>NameSpace</b> option is used to specify the namespace to collect metric data under. Defaults to "EV-WEB-METRICS" if not
     * explicitly set.
     *
     * @return The value of the Namespace option
     */
    public String getNameSpace() {
        return nameSpace;
    }

    /**
     * Sets the value of the <b>NameSpace</b> option. This can be any valid String but AWS will reject if it starts with "AWS".
     *
     * @param graphType The new value for the GraphType option.
     */
    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public void activateOptions() {
        cloudwatchMetricWriter = createCloudWatchMetricWriter();

        //update the static APPENDERS_BY_NAME object
        if (getName() != null) {
            APPENDERS_BY_NAME.put(getName(), this);
        }
    }

    /**
     * Helper method creates a new CloudWatchMetricWriter based on the options set on this appender. 
     *
     * @return A newly created CloudWatchMetricWriter.
     */
    protected CloudWatchMetricWriter createCloudWatchMetricWriter() {
        //create the cloudwatch metric writer
    	if (StringUtils.isBlank(nameSpace)) {
    		String runlevel = System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL);
    		nameSpace = "EV-WEB-METRICS-" + (StringUtils.isNotBlank(runlevel) ? runlevel.toUpperCase() : "ENVNOTSET"); 
    	}
    	CloudWatchMetricWriter retVal = new CloudWatchMetricWriter(nameSpace);
        return retVal;
    }

    // --- exposed objects ---

    /**
     * This static method returns any created GraphingStatisticsAppender by its name.
     *
     * @param appenderName the name of the GraphingStatisticsAppender to return
     * @return the specified GraphingStatisticsAppender, or null if not found
     */
    public static CloudWatchAppender getAppenderByName(String appenderName) {
        return APPENDERS_BY_NAME.get(appenderName);
    }

    /**
     * This static method returns an unmodifiable collection of all GraphingStatisticsAppenders that have been created.
     *
     * @return The collection of GraphingStatisticsAppenders created in this VM.
     */
    public static Collection<CloudWatchAppender> getAllCloudWatchStatisticsAppenders() {
        return Collections.unmodifiableCollection(APPENDERS_BY_NAME.values());
    }

    // --- appender attachable methods ---

    public void addAppender(Appender appender) {
        synchronized (downstreamAppenders) {
            downstreamAppenders.addAppender(appender);
        }
    }

    public Enumeration getAllAppenders() {
        synchronized (downstreamAppenders) {
            return downstreamAppenders.getAllAppenders();
        }
    }

    public Appender getAppender(String name) {
        synchronized (downstreamAppenders) {
            return downstreamAppenders.getAppender(name);
        }
    }

    public boolean isAttached(Appender appender) {
        synchronized (downstreamAppenders) {
            return downstreamAppenders.isAttached(appender);
        }
    }

    public void removeAllAppenders() {
        synchronized (downstreamAppenders) {
            downstreamAppenders.removeAllAppenders();
        }
    }

    public void removeAppender(Appender appender) {
        synchronized (downstreamAppenders) {
            downstreamAppenders.removeAppender(appender);
        }
    }

    public void removeAppender(String name) {
        synchronized (downstreamAppenders) {
            downstreamAppenders.removeAppender(name);
        }
    }

    // --- appender methods ---

    protected void append(LoggingEvent event) {
        Object logMessage = event.getMessage();
        if (logMessage instanceof GroupedTimingStatistics) {
        	cloudwatchMetricWriter.putMetrics((GroupedTimingStatistics) logMessage);
        	// Append to 
            hasUnflushedData = true;
            lastAppendedEventLevel = event.getLevel();
            flush();
        }
    }

    public boolean requiresLayout() {
        return false;
    }

    public void close() {
        //close any downstream appenders
        synchronized (downstreamAppenders) {
            flush();

            for (Enumeration enumer = downstreamAppenders.getAllAppenders();
                 enumer != null && enumer.hasMoreElements();) {
                Appender appender = (Appender) enumer.nextElement();
                appender.close();
            }
        }
    }

    // --- Flushable method ---
    /**
     * This flush method writes the graph, with the data that exists at the time it is calld, to any attached appenders.
     */
    public void flush() {
        synchronized(downstreamAppenders) {
            if (hasUnflushedData && downstreamAppenders.getAllAppenders() != null) {
                downstreamAppenders.appendLoopOnAppenders(
                        new LoggingEvent(Logger.class.getName(),
                                         Logger.getLogger(StopWatch.DEFAULT_LOGGER_NAME),
                                         System.currentTimeMillis(),
                                         lastAppendedEventLevel,
                                         "",
                                         null)
                );
                hasUnflushedData = false;
            }
        }
    }
}
