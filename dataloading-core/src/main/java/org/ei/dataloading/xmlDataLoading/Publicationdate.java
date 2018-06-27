package org.ei.dataloading.xmlDataLoading;

public class Publicationdate extends BaseElement
{

	String previous;
	String reprint;
	String year;
	String month;
	String day;
	String season;
	String date_text;

	public void setPrevious(String previous)
	{
		this.previous = previous;
	}

	public String getPrevious()
	{
		return previous;
	}

	public void setReprint(String reprint)
	{
		this.reprint = reprint;
	}

	public String getReprint()
	{
		return reprint;
	}

	public void setYear(String year)
	{
		this.year = year;
	}

	public String getYear()
	{
		return year;
	}

	public void setMonth(String month)
	{
		this.month = month;
	}

	public String getMonth()
	{
		return month;
	}

	public void setDay(String day)
	{
		this.day = day;
	}

	public String getDay()
	{
		return day;
	}

	public void setSeason(String season)
	{
		this.season = season;
	}

	public String getSeason()
	{
		return season;
	}

	public void setDate_Text(String date_text)
	{
		this.date_text = date_text;
	}

	public String getDate_Text()
	{
		return date_text;
	}


}
