package org.ei.evtools.db.services;

import java.util.List;

import org.ei.evtools.db.domain.RunTimeProperties;
import org.ei.evtools.exception.AWSAccessException;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public interface RunTimePropertiesService {
	 public RunTimeProperties load(String key) throws AWSAccessException;
	 public void save(RunTimeProperties runTimeProperties)  throws AWSAccessException;
	 public List<RunTimeProperties> getDefaultAndCurrentEnvironmentProps(String environment) throws AWSAccessException;
}
