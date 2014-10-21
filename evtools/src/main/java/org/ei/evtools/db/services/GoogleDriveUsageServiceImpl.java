package org.ei.evtools.db.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;
import org.ei.evtools.exception.DatabaseAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Service("GoogleDriveUsageService")
@Repository
public class GoogleDriveUsageServiceImpl implements GoogleDriveUsageService {
	
	private static Logger logger = Logger.getLogger(GoogleDriveUsageServiceImpl.class);

	private final static String queryByDownloadFormat = "select goolgeDriveUsage.format AS KEY_NAME, count(goolgeDriveUsage.format) AS DOWNLOAD_COUNT FROM GoogleDriveUsage "
				+ "AS goolgeDriveUsage GROUP BY goolgeDriveUsage.format ORDER BY goolgeDriveUsage.format ASC";
	
	private final static String queryByIP = "select goolgeDriveUsage.ip AS KEY_NAME, count(goolgeDriveUsage.ip) AS DOWNLOAD_COUNT FROM GoogleDriveUsage "
			+ "AS goolgeDriveUsage GROUP BY goolgeDriveUsage.ip ORDER BY goolgeDriveUsage.ip ASC";
	
	private final static String queryByAcctNo = "select goolgeDriveUsage.acctno AS KEY_NAME, count(goolgeDriveUsage.acctno) AS DOWNLOAD_COUNT FROM GoogleDriveUsage "
			+ "AS goolgeDriveUsage GROUP BY goolgeDriveUsage.acctno ORDER BY goolgeDriveUsage.acctno ASC";
	
	private final static String queryByDownloadFormatWithDate = "select goolgeDriveUsage.format AS KEY_NAME, count(goolgeDriveUsage.format) AS DOWNLOAD_COUNT FROM GoogleDriveUsage "
			+ "AS goolgeDriveUsage WHERE goolgeDriveUsage.timestamp BETWEEN :startDate AND :endDate GROUP BY goolgeDriveUsage.format ORDER BY goolgeDriveUsage.format ASC";
	
	private final static String queryByIPWithDate = "select goolgeDriveUsage.ip AS KEY_NAME, count(goolgeDriveUsage.ip) AS DOWNLOAD_COUNT FROM GoogleDriveUsage "
			+ "AS goolgeDriveUsage WHERE goolgeDriveUsage.timestamp BETWEEN :startDate AND :endDate  GROUP BY goolgeDriveUsage.ip ORDER BY goolgeDriveUsage.ip ASC";
	
	private final static String queryByAcctNoWithDate = "select goolgeDriveUsage.acctno AS KEY_NAME, count(goolgeDriveUsage.acctno) AS DOWNLOAD_COUNT FROM GoogleDriveUsage "
			+ "AS goolgeDriveUsage WHERE goolgeDriveUsage.timestamp BETWEEN :startDate AND :endDate GROUP BY goolgeDriveUsage.acctno ORDER BY goolgeDriveUsage.acctno ASC";
	
	private final static String totalCountQuery = "select count(*) from GoogleDriveUsage goolgeDriveUsage";
	
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
    	return entityManager;
    }


	@PersistenceContext
    public void setEntityManager(EntityManager em) {
		this.entityManager = em;
    }

	@Transactional(readOnly = true)
	public long getTotalCount () throws DatabaseAccessException {
		long count =  0;
		try{
			Query query = entityManager.createQuery(totalCountQuery);
			count = (Long) query.getSingleResult();
		}catch(Exception e){
			logger.error("Error occurred while fetching the total count of google drive usage, exception ----"+e.getMessage());
			throw new DatabaseAccessException("Database fetch failed from goolgeDriveUsage-------"+e.getMessage(),  e);
		}
		return count;
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Map<String, String>  getUsageData(String usageOption,
			Date startDate, Date endDate) throws DatabaseAccessException {
		try{
			Map<String, String>  map = new HashMap<String, String>();
			List<Object[]> results = null;
			if(startDate != null && endDate != null){
				
				results = entityManager.createQuery(constructJqlQuery(usageOption,true))
							.setParameter("startDate", startDate, TemporalType.DATE)  
							.setParameter("endDate", endDate, TemporalType.DATE).getResultList();
			}else{
				results = entityManager.createQuery(constructJqlQuery(usageOption,false))
							.getResultList();
			}
			if(results != null && results.size()>0){
				for (Object[] result : results) {
				    String name = (String) result[0];
				    int count = ((Number) result[1]).intValue();
				    map.put(name, count+"");
				    
				}
			}
			return map;
		}catch(Exception e){
			logger.error("Error occurred while fetching the google drive usage, exception ----"+e.getMessage());
			throw new DatabaseAccessException("Database fetch failed from goolgeDriveUsage-------"+e.getMessage(),  e);
		}
	}
	
	private String  constructJqlQuery(String usageOption,boolean includeDate){
		String jql = null;
		if(usageOption.equalsIgnoreCase("downloadformat")){
			if(includeDate){
				return GoogleDriveUsageServiceImpl.queryByDownloadFormatWithDate;
			}else{
				return GoogleDriveUsageServiceImpl.queryByDownloadFormat;
			}
		}else if(usageOption.equalsIgnoreCase("ip")){
			if(includeDate){
				return GoogleDriveUsageServiceImpl.queryByIPWithDate;
			}else{
				return GoogleDriveUsageServiceImpl.queryByIP;
			}
		}else if(usageOption.equalsIgnoreCase("acctNo")){
			if(includeDate){
				return GoogleDriveUsageServiceImpl.queryByAcctNoWithDate;
			}else{
				return GoogleDriveUsageServiceImpl.queryByAcctNo;
			}
		}
		return jql;
	}

}
