package org.ei.controller;

public class Department
{
	private String ID;
	private String name;

	public Department(String ID,
					  String name)
	{
		this.ID = ID;
		this.name = name;
	}

	public String getID()
	{
		return this.ID;
	}

	public String getName()
	{
		return this.name;
	}
}