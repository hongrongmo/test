package org.ei.controller;

import java.util.Enumeration;
import java.util.Properties;

import org.ei.controller.content.ContentDescriptor;
import org.ei.session.UserSession;
import org.ei.util.GUID;
import org.ei.util.StringUtil;

public class DataRequest
{

	private UserSession userSession;
	private Properties requestParameters;
	private ContentDescriptor contentDescriptor;
	private boolean validContentDescriptor;
	private boolean poll = false;
	private boolean metaRefreshPoll = false;
	private boolean collect = false;
	private String requestID;
	private long requestTime;
	private String theHash;

	public DataRequest(UserSession userSession,
			   Properties requestParameters,
			   ContentDescriptor contentDescriptor)
		throws RequestException
	{
		try
		{
			this.requestTime = System.currentTimeMillis();
			this.userSession = userSession;
			this.requestParameters = requestParameters;
			this.contentDescriptor = contentDescriptor;
			if(this.contentDescriptor == null)
			{
				validContentDescriptor = false;
			}
			else
			{
				validContentDescriptor = true;
			}

			if(requestParameters.containsKey("POLL"))
			{
				this.poll = true;
				this.requestID = requestParameters.getProperty("RID");
			}
			else if(requestParameters.containsKey("META_REFRESH_POLL"))
			{
				this.metaRefreshPoll = true;
				this.requestID = requestParameters.getProperty("RID");
			}
			else if(requestParameters.containsKey("COLLECT"))
			{
				collect = true;
				this.requestID = requestParameters.getProperty("RID");
			}
			else
			{
				this.requestID = new GUID().toString();
			}
		}
		catch(Exception e)
		{
			throw new RequestException(e);
		}
	}

	public long getRequestTime()
	{
		return this.requestTime;
	}



	public String getRequestID()
	{
		return this.requestID;
	}

	public boolean hasValidContentDescriptor()
	{
		return validContentDescriptor;
	}

	public void setUserSession(UserSession userSession)
	{
		this.userSession = userSession;
	}

	public UserSession getUserSession()
	{
		return this.userSession;
	}

	public void setRequestParameters(Properties requestParameters)
	{
		this.requestParameters = requestParameters;
		theHash = null;
	}

	public Properties getRequestParameters()
	{
		return this.requestParameters;
	}

	public void setContentDescriptor(ContentDescriptor contentDescriptor)
	{
		this.contentDescriptor = contentDescriptor;
	}

	public ContentDescriptor getContentDescriptor()
	{
		return this.contentDescriptor;
	}

	public String getUniqueHash()
		throws RequestException
	{

		try
		{
			if(theHash == null)
			{
				StringBuffer buf = new StringBuffer("B");
				Enumeration en = this.requestParameters.keys();
				while(en.hasMoreElements())
				{
					String key = (String)en.nextElement();
					String value = requestParameters.getProperty(key);
					buf.append(key+value);
				}
				StringUtil util = new StringUtil();
				theHash = util.computeUniqueHash(buf.toString());
			}
		}
		catch(Exception e)
		{
			throw new RequestException(e);
		}

		return theHash;
	}

}
