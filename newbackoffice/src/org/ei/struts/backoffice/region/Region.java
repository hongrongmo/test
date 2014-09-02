package org.ei.struts.backoffice.region;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Region {

	private static Log log = LogFactory.getLog("Region");

	private String m_strRegionID = null;
	private String m_strRegionName = null;

	public String getRegionID() { return m_strRegionID; }
	public void setRegionID(String regionid) { m_strRegionID = regionid; }

	public String getRegionName() { return m_strRegionName; }
	public void setRegionName(String regionname) { m_strRegionName = regionname; }
}
