/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/village/Village.java-arc   1.0   Jan 14 2008 17:10:56   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:56  $
 *
 */
package org.ei.struts.backoffice.customeroptions.village;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.customeroptions.Options;

public abstract class Village extends Options {

	private Log log = LogFactory.getLog("Village");
	private String m_product = null;
	protected String OPTION_SEPARATOR = ";";

	private String[] m_selectedOptions = new String[] {};
    private String[] m_selectedDefaultDatabases = new String[] {};

	public String[] getSelectedOptions() {
		return (this.m_selectedOptions);
	}
	public void setSelectedOptions(String[] selectedOptions) {
		this.m_selectedOptions = selectedOptions;
	}

	public String[] getSelectedDefaultDatabases() {
		return (this.m_selectedDefaultDatabases);
	}
	public void setSelectedDefaultDatabases(String[] selecteddbs) {
		this.m_selectedDefaultDatabases = selecteddbs;
	}


	// ---------------------------------------------------------
	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();

		strBufObjectValue.append(super.toString());
		strBufObjectValue.append(" Selected Options: [").append(joinOptions());
		strBufObjectValue.append(" ]");
		return strBufObjectValue.toString();
	}

	public String joinOptions() {
		StringBuffer strBufObjectValue = new StringBuffer();
		String[] strArray = getSelectedOptions();
 
		for(int x = 0; x < strArray.length; x++) {
			if(x > 0) {
				strBufObjectValue.append(OPTION_SEPARATOR);
			}
			strBufObjectValue.append(strArray[x]);
		}
		return strBufObjectValue.toString();
	}

}