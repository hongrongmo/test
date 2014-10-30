package org.ei.evtools.db.services;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.evtools.exception.CSServiceException;

import com.elsevier.edit.common.pool.IPoolableFactory;
import com.elsevier.edit.common.pool.ITSPool;
import com.elsevier.edit.common.pool.PoolException;
import com.elsevier.edit.common.pool.PoolFactory;
import com.elsevier.wsdls.csas.service.v13.CSApplicationServicePortTypeV13;
import com.elsevier.wsdls.csas.service.v13.CSApplicationServiceV13;

/**
 * @author kamaramx
 * @version 1.0
 * 
 * This class holds the proxy pool used for csws access. This class provides
 * access to the pool as a singleton.
 */
final class ANEServiceProxy {

	private final static Logger logger = Logger.getLogger(ANEServiceProxy.class);
	
	public static final String CUSTOMER_SYSTEM_WEB_SERVICE_END_POINT = "/PCSASSvc/CSApplicationService_V13";
	public static final String CUSTOMER_SERVICE_WSDL_PATH = "/PCSASSvc/CSApplicationService_V13/WEB-INF/wsdl/v13/service_v13.wsdl";

	private static ITSPool c_proxyPool = null;

	private ANEServiceProxy() {
	}

	/**
	 * Retrieve the proxy pool.
	 *
	 * @return ITSPool
	 * @throws InfrastructureException
	 */
	public static synchronized ITSPool getProxyPool(String endPointUrl) throws CSServiceException {

		if (c_proxyPool == null) {

			int maxCSWSProxies = Integer.parseInt(CSWebServiceImpl.MAX_POOL_CSWS_PROXIES);

			// int maxCSWSProxies = 0;
			try {
				if (maxCSWSProxies == 0) {
					c_proxyPool = PoolFactory.makeThreadSafePool(new CSWSWebServiceProxyFactory(endPointUrl));
				} else {
					c_proxyPool = PoolFactory.makeThreadSafePool(new CSWSWebServiceProxyFactory(endPointUrl), true, 0, maxCSWSProxies);
				}
			} catch (PoolException pe) {
				logger.error("Unable to create CSWS proxy pool", pe);
				throw new CSServiceException("Unable to create CSWS proxy pool", pe);
			} catch (MalformedURLException mue) {
				logger.error("Unable to create CSWS proxy factory", mue);
				throw new CSServiceException("Unable to create CSWS proxy factory", mue);
			}
		}
		return c_proxyPool;
	}

	private static final class CSWSWebServiceProxyFactory implements IPoolableFactory {
		private URL m_endPoint;
		private String endPointUrl;
		

		CSWSWebServiceProxyFactory(String endPointurl) throws MalformedURLException {
			endPointUrl = endPointurl;
			m_endPoint = new URL(endPointUrl+CUSTOMER_SYSTEM_WEB_SERVICE_END_POINT);

			logger.info("m_endPoint" + m_endPoint.toExternalForm());
			logger.info("Customer System web service URL: " + m_endPoint.toString());
		}

		/**
		 * Create an object instance.
		 *
		 * @return Object
		 */
		public Object makeInstance() {

			CSApplicationServicePortTypeV13 port = null;
			try {
				CSApplicationServiceV13 service;
				String serviceWSDLPath = endPointUrl+CUSTOMER_SERVICE_WSDL_PATH;
				String wsdlVersionNumber = CSWebServiceImpl.WEBSERVICE_VERSION;

				if (StringUtils.isNotBlank(serviceWSDLPath)) {
					service = new CSApplicationServiceV13(new URL(serviceWSDLPath), new QName("http://wsdls.elsevier.com/CSAS/service/v" + wsdlVersionNumber,
							"CSApplicationService_v" + wsdlVersionNumber));
				} else {
					service = new CSApplicationServiceV13();
				}

				port = service.getCSASV13();
				((javax.xml.ws.BindingProvider) port).getRequestContext().put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
						m_endPoint.toExternalForm());

			} catch (MalformedURLException e) {
				logger.info("Unable to create csws proxy", e);
				new CSServiceException("Unable to create csws proxy", e);
			}
			return port;
		}
	}
}
