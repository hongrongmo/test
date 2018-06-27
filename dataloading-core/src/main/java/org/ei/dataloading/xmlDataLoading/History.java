package org.ei.dataloading.xmlDataLoading;

public class History extends BaseElement
{

	Date_created date_created;
	Date_completed date_completed;
	Date_revised date_revised;


	public void setDate_created(Date_created date_created)
	{
		this.date_created = date_created;
	}

	public Date_created getDate_created()
	{
		return this.date_created;
	}

	public void setDate_completed(Date_completed date_completed)
	{
		this.date_completed = date_completed;
	}

	public Date_completed getDate_completed()
	{
		return this.date_completed;
	}

	public void setDate_revised(Date_revised date_revised)
	{
		this.date_revised = date_revised;
	}

	public Date_revised getDate_revised()
	{
		return this.date_revised;
	}
}
