package org.ei.dataloading.lookup;

/*
 * @Author: TELEBH
 * @Date: 09/01/2020
 * @Description: Represent Affiliation Lookup Record for populating Lookups for ES
 */

public class AffiliationLookupRec {
	
	private String database;
	private String affiliationId;
	private String pui;
	private String loadNumber;
	private String accessNumber;

	
	private String affiliationName;
	public String getAffiliationName() {
		return affiliationName;
	}
	public void setAffiliationName(String affiliationName) {
		this.affiliationName = affiliationName;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getAffiliationId() {
		return affiliationId;
	}
	public void setAffiliationId(String affiliationId) {
		this.affiliationId = affiliationId;
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
