package org.ei.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.controller.content.ContentConfig;
import org.ei.domain.HelpLinksCache;

/**
 * Wraps all property access for EV.  Currently the following are included:
 *
 * - RuntimeProperties: properties loaded for runtime, e.g. Fast base URL, SMTP host, etc.
 * - JSPPathProperties: properties loaded for XML transformations (location of JSP returning XML)
 * - ContentConfig:  the configuration file for working with the engvillage data service
 * - Bypass objects: there are "bypass" options to allow direct access to EV from certain IP addresses and/or Customer IDs
 *
 */
public final class EVProperties {
    private static Logger log4j = Logger.getLogger(EVProperties.class);

    private static EVProperties instance;

	public static String NEWLINE = "\n";
	static {
		String nl = System.getProperty("line.separator");
		if (nl != null) NEWLINE = nl;
	}

	private RuntimeProperties runtimeProperties;
	private JSPPathProperties jspPathProperties;
	private ContentConfig contentConfig;

    // IP bypass maps
    private Map<String, String> ipBypass = new HashMap<String, String>();
    private Map<String, String> custBypass = new HashMap<String, String>();

    // HelpLinks cache object
    private HelpLinksCache hCache;
    HelpLinksCache helpLinksCache;
    static List<String> templateNameList= new ArrayList<String>();

    // Time of app startup
    private long startup;

    // Private instance
	private EVProperties() {}

	/**
	 * Return singleton instance
	 * @return
	 */
	public static EVProperties getInstance() {

		if (instance == null) {
			instance = new EVProperties();
		}

		return instance;
	}

	/**
	 * Retrieve a Runtime property
	 * @param key
	 * @return
	 */
	public static String getRuntimeProperty(String key) {
		return getInstance().runtimeProperties.getProperty(key);
	}

    /**
     * Retrieve a JSPPath property
     * @param key
     * @return
     */
	public static String getJSPPath(String key) {
	    String jsppath = instance.jspPathProperties.getProperty(key);
	    String dataurl = getRuntimeProperty(RuntimeProperties.DATA_URL);
		if (GenericValidator.isBlankOrNull(jsppath) || GenericValidator.isBlankOrNull(dataurl)) {
            log4j.warn("Unable to find JSP path for key: " + key);
            return "";
		} else {
            return  dataurl + jsppath;
		}
	}

	/**
	 * Returns the ContentConfig file.  EVProperties MUST be initialized first
	 * @return
	 */
    public static ContentConfig getContentConfig() {
        return instance.contentConfig;
    }

    public static HelpLinksCache getHelpLinksCache() {
        return instance.hCache;
        }

    public static void setHelpLinksCache(HelpLinksCache helpLinksCache) {
		instance.helpLinksCache = helpLinksCache;
    }

	public static void setRuntimeProperties(RuntimeProperties runtimeProperties) {
 		instance.runtimeProperties = runtimeProperties;
	}

	public static RuntimeProperties getRuntimeProperties() {
	    return instance.runtimeProperties;
	}

	public static void setJspPathProperties(JSPPathProperties jspPathProperties) {
		instance.jspPathProperties = jspPathProperties;
	}

	public static JSPPathProperties getJspPathProperties() {
        return instance.jspPathProperties;
    }

	public static void setContentConfig(ContentConfig contentConfig) {
		instance.contentConfig = contentConfig;
	}

	//
	// Bypass properties - IP and Customer
	//
    public Map<String, String> getIpBypass() {
        return ipBypass;
    }

    public Map<String, String> getCustBypass() {
        return custBypass;
    }

    public static long getStartup() {
        return instance.startup;
    }

    public static void setStartup(long startup) {
        instance.startup = startup;
    }


    public static List<String> getCarResponseOverwriteRequiredList(){
    	if(templateNameList.isEmpty()){
	    	String templateNames= getRuntimeProperty(RuntimeProperties.CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS);
	    	StringTokenizer stringTokenizer= new StringTokenizer(templateNames, ",");

	    	while(stringTokenizer.hasMoreTokens()){
	    		templateNameList.add(stringTokenizer.nextToken().trim());
	    	}
	    	return templateNameList;
    	}else{
    		return templateNameList;
    	}
    }
}
