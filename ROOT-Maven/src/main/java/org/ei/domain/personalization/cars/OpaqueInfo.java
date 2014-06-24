package org.ei.domain.personalization.cars;

import java.io.Serializable;


public class OpaqueInfo implements Serializable {

	private static final long serialVersionUID = 9179223587174554911L;
	private String name;
	private String value;
	

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}


