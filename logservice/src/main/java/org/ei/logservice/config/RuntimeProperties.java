// All rights reserved.
//
// This software is the confidential and proprietary information

///////////////////////////////////////////////////////////////////////////////
// Revision History Information for this file.
// $Header:   P:/VM/ei-dev/archives/baja/eijava/eilib/src/org/ei/config/RuntimeProperties.java-arc   1.0   May 09 2007 15:33:12   johna  $
// $Project: EI$
// $Revision:   1.0  $
// $Locker:  $
// $Log:
//
///////////////////////////////////////////////////////////////////////////////

package org.ei.logservice.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

/**
 * This class encapsulates the properties class.
 *
 * @author Laxman
 **/

@SuppressWarnings("serial")
public final class RuntimeProperties extends Properties {
    private static Logger log4j = Logger.getLogger(RuntimeProperties.class);
	public static final String SYSTEM_ENVIRONMENT_RUNLEVEL = "com.elsevier.env";

	public static final String RELEASE_VERSION = "release.version";
	public static final String LOGFILE = "logserviceLogFileLocation";

	//
	// Static instance of the RuntimeProperties
	//
	private static RuntimeProperties instance;

	private String runlevel = "";
	private RuntimeProperties() {}

    /**
     * Load from the Runtime.properties file. This file should be located in the
     * build path and picked up through resource loading.
     *
     * @return RuntimeProperties object
     * @throws IOException
     */
    public static synchronized RuntimeProperties getInstance() throws IOException {
        if (instance == null) {
            instance = new RuntimeProperties();
            InputStream is = RuntimeProperties.class.getResourceAsStream("/Runtime.properties");
            init(is);
            instance.runlevel = System.getProperty(SYSTEM_ENVIRONMENT_RUNLEVEL);
        }
        return instance;
    }

	/**
	 * Load properties from a file handle
	 *
	 * @param propsFile
	 * @return RuntimeProperties object
	 * @throws IOException
	 */
	public static synchronized RuntimeProperties getInstance(String propsFile) throws IOException {
		if (instance == null) {
			instance = new RuntimeProperties();
			InputStream is = new BufferedInputStream(new FileInputStream(propsFile));
			init(is);
		}
		return instance;
	}

	/**
	 * This is meant to finish initialization from an InputStream
	 * @param is InputStream
	 * @throws IOException
	 */
	private static void init(InputStream is) throws IOException {
		if (is == null) {
			log4j.error("Unable to load RuntimeProperties resource - please check it is available!");
		}
		instance.load(new BufferedInputStream(is));

		//
		// Print for debug
		//
		if (log4j.isDebugEnabled()) {
			log4j.debug("*************** Runtime properties: ");
			for (Enumeration<?> e = instance.propertyNames(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String val = instance.getProperty(key);
				log4j.debug("    " + key + "=" + val);
			}
		}

        //
        // Try to load release version number from properties
        //
        try {
            String rv = instance.getProperty(RELEASE_VERSION);
            if (GenericValidator.isBlankOrNull(rv)) {
                log4j.warn("***************  RELEASE VERSION NOT FOUND!! ******************");
            }
        } catch (Exception e) {
            log4j.warn("***************  UNABLE TO RETRIEVE RELEASE VERSION!! ******************", e);
        }

	}

	/**
	 * Prints this property list out to the specified output stream. This method
	 * is useful for debugging.
	 *
	 * @param out
	 *            an output stream.
	 */
	public synchronized void list(PrintStream out) {
		if (instance == null) {
			throw new RuntimeException(
					"RuntimeProperties has not been initialized!");
		}
		for (Enumeration<?> e = instance.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String val = instance.getProperty(key);
			out.println(key + "=" + val);
			out.println("");
		}
	}

	/**
	 * Prints this property list out to the specified output stream. This method
	 * is useful for debugging.
	 *
	 * @param out
	 *            an output stream.
	 */
	public synchronized void list(PrintWriter out) {
		for (Enumeration<?> e = propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String val = getProperty(key);
			out.println(key + "=" + val);
			out.println("");
		}
	}

	/**
	 * Override the getProperty method to use the "runlevel"
	 */
    @Override
    public String getProperty(String key) {
        String prop = super.getProperty(key);
        if (!GenericValidator.isBlankOrNull(runlevel)) {
            String runlevelprop = super.getProperty(key + "." + runlevel);
            if (runlevelprop != null) {
                return runlevelprop;
            }
        }
        return prop;
    }

	/**
	 * Calls the hashtable method <code>put</code>. Provided for parallelism
	 * with the getProperties method. Enforces use of strings for property keys
	 * and values. This is a duplicate of the jdk1.2.2 implementaion. It is put
	 * in for compatibility with jdk1.x
	 */
	public synchronized Object setProperty(String key, String value) {
		return put(key, value);
	}

	/**
	 * Returns the system runlevel.  This is usually set at application startup via
	 * "-Drun.level=xxx" where xxx equals 'local', 'cert', 'cert2', 'prod' or 'prod2'
	 * @return
	 */
	public String getRunlevel() {
		return this.runlevel;
	}
}

// END OF FILE : EIProperties.java

