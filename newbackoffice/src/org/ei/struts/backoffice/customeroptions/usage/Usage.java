/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/usage/Usage.java-arc   1.0   Jan 14 2008 17:10:54   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:54  $
 *
 */
package org.ei.struts.backoffice.customeroptions.usage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.Options;

public final class Usage extends Options {

	private Log log = LogFactory.getLog("Usage");

	public String getProduct() {
		return Constants.USAGE_ID;
	}

	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();

		strBufObjectValue.append(super.toString());
		strBufObjectValue.append(",").append(this.getProduct());

		return strBufObjectValue.toString();

	}
}