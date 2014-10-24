package org.ei.biz.personalization.cars;

import java.io.Serializable;


public class ShibbolethInfo implements Serializable {

	private static final long serialVersionUID = 9009050375719606070L;
    private String providerID;
    private String entitlements;
    private String targetedId;


	public String getEntitlements() {
		return entitlements;
	}

	public void setEntitlements(String entitlements) {
		this.entitlements = entitlements;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public String getTargetId() {
		return targetedId;
	}

	public void setTargetedId(String targetedId) {
		this.targetedId = targetedId;
	}
}

