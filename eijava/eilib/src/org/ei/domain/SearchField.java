package org.ei.domain;


public class SearchField
{
	protected String ID;
	protected String title;

	public SearchField(String ID,
	                   String title)
	{
		this.ID = ID;
		this.title = title;
	}

	public SearchField(SearchField s,
			   		   String title)
	{
		this.title = title;
		this.ID = s.getID();
	}

	public String getID()
	{
		return this.ID;
	}

	public String getTitle(Database databases[])
	{
		return this.title;
	}

	public String getTitle()
	{
		return this.title;
	}
}