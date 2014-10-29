package org.ei.evtools.db.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@DynamoDBTable(tableName = "BlockedIPStatus")
public class BlockedIPStatus {
    
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
       
    };

    public BlockedIPStatus(String IP, String environment) {
        this();
        this.IP = IP;
        this.environment = environment;
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
    public void addAccount(Account account) {
        if (account != null) {
            this.setAccountID(account.getAccountId());
            this.setAccountName(account.getAccountName());
            this.setAccountNumber(account.getAccountNumber());
            this.setDepartmentName(account.getDepartmentName());
            this.setDepartmentNumber(account.getDepartmentId());
        }
    }
    
    /**
     * Return blocked status
     *
     * @return
     */
    @DynamoDBIgnore
    public boolean isBlocked() {
        if (!StringUtils.isNotBlank(this.status))
            return false;
        return (BlockedIPStatus.STATUS_BLOCKED.equals(this.status));
    }
}
