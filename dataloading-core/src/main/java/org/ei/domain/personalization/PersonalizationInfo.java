package org.ei.domain.personalization;

/**
 * Keeps track of the counts for the personalization of this user profile.  Mostly used
 * during merging a user that has multiple old profiles and needs to select which ones to merge
 *
 */
public class PersonalizationInfo {
	private int tags;
	private int tagGroups;
	private int alerts;
	private int savedSearches;
	private int folders;
	private String firm;
	
	public PersonalizationInfo(){
		tags = 0;
		tagGroups = 0;
		alerts = 0;
		savedSearches = 0;
		folders = 0;
	}
	
	public PersonalizationInfo(int tags, int tagGroups, int alerts, int savedSearches, int folders){
		this.tags = tags;
		this.tagGroups = tagGroups;
		this.alerts = alerts;
		this.savedSearches = savedSearches;
		this.folders = folders;		
	}
	
	public int getTags() {
		return tags;
	}
	public void setTags(int tags) {
		this.tags = tags;
	}
	public int getTagGroups() {
		return tagGroups;
	}
	public void setTagGroups(int tagGroups) {
		this.tagGroups = tagGroups;
	}
	public int getAlerts() {
		return alerts;
	}
	public void setAlerts(int alerts) {
		this.alerts = alerts;
	}
	public int getSavedSearches() {
		return savedSearches;
	}
	public void setSavedSearches(int savedSearches) {
		this.savedSearches = savedSearches;
	}
	public int getFolders() {
		return folders;
	}
	public void setFolders(int folders) {
		this.folders = folders;
	}

	public String getFirm() {
		return firm;
	}

	public void setFirm(String firm) {
		this.firm = firm;
	}
	
	
	
	

}
