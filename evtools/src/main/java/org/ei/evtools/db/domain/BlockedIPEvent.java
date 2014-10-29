package org.ei.evtools.db.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@DynamoDBTable(tableName = "BlockedIPEvent")
@Component
public class BlockedIPEvent  {
	
	public static final String EVENT_REQUEST_LIMIT = "REQUEST_LIMIT";
    public static final String EVENT_SESSION_LIMIT = "SESSION_LIMIT";
    public static final String EVENT_REQUESTRATE_LIMIT = "REQUESTRATE_LIMIT";

    public static final String KEY_IP = "IP";
    public static final String ATTRIBUTE_EVENT = "Event";
    public static final String ATTRIBUTE_MESSAGE = "Message";
    public static final String ATTRIBUTE_ENVIRONMENT = "Environment";
    public static final String ATTRIBUTE_TIMESTAMP = "Timestamp";

    private String IP;
    private String event;
    private String bucket;
    private String message;
    private String environment;
    private Date timestamp = new Date();

    public BlockedIPEvent(String ip, String environment) {
    	this();
        this.environment = environment;
        this.IP = ip;
    };
    
    public BlockedIPEvent() {
   
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
  
}
