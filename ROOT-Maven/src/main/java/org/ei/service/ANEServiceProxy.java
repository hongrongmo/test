package org.ei.service;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;

import com.elsevier.edit.common.pool.IPoolableFactory;
import com.elsevier.edit.common.pool.ITSPool;
import com.elsevier.edit.common.pool.PoolException;
import com.elsevier.edit.common.pool.PoolFactory;
import com.elsevier.wsdls.csas.service.v12.CSApplicationServicePortTypeV12;
import com.elsevier.wsdls.csas.service.v12.CSApplicationServiceV12;

/**
 * This class holds the proxy pool used for csws access. This class provides
 * access to the pool as a singleton.
 */
final class ANEServiceProxy {

	private final static Logger log4j = Logger.getLogger(ANEServiceProxy.class);

	private static ITSPool c_proxyPool = null;

	private ANEServiceProxy() {
	}

	/**
	 * Retrieve the proxy pool.
	 *
	 * @return ITSPool
	 * @throws InfrastructureException
	 */
	public static synchronized ITSPool getProxyPool() throws ServiceException {

		if (c_proxyPool == null) {

			int maxCSWSProxies = Integer.parseInt(ANEServiceConstants.getMaxNumberPoolConnections());

			// int maxCSWSProxies = 0;
			try {
				if (maxCSWSProxies == 0) {
					c_proxyPool = PoolFactory.makeThreadSafePool(new CSWSWebServiceProxyFactory());
				} else {
					c_proxyPool = PoolFactory.makeThreadSafePool(new CSWSWebServiceProxyFactory(), true, 0, maxCSWSProxies);
				}
			} catch (PoolException pe) {
				throw new ServiceException(SystemErrorCodes.CSWS_NO_PROXY, "Unable to create CSWS proxy pool", pe);
			} catch (MalformedURLException mue) {
				throw new ServiceException(SystemErrorCodes.CSWS_NO_PROXY, "Unable to create CSWS proxy factory", mue);
			}
		}
		return c_proxyPool;
	}

	private static final class CSWSWebServiceProxyFactory implements IPoolableFactory {
		private URL m_endPoint;

		CSWSWebServiceProxyFactory() throws MalformedURLException {
			m_endPoint = new URL(ANEServiceConstants.getCustomerSystemWebServiceEndPoint());

			log4j.info("m_endPoint" + m_endPoint.toExternalForm());
			log4j.info("Customer System web service URL: " + m_endPoint.toString());
		}

		/**
		 * Create an object instance.
		 *
		 * @return Object
		 */
		public Object makeInstance() {

			CSApplicationServicePortTypeV12 port = null;
			try {
				CSApplicationServiceV12 service;
				String serviceWSDLPath = ANEServiceConstants.getCustomerServiceWSDLPath();
				String wsdlVersionNumber = ANEServiceConstants.getWebserviceVersion();

				if (StringUtils.isNotBlank(serviceWSDLPath)) {
					service = new CSApplicationServiceV12(new URL(serviceWSDLPath), new QName("http://wsdls.elsevier.com/CSAS/service/v" + wsdlVersionNumber,
							"CSApplicationService_V" + wsdlVersionNumber));
				} else {
					service = new CSApplicationServiceV12();
				}

				port = service.getCSASV12();
				((javax.xml.ws.BindingProvider) port).getRequestContext().put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
						m_endPoint.toExternalForm());

			} catch (MalformedURLException e) {
				log4j.info("Unable to create csws proxy", e);
				new InfrastructureException(SystemErrorCodes.CSWS_NO_PROXY, "Unable to create csws proxy", e);
			}
			return port;
		}
	}
}
