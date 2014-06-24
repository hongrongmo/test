package test.org.ei.action.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.stripes.mock.MockRoundtrip;

import org.apache.log4j.Logger;
import org.ei.ane.entitlements.UserEntitlement;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.SearchForm;
import org.ei.gui.ListBoxOption;
import org.ei.session.UserPreferences;
import org.ei.session.UserSession;
import org.ei.stripes.action.search.ThesaurusSearchAction;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import test.org.ei.action.BaseActionTest;
import test.org.ei.action.MockEVActionBeanContext;

public class ThesaurusSearchActionTest extends BaseActionTest {
    private static Logger log4j = Logger.getLogger(ThesaurusSearchActionTest.class);

    private ThesaurusSearchAction thesaurusSearchActionBean;
    private List<String> doctypeopts = new ArrayList<String>();
    private List<String> treatmenttypeopts = new ArrayList<String>();
    private List<String> section1opts = new ArrayList<String>();
    private List<String> section2opts = new ArrayList<String>();
    private List<String> section3opts = new ArrayList<String>();
    private List<String> disciplinetypeopts = new ArrayList<String>();
    private List<String> endyearopts = new ArrayList<String>();
    private List<String> startyearopts = new ArrayList<String>();
    private List<String> languageopts = new ArrayList<String>();

    private String endyear = Integer.toString(SearchForm.ENDYEAR);

    @BeforeTest
    private void beforeAllMethods() {
        MockEVActionBeanContext.restore();
    }

    @BeforeGroups(groups = { "CPXSETUP", "INSSETUP", "GEOSETUP", "GRFSETUP", "ENCSETUP" })
    public void beforeALL() {
        doctypeopts.clear();
        treatmenttypeopts.clear();
        languageopts.clear();
        section1opts.clear();
        section2opts.clear();
        section3opts.clear();
        disciplinetypeopts.clear();
        endyearopts.clear();
        startyearopts.clear();

        doctypeopts.add("NO-LIMIT");
        doctypeopts.add("JA");
        doctypeopts.add("CA");
        doctypeopts.add("CP");
        doctypeopts.add("MC");
        doctypeopts.add("MR");
        doctypeopts.add("RC");
        doctypeopts.add("RR");
        doctypeopts.add("DS");
        doctypeopts.add("PA");

        treatmenttypeopts.add("NO-LIMIT");
        treatmenttypeopts.add("APP");
        treatmenttypeopts.add("ECO");
        treatmenttypeopts.add("EXP");
        treatmenttypeopts.add("GEN");
        treatmenttypeopts.add("THR");

        languageopts.add("NO-LIMIT");
        languageopts.add("English");
        languageopts.add("Chinese");
        languageopts.add("French");
        languageopts.add("German");
        languageopts.add("Italian");
        languageopts.add("Japanese");
        languageopts.add("Russian");
        languageopts.add("Spanish");

    }

    @BeforeGroups(groups = { "CPXSETUP" })
    public void beforeCPX() {
        doctypeopts.add("IP");

        treatmenttypeopts.add("BIO");
        treatmenttypeopts.add("HIS");
        treatmenttypeopts.add("LIT");
        treatmenttypeopts.add("MAN");
        treatmenttypeopts.add("NUM");
    }

    @BeforeGroups(groups = { "INSSETUP" })
    public void beforeINS() {
        doctypeopts.add("UP");

        treatmenttypeopts.add("BIB");
        treatmenttypeopts.add("NEW");
        treatmenttypeopts.add("PRA");
        treatmenttypeopts.add("PRO");

        disciplinetypeopts.add("NO-LIMIT");
        disciplinetypeopts.add("A");
        disciplinetypeopts.add("B");
        disciplinetypeopts.add("C");
        disciplinetypeopts.add("D");
        disciplinetypeopts.add("E");

    }

    @BeforeGroups(groups = { "GEOSETUP" })
    public void beforeGEO() {
        doctypeopts.remove("CP");
        doctypeopts.remove("MC");
        doctypeopts.remove("RC");
        doctypeopts.remove("RR");
        doctypeopts.remove("DS");
        doctypeopts.remove("PA");

        treatmenttypeopts.clear();
        treatmenttypeopts.add("NO-LIMIT");
    }

    @BeforeGroups(groups = { "GRFSETUP" })
    public void beforeGRF() {
        doctypeopts.remove("PA");
        doctypeopts.add("MP");

        treatmenttypeopts.clear();
        treatmenttypeopts.add("NO-LIMIT");

    }

    @BeforeGroups(groups = { "ENCSETUP" })
    public void beforeENC() {
        doctypeopts.clear();
        doctypeopts.add("NO-LIMIT");

        treatmenttypeopts.clear();
        treatmenttypeopts.add("NO-LIMIT");

        languageopts.remove("Italian");
    }

    /**
     * Test Thesaurus search tab initial URL redirects to add default database
     * 
     * @throws Exception
     */
    @Test
    public void noParameters() throws Exception {
        // Ensure Thes fence is turned ON
        UserSession usersession = MockEVActionBeanContext.getStaticUserSession();
        if (!usersession.getUser().getPreference(UserPreferences.FENCE_THESAURUS)) {
            usersession.getUser().setPreference(UserPreferences.FENCE_THESAURUS, true);
        }

        MockRoundtrip trip = new MockRoundtrip(mockcontext, "/search/thesHome.url");
        trip.execute();
        this.thesaurusSearchActionBean = trip.getActionBean(ThesaurusSearchAction.class);

        // This should add the default database to the URL
        int defaultdb = this.thesaurusSearchActionBean.defaultDatabase();
        Assert.assertTrue(defaultdb != 0, "Parameter 'database' should be defaulted!");

        // Now remove INS entitlement but try to go there
        Set<UserEntitlement> userentitlements = usersession.getUserEntitlements();
        userentitlements.remove(UserEntitlement.INS_ENTITLEMENT);
        userentitlements.remove(UserEntitlement.IBF_ENTITLEMENT);
        MockEVActionBeanContext.buildCartridge();

        trip = new MockRoundtrip(mockcontext, "/search/thesHome.url?database=" + DatabaseConfig.INS_MASK);
        trip.execute();
        this.thesaurusSearchActionBean = trip.getActionBean(ThesaurusSearchAction.class);
        String redir = trip.getRedirectUrl();
        // TMH - I initially thought this would redirect to a valid database URl but it does not! Bug?
        // Assert.assertTrue(!GenericValidator.isBlankOrNull(redir) && redir.contains("database=" + DatabaseConfig.CPX_MASK),
        // "ThesaurusSearchAction should redirect to Compendex!");
        Assert.assertNull(redir, "No redirect should happen!");

    }

    /**
     * Test Thesaurus search tab access - fence off and database entitlements
     * 
     * @throws Exception
     */
    @Test(priority = 99)
    public void noAccess() throws Exception {
        // Ensure Thes fence is turned OFF
        UserSession usersession = MockEVActionBeanContext.getStaticUserSession();
        if (usersession.getUser().getPreference(UserPreferences.FENCE_THESAURUS)) {
            usersession.getUser().setPreference(UserPreferences.FENCE_THESAURUS, false);
        }

        MockRoundtrip trip = new MockRoundtrip(mockcontext, "/search/thesHome.url");
        trip.execute();
        this.thesaurusSearchActionBean = trip.getActionBean(ThesaurusSearchAction.class);

        // This should return a redirect to system error page - no access
        String redir = trip.getRedirectUrl();
        Assert.assertNotNull(redir.contains("/system/error.url"), "ThesaurusSearchAction should redirect with no Thesaurus Fence!");

        // Now turn fence on but remove all DB entitlements
        usersession.getUser().setPreference(UserPreferences.FENCE_THESAURUS, true);
        Set<UserEntitlement> userentitlements = usersession.getUserEntitlements();
        userentitlements.remove(UserEntitlement.CPX_ENTITLEMENT);
        userentitlements.remove(UserEntitlement.C84_ENTITLEMENT);
        userentitlements.remove(UserEntitlement.INS_ENTITLEMENT);
        userentitlements.remove(UserEntitlement.IBF_ENTITLEMENT);
        userentitlements.remove(UserEntitlement.GEO_ENTITLEMENT);
        userentitlements.remove(UserEntitlement.GRF_ENTITLEMENT);
        userentitlements.remove(UserEntitlement.ELT_ENTITLEMENT);
        userentitlements.remove(UserEntitlement.EPT_ENTITLEMENT);
        MockEVActionBeanContext.buildCartridge();

        // This should return a redirect to system error page - no access
        trip = new MockRoundtrip(mockcontext, "/search/thesHome.url");
        trip.execute();
        this.thesaurusSearchActionBean = trip.getActionBean(ThesaurusSearchAction.class);
        redir = trip.getRedirectUrl();
        Assert.assertNotNull(redir.contains("/system/error.url"), "ThesaurusSearchAction should redirect with no Thesaurus Databases!");
    }

    /**
     * Ensure thesaurus search page is setup correctly for Compendex DB
     * 
     * @throws Exception
     */
    @Test(groups = { "CPXSETUP" }, dependsOnMethods = { "noParameters", "noAccess" })
    public void cpxSetup() throws Exception {
        // Ensure Thes fence is turned ON and CPX is entitled
        UserSession usersession = MockEVActionBeanContext.getStaticUserSession();
        if (!usersession.getUser().getPreference(UserPreferences.FENCE_THESAURUS)) {
            usersession.getUser().setPreference(UserPreferences.FENCE_THESAURUS, true);
        }
        Set<UserEntitlement> userentitlements = usersession.getUserEntitlements();
        if (!userentitlements.contains(UserEntitlement.CPX_ENTITLEMENT)) {
            userentitlements.add(UserEntitlement.CPX_ENTITLEMENT);
        }
        if (!userentitlements.contains(UserEntitlement.C84_ENTITLEMENT)) {
            userentitlements.add(UserEntitlement.C84_ENTITLEMENT);
        }
        MockEVActionBeanContext.buildCartridge();

        // Simulate the call
        MockRoundtrip trip = new MockRoundtrip(mockcontext, "/search/thesHome.url?database=" + DatabaseConfig.CPX_MASK);
        trip.execute();
        this.thesaurusSearchActionBean = trip.getActionBean(ThesaurusSearchAction.class);

        // Check list box contents
        List<ListBoxOption> doctypeopts = this.thesaurusSearchActionBean.getDoctypeopts();
        List<ListBoxOption> treatmenttypeopts = this.thesaurusSearchActionBean.getTreatmenttypeopts();
        List<ListBoxOption> languageopts = this.thesaurusSearchActionBean.getLanguageopts();
        List<ListBoxOption> section1opts = this.thesaurusSearchActionBean.getSection1opts();
        List<ListBoxOption> section2opts = this.thesaurusSearchActionBean.getSection2opts();
        List<ListBoxOption> section3opts = this.thesaurusSearchActionBean.getSection3opts();
        List<ListBoxOption> disciplinetypeopts = this.thesaurusSearchActionBean.getDisciplinetypeopts();
        List<ListBoxOption> endyearopts = this.thesaurusSearchActionBean.getEndyearopts();
        List<ListBoxOption> startyearopts = this.thesaurusSearchActionBean.getStartyearopts();

        Assert.assertTrue(doctypeopts != null && doctypeopts.size() == this.doctypeopts.size(), "Compendex setup should have " + this.doctypeopts.size()
            + " doctype options!");
        for (ListBoxOption opt : doctypeopts) {
            Assert.assertTrue(this.doctypeopts.contains(opt.getName()), "Compendex setup should NOT contain doctype option with name: " + opt.getName());
        }

        Assert.assertTrue(treatmenttypeopts != null && treatmenttypeopts.size() == this.treatmenttypeopts.size(), "Compendex setup should have "
            + this.treatmenttypeopts.size() + " treatmenttype options!");
        for (ListBoxOption opt : treatmenttypeopts) {
            Assert.assertTrue(this.treatmenttypeopts.contains(opt.getName()),
                "Compendex setup should NOT contain treatmenttype option with name: " + opt.getName());
        }

        Assert.assertTrue(languageopts != null && languageopts.size() == this.languageopts.size(), "Compendex setup should have " + this.languageopts.size()
            + " language options!");
        for (ListBoxOption opt : languageopts) {
            Assert.assertTrue(this.languageopts.contains(opt.getName()), "Compendex setup should NOT contain language option with name: " + opt.getName());
        }

        Assert.assertTrue(section1opts == null || section1opts.size() > 0, "Compendex setup should have NO section1 options!");
        Assert.assertTrue(section2opts == null || section2opts.size() > 0, "Compendex setup should have NO section2 options!");
        Assert.assertTrue(section3opts == null || section3opts.size() > 0, "Compendex setup should have NO section3 options!");
        Assert.assertTrue(disciplinetypeopts == null || disciplinetypeopts.size() > 0, "Compendex setup should have NO disciplinetype options!");

        String startyear = Integer.toString(DatabaseConfig.getInstance().getDatabase(DatabaseConfig.CPX_PREF).getStartYear(true));

        Assert.assertTrue(endyearopts != null && endyearopts.get(0).getName().equals(startyear), "Compendex setup should have endyear that starts with "
            + startyear + "!");
        Assert.assertTrue(endyearopts != null && endyearopts.get(endyearopts.size() - 1).getName().equals(endyear),
            "Compendex setup should have endyear that starts with " + endyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(0).getName().equals(startyear), "Compendex setup should have startyear that starts with "
            + startyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(startyearopts.size() - 1).getName().equals(endyear),
            "Compendex setup should have endyear that starts with " + endyear + "!");

    }

    /**
     * Ensure thesaurus search page is setup correctly for Inspec DB
     * 
     * @throws Exception
     */
    @Test(groups = { "INSSETUP" }, dependsOnMethods = { "noParameters", "noAccess" })
    public void insSetup() throws Exception {
        // Ensure Thes fence is turned ON and CPX is entitled
        UserSession usersession = MockEVActionBeanContext.getStaticUserSession();
        if (!usersession.getUser().getPreference(UserPreferences.FENCE_THESAURUS)) {
            usersession.getUser().setPreference(UserPreferences.FENCE_THESAURUS, true);
        }
        Set<UserEntitlement> userentitlements = usersession.getUserEntitlements();
        if (!userentitlements.contains(UserEntitlement.INS_ENTITLEMENT)) {
            userentitlements.add(UserEntitlement.INS_ENTITLEMENT);
        }
        if (!userentitlements.contains(UserEntitlement.IBF_ENTITLEMENT)) {
            userentitlements.add(UserEntitlement.IBF_ENTITLEMENT);
        }
        MockEVActionBeanContext.buildCartridge();

        // Simulate the call
        MockRoundtrip trip = new MockRoundtrip(mockcontext, "/search/thesHome.url?database=" + DatabaseConfig.INS_MASK);
        trip.execute();
        this.thesaurusSearchActionBean = trip.getActionBean(ThesaurusSearchAction.class);

        // Check list box contents
        List<ListBoxOption> doctypeopts = this.thesaurusSearchActionBean.getDoctypeopts();
        List<ListBoxOption> treatmenttypeopts = this.thesaurusSearchActionBean.getTreatmenttypeopts();
        List<ListBoxOption> languageopts = this.thesaurusSearchActionBean.getLanguageopts();
        List<ListBoxOption> section1opts = this.thesaurusSearchActionBean.getSection1opts();
        List<ListBoxOption> section2opts = this.thesaurusSearchActionBean.getSection2opts();
        List<ListBoxOption> section3opts = this.thesaurusSearchActionBean.getSection3opts();
        List<ListBoxOption> disciplinetypeopts = this.thesaurusSearchActionBean.getDisciplinetypeopts();
        List<ListBoxOption> endyearopts = this.thesaurusSearchActionBean.getEndyearopts();
        List<ListBoxOption> startyearopts = this.thesaurusSearchActionBean.getStartyearopts();

        Assert.assertTrue(doctypeopts != null && doctypeopts.size() == this.doctypeopts.size(), "Inspec setup should have " + this.doctypeopts.size()
            + " doctype options!");
        for (ListBoxOption opt : doctypeopts) {
            Assert.assertTrue(this.doctypeopts.contains(opt.getName()), "Inspec setup should NOT contain doctype option with name: " + opt.getName());
        }

        Assert.assertTrue(treatmenttypeopts != null && treatmenttypeopts.size() == this.treatmenttypeopts.size(), "Inspec setup should have "
            + this.treatmenttypeopts.size() + " treatmenttype options!");
        for (ListBoxOption opt : treatmenttypeopts) {
            Assert.assertTrue(this.treatmenttypeopts.contains(opt.getName()),
                "Inspec setup should NOT contain treatmenttype option with name: " + opt.getName());
        }

        Assert.assertTrue(disciplinetypeopts != null && disciplinetypeopts.size() == this.disciplinetypeopts.size(), "Inspec setup should have "
            + this.disciplinetypeopts.size() + " discipline options!");
        for (ListBoxOption opt : disciplinetypeopts) {
            Assert.assertTrue(this.disciplinetypeopts.contains(opt.getName()), "Inspec setup should NOT contain discipline option with name: " + opt.getName());
        }

        Assert.assertTrue(languageopts != null && languageopts.size() == this.languageopts.size(), "Inspec setup should have " + this.languageopts.size()
            + " language options!");
        for (ListBoxOption opt : languageopts) {
            Assert.assertTrue(this.languageopts.contains(opt.getName()), "Inspec setup should NOT contain language option with name: " + opt.getName());
        }

        Assert.assertTrue(section1opts == null || section1opts.size() > 0, "Inspec setup should have NO section1 options!");
        Assert.assertTrue(section2opts == null || section2opts.size() > 0, "Inspec setup should have NO section2 options!");
        Assert.assertTrue(section3opts == null || section3opts.size() > 0, "Inspec setup should have NO section3 options!");

        String startyear = Integer.toString(DatabaseConfig.getInstance().getDatabase(DatabaseConfig.INS_PREF).getStartYear(true));

        Assert.assertTrue(endyearopts != null && endyearopts.get(0).getName().equals(startyear), "Inspec setup should have endyear that starts with "
            + startyear + "!");
        Assert.assertTrue(endyearopts != null && endyearopts.get(endyearopts.size() - 1).getName().equals(endyear),
            "Inspec setup should have endyear that starts with " + endyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(0).getName().equals(startyear), "Inspec setup should have startyear that starts with "
            + startyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(startyearopts.size() - 1).getName().equals(endyear),
            "Inspec setup should have endyear that starts with " + endyear + "!");

    }

    /**
     * Ensure thesaurus search page is setup correctly for Geobase DB
     * 
     * @throws Exception
     */
    @Test(groups = { "GEOSETUP" }, dependsOnMethods = { "noParameters", "noAccess" })
    public void geoSetup() throws Exception {
        // Ensure Thes fence is turned ON and CPX is entitled
        UserSession usersession = MockEVActionBeanContext.getStaticUserSession();
        if (!usersession.getUser().getPreference(UserPreferences.FENCE_THESAURUS)) {
            usersession.getUser().setPreference(UserPreferences.FENCE_THESAURUS, true);
        }
        Set<UserEntitlement> userentitlements = usersession.getUserEntitlements();
        if (!userentitlements.contains(UserEntitlement.GEO_ENTITLEMENT)) {
            userentitlements.add(UserEntitlement.GEO_ENTITLEMENT);
        }
        MockEVActionBeanContext.buildCartridge();

        // Simulate the call
        MockRoundtrip trip = new MockRoundtrip(mockcontext, "/search/thesHome.url?database=" + DatabaseConfig.GEO_MASK);
        trip.execute();
        this.thesaurusSearchActionBean = trip.getActionBean(ThesaurusSearchAction.class);

        // Check list box contents
        List<ListBoxOption> doctypeopts = this.thesaurusSearchActionBean.getDoctypeopts();
        List<ListBoxOption> treatmenttypeopts = this.thesaurusSearchActionBean.getTreatmenttypeopts();
        List<ListBoxOption> languageopts = this.thesaurusSearchActionBean.getLanguageopts();
        List<ListBoxOption> section1opts = this.thesaurusSearchActionBean.getSection1opts();
        List<ListBoxOption> section2opts = this.thesaurusSearchActionBean.getSection2opts();
        List<ListBoxOption> section3opts = this.thesaurusSearchActionBean.getSection3opts();
        List<ListBoxOption> disciplinetypeopts = this.thesaurusSearchActionBean.getDisciplinetypeopts();
        List<ListBoxOption> endyearopts = this.thesaurusSearchActionBean.getEndyearopts();
        List<ListBoxOption> startyearopts = this.thesaurusSearchActionBean.getStartyearopts();

        Assert.assertTrue(doctypeopts != null && doctypeopts.size() == this.doctypeopts.size(), "Geobase setup should have " + this.doctypeopts.size()
            + " doctype options!");
        for (ListBoxOption opt : doctypeopts) {
            Assert.assertTrue(this.doctypeopts.contains(opt.getName()), "Geobase setup should NOT contain doctype option with name: " + opt.getName());
        }

        Assert.assertTrue(languageopts != null && languageopts.size() == this.languageopts.size(), "Geobase setup should have " + this.languageopts.size()
            + " language options!");
        for (ListBoxOption opt : languageopts) {
            Assert.assertTrue(this.languageopts.contains(opt.getName()), "Geobase setup should NOT contain language option with name: " + opt.getName());
        }

        Assert.assertTrue(treatmenttypeopts == null || treatmenttypeopts.size() > 0, "Geobase setup should have NO treatmenttype options!");
        Assert.assertTrue(disciplinetypeopts == null || disciplinetypeopts.size() > 0, "Geobase setup should have NO disciplinetype options!");
        Assert.assertTrue(section1opts == null || section1opts.size() > 0, "Geobase setup should have NO section1 options!");
        Assert.assertTrue(section2opts == null || section2opts.size() > 0, "Geobase setup should have NO section2 options!");
        Assert.assertTrue(section3opts == null || section3opts.size() > 0, "Geobase setup should have NO section3 options!");

        String startyear = Integer.toString(DatabaseConfig.getInstance().getDatabase(DatabaseConfig.GEO_PREF).getStartYear(true));

        Assert.assertTrue(endyearopts != null && endyearopts.get(0).getName().equals(startyear), "Geobase setup should have endyear that starts with "
            + startyear + "!");
        Assert.assertTrue(endyearopts != null && endyearopts.get(endyearopts.size() - 1).getName().equals(endyear),
            "Geobase setup should have endyear that starts with " + endyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(0).getName().equals(startyear), "Geobase setup should have startyear that starts with "
            + startyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(startyearopts.size() - 1).getName().equals(endyear),
            "Geobase setup should have endyear that starts with " + endyear + "!");

    }

    /**
     * Ensure thesaurus search page is setup correctly for Georef DB
     * 
     * @throws Exception
     */
    @Test(groups = { "GRFSETUP" }, dependsOnMethods = { "noParameters", "noAccess" })
    public void grfSetup() throws Exception {
        // Ensure Thes fence is turned ON and CPX is entitled
        UserSession usersession = MockEVActionBeanContext.getStaticUserSession();
        if (!usersession.getUser().getPreference(UserPreferences.FENCE_THESAURUS)) {
            usersession.getUser().setPreference(UserPreferences.FENCE_THESAURUS, true);
        }
        Set<UserEntitlement> userentitlements = usersession.getUserEntitlements();
        if (!userentitlements.contains(UserEntitlement.GRF_ENTITLEMENT)) {
            userentitlements.add(UserEntitlement.GRF_ENTITLEMENT);
        }
        MockEVActionBeanContext.buildCartridge();

        // Simulate the call
        MockRoundtrip trip = new MockRoundtrip(mockcontext, "/search/thesHome.url?database=" + DatabaseConfig.GRF_MASK);
        trip.execute();
        this.thesaurusSearchActionBean = trip.getActionBean(ThesaurusSearchAction.class);

        // Check list box contents
        List<ListBoxOption> doctypeopts = this.thesaurusSearchActionBean.getDoctypeopts();
        List<ListBoxOption> treatmenttypeopts = this.thesaurusSearchActionBean.getTreatmenttypeopts();
        List<ListBoxOption> languageopts = this.thesaurusSearchActionBean.getLanguageopts();
        List<ListBoxOption> section1opts = this.thesaurusSearchActionBean.getSection1opts();
        List<ListBoxOption> section2opts = this.thesaurusSearchActionBean.getSection2opts();
        List<ListBoxOption> section3opts = this.thesaurusSearchActionBean.getSection3opts();
        List<ListBoxOption> disciplinetypeopts = this.thesaurusSearchActionBean.getDisciplinetypeopts();
        List<ListBoxOption> endyearopts = this.thesaurusSearchActionBean.getEndyearopts();
        List<ListBoxOption> startyearopts = this.thesaurusSearchActionBean.getStartyearopts();

        Assert.assertTrue(doctypeopts != null && doctypeopts.size() == this.doctypeopts.size(), "Georef setup should have " + this.doctypeopts.size()
            + " doctype options!");
        for (ListBoxOption opt : doctypeopts) {
            Assert.assertTrue(this.doctypeopts.contains(opt.getName()), "Georef setup should NOT contain doctype option with name: " + opt.getName());
        }

        Assert.assertTrue(languageopts != null && languageopts.size() == this.languageopts.size(), "Georef setup should have " + this.languageopts.size()
            + " language options!");
        for (ListBoxOption opt : languageopts) {
            Assert.assertTrue(this.languageopts.contains(opt.getName()), "Georef setup should NOT contain language option with name: " + opt.getName());
        }

        Assert.assertTrue(treatmenttypeopts == null || treatmenttypeopts.size() > 0, "Georef setup should have NO treatmenttype options!");
        Assert.assertTrue(disciplinetypeopts == null || disciplinetypeopts.size() > 0, "Georef setup should have NO disciplinetype options!");
        Assert.assertTrue(section1opts == null || section1opts.size() > 0, "Georef setup should have NO section1 options!");
        Assert.assertTrue(section2opts == null || section2opts.size() > 0, "Georef setup should have NO section2 options!");
        Assert.assertTrue(section3opts == null || section3opts.size() > 0, "Georef setup should have NO section3 options!");

        String startyear = Integer.toString(DatabaseConfig.getInstance().getDatabase(DatabaseConfig.GRF_PREF).getStartYear(true));

        Assert.assertTrue(endyearopts != null && endyearopts.get(0).getName().equals(startyear), "Georef setup should have endyear that starts with "
            + startyear + "!");
        Assert.assertTrue(endyearopts != null && endyearopts.get(endyearopts.size() - 1).getName().equals(endyear),
            "Georef setup should have endyear that starts with " + endyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(0).getName().equals(startyear), "Georef setup should have startyear that starts with "
            + startyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(startyearopts.size() - 1).getName().equals(endyear),
            "Georef setup should have endyear that starts with " + endyear + "!");

    }

    /**
     * Ensure thesaurus search page is setup correctly for Encompass DB
     * 
     * @throws Exception
     */
    @Test(groups = { "ENCSETUP" }, dependsOnMethods = { "noParameters", "noAccess" })
    public void encSetup() throws Exception {
        // Ensure Thes fence is turned ON and CPX is entitled
        UserSession usersession = MockEVActionBeanContext.getStaticUserSession();
        if (!usersession.getUser().getPreference(UserPreferences.FENCE_THESAURUS)) {
            usersession.getUser().setPreference(UserPreferences.FENCE_THESAURUS, true);
        }
        Set<UserEntitlement> userentitlements = usersession.getUserEntitlements();
        if (!userentitlements.contains(UserEntitlement.EPT_ENTITLEMENT)) {
            userentitlements.add(UserEntitlement.EPT_ENTITLEMENT);
        }
        MockEVActionBeanContext.buildCartridge();

        // Simulate the call
        MockRoundtrip trip = new MockRoundtrip(mockcontext, "/search/thesHome.url?database=" + DatabaseConfig.EPT_MASK);
        trip.execute();
        this.thesaurusSearchActionBean = trip.getActionBean(ThesaurusSearchAction.class);

        // Check list box contents
        List<ListBoxOption> doctypeopts = this.thesaurusSearchActionBean.getDoctypeopts();
        List<ListBoxOption> treatmenttypeopts = this.thesaurusSearchActionBean.getTreatmenttypeopts();
        List<ListBoxOption> languageopts = this.thesaurusSearchActionBean.getLanguageopts();
        List<ListBoxOption> section1opts = this.thesaurusSearchActionBean.getSection1opts();
        List<ListBoxOption> section2opts = this.thesaurusSearchActionBean.getSection2opts();
        List<ListBoxOption> section3opts = this.thesaurusSearchActionBean.getSection3opts();
        List<ListBoxOption> disciplinetypeopts = this.thesaurusSearchActionBean.getDisciplinetypeopts();
        List<ListBoxOption> endyearopts = this.thesaurusSearchActionBean.getEndyearopts();
        List<ListBoxOption> startyearopts = this.thesaurusSearchActionBean.getStartyearopts();

        Assert.assertTrue(languageopts != null && languageopts.size() == this.languageopts.size(), "Encompass setup should have " + this.languageopts.size()
            + " language options!");
        for (ListBoxOption opt : languageopts) {
            Assert.assertTrue(this.languageopts.contains(opt.getName()), "Encompass setup should NOT contain language option with name: " + opt.getName());
        }

        Assert.assertTrue(doctypeopts == null || doctypeopts.size() > 0, "Encompass setup should have NO doctype options!");
        Assert.assertTrue(treatmenttypeopts == null || treatmenttypeopts.size() > 0, "Encompass setup should have NO treatmenttype options!");
        Assert.assertTrue(disciplinetypeopts == null || disciplinetypeopts.size() > 0, "Encompass setup should have NO disciplinetype options!");
        Assert.assertTrue(section1opts == null || section1opts.size() > 0, "Encompass setup should have NO section1 options!");
        Assert.assertTrue(section2opts == null || section2opts.size() > 0, "Encompass setup should have NO section2 options!");
        Assert.assertTrue(section3opts == null || section3opts.size() > 0, "Encompass setup should have NO section3 options!");

        String startyear = Integer.toString(DatabaseConfig.getInstance().getDatabase(DatabaseConfig.EPT_PREF).getStartYear(true));

        Assert.assertTrue(endyearopts != null && endyearopts.get(0).getName().equals(startyear), "Encompass setup should have endyear that starts with "
            + startyear + "!");
        Assert.assertTrue(endyearopts != null && endyearopts.get(endyearopts.size() - 1).getName().equals(endyear),
            "Encompass setup should have endyear that starts with " + endyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(0).getName().equals(startyear), "Encompass setup should have startyear that starts with "
            + startyear + "!");
        Assert.assertTrue(startyearopts != null && startyearopts.get(startyearopts.size() - 1).getName().equals(endyear),
            "Encompass setup should have endyear that starts with " + endyear + "!");

    }
}
