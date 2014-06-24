package org.ei.service.properties;

import org.apache.commons.lang.StringUtils;
import org.ei.service.properties.DAO.RuntimePropertiesDAO;
import org.ei.service.properties.DAO.RuntimePropertiesDAOException;



public class RuntimePropertiesServiceImpl implements RuntimePropertiesService
{
	
	RuntimePropertiesDAO dao = null; 
	
	public RuntimePropertiesServiceImpl(){
		dao= new RuntimePropertiesDAO();
	}

	public boolean getSSOCoreRedirectFlag() throws RuntimePropertiesServiceException{
		
		try {
			String ssoCoreRedirectFlag = getDao().getSSOCoreRedirectFlag();
			if(StringUtils.isNotBlank(ssoCoreRedirectFlag)){
				return Boolean.valueOf(ssoCoreRedirectFlag);
			}
		} catch (RuntimePropertiesDAOException e) {
			throw new RuntimePropertiesServiceException("error in fetching the SSO CORE REDIRECT FALG From Database", e);
		}
		
		return false;
	}
	
	
	public RuntimePropertiesDAO getDao() {
		return dao;
	}

	public void setDao(RuntimePropertiesDAO dao) {
		this.dao = dao;
	}
	
}