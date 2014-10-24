package org.ei.ane.template;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;



public class CARSTemplateData {

	private Timestamp cacheBuiltUpTime;
	private Map<String, Object> aneTemplateMap=Collections.emptyMap();

	
	public Timestamp getCacheBuiltUpTime() {
		return cacheBuiltUpTime;
	}
	public void setCacheBuiltUpTime(Timestamp cacheBuiltUpTime) {
		this.cacheBuiltUpTime = cacheBuiltUpTime;
	}
	public Map<String, Object> getAneTemplateMap() {
		return aneTemplateMap;
	}
	public void setAneTemplateMap(Map<String, Object> aneTemplateMap) {
		this.aneTemplateMap = aneTemplateMap;
	}

		
	
}
