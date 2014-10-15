package org.ei.system;

import java.util.ArrayList;
import java.util.List;

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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;

/**
 * @author kamaramx
 * This class is domain representation for runtimeproperties table from dynamo db
 * Helps us to update/delete the values for particular environment attribute
 * 
 *
 */
@DynamoDBTable(tableName = "runtimeproperties")
public class RuntimePropertiesEntity {
	
	private static final Logger log4j = Logger.getLogger(RuntimePropertiesEntity.class);
	
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
    	
    // This configuration for Updating an item and not recreating it again  
    private static final DynamoDBMapperConfig saveconfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE);
    
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
	
	
	/**
	 * @return
	 */
	@DynamoDBIgnore
    public static String getCurrentEnvironment() {
        // Set then environment from current runtime properties
        //String environment = ApplicationProperties.RUNLEVEL_PROD;
		String environment = ApplicationProperties.RUNLEVEL_CERT;
        try {
            environment = EVProperties.getApplicationProperties().getRunlevel();
        } catch (Throwable t) {
            log4j.error("Unable to retrieve ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL", t);
            //environment = ApplicationProperties.RUNLEVEL_PROD;
            environment = ApplicationProperties.RUNLEVEL_CERT;
        }
        return environment;
    }
	
	/**
	 * @param environment
	 * @return
	 */
	@DynamoDBIgnore
	public static List<RuntimePropertiesEntity> getDefaultAndCurrentEnvironmentProps(String environment) {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<RuntimePropertiesEntity> scanlist = mapper.scan(RuntimePropertiesEntity.class, scanExpression);
        if(scanlist != null && !scanlist.isEmpty()){
        	for(RuntimePropertiesEntity object : scanlist){
        		if(environment.equalsIgnoreCase(ATTRIBUTE_CERT)){
        			object.setCurrentEnv(object.getCert());
        		}else if(environment.equalsIgnoreCase(ATTRIBUTE_DEV)){
        			object.setCurrentEnv(object.getDev());
        		}else if(environment.equalsIgnoreCase(ATTRIBUTE_LOCAL)){
        			object.setCurrentEnv(object.getLocal());
        		}else if(environment.equalsIgnoreCase(ATTRIBUTE_PROD)){
        			object.setCurrentEnv(object.getProd());
        		}else if(environment.equalsIgnoreCase(ATTRIBUTE_RELEASE)){
        			object.setCurrentEnv(object.getRelease());
        		}
        	}
        }
        return scanlist;
    }
	
	// Update the return list whenever add/remove the run levels
    // This method does not include DEFAULT(since it is not a actual run level)
	/**
	 * @return
	 */
	@DynamoDBIgnore
    public static List<String> getEnvRunLevels(){
    	List<String> envrunlevellist = new ArrayList<String>();
        envrunlevellist.add(ATTRIBUTE_CERT);
        envrunlevellist.add(ATTRIBUTE_DEV);
        envrunlevellist.add(ATTRIBUTE_LOCAL);
        //envrunlevellist.add(ATTRIBUTE_PROD);
        envrunlevellist.add(ATTRIBUTE_RELEASE);
        return envrunlevellist;
    }
    
	/**
	 * @param key
	 * @return
	 */
	@DynamoDBIgnore
    public static RuntimePropertiesEntity load(String key) {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        return mapper.load(RuntimePropertiesEntity.class, key);
    }
    
	/**
	 * 
	 */
	@DynamoDBIgnore
    public void save() {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        mapper.save(this, saveconfig);
    }


	
}
