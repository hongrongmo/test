package org.ei.dataloading.xmlDataLoading;

public class Manufacturergroup extends BaseElement
{
	Manufacturers manufacturers;
	String manufacturers_type;

	public void setManufacturers(Manufacturers manufacturers)
	{
		this.manufacturers = manufacturers;
	}

	public Manufacturers getManufacturers()
	{
		return this.manufacturers;
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

