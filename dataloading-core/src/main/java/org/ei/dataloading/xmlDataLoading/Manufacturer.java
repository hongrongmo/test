package org.ei.dataloading.xmlDataLoading;

public class Manufacturer extends BaseElement
{
	String manufacturer;
	String manufacturer_country;

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public String getManufacturer()
	{
		return this.manufacturer;
	}

	public void setManufacturer_country(String manufacturer_country)
	{
		this.manufacturer_country = manufacturer_country;
	}

	public String getManufacturer_country()
	{
		return this.manufacturer_country;
	}

}
