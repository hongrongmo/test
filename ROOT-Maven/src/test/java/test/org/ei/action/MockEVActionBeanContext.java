package test.org.ei.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ei.ane.entitlements.UserEntitlement;
import org.ei.biz.personalization.EVWebUser;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.controller.logging.LogEntry;
import org.ei.exception.SessionException;
import org.ei.session.CartridgeBuilder;
import org.ei.session.UserPreferences;
import org.ei.session.UserSession;
import org.ei.stripes.EVActionBeanContext;

public class MockEVActionBeanContext extends EVActionBeanContext {
    private static Logger log4j = Logger.getLogger(MockEVActionBeanContext.class);

    private LogEntry logentry = new MockLogEntry();
    @Override
    public LogEntry getLogEntry() {
        return logentry;
    }


    // Entitlements: [C84, CBN, CHM, CPX, ELT, EPT, EUP, GEO, GRF, IBF, INS, NTI, PCH, UPA, ZBF, a, cp, czl, czp, ets, frl, frp, he, ng, ocl, ocp, pol, pp, prp, ps_l, ps_p, psp, tl, tp, ts]
    // Fences: [LHL, LHC, THS, REF, TAG, GAR, STMOff, SRTre, CHE, CIV, COM, ELE, MAT, SEC, PAG]
    // Text Zones: [CSY1884, ISY1896, NSY1899, USY1790, ESY1978, BSY1985, GSY1980, ASY1967, PSY1987, ZSY1884, HSY1970, MSY1963, LSY1962, FSY1896, XSY1785, expertSearch]
    private static UserEntitlement[] alldbs = new UserEntitlement[] {
        UserEntitlement.CPX_ENTITLEMENT, UserEntitlement.CBF_ENTITLEMENT, UserEntitlement.CBN_ENTITLEMENT, UserEntitlement.CHM_ENTITLEMENT,
        UserEntitlement.ELT_ENTITLEMENT, UserEntitlement.EPT_ENTITLEMENT, UserEntitlement.EUP_ENTITLEMENT, UserEntitlement.UPA_ENTITLEMENT,
        UserEntitlement.GEO_ENTITLEMENT, UserEntitlement.GRF_ENTITLEMENT, UserEntitlement.INS_ENTITLEMENT, UserEntitlement.IBF_ENTITLEMENT,
        UserEntitlement.NTI_ENTITLEMENT, UserEntitlement.PCH_ENTITLEMENT};
    private static String[] allfences = new String[] {
        UserPreferences.FENCE_ASKANEXPERT, UserPreferences.FENCE_AUTOSTEMMING, UserPreferences.FENCE_BLOGTHIS_OFF, UserPreferences.FENCE_BULLETINS,
        UserPreferences.FENCE_EMAIL_ALERTS_CC, UserPreferences.FENCE_EBOOK, UserPreferences.FENCE_ENCOMPASS_LIT_HTML, UserPreferences.FENCE_ENCOMPASS_LIT_PDF,
        UserPreferences.FENCE_ENCOMPASS_PAT_HTML, UserPreferences.FENCE_ENCOMPASS_PAT_PDF, UserPreferences.FENCE_FULL_TEXT_LINKING_ABSTRACT_DETAILED,
        UserPreferences.FENCE_FULL_TEXT_LINKING_CITATION, UserPreferences.FENCE_NAV_GRAPHS_DOWNLOAD, UserPreferences.FENCE_THESAURUS};
    private static Map<String,String> alltextzones = new HashMap<String,String>();

    private static UserSession usersession = new UserSession();
    static {
        restore();
    }

    public static void restore() {
        Set<UserEntitlement> userEntitlementList = new HashSet<UserEntitlement>();
        userEntitlementList.addAll(Arrays.asList(alldbs));

        alltextzones.clear();
        usersession.getUserEntitlements().clear();

        alltextzones.put("INSPEC_SELECTED_START_YEAR", "1896");
        alltextzones.put("ENG_IND_BACKFILE_START", "1884");
        alltextzones.put("CUSTOMER_ID", "1001772");
        alltextzones.put("IMAGE_URL_1", "");
        alltextzones.put("IMAGE_URL_2", "");
        alltextzones.put("GEOREF_SELECTED_START_YEAR", "1785");
        alltextzones.put("IMAGE_URL_3", "");
        alltextzones.put("DEFAULT_URL_3",
                "/controller/servlet/Controller?CID=lochform&title=[ATITLE]&author=[AUFULL]&issn=[ISSN]&isbn=[ISBN1]&volume=[VOLUME]&issue=[ISSUE]&serialtitle=[TITLE]&source=[STITLE]&startpage=[SPAGE]&year=[YEAR]&email=tharover@gmail.com");
        alltextzones.put("DEFAULT_URL_2",
                "/search/results/localholding.url?Title=[ATITLE]&AUTHOR=[AUFULL]&ISSN=[ISSN]&ISBN=[ISBN1]&Volume=[VOLUME]&Issue=[ISSUE]&SerialTitle=[TITLE]&conftitle=[CTITLE]&source=[STITLE]&StartPage=[SPAGE]&Year=[YEAR]");
        alltextzones.put("COMPENDEX_SELECTED_START_YEAR", "1884");
        alltextzones.put("DEFAULT_URL_5", "");
        alltextzones.put("DEFAULT_URL_4",
                "/search/results/localholding.url?title=[ATITLE]&author=[AUFULL]&issn=[ISSN]&isbn=[ISBN1]&volume=[VOLUME]&issue=[ISSUE]&serialtitle=[TITLE]&source=[STITLE]&startpage=[SPAGE]&year=[YEAR]&email=tharover@gmail.com");
        alltextzones.put("CHIMICA_SELECTED_START_YEAR", "1970");
        alltextzones.put("GEOBASE_SELECTED_START_YEAR", "1980");
        alltextzones.put("DEFAULT_URL_1",
                "/controller/servlet/Controller?CID=loch&Title=[ATITLE]&AUTHOR=[AUFULL]&ISSN=[ISSN]&ISBN=[ISBN1]&Volume=[VOLUME]&Issue=[ISSUE]&SerialTitle=[TITLE]&conftitle=[CTITLE]&source=[STITLE]&StartPage=[SPAGE]&Year=[YEAR]");
        alltextzones.put("PAPER_CHEM_SELECTED_START_YEAR", "1967");
        alltextzones.put("OPTION2",
            "<a href=\"http://www.ei.org/trainingpresentations\" title=\"Training videos\" target=\"_new\" class=\"evpopup\">Training videos</a>");

        IEVWebUser user = new EVWebUser();
        usersession.setUser(user);
        usersession.addUserEntitlements(userEntitlementList);
        usersession.getUserTextZones().putAll(alltextzones);
        for (String fence : allfences) {
            user.setPreference(fence, true);
        }

        buildCartridge();
    }

    public static void buildCartridge() {
        List<String> cartridge;
        IEVWebUser user = usersession.getUser();
        try {
            cartridge = CartridgeBuilder.buildUserCartridge(usersession);
            user.setCartridge(cartridge.toArray(new String[cartridge.size()]));
        } catch (SessionException e) {
            log4j.error("Unable to build user cartridge!",e);
        }

    }

    @Override
    public void setUserSession(UserSession us) {
        synchronized(usersession) {
            usersession = us;
        }
    }

    @Override
    public UserSession getUserSession() {
        return usersession;
    }

    public static UserSession getStaticUserSession() {
        return usersession;
    }

    @Override
    public void setSessionCookie() {
        // When unit testing, do nothing here!encodedACWRequestParm
        return;
    }

}
