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
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.books.LemQueryWriter;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.domain.DriverConfig;
import org.ei.domain.FastSearchControl;
import org.ei.thesaurus.ThesaurusSearchControl;

/**
 * This class encapsulates the properties class.
 *
 * @author Laxman
 **/

@SuppressWarnings("serial")
public final class RuntimeProperties extends Properties {
    private static Logger log4j = Logger.getLogger(RuntimeProperties.class);
	public static final String SYSTEM_ENVIRONMENT_RUNLEVEL = "env.runlevel";
	public static final String ROOT_LOG_LEVEL = "root.log.level";
	public static final String STRIPES_LOG_LEVEL = "stripes.log.level";

	public static final String REQUEST_ATTRIBUTE = "runtimeproperties";

	public static final String RELEASE_VERSION = "release.version";

	public static final String FAST_BASE_URL = "fast.base.url";
	public static final String THES_BASE_URL = "thesaurus.base.url";
	public static final String FAST_LEM_BASE_URL = "fast.lem.base.url";
	public static final String FAST_DOCVIEW_BASE_URL = "fast.docview.base.url";
	public static final String REFEREX_TOC_BASE_PATH = "referex.toc.base.path";
	public static final String WHOLE_BOOK_DOWNLOAD_BASE_URL = "whole.book.download.base.url";
	public static final String CITED_BY_URL = "citedby.url";
	public static final String APP_NAME = "app.name";
	public static final String APPEND_SESSION = "append.session";
	public static final String DATA_URL = "data.url";
	public static final String LOG_URL = "log.url";
	public static final String AUTH_URL = "auth.url";
	public static final String LOGSERVICE_LOG_PATH = "logservice.log.path";

	public static final String MEMCACHE_SERVERS = "memcache.servers";
	public static final String MEMCACHE_WEIGHTS = "memcache.weights";
	public static final String DATA_CACHE_DIR = "data.cache.dir";
	public static final String GOOGLE_ANALYTICS_ACCOUNT = "google.analytics.account";
    public static final String BULLETIN_FILE_LOCATION = "bulletin.file.location";

	public static final String PAGESIZE = "PAGESIZE";
	public static final String DISPLAY_PAGESIZE = "DISPLAYPAGESIZE";
	public static final String BASKET_PAGESIZE = "BASKETPAGESIZE";
	public static final String LOOKUP_PAGESIZE = "LOOKUPPAGESIZE";
	public static final String SEARCHHISTORY_SIZE = "SEARCHHISTORYSIZE";
	public static final String EMAILALERT_SIZE = "EMAILALERTSIZE";
	public static final String EMAILALERT_DAY = "EMAILALERTDAY";
	public static final String MAX_FOLDERSIZE = "MAXFOLDERSIZE";
	public static final String MAX_BASKETSIZE = "MAXBASKETSIZE";
	public static final String SERVER_LOCATION = "SERVERLOCATION";
	public static final String DEDUPSET_SIZE = "DEDUPSETSIZE";
	public static final String SYSTEM_ENDYEAR = "SYSTEM_ENDYEAR";
	public static final String PAGESIZE_OPTIONS = "PAGESIZEOPTIONS";
	public static final String SAVED_SERCHES_ALERTS_LIMIT = "SAVED_SERCHES_ALERTS_LIMIT";
	public static final String SESSION_TIMEOUT = "SESSION_TIMEOUT";
    public static final String APP_DOMAIN= "APP_DOMAIN";
    public static final String DISABLE_SSO_AUTH= "disable_sso_auth";
    public static final String LOGIN_FULL_CANCEL_URI= "LOGIN_FULL_CANCEL_URI";
    public static final String CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS= "CAR_RESPONSE_MODIFICATION_REQUIRED_TEMPALTE_IDS";


	public static final String SMTP_HOST = "mail.smtp.host";
	public static final String DEBUG = "debug";
	public static final String SENDER = "sender";
	public static final String TO_RECEPIENTS = "TORecepients";
	public static final String BCC_RECEPIENTS = "BCCRecepients";

	public static final String LHL_PRODUCTID = "lhl.product.id";
	public static final String LHL_TO_RECIPIENTS = "lhl.to.recipients";
	public static final String LHL_CC_RECIPIENTS = "lhl.cc.recipients";
	public static final String LHL_FROM_RECIPIENTS = "lhl.from.recipients";
	public static final String LHL_EMAIL_SUBJECT = "lhl.email.subject";

	public static final String HELP_URL = "HelpUrl";
	public static final String HELP_CONTEXT_SEARCH_QUICK = "help.context.search.quick";
	public static final String HELP_CONTEXT_SEARCH_EBOOK = "help.context.search.ebook";
	public static final String HELP_CONTEXT_SEARCH_EXPERT = "help.context.search.expert";
	public static final String HELP_CONTEXT_SEARCH_THESHOME = "help.context.search.theshome";
	public static final String HELP_CONTEXT_CUSTOMER_PROFILE = "help.context.customer.profile";
	public static final String HELP_CONTEXT_PERSONALLOGINFORM = "help.context.personalLoginForm";//CAN DELETE
	public static final String HELP_CONTEXT_PERSONAL_SAVESEARCH_DISPLAY = "help.context.personal.savesearch.display";
	public static final String HELP_CONTEXT_ASKANEXPERT_DISPLAY = "help.context.askanexpert.display";
	public static final String HELP_CONTEXT_SEARCH_RESULTS_QUICK = "help.context.search.results.quick";
	public static final String HELP_CONTEXT_SEARCH_DOC_ABSTRACT = "help.context.search.doc.abstract";
	public static final String HELP_CONTEXT_SEARCH_DOC_DETAILED = "help.context.search.doc.detailed";
	public static final String HELP_CONTEXT_SEARCH_BOOK_DETAILED = "help.context.search.book.detailed";
	public static final String HELP_CONTEXT_SEARCH_BOOK_BOOKDETAILED = "help.context.search.book.bookdetailed";
	public static final String HELP_CONTEXT_SEARCH_RESULTS_EXPERT = "help.context.search.results.expert";
	public static final String HELP_CONTEXT_SEARCH_RESULTS_THES = "help.context.search.results.thes";
	public static final String HELP_CONTEXT_CUSTOMER_SETTINGS = "help.context.customer.settings";
	public static final String HELP_CONTEXT_SEARCH_RESULTS_TAGS = "help.context.search.results.tags";
	public static final String HELP_CONTEXT_TAGSGROUPS_DISPLAY = "help.context.tagsgroups.display";
	public static final String HELP_CONTEXT_TAGSGROUPS_EDITGROUPS = "help.context.tagsgroups.editgroups";
	public static final String HELP_CONTEXT_TAGSHOME = "help.context.tagGroups";//CAN DELETE
	public static final String HELP_CONTEXT_TAGSGROUPS_RENAMETAG = "help.context.tagsgroups.renametag";
	public static final String HELP_CONTEXT_TAGSGROUPS_DELETETAG = "help.context.tagsgroups.deleteTag";
	public static final String HELP_CONTEXT_BULLETINS_DISPLAY = "help.context.bulletins.display";
	public static final String HELP_CONTEXT_BULLETINS_ARCHIVE = "help.context.bulletins.archive";
	public static final String HELP_CONTEXT_SEARCH_RESULTS_DEDUPFORM = "help.context.search.results.dedupform";
	public static final String HELP_CONTEXT_SEARCH_RESULTS_DEDUP = "help.context.search.results.dedup";
	public static final String HELP_CONTEXT_PERSONAL_FOLDERS_DISPLAY = "help.context.personal.folders.display";
	public static final String HELP_CONTEXT_PERSONAL_FOLDERS = "help.context.personal.folders";
	public static final String HELP_CONTEXT_ADDFOLDER = "help.context.addPersonalFolder";//CAN DELETE
	public static final String HELP_CONTEXT_RENAMEFOLDER = "help.context.renamePersonalFolder";//CAN DELETE
	public static final String HELP_CONTEXT_DELETEFOLDER = "help.context.deletePersonalFolder";//CAN DELETE
	public static final String HELP_CONTEXT_UPDATEFOLDER = "help.context.updatePersonalFolder";//CAN DELETE
	public static final String HELP_CONTEXT_SELECTED_CITATIONFOLDER = "help.context.selected.citationfolder";
	public static final String HELP_CONTEXT_PATENTRESULTS = "help.context.quickSearchReferencesFormat";//CAN DELETE
	public static final String HELP_CONTEXT_SELECTED_CITATION = "help.context.selected.citation";
	public static final String HELP_CONTEXT_SELECTED_ABSTRACT = "help.context.selected.abstract";
	public static final String HELP_CONTEXT_SELECTED_DETAILED = "help.context.selected.detailed";
	public static final String HELP_CONTEXT_CUSTOMER_PASSWORD = "help.context.customer.password";
	public static final String HELP_CONTEXT_CUSTOMER_PASSWORD_GUIDELINES = "help.context.customer.password.guidelines";
	public static final String HELP_CONTEXT_CUSTOMER_REMINDER = "help.context.customer.reminder";
	public static final String HELP_CONTEXT_CUSTOMER_PROFILE_DISPLAY = "help.context.customer.profile.display";//TODO
	public static final String HELP_CONTEXT_CUSTOMER_AUTHENTICATE_MANRA = "help.context.customer.authenticate.manra";//TODO
	public static final String HELP_CONTEXT_CUSTOMER_AUTHENTICATE_REMINDER = "help.context.customer.authenticate.reminder";//TODO
	public static final String HELP_CONTEXT_CUSTOMER_INSTITUTIONCHOICE = "help.context.customer.institutionchoice";//TODO
	public static final String HELP_CONTEXT_CUSTOMER_AUTHENTICATE_LOGINFULL = "help.context.customer.authenticate.loginfull";//TODO
	public static final String HELP_CONTEXT_CUSTOMER_AUTHENTICATE = "help.context.customer.authenticate";//TODO
	public static final String HELP_CONTEXT_HOME = "help.context.home";//TODO

	public static final String CARS_USER_MIGRATION_FLAG = "CARS_USER_MIGRATION_FLAG";
	public static final String APP_DEFAULT_IMAGE="APP_DEFAULT_IMAGE";
	public static final String REG_ID_ASSOC_CANCEL="REG_ID_ASSOC_CANCEL";
	public static final String CARS_REG_ID_ASSOCIATION_REGISTER_TZ="CARS_REG_ID_ASSOCIATION_REGISTER_TZ";

	public static final String LIBRARIAN_EMAIL="LIBRARIAN_EMAIL";
	public static final String SPECIALIST_EMAIL="SPECIALIST_EMAIL";
	public static final String ENGINEER_EMAIL="ENGINEER_EMAIL";

	public static final String RATELIMITER_ENABLED = "ratelimiter.enabled";
	public static final String RATELIMITER_MAX_RATE = "ratelimiter.max.rate";
	public static final String RATELIMITER_MIN_REQUESTS = "ratelimiter.min.requests";
	public static final String RATELIMITER_RESET = "ratelimiter.reset";
	public static final String RATELIMITER_EMAIL_TO = "ratelimiter.email.to";

	//SSL
    public static final String HTTP_PORT ="HTTP_PORT";
    public static final String HTTPS_PORT ="HTTPS_PORT";

	//
	// Static instance of the RuntimeProperties
	//
	private static RuntimeProperties instance;
	public static int RELEASEVERSIONNUMBER = 1;

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
		try {
			instance.load(new BufferedInputStream(is));
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
	            if (!GenericValidator.isBlankOrNull(rv)) {
	                RELEASEVERSIONNUMBER = Integer.parseInt(rv);
	            } else {
	                log4j.warn("***************  RELEASE VERSION NOT FOUND!! ******************");
	            }
	        } catch (Exception e) {
	            log4j.warn("***************  RELEASE VERSION COULD NOT BE SET!! ******************");
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
	 * Override the getProperty method to use the "runlevel"
	 */
    @Override
    public String getProperty(String key) {
        String prop = super.getProperty(key);
        if (!GenericValidator.isBlankOrNull(runlevel)) {
            String runlevelprop = super.getProperty(key + "." + runlevel);
            if (runlevelprop != null) {
                return runlevelprop.trim();
            }
        }
        if(!GenericValidator.isBlankOrNull(prop)){
        	return prop.trim();
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
        if (!GenericValidator.isBlankOrNull(runlevel)) {
        	key += ("." + runlevel);
        }
		return put(key, value);
	}

    //
    // Convienence methods
    //

	public String getConfigService() {
	    return getDataUrl() + "/engvillage/servlet/ConfigService";
	}

    public String getFastUrl() {
        return instance.getProperty(FAST_BASE_URL);
    }

    public String getCitedbyUrl() {
        return instance.getProperty(CITED_BY_URL);
    }

    public String getAppName() {
        return instance.getProperty(APP_NAME);
    }

    public boolean isAppendSession() {
        return Boolean.parseBoolean(instance.getProperty(APPEND_SESSION));
    }

    public String getDataUrl() {
        return instance.getProperty(DATA_URL);
    }

    public String getLogUrl() {
        return instance.getProperty(LOG_URL);
    }

    public String getAuthUrl() {
        return instance.getProperty(AUTH_URL);
    }

    public String getAppDomain() {
        return instance.getProperty(APP_DOMAIN);
    }

    public String getDatacacheDir() {
        return instance.getProperty(DATA_CACHE_DIR);
    }

    public String getHelpUrl() {
        return instance.getProperty(HELP_URL);
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

