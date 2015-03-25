package org.ei.dataloading.xmlDataLoading;

public class Tradenamegroup extends BaseElement
{
	Tradenames tradenames;
	String tradenames_type;

	public void setTradenames(Tradenames tradenames)
	{
		this.tradenames = tradenames;
	}

	public Tradenames getTradenames()
	{
		return this.tradenames;
	}

	public void setTradenames_type(String tradenames_type)
	{
		this.tradenames_type = tradenames_type;
	}

	public String getTradenames_type()
	{
		return this.tradenames_type;
	}

}

