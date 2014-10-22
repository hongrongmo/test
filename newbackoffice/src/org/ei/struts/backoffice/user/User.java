package org.ei.struts.backoffice.user;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;

public final class User implements Serializable {

  private static Log log = LogFactory.getLog(User.class);

  private String m_strSalesRepId = null;
  private String m_strName = null;
  private String m_strEmail = null;
  private String m_strPhone = null;
  private String m_strUsername = null;
  private String m_strPassword = null;
  private int m_intEnabled = Constants.ENABLED;

  private String[] m_aryRoles = new String[]{"0"};
  private String[] m_aryRegions = new String[]{"0"};

  // ----------------------------------------------------------- Properties

  public String getSalesRepId() { return m_strSalesRepId; }
  public void setSalesRepId(String id) { this.m_strSalesRepId = id; }

  public String getName () { return m_strName; }
  public void setName (String name) { this.m_strName = name; }

  public String getEmail() { return m_strEmail; }
  public void setEmail(String email) { this.m_strEmail = email; }

  public String getPhone () { return m_strPhone; }
  public void setPhone(String phone) { this.m_strPhone = phone; }

  public String getUsername() { return m_strUsername; }
  public void setUsername(String username) { this.m_strUsername = username; }

  public String getPassword() { return m_strPassword; }
  public void setPassword(String password) { this.m_strPassword = password; }

  public int getIsEnabled() { return m_intEnabled ; }
  public void setIsEnabled(int enabled) { this.m_intEnabled = enabled; }

	// -----------------------------------
	public String[] getRoles() { return m_aryRoles; }
	public void setRoles(String[] roles) { this.m_aryRoles = roles; }

	public String[] getRegions () { return m_aryRegions; }
	public void setRegions(String[] regions) { this.m_aryRegions = regions; }

	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();
		strBufObjectValue.append(m_strSalesRepId);
    strBufObjectValue.append(":Access=");
		strBufObjectValue.append(m_intEnabled);
    strBufObjectValue.append(":");
		strBufObjectValue.append(m_strUsername);
    strBufObjectValue.append(":");
		strBufObjectValue.append(m_strName);
    strBufObjectValue.append(":");
		strBufObjectValue.append(m_strEmail);
    strBufObjectValue.append(":");
		strBufObjectValue.append(m_strPhone);

		if( m_aryRoles != null) {
		  strBufObjectValue.append(":Roles=");
			for(int idx = 0; idx < m_aryRoles.length; idx++) {
				strBufObjectValue.append(m_aryRoles[idx]);
				if(idx + 1 < m_aryRoles.length)
				{
				  strBufObjectValue.append(",");
				}
			}
		}
		if( m_aryRegions != null) {
		  strBufObjectValue.append(":Regions=");
			for(int idx = 0; idx < m_aryRegions.length; idx++) {
				strBufObjectValue.append(m_aryRegions[idx]);
				if(idx + 1 < m_aryRoles.length)
				{
				  strBufObjectValue.append(",");
				}
			}
		}

		return strBufObjectValue.toString();
	}
}