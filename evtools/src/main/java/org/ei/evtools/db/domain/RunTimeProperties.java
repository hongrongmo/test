package org.ei.evtools.db.domain;

import org.ei.evtools.utils.EVToolsConstants;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@DynamoDBTable(tableName = "runtimeproperties")
public class RunTimeProperties {

	private String key;
    private String dfault;
    private String local;
    private String cert;
    private String dev;
    private String prod;
    private String release;
    
    private String currentEnv = "";
    
    @DynamoDBHashKey(attributeName = EVToolsConstants.KEY)
    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@DynamoDBAttribute(attributeName = EVToolsConstants.ATTRIBUTE_DEFAULT)
	public String getDfault() {
		return dfault;
	}

	public void setDfault(String dfault) {
		this.dfault = dfault;
	}

	@DynamoDBAttribute(attributeName = EVToolsConstants.ATTRIBUTE_LOCAL)
	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	@DynamoDBAttribute(attributeName = EVToolsConstants.ATTRIBUTE_CERT)
	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

	@DynamoDBAttribute(attributeName = EVToolsConstants.ATTRIBUTE_DEV)
	public String getDev() {
		return dev;
	}

	public void setDev(String dev) {
		this.dev = dev;
	}

	@DynamoDBAttribute(attributeName = EVToolsConstants.ATTRIBUTE_PROD)
	public String getProd() {
		return prod;
	}

	public void setProd(String prod) {
		this.prod = prod;
	}

	@DynamoDBAttribute(attributeName = EVToolsConstants.ATTRIBUTE_RELEASE)
	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}
	
	public void setCurrentEnv(String currentEnv) {
		this.currentEnv = currentEnv;
	}
	
	@DynamoDBIgnore
	public String getCurrentEnvValue() {
		return this.currentEnv;
	}
	
	
}
