/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/village/cv/Cv.java-arc   1.0   Jan 14 2008 17:10:56   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:56  $
 *
 */
package org.ei.struts.backoffice.customeroptions.village.cv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.village.Village;

public final class Cv extends Village {

	private Log log = LogFactory.getLog("Cv");

	public String getProduct() {
		return Constants.CV;
	}

	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();
		strBufObjectValue.append(super.toString());
		return strBufObjectValue.toString();
	}

}