package org.ei.evtools.db.domain;

import java.util.ArrayList;
import java.util.List;

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

	public static final String KEY = "key";
	public static final String ATTRIBUTE_DEFAULT = "default";
    public static final String ATTRIBUTE_LOCAL = "local";
    public static final String ATTRIBUTE_CERT = "cert";
    public static final String ATTRIBUTE_DEV = "dev";
    public static final String ATTRIBUTE_PROD = "prod";
    public static final String ATTRIBUTE_RELEASE = "release";
    
    private String key;
    private String dfault;
    private String local;
    private String cert;
    private String dev;
    private String prod;
    private String release;
    
    private String currentEnv = "";
    
    @DynamoDBHashKey(attributeName = KEY)
    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@DynamoDBAttribute(attributeName = ATTRIBUTE_DEFAULT)
	public String getDfault() {
		return dfault;
	}

	public void setDfault(String dfault) {
		this.dfault = dfault;
	}

	@DynamoDBAttribute(attributeName = ATTRIBUTE_LOCAL)
	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	@DynamoDBAttribute(attributeName = ATTRIBUTE_CERT)
	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

	@DynamoDBAttribute(attributeName = ATTRIBUTE_DEV)
	public String getDev() {
		return dev;
	}

	public void setDev(String dev) {
		this.dev = dev;
	}

	@DynamoDBAttribute(attributeName = ATTRIBUTE_PROD)
	public String getProd() {
		return prod;
	}

	public void setProd(String prod) {
		this.prod = prod;
	}

	@DynamoDBAttribute(attributeName = ATTRIBUTE_RELEASE)
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
	
	@DynamoDBIgnore
    public static List<String> getEnvRunLevels(){
    	List<String> envrunlevellist = new ArrayList<String>();
        envrunlevellist.add(ATTRIBUTE_CERT);
        envrunlevellist.add(ATTRIBUTE_DEV);
        envrunlevellist.add(ATTRIBUTE_LOCAL);
        envrunlevellist.add(ATTRIBUTE_PROD);
        envrunlevellist.add(ATTRIBUTE_RELEASE);
        return envrunlevellist;
    }
}
