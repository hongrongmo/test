/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/village/enc2/Enc2.java-arc   1.0   Jan 14 2008 17:10:58   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:58  $
 *
 */
package org.ei.struts.backoffice.customeroptions.village.enc2;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.village.Village;

public final class Enc2 extends Village {

	private Log log = LogFactory.getLog(Enc2.class);

	private String startCID = null;
	private String referenceServicesLink = null;
	private String defaultDatabase = null;
	
	private String[] litbulletins = new String[] {};
	public String[] getLitbulletins() {
		return (this.litbulletins);
	}
	public void setLitbulletins(String[] litbulletins) {
		this.litbulletins = litbulletins;
	}

	private String[] patbulletins = new String[] {};
	public String[] getPatbulletins() {
		return (this.patbulletins);
	}
	public void setPatbulletins(String[] patbulletins) {
		this.patbulletins = patbulletins;
	}

	public String joinOptions() 
	{
		StringBuffer opts = new StringBuffer();
		opts.append(super.joinOptions());
		
		String[] strArray = getLitbulletins();
		if(strArray != null && strArray.length > 0)
		{
			if(opts.length() != 0)
			{
				opts.append(OPTION_SEPARATOR);
			}
			for(int x = 0; x < strArray.length; x++) {
				if(x > 0) {
					opts.append(OPTION_SEPARATOR);
				}
				opts.append(strArray[x]);
			}
		}

		strArray = getPatbulletins();
		if(strArray != null && strArray.length > 0)
		{
			if(opts.length() != 0)
			{
				opts.append(OPTION_SEPARATOR);
			}
			for(int x = 0; x < strArray.length; x++) {
				if(x > 0) {
					opts.append(OPTION_SEPARATOR);
				}
				opts.append(strArray[x]);
			}
		}

		return opts.toString();
	}
 
	public String getProduct() {
		return Constants.ENC2;
	}

	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();
		strBufObjectValue.append(super.toString());
		strBufObjectValue.append(", Lit Bulletins: ");
		strBufObjectValue.append(Arrays.asList(getLitbulletins()));
		strBufObjectValue.append(", Pat Bulletins: ");
		strBufObjectValue.append(Arrays.asList(getPatbulletins()));
		return strBufObjectValue.toString();
	}

	/**
	 * @return
	 */
	public String getStartCID() {
		return startCID;
	}

	/**
	 * @param string
	 */
	public void setStartCID(String string) {
		startCID = string;
	}

	/**
	 * @return
	 */
	public String getReferenceServicesLink() {
		return referenceServicesLink;
	}

	/**
	 * @param string
	 */
	public void setReferenceServicesLink(String string) {
		referenceServicesLink = string;
	}

	/**
	 * @return
	 */
	public String getDefaultDatabase() {
		return defaultDatabase;
	}

	/**
	 * @param string
	 */
	public void setDefaultDatabase(String string) {
		defaultDatabase = string;
	}

}