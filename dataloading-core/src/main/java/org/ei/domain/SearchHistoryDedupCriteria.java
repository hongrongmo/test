package org.ei.domain;



public class SearchHistoryDedupCriteria {
    
	private String fieldPref;
	private String dbPref;
	private String dbPrefQueryString;
	private String fieldPrefDisplayName;
	private String dbPrefDisplayName;
	
	
	public String getFieldPref() {
		return fieldPref;
	}
	public void setFieldPref(String fieldPref) {
		this.fieldPref = fieldPref;
	}
	public String getDbPref() {
		return dbPref;
	}
	public void setDbPref(String dbPref) {
		this.dbPref = dbPref;
	}
	
	public String getDbPrefQueryString() {
		if (null != getDbPref()) {
			if (getDbPref().equals("1")) {
				dbPrefQueryString = "cpx";
			}
			if (getDbPref().equals("2")) {
				dbPrefQueryString = "ins";
			}
			if (getDbPref().equals("8192")) {
				dbPrefQueryString = "geo";
			}
			if (getDbPref().equals("2097152")) {
				dbPrefQueryString = "grf";
			}
			if (getDbPref().equals("128")) {
				dbPrefQueryString = "chm";
			}

			if (getDbPref().equals("64")) {
				dbPrefQueryString = "pch";
			}
			if (getDbPref().equals("1024")) {
				dbPrefQueryString = "elt";
			}
		}
		return dbPrefQueryString;
	}
	
	public void setDbPrefQueryString(String dbPrefQueryString) {
		this.dbPrefQueryString = dbPrefQueryString;
	}	
	
	
	
	public String getFieldPrefDisplayName() {
		if (null != getFieldPref()) {
			if ("0".equals(getFieldPref())) {
				fieldPrefDisplayName = "No Field";
			}

			if ("1".equals(getFieldPref())) {
				fieldPrefDisplayName = "Abstract";
			}

			if ("2".equals(getFieldPref())) {
				fieldPrefDisplayName = "Index Terms";
			}

			if ("4".equals(getFieldPref())) {
				fieldPrefDisplayName = "Full Text";
			}
		}
    	
		return fieldPrefDisplayName;
	}
	public void setFieldPrefDisplayName(String fieldPrefDisplayName) {
		this.fieldPrefDisplayName = fieldPrefDisplayName;
	}
	
	public String getDbPrefDisplayName() {
		if(null != getDbPref()){
			this.dbPrefDisplayName = DatabaseDisplayHelper.getDisplayName(Integer.parseInt(getDbPref()));
		}
		
		return dbPrefDisplayName;
	}
	
	public void setDbPrefDisplayName(String dbPrefDisplayName) {
		this.dbPrefDisplayName = dbPrefDisplayName;
	}
	
}
	
