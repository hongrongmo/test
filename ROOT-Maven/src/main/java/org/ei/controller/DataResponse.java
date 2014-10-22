package org.ei.controller;

import java.util.Properties;

import org.ei.exception.ErrorXml;
import org.ei.session.UserSession;

public class DataResponse {
	private DataRequest dataRequest;
	private Properties logProperties;
	private UserSession updatedSession;
	private long responseTime;
	private String redirectURL;
	private ErrorXml errorXml;

	public DataResponse(DataRequest dataRequest, UserSession session, Properties logProps) {
		this.dataRequest = dataRequest;
		this.updatedSession = session;
		this.logProperties = logProps;
		this.responseTime = System.currentTimeMillis();
	}

	public String getRedirectURL() {
		return this.redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public ErrorXml getErrorXml() {
		return this.errorXml;
	}

	public void setErrorXml(ErrorXml errorxml) {
		this.errorXml = errorxml;
	}

	public boolean isError() {
		boolean b = false;
		if (this.errorXml != null) {
			b = true;
		}

		return b;
	}

	public boolean isRedirect() {
		boolean b = false;

		if (this.redirectURL != null) {
			b = true;
		}

		return b;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public long getResponseTime() {
		return this.responseTime;
	}

	public DataRequest getDataRequest() {
		return this.dataRequest;
	}

	public void setDataRequest(DataRequest request) {
		this.dataRequest = request;
	}

	public Properties getLogProperties() {
		return this.logProperties;
	}

	public void setLogProperties(Properties logProperties) {
		this.logProperties = logProperties;
	}

	public UserSession getUpdatedSession() {
		return this.updatedSession;
	}

	public void setUpdatedSession(UserSession updatedSession) {
		this.updatedSession = updatedSession;
	}

}
