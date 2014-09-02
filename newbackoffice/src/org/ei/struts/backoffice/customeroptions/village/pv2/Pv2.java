/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/village/pv2/Pv2.java-arc   1.0   Jan 14 2008 17:10:58   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:58  $
 *
 */
package org.ei.struts.backoffice.customeroptions.village.pv2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.village.Village;

public final class Pv2 extends Village {

	private Log log = LogFactory.getLog("Pv2");

	public String getProduct() {
		return Constants.PV2;
	}

	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();
		strBufObjectValue.append(super.toString());
		return strBufObjectValue.toString();
	}

}