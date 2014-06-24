package org.ei.service.cars.rest;

import java.util.List;


public class CARSGenericKeyEventData {
	private String recordId;
	private List<String> keyEventName;
	private List<String> keyEventValue;
	
	public List<String> getKeyEventName() {
		return keyEventName;
	}
	
	public void setKeyEventName(List<String> keyEventName) {
		this.keyEventName = keyEventName;
	}
	
	public List<String> getKeyEventValue() {
		return keyEventValue;
	}
	
	public void setKeyEventValue(List<String> keyEventValue) {
		this.keyEventValue = keyEventValue;
	}
	
	public String getRecordId() {
		return recordId;
	}
	
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

}

