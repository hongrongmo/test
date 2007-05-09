package org.ei.controller.content;

public class ContentDescriptor
{


	private String contentID;
	private String dataSourceURL;
	private String displayURL;
	private boolean bulkmode;
	private String authGroup;
	private String mimeType;




	public ContentDescriptor(String authGroup)
	{
		this.authGroup = authGroup;
	}

	public void setAuthGroup(String authGroup)
	{
		this.authGroup = authGroup;
	}

	public String getAuthGroup()
	{
		return this.authGroup;
	}

	public void setContentID(String contentID)
	{
		this.contentID = contentID;
	}

	public String getContentID()
	{
		return this.contentID;
	}

	public String getDataSourceURL()
	{
		return this.dataSourceURL;
	}

	public void setDataSourceURL(String dataSourceURL)
	{
		this.dataSourceURL = dataSourceURL;
	}

	public String getDisplayURL()
	{
		return this.displayURL;
	}

	public void setDisplayURL(String displayURL)
	{
		this.displayURL = displayURL;
	}


	public void setMimeType(String mimeType)
	{
		this.mimeType=mimeType;
	}

	public String getMimeType()
	{
		return this.mimeType;
	}

	public boolean isBulkmode()
	{
		return bulkmode;
	}

	public void setBulkmode(boolean bmode)
	{
		this.bulkmode = bmode;
	}

}
