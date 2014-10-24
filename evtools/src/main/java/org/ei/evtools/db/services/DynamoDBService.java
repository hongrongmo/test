package org.ei.evtools.db.services;

import java.util.List;

import org.ei.evtools.db.domain.BlockedIPEvent;
import org.ei.evtools.db.domain.RunTimeProperties;
import org.ei.evtools.exception.AWSAccessException;
import org.ei.evtools.utils.EVToolsUtils.TimePeriod;
import org.ei.evtools.db.domain.BlockedIPStatus;


/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public interface DynamoDBService {
	 
	 public RunTimeProperties load(String key) throws AWSAccessException;
	 public void save(RunTimeProperties runTimeProperties)  throws AWSAccessException;
	 public List<RunTimeProperties> getDefaultAndCurrentEnvironmentProps(String environment) throws AWSAccessException;
	 
	 public BlockedIPStatus loadBlockedIPStatus(String ip, String environment) throws AWSAccessException;
	 public void addBlockedIP(String ip, String environment) throws AWSAccessException;
	 public List<BlockedIPEvent> getBlockedIPEvents(String ip, String environment) throws AWSAccessException;
	 public void deleteBlockedIP(BlockedIPStatus blockedIPStatus) throws AWSAccessException;
	 public void updateBlockedIP(BlockedIPStatus blockedIPStatus) throws AWSAccessException;
	 public List<BlockedIPStatus> getByStatus(String status, String environment) throws AWSAccessException;
	 public List<BlockedIPEvent> getByIP(String IP, String environment) throws AWSAccessException;
	 public List<BlockedIPEvent> getByTimePeriod(String IP, String environment, TimePeriod t) throws AWSAccessException;

}
