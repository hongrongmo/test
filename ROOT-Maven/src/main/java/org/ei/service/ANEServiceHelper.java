package org.ei.service;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;

import com.elsevier.edit.common.pool.ITSPool;
import com.elsevier.edit.common.pool.PoolException;
import com.elsevier.webservices.schemas.csas.types.v13.PlatformInfoType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.LogLevelType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.RequestHeaderType;
import com.elsevier.wsdls.csas.service.v13.CSApplicationServicePortTypeV13;

/**
 * This class holds the proxy pool used for csws access. This class provides
 * access to the pool as a singleton.
 */
public final class ANEServiceHelper {

	private final static Logger log4j = Logger.getLogger(ANEServiceHelper.class);
	private static ITSPool aneWSProxyPool = null;

	public static CSApplicationServicePortTypeV13 getANEService() throws ServiceException {
		log4j.info("Getting proxy pool for ANE service...");
		CSApplicationServicePortTypeV13 port = null;
		aneWSProxyPool = ANEServiceProxy.getProxyPool();

		try {
			port = (CSApplicationServicePortTypeV13) aneWSProxyPool.get(0);
		} catch (PoolException e) {
			throw new ServiceException(SystemErrorCodes.CSWS_NO_PROXY, "error in creating proxy for ANE service", e);
		}
		return port;
	}

	public static void releasePort(CSApplicationServicePortTypeV13 port) {
		aneWSProxyPool.release(port);
	}

	public static RequestHeaderType getRequestHeaderHolder() {
		RequestHeaderType reqHeader = new RequestHeaderType();

		reqHeader.setConsumer(ANEServiceConstants.getConsumerApp());
		reqHeader.setConsumerClient(ANEServiceConstants.getConsumerClient());// ENGVIL
		reqHeader.setLogLevel(LogLevelType.fromValue(ANEServiceConstants.getWebserviceLogLevel()));// Default

		/*
		 * reqHeader.setConsumer("ENGVIL");
		 * reqHeader.setConsumerClient("ENGVIL");//ENGVIL
		 * reqHeader.setLogLevel(LogLevelType.fromValue("Default"));//Default
		 */
		String ipAddress;
		String transId;
		String transGroupId;
		ipAddress = ANEServiceConstants.NOT_AVAILABLE;
		transId = ANEServiceConstants.NOT_AVAILABLE;
		transGroupId = ANEServiceConstants.NOT_AVAILABLE;
		reqHeader.setOpaqueInfo("Clone=" + ANEServiceConstants.CLONE_HOST_NAME + ";IP=" + ipAddress + ";transGroupId=" + transGroupId);
		reqHeader.setReqId(transId);

		reqHeader.setVer(ANEServiceConstants.getWebserviceVersion());// 10
		// reqHeader.setVer("10");//10
		return reqHeader;
	}

	public static PlatformInfoType getEVPlatformInfo() {
		PlatformInfoType platformInfo = new PlatformInfoType();
		platformInfo.setPlatformCode(ANEServiceConstants.getPlatformCode());
		platformInfo.setSiteIdentifier(ANEServiceConstants.getSiteIdentifier());
		return platformInfo;
	}

}
