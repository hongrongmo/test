/**
 *
 */
package org.ei.session;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.service.amazon.AmazonServiceHelper;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

/**
 * @author harovetm
 *
 */
@DynamoDBTable(tableName = "BlockedIPEvent")
public class BlockedIPEvent  {
    private static final Logger log4j = Logger.getLogger(BlockedIPEvent.class);

    public static final String EVENT_REQUEST_LIMIT = "REQUEST_LIMIT";
    public static final String EVENT_SESSION_LIMIT = "SESSION_LIMIT";
    public static final String EVENT_REQUESTRATE_LIMIT = "REQUESTRATE_LIMIT";

    public static final String KEY_IP = "IP";
    public static final String ATTRIBUTE_EVENT = "Event";
    public static final String ATTRIBUTE_MESSAGE = "Message";
    public static final String ATTRIBUTE_ENVIRONMENT = "Environment";
    public static final String ATTRIBUTE_TIMESTAMP = "Timestamp";

    private static final DynamoDBMapperConfig saveconfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER);

    private String IP;
    private String event;
    private String bucket;
    private String message;
    private String environment;
    private Date timestamp = new Date();

    public BlockedIPEvent() {
        try {
            this.environment = EVProperties.getApplicationProperties().getRunlevel();
        } catch (Throwable t) {
            this.environment = ApplicationProperties.RUNLEVEL_PROD;
        }
    };
    public BlockedIPEvent(String IP) {
        this();
        this.IP = IP;
    }

    @DynamoDBHashKey(attributeName = KEY_IP)
    public String getIP() {
        return IP;
    }

    public void setIP(String iP) {
        IP = iP;
    }

    @DynamoDBRangeKey(attributeName = ATTRIBUTE_TIMESTAMP)
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @DynamoDBIgnore
    public String getPrettyTimestamp() {
        if (this.timestamp == null) return "";
        else return dateFormatter.format(this.timestamp);
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_EVENT)
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_EVENT)
    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_MESSAGE)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_ENVIRONMENT)
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @DynamoDBIgnore
    private static String getCurrentEnvironment() {
        // Set then environment from current runtime properties
        String environment = ApplicationProperties.RUNLEVEL_PROD;
        try {
            environment = EVProperties.getApplicationProperties().getRunlevel();
        } catch (Throwable t) {
            log4j.error("Unable to retrieve ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL", t);
            environment = ApplicationProperties.RUNLEVEL_PROD;
        }
        return environment;
    }

    /**
     * Retrieve all BlockedIPEvent objects by Environment
     * @param IP
     * @param status
     * @return
     */
    public static List<BlockedIPEvent> getByEnvironment(String environment) {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        // Add Environment to scan filter
        scanExpression.addFilterCondition("Environment",
            new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(environment)));

        return mapper.scan(BlockedIPEvent.class, scanExpression);
    }

    /**
     * Retrieve all BlockedIPEvent objects by IP
     * @param IP
     * @param status
     * @return
     */
    public static List<BlockedIPEvent> getByIP(String IP) {
        return getByIP(IP, getCurrentEnvironment());
    }

    /**
     * Retrieve all BlockedIPEvent objects by IP
     * @param IP
     * @param status
     * @return
     */
    public static List<BlockedIPEvent> getByIP(String IP, String environment) {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        // Add IP to scan filter
        scanExpression.addFilterCondition("IP",
            new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(IP)));

        // Add Environment to scan filter
        scanExpression.addFilterCondition("Environment",
            new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(environment)));

        return mapper.scan(BlockedIPEvent.class, scanExpression);
    }

    /**
     * TimePeriod enum for use with getByCalendar calls
     * @author harovetm
     *
     */
    public enum TimePeriod {
        LASTYEAR(Calendar.YEAR, -1),
        LAST6MONTHS(Calendar.MONTH, -6),
        LASTMONTH(Calendar.MONTH, -1),
        LASTWEEK(Calendar.WEEK_OF_YEAR, -1),
        LASTDAY(Calendar.DAY_OF_YEAR, -1);

        int field;
        int period;
        TimePeriod(int field, int period) {
            this.field = field;
            this.period = period;
        }

        public Date date() {
            Calendar timestamp = Calendar.getInstance();
            timestamp.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            timestamp.add(field, period);
            return timestamp.getTime();
        }

        public String toString() {
            dateFormatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            return dateFormatter.format(this.date());
        }
    }

    /**
     * Retrieve all BlockedIPEvent objects by IP in TimePeriod
     * @param IP
     * @param status
     * @return
     */
    public static List<BlockedIPEvent> getByTimePeriod(String IP, TimePeriod t) {
        return getByTimePeriod(IP, getCurrentEnvironment(), t);
    }

    /**
     * Retrieve all BlockedIPEvent objects by IP in the last year
     * @param IP
     * @param environment
     * @return
     */
    public static List<BlockedIPEvent> getByTimePeriod(String IP, String environment, TimePeriod t) {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

        Condition rangeKeyCondition = new Condition()
            .withComparisonOperator(ComparisonOperator.GT.toString())
            .withAttributeValueList(new AttributeValue().withS(t.toString()));

        BlockedIPEvent blockedipevent = new BlockedIPEvent(IP);
        blockedipevent.setEnvironment(environment);
        DynamoDBQueryExpression<BlockedIPEvent> queryExpression = new DynamoDBQueryExpression<BlockedIPEvent>()
            .withHashKeyValues(blockedipevent)
            .withRangeKeyCondition(ATTRIBUTE_TIMESTAMP, rangeKeyCondition).withScanIndexForward(false);

        return mapper.query(BlockedIPEvent.class, queryExpression);
    }

    /**
     * Delete the blocking status of an IP address
     * @param ipstatus
     */
    public static void deleteByIP(String ip) {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        mapper.batchDelete(BlockedIPEvent.getByIP(ip));
    }

    /**
     * Delete the blocking events for an Environment
     * @param ipstatus
     */
    public static void deleteByEnvironment(String environment) {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        mapper.batchDelete(BlockedIPEvent.getByEnvironment(environment));
    }

    /**
     * Save the blocking status of an IP address
     * @param ipstatus
     */
    public void save() {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

        mapper.save(this, saveconfig);
    }

    /**
     * Delete the blocking status of an IP address
     * @param ipstatus
     */
    public void delete() {
        // Get DynamoDB client
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        mapper.delete(this, saveconfig);
    }
}
