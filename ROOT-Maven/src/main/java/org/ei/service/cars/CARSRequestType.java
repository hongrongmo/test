package org.ei.service.cars;

public enum CARSRequestType {
    URLBASED("urlbased"),
    TERMINATE("terminate"),
    AUTHENTICATE("authenticate"),
    IPAUTHENTICATE("ipauthenticate"),
    LOGINFULL("loginfull"),
    PROFILEDISPLAY("profiledisplay"),
    BULKAUTHENTICATE("bulkauthenticate"),
	REGISTER("register");
	
	/** The attribute to hold the constant name */
	private final String m_value;

	/**
	 * Constructor
	 */
	private CARSRequestType(String str) {
		this.m_value = str;
	}

	/**
	 * Method to get the constant value
	 * @return m_value
	 */
	public String value() {
		return m_value;
	}
}
