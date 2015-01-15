package org.ei.controller.logging;

import java.util.Properties;

public class LogEntry
{
	
	private Properties logProperties = new Properties();
	private long requestTime;
	private long responseTime;
	private String IPAddress;
	private String sessionID;
	private String contentID;
	private String customerID;
	private String refURL;
	private String userAgent;
	private String appName;
	private Integer transNum;
	private String username;
	private String requestID;
	private String URLStem;
	private String URLQuery;

	public String getURLQuery()
	{
		return this.URLQuery;
	}

	public void setURLQuery(String URLQuery)
	{
		this.URLQuery = URLQuery;
	}

	public String getURLStem()
	{
		return URLStem;
	}

	public void setURLStem(String URLStem)
	{
		this.URLStem = URLStem;
	}




	public void setRequestID(String requestID)
	{
		this.requestID = requestID; 
	}

	public String getRequestID()
	{
		return this.requestID;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getUsername()
	{
		return this.username;
	}

	public void setTransNum(Integer i)
	{
		this.transNum = i;
	}

	public Integer getTransNum()
	{
		return transNum;
	}

	public String getAppName()
	{	
		return this.appName;
	}

	public void setAppName(String appName)
	{
		this.appName = appName;
	}

	public String getRefURL()
	{
		return refURL;
	}

	public void setRefURL(String refURL)
	{
		this.refURL = refURL;
	}

	public String getUserAgent()
	{
		return this.userAgent;
	}

	public void setUserAgent(String userAgent)
	{
		this.userAgent = userAgent;
	}
	

	public long getRequestTime()
	{
		return this.requestTime;
	}

	public void setRequestTime(long beginTime)
	{
		this.requestTime = beginTime;
	}

	public long getResponseTime()
	{
		return this.responseTime;
	}

	public void setResponseTime(long responseTime)
	{
		this.responseTime = responseTime;
	}

	public String getIPAddress()
	{
		return this.IPAddress;
	}

	public void setIPAddress(String IPAddress)
	{
		this.IPAddress = IPAddress;
	}

	public String getSessionID()
	{
		return this.sessionID;
	}

	public void setSessionID(String sessionID)
	{
		this.sessionID = sessionID;
	}

	public void setContentID(String contentID)
	{
		this.contentID = contentID;
	}

	public String getContentID()
	{
		return this.contentID;
	}

	public String getCustomerID()
	{
		return this.customerID;
	}

	public void setCustomerID(String customerID)
	{
		this.customerID = customerID;
	}
	
	public Properties getLogProperties()
	{
		return this.logProperties;
	}

	public void setLogProperties(Properties logProperties)
	{
		this.logProperties = logProperties;
	}

}
