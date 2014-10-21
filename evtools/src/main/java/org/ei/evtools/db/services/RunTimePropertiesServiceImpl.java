package org.ei.evtools.db.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.ei.evtools.db.domain.RunTimeProperties;
import org.ei.evtools.exception.AWSAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Service("RunTimePropertiesService")
public class RunTimePropertiesServiceImpl implements RunTimePropertiesService {

	private static Logger logger = Logger.getLogger(RunTimePropertiesServiceImpl.class);
	
	@Autowired
	AmazonDynamoDBClient amazonDynamoDBClient;
	
	private static final DynamoDBMapperConfig saveconfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE);

	@Override
	public RunTimeProperties load(String key)  throws AWSAccessException{
		try{
			DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
	        return mapper.load(RunTimeProperties.class, key);
		}catch(Exception e){
			logger.error("AWS access exception thrown for loading dynamo db item '"+key+"' -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown for loading dynamo db item '"+key+"' -------"+e.getMessage(),  e);
		}
	}

	@Override
	public void save(RunTimeProperties runTimeProperties)  throws AWSAccessException{
		try{
	    	DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
		    mapper.save(runTimeProperties, saveconfig);
		}catch(Exception e){
			logger.error("AWS access exception thrown for saving dynamo db item '"+runTimeProperties.getKey()+"' -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown for saving dynamo db item '"+runTimeProperties.getKey()+"' -------"+e.getMessage(),  e);
		}
	}

	@Override
	public List<RunTimeProperties> getDefaultAndCurrentEnvironmentProps(
			String environment)  throws AWSAccessException{
		try{
			DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
	        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
	        PaginatedScanList<RunTimeProperties> scanlist = mapper.scan(RunTimeProperties.class, scanExpression);
	        if(scanlist != null && !scanlist.isEmpty()){
	        	for(RunTimeProperties object : scanlist){
	        		if(environment.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_CERT)){
	        			object.setCurrentEnv(object.getCert());
	        		}else if(environment.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_DEV)){
	        			object.setCurrentEnv(object.getDev());
	        		}else if(environment.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_LOCAL)){
	        			object.setCurrentEnv(object.getLocal());
	        		}else if(environment.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_PROD)){
	        			object.setCurrentEnv(object.getProd());
	        		}else if(environment.equalsIgnoreCase(RunTimeProperties.ATTRIBUTE_RELEASE)){
	        			object.setCurrentEnv(object.getRelease());
	        		}
	        	}
	        }
	        return scanlist;
		}catch(Exception e){
			logger.error("AWS access exception thrown while fetching the results from the dynamodb table 'runtimeproperties' -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown while fetching the results from the dynamodb table 'runtimeproperties' -------"+e.getMessage(),  e);
		}
	}
}
