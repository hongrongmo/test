package org.ei.stripes.view;

public class AbstractTerm {
	private String id;
	private String value;
	private String searchlink;
	private String cssclass;
	private String field;
	private String ipcdescription;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getSearchlink() {
		return searchlink;
	}
	public void setSearchlink(String searchlink) {
		this.searchlink = searchlink;
	}
	public String getCssclass() {
		return cssclass;
	}
	public void setCssclass(String cssclass) {
		this.cssclass = cssclass;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getIpcdescription() {
		return ipcdescription;
	}
	public void setIpcdescription(String ipcdescription) {
		this.ipcdescription = ipcdescription;
	}
}
