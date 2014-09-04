/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/Credentials.java-arc   1.1   Mar 17 2009 15:31:36   johna  $
 * $Revision:   1.1  $
 * $Date:   Mar 17 2009 15:31:36  $
 *
 * ====================================================================
 */


package org.ei.struts.backoffice.credentials;

import java.util.HashMap;
import java.util.Map;

import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.credentials.access.Access;


/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.1  $ $Date:   Mar 17 2009 15:31:36  $
 */
public abstract class Credentials {

	private String m_strIndexID = null;
	private Access m_access = new Access();

	public String getIndexID() { return m_strIndexID; }
	public void setIndexID(String indexid) { m_strIndexID= indexid; }

	public Access getAccess() { return this.m_access;	}
	public void setAccess(Access access) { this.m_access = access; }

	public abstract String getType();
	public abstract String getDisplayString();

	public Map getLinkParameters() {

		Map map = new HashMap();
		map.put(Tokens.INDEXID, getIndexID());
		map.put(Tokens.CONTRACTID, getAccess().getContractID());
		// This was slowing things down immensely on page load when listing access information
		// since it called to the database! - It wasn;t even needed
		//map.put(Tokens.CUSTOMERID, getAccess().getContract().getCustomerID());
		map.put(Tokens.ITEMID, getAccess().getItemID());

		return map;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" Index ID : " + getIndexID());
		sb.append(" Access : " + m_access.toString());
		sb.append(" Credentials: " + getDisplayString());
		return sb.toString();
	}

}
