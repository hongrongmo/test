package org.ei.dataloading.xmlDataLoading;

public class Process_info extends BaseElement
{
	Date_delivered date_delivered;
	Date_sort date_sort;
	Status status;

	public void setDate_delivered(Date_delivered date_delivered)
	{
		this.date_delivered = date_delivered;
	}

	public Date_delivered getDate_delivered()
	{
		return this.date_delivered;
	}


	public void setDate_sort(Date_sort date_sort)
	{
		this.date_sort = date_sort;
	}

	public Date_sort getDate_sort()
	{
		return this.date_sort;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Status getStatus()
	{
		return this.status;
	}

}
