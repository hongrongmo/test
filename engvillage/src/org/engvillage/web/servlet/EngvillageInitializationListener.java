package org.engvillage.web.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.domain.ClassNodeManager;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.domain.DriverConfig;
import org.ei.exception.ServiceException;
import org.engvillage.config.RuntimeProperties;
import org.engvillage.service.amazon.dynamodb.RuntimePropsDynamoDBServiceImpl;


public class EngvillageInitializationListener implements ServletContextListener {

	private static final Logger log4j = Logger.getLogger(EngvillageInitializationListener.class);

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
        String runlevel = System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL);
        if (GenericValidator.isBlankOrNull(runlevel)) {
            log4j.warn(
                "\n\n*********************************************************************\n" +
                "* UNABLE TO RETRIEVE '" + ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "' property!\n" +
                "* DEFAULTING TO 'live'. \n" +
                "* (e.g. -D" + ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL + "=<environment>)\n" +
                "*********************************************************************\n\n");
            runlevel = "live";
        }
        ApplicationProperties.getInstance().setProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL, runlevel);

		try {
		    // Populate various application-specific items
		    log4j.info("Populating ApplicationProperties...");
			populateApplicationProperties();

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
	private void populateApplicationProperties() throws IOException {
		startApplicationPropertiesJob();
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

	    ApplicationProperties runtimeproperties = ApplicationProperties.getInstance();
        // Attempt to get the console logging flag from properties file.  Add console
        // logger if true.
        String rootlogtoconsole = runtimeproperties.getProperty(ApplicationProperties.ROOT_LOG_TO_CONSOLE);
        if (GenericValidator.isBlankOrNull(rootlogtoconsole) || !Boolean.parseBoolean(rootlogtoconsole)) {
            log4j.warn("********** Removing Console Logger per property setting **********");
            rootlogger.removeAppender("CA");
        }

        // Attempt to get the ROOT logging level from properties file.  Override
        // current log level if found.
		String rootloglevel = runtimeproperties.getProperty(ApplicationProperties.ROOT_LOG_LEVEL);
		if (!GenericValidator.isBlankOrNull(rootloglevel)) {
		    log4j.warn("********** Changing log level to: " + rootloglevel + " **********");
		    rootlogger.setLevel(Level.toLevel(rootloglevel));
		} else {
            log4j.warn("********** No Root log level override found in runtime properties! **********");
		}

	}

    /** The timer. */
    public  static Timer timer = null;

    /**
	 * This is called from the container when the web application shuts down.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if(timer != null){
			timer.cancel();
		}
	}

    /**
     * Refresh properties.
     *
     * @param runtimeProperties the runtime properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void  refreshProperties(ApplicationProperties applicationproperties) throws IOException {
        RuntimePropsDynamoDBServiceImpl amazonDynamoDBService = new RuntimePropsDynamoDBServiceImpl();
        InputStream is = null;
        Map<String, String> properties = new HashMap<String, String>();
        String runlevel = applicationproperties.getRunlevel();
        try {
            amazonDynamoDBService.setRunLevel(runlevel);
            properties = amazonDynamoDBService.getAllItems();
            Iterator<String> it = properties.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                applicationproperties.setProperty(key, properties.get(key));
            }
            is = RuntimeProperties.class.getResourceAsStream("/override.properties");
            if (is != null) {
                applicationproperties.load(new BufferedInputStream(is));
            }else{
                log4j.error("Unable to load overiride.properties resource - please check it is available!");
            }
            /*for (Enumeration<?> e = runtimeProperties.propertyNames(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String val = runtimeProperties.getProperty(key);
                log4j.info(key + "=" + val);
            }*/
            log4j.info("Runtime properties for the runlevel '"+runlevel+"' has been refreshed");
        } catch (ServiceException e) {
            log4j.error("Error occured while fetching properties from dynmo DB due to :"+e.getMessage());
        }finally{
            try{
                if (is != null) {
                     is.close();
                     is = null;
                 }
            }catch(Throwable t){
                log4j.error("Unable to close returnstream! Error = " + t.getClass().getName() + ", message = " + t.getMessage());
            }
        }

    }

    /**
     * Start runtime properties job.
     */
    public static void startApplicationPropertiesJob() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
             public void run() {
                try{
                    ApplicationProperties applicationproperties = ApplicationProperties.getInstance();
                    refreshProperties(applicationproperties);
                }catch(Exception e){
                    log4j.error("Application properties refresh job interrupted due to :!"+e.getMessage());
                }
            }
      };
      timer.schedule(task, 60000*5,60000*5);
      log4j.info("Successfully initiated the runtime properties refresh job!");

    }

}
