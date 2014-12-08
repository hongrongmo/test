package org.ei.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.books.LemQueryWriter;
import org.ei.controller.content.ContentConfig;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.domain.DriverConfig;
import org.ei.domain.FastSearchControl;
import org.ei.domain.HelpLinksCache;
import org.ei.exception.ServiceException;
import org.ei.service.amazon.dynamodb.RuntimePropsDynamoDBServiceImpl;
import org.ei.thesaurus.ThesaurusSearchControl;

/**
 * Wraps all property access for EV.  Currently the following are included:
 *
 * - ApplicationProperties: properties loaded for runtime, e.g. Fast base URL, SMTP host, etc.
 * - JSPPathProperties: properties loaded for XML transformations (location of JSP returning XML)
 * - ContentConfig:  the configuration file for working with the engvillage data service
 * - Bypass objects: there are "bypass" options to allow direct access to EV from certain IP addresses and/or Customer IDs
 *
 */
/**
 * @author kamaramx
 *
 */
public final class EVProperties {
    private static Logger log4j = Logger.getLogger(EVProperties.class);

    /** The Constant APP_NAME. */
    public static final String APP_NAME = "app.name";

    /** The Constant SESSION_TIMEOUT. */
    public static final String SESSION_TIMEOUT = "SESSION_TIMEOUT";

    /** The Constant REQUEST_ATTRIBUTE. */
    public static final String REQUEST_ATTRIBUTE = "runtimeproperties";

    /** The Constant PREVENT_CSRF_ATTACK. */
    public static final String PREVENT_CSRF_ATTACK ="PREVENT_CSRF_ATTACK";

    /** The Constant MAINTENANCE_MODE. */
    public static final String MAINTENANCE_MODE ="maintenance.mode";

    /** The Constant MEMCACHE_SERVERS. */
    public static final String MEMCACHE_SERVERS = "memcache.servers";

    /** The Constant MEMCACHE_WEIGHTS. */
    public static final String MEMCACHE_WEIGHTS = "memcache.weights";

    /** The Constant MEMCACHE_ENABLED. */
    public static final String MEMCACHE_ENABLED = "memcache.enabled";

    /** The Constant SYNC_TOKEN_LIST_SIZE. */
    public static final String SYNC_TOKEN_LIST_SIZE = "SYNC_TOKEN_LIST_SIZE";

    /** The Constant S3_BULLETIN_BUCKET_NAME. */
    public static final String S3_BULLETIN_BUCKET_NAME= "s3.bulletin.bucket.name";

    /** The Constant S3_CUSTOMERIMAGE_BUCKET_NAME. */
    public static final String S3_CUSTOMERIMAGE_BUCKET_NAME= "s3.customerimage.bucket.name";

    /** The Constant S3_DOCVIEW_BUCKET_NAME. */
    public static final String S3_DOCVIEW_BUCKET_NAME= "s3.docview.bucket.name";

    /** The Constant S3_REFEREX_BUCKET_NAME. */
    public static final String S3_REFEREX_BUCKET_NAME = "s3.referex.bucket.name";

    /** The Constant S3_REFEREX_BUCKET_KEY. */
    public static final String S3_REFEREX_BUCKET_KEY = "s3.referex.key";

    /** The Constant AWS_METADATA_URL. */
    public static final String AWS_METADATA_URL = "aws.metadata.url";

    /** The Constant APP_DOMAIN. */
    public static final String APP_DOMAIN= "APP_DOMAIN";

    /** The Constant DISABLE_SSO_AUTH. */
    public static final String DISABLE_SSO_AUTH= "disable_sso_auth";

    /** The Constant SYSTEM_ENVIRONMENT_RUNLEVEL. */
    public static final String SSO_CORE_REDIRECT_FLAG = "sso.core.redirect.flag";

    /** The Constant LOGIN_FULL_CANCEL_URI. */
    public static final String LOGIN_FULL_CANCEL_URI= "LOGIN_FULL_CANCEL_URI";

    /** The Constant CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS. */
    public static final String CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS= "CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS";

    /** The Constant RATELIMITER_ENABLED. */
    public static final String RATELIMITER_ENABLED = "ratelimiter.enabled";

    /** The Constant RATELIMITER_MAX_RATE. */
    public static final String RATELIMITER_MAX_RATE = "ratelimiter.max.rate";

    /** The Constant RATELIMITER_MIN_REQUESTS. */
    public static final String RATELIMITER_MIN_REQUESTS = "ratelimiter.min.requests";

    /** The Constant RATELIMITER_RESET. */
    public static final String RATELIMITER_RESET = "ratelimiter.reset";

    /** The Constant RATELIMITER_EMAIL_TO. */
    public static final String RATELIMITER_EMAIL_TO = "ratelimiter.email.to";

    /** Max error threshold for exception handler. */
    public static final String MAX_ERROR_THRESHHOLD = "max.error.threshhold";

    /** Max error interval for exception handler*/
    public static final String MAX_ERROR_INTERVAL = "max.error.interval";

    /** The topic ARN for SNS */
    public static final String SNS_TOPIC_AWSALERTS = "sns.topic.awsalerts";

    /** The Constant GOOGLE_ANALYTICS_ACCOUNT. */
    public static final String GOOGLE_ANALYTICS_ACCOUNT = "google.analytics.account";

    /** The Constant HELP_URL. */
    public static final String HELP_URL = "HelpUrl";

    /** The Constant HELP_CONTEXT_SEARCH_QUICK. */
    public static final String HELP_CONTEXT_SEARCH_QUICK = "help.context.search.quick";

    /** The Constant HELP_CONTEXT_SEARCH_EBOOK. */
    public static final String HELP_CONTEXT_SEARCH_EBOOK = "help.context.search.ebook";

    /** The Constant HELP_CONTEXT_SEARCH_EXPERT. */
    public static final String HELP_CONTEXT_SEARCH_EXPERT = "help.context.search.expert";

    /** The Constant HELP_CONTEXT_SEARCH_THESHOME. */
    public static final String HELP_CONTEXT_SEARCH_THESHOME = "help.context.search.theshome";

    /** The Constant HELP_CONTEXT_CUSTOMER_PROFILE. */
    public static final String HELP_CONTEXT_CUSTOMER_PROFILE = "help.context.customer.profile";

    /** The Constant HELP_CONTEXT_PERSONALLOGINFORM. */
    public static final String HELP_CONTEXT_PERSONALLOGINFORM = "help.context.personalLoginForm";//CAN DELETE

    /** The Constant HELP_CONTEXT_PERSONAL_SAVESEARCH_DISPLAY. */
    public static final String HELP_CONTEXT_PERSONAL_SAVESEARCH_DISPLAY = "help.context.personal.savesearch.display";

    /** The Constant HELP_CONTEXT_ASKANEXPERT_DISPLAY. */
    public static final String HELP_CONTEXT_ASKANEXPERT_DISPLAY = "help.context.askanexpert.display";

    /** The Constant HELP_CONTEXT_SEARCH_RESULTS_QUICK. */
    public static final String HELP_CONTEXT_SEARCH_RESULTS_QUICK = "help.context.search.results.quick";

    /** The Constant HELP_CONTEXT_SEARCH_DOC_ABSTRACT. */
    public static final String HELP_CONTEXT_SEARCH_DOC_ABSTRACT = "help.context.search.doc.abstract";

    /** The Constant HELP_CONTEXT_SEARCH_DOC_DETAILED. */
    public static final String HELP_CONTEXT_SEARCH_DOC_DETAILED = "help.context.search.doc.detailed";

    /** The Constant HELP_CONTEXT_SEARCH_BOOK_DETAILED. */
    public static final String HELP_CONTEXT_SEARCH_BOOK_DETAILED = "help.context.search.book.detailed";

    /** The Constant HELP_CONTEXT_SEARCH_BOOK_BOOKDETAILED. */
    public static final String HELP_CONTEXT_SEARCH_BOOK_BOOKDETAILED = "help.context.search.book.bookdetailed";

    /** The Constant HELP_CONTEXT_SEARCH_RESULTS_EXPERT. */
    public static final String HELP_CONTEXT_SEARCH_RESULTS_EXPERT = "help.context.search.results.expert";

    /** The Constant HELP_CONTEXT_SEARCH_RESULTS_THES. */
    public static final String HELP_CONTEXT_SEARCH_RESULTS_THES = "help.context.search.results.thes";

    /** The Constant HELP_CONTEXT_CUSTOMER_SETTINGS. */
    public static final String HELP_CONTEXT_CUSTOMER_SETTINGS = "help.context.customer.settings";

    /** The Constant HELP_CONTEXT_SEARCH_RESULTS_TAGS. */
    public static final String HELP_CONTEXT_SEARCH_RESULTS_TAGS = "help.context.search.results.tags";

    /** The Constant HELP_CONTEXT_TAGSGROUPS_DISPLAY. */
    public static final String HELP_CONTEXT_TAGSGROUPS_DISPLAY = "help.context.tagsgroups.display";

    /** The Constant HELP_CONTEXT_TAGSGROUPS_EDITGROUPS. */
    public static final String HELP_CONTEXT_TAGSGROUPS_EDITGROUPS = "help.context.tagsgroups.editgroups";

    /** The Constant HELP_CONTEXT_TAGSHOME. */
    public static final String HELP_CONTEXT_TAGSHOME = "help.context.tagGroups";//CAN DELETE

    /** The Constant HELP_CONTEXT_TAGSGROUPS_RENAMETAG. */
    public static final String HELP_CONTEXT_TAGSGROUPS_RENAMETAG = "help.context.tagsgroups.renametag";

    /** The Constant HELP_CONTEXT_TAGSGROUPS_DELETETAG. */
    public static final String HELP_CONTEXT_TAGSGROUPS_DELETETAG = "help.context.tagsgroups.deleteTag";

    /** The Constant HELP_CONTEXT_BULLETINS_DISPLAY. */
    public static final String HELP_CONTEXT_BULLETINS_DISPLAY = "help.context.bulletins.display";

    /** The Constant HELP_CONTEXT_BULLETINS_ARCHIVE. */
    public static final String HELP_CONTEXT_BULLETINS_ARCHIVE = "help.context.bulletins.archive";

    /** The Constant HELP_CONTEXT_SEARCH_RESULTS_DEDUPFORM. */
    public static final String HELP_CONTEXT_SEARCH_RESULTS_DEDUPFORM = "help.context.search.results.dedupform";

    /** The Constant HELP_CONTEXT_SEARCH_RESULTS_DEDUP. */
    public static final String HELP_CONTEXT_SEARCH_RESULTS_DEDUP = "help.context.search.results.dedup";

    /** The Constant HELP_CONTEXT_PERSONAL_FOLDERS_DISPLAY. */
    public static final String HELP_CONTEXT_PERSONAL_FOLDERS_DISPLAY = "help.context.personal.folders.display";

    /** The Constant HELP_CONTEXT_PERSONAL_FOLDERS. */
    public static final String HELP_CONTEXT_PERSONAL_FOLDERS = "help.context.personal.folders";

    /** The Constant HELP_CONTEXT_ADDFOLDER. */
    public static final String HELP_CONTEXT_ADDFOLDER = "help.context.addPersonalFolder";//CAN DELETE

    /** The Constant HELP_CONTEXT_RENAMEFOLDER. */
    public static final String HELP_CONTEXT_RENAMEFOLDER = "help.context.renamePersonalFolder";//CAN DELETE

    /** The Constant HELP_CONTEXT_DELETEFOLDER. */
    public static final String HELP_CONTEXT_DELETEFOLDER = "help.context.deletePersonalFolder";//CAN DELETE

    /** The Constant HELP_CONTEXT_UPDATEFOLDER. */
    public static final String HELP_CONTEXT_UPDATEFOLDER = "help.context.updatePersonalFolder";//CAN DELETE

    /** The Constant HELP_CONTEXT_SELECTED_CITATIONFOLDER. */
    public static final String HELP_CONTEXT_SELECTED_CITATIONFOLDER = "help.context.selected.citationfolder";

    /** The Constant HELP_CONTEXT_PATENTRESULTS. */
    public static final String HELP_CONTEXT_PATENTRESULTS = "help.context.quickSearchReferencesFormat";//CAN DELETE

    /** The Constant HELP_CONTEXT_SELECTED_CITATION. */
    public static final String HELP_CONTEXT_SELECTED_CITATION = "help.context.selected.citation";

    /** The Constant HELP_CONTEXT_SELECTED_ABSTRACT. */
    public static final String HELP_CONTEXT_SELECTED_ABSTRACT = "help.context.selected.abstract";

    /** The Constant HELP_CONTEXT_SELECTED_DETAILED. */
    public static final String HELP_CONTEXT_SELECTED_DETAILED = "help.context.selected.detailed";

    /** The Constant HELP_CONTEXT_CUSTOMER_PASSWORD. */
    public static final String HELP_CONTEXT_CUSTOMER_PASSWORD = "help.context.customer.password";

    /** The Constant HELP_CONTEXT_CUSTOMER_PASSWORD_GUIDELINES. */
    public static final String HELP_CONTEXT_CUSTOMER_PASSWORD_GUIDELINES = "help.context.customer.password.guidelines";

    /** The Constant HELP_CONTEXT_CUSTOMER_REMINDER. */
    public static final String HELP_CONTEXT_CUSTOMER_REMINDER = "help.context.customer.reminder";

    /** The Constant HELP_CONTEXT_CUSTOMER_PROFILE_DISPLAY. */
    public static final String HELP_CONTEXT_CUSTOMER_PROFILE_DISPLAY = "help.context.customer.profile.display";//TODO

    /** The Constant HELP_CONTEXT_CUSTOMER_AUTHENTICATE_MANRA. */
    public static final String HELP_CONTEXT_CUSTOMER_AUTHENTICATE_MANRA = "help.context.customer.authenticate.manra";//TODO

    /** The Constant HELP_CONTEXT_CUSTOMER_AUTHENTICATE_REMINDER. */
    public static final String HELP_CONTEXT_CUSTOMER_AUTHENTICATE_REMINDER = "help.context.customer.authenticate.reminder";//TODO

    /** The Constant HELP_CONTEXT_CUSTOMER_INSTITUTIONCHOICE. */
    public static final String HELP_CONTEXT_CUSTOMER_INSTITUTIONCHOICE = "help.context.customer.institutionchoice";//TODO

    /** The Constant HELP_CONTEXT_CUSTOMER_AUTHENTICATE_LOGINFULL. */
    public static final String HELP_CONTEXT_CUSTOMER_AUTHENTICATE_LOGINFULL = "help.context.customer.authenticate.loginfull";//TODO

    /** The Constant HELP_CONTEXT_CUSTOMER_AUTHENTICATE. */
    public static final String HELP_CONTEXT_CUSTOMER_AUTHENTICATE = "help.context.customer.authenticate";//TODO

    /** The Constant HELP_CONTEXT_HOME. */
    public static final String HELP_CONTEXT_HOME = "help.context.home";//TODO

    /** The Constant CARS_USER_MIGRATION_FLAG. */
    public static final String CARS_USER_MIGRATION_FLAG = "CARS_USER_MIGRATION_FLAG";

    /** The Constant APP_DEFAULT_IMAGE. */
    public static final String APP_DEFAULT_IMAGE="APP_DEFAULT_IMAGE";

    /** The Constant REG_ID_ASSOC_CANCEL. */
    public static final String REG_ID_ASSOC_CANCEL="REG_ID_ASSOC_CANCEL";

    /** The Constant CARS_REG_ID_ASSOCIATION_REGISTER_TZ. */
    public static final String CARS_REG_ID_ASSOCIATION_REGISTER_TZ="CARS_REG_ID_ASSOCIATION_REGISTER_TZ";

    /** The Constant LOG_CARS_RESPONSE_ERRORS. */
    public static final String LOG_CARS_RESPONSE_ERRORS="log.cars.response.errors";
    
    /** The Constant S3_FIG_URL. */
    public static final String S3_FIG_URL ="s3.fig.url";
    
    /** The Constant DOWNTIME_MESSAGE_ENABLED. */
    public static final String DOWNTIME_MESSAGE_ENABLED ="downtime.message.enabled";
    
    /** The Constant DOWNTIME_MESSAGE_TEXT. */
    public static final String DOWNTIME_MESSAGE_TEXT ="downtime.message.text";
    
    /** The Constant DOWNTIME_MESSAGE_COLOR. */
    public static final String DOWNTIME_MESSAGE_COLOR ="downtime.message.color";
    
    /** The Constant IE7_WARN_MSG_ENABLED. */
    public static final String IE7_WARN_MSG_ENABLED ="ie7.warn.msg.enabled";
    
    /** The Constant IE7_WARN_MSG_TEXT. */
    public static final String IE7_WARN_MSG_TEXT ="ie7.warn.msg.text";

    private static EVProperties instance;

	public static String NEWLINE = "\n";
	static {
		String nl = System.getProperty("line.separator");
		if (nl != null) NEWLINE = nl;
	}

	private ApplicationProperties applicationProperties;
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

    /** The timer. */
    public  static Timer timer = null;

    /**
     * Refresh properties.
     *
     * @param applicationProperties the runtime properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void  refreshProperties() throws IOException {
        RuntimePropsDynamoDBServiceImpl amazonDynamoDBService = new RuntimePropsDynamoDBServiceImpl();
        InputStream is = null;
        Map<String, String> properties = new HashMap<String, String>();
        try {
            amazonDynamoDBService.setRunLevel(this.applicationProperties.getRunlevel());
            properties = amazonDynamoDBService.getAllItems();
            Iterator<String> it = properties.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                applicationProperties.setProperty(key, properties.get(key));
            }
            if(!GenericValidator.isBlankOrNull(System.getProperty("os.name")) && System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
            	log4j.info("*********USING MAC PROPERTY OVERRIDES*************");
            	is = ApplicationProperties.class.getResourceAsStream("/override.properties.mac");
            }else{
            	is = ApplicationProperties.class.getResourceAsStream("/override.properties");
            }
            if (is != null) {
                applicationProperties.load(new BufferedInputStream(is));
            }else{
                log4j.error("Unable to load overiride.properties resource - please check it is available!");
            }
            /*for (Enumeration<?> e = applicationProperties.propertyNames(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String val = applicationProperties.getProperty(key);
                log4j.info(key + "=" + val);
            }*/
            log4j.info("Application properties for the runlevel '"+this.applicationProperties.getRunlevel()+"' has been refreshed");
        } catch (ServiceException e) {
            log4j.error("Error occured while fetching properties from dynamo DB due to :"+e.getMessage());
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
    public void startApplicationPropertiesJob() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
             public void run() {
                try{
                    refreshProperties();
                }catch(Exception e){
                    log4j.error("Runtime properties refresh job interrupted due to :!"+e.getMessage());
                }
            }
      };
      timer.schedule(task, 60000*5,60000*5);
      log4j.info("Successfully initiated the runtime properties refresh job!");

    }

    // Private instance
	private EVProperties() {

	}

    /**
     * Gets the single instance of RuntimeProperties.
     *
     * @return single instance of RuntimeProperties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static synchronized EVProperties getInstance() {
        if (instance == null) {
            instance = new EVProperties();
            instance.applicationProperties = ApplicationProperties.getInstance();
            instance.applicationProperties.setProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL, System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL));
            instance.applicationProperties.setRunlevel(System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL));

            try {
                instance.refreshProperties();
                instance.init();
                instance.startApplicationPropertiesJob();
            } catch (IOException e) {
                log4j.error("Unable to initialize RuntimeProperties!",e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    /**
     * Load properties from a file handle.
     *
     * @param propsFile the props file
     * @return RuntimeProperties object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("unused")
    public static synchronized EVProperties getInstance(String propsFile) throws IOException {
        if (instance == null) {
            instance = new EVProperties();
            instance.applicationProperties = ApplicationProperties.getInstance();
            instance.applicationProperties.setProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL, System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL));
            instance.applicationProperties.setRunlevel(System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL));

            InputStream is = new BufferedInputStream(new FileInputStream(propsFile));
            if (is == null)
                log4j.error("Unable to load RuntimeProperties resource - please check it is available!");
            else{
                instance.applicationProperties.load(new BufferedInputStream(is));
                instance.init();
            }
        }
        return instance;
    }


	/**
	 * Retrieve a Runtime property
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
	    try {
	        return getInstance().applicationProperties.getProperty(key);
	    } catch (Throwable t) {
	        log4j.error("Unable to retrieve property by key: '" + key +"'");
	    }
	    return "";
	}

    /**
     * Retrieve a JSPPath property
     * @param key
     * @return
     */
	public static String getJSPPath(String key) {
	    String jsppath = instance.jspPathProperties.getProperty(key);
	    String dataurl = getProperty(ApplicationProperties.DATA_URL);
		if (GenericValidator.isBlankOrNull(jsppath) || GenericValidator.isBlankOrNull(dataurl)) {
            log4j.warn("Unable to find JSP path for key: " + key);
            return "";
		} else {
            return  dataurl + jsppath;
		}
	}

    /**
     * This is meant to finish initialization from an InputStream.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void init() throws IOException {
        try {

            //
            // Init the DatabaseConfig
            //
            DatabaseConfig.getInstance(DriverConfig.getDriverTable());

            //
            // Init some static classes from property values
            //
            FastSearchControl.BASE_URL = applicationProperties.getProperty(ApplicationProperties.FAST_BASE_URL);
            LemQueryWriter.BASE_URL = applicationProperties.getProperty(ApplicationProperties.FAST_LEM_BASE_URL);
            ThesaurusSearchControl.BASE_URL = applicationProperties.getProperty(ApplicationProperties.THES_BASE_URL);

            //
            // Print for debug
            //
            if (log4j.isDebugEnabled()) {
                log4j.debug("*************** Runtime properties: ");
                for (Enumeration<?> e = applicationProperties.propertyNames(); e.hasMoreElements();) {
                    String key = (String) e.nextElement();
                    String val = applicationProperties.getProperty(key);
                    log4j.debug("    " + key + "=" + val);
                }
            }

            //
            // Try to load release version number from properties
            //
            try {
                String rv = applicationProperties.getProperty(ApplicationProperties.RELEASE_VERSION);
                if (GenericValidator.isBlankOrNull(rv)) {
                    log4j.warn("***************  RELEASE VERSION NOT FOUND!! ******************");
                }
            } catch (Exception e) {
                log4j.warn("***************  UNABLE TO RETRIEVE RELEASE VERSION!! ******************", e);
            }

        } catch (DatabaseConfigException e) {
            throw new IOException("Unable to configure DatabaseConfig object!",e);
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
        for (Enumeration<?> e = applicationProperties.propertyNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String val = applicationProperties.getProperty(key);
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
        for (Enumeration<?> e = applicationProperties.propertyNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String val = getProperty(key);
            out.println(key + "=" + val);
            out.println("");
        }
    }



    /**
     * Gets the config service.
     *
     * @return the config service
     */
    public String getConfigService() {
        return getDataUrl() + "/engvillage/servlet/ConfigService";
    }

    /**
     * Gets the fast url.
     *
     * @return the fast url
     */
    public String getFastUrl() {
        return applicationProperties.getProperty(ApplicationProperties.FAST_BASE_URL);
    }

    /**
     * Gets the citedby url.
     *
     * @return the citedby url
     */
    public String getCitedbyUrl() {
        return applicationProperties.getProperty(ApplicationProperties.CITED_BY_URL);
    }

    /**
     * Gets the app name.
     *
     * @return the app name
     */
    public String getAppName() {
        return applicationProperties.getProperty(APP_NAME);
    }

    /**
     * Checks if is append session.
     *
     * @return true, if is append session
     */
    public boolean isAppendSession() {
        return Boolean.parseBoolean(applicationProperties.getProperty(ApplicationProperties.APPEND_SESSION));
    }

    /**
     * Gets the data url.
     *
     * @return the data url
     */
    public String getDataUrl() {
        return applicationProperties.getProperty(ApplicationProperties.DATA_URL);
    }

    /**
     * Gets the log url.
     *
     * @return the log url
     */
    public String getLogUrl() {
        return applicationProperties.getProperty(ApplicationProperties.LOG_URL);
    }

    /**
     * Gets the auth url.
     *
     * @return the auth url
     */
    public String getAuthUrl() {
        return applicationProperties.getProperty(ApplicationProperties.AUTH_URL);
    }

    /**
     * Gets the app domain.
     *
     * @return the app domain
     */
    public String getAppDomain() {
        return applicationProperties.getProperty(APP_DOMAIN);
    }

    /**
     * Gets the datacache dir.
     *
     * @return the datacache dir
     */
    public String getDatacacheDir() {
        return applicationProperties.getProperty(ApplicationProperties.DATA_CACHE_DIR);
    }

    /**
     * Gets the help url.
     *
     * @return the help url
     */
    public static String getHelpUrl() {
        return EVProperties.getProperty(HELP_URL);
    }

    /**
     * Return Log CARS response errors flag
     *
     * @return
     */
    public static Boolean isLogCarsResponseErrors() {
        String logcarsresponseerrors = EVProperties.getProperty(LOG_CARS_RESPONSE_ERRORS);
        if (logcarsresponseerrors != null) return Boolean.parseBoolean(logcarsresponseerrors);
        else return false;
    }

    /**
     * Gets the aWS meta data url.
     *
     * @return the aWS meta data url
     */
    public String getAWSMetaDataUrl(){
        return applicationProperties.getProperty(AWS_METADATA_URL);
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

	public static void setApplicationProperties(ApplicationProperties applicationProperties) {
 		instance.applicationProperties = applicationProperties;
	}

	public static ApplicationProperties getApplicationProperties() {
	    return instance.applicationProperties;
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
	    	String templateNames= getProperty(CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS);
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
