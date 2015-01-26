package org.ei.session;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ei.biz.access.IUserPreferences;

public class UserPreferences implements IUserPreferences, Serializable {
    /**
	 *
	 */
    private static final long serialVersionUID = -3941528220251382776L;

    private static final Logger log4j = Logger.getLogger(UserPreferences.class);

    // Database fences
    public static final String FENCE_COMPENDEX = "CPX";
    public static final String FENCE_COMPENDEX_BACKFILE = "C84";
    public static final String FENCE_EI_BACKFILE = "ZBF";
    public static final String FENCE_INSPEC = "INS";
    public static final String FENCE_INSPEC_BACKFILE = "BFS";
    public static final String FENCE_INSPEC_STANDALONE = "IBS";
    public static final String FENCE_NTIS = "NTI";
    public static final String FENCE_USPATENTS = "UPA";
    public static final String FENCE_EUPATENTS = "EUP";
    public static final String FENCE_CBNB = "CBN";
    public static final String FENCE_GEOREF = "GRF";
    public static final String FENCE_GEOBASE = "GEO";
    public static final String FENCE_PAPER_CHEM = "PCH";
    public static final String FENCE_CHIMICA = "CHM";
    public static final String FENCE_ENCOMPASS_LIT = "ELT";
    public static final String FENCE_ENCOMPASS_PAT = "EPT";
    public static final String FENCE_EBOOK = "PAG";

    // More search source databases
    public static final String FENCE_UPO = "UPO";
    public static final String FENCE_LEXISNEXISNEWS = "LEXIS-NEXIS_NEWS";
    public static final String FENCE_SCIRUS = "SCIRUS";
    public static final String FENCE_ESPACENET = "ESPACENET";
    public static final String FENCE_GLOBAL_SPEC = "GLOBAL_SPEC";
    public static final String FENCE_CRC_ENGnetBASE = "CRC";

    // Feature fences
    public static final String FENCE_THESAURUS = "THESAURUS";
    public static final String FENCE_BULLETINS = "BULLETINS";
    public static final String FENCE_ENCOMPASS_LIT_HTML = "LIT_HTM";
    public static final String FENCE_ENCOMPASS_PAT_HTML = "PAT_HTM";
    public static final String FENCE_ENCOMPASS_LIT_PDF = "LIT_PDF";
    public static final String FENCE_ENCOMPASS_PAT_PDF = "PAT_PDF";
    public static final String FENCE_ASKANEXPERT = "REFERENCE_SERVICES";
    public static final String FENCE_TAGSGROUPS = "TAGS_GROUPS";
    public static final String FENCE_EASY_SEARCH = "EASY_SEARCH";
    public static final String FENCE_RSS_OFF_FEATURE = "RSS";
    public static final String FENCE_BLOGTHIS_OFF = "BLOG_THIS";
    public static final String FENCE_NAV_GRAPHS_DOWNLOAD = "NAVIGATOR_GRAPH_DOWNLOAD";
    public static final String FENCE_CUSTOM_LOGO = "CUSTOMIZED_INSTITUTIONAL_LOGO";
    public static final String FENCE_EMAIL_ALERTS_CC = "EMAIL_ALERT_CC_LIST";
    public static final String FENCE_FULL_TEXT_LINKING_ABSTRACT_DETAILED = "FULL_TEXT_LINKING_ABSTRACT_DETAILED";
    public static final String FENCE_FULL_TEXT_LINKING_CITATION = "CFU_FULL_TEXT_LINKING_CITATION";
    public static final String FENCE_DDS_SERVICE_LHL = "LINDA_HALL_DOC_DELIVERY_SERVICE";
    public static final String FENCE_LHL_EDITABLE = "LINDA_HALL_EDITABLE";
    public static final String FENCE_LOCALHOLDINGS_CIT_FORMAT = "LOCAL_HOLDINGS_CITATION_FORMAT";
    public static final String FENCE_AUTOSTEMMING = "AUTOSTEMMING";
    public static final String FENCE_SORT_BY = "SORT_BY";
    public static final String FENCE_ALLOW_MANRA = "ALLOW_MANRA";
    public static final String FENCE_ALLOW_AUTOCOMPLETE = "ALLOW_AUTOCOMPLETE";
    public static final String FENCE_ALLOW_CPX_AUTOCOMPLETE = "ALLOW_CPX_AUTOCOMPLETE";
    public static final String FENCE_ENABLE_FEATURE_HIGHLIGHT = "ENABLE_FEATURE_HIGHLIGHT";
    public static final String FENCE_ENABLE_MODAL_DIALOG = "ENABLE_MODAL_DIALOG";
    public static final String FENCE_ENABLE_MODAL_DIALOG_2 = "ENABLE_MODAL_DIALOG_2";



    // Referex fences
    public static final String FENCE_REFEREX_PERPETUAL = "BPE";
    public static final String FENCE_REFEREX_CHE = "CHE";
    public static final String FENCE_REFEREX_CHE1 = "CHE1";
    public static final String FENCE_REFEREX_CHE2 = "CHE2";
    public static final String FENCE_REFEREX_CHE3 = "CHE3";
    public static final String FENCE_REFEREX_CHE4 = "CHE4";
    public static final String FENCE_REFEREX_CHE5 = "CHE5";
    public static final String FENCE_REFEREX_CHE6 = "CHE6";
    public static final String FENCE_REFEREX_CHE7 = "CHE7";
    public static final String FENCE_REFEREX_CHE8 = "CHE8";
    public static final String FENCE_REFEREX_CIV = "CIV";
    public static final String FENCE_REFEREX_CIV3 = "CIV3";
    public static final String FENCE_REFEREX_CIV4 = "CIV4";
    public static final String FENCE_REFEREX_CIV5 = "CIV5";
    public static final String FENCE_REFEREX_CIV6 = "CIV6";
    public static final String FENCE_REFEREX_CIV7 = "CIV7";
    public static final String FENCE_REFEREX_CIV8 = "CIV8";
    public static final String FENCE_REFEREX_COM = "COM";
    public static final String FENCE_REFEREX_COM3 = "COM3";
    public static final String FENCE_REFEREX_COM4 = "COM4";
    public static final String FENCE_REFEREX_COM6 = "COM6";
    public static final String FENCE_REFEREX_COM7 = "COM7";
    public static final String FENCE_REFEREX_COM8 = "COM8";
    public static final String FENCE_REFEREX_ELE = "ELE";
    public static final String FENCE_REFEREX_ELE1 = "ELE1";
    public static final String FENCE_REFEREX_ELE2 = "ELE2";
    public static final String FENCE_REFEREX_ELE3 = "ELE3";
    public static final String FENCE_REFEREX_ELE4 = "ELE4";
    public static final String FENCE_REFEREX_ELE5 = "ELE5";
    public static final String FENCE_REFEREX_ELE6 = "ELE6";
    public static final String FENCE_REFEREX_ELE7 = "ELE7";
    public static final String FENCE_REFEREX_ELE8 = "ELE8";
    public static final String FENCE_REFEREX_MAT = "MAT";
    public static final String FENCE_REFEREX_MAT1 = "MAT1";
    public static final String FENCE_REFEREX_MAT2 = "MAT2";
    public static final String FENCE_REFEREX_MAT3 = "MAT3";
    public static final String FENCE_REFEREX_MAT4 = "MAT4";
    public static final String FENCE_REFEREX_MAT5 = "MAT5";
    public static final String FENCE_REFEREX_MAT6 = "MAT6";
    public static final String FENCE_REFEREX_MAT7 = "MAT7";
    public static final String FENCE_REFEREX_MAT8 = "MAT8";
    public static final String FENCE_REFEREX_SEC = "SEC";
    public static final String FENCE_REFEREX_SEC3 = "SEC3";
    public static final String FENCE_REFEREX_SEC4 = "SEC4";
    public static final String FENCE_REFEREX_SEC6 = "SEC6";
    public static final String FENCE_REFEREX_SEC7 = "SEC7";
    public static final String FENCE_REFEREX_SEC8 = "SEC8";
    public static final String FENCE_REFEREX_TNFCHE = "TNFCHE";
    public static final String FENCE_REFEREX_TNFCHE1 = "TNFCHE1";
    public static final String FENCE_REFEREX_TNFCHE7 = "TNFCHE7";
    public static final String FENCE_REFEREX_TNFCIV = "TNFCIV";
    public static final String FENCE_REFEREX_TNFCIV3 = "TNFCIV3";
    public static final String FENCE_REFEREX_TNFCIV4 = "TNFCIV4";
    public static final String FENCE_REFEREX_TNFCIV6 = "TNFCIV6";
    public static final String FENCE_REFEREX_TNFCIV7 = "TNFCIV7";
    public static final String FENCE_REFEREX_TNFELE = "TNFELE";
    public static final String FENCE_REFEREX_TNFMAT = "TNFMAT";
    public static final String FENCE_REFEREX_TNFMAT1 = "TNFMAT1";
    public static final String FENCE_REFEREX_TNFMAT3 = "TNFMAT3";
    public static final String FENCE_REFEREX_TNFMAT4 = "TNFMAT4";
    public static final String FENCE_REFEREX_TNFMAT6 = "TNFMAT6";
    public static final String FENCE_REFEREX_TNFMAT7 = "TNFMAT7";

    // Misc Fences/Text Zones
    public static final String TZ_START_PAGE = "START_PAGE";
    public static final String TZ_LHL_USERNAME = "LINDA_HALL_DDS_USERNAME";
    public static final String TZ_LHL_PASSWORD = "LINDA_HALL_DDS_PASSWORD";
    public static final String DEFAULT_DB = "DefaultDB";
    public static final String AUTOSTEMMING = "AUTOSTEMMING";
    public static final String TZ_CUSTOMER_ID = "CUSTOMER_ID";
    public static final String TZ_REFERENCE_SERVICES_LINK = "REFERENCE_SERVICES_LINK";
    // Local Linking
    public static final String[] TZ_LINK_LABELS = { "LINK_LABEL_1", "LINK_LABEL_2", "LINK_LABEL_3", "LINK_LABEL_4", "LINK_LABEL_5" };
    public static final String[] TZ_DYNAMIC_URLS = { "DYNAMIC_URL_1", "DYNAMIC_URL_2", "DYNAMIC_URL_3", "DYNAMIC_URL_4", "DYNAMIC_URL_5" };
    public static final String[] TZ_DEFAULT_URLS = { "DEFAULT_URL_1", "DEFAULT_URL_2", "DEFAULT_URL_3", "DEFAULT_URL_4", "DEFAULT_URL_5" };
    public static final String[] TZ_IMAGE_URLS = { "IMAGE_URL_1", "IMAGE_URL_2", "IMAGE_URL_3", "IMAGE_URL_4", "IMAGE_URL_5" };


    public static final String EVPREFS_SORT_REL = "relevance";
    public static final String EVPREFS_SORT_DATE_NEW = "date_dw";
    public static final String EVPREFS_SORT_DATE_OLD = "date_up";
    public static final String EVPREFS_SORT_DATE = "yr";

    public static final String EVPREFS_DL_OUTPUT_CIT = "citation";
    public static final String EVPREFS_DL_OUTPUT_ABS = "abstract";
    public static final String EVPREFS_DL_OUTPUT_DET = "detailed";
    public static final String EVPREFS_DL_OUTPUT_DEF = "default";
    public static final String EVPREFS_DL_LOC_PC = "mypc";
    public static final String EVPREFS_DL_LOC_GD = "googledrive";
    public static final String EVPREFS_DL_LOC_DB = "dropbox";
    public static final String EVPREFS_DL_LOC_REF = "refworks";

    public static final String FENCE_HIGHLIGHT_V1 = "HIGHLIGHT_V1";
    public static final String FENCE_HIGHLIGHT_V2 = "HIGHLIGHT_V2";
    public static final String FENCE_FEATURE_SURVEY = "FEATURE_SURVEY";
    public static final String FENCE_EXIT_SURVEY = "EXIT_SURVEY";
    
    public static final String FENCE_KNOVEL_SEARCH_BTN = "KNOVEL_SEARCH_BTN";

    // Shiboleth fence
    public static final String FENCE_INSTITUTIONAL_SHIB_LOGIN_LINK = "FENCE_INSTITUTIONAL_SHIB_LOGIN_LINK";

    public static final String GLOBAL_MESSAGE = "PPD";

    protected Map<String, String> features = new Hashtable<String, String>();
    public boolean bulletinEnt = true;

    public Map<String, String> getFeatures() {
        return features;
    }

    /**
     * Init Hashtable with defaults
     */
    /*
     * public void init() { features = new Hashtable<String, String>();
     *
     *
     * //features.put(INSCPX_FEATURE, Boolean.toString(false)); features.put(FENCE_THESAURUS, Boolean.toString(false));
     *
     * features.put(FENCE_BULLETINS, Boolean.toString(false)); features.put(FENCE_ENCOMPASS_LIT_HTML, Boolean.toString(false));
     * features.put(FENCE_ENCOMPASS_PAT_HTML, Boolean.toString(false)); features.put(FENCE_ENCOMPASS_LIT_PDF, Boolean.toString(false));
     * features.put(FENCE_ENCOMPASS_PAT_PDF, Boolean.toString(false));
     *
     * features.put(FENCE_TAGSGROUPS, Boolean.toString(true)); features.put(FENCE_ASKANEXPERT, Boolean.toString(true)); features.put(FENCE_EASY_SEARCH,
     * Boolean.toString(true));
     *
     * features.put(FENCE_EMAIL_ALERTS_CC, Boolean.toString(false));
     *
     * features.put(FENCE_SORT_BY, Boolean.toString(true)); // true == Relevance sort features.put(AUTOSTEMMING, Boolean.toString(true)); // true ==
     * Autostemming ON
     *
     * // TODO initialize these correctly... features.put(TZ_START_PAGE, "quickSearch"); features.put(DEFAULT_DB, Long.toString(1)); }
     */
    public UserPreferences() {
    }

    @Override
    public String getString(String key) {
        return features.get(key);
    }

    @Override
    public void setString(String key, String value) {
        features.put(key, value);
    }

    @Override
    public boolean getBoolean(String key) {
        if (null != features.get(key)) {
            return Boolean.parseBoolean(features.get(key));
        }
        return false;
    }

    @Override
    public void setBoolean(String key, boolean value) {
        features.put(key, Boolean.toString(value));
    }

    @Override
    public long getLong(String key) {
        return Long.parseLong(features.get(key));
    }

    @Override
    public void setLong(String key, long value) {
        features.put(key, Long.toString(value));
    }

    @Override
    public Set keys() {
        // TODO Auto-generated method stub
        return features.keySet();
    }

    //
    // Convienence methods for JSP calls
    //
    public boolean isBook() {
        return getBoolean(FENCE_EBOOK);
    }

    public boolean isThesaurus() {
        return getBoolean(FENCE_THESAURUS);
    }

    public boolean isTag() {
        return getBoolean(FENCE_TAGSGROUPS);
    }

    public boolean isReference() {
        return getBoolean(FENCE_ASKANEXPERT);
    }

    public boolean isBulletin() {
        return getBoolean(FENCE_BULLETINS);
    }

    public boolean isRelevanceSort() {
        return getBoolean(FENCE_SORT_BY);
    }

    public boolean isAutostemming() {
        return getBoolean(AUTOSTEMMING);
    }

    public boolean isClientCustomLogo() {
        return getBoolean(FENCE_CUSTOM_LOGO);
    }

    public boolean getMoreSearchSources(String key) {
        return getBoolean(key);
    }

    public boolean isBulletinEnt() {
        return bulletinEnt;
    }

    public void setBulletinEnt(boolean bulletinEnt) {
        this.bulletinEnt = bulletinEnt;
    }

    public String getManraEnabled() {
        return getString(FENCE_ALLOW_MANRA);
    }

    public boolean isAutocomplete() {
        return getBoolean(FENCE_ALLOW_AUTOCOMPLETE);
    }

    public boolean isCpxAutocomplete() {
        return getBoolean(FENCE_ALLOW_CPX_AUTOCOMPLETE);
    }

    public boolean isFeatureHighlight(){
    	return getBoolean(FENCE_ENABLE_FEATURE_HIGHLIGHT);
    }

    public boolean isModalDialog(){
    	return getBoolean(FENCE_ENABLE_MODAL_DIALOG);
    }

    public boolean isModalDialog2(){
    	return getBoolean(FENCE_ENABLE_MODAL_DIALOG_2);
    }
    public boolean isExitSurvey(){
    	return getBoolean(FENCE_EXIT_SURVEY);
    }
    public boolean isFeatureSurvey(){
    	return getBoolean(FENCE_FEATURE_SURVEY);
    }
    public boolean isKnovelSearchButton(){
    	return getBoolean(FENCE_KNOVEL_SEARCH_BTN);
    }
}
