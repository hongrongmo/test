package org.ei.dataloading.lookup;

/*
 * @Author: TELEBH
 * @Date: 09/01/2020
 * @Description: Represent Author Lookup Record for populating Lookups for ES
 */
public class AuthorLookupRec {
	
	private String authorName;
	private String database;
	private String authorId;
	private String pui;
	private String loadNumber;
	private String accessNumber;
	
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	public String getPui() {
		return pui;
	}
	public void setPui(String pui) {
		this.pui = pui;
	}
	public String getLoadNumber() {
		return loadNumber;
	}
	public void setLoadNumber(String loadNumber) {
		this.loadNumber = loadNumber;
	}
	public String getAccessNumber() {
		return accessNumber;
	}
	public void setAccessNumber(String accessNumber) {
		this.accessNumber = accessNumber;
	}
	
	

}
