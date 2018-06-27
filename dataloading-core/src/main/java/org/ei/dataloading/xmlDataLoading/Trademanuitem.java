package org.ei.dataloading.xmlDataLoading;

public class Trademanuitem extends BaseElement
{
	String tradename;
	Manufacturer manufacturer;

	public void setTradename(String tradename)
	{
		this.tradename = tradename;
	}

	public String getTradename()
	{
		return this.tradename;
	}


	public void setManufacturer(Manufacturer manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public Manufacturer getManufacturer()
	{
		return this.manufacturer;
	}
}
