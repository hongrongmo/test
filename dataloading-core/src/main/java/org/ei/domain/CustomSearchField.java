package org.ei.domain;


public class CustomSearchField
{
	private Database[] databases;
	private SearchField searchField;

	public CustomSearchField(Database[] databases,
				 			 SearchField searchField)
	{
		this.databases = databases;
		this.searchField = searchField;
	}

	public Database[] getDatabases()
	{
		return this.databases;
	}

	public SearchField getSearchField()
	{
		return this.searchField;
	}

	public String getTitle()
	{
		return searchField.getTitle(databases);
	}
}