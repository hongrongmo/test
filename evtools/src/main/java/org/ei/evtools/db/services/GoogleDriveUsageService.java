package org.ei.evtools.db.services;

import java.util.Date;
import java.util.Map;

import org.ei.evtools.exception.DatabaseAccessException;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public interface GoogleDriveUsageService {
	
	long getTotalCount() throws DatabaseAccessException;
	
	Map<String, String>  getUsageData(String usageOption,Date startDate,Date endDate) throws DatabaseAccessException;
}
