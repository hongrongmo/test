package org.ei.dataloading.xmlDataLoading;

public class Confdate extends BaseElement
{
	Startdate startdate;
	Enddate enddate;
	String date_text;

	public void setStartdate(Startdate startdate)
	{
		this.startdate = startdate;
	}

	public Startdate getStartdate()
	{
		return this.startdate;
	}

	public void setEnddate(Enddate enddate)
	{
		this.enddate = enddate;
	}

	public Enddate getEnddate()
	{
		return this.enddate;
	}

	public void setDate_text(String date_text)
	{
		this.date_text = date_text;
	}

	public String getDate_text()
	{
		return this.date_text;
	}
}
