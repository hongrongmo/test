package org.ei.stripes.action.folders;

import java.io.UnsupportedEncodingException;
import java.util.List;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.IndividualAuthRequiredAccessControl;
import org.ei.domain.personalization.Folder;
import org.ei.domain.personalization.SavedRecords;
import org.ei.domain.personalization.SavedRecordsException;
import org.ei.service.cars.CARSConstants;
import org.ei.stripes.action.personalaccount.IPersonalLogin;

/**
 * This is the base class for the personal folders.
 * 
 * @author bhanu
 * 
 */

@UrlBinding("/personal/folders{$event}.url")
public class PersonalFoldersAction extends FolderActionBean implements IPersonalLogin {
    
    private final static Logger log4j = Logger.getLogger(PersonalFoldersAction.class);
    
    @Override
	public IAccessControl getAccessControl() {
		return new IndividualAuthRequiredAccessControl();
	}
    
    /**
     * Displays the view Folders page -
     * 
     * @return Resolution
     */
    @DefaultHandler
    @DontValidate
    public Resolution display() {
        log4j.info("Starting view Folders view...");
        setRoom(ROOM.mysettings);
        setPersonalization(false);
        return new ForwardResolution("/WEB-INF/pages/customer/folders/ViewPersonalFolders.jsp");
    }
    
    /**
     * Handles cancel button from main display
     * 
     * @return Resolution
     */
    @HandlesEvent("cancel")
    @DontValidate
    public Resolution cancel() {
        log4j.info("Starting cancel...");
        return new RedirectResolution(CARSConstants.getLocalSettingsURI());
    }
        
    /**
     * Handles cancel button from main display
     * 
     * @return Resolution
     */
    @HandlesEvent("cancelrename")
    @DontValidate
    public Resolution cancelrename() {
        log4j.info("Starting cancel from rename...");
        return new RedirectResolution(FOLDERS_DISPLAY_URL);
    }
        
    
    /**
     * Handles saving a new folder
     * 
     * @return Resolution
     * @throws SavedRecordsException 
     */
    @HandlesEvent("add")
    @DontValidate
    public Resolution add() throws SavedRecordsException {
        log4j.info("Starting add Folder ...");
        SavedRecords sr = new SavedRecords(context.getUserSession().getUser().getUserInfo().getUserId());
        Folder newFolder = new Folder(this.foldername);
        if (!sr.addFolder(newFolder)) {
            log4j.error("Call to addFolder() failed!");
        }
        return new RedirectResolution(FOLDERS_DISPLAY_URL);
    }
    
    /**
     * Handles displaying the rename page
     * 
     * @return Resolution
     * @throws SavedRecordsException 
     */
    @HandlesEvent("rename")
    @DontValidate
    public Resolution rename() throws SavedRecordsException {
        return new ForwardResolution("/WEB-INF/pages/customer/folders/RenamePersonalFolder.jsp");
    }
    
    /**
     * Handles renaming a folder
     * 
     * @return Resolution
     * @throws SavedRecordsException 
     * @throws UnsupportedEncodingException 
     */
    @HandlesEvent("update")
    @DontValidate
    public Resolution update() throws SavedRecordsException, UnsupportedEncodingException {
        SavedRecords sr = new SavedRecords(context.getUserSession().getUser().getUserInfo().getUserId());
        Folder folder = new Folder(folderid, foldername);
        if (!sr.renameFolder(folder, folder)) {
            log4j.error("Call to renameFolder() failed!");
        }
        return new RedirectResolution(FOLDERS_DISPLAY_URL);
    }
    
    /**
     * Handles saving a new folder
     * 
     * @return Resolution
     * @throws SavedRecordsException 
     */
    @HandlesEvent("delete")
    @DontValidate
    public Resolution delete() throws SavedRecordsException {
        SavedRecords sr = new SavedRecords(context.getUserSession().getUser().getUserInfo().getUserId());
        String strFolderName = sr.getFolderName(folderid);
        Folder newFolder = new Folder(strFolderName);
        newFolder.setFolderID(folderid);
        if (!sr.removeFolder(newFolder)) {
            log4j.error("Call to removeFolder() failed!");
        }
        return new RedirectResolution(FOLDERS_DISPLAY_URL);
    }
    
    @Before(on = { "display", "rename" })
    protected void callRequestCommonCode() {
        super.callRequestCommonCode();
    }
    
    /**
     * Returns a Resolution object for the login page
     */
    @Override
    public String getLoginNextUrl() {
        return FOLDERS_DISPLAY_URL;
    }
    
    @Override
    public String getLoginCancelUrl() {
        return super.getBackurl();
    }
    
    public String getFoldername() {
        return this.foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public String getOldfoldername() {
        return this.oldfoldername;
    }

    public void setOldfoldername(String oldfoldername) {
        this.oldfoldername = oldfoldername;
    }

    public List<Folder> getFolderlist() {
        return folderlist;
    }

    public void setFolderlist(List<Folder> folderlist) {
        this.folderlist = folderlist;
    }

    public int getFoldercount() {
        return foldercount;
    }

    public void setFoldercount(int foldercount) {
        this.foldercount = foldercount;
    }

    public String getFolderid() {
        return this.folderid;
    }

    public void setFolderid(String folderid) {
        this.folderid = folderid;
    }
}