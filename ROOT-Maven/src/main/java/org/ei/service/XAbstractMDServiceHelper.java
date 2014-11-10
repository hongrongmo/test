package org.ei.service;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;

import com.elsevier.edit.common.pool.ITSPool;
import com.elsevier.edit.common.pool.PoolException;
import com.elsevier.webservices.schemas.easi.headers.types.v1.LogLevelType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.RequestHeaderType;
import com.elsevier.webservices.wsdls.metadata.abstracts.service.v10.AbstractsMetadataServicePortTypeV10;

public class XAbstractMDServiceHelper {
	private final static Logger log4j = Logger.getLogger(XAbstractMDServiceHelper.class);
	private static ITSPool xabsMDWSProxyPool = null;

	public static AbstractsMetadataServicePortTypeV10 getXABSMDService() throws ServiceException {
		log4j.info("Getting proxy pool for XABS MD service...");
		AbstractsMetadataServicePortTypeV10 port = null;
		xabsMDWSProxyPool = XAbstractMDServiceProxy.getProxyPool();

		try {
			port = (AbstractsMetadataServicePortTypeV10) xabsMDWSProxyPool.get(0);
		} catch (PoolException e) {
			throw new ServiceException(SystemErrorCodes.XABS_MD_NO_PROXY, "error in creating proxy for XABS MD service", e);
		}
		return port;
	}

	public static RequestHeaderType getRequestHeaderHolder() {
		RequestHeaderType reqHeader = new RequestHeaderType();

		reqHeader.setConsumer(XAbstractMDServiceConstants.getConsumerApp());// ENGVIL
		reqHeader.setConsumerClient(XAbstractMDServiceConstants.getConsumerClient());// ENGVIL
		reqHeader.setLogLevel(LogLevelType.fromValue(XAbstractMDServiceConstants.getWebserviceLogLevel()));// Default

		String ipAddress;
		String transId;
		String transGroupId;
		ipAddress = XAbstractMDServiceConstants.NOT_AVAILABLE;
		transId = XAbstractMDServiceConstants.NOT_AVAILABLE;
		transGroupId = XAbstractMDServiceConstants.NOT_AVAILABLE;
		reqHeader.setOpaqueInfo("Clone=" + XAbstractMDServiceConstants.CLONE_HOST_NAME + ";IP=" + ipAddress + ";transGroupId=" + transGroupId);
		reqHeader.setReqId(transId);

		reqHeader.setVer(XAbstractMDServiceConstants.getWebserviceVersion());// 10
		return reqHeader;
	}

	public static void releasePort(AbstractsMetadataServicePortTypeV10 port) {
		xabsMDWSProxyPool.release(port);
	}
}
