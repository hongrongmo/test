package org.ei.stripes.action.delivery;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.DocumentBasket;
import org.ei.domain.personalization.Folder;
import org.ei.domain.personalization.SavedRecords;
import org.ei.domain.personalization.SavedRecordsException;
import org.ei.exception.InfrastructureException;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVPathUrl;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.action.search.BaseSearchAction;

public abstract class AbstractDeliveryAction extends BaseSearchAction {

    public static final String DOWNLOAD_FORMAT_ASCII = "ascii";
    public static final String DOWNLOAD_FORMAT_RIS = "ris";
    public static final String DOWNLOAD_FORMAT_BIBTEXT = "bib";
    public static final String DOWNLOAD_FORMAT_REFWORKS = "refworks";
    public static final String DOWNLOAD_FORMAT_CSV = "csv";
    public static final String DOWNLOAD_FORMAT_PDF = "pdf";
    public static final String DOWNLOAD_FORMAT_RTF = "rtf";
    public static final String DOWNLOAD_FORMAT_EXCEL = "excel";


    private final static Logger log4j = Logger.getLogger(AbstractDeliveryAction.class);

    @Validate(required = true)
    protected String displayformat;
    protected String docidlist;
    protected String handlelist;
    protected String folderid;

    protected int count;

    // DocumentBasket for the JSP
    protected DocumentBasket basket;

    // Folder
    protected Folder folder;

    /**
     * Initialize the DocumentBasket
     *
     * @throws InfrastructureException
     */
    @After(stages = LifecycleStage.BindingAndValidation)
    protected Resolution init() {
        UserSession usersession = context.getUserSession();
        if (usersession == null) {
            throw new RuntimeException("UserSession is null!");
        }

        // If sessionid is NOT passed in, set it to the current user's session ID
        this.sessionid = context.getRequest().getParameter("sessionid");
        if (GenericValidator.isBlankOrNull(sessionid)) {
            if (usersession.getSessionID() != null) {
                this.sessionid = usersession.getSessionID().getID();
            } else {
                log4j.warn("No session ID available - return to home page!");
                return new RedirectResolution(EVPathUrl.EV_HOME.value() + "?redir=t", false);
            }
        }

        // Init from request parms
        if (!GenericValidator.isBlankOrNull(folderid)) {

            try {
                // Get the count from the folder
                SavedRecords savedRecords = new SavedRecords();
                this.folder = savedRecords.getFolder(folderid);
                this.count = this.folder.getFolderSize();
            } catch (SavedRecordsException e) {
                log4j.error("Unable to retrieve folder for ID: " + folderid + ", message: " + e.getMessage());
                return SystemMessage.SYSTEM_ERROR_RESOLUTION;
            }
        } else if (!GenericValidator.isBlankOrNull(this.docidlist)) {
            // Get the count from docids
            this.count = this.docidlist.split(",").length;
        } else {
            try {
                this.basket = new DocumentBasket(this.getSessionid());
                this.count = basket.getBasketSize();
            } catch (InfrastructureException e) {
                log4j.error("Unable to retrieve basket for ID: " + usersession.getSessionID().getID() + ", message: " + e.getMessage());
                return SystemMessage.SYSTEM_ERROR_RESOLUTION;
            }
        }
        // All good, return null to continue processing
        return null;
    }

    public String getDocidlist() {
        return docidlist;
    }

    public void setDocidlist(String docidlist) {
        this.docidlist = docidlist;
    }

    public String getHandlelist() {
        return handlelist;
    }

    public void setHandlelist(String handles) {
        this.handlelist = handles;
    }

    public String getFolderid() {
        return folderid;
    }

    public void setFolderid(String folderid) {
        this.folderid = folderid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDisplayformat() {
        return displayformat;
    }

    public void setDisplayformat(String format) {
        this.displayformat = format;
    }

    public DocumentBasket getBasket() {
        return basket;
    }

}
