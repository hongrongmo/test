package org.ei.stripes.action.folders;

import java.util.List;

import net.sourceforge.stripes.validation.Validate;

import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.domain.personalization.Folder;
import org.ei.domain.personalization.FolderEntry;
import org.ei.domain.personalization.SavedRecords;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;

/**
 * This is the getter/setter class for the folders.
 *
 * @author bhanu
 *
 */
public abstract class FolderActionBean extends EVActionBean {
    private final static Logger log4j = Logger.getLogger(FolderActionBean.class);

    public static final String FOLDERS_DISPLAY_URL = "/personal/folders.url";
    public static final String FOLDERS_VIEW_FROM_SAVE_URL = "/personal/folders/save/view.url";

    // Rename request parms
    @Validate(trim=true,mask=".*")
    protected String folderid;
    @Validate(trim=true,mask="[a-zA-Z0-9_]*",maxlength=32)
    protected String foldername;
    @Validate(trim=true,mask="[a-zA-Z0-9_]*",maxlength=32)
    protected String oldfoldername;
    // Common request parms
    @Validate(trim=true,mask="\\d+",maxlength=32)
    protected int foldercount;
    protected List<Folder> folderlist;

    // Search results parameters
    @Validate(trim=true,mask=".*")
    protected String searchresults = null;
    @Validate(trim=true,mask=".*")
    protected String newsearch = null;
    @Validate(trim=true,mask=".*")
    protected String docindex;
    @Validate(trim=true,mask=".*")
    protected String format = null;

    //Folder entry list
    protected List<FolderEntry> listOfFolderEntries=null;

	//For the basket count
	protected String source=null;

	//string count
	protected String count=null;
	//flag
	protected boolean selectedSet=false;
	protected int maxFolderSize=0;
	protected String docid = null;
	protected int basketSize=0;
	//Folder object
	protected Folder folder=null;
	//flag
	protected int documentCount = 0;
	protected String hiddenCID=null;

	/**
	 * Shareable method to retrieve folder info
	 */
    protected void callRequestCommonCode()
    {
        try {

            UserSession ussession = context.getUserSession();

            // if the user exists get all the folders from the system .
            setPersonalization(true);
            SavedRecords sr = new SavedRecords(ussession.getUserIDFromSession());
            setFoldercount(sr.getFolderCount());
            this.folderlist = sr.viewListOfFolders();
            log4j.debug("Folder List set .... ");

            if (getRequest().getParameter("DOCINDEX") != null) {
                docindex = getRequest().getParameter("DOCINDEX");
                log4j.debug("docindex--> " + docindex);
            }

            maxFolderSize = Integer.parseInt(EVProperties.getProperty(ApplicationProperties.MAX_FOLDERSIZE));

        } catch (Exception e) {
            log4j.error("Error during common code!", e);
        }

    }


	public int getFoldercount() {
		return foldercount;
	}

	public void setFoldercount(int foldercount) {
		this.foldercount = foldercount;
	}

	public String getSource() {
		return source;
	}

	public String getCount() {
		return count;
	}

	public boolean isSelectedSet() {
		return selectedSet;
	}

	public int getMaxFolderSize() {
		return maxFolderSize;
	}

	public int getBasketSize() {
		return basketSize;
	}

	public String getDocid() {
		return docid;
	}

	public int getDocumentCount() {
	return documentCount;
	}

	public String getHiddenCID() {
		return hiddenCID;
	}

	public void setHiddenCID(String hiddenCID) {
		this.hiddenCID = hiddenCID;
	}

	public String getOldfoldername() {
		return oldfoldername;
	}

	public void setOldfoldername(String oldfoldername) {
		this.oldfoldername = oldfoldername;
	}

	public String getSearchresults() {
		return searchresults;
	}

	public void setSearchresults(String searchresults) {
		this.searchresults = searchresults;
	}

	public String getNewsearch() {
		return newsearch;
	}

	public void setNewsearch(String newsearch) {
		this.newsearch = newsearch;
	}

	public String getDocindex() {
		return docindex;
	}

	public void setDocindex(String docindex) {
		this.docindex = docindex;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

    public String getFolderid() {
        return this.folderid;
    }

    public void setFolderid(String folderid) {
        this.folderid = folderid;
    }

    public String getFoldername() {
        return this.foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public List<Folder> getFolderlist() {
        return this.folderlist;
    }

    public Folder getFolder() {
        return this.folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public void setBasketSize(int basketSize) {
        this.basketSize = basketSize;
    }

    public void setDocumentCount(int documentCount) {
        this.documentCount = documentCount;
    }


}