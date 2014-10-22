package org.ei.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.ane.entitlements.UserEntitlement;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.books.collections.ReferexCollection;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;

/**
 * This class exists for legacy EV purposes. It transposes the fence, text zone and entitlement values from the Customer System into a legacy "cartridge" string
 * usable by existing EV biz logic.
 *
 * @author harovetm
 *
 */
public final class CartridgeBuilder {
    private static final Logger log4j = Logger.getLogger(CartridgeBuilder.class);

    public final static String DB_COMPENDEX = "CPX";
    public final static String DB_COMPENDEX_BACKFILE = "C84";
    public final static String DB_EI_BACKFILE = "ZBF";
    public final static String DB_INSPEC = "INS";
    public final static String DB_INSPEC_BACKFILE = "IBF";
    public final static String DB_INSPEC_STANDALONE = "IBS";
    public final static String DB_NTIS = "BFS";
    public final static String DB_USPATENTS = "UPA";
    public final static String DB_EUPATENTS = "EUP";
    public final static String DB_CBNB = "CBN";
    public final static String DB_GEOREF = "GEO";
    public final static String DB_GEOBASE = "GRF";
    public final static String DB_PAPER_CHEM = "PCH";
    public final static String DB_CHIMICA = "CHM";
    public final static String DB_ENCOMPASS_LIT = "ELT";
    public final static String DB_ENCOMPASS_PAT = "EPT";
    public final static String DB_REFEREX = "PAG";

    public final static String FEATURE_CUSTOM_LOGO = "LGO";
    public final static String FEATURE_THESAURUS = "THS";
    public final static String FEATURE_ENCOMPASS_LIT_HTML = "LIT_HTM";
    public final static String FEATURE_ENCOMPASS_PAT_HTML = "PAT_HTM";
    public final static String FEATURE_ENCOMPASS_LIT_PDF = "LIT_PDF";
    public final static String FEATURE_ENCOMPASS_PAT_PDF = "PAT_PDF";
    public final static String FEATURE_TAGSGROUPS = "TAG";
    public final static String FEATURE_EASY_SEARCH = "EZY";
    public final static String FEATURE_RSS_OFF_FEATURE = "RSS";
    public final static String FEATURE_BLOGTHIS_OFF = "BLG";
    public final static String FEATURE_NAV_GRAPHS_DOWNLOAD = "GAR";
    public final static String FEATURE_EMAIL_ALERTS_CC = "CCL";
    public final static String FEATURE_FULL_TEXT_LINKING_ABSTRACT_DETAILED = "FUL";
    public final static String FEATURE_DDS_SERVICE_LHL = "LHL";
    public final static String FEATURE_FULL_TEXT_LINKING_CITATION = "CFU";
    public final static String FEATURE_LOCALHOLDINGS_CIT_FORMAT = "LHC";
    public final static String FEATURE_LEXIS_NEXIS_NEWS = "LEX";

    public final static String FEATURE_AUTOSTEMMING_ON = "STMOn";
    public final static String FEATURE_AUTOSTEMMING_OFF = "STMOff";
    public final static String FEATURE_SORTBY_REL = "SRTre";
    public final static String FEATURE_SORTBY_YR = "SRTyr";

    public final static String TEXTZONE_PREFIX_FOR_COMPENDEX_SELECTED_START_YEAR = "CSY";
    public final static String TEXTZONE_PREFIX_FOR_INSPEC_SELECTED_START_YEAR = "ISY";
    public final static String TEXTZONE_PREFIX_FOR_NTIS_SELECTED_START_YEAR = "NSY";
    public final static String TEXTZONE_PREFIX_FOR_US_PATENTS_SELECTED_START_YEAR = "USY";
    public final static String TEXTZONE_PREFIX_FOR_EUROPEAN_PATENTS_SELECTED_START_YEAR = "ESY";
    public final static String TEXTZONE_PREFIX_FOR_CBNB_SELECTED_START_YEAR = "BSY";
    public final static String TEXTZONE_PREFIX_FOR_GEOBASE_SELECTED_START_YEAR = "GSY";
    public final static String TEXTZONE_PREFIX_FOR_PAPER_CHEM_SELECTED_START_YEAR = "ASY";
    public final static String TEXTZONE_PREFIX_FOR_REFEREX_SELECTED_START_YEAR = "PSY";
    public final static String TEXTZONE_PREFIX_FOR_ENG_IND_BACKFILE_START = "ZSY";
    public final static String TEXTZONE_PREFIX_FOR_CHIMICA_SELECTED_START_YEAR = "HSY";
    public final static String TEXTZONE_PREFIX_FOR_ENCOMPASS_PAT_SELECTED_START_YEAR = "MSY";
    public final static String TEXTZONE_PREFIX_FOR_ENCOMPASS_LIT_SELECTED_START_YEAR = "LSY";
    public final static String TEXTZONE_PREFIX_FOR_INSPEC_ARC_STAND_START_YEAR = "FSY";
    public final static String TEXTZONE_PREFIX_FOR_GEOREF_SELECTED_START_YEAR = "XSY";

    public final static String COMPENDEX_SELECTED_START_YEAR = "COMPENDEX_SELECTED_START_YEAR";
    public final static String INSPEC_SELECTED_START_YEAR = "INSPEC_SELECTED_START_YEAR";
    public final static String NTIS_SELECTED_START_YEAR = "NTIS_SELECTED_START_YEAR";
    public final static String US_PATENTS_SELECTED_START_YEAR = "US_PATENTS_SELECTED_START_YEAR";
    public final static String EUROPEAN_PATENTS_SELECTED_START_YEAR = "EUROPEAN_PATENTS_SELECTED_START_YEAR";
    public final static String CBNB_SELECTED_START_YEAR = "CBNB_SELECTED_START_YEAR";
    public final static String GEOBASE_SELECTED_START_YEAR = "GEOBASE_SELECTED_START_YEAR";
    public final static String PAPER_CHEM_SELECTED_START_YEAR = "PAPER_CHEM_SELECTED_START_YEAR";
    public final static String REFEREX_SELECTED_START_YEAR = "REFEREX_SELECTED_START_YEAR";
    public final static String ENG_IND_BACKFILE_START_YEAR = "ENG_IND_BACKFILE_START";
    public final static String CHIMICA_SELECTED_START_YEAR = "CHIMICA_SELECTED_START_YEAR";
    public final static String ENCOMPASS_PAT_SELECTED_START_YEAR = "ENCOMPASS_PAT_SELECTED_START_YEAR";
    public final static String ENCOMPASS_LIT_SELECTED_START_YEAR = "ENCOMPASS_LIT_SELECTED_START_YEAR";
    public final static String INSPEC_ARC_STAND_START_YEAR = "INSPEC_ARC_STAND_START_YEAR";
    public final static String GEOREF_SELECTED_START_YEAR = "GEOREF_SELECTED_START_YEAR";

    public static final Pattern REFEREX_DATABASES_PATTERN = Pattern.compile("(ELE|MAT|CHE|COM|SEC|CIV|TNFELE|TNFMAT|TNFCHE|TNFCOM|TNFSEC|TNFCIV)(\\d?)");

    /**
     * Builds a cartridge String from an IEVWebUser object. NOTE!!!!!! This method assumes the user has already been authenticated and thus contains valid
     * fence/text zone/entitlements!
     *
     * @param user
     * @return
     * @throws SessionException
     * @throws SessionException
     */

    public static List<String> buildUserCartridge(UserSession userSession) throws SessionException {
        List<String> cartridge = new ArrayList<String>();
        List<String> buildCartridgesForFences = buildCartridgesForFences(getUserFromUserSession(userSession));

        if (!buildCartridgesForFences.isEmpty()) {
            cartridge.addAll(buildCartridgesForFences);
        }

        List<String> buildCartridgesForTextZones;
        try {
            buildCartridgesForTextZones = buildCartridgesForTextZones(userSession);
            if (!buildCartridgesForTextZones.isEmpty()) {
                cartridge.addAll(buildCartridgesForTextZones);
            }
        } catch (SessionException e) {
            throw new SessionException(SystemErrorCodes.CARTRIDGE_ERROR, "cartridge building Process failed", e);
        }

        if (!getUserEntitlements(userSession).isEmpty()) {
            // Add Referex to entitlements if any Referex collection came in from Fences
            if (ReferexCollection.ALLCOLS_PATTERN.matcher(cartridge.toString()).find()) {
                userSession.getUserEntitlements().add(UserEntitlement.PAG_ENTITLEMENT);
            }
            for (UserEntitlement ent : userSession.getUserEntitlements()) {
                cartridge.add(ent.getName());
            }
        }

        return cartridge;

    }

    /**
     * This method takes a database mask from a previously created RSS feed and converts
     * it to a set of credentials.
     * @param dbmask
     * @return
     */
    public static List<String> buildUserCartridgeForRSS(int dbmask) {
        if (dbmask <= 0) {
            throw new IllegalArgumentException("DB mask is incorrect!");
        }

        List<String> cartridge = new ArrayList<String>();
        if ((dbmask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) {
            cartridge.add(DatabaseConfig.CPX_PREF);
        }
        if ((dbmask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            cartridge.add(DatabaseConfig.INS_PREF);
        }
        if ((dbmask & DatabaseConfig.CBN_MASK) == DatabaseConfig.CBN_MASK) {
            cartridge.add(DatabaseConfig.CBN_PREF);
        }
        if ((dbmask & DatabaseConfig.CHM_MASK) == DatabaseConfig.CHM_MASK) {
            cartridge.add(DatabaseConfig.CHM_PREF);
        }
        if ((dbmask & DatabaseConfig.ELT_MASK) == DatabaseConfig.ELT_MASK) {
            cartridge.add(DatabaseConfig.ELT_PREF);
        }
        if ((dbmask & DatabaseConfig.EPT_MASK) == DatabaseConfig.EPT_MASK) {
            cartridge.add(DatabaseConfig.EPT_PREF);
        }
        if ((dbmask & DatabaseConfig.EUP_MASK) == DatabaseConfig.EUP_MASK) {
            cartridge.add(DatabaseConfig.EUP_PREF);
        }
        if ((dbmask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK) {
            cartridge.add(DatabaseConfig.GEO_PREF);
        }
        if ((dbmask & DatabaseConfig.GRF_MASK) == DatabaseConfig.GRF_MASK) {
            cartridge.add(DatabaseConfig.GRF_PREF);
        }
        if ((dbmask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK) {
            cartridge.add(DatabaseConfig.NTI_PREF);
        }
        if ((dbmask & DatabaseConfig.PCH_MASK) == DatabaseConfig.PCH_MASK) {
            cartridge.add(DatabaseConfig.PCH_PREF);
        }
        if ((dbmask & DatabaseConfig.UPA_MASK) == DatabaseConfig.UPA_MASK) {
            cartridge.add(DatabaseConfig.UPT_PREF);
        }
        return cartridge;
    }

    /**
     * Convert cartridge List to String
     *
     * @param cartridge
     * @return
     */
    public static String toString(List<String> cartridge) {
        StringBuffer buf = new StringBuffer();
        if (cartridge == null) {
            return "-";
        } else {
            for (int x = 0; x < cartridge.size(); ++x) {
                if (x > 0) {
                    buf.append(";");
                }
                buf.append(cartridge.get(x));
            }

        }
        return buf.toString();
    }

    /**
     * Convert cartridge Array to String
     *
     * @param cartridge
     * @return
     */
    public static String toString(String[] cartridge) {
        StringBuffer buf = new StringBuffer();
        if (cartridge == null) {
            return "-";
        } else {

            for (int x = 0; x < cartridge.length; ++x) {
                if (x > 0) {
                    buf.append(";");
                }
                buf.append(cartridge[x]);
            }

        }

        return buf.toString();
    }

    /**
     * Return user Entitlements
     *
     * @param userSession
     * @return
     */
    private static Set<UserEntitlement> getUserEntitlements(UserSession userSession) {
        return userSession.getUserEntitlements();
    }

    /**
     * Build a List object from Text Zone values from Customer System.
     *
     * @param userSession
     * @return
     * @throws SessionException
     */
    public static List<String> buildCartridgesForTextZones(UserSession userSession) throws SessionException {
        List<String> textZoneCartridge = new ArrayList<String>();

        if (!getUserTextZones(userSession).isEmpty()) {

            String compendexSelectedYear = getSelectedStartYearValue(userSession, COMPENDEX_SELECTED_START_YEAR);

            if (StringUtils.isNotBlank(compendexSelectedYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_COMPENDEX_SELECTED_START_YEAR + compendexSelectedYear);
            }

            String inspecSelectedStartYear = getSelectedStartYearValue(userSession, INSPEC_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(inspecSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_INSPEC_SELECTED_START_YEAR + inspecSelectedStartYear);
            }

            String ntisSelectedStartYear = getSelectedStartYearValue(userSession, NTIS_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(ntisSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_NTIS_SELECTED_START_YEAR + ntisSelectedStartYear);
            }

            String usPatentSelectedStartYear = getSelectedStartYearValue(userSession, US_PATENTS_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(usPatentSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_US_PATENTS_SELECTED_START_YEAR + usPatentSelectedStartYear);
            }

            String europeanPatentSelectedStartYear = getSelectedStartYearValue(userSession, EUROPEAN_PATENTS_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(europeanPatentSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_EUROPEAN_PATENTS_SELECTED_START_YEAR + europeanPatentSelectedStartYear);
            }

            String cbnbSelectedStartYear = getSelectedStartYearValue(userSession, CBNB_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(cbnbSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_CBNB_SELECTED_START_YEAR + cbnbSelectedStartYear);
            }

            String geobaseSelectedStartYear = getSelectedStartYearValue(userSession, GEOBASE_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(geobaseSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_GEOBASE_SELECTED_START_YEAR + geobaseSelectedStartYear);
            }

            String paperChemSelectedStartYear = getSelectedStartYearValue(userSession, PAPER_CHEM_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(paperChemSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_PAPER_CHEM_SELECTED_START_YEAR + paperChemSelectedStartYear);
            }

            String referexSelectedStartYear = getSelectedStartYearValue(userSession, REFEREX_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(referexSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_REFEREX_SELECTED_START_YEAR + referexSelectedStartYear);
            }

            String engIndBackfileStartYear = getSelectedStartYearValue(userSession, ENG_IND_BACKFILE_START_YEAR);
            if (StringUtils.isNotBlank(engIndBackfileStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_ENG_IND_BACKFILE_START + engIndBackfileStartYear);
            }

            String chimicaSelectedStartYear = getSelectedStartYearValue(userSession, CHIMICA_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(chimicaSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_CHIMICA_SELECTED_START_YEAR + chimicaSelectedStartYear);
            }

            String encompassPatSelectedStartYear = getSelectedStartYearValue(userSession, ENCOMPASS_PAT_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(encompassPatSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_ENCOMPASS_PAT_SELECTED_START_YEAR + encompassPatSelectedStartYear);
            }

            String emcompassLitSelectedStartYear = getSelectedStartYearValue(userSession, ENCOMPASS_LIT_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(emcompassLitSelectedStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_ENCOMPASS_LIT_SELECTED_START_YEAR + emcompassLitSelectedStartYear);
            }

            String inspecArcStandStartYear = getSelectedStartYearValue(userSession, INSPEC_ARC_STAND_START_YEAR);
            if (StringUtils.isNotBlank(inspecArcStandStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_INSPEC_ARC_STAND_START_YEAR + inspecArcStandStartYear);
            }

            String georefSelectedStartYear = getSelectedStartYearValue(userSession, GEOREF_SELECTED_START_YEAR);
            if (StringUtils.isNotBlank(inspecArcStandStartYear)) {
                textZoneCartridge.add(TEXTZONE_PREFIX_FOR_GEOREF_SELECTED_START_YEAR + georefSelectedStartYear);
            }
        }

        return textZoneCartridge;
    }

    /**
     * Build List from user Fences from Customer System
     *
     * @param user
     * @return
     */
    public static List<String> buildCartridgesForFences(IEVWebUser user) {
        // Go through UserPreferences to create Cartridge string
        if (user == null || user.getUserPreferences() == null) {
            log4j.warn("User or UserPreferences are empty!");
            return new ArrayList<String>();
        }

        List<String> fencesCartridge = new ArrayList<String>();

        //
        // Add Features
        //
        if (!user.getPreference(UserPreferences.FENCE_EMAIL_ALERTS_CC))
            fencesCartridge.add(FEATURE_EMAIL_ALERTS_CC);
        if (!user.getPreference(UserPreferences.FENCE_RSS_OFF_FEATURE))
            fencesCartridge.add(FEATURE_RSS_OFF_FEATURE);
        if (!user.getPreference(UserPreferences.FENCE_BLOGTHIS_OFF))
            fencesCartridge.add(FEATURE_BLOGTHIS_OFF);
        if (!user.getPreference(UserPreferences.FENCE_FULL_TEXT_LINKING_ABSTRACT_DETAILED))
            fencesCartridge.add(FEATURE_FULL_TEXT_LINKING_ABSTRACT_DETAILED);
        if (!user.getPreference(UserPreferences.FENCE_FULL_TEXT_LINKING_CITATION))
            fencesCartridge.add(FEATURE_FULL_TEXT_LINKING_CITATION);
        if (user.getPreference(UserPreferences.FENCE_DDS_SERVICE_LHL))
            fencesCartridge.add(FEATURE_DDS_SERVICE_LHL);
        if (user.getPreference(UserPreferences.FENCE_LOCALHOLDINGS_CIT_FORMAT))
            fencesCartridge.add(FEATURE_LOCALHOLDINGS_CIT_FORMAT);
        if (user.getPreference(UserPreferences.FENCE_CUSTOM_LOGO))
            fencesCartridge.add(FEATURE_CUSTOM_LOGO);
        if (user.getPreference(UserPreferences.FENCE_THESAURUS))
            fencesCartridge.add(FEATURE_THESAURUS);
        if (user.getPreference(UserPreferences.FENCE_LEXISNEXISNEWS))
            fencesCartridge.add(FEATURE_LEXIS_NEXIS_NEWS);
        if (user.getPreference(UserPreferences.FENCE_TAGSGROUPS))
            fencesCartridge.add(FEATURE_TAGSGROUPS);
        if (user.getPreference(UserPreferences.FENCE_NAV_GRAPHS_DOWNLOAD))
            fencesCartridge.add(FEATURE_NAV_GRAPHS_DOWNLOAD);
        if (user.getPreference(UserPreferences.FENCE_AUTOSTEMMING)) {
            fencesCartridge.add(FEATURE_AUTOSTEMMING_ON);
        } else {
            fencesCartridge.add(FEATURE_AUTOSTEMMING_OFF);
        }
        if (user.getPreference(UserPreferences.FENCE_SORT_BY)) {
            fencesCartridge.add(FEATURE_SORTBY_REL);
        } else {
            fencesCartridge.add(FEATURE_SORTBY_YR);
        }

        // Bulletin Fences
        if (user.getPreference(UserPreferences.FENCE_ENCOMPASS_LIT_HTML))
            fencesCartridge.add(FEATURE_ENCOMPASS_LIT_HTML);
        if (user.getPreference(UserPreferences.FENCE_ENCOMPASS_PAT_HTML))
            fencesCartridge.add(FEATURE_ENCOMPASS_PAT_HTML);
        if (user.getPreference(UserPreferences.FENCE_ENCOMPASS_LIT_PDF))
            fencesCartridge.add(FEATURE_ENCOMPASS_LIT_PDF);
        if (user.getPreference(UserPreferences.FENCE_ENCOMPASS_PAT_PDF))
            fencesCartridge.add(FEATURE_ENCOMPASS_PAT_PDF);

        //
        // Fetch Referex entitlements (yes, they are actually Fences)
        //
        boolean isPerpetual = user.getPreference(UserPreferences.FENCE_REFEREX_PERPETUAL);
        if (!isPerpetual) {
            // If the "Perpetual Books" flag is OFF, then any Base collection will also include sub-collections
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE1);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE2);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE3);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE4);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE5);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE6);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE7);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE8);
            }
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CIV)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV3);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV4);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV5);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV6);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV7);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV8);
            }
            if (user.getPreference(UserPreferences.FENCE_REFEREX_COM)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM3);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM4);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM6);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM7);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM8);
            }
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE1);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE2);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE3);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE4);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE5);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE6);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE7);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE8);
            }
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT1);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT2);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT3);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT4);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT5);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT6);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT7);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT8);
            }
            if (user.getPreference(UserPreferences.FENCE_REFEREX_SEC)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC3);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC4);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC6);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC7);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC8);
            }
            // Do the same for TNF titles. Base selected = all sub-collections.
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCHE)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCHE);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCHE1);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCHE7);
            }
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFELE)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFELE);
            }
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCIV)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV3);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV4);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV6);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV7);
            }
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFMAT)) {
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT1);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT3);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT4);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT6);
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT7);
            }
        } else {
            // "Perpetual Books" is ON - just add any Referex fence set to yes to the cartridge
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE1))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE1);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE2))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE2);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE3))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE3);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE4))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE4);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE5))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE5);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE6))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE6);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE7))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE7);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CHE8))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CHE8);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CIV))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CIV3))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV3);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CIV4))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV4);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CIV6))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV6);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CIV7))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV7);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_CIV8))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_CIV8);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_COM))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_COM3))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM3);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_COM4))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM4);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_COM6))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM6);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_COM7))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM7);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_COM8))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_COM8);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE1))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE1);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE2))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE2);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE3))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE3);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE4))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE4);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE5))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE5);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE6))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE6);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE7))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE7);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_ELE8))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_ELE8);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT1))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT1);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT2))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT2);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT3))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT3);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT4))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT4);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT5))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT5);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT6))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT6);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT7))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT7);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_MAT8))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_MAT8);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_SEC))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_SEC3))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC3);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_SEC4))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC4);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_SEC6))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC6);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_SEC7))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC7);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_SEC8))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_SEC8);
            // Add TNF fences.
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCHE))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCHE);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCHE1))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCHE1);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCHE7))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCHE7);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCIV))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCIV3))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV3);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCIV4))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV4);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCIV6))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV6);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFCIV7))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFCIV7);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFELE))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFELE);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFMAT))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFMAT1))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT1);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFMAT3))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT3);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFMAT4))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT4);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFMAT6))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT6);
            if (user.getPreference(UserPreferences.FENCE_REFEREX_TNFMAT7))
                fencesCartridge.add(UserPreferences.FENCE_REFEREX_TNFMAT7);
        }

        // ALWAYS set the Perpetual flag!!!
        fencesCartridge.add(UserPreferences.FENCE_REFEREX_PERPETUAL);

        // Set access to eBook from cartridge string
        String temp = fencesCartridge.toString();
        if (ReferexCollection.ALLCOLS_PATTERN.matcher(temp).find()) {
            if (!Pattern.compile(UserPreferences.FENCE_EBOOK).matcher(temp).find()) {
                fencesCartridge.add(UserPreferences.FENCE_EBOOK);
            }
        }

        return fencesCartridge;
    }

    private static String getSelectedStartYearValue(UserSession userSession, String databaseName) throws SessionException {
        String databaseSelectedYearValue = getTextZoneValue(userSession, databaseName);
        if (StringUtils.isBlank(databaseSelectedYearValue)) {
            return null;
        }
        try {
            Integer.valueOf(databaseSelectedYearValue);
        } catch (NumberFormatException e) {
            throw new SessionException(SystemErrorCodes.TEXTZONE_INVALID_VALUE_ERROR, "The Value of " + databaseName + " is " + databaseSelectedYearValue, e);
        }
        return databaseSelectedYearValue;
    }

    private static String getTextZoneValue(UserSession userSession, String name) {
        return getUserTextZones(userSession).get(name);
    }

    private static Map<String, String> getUserTextZones(UserSession userSession) {
        return userSession.getUserTextZones();
    }

    private static IEVWebUser getUserFromUserSession(UserSession userSession) {
        return userSession.getUser();
    }
}
