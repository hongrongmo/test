package org.ei.service;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;

import com.elsevier.edit.common.pool.ITSPool;
import com.elsevier.edit.common.pool.PoolException;
import com.elsevier.webservices.schemas.easi.headers.types.v1.LogLevelType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.RequestHeaderType;
import com.elsevier.webservices.wsdls.search.fast.service.v4.FastSearchServicePortTypeV4;

public class FastSearchServiceHelper {

	private final static Logger log4j = Logger.getLogger(FastSearchServiceHelper.class);
	private static ITSPool fastWSProxyPool = null;

	public static FastSearchServicePortTypeV4 getFastSearchService() throws ServiceException {
		log4j.info("Getting proxy pool for Fast Search service...");
		FastSearchServicePortTypeV4 port = null;
		fastWSProxyPool = FastSearchServiceProxy.getProxyPool();

		try {
			port = (FastSearchServicePortTypeV4) fastWSProxyPool.get(0);
		} catch (PoolException e) {
			throw new ServiceException(SystemErrorCodes.FSWS_NO_PROXY, "error in creating proxy for fast search service", e);
		}
		return port;
	}

	public static RequestHeaderType getRequestHeaderHolder() {
		RequestHeaderType reqHeader = new RequestHeaderType();

		reqHeader.setConsumer(FastSearchServiceConstants.getConsumerApp());
		reqHeader.setConsumerClient(FastSearchServiceConstants.getConsumerClient());
		reqHeader.setLogLevel(LogLevelType.fromValue(FastSearchServiceConstants.getWebserviceLogLevel()));

		String ipAddress;
		String transId;
		String transGroupId;
		ipAddress = FastSearchServiceConstants.NOT_AVAILABLE;
		transId = FastSearchServiceConstants.NOT_AVAILABLE;
		transGroupId = FastSearchServiceConstants.NOT_AVAILABLE;
		reqHeader.setOpaqueInfo("Clone=" + FastSearchServiceConstants.CLONE_HOST_NAME + ";IP=" + ipAddress + ";transGroupId=" + transGroupId);
		reqHeader.setReqId(transId);

		reqHeader.setVer(ANEServiceConstants.getWebserviceVersion());
		return reqHeader;
	}

	public static void releasePort(FastSearchServicePortTypeV4 port) {
		fastWSProxyPool.release(port);
	}

}
