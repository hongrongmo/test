package org.ei.dataloading.xmlDataLoading;

public class Website extends BaseElement
{
	String websitename;
	String e_address;
	String website_type;

	public void setWebsitename(String websitename)
	{
		this.websitename = websitename;
	}

	public String getWebsitename()
	{
		return this.websitename;
	}

	public void setE_address(String e_address)
	{
		this.e_address = e_address;
	}

	public String getE_address()
	{
		return this.e_address;
	}

	public void setWebsite_type(String website_type)
	{
		this.website_type = website_type;
	}

	public String getWebsite_type()
	{
		return this.website_type;
	}
}
