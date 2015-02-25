package org.ei.stripes;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ei.ane.fences.PlatformFencesServiceImpl;
import org.ei.ane.template.ANETemplatesServiceImpl;
import org.ei.cache.EVCache;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.config.JSPPathProperties;
import org.ei.controller.MemcachedUtil;
import org.ei.controller.content.ContentConfig;
import org.ei.controller.logging.LogException;
import org.ei.domain.ClassNodeManager;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.domain.DriverConfig;
import org.ei.domain.HelpLinksCache;
import org.ei.exception.ServiceException;
import org.ei.service.amazon.AmazonServiceHelper;
import org.ei.session.SessionManager;
import org.ei.stripes.exception.EVExceptionHandler;


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
				log4j.info("Config service starting up. Configuring system...");

		// Ensure the "runlevel" property is set
        String runlevel = System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL);
        if (GenericValidator.isBlankOrNull(runlevel)) {
            log4j.error(
                "\n\n*********************************************************************\n" +
                "* UNABLE TO RETRIEVE '" + ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "' property!\n" +
                "* THIS MUST BE INITIALIZED VIA TOMCAT STARTUP PARAM \n" +
                "* (e.g. -D" + ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "=<environment>)\n" +
                "*********************************************************************\n\n");
            log4j.error("Unable to initialize - property '" + ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "' is not set!");
            return;
        }

	        //intialize the EV Properties object
	        EVProperties.getInstance();
	        EVProperties.setStartup(System.currentTimeMillis());

		try {

		    // Populate various application-specific items
            log4j.info("Populating JspPathProperties...");
			populateJspPathProperties();
            log4j.info("Populating ContentConfig...");
			populateContentConfig();
            log4j.info("Populating HelpLinksList...");
			populateHelpLinksList();
			log4j.info("Initializing ClassNodeManager");
			ClassNodeManager.init(EVProperties.getApplicationProperties());
            // Initialize log4j
            log4j.info("Init log4j...");
            initLog4j();

            log4j.warn("*******************  EVInitializationListener has configured system.  " + EVProperties.NEWLINE +
                "    Environment     = '" + EVProperties.getApplicationProperties().getRunlevel() + "'"+ EVProperties.NEWLINE +
                "    Release version = '" + EVProperties.getProperty(ApplicationProperties.RELEASE_VERSION) + "'"+ EVProperties.NEWLINE +
                "    Log level       = '" + EVProperties.getProperty(ApplicationProperties.ROOT_LOG_LEVEL) + "'");

		} catch (Exception e) {
		    log4j.error("Unable to initialize application items: ", e);
		    return;
		}

		try {
			// Initialize the MemCache object
			String memcacheservers = EVProperties.getApplicationProperties().getProperty(EVProperties.MEMCACHE_SERVERS);
			if (GenericValidator.isBlankOrNull(memcacheservers)) {
				throw new RuntimeException("The '" + EVProperties.MEMCACHE_SERVERS + "' application property must be set!");
			}
            log4j.info("Initializing Memcached servers...");
            MemcachedUtil.initialize(memcacheservers);
        } catch (Exception e) {
            log4j.error("Unable to initialize EV Cache: ", e);
            return;
        }

		try {
			// Initialize the EVCache
            log4j.info("Populating EVCache...");
			populateEVCache(event);
        } catch (Exception e) {
            log4j.error("Unable to initialize EV Cache: ", e);
            return;
        }

		try {
			// Initialize the SessionManager
            log4j.info("Initializing SessionManager...");
			SessionManager.init();
        } catch (Exception e) {
            log4j.error("Unable to initialize SessionManager: ", e);
            return;
        }

		try {
			// Initialize CARS Template and Text Zone data
            log4j.info("Initialize CarsTemplateCache...");
			intializeCarsTemplatesCache();
        } catch (Exception e) {
            log4j.error("Unable to initialize CARS templates: ", e);
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
        // Initialize the logging service (usage)
        //
        try {
            String logurl = EVProperties.getProperty(ApplicationProperties.LOG_URL);
            log4j.info("Initializing Logger,  = '" + logurl + "'");
            org.ei.controller.logging.Logger.init(logurl);
        } catch (LogException e) {
            log4j.error("Unable to initialize Logging service: ", e);
            return;
        }

        //
        // Initialize CAPTCHA bypass - should be replaced by new Fence!!
        //
        initBypass();

        //
        // If we make it here, mark application as initialized!
        //
        synchronized(initialized) {
            initialized = true;
        }
	}

	/**
	 * Initialize the list of Help Links
	 */
	private void populateHelpLinksList() {
        EVProperties.setHelpLinksCache(HelpLinksCache.getInstance());

	}

	/**
	 * Populate the ContentConfig object from the XML file
	 */
	private void populateContentConfig() {
		try {
			EVProperties.setContentConfig(ContentConfig.getInstance( this.getClass().getResource("/ContentConfig.xml").getFile()));
		} catch (Exception e) {
			EVExceptionHandler.logException("Unable to initialize from EVInitializationListener!", e);
			throw new RuntimeException("Unable to initialize from EVInitializationListener!");
		}

	}

	/**
	 * Populate CARS templates
	 * @throws ServiceException
	 */
	private void intializeCarsTemplatesCache() throws ServiceException {
		ANETemplatesServiceImpl carsTemplateService= new ANETemplatesServiceImpl();
		carsTemplateService.populateCacheWithCarsTemplateAndTextZones();
	}

	/**
	 * Read all properties for JSP Paths
	 * @throws IOException
	 */
	private void populateJspPathProperties() throws IOException {

		// Load JSPPath.properties file from /resources

		JSPPathProperties jspPathProperties = new JSPPathProperties();
		jspPathProperties.load(this.getClass().getResourceAsStream("/JSPPath.properties"));
		EVProperties.setJspPathProperties(jspPathProperties);
	}

	/**
	 * Populate the EVCache object
	 * @param event
	 * @return
	 * @throws ServiceException
	 */
	private void populateEVCache(ServletContextEvent event) throws ServiceException {
		EVCache.getInstance(this.getClass().getResourceAsStream("/ehcache.xml"));

		// Try to retrieve and store the Platform Fences
		PlatformFencesServiceImpl p= new PlatformFencesServiceImpl();
		p.populatePlatformFences();
	}

	/**
	 * Initialize Bypass for CAPTCHA
	 * TODO deprecated for A&E - should be replaced by A&E Fence!
	 */
	@Deprecated
    public void initBypass() {
        //
        // Initialize the IP bypass filters
        //
        log4j.info("Initializing IP bypass filters");
        Map<String, String> ipbypass = EVProperties.getInstance().getIpBypass();
        /* App calling itself */
        ipbypass.put("127.0.0.1", "y");
        /* Refworks */
        ipbypass.put("207.158.24.2", "y");
        ipbypass.put("207.158.24.3", "y");
        ipbypass.put("207.158.24.4", "y");
        ipbypass.put("207.158.24.5", "y");
        ipbypass.put("207.158.24.6", "y");
        ipbypass.put("207.158.24.7", "y");
        ipbypass.put("207.158.24.8", "y");
        ipbypass.put("207.158.24.10", "y");
        ipbypass.put("207.158.24.11", "y");
        ipbypass.put("207.158.24.12", "y");
        ipbypass.put("207.158.24.13", "y");
        ipbypass.put("207.158.24.14", "y");
        ipbypass.put("207.158.24.15", "y");
        ipbypass.put("207.158.24.16", "y");
        ipbypass.put("207.158.24.17", "y");
        ipbypass.put("207.158.24.18", "y");
        ipbypass.put("207.158.24.19", "y");
        ipbypass.put("207.158.24.20", "y");
        ipbypass.put("207.158.24.21", "y");
        ipbypass.put("207.158.24.22", "y");
        ipbypass.put("207.158.24.23", "y");
        ipbypass.put("207.158.24.24", "y");
        ipbypass.put("207.158.24.25", "y");
        ipbypass.put("207.158.24.26", "y");
        ipbypass.put("207.158.24.27", "y");
        ipbypass.put("207.158.24.28", "y");
        ipbypass.put("207.158.24.29", "y");
        ipbypass.put("207.158.24.30", "y");
        ipbypass.put("142.150.190.94", "y");
        ipbypass.put("142.150.190.95", "y");
        ipbypass.put("142.150.190.96", "y");
        ipbypass.put("142.150.190.97", "y");
        ipbypass.put("142.150.190.98", "y");
        ipbypass.put("142.150.190.99", "y");
        ipbypass.put("142.150.190.17", "y");

        /* Web of Science, calling for IBM */
        ipbypass.put("84.18.184.16", "y");
        ipbypass.put("170.107.188.20", "y");
        /* Univ. of Illinois */
        ipbypass.put("128.174.36.130", "y");
        /* UCLA */
        ipbypass.put("216.147.218.158", "y");

        //
        // Initialize the Customer bypass filters
        //
        log4j.info("Initializing Customer bypass filters");
        Map<String, String> custbypass = EVProperties.getInstance().getIpBypass();
        custbypass.put("826", "y");
        custbypass.put("1002198", "y");
        custbypass.put("34764", "y");
        custbypass.put("1001861", "y");
        custbypass.put("21014237", "y");

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
        String rootlogtoconsole = EVProperties.getProperty(ApplicationProperties.ROOT_LOG_TO_CONSOLE);
        if (GenericValidator.isBlankOrNull(rootlogtoconsole) || !Boolean.parseBoolean(rootlogtoconsole)) {
            log4j.warn("********** Removing Console Logger per property setting **********");
            rootlogger.removeAppender("CA");
        }

        // Attempt to get the ROOT logging level from properties file.  Override
        // current log level if found.
		String rootloglevel = EVProperties.getProperty(ApplicationProperties.ROOT_LOG_LEVEL);
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

		//
		// Shutdown the logging service
		//
		org.ei.controller.logging.Logger logger = org.ei.controller.logging.Logger.getInstance();
		if (logger != null) logger.shutdown();

		if(EVProperties.timer != null){
			EVProperties.timer.cancel();
		}

		// Shutdown memcached
        try {
            MemcachedUtil.getInstance().shutdown();
        } catch (Throwable t) {
            log4j.error("Unable to shutdown memcached client", t);
        }
        
        // Shutdown AWS services
        AmazonServiceHelper.getInstance().shutdown();
	}

}
