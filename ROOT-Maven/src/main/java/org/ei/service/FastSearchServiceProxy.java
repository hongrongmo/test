package org.ei.service;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;

import com.elsevier.edit.common.pool.IPoolableFactory;
import com.elsevier.edit.common.pool.ITSPool;
import com.elsevier.edit.common.pool.PoolException;
import com.elsevier.edit.common.pool.PoolFactory;
import com.elsevier.webservices.wsdls.search.fast.service.v4.FASTSearchServiceV4;
import com.elsevier.webservices.wsdls.search.fast.service.v4.FastSearchServicePortTypeV4;

public class FastSearchServiceProxy {

	private final static Logger log4j = Logger.getLogger(FastSearchServiceProxy.class);

	private static ITSPool c_proxyPool = null;

	private FastSearchServiceProxy() {
	}

	/**
	 * Retrieve the proxy pool.
	 *
	 * @return ITSPool
	 * @throws InfrastructureException
	 */
	public static synchronized ITSPool getProxyPool() throws ServiceException {

		if (c_proxyPool == null) {

			int maxFSWSProxies = Integer.parseInt(FastSearchServiceConstants.getMaxNumberPoolConnections());// 0

			try {
				if (maxFSWSProxies == 0) {
					c_proxyPool = PoolFactory.makeThreadSafePool(new FastSearchWebServiceProxyFactory());
				} else {
					c_proxyPool = PoolFactory.makeThreadSafePool(new FastSearchWebServiceProxyFactory(), true, 0, maxFSWSProxies);
				}
			} catch (PoolException pe) {
				throw new ServiceException(SystemErrorCodes.FSWS_NO_PROXY, "Unable to create FSWS proxy pool", pe);
			} catch (MalformedURLException mue) {
				throw new ServiceException(SystemErrorCodes.FSWS_NO_PROXY, "Unable to create FSWS proxy factory", mue);
			}
		}
		return c_proxyPool;
	}

	private static final class FastSearchWebServiceProxyFactory implements IPoolableFactory {
		private URL m_endPoint;

		FastSearchWebServiceProxyFactory() throws MalformedURLException {
			m_endPoint = new URL(FastSearchServiceConstants.getFastSearchWebServiceEndPoint());

			log4j.info("m_endPoint" + m_endPoint.toExternalForm());
			log4j.info("Fast search service URL: " + m_endPoint.toString());
		}

		/**
		 * Create an object instance.
		 *
		 * @return Object
		 */
		public Object makeInstance() {

			FastSearchServicePortTypeV4 port = null;
			try {
				FASTSearchServiceV4 service;
				String serviceWSDLPath = FastSearchServiceConstants.getFastSearchWSDLPath();
				String wsdlVersionNumber = FastSearchServiceConstants.getWebserviceVersion();

				if (StringUtils.isNotBlank(serviceWSDLPath)) {
					service = new FASTSearchServiceV4(new URL(serviceWSDLPath), new QName("http://webservices.elsevier.com/wsdls/search/fast/service/v" + wsdlVersionNumber,
							"FASTSearchServiceV" + wsdlVersionNumber));
				} else {
					service = new FASTSearchServiceV4();
				}

				port = service.getFASTSearchServicePortV4();
				((javax.xml.ws.BindingProvider) port).getRequestContext().put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
						m_endPoint.toExternalForm());

			} catch (MalformedURLException e) {
				log4j.info("Unable to create fsws proxy", e);
				new InfrastructureException(SystemErrorCodes.FSWS_NO_PROXY, "Unable to create fsws proxy", e);
			}
			return port;
		}
	}
}
