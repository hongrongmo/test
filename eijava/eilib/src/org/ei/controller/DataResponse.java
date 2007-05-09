package org.ei.controller;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.ei.session.UserSession;

public class DataResponse
{
	private DataRequest dataRequest;
	private Properties logProperties;
	private String xmlDataFile;
	private UserSession updatedSession;
	private long responseTime;
	private String redirectURL;
	private String error;


	public DataResponse(DataRequest dataRequest, 
			    UserSession session,
			    Properties logProps,
			    String xmlDataFile)
	{
		this.dataRequest = dataRequest;
		this.updatedSession = session;
		this.logProperties = logProps;
		this.xmlDataFile = xmlDataFile;
		this.responseTime = System.currentTimeMillis();
	}
	
	
	public DataResponse(DataRequest dataRequest, 
			    UserSession session,
			    Properties logProps)
	{
		this.dataRequest = dataRequest;
		this.updatedSession = session;
		this.logProperties = logProps;
		this.responseTime = System.currentTimeMillis();
	} 


	public InputStream getXMLStream()
		throws Exception
	{
		return new FileInputStream(this.xmlDataFile);
	}
	
	public String getRedirectURL()
	{
		return this.redirectURL;
	}

	public void setRedirectURL(String redirectURL)
	{
		this.redirectURL = redirectURL;
	}

	public String getError()
	{
		return this.error;
	}	

	public void setError(String error)
	{
		this.error = error;
	}

	public boolean hasError()
	{
		boolean b = false;
		if(this.error != null)
		{
			b = true;
		}
		
		return b;
	}

	public boolean isRedirect()
	{
		boolean b = false;

		if(this.redirectURL != null)
		{
			b = true;
		}

		return b;
	}

	public void setResponseTime(long responseTime)
	{
		this.responseTime = responseTime;
	}

	public long getResponseTime()
	{
		return this.responseTime;
	}


	public DataRequest getDataRequest()
	{
		return this.dataRequest;
	}

	public void setDataRequest(DataRequest request)
	{
		this.dataRequest = request;
	}

	public Properties getLogProperties()
	{
		return this.logProperties;
	}

	public void setLogProperties(Properties logProperties)
	{
		this.logProperties = logProperties;
	}

	

	public UserSession getUpdatedSession()
	{
		return this.updatedSession;
	}

	public void setUpdatedSession(UserSession updatedSession)
	{
		this.updatedSession = updatedSession;
	}



}
