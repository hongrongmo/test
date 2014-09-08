/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/gateway/Gateway.java-arc   1.1   Apr 01 2009 10:53:56   johna  $
 * $Revision:   1.1  $
 * $Date:   Apr 01 2009 10:53:56  $
 *
 * ====================================================================
 */


package org.ei.struts.backoffice.credentials.gateway;


import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.credentials.Credentials;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.1  $ $Date:   Apr 01 2009 10:53:56  $
 */
public class Gateway extends Credentials {

	private static Log log = LogFactory.getLog("Gateway");

	private String m_strGatewayUrl = null;

	public String getGatewayUrl() { return m_strGatewayUrl ; }
	public void setGatewayUrl(String gateway) { m_strGatewayUrl = (gateway != null) ? gateway.trim() : null; }

	public Map getLinkParameters() {

		Map map = super.getLinkParameters();
		map.put(Tokens.ACCESS, Constants.GATEWAY);

		return map;
	}
	public String getType() { return Constants.GATEWAY; }

	public String getDisplayString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(getGatewayUrl());
		return strBuf.toString();
	}




}
