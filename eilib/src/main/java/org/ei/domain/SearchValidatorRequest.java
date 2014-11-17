package org.ei.domain;

public class SearchValidatorRequest {
	private String searchtype;
	private int databasemask;
	private String[] cartridge;
	private String sessionid;
	private String userid;
	private ISearchForm searchform;

	public String getSearchtype() {
		return searchtype;
	}
	public void setSearchtype(String searchtype) {
		this.searchtype = searchtype;
	}
	public int getDatabasemask() {
		return databasemask;
	}
	public void setDatabasemask(int databasemask) {
		this.databasemask = databasemask;
	}
	public String[] getCartridge() {
		return cartridge;
	}
	public void setCartridge(String[] cartridge) {
		this.cartridge = cartridge;
	}
	public String getSessionid() {
		return sessionid;
	}
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public ISearchForm getSearchform() {
		return searchform;
	}
	public void setSearchform(ISearchForm searchform) {
		this.searchform = searchform;
	}


}
