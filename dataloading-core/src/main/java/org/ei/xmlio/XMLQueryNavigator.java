package org.ei.xmlio;


public class XMLQueryNavigator
{
	String searchID = null;
	String database = null;
	String format = null;
	String serverName = null;
	String CID = null;
	String prevPageID = null;
	String nextPageID = null;
	String sessionID = null;

	public void setServerName(String serverName)
	{
		this.serverName=serverName;
	}
	public String getServerName()
	{
		return this.serverName;
	}
	public void setCID(String cid)
	{
		this.CID=cid;
	}
	public String getCID()
	{
		return this.CID;
	}

	public void setNextPageID(String nextPageID)
	{
		this.nextPageID=nextPageID;
	}
	public String getNextPageID()
	{
		return this.nextPageID;
	}
	public void setPrevPageID(String prevPageID)
	{
		this.prevPageID=prevPageID;
	}
	public String getPrevPageID()
	{
		return this.prevPageID;
	}

	public String getSearchID()
	{
		return this.searchID;
	}
	public void setSearchID(String searchID)
	{
		this.searchID=searchID;
	}
	public String getFormat()
	{
		return this.format;
	}
	public void setFormat(String format)
	{
		this.format=format;
	}
	public String getDatabase()
		{
			return this.database;
		}
		public void setDatabase(String database)
		{
			this.database=database;
	}

	public String buildNextPageURL() throws Exception
	{
		StringBuffer nextString=new StringBuffer();
		nextString.append("http://");
		nextString.append(serverName);
		nextString.append("/controller/servlet/Controller?");
		nextString.append("CID="+getCID());
		nextString.append("&EISESSION=$SESSIONID&SYSTEM_USE_SESSION_PARAM=true");
		nextString.append("&SEARCHID="+searchID);
		nextString.append("&FORMAT="+format);
		nextString.append("&DATABASE="+database);
		nextString.append("&PAGE="+getNextPageID());
		return nextString.toString();
	}

	public String buildPrevPageURL() throws Exception
	{
		StringBuffer prevString=new StringBuffer();
		prevString.append("http://");
		prevString.append(serverName);
		prevString.append("/controller/servlet/Controller?");
		prevString.append("CID="+getCID());
		prevString.append("&EISESSION=$SESSIONID&SYSTEM_USE_SESSION_PARAM=true");
		prevString.append("&SEARCHID="+searchID);
		prevString.append("&FORMAT="+format);
		prevString.append("&DATABASE="+database);
		prevString.append("&PAGE="+getPrevPageID());
		return prevString.toString();
	}

}
