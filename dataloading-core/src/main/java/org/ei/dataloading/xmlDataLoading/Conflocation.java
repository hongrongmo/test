package org.ei.dataloading.xmlDataLoading;

public class Conflocation extends BaseElement
{
	String venue;
	String address_part;
	String city_group;
	String city;
	String state;
	String postal_code;
	String conflocation_country;

	public void setConflocation_country(String conflocation_country)
	{
		this.conflocation_country = conflocation_country;
	}

	public String getConflocation_country()
	{
		return this.conflocation_country;
	}

	public void setVenue(String venue)
	{
		this.venue = venue;
	}

	public String getVenue()
	{
		return this.venue;
	}

	public void setAddress_part(String address_part)
	{
		this.address_part = address_part;
	}

	public String getAddress_part()
	{
		return this.address_part;
	}

	public void setCity_group(String city_group)
	{
		this.city_group = city_group;
	}

	public String getCity_group()
	{
		return this.city_group;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getCity()
	{
		return this.city;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getState()
	{
		return this.state;
	}

	public void setPostal_code(String postal_code)
	{
		this.postal_code = postal_code;
	}

	public String getPostal_code()
	{
		return this.postal_code;
	}

}
