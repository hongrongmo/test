/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/role/Role.java-arc   1.0   Jan 14 2008 17:11:04   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:11:04  $
 *
 */
package org.ei.struts.backoffice.role;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Role {

  private static Log log = LogFactory.getLog("Role");

	public Role() {

	}

	private String m_strRoleID = null;
	private String m_strRoleName = null;

	public String getRoleName() { return m_strRoleName; }
	public void setRoleName(String rolename) { m_strRoleName = rolename; }

	public String getRoleID() { return m_strRoleID; }
	public void setRoleID(String roleid) { m_strRoleID = roleid; }

	public String toString() {
		StringBuffer strBufObject = new StringBuffer();
		strBufObject.append(m_strRoleName);
		strBufObject.append(m_strRoleID);

		return strBufObject.toString();
	}
}
