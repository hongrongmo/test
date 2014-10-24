package org.ei.service.cars.rest;

public class CARSGenericKeyEvent   {


	private String m_rememberMe;
	private String m_autoLogin;
	private String m_activity;

	public static final String KEY_EVENT_ID = "CARGenericKeyEvent";

	


	public String getAutoLogin() {
		return m_autoLogin;
	}

	public void setAutoLogin(String autoLogin) {
		this.m_autoLogin = autoLogin;
	}

	public String getActivity() {
		return m_activity;
	}

	public void setActivity(String activity) {
		this.m_activity = activity;
	}

	public String getRememberMe() {
		return m_rememberMe;
	}

	public void setRememberMe(String rememberMe) {
		this.m_rememberMe = rememberMe;
	}
}


