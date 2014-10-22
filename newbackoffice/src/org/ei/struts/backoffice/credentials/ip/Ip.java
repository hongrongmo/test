/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/ip/Ip.java-arc   1.3   Apr 01 2009 11:56:44   johna  $
 * $Revision:   1.3  $
 * $Date:   Apr 01 2009 11:56:44  $
 *
 * ====================================================================
 */


package org.ei.struts.backoffice.credentials.ip;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.credentials.Credentials;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.3  $ $Date:   Apr 01 2009 11:56:44  $
 */
public class Ip extends Credentials {

  private static Log log = LogFactory.getLog("Ip");

  private String m_strHighIp = null;
  private String m_strLowIp = null;
  private String m_strIpBlock = null;

  public String getHighIp() { return m_strHighIp ; }
  public void setHighIp(String ipaddress) { m_strHighIp = (ipaddress != null) ? ipaddress.trim() : null; }

  public String getLowIp() { return m_strLowIp ; }
  public void setLowIp(String ipaddress) { m_strLowIp = (ipaddress != null) ? ipaddress.trim() : null; }

  public String getIpBlock() { return m_strIpBlock ; }
  public void setIpBlock(String ipblock) { m_strIpBlock = (ipblock != null) ? ipblock.trim() : null; }

  private Collection m_ipranges = new ArrayList();

  public Collection getIpRanges() { return m_ipranges ; }
  public void setIpRanges(Collection ips) { m_ipranges = ips; }

  public void addIpRange(IpRange iprange) {
    m_ipranges.add(iprange);
  }

  public Map getLinkParameters() {

    Map map = super.getLinkParameters();
    map.put(Tokens.ACCESS, Constants.IP);

    return map;
  }

  public String getType() { return Constants.IP; }



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
      buffer.append("ipBlock").append("='").append(getIpBlock()).append("' ");
      buffer.append("]");

      return buffer.toString();
  }

}
