package org.ei.domain;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;

public class HelpLinksCache {

	// Map for key/value help link pairs
	private Map<String,String> helpLinks = new Hashtable<String,String>();
	
	// Instance variable
	private static  HelpLinksCache instance;
	static {
		instance = new HelpLinksCache();
	}
	
	// Default topic
	private static String defaulthelp = "Quick_srch_over.htm";
	
	public static enum KEYS {
		quickSearch, ebookSearch, expertSearch, thesHome,
		viewSavedSearches, referenceServices, 
		quickSearchCitationFormat, expertSearchCitationFormat, thesSearchCitationFormat, quickSearchReferencesFormat,
		tagSearch, tagsLoginForm, tagGroups, renameTag, deleteTag, 
		bulletinSearch, bulletinResults, 
		dedupForm, viewCitationSavedRecords, 
		viewPersonalFolders, addPersonalFolder, renamePersonalFolder, deletePersonalFolder, updatePersonalFolder, 
		citationSelectedSet, abstractSelectedSet, detailedSelectedSet, 
		myprofile, editPersonalAccount, personalLoginForm, forgotpassword;
	}
	
	/**
	 * Private constructor for Singleton
	 */
    private HelpLinksCache()
	{
        //key is based on CID to get required html value   	
        helpLinks.put(KEYS.quickSearch.toString(), "Quick_srch_over.htm");
        helpLinks.put(KEYS.ebookSearch.toString(), "eBook_search_overview.htm");
        helpLinks.put(KEYS.expertSearch.toString(), "Expert_search_overview.htm");
        helpLinks.put(KEYS.thesHome.toString(), "Thesaurus_search_over.htm");
        helpLinks.put(KEYS.editPersonalAccount.toString(), "Modify_acct_details.htm");
        helpLinks.put(KEYS.personalLoginForm.toString(), "Login.htm");
        helpLinks.put(KEYS.viewSavedSearches.toString(), "Saved_searches_work_with.htm");
        helpLinks.put(KEYS.referenceServices.toString(), "Ask_an_expert.htm");
        helpLinks.put(KEYS.quickSearchCitationFormat.toString(), "Search_rslts_work_with.htm");
        helpLinks.put(KEYS.expertSearchCitationFormat.toString(), "Search_rslts_work_with.htm");
        helpLinks.put(KEYS.thesSearchCitationFormat.toString(), "Search_rslts_work_with.htm");
        helpLinks.put(KEYS.myprofile.toString(), "Settings.htm");
        helpLinks.put(KEYS.tagSearch.toString(), "search_rslts_work_with.htm");
        helpLinks.put(KEYS.tagsLoginForm.toString(), "tags_introduction.htm");
        helpLinks.put(KEYS.tagGroups.toString(), "view_edit_delete_groups.htm");
        helpLinks.put(KEYS.renameTag.toString(), "edit_delete_rename_tags.htm");
        helpLinks.put(KEYS.deleteTag.toString(), "edit_delete_rename_tags.htm");
        helpLinks.put(KEYS.bulletinSearch.toString(), "Encompass_bulletins.htm");
        helpLinks.put(KEYS.bulletinResults.toString(), "Encompass_bulletins.htm");
        helpLinks.put(KEYS.dedupForm.toString(), "Deduplication_feature.htm");
        helpLinks.put(KEYS.viewPersonalFolders.toString(), "View_update_folder_contents.htm");
        helpLinks.put(KEYS.addPersonalFolder.toString(), "View_update_folder_contents.htm");
        helpLinks.put(KEYS.viewCitationSavedRecords.toString(), "View_update_folder_contents.htm");
        helpLinks.put(KEYS.renamePersonalFolder.toString(), "View_update_folder_contents.htm");
        helpLinks.put(KEYS.deletePersonalFolder.toString(), "View_update_folder_contents.htm");
        helpLinks.put(KEYS.updatePersonalFolder.toString(), "View_update_folder_contents.htm");
        helpLinks.put(KEYS.quickSearchReferencesFormat.toString(), "Patent_search_results_work_with.htm");
        helpLinks.put(KEYS.citationSelectedSet.toString(), "selected_recs.htm");
        helpLinks.put(KEYS.abstractSelectedSet.toString(), "selected_recs.htm");
        helpLinks.put(KEYS.detailedSelectedSet.toString(), "selected_recs.htm");
        helpLinks.put(KEYS.forgotpassword.toString(), "passwords.htm");
        helpLinks.put("pageDetailedFormat", "view_page_details_bk.htm");
        helpLinks.put("bookSummary", "view_book_details.htm");
        helpLinks.put("quickSearchAbstractFormat", "view_abstracts.htm");
        helpLinks.put("quickSearchDetailedFormat", "view_doc_details.htm");
        helpLinks.put("changePassword","passwords.htm");
        helpLinks.put("viewSavedFolders","view_update_folders_overview.htm");
        
	}
	
    /**
     * Retrieve a Help link by key
     * @param key
     * @return
     */
    public static String get(String key) {
		if(instance == null) {
			throw new RuntimeException("HelpLinksCache is not initialized!!");
		}
		if (key == null) {
			return defaulthelp;
		}
		String link = instance.helpLinks.get(key);
		if (GenericValidator.isBlankOrNull(link)) {
			link = defaulthelp;
		}
		return link;
    }
    
	/**
	 * Instance retrieval - should be called once at app initialization
	 * @return
	 * @throws Exception
	 */
	public static  HelpLinksCache getInstance(){
		if(instance == null) {
			throw new RuntimeException("HelpLinksCache is not initialized!!");
		}
		return instance;
	}
	
	/**
	 * Return all help links as Map
	 * @return
	 */
	public Map<String, String> getHelpLinks() {
		return helpLinks;
	}

}
