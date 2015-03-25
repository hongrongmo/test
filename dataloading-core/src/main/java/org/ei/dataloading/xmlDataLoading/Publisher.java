package org.ei.dataloading.xmlDataLoading;

public class Publisher extends BaseElement
{

	String publishername;
	String publisheraddress;
    String e_address;


	public void setPublishername(String publishername)
	{
		this.publishername = publishername;
	}

	public String getPublishername()
	{
		return this.publishername;
	}

	public void setPublisheraddress(String publisheraddress)
	{
		this.publisheraddress = publisheraddress;
	}

	public String getPublisheraddress()
	{
		return this.publisheraddress;
	}

	public void setE_address(String e_address)
	{
		this.e_address = e_address;
	}

	public String getE_address()
	{
		return this.e_address;
	}

}
