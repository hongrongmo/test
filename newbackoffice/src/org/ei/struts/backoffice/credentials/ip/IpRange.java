/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/ip/IpRange.java-arc   1.0   Mar 18 2009 13:23:56   johna  $
 * $Revision:   1.0  $
 * $Date:   Mar 18 2009 13:23:56  $
 *
 * ====================================================================
 */
package org.ei.struts.backoffice.credentials.ip;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.credentials.Credentials;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Mar 18 2009 13:23:56  $
 */
public class IpRange {

  private static Log log = LogFactory.getLog("IpRange");

	private String m_strIndexID = null;

	public String getIndexID() { return m_strIndexID; }
	public void setIndexID(String indexid) { m_strIndexID = indexid; }

  private String m_strHighIp = null;
  private String m_strLowIp = null;

  public String getHighIp() { return m_strHighIp ; }
  public void setHighIp(String highip) { m_strHighIp = highip; }

  public String getLowIp() { return m_strLowIp ; }
  public void setLowIp(String lowip) { m_strLowIp = lowip; }

  public String getDisplayString() {

    StringBuffer strBuf = new StringBuffer();
    if((getLowIp() != null) && (getHighIp() != null))
    {
      strBuf.append(Constants.formatIpAddress(getLowIp()));
      strBuf.append(" - ");
      strBuf.append(Constants.formatIpAddress(getHighIp()));
    }
    return strBuf.toString();
  }

  public String toString() {
      StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("lowIP").append("='").append(getLowIp()).append("' ");
      buffer.append("highIP").append("='").append(getHighIp()).append("' ");
      buffer.append("]");

      return buffer.toString();
  }

}
