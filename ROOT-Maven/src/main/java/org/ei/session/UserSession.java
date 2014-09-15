package org.ei.session;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.validator.GenericValidator;
import org.ei.ane.entitlements.UserEntitlement;
import org.ei.ane.entitlements.UserEntitlement.ENTITLEMENT_TYPE;
import org.ei.biz.personalization.EVWebUser;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.domain.Sort;
import org.ei.domain.personalization.IUserSessionInfo;
import org.ei.util.StringUtil;
import org.ei.util.SyncTokenFIFOQueue;

/**
 * This class represents an EV user's session. Note that it marks some items transient. This is because this object is eligible to be written to a data store
 * via serialization. In order to keep the size of it to a minimum, the items related to authentication (entitlements, text zones, etc.) are NOT serialized.
 *
 * Tomcat offers a PersistenceManager that can be configured in the application's context.xml to write session information to a data store. For EV this can be
 * enabled by adding the following to WebContent/META-INF/context.xml:
 *
 * &lt;Manager className="org.apache.catalina.session.PersistentManager" saveOnRestart="true" maxIdleBackup="20" minIdleSwap="-1" maxIdleSwap="-1"
 * maxInactiveInterval="1800"&gt; &lt;Store className="org.apache.catalina.session.JDBCStore" dataSourceName="jdbc/session" sessionAppCol="app_name"
 * sessionDataCol="session_data" sessionIdCol="session_id" sessionLastAccessedCol="last_access" sessionMaxInactiveCol="max_inactive"
 * sessionTable="tomcat_sessions" sessionValidCol="valid_session" /&gt; &lt;/Manager&gt;
 *
 * NOTE that we do NOT typically configure this in a local developer's environment!
 *
 * @author harovetm
 *
 */
public class UserSession implements Serializable, IUserSessionInfo {

    private static final long serialVersionUID = -3736768025268651342L;

    public static final String SESSION_END_REDIR = "ENV_SESSION_END_REDIR";

    private static final String SESSIONID_KEY = "SESSION.SK";
    private static final String USER_KEY = "SESSION.UK";
    private static final String LASTTOUCHED_KEY = "SESSION.LK";
    private static final String EXPIREIN_KEY = "SESSION.EX";
    private static final String STATUS_KEY = "SESSION.ST";

    public static final String LOCAL_HOLDING_KEY = "ENV_LHK";

    public static final String ENV_IPADDRESS = "ENV_IPADDRESS";
    public static final String ENV_BASEADDRESS = "ENV_BASEADDRESS";
    public static final String ENV_UAGENT = "ENV_UAGENT";

    public static final String CUSTOM_CUSTOMIZED_KEY = "ENV_CK";
    public static final String CUSTOM_CUSTOMIZED_VALUE = "CUSTOMIZED";
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

    // these five session parameter are being set per each request. should not be session parameters
    public static final String ENV_DAYPASS = "ENV_DAYPASS";
    public static final String SHOW_NEWSEARCH_ALERT = "P_NEWSRCH_ALERT";	// Flag for Clear all basket records
    public static final String SHOW_MAX_ALERT = "P_MAX_ALERT";				// Flag for select Max basket records, empty basket
    public static final String SHOW_MAX_ALERTCLEAR = "P_MAX_ALERTCLEAR";	// Flag for select Max basket records, basket with items

    public static final String SUGGESTION_MAP = "AC_SUGGESTION_MAP";

    // CARS information
    public static final String SESSION_AFFINITY_KEY = "JSESSIONID";

    public static final String AUTO_COMPLETE_ENABLED = "AUTO_COMPLETE_ENABLED";
    public static final String AC_USE_RECOMMENDED_TERM = "AC_USE_RECOMMENDED_TERM";

    // SSO attributes
    public String browserSSOKey;
    public Long carsLastSucessAccessTime;

    private transient Map<String, String> userTextZones = new HashMap<String, String>();
    private transient Set<UserEntitlement> userEntitlements = new HashSet<UserEntitlement>();
    private transient CARSMetadata carsMetaData = new CARSMetadata();
    private transient IEVWebUser user;
    private SyncTokenFIFOQueue fifoQueue = new SyncTokenFIFOQueue();

    // ***************************************************************************
    // This is where all "real" session information should be stored! Anything
    // required to be available when a session is restored from database should
    // go in here - results-per-page, navigator state, etc.
    // ***************************************************************************
    private SessionID sessionid;
    private long lastTouched = System.currentTimeMillis();
    private boolean touched = false;
    private long expireIn;
    private String status = SessionStatus.NEW;	// Default to NEW

    private Properties sessionProperties = new Properties();

    /**
     * Loads session information from a properties object. This is only used in the /engvillage project to restore the session values from incoming request.
     *
     * @param props
     */
    public void loadFromProperties(Properties props) {
        Enumeration<Object> en = props.keys();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();

            if (key.equals(SESSIONID_KEY)) {
                this.sessionid = new SessionID(props.getProperty(key));
            } else if (key.equals(USER_KEY)) {
                this.user = new EVWebUser(props.getProperty(key));
            } else if (key.equals(LASTTOUCHED_KEY)) {
                this.lastTouched = Long.parseLong(props.getProperty(key));
            } else if (key.equals(EXPIREIN_KEY)) {
                this.expireIn = Long.parseLong(props.getProperty(key));
            } else if (key.equals(STATUS_KEY)) {
                this.status = props.getProperty(key);
            } else {
                sessionProperties.put(key.substring(key.indexOf(".") + 1, key.length()), props.get(key));
            }
        }
    }

    /**
     * This is the counterpart to loadFromProperties. This is used before transferring session information to the /engvillage application
     *
     * @return
     */
    public Properties unloadToProperties() {
        Properties props = new Properties();
        if (this.sessionid != null)
            props.put(SESSIONID_KEY, this.sessionid.toString());
        if (this.user != null)
            props.put(USER_KEY, this.user.toString());
        props.put(LASTTOUCHED_KEY, Long.toString(this.lastTouched));
        props.put(EXPIREIN_KEY, Long.toString(this.expireIn));
        if (this.status != null)
            props.put(STATUS_KEY, this.status);
        if (sessionProperties != null) {
            Enumeration<Object> en = sessionProperties.keys();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                props.put("SESSION." + key, sessionProperties.get(key));
            }
        }

        return props;
    }

    /**
     * Retrieve set of entitlements filtered on entitlement type
     *
     * @param entitlementtype
     * @return
     */
    public Set<UserEntitlement> filterUserEntitlements(ENTITLEMENT_TYPE entitlementtype) {
        if (this.userEntitlements == null)
            return Collections.EMPTY_SET;
        Set<UserEntitlement> subset = new HashSet<UserEntitlement>();
        for (UserEntitlement ent : this.userEntitlements) {
            if (ent.getEntitlementType().equals(entitlementtype)) {
                subset.add(ent);
            }
        }
        return subset;
    }

    /**
     * Add user entitlements to current user session
     *
     * @param userEntitlements
     */
    public void addUserEntitlements(Set<UserEntitlement> userEntitlements) {
        if (this.userEntitlements == null)
            userEntitlements = new HashSet<UserEntitlement>();
        this.userEntitlements.addAll(userEntitlements);
    }

    /**
     * Remove specified user entitlements
     *
     * @param userEntitlements
     */
    public void removeUserEntitlements(List<String> userEntitlements) {
        if (this.userEntitlements != null)
            this.userEntitlements.removeAll(userEntitlements);
    }

    public void addUserTextZones(Map<String, String> textzones) {
        if (this.userTextZones == null)
            userTextZones = new HashMap<String, String>();
        userTextZones.putAll(textzones);
    }

    //
    // GETTERS/SETTERS
    //

    // Required from IUserSessionInfo interface
    @Override
    public String getUserid() {
        return getUser().getUserInfo().getUserId();
    }

    @Override
    public String getSessionid() {
        return this.sessionid.getID();
    }

    @Override
    public long getLastTouched() {
        return this.lastTouched;
    }

    @Override
    public long getExpireIn() {
        return this.expireIn;
    }

    @Override
    public String getCustomerid() {
        return this.user.getCustomerID();
    }

    @Override
    public String getStartpage() {
        return this.user.getStartPage();
    }

    @Override
    public String getDefaultdb() {
        return this.user.getDefaultDB();
    }

    @Override
    public String getRecordsPerPage() {
        String pagesize = sessionProperties.getProperty(RECORDS_PER_PAGE);
        if (GenericValidator.isBlankOrNull(pagesize)) {
            pagesize = EVProperties.getProperty(ApplicationProperties.PAGESIZE);
            if (GenericValidator.isBlankOrNull(pagesize)) {
                pagesize = "25";
            }
            this.setProperty(RECORDS_PER_PAGE, pagesize);
        }
        return pagesize;
    }

    @Override
    public String getSortBy() {
        String sSortBy = getCartridgeString().toUpperCase();
        if (sSortBy.indexOf("SRTYR") > -1) {
            sSortBy = Sort.PUB_YEAR_FIELD;
        } else if (sSortBy.indexOf("SRTRE") > -1) {
            sSortBy = Sort.RELEVANCE_FIELD;
        } else {
            sSortBy = Sort.DEFAULT_FIELD;
        }
        return sSortBy;
    }

    @Override
    public String getAutostem() {
        String sAutostem = getCartridgeString().toUpperCase();
        if (sAutostem.indexOf("STMON") > -1) {
            sAutostem = ON;
        } else if (sAutostem.indexOf("STMOFF") > -1) {
            sAutostem = OFF;
        } else {
            sAutostem = AUTOSTEM_DEFAULT;
        }
        return sAutostem;
    }

    @Override
    public String getRefemail() {
        return this.getUser().getEmail();
    }

    @Override
    public String getCartridgeString() {
        return CartridgeBuilder.toString(getCartridge());
    }

    @Override
    public String[] getCartridge() {
        return this.user.getCartridge();
    }

    @Override
    public String getLogo() {
        if (getCartridgeString().indexOf("LGO") > -1) {
            return user.getCustomerID();
        } else {
            return StringUtil.EMPTY_STRING;
        }
    }

    @Override
    public boolean isCustomer() {
        return this.getUser().isCustomer();
    }

    //
    //
    // Other Getters/Setters
    //
    //

    public String getUserIDFromSession() {
        return getUser().getUserInfo().getUserId();
    }

    public CARSMetadata getCarsMetaData() {
        return carsMetaData;
    }

    public void setCarsMetaData(CARSMetadata carsMetaData) {
        this.carsMetaData = carsMetaData;
    }

    public Map<String, String> getUserTextZones() {
        if (userTextZones == null)
            userTextZones = new HashMap<String, String>();
        return userTextZones;
    }

    public Set<UserEntitlement> getUserEntitlements() {
        if (userEntitlements == null)
            userEntitlements = new HashSet<UserEntitlement>();
        return this.userEntitlements;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public boolean getTouched() {
        return this.touched;
    }

    public SessionID getSessionID() {
        return this.sessionid;
    }

    public String getID() {
        return this.sessionid.getID();
    }

    public void setSessionID(SessionID sessionID) {
        this.sessionid = sessionID;
    }

    public void setLastTouched(long lastTouched) {
        this.lastTouched = lastTouched;
    }

    void setExpireIn(long expireIn) {
        this.expireIn = expireIn;
    }

    public String getStatus() {
        return this.status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    public IEVWebUser getUser() {
        return this.user;
    }

    public void setUser(IEVWebUser user) {
        this.user = user;
    }

    //
    // Convience methods to get info out of sessionProperties object
    //

    public String getAuthtoken() {
        return sessionProperties.getProperty(AUTHTOKEN);
    }

    public void setAuthtoken(String authtoken) {
        if (authtoken == null)
            return;
        sessionProperties.setProperty(AUTHTOKEN, authtoken);
    }

    public String getEnvDayPass() {
        return sessionProperties.getProperty(ENV_DAYPASS);
    }

    public void setEnvDayPass(String envDayPass) {
        if (envDayPass == null)
            return;
        sessionProperties.setProperty(ENV_DAYPASS, envDayPass);
    }

    public String getEnvUagent() {
        return sessionProperties.getProperty(ENV_UAGENT);
    }

    public void setEnvUagent(String envUagent) {
        if (envUagent == null)
            return;
        sessionProperties.setProperty(ENV_UAGENT, envUagent);
    }

    public String getEnvBaseAddress() {
        return sessionProperties.getProperty(ENV_BASEADDRESS);
    }

    public void setEnvBaseAddress(String envBaseAddress) {
        if (envBaseAddress == null)
            return;
        sessionProperties.setProperty(ENV_BASEADDRESS, envBaseAddress);
    }

    public String getEnvIPAddress() {
        return sessionProperties.getProperty(ENV_IPADDRESS);
    }

    public void setEnvIPAddress(String envIPAddress) {
        if (envIPAddress == null)
            return;
        sessionProperties.setProperty(ENV_IPADDRESS, envIPAddress);
    }

    public String getEnvSessionEndRedir() {
        return sessionProperties.getProperty(ENV_SESSION_END_REDIR);
    }

    public void setEnvSessionEndRedir(String envSessionEndRedir) {
        if (envSessionEndRedir == null)
            return;
        sessionProperties.setProperty(ENV_SESSION_END_REDIR, envSessionEndRedir);
    }

    public String getLocalHoldingKey() {
        return sessionProperties.getProperty(LOCAL_HOLDING_KEY);
    }

    public void setLocalHoldingKey(String localHoldingKey) {
        if (localHoldingKey == null)
            return;
        sessionProperties.setProperty(LOCAL_HOLDING_KEY, localHoldingKey);
    }

    public String getEntryToken() {
        return sessionProperties.getProperty(ENTRY_TOKEN);
    }

    public void setEntryToken(String entryToken) {
        if (entryToken == null)
            return;
        sessionProperties.setProperty(ENTRY_TOKEN, entryToken);
    }

    public String getTokenExpired() {
        return sessionProperties.getProperty(TOKEN_EXPIRED);
    }

    public void setTokenExpired(String tokenExpired) {
        if (tokenExpired == null)
            return;
        sessionProperties.setProperty(TOKEN_EXPIRED, tokenExpired);
    }

    public String getTokenConcurrent() {
        return sessionProperties.getProperty(TOKEN_CONCURRENT);
    }

    public void setTokenConcurrent(String TokenConcurrent) {
        if (TokenConcurrent == null)
            return;
        sessionProperties.setProperty(TOKEN_CONCURRENT, TokenConcurrent);
    }

    public void setRecordsPerPage(String recordsPerPage) {
        if (recordsPerPage == null)
            return;
        sessionProperties.setProperty(RECORDS_PER_PAGE, recordsPerPage);
    }

    public String getHighlightState() {
        return sessionProperties.getProperty(HIGHLIGHT_STATE);
    }

    public void setHighlightState(String highlightState) {
        if (highlightState == null)
            return;
        sessionProperties.setProperty(HIGHLIGHT_STATE, highlightState);
    }

    public String getNavState() {
        return sessionProperties.getProperty(NAV_STATE);
    }

    public void setNavState(String navState) {
        if (navState == null)
            return;
        sessionProperties.setProperty(NAV_STATE, navState);
    }

    public String getBackUrl() {
        return sessionProperties.getProperty(BACK_URL);
    }

    public void setBackUrl(String backUrl) {
        if (backUrl == null) {
            sessionProperties.remove(BACK_URL);
            return;
        }
        sessionProperties.setProperty(BACK_URL, backUrl);
    }

    public String getNextUrl() {
        return sessionProperties.getProperty(NEXT_URL);
    }

    public void setNextUrl(String nextUrl) {
        if (nextUrl == null) {
            sessionProperties.remove(NEXT_URL);
            return;
        }
        sessionProperties.setProperty(NEXT_URL, nextUrl);
    }

    public String getProperty(String prop) {
        return this.sessionProperties.getProperty(prop);
    }

    public void setProperty(String propKey, String propVal) {
        this.sessionProperties.put(propKey, propVal);
    }

    public void removeProperty(String propKey) {
        this.sessionProperties.remove(propKey);
    }

    public Properties getProperties() {
        return this.sessionProperties;
    }

    public void setProperties(Properties sessionProperties) {
        this.sessionProperties = sessionProperties;
    }

    public String getBrowserSSOKey() {
        return browserSSOKey;
    }

    public void setBrowserSSOKey(String browserSSOKey) {
        this.browserSSOKey = browserSSOKey;
    }

    public Long getCarsLastSucessAccessTime() {
        return carsLastSucessAccessTime;
    }

    public void setCarsLastSucessAccessTime(Long carsLastSucessAccessTime) {
        this.carsLastSucessAccessTime = carsLastSucessAccessTime;
    }

    public HashMap<String, String> getSuggestionMap() {
        HashMap<String, String> suggestions = (HashMap<String, String>) this.sessionProperties.get(SUGGESTION_MAP);
        if (suggestions == null) {
            suggestions = new HashMap<String, String>();
            setSuggestionMap(suggestions);
        }

        return suggestions;
    }

    public void setSuggestionMap(HashMap<String, String> suggestions) {
        this.sessionProperties.put(SUGGESTION_MAP, suggestions);
    }

    public void setAutoCompleteEnabled(boolean enabled) {
        this.sessionProperties.put(AUTO_COMPLETE_ENABLED, new Boolean(enabled));
    }

    public boolean getAutoCompleteEnabled() {
        Boolean enabled = ((Boolean) sessionProperties.get(AUTO_COMPLETE_ENABLED));
        if (enabled == null) {
            return true;
        }

        return enabled;
    }

    public void setUseRecommendedTerm(boolean enabled) {
        this.sessionProperties.put(AC_USE_RECOMMENDED_TERM, new Boolean(enabled));
    }

    public boolean getUseRecommendedTerm() {
        Boolean enabled = ((Boolean) sessionProperties.get(AC_USE_RECOMMENDED_TERM));
        if (enabled == null) {
            return false;
        }

        return enabled;
    }

    public SyncTokenFIFOQueue getFifoQueue() {
        return fifoQueue;
    }

    public void setFifoQueue(SyncTokenFIFOQueue fifoQueue) {
        this.fifoQueue = fifoQueue;
    }

}
