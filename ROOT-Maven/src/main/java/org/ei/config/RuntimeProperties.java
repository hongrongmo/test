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

package org.ei.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.books.LemQueryWriter;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.domain.DriverConfig;
import org.ei.domain.FastSearchControl;
import org.ei.exception.ServiceException;
import org.ei.service.amazon.dynamodb.AmazonDynamoDBService;
import org.ei.service.amazon.dynamodb.RuntimePropsDynamoDBServiceImpl;
import org.ei.thesaurus.ThesaurusSearchControl;

// TODO: Auto-generated Javadoc
/**
 * This class encapsulates the properties class.
 *
 * @author Laxman
 **/

@SuppressWarnings("serial")
public final class RuntimeProperties extends Properties {

    public final static String RUNLEVEL_PROD = "prod";
    public final static String RUNLEVEL_CERT = "cert";
    public final static String RUNLEVEL_DEV = "dev";
    public final static String RUNLEVEL_LOCAL = "local";

    /** The log4j. */
    private static Logger log4j = Logger.getLogger(RuntimeProperties.class);

    /** The Constant SYSTEM_ENVIRONMENT_RUNLEVEL. */
    public static final String SYSTEM_ENVIRONMENT_RUNLEVEL = "com.elsevier.env";

    /** The Constant SYSTEM_ENVIRONMENT_RUNLEVEL. */
    public static final String SSO_CORE_REDIRECT_FLAG = "sso.core.redirect.flag";

    /** The Constant ROOT_LOG_LEVEL. */
    public static final String ROOT_LOG_LEVEL = "root.log.level";

    /** The Constant ROOT_LOG_TO_CONSOLE. */
    public static final String ROOT_LOG_TO_CONSOLE = "root.log.to.console";

    /** The Constant REQUEST_ATTRIBUTE. */
    public static final String REQUEST_ATTRIBUTE = "runtimeproperties";

    /** The Constant RELEASE_VERSION. */
    public static final String RELEASE_VERSION = "release.version";

    /** The Constant FAST_BASE_URL. */
    public static final String FAST_BASE_URL = "fast.base.url";

    /** The Constant THES_BASE_URL. */
    public static final String THES_BASE_URL = "thesaurus.base.url";

    /** The Constant FAST_LEM_BASE_URL. */
    public static final String FAST_LEM_BASE_URL = "fast.lem.base.url";

    /** The Constant FAST_DOCVIEW_BASE_URL. */
    public static final String FAST_DOCVIEW_BASE_URL = "fast.docview.base.url";

    /** The Constant REFEREX_TOC_BASE_PATH. */
    @Deprecated
    public static final String REFEREX_TOC_BASE_PATH = "referex.toc.base.path";

    /** The Constant WHOLE_BOOK_DOWNLOAD_BASE_URL. */
    public static final String WHOLE_BOOK_DOWNLOAD_BASE_URL = "whole.book.download.base.url";

    /** The Constant CITED_BY_URL. */
    public static final String CITED_BY_URL = "citedby.url";

    /** The Constant APP_NAME. */
    public static final String APP_NAME = "app.name";

    /** The Constant APPEND_SESSION. */
    public static final String APPEND_SESSION = "append.session";

    /** The Constant DATA_URL. */
    public static final String DATA_URL = "data.url";

    /** The Constant LOG_URL. */
    public static final String LOG_URL = "log.url";

    /** The Constant AUTH_URL. */
    public static final String AUTH_URL = "auth.url";

    /** The Constant LOGSERVICE_LOG_PATH. */
    public static final String LOGSERVICE_LOG_PATH = "logservice.log.path";

    /** The Constant AWS_METADATA_URL. */
    public static final String AWS_METADATA_URL = "aws.metadata.url";

    /** The Constant USPTO_LUCENE_INDEX_DIR. */
    public static final String USPTO_LUCENE_INDEX_DIR = "uspto.lucene.index.dir";

    /** The Constant IPC_LUCENE_INDEX_DIR. */
    public static final String IPC_LUCENE_INDEX_DIR = "ipc.lucene.index.dir";

    /** The Constant ECLA_LUCENE_INDEX_DIR. */
    public static final String ECLA_LUCENE_INDEX_DIR = "ecla.lucene.index.dir";

    /** The Constant MEMCACHE_SERVERS. */
    public static final String MEMCACHE_SERVERS = "memcache.servers";

    /** The Constant MEMCACHE_WEIGHTS. */
    public static final String MEMCACHE_WEIGHTS = "memcache.weights";

    /** The Constant MEMCACHE_ENABLED. */
    public static final String MEMCACHE_ENABLED = "memcache.enabled";

    /** The Constant DATA_CACHE_DIR. */
    public static final String DATA_CACHE_DIR = "data.cache.dir";

    /** The Constant GOOGLE_ANALYTICS_ACCOUNT. */
    public static final String GOOGLE_ANALYTICS_ACCOUNT = "google.analytics.account";

    /** The Constant BULLETIN_FILE_LOCATION. */
    public static final String BULLETIN_FILE_LOCATION = "bulletin.file.location";

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

	/** The Constant REFEREX_MASK_FLAG. */
	public static final String REFEREX_MASK_FLAG = "referex.mask.flag";

	/** The Constant REFEREX_MASK_DATE. */
	public static final String REFEREX_MASK_DATE = "referex.mask.date";

    /** The Constant PAGESIZE. */
    public static final String PAGESIZE = "PAGESIZE";

    /** The Constant DISPLAY_PAGESIZE. */
    public static final String DISPLAY_PAGESIZE = "DISPLAYPAGESIZE";

    /** The Constant BASKET_PAGESIZE. */
    public static final String BASKET_PAGESIZE = "BASKETPAGESIZE";

    /** The Constant LOOKUP_PAGESIZE. */
    public static final String LOOKUP_PAGESIZE = "LOOKUPPAGESIZE";

    /** The Constant SEARCHHISTORY_SIZE. */
    public static final String SEARCHHISTORY_SIZE = "SEARCHHISTORYSIZE";

    /** The Constant EMAILALERT_SIZE. */
    public static final String EMAILALERT_SIZE = "EMAILALERTSIZE";

    /** The Constant EMAILALERT_DAY. */
    public static final String EMAILALERT_DAY = "EMAILALERTDAY";

    /** The Constant MAX_FOLDERSIZE. */
    public static final String MAX_FOLDERSIZE = "MAXFOLDERSIZE";

    /** The Constant MAX_BASKETSIZE. */
    public static final String MAX_BASKETSIZE = "MAXBASKETSIZE";

    /** The Constant SERVER_LOCATION. */
    public static final String SERVER_LOCATION = "SERVERLOCATION";

    /** The Constant DEDUPSET_SIZE. */
    public static final String DEDUPSET_SIZE = "DEDUPSETSIZE";

    /** The Constant SYSTEM_ENDYEAR. */
    public static final String SYSTEM_ENDYEAR = "SYSTEM_ENDYEAR";

    /** The Constant PAGESIZE_OPTIONS. */
    public static final String PAGESIZE_OPTIONS = "PAGESIZEOPTIONS";

    /** The Constant SAVED_SERCHES_ALERTS_LIMIT. */
    public static final String SAVED_SERCHES_ALERTS_LIMIT = "SAVED_SERCHES_ALERTS_LIMIT";

    /** The Constant SESSION_TIMEOUT. */
    public static final String SESSION_TIMEOUT = "SESSION_TIMEOUT";

    /** The Constant APP_DOMAIN. */
    public static final String APP_DOMAIN= "APP_DOMAIN";

    /** The Constant DISABLE_SSO_AUTH. */
    public static final String DISABLE_SSO_AUTH= "disable_sso_auth";

    /** The Constant LOGIN_FULL_CANCEL_URI. */
    public static final String LOGIN_FULL_CANCEL_URI= "LOGIN_FULL_CANCEL_URI";

    /** The Constant CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS. */
    public static final String CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS= "CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS";


    /** The Constant SMTP_HOST. */
    public static final String SMTP_HOST = "mail.smtp.host";

    /** The Constant SMTP_USERNAME. */
    public static final String SMTP_USERNAME = "mail.smtp.username";

    /** The Constant SMTP_PASSWORD. */
    public static final String SMTP_PASSWORD = "mail.smtp.password";

	/** The Constant DEBUG. */
	public static final String DEBUG = "mail.smtp.debug";

    /** The Constant SENDER. */
    public static final String SENDER = "sender";

    /** The Constant TO_RECEPIENTS. */
    public static final String TO_RECEPIENTS = "TORecepients";

    /** The Constant BCC_RECEPIENTS. */
    public static final String BCC_RECEPIENTS = "BCCRecepients";

    /** The Constant LHL_PRODUCTID. */
    public static final String LHL_PRODUCTID = "lhl.product.id";

    /** The Constant LHL_TO_RECIPIENTS. */
    public static final String LHL_TO_RECIPIENTS = "lhl.to.recipients";

    /** The Constant LHL_CC_RECIPIENTS. */
    public static final String LHL_CC_RECIPIENTS = "lhl.cc.recipients";

    /** The Constant LHL_FROM_RECIPIENTS. */
    public static final String LHL_FROM_RECIPIENTS = "lhl.from.recipients";

    /** The Constant LHL_EMAIL_SUBJECT. */
    public static final String LHL_EMAIL_SUBJECT = "lhl.email.subject";

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

    /** The Constant LIBRARIAN_EMAIL. */
    public static final String LIBRARIAN_EMAIL="LIBRARIAN_EMAIL";

    /** The Constant SPECIALIST_EMAIL. */
    public static final String SPECIALIST_EMAIL="SPECIALIST_EMAIL";

    /** The Constant ENGINEER_EMAIL. */
    public static final String ENGINEER_EMAIL="ENGINEER_EMAIL";

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

    /** The Constant CUSTOMER_IMAGES_URL_PATH. */
    public static final String CUSTOMER_IMAGES_URL_PATH = "customer.images.url.path";

    /** The Constant CUSTOMER_IMAGES_PREFIX. */
    public static final String CUSTOMER_IMAGES_PREFIX = "customer.images.prefix";

    /** The Constant CONTACT_US_LINK. */
    public static final String CONTACT_US_LINK = "contact.us.link";

    /** Max error threshold for exception handler. */
    public static final String MAX_ERROR_THRESHHOLD = "max.error.threshhold";

    /** Max error interval for exception handler*/
    public static final String MAX_ERROR_INTERVAL = "max.error.interval";

    /** The topic ARN for SNS */
    public static final String SNS_TOPIC_AWSALERTS = "sns.topic.awsalerts";

    //SSL
    /** The Constant HTTP_PORT. */
    public static final String HTTP_PORT ="HTTP_PORT";

    /** The Constant HTTPS_PORT. */
    public static final String HTTPS_PORT ="HTTPS_PORT";

    //
    // Static instance of the RuntimeProperties
    //
    /** The instance. */
    private static RuntimeProperties instance;

    /** The runlevel. */
    private String runlevel = "";

    /**
     * Instantiates a new runtime properties.
     */
    private RuntimeProperties() {}


    /** The timer. */
    public  static Timer timer = null;


    /**
     * Gets the single instance of RuntimeProperties.
     *
     * @return single instance of RuntimeProperties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static synchronized RuntimeProperties getInstance() throws IOException {
        if (instance == null) {
            instance = new RuntimeProperties();
            instance.runlevel = System.getProperty(SYSTEM_ENVIRONMENT_RUNLEVEL);
            RuntimeProperties.refreshProperties(instance);
            init();
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
	public static synchronized RuntimeProperties getInstance(String propsFile) throws IOException {
        if (instance == null) {
            instance = new RuntimeProperties();
            InputStream is = new BufferedInputStream(new FileInputStream(propsFile));
            if (is == null)
				log4j.error("Unable to load RuntimeProperties resource - please check it is available!");
			else{
            	instance.load(new BufferedInputStream(is));
            	init();
            }
        }
        return instance;
    }

    /**
     * This is meant to finish initialization from an InputStream.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void init() throws IOException {
        try {

            //
            // Init the DatabaseConfig
            //
            DatabaseConfig.getInstance(DriverConfig.getDriverTable());

            //
            // Init some static classes from property values
            //
            FastSearchControl.BASE_URL = instance.getProperty(FAST_BASE_URL);
            LemQueryWriter.BASE_URL = instance.getProperty(FAST_LEM_BASE_URL);
            ThesaurusSearchControl.BASE_URL = instance.getProperty(THES_BASE_URL);

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
     * Check a runtime propterty that is in the date form 'yyyyMMddHHmmss'
     * i.e. 04-01-2014 would be 20140401000000
     *
     * @param property the property
     * @return true, if is it time
     * @throws ParseException the parse exception
     */
    public boolean isItTime(String property) throws ParseException{
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		Date maskDate = df.parse(instance.getProperty(property));
		Date currentTime = Calendar.getInstance().getTime();
		log4j.info("**Current Time : " +currentTime.toString());
		if(maskDate.after(currentTime)){
			return false;
		}
		return true;

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
        return instance.getProperty(FAST_BASE_URL);
    }

    /**
     * Gets the citedby url.
     *
     * @return the citedby url
     */
    public String getCitedbyUrl() {
        return instance.getProperty(CITED_BY_URL);
    }

    /**
     * Gets the app name.
     *
     * @return the app name
     */
    public String getAppName() {
        return instance.getProperty(APP_NAME);
    }

    /**
     * Checks if is append session.
     *
     * @return true, if is append session
     */
    public boolean isAppendSession() {
        return Boolean.parseBoolean(instance.getProperty(APPEND_SESSION));
    }

    /**
     * Gets the data url.
     *
     * @return the data url
     */
    public String getDataUrl() {
        return instance.getProperty(DATA_URL);
    }

    /**
     * Gets the log url.
     *
     * @return the log url
     */
    public String getLogUrl() {
        return instance.getProperty(LOG_URL);
    }

    /**
     * Gets the auth url.
     *
     * @return the auth url
     */
    public String getAuthUrl() {
        return instance.getProperty(AUTH_URL);
    }

    /**
     * Gets the app domain.
     *
     * @return the app domain
     */
    public String getAppDomain() {
        return instance.getProperty(APP_DOMAIN);
    }

    /**
     * Gets the datacache dir.
     *
     * @return the datacache dir
     */
    public String getDatacacheDir() {
        return instance.getProperty(DATA_CACHE_DIR);
    }

    /**
     * Gets the help url.
     *
     * @return the help url
     */
    public String getHelpUrl() {
        return instance.getProperty(HELP_URL);
    }

    /**
     * Gets the aWS meta data url.
     *
     * @return the aWS meta data url
     */
    public String getAWSMetaDataUrl(){
    	return instance.getProperty(AWS_METADATA_URL);
    }

    /**
     * Returns the system runlevel.  This is usually set at application startup via
     * "-Drun.level=xxx" where xxx equals 'local', 'cert', 'cert2', 'prod' or 'prod2'
     *
     * @return the runlevel
     */
    public String getRunlevel() {
        return this.runlevel;
    }

    /**
     * Refresh properties.
     *
     * @param runtimeProperties the runtime properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void  refreshProperties(RuntimeProperties runtimeProperties) throws IOException {
    	RuntimePropsDynamoDBServiceImpl amazonDynamoDBService = new RuntimePropsDynamoDBServiceImpl();
    	InputStream is = null;
		Map<String, String> properties = new HashMap<String, String>();
		try {
			amazonDynamoDBService.setRunLevel(runtimeProperties.runlevel);
			properties = amazonDynamoDBService.getAllItems();
			Iterator<String> it = properties.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				runtimeProperties.setProperty(key, properties.get(key));
			}
			is = RuntimeProperties.class.getResourceAsStream("/override.properties");
			if (is != null) {
				runtimeProperties.load(new BufferedInputStream(is));
            }else{
            	log4j.error("Unable to load overiride.properties resource - please check it is available!");
            }
			/*for (Enumeration<?> e = runtimeProperties.propertyNames(); e.hasMoreElements();) {
	            String key = (String) e.nextElement();
	            String val = runtimeProperties.getProperty(key);
	            log4j.info(key + "=" + val);
	        }*/
			log4j.info("Runtime properties for the runlevel '"+runtimeProperties.runlevel+"' has been refreshed");
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
    public static void startRuntimePropertiesJob() {
		timer = new Timer();
    	TimerTask task = new TimerTask() {
            @Override
             public void run() {
            	try{
					RuntimeProperties runtimeProperties = RuntimeProperties.getInstance();
					refreshProperties(runtimeProperties);
				}catch(Exception e){
					log4j.error("Runtime properties refresh job interrupted due to :!"+e.getMessage());
				}
            }
      };
      timer.schedule(task, 60000*5,60000*5);
      log4j.info("Successfully initiated the runtime properties refresh job!");

	}
}

// END OF FILE : EIProperties.java

