package org.ei.stripes.action.folders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.IndividualAuthRequiredAccessControl;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBasket;
import org.ei.domain.personalization.Folder;
import org.ei.domain.personalization.FolderEntry;
import org.ei.domain.personalization.SavedRecords;
import org.ei.stripes.action.EVPathUrl;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.action.personalaccount.IPersonalLogin;

/**
 * This is the base class for the saved folders.
 *
 * @author bhanu
 *
 */

@UrlBinding("/personal/folders/save/{$event}.url")
public class SavedFoldersAction extends FolderActionBean implements IPersonalLogin {

    private final static Logger log4j = Logger.getLogger(SavedFoldersAction.class);

    /**
     * Override for the ISecuredAction interface. This ActionBean requires individual authentication
     */
    @Override
    public IAccessControl getAccessControl() {
        return new IndividualAuthRequiredAccessControl();
    }

    /**
     * Displays the save Folders page -
     *
     * @return Resolution
     */
    @HandlesEvent("view")
    @DefaultHandler
    @DontValidate
    public Resolution view() {

        log4j.info("Starting view Folders view...");
        setRoom(ROOM.mysettings);
        setPersonalization(false);
        IEVWebUser user = context.getUserSession().getUser();

        try {
            if (source != null) {
                if (source.equals("selectedset")) {
                    selectedSet = true;
                    DocumentBasket basket = new DocumentBasket(this.getSessionid());
                    basketSize = basket.getBasketSize();
                    hiddenCID = "addSelectedSetRecords";
                    documentCount = basketSize;
                } else {
                    documentCount = 1;
                    hiddenCID = "saveRecordsToFolder";
                }
            }

            if (user.isIndividuallyAuthenticated()) {
                setPersonalization(true);
                SavedRecords sr = new SavedRecords(user.getUserId());
                this.folderlist = sr.viewListOfFolders();
                this.foldercount = sr.getFolderCount();
            }

            return new ForwardResolution("/WEB-INF/pages/customer/folders/SaveToFolder.jsp");

        } catch (Exception e) {
            log4j.error("View - Unable to process the Request!", e);
            return new ForwardResolution(SystemMessage.SYSTEM_ERROR_URL);
        }
    }

    /**
     * Displays the add Folders page -
     *
     * @return Resolution
     */
    @HandlesEvent("add")
    @DontValidate
    public Resolution add() {

        log4j.info("Starting add Folders view...");
        setRoom(ROOM.mysettings);
        setPersonalization(false);
        IEVWebUser user = context.getUserSession().getUser();

        try {

            if (this.folderid != null && !this.folderid.equals("")) {
                setFolderid(this.folderid.substring(0, this.folderid.trim().indexOf(",")));
            }

            if (user.isIndividuallyAuthenticated()) {
                SavedRecords sr = new SavedRecords(user.getUserId());
                if ((this.foldername != null) && (this.foldername.length() != 0)) {
                    folder = new Folder(this.foldername.trim());
                    if (!sr.addFolder(folder)) {
                        log4j.error("Unable to create folder!");
                        return SystemMessage.SYSTEM_ERROR_RESOLUTION;
                    }
                    this.folderid = sr.getFolderId(this.foldername.trim());
                    folder.setFolderID(this.folderid);
                } else {
                    this.foldername = sr.getFolderName(this.folderid);
                    folder = new Folder(this.folderid, this.foldername);
                }

                DocumentBasket basket = new DocumentBasket(this.getSessionid());
                if (basket.getBasketSize() > 0) {
                    @SuppressWarnings("unchecked")
                    List<DocID> docIDs = basket.getAllDocIDs();
                    int count = docIDs.size();

                    // Create FolderEntry objects and add to list of FolderEntries.
                    listOfFolderEntries = new ArrayList<FolderEntry>();
                    for (int j = 0; j < count; j++) {
                        DocID docId = docIDs.get(j);
                        FolderEntry fe = new FolderEntry(docId);
                        listOfFolderEntries.add(fe);
                    }
                    if (listOfFolderEntries != null) {
                        folder.setMaxfolderSize(maxFolderSize);
                        if (!sr.addSelectedRecords(listOfFolderEntries, folder)) {
                            log4j.error("Unable to add selected records to folder: " + folder.getFolderName() + "!");
                            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
                        }
                    }
                }
            }

            return new ForwardResolution("/WEB-INF/pages/customer/folders/addSelectedSetToSavedRecords.jsp");

        } catch (Exception e) {
            log4j.error("add - Unable to process the Request!", e);
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        }
    }

    @Before(on = { "view", "add" })
    protected void callRequestCommonCode()
    {
        super.callRequestCommonCode();
        try {
            maxFolderSize = Integer.parseInt(EVProperties.getProperty(ApplicationProperties.MAX_FOLDERSIZE));
        } catch (Exception e) {
            log4j.error("callRequestCommonCode - Unable to process the Request!", e);
        }
    }

    /**
     * Returns a Resolution object for the login page
     */
    @Override
    public String getLoginNextUrl() {
        try {
			return URLEncoder.encode(FOLDERS_VIEW_FROM_SAVE_URL + "?" + context.getRequest().getQueryString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log4j.error("This should never happen!", e);
			return EVPathUrl.EV_HOME.value();
		}
    }

    @Override
    public String getLoginCancelUrl() {
        return super.getBackurl();
    }


}
