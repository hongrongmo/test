package org.ei.service.properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.service.properties.DAO.RuntimePropertiesDAO;

public class RuntimePropertiesServiceImpl implements RuntimePropertiesService {
    public static final Logger log4j = Logger.getLogger(RuntimePropertiesServiceImpl.class.getName());

    RuntimePropertiesDAO dao = null;

    public RuntimePropertiesServiceImpl() {
        dao = new RuntimePropertiesDAO();
    }

    public boolean getSSOCoreRedirectFlag() throws RuntimePropertiesServiceException {

        try {
            String ssoCoreRedirectFlag = getDao().getSSOCoreRedirectFlag();
            if (StringUtils.isNotBlank(ssoCoreRedirectFlag)) {
                return Boolean.valueOf(ssoCoreRedirectFlag);
            }
        } catch (Exception e) {
            log4j.error("Error in fetching the SSO CORE REDIRECT FLAG From Database", e);
            if (e.getCause() != null) {
                log4j.error("Caused by: ", e.getCause());
            }
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