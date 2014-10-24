package org.ei.evtools.db.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.evtools.db.domain.BlockedIPEvent;
import org.ei.evtools.db.domain.BlockedIPStatus;
import org.ei.evtools.db.domain.RunTimeProperties;
import org.ei.evtools.exception.AWSAccessException;
import org.ei.evtools.utils.EVToolsConstants;
import org.ei.evtools.utils.EVToolsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Service("DynamoDBService")
public class DynamoDBServiceImpl implements DynamoDBService {

	private static Logger logger = Logger.getLogger(DynamoDBServiceImpl.class);
	
    @Autowired
	AmazonDynamoDBClient amazonDynamoDBClient;
	
	@Autowired
	CSWebService csWebService;
	
	private static final DynamoDBMapperConfig updatesaveconfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE);
	private static final DynamoDBMapperConfig clobbersaveconfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER);

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
		    mapper.save(runTimeProperties, updatesaveconfig);
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
	        		if(environment.equalsIgnoreCase(EVToolsConstants.ATTRIBUTE_CERT)){
	        			object.setCurrentEnv(object.getCert());
	        		}else if(environment.equalsIgnoreCase(EVToolsConstants.ATTRIBUTE_DEV)){
	        			object.setCurrentEnv(object.getDev());
	        		}else if(environment.equalsIgnoreCase(EVToolsConstants.ATTRIBUTE_LOCAL)){
	        			object.setCurrentEnv(object.getLocal());
	        		}else if(environment.equalsIgnoreCase(EVToolsConstants.ATTRIBUTE_PROD)){
	        			object.setCurrentEnv(object.getProd());
	        		}else if(environment.equalsIgnoreCase(EVToolsConstants.ATTRIBUTE_RELEASE)){
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

	@Override
	public BlockedIPStatus loadBlockedIPStatus(String ip, String environment)
			throws AWSAccessException {
		try{
			DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
			return mapper.load(BlockedIPStatus.class, ip, environment);
		}catch(Exception e){
			logger.error("AWS access exception thrown while fetching the blocked ip status for ip= "+ip+" , environment= "+environment+" -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown while fetching the blocked ip status for ip= "+ip+" , environment= "+environment+" -------"+e.getMessage(),  e);
		}
	}

	@Override
	public void addBlockedIP(String ip, String environment)
			throws AWSAccessException {
		try{
			DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
			BlockedIPStatus ipstatus = new BlockedIPStatus(ip,environment);
			ipstatus.addAccount(csWebService.getAccountInfo(ip));
			mapper.save(ipstatus,clobbersaveconfig);
		}catch(Exception e){
			logger.error("AWS access exception thrown while adding the blocked ip status for ip= "+ip+" , environment= "+environment+" -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown while adding the blocked ip status for ip= "+ip+" , environment= "+environment+" -------"+e.getMessage(),  e);
		}
	}

	@Override
	public List<BlockedIPEvent> getBlockedIPEvents(String ip, String environment)
			throws AWSAccessException {
		try{
			DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);

	        Condition rangeKeyCondition = new Condition()
	            .withComparisonOperator(ComparisonOperator.GT.toString())
	            .withAttributeValueList(new AttributeValue().withS( EVToolsUtils.TimePeriod.LASTYEAR.toString()));

	        BlockedIPEvent blockedipevent = new BlockedIPEvent(ip,environment);
	        DynamoDBQueryExpression<BlockedIPEvent> queryExpression = new DynamoDBQueryExpression<BlockedIPEvent>()
	            .withHashKeyValues(blockedipevent)
	            .withRangeKeyCondition(BlockedIPEvent.ATTRIBUTE_TIMESTAMP, rangeKeyCondition).withScanIndexForward(false);

	        return mapper.query(BlockedIPEvent.class, queryExpression);
		}catch(Exception e){
			logger.error("AWS access exception thrown while fetching events for the blocked ip = "+ip+" , environment= "+environment+" -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown while fetching events for the blocked ip = "+ip+" , environment= "+environment+" -------"+e.getMessage(),  e);
		}
	}

	@Override
	public void deleteBlockedIP(BlockedIPStatus blockedIPStatus)
			throws AWSAccessException {
		try{
			DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
	        mapper.delete(blockedIPStatus, clobbersaveconfig);
	        mapper.batchDelete(getByIP(blockedIPStatus.getIP(), blockedIPStatus.getEnvironment()));
	       
		}catch(Exception e){
			logger.error("AWS access exception thrown while deleting the blocked ip status for ip= "+blockedIPStatus.getIP()+" , environment= "+ blockedIPStatus.getEnvironment()+" -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown while deleting the blocked ip status for ip= "+blockedIPStatus.getIP()+" , environment= "+ blockedIPStatus.getEnvironment()+" -------"+e.getMessage(),  e);
		}
	}

	@Override
	public void updateBlockedIP(BlockedIPStatus blockedIPStatus) throws AWSAccessException {
		
		try{
			DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
	        mapper.save(blockedIPStatus, clobbersaveconfig);
	    }catch(Exception e){
			logger.error("AWS access exception thrown while updating the blocked ip status for ip= "+blockedIPStatus.getIP()+" , environment= "+ blockedIPStatus.getEnvironment()+" -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown while updating the blocked ip status for ip= "+blockedIPStatus.getIP()+" , environment= "+ blockedIPStatus.getEnvironment()+" -------"+e.getMessage(),  e);
		}
	}
	
	@Override
	public List<BlockedIPEvent> getByIP(String IP, String environment) throws AWSAccessException{
	       DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
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

	@Override
	public List<BlockedIPStatus> getByStatus(String status, String environment)
			throws AWSAccessException {
		try{
			DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);

	        // Add Status to scan filter if appropriate
	        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
	        if (!StringUtils.isNotBlank(status) && !(BlockedIPStatus.STATUS_ANY.equals(status))) {
	            scanExpression.addFilterCondition("Status",
	                new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(status)));
	        }

	        // Add Environment to scan filter
	        scanExpression.addFilterCondition("Environment",
	            new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(environment)));

	        PaginatedScanList<BlockedIPStatus> scanlist = mapper.scan(BlockedIPStatus.class, scanExpression);
	        return scanlist;
	    }catch(Exception e){
			logger.error("AWS access exception thrown while fetching blocked ips list for the environment="+environment+" -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown while fetching blocked ips list for the environment="+environment+" -------"+e.getMessage(),  e);
		}
	}
	
	@Override
	public List<BlockedIPEvent> getByTimePeriod(String IP, String environment, EVToolsUtils.TimePeriod t) throws AWSAccessException{
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
        try{
        	
        	Condition rangeKeyCondition = new Condition()
	            .withComparisonOperator(ComparisonOperator.GT.toString())
	            .withAttributeValueList(new AttributeValue().withS(t.toString()));
	        BlockedIPEvent blockedipevent = new BlockedIPEvent(IP,environment);
	        DynamoDBQueryExpression<BlockedIPEvent> queryExpression = new DynamoDBQueryExpression<BlockedIPEvent>()
	                .withHashKeyValues(blockedipevent)
	                .withQueryFilterEntry(BlockedIPEvent.ATTRIBUTE_ENVIRONMENT, new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(environment)))
	    	        .withRangeKeyCondition(BlockedIPEvent.ATTRIBUTE_TIMESTAMP, rangeKeyCondition).withScanIndexForward(false);

	        return mapper.query(BlockedIPEvent.class, queryExpression);
	    }catch(Exception e){
        	logger.error("AWS access exception thrown while fetching blocked ips history for the environment="+environment+" -------"+e.getMessage());
			throw new AWSAccessException("AWS access exception thrown while fetching blocked ips history for the environment="+environment+" -------"+e.getMessage(),  e);
        }
    }
	
	
}
