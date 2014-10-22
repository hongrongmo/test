package org.ei.stripes.action;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.security.BulletinsAccessControl;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.ISecuredAction;
import org.ei.bulletins.BulletinBuilder;
import org.ei.bulletins.BulletinGUI;
import org.ei.bulletins.BulletinPage;
import org.ei.bulletins.BulletinQuery;
import org.ei.bulletins.BulletinResultNavigator;
import org.ei.service.amazon.s3.AmazonS3Service;
import org.ei.service.amazon.s3.AmazonS3ServiceImpl;
import org.ei.session.UserPreferences;
import org.ei.session.UserSession;

import com.amazonaws.services.s3.model.AmazonS3Exception;

@UrlBinding("/bulletins/{$event}.url")
public class BulletinsAction extends EVActionBean implements ISecuredAction {

    private final static Logger log4j = Logger.getLogger(BulletinsAction.class);

    // Document index
    private int docIndex = 1;

    // Query string
    private String queryStr;

    // Select Category dropdown options
    private String cy;

    // Select Year dropdown options
    private String yr = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

    // Name used for view requests
    private String fn;

    // Content type (HTML or PDF) for view requests
    private String ct = "HTML";

    // Bulletins db value
    private String db = "1";

    // Results per page (default 25)
    private int pageSize = 25;

    // Bulletin page objects
    private BulletinPage litpage;
    private BulletinPage patpage;
    private BulletinQuery bulletinquery = new BulletinQuery();

    // Entitlement indicators
    boolean lit = false;
    boolean pat = false;

    /**
     * Override for the ISecuredAction interface. This ActionBean requires Bulletins feature to be enabled in addition to regular authentication
     */
    @Override
    public IAccessControl getAccessControl() {
        return new BulletinsAccessControl();
    }

    /**
     * Init method to set the "lit" and "pat" flags
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @Before
    public Resolution init() throws UnsupportedEncodingException {
        UserSession usersession = context.getUserSession();
        String cartridge = usersession.getUser().getCartridgeString();

        lit = !GenericValidator.isBlankOrNull(BulletinGUI.getLITCartridges(usersession.getUser().getCartridgeString()));
        pat = !GenericValidator.isBlankOrNull(BulletinGUI.getPATCartridges(usersession.getUser().getCartridgeString()));

        if (!lit && !pat) {
            log4j.warn("************ Bulletins fence is ON but no entitlements! *************");
            return SystemMessage.FEATURE_DISABLED_RESOLUTION;
        }
        return null;
    }

    /**
     * Displays the main bulletins page -
     *
     * @return Resolution
     */
    @HandlesEvent("display")
    @DontValidate
    public Resolution display() {

        log4j.info("Starting bulletins display...");

        setRoom(ROOM.bulletins);
        UserSession ussession = context.getUserSession();
        if (ussession == null) {
            log4j.warn("No UserSession object available!");
            return new ForwardResolution(SystemMessage.SYSTEM_ERROR_URL);
        }
        IEVWebUser user = ussession.getUser();
        setSessionid(ussession.getSessionID().toString());
        String cartridges[] = user.getCartridge();

        //
        // Build the BulletinPage objects
        //
        BulletinBuilder builder = new BulletinBuilder();
        try {
            if (lit) {
                litpage = builder.buildLITRecent();
                litpage.initForDisplay(cartridges, "1", "");
            }
            if (pat) {
                if (!lit)
                    db = "2";
                patpage = builder.buildPATRecent();
                patpage.initForDisplay(cartridges, "2", "");
            }
        } catch (Exception e) {
            log4j.warn("Unable to build bulletin pages!");
            return new ForwardResolution(SystemMessage.SYSTEM_ERROR_URL);
        }

        return new ForwardResolution("/WEB-INF/pages/customer/bulletins/recent.jsp");
    }

    // Results-only variables
    private int hitcount;
    private String path;

    /**
     * Handles bulletin archive search results
     *
     * @return
     */
    @HandlesEvent("archive")
    @DontValidate
    public Resolution archive() {

        if ("1".equals(bulletinquery.getDatabase()) && !lit) {
            log4j.error("Attempt to run LIT search with no entitlements!");
            return new ForwardResolution(SystemMessage.SYSTEM_ERROR_URL);
        }
        if ("2".equals(bulletinquery.getDatabase()) && !pat) {
            log4j.error("Attempt to run PAT search with no entitlements!");
            return new ForwardResolution(SystemMessage.SYSTEM_ERROR_URL);
        }

        setRoom(ROOM.bulletins);
        UserSession ussession = context.getUserSession();
        IEVWebUser user = ussession.getUser();
        setSessionid(ussession.getSessionID().toString());

        BulletinPage btPage = null;
        BulletinBuilder builder = new BulletinBuilder();
        BulletinResultNavigator navigator = null;

        try {
            //
            // Create the query object
            //
            if (!GenericValidator.isBlankOrNull(queryStr)) {
                bulletinquery.setQuery(queryStr);
                yr = bulletinquery.getYr();
                cy = bulletinquery.getCategory();
                db = bulletinquery.getDatabase();
            } else {
                bulletinquery.setYr(yr);
                bulletinquery.setDatabase(db);
                bulletinquery.setCategory(cy);
                queryStr = URLEncoder.encode(db + "::" + yr + "::" + cy, "UTF-8");
            }

            //
            // Create the page object
            //
            btPage = builder.buildBulletinResults(bulletinquery.getDatabase(), bulletinquery.getYr(), bulletinquery.getCategory());
            navigator = new BulletinResultNavigator(docIndex, pageSize, btPage.size());
            hitcount = navigator.getHitCount();
            btPage = navigator.filter(btPage);

            //
            // Set the appropriate output page
            //
            String cartridges[] = user.getCartridge();
            if ("1".equals(bulletinquery.getDatabase())) {
                litpage = btPage;
                litpage.initForDisplay(cartridges, "1", cy);	// Init lit WITH category
                path = "EncompassLIT&nbsp;&nbsp;&gt;&nbsp;&nbsp;" + bulletinquery.getYr() + "&nbsp;&nbsp;&gt;&nbsp;&nbsp;"
                    + BulletinQuery.getDisplayCategory(bulletinquery.getCategory());
                patpage = new BulletinPage();
                patpage.initForDisplay(cartridges, "2", "");	// Init patents WITHOUT category
            } else if ("2".equals(bulletinquery.getDatabase())) {
                patpage = btPage;
                patpage.initForDisplay(cartridges, "2", cy);	// Init patents WITH category
                path = "EncompassPAT&nbsp;&nbsp;&gt;&nbsp;&nbsp;" + bulletinquery.getYr() + "&nbsp;&nbsp;&gt;&nbsp;&nbsp;"
                    + BulletinQuery.getDisplayCategory(bulletinquery.getCategory());
                litpage = new BulletinPage();
                litpage.initForDisplay(cartridges, "1", "");	// Init lit WITHOUT category
            }

        } catch (Exception e) {
            log4j.error("Unable to get archive search results!");
            return new ForwardResolution(SystemMessage.SYSTEM_ERROR_URL);
        }

        return new ForwardResolution("/WEB-INF/pages/customer/bulletins/archive.jsp");
    }

    private static final String PAT_PATH = "patent/";
    private static final String LIT_PATH = "lit/";

    /**
     * Displays an individual bulletin in PDF or HTML
     *
     * @return Resolution
     */
    @HandlesEvent("view")
    @DontValidate
    public Resolution view() {
        InputStream dataFileFromS3Bucket = null;
        String contenttype = "text/html";

        // Ensure parameters are present
        if (GenericValidator.isBlankOrNull(db) || GenericValidator.isBlankOrNull(fn) || GenericValidator.isBlankOrNull(ct)
            || GenericValidator.isBlankOrNull(yr)) {
            throw new IllegalArgumentException("Parameter missing for View Bulletin!");
        }
        log4j.info("Bulletin request, fn: " + fn + ", ct: " + ct + ", db: " + db + ", yr: " + yr);

        try {
            String link = null;

            // Set the "link" path
            link = ("1".equals(db) ? LIT_PATH : PAT_PATH);
            log4j.info("Servicing request for path: " + link + "...");

            // Set the filename from the "ct" (content type) parm
            log4j.info("Setting filename from content type: " + ct);
            String filename = fn + ".htm";
            String type = "graphic";
            boolean showsaveas = false;
            if ("HTML".equals(ct)) {
                type = "html";
            } else if ("PDF".equals(ct)) {
                contenttype = "application/pdf";
                filename = fn + ".pdf";
                type = "pdf";
            } else if ("ZIP".equals(ct)) {
                contenttype = "application/zip";
                filename = fn + ".zip";
                showsaveas = true;
            } else if ("SAVEHTML".equals(ct)) {
                type = "html";
                showsaveas = true;
            } else if ("SAVEPDF".equals(ct)) {
                filename = fn + ".pdf";
                type = "pdf";
                showsaveas = true;
            }

            // Build full URL to bulletin
            StringBuffer fullUrl = new StringBuffer();
            fullUrl.append(link).append(yr).append("/").append(cy).append("/").append(type).append("/").append(filename);

            // Retrieve from Amazon S3 bucket
            AmazonS3Service s3Srvc = new AmazonS3ServiceImpl();
            dataFileFromS3Bucket = s3Srvc.getBulletinDataFileFromS3Bucket(fullUrl.toString());
            StreamingResolution streamingresolution = new StreamingResolution(contenttype, dataFileFromS3Bucket);
            if (showsaveas) streamingresolution.setFilename(filename);
            return streamingresolution;

        } catch (AmazonS3Exception e) {
            log4j.error("Amazon S3 error has occurred!", e);
            return new StreamingResolution("text/html", "The file you required is not yet available, please check back later.");
        } catch (Exception e) {
            log4j.error("Exception has occurred - " + e.getClass().getName() + ": " + e.getMessage());
            return new StreamingResolution("text/html", "Unable to process this request.");
        }
    }

    //
    //
    // GETTERS/SETTERS
    //
    //
    public String getQueryStr() {
        return queryStr;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    public String getCy() {
        return cy;
    }

    public void setCy(String cy) {
        this.cy = cy;
    }

    public String getYr() {
        return yr;
    }

    public void setYr(String yr) {
        this.yr = yr;
    }

    public BulletinPage getLitpage() {
        return litpage;
    }

    public BulletinPage getPatpage() {
        return patpage;
    }

    public int getDocIndex() {
        return docIndex;
    }

    public void setDocIndex(int docIndex) {
        this.docIndex = docIndex;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getHitcount() {
        return hitcount;
    }

    public void setHitcount(int hitcount) {
        this.hitcount = hitcount;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplaycategory() {
        return BulletinQuery.getDisplayCategory(bulletinquery.getCategory());
    }

    public boolean isLit() {
        return lit;
    }

    public boolean isPat() {
        return pat;
    }

}
