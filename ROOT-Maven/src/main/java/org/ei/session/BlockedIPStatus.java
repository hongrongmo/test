/**
 *
 */
package org.ei.session;

import java.util.Date;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.RuntimeProperties;
import org.ei.service.amazon.AmazonServiceHelper;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

/**
 * @author harovetm
 *
 */
@DynamoDBTable(tableName = "BlockedIPStatus")
public class BlockedIPStatus {
    private static final Logger log4j = Logger.getLogger(BlockedIPStatus.class);

    public static final String STATUS_BLOCKED = "BLOCKED";
    public static final String STATUS_UNBLOCKED = "UNBLOCKED";
    public static final String STATUS_WHITELIST = "WHITELIST";
    public static final String STATUS_ANY = "ANY";

    public static final String KEY_IP = "IP";
    public static final String ATTRIBUTE_STATUS = "Status";
    public static final String ATTRIBUTE_ENVIRONMENT = "Environment";
    public static final String ATTRIBUTE_TIMESTAMP = "Timestamp";
    public static final String ATTRIBUTE_ACCOUNTID = "AccountID";
    public static final String ATTRIBUTE_ACCOUNTNAME = "AccountName";
    public static final String ATTRIBUTE_ACCOUNTNUMBER = "AccountNumber";
    public static final String ATTRIBUTE_DEPARTMENTNAME = "DepartmentName";
    public static final String ATTRIBUTE_DEPARTMENTNUMBER = "DepartmentNumber";

    private static final DynamoDBMapperConfig saveconfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER);

    private String IP;
    private String status = STATUS_UNBLOCKED;
    private String environment;
    private String accountid;
    private String accountname;
    private String accountnumber;
    private String departmentname;
    private String departmentnumber;
    private Date timestamp = new Date();
    private Integer version;

    public BlockedIPStatus() {
        try {
            this.environment = RuntimeProperties.getInstance().getRunlevel();
        } catch (Throwable t) {
            this.environment = "prod";
        }
    };

    public BlockedIPStatus(String IP) {
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

    @DynamoDBAttribute(attributeName = ATTRIBUTE_STATUS)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBRangeKey(attributeName = ATTRIBUTE_ENVIRONMENT)
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_ACCOUNTID)
    public String getAccountID() {
        return accountid;
    }

    public void setAccountID(String accountid) {
        this.accountid = accountid;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_ACCOUNTNAME)
    public String getAccountName() {
        return accountname;
    }

    public void setAccountName(String accountname) {
        this.accountname = accountname;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_ACCOUNTNUMBER)
    public String getAccountNumber() {
        return accountnumber;
    }

    public void setAccountNumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_DEPARTMENTNAME)
    public String getDepartmentName() {
        return departmentname;
    }

    public void setDepartmentName(String departmentname) {
        this.departmentname = departmentname;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_DEPARTMENTNUMBER)
    public String getDepartmentNumber() {
        return departmentnumber;
    }

    public void setDepartmentNumber(String departmentnumber) {
        this.departmentnumber = departmentnumber;
    }

    @DynamoDBAttribute(attributeName = "Timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBVersionAttribute
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    @DynamoDBIgnore
    public void addAccount(org.ei.domain.personalization.cars.Account account) {
        if (account != null) {
            this.setAccountID(account.getAccountId());
            this.setAccountName(account.getAccountName());
            this.setAccountNumber(account.getAccountNumber());
            this.setDepartmentName(account.getDepartmentName());
            this.setDepartmentNumber(account.getDepartmentId());
        }
    }

    @DynamoDBIgnore
    private static String getCurrentEnvironment() {
        // Set then environment from current runtime properties
        String environment = "prod";
        try {
            environment = RuntimeProperties.getInstance().getRunlevel();
        } catch (Throwable t) {
            log4j.error("Unable to retrieve RuntimeProperties.SYSTEM_ENVIRONMENT_RUNLEVEL", t);
            environment = "prod";
        }
        return environment;
    }

    /**
     * Retrieve status by IP and environment
     * @param status
     * @param environment
     * @return
     */
    public static List<BlockedIPStatus> getByStatus(String status, String environment) {
        // Get DynamoDB client
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

        // Add Status to scan filter if appropriate
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        if (!GenericValidator.isBlankOrNull(status) && !(STATUS_ANY.equals(status))) {
            scanExpression.addFilterCondition("Status",
                new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(status)));
        }

        // Add Environment to scan filter
        scanExpression.addFilterCondition("Environment",
            new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(environment)));

        PaginatedScanList<BlockedIPStatus> scanlist = mapper.scan(BlockedIPStatus.class, scanExpression);
        return scanlist;
    }

    /**
     * Retrieve status by IP
     *
     * @param IP
     * @return
     */
    public static List<BlockedIPStatus> getByStatus(String status) {
        return getByStatus(status, getCurrentEnvironment());
    }

    /**
     * Retrieve status by IP
     *
     * @param IP
     * @return
     */
    public static BlockedIPStatus load(String ip, String environment) {
        // Get DynamoDB client
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        return mapper.load(BlockedIPStatus.class, ip, environment);
    }

    /**
     * Retrieve status by IP
     *
     * @param IP
     * @return
     */
    public static BlockedIPStatus load(String ip) {
        return load(ip, getCurrentEnvironment());
    }

    /**
     * Save the blocking status of an IP address
     *
     * @param ipstatus
     */
    public void save() {
        // Get DynamoDB client
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

        mapper.save(this, saveconfig);
    }

    /**
     * Delete the blocking status of an IP address
     *
     * @param ipstatus
     */
    public void delete() {
        this.delete(true);
    }

    /**
     * Return blocked status
     *
     * @return
     */
    @DynamoDBIgnore
    public boolean isBlocked() {
        if (GenericValidator.isBlankOrNull(this.status))
            return false;
        return (BlockedIPStatus.STATUS_BLOCKED.equals(this.status));
    }

    /**
     * Delete the blocking status of an IP address
     *
     * @param ipstatus
     */
    public void delete(boolean withevents) {
        // Get DynamoDB client
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        mapper.delete(this, saveconfig);
        if (withevents) {
            BlockedIPEvent.deleteByIP(this.getIP());
        }
    }

}
