package org.ei.dataloading.xmlDataLoading;

public class Manufacturers extends BaseElement
{
	Manufacturer manufacturer;
	String manufacturers_type;

	public void setManufacturer(Manufacturer manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public Manufacturer getManufacturer()
	{
		return this.manufacturer;
	}

	public void setManufacturers_type(String manufacturers_type)
	{
		this.manufacturers_type = manufacturers_type;
	}

	public String getManufacturers_type()
	{
		return this.manufacturers_type;
	}

}
