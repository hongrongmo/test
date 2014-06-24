package org.ei.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;

import com.elsevier.edit.common.pool.IPoolableFactory;
import com.elsevier.edit.common.pool.ITSPool;
import com.elsevier.edit.common.pool.PoolException;
import com.elsevier.edit.common.pool.PoolFactory;
import com.elsevier.webservices.wsdls.metadata.abstracts.service.v10.AbstractsMetadataServicePortTypeV10;
import com.elsevier.webservices.wsdls.metadata.abstracts.service.v10.XAbstractsMetadataServiceV10;

public class XAbstractMDServiceProxy {
	private final static Logger log4j = Logger.getLogger(XAbstractMDServiceProxy.class);

	private static ITSPool c_proxyPool = null;

	private XAbstractMDServiceProxy() {
	}

	/**
	 * Retrieve the proxy pool.
	 *
	 * @return ITSPool
	 * @throws InfrastructureException
	 */
	public static synchronized ITSPool getProxyPool() throws ServiceException {

		if (c_proxyPool == null) {

			int maxXABSMDWSProxies = Integer.parseInt(XAbstractMDServiceConstants.getMaxNumberPoolConnections());// 0

			try {
				if (maxXABSMDWSProxies == 0) {
					c_proxyPool = PoolFactory.makeThreadSafePool(new XAbstractMDServiceProxyFactory());
				} else {
					c_proxyPool = PoolFactory.makeThreadSafePool(new XAbstractMDServiceProxyFactory(), true, 0, maxXABSMDWSProxies);
				}
			} catch (PoolException pe) {
				throw new ServiceException(SystemErrorCodes.XABS_MD_NO_PROXY, "Unable to create XABS MD WS proxy pool", pe);
			} catch (MalformedURLException mue) {
				throw new ServiceException(SystemErrorCodes.XABS_MD_NO_PROXY, "Unable to create XABS MD WS proxy pool", mue);
			}
		}
		return c_proxyPool;
	}

	private static final class XAbstractMDServiceProxyFactory implements IPoolableFactory {
		private URL m_endPoint;

		XAbstractMDServiceProxyFactory() throws MalformedURLException {
			m_endPoint = new URL(XAbstractMDServiceConstants.getXAbstractMDWebServiceEndPoint());

			log4j.info("m_endPoint" + m_endPoint.toExternalForm());
			log4j.info("XAbstract metadata service URL: " + m_endPoint.toString());
		}

		/**
		 * Create an object instance.
		 *
		 * @return Object
		 */
		public Object makeInstance() {

			AbstractsMetadataServicePortTypeV10 port = null;
			try {
				XAbstractsMetadataServiceV10 service;
				String serviceWSDLPath = XAbstractMDServiceConstants.getXAbstractMDWSDLPath();

				service = new XAbstractsMetadataServiceV10(new URL(serviceWSDLPath));

				port = service.getAbstractsMetadataServicePortV10();
				((javax.xml.ws.BindingProvider) port).getRequestContext().put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
						m_endPoint.toExternalForm());

			} catch (MalformedURLException e) {
				log4j.info("Unable to create xabs metadata ws proxy", e);
				new InfrastructureException(SystemErrorCodes.XABS_MD_NO_PROXY, "Unable to create xabs metadata ws proxy", e);
			}
			return port;
		}
	}
}
