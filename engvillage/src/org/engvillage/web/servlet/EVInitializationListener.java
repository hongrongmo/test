package org.engvillage.web.servlet;

import java.io.IOException;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.domain.DriverConfig;


public class EVInitializationListener implements ServletContextListener {

	private Logger log4j = Logger.getLogger(EVInitializationListener.class);

	private static Boolean initialized = false;
	public static boolean isInitialized() {
	    return initialized;
	}
	
	/**
	 * This is called from the container when the web application starts up.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		log4j.info("Engvillage app starting up. Configuring system...");
		
		// Ensure the "runlevel" property is set
        String runlevel = System.getProperty(RuntimeProperties.SYSTEM_ENVIRONMENT_RUNLEVEL);
        if (GenericValidator.isBlankOrNull(runlevel)) {
            log4j.warn(
                "\n\n*********************************************************************\n" +
                "* UNABLE TO RETRIEVE '" + RuntimeProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "' property!\n" +
                "* DEFAULTING TO 'live'. \n" +
                "* (e.g. -D" + RuntimeProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "=<environment>)\n" +
                "*********************************************************************\n\n");
            runlevel = "live";
        }

		//intialize the EV Properties object
		EVProperties.getInstance();
		EVProperties.setStartup(System.currentTimeMillis());

		try {
		    // Populate various application-specific items
		    log4j.info("Populating RuntimeProperties...");
			populateRuntimeProperties();

			// Initialize log4j
            log4j.info("Init log4j...");
			initLog4j();
			
		} catch (Exception e) {
		    log4j.error("Unable to initialize application items: ", e);
		    return;
		}
		
        //
        // Init the DatabaseConfig instance. Just calling the
        // instance() method will initialize for future calls
        //
        try {
            log4j.info("Initialize DatabaseConfig...");
            DatabaseConfig.getInstance(DriverConfig.getDriverTable());
        } catch (DatabaseConfigException e) {
            log4j.error("Unable to initialize DatabaseConfig: ", e);
            return;
        }

        //
        // If we make it here, mark application as initialized!
        //
        synchronized(initialized) {
            initialized = true;
        }
	}

	/**
	 * Read all *.properties files to initialize application
	 * 
	 * @throws IOException
	 */
	private void populateRuntimeProperties() throws IOException {
		EVProperties.setRuntimeProperties(RuntimeProperties.getInstance());
		RuntimeProperties.startRuntimePropertiesJob();
	
	}

	/**
	 * Use runtime properties to override initial log4j settings
	 */
	private void initLog4j() {
	    Logger rootlogger = Logger.getRootLogger();
	    if (rootlogger == null) {
            log4j.warn("********** Unable to retrieve Root logger! **********");
	        return;
	    }
	    
        // Attempt to get the console logging flag from properties file.  Add console
        // logger if true.
        String rootlogtoconsole = EVProperties.getRuntimeProperty(RuntimeProperties.ROOT_LOG_TO_CONSOLE);
        if (GenericValidator.isBlankOrNull(rootlogtoconsole) || !Boolean.parseBoolean(rootlogtoconsole)) {
            log4j.warn("********** Removing Console Logger per property setting **********");
            rootlogger.removeAppender("CA");
        }
        
        // Attempt to get the ROOT logging level from properties file.  Override
        // current log level if found.
		String rootloglevel = EVProperties.getRuntimeProperty(RuntimeProperties.ROOT_LOG_LEVEL);
		if (!GenericValidator.isBlankOrNull(rootloglevel)) {
		    log4j.warn("********** Changing log level to: " + rootloglevel + " **********");
		    rootlogger.setLevel(Level.toLevel(rootloglevel));
		} else {
            log4j.warn("********** No Root log level override found in runtime properties! **********");
		}
		
	}
	
	/**
	 * This is called from the container when the web application shuts down.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if(RuntimeProperties.timer != null){
			RuntimeProperties.timer.cancel();
		}
	}

}
