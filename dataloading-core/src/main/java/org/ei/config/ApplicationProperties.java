package org.ei.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

/**
 * This class provides properties for this common layer. It is expected that the application using eilib will initialize it appropriately.
 *
 * @author harovetm
 *
 */
public class ApplicationProperties extends Properties {

    /**
     *
     */
    private static final long serialVersionUID = -4566335097887869477L;

    /** The log4j. */
    private static Logger log4j = Logger.getLogger(ApplicationProperties.class);

    //
    //
    // CONSTANTS for retrieving properties
    //
    //

    public final static String RUNLEVEL_PROD = "prod";
    public final static String RUNLEVEL_CERT = "cert";
    public final static String RUNLEVEL_DEV = "dev";
    public final static String RUNLEVEL_LOCAL = "local";

    /** The Constant SYSTEM_ENVIRONMENT_RUNLEVEL. */
    public static final String SYSTEM_ENVIRONMENT_RUNLEVEL = "com.elsevier.env";

    /** The Constant RELEASE_VERSION. */
    public static final String RELEASE_VERSION = "release.version";

    /** The Constant ROOT_LOG_LEVEL. */
    public static final String ROOT_LOG_LEVEL = "root.log.level";

    /** The Constant ROOT_LOG_TO_CONSOLE. */
    public static final String ROOT_LOG_TO_CONSOLE = "root.log.to.console";

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

    /** The Constant APPEND_SESSION. */
    public static final String APPEND_SESSION = "append.session";

    /** The Constant DATA_URL. */
    public static final String DATA_URL = "data.url";

    /** The Constant DATA_CACHE_DIR. */
    public static final String DATA_CACHE_DIR = "data.cache.dir";

    /** The Constant LOG_URL. */
    public static final String LOG_URL = "log.url";

    /** The Constant AUTH_URL. */
    public static final String AUTH_URL = "auth.url";

    /** The Constant LOGSERVICE_LOG_PATH. */
    public static final String LOGSERVICE_LOG_PATH = "logservice.log.path";

    /** The Constant USPTO_LUCENE_INDEX_DIR. */
    public static final String USPTO_LUCENE_INDEX_DIR = "uspto.lucene.index.dir";

    /** The Constant IPC_LUCENE_INDEX_DIR. */
    public static final String IPC_LUCENE_INDEX_DIR = "ipc.lucene.index.dir";

    /** The Constant ECLA_LUCENE_INDEX_DIR. */
    public static final String ECLA_LUCENE_INDEX_DIR = "ecla.lucene.index.dir";

    /** The Constant REFEREX_MASK_FLAG. */
    public static final String REFEREX_MASK_FLAG = "referex.mask.flag";

    /** The Constant REFEREX_MASK_DATE. */
    public static final String REFEREX_MASK_DATE = "referex.mask.date";

    /** The Constant CUSTOMER_IMAGES_URL_PATH. */
    public static final String CUSTOMER_IMAGES_URL_PATH = "customer.images.url.path";

    /** The Constant CUSTOMER_IMAGES_PREFIX. */
    public static final String CUSTOMER_IMAGES_PREFIX = "customer.images.prefix";

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

    /** The Constant LIBRARIAN_EMAIL. */
    public static final String LIBRARIAN_EMAIL="LIBRARIAN_EMAIL";

    /** The Constant SPECIALIST_EMAIL. */
    public static final String SPECIALIST_EMAIL="SPECIALIST_EMAIL";

    /** The Constant ENGINEER_EMAIL. */
    public static final String ENGINEER_EMAIL="ENGINEER_EMAIL";

    /** The Constant CONTACT_US_LINK. */
    public static final String CONTACT_US_LINK = "contact.us.link";

    /** The Constant HTTP_PORT. */
    public static final String HTTP_PORT ="HTTP_PORT";

    /** The Constant HTTPS_PORT. */
    public static final String HTTPS_PORT ="HTTPS_PORT";

    /** The runlevel. */
    /**
     * Returns the system runlevel.  This is usually set at application startup via
     * "-Drun.level=xxx" where xxx equals 'local', 'cert', 'cert2', 'prod' or 'prod2'
     *
     * @return the runlevel
     */
    public String getRunlevel() {
        String runlevel = this.getProperty(SYSTEM_ENVIRONMENT_RUNLEVEL);
        if (GenericValidator.isBlankOrNull(runlevel)) {
            log4j.warn(SYSTEM_ENVIRONMENT_RUNLEVEL + " property is not set!");
        }
        return runlevel;
    }

    public void setRunlevel(String runlevel) {
        this.setProperty(SYSTEM_ENVIRONMENT_RUNLEVEL, runlevel);
    }

    /** The instance. */
    private static ApplicationProperties instance;

    /**
     * Private constructor
     */
    private ApplicationProperties() {
    }

    /**
     * Gets the single instance of RuntimeProperties.
     *
     * @return single instance of RuntimeProperties
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static synchronized ApplicationProperties getInstance() {
        if (instance == null) {
            instance = new ApplicationProperties();
        }
        return instance;
    }

    public static synchronized ApplicationProperties getInstance(String propsFile) throws IOException {
        if (instance == null) {
            instance = new ApplicationProperties();
            InputStream is = new BufferedInputStream(new FileInputStream(propsFile));
            instance.load(new BufferedInputStream(is));
        }
        return instance;
    }

    /**
     * Refresh properties from a Properties file input
     */
    public static synchronized void load(Properties properties) {
        ApplicationProperties applicationproperties = ApplicationProperties.getInstance();
        Iterator<Object> it = properties.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            applicationproperties.setProperty((String) key, properties.get(key).toString());
        }
    }

    /**
     * Prints this property list out to the specified output stream. This method is useful for debugging.
     *
     * @param out
     *            an output stream.
     */
    public synchronized void list(PrintStream out) {
        if (instance == null) {
            throw new RuntimeException("RuntimeProperties has not been initialized!");
        }
        for (Enumeration<?> e = instance.propertyNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String val = instance.getProperty(key);
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

}
