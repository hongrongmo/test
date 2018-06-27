/**
 *
 */
package org.ei.domain.personalization;

/**
 * @author harovetm
 *
 */
public interface IUserSessionInfo {
    public final String OFF = "off";
    public final String ON = "on";
    public final String AUTOSTEM_DEFAULT = ON;

    public static final String SESSION_END_REDIR = "ENV_SESSION_END_REDIR";

    public static final String SESSIONID_KEY = "SESSION.SK";
    public static final String USER_KEY = "SESSION.UK";
    public static final String LASTTOUCHED_KEY = "SESSION.LK";
    public static final String EXPIREIN_KEY ="SESSION.EX";
    public static final String STATUS_KEY = "SESSION.ST";

    public static final String LOCAL_HOLDING_KEY = "ENV_LHK";

    public static final String ENV_IPADDRESS = "ENV_IPADDRESS";
    public static final String ENV_BASEADDRESS = "ENV_BASEADDRESS";
    public static final String ENV_UAGENT = "ENV_UAGENT";

    public static final String CUSTOM_CUSTOMIZED_KEY = "ENV_CK";
    public static final String CUSTOM_CUSTOMIZED_VALUE= "CUSTOMIZED";
    public static final String CUSTOM_CARTRIDGE_KEY = "ENV_CCK";
    public static final String CUSTOM_LOGO_KEY = "ENV_CLK";
    public static final String CUSTOM_START_PAGE_KEY = "ENV_CSPK";
    public static final String CUSTOM_CLASSIFICATION_KEY = "ENV_CLASSK";
    public static final String CUSTOM_YEARS_KEY = "ENV_CYK";
    public static final String CUSTOM_DISPLAYED_PAGES_KEY = "ENV_CDPK";
    public static final String CUSTOM_FULL_TEXT_KEY = "ENV_CFTK";
    public static final String CUSTOM_LOCAL_HOLDING_FLAG_KEY = "ENV_CLHK";
    public static final String CUSTOM_DDS_KEY = "ENV_CDK";
    public static final String CUSTOM_PERSONALIZATION_KEY = "ENV_CPK";
    public static final String CUSTOM_EMAIL_ALERT_KEY = "ENV_CEAK";

    public static final String AUTHTOKEN = "P_AUTHTOKEN";
    public static final String BACK_URL = "P_BACKURL";
    public static final String NEXT_URL = "P_NEXTURL";
    public static final String NAV_STATE = "P_NAV_STATE";
    public static final String HIGHLIGHT_STATE = "P_HIGHLIGHT_STATE";
    public static final String RECORDS_PER_PAGE = "RECORDS_PER_PAGE";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String TOKEN_CONCURRENT = "TOKEN_CONCURRENT";
    public static final String ENTRY_TOKEN = "ENTRY_TOKEN";
    public static final String ENV_SESSION_END_REDIR = "ENV_SESSION_END_REDIR";

    //these five session parameter are being set per each request. should not be session parameters
    public static final String ENV_DAYPASS="ENV_DAYPASS";
    public static final String SHOW_NEWSEARCH_ALERT = "P_NEWSRCH_ALERT";    // Flag for Clear all basket records
    public static final String SHOW_MAX_ALERT = "P_MAX_ALERT";              // Flag for select Max basket records, empty basket
    public static final String SHOW_MAX_ALERTCLEAR = "P_MAX_ALERTCLEAR";    // Flag for select Max basket records, basket with items


    /**
     * Return the session ID
     * @return
     */
    public String getSessionid();

    public String getUserid();

    public String getCustomerid();

    public long getLastTouched();

    public long getExpireIn();

    public String getStartpage();

    public String getDefaultdb();

    public String getSortBy();

    public String getAutostem();

    public String getRecordsPerPage();

    public String getRefemail();

    public String getCartridgeString();

    public String[] getCartridge();

    @Deprecated
    public String getLogo();

    public String getStatus();

    public boolean isCustomer();
}
